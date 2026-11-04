package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Location
 * Subtype: Site
 * Title: Geonosis: Rocky Plains
 */
public class Card1291_059 extends AbstractSite {
    public Card1291_059() {
        super(Side.LIGHT, "Geonosis: Rocky Plains", Title.Geonosis, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLocationLightSideGameText("Your total power here is +1 for each [Clone Army] card present.");
        setLocationDarkSideGameText("Once per turn, if you control, may raise your converted Rocky Plains to the top.");
        setGameText("Light: Your total power here is +1 for each [Clone Army] card present. Dark: Once per turn, if you control, may raise your converted Rocky Plains to the top.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.EPISODE_I, Icon.PLANET, Icon.EXTERIOR_SITE);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, self, new PresentEvaluator(self, Icon.CLONE_ARMY), playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter converted = Filters.and(Filters.title("Geonosis: Rocky Plains"), Filters.canBeConvertedByRaisingYourLocationToTop(playerOnDarkSideOfLocation));
        if (GameConditions.controls(game, playerOnDarkSideOfLocation, self)
                && GameConditions.isOncePerTurn(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId)
                && GameConditions.canTarget(game, self, converted)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
            action.setText("Raise converted Rocky Plains");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerOnDarkSideOfLocation, "Choose converted Rocky Plains", converted) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(new ConvertLocationByRaisingToTopEffect(action, targetedCard, true));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
