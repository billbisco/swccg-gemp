package com.gempukku.swccgo.ai.features;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class InformationSetV1Test {

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
}
