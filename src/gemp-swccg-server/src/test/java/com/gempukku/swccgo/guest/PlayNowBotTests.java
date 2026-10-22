package com.gempukku.swccgo.guest;

import com.gempukku.swccgo.game.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PlayNowBotTests {

    @Test
    public void BeginnerIsOzzelBotAndHarderIsYodaBot() {
        assertEquals(PlayNowBot.OZZEL, PlayNowBot.fromDifficulty("beginner"));
        assertEquals(PlayNowBot.OZZEL, PlayNowBot.fromDifficulty("BEGINNER"));
        assertEquals(PlayNowBot.YODA, PlayNowBot.fromDifficulty("harder"));
        assertEquals(PlayNowBot.YODA, PlayNowBot.fromDifficulty("hard"));
        assertEquals(PlayNowBot.YODA, PlayNowBot.fromDifficulty("advanced"));
        assertNull(PlayNowBot.fromDifficulty("elite"));
        assertNull(PlayNowBot.fromDifficulty(null));
        assertEquals(Player.AI_BEGINNER_ID, PlayNowBot.OZZEL.getPlayerId());
        assertEquals(Player.AI_ADVANCED_ID, PlayNowBot.YODA.getPlayerId());
    }

    @Test
    public void BotParamWinsOverDifficulty() {
        assertEquals(PlayNowBot.YODA, PlayNowBot.fromChoice("yoda", "beginner"));
        assertEquals(PlayNowBot.OZZEL, PlayNowBot.fromChoice(null, "beginner"));
        assertEquals(PlayNowBot.YODA, PlayNowBot.fromChoice("", "harder"));
        assertNull(PlayNowBot.fromChoice("rando", "elite"));
    }
}
