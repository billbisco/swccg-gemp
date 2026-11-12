package com.gempukku.swccgo.cards.set9.dark;

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
 * Captain Jonus text states (docs/legacy-versions.md, "Text states"): 9_101o as printed and the bare card 9_101
 * unchanged. Printed text from wiki.swccg.com [[Captain Jonus]] (Decipher archive scan); current text
 * [[Captain Jonus (PC Errata)]]. The PC errata changed wording only, so both play the same game text code.
 */
public class Card_9_101_TextStates_Tests {
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
    public void CaptainJonusPrintedHasThePrintedText() {
        var card = blueprint("9_101o");
        assertEquals("Deploys -2 aboard Scimitar 2. Adds 2 to power of anything he pilots. When forfeited from a TIE/sa, also satisfies all remaining attrition and battle damage against you.", card.getGameText());
        TextState state = card.getTextState();
        assertEquals("9_101", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(2000, 7, 1), state.printedOn());
        assertEquals(card.getTitle() + " [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void CaptainJonusBareCardIsUnchangedAndTheStateIsTheSameCard() {
        var printed = blueprint("9_101o");
        var bare = blueprint("9_101");
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
