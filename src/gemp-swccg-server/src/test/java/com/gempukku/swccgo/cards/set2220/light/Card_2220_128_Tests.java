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

/** Maneuvering Flaps: original (pre-errata) version of 1220_128. */
public class Card_2220_128_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("version", "2220_128");
                    put("current", "1220_128");
                }},
                new HashMap<>() {{

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
        assertEquals(ExpansionSet.LEGACY, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("Deploy on table. During your deploy phase, may reveal an unpiloted combat vehicle from hand to [upload] its matching pilot character (or vice versa) and deploy both simultaneously. (Immune to Alter.)",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_128", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2012, 7, 21), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    @Test
    public void MayBeSuspended() {
        var scn = GetScenario();
        var flaps = scn.GetLSCard("version");
        var current = scn.GetLSCard("current");

        //test1: the current card may not be suspended; the original may
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(flaps, current);
        assertTrue(scn.game().getModifiersQuerying().isProhibitedFromBeingSuspended(scn.gameState(), current));
        assertFalse(scn.game().getModifiersQuerying().isProhibitedFromBeingSuspended(scn.gameState(), flaps)); //test1
    }
}
