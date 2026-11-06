package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1220_092_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("webb", "1220_92"); put("leia", "1_17"); put("r2", "2_14"); put("corvette", "1_140"); put("system", "6_87");
        }}, new HashMap<>() {{
            put("bay", "1_285"); put("deathstar", "2_143"); put("sim", "1230_18"); put("garindan", "1_177"); put("nevar", "1_261");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void JeroenWebbVStatsAndKeywordsAreCorrect() {
        var scn = GetScenario(); var physical = scn.GetLSCard("webb"); var card = physical.getBlueprint();
        assertEquals(Title.Jeroen_Webb, card.getTitle()); assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide()); assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(2, card.getDestiny(), scn.epsilon); assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon); assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon); assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(1, scn.GetLandspeed(physical));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.REBEL); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{ add(Icon.REBEL); add(Icon.PILOT); add(Icon.HOTH); add(Icon.LEGACY_BLOCK_2); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.SPY); }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet()); assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void JeroenWebbVMustDeployUndercoverForZeroForceAtOpponentsMobileSite() {
        var scn = GetScenario(); var webb = scn.GetLSCard("webb"); var bay = scn.GetDSCard("bay");
        scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("deathstar")); scn.MoveLocationToTable(bay);
        scn.MoveCardsToLSHand(webb); scn.EnsureLSForcePile(5); scn.SkipToLSTurn(Phase.DEPLOY);
        int before = scn.GetLSForcePileCount(); scn.LSDeployCard(webb); scn.LSChooseCard(bay); scn.PassAllResponses();
        assertEquals(bay, webb.getAtLocation()); assertTrue(webb.isUndercover());
        assertEquals(before, scn.GetLSForcePileCount());
        assertEquals(scn.DS, webb.getZoneOwner());
    }

    @Test
    public void JeroenWebbVDeploysNormallyForTwoAtOwnMobileSite() {
        DeployNormallyAt(false);
    }

    @Test
    public void JeroenWebbVDeploysNormallyForTwoAtOpponentsPlanetarySite() {
        DeployNormallyAt(true);
    }

    private void DeployNormallyAt(boolean opponentPlanetary) {
        var scn = GetScenario(); var webb = scn.GetLSCard("webb");
        var site = opponentPlanetary ? scn.GetDSStartingLocation() : scn.GetLSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLSHand(webb); scn.EnsureLSForcePile(5); scn.SkipToLSTurn(Phase.DEPLOY);
        int before = scn.GetLSForcePileCount(); scn.LSDeployCard(webb); scn.LSChooseCard(site); scn.PassAllResponses();
        assertEquals(site, webb.getAtLocation()); assertFalse(webb.isUndercover());
        assertEquals(before - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void JeroenWebbVReducesOpponentsInfluenceForOwnSpiesAtSitesOnly() {
        var scn = GetScenario(); var site = scn.GetDSStartingLocation(); var other = scn.GetLSStartingLocation();
        var webb = scn.GetLSCard("webb"); var leia = scn.GetLSCard("leia"); var sim = scn.GetDSCard("sim");
        scn.StartGame(); scn.MoveCardsToLocation(site, sim);
        assertEquals(3, Influence(scn, site), scn.epsilon);
        scn.MoveCardsToLocation(site, webb); assertEquals(2, Influence(scn, site), scn.epsilon);
        scn.MoveCardsToLocation(other, leia); assertEquals(1, Influence(scn, site), scn.epsilon);
        scn.MoveCardsToLSHand(leia); assertEquals(2, Influence(scn, site), scn.epsilon);
        scn.MoveLocationToTable(scn.GetLSCard("system")); scn.MoveCardsToLocation(scn.GetLSCard("system"), scn.GetLSCard("corvette"));
        scn.BoardAsPassenger(scn.GetLSCard("corvette"), scn.GetLSCard("r2"));
        assertEquals(2, Influence(scn, site), scn.epsilon);
    }

    @Test
    public void JeroenWebbVInfluenceReductionWorksWhileUndercoverAndCountsUndercoverSpies() {
        var scn = GetScenario(); var webb = scn.GetLSCard("webb"); var bay = scn.GetDSCard("bay");
        scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("deathstar")); scn.MoveLocationToTable(bay);
        scn.MoveCardsToLocation(bay, scn.GetDSCard("sim")); scn.MoveCardsToLSHand(webb);
        scn.EnsureLSForcePile(5); scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(3, Influence(scn, bay), scn.epsilon);
        scn.LSDeployCard(webb); scn.LSChooseCard(bay); scn.PassAllResponses(); assertTrue(webb.isUndercover());
        assertEquals(2, Influence(scn, bay), scn.epsilon);
    }

    @Test
    public void JeroenWebbVPreventsNevarYalnalFromTargetingSpiesAtHisSite() {
        var scn = GetScenario(); var site = scn.GetDSStartingLocation(); var nevar = scn.GetDSCard("nevar");
        scn.StartGame(); scn.MoveCardsToLocation(site, scn.GetLSCard("webb"), scn.GetLSCard("leia"), scn.GetDSCard("garindan"));
        scn.MoveCardsToDSHand(nevar); scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(nevar));
        assertFalse(Filters.canBeTargetedBy(nevar).accepts(scn.game(), scn.GetLSCard("leia")));
    }

    @Test
    public void JeroenWebbVDoesNotProtectSpiesAtOtherSitesFromNevarYalnal() {
        var scn = GetScenario(); var site = scn.GetDSStartingLocation(); var nevar = scn.GetDSCard("nevar");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("webb"));
        scn.MoveCardsToLocation(site, scn.GetLSCard("leia"), scn.GetDSCard("garindan"));
        scn.MoveCardsToDSHand(nevar); scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardPlayAvailable(nevar));
        assertTrue(Filters.canBeTargetedBy(nevar).accepts(scn.game(), scn.GetLSCard("leia")));
    }

    @Test
    public void JeroenWebbVProtectsSpiesFromNevarYalnalWhileUndercover() {
        var scn = GetScenario(); var webb = scn.GetLSCard("webb"); var bay = scn.GetDSCard("bay");
        var nevar = scn.GetDSCard("nevar");
        scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("deathstar")); scn.MoveLocationToTable(bay);
        scn.MoveCardsToLocation(bay, scn.GetDSCard("garindan")); scn.MoveCardsToLSHand(webb);
        scn.EnsureLSForcePile(5); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(webb); scn.LSChooseCard(bay); scn.PassAllResponses();
        assertTrue(webb.isUndercover());
        scn.MoveCardsToDSHand(nevar); scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(nevar));
        assertFalse(Filters.canBeTargetedBy(nevar).accepts(scn.game(), webb));
    }

    private float Influence(VirtualTableScenario scn, PhysicalCardImpl site) {
        return scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, site);
    }
}
