package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_050_Tests {
    private static final String DOWNLOAD = "Deploy an electropole, Coruscant Naboo location, or Theed Palace vehicle";
    private static final String OUT_OF_PLAY = "Place Jar Jar and a Coruscant Political Effect out of play";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("jarJar", "1250_50"); put("electropole", "14_65"); put("plains", "12_79");
            put("speeder", "14_60"); put("snow", "3_69"); put("dual", "3_74");
            put("wrongNaboo", "14_51"); put("dock", "12_74"); put("mace", "14_19");
            put("home", "8_69"); put("related", "8_77"); put("luke", "1_19");
            put("senate", "12_75"); put("platform", "1113_3"); put("political", "12_40"); put("ordinary", "12_42");
        }}, new HashMap<>() {{
            put("political", "12_127"); put("wrongPolitical", "1112_29");
            put("scout", "8_175"); put("cannon", "8_178"); put("rifle", "1_312");
            put("trooper", "1_194"); put("yularen", "1_166");
        }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void SenatorJarJarMetadataMatchesStandardAndAlternateFullTemplates() {
        var s = scenario(); var bp = s.GetLSCard("jarJar").getBlueprint();
        assertEquals("Senator Jar Jar Binks", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(2, bp.getDestiny(), s.epsilon); assertEquals(2, bp.getDeployCost(), s.epsilon);
        assertEquals(3, bp.getPower(), s.epsilon); assertEquals(3, bp.getAbility(), s.epsilon);
        assertEquals(2, bp.getPolitics(), s.epsilon); assertEquals(4, bp.getForfeit(), s.epsilon);
        assertEquals(Species.GUNGAN, bp.getSpecies()); assertTrue(bp.getPersonas().contains(Persona.JAR_JAR));
        assertEquals("Gungan.", bp.getLore());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.ALIEN, CardType.REPUBLIC)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.ALIEN, Icon.REPUBLIC,
                Icon.THEED_PALACE, Icon.EPISODE_I, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test
    public void SenatorJarJarHasOnlyJusticeAndOrderAgendasOnTable() {
        var s = scenario(); s.StartGame(); var jarJar = s.GetLSCard("jarJar");
        s.MoveCardsToLocation(s.GetLSStartingLocation(), jarJar);
        assertEquals(List.of(Agenda.JUSTICE, Agenda.ORDER), s.game().getModifiersQuerying().getAgendas(s.gameState(), jarJar));
    }

    private boolean action(VirtualTableScenario s, String label) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("jarJar"), label);
    }

    private VirtualTableScenario search(boolean darkTurn) {
        var s = scenario(); s.StartGame(); var home = s.GetLSCard("home");
        s.MoveLocationToTable(home); s.MoveCardsToLocation(home, s.GetLSCard("jarJar"));
        if (darkTurn) s.SkipToDSTurn(Phase.CONTROL); else s.SkipToLSTurn(Phase.CONTROL);
        if (s.GetLSForcePileCount() < 2) s.LSActivateForceCheat(2);
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("electropole"), s.GetLSCard("plains"), s.GetLSCard("speeder"),
                s.GetLSCard("snow"), s.GetLSCard("wrongNaboo"), s.GetLSCard("dock"), s.GetLSCard("mace"));
        if (darkTurn && s.DSAnyDecisionsAvailable()) s.DSPass();
        return s;
    }

    private void assertSelectable(VirtualTableScenario s, String label, boolean expected) {
        int index = s.LSGetADParamAsList("blueprintId").indexOf(s.GetLSCard(label).getBlueprintId(true));
        assertTrue(label + " missing from Reserve", index >= 0);
        assertEquals(label, Boolean.toString(expected), s.LSGetADParam("selectable")[index]);
    }

    private void deployFromReserve(VirtualTableScenario s, String target, boolean audit) {
        var card = s.GetLSCard(target); assertTrue(action(s, DOWNLOAD));
        s.LSUseCardAction(s.GetLSCard("jarJar"), DOWNLOAD); s.PassAllResponses();
        if (audit) {
            for (String eligible : List.of("electropole", "plains", "speeder")) assertSelectable(s, eligible, true);
            for (String ineligible : List.of("snow", "wrongNaboo", "dock", "mace")) assertSelectable(s, ineligible, false);
        }
        s.LSChooseCard(card); s.PassAllResponses();
        if (s.LSDecisionAvailable("Choose where to deploy") || s.LSDecisionAvailable("Choose location where to deploy")) {
            s.LSChooseCard(s.GetLSCard(target.equals("electropole") ? "jarJar" : "home"));
        }
        if (s.LSDecisionAvailable("On which side")) s.LSChooseOption("Right");
        s.PassAllResponses();
        switch (target) {
            case "electropole" -> assertEquals(s.GetLSCard("jarJar"), card.getAttachedTo());
            case "plains" -> assertEquals(Zone.LOCATIONS, card.getZone());
            case "speeder" -> assertEquals(s.GetLSCard("home"), s.game().getModifiersQuerying().getLocationThatCardIsAt(s.gameState(), card));
            default -> fail("Unexpected target: " + target);
        }
    }

    @Test
    public void SenatorJarJarSearchesOnlyTheThreePrintedClassesAndReshuffles() {
        var s = search(false); var marker = s.GetLSCard("snow");
        s.gameState().markCardsRevealedFromPile(List.of(marker)); assertTrue(s.gameState().isCardRevealedFromPile(marker));
        deployFromReserve(s, "electropole", true);
        assertFalse(s.gameState().isCardRevealedFromPile(marker));
    }

    @Test
    public void SenatorJarJarActuallyDownloadsCoruscantNabooSite() { deployFromReserve(search(false), "plains", false); }

    @Test
    public void SenatorJarJarActuallyDownloadsTheedVehicleAtNormalCost() {
        var s = search(false); int force = s.GetLSForcePileCount();
        deployFromReserve(s, "speeder", false); assertEquals(force - 2, s.GetLSForcePileCount());
    }

    @Test
    public void SenatorJarJarMayDownloadOnOpponentsControlPhase() { deployFromReserve(search(true), "plains", false); }

    @Test
    public void SenatorJarJarDownloadLimitSpansPhasesAndResetsNextTurn() {
        var s = search(false); deployFromReserve(s, "electropole", false);
        int turn = s.gameState().getPlayersLatestTurnNumber(s.LS);
        s.SkipToPhase(Phase.MOVE);
        assertEquals(turn, s.gameState().getPlayersLatestTurnNumber(s.LS));
        assertTrue(s.AwaitingLSMovePhaseActions()); assertFalse(action(s, DOWNLOAD));
        for (int i = 0; i < 100 && !(s.GetCurrentPlayer().equals(s.DS) && s.GetCurrentPhase() == Phase.CONTROL); i++) {
            String prompt = s.GetCurrentDecision().getText();
            if (prompt.equals("You have not activated Force. Do you want to Pass?")) {
                if (s.DSDecisionAvailable(prompt)) s.DSChooseYes(); else s.LSChooseYes();
            } else {
                assertTrue(prompt, prompt.contains("action or Pass") || prompt.contains("Optional responses")
                        || prompt.contains("RECIRCULATED"));
                if (s.DSDecisionAvailable(prompt)) s.DSPass(); else s.LSPass();
            }
        }
        assertEquals(s.DS, s.GetCurrentPlayer()); assertEquals(Phase.CONTROL, s.GetCurrentPhase());
        if (s.DSAnyDecisionsAvailable()) s.DSPass();
        deployFromReserve(s, "plains", false);
    }

    private void darkShot(String jarJarSite, boolean vehicle, int destiny, boolean expectedHit) {
        var s = scenario(); s.StartGame(); var site = s.GetLSCard("home"); var luke = s.GetLSCard("luke");
        s.MoveLocationToTable(site);
        var jarSite = s.GetLSCard(jarJarSite);
        if (!jarSite.equals(site)) s.MoveLocationToTable(jarSite);
        s.MoveCardsToLocation(jarSite, s.GetLSCard("jarJar"));
        var carrier = s.GetDSCard(vehicle ? "scout" : "trooper");
        var gun = s.GetDSCard(vehicle ? "cannon" : "rifle");
        s.MoveCardsToLocation(site, luke, carrier); s.AttachCardsTo(carrier, gun);
        s.DSStartBattleAndSkipToWeaponsSegment(site); s.PrepareDSDestiny(destiny);
        assertTrue(s.DSCardActionAvailable(gun, "Fire")); s.DSUseCardAction(gun, "Fire");
        s.DSChooseCard(luke); s.PassWeaponFireWithDestinyDraw(); s.PassAllResponses();
        assertEquals(expectedHit, luke.isHit());
    }

    @Test
    public void SenatorJarJarMakesSameSiteVehicleWeaponMissAtEquality() { darkShot("home", true, 5, false); }

    @Test
    public void SenatorJarJarDoesNotPreventVehicleHitAboveReducedBoundary() { darkShot("home", true, 6, true); }

    @Test
    public void SenatorJarJarAlsoReducesVehicleWeaponAtRelatedSite() { darkShot("related", true, 5, false); }

    @Test
    public void SenatorJarJarDoesNotReduceVehicleWeaponAtUnrelatedSite() { darkShot("plains", true, 5, true); }

    @Test
    public void SenatorJarJarDoesNotReduceOrdinaryCharacterWeapon() { darkShot("home", false, 5, true); }

    @Test
    public void SenatorJarJarReducesFriendlyVehicleWeaponToo() {
        var s = scenario(); s.StartGame(); var site = s.GetLSCard("home"); s.MoveLocationToTable(site);
        var snow = s.GetLSCard("snow"); var dual = s.GetLSCard("dual"); var yularen = s.GetDSCard("yularen");
        s.MoveCardsToLocation(site, s.GetLSCard("jarJar"), snow, yularen);
        s.AttachCardsTo(snow, dual);
        s.LSStartBattleAndSkipToWeaponsSegment(site); s.PrepareLSDestiny(4);
        assertTrue(s.LSCardActionAvailable(dual, "Fire")); s.LSUseCardAction(dual, "Fire");
        s.LSChooseCard(yularen); s.PassWeaponFireWithDestinyDraw(); s.PassAllResponses();
        assertFalse(yularen.isHit());
    }

    private VirtualTableScenario politics(String location, boolean eligible) {
        var s = scenario(); s.StartGame(); var site = s.GetLSCard(location);
        s.MoveLocationToTable(site); s.MoveCardsToLocation(site, s.GetLSCard("jarJar"));
        s.MoveCardsToLSSideOfTable(s.GetLSCard("ordinary"));
        s.MoveCardsToDSSideOfTable(s.GetDSCard("wrongPolitical"));
        if (eligible) {
            s.MoveCardsToLSSideOfTable(s.GetLSCard("political"));
            s.MoveCardsToDSSideOfTable(s.GetDSCard("political"));
        }
        s.SkipToLSTurn(Phase.CONTROL);
        return s;
    }

    private void placeOut(boolean dark) {
        var s = politics("senate", true); var jarJar = s.GetLSCard("jarJar");
        var own = s.GetLSCard("political"); var opp = s.GetDSCard("political");
        PhysicalCardImpl chosen = dark ? opp : own;
        assertTrue(action(s, OUT_OF_PLAY)); s.LSUseCardAction(jarJar, OUT_OF_PLAY); s.PassAllResponses();
        assertTrue(s.LSDecisionAvailable("Choose a Coruscant Political Effect"));
        assertTrue(s.LSHasCardChoicesAvailable(own, opp));
        assertFalse(s.LSHasCardChoiceAvailable(s.GetLSCard("ordinary")));
        assertFalse(s.LSHasCardChoiceAvailable(s.GetDSCard("wrongPolitical")));
        s.LSChooseCard(chosen); s.PassAllResponses();
        if (s.LSDecisionAvailable("Choose card to place out of play")) {
            s.LSChooseCard(jarJar); s.PassAllResponses();
        }
        assertEquals(Zone.OUT_OF_PLAY, jarJar.getZone()); assertEquals(Zone.OUT_OF_PLAY, chosen.getZone());
        assertNotEquals(Zone.OUT_OF_PLAY, (dark ? own : opp).getZone());
    }

    @Test
    public void SenatorJarJarAtSenateCanPlaceHimselfAndDarkCoruscantPoliticalEffectOutOfPlay() { placeOut(true); }

    @Test
    public void SenatorJarJarAtSenateCanPlaceHimselfAndLightCoruscantPoliticalEffectOutOfPlay() { placeOut(false); }

    @Test
    public void SenatorJarJarCannotPlaceCardsOutOfPlayFromSenateLandingPlatform() {
        assertFalse(action(politics("platform", true), OUT_OF_PLAY));
    }

    @Test
    public void SenatorJarJarCannotPlaceHimselfOutWithoutMatchingPoliticalEffect() {
        assertFalse(action(politics("senate", false), OUT_OF_PLAY));
    }
}
