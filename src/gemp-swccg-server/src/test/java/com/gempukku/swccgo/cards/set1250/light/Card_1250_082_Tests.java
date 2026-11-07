package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_082_Tests {
    private static StartingSetup councilStart(String chamberId) {
        return new StartingSetup() {
            @Override
            public HashMap<String, String> Cards() {
                return new HashMap<>() {{ put("objective", "1250_82"); put("chamber", chamberId); }};
            }

            @Override
            public void Setup(VirtualTableScenario s) {
                if (s.LSDecisionAvailable("Choose your Objective")) s.LSChooseCard(s.GetLSCard("objective"));
                if (s.LSDecisionAvailable("Choose Jedi Council Chamber")) s.LSChooseCard(s.GetLSCard("chamber"));
            }
        };
    }

    private VirtualTableScenario scenario(String chamberId) {
        return new VirtualTableScenario(new HashMap<>() {{
            put("yoda", "12_35"); put("depa", "12_4"); put("mace", "12_13"); put("plo", "12_21");
            put("senate", "12_75"); put("speak", "12_69"); put("affect", "1_43"); put("newrepublic", "601_46");
            put("luke", "1_19"); put("trooper", "1_26"); put("home", "9_74"); put("tibrin", "6_87");
            put("test", "4_75"); put("other", "1_109"); put("another", "11_28");
            put("plains", "12_79"); put("cantina", "1_128");
        }}, new HashMap<>() {{
            put("trooper", "1_194"); put("vader", "1_168");
        }}, 40, 40, councilStart(chamberId), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario start() {
        var s = scenario("12_76"); s.StartGame();
        var objective = s.GetLSCard("objective");
        assertTrue(objective.isObjectiveDeploymentComplete());
        assertEquals(Zone.LOCATIONS, s.GetLSCard("chamber").getZone());
        return s;
    }

    private void verifyFace(VirtualTableScenario s, com.gempukku.swccgo.game.SwccgCardBlueprint bp, String title, int destiny) {
        assertEquals(title, bp.getTitle()); assertEquals(destiny, bp.getDestiny(), s.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet()); assertTrue(bp.hasVirtualSuffix());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.OBJECTIVE)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.OBJECTIVE, Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test
    public void BothFinalFacesAndActualStartingChamber() {
        var s = start(); var objective = s.GetLSCard("objective");
        assertFalse(objective.isFlipped());
        verifyFace(s, objective.getBlueprint(), Title.Well_Handle_This, 0);
        verifyFace(s, objective.getOtherSideBlueprint(), Title.Duel_Of_The_Fates, 7);
    }

    private void fund(VirtualTableScenario s, int amount) {
        if (s.GetLSForcePileCount() < amount) s.LSActivateForceCheat(amount - s.GetLSForcePileCount());
    }

    private void deployAt(VirtualTableScenario s, PhysicalCardImpl card, PhysicalCardImpl site) {
        waitForLightDeploy(s);
        assertTrue(s.LSDeployAvailable(card)); s.LSDeployCard(card);
        if (s.LSDecisionAvailable("Choose where to deploy")) s.LSChooseCard(site);
        s.PassAllResponses(); assertEquals(site, card.getAtLocation());
    }

    private void waitForLightDeploy(VirtualTableScenario s) {
        for (int i = 0; i < 8 && !s.AwaitingLSDeployPhaseActions(); i++) {
            if (s.AwaitingDSDeployPhaseActions()) s.DSPass();
            else if (s.GetCurrentDecision().getText().contains("Optional responses")) s.PlayerPass(s.GetDecidingPlayer());
            else break;
        }
        assertTrue(s.AwaitingLSDeployPhaseActions());
    }

    private void stageFlip(VirtualTableScenario s, boolean twoOtherJedi) {
        var chamber = s.GetLSCard("chamber");
        s.MoveCardsToLocation(chamber, s.GetLSCard("depa"));
        if (twoOtherJedi) s.MoveCardsToLocation(chamber, s.GetLSCard("mace"));
        s.MoveCardsToHand(s.GetLSCard("yoda"));
    }

    @Test
    public void ActualYodaDeployCostsOneAndYodaWithTwoOtherJediFlips() {
        var s = start(); stageFlip(s, true); s.SkipToLSTurn(Phase.DEPLOY); fund(s, 5);
        int force = s.GetLSForcePileCount();
        deployAt(s, s.GetLSCard("yoda"), s.GetLSCard("chamber"));
        assertEquals(force - 1, s.GetLSForcePileCount());
        assertTrue(s.GetLSCard("objective").isFlipped());
    }

    @Test
    public void YodaWithOnlyOneOtherJediDoesNotFlip() {
        var s = start(); stageFlip(s, false); s.MoveLocationToTable(s.GetLSCard("plains"));
        s.MoveCardsToLocation(s.GetLSCard("plains"), s.GetLSCard("mace"));
        s.SkipToLSTurn(Phase.DEPLOY); fund(s, 5);
        deployAt(s, s.GetLSCard("yoda"), s.GetLSCard("chamber"));
        assertFalse(s.GetLSCard("objective").isFlipped());
    }

    @Test
    public void PermanentDeploymentRestrictionsSurviveActualFlip() {
        var s = start(); stageFlip(s, true); s.MoveLocationToTable(s.GetLSCard("tibrin"));
        var affect = s.GetLSCard("affect"); var senate = s.GetLSCard("senate"); var luke = s.GetLSCard("luke");
        var newRepublic = s.GetLSCard("newrepublic"); var home = s.GetLSCard("home");
        s.MoveCardsToHand(affect, senate, luke, newRepublic, home, s.GetLSCard("speak"), s.GetLSCard("plo"));
        fund(s, 14); s.SkipToLSTurn(Phase.DEPLOY);
        for (var blocked : List.of(affect, senate, luke, newRepublic, home)) {
            assertFalse(blocked.getBlueprint().getTitle(), s.LSDeployAvailable(blocked));
        }
        assertTrue(s.LSDeployAvailable(s.GetLSCard("plo")));
        deployAt(s, s.GetLSCard("yoda"), s.GetLSCard("chamber"));
        assertTrue(s.GetLSCard("objective").isFlipped());
        waitForLightDeploy(s);
        for (var blocked : List.of(affect, senate, luke, newRepublic, home)) {
            assertFalse(blocked.getBlueprint().getTitle(), s.LSDeployAvailable(blocked));
        }
        assertEquals(CardSubtype.USED, s.game().getModifiersQuerying().getInterruptType(s.gameState(), s.GetLSCard("speak")));
    }

    @Test
    public void SpeakWithCouncilResolvesItsActualDeploymentModeToUsedPile() {
        var s = start(); var speak = s.GetLSCard("speak"); var depa = s.GetLSCard("depa");
        s.MoveCardsToHand(speak); fund(s, 9); s.SkipToLSTurn(Phase.DEPLOY);
        s.MoveCardsToTopOfLSReserveDeck(depa, s.GetLSCard("mace"));
        assertTrue(s.LSCardPlayAvailable(speak, "Deploy a Jedi Council member"));
        s.LSPlayCard(speak, "Deploy a Jedi Council member"); s.PassAllResponses();
        s.LSChooseCard(depa); s.PassAllResponses();
        if (s.LSDecisionAvailable("Choose where to deploy")) s.LSChooseCard(s.GetLSCard("chamber"));
        s.PassAllResponses();
        assertEquals(s.GetLSCard("chamber"), depa.getAtLocation());
        assertTrue(s.GetLSUsedPile().contains(speak)); assertFalse(s.GetLSLostPile().contains(speak));
    }

    @Test
    public void BackLetsRestrictedJediActuallyDeployAwayFromChamber() {
        var s = start(); stageFlip(s, true); var away = s.GetDSStartingLocation();
        s.MoveCardsToLocation(away, s.GetLSCard("trooper")); s.MoveCardsToHand(s.GetLSCard("plo"));
        fund(s, 14); s.SkipToLSTurn(Phase.DEPLOY);
        deployAt(s, s.GetLSCard("yoda"), s.GetLSCard("chamber"));
        assertTrue(s.GetLSCard("objective").isFlipped());
        deployAt(s, s.GetLSCard("plo"), away);
    }

    private boolean retrieveOffered(VirtualTableScenario s) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("objective"), "Retrieve 2 Force");
    }

    private VirtualTableScenario initiateBattle(boolean jedi) {
        var s = start(); stageFlip(s, true); var site = s.GetDSStartingLocation();
        s.MoveCardsToLocation(site, s.GetDSCard("trooper"), s.GetLSCard(jedi ? "plo" : "trooper"));
        s.SkipToLSTurn(Phase.DEPLOY); fund(s, 8); deployAt(s, s.GetLSCard("yoda"), s.GetLSCard("chamber"));
        assertTrue(s.GetLSCard("objective").isFlipped());
        s.MoveCardsToTopOfLSLostPile(s.GetLSCard("other"), s.GetLSCard("another"));
        s.SkipToPhase(Phase.BATTLE);
        assertTrue(s.LSCanInitiateBattle(site)); s.LSInitiateBattle(site);
        for (int i = 0; i < 12 && !retrieveOffered(s)
                && s.GetCurrentDecision().getText().contains("Optional responses"); i++) {
            s.PlayerPass(s.GetDecidingPlayer());
        }
        return s;
    }

    @Test
    public void InitiatingWithJediOffersActualTwoForceRetrieval() {
        var s = initiateBattle(true);
        assertTrue(retrieveOffered(s));
        s.LSUseCardAction(s.GetLSCard("objective"), "Retrieve 2 Force"); s.PassAllResponses();
        assertTrue(s.GetLSUsedPile().containsAll(List.of(s.GetLSCard("other"), s.GetLSCard("another"))));
        assertEquals(0, s.GetLSLostPileCount());
    }

    @Test
    public void InitiatingWithNonJediDoesNotRetrieveEvenWithJediElsewhere() {
        var s = initiateBattle(false);
        assertFalse(retrieveOffered(s)); assertEquals(2, s.GetLSLostPileCount());
    }

    @Test
    public void FrontReducesAnActualLightForceDrainByOne() {
        var s = start(); var cantina = s.GetLSCard("cantina"); s.MoveLocationToTable(cantina);
        s.MoveCardsToLocation(cantina, s.GetLSCard("trooper"));
        s.SkipToLSTurn(Phase.CONTROL);
        assertFalse(s.GetLSCard("objective").isFlipped()); s.LSForceDrainAt(cantina);
        s.PassAllResponses();
        assertEquals(1, s.GetForceDrainTotal());
        assertTrue(s.AwaitingDSForceLossPayment());
        int before = s.GetDSLostPileCount(); s.DSPayForceLossFromReserveDeck(); s.PassAllResponses();
        assertEquals(before + 1, s.GetDSLostPileCount());
    }

    @Test
    public void BackStaysFlippedWithOnlyYodaThenFlipsAndClearsTestsWhenHeLeaves() {
        var s = start(); stageFlip(s, true); var chamber = s.GetLSCard("chamber");
        var objective = s.GetLSCard("objective"); var away = s.GetDSStartingLocation();
        var test = s.GetLSCard("test"); var plains = s.GetLSCard("plains"); var cantina = s.GetLSCard("cantina");
        s.MoveCardsToHand(plains, cantina); fund(s, 8); s.SkipToLSTurn(Phase.DEPLOY);
        deployAt(s, s.GetLSCard("yoda"), chamber); assertTrue(objective.isFlipped());
        s.MoveCardsToLocation(away, s.GetLSCard("depa"), s.GetLSCard("mace"));
        waitForLightDeploy(s);
        s.LSDeployLocation(plains); s.PassAllResponses();
        assertTrue(objective.isFlipped());

        var apprentice = s.GetLSCard("depa");
        test.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, apprentice, Filters.sameCardId(apprentice));
        test.setJediTestStatus(JediTestStatus.COMPLETED);
        s.AttachCardsTo(apprentice, test);
        s.MoveCardsToLocation(away, s.GetLSCard("yoda"));
        waitForLightDeploy(s);
        s.LSDeployLocation(cantina); s.PassAllResponses();
        assertFalse(objective.isFlipped()); assertTrue(s.GetLSUsedPile().contains(test));
    }

    @Test
    public void BackBlocksAnOtherwiseLegalChamberDrainButAllowsAnActualDrainElsewhere() {
        var s = scenario("1116_29"); s.StartGame(); stageFlip(s, true);
        var chamber = s.GetLSCard("chamber"); var cantina = s.GetLSCard("cantina");
        s.MoveLocationToTable(cantina); s.MoveCardsToLocation(cantina, s.GetLSCard("trooper"));
        s.SkipToLSTurn(Phase.CONTROL);
        assertTrue(s.LSForceDrainAvailable(chamber));
        s.SkipToPhase(Phase.DEPLOY); fund(s, 8); deployAt(s, s.GetLSCard("yoda"), chamber);
        assertTrue(s.GetLSCard("objective").isFlipped());
        s.SkipToLSTurn(Phase.CONTROL);
        assertFalse(s.LSForceDrainAvailable(chamber)); assertTrue(s.LSForceDrainAvailable(cantina));
        s.LSForceDrainAt(cantina); s.PassAllResponses();
        assertEquals(2, s.GetForceDrainTotal());
    }

    @Test
    public void BackGrantsThePrintedAttritionThresholdToJediButNotOtherCharacters() {
        var s = start(); stageFlip(s, true);
        var site = s.GetDSStartingLocation(); var jedi = s.GetLSCard("plo"); var trooper = s.GetLSCard("trooper");
        s.MoveCardsToLocation(site, jedi, trooper);
        var modifiers = s.game().getModifiersQuerying();
        assertTrue(modifiers.getImmunityToAttritionLessThan(s.gameState(), jedi) < 5);
        s.SkipToLSTurn(Phase.DEPLOY); fund(s, 8); deployAt(s, s.GetLSCard("yoda"), s.GetLSCard("chamber"));
        assertTrue(s.GetLSCard("objective").isFlipped());
        assertEquals(5, modifiers.getImmunityToAttritionLessThan(s.gameState(), jedi), s.epsilon);
        assertEquals(0, modifiers.getImmunityToAttritionLessThan(s.gameState(), trooper), s.epsilon);
    }

    @Test
    public void ActualVengeanceForceLossIsPreventedAfterTheLightObjectiveFlips() {
        var s = start(); stageFlip(s, true);
        var site = s.GetDSStartingLocation();
        var vengeance = createDarkCard(s, "10_29");
        var xizor = createDarkCard(s, "10_45");
        s.MoveCardsToDSSideOfTable(vengeance); vengeance.setObjectiveDeploymentComplete(true);
        s.gameState().flipCard(s.game(), vengeance, true);
        s.MoveCardsToLocation(site, xizor);
        s.SkipToLSTurn(Phase.DEPLOY); fund(s, 8); deployAt(s, s.GetLSCard("yoda"), s.GetLSCard("chamber"));
        assertTrue(s.GetLSCard("objective").isFlipped());
        s.SkipToDSTurn(Phase.CONTROL);
        String action = "Make " + s.LS + " lose 1 Force";
        assertTrue(s.DSCardActionAvailable(vengeance, action));
        int lost = s.GetLSLostPileCount();
        s.DSUseCardAction(vengeance, action); s.PassAllResponses();
        assertFalse(s.AwaitingLSForceLossPayment()); assertEquals(lost, s.GetLSLostPileCount());
    }

    private PhysicalCardImpl createDarkCard(VirtualTableScenario s, String id) {
        var card = (PhysicalCardImpl) s.gameState().createPhysicalCard(s.DS, VirtualTableScenario._cardLibrary, id);
        s.gameState().addCardToZone(card, Zone.HAND, s.DS, true, false, false);
        return card;
    }
}
