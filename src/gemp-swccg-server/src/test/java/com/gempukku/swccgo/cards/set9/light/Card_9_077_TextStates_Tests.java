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
 * Luminous text states (docs/legacy-versions.md, "Text states"): 9_77o as printed and the bare card 9_77
 * unchanged text. Printed text from wiki.swccg.com [[Luminous]] (Decipher archive scan); current text
 * [[Luminous (PC Errata)]]. Play differences (ship-docking, Covert Landing while landed) are tested in ErrataPlayFixesTests.
 */
public class Card_9_077_TextStates_Tests {
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
    public void LuminousPrintedHasThePrintedText() {
        var card = blueprint("9_77o");
        assertEquals("Deploys and moves like a starfighter. May add 1 pilot and 6 passengers. Has ship-docking capability. Permanent pilot provides ability of 2. May use Covert Landing like a shuttle or starfighter.", card.getGameText());
        TextState state = card.getTextState();
        assertEquals("9_77", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(2000, 7, 1), state.printedOn());
        assertEquals(card.getTitle() + " [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void LuminousBareCardIsUnchangedAndTheStateIsTheSameCard() {
        var printed = blueprint("9_77o");
        var bare = blueprint("9_77");
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
