package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.google.gson.*;
import org.junit.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;

public class ReflectionsGoldDarkChangedStatsTests {
    private static final SwccgCardBlueprintLibrary LIBRARY = new SwccgCardBlueprintLibrary();
    private static final VirtualTableScenario SCENARIO = new VirtualTableScenario(new HashMap<>(), new HashMap<>());
    private static final Map<String, JsonObject> EXPECTED = load();

    private static Map<String, JsonObject> load() {
        var stream = ReflectionsGoldDarkChangedStatsTests.class.getResourceAsStream("/reflections-gold-dark-changed-reprints.json");
        assertNotNull(stream);
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            Map<String, JsonObject> result = new HashMap<>();
            for (var entry : JsonParser.parseReader(reader).getAsJsonArray()) {
                var card = entry.getAsJsonObject();
                result.put(card.get("id").getAsString(), card);
            }
            return result;
        } catch (java.io.IOException ex) { throw new java.io.UncheckedIOException(ex); }
    }

    private void check(String id) {
        var expected = EXPECTED.get(id);
        var card = LIBRARY.getSwccgoCardBlueprint(id);
        assertEquals(expected.get("title").getAsString(), card.getTitle());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(expected.get("destiny").getAsFloat(), card.getDestiny(), SCENARIO.epsilon);
        assertEquals(Uniqueness.valueOf(expected.get("uniqueness").getAsString()), card.getUniqueness());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals(expected.get("credits").getAsInt(), card.getCredits());
        assertEquals(expected.get("gameText").getAsString(), card.getGameText());
        assertEquals("", card.getLore());
        assertTrue(card.isHorizontal());
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.valueOf(expected.get("type").getAsString())));
        assertEquals(CardSubtype.valueOf(expected.get("subtype").getAsString()), card.getCardSubtype());
        List<Icon> icons = new ArrayList<>();
        for (var icon : expected.getAsJsonArray("icons")) icons.add(Icon.valueOf(icon.getAsString()));
        SCENARIO.BlueprintIconCheck(card, icons);
        List<Keyword> keywords = new ArrayList<>();
        for (var keyword : expected.getAsJsonArray("keywords")) keywords.add(Keyword.valueOf(keyword.getAsString()));
        SCENARIO.BlueprintKeywordCheck(card, keywords);
    }

    @Test
    public void CrossfirePrintedMetadataMatchesRemaster() { check("15_177"); }

    @Test
    public void FlagshipOperationsPrintedMetadataMatchesRemaster() { check("15_182"); }

    @Test
    public void WeHaveAPrisonerPrintedMetadataMatchesRemaster() { check("15_303"); }
}
