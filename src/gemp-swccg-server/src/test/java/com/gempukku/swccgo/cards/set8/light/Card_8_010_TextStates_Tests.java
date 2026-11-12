package com.gempukku.swccgo.cards.set8.light;

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
 * Endor Scout Trooper text states (docs/legacy-versions.md, "Text states"): 8_10o as printed and the bare card 8_10
 * unchanged. Printed text from wiki.swccg.com [[Endor Scout Trooper]] (Decipher archive scan); current text
 * [[Endor Scout Trooper (PC Errata)]]. The PC errata changed wording only, so both play the same game text code.
 */
public class Card_8_010_TextStates_Tests {
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
    public void EndorScoutTrooperPrintedHasThePrintedText() {
        var card = blueprint("8_10o");
        assertEquals("Power -1 while not on Endor. While Endor Scout Trooper is on Endor, Lieutenant Page and each of your Rebel scouts of ability < 3 at same and adjacent exterior sites are immune to attrition < 2.", card.getGameText());
        TextState state = card.getTextState();
        assertEquals("8_10", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 6, 1), state.printedOn());
        assertEquals(card.getTitle() + " [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void EndorScoutTrooperBareCardIsUnchangedAndTheStateIsTheSameCard() {
        var printed = blueprint("8_10o");
        var bare = blueprint("8_10");
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
