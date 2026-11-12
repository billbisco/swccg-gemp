package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Imperial
 * Title: Commander Nemet
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds to power and maneuver only of a starship he pilots; Decipher Glossary 2.0 (November 1998)
 * changed it to "anything he pilots" (the current card, Card4_097), so a vehicle he pilots gets nothing here.
 */
public class Card4_097o extends Card4_097 {
    public Card4_097o() {
        super();
        setGameText("Adds 1 to power and maneuver of any starship he pilots. Opponent may not 'react' to or from same location.");
        setTextState(new TextState("4_97", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1, Filters.starship));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.starship, Filters.hasPiloting(self)), 1));
        modifiers.add(new MayNotReactToLocationModifier(self, Filters.sameLocation(self), opponent));
        modifiers.add(new MayNotReactFromLocationModifier(self, Filters.sameLocation(self), opponent));
        return modifiers;
    }
}
