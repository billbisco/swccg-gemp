package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return GetScenario(StartingSetup.BHBMObjective);
    }

    protected VirtualTableScenario GetScenario(StartingSetup dsStart) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("obiwan", "1_21");
                }},
                new HashMap<>() {{
                    put("gyttds", "1281_18");
                    put("forest", "8_158");
                    put("combat", "9_121");
                    put("mobilization", "9_129");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                dsStart,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private static PhysicalCardImpl sonOfVaderInReserve(VirtualTableScenario scn) {
        return MissionTestSupport.putInReserve(scn, 98601, scn.DS, Vb81RestTestSupport.sonOfVader());
    }

    /**
     * Starts the game and declines the Starting Interrupt.
     */
    private static void startGame(VirtualTableScenario scn) {
        scn.StartGame();
        for (int i = 0; i < 2; ++i) {
            if (Vb81RestTestSupport.holdsDecision(scn, scn.DS, "starting interrupt")) {
                scn.DSPass();
            }
            if (Vb81RestTestSupport.holdsDecision(scn, scn.LS, "starting interrupt")) {
                scn.LSPass();
            }
        }
    }

    @Test
    public void GiveYourselfToTheDarkSideStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("gyttds").getBlueprint();

        assertEquals("Give Yourself To The Dark Side", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EPIC_EVENT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPIC_EVENT);
            add(Icon.DEATH_STAR_II);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void GiveYourselfToTheDarkSideIsAStartingInterruptOnlyIfBringHimBeforeMeOnTable() {
        var scn = GetScenario();
        var gyttds = scn.GetDSCard("gyttds");
        scn.StartGame();
        assertTrue(gyttds.getBlueprint().playableAsStartingInterrupt(scn.game(), gyttds));

        var other = GetScenario(StartingSetup.DefaultDSGroundLocation);
        var otherCard = other.GetDSCard("gyttds");
        other.StartGame();
        assertFalse(otherCard.getBlueprint().playableAsStartingInterrupt(other.game(), otherCard));
    }

    @Test
    public void GiveYourselfToTheDarkSidePlaysInsteadOfAStartingInterruptDeployingSiteEffectsAndStackingSonOfVader() {
        var scn = GetScenario();
        var gyttds = scn.GetDSCard("gyttds");
        var forest = scn.GetDSCard("forest");
        var combat = scn.GetDSCard("combat");
        var mobilization = scn.GetDSCard("mobilization");
        var son = sonOfVaderInReserve(scn);

        scn.StartGame();
        assertTrue(scn.DSDecisionAvailable("starting interrupt"));
        assertTrue(scn.DSHasCardChoiceAvailable(gyttds));
        scn.DSChooseCard(gyttds);
        for (int i = 0; i < 30; ++i) {
            if (Vb81RestTestSupport.dsCardChoiceAvailable(scn, forest)) {
                scn.DSChooseCard(forest);
            }
            else if (Vb81RestTestSupport.dsCardChoiceAvailable(scn, combat) && !combat.getZone().isInPlay()) {
                scn.DSChooseCard(combat);
            }
            else if (Vb81RestTestSupport.dsCardChoiceAvailable(scn, mobilization) && !mobilization.getZone().isInPlay()) {
                scn.DSChooseCard(mobilization);
            }
            else if (Vb81RestTestSupport.holdsDecision(scn, scn.DS, "Son Of Vader into hand")) {
                scn.DSChooseYes();
            }
            else if (Vb81RestTestSupport.dsCardChoiceAvailable(scn, son)) {
                scn.DSChooseCard(son);
            }
            else if (Vb81RestTestSupport.holdsDecision(scn, scn.DS, "On which side")) {
                scn.DSDecided("0");
            }
            else if (scn.GetCurrentDecision() != null && scn.GetCurrentDecision().getText().toLowerCase().contains("optional")) {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
            else {
                break;
            }
        }

        assertEquals(Zone.SIDE_OF_TABLE, gyttds.getZone());
        assertTrue(forest.getZone().isInPlay());
        assertTrue(combat.getZone().isInPlay());
        assertTrue(mobilization.getZone().isInPlay());
        assertEquals(gyttds, son.getStackedOn());
    }

    @Test
    public void GiveYourselfToTheDarkSideRebelsLoseImmunityToAttritionWhileSonOfVaderOnTable() {
        var scn = GetScenario();
        var gyttds = scn.GetDSCard("gyttds");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();
        var son = sonOfVaderInReserve(scn);

        startGame(scn);
        scn.MoveCardsToDSSideOfTable(gyttds);
        scn.MoveCardsToLocation(site, luke);
        assertTrue(scn.game().getModifiersQuerying().hasAnyImmunityToAttrition(scn.gameState(), luke));
        scn.MoveCardsToLocation(site, son);
        assertFalse(scn.game().getModifiersQuerying().hasAnyImmunityToAttrition(scn.gameState(), luke));
    }

    @Test
    public void GiveYourselfToTheDarkSideOpponentLosesOneForceAtEndOfEachTurnWhileSonOfVaderOnTable() {
        var scn = GetScenario();
        var gyttds = scn.GetDSCard("gyttds");
        var site = scn.GetLSStartingLocation();
        var son = sonOfVaderInReserve(scn);

        startGame(scn);
        scn.MoveCardsToDSSideOfTable(gyttds);
        scn.MoveCardsToLocation(site, son);
        scn.SkipToPhase(Phase.DRAW);
        int lsLife = scn.GetLSLifeForceRemaining();
        scn.PassDrawActions();
        Vb81RestTestSupport.payForceLossFromReserveDeck(scn);
        assertEquals(lsLife - 1, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void GiveYourselfToTheDarkSideStacksSonOfVaderHereWhenHeLeavesTable() {
        var scn = GetScenario();
        var gyttds = scn.GetDSCard("gyttds");
        var luke = scn.GetLSCard("luke");
        var obiwan = scn.GetLSCard("obiwan");
        var site = scn.GetLSStartingLocation();
        var son = sonOfVaderInReserve(scn);

        startGame(scn);
        scn.MoveCardsToDSSideOfTable(gyttds);
        scn.MoveCardsToLocation(site, son, luke, obiwan);
        scn.DSActivateForceCheat(3);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PrepareLSDestiny(7);
        scn.PrepareDSDestiny(0);
        scn.SkipToDamageSegment(true);
        assertTrue(scn.AwaitingDSBattleDamagePayment());
        scn.DSChooseCard(son);
        scn.PassAllResponses();

        assertEquals(gyttds, son.getStackedOn());
    }

    @Test
    public void GiveYourselfToTheDarkSideLosesYourDestinyIfLukeCrossedOverAndPersonaReplacesHim() {
        var scn = GetScenario();
        var gyttds = scn.GetDSCard("gyttds");
        var destiny = scn.GetDSCard("destiny");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();
        var son = sonOfVaderInReserve(scn);

        startGame(scn);
        assertTrue(destiny.getZone().isInPlay());
        scn.MoveCardsToDSSideOfTable(gyttds);
        scn.StackCardsOn(gyttds, son);
        scn.MoveCardsToLocation(site, luke);
        scn.gameState().crossOverCharacterOnTable(luke);
        scn.DSActivateForceCheat(3);
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(Vb81RestTestSupport.inZone(destiny, Zone.LOST_PILE));
        assertTrue(scn.DSCardActionAvailable(gyttds, "Persona replace"));
        scn.DSUseCardAction(gyttds, "Persona replace");
        if (Vb81RestTestSupport.dsCardChoiceAvailable(scn, son)) {
            scn.DSChooseCard(son);
        }
        scn.PassAllResponses();
        assertAtLocation(site, son);
    }

    @Test
    public void GiveYourselfToTheDarkSideDeploysSonOfVaderFromHereAfterLukeCrossedOver() {
        var scn = GetScenario();
        var gyttds = scn.GetDSCard("gyttds");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();
        var son = sonOfVaderInReserve(scn);

        startGame(scn);
        scn.MoveCardsToDSSideOfTable(gyttds);
        scn.StackCardsOn(gyttds, son);
        scn.DSActivateForceCheat(3);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(gyttds, "Deploy Son Of Vader from here"));

        scn.MoveCardsToLocation(site, luke);
        scn.gameState().crossOverCharacterOnTable(luke);
        scn.SkipToPhase(Phase.BATTLE);
        scn.MoveCardsToTopOfLSLostPile(luke);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(gyttds, "Deploy Son Of Vader from here"));
        scn.DSUseCardAction(gyttds, "Deploy Son Of Vader from here");
        for (int i = 0; i < 6; ++i) {
            if (Vb81RestTestSupport.dsCardChoiceAvailable(scn, son)) {
                scn.DSChooseCard(son);
            }
            else if (Vb81RestTestSupport.dsCardChoiceAvailable(scn, site)) {
                scn.DSChooseCard(site);
            }
            else {
                break;
            }
        }
        scn.PassAllResponses();
        assertAtLocation(site, son);
    }
}
