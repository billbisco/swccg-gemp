package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_035_Tests {
    private static final String DOWNLOAD = "Deploy battleground system from Reserve Deck";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19", "falcon", "1_143",
                "corvette", "1_140", "alter", "1_71")),
                new HashMap<>(Map.ofEntries(
                        Map.entry("facility", "1240_35"), Map.entry("avenger", "4_172"),
                        Map.entry("scythe", "9_171"), Map.entry("fighter", "1_304"), Map.entry("custom", "1_306"),
                        Map.entry("squadron", "2_153"), Map.entry("devastator", "1_301"), Map.entry("punishing", "4_171"),
                        Map.entry("vader", "1_168"), Map.entry("system", "1_289"), Map.entry("away", "1_288"),
                        Map.entry("site", "1_293"), Map.entry("watch", "1210_70"), Map.entry("blastDoors", "1_210"),
                        Map.entry("shortRange", "12_158"))),
                15, 15, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void TieFighterConstructionFacilityHasPrintedStatsIconsAndKeywords() {
        var scn = scenario();
        var card = scn.GetDSCard("facility").getBlueprint();
        assertEquals("TIE Fighter Construction Facility", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertTrue(card.getCardTypes().contains(CardType.EFFECT));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.EFFECT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
    }

    @Test
    public void TieFighterConstructionFacilityDeploysOnTableAndResetsOnlyPrintedTieGroups() {
        var scn = scenario();
        var facility = scn.GetDSCard("facility");
        var system = scn.GetDSCard("system");
        var avenger = scn.GetDSCard("avenger");
        var scythe = scn.GetDSCard("scythe");
        var fighter = scn.GetDSCard("fighter");
        var custom = scn.GetDSCard("custom");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveCardsToDSHand(facility, avenger, scythe, fighter, custom);
        scn.DSActivateForceCheat(7);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertEquals(3, scn.GetDeployCost(avenger));
        assertEquals(4, scn.GetDeployCost(scythe));
        scn.DSPlayCardAndPassResponses(facility);
        assertInZone(Zone.SIDE_OF_TABLE, facility);
        deployForTwo(scn, avenger, system);
        deployForTwo(scn, scythe, system);
        assertEquals(4, scn.GetForfeit(avenger));
        assertEquals(4, scn.GetForfeit(scythe));
        assertEquals(1, scn.GetDeployCost(fighter));
        assertEquals(2, scn.GetForfeit(fighter));
        assertEquals(2, scn.GetDeployCost(custom));
        assertEquals(4, scn.GetForfeit(custom));

        var mods = scn.game().getModifiersQuerying();
        assertEquals(4, mods.getImmunityToAttritionLessThan(scn.gameState(), avenger), scn.epsilon);
        assertEquals(4, mods.getImmunityToAttritionLessThan(scn.gameState(), scythe), scn.epsilon);
        assertEquals(0, mods.getImmunityToAttritionLessThan(scn.gameState(), custom), scn.epsilon);
    }

    private static void deployForTwo(VirtualTableScenario scn, PhysicalCardImpl ship, PhysicalCardImpl system) {
        int force = scn.GetDSForcePileCount();
        assertEquals(2, scn.GetDeployCost(ship));
        scn.DSDeployCard(ship);
        scn.DSChooseCard(system);
        scn.PassAllResponses();
        assertAtLocation(system, ship);
        assertEquals(force - 2, scn.GetDSForcePileCount());
    }

    @Test
    public void TieFighterConstructionFacilityProtectsScytheAtThreeButNotFourAttrition() {
        verifyScytheProtection(3, false);
        verifyScytheProtection(4, true);
    }

    private void verifyScytheProtection(int attrition, boolean canForfeit) {
        var scn = scenario();
        var system = scn.GetDSCard("system");
        var scythe = scn.GetDSCard("scythe");
        var fighter = scn.GetDSCard("fighter");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("facility"));
        scn.MoveCardsToLocation(system, scythe, fighter, scn.GetLSCard("falcon"));
        attachPilot(scn, scn.GetLSCard("luke"), scn.GetLSCard("falcon"));
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(system);
        scn.PrepareLSDestiny(attrition);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(attrition, scn.GetLSTotalDestiny());
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        if (scn.AwaitingDSBattleDamagePayment()) {
            scn.DSPayRemainingBattleDamageFromReserveDeck();
        }
        assertEquals(attrition, scn.GetUnpaidDSAttrition());
        assertTrue(scn.DSHasCardChoiceAvailable(fighter));
        assertEquals(canForfeit, scn.DSHasCardChoiceAvailable(scythe));
    }

    @Test
    public void TieFighterConstructionFacilityAddsThreeAttritionOnlyAgainstOpponentWithThePrintedPair() {
        verifyAttrition(true, Pair.PRESENT, 5);
        verifyAttrition(false, Pair.PRESENT, 2);
    }

    @Test
    public void TieFighterConstructionFacilityDoesNotCountSquadronRemoteOrAboardTies() {
        verifyAttrition(true, Pair.SQUADRON, 2);
        verifyAttrition(true, Pair.REMOTE, 2);
        verifyAttrition(true, Pair.ABOARD, 2);
    }

    @Test
    public void TieFighterConstructionFacilityDoesNotCountOpponentsCapitalShip() {
        verifyAttrition(true, Pair.ENEMY_CAPITAL, 2);
    }

    private enum Pair { PRESENT, SQUADRON, REMOTE, ABOARD, ENEMY_CAPITAL }

    private void verifyAttrition(boolean facilityOnTable, Pair pair, int expected) {
        var scn = scenario();
        var system = scn.GetDSCard("system");
        var away = scn.GetDSCard("away");
        var capital = scn.GetDSCard("devastator");
        var fighter = scn.GetDSCard("fighter");
        var punishing = scn.GetDSCard("punishing");
        var falcon = scn.GetLSCard("falcon");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveLocationToTable(away);
        if (facilityOnTable) {
            scn.MoveCardsToDSSideOfTable(scn.GetDSCard("facility"));
        }
        scn.MoveCardsToLocation(system, punishing, falcon);
        attachPilot(scn, scn.GetDSCard("vader"), punishing);
        attachPilot(scn, scn.GetLSCard("luke"), falcon);
        if (pair == Pair.ENEMY_CAPITAL) {
            scn.MoveCardsToLocation(system, scn.GetLSCard("corvette"), fighter);
        } else {
            scn.MoveCardsToLocation(system, capital);
            switch (pair) {
                case PRESENT -> scn.MoveCardsToLocation(system, fighter, scn.GetDSCard("scythe"));
                case SQUADRON -> scn.MoveCardsToLocation(system, scn.GetDSCard("squadron"));
                case REMOTE -> scn.MoveCardsToLocation(away, fighter);
                case ABOARD -> {
                    scn.RemoveCardZone(fighter);
                    scn.gameState().attachCardInStarfighterOrTIECapacitySlot(fighter, capital);
                }
                default -> throw new AssertionError(pair);
            }
        }
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(system);
        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(1);
        scn.SkipToEndOfPowerSegment(true);

        assertEquals(2, scn.GetDSTotalDestiny());
        assertEquals(1, scn.GetLSTotalDestiny());
        var battle = scn.gameState().getBattleState();
        assertEquals(expected, battle.getAttritionTotal(scn.game(), VirtualTableScenario.LS), scn.epsilon);
        assertEquals(1, battle.getAttritionTotal(scn.game(), VirtualTableScenario.DS), scn.epsilon);
    }

    private static void attachPilot(VirtualTableScenario scn, PhysicalCardImpl pilot, PhysicalCardImpl ship) {
        scn.RemoveCardZone(pilot);
        scn.gameState().attachCardInPilotCapacitySlot(pilot, ship);
    }

    @Test
    public void TieFighterConstructionFacilityIsImmuneToOriginalAlter() {
        var scn = scenario();
        var facility = scn.GetDSCard("facility");
        var normal = scn.GetDSCard("blastDoors");
        var alter = scn.GetLSCard("alter");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(facility, normal);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        scn.MoveCardsToLSHand(alter);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(1);
        scn.LSUseCardAction(alter, "Draw destiny to cancel Effect");
        assertTrue(scn.LSHasCardChoiceAvailable(normal));
        assertFalse(scn.LSHasCardChoiceAvailable(facility));
        scn.LSChooseCard(normal);
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke"))) {
            scn.LSChooseCard(scn.GetLSCard("luke"));
        }
        scn.PrepareLSDestiny(1);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, normal);
        assertInZone(Zone.SIDE_OF_TABLE, facility);
    }

    @Test
    public void TieFighterConstructionFacilityPreservesFinalWatchYourBackSystemDownload() {
        verifyWatchYourBack(false);
        verifyWatchYourBack(true);
    }

    private void verifyWatchYourBack(boolean facilityOnTable) {
        var scn = scenario();
        var watch = scn.GetDSCard("watch");
        var system = scn.GetDSCard("system");
        var site = scn.GetDSCard("site");
        scn.StartGame();
        if (facilityOnTable) {
            scn.MoveCardsToDSSideOfTable(scn.GetDSCard("facility"));
        }
        scn.MoveCardsToDSHand(watch);
        scn.MoveCardsToTopOfDSReserveDeck(system, site);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(watch, DOWNLOAD));
        scn.DSUseCardAction(watch, DOWNLOAD);
        scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoiceAvailable(system));
        assertFalse(scn.DSHasCardChoiceAvailable(site));
        scn.DSChooseCard(system);
        scn.PassAllResponses();
        assertInZone(Zone.LOCATIONS, system);
        assertTrue(scn.GetDSUsedPile().contains(watch));
    }

    @Test
    public void WithoutFacilityCoruscantWatchYourBackTakesOnlyUniqueStarfighters() {
        verifyCoruscantWatchYourBack(false);
    }

    @Test
    public void FacilityMakesCoruscantWatchYourBackIgnoreUnique() {
        verifyCoruscantWatchYourBack(true);
    }

    private void verifyCoruscantWatchYourBack(boolean facilityOnTable) {
        var scn = scenario();
        var shortRange = scn.GetDSCard("shortRange");
        var scythe = scn.GetDSCard("scythe");
        var fighter = scn.GetDSCard("fighter");
        var custom = scn.GetDSCard("custom");
        scn.StartGame();
        if (facilityOnTable) {
            scn.MoveCardsToDSSideOfTable(scn.GetDSCard("facility"));
        }
        scn.MoveCardsToDSHand(shortRange);
        scn.MoveCardsToTopOfDSReserveDeck(scythe, fighter, custom);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(shortRange, "Take card into hand"));
        scn.DSPlayCard(shortRange, "Take card into hand");
        scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoiceAvailable(custom));
        assertEquals(facilityOnTable, scn.DSHasCardChoiceAvailable(scythe));
        assertEquals(facilityOnTable, scn.DSHasCardChoiceAvailable(fighter));
        PhysicalCardImpl pick = facilityOnTable ? scythe : custom;
        scn.DSChooseCard(pick);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, pick);
    }
}
