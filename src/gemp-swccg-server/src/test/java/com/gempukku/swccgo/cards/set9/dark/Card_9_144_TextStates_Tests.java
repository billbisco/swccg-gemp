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
 * Death Star II: Coolant Shaft text states (docs/legacy-versions.md, "Text states"): 9_144o as printed and the bare card 9_144
 * unchanged. Printed text from wiki.swccg.com [[Death Star II: Coolant Shaft]] (Decipher archive scan); current text
 * [[Death Star II: Coolant Shaft (PC Errata)]]. The PC errata changed wording only, so both play the same game text code.
 */
public class Card_9_144_TextStates_Tests {
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
    public void DeathStarIiCoolantShaftPrintedHasThePrintedText() {
        var card = blueprint("9_144o");
        assertEquals("When your starship moves from here, draw movement destiny. Add maneuver. If total destiny < 1, starship is lost.", card.getLocationLightSideGameText());
        assertEquals("Deploys only if Death Star II system on table. Your TIEs at Death Star II locations may not Tallon Roll. Your non-Objective cards with 'Occupation' in title are canceled.", card.getLocationDarkSideGameText());
        TextState state = card.getTextState();
        assertEquals("9_144", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(2000, 7, 1), state.printedOn());
        assertEquals(card.getTitle() + " [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void DeathStarIiCoolantShaftBareCardIsUnchangedAndTheStateIsTheSameCard() {
        var printed = blueprint("9_144o");
        var bare = blueprint("9_144");
        assertNull(bare.getTextState());
        assertEquals(bare.getTitle(), printed.getTitle());
        assertEquals(bare.getUniqueness(), printed.getUniqueness());
        assertEquals(bare.getPersonas(), printed.getPersonas());
        assertEquals(bare.getSide(), printed.getSide());
        assertEquals(bare.getCardCategory(), printed.getCardCategory());
        assertEquals(bare.getRarity(), printed.getRarity());
        assertEquals(bare.getExpansionSet(), printed.getExpansionSet());
        assertEquals(bare.getLocationDarkSideGameText(), printed.getLocationDarkSideGameText());
        assertNotEquals(bare.getClass(), printed.getClass());
    }
}
