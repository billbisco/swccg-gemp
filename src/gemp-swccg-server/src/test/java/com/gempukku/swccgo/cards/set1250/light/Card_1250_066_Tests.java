package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_066_Tests {
    private static final String CHANGE = "Activate 1 Force and shuffle Reserve Deck";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("odds", "1250_66"); put("luke", "1_19"); put("guard", "1_26");
            put("mace", "12_13"); put("hallway", "14_51");
        }}, new HashMap<>() {{
            put("emperor", "9_109"); put("lightning", "9_136"); put("vader", "1_168"); put("saber", "1_324");
            put("djas", "1_171"); put("podrace", "11_79"); put("arena", "11_94");
        }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FinalChangingOddsIsAUsedInterruptWithThePrintedIcons() {
        var s = scenario(); var bp = s.GetLSCard("odds").getBlueprint();
        assertEquals(Title.Changing_The_Odds, bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(CardSubtype.USED, bp.getCardSubtype()); assertEquals(5, bp.getDestiny(), s.epsilon);
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.INTERRUPT)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.EPISODE_I, Icon.TATOOINE, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    private boolean offered(VirtualTableScenario s) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardPlayAvailable(s.GetLSCard("odds"), CHANGE);
    }

    private void passUntilOffer(VirtualTableScenario s) {
        for (int i = 0; i < 40 && !offered(s)
                && s.GetCurrentDecision().getText().contains("Optional responses"); i++) {
            s.PlayerPass(s.GetDecidingPlayer());
        }
    }

    private void checkActivationAndShuffle(VirtualTableScenario s) {
        var top = s.GetDSDestiny(6); var marker = s.GetDSDestiny(3);
        s.MoveCardsToTopOfDSReserveDeck(marker, top);
        assertEquals(top, s.GetTopOfDSReserveDeck());
        s.gameState().markCardsRevealedFromPile(List.of(marker));
        assertTrue(s.GetCurrentDecision().getText(), offered(s));
        int darkForce = s.GetDSForcePileCount(); int lightForce = s.GetLSForcePileCount();
        var odds = s.GetLSCard("odds");
        s.LSPlayCard(odds, CHANGE); s.PassAllResponses();
        assertEquals(darkForce + 1, s.GetDSForcePileCount()); assertTrue(s.GetDSForcePile().contains(top));
        assertEquals(lightForce, s.GetLSForcePileCount());
        assertFalse(s.gameState().isCardRevealedFromPile(marker));
        assertTrue(s.GetLSUsedPile().contains(odds));
    }

    @Test
    public void ActualOpposingInterruptDrawActivatesItsTopCardAndShufflesItsReserve() {
        var s = scenario(); s.StartGame(); var site = s.GetDSStartingLocation();
        var lightning = s.GetDSCard("lightning"); var target = s.GetLSCard("luke");
        s.MoveCardsToLocation(site, s.GetDSCard("emperor"), target);
        s.MoveCardsToHand(lightning, s.GetLSCard("odds")); s.SkipToDSTurn(Phase.CONTROL);
        s.DSPlayCard(lightning, "Make target lost");
        if (s.DSDecisionAvailable("Choose Emperor")) s.DSChooseCard(s.GetDSCard("emperor"));
        if (s.DSDecisionAvailable("Choose character")) s.DSChooseCard(target);
        passUntilOffer(s); checkActivationAndShuffle(s);
    }

    private VirtualTableScenario battle() {
        var s = scenario(); s.StartGame(); var site = s.GetDSStartingLocation();
        var vader = s.GetDSCard("vader");
        s.MoveCardsToLocation(site, vader, s.GetLSCard("luke"), s.GetLSCard("guard"));
        s.AttachCardsTo(vader, s.GetDSCard("saber"));
        s.MoveCardsToHand(s.GetLSCard("odds")); s.DSActivateForceCheat(6);
        s.SkipToDSTurn(Phase.BATTLE); s.DSInitiateBattle(site);
        return s;
    }

    @Test
    public void ActualWeaponDrawAlsoActivatesAndShufflesBeforeItsDestiny() {
        var s = battle(); var saber = s.GetDSCard("saber");
        assertTrue(s.DSCardActionAvailable(saber, "Fire")); s.DSUseCardAction(saber, "Fire");
        s.DSChooseCard(s.GetLSCard("guard")); passUntilOffer(s); checkActivationAndShuffle(s);
    }

    @Test
    public void OrdinaryBattleDestinyDoesNotOfferTheFirstMode() {
        var s = battle(); s.SkipToPowerSegment();
        assertTrue(s.DSDecisionAvailable("battle destiny")); s.DSChooseYes(); passUntilOffer(s);
        assertFalse(offered(s)); assertTrue(s.GetLSHand().contains(s.GetLSCard("odds")));
    }

    @Test
    public void ActualEpicEventActivatesTheDrawerEvenWhenOwnedByTheOtherPlayer() {
        var s = scenario(); s.StartGame();
        var race = s.GetDSCard("podrace"); var arena = s.GetDSCard("arena");
        s.MoveLocationToTable(arena); s.AttachCardsTo(arena, race);
        s.MoveCardsToHand(s.GetLSCard("odds"));
        s.SkipToPhase(Phase.CONTROL);
        assertTrue(s.DSCardActionAvailable(race, "Initiate a Podrace"));
        s.DSUseCardAction(race, "Initiate a Podrace"); s.PassAllResponses();
        for (int i = 0; i < 8 && !s.AwaitingLSControlPhaseActions(); i++) {
            assertTrue(s.AwaitingDSControlPhaseActions()); s.DSPass();
        }
        assertTrue(s.LSCardActionAvailable(race, "Draw race destiny"));
        s.LSUseCardAction(race, "Draw race destiny"); passUntilOffer(s);
        assertTrue(s.GetCurrentDecision().getText(), offered(s));
        var top = s.GetLSDestiny(6); var marker = s.GetLSDestiny(3);
        s.MoveCardsToTopOfLSReserveDeck(marker, top);
        s.gameState().markCardsRevealedFromPile(List.of(marker));
        int lightForce = s.GetLSForcePileCount(); int darkForce = s.GetDSForcePileCount();
        s.LSPlayCard(s.GetLSCard("odds"), CHANGE); s.PassAllResponses();
        assertEquals(lightForce + 1, s.GetLSForcePileCount()); assertTrue(s.GetLSForcePile().contains(top));
        assertEquals(darkForce, s.GetDSForcePileCount()); assertFalse(s.gameState().isCardRevealedFromPile(marker));
    }

    @Test
    public void ActualObjectiveDestinyCanBeChangedOrTheResponseCanBeDeclined() {
        var changed = combatDraw();
        checkActivationAndShuffle(changed);

        var declined = combatDraw();
        assertTrue(declined.GetCurrentDecision().getText(), offered(declined));
        int darkForce = declined.GetDSForcePileCount(); int lightForce = declined.GetLSForcePileCount();
        declined.LSPass(); declined.PassAllResponses();
        assertTrue(declined.GetLSHand().contains(declined.GetLSCard("odds")));
        assertEquals(darkForce, declined.GetDSForcePileCount());
        assertEquals(lightForce, declined.GetLSForcePileCount());
    }

    private VirtualTableScenario combatDraw() {
        var s = scenario(); s.StartGame();
        var objective = (PhysicalCardImpl) s.gameState().createPhysicalCard(s.DS, VirtualTableScenario._cardLibrary, "13_73");
        s.gameState().addCardToZone(objective, Zone.HAND, s.DS, true, false, false);
        s.MoveCardsToDSSideOfTable(objective); objective.setObjectiveDeploymentComplete(true);
        s.gameState().flipCard(s.game(), objective, true);
        var site = s.GetLSCard("hallway"); var mace = s.GetLSCard("mace"); var vader = s.GetDSCard("vader");
        s.MoveLocationToTable(site); s.MoveCardsToLocation(site, mace, vader);
        s.MoveCardsToHand(s.GetLSCard("odds"));
        s.SkipToPhase(Phase.MOVE);
        assertTrue(s.DSCardActionAvailable(objective, "Initiate lightsaber combat"));
        s.DSUseCardAction(objective, "Initiate lightsaber combat");
        if (s.DSHasCardChoiceAvailable(vader)) s.DSChooseCard(vader);
        if (s.DSHasCardChoiceAvailable(mace)) s.DSChooseCard(mace);
        passUntilOffer(s);
        return s;
    }

    @Test
    public void ActivatingTheLastReserveCardLeavesNoCardForTheOriginalDraw() {
        var s = scenario(); s.StartGame(); var site = s.GetDSStartingLocation();
        var lightning = s.GetDSCard("lightning"); var target = s.GetLSCard("luke");
        s.MoveCardsToLocation(site, s.GetDSCard("emperor"), target);
        s.MoveCardsToHand(lightning, s.GetLSCard("odds"));
        s.DSActivateForceCheat(4); s.SkipToPhase(Phase.CONTROL);
        var last = s.GetDSDestiny(6);
        for (var card : List.copyOf(s.GetDSReserveDeck())) s.MoveOutOfPlay((PhysicalCardImpl) card);
        s.MoveCardsToTopOfDSReserveDeck(last);
        assertEquals(1, s.GetDSReserveDeckCount());
        s.DSPlayCard(lightning, "Make target lost");
        if (s.DSDecisionAvailable("Choose Emperor")) s.DSChooseCard(s.GetDSCard("emperor"));
        if (s.DSDecisionAvailable("Choose character")) s.DSChooseCard(target);
        passUntilOffer(s);
        assertTrue(s.GetCurrentDecision().getText(), offered(s));
        var odds = s.GetLSCard("odds");
        s.LSPlayCard(odds, CHANGE); s.PassAllResponses();
        assertTrue(s.GetDSForcePile().contains(last)); assertEquals(0, s.GetDSReserveDeckCount());
        assertEquals(site, target.getAtLocation()); assertTrue(s.GetLSUsedPile().contains(odds));
    }
    private static final String REDRAW = "Cancel and redraw both battle destinies";

    private boolean redrawOffered(VirtualTableScenario s) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardPlayAvailable(s.GetLSCard("odds"), REDRAW);
    }

    /** Battle at the Dark starting site where each player draws battle destiny when offered. */
    private VirtualTableScenario battleDestinies(boolean darkDrawsTwo) {
        var s = scenario(); s.StartGame(); var site = s.GetDSStartingLocation();
        var djas = s.GetDSCard("djas");
        s.MoveCardsToLocation(site, djas, s.GetLSCard("luke"), s.GetLSCard("guard"));
        if (darkDrawsTwo) s.game().getModifiersEnvironment().addUntilEndOfGameModifier(
                new AddsBattleDestinyModifier(djas, 1, s.DS));
        s.MoveCardsToHand(s.GetLSCard("odds")); s.DSActivateForceCheat(6);
        s.SkipToDSTurn(Phase.BATTLE);
        // Redraws come second in each deck; the first draw comes from the top.
        s.PrepareDSDestiny(5); s.PrepareDSDestiny(2); if (darkDrawsTwo) s.PrepareDSDestiny(3);
        s.PrepareLSDestiny(6); s.PrepareLSDestiny(1);
        s.DSInitiateBattle(site); s.SkipToPowerSegment();
        s.PassResponses("BEFORE_BATTLE_DESTINY_DRAWS");
        assertTrue(s.DSDecisionAvailable("battle destiny")); s.DSChooseYes(); s.PassDestinyDrawResponses();
        if (darkDrawsTwo) s.PassDestinyDrawResponses();
        s.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        assertTrue(s.LSDecisionAvailable("battle destiny")); s.LSChooseYes(); s.PassDestinyDrawResponses();
        for (int i = 0; i < 20 && !redrawOffered(s)
                && s.GetCurrentDecision().getText().contains("Optional responses"); i++) {
            s.PlayerPass(s.GetDecidingPlayer());
        }
        return s;
    }

    @Test
    public void BothPlayersDrawingOneBattleDestinyCanBeCanceledAndRedrawn() {
        var s = battleDestinies(false);
        assertEquals(2, s.GetDSTotalDestiny()); assertEquals(1, s.GetLSTotalDestiny());
        assertTrue(s.GetCurrentDecision().getText(), redrawOffered(s));
        var odds = s.GetLSCard("odds");
        var battle = s.gameState().getBattleState();
        var lightDraw = (PhysicalCardImpl) battle.getBattleDestinyDraws(s.LS).get(0);
        var darkDraw = (PhysicalCardImpl) battle.getBattleDestinyDraws(s.DS).get(0);
        var lightRedraw = s.GetTopOfLSReserveDeck(); var darkRedraw = s.GetTopOfDSReserveDeck();
        assertEquals(6, lightRedraw.getBlueprint().getDestiny(), s.epsilon);
        assertEquals(5, darkRedraw.getBlueprint().getDestiny(), s.epsilon);
        s.LSPlayCard(odds, REDRAW); s.PassAllResponses();
        // The player of Changing The Odds picks the draw to cancel for each player (one each here).
        assertTrue(s.LSDecisionAvailable("to cancel")); s.LSChooseCard(lightDraw); s.PassAllResponses();
        assertTrue(s.LSDecisionAvailable("to cancel")); s.LSChooseCard(darkDraw);
        for (int i = 0; i < 40 && !battle.getBattleDestinyDraws(s.DS).contains(darkRedraw)
                && s.GetCurrentDecision().getText().contains("Optional responses"); i++) {
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertEquals(s.GetCurrentDecision().getText() + " by " + s.GetDecidingPlayer() + " DS " + battle.getBattleDestinyDraws(s.DS)
                + " redraw zones " + lightRedraw.getZone() + "/" + darkRedraw.getZone(),
                List.of(lightRedraw), battle.getBattleDestinyDraws(s.LS));
        assertEquals(List.of(darkRedraw), battle.getBattleDestinyDraws(s.DS));
        assertEquals(5, s.GetDSTotalDestiny()); assertEquals(6, s.GetLSTotalDestiny());
        assertTrue(s.GetLSUsedPile().contains(odds));
    }

    @Test
    public void RedrawIsNotOfferedWhenAPlayerDrewTwoBattleDestinies() {
        var s = battleDestinies(true);
        assertEquals(2, s.GetDSBattleDestinyCount()); assertEquals(1, s.GetLSBattleDestinyCount());
        assertFalse(redrawOffered(s)); assertTrue(s.GetLSHand().contains(s.GetLSCard("odds")));
    }
}
