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

/** Original version of 1220_67 (Anything Goes only). */
public class Card_2220_067_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("version", "2220_67");
                    put("current", "1220_67");
                    put("blizzard", "1220_80");
                    put("veers", "3_87");
                    put("gear", "8_119");
                    put("rack", "1_211");
                    put("rack2", "1_211");
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
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("Dark: May deploy Biker Scout Gear here from Reserve Deck; reshuffle, and while here, may deploy one card without ability in its game text from Reserve Deck; reshuffle. Light: Your characters may enter/exit here for 2 Force each. Unless you occupy, AT-AT's immunity to attrition is +1 (or immune to attrition if non-unique).",
                card.getGameText());
        assertNotEquals(current.getGameText(), card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_67", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2010, 6, 28), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    private static final String NAMED = "Deploy a card from Biker Scout Gear game text";

    private boolean deployNamed(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl platform, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        if (!scn.DSCardActionAvailable(platform, NAMED))
            return false;
        scn.DSUseCardAction(platform, NAMED);
        if (scn.DSHasCardChoiceAvailable(card))
            scn.DSChooseCard(card);
        scn.PassAllResponses();
        return true;
    }

    @Test
    public void OriginalDeploysCardsFromBikerScoutGearTextMoreThanOncePerTurn() {
        var scn = GetScenario();
        var platform = scn.GetDSCard("version");
        var blizzard = scn.GetDSCard("blizzard");
        var veers = scn.GetDSCard("veers");
        var gear = scn.GetDSCard("gear");
        var rack = scn.GetDSCard("rack");
        var rack2 = scn.GetDSCard("rack2");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, blizzard);
        scn.BoardAsPilot(blizzard, veers);
        scn.MoveCardsToHand(platform);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(platform);
        scn.PassAllResponses();
        scn.MoveCardsToDSSideOfTable(gear);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(rack, rack2);

        assertTrue(deployNamed(scn, platform, rack));
        var first = rack.getZone() == Zone.SIDE_OF_TABLE ? rack : rack2;
        var second = first == rack ? rack2 : rack;
        assertInZone(Zone.SIDE_OF_TABLE, first);
        if (scn.DSGetDecision() == null && scn.LSGetDecision() != null)
            scn.LSPass();
        assertTrue(deployNamed(scn, platform, second)); //no once-per-turn limit
        assertInZone(Zone.SIDE_OF_TABLE, second);
    }
}
