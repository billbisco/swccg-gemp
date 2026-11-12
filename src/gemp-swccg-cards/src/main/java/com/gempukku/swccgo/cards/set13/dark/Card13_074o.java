package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.util.List;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeDisarmedModifier;
import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Reflections III
 * Title: Lord Maul
 * Text state: o (as printed, 31 October 2001; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Lord Maul]] (Decipher archive scan); the current text is [[Lord Maul (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). As printed Maul is "Immune to Disarmed" (the Interrupt only); the PC errata "May not be Disarmed" covers every card that would disarm him. This state keeps the printed immunity.
 * supersededOn is the date of the Players Committee Decipher Errata sheet of 1 June 2017, which already has it.
 */
public class Card13_074o extends Card13_074 {
    public Card13_074o() {
        super();
        setGameText("Deploys -2 to Naboo. While with Qui-Gon, your battle destinies here are each +1. When Maul wins a lightsaber combat, adds 2 to opponent's Force loss. Immune to Disarmed, Clash Of Sabers, and attrition < 5 (or < 6 if armed with a lightsaber).");
        setTextState(new TextState("13_74", "Printed",
                "Reflections III printing (Decipher, 31 October 2001)",
                LocalDate.of(2001, 10, 31), LocalDate.of(2017, 6, 1)));
    }

    /** As printed: immune to the Interrupt Disarmed, instead of "may not be Disarmed". */
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = super.getGameTextWhileActiveInPlayModifiers(game, self);
        modifiers.removeIf(modifier -> modifier instanceof MayNotBeDisarmedModifier);
        modifiers.add(new ImmuneToTitleModifier(self, Title.Disarmed));
        return modifiers;
    }
}
