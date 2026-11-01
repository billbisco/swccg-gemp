package com.gempukku.swccgo.cards.set1115.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1115_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hls", "1115_22");
                    put("hls2", "1115_22");
                    put("xwing", "1_146");
                    put("incom", "7_66");
                    put("koensayr", "7_67");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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

    @Test
    public void RebelCellHiddenLandingSiteStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("hls").getBlueprint();

        assertEquals("Rebel Cell - Hidden Landing Site", card.getTitle());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.TATOOINE);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
}});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private void deployHlsAndReturnToLsDeploy(VirtualTableScenario scn, PhysicalCardImpl hls) {
        assertTrue(scn.LSDeployAvailable(hls));
        scn.LSDeployLocation(hls);
        scn.PassAllResponses();
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
    }

    @Test
    public void RebelCellHiddenLandingSiteDeploysToTatooine() {
        var scn = GetScenario();
        var hls = scn.GetLSCard("hls");

        scn.StartGame();
        scn.MoveCardsToLSHand(hls);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(hls));
        scn.LSDeployLocation(hls);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, hls.getZone());
        assertEquals("Tatooine", hls.getPartOfSystem());
    }

    @Test
    public void RebelCellHiddenLandingSiteYourStarfightersMayDeployHere() {
        var scn = GetScenario();
        var hls = scn.GetLSCard("hls");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveCardsToLSHand(hls, xwing);
        scn.SkipToLSTurn(Phase.DEPLOY);
        deployHlsAndReturnToLsDeploy(scn, hls);

        assertTrue(scn.LSDeployAvailable(xwing));
        scn.LSDeployCard(xwing);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.LSHasCardChoiceAvailable(hls));
        scn.LSChooseCard(hls);
        scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION, xwing.getZone());
        assertEquals(hls, xwing.getAtLocation());
    }

    @Test
    public void RebelCellHiddenLandingSiteSecondCopyMayNotDeploy() {
        var scn = GetScenario();
        var hls = scn.GetLSCard("hls");
        var hls2 = scn.GetLSCard("hls2");

        scn.StartGame();
        scn.MoveCardsToLSHand(hls, hls2);
        scn.SkipToLSTurn(Phase.DEPLOY);

        deployHlsAndReturnToLsDeploy(scn, hls);
        assertFalse(scn.LSDeployAvailable(hls2));
    }

    @Test
    public void RebelCellHiddenLandingSiteDeploysKoensayrManufacturingHere() {
        var scn = GetScenario();
        var hls = scn.GetLSCard("hls");
        var koensayr = scn.GetLSCard("koensayr");

        scn.StartGame();
        scn.MoveCardsToLSHand(hls, koensayr);
        scn.SkipToLSTurn(Phase.DEPLOY);
        deployHlsAndReturnToLsDeploy(scn, hls);

        assertFalse(scn.LSCardActionAvailable(hls, "Deploy Incom Corporation from Reserve Deck"));
        assertTrue(scn.LSDeployAvailable(koensayr));
        scn.LSDeployCard(koensayr);
        scn.LSChooseCard(hls);
        scn.PassAllResponses();
        assertEquals(Zone.ATTACHED, koensayr.getZone());
        assertEquals(hls, koensayr.getAttachedTo());
    }

    @Test
    public void RebelCellHiddenLandingSiteStarfightersDeployLandedForMinus1Force() {
        var scn = GetScenario();
        var hls = scn.GetLSCard("hls");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveCardsToLSHand(hls, xwing);
        scn.SkipToLSTurn(Phase.DEPLOY);
        deployHlsAndReturnToLsDeploy(scn, hls);

        int forceBefore = scn.GetLSForcePileCount();
        assertTrue(scn.LSDeployAvailable(xwing));
        scn.LSDeployCard(xwing);
        scn.LSChooseCard(hls);
        scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION, xwing.getZone());
        assertEquals(hls, xwing.getAtLocation());
        assertEquals(forceBefore - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void RebelCellHiddenLandingSiteHasNoIncomReserveDownload() {
        var scn = GetScenario();
        var hls = scn.GetLSCard("hls");
        var incom = scn.GetLSCard("incom");

        scn.StartGame();
        scn.MoveCardsToLSHand(hls);
        scn.SkipToLSTurn(Phase.DEPLOY);
        deployHlsAndReturnToLsDeploy(scn, hls);

        scn.MoveCardsToTopOfLSReserveDeck(incom);
        scn.EnsureLSForcePile(2);
        assertFalse(scn.LSCardActionAvailable(hls, "Deploy Incom Corporation from Reserve Deck"));
    }

    @Test
    public void RebelCellHiddenLandingSiteDoesNotDeployToHoth() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("hls", "1115_22");
                    put("hoth", "3_55");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
        var hls = scn.GetLSCard("hls");
        var hoth = scn.GetLSCard("hoth");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLSHand(hls);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(hls));
        scn.LSDeployLocation(hls);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, hls.getZone());
        assertEquals("Tatooine", hls.getPartOfSystem());
        assertFalse("Hoth".equals(hls.getPartOfSystem()));
    }

    @Test
    public void RebelCellHiddenLandingSiteImmuneToNoEscape() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("hls", "1115_22");
                }},
                new HashMap<>() {{
                    put("premium", "112_16");
                    put("shield", "13_78");
                    put("trooper", "1_194");
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
        var hls = scn.GetLSCard("hls");
        var premium = scn.GetDSCard("premium");
        var shield = scn.GetDSCard("shield");

        scn.StartGame();
        scn.MoveCardsToLSHand(hls);
        scn.SkipToLSTurn(Phase.DEPLOY);
        deployHlsAndReturnToLsDeploy(scn, hls);
        scn.MoveCardsToDSSideOfTable(premium, shield);

        assertFalse(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), hls, premium));
        assertFalse(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), hls, shield));
    }

    @Test
    public void RebelCellHiddenLandingSiteImmuneToRevolution() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("hls", "1115_22");
                    put("revolution", "1_62");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
        var hls = scn.GetLSCard("hls");
        var revolution = scn.GetLSCard("revolution");

        scn.StartGame();
        scn.MoveCardsToLSHand(hls);
        scn.SkipToLSTurn(Phase.DEPLOY);
        deployHlsAndReturnToLsDeploy(scn, hls);
        scn.MoveCardsToLSSideOfTable(revolution);

        assertFalse(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), hls, revolution));
    }

    @Test
    public void RebelCellHiddenLandingSiteDeploysIncomCorporationFromHand() {
        var scn = GetScenario();
        var hls = scn.GetLSCard("hls");
        var incom = scn.GetLSCard("incom");

        scn.StartGame();
        scn.MoveCardsToLSHand(hls, incom);
        scn.SkipToLSTurn(Phase.DEPLOY);
        deployHlsAndReturnToLsDeploy(scn, hls);

        assertTrue(scn.LSDeployAvailable(incom));
        scn.LSDeployCard(incom);
        scn.LSChooseCard(hls);
        scn.PassAllResponses();
        assertEquals(Zone.ATTACHED, incom.getZone());
        assertEquals(hls, incom.getAttachedTo());
    }

    @Test
    public void RebelCellHiddenLandingSiteStarfightersTakeOffAndLandForFree() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("hls", "1115_22");
                    put("xwing", "1_146");
                    put("tat", "1_127");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
        var hls = scn.GetLSCard("hls");
        var xwing = scn.GetLSCard("xwing");
        var tat = scn.GetLSCard("tat");
        var farm = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(tat);
        scn.MoveLocationToTable(hls);
        scn.MoveCardsToLocation(hls, xwing);

        float takeOffFromHls = scn.game().getModifiersQuerying().getTakeOffCost(scn.gameState(), xwing, hls, tat, false, 0);
        float takeOffFromFarm = scn.game().getModifiersQuerying().getTakeOffCost(scn.gameState(), xwing, farm, tat, false, 0);
        float landToHls = scn.game().getModifiersQuerying().getLandingCost(scn.gameState(), xwing, tat, hls, false, 0);
        float landToFarm = scn.game().getModifiersQuerying().getLandingCost(scn.gameState(), xwing, tat, farm, false, 0);
        assertEquals(0, Math.round(takeOffFromHls));
        assertEquals(1, Math.round(takeOffFromFarm));
        assertEquals(0, Math.round(landToHls));
        assertEquals(1, Math.round(landToFarm));
    }

    @Test
    public void RebelCellHiddenLandingSiteDoesNotDeployToSubjugatedPlanet() {
        var scn = GetScenario();
        var hls = scn.GetLSCard("hls");

        scn.StartGame();
        scn.gameState().setSubjugatedPlanet("Tatooine");
        scn.MoveCardsToLSHand(hls);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSDeployAvailable(hls));
    }
}
