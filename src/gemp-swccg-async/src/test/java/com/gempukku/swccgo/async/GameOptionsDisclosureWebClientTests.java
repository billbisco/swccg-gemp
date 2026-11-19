package com.gempukku.swccgo.async;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

/** #778: collapsing the optional panel preserves effective rules and per-table reset preserves preferences. */
public class GameOptionsDisclosureWebClientTests {
    @Test
    public void collapsedRulesAndDraftDefaultsRemainConsistent() throws Exception {
        Process process = new ProcessBuilder("node", "src/test/js/gameOptionsDisclosureTests.js",
                Path.of("src/main/web/js/gemp-016/gameOptionsForm.js").toAbsolutePath().toString())
                .redirectErrorStream(true).start();
        try {
            assertTrue(process.waitFor(30, TimeUnit.SECONDS), "Disclosure tests timed out");
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(0, process.exitValue(), output);
            assertTrue(output.contains("game options disclosure tests passed"), output);
        } finally {
            if (process.isAlive()) process.destroyForcibly();
        }
    }
}
