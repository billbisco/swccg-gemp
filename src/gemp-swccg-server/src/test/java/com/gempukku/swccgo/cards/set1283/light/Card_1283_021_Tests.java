package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("combo", "1283_21");
                    put("ellorrs", "1_49");
                    put("revolution", "1_62");
                    put("operative", "7_1");
                    put("boshek", "1_4");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("destiny", "9_134");
                    put("vader", "1_168");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    protected VirtualTableScenario GetHiddenBaseScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("combo", "1283_21");
                    put("ellorrs", "1_49");
                    put("revolution", "1_62");
                    put("operative", "7_1");
                    put("boshek", "1_4");
                    put("quarter", "1283_24");
                    put("yerka", "1210_143");
                    put("cantina", "1_128");
                    put("momaw", "1_20");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("destiny", "9_134");
                    put("vader", "1_168");
                }},
                10, 10,
                HiddenBaseVObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void UnderworldContactsAndUnchartedSettlementsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("combo").getBlueprint();

        assertEquals("Underworld Contacts & Uncharted Settlements", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void UnderworldContactsDoesNotDeployWithoutHiddenBase() {
        var scn = GetScenario();
        var combo = scn.GetLSCard("combo");

        scn.StartGame();
        scn.MoveCardsToLSHand(combo);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(combo));
    }

    @Test
    public void UnderworldContactsDeploysOnVirtualBlock4HiddenBaseAndSetsAlienPair() {
        var scn = GetHiddenBaseScenario();
        var combo = scn.GetLSCard("combo");
        var hiddenbase = scn.GetLSCard("hiddenbase");

        scn.StartGame();
        scn.MoveCardsToLSHand(combo);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(combo));
        scn.LSPlayCard(combo);
        scn.LSChooseCard(hiddenbase);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(hiddenbase, combo));
        assertEquals("Alien", hiddenbase.getWhileInPlayData().getTextValue());
    }

    @Test
    public void UnderworldContactsDoesNotDeployOnNonVirtualHiddenBase() {
        StartingSetup originalHiddenBaseOutsideDeck = new StartingSetup() {
            @Override
            public HashMap<String, String> Cards() {
                return new HashMap<>() {{
                    put("hiddenbase", "7_136");
                }};
            }

            @Override
            public void Setup(VirtualTableScenario scn) {
            }
        };
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("combo", "1283_21");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                originalHiddenBaseOutsideDeck,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var combo = scn.GetLSCard("combo");
        var hiddenbase = scn.GetLSCard("hiddenbase");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(hiddenbase);
        scn.MoveCardsToLSHand(combo);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(combo));
    }

    @Test
    public void UnderworldContactsCancelsYourDestinyOnTable() {
        var scn = GetHiddenBaseScenario();
        var combo = scn.GetLSCard("combo");
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var destiny = scn.GetDSCard("destiny");

        scn.StartGame();
        scn.AttachCardsTo(hiddenbase, combo);
        scn.gameState().reapplyAffectingForCard(scn.game(), combo);
        PhysicalCardImpl bhbm = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.DS, VirtualTableScenario._cardLibrary, "9_151");
        scn.gameState().addCardToZone(bhbm, Zone.SIDE_OF_TABLE, scn.DS, true, false, false);
        bhbm.setObjectiveDeploymentComplete(true);
        scn.gameState().reapplyAffectingForCard(scn.game(), bhbm);
        scn.AttachCardsTo(bhbm, destiny);
        scn.gameState().reapplyAffectingForCard(scn.game(), destiny);

        // The cards were placed directly on the table, so the next "table changed" check happens at the end of
        // the Dark Side Activate phase. Pass both players through it.
        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSPass();
        assertTrue(scn.DSDecisionAvailable("You have not activated Force. Do you want to Pass?"));
        scn.DSChooseYes();
        assertTrue(scn.AwaitingLSActivatePhaseActions());
        scn.LSPass();

        // Underworld Contacts and Uncharted Settlements (V) each have a required trigger that cancels Your Destiny,
        // so the current player (DS) is asked which fires first. Underworld Contacts' own trigger must be offered.
        assertTrue(scn.DSDecisionAvailable("END_OF_PHASE - Required responses"));
        assertTrue(scn.GetDSAvailableActions().contains("Cancel Your Destiny"));
        assertTrue(scn.DSGetADParamAsList("blueprintId").contains(combo.getBlueprintId(true)));
        scn.DSChooseAction("Cancel Your Destiny");

        assertTrue(scn.DSDecisionAvailable("Cancel Your Destiny - Optional responses"));
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, destiny);
    }

    @Test
    public void UnderworldContactsMayNotDeployEllorrsRevolutionOrOperatives() {
        var scn = GetHiddenBaseScenario();
        var combo = scn.GetLSCard("combo");
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var ellorrs = scn.GetLSCard("ellorrs");
        var revolution = scn.GetLSCard("revolution");
        var operative = scn.GetLSCard("operative");
        var boshek = scn.GetLSCard("boshek");

        scn.StartGame();
        scn.AttachCardsTo(hiddenbase, combo);
        scn.MoveCardsToLSHand(ellorrs, revolution, operative, boshek);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(ellorrs));
        assertFalse(scn.LSCardPlayAvailable(revolution));
        assertFalse(scn.LSCardPlayAvailable(operative));
        assertTrue(scn.LSCardPlayAvailable(boshek));
    }

    @Test
    public void UnderworldContactsOpponentGeneratesNoForceAtHiddenLandingSite() {
        var scn = GetHiddenBaseScenario();
        var combo = scn.GetLSCard("combo");
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var hls = scn.GetLSCard("hls");

        scn.StartGame();
        scn.AttachCardsTo(hiddenbase, combo);

        float dsGen = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), hls, scn.DS);
        assertEquals(0, Math.round(dsGen));
    }

    @Test
    public void UnderworldContactsAddsAttritionWhileSystemsWillSlipIsOnTable() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("combo", "1283_21");
                    put("station", "1240_103");
                    put("perimeter", "1240_104");
                    put("luke", "1_19");
                    put("biggs", "1_3");
                    put("leia", "1_17");
                    put("boshek", "1_4");
                    put("jawa", "1_12");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("vader", "1_168");
                }},
                10, 10,
                HiddenBaseVObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var combo = scn.GetLSCard("combo");
        var hls = scn.GetLSCard("hls");
        var station = scn.GetLSCard("station");
        var perimeter = scn.GetLSCard("perimeter");
        var luke = scn.GetLSCard("luke");
        var biggs = scn.GetLSCard("biggs");
        var leia = scn.GetLSCard("leia");
        var boshek = scn.GetLSCard("boshek");
        var jawa = scn.GetLSCard("jawa");
        var vader = scn.GetDSCard("vader");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        var stationPlacement = scn.gameState().getLocationPlacement(scn.game(), station, "Yavin 4", null);
        scn.MoveLocationToTable(station, stationPlacement.getFirst());
        var perimeterPlacement = scn.gameState().getLocationPlacement(scn.game(), perimeter, "Yavin 4", null);
        scn.MoveLocationToTable(perimeter, perimeterPlacement.getFirst());
        scn.MoveCardsToLocation(hls, luke);
        scn.MoveCardsToLocation(station, biggs);
        scn.MoveCardsToLocation(perimeter, leia);
        scn.SkipToPhase(Phase.CONTROL);
        assertEquals("Systems Will Slip Through Your Fingers", hiddenbase.getBlueprint().getTitle());

        scn.AttachCardsTo(hiddenbase, combo);
        scn.MoveCardsToLocation(hls, boshek);
        scn.MoveCardsToLocation(station, jawa);
        scn.MoveCardsToLocation(hls, vader, trooper);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(hls);
        scn.PrepareDSDestiny(0);
        scn.PrepareLSDestiny(0);
        scn.SkipToDamageSegment(true);

        assertEquals(2, scn.GetUnpaidDSAttrition());
    }

    /**
     * Dark Side deploys Vader from hand to the Cantina (where Light Side has no characters) during its deploy phase.
     * Yerka Mig (V) is then lost by its own game text because opponent controls the Cantina.
     */
    private void DarkSideTakesControlOfCantinaMakingYerkaLost(VirtualTableScenario scn) {
        var cantina = scn.GetLSCard("cantina");
        var yerka = scn.GetLSCard("yerka");
        var vader = scn.GetDSCard("vader");

        scn.MoveCardsToDSHand(vader);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(8);

        assertTrue(scn.IsAttachedTo(cantina, yerka));
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSDeployAvailable(vader));
        scn.DSDeployCard(vader);
        assertTrue(scn.DSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.DSHasCardChoiceAvailable(cantina));
        scn.DSChooseCard(cantina);
        assertTrue(scn.LSDecisionAvailable("Use 6 Force - Optional responses"));
        scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("Use 6 Force - Optional responses"));
        scn.DSPass();
        assertAtLocation(cantina, vader);
        // Yerka Mig (V)'s own required trigger makes it lost as soon as opponent controls the Cantina.
        assertTrue(scn.LSDecisionAvailable("ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses"));
        scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses"));
        scn.DSPass();
        assertInZone(Zone.LOST_PILE, yerka);
    }

    @Test
    public void UnderworldContactsMayPlaceJustLostYerkaMigVInUsedPile() {
        var scn = GetHiddenBaseScenario();
        var combo = scn.GetLSCard("combo");
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var cantina = scn.GetLSCard("cantina");
        var yerka = scn.GetLSCard("yerka");

        scn.StartGame();
        scn.AttachCardsTo(hiddenbase, combo);
        scn.MoveLocationToTable(cantina);
        scn.AttachCardsTo(cantina, yerka);
        DarkSideTakesControlOfCantinaMakingYerkaLost(scn);

        assertTrue(scn.LSDecisionAvailable("Just lost"));
        assertTrue(scn.LSDecisionAvailable("Optional responses"));
        assertTrue(scn.LSCardActionAvailable(combo, "Place Yerka Mig (V) in Used Pile"));
        scn.LSUseCardAction(combo, "Place Yerka Mig (V) in Used Pile");
        scn.PassAllResponses();

        assertInZone(Zone.USED_PILE, yerka);
    }

    @Test
    public void JustLostYerkaMigVStaysInLostPileWithoutUnderworldContacts() {
        var scn = GetHiddenBaseScenario();
        var combo = scn.GetLSCard("combo");
        var cantina = scn.GetLSCard("cantina");
        var yerka = scn.GetLSCard("yerka");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.AttachCardsTo(cantina, yerka);
        DarkSideTakesControlOfCantinaMakingYerkaLost(scn);

        assertFalse(scn.LSCardActionAvailable(combo, "Place Yerka Mig (V) in Used Pile"));
        assertFalse(scn.LSActionAvailable("in Used Pile"));
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, yerka);
    }

    /**
     * Light Side deploys the given character from hand to the Cantina during its deploy phase and both players pass
     * the Force-use and play responses, stopping at Light Side's "just deployed" optional response window (if any).
     */
    private void DeployFromHandToCantina(VirtualTableScenario scn, PhysicalCardImpl character, int deployCost) {
        var cantina = scn.GetLSCard("cantina");

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(character));
        scn.LSDeployCard(character);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.LSHasCardChoiceAvailable(cantina));
        scn.LSChooseCard(cantina);
        assertTrue(scn.DSDecisionAvailable("Use " + deployCost + " Force - Optional responses"));
        scn.DSPass();
        assertTrue(scn.LSDecisionAvailable("Use " + deployCost + " Force - Optional responses"));
        scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("PLAY - Optional responses"));
        scn.DSPass();
        assertAtLocation(cantina, character);
    }

    /**
     * With Yerka Mig (V) on the Cantina and only aliens providing Light Side ability, deploys BoShek and then Momaw
     * Nadon (both unique aliens) to the Cantina in the same deploy phase, retrieving with Yerka Mig (V) after the
     * first deploy. Returns whether Yerka Mig (V)'s retrieval was offered again after the second deploy.
     */
    private boolean RetrieveOfferedForSecondUniqueAlienDeployedThisTurn(VirtualTableScenario scn) {
        var cantina = scn.GetLSCard("cantina");
        var yerka = scn.GetLSCard("yerka");
        var boshek = scn.GetLSCard("boshek");
        var momaw = scn.GetLSCard("momaw");
        var revolution = scn.GetLSCard("revolution");
        var operative = scn.GetLSCard("operative");

        scn.MoveLocationToTable(cantina);
        scn.AttachCardsTo(cantina, yerka);
        scn.MoveCardsToTopOfLSLostPile(revolution, operative);
        scn.MoveCardsToLSHand(boshek, momaw);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(10);

        DeployFromHandToCantina(scn, boshek, 4);
        assertTrue(scn.LSDecisionAvailable("just deployed - Optional responses"));
        assertTrue(scn.LSCardActionAvailable(yerka, "Retrieve 1 Force"));
        scn.LSUseCardAction(yerka, "Retrieve 1 Force");
        scn.PassAllResponses();
        assertEquals(1, scn.GetLSLostPileCount());

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPass();
        DeployFromHandToCantina(scn, momaw, 2);
        return scn.LSCardActionAvailable(yerka, "Retrieve 1 Force");
    }

    @Test
    public void UnderworldContactsLimitsYerkaMigVRetrievalToOncePerTurn() {
        var scn = GetHiddenBaseScenario();
        var combo = scn.GetLSCard("combo");
        var hiddenbase = scn.GetLSCard("hiddenbase");

        scn.StartGame();
        scn.AttachCardsTo(hiddenbase, combo);

        assertFalse(RetrieveOfferedForSecondUniqueAlienDeployedThisTurn(scn));
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertEquals(1, scn.GetLSLostPileCount());
    }

    @Test
    public void YerkaMigVRetrievesForEachUniqueAlienDeployedWithoutUnderworldContacts() {
        var scn = GetHiddenBaseScenario();
        var yerka = scn.GetLSCard("yerka");

        scn.StartGame();

        assertTrue(RetrieveOfferedForSecondUniqueAlienDeployedThisTurn(scn));
        scn.LSUseCardAction(yerka, "Retrieve 1 Force");
        scn.PassAllResponses();
        assertEquals(0, scn.GetLSLostPileCount());
    }

    /**
     * Hidden Base (V) / Systems Will Slip Through Your Fingers (V). The objective deploys Yavin 4, Hidden Landing
     * Site and Uncharted Settlements (V) by itself; the only decision Light Side is asked is which pair it may deploy.
     */
    StartingSetup HiddenBaseVObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("hiddenbase", "1240_107");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            assertTrue(scn.LSDecisionAvailable("Choose pair you may deploy"));
            scn.LSChoose("Alien / Independent starship");

            assertInZone(Zone.LOCATIONS, scn.GetLSCard("yavin"));
            assertInZone(Zone.LOCATIONS, scn.GetLSCard("hls"));
            assertTrue(scn.IsAttachedTo(scn.GetLSCard("hiddenbase"), scn.GetLSCard("uncharted")));
            assertTrue(scn.AwaitingDSActivatePhaseActions());
        }
    };
}
