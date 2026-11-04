package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class Card_1273_023_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("antilles", "1273_23");
                    put("corvette", "1_140");
                    put("red1", "1_144");
                }},
                new HashMap<>() {{
                    put("black2", "1_299");
                    put("handcard", "1_194");
                    put("vader", "1_168");
                    put("bast", "1_165");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void CaptainAntillesStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("antilles").getBlueprint();

        assertEquals("Captain Antilles", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.CAPTAIN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CaptainAntillesAddsTwoToPowerOfStarshipAndThreeToCorvette() {
        var scn = GetScenario();
        var antilles = scn.GetLSCard("antilles");
        var corvette = scn.GetLSCard("corvette");
        var red1 = scn.GetLSCard("red1");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, corvette, red1);
        int corvettePower = (int) (float) corvette.getBlueprint().getPower();
        int red1Power = (int) (float) red1.getBlueprint().getPower();

        scn.BoardAsPilot(red1, antilles);
        assertEquals(red1Power + 2, scn.GetPower(red1));

        scn.MoveCardToZone(scn.LS, antilles, com.gempukku.swccgo.common.Zone.HAND);
        scn.BoardAsPilot(corvette, antilles);
        assertEquals(corvettePower + 3, scn.GetPower(corvette));
    }

    @Test
    public void CaptainAntillesCorvetteHePilotsIsImmuneToAttritionLessThan5() {
        var scn = GetScenario();
        var antilles = scn.GetLSCard("antilles");
        var corvette = scn.GetLSCard("corvette");
        var red1 = scn.GetLSCard("red1");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, corvette, red1);
        assertEquals(0, immunityLessThan(scn, corvette), scn.epsilon);

        scn.BoardAsPilot(red1, antilles);
        assertEquals(0, immunityLessThan(scn, red1), scn.epsilon);
        assertEquals(0, immunityLessThan(scn, corvette), scn.epsilon);

        scn.MoveCardToZone(scn.LS, antilles, com.gempukku.swccgo.common.Zone.HAND);
        scn.BoardAsPilot(corvette, antilles);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), corvette), scn.epsilon);
    }

    @Test
    public void CaptainAntillesOncePerTurnPeeksAtCardInOpponentsHandWhilePilotingCorvette() {
        var scn = GetScenario();
        var antilles = scn.GetLSCard("antilles");
        var corvette = scn.GetLSCard("corvette");
        var red1 = scn.GetLSCard("red1");
        var handcard = scn.GetDSCard("handcard");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, corvette, red1);
        scn.MoveCardsToDSHand(handcard);
        scn.BoardAsPilot(corvette, antilles);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(antilles, "Peek at a card in opponent's hand"));
        scn.LSUseCardAction(antilles, "Peek at a card in opponent's hand");
        for (int i = 0; i < 10 && !Vb81RestTestSupport.holdsDecision(scn, scn.LS, "Choose Control action"); ++i) {
            if (scn.GetCurrentDecision() == null)
                break;
            if (scn.GetDecidingPlayer().equals(scn.LS) && scn.GetCurrentDecision().getText().toLowerCase().contains("hand")) {
                scn.LSDismissRevealedCards();
            }
            else {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
        }
        assertFalse(scn.LSCardActionAvailable(antilles, "Peek at a card in opponent's hand"));
    }

    @Test
    public void CaptainAntillesOncePerGameMovesStarshipHePilotsAwayFromBattleForTwoForce() {
        var scn = GetScenario();
        var antilles = scn.GetLSCard("antilles");
        var corvette = scn.GetLSCard("corvette");
        var black2 = scn.GetDSCard("black2");
        var bast = scn.GetDSCard("bast");
        var tibrin = scn.GetLSStartingLocation();
        var dantooine = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dantooine, corvette, black2);
        scn.BoardAsPilot(corvette, antilles);
        scn.BoardAsPilot(black2, bast);
        scn.LSActivateForceCheat(4);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(dantooine);
        Vb81RestTestSupport.passUntil(scn, () -> scn.LSCardActionAvailable(antilles, "Move starship away"));
        assertTrue(scn.LSCardActionAvailable(antilles, "Move starship away"));
        int forceBefore = scn.GetLSForcePileCount();
        scn.LSUseCardAction(antilles, "Move starship away");
        for (int i = 0; i < 20 && scn.GetCurrentDecision() != null; ++i) {
            if (scn.LSDecisionAvailable("Choose") && scn.LSHasCardChoiceAvailable(corvette)) {
                scn.LSChooseCard(corvette);
            }
            else if (scn.LSDecisionAvailable("Choose") && scn.LSHasCardChoiceAvailable(tibrin)) {
                scn.LSChooseCard(tibrin);
            }
            else if (scn.LSGetActionChoices() != null && scn.LSGetActionChoices().stream().anyMatch(s -> s.contains("Move"))) {
                scn.LSChooseAction(scn.LSGetActionChoices().stream().filter(s -> s.contains("Move")).findFirst().get());
            }
            else if (scn.GetCurrentDecision().getText().toLowerCase().contains("optional")) {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
            else {
                break;
            }
            if (corvette.getAtLocation() != null && corvette.getAtLocation() != dantooine) {
                break;
            }
        }
        assertNotEquals(dantooine, corvette.getAtLocation());
        assertEquals(forceBefore - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void CaptainAntillesMayNotMoveStarshipAwayWhenVaderHere() {
        var scn = GetScenario();
        var antilles = scn.GetLSCard("antilles");
        var corvette = scn.GetLSCard("corvette");
        var black2 = scn.GetDSCard("black2");
        var vader = scn.GetDSCard("vader");
        var dantooine = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dantooine, corvette, black2);
        scn.BoardAsPilot(corvette, antilles);
        scn.BoardAsPilot(black2, vader);
        scn.LSActivateForceCheat(4);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(dantooine);
        Vb81RestTestSupport.passUntil(scn, () -> scn.AwaitingLSWeaponsSegmentActions() || scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(Vb81RestTestSupport.lsCardActionAvailable(scn, antilles, "Move starship away"));
    }

    private static float immunityLessThan(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        return scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), card);
    }
}
