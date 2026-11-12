package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ProbeSystemEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ProbedHiddenBasedResult;
import com.gempukku.swccgo.logic.timing.results.ProbedSystemResult;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Objective
 * Title: Systems Will Slip Through Your Fingers
 * Text state: o (back of Card7_136o, as printed, 1999)
 * <p>
 * The print lets opponent 'probe' during any draw phase; Decipher's Glossary Supplement (2002) moved it to the deploy
 * phase (Card7_136d_BACK and the bare Card7_136_BACK). Everything else plays as the bare back side.
 */
public class Card7_136o_BACK extends Card7_136_BACK {
    public Card7_136o_BACK() {
        super();
        setGameText("While this side up, to draw a card from Force Pile, opponent must first use 1 Force. For each battleground system you control, you may cancel one opponent's Force drain (limit twice per turn). You may not deploy any systems. At each system opponent occupies during any draw phase, opponent may 'probe' there by placing one card from hand face down beneath that system. Place out of play if \"Hidden Base\" system is 'probed;' Dark Side places 'probe' cards in Used Pile (and may retrieve 1 Force for each Probe Droid used to 'probe').");
        setTextState(new TextState("7_136_BACK", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2002, 1, 29)));
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s): printed, opponent may 'probe' during any draw phase
        if (GameConditions.isEitherPlayersPhase(game, Phase.DRAW)
                && GameConditions.hasHand(game, playerId)) {
            Filter systemsFilter = Filters.and(Filters.system, Filters.occupies(playerId), Filters.not(Filters.hasStacked(Filters.probeCard)));
            if (GameConditions.canSpotLocation(game, systemsFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("'Probe' a system");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose system to 'probe'", systemsFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("'Probe' " + GameUtils.getCardLink(targetedCard),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ProbeSystemEffect(action, targetedCard));
                                            }
                                        }
                                );
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
