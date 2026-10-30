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
    public void AnythingGoesAndPremiereOvs1IncludeOriginalVirtualSet1() {
        SwccgFormat anythingGoes = formats.getFormat("anything_goes");
        for (int set = 1100; set <= 1117; set++) {
            assertTrue(set + " missing from Anything Goes", anythingGoes.getValidSets().contains(set));
        }
        SwccgFormat premiereOvs1 = formats.getFormat("premiere_ovs1");
        assertNotNull(premiereOvs1);
        assertTrue(premiereOvs1.getValidSets().contains(1101));
        assertFalse(premiereOvs1.getValidSets().contains(1210));
        assertFalse(formats.getFormat("open").getValidSets().contains(1101));
        assertFalse(formats.getFormat("legacy").getValidSets().contains(1101));
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
}
