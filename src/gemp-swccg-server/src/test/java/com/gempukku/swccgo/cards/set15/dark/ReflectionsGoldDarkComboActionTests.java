package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldDarkComboActionTests {
    private void selectFromReserve(VirtualTableScenario s, String blueprint, int count) {
        for (int selected = 0; selected < count; selected++) {
            var ids = s.DSGetADParam("cardId");
            var bps = s.DSGetADParam("blueprintId");
            var selectable = s.DSGetADParam("selectable");
            String chosen = null;
            for (int i = 0; i < bps.length && chosen == null; i++)
                if (blueprint.equals(bps[i]) && "true".equals(selectable[i])) chosen = ids[i];
            assertNotNull("Expected a selectable matching card", chosen);
            s.PlayerDecided(com.gempukku.swccgo.framework.TestBase.DS, chosen);
            s.PassAllResponses();
        }
    }
    private VirtualTableScenario scenario(int id, String target) {
        var dark = new HashMap<String, String>();
        dark.put("card", "15_" + id);
        dark.put("target", target);
        return new VirtualTableScenario(new HashMap<>(), dark);
    }

    private void search(int id, String targetId, String text, int cost, boolean lost) {
        var scn = scenario(id, targetId);
        var card = scn.GetDSCard("card");
        var target = scn.GetDSCard("target");
        scn.StartGame();
        scn.MoveCardsToDSHand(card);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(5);
        scn.MoveCardsToTopOfDSReserveDeck(target);
        int force = scn.GetDSForcePileCount();
        scn.DSPlayCard(card, text);
        scn.PassAllResponses();
        scn.DSChooseCard(target);
        scn.PassAllResponses();
        assertTrue(scn.GetDSHand().contains(target));
        assertEquals(force - cost, scn.GetDSForcePileCount());
        assertTrue((lost ? scn.GetDSLostPile() : scn.GetDSUsedPile()).contains(card));
    }

    @Test public void MasterDestroyersAndRollingRollingRollingUsedSearchFindsDestroyerDroidWithoutForceCost() {
        search(263, "12_104", "Take card into hand from Reserve Deck", 0, false);
    }
    @Test public void MaulStrikesAndNoMatchForASithSearchCostsOneAndIsLost() {
        search(264, "1_324", "Take lightsaber into hand from Reserve Deck", 1, true);
    }
    @Test public void ReleaseYourAngerAndUnexpectedInterruptionSearchCostsTwoAndIsLost() {
        search(276, "1_249", "Take Interrupt into hand from Reserve Deck", 2, true);
    }
    @Test public void WeMustAccelerateOurPlansAndVoteNowEffectSearchCostsThreeAndIsUsed() {
        search(304, "1_219", "Take Effect into hand from Reserve Deck", 3, false);
    }
    @Test public void OperationalAsPlannedAndWoundedWarriorPlacesHandCardOnForcePileAsUsed() {
        var scn = scenario(267, "1_192");
        var card = scn.GetDSCard("card");
        var target = scn.GetDSCard("target");
        scn.StartGame();
        scn.MoveCardsToDSHand(card, target);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPlayCard(card, "Place a card from hand on Force Pile");
        scn.PassAllResponses();
        assertEquals(target, scn.GetTopOfDSForcePile());
        assertTrue(scn.GetDSUsedPile().contains(card));
    }
    @Test public void PointManAndSlipSlidingAwayMovesTopCardToBottomWithoutChangingPileSize() {
        var scn = scenario(273, "1_192");
        var card = scn.GetDSCard("card");
        var target = scn.GetDSCard("target");
        scn.StartGame();
        scn.MoveCardsToDSHand(card);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(3);
        scn.MoveCardsToTopOfDSForcePile(target);
        int size = scn.GetDSForcePileCount();
        scn.DSPlayCard(card, "Relocate card to bottom of Deck or Pile");
        scn.DSChooseCard(target);
        scn.PassAllResponses();
        assertNotEquals(target, scn.GetTopOfDSForcePile());
        assertEquals(size, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSForcePile().contains(target));
        assertTrue(scn.GetDSUsedPile().contains(card));
    }
    @Test public void TheyreStillComingThroughCanPutPeekedCardUnderReserveDeckAsUsed() {
        var scn = scenario(291, "1_192");
        var card = scn.GetDSCard("card");
        var target = scn.GetDSCard("target");
        scn.StartGame();
        scn.MoveCardsToDSHand(card);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSForcePile(target);
        int size = scn.GetDSReserveDeckCount();
        scn.DSPlayCard(card, "Peek at top card of Force Pile");
        scn.PassAllResponses();
        scn.DSDismissRevealedCards();
        scn.PassAllResponses();
        scn.DSChooseOption("Place it under Reserve Deck");
        scn.PassAllResponses();
        assertEquals(size + 1, scn.GetDSReserveDeckCount());
        assertTrue(scn.GetDSReserveDeck().contains(target));
        assertFalse(scn.GetDSForcePile().contains(target));
        assertTrue(scn.GetDSUsedPile().contains(card));
    }

    private void cancelEffect(int id, String effectId) {
        var light = new HashMap<String, String>();
        light.put("effect", effectId); light.put("han", "1_15"); light.put("kessel", "1_126");
        var dark = new HashMap<String, String>();
        dark.put("card", "15_" + id);
        var scn = new VirtualTableScenario(light, dark);
        var effect = scn.GetLSCard("effect");
        var card = scn.GetDSCard("card");
        scn.StartGame();
        if (id == 266) {
            scn.MoveLocationToTable(scn.GetLSCard("kessel"));
            scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("han"));
            scn.AttachCardsTo(scn.GetLSCard("kessel"), effect);
            effect.setTargetedCard(com.gempukku.swccgo.common.TargetId.UTINNI_EFFECT_TARGET_1, null, scn.GetLSCard("han"), com.gempukku.swccgo.filters.Filters.any);
        } else scn.MoveCardsToLSSideOfTable(effect);
        scn.MoveCardsToDSHand(card);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPlayCard(card);
        scn.DSChooseCard(effect);
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(effect));
        assertTrue(scn.GetDSUsedPile().contains(card));
    }
    @Test public void OotaGootaSoloCancelsKesselRunAndIsUsed() { cancelEffect(266, "1_52"); }
    @Test public void ShutHimUpOrShutHimDownAndVoyeurCancelsBactaTankAsUsed() { cancelEffect(280, "3_32"); }

    @Test public void PutAllSectionsOnAlertAndFullScaleAlertCancelsOnlyRebelAtDeathStarSite() {
        var light = new HashMap<String, String>();
        light.put("luke", "1_19"); light.put("obi", "1_21");
        var dark = new HashMap<String, String>();
        dark.put("card", "15_275"); dark.put("site", "1_285");
        var scn = new VirtualTableScenario(light, dark);
        var card = scn.GetDSCard("card"); var site = scn.GetDSCard("site");
        var luke = scn.GetLSCard("luke"); var obi = scn.GetLSCard("obi");
        scn.StartGame(); scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), obi);
        scn.MoveCardsToDSHand(card); scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPlayCard(card, "Target a Rebel");
        scn.DSChooseCard(luke); scn.PassAllResponses();
        assertEquals(0, scn.GetBattleDestinyAbility(luke));
        assertEquals(6, scn.GetBattleDestinyAbility(obi));
        assertTrue(scn.GetDSUsedPile().contains(card));
    }

    @Test public void TheCircleIsNowCompleteAndVadersObsessionDuelsObiWanAndNotLuke() {
        var light = new HashMap<String, String>();
        light.put("obi", "1_21"); light.put("luke", "1_19");
        var dark = new HashMap<String, String>();
        dark.put("card", "15_290"); dark.put("vader", "1_168");
        var scn = new VirtualTableScenario(light, dark);
        var card = scn.GetDSCard("card"); var vader = scn.GetDSCard("vader");
        var obi = scn.GetLSCard("obi"); var luke = scn.GetLSCard("luke");
        scn.StartGame(); scn.MoveCardsToDSHand(card);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader, luke, obi);
        scn.SkipToDSTurn(Phase.CONTROL); scn.EnsureDSForcePile(3);

        int force = scn.GetDSForcePileCount();
        scn.DSPlayCard(card);
        scn.DSChooseCard(obi); scn.DSChooseCard(vader); scn.PassAllResponses();
        assertEquals(com.gempukku.swccgo.common.Zone.OUT_OF_PLAY, obi.getZone());
        assertFalse(scn.GetLSLostPile().contains(obi));
        assertEquals(force - 1, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSLostPile().contains(card));
        assertNotEquals(com.gempukku.swccgo.common.Zone.OUT_OF_PLAY, luke.getZone());
    }

    @Test public void TrooperAssaultAndTacticalSupportLosesOneForceThenUploadsTrooperAsLost() {
        var scn = scenario(297, "1_194");
        var card = scn.GetDSCard("card"); var target = scn.GetDSCard("target");
        scn.StartGame(); scn.MoveCardsToDSHand(card); scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(3); scn.MoveCardsToTopOfDSReserveDeck(target);
        var payment = scn.GetTopOfDSForcePile();
        scn.DSPlayCard(card, "Take troopers into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.DSChooseCard(payment); scn.PassAllResponses();
        selectFromReserve(scn, target.getBlueprintId(true), 1);
        scn.DSPass(); scn.PassAllResponses();
        assertTrue(scn.GetDSHand().contains(target));
        assertTrue(scn.GetDSLostPile().contains(payment));
        assertTrue(scn.GetDSLostPile().contains(card));
    }

    @Test public void WattosChanceCubeAndYouSwindledMeLetsOpponentChooseActivationAndIsLost() {
        var scn = scenario(302, "1_192"); var card = scn.GetDSCard("card");
        scn.StartGame(); scn.MoveCardsToDSHand(card); scn.SkipToDSTurn(Phase.CONTROL);
        int darkForce = scn.GetDSForcePileCount(); int lightForce = scn.GetLSForcePileCount();
        scn.DSPlayCard(card, "Make opponent choose"); scn.PassAllResponses();
        scn.LSChooseOption("Activate up to 2 Force");
        scn.PlayerDecided(com.gempukku.swccgo.framework.TestBase.LS, "2");
        scn.PlayerDecided(com.gempukku.swccgo.framework.TestBase.DS, "4");
        scn.PassAllResponses();
        assertEquals(lightForce + 2, scn.GetLSForcePileCount());
        assertEquals(darkForce + 4, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSLostPile().contains(card));
    }

    @Test public void TradeFederationTacticsAndWereHitArtooAddsFlagshipArmorToBattlePowerAsLost() {
        var light = new HashMap<String, String>(); light.put("ship", "1_147");
        var dark = new HashMap<String, String>();
        dark.put("card", "15_294"); dark.put("ship", "14_114"); dark.put("system", "1_288");
        var scn = new VirtualTableScenario(light, dark);
        var card = scn.GetDSCard("card"); var ship = scn.GetDSCard("ship");
        var system = scn.GetDSCard("system");
        scn.StartGame(); scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, ship, scn.GetLSCard("ship"));
        scn.MoveCardsToDSHand(card); scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureDSForcePile(3);
        scn.DSInitiateBattle(system); scn.PassAllResponses();
        scn.DSPlayCard(card, "Add to total power");
        scn.DSChooseCard(ship); scn.PassAllResponses();
        scn.SkipToEndOfPowerSegment(false);
        assertEquals(13, scn.GetDSTotalPower());
        assertTrue(scn.GetDSLostPile().contains(card));
    }
    @Test public void SomethingHitUsAndWhyDidntYouTellMeAddsOneAttritionForOneLostForce() {
        var light = new HashMap<String, String>(); light.put("luke", "1_19");
        var dark = new HashMap<String, String>(); dark.put("card", "15_282"); dark.put("vader", "1_168");
        var scn = new VirtualTableScenario(light, dark); var card = scn.GetDSCard("card");
        scn.StartGame(); scn.MoveCardsToDSHand(card);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"), scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureDSForcePile(3);
        scn.PrepareDSDestiny(0); scn.PrepareLSDestiny(0);
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        scn.SkipToEndOfPowerSegment(true);
        if (scn.LSGetDecision() != null) scn.LSPass();
        var payment = scn.GetTopOfDSForcePile();
        int attritionBefore = scn.GetUnpaidLSAttrition();
        scn.DSPlayCard(card, "Add 1 to attrition against opponent");
        scn.PassAllResponses();
        scn.DSChooseCard(payment); scn.PassAllResponses();
        assertEquals(attritionBefore + 1, scn.GetUnpaidLSAttrition());
        assertTrue(scn.GetDSLostPile().contains(payment));
        assertTrue(scn.GetDSLostPile().contains(card));
    }
    @Test public void PodracerCollisionAndHitRacerReturnsRandomRaceDestinyForOneLostForce() {
        var dark = new HashMap<String, String>();
        dark.put("card", "15_272"); dark.put("arena", "11_94");
        dark.put("podracer", "11_98"); dark.put("boonta", "11_79");
        var scn = new VirtualTableScenario(new HashMap<>(), dark);
        var card = scn.GetDSCard("card"); var arena = scn.GetDSCard("arena");
        var racer = scn.GetDSCard("podracer"); var boonta = scn.GetDSCard("boonta");
        var destiny = scn.GetDSFiller(1);
        scn.StartGame(); scn.MoveCardsToDSHand(card, arena, boonta);
        scn.SkipToDSTurn(Phase.DEPLOY); scn.DSDeployLocation(arena); scn.PassAllResponses(); scn.LSPass();
        scn.MoveCardsToLocation(arena, racer);
        scn.StackCardsOn(racer, destiny);
        destiny.setRaceDestinyForPlayer(com.gempukku.swccgo.framework.TestBase.DS);
        assertFalse(scn.DSCardPlayAvailable(card, "Return a race destiny to hand"));
        scn.DSDeployCardAndPassResponses(boonta, arena); scn.LSPass();
        scn.DSUseCardAction(boonta); scn.PassAllResponses(); scn.LSPass();
        scn.EnsureDSForcePile(2); var payment = scn.GetTopOfDSForcePile();
        scn.DSPlayCard(card, "Return a race destiny to hand");
        scn.DSChooseCard(racer);
        scn.PassAllResponses();
        scn.DSChooseCard(payment); scn.PassAllResponses();
        assertTrue(scn.GetDSHand().contains(destiny));
        assertFalse(racer.getCardsStacked().contains(destiny));
        assertTrue(scn.GetDSLostPile().contains(payment));
        assertTrue(scn.GetDSLostPile().contains(card));
    }

    @Test public void MasterDestroyersAndRollingRollingRollingLostSearchUploadsThreeAndLosesOneForce() {
        var dark = new HashMap<String, String>(); dark.put("card", "15_263");
        dark.put("one", "12_104"); dark.put("two", "12_104"); dark.put("three", "12_104");
        var scn = new VirtualTableScenario(new HashMap<>(), dark);
        var card = scn.GetDSCard("card"); var one = scn.GetDSCard("one");
        var two = scn.GetDSCard("two"); var three = scn.GetDSCard("three");
        scn.StartGame(); scn.MoveCardsToDSHand(card); scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(3); scn.MoveCardsToTopOfDSReserveDeck(one, two, three);
        var payment = scn.GetTopOfDSForcePile();
        scn.DSPlayCard(card, "Take cards into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.DSChooseCard(payment); scn.PassAllResponses();
        selectFromReserve(scn, one.getBlueprintId(true), 3); scn.PassAllResponses();
        assertTrue(scn.GetDSHand().containsAll(java.util.List.of(one, two, three)));
        assertTrue(scn.GetDSLostPile().contains(payment));
        assertTrue(scn.GetDSLostPile().contains(card));
    }

    @Test public void TrampleAndLightsaberDeficiencyCanTargetOwnCharacterUnlikeOriginalTrample() {
        var dark = new HashMap<String, String>(); dark.put("card", "15_295");
        dark.put("original", "3_138"); dark.put("walker", "3_156");
        var scn = new VirtualTableScenario(new HashMap<>(), dark);
        var card = scn.GetDSCard("card"); var original = scn.GetDSCard("original");
        var victim = scn.GetDSFiller(1);
        scn.StartGame(); scn.MoveCardsToDSHand(card, original);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), victim, scn.GetDSCard("walker"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.PrepareDSDestiny(2);
        assertFalse(scn.DSCardPlayAvailable(original));
        assertTrue(scn.DSCardPlayAvailable(card));
        scn.DSPlayCard(card); scn.DSChooseCard(victim); scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(victim));
        assertTrue(scn.GetDSUsedPile().contains(card));
    }

    @Test public void TradeFederationTacticsAndWereHitArtooPreservesCanonicalTitleMatching() {
        var scn = scenario(294, "14_109"); var card = scn.GetDSCard("card");
        assertEquals("Trade Federation Tactics & We're Hit Artoo", card.getBlueprint().getTitle());
        assertTrue(com.gempukku.swccgo.filters.Filters.title("We're Hit, Artoo").accepts(scn.game(), card));
        assertTrue(com.gempukku.swccgo.filters.Filters.title("Trade Federation Tactics").accepts(scn.game(), card));
    }

    @Test public void StartYourEnginesAndPitCrewsDoesNotInheritOriginalPeekAtThreeMode() {
        var scn = scenario(285, "11_87");
        var card = scn.GetDSCard("card"); var original = scn.GetDSCard("target");
        scn.MoveCardsToDSHand(card, original); scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardPlayAvailable(original, "Peek at top 3 cards of Reserve Deck"));
        assertFalse(scn.DSCardPlayAvailable(card, "Peek at top 3 cards of Reserve Deck"));
    }
    @Test public void PointManAndSlipSlidingAwayDoesNotInheritWeatherVaneKillMode() {
        var dark = new HashMap<String, String>(); dark.put("card", "15_273");
        dark.put("original", "5_154"); dark.put("vane", "5_127");
        var light = new HashMap<String, String>(); light.put("luke", "1_19");
        var scn = new VirtualTableScenario(light, dark);
        var card = scn.GetDSCard("card"); var original = scn.GetDSCard("original");
        var vane = scn.GetDSCard("vane");
        scn.StartGame(); scn.MoveCardsToDSHand(card, original);
        scn.MoveCardsToDSSideOfTable(vane); scn.AttachCardsTo(vane, scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.EnsureDSForcePile(3);
        assertTrue(scn.DSCardPlayAvailable(original, "Make character on Weather Vane lost"));
        assertFalse(scn.DSCardPlayAvailable(card, "Make character on Weather Vane lost"));
    }

    @Test public void WeMustAccelerateOurPlansAndVoteNowPoliticsModeDrawsAtMostFourCardsAsUsed() {
        var dark = new HashMap<String, String>(); dark.put("card", "15_304");
        dark.put("senate", "12_167"); dark.put("lott", "12_110"); dark.put("senator", "12_105");
        var scn = new VirtualTableScenario(new HashMap<>(), dark);
        var card = scn.GetDSCard("card"); var senate = scn.GetDSCard("senate");
        scn.StartGame(); scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(senate, scn.GetDSCard("lott"), scn.GetDSCard("senator"));
        scn.MoveCardsToDSHand(card); scn.SkipToDSTurn(Phase.CONTROL);
        for (int i = 1; i <= 10; i++) scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSFiller(i));
        scn.PrepareDSDestiny(3);
        int hand = scn.GetDSHandCount();
        scn.DSPlayCard(card, "Draw destiny to draw cards"); scn.PassAllResponses();
        assertEquals(4, scn.DSGetChoiceMax());
        scn.PlayerDecided(com.gempukku.swccgo.framework.TestBase.DS, "4"); scn.PassAllResponses();
        assertEquals(hand - 1 + 4, scn.GetDSHandCount());
        assertTrue(scn.GetDSUsedPile().contains(card));
    }

    @Test public void ReleaseYourAngerAndUnexpectedInterruptionRestoresImmunityAfterBattleWithinSameTurn() {
        var light = new HashMap<String, String>(); light.put("obi", "1_21");
        var dark = new HashMap<String, String>(); dark.put("card", "15_276");
        dark.put("vader", "1_168"); dark.put("saber", "1_324");
        var scn = new VirtualTableScenario(light, dark);
        var card = scn.GetDSCard("card"); var obi = scn.GetLSCard("obi");
        scn.StartGame(); scn.MoveCardsToDSHand(card);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), obi, scn.GetDSCard("vader"));
        scn.AttachCardsTo(scn.GetDSCard("vader"), scn.GetDSCard("saber"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(3);
        float immunity = scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), obi);
        assertTrue(immunity > 0);
        scn.LSInitiateBattle(scn.GetDSStartingLocation()); scn.LSPass();
        scn.DSPlayCard(card, "Make character lose immunity to attrition");
        scn.DSChooseCard(obi); scn.PassAllResponses();
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), obi), scn.epsilon);
        scn.SkipToDamageSegment(false);
        scn.PassDamageSegmentActions();
        scn.LSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses();
        scn.SkipToPhase(Phase.MOVE);
        assertEquals(immunity, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), obi), scn.epsilon);
        assertTrue(scn.GetDSLostPile().contains(card));
    }
    @Test public void SurpriseAndEChuTaInsultsCharacterAsUsedAndCancelsGameText() {
        var light = new HashMap<String, String>(); light.put("obi", "1_21");
        var dark = new HashMap<String, String>(); dark.put("card", "15_286"); dark.put("droid", "4_91");
        var scn = new VirtualTableScenario(light, dark);
        var card = scn.GetDSCard("card"); var droid = scn.GetDSCard("droid"); var obi = scn.GetLSCard("obi");
        scn.StartGame(); scn.MoveCardsToDSHand(card);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), droid, obi);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPlayCard(card, "'Insult' a character"); scn.DSChooseCard(droid); scn.DSChooseCard(obi);
        scn.PassAllResponses();
        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), obi));
        assertTrue(scn.GetDSUsedPile().contains(card));
    }
    @Test public void SurpriseAndEChuTaInitiatesDroidOnlyBattleAndAddsDestinyForBothPlayers() {
        var light = new HashMap<String, String>(); light.put("droid", "1_5");
        var dark = new HashMap<String, String>(); dark.put("card", "15_286"); dark.put("droid", "4_91");
        var scn = new VirtualTableScenario(light, dark); var card = scn.GetDSCard("card");
        var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToDSHand(card);
        scn.MoveCardsToLocation(site, scn.GetDSCard("droid"), scn.GetLSCard("droid"));
        scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureDSForcePile(3);
        scn.DSPlayCard(card, "Initiate battle"); scn.DSChooseCard(site); scn.PassAllResponses();
        assertNotNull(scn.gameState().getBattleState());
        assertEquals(1, scn.GetDSBattleDestinyCount()); assertEquals(1, scn.GetLSBattleDestinyCount());
        scn.SkipToDamageSegment(false); scn.PassDamageSegmentActions();
        if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayRemainingBattleDamageFromReserveDeck();
        if (scn.AwaitingDSBattleDamagePayment()) scn.DSPayRemainingBattleDamageFromReserveDeck();
        scn.PassAllResponses(); scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.GetDSUsedPile().contains(card));
    }
}
