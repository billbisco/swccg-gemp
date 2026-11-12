package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Title: Wedge Antilles
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Wedge Antilles]] (Decipher archive scan); superseded by Decipher errata, wiki [[Wedge Antilles (Errata)]] (Glossary; upper bound November 1998).
 * As printed, the pilot bonus applies only to a starship he pilots ("any starship he pilots"); the errata made it
 * "anything he pilots" (vehicles too). Otherwise this state plays as the bare card (Card2_023).
 */
public class Card2_023o extends Card2_023 {
    public Card2_023o() {
        super();
        setGameText("Adds 3 to power of any starship he pilots. On Red 2, also adds 2 to maneuver and draws one battle destiny if not able to otherwise. May use 1 Force to search your Reserve Deck and take one Corellian Slip into hand. Shuffle, cut and replace.");
        setTextState(new TextState("2_23", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.removeIf(modifier -> modifier instanceof AddsPowerToPilotedBySelfModifier);
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.starship));
        return modifiers;
    }
}
