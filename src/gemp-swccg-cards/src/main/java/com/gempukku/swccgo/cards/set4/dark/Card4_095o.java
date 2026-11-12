package com.gempukku.swccgo.cards.set4.dark;

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
 * Set: Dagobah
 * Type: Character
 * Subtype: Imperial
 * Title: Commander Brandei
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * As printed he adds 2 to power only of a starship he pilots; Decipher Glossary 2.0 (November 1998) changed it to
 * "anything he pilots" (the current card, Card4_095). The TIE search plays the same ("search your Reserve Deck ...
 * Shuffle, cut and replace" is the long form of "take ... from Reserve Deck; reshuffle"), so it is inherited.
 */
public class Card4_095o extends Card4_095 {
    public Card4_095o() {
        super();
        setGameText("Adds 2 to power of any starship he pilots. If aboard a Star Destroyer or at any docking bay, once during each of your control phases, may use 1 Force to search your Reserve Deck and take one TIE into hand. Shuffle, cut and replace.");
        setTextState(new TextState("4_95", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.starship));
        return modifiers;
    }
}
