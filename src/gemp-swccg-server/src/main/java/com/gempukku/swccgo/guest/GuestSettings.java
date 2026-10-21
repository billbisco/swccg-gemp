package com.gempukku.swccgo.guest;

import com.gempukku.swccgo.common.ApplicationConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Play now settings, all from gemp-swccg.properties ({@code guest.*}). Guest play is off unless
 * {@code guest.enabled=true}.
 * @param enabled whether visitors without an account may start games
 * @param maxConcurrentGames Play now games kept in memory at once (unloaded saved games don't count)
 * @param maxConcurrentPerIp live guest games one IP may have
 * @param startsPerIpPerHour guest game starts one IP may make in an hour
 * @param startsPerHourGlobal guest game starts the whole server takes in an hour
 * @param ipAllowlist IPs exempt from the per-IP limits (venues, schools, carrier NAT)
 * @param unloadIdleMinutes a game nobody has looked at for this long is saved and dropped from memory
 * @param capEvictIdleMinutes at the cap, the minimum idle time of a game that may be evicted
 * @param saveRetentionDays a saved game not played for this long is deleted
 */
public record GuestSettings(boolean enabled, int maxConcurrentGames, int maxConcurrentPerIp, int startsPerIpPerHour,
                            int startsPerHourGlobal, Set<String> ipAllowlist, int unloadIdleMinutes,
                            int capEvictIdleMinutes, int saveRetentionDays) {

    public static GuestSettings defaults() {
        return parse(key -> null);
    }

    public static GuestSettings fromConfiguration() {
        return parse(ApplicationConfiguration::getProperty);
    }

    /**
     * Reads the settings, falling back to the defaults for anything missing or unreadable.
     * @param properties property lookup (returns null when a key is not set)
     */
    public static GuestSettings parse(Function<String, String> properties) {
        String allowlist = properties.apply("guest.ipAllowlist");
        Set<String> ips = new LinkedHashSet<>();
        if (allowlist != null) {
            Arrays.stream(allowlist.split(",")).map(String::trim).filter(s -> !s.isEmpty()).forEach(ips::add);
        }
        return new GuestSettings(
                Boolean.parseBoolean(trim(properties.apply("guest.enabled"))),
                number(properties, "guest.maxConcurrentGames", 20),
                number(properties, "guest.maxConcurrentPerIp", 2),
                number(properties, "guest.startsPerIpPerHour", 10),
                number(properties, "guest.startsPerHourGlobal", 120),
                Collections.unmodifiableSet(ips),
                number(properties, "guest.unloadIdleMinutes", 15),
                number(properties, "guest.capEvictIdleMinutes", 30),
                number(properties, "guest.saveRetentionDays", 15));
    }

    private static int number(Function<String, String> properties, String key, int fallback) {
        String value = trim(properties.apply(key));
        if (value == null || value.isEmpty()) {
            return fallback;
        }
        try {
            int n = Integer.parseInt(value);
            return n < 0 ? fallback : n;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
