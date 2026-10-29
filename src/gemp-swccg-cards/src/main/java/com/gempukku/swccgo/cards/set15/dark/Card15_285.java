package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealOpponentsHandEffect;
import com.gempukku.swccgo.cards.set11.dark.Card11_087;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import java.util.LinkedList;
import java.util.List;

public class Card15_285 extends Card11_087 {
    public Card15_285() {
        setCardSubtype(CardSubtype.LOST_OR_STARTING);
        setAlternateDestiny(3);
        setLore("");
        setGameText("LOST: Use 3 Force to reveal opponent's hand. All cards opponent has 3 or more of in hand are lost. OR Use 1 Force to 'repair' your Podracer. OR Target a Podracer. Draw destiny. If destiny > Podracer's destiny number, target Podracer is 'damaged.' STARTING: Deploy Podrace Arena (with a Podracer, opponent may also deploy a Podracer there), Boonta Eve Podrace, and any Effect that deploys for free. Place Interrupt in Reserve Deck.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        addComboCardTitles("Start Your Engines!", "Pit Crews");
    }
    @Override public String getTitle() { return "Start Your Engines! & Pit Crews"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        final float action1ForceCost = 3;
        if (GameConditions.hasHand(game, opponent)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, action1ForceCost)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Reveal opponent's hand");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, action1ForceCost));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RevealOpponentsHandEffect(action, playerId) {
                                        @Override
                                        protected void cardsRevealed(List<PhysicalCard> revealedCards) {
                                            action.appendEffect(
                                                    new LoseCardsFromHandEffect(action, opponent, Filters.and(Filters.threeOrMoreOfInHand(opponent), Filters.canBeTargetedBy(self))));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        Filter yourDamagedPodracerFilter = Filters.and(Filters.your(self), Filters.damaged, Filters.Podracer);

        // Check condition(s)
        final float action2ForceCost = 1;
        if (GameConditions.isDuringPodrace(game)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, action2ForceCost)
                && GameConditions.canTarget(game, self, yourDamagedPodracerFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("'Repair' a Podracer");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Podracer", yourDamagedPodracerFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedPodracer) {
                            action.addAnimationGroup(targetedPodracer);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, action2ForceCost));
                            // Allow response(s)
                            action.allowResponses("'Repair' " + GameUtils.getCardLink(targetedPodracer),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            PhysicalCard finalPodracer = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new RepairPodracerEffect(action, finalPodracer));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringPodrace(game)
                && GameConditions.canTarget(game, self, Filters.Podracer)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Make a Podracer 'damaged'");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Podracer", Filters.Podracer) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedPodracer) {
                            action.addAnimationGroup(targetedPodracer);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedPodracer) + " 'damaged'",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            final PhysicalCard finalPodracer = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            float podracerDestiny = game.getModifiersQuerying().getDestiny(game.getGameState(), finalPodracer);
                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            gameState.sendMessage("Podracer's destiny: " + GuiUtils.formatAsString(podracerDestiny));
                                                            if (totalDestiny > podracerDestiny) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new DamagePodracerEffect(action, finalPodracer));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
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
}
