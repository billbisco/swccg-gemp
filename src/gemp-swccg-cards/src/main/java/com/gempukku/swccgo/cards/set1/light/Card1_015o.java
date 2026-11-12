package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Kal'Falnl C'ndros
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (December 1995) text, from wiki.swccg.com [[Kal'Falnl C'ndros]] (Decipher archive scan). As
 * printed she adds 2 to power only of capital starships she pilots. The Players Committee's errata
 * ([[Kal'Falnl C'ndros (PC Errata)]], SWCCG 2023 Advanced Rulebook Appendix A) made it "anything she pilots", which is
 * the current card (Card1_015). Her battle destiny text and boarding limits play as the current card.
 */
public class Card1_015o extends Card1_015 {
    public Card1_015o() {
        super();
        setGameText("If both players draw one battle destiny involving Kal, and yours is higher, the opponent's destiny is reduced to zero. Landspeed = 3. Adds 2 to power of capital starships she pilots. Legs too long to board starfighters or enclosed vehicles.");
        setTextState(new TextState("1_15", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 3));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.capital_starship));
        return modifiers;
    }
}
