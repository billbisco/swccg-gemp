package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static org.junit.Assert.*;

/** Let's Go Left (V) original version of 1250_33. */
public class Card_2250_033_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2250_33", "current", "1250_33")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1250_33", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21),
                "At each system a player controls with a capital starship, non-[Presence] characters (and vehicles) deploy -1 to related sites and sectors. If you occupy a system with a capital starship (or three unique starships) and have no Objective on table, may add 1 to each of your Force drains at related battleground sites (opponent loses no more than 2 Force from those drains).");
    }

    @Test
    public void OriginalDiscountsBothPlayersCharactersAtTheControlledSystemsSites() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("v", "2250_33", "endor", "8_68", "site", "8_69", "capital", "2_73", "luke", "1_19")),
                new HashMap<>(Map.of("trooper", "1_194", "droid", "14_73")),
                35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("endor"));
        scn.MoveLocationToTable(scn.GetLSCard("site"));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("v"));
        var q = scn.game().getModifiersQuerying();
        var site = scn.GetLSCard("site");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var droid = scn.GetDSCard("droid");
        float lukeBase = q.getDeployCost(scn.gameState(), luke, luke, site, false, null, false, 0, null, true);
        float trooperBase = q.getDeployCost(scn.gameState(), trooper, trooper, site, false, null, false, 0, null, true);
        float droidBase = q.getDeployCost(scn.gameState(), droid, droid, site, false, null, false, 0, null, true);
        scn.MoveCardsToLocation(scn.GetLSCard("endor"), scn.GetLSCard("capital"));
        assertEquals(lukeBase - 1, q.getDeployCost(scn.gameState(), luke, luke, site, false, null, false, 0, null, true), scn.epsilon);
        // No "their": opponent's non-[Presence] characters are discounted too.
        assertEquals(trooperBase - 1, q.getDeployCost(scn.gameState(), trooper, trooper, site, false, null, false, 0, null, true), scn.epsilon);
        assertEquals(droidBase, q.getDeployCost(scn.gameState(), droid, droid, site, false, null, false, 0, null, true), scn.epsilon);
    }
}
