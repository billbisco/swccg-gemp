package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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

public class Card_1272_016_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("wedge", "1272_16");
                    put("rogue1", "3_66");
                    put("pass", "3_47");
                    put("pass2", "3_47");
                    put("camp", "1_131");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("trooper", "1_194");
                    put("dscamp", "1_293");
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

    private PhysicalCardImpl putObjectiveOnTable(VirtualTableScenario scn, String blueprintId) {
        PhysicalCardImpl objective = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.DS, VirtualTableScenario._cardLibrary, blueprintId);
        scn.gameState().addCardToZone(objective, Zone.SIDE_OF_TABLE, scn.DS, true, false, false);
        return objective;
    }

    @Test
    public void CommanderWedgeAntillesStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("wedge").getBlueprint();

        assertEquals("Commander Wedge Antilles", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.WEDGE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.COMMANDER);
            add(Keyword.LEADER);
            add(Keyword.ROGUE_SQUADRON);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CommanderWedgeAntillesAddsThreeToPowerAndOneToManeuverOfAnythingHePilots() {
        var scn = GetScenario();
        var wedge = scn.GetLSCard("wedge");
        var rogue1 = scn.GetLSCard("rogue1");
        var camp = scn.GetLSCard("camp");

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(camp, rogue1);
        scn.BoardAsPilot(rogue1, wedge);
        assertEquals((int) (float) rogue1.getBlueprint().getPower() + 3, scn.GetPower(rogue1));
        assertEquals((int) (float) rogue1.getBlueprint().getManeuver() + 1, scn.GetManeuver(rogue1));
    }

    @Test
    public void CommanderWedgeAntillesTakesOneMorePassIntoHandOncePerTurn() {
        var scn = GetScenario();
        var wedge = scn.GetLSCard("wedge");
        var pass = scn.GetLSCard("pass");
        var pass2 = scn.GetLSCard("pass2");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), wedge);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(pass, pass2);

        assertTrue(scn.LSCardActionAvailable(wedge, "Take One More Pass into hand"));
        scn.LSUseCardAction(wedge, "Take One More Pass into hand");
        scn.LSChooseCard(pass);
        scn.PassAllResponses();
        assertTrue(pass.getZone() == Zone.HAND || pass2.getZone() == Zone.HAND);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(wedge, "Take One More Pass into hand"));
    }

    @Test
    public void CommanderWedgeAntillesDrawsOneBattleDestinyWhilePilotingAtBattlegroundSite() {
        var scn = GetScenario();
        var wedge = scn.GetLSCard("wedge");
        var rogue1 = scn.GetLSCard("rogue1");
        var camp = scn.GetLSCard("camp");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(camp, rogue1);
        scn.BoardAsPilot(rogue1, wedge);
        scn.MoveCardsToLocation(camp, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSStartBattleAndSkipToWeaponsSegment(camp);
        scn.SkipToPowerSegment();
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void CommanderWedgeAntillesDoesNotDrawBattleDestinyWhenNotPiloting() {
        var scn = GetScenario();
        var wedge = scn.GetLSCard("wedge");
        var camp = scn.GetLSCard("camp");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(camp, wedge);
        scn.MoveCardsToLocation(camp, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSStartBattleAndSkipToWeaponsSegment(camp);
        scn.SkipToPowerSegment();
        assertEquals(0, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void CommanderWedgeAntillesFlipsVirtualTheirFireAndItMayNotFlipBack() {
        var scn = GetScenario();
        var wedge = scn.GetLSCard("wedge");
        var rogue1 = scn.GetLSCard("rogue1");
        var camp = scn.GetLSCard("camp");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var dscamp = scn.GetDSCard("dscamp");

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveLocationToTable(dscamp);
        scn.MoveCardsToLocation(dscamp, vader);
        var hunt = putObjectiveOnTable(scn, "601_87");
        scn.gameState().flipCard(scn.game(), hunt, true);
        assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, hunt.getBlueprint().getTitle());

        scn.MoveCardsToLocation(camp, rogue1);
        scn.BoardAsPilot(rogue1, wedge);
        scn.MoveCardsToLSHand(luke);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(luke);
        if (scn.LSHasCardChoiceAvailable(camp)) {
            scn.LSChooseCard(camp);
        }
        scn.PassAllResponses();

        assertEquals(Title.Hunt_Down_And_Destroy_The_Jedi, hunt.getBlueprint().getTitle());
        assertFalse(GameConditions.canBeFlipped(scn.game(), hunt));
    }

    @Test
    public void CommanderWedgeAntillesDoesNotFlipNonVirtualTheirFire() {
        var scn = GetScenario();
        var wedge = scn.GetLSCard("wedge");
        var rogue1 = scn.GetLSCard("rogue1");
        var camp = scn.GetLSCard("camp");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var dscamp = scn.GetDSCard("dscamp");

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveLocationToTable(dscamp);
        scn.MoveCardsToLocation(dscamp, vader);
        var hunt = putObjectiveOnTable(scn, "7_297");
        scn.gameState().flipCard(scn.game(), hunt, true);
        assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, hunt.getBlueprint().getTitle());

        scn.MoveCardsToLocation(camp, rogue1);
        scn.BoardAsPilot(rogue1, wedge);
        scn.MoveCardsToLSHand(luke);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(luke);
        if (scn.LSHasCardChoiceAvailable(camp)) {
            scn.LSChooseCard(camp);
        }
        scn.PassAllResponses();

        assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, hunt.getBlueprint().getTitle());
    }
}
