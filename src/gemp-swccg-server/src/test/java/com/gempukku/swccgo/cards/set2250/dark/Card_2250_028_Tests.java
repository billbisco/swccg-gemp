package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Pre-errata Trade Federation Tactics (V) (Dec 2009 sheet, slip 28E), a version of 1250_28. */
public class Card_2250_028_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("luke", "1_19"); put("rifle", "1_153"); put("red1", "1_144"); put("torpedoes", "1_158"); }},
                new HashMap<>() {{ put("tactics", "2250_28"); put("current", "1250_28"); put("sil", "14_88"); put("jango", "1250_8");
                    put("trooper", "1_194"); put("avenger", "1263_19"); put("system", "1_282"); }},
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void TradeFederationTacticsOriginalIdentity() {
        var scn = scenario();
        var bp = scn.GetDSCard("tactics").getBlueprint();
        assertLegacyVersion(bp, scn.GetDSCard("current").getBlueprint(), "1250_28", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2012, 8, 27),
                "USED: For remainder of turn, if your starship is about to be hit (or lost), that action may instead affect your character aboard that starship. LOST: If your [Episode I] character is about to be hit (or lost), that action instead affects your other [Episode I] character present.");
        assertEquals(CardSubtype.USED_OR_LOST, bp.getCardSubtype());
        assertEquals("Trade Federation Tactics", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
    }

    private VirtualTableScenario lukeFiresAtSil(String otherDarkCharacter) {
        var scn = scenario();
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("sil"), scn.GetDSCard(otherDarkCharacter));
        scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("rifle"));
        scn.MoveCardsToDSHand(scn.GetDSCard("tactics"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSActivateForceCheat(4);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.PrepareLSDestiny(6);
        scn.LSUseCardAction(scn.GetLSCard("rifle"), "Fire");
        scn.LSChooseCard(scn.GetDSCard("sil"));
        scn.PassForceUseResponses();
        return scn;
    }

    @Test
    public void TradeFederationTacticsOriginalLostRetargetsEpisodeICharacterAndGoesToLostPile() {
        var scn = lukeFiresAtSil("jango");
        var tactics = scn.GetDSCard("tactics");
        assertTrue(scn.DSCardPlayAvailable(tactics));
        scn.DSPlayCard(tactics);
        if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("sil"))) scn.DSChooseCard(scn.GetDSCard("sil"));
        scn.DSChooseCard(scn.GetDSCard("jango"));
        scn.PassAllResponses();
        scn.PassWeaponFireWithDestinyDraw();
        assertTrue(scn.GetDSCard("jango").isHit());
        assertFalse(scn.GetDSCard("sil").isHit());
        assertInZone(Zone.LOST_PILE, tactics);
    }

    @Test
    public void TradeFederationTacticsOriginalMayNotRetargetToNonEpisodeICharacter() {
        var scn = lukeFiresAtSil("trooper");
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("tactics")));
    }

    @Test
    public void TradeFederationTacticsOriginalUsedLetsStarshipHitAffectCharacterAboardForRestOfTurn() {
        var scn = scenario();
        var system = scn.GetDSCard("system");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        var avenger = scn.GetDSCard("avenger");
        var red1 = scn.GetLSCard("red1");
        var jango = scn.GetDSCard("jango");
        var tactics = scn.GetDSCard("tactics");
        scn.MoveCardsToLocation(system, avenger, red1);
        scn.BoardAsPilot(avenger, jango);
        scn.BoardAsPilot(red1, scn.GetLSCard("luke"));
        scn.AttachCardsTo(red1, scn.GetLSCard("torpedoes"));
        scn.MoveCardsToDSHand(tactics);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSActivateForceCheat(4);
        scn.LSInitiateBattle(system);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.LSPass();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardPlayAvailable(tactics, "USED"));
        scn.DSPlayCard(tactics, "USED");
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, tactics);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.PrepareLSDestiny(7);
        scn.LSUseCardAction(scn.GetLSCard("torpedoes"), "Fire");
        if (scn.LSHasCardChoiceAvailable(avenger)) scn.LSChooseCard(avenger);
        scn.PassForceUseResponses();
        scn.PassResponses("Fire ");
        scn.PassDestinyDrawResponses();
        for (int i = 0; i < 6 && !(scn.DSAnyDecisionsAvailable() && scn.DSCardActionAvailable(tactics)); i++) {
            if (scn.LSAnyDecisionsAvailable()) scn.LSPass(); else if (scn.DSAnyDecisionsAvailable()) scn.DSPass();
        }
        assertTrue(scn.DSCardActionAvailable(tactics, "affect a character aboard"));
        scn.DSUseCardAction(tactics, "affect a character aboard");
        if (scn.DSHasCardChoiceAvailable(jango)) scn.DSChooseCard(jango);
        scn.PassAllResponses();
        assertFalse(avenger.isHit());
        assertTrue(jango.isHit());
    }
}
