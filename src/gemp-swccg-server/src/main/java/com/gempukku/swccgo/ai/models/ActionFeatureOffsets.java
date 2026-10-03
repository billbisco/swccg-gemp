package com.gempukku.swccgo.ai.models;

/**
 * Keyword tables and the index where grounded action features begin.
 *
 * <p>This class does not reference {@link LinearPolicyAi} or
 * {@link LinearActionFeatures}. Those two classes initialize each other
 * otherwise: grounded features need the choice-table length, and the policy
 * width needs the grounded length. Whichever class loads first used to read
 * the other's field as 0, so {@code ACTION_FEAT_DIM} collapsed and every
 * weight vector was short. Both classes read the numbers from here instead.
 *
 * <p>Not one weight per card name.
 */
public final class ActionFeatureOffsets {

    public static final int AF_KIND = 24;
    /**
     * AdvancedAi action-table keywords, {@code ACTION_WEIGHTS} then
     * {@code ACTION_PENALTIES}. Strings only. The numeric scores stay in
     * {@link AdvancedAi}; this policy does not copy them. Choice-table
     * strings are {@link #CHOICE_KIND_KEYWORDS}, after this block.
     */
    public static final String[] DECISION_KIND_KEYWORDS = {
            "force drain",
            "initiate battle",
            "battle",
            "weapon",
            "fire",
            "deploy",
            "play",
            "move",
            "activate",
            "retrieve",
            "draw",
            "steal",
            "capture",
            "download",
            "search",
            "react",
            "cancel",
            "take into hand",
            "pass",
            "forfeit",
            "lose",
            "place in lost pile",
            "place in used pile",
            "return to hand",
            "sacrifice",
            "revert"
    };
    public static final int AF_KIND_COUNT = DECISION_KIND_KEYWORDS.length;
    /**
     * First choice-table feature. Sits after the action-keyword block
     * (indices 24..49) so those keyword features do not move.
     * Strings are AdvancedAi {@code CHOICE_WEIGHTS} then {@code CHOICE_PENALTIES}.
     * The numeric scores stay in {@link AdvancedAi}; this policy does not copy them.
     * Not a per-card-name embedding.
     */
    public static final int AF_CHOICE = AF_KIND + AF_KIND_COUNT;
    public static final String[] CHOICE_KIND_KEYWORDS = {
            "draw",
            "retrieve",
            "deploy",
            "battle destiny",
            "weapon destiny",
            "activate",
            "force drain",
            "initiate",
            "capture",
            "steal",
            "download",
            "use",
            "yes",
            "lose",
            "forfeit",
            "lost pile",
            "used pile",
            "return to hand",
            "neither",
            "cancel",
            "pass"
    };
    public static final int AF_CHOICE_COUNT = CHOICE_KIND_KEYWORDS.length;
    /** First grounded slot. Choice-table features occupy the indices just before this. */
    public static final int GROUNDED_START = AF_CHOICE + AF_CHOICE_COUNT;

    private ActionFeatureOffsets() {
    }
}
