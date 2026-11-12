package com.gempukku.swccgo.cards.set3.light;

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
 * Set: Hoth
 * Title: Romas 'Lock' Navander
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds to power only of a starship he pilots ("any starship he pilots"); Decipher Glossary 2.0
 * (November 1998) changed it to "anything he pilots" (the current card, Card3_018). The rest of the printed text plays
 * as the current card, so only the pilot bonus is limited to starships here.
 */
public class Card3_018o extends Card3_018 {
    public Card3_018o() {
        super();
        setGameText("Adds 1 to power and maneuver of any starship he pilots. Opponent may not 'react' to or from same location.");
        setTextState(new TextState("3_18", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
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
