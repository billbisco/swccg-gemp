package com.gempukku.swccgo.cards.set106.dark;

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
 * Set: Premium (Official Tournament Sealed Deck)
 * Type: Character
 * Subtype: Alien
 * Title: Chall Bekan
 * Text state: o (as printed, May 1998; docs/legacy-versions.md "Text states")
 * <p>
 * The printed text adds 2 to power of any starship he pilots (not vehicles); Decipher Glossary Version 2.0
 * (November 1998) changed it to "anything he pilots", which is the current card (Card106_011). The printed
 * "search your Reserve Deck, take one non-unique alien into hand and reshuffle" plays the same as the current
 * "take one non-unique alien into hand from Reserve Deck; reshuffle". Text from wiki.swccg.com [[Chall Bekan]].
 */
public class Card106_011o extends Card106_011 {
    public Card106_011o() {
        super();
        setGameText("When deployed, you may immediately search your Reserve Deck, take one non-unique alien into hand and reshuffle. Adds 2 to power of any starship he pilots. Your aliens deploy -1 to same or adjacent Tatooine site.");
        setTextState(new TextState("106_11", "Printed",
                "Official Tournament Sealed Deck printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        for (Modifier modifier : super.getGameTextWhileActiveInPlayModifiers(game, self)) {
            if (modifier instanceof AddsPowerToPilotedBySelfModifier)
                modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.starship));
            else
                modifiers.add(modifier);
        }
        return modifiers;
    }
}
