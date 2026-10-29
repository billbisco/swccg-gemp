package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetAllCardsAtSameLocationEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardsAwayEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections Gold
 * Type: Interrupt
 * Subtype: Used
 * Title: Hyper Escape
 */
public class Card15_087 extends AbstractUsedInterrupt {
    public Card15_087() {
        super(Side.LIGHT, 5, Title.Hyper_Escape, Uniqueness.UNRESTRICTED, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("");setAsHorizontal(true);setCredits(3);addIcons(Icon.REFLECTIONS_GOLD);
        setGameText("Cancel a battle just initiated at a system by moving all your starships there away.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final Filter starshipFilter = Filters.and(Filters.your(self), Filters.starship,
                Filters.participatingInBattle);

        // Move Away initiation checks a destination exists, not whether hyperspeed or Force can reach it.
        // Actual movement legality and payment are resolved separately for each ship.
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.and(Filters.system, Filters.canBeTargetedBy(self)))
                && GameConditions.canTarget(game, self, starshipFilter)
                && GameConditions.canSpotLocation(game, Filters.or(
                        Filters.and(Filters.system, Filters.not(Filters.battleLocation)),
                        Filters.relatedSector(game.getGameState().getBattleLocation())))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Move starships away");
            // Choose target(s)
            action.appendTargeting(
                    new TargetAllCardsAtSameLocationEffect(action, playerId, "Choose starships to move away", starshipFilter) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                        @Override
                        protected void cardsTargeted(final int targetGroupId1, Collection<PhysicalCard> targetedStarships) {
                            action.addAnimationGroup(targetedStarships);
                            // Set secondary target filter(s)
                            action.addSecondaryTargetFilter(Filters.battleLocation);
                            // Allow response(s)
                            action.allowResponses("Move all starships away",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            Collection<PhysicalCard> finalStarships = action.getPrimaryTargetCards(targetGroupId1);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new MoveCardsAwayEffect(action, playerId, Filters.in(finalStarships)));
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
