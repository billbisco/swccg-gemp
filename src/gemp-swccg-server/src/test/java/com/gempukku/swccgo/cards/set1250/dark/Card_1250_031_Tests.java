package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1250_031_Tests {
    private static final String CANCEL = "Maul disembarks to cancel Jedi attrition immunity";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("obi", "1_21", "rebel", "1_28")),
                new HashMap<>(Map.of("speeder", "1250_31", "maul", "11_54", "trooper", "1_194",
                        "site", "5_169")), 22, 22, StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation, StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("site"));
        return scn;
    }

    @Test public void MaulsSithSpeederPrintedMetadataMatchesIncludingArmorNotManeuver() {
        var scn = scenario();
        var b = scn.GetDSCard("speeder").getBlueprint();
        assertEquals("Maul's Sith Speeder", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.TRANSPORT, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.isLegacy());
        assertFalse(b.hasVirtualSuffix());
        assertEquals(5, b.getDestiny(), scn.epsilon);
        assertEquals(1, b.getDeployCost(), scn.epsilon);
        assertEquals(1, b.getPower(), scn.epsilon);
        assertEquals(6, b.getArmor(), scn.epsilon);
        assertNull(b.getManeuver());
        assertEquals(6, b.getLandspeed(), scn.epsilon);
        assertEquals(2, b.getForfeit(), scn.epsilon);
        assertEquals(1, b.getPilotCapacity());
        assertEquals(0, b.getPassengerCapacity());
        assertEquals("Attain speeds of up to 600 kph. 3 meters long. Extremely difficult to control at high speeds. Dengar rode a similar swoop when injured in a race with Han at Agrilat.", b.getLore());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.VEHICLE)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.VEHICLE, Icon.SPECIAL_EDITION,
                Icon.EPISODE_I, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.SWOOP)));
    }

    @Test public void MaulsSithSpeederAdmitsMaulAsDriverAndNoTrooper() {
        var scn = scenario();
        var site = scn.GetDSCard("site");
        var speeder = scn.GetDSCard("speeder");
        var maul = scn.GetDSCard("maul");
        var trooper = scn.GetDSCard("trooper");
        scn.MoveCardsToLocation(site, speeder, maul, trooper);
        scn.SkipToPhase(Phase.MOVE);
        assertFalse(scn.DSCardActionAvailable(trooper, "Embark"));
        assertTrue(scn.DSCardActionAvailable(maul, "Embark"));
        scn.DSUseCardAction(maul, "Embark");
        if (scn.DSHasCardChoiceAvailable(speeder)) scn.DSChooseCard(speeder);
        scn.PassAllResponses();
        assertTrue(Filters.driving(speeder).accepts(scn.game(), maul));
        assertFalse(scn.DSCardActionAvailable(speeder, CANCEL));
    }

    private VirtualTableScenario reactScenario(boolean withMaul, int availableForce) {
        var scn = scenario();
        var site = scn.GetDSCard("site");
        scn.MoveCardsToLocation(site, scn.GetLSCard("rebel"));
        scn.MoveCardsToDSHand(scn.GetDSCard("speeder"), scn.GetDSCard("trooper"));
        if (withMaul) scn.MoveCardsToDSHand(scn.GetDSCard("maul"));
        else scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("maul"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(availableForce);
        scn.LSForceDrainAt(site);
        return scn;
    }

    @Test public void MaulsSithSpeederReactsWithMaulAndPaysForBothCards() {
        var scn = reactScenario(true, 9);
        var speeder = scn.GetDSCard("speeder");
        var maul = scn.GetDSCard("maul");
        var site = scn.GetDSCard("site");
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardPlayAvailable(speeder));
        scn.DSPlayCard(speeder);
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Do you want to simultaneously deploy a driver")) scn.DSChooseYes();
        if (scn.DSHasCardChoiceAvailable(maul)) {
            assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("trooper")));
            scn.DSChooseCard(maul);
        }
        if (scn.DSHasCardChoiceAvailable(site)) scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertSame(site, speeder.getAtLocation());
        assertTrue(Filters.driving(speeder).accepts(scn.game(), maul));
        assertEquals(forceBefore - 9, scn.GetDSForcePileCount());
    }

    @Test public void MaulsSithSpeederCannotReactWithoutMaulOrWithOneForceTooLittle() {
        for (var scn : List.of(reactScenario(false, 9), reactScenario(true, 8))) {
            assertFalse(scn.DSGetDecision() instanceof CardActionSelectionDecision
                    && scn.DSCardPlayAvailable(scn.GetDSCard("speeder")));
            assertInZone(Zone.HAND, scn.GetDSCard("speeder"));
        }
    }

    private VirtualTableScenario battle(boolean driver, boolean jediHere) {
        var scn = scenario();
        var site = scn.GetDSCard("site");
        var speeder = scn.GetDSCard("speeder");
        var maul = scn.GetDSCard("maul");
        scn.MoveCardsToLocation(site, speeder, scn.GetDSCard("trooper"), scn.GetLSCard("rebel"));
        scn.MoveCardsToLocation(jediHere ? site : scn.GetLSStartingLocation(), scn.GetLSCard("obi"));
        if (driver) scn.BoardAsPilot(speeder, maul);
        else scn.MoveCardsToLocation(site, maul);
        scn.EnsureDSForcePile(1);
        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(1);
        scn.PrepareLSDestiny(6);
        scn.DSInitiateBattle(site);
        return scn;
    }

    @Test public void MaulsSithSpeederDisembarksDuringBattleAndCancelsOnlyHereForThisBattle() {
        var scn = battle(true, true);
        var speeder = scn.GetDSCard("speeder");
        var maul = scn.GetDSCard("maul");
        var obi = scn.GetLSCard("obi");
        var querying = scn.game().getModifiersQuerying();
        assertTrue(querying.hasAnyImmunityToAttrition(scn.gameState(), obi));
        assertTrue(querying.getImmunityToAttritionLessThan(scn.gameState(), speeder) > 99);
        reachDarkBattleActions(scn);
        assertTrue(scn.DSCardActionAvailable(speeder, CANCEL));
        scn.DSUseCardAction(speeder, CANCEL);
        if (scn.DSHasCardChoiceAvailable(obi)) scn.DSChooseCard(obi);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, speeder);
        assertInZone(Zone.AT_LOCATION, maul);
        assertSame(scn.GetDSCard("site"), maul.getAtLocation());
        assertFalse(querying.hasAnyImmunityToAttrition(scn.gameState(), obi));
        assertFalse(scn.DSGetDecision() instanceof CardActionSelectionDecision
                && scn.DSCardActionAvailable(speeder, CANCEL));
        finishBattle(scn);
        assertTrue(querying.hasAnyImmunityToAttrition(scn.gameState(), obi));
    }

    @Test public void MaulsSithSpeederCannotCancelIfMaulIsNotDrivingOrJediIsElsewhere() {
        for (var scn : List.of(battle(false, true), battle(true, false))) {
            reachDarkBattleActions(scn);
            assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("speeder"), CANCEL));
        }
    }

    private void reachDarkBattleActions(VirtualTableScenario scn) {
        for (int i = 0; i < 18; i++) {
            if (scn.DSGetDecision() instanceof CardActionSelectionDecision
                    && scn.GetCurrentDecision().getText().toLowerCase().contains("battle")) return;
            if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected battle decision: " + scn.GetCurrentDecision().getText());
        }
        fail("Dark battle actions not reached");
    }

    private void finishBattle(VirtualTableScenario scn) {
        for (int i = 0; i < 100 && scn.gameState().getBattleState() != null; i++) {
            if (scn.AwaitingDSForceLossPayment()) scn.DSPayRemainingForceLossFromReserveDeck();
            else if (scn.AwaitingLSForceLossPayment()) scn.LSPayRemainingForceLossFromReserveDeck();
            else if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("rebel"))) scn.LSChooseCard(scn.GetLSCard("rebel"));
            else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected battle decision: " + scn.GetCurrentDecision().getText());
        }
        assertNull(scn.gameState().getBattleState());
    }
}
