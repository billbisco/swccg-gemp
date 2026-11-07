package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_034_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("corvette", "1_140", "han", "1_11", "luke", "1_19")),
                new HashMap<>(Map.of("protocols", "1261_34", "destroyer", "1_302", "other", "2_155",
                        "executor", "4_167", "launch", "4_165", "unique", "4_160", "ground", "1_292",
                        "trooper", "1_194", "officer", "1_192", "launch2", "4_165")),
                20, 20, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printingUsesItsOwnTitleAndPrintedEffectMetadata() {
        var scn = scenario();
        var bp = scn.GetDSCard("protocols").getBlueprint();
        assertEquals("Fleet Security Protocols", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(CardSubtype.NORMAL, bp.getCardSubtype());
        assertEquals(PlayCardZoneOption.YOUR_SIDE_OF_TABLE, bp.getSinglePlayCardZoneOption());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
        assertTrue(bp.isImmuneToCardTitle(Title.Alter));
    }

    @Test(timeout = 10000)
    public void actualDownloadOnlyOffersNonUniqueVesselSitesAndIsOncePerTurn() {
        var scn = scenario();
        var effect = scn.GetDSCard("protocols");
        var launch = scn.GetDSCard("launch");
        var unique = scn.GetDSCard("unique");
        var ground = scn.GetDSCard("ground");
        var ship = scn.GetDSCard("destroyer");
        scn.StartGame();
        var system = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(system, ship, scn.GetDSCard("other"), scn.GetDSCard("executor"));
        scn.MoveCardsToDSHand(effect);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(10);
        scn.DSDeployCard(effect);
        scn.PassAllResponses();
        if (scn.LSGetDecision() != null)
            scn.LSPass();
        assertEquals(Zone.SIDE_OF_TABLE, effect.getZone());
        scn.MoveCardsToTopOfDSReserveDeck(launch, unique, ground, scn.GetDSCard("launch2"));
        assertTrue(scn.DSCardActionAvailable(effect, "Deploy non-unique starship or vehicle site"));
        scn.DSUseCardAction(effect, "Deploy non-unique starship or vehicle site");
        scn.PassAllResponses();
        var choices = scn.DSGetDecision().getDecisionParameters();
        var ids = choices.get("blueprintId");
        var selectable = choices.get("selectable");
        int legal = 0;
        for (int i = 0; i < ids.length; i++) {
            if ("true".equals(selectable[i])) {
                assertEquals(launch.getBlueprintId(), ids[i]);
                legal++;
            }
        }
        assertTrue("At least one legal launch bay", legal > 0);
        scn.DSChooseCard(launch);
        if (scn.DSHasCardChoiceAvailable(ship))
            scn.DSChooseCard(ship);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, launch.getZone());
        assertNotNull(launch.getRelatedStarshipOrVehicle());
        if (scn.LSGetDecision() != null)
            scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(effect, "Deploy non-unique starship or vehicle site"));
    }

    @Test(timeout = 10000)
    public void realSpaceBattleAddsTwoPerOwnCharacterToOnlyTheRelatedShip() {
        var scn = scenario();
        var effect = scn.GetDSCard("protocols");
        var launch = scn.GetDSCard("launch");
        var ship = scn.GetDSCard("destroyer");
        var other = scn.GetDSCard("other");
        var corvette = scn.GetLSCard("corvette");
        scn.StartGame();
        var system = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(system, ship, other, corvette);
        scn.BoardAsPilot(corvette, scn.GetLSCard("han"));
        var placement = scn.gameState().getLocationPlacement(scn.game(), launch, null, null).stream()
                .filter(p -> p.getParentStarshipOrVehicleCard() == ship).findFirst().orElseThrow();
        scn.MoveLocationToTable(launch, placement);
        assertSame(ship, launch.getRelatedStarshipOrVehicle());
        scn.MoveCardsToLocation(launch, scn.GetDSCard("trooper"), scn.GetDSCard("officer"),
                scn.GetLSCard("luke"));
        scn.MoveCardsToDSSideOfTable(effect);
        var shipPower = scn.GetPower(ship);
        var otherPower = scn.GetPower(other);
        scn.DSStartBattleAndSkipToWeaponsSegment(system);
        assertTrue(scn.IsActiveBattle());
        assertEquals(shipPower + 4, scn.GetPower(ship), scn.epsilon);
        assertEquals(otherPower, scn.GetPower(other), scn.epsilon);
    }
}
