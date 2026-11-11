package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Legacy T1 (Bill, 2026-11-10 late): "(V)" in a Virtual card's title follows the printed title. The release sheets and
 * the Virtual Cards Master List print a heading over each slip; it carries "(V)" for some new-title cards (Galen, Secret
 * Apprentice (V); Booster's Star Destroyer (V)) and not for others (Mara Jade With Lightsaber; The Force Unleashed). The
 * card's full name shows "(V)" exactly when its printing's heading has it. A version's printed title is shown as printed
 * (no " (V)" suffix is added); the rules title and {@link SwccgCardBlueprint#hasVirtualSuffix()} are unchanged.
 */
public class LegacyVInTitleTests {
    /** Blueprint id -> full name as printed (release sheet heading for originals, Master List heading for current cards). */
    private static final Map<String, String> FULL_NAMES = new LinkedHashMap<>() {{
        // changed by T1
        put("1210_3", "Admiral Motti, Battlestation Coordinator");                 // VB1 p.2 and Master DS p.3: no (V)
        put("2240_26", "He Is Not Ready & Imperial Propaganda");                   // VB4 p.4: no (V)
        put("2240_81", "Eject! Eject! & Imperial Atrocity");                       // VB4 p.9: no (V)
        put("2240_112", "BoShek's Modified Light Freighter (V)");                  // VB4 p.12: (V)
        put("2281_45", "Booster's Star Destroyer (V)");                            // VB8.1 p.8: (V)
        // checked, already right
        put("1240_26", "He Is Not Ready (V)");                                     // Master DS p.26
        put("1240_81", "Eject! Eject! (V)");                                       // Master LS p.23
        put("2240_2", "Black Leader (V)");                                         // VB4 p.2
        put("1240_2", "Juno Eclipse, Black Leader");                               // Master DS p.10
        put("2240_8", "Galen, Secret Apprentice (V)");                             // VB4 p.2
        put("1240_8", "Galen Marek, Starkiller");                                  // Master DS p.8
        put("2240_59", "Galen's Fighter");                                         // VB4 p.8
        put("1240_59", "Rogue Shadow");                                            // Master DS p.55
        put("1240_64", "Galen's Lightsaber");                                      // VB4 p.8, Master DS p.59
        put("1240_10", "Mara Jade With Lightsaber");                               // VB4 p.2
        put("1240_41", "The Force Unleashed");                                     // VB4 p.5
        put("2240_17", "Thrawn's Pet");                                            // VB4 p.3
        put("1240_17", "Thrawn's Ysalamir");                                       // Master DS p.16
        put("1240_112", "Infinity");
        put("2250_8", "The Mandalorian");                                          // VB5 p.2
        put("1250_79", "Alderaan Consular Ship");                                  // VB5 p.9
        put("2271_5", "Ice-Heart");                                                // VB7.1 p.2
        put("1271_5", "Ysanne Isard");                                             // Master DS p.15
        put("2271_1", "Cyborg Commander, Hunter Of Jedi");
        put("1271_1", "Grievous, Hunter Of Jedi");                                 // Master DS p.8
        put("2273_6", "IG Bodyguard Droid");
        put("1273_6", "IG-100 MagnaGuard");                                        // Master DS p.9
        put("2274_4", "Floating Refinery (V)");                                    // VB7.4 p.2
        put("1274_4", "Tibanna Floating Refinery");                                // Master DS p.21
        put("2274_11", "Cyborg Commander's Lightsabers");                          // VB7.4 p.3
        put("1274_11", "Grievous' Lightsabers");                                   // Master DS p.59
        put("1261_45", "One Bright Spot");
        put("1261_46", "One Beautiful Thing");
        put("1261_48", "Weapon Levitation & The Empire's Back");
        put("1261_56", "Black 1");
        put("1273_10", "Kessel Surveillance System");
        put("1273_11", "Trophy Of A Kill");
        put("1273_17", "Coruscant: Palpatine's Quarters");
        put("1273_31", "Planetary Shield");
        put("2281_47", "Rebel Agent's Blaster Rifle");                             // VB8.1 p.8
        put("1281_47", "Kyle Katarn's Blaster Rifle");                             // Master LS p.54
        put("2282_27", "Lando's Luxury Yacht");                                    // VB8.2 p.4
        put("1282_4", "Sith Fury & End This Destructive Conflict");
        put("1282_11", "Assassin's Blaster Rifle");
        put("1283_2", "Greedo With Blaster Pistol");
        put("1210_117", "Lars' Hydroponics Station");
        put("1210_118", "Lars' Vaporator");
        put("1240_45", "Let's Pass On That");
        put("1291_10", "Maul's Cape");
        put("1291_55", "The Dark Side Is Growing");
    }};

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void FullNameShowsVExactlyWhenThePrintingHasIt() {
        for (Map.Entry<String, String> e : FULL_NAMES.entrySet()) {
            SwccgCardBlueprint b = library.getSwccgoCardBlueprint(e.getKey());
            assertEquals(e.getKey(), e.getValue(), GameUtils.getFullName(b));
        }
    }

    @Test
    public void APrintedTitleWithoutVKeepsTheRulesSuffixOfItsCurrentCard() {
        // Display only: the version stays a "(V)" card for rules, like its current printing.
        for (String[] pair : new String[][]{{"2240_26", "1240_26"}, {"2240_81", "1240_81"}}) {
            SwccgCardBlueprint version = library.getSwccgoCardBlueprint(pair[0]);
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(pair[1]);
            assertTrue(pair[0], version.hasVirtualSuffix());
            assertEquals(pair[0], current.hasVirtualSuffix(), version.hasVirtualSuffix());
            assertEquals(pair[0], GameUtils.getFullName(current), GameUtils.getRulesFullName(version));
        }
    }

    @Test
    public void BasePrintingTitlesStillGetTheSuffix() {
        // setBasePrintingTitle names the base card only; the "(V)" suffix is still added.
        assertEquals("You Cannot Hide Forever (Ref III) (V)", GameUtils.getFullName(library.getSwccgoCardBlueprint("1261_27")));
        assertEquals("Let's Keep A Little Optimism Here (Death Star II) (V)", GameUtils.getFullName(library.getSwccgoCardBlueprint("1261_76")));
    }
}
