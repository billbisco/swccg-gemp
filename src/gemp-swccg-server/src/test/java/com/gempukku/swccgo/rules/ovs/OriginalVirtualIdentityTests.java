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
        SwccgFormat premiereOvs4 = formats.getFormat("premiere_ovs4");
        assertNotNull(premiereOvs4);
        assertTrue(premiereOvs4.getValidSets().contains(1101));
        assertTrue(premiereOvs4.getValidSets().contains(1102));
        assertTrue(premiereOvs4.getValidSets().contains(1103));
        assertTrue(premiereOvs4.getValidSets().contains(1104));
        assertFalse(premiereOvs4.getValidSets().contains(1210));
        SwccgFormat premiereOvs5 = formats.getFormat("premiere_ovs5");
        assertNotNull(premiereOvs5);
        assertTrue(premiereOvs5.getValidSets().contains(1101));
        assertTrue(premiereOvs5.getValidSets().contains(1104));
        assertTrue(premiereOvs5.getValidSets().contains(1105));
        assertFalse(premiereOvs5.getValidSets().contains(1210));
        SwccgFormat premiereOvs6 = formats.getFormat("premiere_ovs6");
        assertNotNull(premiereOvs6);
        assertTrue(premiereOvs6.getValidSets().contains(1101));
        assertTrue(premiereOvs6.getValidSets().contains(1105));
        assertTrue(premiereOvs6.getValidSets().contains(1106));
        assertFalse(premiereOvs6.getValidSets().contains(1210));
        assertFalse(formats.getFormat("open").getValidSets().contains(1101));
        assertFalse(formats.getFormat("open").getValidSets().contains(1102));
        assertFalse(formats.getFormat("open").getValidSets().contains(1103));
        assertFalse(formats.getFormat("open").getValidSets().contains(1104));
        assertFalse(formats.getFormat("open").getValidSets().contains(1105));
        assertFalse(formats.getFormat("open").getValidSets().contains(1106));
        assertFalse(formats.getFormat("open").getValidSets().contains(1107));
        assertFalse(formats.getFormat("open").getValidSets().contains(1108));
        assertFalse(formats.getFormat("open").getValidSets().contains(1109));
        assertFalse(formats.getFormat("open").getValidSets().contains(1110));
        SwccgFormat premiereOvs7 = formats.getFormat("premiere_ovs7");
        assertNotNull(premiereOvs7);
        assertTrue(premiereOvs7.getValidSets().contains(1101));
        assertTrue(premiereOvs7.getValidSets().contains(1106));
        assertTrue(premiereOvs7.getValidSets().contains(1107));
        assertFalse(premiereOvs7.getValidSets().contains(1210));
        SwccgFormat premiereOvs8 = formats.getFormat("premiere_ovs8");
        assertNotNull(premiereOvs8);
        assertTrue(premiereOvs8.getValidSets().contains(1101));
        assertTrue(premiereOvs8.getValidSets().contains(1107));
        assertTrue(premiereOvs8.getValidSets().contains(1108));
        assertFalse(premiereOvs8.getValidSets().contains(1109));
        assertFalse(premiereOvs8.getValidSets().contains(1210));
        SwccgFormat premiereOvs9 = formats.getFormat("premiere_ovs9");
        assertNotNull(premiereOvs9);
        assertTrue(premiereOvs9.getValidSets().contains(1101));
        assertTrue(premiereOvs9.getValidSets().contains(1108));
        assertTrue(premiereOvs9.getValidSets().contains(1109));
        assertFalse(premiereOvs9.getValidSets().contains(1110));
        assertFalse(premiereOvs9.getValidSets().contains(1210));
        SwccgFormat premiereOvs10 = formats.getFormat("premiere_ovs10");
        assertNotNull(premiereOvs10);
        assertTrue(premiereOvs10.getValidSets().contains(1101));
        assertTrue(premiereOvs10.getValidSets().contains(1109));
        assertTrue(premiereOvs10.getValidSets().contains(1110));
        assertFalse(premiereOvs10.getValidSets().contains(1111));
        assertFalse(premiereOvs10.getValidSets().contains(1210));
        SwccgFormat premiereOvs11 = formats.getFormat("premiere_ovs11");
        assertNotNull(premiereOvs11);
        assertTrue(premiereOvs11.getValidSets().contains(1101));
        assertTrue(premiereOvs11.getValidSets().contains(1110));
        assertTrue(premiereOvs11.getValidSets().contains(1111));
        assertFalse(premiereOvs11.getValidSets().contains(1112));
        assertFalse(premiereOvs11.getValidSets().contains(1210));
        SwccgFormat premiereOvs12 = formats.getFormat("premiere_ovs12");
        assertNotNull(premiereOvs12);
        assertTrue(premiereOvs12.getValidSets().contains(1101));
        assertTrue(premiereOvs12.getValidSets().contains(1111));
        assertTrue(premiereOvs12.getValidSets().contains(1112));
        assertFalse(premiereOvs12.getValidSets().contains(1113));
        assertFalse(premiereOvs12.getValidSets().contains(1210));
        SwccgFormat premiereOvs13 = formats.getFormat("premiere_ovs13");
        assertNotNull(premiereOvs13);
        assertTrue(premiereOvs13.getValidSets().contains(1101));
        assertTrue(premiereOvs13.getValidSets().contains(1112));
        assertTrue(premiereOvs13.getValidSets().contains(1113));
        assertFalse(premiereOvs13.getValidSets().contains(1114));
        assertFalse(premiereOvs13.getValidSets().contains(1210));
        assertFalse(formats.getFormat("open").getValidSets().contains(1111));
        assertFalse(formats.getFormat("open").getValidSets().contains(1112));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1101));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1102));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1103));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1104));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1105));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1106));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1107));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1108));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1109));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1110));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1111));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1112));
        assertFalse(formats.getFormat("open").getValidSets().contains(1113));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1113));
        assertFalse(formats.getFormat("open").getValidSets().contains(1114));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1114));
        SwccgFormat premiereOvs14 = formats.getFormat("premiere_ovs14");
        assertNotNull(premiereOvs14);
        assertTrue(premiereOvs14.getValidSets().contains(1113));
        assertTrue(premiereOvs14.getValidSets().contains(1114));
        assertFalse(premiereOvs14.getValidSets().contains(1115));
    }

    @Test
    public void Vs14IdsLoadAsOriginalVirtualPrintingsNotLegacyAliases() {
        for (int i = 1; i <= 30; i++) {
            String id = "1114_" + i;
            assertNotNull(id, library.getSwccgoCardBlueprint(id));
            assertEquals(id, library.getBaseBlueprintId(id));
            assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_14, library.getSwccgoCardBlueprint(id).getExpansionSet());
            assertFalse(library.getSwccgoCardBlueprint(id).isLegacy());
        }
    }

    @Test
    public void Vs13IdsLoadAsOriginalVirtualPrintingsNotLegacyAliases() {
        for (int i = 1; i <= 25; i++) {
            String id = "1113_" + i;
            assertNotNull(id, library.getSwccgoCardBlueprint(id));
            assertEquals(id, library.getBaseBlueprintId(id));
            assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_13, library.getSwccgoCardBlueprint(id).getExpansionSet());
            assertFalse(library.getSwccgoCardBlueprint(id).isLegacy());
        }
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

    @Test
    public void Vs4IdsLoadAsOriginalVirtualPrintingsNotLegacyAliases() {
        assertNotNull(library.getSwccgoCardBlueprint("1104_1"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_3"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_8"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_9"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_11"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_12"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_15"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_16"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_19"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_26"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_29"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_30"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_31"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_32"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_34"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_40"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_41"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_45"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_46"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_48"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_49"));
        assertNotNull(library.getSwccgoCardBlueprint("1104_50"));
        assertEquals("1104_1", library.getBaseBlueprintId("1104_1"));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, library.getSwccgoCardBlueprint("1104_1").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, library.getSwccgoCardBlueprint("1104_3").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, library.getSwccgoCardBlueprint("1104_9").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, library.getSwccgoCardBlueprint("1104_11").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, library.getSwccgoCardBlueprint("1104_12").getExpansionSet());
        assertFalse(library.getSwccgoCardBlueprint("1104_1").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1104_3").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1104_9").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1104_11").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1104_12").isLegacy());
    }

    @Test
    public void Vs5IdsLoadAsOriginalVirtualPrintingsNotLegacyAliases() {
        assertNotNull(library.getSwccgoCardBlueprint("1105_1"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_2"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_3"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_6"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_8"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_10"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_11"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_15"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_17"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_18"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_19"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_20"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_21"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_25"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_26"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_28"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_33"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_34"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_35"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_38"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_41"));
        assertNotNull(library.getSwccgoCardBlueprint("1105_47"));
        assertEquals("1105_1", library.getBaseBlueprintId("1105_1"));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, library.getSwccgoCardBlueprint("1105_1").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, library.getSwccgoCardBlueprint("1105_2").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, library.getSwccgoCardBlueprint("1105_10").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, library.getSwccgoCardBlueprint("1105_17").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, library.getSwccgoCardBlueprint("1105_19").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, library.getSwccgoCardBlueprint("1105_26").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, library.getSwccgoCardBlueprint("1105_28").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, library.getSwccgoCardBlueprint("1105_33").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, library.getSwccgoCardBlueprint("1105_34").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, library.getSwccgoCardBlueprint("1105_38").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, library.getSwccgoCardBlueprint("1105_47").getExpansionSet());
        assertFalse(library.getSwccgoCardBlueprint("1105_1").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1105_2").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1105_10").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1105_17").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1105_26").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1105_28").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1105_33").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1105_38").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1105_47").isLegacy());
        assertNotNull(library.getSwccgoCardBlueprint("1106_1"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_6"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_9"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_11"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_14"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_16"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_20"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_25"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_26"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_27"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_31"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_39"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_41"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_42"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_43"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_44"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_48"));
        assertNotNull(library.getSwccgoCardBlueprint("1106_49"));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, library.getSwccgoCardBlueprint("1106_1").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, library.getSwccgoCardBlueprint("1106_9").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, library.getSwccgoCardBlueprint("1106_14").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, library.getSwccgoCardBlueprint("1106_20").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, library.getSwccgoCardBlueprint("1106_26").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, library.getSwccgoCardBlueprint("1106_31").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, library.getSwccgoCardBlueprint("1106_41").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, library.getSwccgoCardBlueprint("1106_48").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, library.getSwccgoCardBlueprint("1106_49").getExpansionSet());
        assertFalse(library.getSwccgoCardBlueprint("1106_1").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1106_9").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1106_14").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1106_20").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1106_26").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1106_31").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1106_41").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1106_42").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1106_48").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1106_49").isLegacy());
    }

    @Test
    public void Vs7IdsLoadAsOriginalVirtualPrintingsNotLegacyAliases() {
        assertNotNull(library.getSwccgoCardBlueprint("1107_1"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_2"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_4"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_5"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_6"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_7"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_9"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_11"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_15"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_16"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_17"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_18"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_19"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_20"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_21"));
        assertNotNull(library.getSwccgoCardBlueprint("1107_22"));
        assertEquals("1107_1", library.getBaseBlueprintId("1107_1"));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, library.getSwccgoCardBlueprint("1107_1").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, library.getSwccgoCardBlueprint("1107_2").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, library.getSwccgoCardBlueprint("1107_5").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, library.getSwccgoCardBlueprint("1107_6").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, library.getSwccgoCardBlueprint("1107_7").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, library.getSwccgoCardBlueprint("1107_11").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, library.getSwccgoCardBlueprint("1107_15").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, library.getSwccgoCardBlueprint("1107_16").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, library.getSwccgoCardBlueprint("1107_17").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, library.getSwccgoCardBlueprint("1107_18").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, library.getSwccgoCardBlueprint("1107_22").getExpansionSet());
        assertFalse(library.getSwccgoCardBlueprint("1107_1").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1107_2").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1107_5").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1107_6").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1107_7").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1107_11").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1107_15").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1107_16").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1107_17").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1107_18").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1107_22").isLegacy());
    }

    @Test
    public void Vs8IdsLoadAsOriginalVirtualPrintingsNotLegacyAliases() {
        assertNotNull(library.getSwccgoCardBlueprint("1108_1"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_2"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_3"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_4"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_5"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_6"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_7"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_8"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_9"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_10"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_11"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_12"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_13"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_14"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_15"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_16"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_17"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_18"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_19"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_20"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_21"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_22"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_24"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_25"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_26"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_27"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_28"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_29"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_30"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_31"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_32"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_33"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_34"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_35"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_36"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_37"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_38"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_39"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_40"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_41"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_42"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_43"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_44"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_45"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_46"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_47"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_48"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_49"));
        assertNotNull(library.getSwccgoCardBlueprint("1108_50"));
        assertEquals("1108_1", library.getBaseBlueprintId("1108_1"));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_1").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_4").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_5").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_6").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_7").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_8").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_10").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_14").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_20").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_21").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_28").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_30").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_31").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_32").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_35").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_36").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_37").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_39").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_45").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_47").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_48").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_49").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, library.getSwccgoCardBlueprint("1108_50").getExpansionSet());
        assertFalse(library.getSwccgoCardBlueprint("1108_1").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_4").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_5").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_6").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_7").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_8").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_10").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_14").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_20").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_28").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_30").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_31").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_32").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_35").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_36").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_37").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_39").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_45").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_47").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_48").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_49").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1108_50").isLegacy());
    }

    @Test
    public void Vs9IdsLoadAsOriginalVirtualPrintingsNotLegacyAliases() {
        assertNotNull(library.getSwccgoCardBlueprint("1109_1"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_2"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_3"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_4"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_5"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_6"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_7"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_8"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_9"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_10"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_11"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_12"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_13"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_14"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_15"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_16"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_17"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_18"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_19"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_20"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_21"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_22"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_23"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_25"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_26"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_27"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_28"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_29"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_30"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_31"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_24"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_32"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_33"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_34"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_35"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_36"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_37"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_38"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_39"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_40"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_41"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_42"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_43"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_44"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_45"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_46"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_47"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_48"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_49"));
        assertNotNull(library.getSwccgoCardBlueprint("1109_50"));
        assertEquals("1109_1", library.getBaseBlueprintId("1109_1"));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_1").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_2").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_3").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_4").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_6").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_7").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_12").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_13").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_14").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_15").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_16").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_17").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_20").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_22").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_23").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_27").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_30").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_24").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_32").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_34").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_45").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_48").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, library.getSwccgoCardBlueprint("1109_50").getExpansionSet());
        assertFalse(library.getSwccgoCardBlueprint("1109_1").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_2").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_3").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_6").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_7").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_12").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_13").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_14").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_15").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_16").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_17").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_22").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_23").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_27").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_30").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_24").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_32").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_34").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_45").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_48").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1109_50").isLegacy());
    }

    @Test
    public void Vs10IdsLoadAsOriginalVirtualPrintingsNotLegacyAliases() {
        assertNotNull(library.getSwccgoCardBlueprint("1110_1"));
        assertNotNull(library.getSwccgoCardBlueprint("1110_2"));
        assertNotNull(library.getSwccgoCardBlueprint("1110_3"));
        assertNotNull(library.getSwccgoCardBlueprint("1110_4"));
        assertNotNull(library.getSwccgoCardBlueprint("1110_5"));
        assertNotNull(library.getSwccgoCardBlueprint("1110_6"));
        assertNotNull(library.getSwccgoCardBlueprint("1110_7"));
        assertNotNull(library.getSwccgoCardBlueprint("1110_8"));
        assertNotNull(library.getSwccgoCardBlueprint("1110_9"));
        assertNotNull(library.getSwccgoCardBlueprint("1110_10"));
        assertNotNull(library.getSwccgoCardBlueprint("1110_11"));
        assertNotNull(library.getSwccgoCardBlueprint("1110_12"));
        assertNotNull(library.getSwccgoCardBlueprint("1110_13"));
        assertNotNull(library.getSwccgoCardBlueprint("1110_14"));
        assertEquals("1110_1", library.getBaseBlueprintId("1110_1"));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_10, library.getSwccgoCardBlueprint("1110_1").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_10, library.getSwccgoCardBlueprint("1110_3").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_10, library.getSwccgoCardBlueprint("1110_6").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_10, library.getSwccgoCardBlueprint("1110_10").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_10, library.getSwccgoCardBlueprint("1110_13").getExpansionSet());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_10, library.getSwccgoCardBlueprint("1110_14").getExpansionSet());
        assertFalse(library.getSwccgoCardBlueprint("1110_1").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1110_3").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1110_6").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1110_10").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1110_13").isLegacy());
        assertFalse(library.getSwccgoCardBlueprint("1110_14").isLegacy());
    }

    @Test
    public void Vs11IdsLoadAsOriginalVirtualPrintingsNotLegacyAliases() {
        for (int i = 1; i <= 30; i++) {
            String id = "1111_" + i;
            assertNotNull(id, library.getSwccgoCardBlueprint(id));
            assertEquals(id, library.getBaseBlueprintId(id));
            assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_11, library.getSwccgoCardBlueprint(id).getExpansionSet());
            assertFalse(library.getSwccgoCardBlueprint(id).isLegacy());
        }
    }

    @Test
    public void Vs12IdsLoadAsOriginalVirtualPrintingsNotLegacyAliases() {
        for (int i = 1; i <= 43; i++) {
            String id = "1112_" + i;
            assertNotNull(id, library.getSwccgoCardBlueprint(id));
            assertEquals(id, library.getBaseBlueprintId(id));
            assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, library.getSwccgoCardBlueprint(id).getExpansionSet());
            assertFalse(library.getSwccgoCardBlueprint(id).isLegacy());
        }
    }
}
