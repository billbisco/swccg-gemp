package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set1.dark.Card1_259;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.List;

public class Card15_262 extends Card1_259 {
    public Card15_262() {
        setLore(""); setCardSubtype(CardSubtype.USED_OR_LOST);
        setGameText("USED: Use 1 Force to search opponent's Used Pile and place any droids you find there to opponent's Lost Pile. LOST: Use X Force to destroy any one droid on table where X = that droid's deploy cost. OR Use 1 Force to cancel an opponent's Force drain at a location. Draw one destiny for each character, starship, and vehicle the opponent has present (limit 2). Compare your destiny total with opponent's total power. Player with lowest total loses Force equal to difference.");
        addComboCardTitles(Title.Look_Sir_Droids,Title.Counter_Assault); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Look Sir, Droids & Counter Assault"; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected CardSubtype getSearchUsedPileSubtype() { return CardSubtype.USED; }
    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent)
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final PhysicalCard forceDrainLocation = game.getGameState().getForceDrainLocation();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Cancel Force drain");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelForceDrainEffect(action));
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            final GameState gameState = game.getGameState();
                                            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                            int numDraws = Filters.countActive(game, self, Filters.and(Filters.opponents(self),
                                                    Filters.or(Filters.character, Filters.starship, Filters.vehicle), Filters.present(forceDrainLocation)));
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId, Math.min(2,numDraws)) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                            if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.COUNTER_ASSAULT__ADD_DESTINY_TO_TOTAL)) {
                                                                action.appendEffect(
                                                                        new DrawDestinyEffect(action, opponent, 1) {
                                                                            @Override
                                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws2, List<Float> destinyDrawValues2, Float totalDestiny2) {
                                                                                gameState.sendMessage("Destiny total: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                                                                                float totalPower = modifiersQuerying.getTotalPowerAtLocation(gameState, forceDrainLocation, opponent, false, false);
                                                                                totalPower += (totalDestiny2 != null ? totalDestiny2 : 0);
                                                                                gameState.sendMessage("Power total: " + GuiUtils.formatAsString(totalPower));
                                                                                if ((totalDestiny != null ? totalDestiny : 0) < totalPower) {
                                                                                    float forceToLose = totalPower - (totalDestiny != null ? totalDestiny : 0);
                                                                                    gameState.sendMessage("Result: " + playerId + " loses " + GuiUtils.formatAsString(forceToLose) + " Force");
                                                                                    action.appendEffect(
                                                                                            new LoseForceEffect(action, playerId, forceToLose));
                                                                                }
                                                                                else if ((totalDestiny != null ? totalDestiny : 0) > totalPower) {
                                                                                    float forceToLose = (totalDestiny != null ? totalDestiny : 0) - totalPower;
                                                                                    gameState.sendMessage("Result: " + opponent + " loses " + GuiUtils.formatAsString(forceToLose) + " Force");
                                                                                    action.appendEffect(
                                                                                            new LoseForceEffect(action, opponent, forceToLose));
                                                                                }
                                                                                else {
                                                                                    gameState.sendMessage("Result: No result");
                                                                                }
                                                                            }
                                                                        }
                                                                );
                                                            }
                                                            else {
                                                                gameState.sendMessage("Destiny total: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                                                                float totalPower = modifiersQuerying.getTotalPowerAtLocation(gameState, forceDrainLocation, opponent, false, false);
                                                                gameState.sendMessage("Power total: " + GuiUtils.formatAsString(totalPower));
                                                                if ((totalDestiny != null ? totalDestiny : 0) < totalPower) {
                                                                    float forceToLose = totalPower - (totalDestiny != null ? totalDestiny : 0);
                                                                    gameState.sendMessage("Result: " + playerId + " loses " + GuiUtils.formatAsString(forceToLose) + " Force");
                                                                    action.appendEffect(
                                                                            new LoseForceEffect(action, playerId, forceToLose));
                                                                }
                                                                else if ((totalDestiny != null ? totalDestiny : 0) > totalPower) {
                                                                    float forceToLose = (totalDestiny != null ? totalDestiny : 0) - totalPower;
                                                                    gameState.sendMessage("Result: " + opponent + " loses " + GuiUtils.formatAsString(forceToLose) + " Force");
                                                                    action.appendEffect(
                                                                            new LoseForceEffect(action, opponent, forceToLose));
                                                                }
                                                                else {
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
            return Collections.singletonList(action);
        }
        return null;
    }
}
