package com.gempukku.swccgo.ai.features;

import com.gempukku.swccgo.ai.HeadlessDecisionTraceWriter;
import com.gempukku.swccgo.ai.models.LinearPolicyAi;
import com.gempukku.swccgo.common.CardCategory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecisionType;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InformationSetV1Test {

    private static final String DARK = "~OzzelBot";
    private static final String LIGHT = "~AckbarBot";
    private static final String SECRET_OPP_TITLE = "SECRET_OPPONENT_HAND_TITLE_SHOULD_NEVER_APPEAR";

    @Test
    public void packedLengthMatchesLayoutAndPadIsZero() {
        InformationSetV1 set = new InformationSetV1();
        set.phase = "CONTROL";
        set.decisionType = "CARD_ACTION_CHOICE";
        set.scalars.put("deciderLF", 28f);
        set.scalars.put("opponentLF", 31f);
        Map<String, Object> card = new LinkedHashMap<>();
        card.put("blueprintId", "1_140");
        card.put("title", "Sorry About The Mess");
        card.put("category", "INTERRUPT");
        set.ownHand.add(card);

        float[] packed = InformationSetEncoder.encodePacked(set);
        assertEquals(FeatureLayoutV1.PACKED_DIM, packed.length);
        assertEquals(1f, packed[FeatureLayoutV1.phaseIndex("CONTROL")], 0f);
        assertEquals(0f, packed[FeatureLayoutV1.phaseIndex("BATTLE")], 0f);
        assertEquals(1f, packed[FeatureLayoutV1.DECISION_ONE_HOT + FeatureLayoutV1.decisionIndex("CARD_ACTION_CHOICE")], 0f);
        for (int i = FeatureLayoutV1.PAD_START; i < packed.length; i++) {
            assertEquals(0f, packed[i], 0f);
        }
        Map<String, Object> json = set.toMap();
        assertFalse(json.containsKey(FeatureLayoutV1.FORBIDDEN_OPPONENT_DECK_KEY));
        assertEquals("1_140", ((Map<?, ?>) ((java.util.List<?>) json.get("ownHand")).get(0)).get("blueprintId"));
    }

    @Test
    public void seenHistoryCapsWhileAggregatesAndRevealsStay() {
        InformationSetTracker tracker = new InformationSetTracker("~AckbarBot", 32);
        Map<String, Object> earlyTech = new LinkedHashMap<>();
        earlyTech.put("blueprintId", "battle_order");
        earlyTech.put("how", "PLAYED");
        tracker.recordOpponentRevealed(earlyTech);
        Map<String, Object> six = new LinkedHashMap<>();
        six.put("blueprintId", "1_abc");
        six.put("destinyValue", 6);
        six.put("recycleHint", "IN_USED_UNTIL_SHUFFLE");
        tracker.recordDestinyRecycle(six);

        for (int i = 0; i < 40; i++) {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("seq", i);
            event.put("blueprintId", "evt_" + i);
            tracker.recordSeen(event);
        }
        assertEquals(32, tracker.seenHistorySize());
        assertEquals(8, tracker.getDroppedSeenEvents());
        assertEquals(1, tracker.aggregateSize());
        assertEquals(1, tracker.opponentRevealedSize());

        InformationSetV1 set = new InformationSetV1();
        tracker.copyInto(set);
        assertEquals(32, set.seenHistory.size());
        assertEquals("evt_8", set.seenHistory.get(0).get("blueprintId"));
        assertEquals("battle_order", set.opponentRevealed.get(0).get("blueprintId"));
        assertFalse(set.toMap().containsKey("opponentDeckPriorKnown"));
    }

    @Test
    public void exactOpponentDeckIsRejected() {
        InformationSetTracker tracker = new InformationSetTracker("~AckbarBot");
        try {
            tracker.rejectExactOpponentDeckPrior();
            fail("expected rejection");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("opponentDeckPriorKnown"));
        }
    }

    @Test
    public void fixedSeedRepeatsAndTrainingModeIsRandom() {
        Random a = SeededShuffleTool.open(SeededShuffleTool.SeedMode.FIXED, 7L);
        Random b = SeededShuffleTool.open(SeededShuffleTool.SeedMode.FIXED, 7L);
        assertEquals(a.nextInt(), b.nextInt());
        assertEquals(SeededShuffleTool.SeedMode.RANDOM, SeededShuffleTool.SeedMode.valueOf("RANDOM"));
        assertTrue(SeededShuffleTool.openForTraining() != null);
        try {
            SeededShuffleTool.open(SeededShuffleTool.SeedMode.FIXED, null);
            fail("FIXED requires a seed");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("FIXED"));
        }
    }

    @Test
    public void fromGameStateFillsOwnHandPhaseLfPilesAndPacked128() {
        GameState gs = mockGameStateWithHands();
        AwaitingDecision decision = mock(AwaitingDecision.class);
        when(decision.getDecisionType()).thenReturn(AwaitingDecisionType.CARD_ACTION_CHOICE);
        when(decision.getText()).thenReturn("Choose action to play");
        Map<String, String[]> params = new LinkedHashMap<>();
        params.put("actionId", new String[]{"0", "1"});
        params.put("actionText", new String[]{"Pass", "Deploy"});
        params.put("noPass", new String[]{"false"});
        when(decision.getDecisionParameters()).thenReturn(params);

        InformationSetTracker tracker = new InformationSetTracker(LIGHT);
        Map<String, Integer> prior = new LinkedHashMap<>();
        prior.put("1_140", 2);
        tracker.setOwnDeckPrior(prior);

        InformationSetV1 set = InformationSetEncoder.from(gs, LIGHT, decision, tracker, "premiere_anh");

        assertEquals(FeatureLayoutV1.PACKED_DIM, set.packed.length);
        assertEquals("CONTROL", set.phase);
        assertEquals("LIGHT", set.side);
        assertEquals(LIGHT, set.playerId);
        assertEquals(1, set.ownHand.size());
        assertEquals("1_140", set.ownHand.get(0).get("blueprintId"));
        assertEquals("Sorry About The Mess", set.ownHand.get(0).get("title"));
        assertEquals(28f, set.scalars.get("lightLF"), 0.01f);
        assertEquals(31f, set.scalars.get("darkLF"), 0.01f);
        assertEquals(5f, set.scalars.get("lightReserveSize"), 0.01f);
        assertEquals(4f, set.scalars.get("darkReserveSize"), 0.01f);
        assertFalse(set.seededFromExactOpponentDeck);
        assertEquals(2, set.ownDeckPrior.get("1_140").intValue());
        assertTrue(set.publicInPlay.size() >= 1);
        assertTrue(set.extractionGaps.contains("destinyListenerNotHooked"));

        Map<String, Object> json = set.toMap();
        assertFalse(json.containsKey(FeatureLayoutV1.FORBIDDEN_OPPONENT_DECK_KEY));
        assertFalse(String.valueOf(json).contains(SECRET_OPP_TITLE));
        assertFalse(json.containsKey("opponentHand"));
    }

    @Test
    public void fromGameStateOmitsOpponentHandTitlesAndIds() {
        GameState gs = mockGameStateWithHands();
        InformationSetV1 set = InformationSetEncoder.from(gs, LIGHT, null, null, "open");

        assertEquals(1, set.ownHand.size());
        assertEquals("Sorry About The Mess", set.ownHand.get(0).get("title"));

        String dump = String.valueOf(set.toMap());
        assertFalse("opponent hand title leaked into InformationSet", dump.contains(SECRET_OPP_TITLE));
        assertFalse(dump.contains("secret_opp_bp"));
        // Opponent hand size is allowed as a scalar only.
        assertEquals(2f, set.scalars.get("opponentHandSize"), 0.01f);
        assertEquals(1f, set.scalars.get("deciderHandSize"), 0.01f);
    }

    @Test
    public void featuresTraceLineEmbedsOwnHandAndOmitsOpponentTitles() throws Exception {
        GameState gs = mockGameStateWithHands();
        AwaitingDecision decision = mock(AwaitingDecision.class);
        when(decision.getDecisionType()).thenReturn(AwaitingDecisionType.CARD_ACTION_CHOICE);
        when(decision.getText()).thenReturn("Choose action");
        when(decision.getAwaitingDecisionId()).thenReturn(7);
        when(decision.getDecisionParameters()).thenReturn(new LinkedHashMap<>());

        Path out = Files.createTempFile("features-trace-", ".jsonl");
        try (HeadlessDecisionTraceWriter writer =
                     new HeadlessDecisionTraceWriter(out, HeadlessDecisionTraceWriter.TraceLevel.FEATURES)) {
            InformationSetTracker tracker = new InformationSetTracker(LIGHT);
            HeadlessDecisionTraceWriter.TraceContext ctx = HeadlessDecisionTraceWriter.fromDecision(
                    "game-1", 1, 0, LIGHT, "BEGINNER", "open",
                    decision, gs, "0", true, null,
                    HeadlessDecisionTraceWriter.TraceLevel.FEATURES, tracker);
            writer.record(ctx);
        }
        String line = Files.readString(out, StandardCharsets.UTF_8);
        assertTrue(line.contains("\"traceLevel\":\"FEATURES\""));
        assertTrue(line.contains("Sorry About The Mess"));
        assertTrue(line.contains("\"ownHand\""));
        assertTrue(line.contains("\"packed\""));
        assertTrue(line.contains("\"bagHash\""));
        assertFalse(line.contains(SECRET_OPP_TITLE));

        JsonObject row = JsonParser.parseString(line.trim()).getAsJsonObject();
        JsonArray logged = row.getAsJsonObject("state").getAsJsonArray("bagHash");
        assertEquals(LinearPolicyAi.DEFAULT_BAG_HASH_DIM, logged.size());
        InformationSetV1 set = InformationSetEncoder.from(gs, LIGHT, decision, new InformationSetTracker(LIGHT), "open");
        float[] expected = LinearPolicyAi.bagHash(set, LinearPolicyAi.DEFAULT_BAG_HASH_DIM);
        boolean any = false;
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], logged.get(i).getAsFloat(), 0f);
            if (expected[i] != 0f) {
                any = true;
            }
        }
        assertTrue("hand blueprint should land in a bag-hash bucket", any);
        Files.deleteIfExists(out);
    }


    @Test
    public void ownDeckPriorPresentAndOpponentDeckAbsent() {
        InformationSetTracker tracker = new InformationSetTracker(LIGHT);
        Map<String, Integer> prior = DecklistMultisets.fromBlueprintIds(
                Arrays.asList("1_140", "1_140", "1_304", "2_001"));
        tracker.setOwnDeckPrior(prior);
        Map<String, Float> hints = new LinkedHashMap<>();
        hints.put("1_140", 4f);
        hints.put("1_304", 5f);
        hints.put("2_001", 6f);
        tracker.setOwnBlueprintDestinyHints(hints);

        GameState gs = mockGameStateWithHands();
        InformationSetV1 set = InformationSetEncoder.from(gs, LIGHT, null, tracker, "premiere_anh");

        assertEquals(2, set.ownDeckPrior.get("1_140").intValue());
        assertEquals(1, set.ownDeckPrior.get("1_304").intValue());
        assertFalse(set.seededFromExactOpponentDeck);
        Map<String, Object> json = set.toMap();
        assertFalse(json.containsKey(FeatureLayoutV1.FORBIDDEN_OPPONENT_DECK_KEY));
        assertFalse(String.valueOf(json).contains(SECRET_OPP_TITLE));
        // Remaining unique after subtracting own hand (1_140) and in-play Luke (1_304)
        assertTrue(set.scalars.get("ownRemainingEstimateUnique") >= 1f);
        // High destiny remaining uses hints on leftover prior copies (2_001 fate 6)
        assertTrue(set.scalars.get("ownHighDestinyRemainingEst") >= 1f);
        // No listener attached in this unit path — gap stays honest.
        assertTrue(set.extractionGaps.contains("destinyListenerNotHooked"));
    }

    @Test
    public void simulatedDestinyRevealLandsInOpponentRevealedAndSeenHistory() {
        InformationSetTracker tracker = new InformationSetTracker(LIGHT);
        InformationSetGameStateListener listener = new InformationSetGameStateListener(tracker);
        assertTrue(tracker.isEventsHooked());

        PhysicalCard oppDestiny = mockCard("9_999", "Opponent Destiny Card", CardCategory.INTERRUPT,
                Side.DARK, DARK, Zone.USED_PILE, 6f);
        when(oppDestiny.getDestinyValueToUse()).thenReturn(6f);

        listener.destinyDrawn(oppDestiny, mockGameStateWithHands(), "Battle destiny");

        assertEquals(1, tracker.seenHistorySize());
        assertEquals(1, tracker.aggregateSize());
        assertEquals(1, tracker.opponentRevealedSize());
        assertEquals(1, listener.getDestinyEvents());
        assertEquals(1, listener.getOpponentRevealEvents());

        InformationSetV1 set = new InformationSetV1();
        tracker.copyInto(set);
        assertEquals("9_999", set.seenHistory.get(0).get("blueprintId"));
        assertEquals("DESTINY", set.seenHistory.get(0).get("how"));
        assertEquals(6.0, ((Number) set.seenHistory.get(0).get("destinyValue")).doubleValue(), 0.01);
        assertEquals("9_999", set.opponentRevealed.get(0).get("blueprintId"));
        assertFalse(set.toMap().containsKey(FeatureLayoutV1.FORBIDDEN_OPPONENT_DECK_KEY));

        InformationSetV1 encoded = InformationSetEncoder.from(mockGameStateWithHands(), LIGHT, null, tracker, "open");
        assertFalse(encoded.extractionGaps.contains("destinyListenerNotHooked"));
        assertFalse(encoded.extractionGaps.contains("examinePeekNotHooked"));
        assertFalse(encoded.extractionGaps.contains("sabaccRevealNotHooked"));
        assertTrue(encoded.extractionGaps.contains("buriedOwnUsedIdentitiesNotPublic"));
        assertEquals(1, encoded.opponentRevealed.size());

    }

    @Test
    public void ownDestinyDoesNotEnterOpponentRevealed() {
        InformationSetTracker tracker = new InformationSetTracker(LIGHT);
        InformationSetGameStateListener listener = new InformationSetGameStateListener(tracker);

        PhysicalCard ownDestiny = mockCard("1_140", "Sorry About The Mess", CardCategory.INTERRUPT,
                Side.LIGHT, LIGHT, Zone.USED_PILE, 4f);
        when(ownDestiny.getDestinyValueToUse()).thenReturn(4f);
        listener.destinyDrawn(ownDestiny, mockGameStateWithHands(), "Weapon destiny");

        assertEquals(1, tracker.seenHistorySize());
        assertEquals(1, tracker.aggregateSize());
        assertEquals(0, tracker.opponentRevealedSize());
        assertEquals(0, listener.getOpponentRevealEvents());
    }

    @Test
    public void opponentInterruptPlayedIsRevealed() {
        InformationSetTracker tracker = new InformationSetTracker(LIGHT);
        InformationSetGameStateListener listener = new InformationSetGameStateListener(tracker);

        PhysicalCard oppInt = mockCard("1_200", "Dark Interrupt", CardCategory.INTERRUPT,
                Side.DARK, DARK, Zone.USED_PILE, 3f);
        listener.interruptPlayed(oppInt, mockGameStateWithHands());

        assertEquals(1, tracker.seenHistorySize());
        assertEquals(1, tracker.opponentRevealedSize());
        InformationSetV1 set = new InformationSetV1();
        tracker.copyInto(set);
        assertEquals("INTERRUPT_PLAYED", set.opponentRevealed.get(0).get("how"));
        assertEquals("1_200", set.opponentRevealed.get(0).get("blueprintId"));
        assertEquals(1, listener.getInterruptEvents());
    }

    @Test
    public void examinedOpponentHandIsRecordedButOtherSeatsDecisionIsNot() {
        InformationSetTracker tracker = new InformationSetTracker(LIGHT);
        InformationSetGameStateListener listener = new InformationSetGameStateListener(tracker);

        PhysicalCard oppHand = mockCard("secret_opp_bp", SECRET_OPP_TITLE, CardCategory.INTERRUPT,
                Side.DARK, DARK, Zone.HAND, 5f);
        ArbitraryCardsSelectionDecision shownToUs = new ArbitraryCardsSelectionDecision(
                "Opponent's hand", Collections.singletonList(oppHand), Collections.<PhysicalCard>emptyList(), 0, 0) {
            @Override
            public void decisionMade(String result) throws DecisionResultInvalidException {
            }
        };
        listener.decisionRequired(LIGHT, shownToUs);

        assertEquals(1, tracker.seenHistorySize());
        assertEquals(1, tracker.opponentRevealedSize());
        assertEquals(1, tracker.aggregateSize());
        assertEquals(64, tracker.getSeenHistoryCap());
        InformationSetV1 set = new InformationSetV1();
        tracker.copyInto(set);
        assertEquals("EXAMINE_HAND", set.opponentRevealed.get(0).get("how"));
        assertEquals("secret_opp_bp", set.opponentRevealed.get(0).get("blueprintId"));
        assertEquals("SEEN_NOT_RECYCLED", set.seenHistory.get(0).get("recycleHint"));
        assertFalse(set.seededFromExactOpponentDeck);

        InformationSetTracker other = new InformationSetTracker(LIGHT);
        InformationSetGameStateListener otherListener = new InformationSetGameStateListener(other);
        otherListener.decisionRequired(DARK, shownToUs);
        assertEquals(0, other.opponentRevealedSize());
        assertEquals(0, other.seenHistorySize());
        InformationSetV1 hidden = new InformationSetV1();
        other.copyInto(hidden);
        assertFalse(hidden.toMap().toString().contains(SECRET_OPP_TITLE));
        assertFalse(hidden.seededFromExactOpponentDeck);
    }

    @Test
    public void revealedSabaccHandIsRecordedAndUnrevealedSabaccIsNot() {
        InformationSetTracker tracker = new InformationSetTracker(LIGHT);
        InformationSetGameStateListener listener = new InformationSetGameStateListener(tracker);
        GameState gs = mockGameStateWithHands();

        PhysicalCard revealed = mockCard("8_008", "Sabacc Opponent Card", CardCategory.INTERRUPT,
                Side.DARK, DARK, Zone.REVEALED_SABACC_HAND, 2f);
        listener.cardCreated(revealed, gs, false);
        listener.cardCreated(revealed, gs, true);

        PhysicalCard hidden = mockCard("secret_sabacc_bp", SECRET_OPP_TITLE, CardCategory.INTERRUPT,
                Side.DARK, DARK, Zone.SABACC_HAND, 3f);
        listener.cardCreated(hidden, gs, false);

        assertEquals(1, tracker.opponentRevealedSize());
        assertEquals(1, tracker.seenHistorySize());
        assertEquals(1, tracker.aggregateSize());
        InformationSetV1 set = new InformationSetV1();
        tracker.copyInto(set);
        assertEquals("SABACC_REVEAL", set.opponentRevealed.get(0).get("how"));
        assertEquals("8_008", set.opponentRevealed.get(0).get("blueprintId"));
        assertFalse(set.toMap().toString().contains(SECRET_OPP_TITLE));
        assertFalse(set.seededFromExactOpponentDeck);
    }

    @Test
    public void publicUsedAndLostIdentitiesTightenRemainingEstimate() {
        GameState gs = mockGameStateWithHands();
        PhysicalCard lost = mockCard("2_001", "Public Lost", CardCategory.INTERRUPT,
                Side.LIGHT, LIGHT, Zone.LOST_PILE, 6f);
        PhysicalCard usedTop = mockCard("4_003", "Public Used Top", CardCategory.INTERRUPT,
                Side.LIGHT, LIGHT, Zone.TOP_OF_USED_PILE, 5f);
        PhysicalCard usedBuried = mockCard("4_002", "Buried Used", CardCategory.INTERRUPT,
                Side.LIGHT, LIGHT, Zone.USED_PILE, 7f);
        PhysicalCard oppLost = mockCard("opp_lost_bp", SECRET_OPP_TITLE, CardCategory.INTERRUPT,
                Side.DARK, DARK, Zone.LOST_PILE, 6f);

        when(gs.getLostPile(LIGHT)).thenReturn(Collections.singletonList(lost));
        when(gs.getLostPile(DARK)).thenReturn(Collections.singletonList(oppLost));
        when(gs.getUsedPile(LIGHT)).thenReturn(java.util.Arrays.asList(usedTop, usedBuried));
        when(gs.isLostPileTurnedOver(LIGHT)).thenReturn(false);
        when(gs.isLostPileTurnedOver(DARK)).thenReturn(false);
        when(gs.isUsedPilesTurnedOver()).thenReturn(false);

        InformationSetTracker tracker = new InformationSetTracker(LIGHT);
        Map<String, Integer> prior = new LinkedHashMap<>();
        prior.put("2_001", 1);
        prior.put("4_002", 1);
        prior.put("4_003", 1);
        tracker.setOwnDeckPrior(prior);
        Map<String, Float> hints = new LinkedHashMap<>();
        hints.put("2_001", 6f);
        hints.put("4_002", 7f);
        hints.put("4_003", 5f);
        tracker.setOwnBlueprintDestinyHints(hints);

        InformationSetV1 set = InformationSetEncoder.from(gs, LIGHT, null, tracker, "premiere_anh");
        assertFalse(set.seededFromExactOpponentDeck);
        assertEquals(1f, set.scalars.get("ownHighDestinyRemainingEst"), 0.01f);
        assertEquals(1f, set.scalars.get("ownRemainingEstimateUnique"), 0.01f);
        String piles = String.valueOf(set.ownPublicPiles);
        assertTrue(piles.contains("2_001"));
        assertTrue(piles.contains("4_003"));
        assertFalse(piles.contains("4_002"));
        assertFalse(set.toMap().toString().contains(SECRET_OPP_TITLE));
        assertTrue(set.extractionGaps.contains("buriedOwnUsedIdentitiesNotPublic"));
        assertFalse(set.extractionGaps.contains("ownHighDestinyRemainingEstIgnoresUsedLost"));
    }

    private static GameState mockGameStateWithHands() {
        GameState gs = mock(GameState.class);
        when(gs.getDarkPlayer()).thenReturn(DARK);
        when(gs.getLightPlayer()).thenReturn(LIGHT);
        when(gs.getOpponent(LIGHT)).thenReturn(DARK);
        when(gs.getOpponent(DARK)).thenReturn(LIGHT);
        when(gs.getSide(LIGHT)).thenReturn(Side.LIGHT);
        when(gs.getSide(DARK)).thenReturn(Side.DARK);
        when(gs.getCurrentPhase()).thenReturn(Phase.CONTROL);
        when(gs.getPlayerLifeForce(LIGHT)).thenReturn(28);
        when(gs.getPlayerLifeForce(DARK)).thenReturn(31);
        when(gs.getPlayersLatestTurnNumber(LIGHT)).thenReturn(3);
        when(gs.getPlayersLatestTurnNumber(DARK)).thenReturn(3);
        when(gs.getReserveDeckSize(LIGHT)).thenReturn(5);
        when(gs.getReserveDeckSize(DARK)).thenReturn(4);
        when(gs.getForcePileSize(LIGHT)).thenReturn(2);
        when(gs.getForcePileSize(DARK)).thenReturn(1);
        when(gs.getUsedPile(LIGHT)).thenReturn(Collections.emptyList());
        when(gs.getUsedPile(DARK)).thenReturn(Collections.emptyList());
        when(gs.getLostPile(LIGHT)).thenReturn(Collections.emptyList());
        when(gs.getLostPile(DARK)).thenReturn(Collections.emptyList());
        when(gs.getPlayersTotalForceGeneration(LIGHT)).thenReturn(3f);
        when(gs.getPlayersTotalForceGeneration(DARK)).thenReturn(2f);
        when(gs.isDuringForceDrain()).thenReturn(false);
        when(gs.isDuringBattle()).thenReturn(false);
        when(gs.getObjectivePlayed(LIGHT)).thenReturn(null);
        when(gs.getObjectivePlayed(DARK)).thenReturn(null);
        when(gs.getLocationsInOrder()).thenReturn(Collections.emptyList());

        PhysicalCard ownHandCard = mockCard("1_140", "Sorry About The Mess", CardCategory.INTERRUPT,
                Side.LIGHT, LIGHT, Zone.HAND, 4f);
        PhysicalCard oppHandCard = mockCard("secret_opp_bp", SECRET_OPP_TITLE, CardCategory.INTERRUPT,
                Side.DARK, DARK, Zone.HAND, 5f);
        PhysicalCard inPlayChar = mockCard("1_304", "Luke Skywalker", CardCategory.CHARACTER,
                Side.LIGHT, LIGHT, Zone.AT_LOCATION, 1f);

        when(gs.getHand(LIGHT)).thenReturn(Collections.singletonList(ownHandCard));
        when(gs.getHand(DARK)).thenReturn(Arrays.asList(oppHandCard, oppHandCard));
        when(gs.getAllPermanentCards()).thenReturn(Collections.singletonList(inPlayChar));
        return gs;
    }

    private static PhysicalCard mockCard(String blueprintId, String title, CardCategory category,
                                         Side side, String owner, Zone zone, float destiny) {
        PhysicalCard card = mock(PhysicalCard.class);
        SwccgCardBlueprint bp = mock(SwccgCardBlueprint.class);
        when(bp.getCardCategory()).thenReturn(category);
        when(bp.getDestiny()).thenReturn(destiny);
        when(bp.getSide()).thenReturn(side);
        when(card.getBlueprint()).thenReturn(bp);
        when(card.getBlueprintId(true)).thenReturn(blueprintId);
        when(card.getTitle()).thenReturn(title);
        when(card.getOwner()).thenReturn(owner);
        when(card.getZone()).thenReturn(zone);
        when(card.getDestinyValueToUse()).thenReturn(destiny);
        return card;
    }
}
