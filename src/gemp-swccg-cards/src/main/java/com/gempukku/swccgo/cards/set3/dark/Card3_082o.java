package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Title: Admiral Ozzel
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds to power only of a starship he pilots ("any starship he pilots"); Decipher Glossary 2.0 (November 1998) changed it to "anything he pilots" (the current card, Card3_082). The rest of the printed text plays as the current card (the Glossary Supplement, 29 January 2002, only reworded it), so only the pilot bonus is limited to starships here.
 */
public class Card3_082o extends Card3_082 {
    public Card3_082o() {
        super();
        setGameText("Your capital starships are deploy -1 to same system. Adds 2 to power of any starship he pilots. Immediately lost if Vader is on table and opponent 'reacts' to same location as Ozzel.");
        setTextState(new TextState("3_82", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.starship));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.capital_starship),
                -1, Filters.sameSystem(self)));
        return modifiers;
    }
}
