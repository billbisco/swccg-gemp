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
 * Death Star II: Capacitors text states (docs/legacy-versions.md, "Text states"): 9_143o as printed and the bare card 9_143
 * unchanged. Printed text from wiki.swccg.com [[Death Star II: Capacitors]] (Decipher archive scan); current text
 * [[Death Star II: Capacitors (PC Errata)]]. The PC errata changed wording only, so both play the same game text code.
 */
public class Card_9_143_TextStates_Tests {
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
    public void DeathStarIiCapacitorsPrintedHasThePrintedText() {
        var card = blueprint("9_143o");
        assertEquals("When your starship moves from here, draw movement destiny. Add maneuver. If total destiny < 2, starship is lost.", card.getLocationLightSideGameText());
        assertEquals("Deploys only if Coolant Shaft on table. While That Thing's Operational on table, you may add one battle destiny during battles at Death Star II system and system it orbits.", card.getLocationDarkSideGameText());
        TextState state = card.getTextState();
        assertEquals("9_143", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(2000, 7, 1), state.printedOn());
        assertEquals(card.getTitle() + " [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void DeathStarIiCapacitorsBareCardIsUnchangedAndTheStateIsTheSameCard() {
        var printed = blueprint("9_143o");
        var bare = blueprint("9_143");
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
