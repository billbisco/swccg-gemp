package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.GameUtils;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Shared checks for the Legacy version classes of Virtual Block 8, Virtual Block 9 and Virtual Shields
 * (docs/legacy-versions.md): exact text and version info of a version, exact text of the current card, and
 * "same card" (title, uniqueness, persona) between a version and its current printing.
 */
public final class Vb89VersionAsserts {
    private static SwccgCardBlueprintLibrary library;

    private Vb89VersionAsserts() {
    }

    public static synchronized SwccgCardBlueprint blueprint(String id) {
        if (library == null)
            library = new SwccgCardBlueprintLibrary();
        SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(id);
        assertNotNull(id, blueprint);
        return blueprint;
    }

    /** The current (Legacy) card plays exactly this text and is no version. */
    public static void assertCurrentCard(String id, String title, String gameText) {
        SwccgCardBlueprint card = blueprint(id);
        assertEquals(title, card.getTitle());
        assertEquals(gameText, card.getGameText());
        assertNull(id + " is the current card", card.getLegacyVersion());
    }

    /** A version: exact text, the current printing it belongs to, label (shown in the full name) and dates. */
    public static void assertVersion(String id, String currentId, String title, String gameText, String label,
                                     LocalDate printedOn, LocalDate supersededOn) {
        SwccgCardBlueprint card = blueprint(id);
        assertEquals(title, card.getTitle());
        assertTrue(card.isLegacy());
        assertEquals(gameText, card.getGameText());
        LegacyVersion version = card.getLegacyVersion();
        assertNotNull(id + " has version info", version);
        assertEquals(currentId, version.currentPrintingId());
        assertEquals(label, version.label());
        assertEquals(printedOn, version.printedOn());
        assertEquals(supersededOn, version.supersededOn());
        String currentName = GameUtils.getFullName(blueprint(currentId));
        assertEquals(label.isEmpty() ? currentName : currentName + " [" + label + "]", GameUtils.getFullName(card));
    }

    /** A version is the same card as its current printing (uniqueness and persona rules see one card). */
    public static void assertSameCard(String versionId, String currentId) {
        SwccgCardBlueprint version = blueprint(versionId);
        SwccgCardBlueprint current = blueprint(currentId);
        assertEquals(current.getTitle(), version.getTitle());
        assertEquals(current.getTitles(), version.getTitles());
        assertEquals(current.getUniqueness(), version.getUniqueness());
        assertEquals(current.getPersonas(), version.getPersonas());
        assertEquals(current.getSide(), version.getSide());
        assertEquals(current.getCardCategory(), version.getCardCategory());
        assertTrue(LegacyVersionAssertions.differsOrIsRepeatedByTheCurrentCard(current, version));
    }

    /**
     * Gameplay check for a character: each may deploy alone, but not while the other is on table (same unique
     * card).
     */
    public static void assertCharacterMayNotDeployWhileOtherIsOnTable(String versionId, String currentId) {
        boolean dark = blueprint(currentId).getSide() == Side.DARK;
        for (int otherOut = 0; otherOut < 3; otherOut++) {
            boolean currentOnTable = otherOut == 1;
            HashMap<String, String> cards = new HashMap<>();
            cards.put("version", versionId);
            cards.put("current", currentId);
            var scn = new VirtualTableScenario(
                    dark ? new HashMap<>() : cards,
                    dark ? cards : new HashMap<>(),
                    30, 30,
                    StartingSetup.DefaultLSGroundLocation,
                    StartingSetup.DefaultDSGroundLocation,
                    StartingSetup.NoLSStartingInterrupts,
                    StartingSetup.NoDSStartingInterrupts,
                    StartingSetup.NoLSShields,
                    StartingSetup.NoDSShields,
                    VirtualTableScenario.Open);
            var onTable = dark ? scn.GetDSCard(currentOnTable ? "current" : "version") : scn.GetLSCard(currentOnTable ? "current" : "version");
            var inHand = dark ? scn.GetDSCard(currentOnTable ? "version" : "current") : scn.GetLSCard(currentOnTable ? "version" : "current");
            scn.StartGame();
            if (dark) {
                if (otherOut > 0)
                    scn.MoveCardsToLocation(scn.GetDSStartingLocation(), onTable);
                scn.MoveCardsToDSHand(inHand);
                scn.SkipToDSTurn(Phase.DEPLOY);
                scn.EnsureDSForcePile(10);
                assertEquals("other copy on table: " + otherOut, otherOut == 0, scn.DSDeployAvailable(inHand));
            } else {
                if (otherOut > 0)
                    scn.MoveCardsToLocation(scn.GetLSStartingLocation(), onTable);
                scn.MoveCardsToLSHand(inHand);
                scn.SkipToLSTurn(Phase.DEPLOY);
                scn.EnsureLSForcePile(10);
                assertEquals("other copy on table: " + otherOut, otherOut == 0, scn.LSDeployAvailable(inHand));
            }
        }
    }
}
