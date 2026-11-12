package com.gempukku.swccgo.cards.set1.dark;

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
 * Set: Premiere
 * Type: Character
 * Title: DS-61-2
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[DS-61-2]]. As printed, the pilot bonus applies only to a starship
 * he pilots ("any starship he pilots"); the errata ([[DS-61-2 (Errata)]], Decipher Glossary Version 2.0 (November
 * 1998)) made it "anything he pilots", so vehicles get it
 * too, which is how the current card (Card1_173) plays. Everything else plays as Card1_173.
 */
public class Card1_173o extends Card1_173 {
    public Card1_173o() {
        super();
        setGameText("Adds 3 to power of any starship he pilots. On Black 2, also adds 1 to maneuver and may draw one battle destiny if not able to otherwise.");
        setTextState(new TextState("1_173", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        for (Modifier modifier : super.getGameTextWhileActiveInPlayModifiers(game, self)) {
            if (modifier instanceof AddsPowerToPilotedBySelfModifier)
                modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.starship));
            else
                modifiers.add(modifier);
        }
        return modifiers;
    }
}
