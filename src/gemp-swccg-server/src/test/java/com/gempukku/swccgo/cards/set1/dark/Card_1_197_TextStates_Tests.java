package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * WED15-I662 'Treadwell' Droid text states (docs/legacy-versions.md, "Text states"): 1_197o as printed (Premiere, 1995) and the bare card
 * 1_197 unchanged. Texts from wiki.swccg.com [[WED15-I662 'Treadwell' Droid]] and [[WED15-I662 'Treadwell' Droid (Errata)]]. As printed the card knows only systems
 * (sectors came later); the errata added "or sector".
 */
public class Card_1_197_TextStates_Tests {
    private static final String PRINTED_TEXT = "All your vehicles and droids at same location are immune to attrition < 2. Also, if 'treadwell' droid is at a docking bay, all your starfighters at the related system are immune to attrition < 3.";
    private static final String CURRENT_TEXT = "Adds immunity to attrition < 2 to all your vehicles and droids at same location. Also, if 'treadwell' droid is at a docking bay, adds immunity to attrition < 3 to all your starfighters at the related system and related sectors.";

    private static VirtualTableScenario GetScenario(String id) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("filler", "1_28");
                }},
                new HashMap<>() {{
                    put("bay", "5_169");
                    put("ship1", "1_304");
                    put("ship2", "1_304");
                    put("card", id);
                    put("bespin", "5_164");   // Bespin (system)
                    put("clouds", "5_174");   // Clouds (sector related to Bespin)
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes"
        );
    }

    @Test
    public void WED15I662TreadwellDroidPrintedHasThePrintedTextAndTextState() {
        var printed = GetScenario("1_197o").GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_197").GetDSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        assertEquals("1_197", printed.getTextState().currentId());
        assertEquals("Printed", printed.getTextState().label());
        assertEquals(LocalDate.of(1995, 12, 1), printed.getTextState().printedOn());
        assertEquals(bare.getTitle(), printed.getTitle());
        assertEquals(bare.getUniqueness(), printed.getUniqueness());
        assertEquals(bare.getSide(), printed.getSide());
        assertEquals(bare.getCardTypes(), printed.getCardTypes());
        assertEquals(bare.getCardSubtype(), printed.getCardSubtype());
        assertEquals(bare.getRarity(), printed.getRarity());
        assertEquals(bare.getLore(), printed.getLore());
        assertEquals(bare.getDestiny(), printed.getDestiny(), 0.01f);
        assertEquals("WED15-I662 'Treadwell' Droid [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void WED15I662TreadwellDroidBareCardIsUnchanged() {
        var bare = GetScenario("1_197").GetDSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
    }

    /** Immunity to attrition of a starfighter at the related system and at a related sector while the droid is at the docking bay. */
    private static float[] starfighterImmunity(String id) {
        var scn = GetScenario(id);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("bespin"));
        scn.MoveLocationToTable(scn.GetDSCard("clouds"));
        scn.MoveLocationToTable(scn.GetDSCard("bay"));
        scn.MoveCardsToLocation(scn.GetDSCard("bay"), scn.GetDSCard("card"));
        scn.MoveCardsToLocation(scn.GetDSCard("bespin"), scn.GetDSCard("ship1"));
        scn.MoveCardsToLocation(scn.GetDSCard("clouds"), scn.GetDSCard("ship2"));
        var q = scn.game().getModifiersQuerying();
        return new float[]{q.getImmunityToAttritionLessThan(scn.gameState(), scn.GetDSCard("ship1")),
                q.getImmunityToAttritionLessThan(scn.gameState(), scn.GetDSCard("ship2"))};
    }

    @Test
    public void WED15I662TreadwellDroidPrintedProtectsStarfightersOnlyAtTheRelatedSystem() {
        float[] bare = starfighterImmunity("1_197");
        assertTrue(bare[0] >= 3);
        assertTrue(bare[1] >= 3);
        float[] printed = starfighterImmunity("1_197o");
        assertTrue(printed[0] >= 3);   // related system: as the current card
        assertTrue(printed[1] < 3);    // related sector: not as printed
    }
}
