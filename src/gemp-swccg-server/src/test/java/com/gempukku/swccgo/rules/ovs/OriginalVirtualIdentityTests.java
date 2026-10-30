package com.gempukku.swccgo.rules.ovs;

import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OriginalVirtualIdentityTests {
    private static SwccgCardBlueprintLibrary library;
    private static SwccgoFormatLibrary formats;

    @BeforeClass
    public static void loadLibrary() {
        library = new SwccgCardBlueprintLibrary();
        formats = new SwccgoFormatLibrary(library);
    }

    @Test
    public void OriginalVirtualSetsAreNotLegacyBlockSets() {
        assertEquals(1100, ExpansionSet.VIRTUAL_ORIGINAL_SHIELDS.getSetNumber());
        assertEquals(1101, ExpansionSet.VIRTUAL_ORIGINAL_SET_1.getSetNumber());
        assertEquals(1117, ExpansionSet.VIRTUAL_ORIGINAL_SET_17.getSetNumber());
        assertEquals("Virtual Set 1: Legacy", ExpansionSet.VIRTUAL_ORIGINAL_SET_1.getHumanReadable());
        for (int n = 1100; n <= 1117; n++) {
            ExpansionSet set = ExpansionSet.getSetFromNumber(n);
            assertNotNull("missing ExpansionSet " + n, set);
            assertFalse(set.name(), set.isLegacyBlockSet());
            assertEquals(set.name(), null, set.getLegacyBlockIcon());
        }
    }

    @Test
    public void AnythingGoesAndPremiereOvsFormatsIncludeOriginalVirtualSets() {
        SwccgFormat anythingGoes = formats.getFormat("anything_goes");
        for (int set = 1100; set <= 1117; set++) {
            assertTrue(set + " missing from Anything Goes", anythingGoes.getValidSets().contains(set));
        }
        SwccgFormat premiereOvs1 = formats.getFormat("premiere_ovs1");
        assertNotNull(premiereOvs1);
        assertTrue(premiereOvs1.getValidSets().contains(1101));
        assertFalse(premiereOvs1.getValidSets().contains(1102));
        assertFalse(premiereOvs1.getValidSets().contains(1210));
        SwccgFormat premiereOvs2 = formats.getFormat("premiere_ovs2");
        assertNotNull(premiereOvs2);
        assertTrue(premiereOvs2.getValidSets().contains(1101));
        assertTrue(premiereOvs2.getValidSets().contains(1102));
        assertFalse(premiereOvs2.getValidSets().contains(1210));

        SwccgFormat premiereOvs3 = formats.getFormat("premiere_ovs3");
        assertNotNull(premiereOvs3);
        assertTrue(premiereOvs3.getValidSets().contains(1101));
        assertTrue(premiereOvs3.getValidSets().contains(1102));
        assertTrue(premiereOvs3.getValidSets().contains(1103));
        assertFalse(premiereOvs3.getValidSets().contains(1210));
        assertFalse(formats.getFormat("open").getValidSets().contains(1101));
        assertFalse(formats.getFormat("open").getValidSets().contains(1102));
        assertFalse(formats.getFormat("open").getValidSets().contains(1103));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1101));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1102));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1103));
    }

    @Test
    public void Vs1IdsLoadAsOriginalVirtualPrintingsNotLegacyAliases() {
        assertNotNull(library.getSwccgoCardBlueprint("1101_1"));
        assertNotNull(library.getSwccgoCardBlueprint("1101_4"));
        assertNotNull(library.getSwccgoCardBlueprint("1101_10"));
        assertEquals("1101_1", library.getBaseBlueprintId("1101_1"));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_1, library.getSwccgoCardBlueprint("1101_1").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_1, library.getSwccgoCardBlueprint("1101_10").getExpansionSet());
        assertFalse(library.getSwccgoCardBlueprint("1101_1").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1101_10").isLegacy());
    }

    @Test
    public void Vs2IdsLoadAsOriginalVirtualPrintingsNotLegacyAliases() {
        assertNotNull(library.getSwccgoCardBlueprint("1102_1"));
        assertNotNull(library.getSwccgoCardBlueprint("1102_6"));
        assertNotNull(library.getSwccgoCardBlueprint("1102_12"));
        assertNotNull(library.getSwccgoCardBlueprint("1102_16"));
        assertNotNull(library.getSwccgoCardBlueprint("1102_21"));
        assertNotNull(library.getSwccgoCardBlueprint("1102_26"));
        assertNotNull(library.getSwccgoCardBlueprint("1102_30"));
        assertNotNull(library.getSwccgoCardBlueprint("1102_31"));
        assertNotNull(library.getSwccgoCardBlueprint("1102_32"));
        assertNotNull(library.getSwccgoCardBlueprint("1102_36"));
        assertNotNull(library.getSwccgoCardBlueprint("1102_45"));
        assertNotNull(library.getSwccgoCardBlueprint("1102_46"));
        assertNotNull(library.getSwccgoCardBlueprint("1102_50"));
        assertEquals("1102_1", library.getBaseBlueprintId("1102_1"));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, library.getSwccgoCardBlueprint("1102_1").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, library.getSwccgoCardBlueprint("1102_6").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, library.getSwccgoCardBlueprint("1102_12").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, library.getSwccgoCardBlueprint("1102_16").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, library.getSwccgoCardBlueprint("1102_26").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, library.getSwccgoCardBlueprint("1102_32").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, library.getSwccgoCardBlueprint("1102_36").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, library.getSwccgoCardBlueprint("1102_50").getExpansionSet());
        assertFalse(library.getSwccgoCardBlueprint("1102_1").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1102_6").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1102_12").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1102_16").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1102_26").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1102_32").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1102_36").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1102_50").isLegacy());
    }

    @Test
    public void Vs3IdsLoadAsOriginalVirtualPrintingsNotLegacyAliases() {
        assertNotNull(library.getSwccgoCardBlueprint("1103_1"));
        assertNotNull(library.getSwccgoCardBlueprint("1103_2"));
        assertNotNull(library.getSwccgoCardBlueprint("1103_8"));
        assertNotNull(library.getSwccgoCardBlueprint("1103_11"));
        assertNotNull(library.getSwccgoCardBlueprint("1103_15"));
        assertEquals("1103_1", library.getBaseBlueprintId("1103_1"));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, library.getSwccgoCardBlueprint("1103_1").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, library.getSwccgoCardBlueprint("1103_2").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, library.getSwccgoCardBlueprint("1103_8").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, library.getSwccgoCardBlueprint("1103_15").getExpansionSet());
        assertFalse(library.getSwccgoCardBlueprint("1103_1").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1103_2").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1103_8").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1103_15").isLegacy());
    }
}
