package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.util.List;
import com.gempukku.swccgo.logic.modifiers.MayNotBeStolenModifier;
import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Reflections III
 * Title: Maul's Double-Bladed Lightsaber
 * Text state: o (as printed, 31 October 2001; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Maul's Double-Bladed Lightsaber]] (Decipher archive scan); the current text is [[Maul's Double-Bladed Lightsaber (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). As printed it "May not be stolen" anywhere; the PC errata limits that to while it is on Maul. This state keeps the printed text.
 * supersededOn is the date of the Players Committee Decipher Errata sheet of 1 June 2017, which already has it.
 */
public class Card13_075o extends Card13_075 {
    public Card13_075o() {
        super();
        setGameText("Deploy on Maul. Adds 1 to Maul's lightsaber combat total. May lose 1 Force to add 2 to Force drain where present. Twice per battle, may target a character. Draw two destiny. Target hit, and its forfeit = 0, if total > defense value. May not be stolen.");
        setTextState(new TextState("13_75", "Printed",
                "Reflections III printing (Decipher, 31 October 2001)",
                LocalDate.of(2001, 10, 31), LocalDate.of(2017, 6, 1)));
    }

    /** As printed: may not be stolen, wherever it is. */
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = super.getGameTextWhileActiveInPlayModifiers(game, self);
        modifiers.removeIf(modifier -> modifier instanceof MayNotBeStolenModifier);
        modifiers.add(new MayNotBeStolenModifier(self));
        return modifiers;
    }
}
