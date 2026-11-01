package com.gempukku.swccgo.cards.set1115.dark;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1115_051_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("xwing", "1_146");
                    put("corvette", "1_140");
                    put("yavin", "1_135");
                }},
                new HashMap<>() {{
                    put("pilot", "1115_51");
                    put("avenger", "4_172");
                    put("tatooine", "1_289");
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
    public void ImperialPilotStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("pilot").getBlueprint();

        assertEquals("Imperial Pilot", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(0, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.FEMALE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ImperialPilotAddsTwoToPowerOfAnythingShePilots() {
        var scn = GetScenario();
        var pilot = scn.GetDSCard("pilot");
        var avenger = scn.GetDSCard("avenger");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, avenger);
        scn.BoardAsPilot(avenger, pilot);
        assertEquals(4, scn.GetPower(avenger));
    }

    @Test
    public void ImperialPilotIsMatchingPilotForTieAvenger() {
        var scn = GetScenario();
        var pilot = scn.GetDSCard("pilot");
        var avenger = scn.GetDSCard("avenger");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, avenger);
        scn.BoardAsPilot(avenger, pilot);
        assertTrue(scn.game().getModifiersQuerying().isMatchingPair(scn.gameState(), pilot, avenger));
    }

    @Test
    public void ImperialPilotWhilePilotingTieAddsTwoManeuverAndMovesForFree() {
        var scn = GetScenario();
        var pilot = scn.GetDSCard("pilot");
        var avenger = scn.GetDSCard("avenger");
        var tatooine = scn.GetDSCard("tatooine");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(tatooine, avenger);
        scn.BoardAsPilot(avenger, pilot);
        assertEquals(6, scn.GetManeuver(avenger));
        assertEquals(0, scn.game().getModifiersQuerying().getMoveUsingHyperspeedCost(
                scn.gameState(), avenger, tatooine, yavin, false, 0), scn.epsilon);
    }

    @Test
    public void ImperialPilotJustLostFromTieMayUseOneForceToPlaceInUsedPile() {
        var scn = GetScenario();
        var pilot = scn.GetDSCard("pilot");
        var avenger = scn.GetDSCard("avenger");
        var tatooine = scn.GetDSCard("tatooine");
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, avenger, xwing);
        scn.BoardAsPilot(avenger, pilot);
        scn.BoardAsPilot(xwing, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(2);
        scn.DSInitiateBattle(tatooine);
        scn.PassWeaponsSegmentActions();
        scn.SkipBattleDestinyDraws(false);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertTrue(scn.AwaitingDSBattleDamagePayment());
        scn.DSChooseCard(pilot);
        scn.PassCardLeavingTable();
        scn.DSChooseAction("Place in Used Pile");
        scn.PassAllResponses();
        assertTrue(pilot.getZone() == Zone.USED_PILE || pilot.getZone() == Zone.TOP_OF_USED_PILE);
    }

    @Test
    public void ImperialPilotMakesCorvettesDeployPlusOneHere() {
        var scn = GetScenario();
        var pilot = scn.GetDSCard("pilot");
        var avenger = scn.GetDSCard("avenger");
        var tatooine = scn.GetDSCard("tatooine");
        var yavin = scn.GetLSCard("yavin");
        var corvette = scn.GetLSCard("corvette");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(tatooine, avenger);
        scn.BoardAsPilot(avenger, pilot);
        scn.MoveCardsToLSHand(corvette);

        assertEquals(5, scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), corvette, corvette, tatooine, false, null, false, 0, null, false), scn.epsilon);
        assertEquals(4, scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), corvette, corvette, yavin, false, null, false, 0, null, false), scn.epsilon);
    }
}
