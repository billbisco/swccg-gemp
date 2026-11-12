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
 * Title: Garouf Lafoe
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Garouf Lafoe]] (Decipher archive scan); superseded by Decipher errata, wiki [[Garouf Lafoe (Errata)]] (Glossary; upper bound November 1998).
 * As printed, the pilot bonus applies only to a starship he pilots ("any starship he pilots"); the errata made it
 * "anything he pilots" (vehicles too). Otherwise this state plays as the bare card (Card2_008).
 */
public class Card2_008o extends Card2_008 {
    public Card2_008o() {
        super();
        setGameText("Adds 1 to power of any starship he pilots. All your characters at same Tatooine site are forfeit +1. All opponent's characters at same Hoth site are forfeit -1. Game text canceled if at same site as a tax collector.");
        setTextState(new TextState("2_8", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.removeIf(modifier -> modifier instanceof AddsPowerToPilotedBySelfModifier);
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1, Filters.starship));
        return modifiers;
    }
}
