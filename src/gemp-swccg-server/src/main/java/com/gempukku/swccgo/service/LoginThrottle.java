package com.gempukku.swccgo.service;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory brute-force brake for logins (and the wiki auth check, which goes through the same path).
 *
 * <ul>
 *   <li>Per account: after {@code maxAccountFailures} failures inside the window, that name is locked
 *   for {@code lockMillis} from the last failure. A success clears the count.</li>
 *   <li>Per IP: after {@code maxIpFailures} failures inside the window, that IP is refused until the
 *   window rolls over. This catches one client spraying many names.</li>
 * </ul>
 *
 * State is per process and resets on restart, which is fine for a speed bump. nginx {@code limit_req}
 * is still the first line for raw request floods.
 */
public class LoginThrottle {
    public static final int DEFAULT_MAX_ACCOUNT_FAILURES = 10;
    public static final int DEFAULT_MAX_IP_FAILURES = 50;
    public static final long DEFAULT_WINDOW_MILLIS = 15L * 60 * 1000;
    public static final long DEFAULT_LOCK_MILLIS = 5L * 60 * 1000;

    private final int _maxAccountFailures;
    private final int _maxIpFailures;
    private final long _windowMillis;
    private final long _lockMillis;

    private final Map<String, Counter> _byAccount = new ConcurrentHashMap<>();
    private final Map<String, Counter> _byIp = new ConcurrentHashMap<>();

    private static final class Counter {
        long windowStart;
        long lastFailure;
        int failures;
    }

    public LoginThrottle() {
        this(DEFAULT_MAX_ACCOUNT_FAILURES, DEFAULT_MAX_IP_FAILURES, DEFAULT_WINDOW_MILLIS, DEFAULT_LOCK_MILLIS);
    }

    public LoginThrottle(int maxAccountFailures, int maxIpFailures, long windowMillis, long lockMillis) {
        _maxAccountFailures = maxAccountFailures;
        _maxIpFailures = maxIpFailures;
        _windowMillis = windowMillis;
        _lockMillis = lockMillis;
    }

    private static String accountKey(String login) {
        return login == null ? "" : login.toLowerCase(Locale.ROOT);
    }

    public boolean isBlocked(String login, String ip, long now) {
        Counter account = _byAccount.get(accountKey(login));
        if (account != null) {
            synchronized (account) {
                if (account.failures >= _maxAccountFailures && now - account.lastFailure < _lockMillis)
                    return true;
            }
        }
        Counter byIp = ip != null ? _byIp.get(ip) : null;
        if (byIp != null) {
            synchronized (byIp) {
                if (byIp.failures >= _maxIpFailures && now - byIp.windowStart < _windowMillis)
                    return true;
            }
        }
        return false;
    }

    public void recordFailure(String login, String ip, long now) {
        bump(_byAccount.computeIfAbsent(accountKey(login), k -> new Counter()), now);
        if (ip != null)
            bump(_byIp.computeIfAbsent(ip, k -> new Counter()), now);
        if (_byAccount.size() + _byIp.size() > 100_000)
            prune(now);
    }

    public void recordSuccess(String login, String ip) {
        _byAccount.remove(accountKey(login));
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
        _byAccount.values().removeIf(c -> now - c.lastFailure > horizon);
        _byIp.values().removeIf(c -> now - c.lastFailure > horizon);
    }
}
