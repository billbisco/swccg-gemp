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

/** Danz Borin: original (pre-errata) version of 1210_6. */
public class Card_2210_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("han", "1_11");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("pilot", "1210_13");
                    put("avenger", "4_172");
                    put("slave", "5_177");
                    put("nalhutta", "6_168");
                    put("version", "2210_6");
                    put("current", "1210_6");
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
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("Adds 3 to power of anything he pilots. May add 1 to Force drains at same Nal Hutta location. Danz is a matching pilot for any [Independent Starship] starship and, while piloting one, may not draw more than one battle destiny here (that destiny may not be canceled or modified by opponent).",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1210_6", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2013, 4, 13), version.supersededOn());
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
                scn.EnsureDSForcePile(5);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.DSCardPlayAvailable(inHand));
            }
        }
    }

    private VirtualTableScenario battleWithDanzPilotingSlaveI(int destiny) {
        var scn = GetScenario();
        var danz = scn.GetDSCard("version");
        var pilot = scn.GetDSCard("pilot");
        var slave = scn.GetDSCard("slave");
        var avenger = scn.GetDSCard("avenger");
        var xwing = scn.GetLSCard("xwing");
        var han = scn.GetLSCard("han");
        var nalhutta = scn.GetDSCard("nalhutta");

        scn.StartGame();
        scn.MoveLocationToTable(nalhutta);
        scn.MoveCardsToLocation(nalhutta, slave, avenger, xwing);
        scn.BoardAsPilot(slave, danz);
        scn.BoardAsPilot(avenger, pilot);
        scn.BoardAsPilot(xwing, han);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(destiny);
        scn.DSInitiateBattle(nalhutta);
        return scn;
    }

    @Test
    public void WhilePilotingIndependentStarshipMayNotDrawMoreThanOneBattleDestiny() {
        var scn = battleWithDanzPilotingSlaveI(4);
        //test1: one battle destiny only (two Imperial pilots would otherwise draw two)
        scn.PassWeaponsSegmentActions();
        scn.SkipBattleDestinyDraws(true);
        assertEquals(4, scn.GetDSTotalDestiny(), scn.epsilon); //test1
        assertFalse(scn.DSDecisionAvailable("battle destiny?"));
    }

    @Test
    public void ThatDestinyMayNotBeCanceledOrModifiedByOpponent() {
        var scn = battleWithDanzPilotingSlaveI(4);
        //test1: opponent may not cancel DS battle destiny draws here
        //test2: opponent may not modify them
        scn.PassAllResponses();
        assertTrue(scn.game().getModifiersQuerying().mayNotCancelBattleDestinyDraws(
                scn.gameState(), scn.DS, scn.LS, false)); //test1
        assertTrue(scn.game().getModifiersQuerying().mayNotModifyBattleDestinyDraws(
                scn.gameState(), scn.DS, scn.LS)); //test2
    }

    @Test
    public void IsMatchingPilotForIndependentStarshipAndAddsThreeToPower() {
        var scn = GetScenario();
        var danz = scn.GetDSCard("version");
        var slave = scn.GetDSCard("slave");
        var nalhutta = scn.GetDSCard("nalhutta");

        scn.StartGame();
        scn.MoveLocationToTable(nalhutta);
        scn.MoveCardsToLocation(nalhutta, slave);
        scn.BoardAsPilot(slave, danz);
        assertTrue(scn.game().getModifiersQuerying().isMatchingPair(scn.gameState(), danz, slave));
        assertEquals(7, scn.GetPower(slave));
    }
}
