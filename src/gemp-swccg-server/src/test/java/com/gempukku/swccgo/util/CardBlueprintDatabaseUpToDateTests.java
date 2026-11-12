package com.gempukku.swccgo.util;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * The card blueprint database files are generated from the Java card classes (swccg-guild/work#284).
 * These tests fail when the committed files no longer match a fresh export, so a card change or a
 * Players Committee sync can't leave them stale or overwrite our cards.
 */
public class CardBlueprintDatabaseUpToDateTests {

    private static Path resourcesDir() {
        // Maven runs tests from src/gemp-swccg-server
        Path dir = Paths.get(System.getProperty("user.dir"), "..", CardDataExporter.DEFAULT_RESOURCES_DIR).normalize();
        assertTrue("Could not find " + dir, Files.isDirectory(dir));
        return dir;
    }

    @Test
    public void CommittedDatabaseFilesMatchAFreshExport() throws Exception {
        Map<String, String> rendered = CardDataExporter.renderDatabaseFiles(new CardDataExporter().exportAllCards());
        List<String> stale = CardDataExporter.staleDatabaseFiles(rendered, resourcesDir());
        assertTrue("These card database files are out of date with the card classes: " + stale
                + ". Do not edit them by hand. Run " + CardDataExporter.REGENERATE_COMMAND
                + " and commit the result.", stale.isEmpty());
    }

    @Test
    public void ExportIsTheSameEveryRun() throws Exception {
        CardDataExporter exporter = new CardDataExporter();
        assertEquals(CardDataExporter.renderDatabaseFiles(exporter.exportAllCards()),
                CardDataExporter.renderDatabaseFiles(exporter.exportAllCards()));
    }

    @Test
    public void StaleOrMissingFileIsReported() throws Exception {
        Map<String, Object> card = new LinkedHashMap<>();
        card.put("cardId", "1_1");
        card.put("side", "LIGHT");
        List<Map<String, Object>> cards = new ArrayList<>();
        cards.add(card);
        Map<String, String> rendered = CardDataExporter.renderDatabaseFiles(cards);

        Path dir = Files.createTempDirectory("blueprint-db");
        for (Map.Entry<String, String> file : rendered.entrySet()) {
            Files.writeString(dir.resolve(file.getKey()), file.getValue(), StandardCharsets.UTF_8);
        }
        assertTrue(CardDataExporter.staleDatabaseFiles(rendered, dir).isEmpty());

        // A hand edit (or another repo's copy) makes the file stale
        Files.writeString(dir.resolve(CardDataExporter.LIGHT_DATABASE_FILE), "[]\n", StandardCharsets.UTF_8);
        Files.delete(dir.resolve(CardDataExporter.DARK_DATABASE_FILE));
        assertEquals(List.of(CardDataExporter.LIGHT_DATABASE_FILE, CardDataExporter.DARK_DATABASE_FILE),
                CardDataExporter.staleDatabaseFiles(rendered, dir));
    }

    @Test
    public void SideFilesHoldOnlyTheirSide() {
        Map<String, Object> light = new LinkedHashMap<>();
        light.put("cardId", "1_1");
        light.put("side", "LIGHT");
        Map<String, Object> dark = new LinkedHashMap<>();
        dark.put("cardId", "1_2");
        dark.put("side", "DARK");
        Map<String, String> rendered = CardDataExporter.renderDatabaseFiles(List.of(light, dark));
        assertTrue(rendered.get(CardDataExporter.LIGHT_DATABASE_FILE).contains("1_1"));
        assertFalse(rendered.get(CardDataExporter.LIGHT_DATABASE_FILE).contains("1_2"));
        assertTrue(rendered.get(CardDataExporter.DARK_DATABASE_FILE).contains("1_2"));
        assertFalse(rendered.get(CardDataExporter.DARK_DATABASE_FILE).contains("1_1"));
    }
}
