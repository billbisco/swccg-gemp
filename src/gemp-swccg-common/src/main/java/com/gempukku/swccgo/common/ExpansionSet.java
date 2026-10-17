package com.gempukku.swccgo.common;

/**
 * Represents an expansion set in SWCCG.
 */
public enum ExpansionSet {

    PREMIERE("Premiere", 1),
    A_NEW_HOPE("A New Hope", 2),
    HOTH("Hoth", 3),
    DAGOBAH("Dagobah", 4),
    CLOUD_CITY("Cloud City", 5),
    JABBAS_PALACE("Jabba's Palace", 6),
    SPECIAL_EDITION("Special Edition", 7),
    ENDOR("Endor", 8),
    DEATH_STAR_II("Death Star II", 9),
    REFLECTIONS_II("Reflections II", 10),
    TATOOINE("Tatooine", 11),
    CORUSCANT("Coruscant", 12),
    REFLECTIONS_III("Reflections III", 13),
    THEED_PALACE("Theed Palace", 14),

    PREMIERE_INTRO_TWO_PLAYER("Premiere Introductory Two Player Game", 101),
    JEDI_PACK("Jedi Pack", 102),
    REBEL_LEADER_PACK("Rebel Leader Pack", 103),
    ESB_INTRO_TWO_PLAYER("Empire Strikes Back Introductory Two Player Game", 104),
    FIRST_ANTHOLOGY("First Anthology", 105),
    OTSD("Official Tournament Sealed Deck", 106),
    SECOND_ANTHOLOGY("Second Anthology", 107),
    ENHANCED_PREMIERE("Enhanced Premiere", 108),
    ENHANCED_CLOUD_CITY("Enhanced Cloud City", 109),
    ENHANCED_JABBAS_PALACE("Enhanced Jabba's Palace", 110),
    THIRD_ANTHOLOGY("Third Anthology", 111),
    JPSD("Jabba's Palace Sealed Deck", 112),

    SET_0("Set 0", 200),
    SET_1("Set 1", 201),
    SET_2("Set 2", 202),
    SET_3("Set 3", 203),
    SET_4("Set 4", 204),
    SET_5("Set 5", 205),
    SET_6("Set 6", 206),
    SET_7("Set 7", 207),
    SET_8("Set 8", 208),
    SET_9("Set 9", 209),
    SET_10("Set 10", 210),
    SET_11("Set 11", 211),
    SET_12("Set 12", 212),
    SET_13("Set 13", 213),
    SET_14("Set 14", 214),
    SET_15("Set 15", 215),
    SET_16("Set 16", 216),
    SET_17("Set 17", 217),
    SET_18("Set 18", 218),
    SET_19("Set 19", 219),
    SET_20("Set 20", 220),
    SET_21("Set 21", 221),
    SET_22("Set 22", 222),
    SET_23("Set 23", 223),
    SET_24("Set 24", 224),
    SET_25("Set 25", 225),
    SET_26("Set 26", 226),
    SET_27("Set 27", 227),

    DEMO_DECK("Virtual Premium Set", 301),

    DREAM_CARDS("Dream Cards", 401),

    PLAYTESTING("Playtesting", 501),

    LEGACY("Legacy", 601),

    VIRTUAL_SHIELDS("Virtual Shields", 1200),
    VIRTUAL_BLOCK_1("Virtual Block 1", 1210),
    VIRTUAL_BLOCK_2("Virtual Block 2", 1220),
    VIRTUAL_BLOCK_3("Virtual Block 3", 1230),
    VIRTUAL_BLOCK_4("Virtual Block 4: Expanded Universe", 1240),
    VIRTUAL_BLOCK_5("Virtual Block 5", 1250),
    VIRTUAL_BLOCK_6_1("Virtual Block 6.1: Reflections IV", 1261),
    VIRTUAL_BLOCK_6_2("Virtual Block 6.2", 1262),
    VIRTUAL_BLOCK_6_3("Virtual Block 6.3: Mercenaries", 1263),
    VIRTUAL_BLOCK_7_1("Virtual Block 7.1", 1271),
    VIRTUAL_BLOCK_7_2("Virtual Block 7.2: Ultimate Freedom", 1272),
    VIRTUAL_BLOCK_7_3("Virtual Block 7.3", 1273),
    VIRTUAL_BLOCK_7_4("Virtual Block 7.4", 1274),
    VIRTUAL_BLOCK_8_1("Virtual Block 8.1", 1281),
    VIRTUAL_BLOCK_8_2("Virtual Block 8.2", 1282),
    VIRTUAL_BLOCK_8_3("Virtual Block 8.3", 1283),
    VIRTUAL_BLOCK_9_1("Virtual Block 9.1", 1291),
    VIRTUAL_BLOCK_9_2("Virtual Block 9.2", 1292);

    private String _humanReadable;
    private int _setNumber;

    ExpansionSet(String humanReadable, int setNumber) {
        _humanReadable = humanReadable;
        _setNumber = setNumber;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }

    public int getSetNumber() {
        return _setNumber;
    }

    public static ExpansionSet getSetFromNumber(Integer setNumber) {
        for (ExpansionSet expansionSet : values()) {
            if (Integer.valueOf(expansionSet.getSetNumber()).equals(setNumber)) {
                return expansionSet;
            }
        }
        return null;
    }
}
