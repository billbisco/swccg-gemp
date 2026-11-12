package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractLocationDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalCarbonFreezingDestinyModifier;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Title: Carbonite Chamber Console
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Carbonite Chamber Console]] (Decipher archive scan): the Reserve Deck search has no
 * once-per-turn limit. Decipher's Glossary Version 2.0 (November 1998) made it once during each of your turns, which the
 * current card (Card5_107) plays, so this state differs only in that limit.
 */
public class Card5_107o extends Card5_107 {
    public Card5_107o() {
        super();
        setGameText("Deploy on Carbonite Chamber. Adds 3 to Carbon-Freezing destiny. Also, during your turn, you may use 1 Force to search your Reserve Deck; take one Ugnaught, Prepare The Chamber or Carbon-Freezing into hand and reshuffle.");
        setTextState(new TextState("5_107", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CARBONITE_CHAMBER_CONSOLE__UPLOAD_UGNAUGHT_PREPARE_THE_CHAMBER_OR_CARBON_FREEZING;

        // Check condition(s)
        if (GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take an Ugnaught, Prepare The Chamber, or Carbon-Freezing into hand from Reserve Deck");
            // As printed: no once-per-turn limit
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Ugnaught, Filters.Prepare_The_Chamber, Filters.Carbon_Freezing), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
