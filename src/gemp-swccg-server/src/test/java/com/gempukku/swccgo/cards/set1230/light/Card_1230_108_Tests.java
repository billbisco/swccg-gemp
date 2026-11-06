package com.gempukku.swccgo.cards.set1230.light;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_108_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rapid", "1230_108"); put("covert", "1230_101");
                    put("forest", "8_72"); put("door", "8_69");
                    put("beezer", "8_4"); put("midge", "9_11"); put("alien", "1_30");
                    put("delevar", "8_5");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194"); put("surprise", "5_156");
                }},
                30, 30,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void RapidDeploymentStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("rapid").getBlueprint();
        assertEquals("Rapid Deployment", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ENDOR);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RapidDeploymentDeploysARebelScoutFromReserveDeck() {
        var scn = GetScenario();
        var rapid = scn.GetLSCard("rapid");
        var delevar = scn.GetLSCard("delevar");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLSHand(rapid);
        scn.MoveCardsToLocation(site, luke);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(6);
        scn.MoveCardsToTopOfLSReserveDeck(delevar);
        assertTrue(scn.LSCardPlayAvailable(rapid, "Deploy Rebel scout from Reserve Deck"));
        scn.LSPlayCard(rapid, "Deploy Rebel scout from Reserve Deck");
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose card")) {
            scn.LSChooseCard(delevar);
        }
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();
        assertAtLocation(site, delevar);
        assertInZone(Zone.LOST_PILE, rapid);
    }

    @Test public void RapidDeploymentDiscountsHandScoutAtTargetSiteByTwo() { CheckHandDeploy("delevar", "forest", 0); }
    @Test public void RapidDeploymentDiscountsHandScoutAtRelatedSiteByTwo() { CheckHandDeploy("delevar", "door", 0); }
    @Test public void RapidDeploymentDoesNotDiscountHandScoutAtUnrelatedSite() { CheckHandDeploy("delevar", "starting-location", 2); }
    @Test public void RapidDeploymentDoesNotDiscountNonScoutRebel() { CheckHandDeploy("luke", "forest", 3); }
    @Test public void RapidDeploymentDoesNotDiscountAlienScout() { CheckHandDeploy("alien", "forest", 3); }
    @Test public void RapidDeploymentDiscountEndsAfterTurn() {
        var scn = DiscountScenario(); scn.SkipToLSTurn(Phase.DEPLOY); int force = scn.GetLSForcePileCount();
        Deploy(scn, "delevar", "forest"); assertEquals(force - 2, scn.GetLSForcePileCount());
    }
    @Test public void RapidDeploymentNewHandScoutCannotMoveButPreviouslyDeployedScoutCan() {
        var scn = DiscountScenario(); Deploy(scn, "delevar", "forest");
        scn.SkipToPhase(Phase.MOVE);
        assertFalse(scn.LSMoveAvailable(scn.GetLSCard("delevar")));
        assertTrue(scn.LSMoveAvailable(scn.GetLSCard("beezer")));
        scn.SkipToLSTurn(Phase.MOVE); scn.EnsureLSForcePile(4);
        assertTrue(scn.LSMoveAvailable(scn.GetLSCard("delevar")));
    }
    @Test public void RapidDeploymentNewHandScoutDoesNotParticipateInBattle() {
        var scn = DiscountScenario(); Deploy(scn, "delevar", "forest");
        scn.MoveCardsToLocation(scn.GetLSCard("forest"), scn.GetLSCard("luke"), scn.GetDSCard("trooper"));
        scn.SkipToPhase(Phase.BATTLE); scn.LSInitiateBattle(scn.GetLSCard("forest")); scn.PassAllResponses();
        assertFalse(scn.IsParticipatingInBattle(scn.GetLSCard("delevar")));
        assertTrue(scn.IsParticipatingInBattle(scn.GetLSCard("luke")));
    }
    @Test public void RapidDeploymentReserveScoutCostsPlusTwoAndMayMove() {
        var scn = GetScenario(); scn.StartGame(); var midge = scn.GetLSCard("midge");
        scn.MoveLocationToTable(scn.GetLSCard("forest")); scn.MoveLocationToTable(scn.GetLSCard("door"));
        scn.MoveCardsToLSHand(scn.GetLSCard("rapid"), midge); scn.EnsureLSForcePile(8); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(midge); int force = scn.GetLSForcePileCount();
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("rapid"), "Deploy Rebel scout from Reserve Deck");
        scn.LSChooseCard(midge); scn.LSChooseCard(scn.GetLSCard("forest")); scn.PassAllResponses();
        assertAtLocation(scn.GetLSCard("forest"), midge); assertEquals(force - 4, scn.GetLSForcePileCount());
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("rapid"));
        scn.SkipToPhase(Phase.MOVE); assertTrue(scn.LSMoveAvailable(midge));
    }
    @Test public void RapidDeploymentHandDiscountDoesNotDiscountScoutDeployedFromReserve() {
        var scn = GetScenario(); scn.StartGame(); var midge = scn.GetLSCard("midge");
        scn.MoveLocationToTable(scn.GetLSCard("forest")); scn.MoveLocationToTable(scn.GetLSCard("door"));
        scn.MoveCardsToLSHand(scn.GetLSCard("rapid"), scn.GetLSCard("covert"), midge); scn.EnsureLSForcePile(8);
        scn.SkipToDSTurn(Phase.MOVE); scn.MoveCardsToTopOfLSReserveDeck(midge); scn.DSPass();
        scn.LSPlayCard(scn.GetLSCard("rapid"), "Reduce Rebel scout deploy cost"); scn.LSChooseCard(scn.GetLSCard("forest")); scn.PassAllResponses();
        scn.DSPass(); int force = scn.GetLSForcePileCount();
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("covert"), "Deploy Rebel scout"); scn.LSChooseCard(midge);
        scn.LSChooseCard(scn.GetLSCard("forest")); scn.PassAllResponses();
        assertAtLocation(scn.GetLSCard("forest"), midge); assertEquals(force - 2, scn.GetLSForcePileCount());
    }
    private void CheckHandDeploy(String card, String site, int cost) {
        var scn = DiscountScenario(); int force = scn.GetLSForcePileCount();
        Deploy(scn, card, site); assertEquals(force - cost, scn.GetLSForcePileCount());
    }
    private VirtualTableScenario DiscountScenario() {
        var scn = GetScenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("forest")); scn.MoveLocationToTable(scn.GetLSCard("door"));
        scn.MoveCardsToLocation(scn.GetLSCard("forest"), scn.GetLSCard("beezer"));
        scn.MoveCardsToLSHand(scn.GetLSCard("rapid"), scn.GetLSCard("delevar"), scn.GetLSCard("luke"), scn.GetLSCard("alien"), scn.GetLSCard("midge"));
        scn.EnsureLSForcePile(12); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(scn.GetLSCard("rapid"), "Reduce Rebel scout deploy cost"); scn.LSChooseCard(scn.GetLSCard("forest")); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("rapid"));
        if (scn.AwaitingDSDeployPhaseActions()) scn.DSPass();
        return scn;
    }
    private void Deploy(VirtualTableScenario scn, String card, String site) {
        if (scn.AwaitingDSDeployPhaseActions()) scn.DSPass();
        scn.LSDeployCard(scn.GetLSCard(card)); scn.LSChooseCard(scn.GetLSCard(site)); scn.PassAllResponses();
        assertAtLocation(scn.GetLSCard(site), scn.GetLSCard(card));
    }

    @Test public void RapidDeploymentDiscountFollowsRetargetedSite() {
        var scn = GetScenario(); scn.StartGame(); var forest = scn.GetLSCard("forest"); var chasm = scn.GetLSStartingLocation();
        scn.MoveLocationToTable(forest); scn.MoveLocationToTable(scn.GetLSCard("door"));
        scn.MoveCardsToLSHand(scn.GetLSCard("rapid"), scn.GetLSCard("delevar"), scn.GetLSCard("midge"));
        scn.MoveCardsToDSHand(scn.GetDSCard("surprise")); scn.EnsureLSForcePile(10); scn.EnsureDSForcePile(5); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(scn.GetLSCard("rapid"), "Reduce Rebel scout deploy cost"); scn.LSChooseCard(forest); scn.LSPass();
        scn.DSPlayCard(scn.GetDSCard("surprise"), "Retarget");
        scn.DSChooseCard(forest);
        scn.DSChooseCard(chasm); scn.PassAllResponses();
        if (scn.AwaitingDSDeployPhaseActions()) scn.DSPass(); int force = scn.GetLSForcePileCount();
        Deploy(scn, "delevar", "forest"); assertEquals(force - 2, scn.GetLSForcePileCount());
        force = scn.GetLSForcePileCount(); Deploy(scn, "midge", "starting-location"); assertEquals(force, scn.GetLSForcePileCount());
    }
}
