package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacyReputation;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromPileEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Objective
 * Title: Contract Killers / Feared Throughout The Galaxy
 */
public class Card1281_024_BACK extends AbstractObjective {
    public Card1281_024_BACK() {
        super(Side.DARK, 7, "Feared Throughout The Galaxy", ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setGameText(Card1281_024.SHEET_GAMETEXT);
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final String playerId = self.getOwner();
        final Filter assassinsInBattle = Filters.and(Filters.your(playerId), Filters.assassin, Filters.not(Filters.Galen), Filters.participatingInBattle);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return gameState.isDuringBattle()
                        && Filters.countActive(gameState.getGame(), self, assassinsInBattle) >= 2;
            }
        }, 1, playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        final String opponent = game.getOpponent(playerId);

        // Once per turn, if opponent just lost a character, an assassin there may gain 'reputation'
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__FEARED_THROUGHOUT_THE_GALAXY__GAIN_REPUTATION;
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.owner(opponent), Filters.character), Filters.any)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasLostPile(game, playerId)) {
            final PhysicalCard location = ((LostFromTableResult) effectResult).getFromLocation();
            final Filter assassinThere = Filters.and(Filters.your(playerId), Filters.assassin, Filters.at(location));
            if (GameConditions.canSpot(game, self, assassinThere)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Have an assassin gain 'reputation'");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new ChooseCardOnTableEffect(action, playerId, "Choose assassin to gain 'reputation'", assassinThere) {
                            @Override
                            protected void cardSelected(final PhysicalCard assassin) {
                                action.addAnimationGroup(assassin);
                                final PhysicalCard aimHigh = Filters.findFirstActive(game, self, Filters.and(Filters.owner(opponent), Filters.title(Title.Aim_High)));
                                if (aimHigh != null) {
                                    // Aim High: opponent must first use 1 Force or that retrieval is canceled.
                                    if (GameConditions.canUseForce(game, playerId, 1)) {
                                        action.appendEffect(
                                                new PlayoutDecisionEffect(action, playerId,
                                                        new YesNoDecision("Aim High: use 1 Force to proceed with retrieval?") {
                                                            @Override
                                                            protected void yes() {
                                                                action.appendEffect(
                                                                        new UseForceEffect(action, playerId, 1));
                                                                appendRetrieveAndStack(action, game, playerId, assassin);
                                                            }

                                                            @Override
                                                            protected void no() {
                                                                game.getGameState().sendMessage("Retrieval canceled by " + GameUtils.getCardLink(aimHigh));
                                                            }
                                                        }));
                                    }
                                    else {
                                        game.getGameState().sendMessage("Retrieval canceled by " + GameUtils.getCardLink(aimHigh));
                                    }
                                }
                                else {
                                    appendRetrieveAndStack(action, game, playerId, assassin);
                                }
                            }
                        });
                actions.add(action);
            }
        }

        // Assassins with 'reputation' may add 1 to Force drains where present
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId,
                Filters.wherePresent(self, Filters.and(Filters.your(playerId), LegacyReputation.assassinWithReputation())))) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 1 to Force drain");
            // Perform result(s)
            action.appendEffect(
                    new AddToForceDrainEffect(action, 1));
            actions.add(action);
        }
        return actions;
    }

    private static void appendRetrieveAndStack(final OptionalGameTextTriggerAction action, SwccgGame game, final String playerId, final PhysicalCard assassin) {
        action.appendEffect(
                new ChooseCardFromPileEffect(action, playerId, Zone.LOST_PILE, playerId, Filters.mayContributeToForceRetrieval) {
                    @Override
                    protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                        action.appendEffect(
                                new StackOneCardFromPileEffect(action, playerId, selectedCard, assassin, true, false, false));
                    }

                    @Override
                    public String getChoiceText(int numCardsToChoose) {
                        return "Choose card to retrieve and stack face down on " + GameUtils.getCardLink(assassin);
                    }
                });
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // During your control phase, opponent loses 1 Force
        if (TriggerConditions.isStartOfYourPhase(game, effectResult, Phase.CONTROL, playerId)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            actions.add(action);
        }

        // Flip this card at the end of any turn if opponent's non-droid character at a battleground site (unless Death Mark is on your side of the table)
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && Card1281_024.opponentsNonDroidCharacterAtBattlegroundSite(game, self)
                && !Card1281_024.deathMarkOnYourSideOfTable(game, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }
        return actions;
    }
}
