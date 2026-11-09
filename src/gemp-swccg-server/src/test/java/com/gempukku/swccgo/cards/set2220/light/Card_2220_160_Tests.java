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

/** Wookiee Strangle: original (pre-errata) version of 1220_160 (Out Of Play Interrupt, no once-per-game limit). */
public class Card_2220_160_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("chewie", "2_3");
                    put("second", "2220_160");
                    put("version", "2220_160");
                    put("current", "1220_160");
                }},
                new HashMap<>() {{
                    put("trooper1", "1_194");
                    put("trooper2", "1_194");
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
        assertEquals("During your move phase, target opponent's character present with your Wookiee. Both players draw destiny. Add Wookiee's power; opponent adds target's defense value. Target lost if your total destiny +2 > opponent's total destiny.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_160", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2012, 11, 19), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    private void strangle(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl strangle,
                          com.gempukku.swccgo.game.PhysicalCardImpl chewie, com.gempukku.swccgo.game.PhysicalCardImpl target) {
        scn.PrepareLSDestiny(1);
        scn.PrepareDSDestiny(1);
        assertTrue("LS actions: " + scn.GetLSAvailableActions() + " target zone " + target.getZone() + " strangle zone " + strangle.getZone(),
                scn.LSCardPlayAvailable(strangle, "Target a character"));
        scn.LSPlayCard(strangle, "Target a character");
        if (scn.LSHasCardChoiceAvailable(target))
            scn.LSChooseCard(target);
        if (scn.LSHasCardChoiceAvailable(chewie))
            scn.LSChooseCard(chewie);
        for (int i = 0; i < 24; i++) {
            if (strangle.getZone() == Zone.OUT_OF_PLAY
                    && (target.getZone() == Zone.LOST_PILE || target.getZone() == Zone.TOP_OF_LOST_PILE))
                break;
            if (scn.LSHasCardChoiceAvailable(target)) {
                scn.LSChooseCard(target);
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(chewie)) {
                scn.LSChooseCard(chewie);
                continue;
            }
            scn.PassAllResponses();
        }
        assertInZone(Zone.LOST_PILE, target);
        assertInZone(Zone.OUT_OF_PLAY, strangle); // Out Of Play Interrupt
    }

    @Test
    public void IsAnOutOfPlayInterrupt() {
        var scn = GetScenario();
        //test1: the sheet slip prints an Out Of Play Interrupt; the current card is a Lost Interrupt
        assertEquals(CardSubtype.OUT_OF_PLAY, scn.GetLSCard("version").getBlueprint().getCardSubtype()); //test1
        assertEquals(CardSubtype.LOST, scn.GetLSCard("current").getBlueprint().getCardSubtype());
    }

    @Test
    public void MayBePlayedMoreThanOncePerGame() {
        var scn = GetScenario();
        var strangle = scn.GetLSCard("version");
        var current = scn.GetLSCard("current");
        var chewie = scn.GetLSCard("chewie");
        var trooper1 = scn.GetDSCard("trooper1");
        var trooper2 = scn.GetDSCard("trooper2");

        var second = scn.GetLSCard("second");

        //test1: a second copy may be played in the same game (the current card: once per game)
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), chewie, trooper1, trooper2);
        scn.MoveCardsToLSHand(strangle, second);
        scn.SkipToLSTurn(Phase.MOVE);
        strangle(scn, strangle, chewie, trooper1);
        scn.SkipToLSTurn(Phase.MOVE); // next turn
        strangle(scn, second, chewie, trooper2); //test1
    }
}
