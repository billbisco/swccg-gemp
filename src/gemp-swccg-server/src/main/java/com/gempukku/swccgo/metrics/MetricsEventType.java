package com.gempukku.swccgo.metrics;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The events GEMP is allowed to count. Extra keys and anything that could identify a person
 * (IP, username, email, user agent, location, chat, decklists) are refused.
 */
public enum MetricsEventType {
    PLAY_CLICK("play_click", Set.of("source")),
    GAME_START("game_start", Set.of("side", "bot")),
    TURN_REACHED("turn_reached", Set.of("turn", "phase")),
    GAME_END("game_end", Set.of("result", "turns", "active_minutes", "total_minutes")),
    GAME_ABANDONED("game_abandoned", Set.of("last_turn", "last_phase", "waiting_for", "active_minutes", "total_minutes")),
    GAME_RESUME("game_resume", Set.of("days_since_last_visit")),
    SIGNUP_AFTER_GUEST("signup_after_guest", Set.of("turns_played")),
    OUTBOUND_CLICK("outbound_click", Set.of("target", "path"));

    private static final Set<String> FORBIDDEN = Set.of(
            "ip", "ip_address", "username", "name", "email", "user_agent", "useragent", "ua",
            "location", "chat", "deck", "decklist", "password", "token");

    private final String wireName;
    private final Set<String> allowedKeys;

    MetricsEventType(String wireName, Set<String> allowedKeys) {
        this.wireName = wireName;
        this.allowedKeys = allowedKeys;
    }

    public String wireName() {
        return wireName;
    }

    public Set<String> allowedKeys() {
        return allowedKeys;
    }

    public static MetricsEventType fromWire(String wireName) {
        for (MetricsEventType type : values()) {
            if (type.wireName.equals(wireName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown metrics event: " + wireName);
    }

    /**
     * Returns a copy of {@code details} with only this event's keys, after checking that nothing forbidden
     * was passed and that every required key is present.
     */
    public Map<String, String> validatedDetails(Map<String, String> details) {
        Map<String, String> incoming = details == null ? Map.of() : details;
        for (String key : incoming.keySet()) {
            if (key == null) {
                throw new IllegalArgumentException("Metrics details cannot have a null key");
            }
            String lower = key.toLowerCase(Locale.ROOT);
            if (FORBIDDEN.contains(lower)) {
                throw new IllegalArgumentException("Metrics details cannot include " + key);
            }
            if (!allowedKeys.contains(key)) {
                throw new IllegalArgumentException("Metrics event " + wireName + " does not store " + key);
            }
        }
        Map<String, String> clean = new LinkedHashMap<>();
        for (String key : allowedKeys) {
            String value = incoming.get(key);
            if (value == null || value.isEmpty()) {
                throw new IllegalArgumentException("Metrics event " + wireName + " needs " + key);
            }
            clean.put(key, value);
        }
        validateValues(clean);
        return Collections.unmodifiableMap(clean);
    }

    private void validateValues(Map<String, String> details) {
        switch (this) {
            case PLAY_CLICK -> requireOneOf(details, "source", "frontpage", "direct", "other");
            case GAME_START -> {
                requireOneOf(details, "side", "light", "dark");
                requireOneOf(details, "bot", "ozzel", "yoda");
            }
            case OUTBOUND_CLICK -> {
                requireOneOf(details, "target", "wiki", "site");
                String path = details.get("path");
                if (!path.startsWith("/") || path.contains("://") || path.contains("\\")) {
                    throw new IllegalArgumentException("outbound_click path must be a site path");
                }
            }
            default -> {
            }
        }
    }

    private static void requireOneOf(Map<String, String> details, String key, String... allowed) {
        String value = details.get(key);
        for (String option : allowed) {
            if (option.equals(value)) {
                return;
            }
        }
        throw new IllegalArgumentException("Metrics detail " + key + " is not an allowed value");
    }
}
