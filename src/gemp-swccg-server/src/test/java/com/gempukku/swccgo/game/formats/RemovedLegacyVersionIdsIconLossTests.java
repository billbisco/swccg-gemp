package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.collection.LegacyCardPool;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Version ids that existed only because of a false version (audit 2026-11-08, Bill: "If there are no changes, there
 * is no errata"): the 2012-13 PC website card list spelled out or dropped icons, and these versions differed from
 * the real text only in that notation. blueprintMapping.txt aliases each to its current card, so decks that use them still load and validate in Anything Goes, and
 * the card pool no longer offers them as versions. 2261_1033 (Dark Waters PC-E1) and 2261_2042 (Lone Pilot PC-E2) are
 * versions again (legacy batch B16: the card list also reordered those texts, and reorders are versions).
 */
public class RemovedLegacyVersionIdsIconLossTests {
    private static final Map<String, String> REMOVED = Map.ofEntries(
            Map.entry("2210_105", "1210_105"),
            Map.entry("2210_150", "1210_150"),
            Map.entry("2230_105", "1230_105"),
            Map.entry("2250_41", "1250_41"),
            Map.entry("2261_40", "1261_40"),
            Map.entry("2261_98", "1261_98"),
            Map.entry("2263_29", "1263_29"),
            Map.entry("2271_3", "1271_3"),
            Map.entry("2271_31", "1271_31"),
            Map.entry("2272_1", "1272_1"),
            Map.entry("2273_4", "1273_4"),
            Map.entry("2274_17", "1274_17"),
            Map.entry("2274_2", "1274_2"),
            Map.entry("2274_3", "1274_3"),
            Map.entry("2210_108", "1210_108"),
            Map.entry("2210_154", "1210_154"),
            Map.entry("2210_158", "1210_158"),
            Map.entry("2210_57", "1210_57"),
            Map.entry("2210_64", "1210_64"),
            Map.entry("2220_129", "1220_129"),
            Map.entry("2220_153", "1220_153"),
            Map.entry("2220_20", "1220_20"),
            Map.entry("2220_44", "1220_44"),
            Map.entry("2220_57", "1220_57"),
            Map.entry("2220_66", "1220_66"),
            Map.entry("2230_2", "1230_2"),
            Map.entry("2230_21", "1230_21"),
            Map.entry("2230_3", "1230_3"),
            Map.entry("2230_32", "1230_32"),
            Map.entry("2230_40", "1230_40"),
            Map.entry("2230_45", "1230_45"),
            Map.entry("2230_46", "1230_46"),
            Map.entry("2230_5", "1230_5"),
            Map.entry("2230_61", "1230_61"),
            Map.entry("2230_65", "1230_65"),
            Map.entry("2240_94", "1240_94"),
            Map.entry("2250_25", "1250_25"),
            Map.entry("2261_100", "1261_100"),
            Map.entry("2261_102", "1261_102"),
            Map.entry("2261_44", "1261_44"),
            Map.entry("2262_6", "1262_6"),
            Map.entry("2263_10", "1263_10"),
            Map.entry("2263_3", "1263_3"),
            Map.entry("2271_11", "1271_11"),
            Map.entry("2271_12", "1271_12"),
            Map.entry("2271_41", "1271_41"),
            Map.entry("2271_6", "1271_6"),
            Map.entry("2271_8", "1271_8"),
            Map.entry("2272_20", "1272_20"),
            Map.entry("2273_22", "1273_22"),
            Map.entry("2281_12", "1281_12"),
            Map.entry("2282_25", "1282_25"));

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
    public void RemovedVersionIdsLoadTheirTarget() {
        assertEquals(52, REMOVED.size());
        REMOVED.forEach((removed, target) -> {
            SwccgCardBlueprint alias = library.getSwccgoCardBlueprint(removed);
            assertNotNull(removed, alias);
            assertEquals(removed, target, library.getBaseBlueprintId(removed));
            SwccgCardBlueprint targetCard = library.getSwccgoCardBlueprint(target);
            assertSame(removed, targetCard.getClass(), alias.getClass());
            assertEquals(removed, targetCard.getGameText(), alias.getGameText());
            assertEquals(removed, targetCard.getTitle(), alias.getTitle());
            if (target.startsWith("22"))
                assertEquals(removed, targetCard.getLegacyVersion().label(), alias.getLegacyVersion().label());
            else
                assertNull(removed, alias.getLegacyVersion());
        });
    }

    @Test
    public void RemovedVersionIdsAreNotOfferedAsVersions() {
        List<String> versions = LegacyCardPool.implementedVersionIds(library);
        for (String removed : REMOVED.keySet()) {
            assertFalse(removed, versions.contains(removed));
            assertEquals(removed, 0, collections.getDefaultCollection(false).getItemCount(removed));
        }
    }

    @Test
    public void AnythingGoesDeckWithTheRemovedVersionIdsStillValidates() throws DeckInvalidException {
        var anythingGoes = formats.getFormat("anything_goes");
        for (Side side : Side.values()) {
            SwccgDeck deck = new SwccgDeck("removed versions " + side);
            for (String removed : REMOVED.keySet()) {
                if (library.getSwccgoCardBlueprint(removed).getSide() == side) {
                    anythingGoes.validateCard(removed, false);
                    deck.addCard(removed);
                }
            }
            while (deck.getCards().size() < 60)
                deck.addCard(side == Side.DARK ? "1_194" : "1_12"); // Stormtrooper / Jawa
            anythingGoes.validateDeck(deck);
        }
    }
}
