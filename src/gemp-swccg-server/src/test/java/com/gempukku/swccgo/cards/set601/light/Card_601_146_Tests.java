package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.legacy.WatchYourStepTestSupport;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_601_146_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    putAll(WatchYourStepTestSupport.pulledCards());
                    put("luke", "1_19");
                    put("corellian", "2_6");
                    put("filler", "1_19");
                    put("filler2", "1_20");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                WatchYourStepTestSupport.WatchYourStepV,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void WatchYourStepNonCorellianWithAbilityPlacesCardFromHandToDeployToCorellia() {
        //test1: deploying Luke to a Corellia site first puts a chosen card from hand on bottom of Used Pile
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var filler = scn.GetLSCard("filler");
        var filler2 = scn.GetLSCard("filler2");
        var city = scn.GetLSCard("city");

        scn.StartGame();
        scn.MoveCardsToLSHand(luke, filler, filler2);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(luke));
        scn.LSDeployCard(luke);
        scn.LSChooseCard(city);
        assertTrue(scn.LSHasCardChoiceAvailable(filler));
        assertTrue(scn.LSHasCardChoiceAvailable(filler2));
        assertFalse(scn.LSHasCardChoiceAvailable(luke));
        scn.LSChooseCard(filler);
        scn.PassAllResponses();

        assertAtLocation(city, luke); //test1
        assertEquals(Zone.USED_PILE, filler.getZone()); //test1
        assertInHand(filler2); //test1
    }

    @Test
    public void WatchYourStepCorellianDeploysToCorelliaWithoutPlacingCard() {
        //test1: a Corellian deploys to Corellia without the extra cost
        var scn = GetScenario();
        var corellian = scn.GetLSCard("corellian");
        var filler = scn.GetLSCard("filler");
        var city = scn.GetLSCard("city");

        scn.StartGame();
        scn.MoveCardsToLSHand(corellian, filler);
        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.LSDeployCard(corellian);
        scn.LSChooseCard(city);
        scn.PassAllResponses();

        assertAtLocation(city, corellian); //test1
        assertInHand(filler); //test1
    }

    @Test
    public void WatchYourStepNonCorellianDeploysOutsideCorelliaWithoutPlacingCard() {
        //test1: the cost applies only to Corellia
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var filler = scn.GetLSCard("filler");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(luke, filler);
        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.LSDeployCard(luke);
        scn.LSChooseCard(dsSite);
        scn.PassAllResponses();

        assertAtLocation(dsSite, luke); //test1
        assertInHand(filler); //test1
    }

    @Test
    public void WatchYourStepNonCorellianMayNotDeployToCorelliaWithNoOtherCardInHand() {
        //test1: with no other card in hand, the deployment to Corellia fails before any Force is used
        //test2: Luke may still deploy elsewhere
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var city = scn.GetLSCard("city");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        for (var card : new java.util.ArrayList<>(scn.GetLSHand())) {
            scn.MoveCardsToTopOfLSUsedPile((com.gempukku.swccgo.game.PhysicalCardImpl) card);
        }
        scn.MoveCardsToLSHand(luke);
        scn.SkipToLSTurn(Phase.DEPLOY);

        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(luke);
        scn.LSChooseCard(city);
        assertInHand(luke); //test1
        assertEquals(force, scn.GetLSForcePileCount()); //test1

        scn.LSDeployCard(luke);
        scn.LSChooseCard(dsSite);
        scn.PassAllResponses();
        assertAtLocation(dsSite, luke); //test2
    }
}
