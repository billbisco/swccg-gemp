package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Legacy batch C1 (swccg-guild/work#236, Bill 2026-11-09: spelling, capitals and punctuation in a card document are
 * versions). Nine families whose PC website card-list text (2012-07-21 to 2013-01-11) differs from the release-sheet slip
 * only in capitals or punctuation: the card-list text is PC-E1 and the current card, the slip text is the original
 * (22xx). The Master List (2013-04-27) reprints each original exactly: a stale reprint, not a version. Obi-Wan In
 * Radiant VII: the card list also prints the title "Obi-Wan in Radiant VII" (display only on the current card).
 * Aratech Corporation (V) is held: its PC card image prints a third wording ("you may reveal", slip 24E).
 * Images are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchC1VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1220_127", "Deploy on Leia. At same location, your unique (•) Rebels are deploy -1 and their immunity to attrition is +1. Dark Side Republic characters are deploy +2 and forfeit -2. (Immune to Alter.)"),
            Map.entry("1240_34", "Deploy on your [Reflections II] objective; 'Xizor' on it (and on Guri) is treated as 'Shada.' Non-[Permanent Weapon] alien females are Mistryl and Black Sun agents. You may not deploy cards with ability except Mistryl and [Independent Starship] starships. Mistryl (except Kitik) are power +1 (+2 if unique (•)) and, while Scum And Villainy on table, deploy +1. (Immune to Alter.)"),
            Map.entry("1240_71", "'Device' on Harvest may be treated as '[A New Hope] or [Hoth] droid.' While at a farm: your characters present are forfeit +3; may raise this site (if converted) to the top; and, once per move phase, may put hand in Used Pile to place a 'crop' on Reserve Deck."),
            Map.entry("1250_83", "May add 1 pilot. Permanent pilot is •Obi-Wan, who provides ability of 6. Opponent's starships may not 'cloak'. Your total ability here may not be reduced."),
            Map.entry("1271_18", "If Death Star orbits Alderaan (and [Virtual Block 7] Central Core on table), deploy on Death Star and fire Superlaser at Alderaan; it is 'blown away.' Once during your control phase, if Death Star orbiting a planet (except a Rebel Base) without related sites, may fire Superlaser at planet (regardless of Objective restrictions) as follows: Draw destiny. Add 2 if you control that system. If destiny + Y - Z > 8, target is 'blown away' and opponent loses 4 Force from Life Force. Y = total Death Star sites where opponent has no presence. Z = total sites at Yavin 4."),
            Map.entry("1210_102", "Adds 2 to power of anything he pilots. While piloting Red 6: it is Heroic, immune to attrition < 4, Jek draws one battle destiny if unable to otherwise, and, during your control phase, may use 1 Force to retrieve a Rogue squadron pilot or an X-wing."),
            Map.entry("1210_112", "Adds 2 to power of anything he pilots. While piloting at Dantooine system, when opponent deploys a character to a related site, they choose: opponent must lose 1 Force, or that character may not battle or move this turn."),
            Map.entry("1210_116", "Deploy on Han or a Lars. Character cannot be targeted by [Permanent Weapon] weapons or be captured. If just lost, place this device in Used Pile. Force loss from Or Be Destroyed must come from Reserve Deck (if possible) and may not be reduced below 2."),
            Map.entry("1210_123", "Deploy on table (only at start of game) with up to three systems from outside your deck face-down under here. If non-[Virtual Block 4] Hidden Base on table: once per game, may deploy a system from here and/or exchange Hidden Base indicator with a system (from parsec 1 to 8) under here. (Immune to Alter.)"));
    /** version id -> {current printing id, game text, printed on}; every original is superseded by the card list on 2012-07-21 */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2220_127", new String[]{"1220_127", "Deploy on Leia. At same location, your unique (•) Rebels are deploy -1 and their immunity to attrition is +1. Dark Side Republic characters are deploy +2 and forfeit -2. (Immune to Alter.)", "2009-12-07"}),
            Map.entry("2240_34", new String[]{"1240_34", "Deploy on your [Reflections II] objective; 'Xizor' on it (and on Guri) is treated as 'Shada.' Non-[Permanent Weapon] alien females are Mistryl and Black Sun agents. You may not deploy cards with ability except Mistryl and [Independent Starship] starships. Mistryl (except Kitik) are power +1 (+2 if unique (•)) and, while Scum And Villainy on table, deploy +1. (Immune to Alter.)", "2009-12-07"}),
            Map.entry("2240_71", new String[]{"1240_71", "'Device' on Harvest may be treated as '[A New Hope] or [Hoth] droid.' While at a farm: your characters present are forfeit +3; may raise this site (if converted) to the top; and, once per move phase, may put hand in Used Pile to place a 'crop' on Reserve Deck.", "2009-12-07"}),
            Map.entry("2250_83", new String[]{"1250_83", "May add 1 pilot. Permanent pilot is •Obi-Wan, who provides ability of 6. Opponent's starships may not 'cloak'. Your total ability here may not be reduced.", "2009-12-07"}),
            Map.entry("2271_18", new String[]{"1271_18", "If Death Star orbits Alderaan (and [Virtual Block 7] Central Core on table), deploy on Death Star and fire Superlaser at Alderaan; it is 'blown away.' Once during your control phase, if Death Star orbiting a planet (except a Rebel Base) without related sites, may fire Superlaser at planet (regardless of Objective restrictions) as follows: Draw destiny. Add 2 if you control that system. If destiny + Y - Z > 8, target is 'blown away' and opponent loses 4 Force from Life Force. Y = total Death Star sites where opponent has no presence. Z = total sites at Yavin 4.", "2010-06-18"}),
            Map.entry("2210_102", new String[]{"1210_102", "Adds 2 to power of anything he pilots. While piloting Red 6: it is Heroic, immune to attrition < 4, Jek draws one battle destiny if unable to otherwise, and, during your control phase, may use 1 Force to retrieve a Rogue squadron pilot or an X-wing.", "2009-12-07"}),
            Map.entry("2210_112", new String[]{"1210_112", "Adds 2 to power of anything he pilots. While piloting at Dantooine system, when opponent deploys a character to a related site, they choose: opponent must lose 1 Force, or that character may not battle or move this turn.", "2009-12-07"}),
            Map.entry("2210_116", new String[]{"1210_116", "Deploy on Han or a Lars. Character cannot be targeted by [Permanent Weapon] weapons or be captured. If just lost, place this device in Used Pile. Force loss from Or Be Destroyed must come from Reserve Deck (if possible) and may not be reduced below 2.", "2009-12-07"}),
            Map.entry("2210_123", new String[]{"1210_123", "Deploy on table (only at start of game) with up to three systems from outside your deck face-down under here. If non-[Virtual Block 4] Hidden Base on table: once per game, may deploy a system from here and/or exchange Hidden Base indicator with a system (from parsec 1 to 8) under here. (Immune to Alter.)", "2009-12-07"}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheCardListText() {
        assertEquals(9, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OriginalsAreTheirOwnCards() {
        assertEquals(9, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, "", version.label());
            assertEquals(id, v[1], card.getGameText());
            assertEquals(id, LocalDate.parse(v[2]), version.printedOn());
            assertEquals(id, LocalDate.of(2012, 7, 21), version.supersededOn());
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(v[0]);
            assertEquals(id, current.getTitle(), card.getTitle());
            assertEquals(id, current.getLore(), card.getLore());
            assertNull(id, card.getPrintedTitle());
        });
    }

    @Test
    public void ObiWanInRadiantViiCardListTitleIsDisplayOnly() {
        SwccgCardBlueprint current = library.getSwccgoCardBlueprint("1250_83");
        assertEquals("Obi-Wan In Radiant VII", current.getTitle());
        assertNull(current.getPrintedTitle()); // R8 (2026-11-11): the current card is the Master List text; the card-list title is on 2250_1083
        SwccgCardBlueprint original = library.getSwccgoCardBlueprint("2250_83");
        assertEquals("Obi-Wan In Radiant VII", original.getTitle());
        assertNull(original.getPrintedTitle());
    }
}
