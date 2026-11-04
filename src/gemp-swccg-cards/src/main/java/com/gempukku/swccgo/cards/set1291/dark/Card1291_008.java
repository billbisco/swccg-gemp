package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
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
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Effect
 * Title: Arena Pillars
 */
public class Card1291_008 extends AbstractNormalEffect {
    public Card1291_008() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Arena Pillars", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setGameText("Deploy on Petranaki Arena. During your control phase, may target an imprisoned captive here: each player draws destiny and adds 1 for each of their cards with power present (2 for [Separatist] cards). If your total higher, target 'executed' (lost) and opponent loses 2 Force. (Immune to Alter)");
        addIcons(Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Geonosis_Petranaki_Arena;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);
        final Filter imprisonedCaptiveHere = Filters.and(Filters.captive, Filters.imprisoned, Filters.at(Filters.hasAttached(self)));

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, imprisonedCaptiveHere)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Target an imprisoned captive to 'execute'");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose imprisoned captive", SpotOverride.INCLUDE_CAPTIVE, TargetingReason.TO_BE_LOST, imprisonedCaptiveHere) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard captive) {
                            action.addAnimationGroup(captive);
                            // Allow response(s)
                            action.allowResponses("Target " + GameUtils.getCardLink(captive) + " to be 'executed'",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> yourDestinyCardDraws, List<Float> yourDestinyDrawValues, final Float yourTotalDestiny) {
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, opponent) {
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> opponentsDestinyCardDraws, List<Float> opponentsDestinyDrawValues, Float opponentsTotalDestiny) {
                                                                            GameState gameState = game.getGameState();
                                                                            float yourTotal = (yourTotalDestiny != null ? yourTotalDestiny : 0) + powerPresentBonus(game, self, playerId);
                                                                            float opponentsTotal = (opponentsTotalDestiny != null ? opponentsTotalDestiny : 0) + powerPresentBonus(game, self, opponent);
                                                                            gameState.sendMessage(playerId + "'s total: " + GuiUtils.formatAsString(yourTotal));
                                                                            gameState.sendMessage(opponent + "'s total: " + GuiUtils.formatAsString(opponentsTotal));
                                                                            if (yourTotalDestiny != null && yourTotal > opponentsTotal) {
                                                                                gameState.sendMessage("Result: " + GameUtils.getCardLink(finalTarget) + " 'executed'");
                                                                                action.appendEffect(
                                                                                        new LoseCardFromTableEffect(action, finalTarget));
                                                                                action.appendEffect(
                                                                                        new LoseForceEffect(action, opponent, 2));
                                                                            } else {
                                                                                gameState.sendMessage("Result: No result");
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    /**
     * Adds 1 for each of the player's cards with power present at the site this Effect is on (2 for [Separatist] cards).
     */
    private static float powerPresentBonus(SwccgGame game, PhysicalCard self, String player) {
        Filter withPowerPresent = Filters.and(Filters.owner(player), Filters.hasPowerDefined, Filters.presentAt(Filters.hasAttached(self)));
        int all = Filters.countActive(game, self, withPowerPresent);
        int separatist = Filters.countActive(game, self, Filters.and(withPowerPresent, Icon.SEPARATIST));
        return all + separatist;
    }
}
