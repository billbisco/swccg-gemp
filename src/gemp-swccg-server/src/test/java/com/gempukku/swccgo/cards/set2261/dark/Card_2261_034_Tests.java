package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/** Pre-errata Fleet Security Protocols (Sep 2009 sheet), a version of 1261_34: counts every character at related sites. */
public class Card_2261_034_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("corvette", "1_140", "han", "1_11", "luke", "1_19")),
                new HashMap<>(Map.of("protocols", "2261_34", "current", "1261_34", "e1", "2261_1034", "destroyer", "1_302", "other", "2_155",
                        "launch", "4_165", "trooper", "1_194", "officer", "1_192")),
                20, 20, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FleetSecurityProtocolsOriginalIdentity() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("protocols").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_34", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2010, 4, 20),
                "Deploy on table. During battle involving your starship (or vehicle), it is power +2 for each character at its related sites (those characters may be forfeited to reduce battle damage and attrition). Once per turn, may ▼ a non-unique starship (or vehicle) site (if such sites leave table, place them and all your cards there in Used Pile). (Immune to Alter.)");
    }

    private float shipPowerBonusInBattle(String effectName) {
        var scn = scenario();
        var launch = scn.GetDSCard("launch");
        var ship = scn.GetDSCard("destroyer");
        var corvette = scn.GetLSCard("corvette");
        scn.StartGame();
        var system = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(system, ship, scn.GetDSCard("other"), corvette);
        scn.BoardAsPilot(corvette, scn.GetLSCard("han"));
        var placement = scn.gameState().getLocationPlacement(scn.game(), launch, null, null).stream()
                .filter(p -> p.getParentStarshipOrVehicleCard() == ship).findFirst().orElseThrow();
        scn.MoveLocationToTable(launch, placement);
        assertSame(ship, launch.getRelatedStarshipOrVehicle());
        scn.MoveCardsToLocation(launch, scn.GetDSCard("trooper"), scn.GetDSCard("officer"), scn.GetLSCard("luke"));
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard(effectName));
        var before = scn.GetPower(ship);
        scn.DSStartBattleAndSkipToWeaponsSegment(system);
        return scn.GetPower(ship) - before;
    }

    @Test
    public void FleetSecurityProtocolsOriginalCountsOpponentsCharactersAtRelatedSitesToo() {
        assertEquals(6, shipPowerBonusInBattle("protocols"), 0.001f);
        // PC-E1 (2261_1034, revised 2010 sheet) and the current card (Master List 2013, PC-E4): "for each of your
        // characters"; the card list (PC-E2, 2261_2034) adds to total power instead of the starship's power.
        assertEquals(4, shipPowerBonusInBattle("e1"), 0.001f);
        assertEquals(4, shipPowerBonusInBattle("current"), 0.001f);
    }
}
