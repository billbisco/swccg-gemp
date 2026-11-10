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

/** Slip Sliding Away: original (pre-errata) version of 1220_68. */
public class Card_2220_068_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{

                }},
                new HashMap<>() {{
                    put("vane", "1220_51");
                    put("trooper", "1_194");
                    put("version", "2220_68");
                    put("current", "1220_68");
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
        assertEquals("Cancel Path Of Least Resistance. (Immune to Sense.) OR During your move phase, target opponent's character at an interior Cloud City site. Draw destiny. Relocate target to Weather Vane if destiny > target's ability. OR Relocate your character at an interior Cloud City site to Weather Vane.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_68", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2013, 8, 1), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    @Test
    public void RelocatesYourCharacterAtInteriorCloudCitySiteToWeatherVane() {
        var scn = GetScenario();
        var slip = scn.GetDSCard("version");
        var vane = scn.GetDSCard("vane");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(vane);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), trooper);
        scn.MoveCardsToDSHand(slip);
        scn.SkipToDSTurn(Phase.MOVE);
        assertTrue(scn.DSCardPlayAvailable(slip, "Relocate your character to Weather Vane"));
        scn.DSPlayCard(slip, "Relocate your character to Weather Vane");
        if (scn.DSHasCardChoiceAvailable(trooper))
            scn.DSChooseCard(trooper);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, slip);
        assertTrue(scn.IsStackedOn(vane, trooper));
    }
}
