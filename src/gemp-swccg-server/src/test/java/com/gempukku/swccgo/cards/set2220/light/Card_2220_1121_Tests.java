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

/** PC-E1 version of 1220_121 (Anything Goes only). */
public class Card_2220_1121_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("version", "2220_1121");
                    put("current", "1220_121");
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
    public void VersionHasItsTextAndVersionInfo() {
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
        assertEquals("Deploy on table; shuffle your Reserve Deck, peek at top three cards, and stack them face-up here. During battle, may take a card here into hand to prevent all battle destiny draws from being modified or canceled (each player may draw no more than one battle destiny). (Immune to Alter.)",
                card.getGameText());
        assertNotEquals(current.getGameText(), card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_121", version.currentPrintingId());
        assertEquals("PC-E1", version.label());
        assertEquals(LocalDate.of(2012, 7, 21), version.printedOn());
        assertEquals(LocalDate.of(2013, 4, 27), version.supersededOn());
        assertEquals(GameUtils.getFullName(current) + " [PC-E1]", GameUtils.getFullName(card));
    }
}
