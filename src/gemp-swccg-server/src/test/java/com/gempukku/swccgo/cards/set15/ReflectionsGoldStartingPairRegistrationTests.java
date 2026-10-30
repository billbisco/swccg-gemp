package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldStartingPairRegistrationTests {
    @Test
    public void StartingPairHasImagesRarityInventoryAndPrintedMetadata() throws Exception {
        var stream = getClass().getResourceAsStream("/reflections-gold-starting-pair.json");
        assertNotNull(stream);
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("combo","15_68")),new HashMap<>(Map.of("combo","15_228")));
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
            assertEquals(2, expected.size());
            for (var row : expected) {
                var card = row.getAsJsonObject();
                String id = card.get("id").getAsString();
                var blueprint = (id.equals("15_68") ? scn.GetLSCard("combo") : scn.GetDSCard("combo")).getBlueprint();
                assertEquals(card.get("title").getAsString(),blueprint.getTitle());
                assertEquals(id.equals("15_68") ? Side.LIGHT : Side.DARK,blueprint.getSide());
                assertEquals(Uniqueness.UNIQUE,blueprint.getUniqueness());
                assertEquals(id.equals("15_68") ? List.of(new TitleComponent(Title.Careful_Planning,Uniqueness.UNIQUE),new TitleComponent("Heading For The Medical Frigate",Uniqueness.UNIQUE)) : List.of(new TitleComponent(Title.Combat_Readiness,Uniqueness.UNIQUE),new TitleComponent("Prepared Defenses",Uniqueness.UNIQUE)),blueprint.getTitleComponents());
                assertEquals(3f,blueprint.getDestiny(),scn.epsilon);
                assertEquals(CardSubtype.USED_OR_STARTING,blueprint.getCardSubtype());
                assertEquals(0,blueprint.getCredits());assertEquals("",blueprint.getLore());assertFalse(blueprint.isHorizontal());
                assertEquals(ExpansionSet.REFLECTIONS_GOLD,blueprint.getExpansionSet());
                scn.BlueprintIconCheck(blueprint,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD));
                scn.BlueprintKeywordCheck(blueprint,List.of());scn.BlueprintPersonaCheck(blueprint,List.of());
                assertEquals(id, Rarity.UNKNOWN, rarity.getCardRarity(id));
                assertTrue(id, images.contains("\"" + id + "\":\"" + card.get("imageUrl").getAsString() + "\""));
                assertNotNull(id, inventory.get(id));
                assertTrue(id, inventory.get(id).get("implemented").getAsBoolean());
                assertEquals(id, card.get("image").getAsString(), inventory.get(id).get("image").getAsString());
                assertEquals(id, card.get("credits").getAsInt(), inventory.get(id).get("credits").getAsInt());
            }
        }
    }
}
