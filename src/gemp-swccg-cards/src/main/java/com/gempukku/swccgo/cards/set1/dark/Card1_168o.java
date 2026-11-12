package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Title: Darth Vader
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Darth Vader]]. As printed, the pilot bonus applies only to a
 * starship
 * he pilots ("any starship he pilots"); the errata ([[Darth Vader (PC Errata)]], Players Committee errata (AR 2023
 * Appendix A; date is an upper bound, the first PC errata date is not known)) made it "anything he pilots", so vehicles
 * get it
 * too, which is how the current card (Card1_168) plays. Everything else plays as Card1_168: "adds 1 to each battle
 * destiny" is read as each of your battle destiny draws, as the errata clarified.
 */
public class Card1_168o extends Card1_168 {
    public Card1_168o() {
        super();
        setGameText("When in battle, adds 1 to each battle destiny. Adds 3 to power of any starship he pilots. Adds 4 to power and 3 to maneuver when piloting Vader's Custom TIE. Immune to attrition < 5.");
        setTextState(new TextState("1_168", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        for (Modifier modifier : super.getGameTextWhileActiveInPlayModifiers(game, self)) {
            if (modifier instanceof AddsPowerToPilotedBySelfModifier)
                modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(3, 4, Filters.Vaders_Custom_TIE), Filters.starship));
            else
                modifiers.add(modifier);
        }
        return modifiers;
    }
}
