package com.gempukku.swccgo.cards.set1262.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1262_021_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("gundark", "1262_21"); put("trooper", "1_28"); put("luke", "1_19");
            put("swamp", "4_87"); put("endor", "8_73"); put("hut", "8_71");
        }}, new HashMap<>() {{
            put("vader", "1_168"); put("trooper1", "1_194"); put("trooper2", "1_194");
            put("forest", "8_158");
        }}, 50, 50, StartingSetup.LSStartingLocation("1_137"), StartingSetup.DSStartingLocation("12_176"),
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void GundarkStatsMatchFinalMaster() {
        var scn = scenario(); var b = scn.GetLSCard("gundark").getBlueprint();
        assertEquals("Gundark", b.getTitle()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness()); assertFalse(b.hasVirtualSuffix());
        assertEquals(3, b.getDestiny(), scn.epsilon); assertEquals(4, b.getDeployCost(), scn.epsilon);
        assertNull(b.getFerocity()); assertEquals(0, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.CREATURE)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.CREATURE, Icon.DAGOBAH, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
        scn.BlueprintModelTypeCheck(b, new ArrayList<>());
        assertEquals(4, b.getSpecialDefenseValue(), scn.epsilon);
    }

    private void deploy(String siteName, boolean darkSite, boolean allowed) {
        var scn = scenario(); scn.StartGame();
        var site = siteName == null ? scn.GetLSStartingLocation()
                : darkSite ? scn.GetDSCard(siteName) : scn.GetLSCard(siteName);
        if (siteName != null) scn.MoveLocationToTable(site);
        if (!"forest".equals(siteName)) scn.MoveLocationToTable(scn.GetDSCard("forest"));
        scn.MoveCardsToLSHand(scn.GetLSCard("gundark"));
        scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(10);
        scn.LSDeployCard(scn.GetLSCard("gundark"));
        assertEquals(allowed, scn.LSHasCardChoiceAvailable(site));
        if (allowed) {
            int before = scn.GetLSForcePileCount(); scn.LSChooseCard(site); scn.PassAllResponses();
            assertAtLocation(site, scn.GetLSCard("gundark"));
            assertEquals(4, before - scn.GetLSForcePileCount());
        }
    }
    @Test public void GundarkDeploysForFourToJungle() { deploy(null, false, true); }
    @Test public void GundarkDeploysToForest() { deploy("forest", true, true); }
    @Test public void GundarkDeploysToSwamp() { deploy("swamp", false, true); }
    @Test public void GundarkDeploysToExteriorEndorWithoutHabitatKeyword() { deploy("endor", false, true); }
    @Test public void GundarkDoesNotDeployToInteriorEndorHut() { deploy("hut", false, false); }

    @Test public void GundarkDoesNotDeployToOrdinaryExteriorDesert() {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("swamp"));
        scn.MoveCardsToLSHand(scn.GetLSCard("gundark"));
        scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(10); scn.LSDeployCard(scn.GetLSCard("gundark"));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetDSStartingLocation()));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation()));
    }

    @Test public void GundarkFerocityUsesThreePlusExactlyOneDestiny() {
        var scn = scenario(); scn.StartGame(); var g = scn.GetLSCard("gundark");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), g);
        assertEquals(1, scn.game().getModifiersQuerying().getNumFerocityDestiny(scn.gameState(), g));
        assertEquals(3, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), g, 0f), scn.epsilon);
        assertEquals(9, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), g, 6f), scn.epsilon);
    }

    private VirtualTableScenario battle(boolean here, boolean luke, boolean extraEnemies) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSCard("forest"); scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, scn.GetLSCard(luke ? "luke" : "trooper"), scn.GetDSCard("vader"));
        if (extraEnemies) scn.MoveCardsToLocation(site, scn.GetDSCard("trooper1"), scn.GetDSCard("trooper2"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.MoveCardsToLocation(here ? site : scn.GetDSStartingLocation(), scn.GetLSCard("gundark"));
        scn.LSInitiateBattle(site); scn.PassAllResponses(); scn.SkipToDamageSegment(false); scn.PassDamageSegmentActions();
        return scn;
    }

    private void reduce(boolean luke, boolean extraEnemies, int original, int remaining) {
        var scn = battle(true, luke, extraEnemies); var g = scn.GetLSCard("gundark");
        assertEquals(original, scn.GetUnpaidLSBattleDamage());
        assertTrue(scn.LSCardActionAvailable(g, "Place Gundark in Lost Pile"));
        int lost = scn.GetLSLostPileCount(); scn.LSUseCardAction(g, "Place Gundark in Lost Pile"); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, g);
        assertEquals(lost + 1, scn.GetLSLostPileCount());
        if (remaining > 0) {
            assertTrue(scn.AwaitingLSBattleDamagePayment()); assertEquals(remaining, scn.GetUnpaidLSBattleDamage());
            scn.LSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses();
            assertEquals(lost + 1 + remaining, scn.GetLSLostPileCount());
        } else assertFalse(scn.AwaitingLSBattleDamagePayment());
        assertAtLocation(scn.GetDSCard("forest"), scn.GetLSCard(luke ? "luke" : "trooper"));
    }
    @Test public void GundarkReducesActualFiveDamageToZeroAndIsPlacedInLostPile() { reduce(false, false, 5, 0); }
    @Test public void GundarkReductionDoesNotCreateNegativeDamage() { reduce(true, false, 3, 0); }
    @Test public void GundarkReducesActualSevenDamageToTwo() { reduce(false, true, 7, 2); }

    @Test public void GundarkCannotReduceDamageFromBattleElsewhere() {
        var scn = battle(false, false, false);
        assertTrue(scn.AwaitingLSBattleDamagePayment()); assertEquals(5, scn.GetUnpaidLSBattleDamage());
        int lost = scn.GetLSLostPileCount(); scn.LSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses();
        assertEquals(lost + 5, scn.GetLSLostPileCount()); assertAtLocation(scn.GetDSStartingLocation(), scn.GetLSCard("gundark"));
    }

    @Test public void GundarkReductionIsOptionalAndDoesNotForfeitTheCreature() {
        var scn = battle(true, false, false); var g = scn.GetLSCard("gundark");
        assertTrue(scn.LSCardActionAvailable(g, "Place Gundark in Lost Pile"));
        scn.LSPass(); scn.PassAllResponses();
        assertTrue(scn.AwaitingLSBattleDamagePayment()); assertEquals(5, scn.GetUnpaidLSBattleDamage());
        for (int i = 0; i < 5; i++) {
            scn.LSPayBattleDamageFromReserveDeck(); scn.PassAllResponses();
        }
        assertAtLocation(scn.GetDSCard("forest"), g);
    }
}
