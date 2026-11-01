package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Interrupt
 * Subtype: Used
 * Title: Gravel Storm
 */
public class Card1115_048 extends AbstractUsedInterrupt {
    public Card1115_048() {
        super(Side.DARK, 3, Title.Gravel_Storm, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Tatooine's twin suns cause turbulent storms that strike with little or no warning. Strong winds whip rocks through the air with enormous force.");
        setGameText("Lose 1 Force to target a desert or non-interior Tatooine site. For remainder of turn, all immunity to attrition there is canceled.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter siteFilter = Filters.or(Filters.desert, Filters.and(Filters.Tatooine_site, Filters.not(Filters.interior_site)));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, siteFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel immunity to attrition at a site");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose desert or non-interior Tatooine site", siteFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new LoseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Cancel all immunity to attrition at " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalSite = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelImmunityToAttritionUntilEndOfTurnEffect(action, Filters.at(finalSite),
                                                            "Cancels all immunity to attrition at " + GameUtils.getCardLink(finalSite)));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
