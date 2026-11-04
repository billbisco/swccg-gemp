package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.PlayEpicEventAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DeploySingleCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.effects.PersonaReplaceCharacterEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.StackOneCardFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.StackOneCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromPileEffect;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.CancelCardOnTableResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitedCardToUsedPileFromTableResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;
import com.gempukku.swccgo.logic.timing.results.PlacedCardOutOfPlayFromTableResult;
import com.gempukku.swccgo.logic.timing.results.PutCardInForcePileFromTableResult;
import com.gempukku.swccgo.logic.timing.results.PutCardInReserveDeckFromTableResult;
import com.gempukku.swccgo.logic.timing.results.PutCardInUsedPileFromTableResult;
import com.gempukku.swccgo.logic.timing.results.ReturnedCardToHandFromTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Epic Event
 * Title: Give Yourself To The Dark Side
 */
public class Card1281_018 extends AbstractEpicEventDeployable {
    public Card1281_018() {
        super(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Give Yourself To The Dark Side", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setGameText("If Bring Him Before Me on table, play instead of a Starting Interrupt. Deploy an Endor site and two always [Immune to Alter] Effects from Reserve Deck; reshuffle. Deploy on table; may take Son Of Vader into hand from Reserve Deck (reshuffle) and stack him here. A Powerful Ally: While Son Of Vader on table, Jedi, Rebels, and Rebel starships lose all immunity to attrition and opponent loses 1 Force at the end of each turn. When Son Of Vader leaves table, stack him here. If Luke crossed over, Your Destiny is lost and may deploy (or persona replace) Son Of Vader from here as if from hand.");
        addIcons(Icon.DEATH_STAR_II);
    }

    /**
     * Son Of Vader on table (any card of that persona, including a crossed-over Luke).
     */
    private static Filter sonOfVader() {
        return Filters.persona(Persona.SON_OF_VADER);
    }

    /**
     * The owner's own Son Of Vader card (the one this Epic Event stacks and deploys).
     */
    private static Filter yourSonOfVader(PhysicalCard self) {
        return Filters.and(Filters.owner(self.getOwner()), Filters.character, sonOfVader(), Filters.not(Filters.crossedOver));
    }

    /**
     * A crossed-over Luke (persona Luke, or Son Of Vader once crossed).
     */
    private static Filter crossedOverLuke() {
        return Filters.and(Filters.character, Filters.crossedOver, Filters.or(Filters.Luke, sonOfVader()));
    }

    private static boolean lukeCrossedOver(SwccgGame game, PhysicalCard self) {
        return self.getWhileInPlayData() != null
                || !Filters.filterAllOnTable(game, crossedOverLuke()).isEmpty();
    }

    @Override
    public boolean playableAsStartingInterrupt(SwccgGame game, PhysicalCard self) {
        return !Filters.filterAllOnTable(game, Filters.and(Filters.owner(self.getOwner()), Filters.Bring_Him_Before_Me)).isEmpty();
    }

    @Override
    public PlayCardAction getStartingInterruptAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        if (!playableAsStartingInterrupt(game, self)) {
            return null;
        }
        final PlayEpicEventAction action = new PlayEpicEventAction(self);
        action.setEpicEventState(new EpicEventState(self, EpicEventState.Type.PLAY_INSTEAD_OF_STARTING_INTERRUPT) {
        });
        action.setText("Deploy an Endor site and two Effects from Reserve Deck");
        // Allow response(s)
        action.allowResponses("Deploy an Endor site and two always [Immune to Alter] Effects from Reserve Deck",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.Endor_site, true, false));
                        action.appendEffect(
                                new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), 1, 2, true, true));
                        action.appendEffect(
                                new DeploySingleCardEffect(action, self, Zone.SIDE_OF_TABLE, false, null, false));
                        action.appendEffect(
                                new PlayoutDecisionEffect(action, playerId,
                                        new YesNoDecision("Take Son Of Vader into hand from Reserve Deck and stack him on " + GameUtils.getCardLink(self) + "?") {
                                            @Override
                                            protected void yes() {
                                                action.appendEffect(
                                                        new StackCardFromReserveDeckEffect(action, self, yourSonOfVader(self), true));
                                            }
                                        }));
                    }
                });
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelImmunityToAttritionModifier(self, Filters.or(Filters.Jedi, Filters.Rebel, Filters.Rebel_starship),
                new OnTableCondition(self, sonOfVader())));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Opponent loses 1 Force at the end of each turn while Son Of Vader on table
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)
                && GameConditions.canSpot(game, self, sonOfVader())) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            actions.add(action);
        }

        // When Son Of Vader leaves table, stack him here
        if (TriggerConditions.leavesTable(game, effectResult, yourSonOfVader(self))) {
            final PhysicalCard leftTable = getCardThatLeftTable(effectResult);
            if (leftTable != null && leftTable.getStackedOn() != self && !leftTable.getZone().isInPlay()) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Stack " + GameUtils.getFullName(leftTable) + " here");
                // Perform result(s)
                stackHere(action, leftTable, self);
                actions.add(action);
            }
        }

        // If Luke crossed over, Your Destiny is lost
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            final boolean crossedLukeOnTable = !Filters.filterAllOnTable(game, crossedOverLuke()).isEmpty();
            final Collection<PhysicalCard> yourDestiny = Filters.filterActive(game, self, Filters.Your_Destiny);
            if ((crossedLukeOnTable && self.getWhileInPlayData() == null)
                    || (lukeCrossedOver(game, self) && !yourDestiny.isEmpty())) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Luke crossed over");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                if (self.getWhileInPlayData() == null) {
                                    self.setWhileInPlayData(new WhileInPlayData(true));
                                }
                            }
                        });
                if (!yourDestiny.isEmpty()) {
                    action.appendEffect(
                            new LoseCardsFromTableEffect(action, yourDestiny));
                }
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        if (!lukeCrossedOver(game, self)
                || !GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                || !GameConditions.hasStackedCards(game, self, yourSonOfVader(self))) {
            return actions;
        }
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__GIVE_YOURSELF_TO_THE_DARK_SIDE__DEPLOY_SON_OF_VADER;

        // Deploy Son Of Vader from here as if from hand
        {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Son Of Vader from here");
            // Perform result(s)
            action.appendEffect(
                    new DeployStackedCardEffect(action, self, yourSonOfVader(self), false));
            actions.add(action);
        }

        // Persona replace (crossed-over Luke) with Son Of Vader from here
        final Collection<PhysicalCard> crossedLukes = Filters.filterAllOnTable(game, crossedOverLuke());
        if (crossedLukes.size() == 1) {
            final PhysicalCard crossedLuke = crossedLukes.iterator().next();
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Persona replace " + GameUtils.getFullName(crossedLuke) + " with Son Of Vader from here");
            // Perform result(s)
            action.appendEffect(
                    new ChooseStackedCardEffect(action, playerId, self, yourSonOfVader(self)) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.appendEffect(
                                    new PersonaReplaceCharacterEffect(action, crossedLuke, selectedCard));
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    private static PhysicalCard getCardThatLeftTable(EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.LOST_FROM_TABLE || effectResult.getType() == EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE)
            return ((LostFromTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.FORFEITED_TO_USED_PILE_FROM_TABLE)
            return ((ForfeitedCardToUsedPileFromTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.CANCELED_ON_TABLE)
            return ((CancelCardOnTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.RETURNED_TO_HAND_FROM_TABLE)
            return ((ReturnedCardToHandFromTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.PUT_IN_RESERVE_DECK_FROM_TABLE)
            return ((PutCardInReserveDeckFromTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.PUT_IN_FORCE_PILE_FROM_TABLE)
            return ((PutCardInForcePileFromTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.PUT_IN_USED_PILE_FROM_TABLE)
            return ((PutCardInUsedPileFromTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.PLACED_OUT_OF_PLAY_FROM_TABLE)
            return ((PlacedCardOutOfPlayFromTableResult) effectResult).getCard();
        return null;
    }

    private static void stackHere(Action action, final PhysicalCard card, final PhysicalCard self) {
        Zone zone = GameUtils.getZoneFromZoneTop(card.getZone());
        if (zone == Zone.LOST_PILE) {
            action.appendEffect(new StackOneCardFromLostPileEffect(action, card, self, false, false, true));
        }
        else if (zone == Zone.HAND) {
            action.appendEffect(new StackOneCardFromHandEffect(action, card, self, false));
        }
        else if (zone == Zone.FORCE_PILE) {
            action.appendEffect(new StackOneCardFromForcePileEffect(action, card, self, false));
        }
        else if (zone == Zone.OUT_OF_PLAY) {
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            game.getGameState().removeCardsFromZone(Collections.singleton(card));
                            game.getGameState().stackCard(card, self, false, false, false);
                        }
                    });
        }
        else {
            action.appendEffect(new StackOneCardFromPileEffect(action, self.getOwner(), card, self, false, false, false));
        }
    }
}
