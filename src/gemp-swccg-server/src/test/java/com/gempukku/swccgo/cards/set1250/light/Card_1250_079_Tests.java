package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_1250_079_Tests {
    private VirtualTableScenario scenario() {
        HashMap<String, String> light = new HashMap<>();
        light.put("ship", "1250_79");
        light.put("otherShip", "1_140");
        light.put("system", "1_127");
        light.put("otherSystem", "1_126");
        light.put("senator", "1_17");
        light.put("pilot", "1_3");
        for (int index = 1; index <= 4; index++) {
            light.put("pilot" + index, "1116_8");
            light.put("passenger" + index, "1_28");
        }
        return new VirtualTableScenario(light, new HashMap<>() {{ put("destroyer", "1_301"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private boolean isSelectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        String[] ids = scn.GetADParam(scn.LS, "cardId");
        String identity = String.valueOf(card.getCardId());
        if (ids == null || (ids.length > 0 && ids[0].startsWith("temp"))) {
            ids = scn.GetADParam(scn.LS, "blueprintId");
            identity = card.getBlueprintId(true);
        }
        if (ids == null) {
            return false;
        }
        String[] selectable = scn.GetADParam(scn.LS, "selectable");
        for (int index = 0; index < ids.length; index++) {
            if (ids[index].equals(identity) && (selectable == null || Boolean.parseBoolean(selectable[index]))) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void AlderaanConsularShipMetadataMatchesOfficialImage() {
        var scn = scenario();
        var card = scn.GetLSCard("ship").getBlueprint();
        assertEquals("Alderaan Consular Ship", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(5, card.getPower(), scn.epsilon);
        assertEquals(5, card.getArmor(), scn.epsilon);
        assertEquals(3, card.getHyperspeed(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon);
        assertEquals(3, card.getPilotCapacity());
        assertEquals(4, card.getPassengerCapacity());
        assertEquals(CardSubtype.CAPITAL, card.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, card.getExpansionSet());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.STARSHIP); }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{ add(ModelType.CORELLIAN_CORVETTE); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP); add(Icon.LEGACY_BLOCK_5); add(Icon.DEATH_STAR_II);
            add(Icon.EPISODE_I); add(Icon.REPUBLIC); add(Icon.PILOT);
            add(Icon.NAV_COMPUTER); add(Icon.SCOMP_LINK);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.SHIP_DOCKING_CAPABILITY); }});
    }

    @Test
    public void AlderaanConsularShipPermanentPilotProvidesTwoAbility() {
        var scn = scenario();
        var ship = scn.GetLSCard("ship");
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("system"));
        scn.MoveCardsToLocation(scn.GetLSCard("system"), ship);
        assertEquals(2, scn.game().getModifiersQuerying().getHighestAbilityPiloting(
                scn.gameState(), ship, ship, true, false), scn.epsilon);
        assertEquals(5, scn.GetPower(ship));
    }

    @Test
    public void AlderaanConsularShipAcceptsNonRepublicPilotInThirdSlot() {
        checkPilotCapacity(false);
    }

    @Test
    public void AlderaanConsularShipRejectsFourthPilotWithFullPassengerCapacity() {
        checkPilotCapacity(true);
    }

    private void checkPilotCapacity(boolean full) {
        var scn = scenario();
        var ship = scn.GetLSCard("ship");
        var pilot = scn.GetLSCard("pilot");
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("system"));
        scn.MoveCardsToLocation(scn.GetLSCard("system"), ship);
        scn.BoardAsPilot(ship, scn.GetLSCard("pilot1"), scn.GetLSCard("pilot2"));
        if (full) {
            scn.BoardAsPilot(ship, scn.GetLSCard("pilot3"));
        }
        scn.BoardAsPassenger(ship, scn.GetLSCard("passenger1"), scn.GetLSCard("passenger2"),
                scn.GetLSCard("passenger3"), scn.GetLSCard("passenger4"));
        scn.MoveCardsToHand(pilot);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(pilot);
        assertEquals(!full, isSelectable(scn, ship));
        if (!full) {
            scn.LSChooseCard(ship);
            scn.PassAllResponses();
            assertTrue(scn.IsAboardAsPilot(ship, pilot));
        }
    }

    @Test
    public void AlderaanConsularShipOpponentPaysThreeForceToInitiateBattleHere() {
        checkBattleCost(true, true);
    }

    @Test
    public void AlderaanConsularShipDoesNotTaxOpponentAtOtherSystem() {
        checkBattleCost(true, false);
    }

    @Test
    public void AlderaanConsularShipDoesNotTaxOwnersBattle() {
        checkBattleCost(false, true);
    }

    private void checkBattleCost(boolean darkInitiates, boolean sameSystem) {
        var scn = scenario();
        var system = scn.GetLSCard("system");
        var otherSystem = scn.GetLSCard("otherSystem");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveLocationToTable(otherSystem);
        scn.MoveCardsToLocation(system, scn.GetLSCard("ship"));
        var battleSystem = sameSystem ? system : otherSystem;
        scn.MoveCardsToLocation(battleSystem, scn.GetLSCard("otherShip"), scn.GetDSCard("destroyer"));
        if (darkInitiates) {
            scn.SkipToDSTurn(Phase.BATTLE);
            int before = scn.GetDSForcePileCount();
            scn.DSInitiateBattle(battleSystem);
            assertEquals(before - (sameSystem ? 3 : 1), scn.GetDSForcePileCount());
        }
        else {
            scn.SkipToLSTurn(Phase.BATTLE);
            int before = scn.GetLSForcePileCount();
            scn.LSInitiateBattle(battleSystem);
            assertEquals(before - 1, scn.GetLSForcePileCount());
        }
    }

    @Test
    public void AlderaanConsularShipDeploysSenatorAboardPayingCostOnlyOncePerGame() {
        var scn = scenario();
        var system = scn.GetLSCard("system");
        var ship = scn.GetLSCard("ship");
        var otherShip = scn.GetLSCard("otherShip");
        var senator = scn.GetLSCard("senator");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, ship, otherShip);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(senator, scn.GetLSCard("pilot"));
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(ship, "Deploy a senator aboard");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        assertTrue("Expected senator selection; awaiting " + (scn.LSGetDecision() == null ? "none" : scn.LSGetDecision().getText()), isSelectable(scn, senator));
        assertFalse(isSelectable(scn, scn.GetLSCard("pilot")));
        scn.LSChooseCard(senator);
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        assertTrue(scn.IsAboardAsPassenger(ship, senator));
        assertEquals(force - 3, scn.GetLSForcePileCount());
        assertFalse(scn.GetLSReserveDeck().contains(senator));
        assertFalse(scn.LSCardActionAvailable(ship, "Deploy a senator aboard"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(ship, "Deploy a senator aboard"));
    }
}
