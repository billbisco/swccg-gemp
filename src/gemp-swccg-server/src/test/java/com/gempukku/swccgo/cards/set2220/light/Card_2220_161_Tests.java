package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

/** Hoth: Echo Med Lab: original (pre-errata) version of 1220_161. */
public class Card_2220_161_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("version", "2220_161");
                    put("current", "1220_161");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void VersionHasTheOldTextAndVersionInfo() {
        var scn = GetScenario();
        var card = scn.GetLSCard("version").getBlueprint();
        var current = scn.GetLSCard("current").getBlueprint();

        assertEquals(current.getTitle(), card.getTitle());
        assertEquals(current.getUniqueness(), card.getUniqueness());
        assertEquals(current.getSide(), card.getSide());
        assertEquals(current.hasVirtualSuffix(), card.hasVirtualSuffix());
        assertEquals(current.getCardTypes(), card.getCardTypes());
        for (Icon icon : Icon.values())
            assertEquals(icon.name(), current.getIconCount(icon), card.getIconCount(icon));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("Light: During your deploy phase, may deploy a medical droid here from Reserve Deck; reshuffle. Dark: If you control, with an Imperial present, Force drain +1 here.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_161", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2014, 2, 4), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    @Test
    public void ForceDrainPlusOneOnlyIfDarkSideControlsWithAnImperialPresent() {
        var scn = GetScenario();
        var medlab = scn.GetLSCard("version");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var luke = scn.GetLSCard("luke");
        var mods = scn.game().getModifiersQuerying();

        //test1: Imperial present but Luke there too: Dark Side does not control, no +1 (the current card: +1)
        //test2: Dark Side controls with an Imperial present: +1
        scn.StartGame();
        scn.MoveLocationToTable(medlab);
        float base = mods.getForceDrainAmount(scn.gameState(), medlab, scn.DS);
        scn.MoveCardsToLocation(medlab, stormtrooper, luke);
        assertEquals(base, mods.getForceDrainAmount(scn.gameState(), medlab, scn.DS), scn.epsilon); //test1
        scn.MoveCardsToLSHand(luke);
        assertEquals(base + 1, mods.getForceDrainAmount(scn.gameState(), medlab, scn.DS), scn.epsilon); //test2
    }
}
