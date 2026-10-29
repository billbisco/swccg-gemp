package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.TitleComponent;
import java.util.List;
import java.util.Map;

import com.gempukku.swccgo.common.CardSubtype;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_15_072_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("flyCasual","15_72");
                    put("tydirium", "8_79");
                    put("pilot", "1_027"); //rebel pilot
                    put("pilot2", "1_027"); //rebel pilot
                    put("falcon", "1_143");
                    put("xwing", "1_146");
                    put("ywing", "1_147");
                    put("homeOne", "9_074");
                    put("hoth_db","3_059"); //hoth: echo docking bay
                }},
                new HashMap<>()
                {{
                    put("warning", "8_122");
                    put("deathstar","2_143"); //Careful Planning
                }},
                10,
                10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test public void CorellianRetortAndFlyCasualStatsAndKeywordsAreCorrect() {
        var scn=GetScenario();var card=scn.GetLSCard("flyCasual").getBlueprint();
        assertEquals("Corellian Retort & Fly Casual",card.getTitle());assertEquals(List.of(new TitleComponent("Corellian Retort",Uniqueness.UNIQUE),new TitleComponent("Fly Casual",Uniqueness.UNIQUE)),card.getTitleComponents());
        assertEquals(5f,card.getDestiny(),scn.epsilon);assertEquals(CardSubtype.USED_OR_LOST,card.getCardSubtype());assertEquals(0,card.getCredits());assertFalse(card.isHorizontal());assertEquals("",card.getLore());assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());
        scn.BlueprintCardTypeCheck(card,List.of(CardType.INTERRUPT));scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of());scn.BlueprintPersonaCheck(card,List.of());
    }
@Test
    public void CorellianRetortAndFlyCasualUsedDeploysOnePilotedStarshipToSystemDuringDeploy() {
        //test1: cannot choose unpiloted starship (if no pilot available for simultaneous deploy)
        //test2: can choose piloted starship
        //test3: can choose system with icons
        //test4: can choose system without icons/presence
        //test5: cannot choose non-system (docking bay site)
        //test6: deploys for -1
        var scn = GetScenario();

        var flyCasual = scn.GetLSCard("flyCasual");
        var ywing = scn.GetLSCard("ywing");
        var xwing = scn.GetLSCard("xwing");
        var falcon = scn.GetLSCard("falcon");
        var system = scn.GetLSStartingLocation();
        var hoth_db = scn.GetLSCard("hoth_db");

        var deathstar = scn.GetDSCard("deathstar");

        scn.StartGame();

        scn.MoveCardsToLSHand(flyCasual, ywing, xwing, falcon);
        scn.MoveLocationToTable(deathstar);
        scn.MoveLocationToTable(hoth_db);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(flyCasual,"starship or pilot");
        scn.PassAllResponses();

        assertFalse(scn.LSHasCardChoiceAvailable(falcon)); //test1
        assertTrue(scn.LSHasCardChoiceAvailable(ywing)); //test2
        assertTrue(scn.LSHasCardChoiceAvailable(xwing)); //test2
        scn.LSChooseCard(xwing);

        assertTrue(scn.LSHasCardChoiceAvailable(system)); //test3
        assertTrue(scn.LSHasCardChoiceAvailable(deathstar)); //test4
        assertFalse(scn.LSHasCardChoiceAvailable(hoth_db)); //test5
        scn.LSChooseCard(deathstar);

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertEquals(Zone.TOP_OF_USED_PILE,flyCasual.getZone());
        assertEquals(2, scn.GetLSUsedPileCount()); //test6 (xwing deploy for 1, fly casual to used pile)
        assertTrue(scn.CardsAtLocation(deathstar, xwing));
    }
@Test
    public void CorellianRetortAndFlyCasualUsedDeploysOnePilotToSystem() {
        //shows fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/987
        //test1: cannot choose non-pilot character
        //test2: can choose pilot character
        //test3: can deploy to a piloted starship at a system with capacity
        //test4: can deploy to an unpiloted starship at a system without presence/icons
        //test5: cannot deploy to a starship at a non-system (docking bay) with capacity
        //test6: cannot deploy to a non-system (site)
        //test7: cannot deploy to a starship at a system that does not have capacity
        //test8: can deploy as pilot or passenger
        //test9: deploys for normal deploy cost
        var scn = GetScenario();

        var flyCasual = scn.GetLSCard("flyCasual");
        var pilot = scn.GetLSCard("pilot");
        var pilot2 = scn.GetLSCard("pilot2");
        var ywing = scn.GetLSCard("ywing");
        var xwing = scn.GetLSCard("xwing");
        var homeOne = scn.GetLSCard("homeOne");
        var falcon = scn.GetLSCard("falcon");
        var system = scn.GetLSStartingLocation();
        var hoth_db = scn.GetLSCard("hoth_db");
        var trooper = scn.GetLSFiller(1);

        var deathstar = scn.GetDSCard("deathstar");
        var system2 = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(flyCasual, pilot, pilot2, trooper);
        scn.MoveLocationToTable(deathstar);
        scn.MoveLocationToTable(hoth_db);

        scn.MoveCardsToLocation(deathstar, falcon);
        scn.MoveCardsToLocation(system, homeOne);
        scn.MoveCardsToLocation(system2, xwing);
        scn.MoveCardsToLocation(hoth_db, ywing);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(flyCasual,"starship or pilot");
        scn.PassAllResponses();

        assertFalse(scn.LSHasCardChoiceAvailable(trooper)); //test1
        assertTrue(scn.LSHasCardChoiceAvailable(pilot)); //test2
        assertTrue(scn.LSHasCardChoiceAvailable(pilot2)); //test2
        scn.LSChooseCard(pilot);

        assertTrue(scn.LSHasCardChoiceAvailable(homeOne)); //test3
        assertTrue(scn.LSHasCardChoiceAvailable(falcon)); //test4
        assertFalse(scn.LSHasCardChoiceAvailable(ywing)); //test5
        assertFalse(scn.LSHasCardChoiceAvailable(hoth_db)); //test6
        assertFalse(scn.LSHasCardChoiceAvailable(xwing)); //test7
        scn.LSChooseCard(falcon);

        assertTrue(scn.LSChoiceAvailable("Pilot"));
        assertTrue(scn.LSChoiceAvailable("Passenger"));
        scn.LSChoose("Passenger");

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertEquals(Zone.TOP_OF_USED_PILE,flyCasual.getZone());
        assertEquals(3, scn.GetLSUsedPileCount()); //test8 (pilot deploy for 2, fly casual to used pile)
        assertTrue(scn.CardsAtLocation(deathstar, falcon));
        assertTrue(scn.IsAboardAsPassenger(falcon, pilot));
    }
@Test
    public void CorellianRetortAndFlyCasualUsedDeploysOneShipWithSimultaneousDeployToSystem() {
        //test1: can choose an unpiloted ship
        //test2: can deploy to a system without presence/icons
        //test3: simultaneous deploy of a pilot required
        //test4: ship deploys for -1, pilot deploys at normal cost
        var scn = GetScenario();

        var flyCasual = scn.GetLSCard("flyCasual");
        var pilot = scn.GetLSCard("pilot");
        var falcon = scn.GetLSCard("falcon");
        var trooper = scn.GetLSFiller(1);

        var deathstar = scn.GetDSCard("deathstar");

        scn.StartGame();

        scn.MoveCardsToLSHand(flyCasual, falcon, pilot, trooper);
        scn.MoveLocationToTable(deathstar);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(1);
        assertEquals(4, scn.GetLSForcePileCount()); //4 = enough for falcon (3-1) and pilot (2)

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(flyCasual,"starship or pilot");
        scn.PassAllResponses();

        //falcon automatically selected, as only option
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy")); //test1
        assertTrue(scn.LSHasCardChoiceAvailable(deathstar)); //test2
        scn.LSChooseCard(deathstar);

        //pilot automatically selected for simultaneous deployment, as only option

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertEquals(Zone.TOP_OF_USED_PILE,flyCasual.getZone());
        assertEquals(5, scn.GetLSUsedPileCount()); //test4
        assertTrue(scn.CardsAtLocation(deathstar, falcon));
        assertTrue(scn.IsAboardAsPilot(falcon, pilot));
    }
@Test public void CorellianRetortAndFlyCasualLostTakesTydiriumFromReserveDeck() {
        var scn = GetScenario();
        var card = scn.GetLSCard("flyCasual");
        var ship = scn.GetLSCard("tydirium");
        scn.StartGame();
        scn.MoveCardsToHand(card);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(ship);
        scn.LSPlayCard(card, "Take card");
        scn.PassAllResponses();
        scn.LSChooseCard(ship);
        scn.PassAllResponses();
        assertTrue(scn.GetLSHand().contains(ship));
        assertTrue(scn.GetLSLostPile().contains(card));
    }
    @Test public void CorellianRetortAndFlyCasualCannotDeployOutsideOwnDeployPhase() {
        var scn=GetScenario();scn.StartGame();scn.MoveCardsToHand(scn.GetLSCard("flyCasual"),scn.GetLSCard("xwing"));scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("flyCasual"),"starship or pilot"));
        scn.SkipToPhase(Phase.DEPLOY);assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("flyCasual"),"starship or pilot"));
    }
    private void battle(boolean jabba) {
        var scn=new VirtualTableScenario(new HashMap<>(Map.of("combo","15_72","han","1_11","luke","1_19")),new HashMap<>(Map.of("enemy",jabba?"6_109":"5_91")));
        var combo=scn.GetLSCard("combo");var site=scn.GetDSStartingLocation();scn.StartGame();scn.MoveCardsToHand(combo);scn.MoveCardsToLocation(site,scn.GetLSCard("han"),scn.GetLSCard("luke"),scn.GetDSCard("enemy"));scn.SkipToLSTurn(Phase.BATTLE);scn.LSInitiateBattle(site);scn.PassAllResponses();
        assertEquals(1,scn.GetLSBattleDestinyCount());scn.LSPlayCard(combo,jabba?"Add two":"Add one");scn.PassAllResponses();assertEquals(jabba?3:2,scn.GetLSBattleDestinyCount());assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void CorellianRetortAndFlyCasualAddsOneBattleDestinyWithBountyHunterAndHan() {battle(false);}
    @Test public void CorellianRetortAndFlyCasualAddsTwoBattleDestinyWithJabbaAndHan() {battle(true);}
}
