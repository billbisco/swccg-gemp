package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

/** I'm Sorry: original (pre-errata) version of 1220_36. */
public class Card_2220_036_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{

                }},
                new HashMap<>() {{
                    put("clouds", "5_174");
                    put("cloudcity", "5_165");
                    put("bespin", "5_164");
                    put("version", "2220_36");
                    put("current", "1220_36");
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
        var card = scn.GetDSCard("version").getBlueprint();
        var current = scn.GetDSCard("current").getBlueprint();

        assertEquals(current.getTitle(), card.getTitle());
        assertEquals(current.getUniqueness(), card.getUniqueness());
        assertEquals(current.getSide(), card.getSide());
        assertEquals(current.hasVirtualSuffix(), card.hasVirtualSuffix());
        assertEquals(current.getCardTypes(), card.getCardTypes());
        for (Icon icon : Icon.values())
            assertEquals(icon.name(), current.getIconCount(icon), card.getIconCount(icon));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("Deploy on table. Floating Refinery is suspended during opponent's turn. During your deploy phase, may deploy one Floating Refinery, a non-unique cloud sector, or an Obsidian squadron TIE from Reserve Deck; reshuffle. During your turn, if you control two cloud sectors with TIEs, opponent loses 1 Force. (Immune to Alter.)",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_36", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2013, 4, 27), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    @Test
    public void VersionIsTheSameUniqueCardAsTheCurrentPrinting() {
        for (boolean currentOnTable : java.util.List.of(true, false)) {
            for (boolean otherOut : java.util.List.of(false, true)) {
                var scn = GetScenario();
                var onTable = scn.GetDSCard(currentOnTable ? "current" : "version");
                var inHand = scn.GetDSCard(currentOnTable ? "version" : "current");
                scn.StartGame();

                if (otherOut)
                    scn.MoveCardsToDSSideOfTable(onTable);
                scn.MoveCardsToDSHand(inHand);
                scn.SkipToDSTurn(Phase.DEPLOY);
                scn.EnsureDSForcePile(5);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.DSCardPlayAvailable(inHand));
            }
        }
    }

    private static boolean selectable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        int i = scn.DSGetBPChoices().indexOf(card.getBlueprintId(true));
        return i >= 0 && Boolean.parseBoolean(scn.DSGetADParamAsList("selectable").get(i));
    }
    @Test
    public void DeploysOnlyANonUniqueCloudSector() {
        var scn = GetScenario();
        var imsorry = scn.GetDSCard("version");
        var clouds = scn.GetDSCard("clouds");
        var cloudcity = scn.GetDSCard("cloudcity");
        var bespin = scn.GetDSCard("bespin");

        //test1: the unique Bespin: Cloud City (a cloud sector the current card may deploy) is not a choice
        //test2: non-unique Clouds is
        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveCardsToDSSideOfTable(imsorry);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(clouds, cloudcity);
        assertTrue(scn.DSCardActionAvailable(imsorry, "cloud sector"));
        scn.DSUseCardAction(imsorry, "cloud sector");
        scn.PassAllResponses();
        assertFalse(selectable(scn, cloudcity)); //test1
        assertTrue(selectable(scn, clouds)); //test2
        scn.DSChooseCard(clouds);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, clouds.getZone());
    }
}
