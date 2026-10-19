package com.gempukku.swccgo.security;

import com.gempukku.swccgo.db.PlayerDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * One-shot, idempotent conversion of every remaining unsalted SHA-256 password hash to the wrapped
 * {@code pbkdf2-sha256-legacy} form, so the database never holds an unsalted hash at rest.
 * Players keep their passwords; their first login after this replaces the wrapped hash with a plain one.
 *
 * <p>Runs in a background thread at server start (a few hundred ms per account), and again on every
 * start, where it finds nothing to do. Each row is replaced compare-and-set, so a login or reset that
 * changed the hash meanwhile wins. Backups taken before this ran still hold the old hashes.</p>
 */
public final class LegacyHashMigration {
    private static final Logger LOG = LogManager.getLogger(LegacyHashMigration.class);

    private LegacyHashMigration() {
    }

    /** @return the number of hashes converted */
    public static int run(PlayerDAO playerDAO, PasswordHasher hasher) {
        int converted = 0;
        int failed = 0;
        Map<Integer, String> legacy;
        try {
            legacy = playerDAO.getLegacyPasswordHashes();
        } catch (Exception exp) {
            LOG.error("Legacy password hash migration could not list accounts", exp);
            return 0;
        }
        for (Map.Entry<Integer, String> e : legacy.entrySet()) {
            if (!PasswordHasher.isLegacySha256(e.getValue()))
                continue;
            try {
                if (playerDAO.updatePasswordHash(e.getKey(), e.getValue(), hasher.wrapLegacy(e.getValue())))
                    converted++;
            } catch (Exception exp) {
                failed++;
                LOG.error("Legacy password hash migration failed for player id " + e.getKey(), exp);
            }
        }
        if (converted > 0 || failed > 0)
            LOG.info("Legacy password hash migration: " + converted + " converted, " + failed + " failed");
        return converted;
    }

    /** Starts {@link #run} on a daemon thread so server start-up is not delayed. */
    public static void runInBackground(PlayerDAO playerDAO, PasswordHasher hasher) {
        Thread t = new Thread(() -> run(playerDAO, hasher), "legacy-hash-migration");
        t.setDaemon(true);
        t.start();
    }
}
