package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractLocationDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataEqualsCondition;
import com.gempukku.swccgo.cards.effects.RevealCardFromOwnHandEffect;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ActivatedForceResult;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Device
 * Title: Floating Refinery
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print retrieves 2 Force for each cloud sector or gas miner drawn and revealed; the Players Committee Advanced
 * Rulebook (Appendix A) lowered it to 1 (the bare card). The print shows no uniqueness dots; as for every text state,
 * this state keeps the bare card's uniqueness (docs/legacy-versions.md "Same card").
 */
public class Card7_216o extends Card7_216 {
    public Card7_216o() {
        super();
        setGameText("Deploy on a cloud sector (limit one per sector). Force you activate may be drawn into hand (one per turn for each of your Floating Refineries on table). Each cloud sector or gas miner drawn in this way may be revealed to retrieve 2 Force.");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_216", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)
        if (TriggerConditions.forceActivated(game, effectResult, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            PhysicalCard cardActivated = ((ActivatedForceResult) effectResult).getCard();
            if (cardActivated.getZone() == Zone.TOP_OF_FORCE_PILE) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Draw activated Force into hand");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new DrawCardIntoHandFromForcePileEffect(action, playerId) {
                            @Override
                            protected void cardDrawnIntoHand(final PhysicalCard card) {
                                if (Filters.or(Filters.cloud_sector, Filters.gas_miner).accepts(game, card)) {
                                    action.appendEffect(
                                            new PlayoutDecisionEffect(action, playerId,
                                                    new YesNoDecision("Do you want to reveal " + GameUtils.getCardLink(card) + " to retrieve 2 Force?") {
                                                        @Override
                                                        protected void yes() {
                                                            game.getGameState().sendMessage(playerId + " chooses to reveal " + GameUtils.getCardLink(card) + " to retrieve 2 Force");
                                                            action.appendEffect(
                                                                    new RevealCardFromOwnHandEffect(action, playerId, card) {
                                                                        @Override
                                                                        protected void cardRevealed(PhysicalCard cardRevealed) {
                                                                            action.appendEffect(
                                                                                    new RetrieveForceEffect(action, playerId, 2));
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    }
                                            )
                                    );
                                }
                            }
                        }
                );
                action.appendEffect(
                        new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(true))
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
