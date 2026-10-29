package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.common.Rarity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldDarkSevenRegistrationTests {
    @Test
    public void ReflectionsGoldDarkCardsHaveImagesRarityAndInventory() throws Exception {
        var stream = getClass().getResourceAsStream("/reflections-gold-dark-seven-registration.json");
        assertNotNull(stream);
        var rarity = new RarityReader().getSetRarity("15");
        // Surefire runs from the server module, alongside the frontend module.
        String images = Files.readString(Path.of("../gemp-swccg-async/src/main/web/js/gemp-016/cards/CardImages.js"));
        var inventory = new HashMap<String, JsonObject>();
        for (var row : JsonParser.parseString(Files.readString(Path.of("../../docs/reflections-gold/inventory.json"))).getAsJsonArray()) {
            var card = row.getAsJsonObject();
            inventory.put(card.get("id").getAsString(), card);
        }
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            var expected = JsonParser.parseReader(reader).getAsJsonArray();
            assertEquals(5, expected.size());
            for (var row : expected) {
                var card = row.getAsJsonObject();
                String id = card.get("id").getAsString();
                String filename = card.get("file").getAsString();
                String imageHash = java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("MD5").digest(filename.getBytes(StandardCharsets.UTF_8)));
                assertEquals(id, Rarity.UNKNOWN, rarity.getCardRarity(id));
                assertTrue(id, images.contains("\"" + id + "\":\"" + "https://wiki.swccg.com/images/" + imageHash.charAt(0) + "/" + imageHash.substring(0, 2) + "/" + filename + "\""));
                assertNotNull(id, inventory.get(id));
                assertTrue(id, inventory.get(id).get("implemented").getAsBoolean());
                assertEquals(id, filename, inventory.get(id).get("image").getAsString());
                assertEquals(id, card.get("credits").getAsInt(), inventory.get(id).get("credits").getAsInt());
            }
        }
    }
}
