package com.gempukku.swccgo.async.internal;

import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.service.AuthService;

import java.lang.reflect.Constructor;

/** Builds AuthService.AuthResult (package-private constructor, final class) for mocks. */
final class AuthResults {
    private AuthResults() {
    }

    static AuthService.AuthResult of(AuthService.Status status, Player player) {
        try {
            Constructor<AuthService.AuthResult> c = AuthService.AuthResult.class
                    .getDeclaredConstructor(AuthService.Status.class, Player.class);
            c.setAccessible(true);
            return c.newInstance(status, player);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
