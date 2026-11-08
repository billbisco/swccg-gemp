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

/** Original version of 1220_6 (Anything Goes only). */
public class Card_2220_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("version", "2220_6");
                    put("current", "1220_6");
                    put("devastator", "1_301");
                    put("tatooine", "1_289");
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
        assertEquals("Adds 3 to power of anything he pilots. Twice per game during opponent's turn, may place three cards from hand on your Force Pile to draw top two cards from Reserve Deck.",
                card.getGameText());
        assertNotEquals(current.getGameText(), card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_6", version.currentPrintingId());
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
                var onTable = scn.GetDSCard(currentOnTable ? "current" : "version");
                var inHand = scn.GetDSCard(currentOnTable ? "version" : "current");
                scn.StartGame();

                if (otherOut)
                    scn.MoveCardsToLocation(scn.GetDSStartingLocation(), onTable);
                scn.MoveCardsToDSHand(inHand);
                scn.SkipToDSTurn(Phase.DEPLOY);
                scn.EnsureDSForcePile(8);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.DSCardPlayAvailable(inHand));
            }
        }
    }

    @Test
    public void OriginalAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var piett = scn.GetDSCard("version");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        int unpiloted = devastator.getBlueprint().getPower().intValue();
        scn.BoardAsPilot(devastator, piett);
        assertEquals(unpiloted + 3, scn.GetPower(devastator));
    }
}
