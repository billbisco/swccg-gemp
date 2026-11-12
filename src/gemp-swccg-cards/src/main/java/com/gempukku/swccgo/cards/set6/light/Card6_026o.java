package com.gempukku.swccgo.cards.set6.light;

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
 * Title: Leslomy Tacema
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Leslomy Tacema]] (Decipher archive scan); superseded by Decipher Glossary Version 2.0 (November 1998), whose text the current card keeps.
 * As printed, the pilot bonus is "any starship"; the errata (current card, Card6_026) made it "anything", so this state adds power only to starships it pilots and otherwise plays as the current card.
 */
public class Card6_026o extends Card6_026 {
    public Card6_026o() {
        super();
        setGameText("Adds 3 to power of any starship she pilots. While at Audience Chamber, adds 2 to the power bonus provided by Ellorrs Madak.");
        setTextState(new TextState("6_26", "Printed",
                "Jabba's Palace printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        // As printed, the pilot bonus applies only to starships (the errata made it "anything").
        List<Modifier> modifiers = new LinkedList<Modifier>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.removeIf(modifier -> modifier instanceof AddsPowerToPilotedBySelfModifier);
        modifiers.add(0, new AddsPowerToPilotedBySelfModifier(self, 3, Filters.starship));
        return modifiers;
    }
}
