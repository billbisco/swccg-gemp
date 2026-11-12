package com.gempukku.swccgo.cards.set9.light;

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
 * Lieutenant Telsij text states (docs/legacy-versions.md, "Text states"): 9_23o as printed and the bare card 9_23
 * unchanged. Printed text from wiki.swccg.com [[Lieutenant Telsij]] (Decipher archive scan); current text
 * [[Lieutenant Telsij (PC Errata)]]. The PC errata changed wording only, so both play the same game text code.
 */
public class Card_9_023_TextStates_Tests {
    private SwccgCardBlueprint blueprint(String id) {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{ put("card", id); }},
                new HashMap<>() {{  }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes"
        );
        return scn.GetLSCard("card").getBlueprint();
    }

    @Test
    public void LieutenantTelsijPrintedHasThePrintedText() {
        var card = blueprint("9_23o");
        assertEquals("Adds 2 to power of anything he pilots. When at a system, sector or docking bay, once during each of your deploy phases, may subtract 2 from deploy cost of your unique (•) Y-wing deploying here.", card.getGameText());
        TextState state = card.getTextState();
        assertEquals("9_23", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(2000, 7, 1), state.printedOn());
        assertEquals(card.getTitle() + " [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void LieutenantTelsijBareCardIsUnchangedAndTheStateIsTheSameCard() {
        var printed = blueprint("9_23o");
        var bare = blueprint("9_23");
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
