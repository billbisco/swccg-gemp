package com.gempukku.swccgo.async;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

public class CardImageUrlWebClientTests {
    @Test
    public void LegacyCardScansUseCanonicalOriginAndPreserveConfiguredResourceRouting() throws Exception {
        Assumptions.assumeTrue(nodeAvailable(), "node not installed");
        String cards = "src/main/web/js/gemp-016/cards/";
        Process process = new ProcessBuilder("node", "src/test/js/cardImageUrlTests.js",
                Path.of(cards + "Card.js").toAbsolutePath().toString(),
                Path.of(cards + "CardImages.js").toAbsolutePath().toString(),
                Path.of(cards + "PackImages.js").toAbsolutePath().toString()).redirectErrorStream(true).start();
        try {
            assertTrue(process.waitFor(60, TimeUnit.SECONDS), "Card image URL tests timed out");
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(0, process.exitValue(), output);
            assertTrue(output.contains("card image URL routing passed"), output);
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
