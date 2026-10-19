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

    public void forceLogoutUser(String userName) {
        _readWriteLock.writeLock().lock();
        try {
            final Collection<String> sessionIds = new HashSet<String>(_usersToSessionIds.get(userName));
            for (String sessionId : sessionIds) {
                _sessionIdsToUsers.remove(sessionId);
                _usersToSessionIds.remove(userName, sessionId);
                _lastAccess.remove(sessionId);
            }
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
