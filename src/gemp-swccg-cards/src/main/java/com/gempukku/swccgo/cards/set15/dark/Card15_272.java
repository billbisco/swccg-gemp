package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set11.dark.Card11_081;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.Collections;
import java.util.List;

public class Card15_272 extends Card11_081 {
    public Card15_272() {
        setCardSubtype(CardSubtype.LOST);
        setAlternateDestiny(2);
        setLore("");
        setGameText("During your control phase, use 2 Force to target opponent's Podracer with a race total within 4 of one of your race totals. Draw destiny. If destiny > target Podracer's destiny number, opponent may not draw race destiny this turn and target Podracer is 'damaged.' OR Lose 1 Force to remove a race destiny (random selection) from any Podracer and place it in owner's hand.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        addComboCardTitles("Podracer Collision", "Hit Racer");
    }
    @Override public String getTitle() { return "Podracer Collision & Hit Racer"; }
    @Override public Float getDestiny() { return 2f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    private List<PlayInterruptAction> getGameTextTopLevelActionsFromCombinedCard(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final Filter podracerFilter = Filters.and(Filters.Podracer, Filters.hasStacked(Filters.raceDestiny));

        // Check condition(s)
        if (GameConditions.isDuringPodrace(game)
                && GameConditions.canTarget(game, self, podracerFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Return a race destiny to hand");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Podracer", podracerFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedPodracer) {
                            action.addAnimationGroup(targetedPodracer);
                            // Pay cost(s)
                            action.appendCost(
                                    new LoseForceEffect(action, playerId, 1, true));
                            // Allow response(s)
                            action.allowResponses("Return a random race destiny from " + GameUtils.getCardLink(targetedPodracer) + " to hand",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            PhysicalCard finalPodracer = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            if (finalPodracer != null) {
                                                List<PhysicalCard> randomRaceDestiny = GameUtils.getRandomCards(game, Filters.filter(finalPodracer.getCardsStacked(), game, Filters.raceDestiny), 1);
                                                if (!randomRaceDestiny.isEmpty()) {
                                                    action.appendEffect(
                                                            new ReturnCardToHandFromOffTableEffect(action, randomRaceDestiny.get(0)));
                                                }
                                            }
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

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> result = new java.util.ArrayList<>();
        List<PlayInterruptAction> first = super.getGameTextTopLevelActions(playerId, game, self);
        if (first != null) result.addAll(first);
        List<PlayInterruptAction> second = getGameTextTopLevelActionsFromCombinedCard(playerId, game, self);
        if (second != null) result.addAll(second);
        return result;
    }
}
