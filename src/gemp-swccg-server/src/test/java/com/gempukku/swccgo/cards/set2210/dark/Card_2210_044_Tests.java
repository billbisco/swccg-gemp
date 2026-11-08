package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

/** Original version of 1210_44 (Anything Goes only). */
public class Card_2210_044_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("version", "2210_44");
                    put("current", "1210_44");
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
        var card = scn.GetDSCard("version").getBlueprint();
        var current = scn.GetDSCard("current").getBlueprint();

        assertEquals(current.getTitle(), card.getTitle());
        assertEquals(current.getUniqueness(), card.getUniqueness());
        assertEquals(current.getSide(), card.getSide());
        assertEquals(current.hasVirtualSuffix(), card.hasVirtualSuffix());
        assertEquals(current.getCardTypes(), card.getCardTypes());
        for (Icon icon : Icon.values())
            assertEquals(icon.name(), current.getIconCount(icon), card.getIconCount(icon));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("Deploy on your non-droid character present at a battleground site; character may not be carried from here or move. Target a Jedi at a site. You lose no more than 1 Force to Force drains at target's location. This Utinni Effect canceled when reached by target.",
                card.getGameText());
        assertNotEquals(current.getGameText(), card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1210_44", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2012, 8, 27), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }
}
