package com.gempukku.swccgo.rules.deploy;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * swccg-guild/work#178 (Bill 2026-11-05): locations have no deploy cost. They are free, not "deploy 0", so a deploy
 * cost comparison never matches a location, and a location still deploys for free.
 */
public class LocationsHaveNoDeployCostTests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(), new HashMap<>() {{
                    put("site", "12_176");     // Tatooine: Marketplace
                    put("system", "4_154");    // Anoat
                    put("trooper", "1_194");   // Stormtrooper (deploy 1)
                }}, 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                VirtualTableScenario.Open);
    }

    @Test
    public void LocationsHaveNoDeployCost() {
        var scn = scenario();
        assertNull(scn.GetDSCard("site").getBlueprint().getDeployCost());
        assertNull(scn.GetDSCard("system").getBlueprint().getDeployCost());
        assertEquals(Float.valueOf(1), scn.GetDSCard("trooper").getBlueprint().getDeployCost());
    }

    @Test
    public void ADeployCostComparisonNeverMatchesALocation() {
        var scn = scenario();
        var q = scn.game().getModifiersQuerying();
        PhysicalCardImpl site = scn.GetDSCard("site");
        assertFalse(q.hasDeployCostLessThanOrEqualTo(scn.gameState(), site, 100));
        assertFalse(Filters.deployCostLessThanOrEqualTo(100).accepts(scn.game(), site));
        assertFalse(Filters.printedDeployCostLessThanOrEqualTo(100).accepts(scn.game(), site));
        assertFalse(Filters.printedDeployCostMoreThanOrEqualTo(0).accepts(scn.game(), site));
        assertTrue("a deploy 1 card still compares", q.hasDeployCostLessThanOrEqualTo(scn.gameState(), scn.GetDSCard("trooper"), 1));
    }

    @Test
    public void LocationsDeployForFree() {
        var scn = scenario();
        PhysicalCardImpl site = scn.GetDSCard("site");
        assertTrue(scn.game().getModifiersQuerying().deploysForFree(scn.gameState(), site));
        assertFalse(scn.game().getModifiersQuerying().deploysForFree(scn.gameState(), scn.GetDSCard("trooper")));

        // (the site is also the default starting location's title, so deploy the system)
        PhysicalCardImpl system = scn.GetDSCard("system");
        scn.MoveCardsToHand(system);
        scn.StartGame();
        scn.SkipToDSTurn(Phase.DEPLOY);
        int force = scn.gameState().getForcePile(scn.DS).size();
        assertTrue(scn.DSCardPlayAvailable(system));
        scn.DSPlayCard(system);
        scn.PassAllResponses();
        assertTrue(system.getZone().isInPlay());
        assertEquals("no Force used", force, scn.gameState().getForcePile(scn.DS).size());
    }
}
