package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_1262_004_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("luke", "1_19"); put("leia", "1_17"); put("extra", "1_19"); }},
                new HashMap<>() {{ put("comlink", "1262_4"); put("trooper", "1_194"); put("guard", "1_194"); put("reinforcement", "1_194");
                    put("evazan", "1_172"); put("platform", "5_169"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ComlinkVStatsAndIdentityMatchPrintedCard() {
        var scn = scenario();
        var bp = scn.GetDSCard("comlink").getBlueprint();
        assertEquals("Comlink", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertTrue(bp.isLegacy());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.DEVICE); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.DEVICE); add(Icon.LEGACY_BLOCK_6); }});
        assertTrue(bp.getGameText().startsWith("Deploy on an Imperial warrior."));
    }

    @Test
    public void ComlinkVDeploysForFreeOnlyOnYourImperialWarrior() {
        var scn = scenario();
        var comlink = scn.GetDSCard("comlink");
        var trooper = scn.GetDSCard("trooper");
        var evazan = scn.GetDSCard("evazan");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), trooper, evazan);
        scn.MoveCardsToHand(comlink);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(3);
        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSDeployAvailable(comlink));
        scn.DSDeployCard(comlink);
        assertTrue(scn.DSHasCardChoiceAvailable(trooper));
        assertFalse(scn.DSHasCardChoiceAvailable(evazan));
        scn.DSChooseCard(trooper);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(trooper, comlink));
        assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test
    public void ComlinkVMayNotDeployWithoutImperialWarrior() {
        var scn = scenario();
        var comlink = scn.GetDSCard("comlink");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("evazan"));
        scn.MoveCardsToHand(comlink);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(3);
        assertFalse(scn.DSDeployAvailable(comlink));
    }

    /** Comlink on a trooper at Chasm Walkway; battle is initiated by the Light Side at the given site. */
    private VirtualTableScenario reactSetup(boolean adjacent, boolean otherPlanet) {
        var scn = scenario();
        var walkway = scn.GetLSStartingLocation();
        var platform = scn.GetDSCard("platform");
        scn.StartGame();
        scn.MoveLocationToTable(platform);
        assertTrue(scn.IsAdjacentTo(walkway, platform));
        scn.MoveCardsToLocation(walkway, scn.GetDSCard("trooper"));
        scn.AttachCardsTo(scn.GetDSCard("trooper"), scn.GetDSCard("comlink"));
        var battleSite = otherPlanet ? scn.GetDSStartingLocation() : (adjacent ? platform : walkway);
        scn.MoveCardsToLocation(battleSite, scn.GetDSCard("guard"), scn.GetLSCard("luke"));
        scn.MoveCardsToDSHand(scn.GetDSCard("reinforcement"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(3);
        scn.LSActivateForceCheat(2);
        scn.LSInitiateBattle(battleSite);
        return scn;
    }

    private void reactWith(VirtualTableScenario scn, String card, com.gempukku.swccgo.game.PhysicalCardImpl site) {
        var comlink = scn.GetDSCard("comlink");
        assertTrue(scn.DSCardActionAvailable(comlink, "Deploy a character as a 'react'"));
        scn.DSUseCardAction(comlink, "Deploy a character as a 'react'");
        if (scn.DSHasCardChoiceAvailable(scn.GetDSCard(card))) scn.DSChooseCard(scn.GetDSCard(card));
        if (scn.DSDecisionAvailable("Choose where to deploy") || scn.DSDecisionAvailable("Choose location where to deploy")) {
            scn.DSChooseCard(site);
        }
        scn.PassAllResponses();
        assertEquals(site, scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), scn.GetDSCard(card)));
    }

    @Test
    public void ComlinkVLetsYourCharacterReactToAdjacentSite() {
        var scn = reactSetup(true, false);
        reactWith(scn, "reinforcement", scn.GetDSCard("platform"));
    }

    @Test
    public void ComlinkVLetsYourCharacterReactToSameSite() {
        var scn = reactSetup(false, false);
        reactWith(scn, "reinforcement", scn.GetLSStartingLocation());
    }

    @Test
    public void ComlinkVDoesNotGrantReactToNonAdjacentSite() {
        var scn = reactSetup(false, true);
        assertFalse(scn.AnyDecisionsAvailable(scn.DS));
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    /** Dark Side Force drains at the given site, where Comlink is on a trooper. */
    private VirtualTableScenario drainSetup(boolean atOpponentsSite) {
        var scn = scenario();
        var platform = scn.GetDSCard("platform");
        scn.StartGame();
        scn.MoveLocationToTable(platform);
        var site = atOpponentsSite ? scn.GetLSStartingLocation() : platform;
        scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"));
        scn.AttachCardsTo(scn.GetDSCard("trooper"), scn.GetDSCard("comlink"));
        scn.MoveCardsToLSHand(scn.GetLSCard("leia"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(2);
        scn.MoveCardsToTopOfLSUsedPile(scn.GetLSCard("extra"));
        assertTrue(scn.DSForceDrainAvailable(site));
        scn.DSForceDrainAt(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSForceLossPayment());
        return scn;
    }

    @Test
    public void ComlinkVForceDrainLossAtOpponentsSiteMustComeFromLifeForce() {
        var scn = drainSetup(true);
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("leia")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetTopOfLSReserveDeck()));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetTopOfLSForcePile()));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("extra")));
    }

    @Test
    public void ComlinkVDoesNotRestrictForceLossAtYourOwnSite() {
        var scn = drainSetup(false);
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("leia")));
    }
}
