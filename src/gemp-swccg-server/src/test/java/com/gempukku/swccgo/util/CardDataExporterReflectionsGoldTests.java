package com.gempukku.swccgo.util;

import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.junit.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import static org.junit.Assert.*;

public class CardDataExporterReflectionsGoldTests {
    @Test
    public void ReflectionsGoldLightCardsExportPrintedCredits() throws Exception {
        var stream = getClass().getResourceAsStream("/reflections-gold-light-nonblank.json");
        assertNotNull(stream);
        var library = new SwccgCardBlueprintLibrary();
        var exporter = new CardDataExporter();
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            var evidence = JsonParser.parseReader(reader).getAsJsonArray();
            assertEquals(47, evidence.size());
            for (var item : evidence) {
                var card = item.getAsJsonObject();
                String id = card.get("id").getAsString();
                var blueprint = library.getSwccgoCardBlueprint(id);
                var exported = exporter.extractCardData(id, blueprint, Set.of());
                var json = JsonParser.parseString(new Gson().toJson(exported)).getAsJsonObject();
                assertEquals(id, json.get("cardId").getAsString());
                assertEquals(id, card.get("credits").getAsInt(), json.get("credits").getAsInt());
                assertEquals(id, card.get("credits").getAsInt(), blueprint.getCredits());
            }
        }
    }

    @Test
    public void CardsWithoutPrintedCreditsExportZero() {
        var library = new SwccgCardBlueprintLibrary();
        var exporter = new CardDataExporter();
        for (String id : new String[]{"1_19", "15_2", "15_84", "15_126"}) {
            var card = library.getSwccgoCardBlueprint(id);
            assertEquals(id, 0, card.getCredits());
            assertEquals(id, 0, exporter.extractCardData(id, card, Set.of()).get("credits"));
        }
    }
}
