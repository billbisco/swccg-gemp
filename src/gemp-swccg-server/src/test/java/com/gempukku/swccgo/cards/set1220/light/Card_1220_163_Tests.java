package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.BlowAwayEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1220_163_Tests {
    private VirtualTableScenario fixture(boolean chooseOptional) {
        var ls = new HashMap<>(Map.ofEntries(
                Map.entry("original", "3_61"), Map.entry("trench", "3_63"),
                Map.entry("perimeter", "3_56"), Map.entry("ridge", "3_62"),
                Map.entry("echo", "3_59"), Map.entry("sentry", "7_64"),
                Map.entry("outside", "5_79"), Map.entry("rendezvous", "7_123"),
                Map.entry("rebel", "3_6"), Map.entry("secondRebel", "3_6"),
                Map.entry("thirdRebel", "3_6"), Map.entry("unique", "1_17"),
                Map.entry("speeder", "3_69"), Map.entry("secondSpeeder", "3_69"),
                Map.entry("lsArtillery", "3_72"), Map.entry("take", "2_18")));
        var ds = new HashMap<>(Map.of("dsArtillery", "3_159", "vader", "1_168", "sense", "1_254"));
        return new VirtualTableScenario(ls, ds, 40, 40, new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                return new HashMap<>(Map.of("objective", "1220_163", "hoth", "3_55",
                        "generators", "1261_112", "garrison", "111_3", "flaps", "7_69"));
            }

            @Override public void Setup(VirtualTableScenario scn) {
                for (String card : List.of("hoth", "generators")) {
                    assertTrue("Required setup " + card + " at " + scn.GetCurrentDecision().getText()
                                    + " options=" + java.util.Arrays.toString(scn.GetADParam(scn.LS, "blueprintId"))
                                    + " results=" + java.util.Arrays.toString(scn.GetADParam(scn.LS, "results")),
                            scn.LSHasCardChoiceAvailable(scn.GetLSCard(card)));
                    scn.LSChooseCard(scn.GetLSCard(card));
                }
                for (String card : List.of("garrison", "flaps")) {
                    if (scn.LSHasCardChoiceAvailable(scn.GetLSCard(card))) {
                        if (chooseOptional) scn.LSChooseCard(scn.GetLSCard(card)); else scn.LSDecided("");
                    }
                }
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

    @Test public void generatorsUseTheFinalLocationPrinting() {
        var scn = fixture(false); var b = scn.GetLSCard("generators").getBlueprint();
        assertEquals(Title.Main_Power_Generators, b.getTitle());
        assertEquals(Title.Hoth, b.getSystemName());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        assertEquals(CardSubtype.SITE, b.getCardSubtype()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(Rarity.V, b.getRarity()); assertEquals(Side.LIGHT, b.getSide()); assertTrue(b.hasVirtualSuffix());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.LOCATION)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.LIGHT_FORCE, Icon.HOTH, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.MARKER_1)));
    }

    @Test public void actualSetupDeploysHothAndOnlyBlockSixGeneratorsAndAllowsBothEffects() {
        var scn = fixture(true);
        scn.StartGame();
        for (String name : List.of("objective", "hoth", "generators", "garrison", "flaps")) {
            assertTrue(name, scn.IsCardActive(scn.GetLSCard(name)));
        }
        assertFalse(scn.IsCardActive(scn.GetLSCard("original")));
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
        assertTrue(scn.LSHasCardChoiceAvailable(trench));
        assertFalse(scn.LSHasCardChoiceAvailable(echo));
        assertFalse(scn.LSHasCardChoiceAvailable(outside));
        scn.LSChooseCard(trench); scn.PassAllResponses();
        assertTrue(scn.IsCardActive(trench));
        if (scn.DSGetDecision() != null) scn.DSPass();
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Deploy a non-unique Rebel")));
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
        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSUseCardAction(objective, "Flip");
        scn.PassAllResponses();
        assertEquals(Title.Liberation, objective.getBlueprint().getTitle());
    }

    @Test public void liberationNarrowsTheLiveHothShieldAndEitherSidesHothArtilleryIsPowered() {
        var scn = start(); var objective = scn.GetLSCard("objective"); var generators = scn.GetLSCard("generators");
        var trench = scn.GetLSCard("trench"); var echo = scn.GetLSCard("echo"); var ridge = scn.GetLSCard("ridge");
        for (var location : List.of(trench, echo, ridge)) scn.MoveLocationToTable(location);
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
}
