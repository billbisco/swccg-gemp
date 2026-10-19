package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4: Expanded Universe
 * Type: Character
 * Subtype: Droid
 * Title: R2-A5 (Artoo-Ayfive)
 */
public class Card1240_014 extends AbstractDroid {
    public Card1240_014() {
        super(Side.DARK, 2, 2, 1, 3, "R2-A5 (Artoo-Ayfive)", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Combat communications astromech. Coordinates starfighter attack vectors. Broadcasts target acquisition data to gunnery systems.");
        setGameText("Heroic. When deployed, may play a Defensive Shield from under your Starting Effect (as if from hand). If at a converted site, may use 1 Force to raise your site to the top.");
        addIcons(Icon.NAV_COMPUTER, Icon.SPECIAL_EDITION);
        addModelType(ModelType.ASTROMECH);
        addKeywords(Keyword.HEROIC);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
            if (startingEffect != null) {
                Filter filter = Filters.Defensive_Shield;
                if (GameConditions.hasStackedCards(game, startingEffect, filter)) {
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Play a Defensive Shield");
                    action.appendTargeting(
                            new ChooseStackedCardEffect(action, playerId, startingEffect, filter) {
                                @Override
                                protected void cardSelected(PhysicalCard selectedCard) {
                                    action.appendEffect(new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.isAtLocation(game, self, Filters.and(Filters.site, Filters.canBeConvertedByRaisingYourLocationToTop(playerId)))) {
            final PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsPresentAt(game.getGameState(), self);
            if (location != null) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Raise converted site to the top");
                action.setActionMsg("Raise converted site to the top to convert " + GameUtils.getCardLink(location));
                action.addAnimationGroup(location);
                action.appendCost(new UseForceEffect(action, playerId, 1));
                action.appendEffect(new ConvertLocationByRaisingToTopEffect(action, location, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
