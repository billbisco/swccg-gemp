package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

/** Original version of 1210_99 (Anything Goes only). */
public class Card_2210_099_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("version", "2210_99");
                    put("current", "1210_99");
                    put("dsite", "1_128");
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
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("Adds 2 to power of anything he pilots. If with an Imperial, you may initiate battle here for free. Permanent weapon is blaster rifle (may target a character or creature for free; draw destiny; target hit, and its forfeit = 0, if destiny +1 > defense value).",
                card.getGameText());
        assertEquals(current.getGameText(), card.getGameText()); // the Master List (2013-04-27, the current text) repeats it (batch R8)
        var version = card.getLegacyVersion();
        assertEquals("1210_99", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2012, 7, 21), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    @Test
    public void VersionIsTheSameUniqueCardAsTheCurrentPrinting() {
        for (boolean currentOnTable : java.util.List.of(true, false)) {
            for (boolean otherOut : java.util.List.of(false, true)) {
                var scn = GetScenario();
                var onTable = scn.GetLSCard(currentOnTable ? "current" : "version");
                var inHand = scn.GetLSCard(currentOnTable ? "version" : "current");
                scn.StartGame();
                var site = scn.GetLSCard("dsite");
                scn.MoveLocationToTable(site);
                if (otherOut)
                    scn.MoveCardsToLocation(site, onTable);
                scn.MoveCardsToLSHand(inHand);
                scn.SkipToLSTurn(Phase.DEPLOY);
                scn.EnsureLSForcePile(8);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.LSCardPlayAvailable(inHand));
            }
        }
    }
}
