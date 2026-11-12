package com.gempukku.swccgo.cards.set2.dark;

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
 * Title: DS-61-4
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[DS-61-4]] (Decipher archive scan); superseded by Decipher errata, wiki [[DS-61-4 (Errata)]] (Glossary; upper bound November 1998).
 * As printed, the pilot bonus applies only to a starship he pilots ("any starship he pilots"); the errata made it
 * "anything he pilots" (vehicles too). Otherwise this state plays as the bare card (Card2_088).
 */
public class Card2_088o extends Card2_088 {
    public Card2_088o() {
        super();
        setGameText("Adds 3 to power of any starship he pilots. On Black 4, also draws one battle destiny if not able to otherwise. May use 1 Force to search through your Reserve Deck and take one Lone Pilot into your hand. Shuffle, cut and replace.");
        setTextState(new TextState("2_88", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.removeIf(modifier -> modifier instanceof AddsPowerToPilotedBySelfModifier);
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.starship));
        return modifiers;
    }
}
