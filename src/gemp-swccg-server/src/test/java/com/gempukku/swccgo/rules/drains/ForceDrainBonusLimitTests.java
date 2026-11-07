package com.gempukku.swccgo.rules.drains;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceDrainBonusesModifier;
import com.gempukku.swccgo.logic.modifiers.ResetForceDrainModifier;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ForceDrainBonusLimitTests {
    private VirtualTableScenario setup() {
        var scn = new VirtualTableScenario(new HashMap<>() {{ put("yavin", "1_135"); }},
                new HashMap<>() {{ put("tatooine", "12_175"); put("tie", "1_304"); }},
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("yavin"));
        scn.MoveLocationToTable(scn.GetDSCard("tatooine"));
        return scn;
    }

    private float drain(VirtualTableScenario scn, PhysicalCard location, String player) {
        return scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), location, player);
    }

    private void adjust(VirtualTableScenario scn, PhysicalCard location, int amount, String player) {
        scn.ApplyAdHocModifier(new ForceDrainModifier(scn.GetDSStartingLocation(), location, amount, player));
    }

    private void limitOpposingSystems(VirtualTableScenario scn, PhysicalCard source, int max) {
        scn.ApplyAdHocModifier(new LimitForceDrainBonusesModifier(source,
                Filters.and(Filters.system, Filters.opponents(scn.LS)), max, scn.DS));
    }

    @Test
    public void RealDrainKeepsItsIconsAndCapsTheCombinedPositiveBonuses() {
        var scn = setup();
        var system = scn.GetDSCard("tatooine");
        assertEquals(1, drain(scn, system, scn.DS), 0);
        adjust(scn, system, 2, scn.DS);
        scn.ApplyAdHocModifier(new ForceDrainModifier(system, system, 2, scn.DS));
        assertEquals(5, drain(scn, system, scn.DS), 0);
        limitOpposingSystems(scn, scn.GetDSStartingLocation(), 2);
        limitOpposingSystems(scn, scn.GetLSStartingLocation(), 1);
        scn.MoveCardsToLocation(system, scn.GetDSCard("tie"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(system));
        scn.DSForceDrainAt(system);
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void NegativeModifiersStillApplyAfterCappingBonuses() {
        var scn = setup();
        var system = scn.GetDSCard("tatooine");
        adjust(scn, system, 4, scn.DS);
        scn.ApplyAdHocModifier(new ForceDrainModifier(system, system, -1, scn.DS));
        limitOpposingSystems(scn, scn.GetLSStartingLocation(), 1);
        assertEquals(1, drain(scn, system, scn.DS), 0);
    }

    @Test
    public void CapDoesNotAffectTheOtherPlayerExcludedSystemsOrSites() {
        var scn = setup();
        var yavin = scn.GetLSCard("yavin");
        var tatooine = scn.GetDSCard("tatooine");
        var site = scn.GetDSStartingLocation();
        float ownSystem = drain(scn, yavin, scn.DS);
        float light = drain(scn, tatooine, scn.LS);
        float siteDrain = drain(scn, site, scn.DS);
        adjust(scn, yavin, 3, scn.DS);
        adjust(scn, tatooine, 3, scn.LS);
        adjust(scn, site, 3, scn.DS);
        limitOpposingSystems(scn, scn.GetLSStartingLocation(), 1);
        assertEquals(ownSystem + 3, drain(scn, yavin, scn.DS), 0);
        assertEquals(light + 3, drain(scn, tatooine, scn.LS), 0);
        assertEquals(siteDrain + 3, drain(scn, site, scn.DS), 0);
    }

    @Test
    public void FixedDrainAmountStillOverridesIconsAndBonuses() {
        var scn = setup();
        var system = scn.GetDSCard("tatooine");
        adjust(scn, system, 4, scn.DS);
        limitOpposingSystems(scn, scn.GetLSStartingLocation(), 1);
        scn.ApplyAdHocModifier(new ResetForceDrainModifier(scn.GetLSStartingLocation(), system, 4));
        assertEquals(4, drain(scn, system, scn.DS), 0);
    }
}
