package com.gempukku.swccgo.cards.textstates;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.TextStatesTests;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Printed (o) text states built from Bill's rulings of 12 Nov 2026 (ai-team-skills RULINGS.md "printed-text readings"):
 * For Luck, Naboo: Battle Plains (both sides), Great Shot, Kid!, the four Premiere lightsabers, Zuckuss' Snare Rifle, and
 * the uniqueness-only errata (Beru Stew, Do, Or Do Not, He Is Not Ready, Innocent Scoundrel, Come Here You Big Coward).
 * Printed texts are the plain wiki.swccg.com articles, checked against their Decipher archive scans. One test per state:
 * text, text-state information, and the same card as the bare id (uniqueness differs only for the uniqueness-only errata).
 * Gameplay: RuledTextStatesGameplayTests.
 */
public class RuledTextStatesTests {
    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void load() {
        library = new SwccgCardBlueprintLibrary();
    }

    private static SwccgCardBlueprint check(String stateId, String text, LocalDate printedOn, LocalDate supersededOn) {
        String bareId = TextState.currentIdOf(stateId);
        SwccgCardBlueprint state = library.getSwccgoCardBlueprint(stateId);
        SwccgCardBlueprint bare = library.getSwccgoCardBlueprint(bareId);
        if (text != null) {
            assertEquals(stateId, text, state.getGameText());
        }
        TextState ts = state.getTextState();
        assertEquals(stateId, bareId, ts.currentId());
        assertEquals(stateId, "Printed", ts.label());
        assertEquals(stateId, printedOn, ts.printedOn());
        assertEquals(stateId, supersededOn, ts.supersededOn());
        assertNull(bare.getTextState());
        assertEquals(stateId, bare.getTitle(), state.getTitle());
        assertEquals(stateId, bare.getSide(), state.getSide());
        assertEquals(stateId, bare.getCardTypes(), state.getCardTypes());
        assertEquals(stateId, bare.getPersonas(), state.getPersonas());
        assertEquals(stateId, bare.getLore(), state.getLore());
        assertTrue(stateId, bare.getClass().isInstance(state));
        if (TextStatesTests.PRINTED_NON_UNIQUE_BY_ERRATA.contains(stateId)) {
            assertEquals(stateId, Uniqueness.UNIQUE, bare.getUniqueness());
            assertEquals(stateId, Uniqueness.UNRESTRICTED, state.getUniqueness());
        }
        else {
            assertEquals(stateId, bare.getUniqueness(), state.getUniqueness());
        }
        return state;
    }

    private static final LocalDate PREMIERE = LocalDate.of(1995, 12, 1);
    private static final LocalDate GLOSSARY_2 = LocalDate.of(1998, 11, 1);
    private static final LocalDate AR_2023 = LocalDate.of(2023, 7, 30);

    @Test
    public void ForLuckPrinted() {
        var state = check("102_1o", "Deploy on your side of table. When Counter Assault is played, may use 1 Force to add one destiny to your total. Also, when Sense or Alter is played, may use X Force to exclude X Jedi from being the \"highest-ability character\".",
                LocalDate.of(1996, 4, 1), LocalDate.of(2002, 1, 29));
        assertNotEquals(library.getSwccgoCardBlueprint("102_1").getGameText(), state.getGameText());
    }

    @Test
    public void NabooBattlePlainsPrintedSaysTanks() {
        var light = check("12_79o", null, LocalDate.of(2001, 8, 1), AR_2023);
        assertEquals("Your tanks deploy +1 here.", light.getLocationDarkSideGameText());
        assertEquals("Your AATs deploy +1 here.", library.getSwccgoCardBlueprint("12_79").getLocationDarkSideGameText());
        assertEquals(library.getSwccgoCardBlueprint("12_79").getLocationLightSideGameText(), light.getLocationLightSideGameText());

        var dark = check("12_170o", null, LocalDate.of(2001, 8, 1), AR_2023);
        assertEquals("Unless Gungan Energy Shield on table, your tanks are each power +2 here.", dark.getLocationDarkSideGameText());
        assertEquals(library.getSwccgoCardBlueprint("12_170").getLocationLightSideGameText(), dark.getLocationLightSideGameText());
        // Wording only (Bill 2026-11-12): every AAT is a tank, so the printed classes add no behaviour
        assertEquals(com.gempukku.swccgo.cards.set12.light.Card12_079.class, light.getClass().getSuperclass());
        assertEquals(com.gempukku.swccgo.cards.set12.dark.Card12_170.class, dark.getClass().getSuperclass());
    }

    @Test
    public void GreatShotKidPrinted() {
        var state = check("11_18o", "Deploy on table. If Death Star \"blown away\": once during each of your turns you may deploy (for free) a starship from hand or Reserve Deck and reshuffle; if you just deployed a unique (•) starship to a system location, retrieve 3 Force. (Immune to Alter.)",
                LocalDate.of(2001, 5, 1), LocalDate.of(2004, 2, 9));
        assertEquals(0, state.getClass().getDeclaredMethods().length); // wording only: plays as the bare card
    }

    @Test
    public void PremiereLightsabersPrinted() {
        String x = "Use X Force to deploy on any warrior where X = (7 - warrior's ability), and you may then use as follows: Add 1 to Force drain where present. OR Target a character by using X Force. Draw two destiny. Character hit if total destiny > ability.";
        check("1_155o", x, PREMIERE, GLOSSARY_2);
        check("1_314o", x, PREMIERE, GLOSSARY_2);
        check("1_157o", "Deploy only on Obi-Wan (for free), and you may then use as follows: Add 1 to Force drain where present. OR Target a character for free. Draw two destiny. Character hit, and its forfeit = 0, if total destiny > ability.", PREMIERE, GLOSSARY_2);
        check("1_324o", "Deploy only on Vader (for free), and you may then use as follows: Add 1 to Force drain where present. OR Target a character for free. Draw two destiny. Character hit, and its forfeit = 0, if total destiny > ability.", PREMIERE, GLOSSARY_2);
    }

    @Test
    public void ZuckussSnareRiflePrinted() {
        check("4_180o", "Deploy on Zuckuss, or use 1 Force to deploy on any other bounty hunter. May target using 2 Force. Draw destiny. Character may be immediately captured if destiny -1 > ability. Creature immediately lost if destiny +1 > defense value.",
                LocalDate.of(1997, 4, 1), GLOSSARY_2);
    }

    @Test
    public void UniquenessOnlyErrataPrintedStatesAreNonUnique() {
        var beru = check("1_72o", null, PREMIERE, GLOSSARY_2);
        var doOrDoNot = check("4_21o", null, LocalDate.of(1997, 4, 1), AR_2023);
        var notReady = check("4_123o", null, LocalDate.of(1997, 4, 1), AR_2023);
        var coward = check("7_224o", null, LocalDate.of(1999, 11, 1), AR_2023);
        var scoundrel = check("5_53o", "USED: If opponent just targeted one of your gamblers, opponent must choose to select a new target or lose 2 Force. LOST: Cancel any Effect (except those Immune to Alter) deployed on Han or your Lando.",
                LocalDate.of(1997, 11, 1), LocalDate.of(2002, 1, 29));
        // Same game text as the bare card except Innocent Scoundrel's wording
        assertEquals(library.getSwccgoCardBlueprint("1_72").getGameText(), beru.getGameText());
        assertEquals(library.getSwccgoCardBlueprint("4_21").getGameText(), doOrDoNot.getGameText());
        assertEquals(library.getSwccgoCardBlueprint("4_123").getGameText(), notReady.getGameText());
        assertEquals(library.getSwccgoCardBlueprint("7_224").getGameText(), coward.getGameText());
        assertNotEquals(library.getSwccgoCardBlueprint("5_53").getGameText(), scoundrel.getGameText());
        // Printed "Come Here You Big Coward!" (the errata dropped the exclamation point); the title is unchanged
        assertEquals("Come Here You Big Coward!", coward.getDisplayTitle());
        assertNull(library.getSwccgoCardBlueprint("7_224").getDisplayTitle());
        assertEquals(TextStatesTests.PRINTED_NON_UNIQUE_BY_ERRATA.size(), 5);
    }

    @Test
    public void BareInnocentScoundrelIsUniqueByErrata() {
        assertEquals(Uniqueness.UNIQUE, library.getSwccgoCardBlueprint("5_53").getUniqueness());
        for (String bare : new String[]{"1_72", "4_21", "4_123", "7_224"}) {
            assertEquals(bare, Uniqueness.UNIQUE, library.getSwccgoCardBlueprint(bare).getUniqueness());
        }
    }
}
