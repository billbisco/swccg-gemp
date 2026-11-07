package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_037_Tests {
    private static final String EPIC_DUEL = "Initiate epic duel with Deep Hatred";
    private static final String RETRIEVE = "Retrieve 5 Force?";
    private static final String CANCEL_DESTINY = "Cancel a just drawn destiny";

    private VirtualTableScenario scenario(String lukeId, boolean withObjective) {
        var dark = new HashMap<>(Map.ofEntries(
                Map.entry("deep", "1240_37"), Map.entry("otherDeep", "1240_37"),
                Map.entry("mara", "110_10"), Map.entry("otherMara", "110_10"),
                Map.entry("obsession", "101_6"), Map.entry("saber", "110_11"),
                Map.entry("trooper", "1_194"), Map.entry("vader", "108_6"),
                Map.entry("core", "13_77"), Map.entry("generator", "13_76")));
        return new VirtualTableScenario(new HashMap<>(Map.of(
                "luke", lukeId, "karrde", "10_24", "resilience", "11_29", "han", "108_1", "obi", "1_21")),
                dark, 10, 10, StartingSetup.DefaultLSGroundLocation,
                withObjective ? reflectionsObjective() : StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
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
    public void DeepHatredHasPrintedEpicEventIdentity() {
        var scn = scenario("1_19", false);
        var card = scn.GetDSCard("deep").getBlueprint();
        assertEquals("Deep Hatred", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(Side.DARK, card.getSide());
        assertEquals(CardType.EPIC_EVENT, card.getCardType());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.EPIC_EVENT, Icon.EPISODE_I,
                Icon.REFLECTIONS_III, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
    }

    @Test
    public void ReflectionsObjectiveSuppliesDeepHatredButPreventsOrdinaryRedeployment() {
        var scn = scenario("1_19", true);
        scn.StartGame();
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetDSCard("objective"), scn.GetDSCard("deep"));
        assertInZone(Zone.LOCATIONS, scn.GetDSCard("core"), scn.GetDSCard("generator"));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("deep"));
        var other = scn.GetDSCard("otherDeep");
        scn.MoveCardsToDSHand(other);
        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.DSDeployAvailable(other));
    }

    @Test
    public void DeepHatredDoesNotGrantEpicDuelUnlessMaraIsPresentWithLuke() {
        var scn = scenario("1_19", false);
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("mara"), scn.GetDSCard("vader"));
        deployDeep(scn);
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("obsession"), EPIC_DUEL));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("luke"));
        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("obsession"), EPIC_DUEL));
    }

    @Test
    public void MaraWinsWithTwoNativeDrawsAndLukeGoesOutOfPlayBeforeOptionalRetrieval() {
        var scn = duelReady("1_19", false, false);
        seedLost(scn, 6);
        int lost = scn.GetDSLostPileCount();
        beginDuel(scn, 4, 2, 3, 1);
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable(RETRIEVE));
        assertTrue(Filters.outOfPlay.accepts(scn.game(), scn.GetLSCard("luke")));
        scn.DSChooseYes();
        scn.PassAllResponses();
        assertEquals(lost - 5 + 1, scn.GetDSLostPileCount());
        assertEquals(6, scn.GetAbility(scn.GetDSCard("mara")));
    }

    @Test
    public void DarkCanDeclineRetrievalAndAnyLaterMaraIsAbilitySixForRemainderOfGame() {
        var scn = duelReady("1_19", false, false);
        seedLost(scn, 6);
        int lost = scn.GetDSLostPileCount();
        beginDuel(scn, 4, 2, 3, 1);
        scn.PassAllResponses();
        scn.DSChooseNo();
        scn.PassAllResponses();
        assertEquals(lost + 1, scn.GetDSLostPileCount());
        var mara = scn.GetDSCard("mara");
        assertEquals(6, scn.GetAbility(mara));
        scn.MoveOutOfPlay(scn.GetDSCard("deep"));
        assertEquals(6, scn.GetAbility(mara));
        scn.MoveCardsToTopOfDSLostPile(mara);
        var newMara = scn.GetDSCard("otherMara");
        scn.MoveCardsToDSHand(newMara);
        scn.EnsureDSForcePile(5);
        if (scn.LSAnyActionsAvailable()) scn.LSPass();
        scn.DSDeployCard(newMara);
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertEquals(6, scn.GetAbility(newMara));
    }

    @Test
    public void MaraLosesAndNativeCrossoverMakesHerMaraSkywalkerWithSixAbility() {
        var scn = duelReady("1_19", false, false);
        beginDuel(scn, 2, 1, 5, 1);
        scn.PassAllResponses();
        assertLightMara(scn);
        assertEquals(scn.LS, scn.GetLSCard("luke").getOwner());
        assertInZone(Zone.AT_LOCATION, scn.GetLSCard("luke"));
    }

    @Test
    public void TiedDuelGivesNoAftermath() {
        var scn = duelReady("1_19", false, false);
        beginDuel(scn, 2, 1, 3, 1);
        scn.PassAllResponses();
        assertNoAftermath(scn);
    }

    @Test
    public void KarrdeElsewhereMakesTheDarkDuelTotalOneLower() {
        var scn = duelReady("1_19", false, true);
        beginDuel(scn, 3, 1, 3, 1);
        scn.PassAllResponses();
        assertNoAftermath(scn);
        scn = duelReady("1_19", false, false);
        beginDuel(scn, 3, 1, 3, 1);
        scn.PassAllResponses();
        assertTrue(Filters.outOfPlay.accepts(scn.game(), scn.GetLSCard("luke")));
    }

    @Test
    public void ArmingMaraAddsOneToEachDrawAndChangesTheWinner() {
        var armed = duelReady("1_19", true, false);
        beginDuel(armed, 2, 1, 4, 1);
        armed.PassAllResponses();
        assertTrue(Filters.outOfPlay.accepts(armed.game(), armed.GetLSCard("luke")));
        var unarmed = duelReady("1_19", false, false);
        beginDuel(unarmed, 2, 1, 4, 1);
        unarmed.PassAllResponses();
        assertLightMara(unarmed);
    }

    @Test
    public void LukesNativePermanentLightsaberAddsOneToEachLightDraw() {
        var scn = duelReady("108_3", false, false);
        beginDuel(scn, 4, 2, 3, 2);
        scn.PassAllResponses();
        assertLightMara(scn);
    }

    @Test
    public void OriginalJedisResilienceCancelsAnActualDuelBeforeEitherAftermath() {
        var scn = duelReady("1_19", false, false);
        var resilience = scn.GetLSCard("resilience");
        scn.MoveCardsToLSHand(resilience);
        beginDuel(scn, 4, 2, 3, 1);
        passUntilAction(scn, scn.LS, resilience, "Cancel duel");
        scn.LSPlayCard(resilience, "Cancel duel");
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(scn.GetTopOfLSReserveDeck())) {
            scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        }
        scn.PassAllResponses();
        assertNoAftermath(scn);
        assertInHand(scn.GetDSCard("obsession"));
    }

    @Test
    public void EitherSidesDuelingMaraAddsExactlyOneActualBattleDestinyAfterDeepHatredLeaves() {
        for (boolean light : List.of(false, true)) {
            var scn = aftermathBattle(light);
            scn.SkipToEndOfPowerSegment(true);
            assertEquals(light ? 2 : 1, scn.GetLSBattleDestinyCount());
            assertEquals(light ? 1 : 2, scn.GetDSBattleDestinyCount());
        }
    }

    @Test
    public void EitherSidesDuelingMaraCancelsOneActualDestinyPerBattle() {
        for (boolean light : List.of(false, true)) {
            var scn = aftermathBattle(light);
            var mara = scn.GetDSCard("mara");
            String player = light ? scn.LS : scn.DS;
            scn.SkipToPowerSegment();
            assertTrue(scn.DecisionAvailable(player, "battle destiny?"));
            scn.PlayerChooseYes(player);
            passUntilAction(scn, player, mara, CANCEL_DESTINY);
            if (light) scn.LSUseCardAction(mara, CANCEL_DESTINY);
            else scn.DSUseCardAction(mara, CANCEL_DESTINY);

            boolean laterDestinyWindow = false;
            boolean remainingPlayerChoseToDraw = false;
            for (int i = 0; i < 50; i++) {
                String decision = scn.GetCurrentDecision().getText();
                if (decision.contains("INITIAL_ATTRITION_CALCULATED")) break;
                if (decision.contains("battle destiny?")) {
                    remainingPlayerChoseToDraw = true;
                    scn.PlayerChooseYes(scn.GetDecidingPlayer());
                } else if (decision.toLowerCase().contains("optional response")) {
                    if (remainingPlayerChoseToDraw && decision.contains("DESTINY_DRAWN")) {
                        laterDestinyWindow = true;
                        assertFalse(scn.ActionAvailable(player, mara, CANCEL_DESTINY));
                    }
                    scn.PlayerPass(scn.GetDecidingPlayer());
                } else {
                    throw new AssertionError("Unexpected battle decision: " + decision);
                }
            }
            assertTrue("Opponent's actual subsequent destiny must have a response window", laterDestinyWindow);
        }
    }

    private VirtualTableScenario aftermathBattle(boolean light) {
        var scn = duelReady("1_19", false, false);
        if (light) beginDuel(scn, 2, 1, 5, 1);
        else beginDuel(scn, 4, 2, 3, 1);
        scn.PassAllResponses();
        var mara = scn.GetDSCard("mara");
        assertEquals(light ? scn.LS : scn.DS, mara.getOwner());
        assertEquals(6, scn.GetAbility(mara));
        scn.MoveOutOfPlay(scn.GetDSCard("deep"));
        var site = scn.GetDSStartingLocation();
        if (light) {
            scn.MoveCardsToLocation(site, scn.GetDSCard("vader"));
            scn.EnsureLSForcePile(1);
            scn.LSStartBattleAndSkipToWeaponsSegment(site);
        } else {
            scn.MoveCardsToLocation(site, scn.GetLSCard("obi"));
            scn.EnsureDSForcePile(1);
            scn.DSStartBattleAndSkipToWeaponsSegment(site);
        }
        scn.PrepareDSDestiny(1);
        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(1);
        scn.PrepareLSDestiny(2);
        return scn;
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
            scn.MoveCardsToDSHand(saber);
            scn.EnsureDSForcePile(1);
            scn.SkipToPhase(Phase.DEPLOY);
            scn.DSDeployCard(saber);
            scn.DSChooseCard(mara);
            scn.PassAllResponses();
            assertTrue(scn.IsAttachedTo(mara, saber));
            if (scn.LSAnyActionsAvailable()) scn.LSPass();
        }
        deployDeep(scn);
        return scn;
    }

    private void deployDeep(VirtualTableScenario scn) {
        var deep = scn.GetDSCard("deep");
        scn.MoveCardsToDSHand(deep, scn.GetDSCard("obsession"));
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(deep);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, deep);
        if (scn.LSAnyActionsAvailable()) scn.LSPass();
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

    private void assertLightMara(VirtualTableScenario scn) {
        var mara = scn.GetDSCard("mara");
        assertEquals(scn.LS, mara.getOwner());
        assertTrue(Filters.persona(Persona.MARA_SKYWALKER).accepts(scn.game(), mara));
        assertTrue(Filters.Skywalker.accepts(scn.game(), mara));
        assertEquals(6, scn.GetAbility(mara));
    }

    private void assertNoAftermath(VirtualTableScenario scn) {
        var mara = scn.GetDSCard("mara");
        assertEquals(scn.DS, mara.getOwner());
        assertEquals(5, scn.GetAbility(mara));
        assertInZone(Zone.AT_LOCATION, mara, scn.GetLSCard("luke"));
    }

    private void passUntilAction(VirtualTableScenario scn, String player, PhysicalCardImpl card, String text) {
        for (int i = 0; i < 50; i++) {
            if (scn.AnyActionsAvailable(player) && scn.ActionAvailable(player, card, text)) return;
            assertTrue(scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        throw new AssertionError("Expected native action " + text);
    }
}
