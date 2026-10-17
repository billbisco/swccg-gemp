package com.gempukku.swccgo.rules.legacy;

import com.gempukku.swccgo.common.LegacyIds;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

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
    public void LeaveThemToMeHostPrintingLoadsReflectionsIIIBlueprint() throws DeckInvalidException {
        SwccgCardBlueprint host = library.getSwccgoCardBlueprint("1261_24");
        SwccgCardBlueprint refIii = library.getSwccgoCardBlueprint("1261_21");
        assertNotNull(host);
        assertNotNull(refIii);
        assertEquals(refIii.getClass(), host.getClass());
        assertEquals("1261_21", library.getBaseBlueprintId("1261_24"));
        assertEquals(refIii.getTitle(), host.getTitle());
        assertEquals(Uniqueness.UNIQUE, refIii.getUniqueness());
        assertEquals(Uniqueness.UNIQUE, host.getUniqueness());

        SwccgFormat legacy = formats.getFormat("legacy");
        legacy.validateCard("1261_21", false);
        legacy.validateCard("1261_24", false);
        Set<String> alternates = library.getAllAlternates("1261_21");
        assertNotNull(alternates);
        assertTrue(alternates.contains("1261_24"));
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
}
