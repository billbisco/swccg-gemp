package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static org.junit.Assert.*;

/** Nothing Can Get Through Our Shield (V) original version of 1250_1. */
public class Card_2250_001_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2250_1", "current", "1250_1", "system", "1_289", "capital", "1_301", "trooper", "1_194")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1250_1", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21),
                "At each system a player controls with a capital starship, non-[Presence] characters (and vehicles) deploy -1 to related sites and sectors. If you occupy a system with a capital starship (or three unique starships) and have no Objective on table, may add 1 to each of your Force drains at related battleground sites (opponent loses no more than 2 Force from those drains).");
    }

    private float deployCost(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl location) {
        return scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), card, card,
                location, false, null, false, 0, null, true);
    }

    @Test
    public void OriginalDiscountsBothPlayersCharactersAtTheControlledSystemsSites() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("rebel", "1_28")),
                new HashMap<>(Map.of("v", "2250_1", "system", "1_289", "capital", "1_301", "trooper", "1_194", "presence", "14_69")),
                28, 28, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToSideOfTable(scn.GetDSCard("v"));
        var site = scn.GetDSStartingLocation();
        var rebel = scn.GetLSCard("rebel");
        var trooper = scn.GetDSCard("trooper");
        var presence = scn.GetDSCard("presence");
        float rebelBase = deployCost(scn, rebel, site);
        float trooperBase = deployCost(scn, trooper, site);
        float presenceBase = deployCost(scn, presence, site);
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetDSCard("capital"));
        assertEquals(trooperBase - 1, deployCost(scn, trooper, site), scn.epsilon);
        // "non-[Presence] characters": opponent's characters too (PC-E1 "his" limits it to the controller's).
        assertEquals(rebelBase - 1, deployCost(scn, rebel, site), scn.epsilon);
        assertEquals(presenceBase, deployCost(scn, presence, site), scn.epsilon);
    }
}
