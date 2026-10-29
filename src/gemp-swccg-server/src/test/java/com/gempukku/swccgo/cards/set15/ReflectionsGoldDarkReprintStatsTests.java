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

public class ReflectionsGoldDarkReprintStatsTests {
    private static final SwccgCardBlueprintLibrary LIBRARY = new SwccgCardBlueprintLibrary();
    private static final VirtualTableScenario SCENARIO = new VirtualTableScenario(new HashMap<>(), new HashMap<>());
    private static final Map<String, JsonObject> EXPECTED = load();

    private static Map<String, JsonObject> load() {
        var stream = ReflectionsGoldDarkReprintStatsTests.class.getResourceAsStream("/reflections-gold-dark-reprints.json");
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
    public void AlertMyStarDestroyerPrintedMetadataMatchesRemaster() { check("15_170"); }

    @Test
    public void AratechCorporationPrintedMetadataMatchesRemaster() { check("15_171"); }

    @Test
    public void ExpandTheEmpirePrintedMetadataMatchesRemaster() { check("15_181"); }

    @Test
    public void IHadNoChoicePrintedMetadataMatchesRemaster() { check("15_183"); }

    @Test
    public void ImperialJusticePrintedMetadataMatchesRemaster() { check("15_188"); }

    @Test
    public void OpenFirePrintedMetadataMatchesRemaster() { check("15_195"); }

    @Test
    public void PerimeterPatrolPrintedMetadataMatchesRemaster() { check("15_198"); }

    @Test
    public void QuietlyObservingPrintedMetadataMatchesRemaster() { check("15_201"); }

    @Test
    public void RelentlessTrackingPrintedMetadataMatchesRemaster() { check("15_202"); }

    @Test
    public void ResponsibilityOfCommandPrintedMetadataMatchesRemaster() { check("15_203"); }

    @Test
    public void ScumAndVillainyPrintedMetadataMatchesRemaster() { check("15_205"); }

    @Test
    public void WeaponMalfunctionPrintedMetadataMatchesRemaster() { check("15_214"); }

    @Test
    public void AtmosphericAssaultPrintedMetadataMatchesRemaster() { check("15_220"); }

    @Test
    public void CounterSurpriseAssaultPrintedMetadataMatchesRemaster() { check("15_229"); }

    @Test
    public void DarkJediPresencePrintedMetadataMatchesRemaster() { check("15_231"); }

    @Test
    public void DioxisPrintedMetadataMatchesRemaster() { check("15_234"); }

    @Test
    public void ImperialSupplyPrintedMetadataMatchesRemaster() { check("15_253"); }

    @Test
    public void OurFirstCatchOfTheDayPrintedMetadataMatchesRemaster() { check("15_268"); }

    @Test
    public void PitCrewsPrintedMetadataMatchesRemaster() { check("15_270"); }

    @Test
    public void PodracerCollisionPrintedMetadataMatchesRemaster() { check("15_271"); }

    @Test
    public void ProjectiveTelepathyPrintedMetadataMatchesRemaster() { check("15_274"); }

    @Test
    public void RollingRollingRollingPrintedMetadataMatchesRemaster() { check("15_277"); }

    @Test
    public void ShortRangeFightersPrintedMetadataMatchesRemaster() { check("15_279"); }

    @Test
    public void SquabblingDelegatesPrintedMetadataMatchesRemaster() { check("15_284"); }

    @Test
    public void TallonRollPrintedMetadataMatchesRemaster() { check("15_288"); }

    @Test
    public void ThoseRebelsWontEscapeUsPrintedMetadataMatchesRemaster() { check("15_292"); }

    @Test
    public void ThroughTheCorridorPrintedMetadataMatchesRemaster() { check("15_293"); }

    @Test
    public void VoteNowPrintedMetadataMatchesRemaster() { check("15_301"); }
}
