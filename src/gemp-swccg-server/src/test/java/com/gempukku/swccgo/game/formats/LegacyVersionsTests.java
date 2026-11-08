package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.collection.LegacyCardPool;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SortAndFilterCards;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Pre-errata Legacy versions (docs/legacy-versions.md): legal in Anything Goes, never in Legacy, and counted as the
 * same card as their current printing.
 */
public class LegacyVersionsTests {
    private static final List<String> PILOT_VERSIONS = List.of("2261_33", "2272_23");

    private static SwccgCardBlueprintLibrary library;
    private static SwccgoFormatLibrary formats;
    private static CollectionsManager collections;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
        formats = new SwccgoFormatLibrary(library);
        collections = new CollectionsManager(null, null, null, library);
    }

    @Test
    public void VersionIdsMapToTheirCurrentPrinting() {
        assertEquals("1261_33", LegacyVersion.currentPrintingIdOf("2261_33"));
        assertEquals("1261_33", LegacyVersion.currentPrintingIdOf("2261_1033"));
        assertEquals("1261_33", LegacyVersion.currentPrintingIdOf("2261_33*"));
        assertEquals("1291_29_BACK", LegacyVersion.currentPrintingIdOf("2291_29_BACK"));
        assertEquals("2261_33", LegacyVersion.versionIdOf("1261_33", 0));
        assertEquals("2261_1033", LegacyVersion.versionIdOf("1261_33", 1));
        for (String notAVersion : new String[]{"1261_33", "601_81", "1_19", "200_35", "2293_1", "2199_1", "2261_1000", "2261_x"})
            assertNull(notAVersion, LegacyVersion.currentPrintingIdOf(notAVersion));
    }

    @Test
    public void EveryImplementedVersionIsTheSameCardAsItsCurrentPrinting() {
        List<String> versions = LegacyCardPool.implementedVersionIds(library);
        assertTrue(versions.toString(), versions.containsAll(PILOT_VERSIONS));
        for (String id : versions) {
            SwccgCardBlueprint version = library.getSwccgoCardBlueprint(id);
            LegacyVersion info = version.getLegacyVersion();
            assertNotNull(id + " has no setLegacyVersion", info);
            assertEquals(id, LegacyVersion.currentPrintingIdOf(id), info.currentPrintingId());
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(info.currentPrintingId());
            assertNotNull(id + ": current card missing", current);
            assertNull(current.getLegacyVersion());
            assertEquals(id, current.getTitle(), version.getTitle());
            assertEquals(id, current.getTitles(), version.getTitles());
            assertEquals(id, current.getUniqueness(), version.getUniqueness());
            assertEquals(id, current.getSide(), version.getSide());
            assertEquals(id, current.getCardCategory(), version.getCardCategory());
            assertEquals(id, current.getCardTypes(), version.getCardTypes());
            assertEquals(id, current.hasVirtualSuffix(), version.hasVirtualSuffix());
            assertEquals(id, current.getPersonas(), version.getPersonas());
            assertEquals(id, current.getExpansionSet(), version.getExpansionSet());
            assertTrue(id, version.isLegacy());
            assertTrue(id + ": differs from its current card in game text or printed stats/icons",
                    differsInGameTextOrPrintedData(current, version));
            assertTrue(id + ": label shown in full name",
                    info.label().isEmpty() ? !GameUtils.getFullName(version).contains("[")
                            : GameUtils.getFullName(version).endsWith(" [" + info.label() + "]"));
        }
    }

    /** "A change is a change is a change" (Bill): a version differs in game text or in a printed stat or icon. */
    static boolean differsInGameTextOrPrintedData(SwccgCardBlueprint current, SwccgCardBlueprint version) {
        if (!java.util.Objects.equals(current.getGameText(), version.getGameText()))
            return true;
        List<java.util.function.Function<SwccgCardBlueprint, Object>> stats = List.of(
                SwccgCardBlueprint::getDestiny, SwccgCardBlueprint::getDeployCost, SwccgCardBlueprint::getPower,
                SwccgCardBlueprint::getAbility, SwccgCardBlueprint::getArmor, SwccgCardBlueprint::getManeuver,
                SwccgCardBlueprint::getLandspeed, SwccgCardBlueprint::getHyperspeed, SwccgCardBlueprint::getForfeit);
        for (var stat : stats) {
            if (!java.util.Objects.equals(printedStat(stat, current), printedStat(stat, version)))
                return true;
        }
        for (com.gempukku.swccgo.common.Icon icon : com.gempukku.swccgo.common.Icon.values()) {
            if (current.getIconCount(icon) != version.getIconCount(icon))
                return true;
        }
        return false;
    }

    /** A stat the card type does not have (the getter throws) counts as "none". */
    private static Object printedStat(java.util.function.Function<SwccgCardBlueprint, Object> stat, SwccgCardBlueprint card) {
        try {
            return stat.apply(card);
        } catch (UnsupportedOperationException notThisType) {
            return "n/a";
        }
    }

    @Test
    public void AVersionMayDifferOnlyInAPrintedStat() {
        // Dewback (V): the original printed MANEUVER 4; AR 2.0 errata made it ARMOR 4 with the same game text
        SwccgCardBlueprint current = library.getSwccgoCardBlueprint("1210_80");
        SwccgCardBlueprint version = library.getSwccgoCardBlueprint("2210_80");
        assertEquals(current.getGameText(), version.getGameText());
        assertTrue(differsInGameTextOrPrintedData(current, version));
        assertFalse(differsInGameTextOrPrintedData(current, current));
    }

    @Test
    public void PilotVersionsAreLegalInAnythingGoesButNotInLegacy() throws DeckInvalidException {
        for (String id : PILOT_VERSIONS) {
            formats.getFormat("anything_goes").validateCard(id, false);
            try {
                formats.getFormat("legacy").validateCard(id, false);
                fail(id + " must not be legal in Legacy");
            } catch (DeckInvalidException expected) {
                assertTrue(expected.getMessage(), expected.getMessage().contains("from set banned in this format"));
            }
            // The current printing stays legal in both
            String current = LegacyVersion.currentPrintingIdOf(id);
            formats.getFormat("anything_goes").validateCard(current, false);
            formats.getFormat("legacy").validateCard(current, false);
        }
    }

    @Test
    public void PilotVersionsAreInTheDeckBuilderForAnythingGoesOnly() {
        for (String id : PILOT_VERSIONS) {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(id);
            assertEquals(60, collections.getDefaultCollection(false).getItemCount(id));
            assertEquals(60, collections.getDefaultCollection(true).getItemCount(id));
            assertTrue(formats.getFormat("anything_goes").getAllCardBlueprintsValidInFormat().contains(blueprint));
            assertFalse(formats.getFormat("legacy").getAllCardBlueprintsValidInFormat().contains(blueprint));
        }
        var all = collections.getDefaultCollection(false).getAll().values();
        var anythingGoes = new SortAndFilterCards().process("format:anything_goes", all, library, formats, Collections.emptyMap());
        var legacy = new SortAndFilterCards().process("format:legacy", all, library, formats, Collections.emptyMap());
        var versionSet = new SortAndFilterCards().process("set:" + SortAndFilterCards.LEGACY_VERSIONS_SET_FILTER, all, library, formats, Collections.emptyMap());
        for (String id : PILOT_VERSIONS) {
            assertTrue(id, anythingGoes.stream().anyMatch(item -> item.getBlueprintId().equals(id)));
            assertFalse(id, legacy.stream().anyMatch(item -> item.getBlueprintId().equals(id)));
            assertTrue(id, versionSet.stream().anyMatch(item -> item.getBlueprintId().equals(id)));
        }
        assertTrue(versionSet.stream().allMatch(item -> LegacyVersion.isVersionBlueprint(item.getBlueprintId())));
        var rareVersions = new SortAndFilterCards().process("set:" + SortAndFilterCards.LEGACY_VERSIONS_SET_FILTER + " rarity:V",
                all, library, formats, Collections.emptyMap());
        assertTrue(rareVersions.stream().anyMatch(item -> item.getBlueprintId().equals("2261_33")));
    }

    private static SwccgDeck deck(String... cards) {
        SwccgDeck deck = new SwccgDeck("versions");
        for (String card : cards)
            deck.addCard(card);
        while (deck.getCards().size() < 60)
            deck.addCard("1_194"); // Stormtrooper
        return deck;
    }

    @Test
    public void AnythingGoesDeckMayUseAPreErrataVersionAndLegacyDeckMayNot() throws DeckInvalidException {
        formats.getFormat("anything_goes").validateDeck(deck("2261_33"));
        formats.getFormat("legacy").validateDeck(deck("1261_33"));
        try {
            formats.getFormat("legacy").validateDeck(deck("2261_33"));
            fail("Legacy deck with a pre-errata version");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("Dark Waters & Krayt Dragon Bones"));
        }
    }

    private static DefaultSwccgFormat restrictedAndBannedFormat(String restricted, String banned) {
        DefaultSwccgFormat format = new DefaultSwccgFormat(library, "versions test", false, false, false, false);
        format.setCode("versions_test");
        for (int set : new int[]{1, 1261, 1272, 2261, 2272})
            format.addValidSet(set);
        if (restricted != null)
            format.addRestrictedCard(restricted);
        if (banned != null)
            format.addBannedCard(banned);
        return format;
    }

    @Test
    public void MixingVersionsDoesNotAddCopiesOfARestrictedCard() throws DeckInvalidException {
        var restricted = restrictedAndBannedFormat("1261_33", null);
        restricted.validateDeck(deck("1261_33"));
        restricted.validateDeck(deck("2261_33"));
        for (String[] pair : new String[][]{{"1261_33", "2261_33"}, {"2261_33", "2261_33"}}) {
            try {
                restricted.validateDeck(deck(pair));
                fail("Two copies of a restricted card: " + String.join(",", pair));
            } catch (DeckInvalidException expected) {
                assertTrue(expected.getMessage(), expected.getMessage().contains("more than one copy of an restricted card"));
            }
        }
    }

    @Test
    public void BanningTheCurrentCardBansItsVersions() {
        var banned = restrictedAndBannedFormat(null, "1272_23");
        for (String id : new String[]{"1272_23", "2272_23"}) {
            try {
                banned.validateCard(id, false);
                fail(id + " must be banned with 1272_23");
            } catch (DeckInvalidException expected) {
                assertTrue(expected.getMessage(), expected.getMessage().contains("banned card"));
            }
        }
    }
}
