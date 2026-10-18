package com.gempukku.swccgo.rules.legacy;

import com.gempukku.swccgo.cards.AbstractInterrupt;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyIds;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LegacyIdentityTests {
    private static SwccgCardBlueprintLibrary library;
    private static SwccgoFormatLibrary formats;

    @BeforeClass
    public static void loadLibrary() {
        library = new SwccgCardBlueprintLibrary();
        formats = new SwccgoFormatLibrary(library);
    }

    @Test
    public void LightPrintingsAliasTheLightPcIdsNotTheDarkSameTitle() {
        assertEquals("601_254", library.getBaseBlueprintId("1291_75"));
        assertEquals("601_238", library.getBaseBlueprintId("1271_43"));
        assertNotNull(library.getSwccgoCardBlueprint("1291_75"));
        assertNotNull(library.getSwccgoCardBlueprint("1271_43"));
        assertEquals(library.getSwccgoCardBlueprint("601_254").getClass(),
                library.getSwccgoCardBlueprint("1291_75").getClass());
        assertEquals(library.getSwccgoCardBlueprint("601_238").getClass(),
                library.getSwccgoCardBlueprint("1271_43").getClass());
        assertEquals(Side.LIGHT, library.getSwccgoCardBlueprint("1291_75").getSide());
        assertEquals(Side.LIGHT, library.getSwccgoCardBlueprint("1271_43").getSide());
    }

    @Test
    public void AngerFearAggressionVLoadsSameBlueprintAsIts601Id() {
        SwccgCardBlueprint fromBlock = library.getSwccgoCardBlueprint("1240_78");
        SwccgCardBlueprint fromPc = library.getSwccgoCardBlueprint("601_39");
        assertNotNull(fromBlock);
        assertNotNull(fromPc);
        assertEquals(fromPc.getClass(), fromBlock.getClass());
        assertEquals("601_39", library.getBaseBlueprintId("1240_78"));
        assertEquals("601_39", library.getBaseBlueprintId("601_39"));
    }

    @Test
    public void KetMalissShadowKillerLegacyAndBlock8PrintingsValidateAsOneCard() throws DeckInvalidException {
        SwccgFormat legacy = formats.getFormat("legacy");
        assertNotNull(legacy);
        legacy.validateCard("601_17", false);
        legacy.validateCard("1281_5", false);

        SwccgCardBlueprint fromPc = library.getSwccgoCardBlueprint("601_17");
        SwccgCardBlueprint fromBlock = library.getSwccgoCardBlueprint("1281_5");
        assertNotNull(fromPc);
        assertNotNull(fromBlock);
        assertEquals(fromPc.getClass(), fromBlock.getClass());
        assertEquals("601_17", library.getBaseBlueprintId("1281_5"));
        assertEquals(fromPc.getTitle(), fromBlock.getTitle());
        Set<String> alternates = library.getAllAlternates("601_17");
        assertNotNull(alternates);
        assertTrue(alternates.contains("1281_5"));
    }

    @Test
    public void IsLegacyBlueprintTrueForLegacySetsAndFalseOtherwise() {
        assertTrue(LegacyIds.isLegacyBlueprint("601_5"));
        assertTrue(LegacyIds.isLegacyBlueprint("1240_12"));
        assertTrue(LegacyIds.isLegacyBlueprint("1263_3"));
        assertFalse(LegacyIds.isLegacyBlueprint("200_1"));
        assertFalse(LegacyIds.isLegacyBlueprint("1103_18"));
    }

    @Test
    public void DoTheyHaveACodeClearanceHostPrintingLoadsImplementedClass() throws DeckInvalidException {
        SwccgCardBlueprint host = library.getSwccgoCardBlueprint("1261_23");
        SwccgCardBlueprint pc = library.getSwccgoCardBlueprint("601_34");
        assertNotNull(host);
        assertNotNull(pc);
        assertEquals(pc.getClass(), host.getClass());
        assertEquals("601_34", library.getBaseBlueprintId("1261_23"));
        assertEquals("601_34", library.getBaseBlueprintId("1261_26"));
        assertEquals(pc.getTitle(), host.getTitle());
        assertEquals(Uniqueness.UNIQUE, pc.getUniqueness());
        assertEquals(Uniqueness.UNIQUE, host.getUniqueness());

        SwccgFormat legacy = formats.getFormat("legacy");
        legacy.validateCard("601_34", false);
        legacy.validateCard("1261_23", false);
        Set<String> alternates = library.getAllAlternates("601_34");
        assertNotNull(alternates);
        assertTrue(alternates.contains("1261_23"));
        assertTrue(alternates.contains("1261_26"));
    }

    @Test
    public void FanfareTatooineHostPrintingLoadsImplementedClass() throws DeckInvalidException {
        SwccgCardBlueprint host = library.getSwccgoCardBlueprint("1272_9");
        SwccgCardBlueprint pc = library.getSwccgoCardBlueprint("601_35");
        assertNotNull(host);
        assertNotNull(pc);
        assertEquals(pc.getClass(), host.getClass());
        assertEquals("601_35", library.getBaseBlueprintId("1272_9"));
        assertEquals("601_35", library.getBaseBlueprintId("1200_5"));
        assertEquals(pc.getTitle(), host.getTitle());

        SwccgFormat legacy = formats.getFormat("legacy");
        legacy.validateCard("601_35", false);
        legacy.validateCard("1272_9", false);
        Set<String> alternates = library.getAllAlternates("601_35");
        assertNotNull(alternates);
        assertTrue(alternates.contains("1272_9"));
        assertTrue(alternates.contains("1200_5"));
    }

    @Test
    public void EveryLegacyMappingLineResolvesToALoadableClass() throws Exception {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                LegacyIdentityTests.class.getResourceAsStream("/blueprintMapping.txt"), StandardCharsets.UTF_8))) {
            boolean inBlock = false;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("BEGIN legacy")) {
                    inBlock = true;
                    continue;
                }
                if (line.contains("END legacy")) {
                    break;
                }
                if (inBlock && !line.startsWith("#") && line.contains(",")) {
                    lines.add(line.trim());
                }
            }
        }
        assertFalse(lines.isEmpty());
        for (String line : lines) {
            String[] parts = line.split(",", 2);
            String from = parts[0];
            String to = parts[1];
            assertEquals(line, to, library.getBaseBlueprintId(from));
            assertEquals("rhs must be a class, not another alias: " + line, to, library.getBaseBlueprintId(to));
            assertNotNull(from + " -> " + to, library.getSwccgoCardBlueprint(to));
            assertNotNull(from, library.getSwccgoCardBlueprint(from));
        }
    }

    @Test
    public void LegacyFormatIncludesVirtualBlockSets() {
        SwccgFormat legacy = formats.getFormat("legacy");
        assertTrue(legacy.getValidSets().contains(601));
        assertTrue(legacy.getValidSets().contains(1200));
        assertTrue(legacy.getValidSets().contains(1240));
        assertTrue(legacy.getValidSets().contains(1261));
        assertTrue(legacy.getValidSets().contains(1281));
        assertTrue(legacy.getValidSets().contains(1292));
    }

    @Test
    public void AnythingGoesValidatesALegacyBlockCard() throws DeckInvalidException {
        SwccgFormat anythingGoes = formats.getFormat("anything_goes");
        for (int set : new int[] {1200, 1210, 1220, 1230, 1240, 1250, 1261, 1262, 1263, 1271, 1272, 1273, 1274, 1281, 1282, 1283, 1291, 1292}) {
            assertTrue(set + " missing from Anything Goes", anythingGoes.getValidSets().contains(set));
        }
        assertFalse(formats.getFormat("open").getValidSets().contains(1240));
        anythingGoes.validateCard("1281_5", false);
    }

    @Test
    public void TwelveXxSetsMapToThePrintedBlockIcon() {
        assertEquals(Icon.LEGACY_BLOCK_D, ExpansionSet.VIRTUAL_SHIELDS.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_1, ExpansionSet.VIRTUAL_BLOCK_1.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_2, ExpansionSet.VIRTUAL_BLOCK_2.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_3, ExpansionSet.VIRTUAL_BLOCK_3.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_4, ExpansionSet.VIRTUAL_BLOCK_4.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_5, ExpansionSet.VIRTUAL_BLOCK_5.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_6, ExpansionSet.VIRTUAL_BLOCK_6_1.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_6, ExpansionSet.VIRTUAL_BLOCK_6_2.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_6, ExpansionSet.VIRTUAL_BLOCK_6_3.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_7, ExpansionSet.VIRTUAL_BLOCK_7_1.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_7, ExpansionSet.VIRTUAL_BLOCK_7_2.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_7, ExpansionSet.VIRTUAL_BLOCK_7_3.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_7, ExpansionSet.VIRTUAL_BLOCK_7_4.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_8, ExpansionSet.VIRTUAL_BLOCK_8_1.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_8, ExpansionSet.VIRTUAL_BLOCK_8_2.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_8, ExpansionSet.VIRTUAL_BLOCK_8_3.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_9, ExpansionSet.VIRTUAL_BLOCK_9_1.getLegacyBlockIcon());
        assertEquals(Icon.LEGACY_BLOCK_9, ExpansionSet.VIRTUAL_BLOCK_9_2.getLegacyBlockIcon());
        for (ExpansionSet set : ExpansionSet.values()) {
            if (set.getSetNumber() >= 1200 && set.getSetNumber() <= 1292) {
                assertTrue(set.name(), set.isLegacyBlockSet());
                assertNotNull(set.name(), set.getLegacyBlockIcon());
            }
            else {
                assertFalse(set.name(), set.isLegacyBlockSet());
                assertEquals(set.name(), null, set.getLegacyBlockIcon());
            }
        }
    }

    @Test
    public void TwelveXxBlueprintIsLegacyAndCarriesBlockIconWithoutPerCardCalls() {
        SwccgCardBlueprint block1 = new TestBlockInterrupt(ExpansionSet.VIRTUAL_BLOCK_1);
        assertTrue(block1.isLegacy());
        assertEquals(1, block1.getIconCount(Icon.LEGACY_BLOCK_1));
        assertEquals(0, block1.getIconCount(Icon.LEGACY_BLOCK_D));

        SwccgCardBlueprint shields = new TestBlockInterrupt(ExpansionSet.VIRTUAL_SHIELDS);
        assertTrue(shields.isLegacy());
        assertEquals(1, shields.getIconCount(Icon.LEGACY_BLOCK_D));

        SwccgCardBlueprint block9 = new TestBlockInterrupt(ExpansionSet.VIRTUAL_BLOCK_9_2);
        assertTrue(block9.isLegacy());
        assertEquals(1, block9.getIconCount(Icon.LEGACY_BLOCK_9));
    }

    @Test
    public void NonTwelveXxBlueprintIsNotAutoMarkedLegacy() {
        SwccgCardBlueprint premiere = new TestBlockInterrupt(ExpansionSet.PREMIERE);
        assertFalse(premiere.isLegacy());
        assertEquals(0, premiere.getIconCount(Icon.LEGACY_BLOCK_1));

        SwccgCardBlueprint pcLegacySet = new TestBlockInterrupt(ExpansionSet.LEGACY);
        assertFalse(pcLegacySet.isLegacy());
        assertEquals(0, pcLegacySet.getIconCount(Icon.LEGACY_BLOCK_1));
    }

    /**
     * Tiny 12xx (or other-set) interrupt used only to prove the shared-base derivation.
     */
    private static final class TestBlockInterrupt extends AbstractInterrupt {
        TestBlockInterrupt(ExpansionSet expansionSet) {
            super(Side.DARK, 4f, "Test Block Interrupt", Uniqueness.UNIQUE, expansionSet, Rarity.V);
        }
    }
}
