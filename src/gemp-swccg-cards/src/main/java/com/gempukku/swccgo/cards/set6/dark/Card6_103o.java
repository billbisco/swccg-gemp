package com.gempukku.swccgo.cards.set6.dark;

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
 * Set: Jabba's Palace
 * Title: Fozec
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Fozec]] (Decipher archive scan); superseded by Decipher Glossary Version 2.0 (November 1998), whose text the current card keeps.
 * As printed, the pilot bonus is "any starship"; the errata (current card, Card6_103) made it "anything", so this state adds power only to starships it pilots and otherwise plays as the current card.
 */
public class Card6_103o extends Card6_103 {
    public Card6_103o() {
        super();
        setGameText("Adds 2 to power of any starship he pilots. While at a site you control, Imperials are immune to Ke Chu Ke Kukuta? at that site.");
        setTextState(new TextState("6_103", "Printed",
                "Jabba's Palace printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        // As printed, the pilot bonus applies only to starships (the errata made it "anything").
        List<Modifier> modifiers = new LinkedList<Modifier>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.removeIf(modifier -> modifier instanceof AddsPowerToPilotedBySelfModifier);
        modifiers.add(0, new AddsPowerToPilotedBySelfModifier(self, 2, Filters.starship));
        return modifiers;
    }
}
