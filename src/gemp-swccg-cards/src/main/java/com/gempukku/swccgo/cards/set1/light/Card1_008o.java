package com.gempukku.swccgo.cards.set1.light;

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
 * Title: Dutch
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Dutch]]. As printed, the pilot bonus applies only to a starship
 * he pilots ("any starship he pilots"); the errata ([[Dutch (Errata)]], Decipher Glossary Version 2.0 (November 1998))
 * made it "anything he pilots", so vehicles get it
 * too, which is how the current card (Card1_008) plays. Everything else plays as Card1_008.
 */
public class Card1_008o extends Card1_008 {
    public Card1_008o() {
        super();
        setGameText("Adds 2 to power of any starship he pilots. On Gold 1, also adds 1 to maneuver and draws one battle destiny if not able to otherwise. Forfeit +1 to all other Gold Squadron pilots at same location.");
        setTextState(new TextState("1_8", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
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
