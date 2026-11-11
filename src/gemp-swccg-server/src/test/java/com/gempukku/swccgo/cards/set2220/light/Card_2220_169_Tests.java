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

/** Luke's Blaster Pistol: original (pre-errata) version of 1220_169. */
public class Card_2220_169_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("version", "2220_169");
                    put("current", "1220_169");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("trooper", "1_194");
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
        assertEquals("Deploy on Luke. During battle, opponent may not move from here. Twice per battle, may target a character (target loses immunity to attrition for remainder of turn) or vehicle (with maneuver) for free. Draw destiny. Target hit and forfeit -3 if destiny +2 > defense value.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_169", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2011, 8, 26), version.supersededOn());
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
                    scn.AttachCardsTo(scn.GetLSCard("luke"), onTable);
                scn.MoveCardsToLSHand(inHand);
                scn.SkipToLSTurn(Phase.DEPLOY);
                scn.EnsureLSForcePile(5);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.LSCardPlayAvailable(inHand));
            }
        }
    }

    private void fire(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl blaster, com.gempukku.swccgo.game.PhysicalCardImpl target) {
        scn.PrepareLSDestiny(0);
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(target);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        if (scn.AwaitingDSWeaponsSegmentActions())
            scn.DSPass();
    }

    @Test
    public void FiresTwicePerBattle() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("version");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        //test1: second firing in the same battle (the current card: once)
        //test2: no third firing
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader, trooper);
        scn.AttachCardsTo(luke, blaster);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        fire(scn, blaster, vader);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        fire(scn, blaster, trooper); //test1
        if (scn.AwaitingLSWeaponsSegmentActions())
            assertFalse(scn.LSCardActionAvailable(blaster, "Fire")); //test2
    }
}
