package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.evaluators.MultiplyEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BlowAwayState;
import com.gempukku.swccgo.game.state.DeactivateTheShieldGeneratorState;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.AbstractGameTextAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.AddToBlownAwayForceLossEffect;
import com.gempukku.swccgo.logic.effects.BlowAwayEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.CalculatingEpicEventTotalResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The Deactivate The Shield Generator attempt to 'blow away' Bunker (Charges! Come On, Come On! / Move! Move! Move!),
 * for cards that let a player use that Epic Event outside its own control-phase action (Rebel Strike Team (V):
 * "If you occupy Bunker at the end of a battle you just won there, may use your Epic Event to attempt to 'blow away'
 * Bunker"). Same steps as Card8_043.
 */
public final class LegacyBunkerBlowAwayAttempt {
    private LegacyBunkerBlowAwayAttempt() {
    }

    /**
     * Appends the attempt to the action.
     * @param action the action (an Epic Event action, so the Epic Event state is set)
     * @param playerId the player making the attempt
     * @param epicEvent the Deactivate The Shield Generator card
     */
    public static void appendAttempt(final AbstractGameTextAction action, final String playerId, final PhysicalCard epicEvent) {
        action.appendEffect(
                new DrawDestinyEffect(action, playerId, 2, DestinyType.EPIC_EVENT_DESTINY) {
                    @Override
                    protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                        Modifier modifier = new TotalDestinyModifier(epicEvent, drawDestinyState.getId(), new MultiplyEvaluator(3, new OnTableEvaluator(epicEvent, Filters.and(Filters.Explosive_Charge, Filters.attachedTo(Filters.Bunker)))));
                        return Collections.singletonList(modifier);
                    }

                    @Override
                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                        final GameState gameState = game.getGameState();
                        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                        // 2) Move! Move! Move!
                        if (totalDestiny == null) {
                            gameState.sendMessage("Result: Failed due to failed destiny draw");
                            return;
                        }

                        final float initialEpicEventTotal = modifiersQuerying.getEpicEventCalculationTotal(gameState, epicEvent, totalDestiny);
                        gameState.sendMessage("Epic Event Total: " + GuiUtils.formatAsString(initialEpicEventTotal));
                        // Emit effect result that Deactivate The Shield Generator total is being calculated
                        action.appendEffect(
                                new TriggeringResultEffect(action, new CalculatingEpicEventTotalResult(playerId, epicEvent)));
                        action.appendEffect(
                                new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        float finalEpicEventTotal = modifiersQuerying.getEpicEventCalculationTotal(gameState, epicEvent, totalDestiny);
                                        if (initialEpicEventTotal != finalEpicEventTotal) {
                                            gameState.sendMessage("Epic Event Total: " + GuiUtils.formatAsString(finalEpicEventTotal));
                                        }

                                        if (finalEpicEventTotal > 12) {
                                            gameState.sendMessage("Result: Succeeded");
                                            final PhysicalCard bunker = Filters.findFirstFromTopLocationsOnTable(game, Filters.Bunker);
                                            PhysicalCard landingPlatform = Filters.findFirstFromTopLocationsOnTable(game, Filters.Landing_Platform);
                                            final PhysicalCard backDoor = Filters.findFirstFromTopLocationsOnTable(game, Filters.Back_Door);
                                            if (backDoor != null) {
                                                final Collection<PhysicalCard> charactersToRelocate = Filters.filterActive(game, epicEvent,
                                                        Filters.and(Filters.your(playerId), Filters.character, Filters.at(bunker), Filters.canBeRelocatedToLocation(backDoor, true, 0)));
                                                if (!charactersToRelocate.isEmpty()) {
                                                    action.appendEffect(
                                                            new PlayoutDecisionEffect(action, playerId,
                                                                    new YesNoDecision("Do you want to relocate your characters from " + GameUtils.getCardLink(bunker) + " to " + GameUtils.getCardLink(backDoor) + "?") {
                                                                        @Override
                                                                        protected void yes() {
                                                                            gameState.sendMessage(playerId + " chooses to relocate characters from " + GameUtils.getCardLink(bunker) + " to " + GameUtils.getCardLink(backDoor));
                                                                            action.insertEffect(
                                                                                    new RelocateBetweenLocationsEffect(action, charactersToRelocate, backDoor));
                                                                        }
                                                                        @Override
                                                                        protected void no() {
                                                                            gameState.sendMessage(playerId + " chooses to not relocate characters from " + GameUtils.getCardLink(bunker) + " to " + GameUtils.getCardLink(backDoor));
                                                                        }
                                                                    }
                                                            )
                                                    );
                                                }
                                            }
                                            List<PhysicalCard> sites = new ArrayList<PhysicalCard>();
                                            sites.add(bunker);
                                            if (landingPlatform != null) {
                                                sites.add(landingPlatform);
                                            }
                                            action.appendEffect(
                                                    new BlowAwayEffect(action, sites) {
                                                        @Override
                                                        protected List<ActionProxy> getBlowAwayActionProxies(SwccgGame game, BlowAwayState blowAwayState) {
                                                            ActionProxy actionProxy = new AbstractActionProxy() {
                                                                @Override
                                                                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                                    List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                                                    String opponent = game.getOpponent(playerId);

                                                                    // Check condition(s)
                                                                    if (TriggerConditions.isBlownAwayCalculateForceLossStep(game, effectResult, bunker)) {

                                                                        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(epicEvent, epicEvent.getCardId());
                                                                        action.skipInitialMessageAndAnimation();
                                                                        // Perform result(s)
                                                                        action.appendEffect(
                                                                                new AddToBlownAwayForceLossEffect(action, opponent, 8));
                                                                        actions.add(action);
                                                                    }
                                                                    return actions;
                                                                }
                                                            };
                                                            return Collections.singletonList(actionProxy);
                                                        }
                                                        @Override
                                                        protected StandardEffect getAdditionalGameTextEffect(SwccgGame game, Action blowAwaySubAction) {
                                                            return new LoseCardFromTableEffect(blowAwaySubAction, epicEvent);
                                                        }
                                                    }
                                            );
                                        }
                                        else {
                                            gameState.sendMessage("Result: Failed");
                                        }
                                    }
                                }
                        );
                    }
                }
        );    }
}
