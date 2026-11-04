package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RecordUtinniEffectCompletedEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Mission
 * Title: Kessel Run (V)
 *
 * State is kept in the card's while-in-play data: text "TARGETED", "REACHED:<parsec total>" or
 * "COMPLETED:<parsec total>", with the cards [target, original system].
 */
public class Card1283_026 extends AbstractMission {
    private static final String TARGETED = "TARGETED";
    private static final String REACHED = "REACHED:";
    private static final String COMPLETED = "COMPLETED:";

    public Card1283_026() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Kessel_Run, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V, false);
        setVirtualSuffix(true);
        setGameText("Deploy on Infiltration or non-[Virtual Block 4] Watch Your Step. May [download] Kessel. May target a scoundrel or smuggler at a non-Kessel system (target is immune to Set For Stun). If target is lost, place cards here in Used Pile. When target reaches Kessel, draw five 'parsec' destinies and stack them here (or this Mission is lost). Whenever you win a battle (or at start of your turn), take a card here into hand. When target returns to original system: Mission completed (counts as two Kessel Runs), Infiltration flips, and may [upload] a card. If total 'parsec' destiny < 12 when completed, may subtract 1 from each of opponent's destiny draws. May not be canceled.");
        addIcons(Icon.SETUP);
    }

    private static Filter infiltration() {
        return Filters.and(Filters.Objective, Filters.title("Infiltration"));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(infiltration(),
                Filters.and(Filters.Objective, Filters.Watch_Your_Step, Filters.not(Icon.LEGACY_BLOCK_4))));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(SwccgGame game, PhysicalCard self) {
        return Filters.Objective;
    }

    // ---------- state helpers

    private static String state(PhysicalCard self) {
        WhileInPlayData data = self.getWhileInPlayData();
        return data != null ? data.getTextValue() : null;
    }

    private static PhysicalCard target(PhysicalCard self) {
        WhileInPlayData data = self.getWhileInPlayData();
        return (data != null && data.getPhysicalCards().size() == 2) ? data.getPhysicalCards().get(0) : null;
    }

    private static PhysicalCard originalSystem(PhysicalCard self) {
        WhileInPlayData data = self.getWhileInPlayData();
        return (data != null && data.getPhysicalCards().size() == 2) ? data.getPhysicalCards().get(1) : null;
    }

    private static float parsecTotal(PhysicalCard self) {
        String state = state(self);
        if (state == null || state.indexOf(':') < 0) {
            return 0;
        }
        return Float.parseFloat(state.substring(state.indexOf(':') + 1));
    }

    private static boolean isCompleted(PhysicalCard self) {
        String state = state(self);
        return state != null && state.startsWith(COMPLETED);
    }

    private static void setState(PhysicalCard self, String state) {
        self.setWhileInPlayData(new WhileInPlayData(state, Arrays.asList(target(self), originalSystem(self))));
    }

    /**
     * Filter accepting the current (not yet completed) target of this Mission.
     */
    private static Filter currentTarget(final PhysicalCard self) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard target = target(self);
                return target != null && !isCompleted(self) && target.getCardId() == physicalCard.getCardId();
            }
        };
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeCanceledModifier(self, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, currentTarget(self), Title.Set_For_Stun));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId deployKesselId = GameTextActionId.LEGACY__KESSEL_RUN_V__DEPLOY_KESSEL;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployKesselId, Title.Kessel)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, deployKesselId);
            action.setText("Deploy Kessel from Reserve Deck");
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Kessel_system, true));
            actions.add(action);
        }

        GameTextActionId targetId = GameTextActionId.LEGACY__KESSEL_RUN_V__TARGET;
        final Filter targetFilter = Filters.and(Filters.your(self), Filters.or(Filters.scoundrel, Filters.smuggler),
                Filters.at(Filters.and(Filters.system, Filters.not(Filters.Kessel_system))));
        if (state(self) == null
                && GameConditions.canTarget(game, self, targetFilter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, targetId);
            action.setText("Target a scoundrel or smuggler");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose scoundrel or smuggler", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Target " + GameUtils.getCardLink(targetedCard) + " to make a Kessel Run",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new PassthruEffect(action) {
                                                        @Override
                                                        protected void doPlayEffect(SwccgGame game) {
                                                            PhysicalCard system = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), finalTarget);
                                                            if (system != null && Filters.system.accepts(game, system)) {
                                                                self.setWhileInPlayData(new WhileInPlayData(TARGETED, Arrays.asList(finalTarget, system)));
                                                                game.getGameState().sendMessage(GameUtils.getCardLink(self) + " targets " + GameUtils.getCardLink(finalTarget) + " at " + GameUtils.getCardLink(system));
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Whenever you win a battle (or at start of your turn), take a card here into hand.
        if ((TriggerConditions.wonBattle(game, effectResult, playerId) || TriggerConditions.isStartOfYourTurn(game, effectResult, playerId))
                && GameConditions.hasStackedCards(game, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Take a card here into hand");
            action.appendEffect(
                    new TakeStackedCardIntoHandEffect(action, playerId, self));
            actions.add(action);
        }

        if (!TriggerConditions.isTableChanged(game, effectResult)) {
            return actions.isEmpty() ? null : actions;
        }

        String state = state(self);
        final PhysicalCard target = target(self);
        if (state == null || isCompleted(self) || target == null) {
            return actions.isEmpty() ? null : actions;
        }

        // If target is lost, place cards here in Used Pile.
        if (!Filters.onTable.accepts(game, target)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Place cards here in Used Pile");
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setWhileInPlayData(null);
                        }
                    });
            if (GameConditions.hasStackedCards(game, self)) {
                action.appendEffect(
                        new PutStackedCardsInUsedPileEffect(action, playerId, new ArrayList<PhysicalCard>(Filters.filterStacked(game, Filters.stackedOn(self))), false));
            }
            actions.add(action);
            return actions;
        }

        // When target reaches Kessel, draw five 'parsec' destinies and stack them here (or this Mission is lost).
        if (TARGETED.equals(state)
                && Filters.at(Filters.Kessel_system).accepts(game, target)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Draw five 'parsec' destinies");
            action.appendUsage(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            setState(self, REACHED + "0");
                        }
                    });
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId, 5) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();
                            float total = 0;
                            for (Float value : destinyDrawValues) {
                                if (value != null) {
                                    total += value;
                                }
                            }
                            for (PhysicalCard card : destinyCardDraws) {
                                if (card != null) {
                                    Zone zone = GameUtils.getZoneFromZoneTop(card.getZone());
                                    if (zone == Zone.USED_PILE || zone == Zone.UNRESOLVED_DESTINY_DRAW) {
                                        gameState.removeCardFromZone(card);
                                        gameState.stackCard(card, self, false, false, false);
                                    }
                                }
                            }
                            if (totalDestiny == null || destinyDrawValues.size() < 5) {
                                gameState.sendMessage("Result: Fewer than five 'parsec' destinies drawn; " + GameUtils.getCardLink(self) + " is lost");
                                action.appendEffect(
                                        new LoseCardFromTableEffect(action, self));
                                return;
                            }
                            gameState.sendMessage("Total 'parsec' destiny: " + GuiUtils.formatAsString(total));
                            setState(self, REACHED + total);
                        }
                    });
            actions.add(action);
            return actions;
        }

        // When target returns to original system: Mission completed (counts as two Kessel Runs), Infiltration flips,
        // and may [upload] a card.
        PhysicalCard original = originalSystem(self);
        if (state.startsWith(REACHED) && original != null
                && Filters.at(Filters.and(Filters.system, Filters.sameTitle(original))).accepts(game, target)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Complete Kessel Run");
            final float total = parsecTotal(self);
            action.appendUsage(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            setState(self, COMPLETED + total);
                        }
                    });
            action.appendEffect(
                    new RecordUtinniEffectCompletedEffect(action, self));
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            // Counts as two Kessel Runs
                            game.getModifiersQuerying().completedUtinniEffect(playerId, self);
                        }
                    });
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            PhysicalCard infiltration = Filters.findFirstActive(game, self, Filters.and(Filters.owner(playerId), infiltration()));
                            if (infiltration != null && GameConditions.canBeFlipped(game, infiltration)) {
                                action.appendEffect(
                                        new FlipCardEffect(action, infiltration));
                            }
                            if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, GameTextActionId.LEGACY__KESSEL_RUN_V__TAKE_CARD_FROM_RESERVE_DECK)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Do you want to take a card into hand from Reserve Deck?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.appendEffect(
                                                                new TakeCardIntoHandFromReserveDeckEffect(action, playerId, true));
                                                    }

                                                    @Override
                                                    protected void no() {
                                                    }
                                                }));
                            }
                        }
                    });
            actions.add(action);
            return actions;
        }
        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        if (isCompleted(self)
                && parsecTotal(self) < 12
                && TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.LEGACY__KESSEL_RUN_V__SUBTRACT_FROM_DESTINY);
            action.setText("Subtract 1 from opponent's destiny");
            action.appendEffect(
                    new ModifyDestinyEffect(action, -1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        String state = state(self);
        PhysicalCard target = target(self);
        if (state == null || target == null) {
            return null;
        }
        String info = "Target: " + GameUtils.getCardLink(target) + ", from " + GameUtils.getCardLink(originalSystem(self));
        if (!TARGETED.equals(state)) {
            info += ", 'parsec' total = " + GuiUtils.formatAsString(parsecTotal(self)) + (isCompleted(self) ? " (completed)" : "");
        }
        return info;
    }
}
