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
    private static final List<String> REPEATS_OF_CURRENT_TEXT = List.of("2220_3169", "2220_4169", "2261_1033", "2261_5033", "2240_1055", "2273_2010", "2281_32", "2281_1032", "2281_2037", "2261_1024", "2261_3024", "2261_21", "2261_2021", "2261_1093", "2261_2093");
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
        // AI printings keep their suffix (docs/legacy-versions.md, "AI printings")
        assertEquals("1250_45ai", LegacyVersion.currentPrintingIdOf("2250_45ai"));
        assertEquals("1250_45ai", LegacyVersion.currentPrintingIdOf("2250_1045ai*"));
        assertEquals("2250_45ai", LegacyVersion.versionIdOf("1250_45ai", 0));
        assertEquals("2250_1045ai", LegacyVersion.versionIdOf("1250_45ai", 1));
        assertEquals("1240_3", LegacyVersion.currentPrintingIdOf("2240_3"));
        for (String notAVersion : new String[]{"1261_33", "601_81", "1_19", "200_35", "2293_1", "2199_1", "2261_1000", "2261_x",
                "1250_45ai", "2250_ai", "2250_1000ai", "2250_45xai"})
            assertNull(notAVersion, LegacyVersion.currentPrintingIdOf(notAVersion));
    }

    /** Versions whose words the current text (a later printing) repeats word for word (Batch S1: the PC forum errata texts). */
    private static final java.util.Set<String> WORD_FOR_WORD_REPEATS = java.util.Set.of("2271_2002", "2250_2044", "2273_3027", "2271_2039", "2261_2065", "2271_3002", "2271_3039", "2261_3065");

    @Test
    public void EveryImplementedVersionIsTheSameCardAsItsCurrentPrinting() {
        List<String> versions = LegacyCardPool.implementedVersionIds(library);
        assertTrue(versions.toString(), versions.containsAll(PILOT_VERSIONS));
        List<String> sameAsCurrent = new java.util.ArrayList<>();
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
            boolean differs = differsInGameTextOrPrintedData(current, version)
                    || !printedText(version).equals(printedText(current))
                    || !iconCounts(version).equals(iconCounts(current))
                    || version.getPrintedTitle() != null
                    || backSideDiffers(id, info.currentPrintingId());
            if (WORD_FOR_WORD_REPEATS.contains(id)) {
                // Bill 2026-11-11: every published text is a version, even one the current text later repeats word for word.
                assertTrue(id + ": listed as a repeat but differs", !differsInGameTextOrPrintedData(current, version)
                        && printedText(version).equals(printedText(current)));
            } else if (!differs) {
                if (REPEATS_OF_CURRENT_TEXT.contains(id))
                    sameAsCurrent.add(id);
                else
                    assertTrue(id + ": differs from its current card in game text (incl. location texts), printed stats/icons or printed title",
                            // batch R8: the current Master List text repeats these word for word (LegacyBatchR8B1B6VersionsTests)
                            com.gempukku.swccgo.cards.LegacyBatchR8B1B6VersionsTests.REPEATED_BY_CURRENT.contains(id)
                                    || republishedLater(versions, id, info.currentPrintingId())
                                    || com.gempukku.swccgo.cards.LegacyRepeatedTexts.isRepeatedByCurrent(info));
            }
            assertTrue(id + ": label shown in full name",
                    info.label().isEmpty() ? !GameUtils.getFullName(version).contains("[")
                            : GameUtils.getFullName(version).endsWith(" [" + info.label() + "]"));
        }
        // A version may repeat the current text word for word when a different text was published in between (Bill's
        // ruling of 2026-11-11, RULINGS.md: every published text is a version, even a word-for-word repeat; batch B41).
        // Any other version must differ from its current card in game text (incl. location texts), printed stats/icons or printed title.
        assertEquals(new java.util.TreeSet<>(REPEATS_OF_CURRENT_TEXT), new java.util.TreeSet<>(sameAsCurrent));
    }

    /**
     * Every publication is a version (Bill, 2026-11-11): an older text that a later document printed again word for word
     * (e.g. the release-sheet original that the 2013 Master List repeats after the 2012 card list changed it) is the
     * same text as its current card. It is still a version, as long as another version of the same card differs.
     */
    private boolean republishedLater(List<String> versions, String versionId, String currentId) {
        SwccgCardBlueprint current = library.getSwccgoCardBlueprint(currentId);
        for (String other : versions) {
            if (other.equals(versionId) || !currentId.equals(LegacyVersion.currentPrintingIdOf(other)))
                continue;
            SwccgCardBlueprint sibling = library.getSwccgoCardBlueprint(other);
            if (differsInGameTextOrPrintedData(current, sibling) || !printedText(sibling).equals(printedText(current)))
                return library.getSwccgoCardBlueprint(versionId).getLegacyVersion().supersededOn() != null;
        }
        return false;
    }

    /** A two-sided version whose front is unchanged differs on its back (Wookiee Slaving Operation, 2014 errata to the 7 side; batch B39). */
    private boolean backSideDiffers(String versionId, String currentId) {
        if (versionId.endsWith("_BACK"))
            return false;
        try {
            SwccgCardBlueprint versionBack = library.getSwccgoCardBlueprint(versionId + "_BACK");
            SwccgCardBlueprint currentBack = library.getSwccgoCardBlueprint(currentId + "_BACK");
            return versionBack != null && currentBack != null
                    && differsInGameTextOrPrintedData(currentBack, versionBack);
        } catch (RuntimeException noBackSide) {
            return false;
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

    /** Pre-errata versions of AI printings (docs/legacy-versions.md, "AI printings"). */
    private static final List<String> AI_VERSIONS = List.of("2240_3", "2250_2ai", "2250_44ai", "2250_45ai", "2281_30ai");

    @Test
    public void AiPrintingVersionsAreInThePoolForAnythingGoesOnly() throws DeckInvalidException {
        List<String> versions = LegacyCardPool.implementedVersionIds(library);
        for (String id : AI_VERSIONS) {
            assertTrue(id, versions.contains(id));
            assertEquals(id, 60, collections.getDefaultCollection(false).getItemCount(id));
            formats.getFormat("anything_goes").validateCard(id, false);
            try {
                formats.getFormat("legacy").validateCard(id, false);
                fail(id + " must not be legal in Legacy");
            } catch (DeckInvalidException expected) {
                assertTrue(expected.getMessage(), expected.getMessage().contains("from set banned in this format"));
            }
        }
    }

    @Test
    public void AnAiPrintingAndItsVersionsAreTheSameCardAsTheBasePrinting() {
        DefaultSwccgFormat format = aiFormat(null, null);
        for (String[] sameCard : new String[][]{{"2250_45ai", "1250_45ai", "2250_45", "1250_45"}, {"2240_3", "1240_3", "2240_2", "1240_2"},
                {"2281_30ai", "1281_30ai", "2281_30", "1281_30"}, {"2250_44ai", "1250_44ai", "2250_1044", "1250_44"}}) {
            for (String id : sameCard)
                assertEquals(id, format.sameCardBlueprintId(sameCard[3]), format.sameCardBlueprintId(id));
        }
        assertEquals("1281_30", com.gempukku.swccgo.common.LegacyIds.withoutAlternateImageSuffix("1281_30ai"));
        assertEquals("1281_30", com.gempukku.swccgo.common.LegacyIds.withoutAlternateImageSuffix("1281_30"));
        assertEquals("200_1ai", com.gempukku.swccgo.common.LegacyIds.withoutAlternateImageSuffix("200_1ai"));
        assertEquals("2250_45ai", com.gempukku.swccgo.common.LegacyIds.withoutAlternateImageSuffix("2250_45ai"));
    }

    @Test
    public void MixingAnAiVersionWithTheBasePrintingDoesNotAddCopiesOfARestrictedCard() throws DeckInvalidException {
        var restricted = aiFormat("1250_2", null);
        restricted.validateDeck(deck("2250_2ai"));
        for (String[] pair : new String[][]{{"1250_2", "2250_2ai"}, {"1250_2ai", "2250_2ai"}, {"2250_2", "2250_2ai"}}) {
            try {
                restricted.validateDeck(deck(pair));
                fail("Two copies of a restricted card: " + String.join(",", pair));
            } catch (DeckInvalidException expected) {
                assertTrue(expected.getMessage(), expected.getMessage().contains("more than one copy of an restricted card"));
            }
        }
        var banned = aiFormat(null, "1250_2");
        for (String id : new String[]{"1250_2ai", "2250_2ai"}) {
            try {
                banned.validateCard(id, false);
                fail(id + " must be banned with 1250_2");
            } catch (DeckInvalidException expected) {
                assertTrue(expected.getMessage(), expected.getMessage().contains("banned card"));
            }
        }
    }

    private static DefaultSwccgFormat aiFormat(String restricted, String banned) {
        DefaultSwccgFormat format = new DefaultSwccgFormat(library, "ai versions test", false, false, false, false);
        format.setCode("ai_versions_test");
        for (int set : new int[]{1, 601, 1240, 1250, 1281, 2240, 2250, 2281})
            format.addValidSet(set);
        if (restricted != null)
            format.addRestrictedCard(restricted);
        if (banned != null)
            format.addBannedCard(banned);
        return format;
    }

    /** Game text of any card, both location texts included (locations have no plain game text). */
    private static String printedText(SwccgCardBlueprint blueprint) {
        if (blueprint.getCardCategory() != com.gempukku.swccgo.common.CardCategory.LOCATION)
            return blueprint.getGameText();
        return blueprint.getGameText() + " | " + blueprint.getLocationLightSideGameText() + " | " + blueprint.getLocationDarkSideGameText();
    }

    private static java.util.Map<com.gempukku.swccgo.common.Icon, Integer> iconCounts(SwccgCardBlueprint blueprint) {
        java.util.Map<com.gempukku.swccgo.common.Icon, Integer> counts = new java.util.EnumMap<>(com.gempukku.swccgo.common.Icon.class);
        for (com.gempukku.swccgo.common.Icon icon : com.gempukku.swccgo.common.Icon.values())
            counts.put(icon, blueprint.getIconCount(icon));
        return counts;
    }
}
