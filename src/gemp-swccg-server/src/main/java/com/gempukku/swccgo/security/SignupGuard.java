package com.gempukku.swccgo.security;

import com.gempukku.swccgo.common.ApplicationConfiguration;

import java.util.HashSet;
import java.util.Set;

/**
 * Per-IP signup limit: at most {@code maxPerIpPerDay} new accounts per client IP in 24 hours
 * (default 3, so the 4th is refused). IPs on the allowlist (tournament venues, schools,
 * carrier NAT) are exempt. Needs the real client IP, see {@link ClientIpResolver}.
 */
public class SignupGuard {
    public static final int DEFAULT_MAX_PER_IP_PER_DAY = 3;
    public static final long WINDOW_MILLIS = 24L * 60 * 60 * 1000;

    private final int _maxPerIpPerDay;
    private final Set<String> _allowlist = new HashSet<>();

    public SignupGuard(int maxPerIpPerDay, String allowlist) {
        _maxPerIpPerDay = maxPerIpPerDay;
        if (allowlist != null) {
            for (String part : allowlist.split(",")) {
                String ip = ClientIpResolver.normalize(part.trim());
                if (ip != null)
                    _allowlist.add(ip);
            }
        }
    }

    /** Reads {@code signup.maxPerIpPerDay} and {@code signup.ipAllowlist}. */
    public static SignupGuard fromConfiguration() {
        int max = DEFAULT_MAX_PER_IP_PER_DAY;
        try {
            String v = ApplicationConfiguration.getProperty("signup.maxPerIpPerDay");
            if (v != null && !v.isBlank())
                max = Integer.parseInt(v.trim());
        } catch (RuntimeException ignored) {
            // keep default
        }
        return new SignupGuard(max, ApplicationConfiguration.getProperty("signup.ipAllowlist"));
    }

    public boolean isAllowlisted(String ip) {
        String n = ClientIpResolver.normalize(ip);
        return n != null && _allowlist.contains(n);
    }

    /**
     * @param ip the client IP
     * @param signupsInLastDay accounts already created from that IP in the last 24 hours
     */
    public boolean allows(String ip, int signupsInLastDay) {
        if (_maxPerIpPerDay <= 0 || isAllowlisted(ip))
            return true;
        return signupsInLastDay < _maxPerIpPerDay;
    }

    /**
     * Honeypot: the signup form has a hidden "website" field people never see. Bots that fill
     * every field give themselves away; the caller pretends success and creates nothing.
     */
    public static boolean isHoneypotTripped(String honeypotValue) {
        return honeypotValue != null && !honeypotValue.trim().isEmpty();
    }
}
