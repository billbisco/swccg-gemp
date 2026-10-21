package com.gempukku.swccgo.guest;

import com.gempukku.swccgo.ai.models.AdvancedAi;
import com.gempukku.swccgo.ai.models.BeginnerAi;
import com.gempukku.swccgo.ai.SwccgAiController;
import com.gempukku.swccgo.game.Player;

import java.util.Locale;

/** The bots offered for Play now. Both are deterministic, which saved games rely on. */
public enum PlayNowBot {
    OZZEL("ozzel", Player.AI_BEGINNER_ID, "BEGINNER"),
    YODA("yoda", Player.AI_ADVANCED_ID, "ADVANCED");

    private final String _code;
    private final String _playerId;
    private final String _skill;

    PlayNowBot(String code, String playerId, String skill) {
        _code = code;
        _playerId = playerId;
        _skill = skill;
    }

    /** Short name used in URLs and saves ("ozzel", "yoda"). */
    public String getCode() {
        return _code;
    }

    public String getPlayerId() {
        return _playerId;
    }

    /** Hall AI skill name. */
    public String getSkill() {
        return _skill;
    }

    public SwccgAiController newController() {
        return this == YODA ? new AdvancedAi() : new BeginnerAi();
    }

    /** @return the bot, or null if the code is not one of ours */
    public static PlayNowBot fromCode(String code) {
        if (code == null) {
            return null;
        }
        String c = code.trim().toLowerCase(Locale.ROOT);
        for (PlayNowBot bot : values()) {
            if (bot._code.equals(c)) {
                return bot;
            }
        }
        return null;
    }
}
