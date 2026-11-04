package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("booster", "1274_14");
                    put("xwing", "1_146");
                    put("xwing2", "1_146");
                    put("ywing", "1_147");
                    put("errant", "1281_45");
                    put("hobbie", "3_5");
                    put("luke", "1_19");
                    put("yavin", "1_135");
                }},
                new HashMap<>() {{
                    put("isd", "1_302");
                    put("isd2", "1_302");
                    put("tie", "1_304");
                    put("black2", "1_299");
                    put("vader", "1_168");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void BoosterTerrikStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("booster").getBlueprint();

        assertEquals("Booster Terrik", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(Species.CORELLIAN, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.ALIEN);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BoosterTerrikAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var booster = scn.GetLSCard("booster");
        var xwing = scn.GetLSCard("xwing");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, xwing);
        scn.BoardAsPilot(xwing, booster);
        assertEquals(3 + 2, scn.GetPower(xwing), scn.epsilon);
    }

    @Test
    public void BoosterTerrikStarDestroyersHePilotsAreHyperspeedPlusTwo() {
        var scn = GetScenario();
        var booster = scn.GetLSCard("booster");
        var errant = scn.GetLSCard("errant");
        var xwing = scn.GetLSCard("xwing");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, errant, xwing);
        assertEquals(3, scn.GetHyperspeed(errant), scn.epsilon);
        scn.BoardAsPilot(errant, booster);
        assertEquals(3 + 2, scn.GetHyperspeed(errant), scn.epsilon);

        // Not a Star Destroyer: no hyperspeed bonus
        scn.BoardAsPilot(xwing, scn.GetLSCard("luke"));
        assertEquals(5, scn.GetHyperspeed(xwing), scn.epsilon);
    }

    @Test
    public void BoosterTerrikStealsImperialClassStarDestroyerFromOpponentsLostPileOncePerGame() {
        var scn = GetScenario();
        var booster = scn.GetLSCard("booster");
        var isd = scn.GetDSCard("isd");
        var isd2 = scn.GetDSCard("isd2");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, booster);
        scn.MoveCardsToTopOfDSLostPile(isd, isd2, tie);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(booster, "Steal from opponent's Lost Pile"));
        scn.LSUseCardAction(booster, "Steal from opponent's Lost Pile");
        assertEquals(2, scn.LSGetSelectableCount());
        scn.LSChooseCard(isd);
        scn.PassAllResponses();
        // Both Star Destroyers share a blueprint, so either copy may be the one stolen
        var stolen = isd.getZone() == Zone.HAND ? isd : isd2;
        assertInZone(Zone.HAND, stolen);
        assertTrue(scn.gameState().getHand(scn.LS).contains(stolen));
        assertInZone(Zone.LOST_PILE, tie);

        // Once per game
        if (scn.AwaitingDSDeployPhaseActions()) {
            scn.DSPass();
        }
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(booster, "Steal from opponent's Lost Pile"));
    }

    @Test
    public void BoosterTerrikMayNotStealWhileCorelliaOnTable() {
        var scn = GetScenario();
        var booster = scn.GetLSCard("booster");
        var isd = scn.GetDSCard("isd");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        MissionTestSupport.putLocationOnTable(scn, 9741, scn.DS, MissionTestSupport.namedSystem("Corellia", 6));
        scn.MoveCardsToLocation(system, booster);
        scn.MoveCardsToTopOfDSLostPile(isd);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(booster, "Steal from opponent's Lost Pile"));
    }

    @Test
    public void BoosterTerrikLetsStarshipsHereWithRogueSquadronPilotMoveAsReact() {
        var scn = GetScenario();
        var booster = scn.GetLSCard("booster");
        var hobbie = scn.GetLSCard("hobbie");
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");
        var xwing2 = scn.GetLSCard("xwing2");
        var ywing = scn.GetLSCard("ywing");
        var yavin = scn.GetLSCard("yavin");
        var black2 = scn.GetDSCard("black2");
        var dantooine = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, xwing, ywing);
        scn.BoardAsPilot(xwing, hobbie);
        scn.BoardAsPilot(ywing, booster);
        scn.MoveCardsToLocation(dantooine, xwing2, black2);
        scn.BoardAsPilot(xwing2, luke);
        scn.BoardAsPilot(black2, scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(4);
        scn.EnsureLSForcePile(4);
        scn.DSInitiateBattle(dantooine);

        assertTrue(scn.LSCardActionAvailable(booster, "Move starship as a 'react'"));
        scn.LSUseCardAction(booster, "Move starship as a 'react'");
        assertTrue(scn.LSHasCardChoiceAvailable(xwing));
        assertFalse(scn.LSHasCardChoiceAvailable(ywing));
        scn.LSChooseCard(xwing);
        if (scn.LSHasCardChoiceAvailable(dantooine)) {
            scn.LSChooseCard(dantooine);
        }
        scn.PassAllResponses();
        assertEquals(dantooine, xwing.getAtLocation());
        assertEquals(yavin, ywing.getAtLocation());
    }
}
