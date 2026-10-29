package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.*;

public class ReflectionsGoldBlankBehaviorTests {
    private VirtualTableScenario scenario(String lightId, String darkId) {
        var light = new HashMap<String, String>();
        light.put("card", lightId);
        var dark = new HashMap<String, String>();
        dark.put("card", darkId);
        return new VirtualTableScenario(light, dark);
    }

    @Test
    public void AnakinSkywalkerDeploysForPrintedCostWithoutInventedGameText() {
        var scn = scenario("15_2", "15_261");
        var anakin = scn.GetLSCard("card");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLSHand(anakin);
        scn.EnsureLSForcePile(6);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCardAndPassResponses(anakin, site);
        assertAtLocation(site, anakin);
        assertEquals(force - 3, scn.GetLSForcePileCount());
        assertEquals(1, scn.GetPower(anakin));
    }

    @Test
    public void AckbarsAssaultPlanAndWorryDoNotInventADeploymentDestination() {
        var scn = scenario("15_21", "15_217");
        var light = scn.GetLSCard("card");
        var dark = scn.GetDSCard("card");
        scn.StartGame();
        scn.MoveCardsToLSHand(light);
        scn.MoveCardsToDSHand(dark);
        scn.EnsureLSForcePile(6);
        scn.EnsureDSForcePile(6);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(light));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(dark));
        assertEquals(Zone.HAND, light.getZone());
        assertEquals(Zone.HAND, dark.getZone());
    }

    @Test
    public void LockedDoorHasNoInventedInterruptAction() {
        var scn = scenario("15_2", "15_261");
        var interrupt = scn.GetDSCard("card");
        scn.StartGame();
        scn.MoveCardsToDSHand(interrupt);
        scn.EnsureDSForcePile(6);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(interrupt));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(interrupt));
        assertEquals(Zone.HAND, interrupt.getZone());
    }

    @Test
    public void OwenCannotDeployDuplicateButCanPersonaReplaceOwenLars() {
        var light = new HashMap<String, String>();
        light.put("owen", "15_10");
        light.put("owenLars", "1_22");
        light.put("luke", "1_19");
        var scn = new VirtualTableScenario(light, new HashMap<>());
        var owen = scn.GetLSCard("owen");
        var owenLars = scn.GetLSCard("owenLars");
        scn.StartGame();
        scn.MoveCardsToLSHand(owen, scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), owenLars);
        scn.EnsureLSForcePile(6);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertNotNull(scn.LSGetDecision());
        assertTrue("A separate legal deploy action proves this is an active deploy decision",
                scn.LSCardPlayAvailable(scn.GetLSCard("luke")));
        assertFalse(scn.LSCardPlayAvailable(owen, "Deploy"));
        assertTrue(scn.LSCardActionAvailable(owen, "Persona replace"));
        scn.LSUseCardAction(owen, "Persona replace");
        if (scn.LSHasCardChoiceAvailable(owenLars)) {
            scn.LSChooseCard(owenLars);
        }
        scn.PassAllResponses();
        assertAtLocation(scn.GetLSStartingLocation(), owen);
        assertTrue(scn.GetLSLostPile().contains(owenLars));
        assertEquals(1, Filters.filterActive(scn.game(), null, Filters.Owen).size());
    }
}
