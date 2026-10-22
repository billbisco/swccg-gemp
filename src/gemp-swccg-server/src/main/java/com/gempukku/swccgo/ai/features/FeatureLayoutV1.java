package com.gempukku.swccgo.ai.features;

/**
 * Frozen index map for {@code StateFeaturesV1.packed}.
 * Indices 0..127 match the original layout (including the zero pad).
 * {@link #TABLE_START} appends public table facts. Must match
 * {@code gemp-swccg-trainer/schemas/feature_layout_v1.json}.
 * Bags are not stored in this vector. Not one weight per card name.
 */
public final class FeatureLayoutV1 {

    public static final int SCHEMA_VERSION = 1;
    /** Frozen cap inside the ratified 32–64 range. */
    public static final int SEEN_HISTORY_CAP = 64;
    public static final String FORBIDDEN_OPPONENT_DECK_KEY = "opponentDeckPriorKnown";

    public static final int PHASE_ONE_HOT = 0;
    public static final int PHASE_COUNT = 9;
    public static final int DECISION_ONE_HOT = 9;
    public static final int DECISION_COUNT = 7;
    public static final int SCALARS = 16;
    public static final int SCALAR_COUNT = 45;
    public static final int BOARD = 61;
    public static final int BOARD_COUNT = 16;
    public static final int PROXIES = 77;
    public static final int PROXY_COUNT = 6;
    public static final int FLAGS = 83;
    public static final int FLAG_COUNT = 8;
    public static final int HAND_HIST = 91;
    public static final int HAND_HIST_COUNT = 16;
    public static final int DESTINY_HIST = 107;
    public static final int DESTINY_HIST_COUNT = 8;
    public static final int OPP_REVEALED_NORM = 115;
    public static final int HIGH_DESTINY_REMAINING = 116;
    public static final int PAD_START = 117;

    /** First appended table-fact index. 0..127, including the zero pad, do not move. */
    public static final int TABLE_START = 128;
    /** Power at the first this-many locations in zone-index order. The rest is an overflow sum. */
    public static final int LOCATION_POWER_SLOTS = 8;
    /**
     * Public table facts appended after the original 128. Not one slot per card name.
     * Location power is {@link #LOCATION_POWER_SLOTS} pairs (decider, opponent) plus overflow.
     */
    public static final String[] TABLE_LABELS = {
            "darkFrozenForce", "lightFrozenForce", "deciderFrozenForce", "opponentFrozenForce",
            "darkSabaccHandSize", "lightSabaccHandSize", "deciderSabaccHandSize", "opponentSabaccHandSize",
            "deciderSabaccTotal", "opponentSabaccTotal",
            "darkOutOfPlay", "lightOutOfPlay", "deciderOutOfPlay", "opponentOutOfPlay",
            "deciderRaceTotal", "opponentRaceTotal",
            "deciderPoliticsTotal", "opponentPoliticsTotal",
            "loc0DeciderPower", "loc0OpponentPower",
            "loc1DeciderPower", "loc1OpponentPower",
            "loc2DeciderPower", "loc2OpponentPower",
            "loc3DeciderPower", "loc3OpponentPower",
            "loc4DeciderPower", "loc4OpponentPower",
            "loc5DeciderPower", "loc5OpponentPower",
            "loc6DeciderPower", "loc6OpponentPower",
            "loc7DeciderPower", "loc7OpponentPower",
            "overflowLocationCount", "overflowDeciderPower", "overflowOpponentPower",
            "deciderBattlePower", "opponentBattlePower",
            "deciderBattleDestiny", "opponentBattleDestiny",
            "deciderBattleDamage", "opponentBattleDamage",
            "deciderBattleAttrition", "opponentBattleAttrition",
            "deciderBattleImmune", "opponentBattleImmune",
            "deciderAttackTotal", "opponentAttackTotal",
            "deciderDuelTotal", "opponentDuelTotal",
            "deciderLightsaberTotal", "opponentLightsaberTotal",
            "deciderAttachedCount", "opponentAttachedCount",
            "deciderStackedOnCount", "opponentStackedOnCount",
            "darkFaceDownStackSize", "lightFaceDownStackSize",
            "deciderFaceDownStackSize", "opponentFaceDownStackSize",
            "darkSideOfTableFaceDownSize", "lightSideOfTableFaceDownSize",
            "deciderFrozenCount", "opponentFrozenCount",
            "deciderSidewaysCount", "opponentSidewaysCount",
            "deciderInBattleCount", "opponentInBattleCount",
            "decisionCountThisGame",
            "promptMin", "promptMax",
            "promptYourTurn", "promptAutoPassEligible", "promptNoLongDelay", "promptRevertEligible",
            "promptAsButtons", "promptReturnAnyChange",
            "promptSelectableCount", "promptPreselectedCount", "promptHorizontalCount",
            "promptHasDefault", "promptParamCount"
    };
    public static final int TABLE_COUNT = TABLE_LABELS.length;
    public static final int PACKED_DIM = TABLE_START + TABLE_COUNT;

    public static final String[] PHASES = {
            "PLAY_STARTING_CARDS", "ACTIVATE", "CONTROL", "DEPLOY", "BATTLE",
            "MOVE", "DRAW", "END_OF_TURN", "BETWEEN_TURNS"
    };

    public static final String[] DECISION_TYPES = {
            "EMPTY", "INTEGER", "MULTIPLE_CHOICE", "ARBITRARY_CARDS",
            "CARD_ACTION_CHOICE", "ACTION_CHOICE", "CARD_SELECTION"
    };

    private FeatureLayoutV1() {
    }

    public static int phaseIndex(String phase) {
        return indexOf(PHASES, phase);
    }

    public static int decisionIndex(String decisionType) {
        return indexOf(DECISION_TYPES, decisionType);
    }

    private static int indexOf(String[] values, String needle) {
        if (needle == null) {
            return -1;
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(needle)) {
                return i;
            }
        }
        return -1;
    }
}
