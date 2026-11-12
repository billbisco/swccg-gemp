package com.gempukku.swccgo.async;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

/** swccg-guild/work#222: the Create Tournament card pool form and the players' rules summary (cardPoolForm.js). */
public class CardPoolFormWebClientTests {
    @Test
    public void CardPoolFormBuildsThePoolAndShowsTheRules() throws Exception {
        Assumptions.assumeTrue(nodeAvailable(), "node not installed");
        String js = "src/main/web/js/gemp-016/";
        Process process = new ProcessBuilder("node", "src/test/js/cardPoolFormTests.js",
                Path.of(js + "cardPoolForm.js").toAbsolutePath().toString(),
                Path.of(js + "hallUi.js").toAbsolutePath().toString(),
                Path.of(js + "communication.js").toAbsolutePath().toString(),
                Path.of("src/main/web/hall.html").toAbsolutePath().toString()).redirectErrorStream(true).start();
        try {
            assertTrue(process.waitFor(60, TimeUnit.SECONDS), "Card pool form tests timed out");
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(0, process.exitValue(), output);
            assertTrue(output.contains("card pool form tests passed"), output);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        } finally {
            if (process.isAlive()) process.destroyForcibly();
        }
    }

    private static boolean nodeAvailable() {
        try {
            Process process = new ProcessBuilder("node", "--version").redirectErrorStream(true).start();
            try {
                return process.waitFor(10, TimeUnit.SECONDS) && process.exitValue() == 0;
            } finally {
                if (process.isAlive()) process.destroyForcibly();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (IOException e) { return false; }
    }
}
