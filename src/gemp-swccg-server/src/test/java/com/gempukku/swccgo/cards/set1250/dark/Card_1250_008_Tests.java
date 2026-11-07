package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1250_008_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("jedi", "601_52", "other", "1_17", "saber", "1_155")),
                new HashMap<>(Map.of("mando", "1250_8", "jet", "6_140", "cantina", "1_290", "ship", "7_307",
                        "system", "1_289", "alien", "6_121", "trooper", "1_194")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("cantina"));
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        return scn;
    }

    @Test public void TheMandalorianMatchesOriginalBlockFiveNameAndAttributes() {
        var scn = scenario();
        var b = scn.GetDSCard("mando").getBlueprint();
        assertEquals("The Mandalorian", b.getTitle());
        assertEquals("Trade Federation.", b.getLore());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(2, b.getDestiny(), scn.epsilon);
        assertEquals(3, b.getPower(), scn.epsilon);
        assertEquals(3, b.getAbility(), scn.epsilon);
        assertEquals(5, b.getArmor(), scn.epsilon);
        assertEquals(4, b.getDeployCost(), scn.epsilon);
        assertEquals(4, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.isLegacy());
        assertFalse(b.hasVirtualSuffix());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.ALIEN)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.ALIEN, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.TRADE_FEDERATION)));
    }

    @Test public void TheMandalorianAddsTwoToShipPowerOnlyWhenPiloting() {
        var scn = scenario();
        var mando = scn.GetDSCard("mando");
        var ship = scn.GetDSCard("ship");
        scn.MoveCardsToLocation(scn.GetDSCard("system"), ship);
        int power = scn.GetPower(ship);
        scn.BoardAsPassenger(ship, mando);
        assertEquals(power, scn.GetPower(ship));
        scn.BoardAsPilot(ship, mando);
        assertEquals(power + 2, scn.GetPower(ship));
    }

    @Test public void TheMandalorianActuallyDeploysJetPackFromReserveForFreeAndReshuffles() {
        var scn = scenario();
        var mando = scn.GetDSCard("mando");
        var jet = scn.GetDSCard("jet");
        var other = scn.GetDSCard("alien");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), mando, other);
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(jet, scn.GetDSCard("trooper"));
        scn.SkipToPhase(Phase.DEPLOY);
        var remaining = new ArrayList<>(scn.GetDSReserveDeck());
        remaining.remove(jet);
        scn.gameState().getRandom().setSeed(4096);
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(mando, "Deploy Jet Pack on The Mandalorian");
        boolean found = false;
        for (int i = 0; i < 50 && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (scn.DSHasCardChoiceAvailable(jet)) {
                scn.DSChooseCard(jet);
                found = true;
            } else if (scn.DSHasCardChoiceAvailable(mando)) {
                assertFalse(scn.DSHasCardChoiceAvailable(other));
                scn.DSChooseCard(mando);
            } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected Jet Pack deploy decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(found);
        assertSame(mando, jet.getAttachedTo());
        assertEquals(force, scn.GetDSForcePileCount());
        assertEquals(new HashSet<>(remaining), new HashSet<>(scn.GetDSReserveDeck()));
        assertNotEquals(remaining, scn.GetDSReserveDeck());
    }

    @Test public void TheMandalorianOrdinaryFlightWithJetPackCostsOneLess() {
        for (boolean wearingJet : List.of(false, true)) {
            var scn = scenario();
            var mando = scn.GetDSCard("mando");
            var destination = scn.GetDSCard("cantina");
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), mando);
            if (wearingJet) scn.AttachCardsTo(mando, scn.GetDSCard("jet"));
            scn.SkipToPhase(Phase.CONTROL);
            scn.EnsureDSForcePile(3);
            scn.SkipToPhase(Phase.MOVE);
            int before = scn.GetDSForcePileCount();
            scn.DSMoveCard(mando, destination);
            scn.PassAllResponses();
            assertSame(destination, mando.getAtLocation());
            assertEquals(before - (wearingJet ? 0 : 1), scn.GetDSForcePileCount());
        }
    }

    @Test public void TheMandalorianActuallyFiresDualBlastersAndPaysOneForceForEitherChoice() {
        for (String choice : List.of("Jedi may not fire weapons", "Jedi loses immunity to attrition")) {
            var scn = scenario();
            var mando = scn.GetDSCard("mando");
            var jedi = scn.GetLSCard("jedi");
            var other = scn.GetLSCard("other");
            var saber = scn.GetLSCard("saber");
            var site = scn.GetDSStartingLocation();
            scn.MoveCardsToLocation(site, mando, jedi, other);
            scn.AttachCardsTo(jedi, saber);
            scn.EnsureDSForcePile(3);
            scn.DSStartBattleAndSkipToWeaponsSegment(site);
            int life = scn.GetDSLifeForceRemaining();
            var mods = scn.game().getModifiersQuerying();
            float immunity = mods.getImmunityToAttritionLessThan(scn.gameState(), jedi);
            assertTrue(immunity > 0);
            scn.DSUseCardAction(mando, "Fire Dual Blasters");
            assertTrue(scn.DSHasCardChoiceAvailable(jedi));
            assertFalse(scn.DSHasCardChoiceAvailable(other));
            scn.DSChooseCard(jedi);
            boolean chosen = false;
            for (int i = 0; i < 70 && !scn.AwaitingLSWeaponsSegmentActions(); i++) {
                if (scn.AwaitingDSForceLossPayment()) scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
                else if (scn.DSDecisionAvailable("Choose a Dual Blasters result")) {
                    scn.DSChoose(choice);
                    chosen = true;
                } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected Dual Blasters decision: " + scn.GetCurrentDecision().getText());
            }
            assertTrue(chosen);
            assertTrue(scn.AwaitingLSWeaponsSegmentActions());
            boolean losesImmunity = choice.contains("immunity");
            assertEquals(life - 1, scn.GetDSLifeForceRemaining());
            assertFalse(jedi.isHit());
            assertInZone(Zone.AT_LOCATION, jedi);
            assertEquals(losesImmunity ? 0 : immunity, mods.getImmunityToAttritionLessThan(scn.gameState(), jedi), scn.epsilon);
            assertEquals(losesImmunity, scn.LSCardActionAvailable(saber, "Fire"));
        }
    }
}
