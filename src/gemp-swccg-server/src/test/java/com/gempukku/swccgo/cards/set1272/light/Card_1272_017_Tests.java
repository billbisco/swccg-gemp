package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_017_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("beezer", "1272_17");
                    put("bike", "8_83");
                    put("rogue1", "3_66");
                    put("camp", "1_131");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("bay", "1_285");
                }},
                10,
                10,
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
    public void CorporalBeezerStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("beezer").getBlueprint();

        assertEquals("Corporal Beezer", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SCOUT);
            add(Keyword.FEMALE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.ENDOR);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CorporalBeezerAddsThreeToPowerOfSpeederBikeShePilots() {
        var scn = GetScenario();
        var beezer = scn.GetLSCard("beezer");
        var bike = scn.GetLSCard("bike");
        var camp = scn.GetLSCard("camp");

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(camp, bike);
        scn.BoardAsPilot(bike, beezer);
        assertEquals((int) (float) bike.getBlueprint().getPower() + 3, scn.GetPower(bike));
    }

    @Test
    public void CorporalBeezerAddsTwoToPowerOfOtherVehicleShePilots() {
        var scn = GetScenario();
        var beezer = scn.GetLSCard("beezer");
        var rogue1 = scn.GetLSCard("rogue1");
        var camp = scn.GetLSCard("camp");

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(camp, rogue1);
        scn.BoardAsPilot(rogue1, beezer);
        assertEquals((int) (float) rogue1.getBlueprint().getPower() + 2, scn.GetPower(rogue1));
    }

    @Test
    public void CorporalBeezerLosesForceToCancelForceDrainAtOpponentsInteriorSite() {
        var scn = GetScenario();
        var beezer = scn.GetLSCard("beezer");
        var bike = scn.GetLSCard("bike");
        var camp = scn.GetLSCard("camp");
        var trooper = scn.GetDSCard("trooper");
        var bay = scn.GetDSCard("bay");

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveLocationToTable(bay);
        scn.MoveCardsToLocation(camp, bike);
        scn.BoardAsPilot(bike, beezer);
        scn.MoveCardsToLocation(bay, trooper);
        scn.SkipToDSTurn(Phase.CONTROL);

        int lsLifeBefore = scn.GetLSLifeForceRemaining();
        scn.DSForceDrainAt(bay);
        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(beezer, "Lose 1 Force to cancel Force drain"));
        scn.LSUseCardAction(beezer, "Lose 1 Force to cancel Force drain");
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose Force to lose")) {
            scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        }
        scn.PassResponses("optional");

        assertFalse(scn.IsActiveForceDrain());
        assertEquals(lsLifeBefore - 1, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void CorporalBeezerMayNotCancelForceDrainAtOpponentsExteriorSite() {
        var scn = GetScenario();
        var beezer = scn.GetLSCard("beezer");
        var bike = scn.GetLSCard("bike");
        var camp = scn.GetLSCard("camp");
        var trooper = scn.GetDSCard("trooper");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(camp, bike);
        scn.BoardAsPilot(bike, beezer);
        scn.MoveCardsToLocation(marketplace, trooper);
        scn.SkipToDSTurn(Phase.CONTROL);

        scn.DSForceDrainAt(marketplace);
        scn.DSPass();
        assertTrue(scn.IsActiveForceDrain());
        assertFalse(scn.LSCardActionAvailable(beezer, "Lose 1 Force to cancel Force drain"));
    }
}
