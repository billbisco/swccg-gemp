package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Lost
 * Title: Wookiee Strangle
 *
 * Original version of 1220_160 (Virtual Block 2 release sheet, Dec 2009, card #160E): no once-per-game limit (added on
 * the Virtual Cards Master List, 2013-04-27).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_160 extends AbstractLostInterrupt {
    public Card2220_160() {
        super(Side.LIGHT, 3, Title.Wookiee_Strangle, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Wookiees have been known to dislocate more than just arms.");
        setGameText("During your move phase, target opponent's character present with your Wookiee. Both players draw destiny. Add Wookiee's power; opponent adds target's defense value. Target lost if your total destiny +2 > opponent's total destiny.");
        setLegacyVersion(new LegacyVersion("1220_160", "",
                "Virtual Block 2 release sheet (2009-12-07), card #160", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__WOOKIEE_STRANGLE_V__STRANGLE;

        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentWith(self, Filters.and(Filters.your(self), Filters.Wookiee)));
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Target a character");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's character", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetCard) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose Wookiee", Filters.and(Filters.your(self), Filters.Wookiee, Filters.presentWith(targetCard))) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, PhysicalCard wookiee) {
                                            action.addAnimationGroup(targetCard, wookiee);
                                            action.allowResponses("Target " + GameUtils.getCardLink(wookiee) + " and " + GameUtils.getCardLink(targetCard),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                                            final PhysicalCard finalWookiee = targetingAction.getPrimaryTargetCard(targetGroupId2);

                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                            return Arrays.asList(finalTarget, finalWookiee);
                                                                        }
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float playersTotalDestiny) {
                                                                            action.appendEffect(
                                                                                    new DrawDestinyEffect(action, opponent) {
                                                                                        @Override
                                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float opponentsTotalDestiny) {
                                                                                            GameState gameState = game.getGameState();

                                                                                            gameState.sendMessage(playerId + "'s destiny: " + (playersTotalDestiny != null ? GuiUtils.formatAsString(playersTotalDestiny) : "Failed destiny draw"));
                                                                                            gameState.sendMessage(opponent + "'s destiny: " + (opponentsTotalDestiny != null ? GuiUtils.formatAsString(opponentsTotalDestiny) : "Failed destiny draw"));
                                                                                            float wookieePower = game.getModifiersQuerying().getPower(gameState, finalWookiee);
                                                                                            gameState.sendMessage(GameUtils.getCardLink(finalWookiee) + "'s power: " + GuiUtils.formatAsString(wookieePower));
                                                                                            float targetDefense = game.getModifiersQuerying().getDefenseValue(gameState, finalTarget);
                                                                                            gameState.sendMessage(GameUtils.getCardLink(finalTarget) + "'s defense value: " + GuiUtils.formatAsString(targetDefense));

                                                                                            if (playersTotalDestiny == null && opponentsTotalDestiny == null) {
                                                                                                gameState.sendMessage("Both players failed due to failed destiny draws");
                                                                                                gameState.sendMessage("Result: No result");
                                                                                            } else if (playersTotalDestiny == null) {
                                                                                                gameState.sendMessage(playerId + "'s total failed due to failed destiny draw");
                                                                                                gameState.sendMessage("Result: No result");
                                                                                            } else if (opponentsTotalDestiny == null) {
                                                                                                gameState.sendMessage(opponent + "'s total failed due to failed destiny draw");
                                                                                                gameState.sendMessage("Result: " + GameUtils.getCardLink(finalTarget) + " to be lost");
                                                                                                action.appendEffect(
                                                                                                        new LoseCardFromTableEffect(action, finalTarget));
                                                                                            } else {
                                                                                                float playersTotal = playersTotalDestiny + wookieePower;
                                                                                                gameState.sendMessage(playerId + "'s total: " + GuiUtils.formatAsString(playersTotal));
                                                                                                float opponentsTotal = opponentsTotalDestiny + targetDefense;
                                                                                                gameState.sendMessage(opponent + "'s total: " + GuiUtils.formatAsString(opponentsTotal));
                                                                                                if (playersTotal + 2 > opponentsTotal) {
                                                                                                    gameState.sendMessage("Result: " + GameUtils.getCardLink(finalTarget) + " to be lost");
                                                                                                    action.appendEffect(
                                                                                                            new LoseCardFromTableEffect(action, finalTarget));
                                                                                                } else {
                                                                                                    gameState.sendMessage("Result: No result");
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                            );
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    }
                                            );
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
