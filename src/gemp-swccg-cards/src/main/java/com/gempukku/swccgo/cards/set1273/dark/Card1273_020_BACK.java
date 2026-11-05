package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromCardPileOnBottomOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.ShuffleForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionLimitedToModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Objective
 * Title: A Stunning Move / A Valuable Hostage
 */
public class Card1273_020_BACK extends AbstractObjective {
    public Card1273_020_BACK() {
        super(Side.DARK, 7, Title.A_Valuable_Hostage, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setGameText(Card1273_020.PRINTED_GAMETEXT);
        addIcons(Icon.EPISODE_I, Icon.THEED_PALACE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter opponentsJediStarshipsVehicles = Filters.and(Filters.opponents(self.getOwner()), Filters.or(Filters.Jedi, Filters.starship, Filters.vehicle));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmunityToAttritionLimitedToModifier(self, Filters.and(opponentsJediStarshipsVehicles, Filters.immunityToAttritionMoreThan(5)), 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.cardFlipped(game, effectResult, self)) {
            Filter presenceDroid = Filters.and(Filters.droid, Icon.PRESENCE);
            GameTextActionId gameTextActionId = GameTextActionId.LEGACY__A_VALUABLE_HOSTAGE__TAKE_PRESENCE_DROID;
            List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

            if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
                OptionalGameTextTriggerAction take = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                take.setText("Take a [Presence] droid into hand from Reserve Deck");
                take.setActionMsg("Take a [Presence] droid into hand from Reserve Deck");
                take.appendEffect(
                        new TakeCardIntoHandFromReserveDeckEffect(take, playerId, presenceDroid, true));
                actions.add(take);
            }

            OptionalGameTextTriggerAction retrieve = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            retrieve.setText("Retrieve a [Presence] droid into hand");
            retrieve.setActionMsg("Retrieve a [Presence] droid into hand");
            retrieve.appendEffect(
                    new RetrieveCardIntoHandEffect(retrieve, playerId, presenceDroid));
            actions.add(retrieve);
            return actions;
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        TopLevelGameTextAction deploySite = Card1273_020.deployBlockadeFlagshipSiteAction(playerId, game, self, gameTextSourceCardId);
        if (deploySite != null) {
            actions.add(deploySite);
        }

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__A_VALUABLE_HOSTAGE__REVEAL_CARD_FROM_FORCE_PILE;
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasForcePile(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Search your Force Pile and reveal a card");
            action.setActionMsg("Search your Force Pile and reveal a card");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new ChooseCardFromForcePileEffect(action, playerId) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            final GameState gameState = game.getGameState();
                            final String opponent = game.getOpponent(playerId);
                            action.addAnimationGroup(selectedCard);
                            action.appendEffect(
                                    new PlayoutDecisionEffect(action, opponent,
                                            new MultipleChoiceAwaitingDecision("Opponent revealed " + GameUtils.getCardLink(selectedCard) + " from Force Pile. Choose result", new String[]{"Lose 2 Force to place card on bottom of Used Pile", "Opponent takes card into hand"}) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    if (index == 0) {
                                                        gameState.sendMessage(opponent + " chooses to lose 2 Force and place card on bottom of Used Pile");
                                                        action.appendEffect(
                                                                new LoseForceEffect(action, opponent, 2, true));
                                                        action.appendEffect(
                                                                new PutCardFromCardPileOnBottomOfCardPileEffect(action, playerId, selectedCard, Zone.USED_PILE, false));
                                                    } else {
                                                        gameState.sendMessage(opponent + " chooses to place card in opponent's hand");
                                                        action.appendEffect(
                                                                new TakeCardIntoHandFromForcePileEffect(action, playerId, selectedCard, false));
                                                    }
                                                }
                                            }));
                        }
                    });
            action.appendAfterEffect(
                    new ShuffleForcePileEffect(action, self, playerId));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.isStartOfYourPhase(game, effectResult, Phase.CONTROL, playerId)) {
            int battlegroundsControlled = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.controls(playerId)));
            if (battlegroundsControlled > 0) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.LEGACY__A_VALUABLE_HOSTAGE__CONTROL_PHASE_FORCE_LOSS);
                action.setText("Make opponent lose " + battlegroundsControlled + " Force");
                action.setActionMsg("Make opponent lose 1 Force for each battleground controlled");
                action.appendEffect(
                        new LoseForceEffect(action, opponent, battlegroundsControlled));
                actions.add(action);
            }
        }

        PhysicalCard insidiousPrisoner = Filters.findFirstActive(game, self, Filters.Insidious_Prisoner);
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && insidiousPrisoner != null
                && !Card1273_020.isAtBlockadeFlagshipSite(game, insidiousPrisoner)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }
        return actions;
    }
}
