package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1263_001_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("ben", "1_21")),
                new HashMap<>(Map.of("bane", "1263_1", "blaster", "7_323", "trooper", "1_194",
                        "ship", "7_307", "system", "1_289")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        return scn;
    }

    @Test public void BaneUsesTheOriginalBlockSixNameAndPrintedAttributes() {
        var scn = scenario();
        var b = scn.GetDSCard("bane").getBlueprint();
        assertEquals("Bane, The Bounty Hunter", b.getTitle());
        assertEquals("Bounty hunter.", b.getLore());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(1, b.getDestiny(), scn.epsilon);
        assertEquals(4, b.getPower(), scn.epsilon);
        assertEquals(2, b.getAbility(), scn.epsilon);
        assertEquals(4, b.getDeployCost(), scn.epsilon);
        assertEquals(4, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertFalse(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.ALIEN)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.ALIEN, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.BOUNTY_HUNTER)));
    }

    @Test public void BaneAddsTwoPowerOnlyWhilePiloting() {
        for (boolean piloting : List.of(false, true)) {
            var scn = scenario();
            var ship = scn.GetDSCard("ship");
            var bane = scn.GetDSCard("bane");
            scn.MoveCardsToLocation(scn.GetDSCard("system"), ship);
            int power = scn.GetPower(ship);
            if (piloting) scn.BoardAsPilot(ship, bane);
            else scn.BoardAsPassenger(ship, bane);
            assertEquals(power + (piloting ? 2 : 0), scn.GetPower(ship));
        }
    }

    @Test public void BaneActuallyDeploysTheHoldOutBlasterWithoutForce() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var bane = scn.GetDSCard("bane");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        scn.MoveCardsToLocation(site, bane, trooper);
        scn.MoveCardsToDSHand(blaster);
        scn.SkipToPhase(Phase.CONTROL);
        setForce(scn, 0);
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(0, scn.GetDSForcePileCount());
        scn.DSDeployCard(blaster);
        assertTrue(scn.DSHasCardChoiceAvailable(bane));
        assertFalse(scn.DSHasCardChoiceAvailable(trooper));
        scn.DSChooseCard(bane);
        scn.PassAllResponses();
        assertSame(bane, blaster.getAttachedTo());
        assertEquals(0, scn.GetDSForcePileCount());
    }

    @Test public void BaneActuallyFiresTheHoldOutBlasterForFree() {
        for (boolean baneFires : List.of(false, true)) {
            var scn = scenario();
            var site = scn.GetDSStartingLocation();
            var blaster = scn.GetDSCard("blaster");
            var bearer = scn.GetDSCard(baneFires ? "bane" : "trooper");
            var ben = scn.GetLSCard("ben");
            scn.MoveCardsToLocation(site, bearer, ben);
            scn.AttachCardsTo(bearer, blaster);
            scn.SkipToPhase(Phase.CONTROL);
            setForce(scn, 1);
            scn.SkipToPhase(Phase.BATTLE);
            scn.DSInitiateBattle(site);
            scn.PassAllResponses();
            assertEquals(0, scn.GetDSForcePileCount());
            assertEquals(baneFires, scn.DSCardActionAvailable(blaster, "Fire"));
            if (!baneFires) continue;
            scn.PrepareDSDestiny(6);
            scn.DSUseCardAction(blaster, "Fire");
            scn.DSChooseCard(ben);
            scn.PassWeaponFireWithDestinyDraw();
            for (int i = 0; i < 35 && !ben.isHit(); i++) {
                if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected Bane weapon decision: " + scn.GetCurrentDecision().getText());
            }
            assertTrue(ben.isHit());
            assertEquals(0, scn.GetDSForcePileCount());
        }
    }
    private void setForce(VirtualTableScenario scn, int amount) {
        scn.EnsureDSForcePile(amount);
        while (scn.GetDSForcePileCount() > amount) {
            scn.MoveCardsToTopOfDSReserveDeck((com.gempukku.swccgo.game.PhysicalCardImpl)
                    scn.gameState().getForcePile(scn.DS).getFirst());
        }
    }
}
