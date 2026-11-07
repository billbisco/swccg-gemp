package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_056_Tests {
    private VirtualTableScenario scenario() {
        HashMap<String, String> light = new HashMap<>();
        light.put("yarua", "1250_56"); light.put("wookiee", "8_2"); light.put("rebel", "1_28");
        light.put("forest", "1116_16"); light.put("haven", "1116_17");
        light.put("corridor", "5_82"); light.put("dagobah1", "4_85"); light.put("dagobah2", "4_86");
        light.put("nudj1", "4_5"); light.put("nudj2", "4_5");
        light.put("ship", "1115_26"); light.put("system", "1116_15");
        for (int index = 0; index < 9; index++) light.put("hand" + index, "1_28");
        return new VirtualTableScenario(light, new HashMap<>() {{
            put("snoova", "10_48"); put("sleen", "4_111");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void YaruaMetadataMatchesFinalPrinted55EImage() {
        var scn = scenario();
        var card = scn.GetLSCard("yarua").getBlueprint();
        assertEquals("Yarua", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getPolitics(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, card.getExpansionSet());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.REPUBLIC); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC); add(Icon.CORUSCANT); add(Icon.EPISODE_I);
            add(Icon.WARRIOR); add(Icon.LEGACY_BLOCK_5);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.SENATOR); }});
    }

    @Test
    public void YaruaHasTaxationButNotUnderlyingJusticeAgenda() {
        var scn = scenario();
        var yarua = scn.GetLSCard("yarua");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), yarua);
        assertEquals(List.of(Agenda.TAXATION), scn.game().getModifiersQuerying().getAgendas(scn.gameState(), yarua));
    }

    @Test
    public void YaruaPaysPrintedFourForceWhenDeployingToKashyyyk() {
        var scn = scenario();
        var yarua = scn.GetLSCard("yarua");
        var forest = scn.GetLSCard("forest");
        scn.StartGame();
        scn.MoveLocationToTable(forest);
        scn.MoveCardsToHand(yarua);
        scn.LSActivateForceCheat(8);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(yarua);
        scn.LSChooseCard(forest);
        scn.PassAllResponses();
        assertEquals(forest, yarua.getAtLocation());
        assertEquals(force - 4, scn.GetLSForcePileCount());
    }

    @Test
    public void YaruaDeploymentLosesAllNudjsAtDifferentSitesAndLeavesOtherCreature() {
        var scn = scenario();
        var yarua = scn.GetLSCard("yarua");
        var nudj1 = scn.GetLSCard("nudj1");
        var nudj2 = scn.GetLSCard("nudj2");
        var sleen = scn.GetDSCard("sleen");
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("dagobah1"));
        scn.MoveLocationToTable(scn.GetLSCard("dagobah2"));
        scn.MoveCardsToLocation(scn.GetLSCard("dagobah1"), nudj1, sleen);
        scn.MoveCardsToLocation(scn.GetLSCard("dagobah2"), nudj2);
        scn.MoveCardsToHand(yarua);
        scn.LSActivateForceCheat(8);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.GetLSLostPile().contains(nudj1));
        assertFalse(scn.GetLSLostPile().contains(nudj2));
        scn.LSDeployCard(yarua);
        scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose card to be lost")) {
            scn.LSChooseCard(nudj1);
            scn.PassAllResponses();
        }
        if (scn.LSDecisionAvailable("Choose card to be lost")) {
            scn.LSChooseCard(nudj2);
            scn.PassAllResponses();
        }
        assertTrue(scn.GetLSLostPile().contains(nudj1));
        assertTrue(scn.GetLSLostPile().contains(nudj2));
        assertEquals(scn.GetLSCard("dagobah1"), sleen.getAtLocation());
    }

    @Test
    public void YaruaAlsoLosesNudjThatDeploysAfterHim() {
        var scn = scenario();
        var nudj = scn.GetLSCard("nudj1");
        var dagobah = scn.GetLSCard("dagobah1");
        scn.StartGame();
        scn.MoveLocationToTable(dagobah);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("yarua"));
        scn.MoveCardsToHand(nudj);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(nudj);
        scn.LSChooseCard(dagobah);
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(nudj));
    }

    @Test
    public void YaruaEndDeployDoesNotLoseForceWithSevenCards() {
        checkEndDeploy(7, true, true);
    }

    @Test
    public void YaruaEndDeployLosesOneForceWithExactlyEightCards() {
        checkEndDeploy(8, true, true);
    }

    @Test
    public void YaruaEndDeployLosesOneForceWithNineCards() {
        checkEndDeploy(9, true, true);
    }

    @Test
    public void YaruaAloneDoesNotCountHimselfAsWithAWookiee() {
        checkEndDeploy(8, false, true);
    }

    @Test
    public void YaruaWithWookieeAwayFromKashyyykDoesNotLoseForce() {
        checkEndDeploy(8, true, false);
    }

    @Test
    public void YaruaMayBeWithOpponentsWookieeWithoutControllingTheSite() {
        var scn = scenario();
        var forest = scn.GetLSCard("forest");
        scn.StartGame();
        scn.MoveLocationToTable(forest);
        scn.MoveCardsToLocation(forest, scn.GetLSCard("yarua"), scn.GetDSCard("snoova"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        setHand(scn, 8);
        int lifeForce = scn.GetDSLifeForceRemaining();
        scn.LSPass();
        scn.DSPass();
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetTopOfDSForcePile());
        scn.PassAllResponses();
        assertEquals(lifeForce - 1, scn.GetDSLifeForceRemaining());
        assertEquals(Phase.BATTLE, scn.GetCurrentPhase());
    }

    private void setHand(VirtualTableScenario scn, int size) {
        for (var card : new ArrayList<>(scn.GetLSHand())) {
            scn.MoveCardsToTopOfLSReserveDeck((PhysicalCardImpl) card);
        }
        for (int index = 0; index < size; index++) scn.MoveCardsToHand(scn.GetLSCard("hand" + index));
        assertEquals(size, scn.GetLSHandCount());
    }

    private void checkEndDeploy(int handSize, boolean withWookiee, boolean onKashyyyk) {
        var scn = scenario();
        var forest = scn.GetLSCard("forest");
        scn.StartGame();
        scn.MoveLocationToTable(forest);
        var site = onKashyyyk ? forest : scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("yarua"));
        if (withWookiee) scn.MoveCardsToLocation(site, scn.GetLSCard("wookiee"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        setHand(scn, handSize);
        int lifeForce = scn.GetDSLifeForceRemaining();
        scn.LSPass();
        scn.DSPass();
        scn.PassAllResponses();
        boolean losesForce = handSize >= 8 && withWookiee && onKashyyyk;
        if (losesForce) {
            scn.DSChooseCard(scn.GetTopOfDSForcePile());
            scn.PassAllResponses();
        }
        assertEquals(lifeForce - (losesForce ? 1 : 0), scn.GetDSLifeForceRemaining());
        assertEquals(Phase.BATTLE, scn.GetCurrentPhase());
    }

    @Test
    public void YaruaEndDeployDoesNotApplyDuringOpponentsTurn() {
        var scn = scenario();
        var forest = scn.GetLSCard("forest");
        scn.StartGame();
        scn.MoveLocationToTable(forest);
        scn.MoveCardsToLocation(forest, scn.GetLSCard("yarua"), scn.GetLSCard("wookiee"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        setHand(scn, 8);
        int lifeForce = scn.GetDSLifeForceRemaining();
        scn.DSPass();
        scn.LSPass();
        scn.PassAllResponses();
        assertEquals(lifeForce, scn.GetDSLifeForceRemaining());
        assertEquals(Phase.BATTLE, scn.GetCurrentPhase());
    }

    @Test
    public void YaruaAboardShipAtKashyyykIsNotOnThePlanet() {
        var scn = scenario();
        var ship = scn.GetLSCard("ship");
        var system = scn.GetLSCard("system");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, ship);
        scn.BoardAsPassenger(ship, scn.GetLSCard("yarua"), scn.GetLSCard("wookiee"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        setHand(scn, 8);
        int lifeForce = scn.GetDSLifeForceRemaining();
        scn.LSPass();
        scn.DSPass();
        scn.PassAllResponses();
        assertEquals(lifeForce, scn.GetDSLifeForceRemaining());
        assertEquals(Phase.BATTLE, scn.GetCurrentPhase());
    }

    @Test
    public void YaruaYourWookieeMovesForFreeUsingLandspeedOnKashyyyk() {
        checkMovement(true, true, false);
    }

    @Test
    public void YaruaNonWookieeStillPaysForLandspeedOnKashyyyk() {
        checkMovement(true, false, false);
    }

    @Test
    public void YaruaWookieeAwayFromKashyyykStillPaysForLandspeed() {
        checkMovement(false, true, false);
    }

    @Test
    public void YaruaOpponentWookieeStillPaysForLandspeedOnKashyyyk() {
        checkMovement(true, true, true);
    }

    @Test
    public void YaruaWookieeOnKashyyykStillPaysToShuttle() {
        var scn = scenario();
        var forest = scn.GetLSCard("forest");
        var ship = scn.GetLSCard("ship");
        var system = scn.GetLSCard("system");
        var wookiee = scn.GetLSCard("wookiee");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveLocationToTable(forest);
        scn.MoveCardsToLocation(system, ship);
        scn.MoveCardsToLocation(forest, scn.GetLSCard("yarua"), wookiee);
        scn.SkipToLSTurn(Phase.MOVE);
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(wookiee, "Shuttle");
        scn.LSChooseCard(ship);
        scn.PassAllResponses();
        assertTrue(scn.IsAboardAsPassenger(ship, wookiee));
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    private void checkMovement(boolean onKashyyyk, boolean wookiee, boolean dark) {
        var scn = scenario();
        scn.StartGame();
        var from = onKashyyyk ? scn.GetLSCard("forest") : scn.GetLSStartingLocation();
        var to = scn.GetLSCard(onKashyyyk ? "haven" : "corridor");
        if (onKashyyyk) scn.MoveLocationToTable(from);
        scn.MoveLocationToTable(to);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("yarua"));
        var mover = dark ? scn.GetDSCard("snoova") : scn.GetLSCard(wookiee ? "wookiee" : "rebel");
        scn.MoveCardsToLocation(from, mover);
        if (dark) {
            scn.SkipToDSTurn(Phase.MOVE);
            int force = scn.GetDSForcePileCount();
            scn.DSMoveCard(mover, to);
            scn.PassAllResponses();
            assertEquals(force - 1, scn.GetDSForcePileCount());
        }
        else {
            scn.SkipToLSTurn(Phase.MOVE);
            int force = scn.GetLSForcePileCount();
            scn.LSMoveCard(mover, to);
            scn.PassAllResponses();
            assertEquals(force - (onKashyyyk && wookiee ? 0 : 1), scn.GetLSForcePileCount());
        }
        assertEquals(to, mover.getAtLocation());
    }
}
