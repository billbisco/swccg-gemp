package com.gempukku.swccgo.async.internal.e2e;

import com.gempukku.swccgo.async.internal.InternalAuthServer;
import com.gempukku.swccgo.db.CachedPlayerDAO;
import com.gempukku.swccgo.db.DbAccess;
import com.gempukku.swccgo.db.DbPlayerDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.security.PasswordHasher;
import com.gempukku.swccgo.security.PasswordSettings;
import com.gempukku.swccgo.service.AuthService;
import com.gempukku.swccgo.service.LoginThrottle;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.json.simple.JSONObject;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

/**
 * Live end-to-end harness for the wiki login check (not a unit test; run by
 * swccg-wiki ops/wiki-config/extensions/GempAuth/tests/e2e/run-e2e.sh against a throwaway MariaDB).
 *
 * <p>Wires PlayerDAO and AuthService the same way DaoBuilder and ServerBuilder do, takes AuthService
 * out of the object map like SwccgoAsyncServer, seeds test players through the real DAO/AuthService,
 * writes their ids and the one-time reset code to a JSON file, then runs the real private listener
 * (INTERNAL_AUTH_PORT / GEMP_INTERNAL_AUTH_SECRET from the environment).</p>
 *
 * <p>Usage: {@code WikiAuthE2EServer <seed-output.json>}. Never point it at a real database: it inserts players.</p>
 */
public final class WikiAuthE2EServer {
    public static final String PASSWORD = "Correct-Horse-9";
    public static final String GEMP_ONLY_ADMIN_PASSWORD = "Gemp-Admin-pw1";
    public static final String SEED_IP = "198.51.100.77";

    private WikiAuthE2EServer() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1)
            throw new IllegalArgumentException("usage: WikiAuthE2EServer <seed-output.json>");

        // Same wiring as DaoBuilder.CreateDatabaseAccessObjects + ServerBuilder.CreateServices.
        Map<Type, Object> objects = new HashMap<>();
        DbAccess dbAccess = new DbAccess();
        objects.put(PlayerDAO.class, new CachedPlayerDAO(new DbPlayerDAO(dbAccess)));
        objects.put(AuthService.class, new AuthService((PlayerDAO) objects.get(PlayerDAO.class),
                new PasswordHasher(PasswordSettings.iterations()), new LoginThrottle()));
        PlayerDAO dao = (PlayerDAO) objects.get(PlayerDAO.class);
        AuthService auth = (AuthService) objects.get(AuthService.class);

        JSONObject seed = new JSONObject();
        // OK: registered as "e2eCanon"; the wiki logs in typing "E2ECANON".
        seed.put("canon_id", register(auth, dao, "e2eCanon", PASSWORD));
        // Blank stored hash (pre-migration admin-reset state).
        seed.put("blank_id", register(auth, dao, "e2eblank", PASSWORD));
        sql(dbAccess, "UPDATE player SET password='' WHERE name='e2eblank'");
        // One-time reset code issued by an admin.
        seed.put("reset_id", register(auth, dao, "e2ereset", PASSWORD));
        seed.put("reset_code", auth.issueResetCode("e2ereset", "e2eadmin"));
        // Account already locked by failed game logins (same AuthService, so same LoginThrottle).
        seed.put("throt_id", register(auth, dao, "e2ethrot", PASSWORD));
        for (int i = 0; i < LoginThrottle.DEFAULT_MAX_ACCOUNT_FAILURES; i++) {
            AuthService.Status s = auth.verifyCredentials("e2ethrot", "wrong-" + i, SEED_IP).getStatus();
            if (s != AuthService.Status.INVALID)
                throw new IllegalStateException("seed failure " + i + " was " + s);
        }
        // Endpoint second-layer limit target.
        seed.put("burst_id", register(auth, dao, "e2eburst", PASSWORD));
        // A GEMP account whose name case-folds onto a local-only wiki account ("Lobot" on the wiki).
        // ("admin" itself is a reserved name on GEMP.)
        seed.put("lobot_id", register(auth, dao, "LOBOT", GEMP_ONLY_ADMIN_PASSWORD));
        // Legacy unsalted SHA-256: must log in and be rehashed to PBKDF2.
        sql(dbAccess, "INSERT INTO player (name, password, type, create_ip) VALUES ('e2elegacy', '"
                + PasswordHasher.legacySha256(PASSWORD) + "', 'u', '" + SEED_IP + "')");
        seed.put("legacy_id", dao.findPlayerForLogin("e2elegacy").getId());

        Files.writeString(Path.of(args[0]), seed.toJSONString(), StandardCharsets.UTF_8);
        System.out.println("E2E seeded " + Path.of(args[0]));

        Channel ch = InternalAuthServer.startIfConfigured(new NioEventLoopGroup(1), new NioEventLoopGroup(2), auth);
        if (ch == null)
            throw new IllegalStateException("listener not started: set INTERNAL_AUTH_PORT and GEMP_INTERNAL_AUTH_SECRET");
        System.out.println("E2E listener ready");
        ch.closeFuture().sync();
    }

    private static int register(AuthService auth, PlayerDAO dao, String name, String password) throws Exception {
        if (!auth.register(name, password, SEED_IP))
            throw new IllegalStateException("could not register " + name);
        Player p = dao.findPlayerForLogin(name);
        return p.getId();
    }

    private static void sql(DbAccess db, String statement) throws Exception {
        try (Connection c = db.getDataSource().getConnection(); PreparedStatement ps = c.prepareStatement(statement)) {
            ps.executeUpdate();
        }
    }
}
