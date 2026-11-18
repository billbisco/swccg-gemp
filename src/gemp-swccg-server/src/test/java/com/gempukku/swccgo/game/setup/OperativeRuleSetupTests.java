package com.gempukku.swccgo.game.setup;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.state.GameState;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class OperativeRuleSetupTests {
    @Test
    public void OperativeRuleDefaultsPreserveExistingSetups() {
        var format = VirtualTableScenario._formatLibrary.getFormat("open");
        assertTrue(new GameState().isOperativeRuleEnabled());
        for (var scope : GameOption.Scope.values()) {
            var setup = GameSetup.of(format, Map.of(GameOptions.OPERATIVE_RULE, "on"), scope);
            assertTrue(setup.isDefault());
            assertEquals("true", setup.get(GameOptions.OPERATIVE_RULE, format));
            assertEquals("", setup.chipsText());
        }
        assertEquals("true", GameSetup.fromJson(null, "open").get(GameOptions.OPERATIVE_RULE, format));
    }

    @Test
    public void OperativeRuleChoiceSurvivesEverySetupScopeAndJsonRoundTrip() {
        var format = VirtualTableScenario._formatLibrary.getFormat("open");
        for (var scope : GameOption.Scope.values()) {
            var setup = GameSetup.of(format, Map.of(GameOptions.OPERATIVE_RULE, "off"), scope);
            var restored = GameSetup.fromJson(setup.toJson(), "open");
            assertEquals("false", restored.get(GameOptions.OPERATIVE_RULE, format));
            assertEquals("Rules: no operative rule", restored.chipsText());
            assertEquals("Off", restored.describe(format).get("Rules: operative rule"));
        }
    }

    @Test
    public void OperativeRuleRejectsValuesOtherThanOnOrOff() {
        var format = VirtualTableScenario._formatLibrary.getFormat("open");
        for (String value : new String[]{"sometimes", "2", "-1"})
            assertThrows(IllegalArgumentException.class,
                    () -> GameSetup.of(format, Map.of(GameOptions.OPERATIVE_RULE, value), GameOption.Scope.CASUAL));
    }
}
