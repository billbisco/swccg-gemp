package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.google.gson.*;
import org.junit.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.Assert.*;

public class ReflectionsGoldDarkComboStatsTests {
    private static final SwccgCardBlueprintLibrary LIBRARY = new SwccgCardBlueprintLibrary();
    private static final VirtualTableScenario SCENARIO = new VirtualTableScenario(new HashMap<>(), new HashMap<>());
    private static final Map<String, JsonObject> EXPECTED = load();
    private static Map<String, JsonObject> load() {
        var stream = ReflectionsGoldDarkComboStatsTests.class.getResourceAsStream("/reflections-gold-dark-combos.json");
        assertNotNull(stream);
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            Map<String, JsonObject> result = new HashMap<>();
            for (var entry : JsonParser.parseReader(reader).getAsJsonArray()) {
                var card = entry.getAsJsonObject(); result.put(card.get("id").getAsString(), card);
            }
            return result;
        } catch (java.io.IOException ex) { throw new java.io.UncheckedIOException(ex); }
    }
    private void check(String id) {
        var e = EXPECTED.get(id); var card = LIBRARY.getSwccgoCardBlueprint(id);
        assertEquals(e.get("title").getAsString(), card.getTitle());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(e.get("destiny").getAsFloat(), card.getDestiny(), SCENARIO.epsilon);
        assertEquals(e.get("unique").getAsBoolean() ? Uniqueness.UNIQUE : Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals(e.get("credits").getAsInt(), card.getCredits());
        assertEquals("", card.getLore());
        assertEquals(e.get("horizontal").getAsBoolean(), card.isHorizontal());
        SCENARIO.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        assertEquals(CardSubtype.valueOf(e.get("subtype").getAsString()), card.getCardSubtype());
        var icons = new ArrayList<>(List.of(Icon.INTERRUPT, Icon.REFLECTIONS_GOLD));
        if(e.get("episodeI").getAsBoolean()) icons.add(Icon.EPISODE_I);
        SCENARIO.BlueprintIconCheck(card, icons);
        SCENARIO.BlueprintKeywordCheck(card, List.of());
        SCENARIO.BlueprintPersonaCheck(card, List.of());
        var titles = new ArrayList<String>();
        for(var title : e.getAsJsonArray("titles")) titles.add(title.getAsString());
        assertEquals(titles, card.getTitles());
    }
    @Test public void MasterDestroyersAndRollingRollingRollingPrintedMetadataMatchesRemaster() { check("15_263"); }
    @Test public void MaulStrikesAndNoMatchForASithPrintedMetadataMatchesRemaster() { check("15_264"); }
    @Test public void OotaGootaSoloPrintedMetadataMatchesRemaster() { check("15_266"); }
    @Test public void OperationalAsPlannedAndWoundedWarriorPrintedMetadataMatchesRemaster() { check("15_267"); }
    @Test public void OverwhelmedAndADarkTimeForTheRebellionPrintedMetadataMatchesRemaster() { check("15_269"); }
    @Test public void PodracerCollisionAndHitRacerPrintedMetadataMatchesRemaster() { check("15_272"); }
    @Test public void PointManAndSlipSlidingAwayPrintedMetadataMatchesRemaster() { check("15_273"); }
    @Test public void PutAllSectionsOnAlertAndFullScaleAlertPrintedMetadataMatchesRemaster() { check("15_275"); }
    @Test public void ReleaseYourAngerAndUnexpectedInterruptionPrintedMetadataMatchesRemaster() { check("15_276"); }
    @Test public void ShutHimUpOrShutHimDownAndVoyeurPrintedMetadataMatchesRemaster() { check("15_280"); }
    @Test public void SomethingHitUsAndWhyDidntYouTellMePrintedMetadataMatchesRemaster() { check("15_282"); }
    @Test public void StartYourEnginesAndPitCrewsPrintedMetadataMatchesRemaster() { check("15_285"); }
    @Test public void SurpriseAndEChuTaPrintedMetadataMatchesRemaster() { check("15_286"); }
    @Test public void TheCircleIsNowCompleteAndVadersObsessionPrintedMetadataMatchesRemaster() { check("15_290"); }
    @Test public void TheyreStillComingThroughPrintedMetadataMatchesRemaster() { check("15_291"); }
    @Test public void TradeFederationTacticsAndWereHitArtooPrintedMetadataMatchesRemaster() { check("15_294"); }
    @Test public void TrampleAndLightsaberDeficiencyPrintedMetadataMatchesRemaster() { check("15_295"); }
    @Test public void TrooperAssaultAndTacticalSupportPrintedMetadataMatchesRemaster() { check("15_297"); }
    @Test public void WattosChanceCubeAndYouSwindledMePrintedMetadataMatchesRemaster() { check("15_302"); }
    @Test public void WeMustAccelerateOurPlansAndVoteNowPrintedMetadataMatchesRemaster() { check("15_304"); }
}
