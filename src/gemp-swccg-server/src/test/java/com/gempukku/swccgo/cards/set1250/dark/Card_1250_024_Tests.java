package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1250_024_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("luke", "1_19"); put("leia", "1_17"); put("alter", "1_71"); put("controlEffect", "1_45"); }},
                new HashMap<>() {{ put("effect", "1250_24"); put("controlEffect", "1_224"); put("droid", "12_104"); put("second", "12_104"); put("third", "12_104"); put("unique", "12_114"); put("ordinary", "14_74"); put("trooper", "1_194"); put("pilot", "3_87"); put("ship", "1_299"); put("system", "1_282"); put("vehicle", "3_154"); put("scum", "6_149"); put("audience", "6_162"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test public void WhereAreThoseDroidekasStatsIconsAndKeywordsMatchPrintedImage() {
        var scn = scenario(); var bp = scn.GetDSCard("effect").getBlueprint();
        assertEquals("Where Are Those Droidekas?!", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(4, bp.getDestiny(), scn.epsilon); assertNull(bp.getDeployCost());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet()); assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.EFFECT); add(Icon.REFLECTIONS_III); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_5); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
        assertEquals("When two Jedi are attempting to breach your bridge, even a destroyer droid's response time seems far too slow.", bp.getLore());
    }
    private VirtualTableScenario active(boolean effect) {
        var scn = scenario(); scn.StartGame();
        if (effect) scn.MoveCardsToDSSideOfTable(scn.GetDSCard("effect"));
        return scn;
    }
    @Test public void WhereAreThoseDroidekasActuallyDeploysOnTableWithoutForceCost() {
        var scn = active(false); var effect = scn.GetDSCard("effect");
        scn.MoveCardsToDSHand(effect); scn.SkipToDSTurn(Phase.DEPLOY); int before = scn.GetDSForcePileCount();
        scn.DSDeployCard(effect); scn.PassAllResponses(); assertInZone(Zone.SIDE_OF_TABLE, effect); assertEquals(before, scn.GetDSForcePileCount());
    }
    @Test public void WhereAreThoseDroidekasModifiesOnlyNonuniqueDestroyerDeployAndForfeit() {
        var scn = active(false); var droid = scn.GetDSCard("droid"); var unique = scn.GetDSCard("unique"); var ordinary = scn.GetDSCard("ordinary");
        scn.MoveCardsToDSHand(droid, unique, ordinary); scn.SkipToDSTurn(Phase.DEPLOY);
        int uniqueCost = scn.GetDeployCost(unique), ordinaryCost = scn.GetDeployCost(ordinary);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("effect"));
        assertEquals(2, scn.GetDeployCost(droid)); assertEquals(uniqueCost, scn.GetDeployCost(unique)); assertEquals(ordinaryCost, scn.GetDeployCost(ordinary));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), droid, unique, ordinary);
        assertEquals(4, scn.GetForfeit(droid)); assertEquals(4, scn.GetForfeit(unique)); assertEquals(2, scn.GetForfeit(ordinary));
        scn.MoveCardsToDSHand(scn.GetDSCard("effect")); assertEquals(3, scn.GetForfeit(droid));
        scn.MoveCardsToDSHand(droid); assertEquals(3, scn.GetDeployCost(droid));
    }
    private void deployment(int force, boolean canDeploy) {
        var scn = active(true); var droid = scn.GetDSCard("droid");
        scn.MoveCardsToDSHand(droid); scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(scn.gameState().getForcePile(scn.DS).toArray(com.gempukku.swccgo.game.PhysicalCardImpl[]::new));
        scn.EnsureDSForcePile(force); scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(canDeploy, scn.DSDeployAvailable(droid));
        if (canDeploy) { scn.DSDeployCard(droid); scn.DSChooseCard(scn.GetDSStartingLocation()); scn.PassAllResponses(); assertInZone(Zone.AT_LOCATION, droid); assertEquals(force - 2, scn.GetDSForcePileCount()); }
    }
    @Test public void WhereAreThoseDroidekasNonuniqueDroidDeploysForExactlyTwoForce() { deployment(2, true); }
    @Test public void WhereAreThoseDroidekasOneForceCannotPayReducedDeployCost() { deployment(1, false); }
    private VirtualTableScenario defense(int here, int elsewhere) {
        var scn = active(true); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke")); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leia"));
        String[] names = {"droid", "second", "third"};
        for (int i = 0; i < here; i++) scn.MoveCardsToLocation(site, scn.GetDSCard(names[i]));
        for (int i = 0; i < elsewhere; i++) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard(names[here + i]));
        scn.SkipToDSTurn(Phase.CONTROL); return scn;
    }
    @Test public void WhereAreThoseDroidekasNoPresentDroidsLeavesDefenseUnchanged() { var scn = defense(0, 0); assertEquals(4, scn.GetDefense(scn.GetLSCard("luke"))); }
    @Test public void WhereAreThoseDroidekasOnePresentDroidReducesDefenseByOne() { var scn = defense(1, 0); assertEquals(3, scn.GetDefense(scn.GetLSCard("luke"))); }
    @Test public void WhereAreThoseDroidekasMultipleDroidsCountOnlyAtAffectedCharactersPlace() {
        var scn = defense(2, 1); assertEquals(2, scn.GetDefense(scn.GetLSCard("luke"))); assertEquals(2, scn.GetDefense(scn.GetLSCard("leia")));
        assertEquals(3, scn.GetDefense(scn.GetDSCard("droid")));
    }
    @Test public void WhereAreThoseDroidekasDroidAtAnotherSiteDoesNotReduceHere() { var scn = defense(0, 1); assertEquals(4, scn.GetDefense(scn.GetLSCard("luke"))); assertEquals(2, scn.GetDefense(scn.GetLSCard("leia"))); }
    @Test public void WhereAreThoseDroidekasOwnNondestroyerAtAnySiteDisablesDefenseClauseUntilItLeaves() {
        var scn = defense(1, 0); var trooper = scn.GetDSCard("trooper");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), trooper); assertEquals(4, scn.GetDefense(scn.GetLSCard("luke")));
        scn.MoveCardsToDSHand(trooper); assertEquals(3, scn.GetDefense(scn.GetLSCard("luke")));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("ordinary")); assertEquals(4, scn.GetDefense(scn.GetLSCard("luke")));
    }
    @Test public void WhereAreThoseDroidekasNondestroyerAtSystemDoesNotDisableSiteClause() {
        var scn = defense(1, 0); var ship = scn.GetDSCard("ship"); var pilot = scn.GetDSCard("pilot");
        scn.MoveLocationToTable(scn.GetDSCard("system")); scn.MoveCardsToLocation(scn.GetDSCard("system"), ship); scn.BoardAsPilot(ship, pilot);
        assertEquals(3, scn.GetDefense(scn.GetLSCard("luke")));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), pilot); assertEquals(4, scn.GetDefense(scn.GetLSCard("luke")));
    }
    @Test public void WhereAreThoseDroidekasDroidAboardVehicleIsNotPresentWithCharactersOutside() {
        var scn = defense(1, 0); var droid = scn.GetDSCard("droid"); var vehicle = scn.GetDSCard("vehicle");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vehicle); scn.BoardAsPassenger(vehicle, droid);
        assertEquals(4, scn.GetDefense(scn.GetLSCard("luke"))); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), droid); assertEquals(3, scn.GetDefense(scn.GetLSCard("luke")));
    }
    @Test public void WhereAreThoseDroidekasDefenseReturnsWhenEffectLeavesTable() {
        var scn = defense(1, 0); scn.MoveCardsToDSHand(scn.GetDSCard("effect")); assertEquals(4, scn.GetDefense(scn.GetLSCard("luke")));
    }
    private void weapon(boolean effect, int destiny, boolean hit) {
        var scn = active(effect); var site = scn.GetDSStartingLocation(); var droid = scn.GetDSCard("droid"); var luke = scn.GetLSCard("luke");
        scn.MoveCardsToLocation(site, droid, luke); scn.SkipToDSTurn(Phase.BATTLE); scn.DSInitiateBattle(site); scn.PassAllResponses();
        scn.PrepareDSDestiny(destiny); scn.DSUseCardAction(droid, "Fire"); scn.DSChooseCard(luke);
        scn.PassForceUseResponses(); scn.PassWeaponFireWithDestinyDraw(); assertEquals(hit, luke.isHit());
        if (scn.AwaitingLSForceLossPayment()) scn.LSPayForceLossFromReserveDeck();
    }
    @Test public void WhereAreThoseDroidekasActualWeaponMissesBelowReducedDefense() { weapon(true, 2, false); }
    @Test public void WhereAreThoseDroidekasActualWeaponMissesEqualToReducedDefense() { weapon(true, 3, false); }
    @Test public void WhereAreThoseDroidekasActualWeaponHitsAboveReducedDefense() { weapon(true, 4, true); }
    @Test public void WhereAreThoseDroidekasAbsentLeavesOriginalWeaponThreshold() { weapon(false, 4, false); }
    private void resolveScum(VirtualTableScenario scn) {
        for (int i = 0; i < 10; i++) {
            scn.PassAllResponses();
            if (scn.DSDecisionAvailable("Required responses")) scn.DSChooseAction("Cancel Scum And Villainy");
            else if (scn.DSDecisionAvailable("Target card to cancel")) scn.DSChooseCard(scn.GetDSCard("scum"));
            else return;
        }
        fail("Scum cancellation did not resolve");
    }
    @Test public void WhereAreThoseDroidekasActuallyCancelsExistingScumWhenDeployed() {
        var scn = active(false); var effect = scn.GetDSCard("effect");
        scn.MoveLocationToTable(scn.GetDSCard("audience")); scn.AttachCardsTo(scn.GetDSCard("audience"), scn.GetDSCard("scum"));
        scn.MoveCardsToDSHand(effect); scn.SkipToDSTurn(Phase.DEPLOY); scn.DSDeployCard(effect); resolveScum(scn);
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("scum")); assertInZone(Zone.SIDE_OF_TABLE, effect);
    }
    @Test public void WhereAreThoseDroidekasCancelsNewScumBeforeItDeploys() {
        var scn = active(true); var scum = scn.GetDSCard("scum");
        scn.MoveLocationToTable(scn.GetDSCard("audience")); scn.MoveCardsToDSHand(scum); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(scum); scn.DSChooseCard(scn.GetDSCard("audience")); resolveScum(scn); assertInZone(Zone.LOST_PILE, scum);
    }
    @Test public void WhereAreThoseDroidekasAbsentAllowsActualScumDeployment() {
        var scn = active(false); var scum = scn.GetDSCard("scum");
        scn.MoveLocationToTable(scn.GetDSCard("audience")); scn.MoveCardsToDSHand(scum); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(scum); scn.DSChooseCard(scn.GetDSCard("audience")); scn.PassAllResponses(); assertEquals(scn.GetDSCard("audience"), scum.getAttachedTo());
    }
    @Test public void WhereAreThoseDroidekasCannotBeChosenByActualAlter() {
        var scn = active(true); scn.MoveCardsToLSSideOfTable(scn.GetLSCard("controlEffect")); scn.MoveCardsToDSSideOfTable(scn.GetDSCard("controlEffect"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke")); scn.MoveCardsToLSHand(scn.GetLSCard("alter"));
        scn.SkipToLSTurn(Phase.CONTROL); scn.LSPlayCard(scn.GetLSCard("alter"));
        assertTrue(scn.LSHasCardChoiceNotAvailable(scn.GetDSCard("effect"))); assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("controlEffect")));
    }
}
