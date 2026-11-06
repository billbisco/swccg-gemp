package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1230_116_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("cruiser", "1230_116"); put("other", "1_140"); put("han", "1_11");
        }}, new HashMap<>() {{
            for (int i = 1; i <= 6; i++) put("tie" + i, "1_304");
            put("independent", "5_177"); put("bay", "7_276"); put("vader", "1_168");
        }}, 30, 30, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
        StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
        StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void MonCalamariStarCruiserStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario(); var card = scn.GetLSCard("cruiser").getBlueprint();
        assertEquals("Mon Calamari Star Cruiser", card.getTitle()); assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness()); assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon); assertEquals(8, card.getDeployCost(), scn.epsilon);
        assertEquals(7, card.getPower(), scn.epsilon); assertEquals(5, card.getArmor(), scn.epsilon);
        assertEquals(3, card.getHyperspeed(), scn.epsilon); assertEquals(9, card.getForfeit(), scn.epsilon);
        assertEquals(CardSubtype.CAPITAL, card.getCardSubtype());
        assertEquals(5, card.getPilotCapacity()); assertEquals(6, card.getPassengerCapacity());
        assertEquals(1, card.getVehicleCapacity()); assertEquals(3, card.getStarfighterOrTIECapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.STARSHIP); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP); add(Icon.PILOT); add(Icon.NAV_COMPUTER); add(Icon.SCOMP_LINK);
            add(Icon.DEATH_STAR_II); add(Icon.LEGACY_BLOCK_3);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.SHIP_DOCKING_CAPABILITY); }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{ add(ModelType.MON_CALAMARI_STAR_CRUISER); }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet()); assertEquals(Rarity.V, card.getRarity());
    }
    @Test public void MonCalamariStarCruiserDeploysForEightWithNoImperialShips() { CheckCost(0, 8); }
    @Test public void MonCalamariStarCruiserDeploysForSevenWithOneImperialShip() { CheckCost(1, 7); }
    @Test public void MonCalamariStarCruiserDeploysForThreeWithFiveImperialShips() { CheckCost(5, 3); }
    @Test public void MonCalamariStarCruiserDoesNotDeployBelowThreeWithSixImperialShips() { CheckCost(6, 3); }
    @Test public void MonCalamariStarCruiserDoesNotCountIndependentShipsOrImperialCharacters() {
        var scn = GetScenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("bay"));
        scn.MoveCardsToLocation(scn.GetDSCard("bay"), scn.GetDSCard("independent"), scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("other"));
        scn.MoveCardsToLSHand(scn.GetLSCard("cruiser")); scn.EnsureLSForcePile(10); scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(8, scn.GetDeployCost(scn.GetLSCard("cruiser")));
        int force = scn.GetLSForcePileCount(); scn.LSDeployCard(scn.GetLSCard("cruiser"));
        scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses();
        assertEquals(force - 8, scn.GetLSForcePileCount()); assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("cruiser"));
    }
    @Test public void MonCalamariStarCruiserHasPermanentPilotAbilityTwo() {
        var scn = GetScenario(); scn.StartGame(); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("cruiser"));
        assertEquals(2, scn.game().getModifiersQuerying().getAbility(scn.gameState(), scn.GetLSCard("cruiser"), true), scn.epsilon);
        scn.SkipToLSTurn(Phase.MOVE); assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("cruiser"), "Move"));
    }
    @Test public void MonCalamariStarCruiserDiscountUpdatesWhenImperialShipLeavesTable() {
        var scn = GetScenario(); scn.StartGame(); scn.MoveCardsToLSHand(scn.GetLSCard("cruiser"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("tie1"));
        assertEquals(7, scn.GetDeployCost(scn.GetLSCard("cruiser")));
        scn.MoveCardsToDSHand(scn.GetDSCard("tie1")); assertEquals(8, scn.GetDeployCost(scn.GetLSCard("cruiser")));
    }
    private void CheckCost(int ships, int expected) {
        var scn = GetScenario(); scn.StartGame();
        for (int i = 1; i <= ships; i++) scn.MoveCardsToLocation(i % 2 == 0 ? scn.GetLSStartingLocation() : scn.GetDSStartingLocation(), scn.GetDSCard("tie" + i));
        scn.MoveCardsToLSHand(scn.GetLSCard("cruiser")); scn.EnsureLSForcePile(12); scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(expected, scn.GetDeployCost(scn.GetLSCard("cruiser")));
        int force = scn.GetLSForcePileCount(); scn.LSDeployCard(scn.GetLSCard("cruiser"));
        scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses();
        assertEquals(force - expected, scn.GetLSForcePileCount()); assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("cruiser"));
    }
}
