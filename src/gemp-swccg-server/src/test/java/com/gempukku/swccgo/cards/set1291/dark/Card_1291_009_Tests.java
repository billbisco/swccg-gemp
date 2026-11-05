package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.legacy.WatchYourStepTestSupport;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_009_Tests {
    // Hunt Down And Destroy The Jedi (V), a Dark Side [Virtual Block 4] objective, waiting in the Reserve Deck
    private static final StartingSetup DSGroundLocationWithObjectiveInDeck = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("starting-location", "12_176");
                put("hunt", "601_87");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            if (scn.DSDecisionAvailable("starting")) {
                scn.DSChooseCard(scn.GetDSCard("starting-location"));
            }
        }
    };

    protected VirtualTableScenario GetScenario() {
        return GetScenario(false);
    }

    protected VirtualTableScenario GetScenario(boolean withObjective) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("blyt", "1291_9");
                    put("b2", "1291_1");
                    put("trooper", "1_194");
                    put("vader", "1_168");
                    put("tie", "1_304");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                withObjective ? DSGroundLocationWithObjectiveInDeck : StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    protected VirtualTableScenario GetWatchYourStepScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    putAll(WatchYourStepTestSupport.pulledCards());
                    put("luke", "1_19");
                    put("obi", "1_20");
                    put("filler", "1_19");
                    put("filler2", "1_19");
                }},
                new HashMap<>() {{
                    put("blyt", "1291_9");
                    put("tie", "1_304");
                    put("trooper", "1_194");
                }},
                10, 10,
                WatchYourStepTestSupport.WatchYourStepV,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void BeginLandingYourTroopsStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("blyt").getBlueprint();

        assertEquals("Begin Landing Your Troops & The Dark Path", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BeginLandingYourTroopsDeploysOnTable() {
        var scn = GetScenario();
        var blyt = scn.GetDSCard("blyt");

        scn.StartGame();
        scn.MoveCardsToDSHand(blyt);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.DSCardPlayAvailable(blyt));
        scn.DSPlayCard(blyt);
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, blyt.getZone());
    }

    @Test
    public void BeginLandingYourTroopsSeparatistDroidsArePowerPlusOne() {
        //test1: your [Separatist] droid is power +1
        //test2: your non-droid is not
        var scn = GetScenario();
        var blyt = scn.GetDSCard("blyt");
        var b2 = scn.GetDSCard("b2");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, b2, trooper);
        float b2Power = scn.GetPower(b2);
        float trooperPower = scn.GetPower(trooper);
        scn.MoveCardsToDSSideOfTable(blyt);

        assertEquals(b2Power + 1, scn.GetPower(b2), scn.epsilon); //test1
        assertEquals(trooperPower, scn.GetPower(trooper), scn.epsilon); //test2
    }

    @Test
    public void BeginLandingYourTroopsIsLostIfYourVirtualBlock4ObjectiveOnTable() {
        //test1: deploying it while your [Virtual Block 4] objective is on table makes it lost
        var scn = GetScenario(true);
        var blyt = scn.GetDSCard("blyt");
        var hunt = scn.GetDSCard("hunt");

        scn.StartGame();
        // the objective starts on table; its pulled cards are not in this deck
        while (scn.DSDecisionAvailable("Verify Reserve Deck") || scn.LSDecisionAvailable("Verify Reserve Deck")) {
            if (scn.DSDecisionAvailable("Verify Reserve Deck")) {
                scn.DSPass();
            }
            if (scn.LSDecisionAvailable("Verify Reserve Deck")) {
                scn.LSPass();
            }
        }
        scn.MoveCardsToDSHand(blyt);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToDSSideOfTable(hunt);

        scn.DSPlayCard(blyt);
        scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(blyt)); //test1
    }

    @Test
    public void BeginLandingYourTroopsCancelsAndRedrawsYourBattleDestiny() {
        //test1: during battle, may place it out of play to cancel and redraw your just-drawn destiny
        var scn = GetScenario();
        var blyt = scn.GetDSCard("blyt");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(blyt);
        scn.MoveCardsToLocation(site, vader, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PrepareDSDestiny(5);
        scn.PrepareDSDestiny(1);
        scn.SkipToPowerSegment();
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.LSPass();

        assertTrue(scn.DSCardActionAvailable(blyt, "Cancel and redraw"));
        scn.DSUseCardAction(blyt, "Cancel and redraw");
        scn.PassAllResponses();
        assertEquals(Zone.OUT_OF_PLAY, blyt.getZone()); //test1
        scn.PassDestinyDrawResponses();
        scn.SkipBattleDestinyDraws(false);
        // redrawn 5, +1 from Vader
        assertEquals(6, scn.GetDSTotalDestiny(), scn.epsilon); //test1
    }

    @Test
    public void BeginLandingYourTroopsLetsEachPlayerIgnoreWatchYourStepDeployCostOncePerTurn() {
        //test1: opponent may ignore Watch Your Step's deploy cost once
        //test2: the second deployment that turn pays it
        var scn = GetWatchYourStepScenario();
        var blyt = scn.GetDSCard("blyt");
        var luke = scn.GetLSCard("luke");
        var obi = scn.GetLSCard("obi");
        var filler = scn.GetLSCard("filler");
        var filler2 = scn.GetLSCard("filler2");
        var city = scn.GetLSCard("city");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(blyt);
        scn.MoveCardsToLSHand(luke, obi, filler, filler2);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(8);

        scn.LSDeployCard(luke);
        scn.LSChooseCard(city);
        assertTrue(scn.LSDecisionAvailable("Deploy cost on Watch Your Step"));
        scn.LSChoose("Ignore this deploy cost");
        scn.PassAllResponses();
        if (scn.DSGetDecision() != null) {
            scn.DSPass();
        }
        assertAtLocation(city, luke); //test1
        assertInHand(filler); //test1
        assertInHand(filler2); //test1

        scn.LSDeployCard(obi);
        scn.LSChooseCard(city);
        assertFalse(scn.LSDecisionAvailable("Deploy cost on Watch Your Step")); //test2
        assertTrue(scn.LSDecisionAvailable("bottom of Used Pile")); //test2
        scn.LSChooseCard(filler);
        scn.PassAllResponses();
        assertAtLocation(city, obi); //test2
        assertEquals(Zone.USED_PILE, filler.getZone()); //test2
    }

    @Test
    public void BeginLandingYourTroopsTakesStarfighterOncePerGameWhileWatchYourStepOnTable() {
        //test1: takes a starfighter into hand from Reserve Deck
        //test2: once per game (draw-two option is gone too)
        var scn = GetWatchYourStepScenario();
        var blyt = scn.GetDSCard("blyt");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(blyt);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(tie);

        assertTrue(scn.DSCardActionAvailable(blyt, "Draw two cards"));
        assertTrue(scn.DSCardActionAvailable(blyt, "Take a starfighter"));
        scn.DSUseCardAction(blyt, "Take a starfighter");
        scn.DSChooseCard(tie);
        scn.PassAllResponses();
        assertInHand(tie); //test1
        scn.LSPass();

        assertFalse(scn.DSCardActionAvailable(blyt, "Take a starfighter")); //test2
        assertFalse(scn.DSCardActionAvailable(blyt, "Draw two cards")); //test2
    }

    @Test
    public void BeginLandingYourTroopsMayNotTakeStarfighterWithoutWatchYourStep() {
        //test1: needs Watch Your Step on table
        var scn = GetScenario();
        var blyt = scn.GetDSCard("blyt");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(blyt);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(tie);

        assertFalse(scn.DSCardActionAvailable(blyt, "Take a starfighter")); //test1
        assertFalse(scn.DSCardActionAvailable(blyt, "Draw two cards")); //test1
    }
}
