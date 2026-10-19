package com.gempukku.swccgo.async.internal.e2e;

import com.gempukku.swccgo.async.internal.InternalAuthServer;
import com.gempukku.swccgo.db.CachedPlayerDAO;
import com.gempukku.swccgo.db.DbAccess;
import com.gempukku.swccgo.db.DbPlayerDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.security.PasswordHasher;
import com.gempukku.swccgo.security.PasswordSettings;
import com.gempukku.swccgo.service.AuthService;
import com.gempukku.swccgo.service.LoginThrottle;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Staging-only auth listener: the PR's private listener with a real AuthService and DbPlayerDAO,
 * without the rest of the game server (no card library, no hall), so it is cheap to run next to
 * production services. Wired like DaoBuilder/ServerBuilder; AuthService is taken from the object map.
 *
 * <p>Optional env STAGING_PLAYER + STAGING_PLAYER_PASSWORD_FILE: registers that player through
 * AuthService if it does not exist yet (for smoke tests). Never point this at a production database.</p>
 */
public final class WikiAuthStagingServer {
    private WikiAuthStagingServer() {
    }

    public static void main(String[] args) throws Exception {
        Map<Type, Object> objects = new HashMap<>();
        objects.put(PlayerDAO.class, new CachedPlayerDAO(new DbPlayerDAO(new DbAccess())));
        objects.put(AuthService.class, new AuthService((PlayerDAO) objects.get(PlayerDAO.class),
                new PasswordHasher(PasswordSettings.iterations()), new LoginThrottle()));
        PlayerDAO dao = (PlayerDAO) objects.get(PlayerDAO.class);
        AuthService auth = (AuthService) objects.get(AuthService.class);

        String name = System.getenv("STAGING_PLAYER");
        String pwFile = System.getenv("STAGING_PLAYER_PASSWORD_FILE");
        if (name != null && pwFile != null && dao.findPlayerForLogin(name) == null) {
            String pw = Files.readString(Path.of(pwFile), StandardCharsets.UTF_8).trim();
            System.out.println("staging: registered " + name + " = " + auth.register(name, pw, "127.0.0.1"));
        }

        Channel ch = InternalAuthServer.startIfConfigured(new NioEventLoopGroup(1), new NioEventLoopGroup(2), auth);
        if (ch == null)
            throw new IllegalStateException("listener not started: set INTERNAL_AUTH_PORT and GEMP_INTERNAL_AUTH_SECRET");
        System.out.println("staging: wiki-auth listener ready");
        ch.closeFuture().sync();
    }
}
