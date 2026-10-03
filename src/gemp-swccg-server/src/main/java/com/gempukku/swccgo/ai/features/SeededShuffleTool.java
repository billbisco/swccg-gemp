package com.gempukku.swccgo.ai.features;

import java.util.Random;

/**
 * Debug/test hook for a reproducible shuffle stream.
 *
 * <p><b>Not wired into GEMP's deck shuffle.</b> Training and strength gates must
 * use {@link SeedMode#RANDOM}. A fixed seed may replay one game in a unit test.
 * A green fixed-seed suite is not promotion evidence.
 */
public final class SeededShuffleTool {

    public enum SeedMode {
        /** Real collect, self-play, and strength gates. */
        RANDOM,
        /** Unit tests and one-off debug replays only. */
        FIXED
    }

    private SeededShuffleTool() {
    }

    public static Random open(SeedMode mode, Long fixedSeed) {
        if (mode == null) {
            throw new IllegalArgumentException("seed mode required");
        }
        if (mode == SeedMode.RANDOM) {
            return new Random();
        }
        if (fixedSeed == null) {
            throw new IllegalArgumentException("FIXED mode requires a seed");
        }
        return new Random(fixedSeed);
    }

    /** Training and gates. Ignores any seed argument on purpose. */
    public static Random openForTraining() {
        return open(SeedMode.RANDOM, null);
    }
}
