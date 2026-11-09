package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Original Deep Hatred (V) (Dec 2009 sheet), a version of 1240_37. */
public class Card_2240_037_Tests {
    private static final String EPIC_DUEL = "Initiate epic duel with Deep Hatred";
    private static final String RETRIEVE = "Retrieve 5 Force?";

    private VirtualTableScenario scenario(String lukeId, boolean withObjective) {
        var dark = new HashMap<>(Map.ofEntries(
                Map.entry("deep", "2240_37"), Map.entry("otherDeep", "1240_37"),
                Map.entry("hunt", "601_87"),
                Map.entry("mara", "110_10"), Map.entry("otherMara", "110_10"),
                Map.entry("obsession", "101_6"), Map.entry("saber", "110_11"),
                Map.entry("trooper", "1_194"), Map.entry("vader", "108_6"),
                Map.entry("core", "13_77"), Map.entry("generator", "13_76")));
        var scn = new VirtualTableScenario(new HashMap<>(Map.of(
                "luke", lukeId, "karrde", "10_24", "resilience", "11_29", "han", "108_1", "obi", "1_21")),
                dark, 10, 10, StartingSetup.DefaultLSGroundLocation,
                withObjective ? reflectionsObjective() : StartingSetup.DefaultDSGroundLocation,
                StartingSetup.DeclineLSStartingInterrupt, StartingSetup.DeclineDSStartingInterrupt,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        // The objective is placed by hand where a test needs it, not offered as the starting card.
        scn.MoveOutOfPlay(scn.GetDSCard("hunt"));
        return scn;
    }

    private StartingSetup reflectionsObjective() {
        return new StartingSetup() {
            @Override
            public HashMap<String, String> Cards() {
                return new HashMap<>(Map.of("objective", "13_73"));
            }

            @Override
            public void Setup(VirtualTableScenario scn) {
                if (scn.DSDecisionAvailable("starting")) scn.DSChooseCard(scn.GetDSCard("objective"));
                if (scn.DSDecisionAvailable("Choose Theed Palace Generator Core")) scn.DSChooseCard(scn.GetDSCard("core"));
                if (scn.DSDecisionAvailable("Choose Theed Palace Generator to deploy")) scn.DSChooseCard(scn.GetDSCard("generator"));
                if (scn.DSDecisionAvailable("On which side")) scn.DSChoose("Left");
                if (scn.DSDecisionAvailable("Choose Deep Hatred to deploy")) scn.DSChooseCard(scn.GetDSCard("deep"));
            }
        };
    }

    @Test
    public void DeepHatredIsTheSameCardWithTheOldText() {
        var scn = scenario("1_19", false);
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("otherDeep").getBlueprint(), scn.GetDSCard("deep").getBlueprint(),
                "Unless your [Reflections III] objective on table, deploy on table. Vader's Obsession may be played to initiate an epic duel as follows: If Mara is present with Luke, each player draws two destiny (if armed with a lightsaber, each destiny is +1). Add character's ability. If Karrde on table, your total is -1. Higher total wins. If Luke loses, place him out of play and you may retrieve 5 Force. If Mara loses, cross her to the Light Side (she is now Mara Skywalker). After the duel (unless it was canceled or tied) Mara is ability = 6, adds one battle destiny, and once per battle involving her, may cancel one just drawn destiny.",
                "1240_37", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }

    @Test
    public void AnyObjectiveOfYoursStopsTheOriginalFromDeploying() {
        //test1: with a non-[Reflections III] objective of yours on table, the original may not deploy
        //test2: the current card (only stopped by a [Reflections III] objective) may
        var scn = scenario("1_19", false);
        var deep = scn.GetDSCard("deep");
        var current = scn.GetDSCard("otherDeep");
        scn.StartGame();
        var hunt = scn.GetDSCard("hunt");
        scn.MoveCardsToDSSideOfTable(hunt);
        hunt.setObjectiveDeploymentComplete(true);
        scn.MoveCardsToDSHand(deep, current);
        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.DSDeployAvailable(deep)); //test1
        assertTrue(scn.DSDeployAvailable(current)); //test2
    }

    @Test
    public void OriginalDeploysWithoutAnObjective() {
        var scn = scenario("1_19", false);
        var deep = scn.GetDSCard("deep");
        scn.StartGame();
        scn.MoveCardsToDSHand(deep);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(deep));
    }

    @Test
    public void OnlyTheMaraWhoDueledIsAbilitySixAfterTheDuel() {
        //test1: Mara wins: Luke is placed out of play and the dueling Mara is ability = 6, even after Deep Hatred leaves
        //test2: a later Mara (the dueling one lost) is not ability = 6 (the current text: "any Mara")
        var scn = duelReady("1_19", false, false);
        beginDuel(scn, 4, 2, 3, 1);
        passOptional(scn);
        if (scn.DSDecisionAvailable(RETRIEVE)) scn.DSChooseNo();
        passOptional(scn);
        var mara = scn.GetDSCard("mara");
        assertEquals(6, scn.GetAbility(mara)); //test1
        scn.MoveOutOfPlay(scn.GetDSCard("deep"));
        assertEquals(6, scn.GetAbility(mara)); //test1
        scn.MoveCardsToTopOfDSLostPile(mara);
        var newMara = scn.GetDSCard("otherMara");
        scn.MoveCardsToDSHand(newMara);
        scn.EnsureDSForcePile(5);
        if (scn.LSDecisionAvailable("Deploy action or Pass")) scn.LSPass();
        scn.DSDeployCard(newMara);
        scn.DSChooseCard(scn.GetDSStartingLocation());
        passOptional(scn);
        assertEquals(5, scn.GetAbility(newMara)); //test2
    }

    @Test
    public void TiedDuelGivesNoAftermath() {
        var scn = duelReady("1_19", false, false);
        beginDuel(scn, 2, 1, 3, 1);
        passOptional(scn);
        assertNoAftermath(scn);
    }

    private VirtualTableScenario duelReady(String lukeId, boolean maraArmed, boolean karrde) {
        var scn = scenario(lukeId, false);
        scn.StartGame();
        var mara = scn.GetDSCard("mara");
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, mara, scn.GetLSCard("luke"));
        if (karrde) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("karrde"));
        if (maraArmed) {
            var saber = scn.GetDSCard("saber");
            scn.MoveCardsToDSHand(saber, scn.GetDSCard("deep"), scn.GetDSCard("obsession"));
            scn.EnsureDSForcePile(1);
            scn.SkipToPhase(Phase.DEPLOY);
            scn.DSDeployCard(saber);
            scn.DSChooseCard(mara);
            passOptional(scn);
            assertTrue(scn.IsAttachedTo(mara, saber));
            if (scn.LSDecisionAvailable("Deploy action or Pass")) scn.LSPass();
        }
        deployDeep(scn);
        return scn;
    }

    /** Passes every pending optional-response window (duel destiny windows included), then any remaining responses. */
    private static void passOptional(VirtualTableScenario scn) {
        for (int i = 0; i < 100; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("optional response")) break;
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        scn.PassAllResponses();
    }

    private void deployDeep(VirtualTableScenario scn) {
        var deep = scn.GetDSCard("deep");
        // Moving a card that is already in hand gives it a new id, so only move what is not there yet.
        for (var card : List.of(deep, scn.GetDSCard("obsession"))) {
            if (card.getZone() != Zone.HAND) scn.MoveCardsToDSHand(card);
        }
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(deep);
        passOptional(scn);
        assertInZone(Zone.SIDE_OF_TABLE, deep);
        if (scn.LSDecisionAvailable("Deploy action or Pass")) scn.LSPass();
    }

    private void seedLost(VirtualTableScenario scn, int cards) {
        for (int i = 0; i < cards; i++) scn.MoveCardsToTopOfDSLostPile(scn.GetTopOfDSReserveDeck());
    }

    private void beginDuel(VirtualTableScenario scn, int dark1, int dark2, int light1, int light2) {
        scn.PrepareDSDestiny(dark2);
        scn.PrepareDSDestiny(dark1);
        scn.PrepareLSDestiny(light2);
        scn.PrepareLSDestiny(light1);
        scn.DSPlayCard(scn.GetDSCard("obsession"), EPIC_DUEL);
        scn.DSChooseCard(scn.GetDSCard("mara"));
        scn.DSChooseCard(scn.GetLSCard("luke"));
    }

    private void assertNoAftermath(VirtualTableScenario scn) {
        var mara = scn.GetDSCard("mara");
        assertEquals(scn.DS, mara.getOwner());
        assertEquals(5, scn.GetAbility(mara));
        assertInZone(Zone.AT_LOCATION, mara, scn.GetLSCard("luke"));
    }
}
