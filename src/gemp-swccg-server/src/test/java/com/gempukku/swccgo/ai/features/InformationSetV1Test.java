package com.gempukku.swccgo.ai.features;

import com.gempukku.swccgo.ai.HeadlessDecisionTraceWriter;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.AwaitingDecisionType;
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
        assertFalse(line.contains(SECRET_OPP_TITLE));
        Files.deleteIfExists(out);
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
        return card;
    }
}
