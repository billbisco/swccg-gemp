package com.gempukku.swccgo.service;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LoggedUserHolderTests {
    @Test
    public void sessionIdsAreLongRandomAndUnique() {
        LoggedUserHolder holder = new LoggedUserHolder();
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String id = holder.logUser("player" + i);
            assertEquals(LoggedUserHolder.SESSION_ID_LENGTH, id.length());
            assertTrue(id.matches("^[A-Za-z0-9]+$"));
            ids.add(id);
        }
        assertEquals(1000, ids.size());
    }

    @Test
    public void everyIdleSessionExpiresInOnePassNotJustTheFirst() {
        LoggedUserHolder holder = new LoggedUserHolder();
        long now = System.currentTimeMillis();
        String a = holder.logUser("Alice");
        String b = holder.logUser("Bob");
        String c = holder.logUser("Carol");
        holder.setLastAccessForTest(a, now - 11 * 60 * 1000);
        holder.setLastAccessForTest(b, now - 11 * 60 * 1000);

        assertEquals(2, holder.expireSessions(now));
        assertNull(holder.getLoggedUser(a));
        assertNull(holder.getLoggedUser(b));
        assertEquals("Carol", holder.getLoggedUser(c));
    }

    @Test
    public void forcedLogoutEndsAllSessionsOfThatPlayer() {
        LoggedUserHolder holder = new LoggedUserHolder();
        String s1 = holder.logUser("Bill");
        String s2 = holder.logUser("Bill");
        holder.forceLogoutUser("Bill");
        assertNull(holder.getLoggedUser(s1));
        assertNull(holder.getLoggedUser(s2));
        assertEquals(0, holder.expireSessions(Long.MAX_VALUE / 2), "no stale access entries left behind");
    }
}
