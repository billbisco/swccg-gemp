package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1262_003_Tests {
    private VirtualTableScenario scenario() {
        StartingSetup objective = new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                return new HashMap<>(Map.of("objective", "109_12", "chamber", "5_166"));
            }

            @Override public void Setup(VirtualTableScenario scn) {
                if (scn.DSDecisionAvailable("Choose Cloud City battleground site")) {
                    scn.DSChooseCard(scn.GetDSCard("chamber"));
                }
            }
        };
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("ben", "1_21", "alter", "1_71")),
                new HashMap<>(Map.of("altering", "1262_3", "deal", "5_115", "dining", "5_168",
                        "bespin", "5_164", "sector", "5_165", "scout", "1_305")),
                30, 30, StartingSetup.DefaultLSGroundLocation, objective,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        for (int i = 0; i < 25 && !scn.AwaitingDSActivatePhaseActions(); i++) {
            if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("chamber"))) scn.DSChooseCard(scn.GetDSCard("chamber"));
            else if (scn.DSDecisionAvailable("On which side")) scn.DSChoose("Left");
            else if (scn.GetADParam(scn.DS, "results") != null
                    && Arrays.asList(scn.GetADParam(scn.DS, "results")).contains("No")) scn.DSChooseNo();
            else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected Cloud City setup: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.MoveLocationToTable(scn.GetDSCard("bespin"));
        scn.MoveLocationToTable(scn.GetDSCard("sector"));
        scn.MoveLocationToTable(scn.GetDSCard("dining"));
        return scn;
    }

    @Test public void AlteringTheDealPrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("altering").getBlueprint();
        assertEquals("Altering The Deal", b.getTitle());
        assertEquals("Gamblers are vulnerable to bribery, extortion and other forms of manipulation. Their notorious dealings can be easily taken advantage of.", b.getLore());
        assertEquals(3, b.getDestiny(), scn.epsilon);
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertFalse(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void AlteringTheDealActuallyDeploysOnlyOnDiningRoomAndCancelsDarkDealIfOpponentControlsIt() {
        for (boolean opponentControlsDining : List.of(false, true)) {
            var scn = scenario();
            var altering = scn.GetDSCard("altering");
            var dining = scn.GetDSCard("dining");
            var deal = scn.GetDSCard("deal");
            var alter = scn.GetLSCard("alter");
            scn.AttachCardsTo(scn.GetDSCard("sector"), deal);
            scn.MoveCardsToDSHand(altering);
            scn.MoveCardsToLSHand(alter);
            scn.MoveCardsToLocation(opponentControlsDining ? dining : scn.GetLSStartingLocation(), scn.GetLSCard("ben"));
            scn.SkipToPhase(Phase.DEPLOY);
            deployAltering(scn);
            assertSame(dining, altering.getAttachedTo());
            if (opponentControlsDining) assertInZone(Zone.LOST_PILE, deal);
            else assertSame(scn.GetDSCard("sector"), deal.getAttachedTo());
            scn.SkipToLSTurn(Phase.CONTROL);
            assertFalse(scn.LSCardPlayAvailable(alter));
        }
    }

    @Test public void AlteringTheDealFlipsObjectiveWithDarkDealAndBespinAndBlocksAnActualFlipBack() {
        for (boolean occupiesBespin : List.of(false, true)) {
            var scn = scenario();
            var objective = scn.GetDSCard("objective");
            var altering = scn.GetDSCard("altering");
            var scout = scn.GetDSCard("scout");
            scn.AttachCardsTo(scn.GetDSCard("sector"), scn.GetDSCard("deal"));
            if (occupiesBespin) scn.MoveCardsToLocation(scn.GetDSCard("bespin"), scout);
            scn.MoveCardsToDSHand(altering);
            scn.SkipToPhase(Phase.DEPLOY);
            assertFalse("The normal Objective still needs the Cloud City sector occupied", objective.isFlipped());
            deployAltering(scn);
            assertEquals(occupiesBespin, objective.isFlipped());
            if (!occupiesBespin) continue;
            tryFlip(scn, altering, objective);
            assertTrue(objective.isFlipped());
            scn.MoveCardsToDSHand(scout);
            tryFlip(scn, altering, objective);
            assertFalse(objective.isFlipped());
        }
    }

    private void deployAltering(VirtualTableScenario scn) {
        var altering = scn.GetDSCard("altering");
        var dining = scn.GetDSCard("dining");
        scn.DSDeployCard(altering);
        assertTrue(scn.DSHasCardChoiceAvailable(dining));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("chamber")));
        scn.DSChooseCard(dining);
        settle(scn);
    }

    private void tryFlip(VirtualTableScenario scn, PhysicalCardImpl source, PhysicalCardImpl objective) {
        var action = new TopLevelGameTextAction(source, scn.DS, source.getCardId());
        action.appendEffect(new FlipCardEffect(action, objective));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        settle(scn);
    }

    private void settle(VirtualTableScenario scn) {
        for (int i = 0; i < 50 && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected Altering The Deal decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions());
    }
}
