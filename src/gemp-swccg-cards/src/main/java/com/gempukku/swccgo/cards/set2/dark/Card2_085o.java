package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Title: Danz Borin
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Danz Borin]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Danz Borin (PC Errata)]] (date unknown; upper bound March 2017).
 * As printed, the pilot bonus applies only to a starship he pilots ("any starship he pilots"); the errata made it
 * "anything he pilots" (vehicles too). Otherwise this state plays as the bare card (Card2_085).
 * The passenger clause likewise applies only aboard a starship ("If aboard a starship as a passenger, adds 1 to
 * starship's weapon destiny draws"), where the errata says anything he is aboard as a passenger.
 */
public class Card2_085o extends Card2_085 {
    public Card2_085o() {
        super();
        setGameText("Adds 3 to power of any starship he pilots. OR If aboard a starship as a passenger, adds 1 to starship's weapon destiny draws.");
        setTextState(new TextState("2_85", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2017, 3, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.removeIf(modifier -> modifier instanceof AddsPowerToPilotedBySelfModifier);
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.starship));
        modifiers.removeIf(modifier -> modifier instanceof EachWeaponDestinyModifier);
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, Filters.and(Filters.starship, Filters.hasPassenger(self)), 1));
        return modifiers;
    }
}
