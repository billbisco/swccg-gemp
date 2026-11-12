package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Effect
 * Title: Conduct Your Search
 * Text state: o (as printed, 31 October 2001; docs/legacy-versions.md "Text states")
 * <p>
 * The printed text takes a non-Interrupt card with 'door' in title into hand from Reserve Deck once per turn (any
 * phase). Decipher's Glossary Supplement (29 January 2002) gives the current text (Card13_062): deploy such a card
 * from Reserve Deck once during your deploy phase. The Endor scout modifiers are unchanged.
 * Text from wiki.swccg.com [[Conduct Your Search]].
 */
public class Card13_062o extends Card13_062 {
    public Card13_062o() {
        super();
        setGameText("Deploy on table. Once per turn, you may take a non-Interrupt card with 'door' in title into hand from Reserve Deck; reshuffle. At Endor sites where you have a scout, Rebel scouts are power -1 and forfeit -3. (Immune to Alter.)");
        setTextState(new TextState("13_62", "Printed",
                "Reflections III printing (Decipher, 31 October 2001)",
                LocalDate.of(2001, 10, 31), LocalDate.of(2002, 1, 29)));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CONDUCT_YOUR_SEARCH__DOWNLOAD_CARD_WITH_DOOR_IN_TITLE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a non-Interrupt card with 'door' in title into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.non_Interrupt, Filters.or(Filters.titleContains("door"), Filters.titleContains("doors"))), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
