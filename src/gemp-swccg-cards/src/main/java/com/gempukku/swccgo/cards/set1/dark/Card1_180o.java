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
 * Title: Imperial Pilot
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (1995) text, from wiki.swccg.com [[Imperial Pilot]]. As printed, the pilot bonus applies only to a
 * starship
 * he pilots ("any starship he pilots"); the errata ([[Imperial Pilot (PC Errata)]], Players Committee errata (AR 2023
 * Appendix A; date is an upper bound, the first PC errata date is not known)) made it "anything he pilots", so vehicles
 * get it
 * too, which is how the current card (Card1_180) plays. Everything else plays as Card1_180.
 */
public class Card1_180o extends Card1_180 {
    public Card1_180o() {
        super();
        setGameText("* Power = 0 when at any site, but adds 2 to power of any starship he pilots.");
        setTextState(new TextState("1_180", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
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
