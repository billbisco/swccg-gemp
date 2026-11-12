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
 * Saber 4 text states (docs/legacy-versions.md, "Text states"): 9_165o as printed and the bare card 9_165
 * unchanged. Printed text from wiki.swccg.com [[Saber 4]] (Decipher archive scan); current text
 * [[Saber 4 (PC Errata)]]. The PC errata changed wording only, so both play the same game text code.
 */
public class Card_9_165_TextStates_Tests {
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
    public void Saber4PrintedHasThePrintedText() {
        var card = blueprint("9_165o");
        assertEquals("May deploy with a pilot as a 'react' to a battle initiated against a TIE (for free if TIE is in Saber Squadron). May add 1 pilot. Immune to attrition < 4 when DS-181-4 piloting.", card.getGameText());
        TextState state = card.getTextState();
        assertEquals("9_165", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(2000, 7, 1), state.printedOn());
        assertEquals(card.getTitle() + " [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void Saber4BareCardIsUnchangedAndTheStateIsTheSameCard() {
        var printed = blueprint("9_165o");
        var bare = blueprint("9_165");
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
