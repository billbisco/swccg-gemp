package com.gempukku.swccgo.game.setup;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GameRulePresetsTests {
    private static final com.gempukku.swccgo.game.formats.SwccgoFormatLibrary FORMATS = VirtualTableScenario._formatLibrary;

    @Test
    public void PartialPresetsHaveOnlyTheTwoApprovedDefinitions() {
        var presets = GameRulePresets.all();
        assertEquals(2, presets.size());
        assertEquals("premiere_se", presets.get(0).formatCode());
        assertEquals(Map.of("operativeRule", "false", "projectiveTelepathy", "printed"), presets.get(0).values());
        assertEquals("premiere_ds2", presets.get(1).formatCode());
        assertEquals(Map.of("operativeRule", "true", "projectiveTelepathy", "current"), presets.get(1).values());
        for (var preset : presets) {
            assertTrue(preset.label().contains("(partial rules)"));
            assertFalse(preset.label().contains("Worlds"));
            assertThrows(UnsupportedOperationException.class, () -> preset.values().put("operativeRule", "false"));
        }
        assertThrows(UnsupportedOperationException.class, () -> presets.clear());
    }

    @Test
    public void PartialPresetsUseValidatedExistingChoicesInEveryScopeAndRoundTrip() throws Exception {
        for (var preset : GameRulePresets.all()) {
            var format = FORMATS.getFormat(preset.formatCode());
            for (var scope : GameOption.Scope.values()) {
                var setup = preset.setup(format, scope);
                var restored = GameSetup.fromJson(setup.toJson(), "open");
                assertEquals(setup, restored);
                assertEquals(preset, GameRulePresets.matching(restored, format));
                assertEquals(preset.values().get("operativeRule"), restored.get("operativeRule", format));
                assertTrue(restored.summaryJson(format).contains(GameRulePresets.LIMITATION));
                assertTrue(restored.summaryJson(format).contains(preset.label()));
                var effective = FORMATS.effectiveFormat(restored);
                String expected = preset.id().equals("early-operative") ? "5_149o" : "5_149";
                assertEquals(expected, effective.resolveCardId("5_149o"));
                assertEquals(expected, effective.resolveCardId("5_149"));
            }
            assertThrows(IllegalArgumentException.class, () -> preset.setup(FORMATS.getFormat("open"), GameOption.Scope.CASUAL));
        }
    }

    @Test
    public void PartialPresetNamesFollowActualChoicesAndDoNotRenameCustomPoolsOrDefaults() {
        for (var preset : GameRulePresets.all()) {
            var format = FORMATS.getFormat(preset.formatCode());
            var setup = preset.setup(format, GameOption.Scope.CASUAL);
            assertNull(GameRulePresets.matching(GameSetup.defaults(preset.formatCode()), format));
            assertNull(GameRulePresets.matching(setup.forFormat("open"), format));
            assertNull(GameRulePresets.matching(setup.withPool(CardPool.of(List.of(1), null, null)), format));
            assertNull(GameRulePresets.matching(setup.with("operativeRule", preset.id().equals("early-operative") ? "true" : "false"), format));
            assertNull(GameRulePresets.matching(setup.with("projectiveTelepathy", "format"), format));
            assertFalse(GameSetup.defaults(preset.formatCode()).summaryJson(format).contains("Partial rules bundle"));
        }
    }

    @Test
    public void PartialPresetDoesNotAdmitCardsOutsideItsBasePool() {
        for (var preset : GameRulePresets.all()) {
            var setup = preset.setup(FORMATS.getFormat(preset.formatCode()), GameOption.Scope.CASUAL);
            var deck = new SwccgDeck("Outside approved pool");
            for (int i = 0; i < 60; i++) deck.addCard("216_12");
            assertThrows(DeckInvalidException.class, () -> FORMATS.effectiveFormat(setup).validateDeck(deck));
        }
    }
}
