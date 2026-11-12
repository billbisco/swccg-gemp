package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Title: Dengar
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds to power only of a starship he pilots ("any starship he pilots"); Decipher Glossary 2.0
 * (November 1998) changed it to "anything he pilots" (the current card, Card4_100). The rest of the printed text plays
 * as the current card, so only the pilot bonus is limited to starships here.
 */
public class Card4_100o extends Card4_100 {
    public Card4_100o() {
        super();
        setGameText("Adds 2 to power of any starship he pilots. Power +1 for every opponent's character present. May reduce Han's forfeit to zero while present.");
        setTextState(new TextState("4_100", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.starship));
        modifiers.add(new PowerModifier(self, new PresentEvaluator(self, Filters.and(Filters.opponents(self), Filters.character))));
        return modifiers;
    }
}
