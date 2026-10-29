package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ReflectionsGoldBlankCardsTests {
    private static final SwccgCardBlueprintLibrary LIBRARY = new SwccgCardBlueprintLibrary();
    private static final VirtualTableScenario SCENARIO = new VirtualTableScenario(new HashMap<>(), new HashMap<>());
    private static final Map<Integer, JsonObject> EXPECTED = loadEvidence();

    private static Map<Integer, JsonObject> loadEvidence() {
        var stream = ReflectionsGoldBlankCardsTests.class.getResourceAsStream("/reflections-gold-blank-cards.json");
        assertNotNull("Missing independently transcribed image evidence", stream);
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            Map<Integer, JsonObject> result = new HashMap<>();
            for (JsonElement element : JsonParser.parseReader(reader).getAsJsonArray()) {
                var card = element.getAsJsonObject();
                result.put(card.get("number").getAsInt(), card);
            }
            return result;
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    private static <T extends Enum<T>> List<T> values(JsonArray values, Class<T> type) {
        List<T> result = new ArrayList<>();
        for (JsonElement value : values) result.add(Enum.valueOf(type, value.getAsString()));
        return result;
    }

    private void checkStats(int number) {
        var expected = EXPECTED.get(number);
        var card = LIBRARY.getSwccgoCardBlueprint(expected.get("id").getAsString());
        assertNotNull(card);
        assertEquals(expected.get("title").getAsString(), card.getTitle());
        assertEquals(Side.valueOf(expected.get("side").getAsString().toUpperCase()), card.getSide());
        assertEquals(expected.get("destiny").getAsFloat(), card.getDestiny(), SCENARIO.epsilon);
        assertEquals(expected.get("uniqueness").getAsInt(), card.getUniqueness().getValue());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet());
        assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals("", card.getGameText());
        assertEquals(expected.get("lore").getAsString(), card.getLore());
        SCENARIO.BlueprintIconCheck(card, values(expected.getAsJsonArray("icons"), Icon.class));
        SCENARIO.BlueprintKeywordCheck(card, values(expected.getAsJsonArray("keywords"), Keyword.class));
        SCENARIO.BlueprintCardTypeCheck(card, values(expected.getAsJsonArray("cardTypes"), CardType.class));
        SCENARIO.BlueprintPersonaCheck(card, values(expected.getAsJsonArray("personas"), Persona.class));
        SCENARIO.BlueprintModelTypeCheck(card, values(expected.getAsJsonArray("models"), ModelType.class));
        if (expected.get("kind").getAsString().equals("Character")) {
            assertEquals(expected.get("power").getAsFloat(), card.getPower(), SCENARIO.epsilon);
            assertEquals(expected.get("ability").getAsFloat(), card.getAbility(), SCENARIO.epsilon);
            assertEquals(expected.get("deploy").getAsFloat(), card.getDeployCost(), SCENARIO.epsilon);
            assertEquals(expected.get("forfeit").getAsFloat(), card.getForfeit(), SCENARIO.epsilon);
            assertEquals(1, card.getLandspeed(), SCENARIO.epsilon);
            assertEquals(expected.get("species").isJsonNull() ? null : Species.valueOf(expected.get("species").getAsString()), card.getSpecies());
            if (expected.has("armor")) assertEquals(expected.get("armor").getAsFloat(), card.getArmor(), SCENARIO.epsilon);
            if (expected.has("politics")) assertEquals(expected.get("politics").getAsFloat(), card.getPolitics(), SCENARIO.epsilon);
        } else if (expected.get("kind").getAsString().equals("Effect")) {
            assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        } else if (expected.get("kind").getAsString().equals("Interrupt")) {
            assertEquals(CardSubtype.valueOf(expected.get("subtype").getAsString()), card.getCardSubtype());
        }
    }

    @Test
    public void ReflectionsGoldBlankCardsAreAvailableWithoutRenumberingPublishedSets() {
        assertEquals(14, CardCounts.FULL_SETS_CARD_COUNTS.length);
        var manager = new CollectionsManager(null, null, null, LIBRARY);
        for (int number : EXPECTED.keySet()) {
            assertEquals(60, manager.getDefaultCollection(false).getItemCount("15_" + number));
            assertEquals(60, manager.getDefaultCollection(true).getItemCount("15_" + number));
        }
        assertEquals(60, manager.getDefaultCollection(false).getItemCount("1_19"));
    }

    @Test
    public void TwoOneBeeStatsAndIntentionalBlankGameText() {
        checkStats(1);
    }

    @Test
    public void AnakinSkywalkerStatsAndIntentionalBlankGameText() {
        checkStats(2);
    }

    @Test
    public void ChewbaccaStatsAndIntentionalBlankGameText() {
        checkStats(3);
    }

    @Test
    public void CommanderMyronKonkleStatsAndIntentionalBlankGameText() {
        checkStats(4);
    }

    @Test
    public void DremRedinsStatsAndIntentionalBlankGameText() {
        checkStats(5);
    }

    @Test
    public void EwokTribesmanStatsAndIntentionalBlankGameText() {
        checkStats(6);
    }

    @Test
    public void GoldenrodStatsAndIntentionalBlankGameText() {
        checkStats(7);
    }

    @Test
    public void JarJarStatsAndIntentionalBlankGameText() {
        checkStats(8);
    }

    @Test
    public void OldBenKenobiStatsAndIntentionalBlankGameText() {
        checkStats(9);
    }

    @Test
    public void OwenStatsAndIntentionalBlankGameText() {
        checkStats(10);
    }

    @Test
    public void PalpatineSenatorToNabooStatsAndIntentionalBlankGameText() {
        checkStats(11);
    }

    @Test
    public void RakesJevorikStatsAndIntentionalBlankGameText() {
        checkStats(12);
    }

    @Test
    public void SenatorOrganaStatsAndIntentionalBlankGameText() {
        checkStats(13);
    }

    @Test
    public void SoloStatsAndIntentionalBlankGameText() {
        checkStats(14);
    }

    @Test
    public void TiinnStatsAndIntentionalBlankGameText() {
        checkStats(15);
    }

    @Test
    public void WedgeStatsAndIntentionalBlankGameText() {
        checkStats(16);
    }

    @Test
    public void YodaJediMasterStatsAndIntentionalBlankGameText() {
        checkStats(17);
    }

    @Test
    public void YoungSkywalkerStatsAndIntentionalBlankGameText() {
        checkStats(18);
    }

    @Test
    public void AckbarSAssaultPlanStatsAndIntentionalBlankGameText() {
        checkStats(21);
    }

    @Test
    public void BadLiftersAndFaultyWingsStatsAndIntentionalBlankGameText() {
        checkStats(23);
    }

    @Test
    public void BringMeTheHydrospannersStatsAndIntentionalBlankGameText() {
        checkStats(25);
    }

    @Test
    public void CongratulationsStatsAndIntentionalBlankGameText() {
        checkStats(29);
    }

    @Test
    public void IThoughtHeWasBlindStatsAndIntentionalBlankGameText() {
        checkStats(38);
    }

    @Test
    public void InspectingTheGarbageStatsAndIntentionalBlankGameText() {
        checkStats(40);
    }

    @Test
    public void IsYourStrikeTeamAssembledStatsAndIntentionalBlankGameText() {
        checkStats(41);
    }

    @Test
    public void LaptiNekStatsAndIntentionalBlankGameText() {
        checkStats(42);
    }

    @Test
    public void LookingToTheFutureStatsAndIntentionalBlankGameText() {
        checkStats(44);
    }

    @Test
    public void PrisonerTransferStatsAndIntentionalBlankGameText() {
        checkStats(46);
    }

    @Test
    public void ThatSTheRealTrickStatsAndIntentionalBlankGameText() {
        checkStats(51);
    }

    @Test
    public void TheyAllBrokenStatsAndIntentionalBlankGameText() {
        checkStats(52);
    }

    @Test
    public void ThisIsYourFatherSLightsaberStatsAndIntentionalBlankGameText() {
        checkStats(53);
    }

    @Test
    public void ThroughThePlanetCoreStatsAndIntentionalBlankGameText() {
        checkStats(54);
    }

    @Test
    public void WeAreBraveYourHighnessStatsAndIntentionalBlankGameText() {
        checkStats(57);
    }

    @Test
    public void YouMustCompleteYourTrainingStatsAndIntentionalBlankGameText() {
        checkStats(58);
    }

    @Test
    public void BickeringAtItsFinestStatsAndIntentionalBlankGameText() {
        checkStats(65);
    }

    @Test
    public void BikerScoutDownStatsAndIntentionalBlankGameText() {
        checkStats(66);
    }

    @Test
    public void ClingingToTheVeryEdgeStatsAndIntentionalBlankGameText() {
        checkStats(71);
    }

    @Test
    public void DroidOverboardStatsAndIntentionalBlankGameText() {
        checkStats(74);
    }

    @Test
    public void FalsePastStatsAndIntentionalBlankGameText() {
        checkStats(79);
    }

    @Test
    public void FatherPleaseStatsAndIntentionalBlankGameText() {
        checkStats(80);
    }

    @Test
    public void IUsedToLiveHereYouKnowStatsAndIntentionalBlankGameText() {
        checkStats(89);
    }

    @Test
    public void JediTacticsStatsAndIntentionalBlankGameText() {
        checkStats(95);
    }

    @Test
    public void LukeWeReGoingToHaveCompanyStatsAndIntentionalBlankGameText() {
        checkStats(101);
    }

    @Test
    public void MissionBriefingStatsAndIntentionalBlankGameText() {
        checkStats(104);
    }

    @Test
    public void OldSmoothiesStatsAndIntentionalBlankGameText() {
        checkStats(116);
    }

    @Test
    public void OuchTimeStatsAndIntentionalBlankGameText() {
        checkStats(118);
    }

    @Test
    public void SeeYouAtTheRendezvousStatsAndIntentionalBlankGameText() {
        checkStats(128);
    }

    @Test
    public void StayHereStatsAndIntentionalBlankGameText() {
        checkStats(134);
    }

    @Test
    public void TakingOnTheWholeEmpireByMyselfStatsAndIntentionalBlankGameText() {
        checkStats(137);
    }

    @Test
    public void WeDonTWantToCauseAnyTroubleStatsAndIntentionalBlankGameText() {
        checkStats(147);
    }

    @Test
    public void YodaIMustKnowStatsAndIntentionalBlankGameText() {
        checkStats(152);
    }

    @Test
    public void YousaGuysBombadStatsAndIntentionalBlankGameText() {
        checkStats(155);
    }

    @Test
    public void BobaFettStatsAndIntentionalBlankGameText() {
        checkStats(157);
    }

    @Test
    public void CoilenStatsAndIntentionalBlankGameText() {
        checkStats(158);
    }

    @Test
    public void DarthMaulSidiousApprenticeStatsAndIntentionalBlankGameText() {
        checkStats(159);
    }

    @Test
    public void EmperorStatsAndIntentionalBlankGameText() {
        checkStats(160);
    }

    @Test
    public void EmperorSRoyalGuardStatsAndIntentionalBlankGameText() {
        checkStats(161);
    }

    @Test
    public void GovernorTarkinStatsAndIntentionalBlankGameText() {
        checkStats(162);
    }

    @Test
    public void GriffMirtongStatsAndIntentionalBlankGameText() {
        checkStats(163);
    }

    @Test
    public void GweetaStatsAndIntentionalBlankGameText() {
        checkStats(164);
    }

    @Test
    public void LordVaderLordOfTheSithStatsAndIntentionalBlankGameText() {
        checkStats(165);
    }

    @Test
    public void SSA33StatsAndIntentionalBlankGameText() {
        checkStats(166);
    }

    @Test
    public void TK420StatsAndIntentionalBlankGameText() {
        checkStats(167);
    }

    @Test
    public void WattoJunkDealerStatsAndIntentionalBlankGameText() {
        checkStats(168);
    }

    @Test
    public void BackOnScheduleStatsAndIntentionalBlankGameText() {
        checkStats(172);
    }

    @Test
    public void BattlePlainsVictoryStatsAndIntentionalBlankGameText() {
        checkStats(173);
    }

    @Test
    public void BoontaEveWageringStatsAndIntentionalBlankGameText() {
        checkStats(176);
    }

    @Test
    public void EwokSuperstitionsStatsAndIntentionalBlankGameText() {
        checkStats(180);
    }

    @Test
    public void ImperialCompensationStatsAndIntentionalBlankGameText() {
        checkStats(185);
    }

    @Test
    public void ImperialFirepowerStatsAndIntentionalBlankGameText() {
        checkStats(187);
    }

    @Test
    public void JabbaSDroidPoolStatsAndIntentionalBlankGameText() {
        checkStats(189);
    }

    @Test
    public void JediRocksStatsAndIntentionalBlankGameText() {
        checkStats(190);
    }

    @Test
    public void NameTheSystemStatsAndIntentionalBlankGameText() {
        checkStats(192);
    }

    @Test
    public void NotWorthTheTroubleStatsAndIntentionalBlankGameText() {
        checkStats(193);
    }

    @Test
    public void OneArmSFrozenLunchStatsAndIntentionalBlankGameText() {
        checkStats(194);
    }

    @Test
    public void PerfectHibernationStatsAndIntentionalBlankGameText() {
        checkStats(196);
    }

    @Test
    public void PerformingUnderPressureStatsAndIntentionalBlankGameText() {
        checkStats(197);
    }

    @Test
    public void SarlaccTentaclesStatsAndIntentionalBlankGameText() {
        checkStats(204);
    }

    @Test
    public void TatooineResaleOperationsStatsAndIntentionalBlankGameText() {
        checkStats(209);
    }

    @Test
    public void TatooineUndergroundOperationsStatsAndIntentionalBlankGameText() {
        checkStats(210);
    }

    @Test
    public void VisageOfAViceroyStatsAndIntentionalBlankGameText() {
        checkStats(212);
    }

    @Test
    public void WorryStatsAndIntentionalBlankGameText() {
        checkStats(217);
    }

    @Test
    public void BeginRetreatStatsAndIntentionalBlankGameText() {
        checkStats(221);
    }

    @Test
    public void BenStatsAndIntentionalBlankGameText() {
        checkStats(222);
    }

    @Test
    public void BobaFettWhereStatsAndIntentionalBlankGameText() {
        checkStats(224);
    }

    @Test
    public void CannonFireStatsAndIntentionalBlankGameText() {
        checkStats(226);
    }

    @Test
    public void DisciplinaryActionsStatsAndIntentionalBlankGameText() {
        checkStats(236);
    }

    @Test
    public void EntryDeniedStatsAndIntentionalBlankGameText() {
        checkStats(239);
    }

    @Test
    public void FlagEmblemsStatsAndIntentionalBlankGameText() {
        checkStats(240);
    }

    @Test
    public void GreatInterestStatsAndIntentionalBlankGameText() {
        checkStats(243);
    }

    @Test
    public void IDidnTMeanToStatsAndIntentionalBlankGameText() {
        checkStats(246);
    }

    @Test
    public void ImperialDataNetworkStatsAndIntentionalBlankGameText() {
        checkStats(251);
    }

    @Test
    public void ImperialDetachmentsStatsAndIntentionalBlankGameText() {
        checkStats(252);
    }

    @Test
    public void InThePitsStatsAndIntentionalBlankGameText() {
        checkStats(254);
    }

    @Test
    public void IsThereSomethingAliveDownHereStatsAndIntentionalBlankGameText() {
        checkStats(256);
    }

    @Test
    public void LockedDoorStatsAndIntentionalBlankGameText() {
        checkStats(261);
    }

    @Test
    public void SecuritySweepStatsAndIntentionalBlankGameText() {
        checkStats(278);
    }

    @Test
    public void SpeederDownedStatsAndIntentionalBlankGameText() {
        checkStats(283);
    }

    @Test
    public void TwilightFadingStatsAndIntentionalBlankGameText() {
        checkStats(298);
    }

    @Test
    public void WeWouldBeHonoredIfYouWouldJoinUsStatsAndIntentionalBlankGameText() {
        checkStats(305);
    }

    @Test
    public void WhereAreYouTakingThisThingStatsAndIntentionalBlankGameText() {
        checkStats(306);
    }

    @Test
    public void YouWillMeetYourDestinyStatsAndIntentionalBlankGameText() {
        checkStats(308);
    }
}
