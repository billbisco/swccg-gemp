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

public class ReflectionsGoldDarkNextStatsTests {
    private static final SwccgCardBlueprintLibrary LIBRARY = new SwccgCardBlueprintLibrary();
    private static final VirtualTableScenario SCENARIO = new VirtualTableScenario(new HashMap<>(), new HashMap<>());
    private static final Map<String, JsonObject> EXPECTED = load();

    private static Map<String, JsonObject> load() {
        var stream = ReflectionsGoldDarkNextStatsTests.class.getResourceAsStream("/reflections-gold-dark-combos-next.json");
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
        assertFalse(card.isHorizontal());
        assertEquals(expected.getAsJsonArray("comboTitles").asList().stream().map(JsonElement::getAsString).toList(), card.getTitles());
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.valueOf(expected.get("type").getAsString())));
        if (expected.has("componentUniqueness")) {
            var symbols = expected.getAsJsonArray("componentUniqueness");
            for(int i=0;i<symbols.size();i++) assertEquals(Uniqueness.valueOf(symbols.get(i).getAsString()),card.getTitleComponents().get(i).uniqueness());
        }
        assertEquals(CardSubtype.valueOf(expected.get("subtype").getAsString()), card.getCardSubtype());
        List<Icon> icons = new ArrayList<>();
        for (var icon : expected.getAsJsonArray("icons")) icons.add(Icon.valueOf(icon.getAsString()));
        SCENARIO.BlueprintIconCheck(card, icons);
        List<Keyword> keywords = new ArrayList<>();
        for (var keyword : expected.getAsJsonArray("keywords")) keywords.add(Keyword.valueOf(keyword.getAsString()));
        SCENARIO.BlueprintKeywordCheck(card, keywords);
    }

    @Test public void InformantAndTakeelPrintedMetadataMatchesRemaster() { check("15_255"); }
    @Test public void ColdFeetAndHeHasntComeBackYetPrintedMetadataMatchesRemaster() { check("15_227"); }
    @Test public void PresenceOfTheForceAndDarkWatersPrintedMetadataMatchesRemaster() { check("15_199"); }
    @Test public void ForceLightningAndRiseMyFriendPrintedMetadataMatchesRemaster() { check("15_242"); }
    @Test public void WellGuardedAndSecurityPrecautionsPrintedMetadataMatchesRemaster() { check("15_215"); }
    @Test public void YourDestinyAndInsignificantRebellionPrintedMetadataMatchesRemaster() { check("15_218"); }
    @Test public void EmperorsPowerAndOverseeingItPersonallyPrintedMetadataMatchesRemaster() { check("15_178"); }
}
