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

public class ReflectionsGoldDarkEquivalentStatsTests {
    private static final SwccgCardBlueprintLibrary LIBRARY = new SwccgCardBlueprintLibrary();
    private static final VirtualTableScenario SCENARIO = new VirtualTableScenario(new HashMap<>(), new HashMap<>());
    private static final Map<String, JsonObject> EXPECTED = load();

    private static Map<String, JsonObject> load() {
        var stream = ReflectionsGoldDarkEquivalentStatsTests.class.getResourceAsStream("/reflections-gold-dark-equivalent-reprints.json");
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
    public void SearchAndDestroyPrintedMetadataMatchesRemaster() { check("15_207"); }

    @Test
    public void SecurityPrecautionsPrintedMetadataMatchesRemaster() { check("15_208"); }

    @Test
    public void WellEarnedCommandPrintedMetadataMatchesRemaster() { check("15_216"); }

    @Test
    public void BlowParriedPrintedMetadataMatchesRemaster() { check("15_223"); }

    @Test
    public void DarkManeuversPrintedMetadataMatchesRemaster() { check("15_233"); }

    @Test
    public void ForceLightningPrintedMetadataMatchesRemaster() { check("15_241"); }

    @Test
    public void HitRacerPrintedMetadataMatchesRemaster() { check("15_245"); }

    @Test
    public void ImperialCommandPrintedMetadataMatchesRemaster() { check("15_249"); }

    @Test
    public void JabbasThroughWithYouPrintedMetadataMatchesRemaster() { check("15_258"); }

    @Test
    public void LocalTroublePrintedMetadataMatchesRemaster() { check("15_260"); }

    @Test
    public void NoneShallPassPrintedMetadataMatchesRemaster() { check("15_265"); }

    @Test
    public void SniperPrintedMetadataMatchesRemaster() { check("15_281"); }

    @Test
    public void TacticalSupportPrintedMetadataMatchesRemaster() { check("15_287"); }

    @Test
    public void TheCircleIsNowCompletePrintedMetadataMatchesRemaster() { check("15_289"); }

    @Test
    public void TrapDoorPrintedMetadataMatchesRemaster() { check("15_296"); }

    @Test
    public void UncertainIsTheFuturePrintedMetadataMatchesRemaster() { check("15_299"); }

    @Test
    public void UnexpectedInterruptionPrintedMetadataMatchesRemaster() { check("15_300"); }

    @Test
    public void YouAreBeatenPrintedMetadataMatchesRemaster() { check("15_307"); }
}
