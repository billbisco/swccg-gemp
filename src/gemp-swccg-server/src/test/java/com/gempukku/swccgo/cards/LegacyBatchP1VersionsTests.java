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
 * Legacy batch P1 (swccg-guild/work#236 pilot, Bill 2026-11-09: spelling, capitals and punctuation in a card document
 * are versions). Kuat Drive Yards (V): the card list's "Imperial-Class" is PC-E1 (current), the slip's "Imperial-class"
 * the original 2240_33. Colonel Jendon In Onyx 1: the card list's comma ("•Jendon, who") is PC-E1 (current), the slip
 * the original 2263_17. Han's Blaster, So Uncivilized and Het Nkik (V): the 2012-07-21 card-list text ("cancelled") is
 * a new PC-E1 between the original and the former PC-E1, which becomes PC-E2 (current; "canceled", Virtual Block 8 list
 * from 2012-12-12). The Master List copies of the originals are exact stale reprints, not versions.
 * Images are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchP1VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1240_33", "Deploy on table. Unique (•) Imperial-Class Star Destroyers draw one battle destiny if not able to otherwise and are immune to attrition < 4. During your deploy phase, may use 2 Force to [download] a battleground system. We're In Attack Position Now is canceled. (Immune to Alter.)"),
            Map.entry("1263_17", "May deploy as a 'react.' Permanent pilot is •Jendon, who provides ability of 2. When deployed, may [download] a weapon on Onyx 1. Adds one battle destiny if with Onyx 2. Immune to attrition < 4."),
            Map.entry("1281_46", "Deploy on Han. I've Got a Bad Feeling About This is canceled. When fired, Han is cumulatively power +1 until end of turn. May target a character or creature for free. Draw destiny. Target hit, forfeit = 0 and opponent loses 1 Force if destiny +1 > defense value."),
            Map.entry("1281_29", "Deploys only on Tatooine. While with another Jawa, your Force drains and Force drain bonuses here may not be canceled. During your control phase, may draw X cards from Reserve Deck, where X = the number of deserts you control with a Jawa."));
    /** version id -> {current printing id, label, game text, printed on, superseded on} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2240_33", new String[]{"1240_33", "", "Deploy on table. Unique (•) Imperial-class Star Destroyers draw one battle destiny if not able to otherwise and are immune to attrition < 4. During your deploy phase, may use 2 Force to [download] a battleground system. We're In Attack Position Now is canceled. (Immune to Alter.)", "2009-12-07", "2012-07-21"}),
            Map.entry("2263_17", new String[]{"1263_17", "", "May deploy as a 'react.' Permanent pilot is •Jendon who provides ability of 2. When deployed, may [download] a weapon on Onyx 1. Adds one battle destiny if with Onyx 2. Immune to attrition < 4.", "2010-03-12", "2012-07-21"}),
            Map.entry("2281_46", new String[]{"1281_46", "", "Deploy on Han. I've Got A Bad Feeling About This is canceled. When fired, Han is cumulatively power +1 until end of turn. May target a character or creature for free. Draw destiny. Target hit, its forfeit = 0 and opponent loses 1 Force if destiny +1 > defense value.", "2012-05-11", "2012-07-21"}),
            Map.entry("2281_1046", new String[]{"1281_46", "PC-E1", "Deploy on Han. I've Got a Bad Feeling About This is cancelled. When fired, Han is cumulatively power +1 until end of turn. May target a character or creature for free. Draw destiny. Target hit, forfeit = 0 and opponent loses 1 Force if destiny +1 > defense value.", "2012-07-21", "2012-12-12"}),
            Map.entry("2281_29", new String[]{"1281_29", "", "Deploys only on Tatooine. While with another Jawa, your Force drains and Force drain bonuses here may not be canceled. During your control phase, may draw X cards from Reserve Deck, where X = number of deserts you control with a Jawa.", "2012-05-11", "2012-07-21"}),
            Map.entry("2281_1029", new String[]{"1281_29", "PC-E1", "Deploys only on Tatooine. While with another Jawa, your Force drains and Force drain bonuses here may not be cancelled. During your control phase, may draw X cards from Reserve Deck, where X = the number of deserts you control with a Jawa.", "2012-07-21", "2012-12-12"}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(4, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OlderVersionsAreTheirOwnCardsInDateOrder() {
        assertEquals(6, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, v[2], card.getGameText());
            assertEquals(id, LocalDate.parse(v[3]), version.printedOn());
            assertEquals(id, LocalDate.parse(v[4]), version.supersededOn());
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(v[0]);
            assertEquals(id, current.getTitle(), card.getTitle());
            assertEquals(id, current.getLore(), card.getLore());
        });
        // the inserted PC-E1 follows the original and precedes the current text
        assertEquals(library.getSwccgoCardBlueprint("2281_46").getLegacyVersion().supersededOn(), library.getSwccgoCardBlueprint("2281_1046").getLegacyVersion().printedOn());
        assertEquals(library.getSwccgoCardBlueprint("2281_29").getLegacyVersion().supersededOn(), library.getSwccgoCardBlueprint("2281_1029").getLegacyVersion().printedOn());
    }
}
