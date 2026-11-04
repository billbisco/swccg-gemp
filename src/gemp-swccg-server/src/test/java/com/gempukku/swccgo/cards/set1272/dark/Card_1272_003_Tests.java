package com.gempukku.swccgo.cards.set1272.dark;

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

public class Card_1272_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("irol", "1272_3");
                    put("bike", "8_169");
                    put("tempest", "8_171");
                    put("camp", "1_292");
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
    public void SergeantIrolStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("irol").getBlueprint();

        assertEquals("Sergeant Irol", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BIKER_SCOUT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SergeantIrolAddsThreeToPowerOfSpeederBikeHePilots() {
        var scn = GetScenario();
        var irol = scn.GetDSCard("irol");
        var bike = scn.GetDSCard("bike");
        var camp = scn.GetDSCard("camp");

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(camp, bike);
        int bikePower = (int) (float) bike.getBlueprint().getPower();

        scn.BoardAsPilot(bike, irol);
        assertEquals(bikePower + 3, scn.GetPower(bike));
    }

    @Test
    public void SergeantIrolAddsTwoToPowerOfOtherVehicleHePilots() {
        var scn = GetScenario();
        var irol = scn.GetDSCard("irol");
        var tempest = scn.GetDSCard("tempest");
        var camp = scn.GetDSCard("camp");

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(camp, tempest);
        int tempestPower = scn.GetPower(tempest);

        scn.BoardAsPilot(tempest, irol);
        assertEquals(tempestPower + 2, scn.GetPower(tempest));
    }

    @Test
    public void SergeantIrolLosesForceToCancelForceDrainAtOpponentsInteriorSite() {
        var scn = GetScenario();
        var irol = scn.GetDSCard("irol");
        var bike = scn.GetDSCard("bike");
        var camp = scn.GetDSCard("camp");
        var luke = scn.GetLSCard("luke");
        var walkway = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(camp, bike);
        scn.BoardAsPilot(bike, irol);
        scn.MoveCardsToLocation(walkway, luke);
        scn.SkipToLSTurn(Phase.CONTROL);

        int lsLifeBefore = scn.GetLSLifeForceRemaining();
        int dsLifeBefore = scn.GetDSLifeForceRemaining();
        scn.LSForceDrainAt(walkway);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(irol, "Lose 1 Force to cancel Force drain"));
        scn.DSUseCardAction(irol, "Lose 1 Force to cancel Force drain");
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Choose Force to lose")) {
            scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
        }
        scn.PassResponses("optional");

        assertFalse(scn.IsActiveForceDrain());
        assertEquals(dsLifeBefore - 1, scn.GetDSLifeForceRemaining());
        assertEquals(lsLifeBefore, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void SergeantIrolMayNotCancelForceDrainWhenNotPiloting() {
        var scn = GetScenario();
        var irol = scn.GetDSCard("irol");
        var camp = scn.GetDSCard("camp");
        var luke = scn.GetLSCard("luke");
        var walkway = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(camp, irol);
        scn.MoveCardsToLocation(walkway, luke);
        scn.SkipToLSTurn(Phase.CONTROL);

        scn.LSForceDrainAt(walkway);
        scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(irol, "Lose 1 Force to cancel Force drain"));
    }
}
