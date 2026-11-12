package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Title: Captain Lennox
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds to power only of a starship he pilots ("any starship he pilots"); Decipher Glossary 2.0
 * (November 1998) changed it to "anything he pilots" (the current card, Card3_084). The rest of the printed text plays
 * as the current card, so only the pilot bonus is limited to starships here.
 */
public class Card3_084o extends Card3_084 {
    public Card3_084o() {
        super();
        setGameText("Adds 2 to power of any starship he pilots (3 if starship is Tyrant). When on a Star Destroyer, may use its Tractor Beam once each turn during your control phase.");
        setTextState(new TextState("3_84", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.Tyrant), Filters.starship));
        return modifiers;
    }
}
