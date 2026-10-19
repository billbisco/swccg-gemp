package com.gempukku.swccgo.security;

import com.gempukku.swccgo.common.ApplicationConfiguration;

/**
 * Reads password hashing settings from gemp-swccg.properties.
 */
public final class PasswordSettings {
    private PasswordSettings() {
    }

    /** {@code password.pbkdf2.iterations}, default {@link PasswordHasher#DEFAULT_ITERATIONS}. */
    public static int iterations() {
        try {
            String value = ApplicationConfiguration.getProperty("password.pbkdf2.iterations");
            if (value != null && !value.isBlank())
                return Math.max(100_000, Integer.parseInt(value.trim()));
        } catch (RuntimeException ignored) {
            // fall through to the default
        }
        return PasswordHasher.DEFAULT_ITERATIONS;
    }
}
