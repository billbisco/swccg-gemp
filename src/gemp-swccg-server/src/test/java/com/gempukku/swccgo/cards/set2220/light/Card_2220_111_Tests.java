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

/** Advantage: original (pre-errata) version of 1220_111. */
public class Card_2220_111_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("saber", "1_155");
                    put("version", "2220_111");
                    put("current", "1220_111");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
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
        assertEquals("Deploy on your battleground. During battle, whenever opponent's card is hit by a non-[Permanent Weapon] weapon (once per card targeted), opponent's total power is cumulatively -4 (-2 if hit by a lightsaber).",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_111", version.currentPrintingId());
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
                var onTable = scn.GetLSCard(currentOnTable ? "current" : "version");
                var inHand = scn.GetLSCard(currentOnTable ? "version" : "current");
                scn.StartGame();
                scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
                if (otherOut)
                    scn.AttachCardsTo(scn.GetLSStartingLocation(), onTable);
                scn.MoveCardsToLSHand(inHand);
                scn.SkipToLSTurn(Phase.DEPLOY);
                scn.EnsureLSForcePile(5);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.LSCardPlayAvailable(inHand));
            }
        }
    }

    @Test
    public void LightsaberHitMakesOpponentsTotalPowerMinusTwoWithoutAChoice() {
        var scn = GetScenario();
        var advantage = scn.GetLSCard("version");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        //test1: no optional "Make opponent's total power" action: it is automatic
        //test2: -2 for a lightsaber hit
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, stormtrooper);
        scn.AttachCardsTo(site, advantage);
        scn.AttachCardsTo(luke, saber);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassBattleStartResponses();
        scn.PrepareLSDestiny(7);
        scn.PrepareLSDestiny(7);
        scn.LSUseCardAction(saber, "Fire");
        scn.LSChooseCard(stormtrooper);
        scn.PassForceUseResponses();
        scn.PassWeaponFireWithDestinyDraw(2);
        assertFalse(scn.GetLSAvailableActions().toString().contains("Make opponent's total power")); //test1
        scn.PassAllResponses();
        assertTrue(stormtrooper.isHit());
        assertEquals(-2, advantage.getWhileInPlayData().getFloatValue(), scn.epsilon); //test2
    }
}
