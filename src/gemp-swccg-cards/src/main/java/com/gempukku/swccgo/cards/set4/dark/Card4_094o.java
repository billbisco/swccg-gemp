package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Imperial
 * Title: Comm Chief
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds 2 to power only of a starship he pilots, and only a starship or combat vehicle he pilots moves
 * for free. Decipher Glossary 2.0 (November 1998) changed it to "anything he pilots" (the current card, Card4_094).
 */
public class Card4_094o extends Card4_094 {
    public Card4_094o() {
        super();
        setGameText("Adds 2 to power of any starship he pilots. When piloting a starship or combat vehicle, that starship or vehicle moves for free.");
        setTextState(new TextState("4_94", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.starship));
        modifiers.add(new MovesForFreeModifier(self, Filters.and(Filters.or(Filters.starship, Filters.combat_vehicle), Filters.hasPiloting(self))));
        return modifiers;
    }
}
