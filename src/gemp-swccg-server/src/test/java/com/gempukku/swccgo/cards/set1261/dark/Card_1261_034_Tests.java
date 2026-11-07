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
        var launch2 = scn.GetDSCard("launch2");
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
        scn.MoveCardsToTopOfDSReserveDeck(launch, unique, ground, launch2);
        assertTrue(scn.DSCardActionAvailable(effect, "Deploy non-unique starship or vehicle site"));
        scn.DSUseCardAction(effect, "Deploy non-unique starship or vehicle site");
        scn.PassAllResponses();
        var choices = scn.DSGetDecision().getDecisionParameters();
        var ids = choices.get("blueprintId");
        var selectable = choices.get("selectable");
        int legal = 0;
        for (int i = 0; i < ids.length; i++) {
            if ("true".equals(selectable[i])) {
                assertEquals(launch.getBlueprintId(true), ids[i]);
                legal++;
            }
        }
        assertTrue("At least one legal launch bay", legal > 0);
        scn.DSChooseCard(launch);
        for (int i = 0; i < 16 && launch.getZone() != Zone.LOCATIONS && launch2.getZone() != Zone.LOCATIONS; i++) {
            var decision = scn.GetCurrentDecision();
            String prompt = decision.getText().toLowerCase();
            if (prompt.contains("optional response")) {
                if (scn.DSGetDecision() != null)
                    scn.DSPass();
                else
                    scn.LSPass();
            } else if (scn.DSHasCardChoiceAvailable(ship)) {
                scn.DSChooseCard(ship);
            } else if (scn.DSGetDecision() != null && prompt.contains("on which side")) {
                scn.ChooseOption(scn.DS, scn.DSGetMultipleChoices().getFirst());
            } else {
                fail("Unresolved site placement: " + decision.getText()
                        + " " + decision.getDecisionParameters().keySet());
            }
        }
        assertTrue("Exactly one of the identical copies was downloaded",
                (launch.getZone() == Zone.LOCATIONS) != (launch2.getZone() == Zone.LOCATIONS));
        var deployed = launch.getZone() == Zone.LOCATIONS ? launch : launch2;
        scn.PassAllResponses();
        assertNotNull(deployed.getRelatedStarshipOrVehicle());
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

    @Test(timeout = 10000)
    public void actualVehicleBattleUsesCharactersAtTheRelatedVehicleSite() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19")),
                new HashMap<>(Map.of("protocols", "1261_34", "walker", "3_157",
                        "platform", "1220_67", "trooper", "1_194")),
                18, 18, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        var ground = scn.GetDSStartingLocation();
        var walker = scn.GetDSCard("walker");
        var platform = scn.GetDSCard("platform");
        scn.MoveCardsToLocation(ground, walker, scn.GetLSCard("luke"));
        var placement = scn.gameState().getLocationPlacement(scn.game(), platform, null, null).stream()
                .filter(p -> p.getParentStarshipOrVehicleCard() == walker).findFirst().orElseThrow();
        scn.MoveLocationToTable(platform, placement);
        assertSame(walker, platform.getRelatedStarshipOrVehicle());
        scn.MoveCardsToLocation(platform, scn.GetDSCard("trooper"));
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("protocols"));
        var unmodified = scn.GetPower(walker);
        scn.DSStartBattleAndSkipToWeaponsSegment(ground);
        assertTrue(scn.IsActiveBattle());
        assertEquals(unmodified + 2, scn.GetPower(walker), scn.epsilon);
    }

}
