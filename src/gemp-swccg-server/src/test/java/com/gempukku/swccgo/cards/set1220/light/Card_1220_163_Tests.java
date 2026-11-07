package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.BlowAwayEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1220_163_Tests {
    private VirtualTableScenario fixture(boolean chooseOptional) {
        return fixture(chooseOptional, chooseOptional);
    }

    private VirtualTableScenario fixture(boolean chooseGarrison, boolean chooseFlaps) {
        var ls = new HashMap<>(Map.ofEntries(
                Map.entry("original", "3_61"), Map.entry("trench", "3_63"),
                Map.entry("perimeter", "3_56"), Map.entry("ridge", "3_62"),
                Map.entry("echo", "3_57"), Map.entry("sentry", "7_64"),
                Map.entry("outside", "5_79"), Map.entry("rendezvous", "7_123"),
                Map.entry("rebel", "3_6"), Map.entry("secondRebel", "3_6"),
                Map.entry("thirdRebel", "3_6"), Map.entry("unique", "1_17"),
                Map.entry("speeder", "3_69"), Map.entry("secondSpeeder", "3_69"),
                Map.entry("lsArtillery", "3_72"), Map.entry("take", "2_18")));
        ls.put("formation", "7_107");
        ls.put("nonBattleground", "1_134");
        ls.put("secondFormation", "7_107");
        ls.put("wedge", "205_1");
        ls.put("wedgeSpeeder", "3_69");
        var ds = new HashMap<>(Map.of("dsArtillery", "3_159", "vader", "1_168", "sense", "1_267",
                "garrisonOne", "104_7", "garrisonTwo", "104_7", "cantina", "1_290"));
        return new VirtualTableScenario(ls, ds, 40, 40, new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                return new HashMap<>(Map.of("objective", "1220_163", "hoth", "3_55",
                        "generators", "1261_112", "garrison", "111_3", "flaps", "7_69"));
            }

            @Override public void Setup(VirtualTableScenario scn) {
                var trace = new ArrayList<String>();
                for (int i = 0; i < 20 && scn.GetCurrentDecision() != null; i++) {
                    var prompt = scn.GetCurrentDecision().getText();
                    trace.add(prompt);
                    if (prompt.startsWith("On which side of")) {
                        scn.LSChooseOption("Left");
                    } else if (prompt.startsWith("Verify ")) {
                        scn.PlayerPass(scn.GetDecidingPlayer());
                    } else if (prompt.contains("Choose Hoth system to deploy")) {
                        scn.LSChooseCard(scn.GetLSCard("hoth"));
                    } else if (prompt.contains("Choose Main Power Generators to deploy")) {
                        int original = scn.LSGetBPChoices().indexOf(scn.GetLSCard("original").getBlueprintId(true));
                        assertTrue("Stock generators were selectable", original < 0
                                || !Boolean.parseBoolean(scn.LSGetADParamAsList("selectable").get(original)));
                        scn.LSChooseCard(scn.GetLSCard("generators"));
                    } else if (prompt.equals("Deploy Echo Base Garrison?")) {
                        if (chooseGarrison) scn.LSChooseYes(); else scn.LSChooseNo();
                    } else if (prompt.equals("Deploy Maneuvering Flaps?")) {
                        if (chooseFlaps) scn.LSChooseYes(); else scn.LSChooseNo();
                    } else if (prompt.contains("Choose Echo Base Garrison to deploy")) {
                        if (chooseGarrison) scn.LSChooseCard(scn.GetLSCard("garrison")); else scn.LSPass();
                    } else if (prompt.contains("Choose Maneuvering Flaps to deploy")) {
                        if (chooseFlaps) scn.LSChooseCard(scn.GetLSCard("flaps")); else scn.LSPass();
                    } else if (prompt.toLowerCase().contains("optional response")) {
                        scn.PlayerPass(scn.GetDecidingPlayer());
                    } else {
                        break;
                    }
                }
                assertEquals(trace.toString(), Zone.LOCATIONS, scn.GetLSCard("hoth").getZone());
                assertEquals(trace.toString(), Zone.LOCATIONS, scn.GetLSCard("generators").getZone());
            }
        }, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
    }

    private VirtualTableScenario start() {
        var scn = fixture(false);
        scn.StartGame();
        assertTrue(scn.IsCardActive(scn.GetLSCard("objective")));
        return scn;
    }

    @Test public void bothObjectiveFacesMatchTheFinalPrinting() {
        var scn = fixture(false);
        var card = scn.GetLSCard("objective");
        for (int face = 0; face < 2; face++) {
            var b = card.getBlueprint();
            assertEquals(face == 0 ? "Local Uprising" : "Liberation", b.getTitle());
            assertEquals(face == 0 ? 0 : 7, b.getDestiny(), scn.epsilon);
            assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, b.getExpansionSet());
            assertEquals(Rarity.V, b.getRarity()); assertEquals(Side.LIGHT, b.getSide());
            assertTrue(b.hasVirtualSuffix());
            scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.OBJECTIVE)));
            scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.OBJECTIVE, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_2)));
            scn.BlueprintKeywordCheck(b, new ArrayList<>());
            if (face == 0) {
                scn.MoveCardsToLSSideOfTable(card);
                scn.gameState().flipCard(scn.game(), card, true);
            }
        }
    }

    @Test public void actualSetupDeploysHothAndOnlyBlockSixGeneratorsAndAllowsBothEffects() {
        var scn = fixture(true);
        scn.StartGame();
        for (String name : List.of("objective", "hoth", "generators", "garrison", "flaps")) {
            assertTrue(name, scn.IsCardActive(scn.GetLSCard(name)));
        }
        assertFalse(scn.IsCardActive(scn.GetLSCard("original")));
    }

    @Test public void actualSetupMayTakeOnlyGarrisonAndDeclineFlaps() {
        var scn = fixture(true, false); scn.StartGame();
        assertTrue(scn.IsCardActive(scn.GetLSCard("garrison")));
        assertFalse(scn.IsCardActive(scn.GetLSCard("flaps")));
    }

    @Test public void actualSetupMayDeclineGarrisonAndTakeOnlyFlaps() {
        var scn = fixture(false, true); scn.StartGame();
        assertFalse(scn.IsCardActive(scn.GetLSCard("garrison")));
        assertTrue(scn.IsCardActive(scn.GetLSCard("flaps")));
    }

    @Test public void actualSetupMayDeclineBothEffects() {
        var scn = start();
        assertTrue(scn.IsCardActive(scn.GetLSCard("hoth")));
        assertTrue(scn.IsCardActive(scn.GetLSCard("generators")));
        assertFalse(scn.IsCardActive(scn.GetLSCard("garrison")));
        assertFalse(scn.IsCardActive(scn.GetLSCard("flaps")));
    }

    @Test public void setupRestrictionsSurviveTheObjectiveLeavingPlayAndAllowRendezvousPoint() {
        var scn = start();
        var hoth = scn.GetLSCard("generators");
        var sentry = scn.GetLSCard("sentry"); var outside = scn.GetLSCard("outside");
        var rendezvous = scn.GetLSCard("rendezvous"); var trench = scn.GetLSCard("trench");
        scn.MoveCardsToLocation(hoth, scn.GetLSCard("rebel"));
        scn.MoveOutOfPlay(scn.GetLSCard("objective"));
        scn.MoveCardsToLSHand(sentry, outside, rendezvous, trench);
        scn.SkipToLSTurn(Phase.DEPLOY); scn.EnsureLSForcePile(10);
        assertFalse(scn.LSCardPlayAvailable(sentry));
        assertFalse(scn.LSCardPlayAvailable(outside));
        assertTrue(scn.LSCardPlayAvailable(rendezvous));
        assertTrue(scn.LSCardPlayAvailable(trench));
    }

    @Test public void theTwoDeployChoicesShareOneUseAndSiteSearchIsExteriorHothOnly() {
        var scn = start(); var objective = scn.GetLSCard("objective");
        var trench = scn.GetLSCard("trench"); var echo = scn.GetLSCard("echo"); var outside = scn.GetLSCard("outside");
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(trench, echo, outside);
        assertTrue(scn.LSCardActionAvailable(objective, "Deploy a non-unique Rebel"));
        scn.LSUseCardAction(objective, "Deploy an exterior Hoth site");
        assertTrue(selectable(scn, trench.getBlueprintId(true)));
        assertFalse(selectable(scn, echo.getBlueprintId(true)));
        assertFalse(selectable(scn, outside.getBlueprintId(true)));
        scn.LSChooseCard(trench);
        if (scn.LSDecisionAvailable("On which side of")) scn.LSChooseOption("Left");
        scn.PassAllResponses();
        assertTrue(scn.IsCardActive(trench));
        if (scn.DSGetDecision() != null) scn.DSPass();
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Deploy a non-unique Rebel")));
    }

    private boolean selectable(VirtualTableScenario scn, String blueprint) {
        int index = scn.LSGetBPChoices().indexOf(blueprint);
        return index >= 0 && Boolean.parseBoolean(scn.LSGetADParamAsList("selectable").get(index));
    }

    @Test public void moveFlipNeedsThreeExteriorSitesWithQualifyingCards() {
        var scn = start();
        var objective = scn.GetLSCard("objective"); var trench = scn.GetLSCard("trench");
        var perimeter = scn.GetLSCard("perimeter"); var generators = scn.GetLSCard("generators");
        scn.MoveLocationToTable(trench); scn.MoveLocationToTable(perimeter);
        scn.MoveCardsToLocation(generators, scn.GetLSCard("rebel"));
        scn.MoveCardsToLocation(trench, scn.GetLSCard("secondRebel"));
        scn.MoveCardsToLocation(perimeter, scn.GetLSCard("unique"));
        scn.SkipToLSTurn(Phase.MOVE);
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Flip")));
        scn.MoveCardsToLocation(perimeter, scn.GetLSCard("speeder"));
        scn.SkipToPhase(Phase.DRAW);
        assertEquals(Title.Liberation, objective.getBlueprint().getTitle());
    }

    @Test public void liberationNarrowsTheLiveHothShieldAndEitherSidesHothArtilleryIsPowered() {
        var scn = start(); var objective = scn.GetLSCard("objective"); var generators = scn.GetLSCard("generators");
        var trench = scn.GetLSCard("trench"); var echo = scn.GetLSCard("echo"); var ridge = scn.GetLSCard("ridge");
        for (var location : List.of(trench, echo, ridge)) if (location.getZone() != Zone.LOCATIONS) scn.MoveLocationToTable(location);
        var ls = scn.GetLSCard("lsArtillery"); var ds = scn.GetDSCard("dsArtillery");
        scn.MoveCardsToLocation(trench, ls, ds);
        var query = scn.game().getModifiersQuerying(); var state = scn.gameState();
        assertTrue(query.isLocationUnderHothEnergyShield(state, trench));
        assertTrue(query.isLocationUnderHothEnergyShield(state, generators));
        assertTrue(query.isLocationUnderHothEnergyShield(state, echo));
        assertFalse(query.isLocationUnderHothEnergyShield(state, ridge));
        assertTrue(query.isPowered(state, ls)); assertTrue(query.isPowered(state, ds));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), ds);
        assertFalse(query.isPowered(state, ds));
        scn.gameState().flipCard(scn.game(), objective, true);
        assertTrue(query.isLocationUnderHothEnergyShield(state, generators));
        assertTrue(query.isLocationUnderHothEnergyShield(state, echo));
        assertFalse(query.isLocationUnderHothEnergyShield(state, trench));
    }

    @Test public void actualFormationIsSenseImmuneAndNormalLostOnlyOncePerGame() {
        var scn = start(); var objective = scn.GetLSCard("objective");
        var first = scn.GetLSCard("formation"); var second = scn.GetLSCard("secondFormation");
        var garrison = scn.GetDSCard("garrisonOne"); var later = scn.GetDSCard("garrisonTwo");
        scn.MoveCardsToDSSideOfTable(garrison);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(scn.GetDSCard("sense")); scn.MoveCardsToLSHand(first, second);
        scn.SkipToLSTurn(Phase.CONTROL); scn.EnsureDSForcePile(5);
        assertTrue(scn.LSCardPlayAvailable(first, "Cancel Walker Garrison"));
        scn.LSPlayCard(first, "Cancel Walker Garrison");
        if (scn.LSHasCardChoiceAvailable(garrison)) scn.LSChooseCard(garrison);
        assertFalse(scn.GetDSAvailableActions().stream().anyMatch(x -> x.contains("Draw destiny to cancel")));
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(first));
        assertTrue(scn.GetDSLostPile().contains(garrison));
        scn.MoveOutOfPlay(objective);
        scn.MoveCardsToDSSideOfTable(later);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.IsCardActive(later));
        assertFalse(scn.LSCardPlayAvailable(second, "Cancel Walker Garrison"));
    }

    private VirtualTableScenario liberationWithTwoControlledSites(boolean pair) {
        var scn = start();
        var trench = scn.GetLSCard("trench"); var generators = scn.GetLSCard("generators");
        scn.MoveLocationToTable(trench);
        scn.MoveCardsToLocation(generators, scn.GetLSCard("rebel"), scn.GetLSCard("speeder"));
        scn.MoveCardsToLocation(trench, scn.GetLSCard("secondRebel"));
        if (pair) scn.MoveCardsToLocation(trench, scn.GetLSCard("secondSpeeder"));
        scn.gameState().flipCard(scn.game(), scn.GetLSCard("objective"), true);
        return scn;
    }

    @Test public void liberationCountsOnlyBattlegroundMarkersWithBothCards() {
        var scn = liberationWithTwoControlledSites(true); var objective = scn.GetLSCard("objective");
        scn.SkipToLSTurn(Phase.CONTROL);
        int before = scn.GetDSLostPileCount();
        assertTrue(scn.LSCardActionAvailable(objective, "Make opponent lose 1 Force"));
        assertFalse(scn.LSCardActionAvailable(objective, "Make opponent lose 2 Force"));
        scn.LSUseCardAction(objective, "Make opponent lose 1 Force");
        scn.PassAllResponses(); scn.DSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        if (scn.DSGetDecision() != null) scn.DSPass();
        assertEquals(before + 1, scn.GetDSLostPileCount());
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Make opponent lose")));
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(before + 1, scn.GetDSLostPileCount());

        var noPair = liberationWithTwoControlledSites(false);
        noPair.SkipToLSTurn(Phase.CONTROL);
        assertFalse(noPair.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Make opponent lose")));
    }

    @Test public void liberationPaysTwoForAnyCardAndReshuffles() {
        var scn = liberationWithTwoControlledSites(false); var objective = scn.GetLSCard("objective");
        var take = scn.GetLSCard("take"); var wrongForFront = scn.GetLSCard("outside");
        scn.SkipToLSTurn(Phase.CONTROL); scn.EnsureLSForcePile(5);
        scn.MoveCardsToTopOfLSReserveDeck(take, wrongForFront);
        scn.gameState().markCardsRevealedFromPile(List.of(wrongForFront));
        int before = scn.GetLSForcePileCount();
        scn.LSUseCardAction(objective, "Take any card");
        scn.PassForceUseResponses(); scn.PassAllResponses();
        assertTrue(selectable(scn, wrongForFront.getBlueprintId(true)));
        scn.LSChooseCard(take); scn.PassAllResponses();
        assertTrue(scn.GetLSHand().contains(take));
        assertEquals(before - 2, scn.GetLSForcePileCount());
        assertFalse(scn.gameState().isCardRevealedFromPile(wrongForFront));
        if (scn.DSGetDecision() != null) scn.DSPass();
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Take any card")));
    }

    @Test public void blownAwayMakesLightLoseEightAndPlacesTheObjectiveOutOfPlay() {
        var scn = start(); var objective = scn.GetLSCard("objective");
        var generators = scn.GetLSCard("generators"); var source = scn.GetDSStartingLocation();
        scn.SkipToDSTurn(Phase.CONTROL);
        int before = scn.GetPlayerLifeForceRemaining(scn.LS);
        var action = new TopLevelGameTextAction(source, scn.DS, source.getCardId());
        action.appendEffect(new BlowAwayEffect(action, generators));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        scn.PassAllResponses();
        scn.LSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(before - 8, scn.GetPlayerLifeForceRemaining(scn.LS));
        assertEquals(Zone.OUT_OF_PLAY, objective.getZone());
    }
    @Test public void liberationForcesMarkerLossAtEndOfControlIfNotTakenEarlier() {
        var scn = liberationWithTwoControlledSites(true);
        scn.SkipToLSTurn(Phase.CONTROL);
        int before = scn.GetDSLostPileCount();
        for (int i = 0; i < 12 && !scn.DSDecisionAvailable("Choose Force to lose"); i++) {
            var prompt = scn.GetCurrentDecision().getText().toLowerCase();
            if (!prompt.contains("pass") && !prompt.contains("optional response")) break;
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue(scn.GetCurrentDecision().getText(), scn.DSDecisionAvailable("Choose Force to lose"));
        scn.DSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(before + 1, scn.GetDSLostPileCount());
    }

    @Test public void losingControlOfTheSecondExteriorSiteFlipsLiberationBack() {
        var scn = liberationWithTwoControlledSites(false);
        var objective = scn.GetLSCard("objective");
        var lost = scn.GetLSCard("secondRebel");
        var source = scn.GetDSStartingLocation();
        scn.SkipToDSTurn(Phase.CONTROL);
        assertEquals(Title.Liberation, objective.getBlueprint().getTitle());
        var action = new TopLevelGameTextAction(source, scn.DS, source.getCardId());
        action.appendEffect(new LoseCardFromTableEffect(action, lost));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(lost));
        assertEquals(Title.Local_Uprising, objective.getBlueprint().getTitle());
    }

    private void resolveForceLoss(boolean fromOwnCard, int amount, int remaining) {
        var scn = liberationWithTwoControlledSites(false);
        var player = fromOwnCard ? scn.LS : scn.DS;
        var source = fromOwnCard ? scn.GetLSCard("rebel") : scn.GetDSStartingLocation();
        if (fromOwnCard) scn.SkipToLSTurn(Phase.CONTROL); else scn.SkipToDSTurn(Phase.CONTROL);
        int before = scn.GetLSLostPileCount();
        var action = new TopLevelGameTextAction(source, player, source.getCardId());
        action.appendEffect(new LoseForceEffect(action, scn.LS, amount));
        scn.carryOutEffectInPhaseActionByPlayer(player, action);
        scn.PassAllResponses();
        scn.LSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(remaining, scn.GetLSLostPileCount() - before);
    }

    @Test public void liberationReducesOtherForceLossByTwoOccupiedHothLocations() { resolveForceLoss(false, 5, 3); }
    @Test public void liberationCannotReduceOtherForceLossBelowOne() { resolveForceLoss(false, 2, 1); }
    @Test public void liberationDoesNotReduceForceLossFromOwnCard() { resolveForceLoss(true, 5, 5); }
    @Test public void actualRebelDownloadPaysNormalCostAndRequiresNonUniqueRebelAtHoth() {
        var scn = start();
        var objective = scn.GetLSCard("objective"); var rebel = scn.GetLSCard("rebel");
        var unique = scn.GetLSCard("unique"); var generator = scn.GetLSCard("generators");
        var outside = scn.GetDSStartingLocation();
        scn.LSActivateForceCheat(8); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(rebel, unique);
        scn.gameState().markCardsRevealedFromPile(List.of(unique));
        int before = scn.GetLSForcePileCount();
        scn.LSUseCardAction(objective, "Deploy a non-unique Rebel");
        if (scn.LSDecisionAvailable("Choose card to deploy")) {
            assertFalse(selectable(scn, unique.getBlueprintId(true)));
            scn.LSChooseCard(rebel);
        }
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(generator)) {
            assertFalse(scn.LSHasCardChoiceAvailable(outside));
            scn.LSChooseCard(generator);
        }
        scn.PassForceUseResponses(); scn.PassAllResponses();
        assertEquals(generator, rebel.getAtLocation());
        assertEquals(rebel.getBlueprint().getDeployCost().intValue(), before - scn.GetLSForcePileCount());
        assertFalse(scn.gameState().isCardRevealedFromPile(unique));
    }

    private void actualDrain(boolean battleground) {
        var scn = liberationWithTwoControlledSites(false);
        var drainAt = battleground ? scn.GetDSCard("cantina") : scn.GetLSCard("nonBattleground");
        scn.MoveLocationToTable(drainAt);
        scn.MoveCardsToLocation(drainAt, scn.GetDSCard("vader"));
        assertEquals(battleground, Filters.battleground.accepts(scn.game(), drainAt));
        scn.SkipToDSTurn(Phase.CONTROL);
        int before = scn.GetLSLostPileCount();
        scn.DSForceDrainAt(drainAt); scn.PassAllResponses();
        scn.LSPayRemainingForceLossFromReserveDeck(); scn.PassAllResponses();
        assertEquals(battleground ? 2 : 1, scn.GetLSLostPileCount() - before);
    }

    @Test public void actualBattlegroundDrainIsNotReduced() { actualDrain(true); }
    @Test public void actualNonBattlegroundDrainIsReduced() { actualDrain(false); }

    @Test public void actualBattleDamageIsNotReduced() {
        var scn = liberationWithTwoControlledSites(false);
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader"), scn.GetLSCard("unique"));
        scn.DSActivateForceCheat(8); scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(false);
        scn.PassDamageSegmentActions();
        int amount = scn.GetUnpaidLSBattleDamage(); assertTrue(amount > 1);
        int before = scn.GetLSLostPileCount();
        scn.LSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses();
        assertEquals(amount, scn.GetLSLostPileCount() - before);
    }

    /**
     * Wedge (piloting a T-47) battles Vader at the given site while Dark Side draws two battle destinies. Returns
     * whether Wedge offered to cancel the second Dark Side battle destiny.
     */
    private boolean wedgeMayCancelSecondDarkDestiny(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl site) {
        var wedge = scn.GetLSCard("wedge"); var speeder = scn.GetLSCard("wedgeSpeeder"); var vader = scn.GetDSCard("vader");
        scn.MoveCardsToLocation(site, speeder, vader);
        scn.BoardAsPilot(speeder, wedge);
        scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(vader, 1, scn.DS));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.SkipToPowerSegment();
        boolean offered = false;
        var trace = new ArrayList<String>();
        for (int i = 0; i < 60 && scn.GetCurrentDecision() != null; i++) {
            var text = scn.GetCurrentDecision().getText();
            var decider = scn.GetDecidingPlayer();
            trace.add(decider + ":" + text);
            if (text.contains("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER")) break;
            if (text.contains("battle destiny?")) { scn.PlayerChooseYes(decider); continue; }
            if (decider.equals(scn.LS) && scn.LSCardActionAvailable(wedge, "Cancel opponent's destiny")) { offered = true; break; }
            scn.PlayerPass(decider);
        }
        assertTrue(trace.toString(), trace.stream().anyMatch(t -> t.contains("battle destiny?") || t.contains("DESTINY_DRAWN")));
        return offered;
    }

    private VirtualTableScenario liberationWithThreeControlledSites() {
        var scn = liberationWithTwoControlledSites(false);
        var perimeter = scn.GetLSCard("perimeter"); var ridge = scn.GetLSCard("ridge");
        scn.MoveLocationToTable(perimeter); scn.MoveLocationToTable(ridge);
        scn.MoveCardsToLocation(perimeter, scn.GetLSCard("thirdRebel"));
        return scn;
    }

    @Test public void liberationStopsWedgeCancelingBattleDestinyOnHoth() {
        var scn = liberationWithThreeControlledSites();
        assertFalse(wedgeMayCancelSecondDarkDestiny(scn, scn.GetLSCard("ridge")));
        assertEquals(Title.Liberation, scn.GetLSCard("objective").getBlueprint().getTitle());
    }

    @Test public void liberationDoesNotStopWedgeCancelingBattleDestinyAwayFromHoth() {
        var scn = liberationWithThreeControlledSites();
        assertTrue(wedgeMayCancelSecondDarkDestiny(scn, scn.GetDSStartingLocation()));
        assertEquals(Title.Liberation, scn.GetLSCard("objective").getBlueprint().getTitle());
    }

    @Test public void localUprisingDoesNotStopWedgeCancelingBattleDestinyOnHoth() {
        var scn = start();
        var ridge = scn.GetLSCard("ridge");
        scn.MoveLocationToTable(ridge);
        assertEquals("Local Uprising", scn.GetLSCard("objective").getBlueprint().getTitle());
        assertTrue(wedgeMayCancelSecondDarkDestiny(scn, ridge));
    }
}
