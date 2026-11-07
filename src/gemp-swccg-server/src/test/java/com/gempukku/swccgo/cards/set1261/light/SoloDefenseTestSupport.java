package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public abstract class SoloDefenseTestSupport {
    protected static final String PROTECT = "Protect solo defender";
    protected final String printing;
    private final String matching;
    private final String inverse;
    private final String site;

    protected SoloDefenseTestSupport(String printing, String matching, String inverse, String site) {
        this.printing = printing;
        this.matching = matching;
        this.inverse = inverse;
        this.site = site;
    }

    protected VirtualTableScenario fixture() {
        var ls = new HashMap<>(Map.of("interrupt", printing, "person", matching, "inverse", inverse, "other", "2_18", "daughter", "8_8"));
        var ds = new HashMap<>(Map.of("vader", "1_168", "sense", "1_267", "saber", "1_324"));
        var scn = new VirtualTableScenario(ls, ds, 40, 40, StartingSetup.LSStartingLocation(site),
                StartingSetup.DefaultDSGroundLocation, StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        return scn;
    }

    @Test public void paidDeploySearchMovesOnlyTheNamedPersonaToTheTable() {
        var scn = fixture(); var card = scn.GetLSCard("interrupt"); var person = scn.GetLSCard("person");
        var wrong = scn.GetLSCard("other");
        scn.MoveCardsToLSHand(card);
        scn.SkipToLSTurn(Phase.DEPLOY); scn.EnsureLSForcePile(12);
        scn.MoveCardsToTopOfLSReserveDeck(person, wrong);
        scn.gameState().markCardsRevealedFromPile(java.util.List.of(wrong));
        int before = scn.GetLSForcePileCount();
        scn.LSPlayCard(card, "Deploy from Reserve Deck");
        scn.PassForceUseResponses(); scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(person)) scn.LSChooseCard(person);
        if (scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation())) scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassForceUseResponses(); scn.PassAllResponses();
        assertEquals(scn.GetLSStartingLocation(), person.getAtLocation());
        assertTrue(scn.GetLSLostPile().contains(card));
        assertTrue(before > scn.GetLSForcePileCount());
        assertFalse(scn.gameState().isCardRevealedFromPile(wrong));
    }

    @Test public void paidRetrievalFindsTheNamedCardBelowAnUnrelatedTopCard() {
        var scn = fixture(); var card = scn.GetLSCard("interrupt");
        var person = scn.GetLSCard("person"); var wrong = scn.GetLSCard("other");
        scn.MoveCardsToLSHand(card); scn.MoveCardsToTopOfLSLostPile(person, wrong);
        scn.SkipToLSTurn(Phase.CONTROL); scn.EnsureLSForcePile(4);
        int before = scn.GetLSForcePileCount();
        scn.LSPlayCard(card, "Retrieve from Lost Pile");
        scn.PassForceUseResponses(); scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(person)) scn.LSChooseCard(person);
        scn.PassAllResponses();
        assertTrue(scn.GetLSUsedPile().contains(person));
        assertTrue(scn.GetLSLostPile().contains(wrong));
        assertTrue(scn.GetLSLostPile().contains(card));
        assertEquals(before - 1, scn.GetLSForcePileCount());
    }

    protected VirtualTableScenario battle(String defender, boolean alone, boolean defending) {
        return battle(defender, alone, defending, false);
    }

    private VirtualTableScenario battle(String defender, boolean alone, boolean defending, boolean armedVader) {
        var scn = fixture(); var at = scn.GetLSStartingLocation(); var vader = scn.GetDSCard("vader");
        scn.MoveCardsToLocation(at, scn.GetLSCard(defender), vader);
        if (armedVader) scn.AttachCardsTo(vader, scn.GetDSCard("saber"));
        if (!alone) scn.MoveCardsToLocation(at, scn.GetLSCard("other"));
        scn.MoveCardsToLSHand(scn.GetLSCard("interrupt"));
        scn.MoveCardsToDSHand(scn.GetDSCard("sense"));
        if (defending) {
            scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureDSForcePile(5); scn.DSInitiateBattle(at);
        } else {
            scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(5); scn.LSInitiateBattle(at);
        }
        return scn;
    }

    @Test public void actualSoloDefensePreventsForfeitReductionAndSchedulesBothKindsOfDestiny() {
        var scn = battle("person", true, true); var card = scn.GetLSCard("interrupt"); var person = scn.GetLSCard("person");
        assertTrue(scn.LSCardPlayAvailable(card, PROTECT));
        scn.LSPlayCard(card, PROTECT);
        if (scn.LSHasCardChoiceAvailable(person)) scn.LSChooseCard(person);
        assertFalse(scn.GetDSAvailableActions().stream().anyMatch(x -> x.contains("Draw destiny to cancel")));
        scn.PassAllResponses();
        var query = scn.game().getModifiersQuerying(); var state = scn.gameState();
        assertTrue(query.isProhibitedFromHavingForfeitReduced(state, person));
        assertEquals(1, query.getNumDestinyDrawsToTotalPowerOnly(state, scn.LS, false, false));
        assertEquals(1, query.getNumBattleDestinyDrawsIfUnableToOtherwise(state, scn.LS));
        assertTrue(scn.GetLSLostPile().contains(card));
    }

    private void nativeDraws(boolean protect) {
        nativeDraws("person", protect, false);
    }

    protected void nativeDraws(String key, boolean protect, boolean hasOrdinaryBattleDestiny) {
        var scn = battle(key, true, true);
        var person = scn.GetLSCard(key); var interrupt = scn.GetLSCard("interrupt");
        assertEquals(hasOrdinaryBattleDestiny, scn.GetAbility(person) >= 4);
        // Keep this fixture about the Interrupt's two draws; Han's own redraw requires spending Force.
        scn.MoveCardsToTopOfLSUsedPile(scn.GetLSForcePile().stream()
                .map(PhysicalCardImpl.class::cast).toArray(PhysicalCardImpl[]::new));
        assertEquals(0, scn.GetLSForcePileCount());
        if (protect) {
            scn.LSPlayCard(interrupt, PROTECT);
            if (scn.LSHasCardChoiceAvailable(person)) scn.LSChooseCard(person);
            scn.PassAllResponses();
        }
        var power = scn.GetLSDestiny(2); var battle = scn.GetLSDestiny(3);
        // The helper puts the last listed card on top. Power-only destiny resolves first.
        scn.MoveCardsToTopOfLSReserveDeck(battle, power);
        scn.PrepareDSDestiny(0);
        scn.PassBattleStartResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassPowerSegmentActions();
        scn.PassDestinyDrawResponses();
        scn.SkipBattleDestinyDraws(true);
        var state = scn.gameState().getBattleState();
        var trace = scn.GetCurrentDecision().getText() + " " + scn.gameState().getLastMessages();
        assertEquals(trace, protect ? 1 : 0, state.getNumDestinyToTotalPowerDrawn(scn.LS));
        assertEquals(protect || hasOrdinaryBattleDestiny ? 1 : 0, state.getNumUncanceledBattleDestinyDrawn(scn.LS));
        assertEquals(protect ? 3 : hasOrdinaryBattleDestiny ? 2 : 0, scn.GetLSTotalDestiny());
        assertEquals(protect || hasOrdinaryBattleDestiny, scn.GetLSUsedPile().contains(power));
        assertEquals(protect, scn.GetLSUsedPile().contains(battle));
    }

    @Test public void actualSoloDefenderDrawsSeparatePowerAndFallbackBattleDestinies() { nativeDraws(true); }
    @Test public void theSameDefenderWithoutTheInterruptDrawsNeitherDestiny() { nativeDraws(false); }

    private void nativeLightsaber(boolean protect) {
        var scn = battle("person", true, true, true);
        var person = scn.GetLSCard("person"); var saber = scn.GetDSCard("saber");
        int printedForfeit = scn.GetForfeit(person);
        assertTrue(printedForfeit > 0);
        if (protect) {
            scn.LSPlayCard(scn.GetLSCard("interrupt"), PROTECT);
            if (scn.LSHasCardChoiceAvailable(person)) scn.LSChooseCard(person);
        }
        scn.PassAllResponses();
        if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass();
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(3), scn.GetDSDestiny(4));
        scn.DSUseCardAction(saber, "Fire");
        if (scn.DSHasCardChoiceAvailable(person)) scn.DSChooseCard(person);
        scn.PassAllResponses(); scn.PassWeaponFireWithDestinyDraw(2);
        assertTrue(person.isHit());
        assertEquals(protect ? printedForfeit : 0, scn.GetForfeit(person));
    }

    @Test public void actualVaderLightsaberHitsButCannotReduceTheProtectedForfeit() { nativeLightsaber(true); }
    @Test public void theSameActualLightsaberReducesAnUnprotectedForfeitToZero() { nativeLightsaber(false); }

    @Test public void cannotProtectWhenNotAloneOrWhenLightInitiates() {
        var withAlly = battle("person", false, true);
        assertFalse(withAlly.GetLSAvailableActions().stream().anyMatch(x -> x.contains(PROTECT)));
        var attacking = battle("person", true, false);
        assertFalse(attacking.GetLSAvailableActions().stream().anyMatch(x -> x.contains(PROTECT)));
    }
}
