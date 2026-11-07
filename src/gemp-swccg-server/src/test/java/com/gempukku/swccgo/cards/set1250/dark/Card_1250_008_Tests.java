package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_1250_008_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("obiwan", "1_21"); put("luke", "1_19"); put("cantina", "1_128"); put("db94", "1_129"); }},
                new HashMap<>() {{ put("jango", "1250_8"); put("jetpack", "6_140"); put("avenger", "1263_19"); put("system", "1_282"); }},
                10, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void JangoFettStatsAndIdentityMatchPrintedCard() {
        var scn = scenario();
        var bp = scn.GetDSCard("jango").getBlueprint();
        assertEquals("Jango Fett", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertTrue(bp.isLegacy());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(2, bp.getDestiny(), scn.epsilon);
        assertEquals(4, bp.getDeployCost(), scn.epsilon);
        assertEquals(3, bp.getPower(), scn.epsilon);
        assertEquals(3, bp.getAbility(), scn.epsilon);
        assertEquals(4, bp.getForfeit(), scn.epsilon);
        assertEquals(5, bp.getArmor(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.ALIEN); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.ALIEN); add(Icon.PREMIUM); add(Icon.EPISODE_I); add(Icon.PILOT);
            add(Icon.WARRIOR); add(Icon.PERMANENT_WEAPON); add(Icon.LEGACY_BLOCK_5); }});
        scn.BlueprintPersonaCheck(bp, new ArrayList<>() {{ add(Persona.JANGO_FETT); }});
        assertEquals("Trade Federation.", bp.getLore());
    }

    @Test
    public void JangoFettAddsTwoToPowerOfStarshipHePilots() {
        var scn = scenario();
        scn.StartGame();
        var avenger = scn.GetDSCard("avenger");
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), avenger);
        scn.BoardAsPilot(avenger, scn.GetDSCard("jango"));
        assertEquals(5, scn.GetPower(avenger));
    }

    @Test
    public void JangoFettDeploysJetPackOnHimForFreeFromReserveDeckOncePerTurn() {
        var scn = scenario();
        var jango = scn.GetDSCard("jango");
        var jetpack = scn.GetDSCard("jetpack");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), jango);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(4);
        scn.MoveCardsToTopOfDSReserveDeck(jetpack);
        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(jango, "Deploy Jet Pack"));
        scn.DSUseCardAction(jango, "Deploy Jet Pack");
        if (scn.DSHasCardChoiceAvailable(jetpack)) scn.DSChooseCard(jetpack);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(jango, jetpack));
        assertEquals(force, scn.GetDSForcePileCount());
        if (scn.LSAnyDecisionsAvailable()) scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(jango, "Deploy Jet Pack"));
    }

    private int moveCost(boolean withJetPack) {
        var scn = scenario();
        var jango = scn.GetDSCard("jango");
        var cantina = scn.GetLSCard("cantina");
        var db94 = scn.GetLSCard("db94");
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(db94);
        assertTrue(scn.IsAdjacentTo(cantina, db94));
        scn.MoveCardsToLocation(cantina, jango);
        if (withJetPack) scn.AttachCardsTo(jango, scn.GetDSCard("jetpack"));
        scn.SkipToDSTurn(Phase.MOVE);
        scn.DSActivateForceCheat(3);
        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSMoveAvailable(jango));
        scn.DSMoveCard(jango, db94);
        scn.PassAllResponses();
        assertEquals(db94, scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), jango));
        return force - scn.GetDSForcePileCount();
    }

    @Test
    public void JangoFettMovesUsingJetPackForOneLessForce() {
        assertEquals(1, moveCost(false));
        assertEquals(0, moveCost(true));
    }

    private VirtualTableScenario battle(String target) {
        var scn = scenario();
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetDSCard("jango"), scn.GetLSCard(target));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(3);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        return scn;
    }

    private void fireDualBlasters(VirtualTableScenario scn, String option) {
        var jango = scn.GetDSCard("jango");
        var obiwan = scn.GetLSCard("obiwan");
        int life = scn.GetDSLifeForceRemaining();
        assertTrue(scn.DSCardActionAvailable(jango, "Fire"));
        scn.DSUseCardAction(jango, "Fire");
        if (scn.DSHasCardChoiceAvailable(obiwan)) scn.DSChooseCard(obiwan);
        scn.PassAllResponses();
        if (scn.AwaitingDSForceLossPayment()) scn.DSPayForceLossFromForcePile();
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose effect on"));
        scn.DSChooseOption(option);
        scn.PassAllResponses();
        assertEquals(life - 1, scn.GetDSLifeForceRemaining());
    }

    @Test
    public void JangoFettDualBlastersLoseOneForceTargetJediMayNotFireWeapons() {
        var scn = battle("obiwan");
        var obiwan = scn.GetLSCard("obiwan");
        assertTrue(scn.game().getModifiersQuerying().notProhibitedFromFiringWeapons(scn.gameState(), obiwan));
        fireDualBlasters(scn, "may not fire weapons");
        assertFalse(scn.game().getModifiersQuerying().notProhibitedFromFiringWeapons(scn.gameState(), obiwan));
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), obiwan), scn.epsilon);
    }

    @Test
    public void JangoFettDualBlastersTargetJediLosesImmunityToAttrition() {
        var scn = battle("obiwan");
        var obiwan = scn.GetLSCard("obiwan");
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), obiwan), scn.epsilon);
        fireDualBlasters(scn, "loses immunity");
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), obiwan), scn.epsilon);
        assertTrue(scn.game().getModifiersQuerying().notProhibitedFromFiringWeapons(scn.gameState(), obiwan));
    }

    @Test
    public void JangoFettDualBlastersMayNotTargetNonJedi() {
        var scn = battle("luke");
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("jango"), "Fire"));
    }
}
