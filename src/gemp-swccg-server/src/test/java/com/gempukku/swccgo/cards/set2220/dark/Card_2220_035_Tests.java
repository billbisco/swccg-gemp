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

/** I Want That Ship: original (pre-errata) version of 1220_35. */
public class Card_2220_035_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("han", "1_11");
                }},
                new HashMap<>() {{
                    put("trooper1", "1_194");
                    put("trooper2", "1_194");
                    put("blaster1", "1_317");
                    put("blaster2", "1_317");
                    put("version", "2220_35");
                    put("current", "1220_35");
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
        assertEquals("Deploy on your battleground. During battle, whenever opponent's card is hit by a non-[Permanent Weapon] weapon (once per card targeted), opponent's total power is cumulatively -4 (-2 if hit by a lightsaber).",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_35", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2012, 9, 21), version.supersededOn());
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
                scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("trooper1"));
                if (otherOut)
                    scn.AttachCardsTo(scn.GetDSStartingLocation(), onTable);
                scn.MoveCardsToDSHand(inHand);
                scn.SkipToDSTurn(Phase.DEPLOY);
                scn.EnsureDSForcePile(5);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.DSCardPlayAvailable(inHand));
            }
        }
    }

    private static float runningTotal(com.gempukku.swccgo.game.PhysicalCardImpl card) {
        var data = card.getWhileInPlayData();
        return data == null || data.getFloatValue() == null ? 0 : data.getFloatValue();
    }

    private void fire(VirtualTableScenario scn, String blaster, String target) {
        scn.PrepareDSDestiny(6);
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard(blaster), "Fire"));
        scn.DSUseCardAction(scn.GetDSCard(blaster), "Fire");
        scn.DSChooseCard(scn.GetLSCard(target));
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(scn.GetLSCard(target).isHit());
    }

    @Test
    public void EachHitOnADifferentCardMakesOpponentsTotalPowerCumulativelyMinusFour() {
        var scn = GetScenario();
        var ship = scn.GetDSCard("version");
        var site = scn.GetDSStartingLocation();

        //test1: first hit: -4
        //test2: second hit on another card: cumulatively -8 (the current card: once per battle)
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetLSCard("han"), scn.GetDSCard("trooper1"), scn.GetDSCard("trooper2"));
        scn.AttachCardsTo(scn.GetDSCard("trooper1"), scn.GetDSCard("blaster1"));
        scn.AttachCardsTo(scn.GetDSCard("trooper2"), scn.GetDSCard("blaster2"));
        scn.AttachCardsTo(site, ship);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        fire(scn, "blaster1", "luke");
        assertEquals(-4, runningTotal(ship), scn.epsilon); //test1
        if (scn.AwaitingLSWeaponsSegmentActions())
            scn.LSPass();
        fire(scn, "blaster2", "han");
        assertEquals(-8, runningTotal(ship), scn.epsilon); //test2
    }
}
