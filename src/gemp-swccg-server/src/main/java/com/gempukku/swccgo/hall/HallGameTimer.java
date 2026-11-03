package com.gempukku.swccgo.hall;

/**
 * Casual hall Game Timer presets, matching LOTR GEMP's hall.html options:
 * time bank minutes / per-action timeout minutes.
 */
public enum HallGameTimer {
    DEFAULT("default", 45, 6, "Default (45m/6m)", "Default timer"),
    BLITZ("blitz", 25, 3, "Blitz! (25m/3m)", "Blitz"),
    WC("WC", 20, 10, "Championship (20m/10m)", "Championship timer"),
    SLOW("slow", 80, 10, "Slow (80m/10m)", "Slow timer"),
    GLACIAL("glacial", 1440, 1440, "Glacial (1d/1d)", "Glacial timer");

    private final String _code;
    private final int _bankMinutes;
    private final int _decisionMinutes;
    private final String _label;
    private final String _shortLabel;

    HallGameTimer(String code, int bankMinutes, int decisionMinutes, String label, String shortLabel) {
        _code = code;
        _bankMinutes = bankMinutes;
        _decisionMinutes = decisionMinutes;
        _label = label;
        _shortLabel = shortLabel;
    }

    /** As the setup form shows it, for example "Blitz! (25m/3m)". */
    public String getLabel() {
        return _label;
    }

    /** As lists show it when chosen (#116), for example "Blitz". */
    public String getShortLabel() {
        return _shortLabel;
    }

    public String getCode() {
        return _code;
    }

    public int getBankMinutes() {
        return _bankMinutes;
    }

    public int getDecisionMinutes() {
        return _decisionMinutes;
    }

    public int getBankSeconds() {
        return _bankMinutes * 60;
    }

    public int getDecisionTimeoutSeconds() {
        return _decisionMinutes * 60;
    }

    public static HallGameTimer fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return DEFAULT;
        }
        for (HallGameTimer timer : values()) {
            if (timer._code.equalsIgnoreCase(code.trim())) {
                return timer;
            }
        }
        return DEFAULT;
    }
}
