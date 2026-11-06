package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1250_069_Tests {
    private VirtualTableScenario scenario() {
        HashMap<String, String> light = new HashMap<>();
        light.put("roar", "1250_69");
        light.put("wookiee", "7_50");
        light.put("otherWookiee", "7_50");
        light.put("luke", "1_19");
        for (int index = 0; index < 8; index++) {
            light.put("filler" + index, "1_28");
        }
        HashMap<String, String> dark = new HashMap<>();
        dark.put("vader", "1_168");
        dark.put("snoova", "10_48");
        return new VirtualTableScenario(light, dark, 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void WookieeRoarStatsAndIconsMatchOfficialImage() {
        var scn = scenario();
        var card = scn.GetLSCard("roar").getBlueprint();
        assertEquals("Wookiee Roar", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, card.getExpansionSet());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.INTERRUPT); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT); add(Icon.A_NEW_HOPE); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_5);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
    }

    @Test
    public void WookieeRoarAloneDrawsTwoActualPowerDestiniesAndDefenseLastsUntilTurnEnd() {
        playBattleMode(true);
    }

    @Test
    public void WookieeRoarWithAnotherWookieeDrawsOneAndOnlyChosenWookieeGainsDefense() {
        playBattleMode(false);
    }

    private void playBattleMode(boolean alone) {
        var scn = scenario();
        var site = scn.GetLSStartingLocation();
        var wookiee = scn.GetLSCard("wookiee");
        var other = scn.GetLSCard("otherWookiee");
        var roar = scn.GetLSCard("roar");
        var vader = scn.GetDSCard("vader");
        scn.StartGame();
        scn.MoveCardsToLocation(site, wookiee, vader);
        if (!alone) {
            scn.MoveCardsToLocation(site, other);
        }
        scn.MoveCardsToHand(roar);
        scn.SkipToLSTurn(Phase.BATTLE);
        int basePower = scn.GetPower(wookiee) + (alone ? 0 : scn.GetPower(other));
        scn.LSInitiateBattle(site);
        scn.DSPass();
        assertTrue(scn.LSCardPlayAvailable(roar));
        scn.LSPlayCard(roar);
        scn.LSChooseCard(wookiee);
        scn.PassAllResponses();
        assertEquals(Zone.LOST_PILE, roar.getZone());
        assertEquals(4, scn.GetDefense(wookiee));
        if (!alone) {
            assertEquals(1, scn.GetDefense(other));
        }
        if (alone) {
            scn.PrepareLSDestiny(4);
        }
        scn.PrepareLSDestiny(3);
        scn.SkipToPowerSegment();
        int draws = alone ? 2 : 1;
        for (int draw = 0; draw < draws; draw++) {
            assertTrue(scn.LSDecisionAvailable("destiny"));
            scn.LSChooseYes();
            scn.PassDestinyDrawResponses();
        }
        scn.SkipBattleDestinyDraws(false);
        assertEquals(basePower + (alone ? 7 : 3), scn.GetLSTotalPower());
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        scn.DSChooseCard(vader);
        scn.PassAllResponses();
        assertEquals(4, scn.GetDefense(wookiee));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertEquals(1, scn.GetDefense(wookiee));
    }

    @Test
    public void WookieeRoarCanChooseOpponentsAloneWookieeAndAddsPowerDestiniesForLight() {
        var scn = scenario();
        var site = scn.GetLSStartingLocation();
        var roar = scn.GetLSCard("roar");
        var snoova = scn.GetDSCard("snoova");
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), snoova);
        scn.MoveCardsToHand(roar);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.DSPass();
        assertTrue(scn.LSCardPlayAvailable(roar));
        scn.LSPlayCard(roar);
        scn.LSChooseCard(snoova);
        scn.PassAllResponses();
        assertEquals(7, scn.GetDefense(snoova));
        assertEquals(2, scn.game().getModifiersQuerying().getNumDestinyDrawsToTotalPowerOnly(
                scn.gameState(), scn.LS, false, false));
        assertEquals(0, scn.game().getModifiersQuerying().getNumDestinyDrawsToTotalPowerOnly(
                scn.gameState(), scn.DS, false, false));
        assertEquals(Zone.LOST_PILE, roar.getZone());
    }

    @Test
    public void WookieeRoarBattleModeUnavailableAfterBattleInitiationWindow() {
        var scn = scenario();
        var site = scn.GetLSStartingLocation();
        var roar = scn.GetLSCard("roar");
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("wookiee"), scn.GetDSCard("vader"));
        scn.MoveCardsToHand(roar);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardPlayAvailable(roar));
    }

    @Test
    public void WookieeRoarRequiresWookieeParticipatingInTheNewBattle() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var roar = scn.GetLSCard("roar");
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("wookiee"));
        scn.MoveCardsToHand(roar);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.DSPass();
        assertFalse(scn.LSCardPlayAvailable(roar));
    }

    @Test
    public void WookieeRoarCannotCancelDrainWithSevenCardsInHand() {
        checkDrain(7, false);
    }

    @Test
    public void WookieeRoarCancelsDrainWithExactlyEightCardsIncludingItself() {
        checkDrain(8, true);
    }

    @Test
    public void WookieeRoarCancelsDrainWithNineCardsInHand() {
        checkDrain(9, true);
    }

    private void checkDrain(int handSize, boolean canCancel) {
        var scn = scenario();
        var site = scn.GetLSStartingLocation();
        var roar = scn.GetLSCard("roar");
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.CONTROL);
        for (var card : new ArrayList<>(scn.gameState().getHand(scn.LS))) {
            scn.MoveCardsToTopOfLSReserveDeck((PhysicalCardImpl) card);
        }
        scn.MoveCardsToHand(roar);
        for (int index = 0; index < handSize - 1; index++) {
            scn.MoveCardsToHand(scn.GetLSCard("filler" + index));
        }
        assertEquals(handSize, scn.gameState().getHand(scn.LS).size());
        scn.DSForceDrainAt(site);
        assertEquals(canCancel, scn.LSCardPlayAvailable(roar));
        if (canCancel) {
            scn.LSPlayCard(roar);
            scn.PassAllResponses();
            assertEquals(Zone.LOST_PILE, roar.getZone());
            assertFalse(scn.IsActiveForceDrain());
        }
    }
}
