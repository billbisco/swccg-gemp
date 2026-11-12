package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Interrupt
 * Subtype: Lost
 * Title: Dark Rage
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Reflections III (31 October 2001) text, from wiki.swccg.com [[Dark Rage]] (Decipher archive scan). As
 * printed the third option targets nothing: if Maul has no combat cards, one of all the opponent's combat cards on
 * table (random selection) goes to the opponent's Used Pile, whatever character it is on. The Players Committee's
 * errata ([[Dark Rage (PC Errata)]], SWCCG 2023 Advanced Rulebook Appendix A) made it "target a Jedi", which is the
 * current card (Card13_064). The other two options play as the current card.
 */
public class Card13_064o extends Card13_064 {
    public Card13_064o() {
        super();
        setGameText("If opponent just initiated a duel, use 3 Force to cancel it. OR Take Deep Hatred into hand from Reserve Deck; reshuffle. OR If Maul has no combat cards, place an opponent's combat card (random selection) in opponent's Used Pile. (Immune to Sense.)");
        setTextState(new TextState("13_64", "Printed", "Reflections III printing (Decipher, 31 October 2001)",
                LocalDate.of(2001, 10, 31), LocalDate.of(2023, 7, 30)));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        List<PlayInterruptAction> current = super.getGameTextTopLevelActions(playerId, game, self);
        if (current != null) {
            for (PlayInterruptAction action : current) {
                if (action.getGameTextActionId() != GameTextActionId.OTHER_CARD_ACTION_3)
                    actions.add(action);
            }
        }

        final String opponent = game.getOpponent(playerId);
        Filter maulFilter = Filters.and(Filters.Maul, Filters.not(Filters.hasStacked(1, Filters.combatCard)));
        if (GameConditions.canSpot(game, self, maulFilter) && !opponentsCombatCards(game, opponent).isEmpty()) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, GameTextActionId.OTHER_CARD_ACTION_3);
            action.setImmuneTo(Title.Sense);
            action.setText("Place an opponent's combat card in Used Pile");
            // Allow response(s)
            action.allowResponses("Place one of " + opponent + "'s combat cards (random selection) in Used Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            List<PhysicalCard> combatCards = opponentsCombatCards(game, opponent);
                            if (!combatCards.isEmpty()) {
                                PhysicalCard randomCombatCard = GameUtils.getRandomCards(game, combatCards, 1).getFirst();
                                // Perform result(s)
                                action.appendEffect(
                                        new PutStackedCardInUsedPileEffect(action, playerId, randomCombatCard, true));
                            }
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    /** The opponent's combat cards on table (stacked on cards in play). */
    private static List<PhysicalCard> opponentsCombatCards(SwccgGame game, String opponent) {
        List<PhysicalCard> combatCards = new ArrayList<PhysicalCard>();
        for (PhysicalCard card : Filters.filterAllOnTable(game, Filters.hasStacked(1, Filters.combatCard))) {
            for (PhysicalCard stacked : game.getGameState().getStackedCards(card)) {
                if (stacked.isCombatCard() && opponent.equals(stacked.getOwner()))
                    combatCards.add(stacked);
            }
        }
        return combatCards;
    }
}
