package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractLocationDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
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
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Device
 * Title: Lars' Hydroponics Station
 */
public class Card1210_117 extends AbstractLocationDevice {
    public Card1210_117() {
        super(Side.LIGHT, 4, "Lars' Hydroponics Station", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setLore("Grows fruits and vegetables. Very efficient water use. Often underground. Feeds moisture farm families, but excess vegetables are often sold at markets.");
        setGameText("Deploy on a farm. During your draw phase, while no 'crop' here, may use 1 Force to stack a 'crop' (any one card from Lost Pile) on this device. While you occupy this site, if any just drawn destiny matches destiny of 'crop', may place 'crop' in your Used Pile.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.farm;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.farm;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LARS_HYDROPONICS_STATION__STACK_CROP;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DRAW)
                && !GameConditions.hasStackedCards(game, self)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Stack a 'crop' from Lost Pile");
            action.setActionMsg("Use 1 Force to stack a card from Lost Pile on " + GameUtils.getCardLink(self) + " as 'crop'");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new StackCardFromLostPileEffect(action, self, Filters.any, false));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && !((DestinyDrawnResult) effectResult).isCanceled()
                && GameConditions.hasStackedCards(game, self)
                && GameConditions.occupies(game, playerId, Filters.hasAttached(self))) {
            PhysicalCard drawn = ((DestinyDrawnResult) effectResult).getCard();
            if (drawn == null) {
                return null;
            }
            PhysicalCard crop = game.getGameState().getStackedCards(self).get(0);
            float drawnDestiny = game.getModifiersQuerying().getDestiny(game.getGameState(), drawn);
            float cropDestiny = game.getModifiersQuerying().getDestiny(game.getGameState(), crop);
            if (drawnDestiny == cropDestiny) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place 'crop' in Used Pile");
                action.setActionMsg("Place " + GameUtils.getCardLink(crop) + " in Used Pile");
                action.appendEffect(
                        new PutStackedCardsInUsedPileEffect(action, playerId, Collections.singletonList(crop), true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
