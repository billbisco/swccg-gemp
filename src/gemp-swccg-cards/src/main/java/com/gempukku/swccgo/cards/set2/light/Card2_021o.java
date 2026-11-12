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
 * Title: Tiree
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Tiree]] (Decipher archive scan); superseded by Decipher errata, wiki [[Tiree (Errata)]] (Glossary; upper bound November 1998).
 * As printed, the pilot bonus applies only to a starship he pilots ("any starship he pilots"); the errata made it
 * "anything he pilots" (vehicles too). Otherwise this state plays as the bare card (Card2_021).
 */
public class Card2_021o extends Card2_021 {
    public Card2_021o() {
        super();
        setGameText("Adds 2 to power of any starship he pilots. On Gold 2, also adds 1 to maneuver and (when in Death Star: Trench) adds 1 to total of Attack Run.");
        setTextState(new TextState("2_21", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.removeIf(modifier -> modifier instanceof AddsPowerToPilotedBySelfModifier);
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.starship));
        return modifiers;
    }
}
