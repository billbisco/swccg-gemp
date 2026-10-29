package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ReflectionsGoldChangedEffectsTests {
    @Test
    public void CrossfireDoesNotSuspendManeuveringFlapsForHighManeuverVehicles() {
        checkFlapsAgainstPilotedBike("15_177", false);
    }

    @Test
    public void OriginalCrossfireSuspendsManeuveringFlapsForTheSamePilotedBike() {
        checkFlapsAgainstPilotedBike("8_121", true);
    }

    private void checkFlapsAgainstPilotedBike(String crossfireId, boolean suspended) {
        var light = new HashMap<String, String>();
        light.put("flaps", "7_69");
        light.put("speeder", "3_69");
        var dark = new HashMap<String, String>();
        dark.put("crossfire", crossfireId);
        dark.put("pilot", "1_168");
        dark.put("bike", "8_169");
        var scn = new VirtualTableScenario(light, dark);
        scn.StartGame();
        var flaps = scn.GetLSCard("flaps");
        var speeder = scn.GetLSCard("speeder");
        var bike = scn.GetDSCard("bike");
        scn.MoveCardsToLSSideOfTable(flaps);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("crossfire"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), speeder, bike);
        scn.BoardAsPilot(bike, scn.GetDSCard("pilot"));
        assertTrue(scn.game().getModifiersQuerying().getManeuver(scn.gameState(), bike) > 3);
        scn.EnsureLSForcePile(3);
        scn.SkipToLSTurn(Phase.CONTROL);
        int before = scn.GetPower(speeder);
        assertTrue(scn.LSCardActionAvailable(flaps, "Engage Maneuvering Flaps"));
        scn.LSUseCardAction(flaps, "Engage Maneuvering Flaps");
        scn.PassAllResponses();
        assertEquals(before + (suspended ? 0 : 2), scn.GetPower(speeder));
        assertEquals(suspended, scn.game().getModifiersQuerying().isEffectsFromModifierToCardSuspended(
                scn.gameState(), flaps, speeder));
    }

    private VirtualTableScenario flagshipScenario(int sites) {
        var dark = new HashMap<String, String>();
        dark.put("flagship", "15_182");
        dark.put("executor", "4_167");
        dark.put("tie", "9_175");
        dark.put("system", "1_282");
        for (int i = 0; i < 4; i++) dark.put("site" + i, "4_" + (159 + i));
        var scn = new VirtualTableScenario(new HashMap<>(), dark);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetDSCard("executor"));
        for (int i = 0; i < sites; i++) scn.MoveLocationToTable(scn.GetDSCard("site" + i));
        scn.MoveCardsToDSHand(scn.GetDSCard("flagship"), scn.GetDSCard("tie"));
        scn.EnsureDSForcePile(6);
        scn.SkipToDSTurn(Phase.DEPLOY);
        return scn;
    }

    @Test
    public void FlagshipOperationsCannotDeployWithOnlyThreeExecutorSites() {
        var scn = flagshipScenario(3);
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("tie"), "Deploy"));
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("flagship"), "Deploy"));
    }

    @Test
    public void FlagshipOperationsDeploysWithFourSitesAndAddsOnlyOneToTIEDestiny() {
        var scn = flagshipScenario(4);
        var flagship = scn.GetDSCard("flagship");
        var tie = scn.GetDSCard("tie");
        assertTrue(scn.DSCardPlayAvailable(flagship, "Deploy"));
        scn.DSPlayCardAndPassResponses(flagship, scn.GetDSCard("executor"));
        assertEquals(scn.GetDSCard("executor"), flagship.getAttachedTo());
        assertEquals(3, scn.GetDestiny(tie));
        assertEquals(5, scn.GetForfeit(tie));
        assertEquals(1, scn.GetDeployCost(tie));
        assertFalse(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), flagship, Title.Alter));
    }
}
