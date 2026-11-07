package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1263_001_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("luke", "1_19"); put("leia", "1_17"); }},
                new HashMap<>() {{ put("bane", "1263_1"); put("blaster", "7_323"); put("trooper", "1_194"); put("vader", "1_168");
                    put("avenger", "1263_19"); put("system", "1_282"); }},
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void CadBaneStatsAndIdentityMatchPrintedCard() {
        var scn = scenario();
        var bp = scn.GetDSCard("bane").getBlueprint();
        assertEquals("Cad Bane", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertTrue(bp.isLegacy());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(1, bp.getDestiny(), scn.epsilon);
        assertEquals(4, bp.getDeployCost(), scn.epsilon);
        assertEquals(4, bp.getPower(), scn.epsilon);
        assertEquals(2, bp.getAbility(), scn.epsilon);
        assertEquals(4, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals("Bounty Hunter", bp.getLore());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.ALIEN); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.ALIEN); add(Icon.PILOT); add(Icon.WARRIOR); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_6); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.BOUNTY_HUNTER); }});
        scn.BlueprintPersonaCheck(bp, new ArrayList<>() {{ add(Persona.CAD); }});
    }

    @Test
    public void CadBaneAddsTwoToPowerOfStarshipHePilots() {
        var scn = scenario();
        scn.StartGame();
        var avenger = scn.GetDSCard("avenger");
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), avenger);
        scn.BoardAsPilot(avenger, scn.GetDSCard("bane"));
        assertEquals(5, scn.GetPower(avenger));
    }

    private int deployBlasterCost(String on) {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var blaster = scn.GetDSCard("blaster");
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetDSCard(on));
        scn.MoveCardsToHand(blaster);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(4);
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(blaster);
        if (scn.DSHasCardChoiceAvailable(scn.GetDSCard(on))) scn.DSChooseCard(scn.GetDSCard(on));
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(scn.GetDSCard(on), blaster));
        return force - scn.GetDSForcePileCount();
    }

    @Test
    public void CadBanePondaBabasHoldOutBlasterDeploysForFreeOnBane() {
        assertEquals(0, deployBlasterCost("bane"));
    }

    @Test
    public void CadBaneDoesNotMakeHoldOutBlasterFreeOnOtherWarrior() {
        assertEquals(2, deployBlasterCost("trooper"));
    }

    /** Bane (armed) and Vader battle Luke; Bane fires the Hold-out Blaster at Luke and hits. */
    private VirtualTableScenario hitLuke(int force) {
        var scn = scenario();
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetDSCard("bane"), scn.GetDSCard("vader"), scn.GetLSCard("luke"));
        scn.AttachCardsTo(scn.GetDSCard("bane"), scn.GetDSCard("blaster"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(force + 1);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        while (scn.GetDSForcePileCount() > force) scn.MoveCardsToTopOfDSReserveDeck(scn.GetTopOfDSForcePile());
        var blaster = scn.GetDSCard("blaster");
        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
        scn.PrepareDSDestiny(6);
        scn.DSUseCardAction(blaster, "Fire");
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("luke"))) scn.DSChooseCard(scn.GetLSCard("luke"));
        scn.PassForceUseResponses();
        scn.PassResponses("Fire ");
        scn.PassDestinyDrawResponses();
        scn.PassResponses("ABOUT_TO_BE_HIT");
        assertTrue(scn.GetLSCard("luke").isHit());
        for (int i = 0; i < 8; i++) {
            if (scn.DSAnyDecisionsAvailable() && scn.GetAvailableActions(scn.DS).stream().anyMatch(a -> a.contains("Annihilate"))) break;
            if (scn.LSAnyDecisionsAvailable()) scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable()) scn.DSPass();
        }
        return scn;
    }

    @Test
    public void CadBaneHoldOutBlasterFiresForFreeOnBane() {
        var scn = hitLuke(0);
        assertEquals(0, scn.GetDSForcePileCount());
    }

    private void forfeitLuke(VirtualTableScenario scn) {
        scn.PassAllResponses();
        if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass();
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(4);
        scn.SkipToDamageSegment(true);
        scn.PassDamageSegmentActions();
        var luke = scn.GetLSCard("luke");
        if (scn.AwaitingLSAttritionPayment()) scn.LSPayAttritionFromCardInPlay(luke);
        else scn.LSPayBattleDamageFromCardInPlay(luke);
        scn.PassAllResponses();
    }

    @Test
    public void CadBaneAnnihilatesCharacterHeJustHitSoItIsPlacedOutOfPlayWhenForfeited() {
        var scn = hitLuke(0);
        var bane = scn.GetDSCard("bane");
        assertTrue(scn.DSCardActionAvailable(bane, "'Annihilate'"));
        scn.DSUseCardAction(bane, "'Annihilate'");
        forfeitLuke(scn);
        assertInZone(Zone.OUT_OF_PLAY, scn.GetLSCard("luke"));
    }

    @Test
    public void CadBaneWithoutAnnihilateForfeitedCharacterGoesToLostPile() {
        var scn = hitLuke(0);
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("bane"), "'Annihilate'"));
        forfeitLuke(scn);
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("luke"));
    }
}
