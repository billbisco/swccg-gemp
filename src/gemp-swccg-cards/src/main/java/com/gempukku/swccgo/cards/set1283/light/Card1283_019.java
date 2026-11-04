package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Character
 * Subtype: Alien
 * Title: Uutkik (V)
 */
public class Card1283_019 extends AbstractAlien {
    public Card1283_019() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, "Uutkik", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Experienced Jawa thief. Pilfers equipment and hijacks vehicles from unwary bystanders in Mos Eisley. Het Nkik's ugliest cousin.");
        setGameText("During your move phase, may target a device here. Draw destiny. Device 'pilfered' (lost) if destiny > 4. Once per game, if present at a battleground during opponent's turn, may steal a device, weapon, or transport vehicle from opponent's Lost pile into hand.");
        addKeywords(Keyword.THIEF);
        setSpecies(Species.JAWA);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId pilferId = GameTextActionId.LEGACY__UUTKIK_V__PILFER_DEVICE;
        final Filter deviceHere = Filters.and(Filters.device, Filters.here(self));
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, pilferId, Phase.MOVE)
                && GameConditions.canTarget(game, self, TargetingReason.TO_BE_LOST, deviceHere)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, pilferId);
            action.setText("Target a device to 'pilfer'");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose device", TargetingReason.TO_BE_LOST, deviceHere) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard device) {
                            action.addAnimationGroup(device);
                            action.allowResponses("Target " + GameUtils.getCardLink(device) + " to be 'pilfered'",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard finalDevice = targetingAction.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }
                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            if (totalDestiny > 4) {
                                                                gameState.sendMessage("Result: " + GameUtils.getCardLink(finalDevice) + " 'pilfered'");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, finalDevice));
                                                            } else {
                                                                gameState.sendMessage("Result: No result");
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }

        GameTextActionId stealId = GameTextActionId.LEGACY__UUTKIK_V__STEAL_FROM_LOST_PILE;
        if (GameConditions.isOpponentsTurn(game, playerId)
                && GameConditions.isPresentAt(game, self, Filters.battleground)
                && GameConditions.isOncePerGame(game, self, stealId)
                && GameConditions.canStealCardsFromLostPile(game, playerId, self, stealId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, stealId);
            action.setText("Steal from opponent's Lost Pile");
            action.setActionMsg("Steal a device, weapon, or transport vehicle into hand from opponent's Lost Pile");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new StealCardIntoHandFromLostPileEffect(action, playerId, Filters.or(Filters.device, Filters.weapon, Filters.transport_vehicle)));
            actions.add(action);
        }
        return actions;
    }
}
