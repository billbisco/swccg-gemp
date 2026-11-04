package com.gempukku.swccgo.game.setup;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/** swccg-guild/work#135: settings saved on the account are checked before they are stored. */
public class SavedGameSettingsTests {
    private static final SwccgoFormatLibrary LIBRARY = VirtualTableScenario._formatLibrary;

    @Test
    public void SavedGameSettingsKeepOnlyValuesThatDifferFromTheFormat() {
        assertEquals("{\"deckSize\":\"100\",\"timer\":\"blitz\"}",
                SavedGameSettings.normalize(LIBRARY, "play", "open", Map.of("deckSize", "100", "timer", "BLITZ")));
        assertEquals("the format's own values are not stored", "{}",
                SavedGameSettings.normalize(LIBRARY, "play", "open", Map.of("deckSize", "60", "timer", "default")));
        assertEquals("{}", SavedGameSettings.normalize(LIBRARY, "tournament", "open", null));
        assertEquals("{\"deckSize\":\"200\"}", SavedGameSettings.normalize(LIBRARY, "play", "anything_goes", Map.of("deckSize", "200")));
    }

    @Test
    public void SavedGameSettingsRefuseBadFormsFormatsAndValues() {
        for (Object[] bad : new Object[][] {
                {"lobby", "open", Map.of()},
                {"play", "no_such_format", Map.of()},
                {"play", "open@45", Map.of()},
                {"play", "open", Map.of("deckSize", "101")},
                {"play", "open", Map.of("startingCredits", "4")},
                {"play", "open", Map.of("noSuchOption", "1")}}) {
            @SuppressWarnings("unchecked") Map<String, String> raw = (Map<String, String>) bad[2];
            assertThrows(java.util.Arrays.toString(bad), IllegalArgumentException.class,
                    () -> SavedGameSettings.normalize(LIBRARY, (String) bad[0], (String) bad[1], raw));
        }
        Map<String, String> many = new HashMap<>();
        for (int i = 0; i < 40; i++)
            many.put("o" + i, "1");
        assertThrows(IllegalArgumentException.class, () -> SavedGameSettings.normalize(LIBRARY, "play", "open", many));
    }
}
