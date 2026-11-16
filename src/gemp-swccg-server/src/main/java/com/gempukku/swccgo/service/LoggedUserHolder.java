package com.gempukku.swccgo.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LoggedUserHolder {
    private final long _loggedUserExpireLength = 1000 * 60 * 10; // 10 minutes session length
    private final long _expireCheckInterval = 1000 * 60; // check every minute

    private final Map<String, String> _sessionIdsToUsers = new HashMap<String, String>();
    private final Multimap<String, String> _usersToSessionIds = HashMultimap.create();

    /** #504/#506: the ID token and sign-in session id (sid) each signed-in-with-swccg.com session got. Server-side only. */
    private final Map<String, String[]> _sso = new HashMap<String, String[]>();
    private final Multimap<String, String> _sidToSessionIds = HashMultimap.create();

    private final Map<String, Long> _lastAccess = Collections.synchronizedMap(new HashMap<String, Long>());
    private final ReadWriteLock _readWriteLock = new ReentrantReadWriteLock();
    private ClearExpiredRunnable _clearExpiredRunnable;

    public void start() {
        _clearExpiredRunnable = new ClearExpiredRunnable();
        Thread thr = new Thread(_clearExpiredRunnable);
        thr.start();
    }

    public String getLoggedUser(String sessionId) {
        _readWriteLock.readLock().lock();
        try {
            String loggedUser = _sessionIdsToUsers.get(sessionId);
            if (loggedUser != null) {
                _lastAccess.put(sessionId, System.currentTimeMillis());
                return loggedUser;
            }
        } finally {
            _readWriteLock.readLock().unlock();
        }
        return null;
    }

    public String logUser(String userName) {
        _readWriteLock.writeLock().lock();
        try {
            String userValue = insertValueForUser(userName);
            _lastAccess.put(userValue, System.currentTimeMillis());
            return userValue;
        } finally {
            _readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Signs a user in from swccg.com and keeps the ID token (sent back as id_token_hint at sign-out, #504)
     * and its sid (so a back-channel logout ends only the matching sessions, #506).
     */
    public String logUser(String userName, String idToken, String sid) {
        _readWriteLock.writeLock().lock();
        try {
            String userValue = insertValueForUser(userName);
            _lastAccess.put(userValue, System.currentTimeMillis());
            if (idToken != null || sid != null) {
                _sso.put(userValue, new String[]{idToken, sid});
                if (sid != null && !sid.isEmpty())
                    _sidToSessionIds.put(sid, userValue);
            }
            return userValue;
        } finally {
            _readWriteLock.writeLock().unlock();
        }
    }

    /** The ID token kept for this Play session, or null (classic sign-in, restart, or expired). */
    public String getIdToken(String sessionId) {
        if (sessionId == null)
            return null;
        _readWriteLock.readLock().lock();
        try {
            String[] v = _sso.get(sessionId);
            return v == null ? null : v[0];
        } finally {
            _readWriteLock.readLock().unlock();
        }
    }

    /** Ends one Play session (this device's Sign out). @return whether it existed */
    public boolean logoutSession(String sessionId) {
        if (sessionId == null)
            return false;
        _readWriteLock.writeLock().lock();
        try {
            return removeSession(sessionId);
        } finally {
            _readWriteLock.writeLock().unlock();
        }
    }

    /** Ends the Play sessions signed in from one swccg.com sign-in session (#506). @return how many */
    public int forceLogoutSid(String sid) {
        _readWriteLock.writeLock().lock();
        try {
            int n = 0;
            for (String sessionId : new HashSet<String>(_sidToSessionIds.get(sid)))
                if (removeSession(sessionId))
                    n++;
            _sidToSessionIds.removeAll(sid);
            return n;
        } finally {
            _readWriteLock.writeLock().unlock();
        }
    }

    /** Caller holds the write lock. */
    private boolean removeSession(String sessionId) {
        String userName = _sessionIdsToUsers.remove(sessionId);
        if (userName != null)
            _usersToSessionIds.remove(userName, sessionId);
        _lastAccess.remove(sessionId);
        dropSso(sessionId);
        return userName != null;
    }

    private void dropSso(String sessionId) {
        String[] v = _sso.remove(sessionId);
        if (v != null && v[1] != null)
            _sidToSessionIds.remove(v[1], sessionId);
    }

    /** Ends every Play session of a user (account-wide: block, reset, Sign out everywhere). @return how many */
    public int forceLogoutUser(String userName) {
        _readWriteLock.writeLock().lock();
        try {
            final Collection<String> sessionIds = new HashSet<String>(_usersToSessionIds.get(userName));
            for (String sessionId : sessionIds)
                removeSession(sessionId);
            return sessionIds.size();
        } finally {
            _readWriteLock.writeLock().unlock();
        }
    }

    private final char[] _chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    /** Cryptographically strong; java.util.Random session IDs could be predicted from earlier ones. */
    private final SecureRandom _random = new SecureRandom();
    /** 32 chars from 62 symbols, about 190 bits. */
    static final int SESSION_ID_LENGTH = 32;

    private String insertValueForUser(String userName) {
        String sessionId;
        do {
            StringBuilder result = new StringBuilder(SESSION_ID_LENGTH);
            for (int i = 0; i < SESSION_ID_LENGTH; i++)
                result.append(_chars[_random.nextInt(_chars.length)]);
            sessionId = result.toString();
        } while (_sessionIdsToUsers.containsKey(sessionId));
        _sessionIdsToUsers.put(sessionId, userName);
        _usersToSessionIds.put(userName, sessionId);
        return sessionId;
    }

    /**
     * Drops every session idle longer than the expiry. (This used to check only the first entry per
     * minute, so under real traffic sessions effectively never expired.) Caller holds the write lock.
     * @return how many sessions were removed
     */
    int expireSessions(long currentTime) {
        int removed = 0;
        synchronized (_lastAccess) {
            Iterator<Map.Entry<String, Long>> iterator = _lastAccess.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Long> lastAccess = iterator.next();
                if (lastAccess.getValue() + _loggedUserExpireLength < currentTime) {
                    String sessionId = lastAccess.getKey();
                    final String userName = _sessionIdsToUsers.remove(sessionId);
                    if (userName != null)
                        _usersToSessionIds.remove(userName, sessionId);
                    dropSso(sessionId);
                    iterator.remove();
                    removed++;
                }
            }
        }
        return removed;
    }

    /** Test hook: pretend a session was last used at the given time. */
    void setLastAccessForTest(String sessionId, long time) {
        _lastAccess.put(sessionId, time);
    }

    private class ClearExpiredRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                _readWriteLock.writeLock().lock();
                try {
                    expireSessions(System.currentTimeMillis());
                } finally {
                    _readWriteLock.writeLock().unlock();
                }
                try {
                    Thread.sleep(_expireCheckInterval);
                } catch (InterruptedException exp) {

                }
            }
        }
    }
}
