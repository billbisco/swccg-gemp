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
 * First Officer Thaneespi text states (docs/legacy-versions.md, "Text states"): 9_12o as printed and the bare card 9_12
 * unchanged. Printed text from wiki.swccg.com [[First Officer Thaneespi]] (Decipher archive scan); current text
 * [[First Officer Thaneespi (PC Errata)]]. The PC errata changed wording only, so both play the same game text code.
 */
public class Card_9_012_TextStates_Tests {
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
    public void FirstOfficerThaneespiPrintedHasThePrintedText() {
        var card = blueprint("9_12o");
        assertEquals("Deploys -2 to Home One. Adds 3 to power of any capital starship she pilots. When piloting a Star Cruiser with another Mon Calamari pilot aboard, unless opponent has total ability > 6 piloting here, opponent's total battle destiny here = zero.", card.getGameText());
        TextState state = card.getTextState();
        assertEquals("9_12", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(2000, 7, 1), state.printedOn());
        assertEquals(card.getTitle() + " [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void FirstOfficerThaneespiBareCardIsUnchangedAndTheStateIsTheSameCard() {
        var printed = blueprint("9_12o");
        var bare = blueprint("9_12");
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
