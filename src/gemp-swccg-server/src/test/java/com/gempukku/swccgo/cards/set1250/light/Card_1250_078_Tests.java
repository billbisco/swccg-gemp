package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
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
                    put("palpatine", "1116_10");
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
        return new VirtualTableScenario(new HashMap<>() {{
            put("clone", "1116_9"); put("trooper", "1_26"); put("luke", "1_19");
            put("dock", "12_74"); put("plains", "12_79"); put("marker", "1_109");
        }}, new HashMap<>() {{
            put("trooper", "1_194"); put("vader", "1_168");
        }}, 40, 40, objectiveStart(takeSecurity), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private void assertFace(VirtualTableScenario s, SwccgCardBlueprint bp, String title, int destiny) {
        assertEquals(title, bp.getTitle()); assertEquals(destiny, bp.getDestiny(), s.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet()); assertFalse(bp.hasVirtualSuffix());
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
        assertTrue(objective.isObjectiveDeploymentComplete()); assertFalse(objective.isFlipped());
        assertEquals(Zone.LOCATIONS, s.GetLSCard("coruscant").getZone()); assertEquals(Zone.LOCATIONS, prison.getZone());
        assertEquals(prison, palpatine.getAtLocation()); assertTrue(palpatine.isImprisoned());
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

    @Test
    public void RealDeploymentFlipsWithPalpatineOnCoruscantAndSearchesForAnyCard() {
        var s = scenario(false); s.StartGame(); var objective = s.GetLSCard("objective");
        var dock = s.GetLSCard("dock"); var palpatine = s.GetLSCard("palpatine");
        var clone = s.GetLSCard("clone"); var marker = s.GetLSCard("marker"); var trooper = s.GetLSCard("trooper");
        s.MoveLocationToTable(dock);
        s.SkipToLSTurn(Phase.DEPLOY); s.LSActivateForceCheat(8);
        s.gameState().moveCardToLocation(palpatine, dock, true);
        s.MoveCardsToHand(trooper); s.MoveCardsToTopOfLSReserveDeck(marker, clone);
        s.gameState().markCardsRevealedFromPile(List.of(marker));
        assertTrue(s.LSDeployAvailable(trooper)); s.LSDeployCard(trooper);
        if (s.LSDecisionAvailable("Choose where to deploy")) s.LSChooseCard(dock);
        s.PassAllResponses();
        assertTrue(objective.isFlipped()); assertTrue(s.LSHasCardChoiceAvailable(clone));
        s.LSChooseCard(clone); s.PassAllResponses();
        assertTrue(s.GetLSHand().contains(clone)); assertFalse(s.gameState().isCardRevealedFromPile(marker));
        assertEquals(2, s.game().getModifiersQuerying().getForceGenerationFromLocation(s.gameState(), dock, s.LS), s.epsilon);
        assertEquals(4, s.game().getModifiersQuerying().getImmunityToAttritionOfExactly(s.gameState(), clone), s.epsilon);
    }
}
