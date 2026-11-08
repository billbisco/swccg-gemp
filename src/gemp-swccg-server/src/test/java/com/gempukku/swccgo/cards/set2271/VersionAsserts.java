package com.gempukku.swccgo.cards.set2271;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.logic.GameUtils;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Identity checks shared by the Virtual Block 7 pre-errata version tests: the version is the current card in every
 * printed respect except its game text and version info.
 */
public final class VersionAsserts {
    private VersionAsserts() {
    }

    /**
     * Location version: checks both location texts; {@code iconsDiffer} for an icon-only errata (same text, other
     * Force icons), where the icon comparison is skipped and the texts may be equal.
     */
    public static void assertLocationVersion(SwccgCardBlueprint version, SwccgCardBlueprint current, String currentPrintingId,
                                             String label, LocalDate printedOn, LocalDate supersededOn,
                                             String lightText, String darkText, boolean iconsDiffer) {
        assertEquals(lightText, version.getLocationLightSideGameText());
        assertEquals(darkText, version.getLocationDarkSideGameText());
        if (!iconsDiffer)
            assertFalse(lightText.equals(current.getLocationLightSideGameText()) && darkText.equals(current.getLocationDarkSideGameText()));
        assertIdentity(version, current, currentPrintingId, label, printedOn, supersededOn, !iconsDiffer, current.getCardSubtype());
    }

    public static void assertVersion(SwccgCardBlueprint version, SwccgCardBlueprint current, String currentPrintingId,
                                     String label, LocalDate printedOn, LocalDate supersededOn, String gameText) {
        assertEquals(gameText, version.getGameText());
        assertFalse(gameText.equals(current.getGameText()));
        assertIdentity(version, current, currentPrintingId, label, printedOn, supersededOn, true, current.getCardSubtype());
    }

    /** As {@link #assertVersion}, for a version printed with another subtype (e.g. Critical Mission before an errata). */
    public static void assertVersion(SwccgCardBlueprint version, SwccgCardBlueprint current, String currentPrintingId,
                                     String label, LocalDate printedOn, LocalDate supersededOn, String gameText,
                                     CardSubtype versionSubtype) {
        assertEquals(gameText, version.getGameText());
        assertFalse(gameText.equals(current.getGameText()));
        assertIdentity(version, current, currentPrintingId, label, printedOn, supersededOn, true, versionSubtype);
    }

    private static void assertIdentity(SwccgCardBlueprint version, SwccgCardBlueprint current, String currentPrintingId,
                                       String label, LocalDate printedOn, LocalDate supersededOn, boolean sameIcons,
                                       CardSubtype versionSubtype) {
        LegacyVersion info = version.getLegacyVersion();
        assertNotNull(info);
        assertEquals(currentPrintingId, info.currentPrintingId());
        assertEquals(label, info.label());
        assertEquals(printedOn, info.printedOn());
        assertEquals(supersededOn, info.supersededOn());
        assertEquals(label.isEmpty() ? GameUtils.getFullName(current) : GameUtils.getFullName(current) + " [" + label + "]",
                GameUtils.getRulesFullName(version));

        assertEquals(current.getTitle(), version.getTitle());
        assertEquals(current.getTitles(), version.getTitles());
        assertEquals(current.getSide(), version.getSide());
        assertEquals(current.getUniqueness(), version.getUniqueness());
        assertEquals(current.getCardCategory(), version.getCardCategory());
        assertEquals(current.getCardTypes(), version.getCardTypes());
        assertEquals(versionSubtype, version.getCardSubtype());
        assertEquals(current.getPersonas(), version.getPersonas());
        assertEquals(current.getExpansionSet(), version.getExpansionSet());
        assertEquals(current.getRarity(), version.getRarity());
        assertEquals(current.hasVirtualSuffix(), version.hasVirtualSuffix());
        if (sameIcons)
            for (Icon icon : Icon.values())
                assertEquals(icon.name(), current.getIconCount(icon), version.getIconCount(icon));
        for (Keyword keyword : Keyword.values())
            assertEquals(keyword.name(), current.hasKeyword(keyword), version.hasKeyword(keyword));
        assertEquals(current.getLore(), version.getLore());
        assertTrue(Objects.equals(current.getDestiny(), version.getDestiny()));
        if (current.getCardCategory() != com.gempukku.swccgo.common.CardCategory.INTERRUPT)
            assertTrue(Objects.equals(current.getDeployCost(), version.getDeployCost()));
        assertTrue(version.isLegacy());
    }

    /**
     * Resolves pending decisions until done: card selections take the first selectable cards (as many as allowed),
     * multiple choices take the first option, everything else is passed.
     */
    public static void resolveUntil(com.gempukku.swccgo.framework.VirtualTableScenario scn, java.util.function.BooleanSupplier done) {
        for (int i = 0; i < 40 && !done.getAsBoolean(); i++) {
            String decider = scn.GetDecidingPlayer();
            var d = scn.GetAwaitingDecision(decider);
            if (d == null) {
                return;
            }
            var params = d.getDecisionParameters();
            if (params.get("min") != null && params.get("cardId") == null && params.get("max") != null) {
                scn.PlayerDecided(decider, params.get("max")[0]);
            } else if (params.get("min") != null && params.get("cardId") != null && params.get("actionText") == null) {
                String[] ids = params.get("cardId");
                String[] selectable = params.get("selectable");
                int max = Integer.parseInt(params.get("max")[0]);
                java.util.List<String> chosen = new java.util.ArrayList<>();
                for (int j = 0; j < ids.length && chosen.size() < max; j++) {
                    if (selectable == null || "true".equals(selectable[j])) {
                        chosen.add(ids[j]);
                    }
                }
                scn.PlayerDecided(decider, String.join(",", chosen));
            } else if (params.get("results") != null) {
                scn.PlayerDecided(decider, "0");
            } else {
                scn.PlayerPass(decider);
            }
        }
    }

    /**
     * Uniqueness with the current card: with either printing already on table (placed by {@code place}), the other
     * one may not deploy; with nothing on table (place gets null: set up prerequisites only), the version may deploy. {@code dark} selects the owning side.
     */
    public static void assertSameUniqueCard(java.util.function.Supplier<com.gempukku.swccgo.framework.VirtualTableScenario> scenario,
                                            boolean dark, String versionKey, String currentKey,
                                            java.util.function.BiConsumer<com.gempukku.swccgo.framework.VirtualTableScenario, com.gempukku.swccgo.game.PhysicalCardImpl> place) {
        for (int mode = 0; mode < 3; mode++) {
            var scn = scenario.get();
            var version = dark ? scn.GetDSCard(versionKey) : scn.GetLSCard(versionKey);
            var current = dark ? scn.GetDSCard(currentKey) : scn.GetLSCard(currentKey);
            scn.StartGame();
            if (mode == 0)
                place.accept(scn, current);
            else if (mode == 1)
                place.accept(scn, version);
            else
                place.accept(scn, null); // prerequisites only
            var inHand = mode == 1 ? current : version;
            scn.MoveCardsToHand(inHand);
            if (dark) {
                scn.EnsureDSForcePile(12);
                scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.DEPLOY);
                assertEquals("mode " + mode, mode == 2, scn.DSDeployAvailable(inHand));
            } else {
                scn.EnsureLSForcePile(12);
                scn.SkipToLSTurn(com.gempukku.swccgo.common.Phase.DEPLOY);
                assertEquals("mode " + mode, mode == 2, scn.LSDeployAvailable(inHand));
            }
        }
    }
}
