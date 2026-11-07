package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_1250_046_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("trooper", "1250_46"); put("leader", "12_2");
            put("rebelLeader", "1_17"); put("nonleader", "1116_8");
        }}, new HashMap<>() {{
            put("target", "1_194"); put("vehicle", "8_171"); put("creature", "4_110");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void RepublicTrooperWithBlasterRifleMetadataMatchesOfficialImage() {
        var scn = scenario();
        var card = scn.GetLSCard("trooper").getBlueprint();
        assertEquals("Republic Trooper With Blaster Rifle", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, card.getExpansionSet());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.REPUBLIC); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC); add(Icon.WARRIOR); add(Icon.PERMANENT_WEAPON);
            add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_5);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
    }

    @Test
    public void RepublicTrooperWithBlasterRifleDeploysForTwoWithRepublicLeaderAtSameSite() {
        checkDeployment("leader", true, 2);
    }

    @Test
    public void RepublicTrooperWithBlasterRiflePaysThreeWithoutLeader() {
        checkDeployment(null, false, 3);
    }

    @Test
    public void RepublicTrooperWithBlasterRifleLeaderAtOtherSiteDoesNotReduceDeployCost() {
        checkDeployment("leader", false, 3);
    }

    @Test
    public void RepublicTrooperWithBlasterRifleRebelLeaderDoesNotReduceDeployCost() {
        checkDeployment("rebelLeader", true, 3);
    }

    @Test
    public void RepublicTrooperWithBlasterRifleRepublicNonleaderDoesNotReduceDeployCost() {
        checkDeployment("nonleader", true, 3);
    }

    private void checkDeployment(String companion, boolean sameSite, int cost) {
        var scn = scenario();
        var trooper = scn.GetLSCard("trooper");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        if (companion != null) {
            scn.MoveCardsToLocation(sameSite ? site : scn.GetDSStartingLocation(), scn.GetLSCard(companion));
        }
        scn.MoveCardsToHand(trooper);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(trooper);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, trooper.getAtLocation());
        assertEquals(force - cost, scn.GetLSForcePileCount());
    }

    @Test
    public void RepublicTrooperWithBlasterRifleDestinyPlusOneEqualToDefenseMisses() {
        checkCharacterShot(0, false);
    }

    @Test
    public void RepublicTrooperWithBlasterRifleHitReducesForfeitByTwoForFree() {
        checkCharacterShot(1, true);
    }

    private void checkCharacterShot(int destiny, boolean hit) {
        var scn = scenario();
        var trooper = scn.GetLSCard("trooper");
        var target = scn.GetDSCard("target");
        var vehicle = scn.GetDSCard("vehicle");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, target, vehicle);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(destiny);
        int force = scn.GetLSForcePileCount();
        int forfeit = scn.GetForfeit(target);
        assertEquals(1, scn.GetDefense(target));
        scn.LSUseCardAction(trooper, "Fire");
        assertFalse(scn.LSGetCardChoices().contains(String.valueOf(vehicle.getCardId())));
        scn.LSChooseCard(target);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertEquals(hit, target.isHit());
        assertEquals(forfeit - (hit ? 2 : 0), scn.GetForfeit(target));
        assertEquals(force, scn.GetLSForcePileCount());
    }

    @Test
    public void RepublicTrooperWithBlasterRifleMayHitCreatureForFree() {
        var scn = scenario();
        var trooper = scn.GetLSCard("trooper");
        var creature = scn.GetDSCard("creature");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, creature);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSUseCardAction(site, "attack");
        scn.PassAllResponses();
        scn.PrepareLSDestiny(5);
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(trooper, "Fire");
        scn.LSChooseCard(creature);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(creature.isHit() || scn.GetDSLostPile().contains(creature));
        assertEquals(force, scn.GetLSForcePileCount());
    }
}
