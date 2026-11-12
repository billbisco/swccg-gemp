package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

/**
 * Outflank text states (docs/legacy-versions.md, "Text states"): 8_150o as printed and the bare card 8_150
 * unchanged. Printed text from wiki.swccg.com [[Outflank]] (Decipher archive scan); current text
 * [[Outflank (PC Errata)]]. The PC errata changed wording only, so both play the same game text code.
 */
public class Card_8_150_TextStates_Tests {
    private SwccgCardBlueprint blueprint(String id) {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{  }},
                new HashMap<>() {{ put("card", id); }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes"
        );
        return scn.GetDSCard("card").getBlueprint();
    }

    @Test
    public void OutflankPrintedHasThePrintedText() {
        var card = blueprint("8_150o");
        assertEquals("During a battle, if you have weapons at both sites adjacent to that battle, add 3 to your total power and add one battle destiny. OR For remainder of turn, your biker scout at an exterior site is power +1 and adds 1 to each of that character's weapon destiny draws.", card.getGameText());
        TextState state = card.getTextState();
        assertEquals("8_150", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 6, 1), state.printedOn());
        assertEquals(card.getTitle() + " [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void OutflankBareCardIsUnchangedAndTheStateIsTheSameCard() {
        var printed = blueprint("8_150o");
        var bare = blueprint("8_150");
        assertNull(bare.getTextState());
        assertEquals(bare.getTitle(), printed.getTitle());
        assertEquals(bare.getUniqueness(), printed.getUniqueness());
        assertEquals(bare.getPersonas(), printed.getPersonas());
        assertEquals(bare.getSide(), printed.getSide());
        assertEquals(bare.getCardCategory(), printed.getCardCategory());
        assertEquals(bare.getRarity(), printed.getRarity());
        assertEquals(bare.getExpansionSet(), printed.getExpansionSet());

        assertNotEquals(bare.getClass(), printed.getClass());
    }
}
