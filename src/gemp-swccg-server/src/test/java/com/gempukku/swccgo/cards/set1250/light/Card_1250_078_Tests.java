package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_078_Tests {
    private static StartingSetup objectiveStart(boolean takeSecurity) {
        return new StartingSetup() {
            @Override
            public HashMap<String, String> Cards() {
                return new HashMap<>() {{
                    put("objective", "1250_78");
                    put("coruscant", "12_73");
                    put("prison", "1116_7");
                    put("palpatine", "12_28");
                    put("security", "12_47");
                }};
            }

            @Override
            public void Setup(VirtualTableScenario s) {
                if (s.LSDecisionAvailable("Choose your Objective")) s.LSChooseCard(s.GetLSCard("objective"));
                if (s.LSDecisionAvailable("Choose Coruscant system")) s.LSChooseCard(s.GetLSCard("coruscant"));
                if (s.LSDecisionAvailable("Choose Blockade Flagship: Prison")) s.LSChooseCard(s.GetLSCard("prison"));
                if (s.LSDecisionAvailable("Choose Palpatine to imprison")) s.LSChooseCard(s.GetLSCard("palpatine"));
                if (s.LSDecisionAvailable("Choose Security Control")) {
                    if (takeSecurity) s.LSChooseCard(s.GetLSCard("security"));
                    else s.LSChooseCards();
                }
            }
        };
    }

    private VirtualTableScenario scenario(boolean takeSecurity) {
        return scenario(takeSecurity, true);
    }

    private VirtualTableScenario scenario(boolean takeSecurity, boolean realStartup) {
        var setup = objectiveStart(takeSecurity);
        var outsideDeck = new StartingSetup() {
            @Override public HashMap<String, String> Cards() { return setup.Cards(); }
            @Override public void Setup(VirtualTableScenario s) { }
        };
        var s = new VirtualTableScenario(new HashMap<>() {{
            put("clone", "1116_9"); put("trooper", "1_26"); put("luke", "1_19");
            put("dock", "12_74"); put("plains", "12_79"); put("marker", "1_109");
            put("secondclone", "1116_9"); put("pilot", "1116_8"); put("cantina", "1_128");
        }}, new HashMap<>() {{
            put("trooper", "1_194"); put("vader", "1_168");
            put("emperor", "9_109"); put("lightning", "9_136"); put("monnok", "2_135");
            put("destroyer", "12_104"); put("p59", "12_114");
        }}, 40, 40, realStartup ? setup : StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                realStartup ? StartingSetup.NoLSShields : outsideDeck, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        // The provisional OVS fixture omits the keyword that is native to the Block 5 Prison.
        if (realStartup) s.game().getModifiersEnvironment().addUntilEndOfGameModifier(
                new KeywordModifier(null, Filters.samePermanentCardId(s.GetLSCard("prison")), Keyword.PRISON));
        return s;
    }

    private void assertFace(VirtualTableScenario s, SwccgCardBlueprint bp, String title, int destiny) {
        assertEquals(title, bp.getTitle()); assertEquals(destiny, bp.getDestiny(), s.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet()); assertTrue(bp.hasVirtualSuffix());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.OBJECTIVE)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.OBJECTIVE, Icon.THEED_PALACE, Icon.EPISODE_I, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test
    public void BothFinalFacesAndActualStartingImprisonmentWithSecurityControl() {
        var s = scenario(true); s.StartGame(); var objective = s.GetLSCard("objective");
        var prison = s.GetLSCard("prison"); var palpatine = s.GetLSCard("palpatine");
        assertFace(s, objective.getBlueprint(), Title.We_Have_A_Plan, 0);
        assertFace(s, objective.getOtherSideBlueprint(), Title.They_Will_Be_Lost_And_Confused, 7);
        assertTrue("Startup zones: " + List.of(s.GetLSCard("coruscant").getZone(), prison.getZone(), palpatine.getZone())
                        + "; keyword=" + Filters.prison.accepts(s.game(), prison)
                        + "; persona=" + Filters.Palpatine.accepts(s.game(), palpatine)
                        + "; legal actions=" + palpatine.getBlueprint().getPlayCardActions(s.LS, s.game(), palpatine,
                                objective, true, 0, null, null, DeployAsCaptiveOption.deployAsImprisonedCaptive(),
                                null, null, false, 0, Filters.sameCardId(prison), null).size(),
                objective.isObjectiveDeploymentComplete());
        assertFalse(objective.isFlipped());
        assertEquals(Zone.LOCATIONS, s.GetLSCard("coruscant").getZone()); assertEquals(Zone.LOCATIONS, prison.getZone());
        assertEquals(prison, palpatine.getAttachedTo()); assertTrue(palpatine.isImprisoned());
        assertEquals(Zone.SIDE_OF_TABLE, s.GetLSCard("security").getZone());
        var modifiers = s.game().getModifiersQuerying();
        assertTrue(modifiers.cannotBeConverted(s.gameState(), s.GetLSCard("coruscant")));
        assertTrue(modifiers.mayNotBeTransferred(s.gameState(), palpatine));
    }

    @Test
    public void SecurityControlCanBeDeclinedWithoutInvalidatingStartup() {
        var s = scenario(false); s.StartGame();
        assertTrue(s.GetLSCard("objective").isObjectiveDeploymentComplete());
        assertTrue(s.GetLSReserveDeck().contains(s.GetLSCard("security")));
        assertTrue(s.GetLSCard("palpatine").isImprisoned());
    }

    @Test
    public void CoruscantBattlegroundSiteDownloadPlacesItAndUsesOnlyOneSearch() {
        var s = scenario(false); s.StartGame(); var objective = s.GetLSCard("objective");
        var dock = s.GetLSCard("dock"); var plains = s.GetLSCard("plains");
        var text = "Deploy a battleground site to Coruscant";
        s.SkipToLSTurn(Phase.DEPLOY); s.MoveCardsToTopOfLSReserveDeck(dock, plains);
        assertTrue(s.LSCardActionAvailable(objective, text));
        s.LSUseCardAction(objective, text); s.PassAllResponses();
        if (s.LSHasCardChoiceAvailable(dock)) s.LSChooseCard(dock);
        if (s.LSDecisionAvailable("Choose a location to deploy")) s.LSChooseCard(s.GetLSCard("coruscant"));
        s.PassAllResponses();
        assertEquals(Zone.LOCATIONS, dock.getZone());
        assertEquals(Title.Coruscant, dock.getBlueprint().getSystemName());
        var modifiers = s.game().getModifiersQuerying();
        assertEquals(2, modifiers.getForceGenerationFromLocation(s.gameState(), dock, s.LS), s.epsilon);
        assertTrue(s.GetLSReserveDeck().contains(plains));
        if (s.AwaitingDSDeployPhaseActions()) s.DSPass();
        assertTrue(s.AwaitingLSDeployPhaseActions());
        assertFalse(s.LSCardActionAvailable(objective, text));
    }

    private VirtualTableScenario flipAndSearch() {
        var s = scenario(false, false); s.StartGame(); var objective = s.GetLSCard("objective");
        // Starting imprisonment is verified separately. Here the printed flip and search run normally.
        s.MoveCardsToLSSideOfTable(objective); objective.setObjectiveDeploymentComplete(true);
        var dock = s.GetLSCard("dock"); var palpatine = s.GetLSCard("palpatine");
        var clone = s.GetLSCard("clone"); var marker = s.GetLSCard("marker"); var trooper = s.GetLSCard("trooper");
        s.MoveLocationToTable(dock);
        s.MoveCardsToHand(trooper); s.LSActivateForceCheat(8);
        s.SkipToLSTurn(Phase.DEPLOY);
        s.MoveCardsToLocation(dock, palpatine);
        s.MoveCardsToTopOfLSReserveDeck(marker, clone);
        s.gameState().markCardsRevealedFromPile(List.of(marker));
        assertTrue(s.LSDeployAvailable(trooper)); s.LSDeployCard(trooper);
        if (s.LSDecisionAvailable("Choose where to deploy")) s.LSChooseCard(dock);
        s.PassAllResponses();
        assertTrue(objective.isFlipped()); assertTrue(s.LSHasCardChoiceAvailable(clone));
        s.LSChooseCard(clone); s.PassAllResponses();
        assertTrue(s.GetLSHand().contains(clone)); assertFalse(s.gameState().isCardRevealedFromPile(marker));
        return s;
    }

    @Test
    public void RealDeploymentFlipsWithPalpatineOnCoruscantAndSearchesForAnyCard() {
        flipAndSearch();
    }

    @Test
    public void BackCanDeployOneCloneFromLostPileAtNormalCostAndGrantsImmunityBelowFour() {
        var s = flipAndSearch(); var objective = s.GetLSCard("objective");
        var clone = s.GetLSCard("clone"); var second = s.GetLSCard("pilot");
        var dock = s.GetLSCard("dock"); var text = "Deploy a clone from Lost Pile";
        s.MoveCardsToTopOfLSLostPile(clone, second);
        s.LSActivateForceCheat(5);
        if (s.AwaitingDSDeployPhaseActions()) s.DSPass();
        assertTrue(s.LSCardActionAvailable(objective, text));
        int force = s.GetLSForcePileCount();
        s.LSUseCardAction(objective, text); s.PassAllResponses();
        if (s.LSHasCardChoiceAvailable(clone)) s.LSChooseCard(clone);
        if (s.LSDecisionAvailable("Choose where to deploy")) s.LSChooseCard(dock);
        s.PassAllResponses();
        assertEquals(s.GetCurrentDecision().getText() + "; clone zone " + clone.getZone(), dock, clone.getAtLocation());
        assertEquals(force - clone.getBlueprint().getDeployCost(), s.GetLSForcePileCount(), s.epsilon);
        assertEquals(4, s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(), clone), s.epsilon);
        if (s.AwaitingDSDeployPhaseActions()) s.DSPass();
        assertTrue(s.AwaitingLSDeployPhaseActions());
        assertFalse(s.LSCardActionAvailable(objective, text)); assertTrue(s.GetLSLostPile().contains(second));
    }

    @Test
    public void TwoCloneBattlegroundsRequireTwoOpposingCardsWithAbilityToDrain() {
        var s = flipAndSearch(); var dock = s.GetLSCard("dock"); var cantina = s.GetLSCard("cantina");
        var marketplace = s.GetDSStartingLocation();
        s.MoveLocationToTable(cantina);
        s.MoveCardsToLocation(dock, s.GetLSCard("clone"));
        s.MoveCardsToLocation(cantina, s.GetLSCard("secondclone"));
        s.MoveCardsToLocation(marketplace, s.GetDSCard("vader"));
        s.SkipToDSTurn(Phase.CONTROL);
        assertFalse(s.DSForceDrainAvailable(marketplace));
        s.MoveCardsToLocation(marketplace, s.GetDSCard("trooper"));
        // Refresh the normal Control decision after the table fixture changes.
        s.DSPass();
        if (s.AwaitingLSControlPhaseActions()) s.LSPass();
        if (!s.AwaitingDSControlPhaseActions()) s.SkipToDSTurn(Phase.CONTROL);
        assertTrue(s.DSForceDrainAvailable(marketplace));
        s.DSForceDrainAt(marketplace); s.PassAllResponses();
        assertTrue(s.AwaitingLSForceLossPayment());
    }

    private VirtualTableScenario resolveMonnok(int handSize) {
        var s = scenario(false); s.StartGame();
        assertTrue("Actual startup is required to test the permanent Monnok clause",
                s.GetLSCard("objective").isObjectiveDeploymentComplete());
        var clone = s.GetLSCard("clone"); var second = s.GetLSCard("secondclone");
        var monnok = s.GetDSCard("monnok");
        s.MoveCardsToHand(clone, second, monnok);
        while (s.GetLSHandCount() < handSize) {
            s.MoveCardsToHand(s.GetTopOfLSReserveDeck());
        }
        s.SkipToPhase(Phase.CONTROL); s.DSActivateForceCheat(4);
        assertEquals(handSize, s.GetLSHandCount());
        var originalHand = List.copyOf(s.GetLSHand());
        assertTrue(s.DSPlayLostInterruptAvailable(monnok)); s.DSPlayLostInterrupt(monnok);
        for (int i = 0; i < 70 && !s.AwaitingDSControlPhaseActions(); i++) {
            if (s.LSDecisionAvailable("Choose card to put on Lost Pile")) {
                var choice = originalHand.stream().map(PhysicalCardImpl.class::cast)
                        .filter(s::LSHasCardChoiceAvailable).findFirst().orElseThrow();
                s.LSChooseCard(choice);
            } else if (s.DSDecisionAvailable("Opponent's hand")) {
                s.DSPass();
            } else if (s.GetCurrentDecision().getText().contains("Optional responses")
                    || s.AwaitingLSControlPhaseActions()) {
                s.PlayerPass(s.GetDecidingPlayer());
            } else {
                break;
            }
        }
        assertTrue(s.GetDSLostPile().contains(monnok));
        return s;
    }

    @Test
    public void ActualMonnokCannotLoseDuplicateClonesAtTwelveCardsButCanAtThirteen() {
        var protectedHand = resolveMonnok(12);
        assertTrue(protectedHand.GetLSHand().containsAll(List.of(
                protectedHand.GetLSCard("clone"), protectedHand.GetLSCard("secondclone"))));
        var exposedHand = resolveMonnok(13);
        assertTrue(exposedHand.GetLSLostPile().containsAll(List.of(
                exposedHand.GetLSCard("clone"), exposedHand.GetLSCard("secondclone"))));
    }

    private VirtualTableScenario loseCharacterToLightning(String name) {
        var s = flipAndSearch(); var site = s.GetDSStartingLocation();
        var target = s.GetLSCard(name); var lightning = s.GetDSCard("lightning");
        s.MoveCardsToLocation(site, target, s.GetDSCard("emperor"));
        s.MoveCardsToHand(lightning); s.SkipToDSTurn(Phase.CONTROL); s.PrepareDSDestiny(6);
        s.DSPlayCard(lightning, "Make target lost");
        if (s.DSDecisionAvailable("Choose Emperor")) s.DSChooseCard(s.GetDSCard("emperor"));
        if (s.DSDecisionAvailable("Choose character")) s.DSChooseCard(target);
        s.PassDestinyDrawResponses();
        for (int i = 0; i < 70 && !s.AwaitingLSForceLossPayment()
                && s.GetCurrentDecision().getText().contains("Optional responses"); i++) {
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertTrue(s.GetCurrentDecision().getText() + "; target zone " + target.getZone(),
                s.GetLSLostPile().contains(target));
        return s;
    }

    @Test
    public void LosingAbilityFourCostsItsOwnerOneForceButLosingAbilityTwoDoesNot() {
        var s = loseCharacterToLightning("luke"); var before = s.GetLSLostPileCount();
        assertTrue(s.AwaitingLSForceLossPayment());
        s.LSPayForceLossFromReserveDeck(); s.PassAllResponses();
        assertEquals(before + 1, s.GetLSLostPileCount());
        var inverse = loseCharacterToLightning("clone");
        assertFalse(inverse.AwaitingLSForceLossPayment()); assertEquals(1, inverse.GetLSLostPileCount());
    }

    private VirtualTableScenario opposingDrain(boolean twoCloneBattlegrounds, String... occupants) {
        var s = flipAndSearch();
        var dock = s.GetLSCard("dock"); var cantina = s.GetLSCard("cantina");
        s.MoveLocationToTable(cantina);
        s.MoveCardsToLocation(dock, s.GetLSCard("clone"));
        if (twoCloneBattlegrounds) s.MoveCardsToLocation(cantina, s.GetLSCard("secondclone"));
        for (String occupant : occupants) s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard(occupant));
        s.SkipToDSTurn(Phase.CONTROL);
        return s;
    }

    @Test
    public void TwoPresenceDroidsQualifyButOneDroidAndOneCardWithAbilityDoNot() {
        var mixed = opposingDrain(true, "destroyer", "vader");
        assertFalse(mixed.DSForceDrainAvailable(mixed.GetDSStartingLocation()));

        var twoDroids = opposingDrain(true, "destroyer", "p59");
        var location = twoDroids.GetDSStartingLocation();
        assertTrue(twoDroids.DSForceDrainAvailable(location));
        twoDroids.DSForceDrainAt(location); twoDroids.PassAllResponses();
        assertTrue(twoDroids.AwaitingLSForceLossPayment());
    }

    @Test
    public void OnlyOneControlledCloneBattlegroundDoesNotRestrictAnActualDarkDrain() {
        var s = opposingDrain(false, "vader"); var site = s.GetDSStartingLocation();
        assertTrue(s.DSForceDrainAvailable(site)); s.DSForceDrainAt(site); s.PassAllResponses();
        assertTrue(s.AwaitingLSForceLossPayment());
    }
}
