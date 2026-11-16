package com.gempukku.swccgo.service;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory brute-force brake for logins (and the wiki auth check, which goes through the same path).
 *
 * <ul>
 *   <li>Per (account, IP): after {@code maxPairFailures} failures inside the window, that name is locked
 *   <em>for that IP only</em> for {@code lockMillis} from the last failure. Someone mistyping at a
 *   tournament venue does not lock the account for everyone else.</li>
 *   <li>Per account, any IP: never a lock (#564). A stranger could otherwise lock a player out by
 *   failing on purpose from many places. Past {@code accountDelayAfter} failures inside the window each
 *   further try is slowed instead: 500 ms per failure over the ceiling, capped at 5 seconds, the same
 *   curve the sign-in service uses. See {@link #delayMillis}.</li>
 *   <li>Per IP: after {@code maxIpFailures} failures inside the window, that IP is refused until the
 *   window rolls over. This catches one client spraying many names.</li>
 * </ul>
 * A successful login clears that account's counters.
 *
 * State is per process and resets on restart, which is fine for a speed bump. nginx {@code limit_req}
 * is still the first line for raw request floods.
 */
public class LoginThrottle {
    public static final int DEFAULT_MAX_PAIR_FAILURES = 10;
    /** Failures for one name (any IP) inside the window before tries for it start to slow down. */
    public static final int DEFAULT_ACCOUNT_DELAY_AFTER = 30;
    public static final long DELAY_STEP_MILLIS = 500;
    public static final long MAX_DELAY_MILLIS = 5_000;
    public static final int DEFAULT_MAX_IP_FAILURES = 50;
    public static final long DEFAULT_WINDOW_MILLIS = 15L * 60 * 1000;
    public static final long DEFAULT_LOCK_MILLIS = 5L * 60 * 1000;

    private final int _maxPairFailures;
    private final int _accountDelayAfter;
    private final int _maxIpFailures;
    private final long _windowMillis;
    private final long _lockMillis;

    private final Map<String, Counter> _byPair = new ConcurrentHashMap<>();
    private final Map<String, Counter> _byAccount = new ConcurrentHashMap<>();
    private final Map<String, Counter> _byIp = new ConcurrentHashMap<>();

    private static final class Counter {
        long windowStart;
        long lastFailure;
        int failures;
    }

    public LoginThrottle() {
        this(DEFAULT_MAX_PAIR_FAILURES, DEFAULT_ACCOUNT_DELAY_AFTER, DEFAULT_MAX_IP_FAILURES,
                DEFAULT_WINDOW_MILLIS, DEFAULT_LOCK_MILLIS);
    }

    public LoginThrottle(int maxPairFailures, int accountDelayAfter, int maxIpFailures, long windowMillis, long lockMillis) {
        _maxPairFailures = maxPairFailures;
        _accountDelayAfter = accountDelayAfter;
        _maxIpFailures = maxIpFailures;
        _windowMillis = windowMillis;
        _lockMillis = lockMillis;
    }

    private static String accountKey(String login) {
        return login == null ? "" : login.toLowerCase(Locale.ROOT);
    }

    private static String pairKey(String login, String ip) {
        return accountKey(login) + "\n" + (ip == null ? "" : ip);
    }

    private boolean locked(Counter c, int max, long now) {
        if (c == null)
            return false;
        synchronized (c) {
            return c.failures >= max && now - c.lastFailure < _lockMillis;
        }
    }

    /** True when this name is locked for this IP, or this IP is refused. Never true for a name alone. */
    public boolean isBlocked(String login, String ip, long now) {
        if (locked(_byPair.get(pairKey(login, ip)), _maxPairFailures, now))
            return true;
        Counter byIp = ip != null ? _byIp.get(ip) : null;
        if (byIp != null) {
            synchronized (byIp) {
                if (byIp.failures >= _maxIpFailures && now - byIp.windowStart < _windowMillis)
                    return true;
            }
        }
        return false;
    }

    /**
     * How long to hold the next try for this name, from its failures from every IP in the window:
     * 0 up to {@code accountDelayAfter}, then {@link #DELAY_STEP_MILLIS} per failure over it, capped at
     * {@link #MAX_DELAY_MILLIS}. A slow-down, never a refusal.
     */
    public long delayMillis(String login, long now) {
        Counter c = _byAccount.get(accountKey(login));
        if (c == null)
            return 0;
        synchronized (c) {
            if (now - c.windowStart >= _windowMillis)
                return 0;
            int over = c.failures - _accountDelayAfter + 1;
            if (over <= 0)
                return 0;
            return Math.min(MAX_DELAY_MILLIS, over * DELAY_STEP_MILLIS);
        }
    }

    public void recordFailure(String login, String ip, long now) {
        bump(_byPair.computeIfAbsent(pairKey(login, ip), k -> new Counter()), now);
        bump(_byAccount.computeIfAbsent(accountKey(login), k -> new Counter()), now);
        if (ip != null)
            bump(_byIp.computeIfAbsent(ip, k -> new Counter()), now);
        if (_byPair.size() + _byAccount.size() + _byIp.size() > 100_000)
            prune(now);
    }

    /** Clears the account's counters (all IPs). The per-IP counter is left alone. */
    public void recordSuccess(String login, String ip) {
        String account = accountKey(login);
        _byAccount.remove(account);
        String prefix = account + "\n";
        _byPair.keySet().removeIf(k -> k.startsWith(prefix));
    }

    private void bump(Counter c, long now) {
        synchronized (c) {
            if (now - c.windowStart >= _windowMillis && now - c.lastFailure >= _lockMillis) {
                c.windowStart = now;
                c.failures = 0;
            }
            c.failures++;
            c.lastFailure = now;
        }
    }

    private void prune(long now) {
        long horizon = Math.max(_windowMillis, _lockMillis);
        _byPair.values().removeIf(c -> now - c.lastFailure > horizon);
        _byAccount.values().removeIf(c -> now - c.lastFailure > horizon);
        _byIp.values().removeIf(c -> now - c.lastFailure > horizon);
    }
}
