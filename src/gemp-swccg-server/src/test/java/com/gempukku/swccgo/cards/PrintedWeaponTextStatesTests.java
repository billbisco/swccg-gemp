package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Printed (o) text states of Decipher weapons (docs/legacy-versions.md, "Text states"). One test per state: the printed
 * game text (wiki.swccg.com plain article, checked against the Decipher archive scan), the text-state information, the
 * same card as the bare id, and whether the class only changes the text (plays as the bare card) or also overrides how
 * the weapon fires. Gameplay differences: PrintedWeaponTextStatesGameplayTests.
 */
@RunWith(Parameterized.class)
public class PrintedWeaponTextStatesTests {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"1_152o", "com.gempukku.swccgo.cards.set1.light.Card1_152", "Use 1 Force to deploy on any warrior. May target using 1 Force. Draw destiny. Character hit if destiny > ability. Vehicle hit if destiny > maneuver or armor.", false, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"1_153o", "com.gempukku.swccgo.cards.set1.light.Card1_153", "Use 2 Force to deploy on any warrior. May target using 2 Force. Draw destiny. Character hit if destiny +1 > ability. Vehicle hit if destiny +1 > maneuver or armor.", false, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"1_154o", "com.gempukku.swccgo.cards.set1.light.Card1_154", "Use 1 Force to deploy on Han, 3 on any other warrior. May target using 1 Force. Draw destiny. Character hit if destiny +1 > ability. Vehicle hit if destiny +1 > maneuver or armor. If hit by Han, target's forfeit = 0.", false, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"1_156o", "com.gempukku.swccgo.cards.set1.light.Card1_156", "Use 1 Force to deploy on Leia, 2 on any other warrior. May target for free. Draw destiny. Character hit if destiny -1 > ability. Vehicle hit if destiny -1 > maneuver or armor. If hit by Leia, target's forfeit = 0.", false, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"1_158o", "com.gempukku.swccgo.cards.set1.light.Card1_158", "Use 1 Force to deploy on any X-wing, Y-wing or B-wing. May target using 1 Force. Draw destiny. Capital starship hit if destiny > armor. Starfighter hit if destiny > maneuver.", true, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"1_159o", "com.gempukku.swccgo.cards.set1.light.Card1_159", "Use 2 Force to deploy on Corellian Corvette or Millennium Falcon. May target using 1 Force. Draw destiny. Capital starship hit if destiny > armor. Starfighter hit if destiny +1 > maneuver.", true, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"1_311o", "com.gempukku.swccgo.cards.set1.dark.Card1_311", "Use 3 Force to deploy on any warrior. May fire during a battle at same site or an adjacent site. Select target involved in battle for 2 Force. Draw destiny. Character hit if destiny +1 > ability. Vehicle hit if destiny +1 > maneuver or armor.", false, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"1_312o", "com.gempukku.swccgo.cards.set1.dark.Card1_312", "Use 2 Force to deploy on any warrior. May target using 2 Force. Draw destiny. Character hit if destiny +1 > ability. Vehicle hit if destiny +1 > maneuver or armor.", false, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"1_313o", "com.gempukku.swccgo.cards.set1.dark.Card1_313", "Use 1 Force to deploy on any TIE Advanced x1. May target using 1 Force. Draw destiny. Capital starship hit if destiny -1 > armor. Starfighter hit if destiny +1 > maneuver.", true, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"1_315o", "com.gempukku.swccgo.cards.set1.dark.Card1_315", "Use 2 Force to deploy on any Tusken Raider. May target for free. If a battle has just been initiated where present, immediately draw two destiny. If total destiny > 5, opponent's weapons are \"knocked away\" (may not be used this battle).", true, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"1_317o", "com.gempukku.swccgo.cards.set1.dark.Card1_317", "Use 1 Force to deploy on any warrior. May target using 1 Force. Draw destiny. Character hit if destiny > ability. Vehicle hit if destiny > maneuver or armor.", false, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"1_318o", "com.gempukku.swccgo.cards.set1.dark.Card1_318", "Use 2 Force to deploy on any Star Destroyer. May target using 1 Force. Draw destiny. If destiny +2 > target's armor or maneuver, all weapons aboard target are lost, armor or maneuver = 0 and hyperspeed = 0.", false, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"1_319o", "com.gempukku.swccgo.cards.set1.dark.Card1_319", "Use 2 Force to deploy at an interior site. Cannot move. May target for free. Draw destiny. Character hit if destiny -1 > ability. Seeker hit if destiny > 2. Laser Projector may be targeted by another weapon and hit if destiny > 1.", false, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"1_320o", "com.gempukku.swccgo.cards.set1.dark.Card1_320", "Use 2 Force to deploy on any warrior. If a power droid or any fusion generator present, may target using 2 Force. Draw destiny. Character hit if destiny +1 > ability. Vehicle hit if destiny +1 > maneuver or armor. May fire repeatedly for 2 Force each time.", false, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"1_323o", "com.gempukku.swccgo.cards.set1.dark.Card1_323", "Use 3 Force to deploy on any Star Destroyer or any mobile system. May target using 2 Force. Draw two destiny. Capital starship hit if total destiny -2 > armor. Starfighter hit if total destiny -5 > maneuver.", true, LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)},
                {"2_77o", "com.gempukku.swccgo.cards.set2.light.Card2_077", "Use X Force to deploy on any warrior where X = (7 - warrior's power). X must be at least 1. May target using X Force. Draw destiny. Character hit if total destiny +1 > ability. Creature immediately lost if destiny +2 > defense value.", false, LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)},
                {"2_78o", "com.gempukku.swccgo.cards.set2.light.Card2_078", "Use 1 Force to deploy on any Jawa, 3 on any warrior. May target using 1 Force. Draw destiny. Droid immediately 'stolen' if destiny +1 > forfeit. Non-droid character excluded from battle if destiny = ability.", false, LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)},
                {"2_79o", "com.gempukku.swccgo.cards.set2.light.Card2_079", "Use 1 Force to deploy on Luke or Owen Lars, 3 on any non-droid character (even if not a warrior). May target using 1 Force. Draw destiny. Character hit if destiny -1 > ability. Creature immediately lost if destiny +2 > defense value.", false, LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)},
                {"2_81o", "com.gempukku.swccgo.cards.set2.light.Card2_081", "Use 1 Force to deploy on any Y-wing or B-wing. May target using 1 Force. Draw destiny. If destiny > target's armor or maneuver, all weapons aboard target are lost, armor or maneuver = 0 and hyperspeed = 0.", false, LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)},
                {"2_158o", "com.gempukku.swccgo.cards.set2.dark.Card2_158", "Use 1 Force to deploy on any TIE. May target using 1 Force. Draw destiny. Capital starship hit if destiny -2 > armor. Starfighter hit if destiny > maneuver.", true, LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)},
                {"2_159o", "com.gempukku.swccgo.cards.set2.dark.Card2_159", "Deploy free on any Jawa. May target for free. Draw destiny. Character hit if destiny -1 > ability. Jawa Blaster explodes, causing it and character firing it to be lost, if destiny = 0.", false, LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)},
                {"2_161o", "com.gempukku.swccgo.cards.set2.dark.Card2_161", "Deploy on Death Star system at parsec 0. May target a capital starship at Death Star system, or at a system it orbits, using 4 Force. Draw two destiny. Capital starship hit if total destiny > armor.", true, LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)},
                {"2_162o", "com.gempukku.swccgo.cards.set2.dark.Card2_162", "Use 1 Force to deploy on URoRRuR'R'R or Chief Bast, 3 on any non-droid character (even if not a warrior). May target using 1 Force. Draw destiny. Character hit if destiny -1 > ability. Creature immediately lost if destiny +2 > defense value.", false, LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)},
                {"3_72o", "com.gempukku.swccgo.cards.set3.light.Card3_072", "Deploy on an exterior planet site. Cannot be moved. Your warrior present may target at same or adjacent site using 2 Force. Draw destiny. Vehicle 'crashes' if destiny +2 > armor. Vehicle hit if destiny +1 > maneuver.", true, LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)},
                {"3_73o", "com.gempukku.swccgo.cards.set3.light.Card3_073", "Use 2 Force to deploy on any warrior. May 'throw' at same or adjacent site. Draw destiny. All characters, weapons and devices with that destiny number at that site are lost. (Only your warrior is lost if destiny = 0.) Concussion Grenade also lost.", false, LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)},
                {"3_74o", "com.gempukku.swccgo.cards.set3.light.Card3_074", "Use 2 Force to deploy on any T-47. May target using 1 Force. Draw destiny. Character hit if destiny -1 > ability. Vehicle hit if destiny +2 > armor or maneuver.", false, LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)},
                {"3_77o", "com.gempukku.swccgo.cards.set3.light.Card3_077", "Deploy on any site. May be moved by two warriors for 1 additional Force. Your warrior present may target two characters at same or adjacent site using 2 Force. Draw two destiny. Characters hit if total destiny > total ability.", false, LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)},
                {"3_78o", "com.gempukku.swccgo.cards.set3.light.Card3_078", "Deploy on an exterior site at a Rebel Base. Cannot be moved. During a battle at related system, may target a capital starship there using 2 Force. Draw destiny. If destiny +3 > armor, all weapons aboard target are lost, power = 0 and hyperspeed = 0.", false, LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)},
                {"3_80o", "com.gempukku.swccgo.cards.set3.light.Card3_080", "Use 1 Force to deploy on any starfighter, free on Millennium Falcon. May target a character at same site using 1 Force. Draw destiny. Character hit if destiny +1 > ability.", false, LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)},
                {"3_158o", "com.gempukku.swccgo.cards.set3.dark.Card3_158", "Use 2 Force to deploy on any AT-AT. May fire during a battle at same or adjacent site. May select a target only at an adjacent site using 2 Force. Draw destiny. Vehicle hit if destiny +2 > armor or maneuver. Starfighter hit if destiny > 3. Character hit if destiny +1 > ability.", false, LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)},
                {"3_159o", "com.gempukku.swccgo.cards.set3.dark.Card3_159", "Deploy on any site. May be moved with two warriors for 1 additional Force. Your warrior present may target using 2 Force. Draw destiny. Character or vehicle hit if destiny +1 > ability, armor or maneuver. Starfighter hit if destiny > 4.", false, LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)},
                {"3_161o", "com.gempukku.swccgo.cards.set3.dark.Card3_161", "Deploy on any Probe Droid. May target for free. Draw destiny. Character hit if destiny > ability. Artillery weapon immediately lost if destiny > 5.", false, LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)},
                {"4_174o", "com.gempukku.swccgo.cards.set4.dark.Card4_174", "Use 4 Force to deploy on any warrior, free on 4-LOM. May target for free during a battle at same site or exterior site up to 2 sites away. Draw destiny. Add 1 if Blaster Scope attached. Character hit if total destiny -X > ability, where X = distance to target.", false, LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)},
                {"4_176o", "com.gempukku.swccgo.cards.set4.dark.Card4_176", "Use 1 Force to deploy on Dengar, 3 on any other warrior. May target using 1 Force. Draw destiny. Character hit if destiny +1 > ability. Vehicle hit if destiny +1 > maneuver or armor. If hit by Dengar, target's forfeit = 0.", false, LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)},
                {"4_178o", "com.gempukku.swccgo.cards.set4.dark.Card4_178", "Use 1 Force to deploy on IG-88, 4 on any other warrior. Adds 2 to power. May target X non-droid characters using X Force. Draw destiny for each. If destiny = 0, character is power -1 and forfeit -1 until end of turn. If destiny -1 > ability, character hit.", false, LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)},
                {"5_89o", "com.gempukku.swccgo.cards.set5.light.Card5_089", "Use 2 Force to deploy on any warrior at a Cloud City site. May target using 2 Force. Draw destiny. Character hit (and may not be used to satisfy attrition) if destiny > ability.", false, LocalDate.of(1997, 11, 1), LocalDate.of(1998, 11, 1)},
                {"5_90o", "com.gempukku.swccgo.cards.set5.light.Card5_090", "Use 1 Force to deploy on Luke, 3 on any other warrior. May target using 2 Force. (If targeted by Luke, target loses immunity to attrition for remainder of turn.) Draw destiny. Character hit if destiny +1 > ability. Vehicle hit if destiny > armor or maneuver.", false, LocalDate.of(1997, 11, 1), LocalDate.of(1998, 11, 1)},
                {"5_179o", "com.gempukku.swccgo.cards.set5.dark.Card5_179", "Use 1 Force to deploy on Boba Fett, 3 on any other bounty hunter. May deploy as a 'react.' May target using 2 Force. Draw destiny. Character hit if destiny > ability. Vehicle hit if destiny +1 > armor or maneuver. May fire repeatedly for 1 Force each time.", false, LocalDate.of(1997, 11, 1), LocalDate.of(1998, 11, 1)},
                {"5_180o", "com.gempukku.swccgo.cards.set5.dark.Card5_180", "Use 2 Force to deploy on any warrior at a Cloud City site. May target using 2 Force. Draw destiny. Character hit (and may not be used to satisfy attrition) if destiny > ability.", false, LocalDate.of(1997, 11, 1), LocalDate.of(1998, 11, 1)},
                {"6_89o", "com.gempukku.swccgo.cards.set6.light.Card6_089", "Use 1 Force to deploy on any alien warrior (free if on Tatooine). May target for free. Draw destiny. Character is forfeit -2 for remainder of turn if destiny +2 > ability.", false, LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)},
                {"6_90o", "com.gempukku.swccgo.cards.set6.light.Card6_090", "Use 3 Force to deploy on any alien warrior. Adds 1 to power. May target using 1 Force. Both players draw destiny. Target immediately excluded from battle if warrior's power + your destiny > target's power + opponent's destiny.", true, LocalDate.of(1998, 5, 1), LocalDate.of(2023, 1, 1)},
                {"6_174o", "com.gempukku.swccgo.cards.set6.dark.Card6_174", "Use 3 Force to deploy on any transport vehicle. Your warrior present, if aboard, may target using 3 Force. Draw destiny. Character hit if destiny +2 > ability. May fire repeatedly for 2 Force each time.", false, LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)},
                {"6_175o", "com.gempukku.swccgo.cards.set6.dark.Card6_175", "Use 2 Force to deploy on Dengar, 5 on any other bounty hunter. May target a non-droid character using 3 Force. Draw destiny. Character immediately captured if destiny +3 > ability.", false, LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)},
                {"6_176o", "com.gempukku.swccgo.cards.set6.dark.Card6_176", "Use 4 Force to deploy on Jabba's Sail Barge or a sandcrawler; it is power +3 and immune to attrition < 5. Your warrior present, if aboard, may target using 2 Force. Draw destiny. Vehicle hit if destiny +2 > armor or maneuver.", true, LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)},
                {"6_177o", "com.gempukku.swccgo.cards.set6.dark.Card6_177", "Use 1 Force to deploy on any Gamorrean. Adds 1 to power. When present at a site, Gamorrean draws one battle destiny if not able to otherwise. May target for free. Draw destiny. Character hit if destiny > ability.", false, LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)},
                {"6_178o", "com.gempukku.swccgo.cards.set6.dark.Card6_178", "Use 1 Force to deploy on any alien warrior (free if on Tatooine). May target for free. Draw destiny. Character is forfeit -2 for remainder of turn if destiny +2 > ability.", false, LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)},
                {"6_180o", "com.gempukku.swccgo.cards.set6.dark.Card6_180", "Use 3 Force to deploy on any alien warrior. Adds 1 to power. May target using 1 Force. Both players draw destiny. Target immediately excluded from battle if warrior's power + your destiny > target's power + opponent's destiny.", true, LocalDate.of(1998, 5, 1), LocalDate.of(2023, 1, 1)}
        });
    }

    private final String _id;
    private final String _bareClass;
    private final String _printedText;
    private final boolean _playsAsBare;
    private final LocalDate _printedOn;
    private final LocalDate _supersededOn;

    public PrintedWeaponTextStatesTests(String id, String bareClass, String printedText, boolean playsAsBare, LocalDate printedOn, LocalDate supersededOn) {
        _id = id;
        _bareClass = bareClass;
        _printedText = printedText;
        _playsAsBare = playsAsBare;
        _printedOn = printedOn;
        _supersededOn = supersededOn;
    }

    @Test
    public void printedStateHasThePrintedTextAndIsTheSameCardAsTheBareId() throws Exception {
        Class<?> bareClass = Class.forName(_bareClass);
        Class<?> stateClass = Class.forName(_bareClass + "o");
        assertEquals(bareClass, stateClass.getSuperclass());
        SwccgCardBlueprint bare = (SwccgCardBlueprint) bareClass.getDeclaredConstructor().newInstance();
        SwccgCardBlueprint printed = (SwccgCardBlueprint) stateClass.getDeclaredConstructor().newInstance();

        assertEquals(_printedText, printed.getGameText());
        assertNotEquals(bare.getGameText(), printed.getGameText());

        assertEquals(bare.getTitle(), printed.getTitle());
        assertEquals(bare.getUniqueness(), printed.getUniqueness());
        assertEquals(bare.getSide(), printed.getSide());
        assertEquals(bare.getCardCategory(), printed.getCardCategory());
        assertEquals(bare.getCardSubtype(), printed.getCardSubtype());
        assertEquals(bare.getExpansionSet(), printed.getExpansionSet());
        assertEquals(bare.getRarity(), printed.getRarity());
        assertEquals(bare.getLore(), printed.getLore());
        assertEquals(bare.getDestiny(), printed.getDestiny(), 0.0f);
        for (Icon icon : Icon.values())
            assertEquals(icon.name(), bare.getIconCount(icon), printed.getIconCount(icon));

        TextState state = printed.getTextState();
        assertEquals(_id.substring(0, _id.length() - 1), state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(_printedOn, state.printedOn());
        assertEquals(_supersededOn, state.supersededOn());
        assertTrue(state.source().contains("Decipher"));
        assertNull(bare.getTextState());

        // A state that plays differently overrides how the weapon fires; one that plays as the bare card does not.
        boolean overridesFiring = Arrays.stream(stateClass.getDeclaredMethods())
                .map(Method::getName).anyMatch("getGameTextFireWeaponActions"::equals);
        assertEquals(!_playsAsBare, overridesFiring);
    }

    @Test
    public void printedStateIsListedForTheCardPoolAndHasItsArchiveScan() throws Exception {
        String pool = new String(TextState.class.getClassLoader().getResourceAsStream("textStates.txt").readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(Arrays.asList(pool.split("\\R")).contains(_id));
        Path images = Paths.get("..", "gemp-swccg-async", "src", "main", "web", "js", "gemp-016", "cards", "CardImages.js");
        if (Files.exists(images)) {
            String js = Files.readString(images);
            assertTrue(_id, js.contains("\"" + _id + "\":\"https://wiki.swccg.com/images/"));
            int line = js.indexOf("\"" + _id + "\":");
            assertTrue(_id, js.substring(line, js.indexOf('\n', line)).endsWith("decipher-archive.gif\","));
        }
        assertFalse(_id.isEmpty());
    }
}
