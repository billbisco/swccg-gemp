package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1250_061_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("wesa", "1250_61"); put("endor", "8_68"); put("naboo", "12_78");
            put("forest", "8_74"); put("plains", "12_79"); put("ewok", "8_12");
            put("gungan", "12_7"); put("gungan2", "12_7"); put("oldGungan", "12_7");
            put("trooper", "1_28"); put("planners", "1_60"); put("bay", "8_76"); put("ship", "2_73"); put("other", "1_126");
        }}, new HashMap<>() {{
            put("emperor", "9_109"); put("lightning", "9_136"); put("lightning2", "9_136");
            put("lightning3", "9_136"); put("alter", "1_234"); put("ship", "1_301");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void WesaReadyMetadataMatchesPrinted64Image() {
        var scn = scenario(); var bp = scn.GetLSCard("wesa").getBlueprint();
        assertEquals("Wesa Ready To Do Our-sa Part", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness()); assertEquals(5, bp.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals("Every Gungan that died on the battle plains gave Amidala and her troops that much more time to capture the Viceroy.", bp.getLore());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.EFFECT); add(Icon.THEED_PALACE); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_5); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test
    public void WesaReadyActuallyDeploysOnEndorSystem() { checkDeploy("endor"); }
    @Test
    public void WesaReadyActuallyDeploysOnNabooSystem() { checkDeploy("naboo"); }
    private void checkDeploy(String target) {
        var scn = scenario(); scn.StartGame(); var wesa = scn.GetLSCard("wesa");
        scn.MoveLocationToTable(scn.GetLSCard("endor")); scn.MoveLocationToTable(scn.GetLSCard("naboo"));
        scn.MoveLocationToTable(scn.GetLSCard("forest")); scn.MoveLocationToTable(scn.GetLSCard("other"));
        scn.MoveCardsToHand(wesa); scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(wesa)); scn.LSDeployCard(wesa);
        assertTrue(scn.LSHasCardChoicesAvailable(scn.GetLSCard("endor")));
        assertTrue(scn.LSHasCardChoicesAvailable(scn.GetLSCard("naboo")));
        assertFalse(scn.LSHasCardChoicesAvailable(scn.GetLSCard("forest")));
        assertFalse(scn.LSHasCardChoicesAvailable(scn.GetLSCard("other")));
        scn.LSChooseCard(scn.GetLSCard(target)); scn.PassAllResponses();
        assertEquals(scn.GetLSCard(target), wesa.getAttachedTo());
    }

    private int totalPower(VirtualTableScenario scn, PhysicalCardImpl location, String player) {
        return (int) scn.game().getModifiersQuerying().getTotalPowerAtLocation(scn.gameState(), location, player, false, false);
    }

    @Test
    public void WesaReadyCountsBothSpeciesAtRelatedSitesAndExpiresWhenTheyLeave() {
        var scn = scenario(); scn.StartGame(); var endor = scn.GetLSCard("endor"); var forest = scn.GetLSCard("forest");
        scn.MoveLocationToTable(endor); scn.MoveLocationToTable(forest);
        scn.MoveCardsToLocation(endor, scn.GetLSCard("ship"));
        int before = totalPower(scn, endor, scn.LS);
        scn.AttachCardsTo(endor, scn.GetLSCard("wesa")); assertEquals(before, totalPower(scn, endor, scn.LS));
        scn.MoveCardsToLocation(forest, scn.GetLSCard("ewok")); assertEquals(before + 1, totalPower(scn, endor, scn.LS));
        scn.MoveCardsToLocation(forest, scn.GetLSCard("gungan")); assertEquals(before + 2, totalPower(scn, endor, scn.LS));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("ewok")); assertEquals(before + 1, totalPower(scn, endor, scn.LS));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("gungan")); assertEquals(before, totalPower(scn, endor, scn.LS));
    }

    @Test
    public void WesaReadyDoesNotCountOtherPlanetNonNativesOrAboardSystemShips() {
        var scn = scenario(); scn.StartGame(); var endor = scn.GetLSCard("endor"); var forest = scn.GetLSCard("forest"); var plains = scn.GetLSCard("plains");
        scn.MoveLocationToTable(endor); scn.MoveLocationToTable(forest); scn.MoveLocationToTable(plains);
        var ship = scn.GetLSCard("ship"); scn.MoveCardsToLocation(endor, ship); int before = totalPower(scn, endor, scn.LS);
        scn.AttachCardsTo(endor, scn.GetLSCard("wesa"));
        scn.MoveCardsToLocation(plains, scn.GetLSCard("gungan")); scn.MoveCardsToLocation(forest, scn.GetLSCard("trooper"));
        scn.AttachCardsTo(ship, scn.GetLSCard("ewok"));
        assertEquals(before, totalPower(scn, endor, scn.LS));
    }

    @Test
    public void WesaReadyAddsToActualSpaceBattlePowerOnlyForLight() {
        var scn = scenario(); scn.StartGame(); var endor = scn.GetLSCard("endor"); var forest = scn.GetLSCard("forest");
        scn.MoveLocationToTable(endor); scn.MoveLocationToTable(forest);
        scn.MoveCardsToLocation(endor, scn.GetLSCard("ship"), scn.GetDSCard("ship"));
        scn.MoveCardsToLocation(forest, scn.GetLSCard("ewok"), scn.GetLSCard("gungan"));
        int ls = totalPower(scn, endor, scn.LS); int ds = totalPower(scn, endor, scn.DS);
        scn.AttachCardsTo(endor, scn.GetLSCard("wesa")); scn.LSStartBattleAndSkipToWeaponsSegment(endor);
        scn.SkipToEndOfPowerSegment(false); assertEquals(ls + 2, scn.GetLSTotalPower()); assertEquals(ds, scn.GetDSTotalPower());
    }

    private void setupLoss(VirtualTableScenario scn) {
        scn.StartGame(); var system = scn.GetLSCard("endor"); scn.MoveLocationToTable(system);
        scn.AttachCardsTo(system, scn.GetLSCard("wesa"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("gungan"), scn.GetLSCard("gungan2"), scn.GetLSCard("ewok"), scn.GetLSCard("trooper"), scn.GetDSCard("emperor"));
        scn.MoveCardsToHand(scn.GetDSCard("lightning"), scn.GetDSCard("lightning2"), scn.GetDSCard("lightning3"));
        scn.DSActivateForceCheat(12); scn.SkipToDSTurn(Phase.CONTROL);
    }
    private void lightning(VirtualTableScenario scn, String interrupt, String victim) {
        if (scn.AwaitingLSControlPhaseActions()) scn.LSPass();
        assertTrue(scn.AwaitingDSControlPhaseActions());
        scn.PrepareDSDestiny(3); scn.DSPlayCard(scn.GetDSCard(interrupt), "Make target lost");
        if (scn.DSDecisionAvailable("Choose Emperor")) scn.DSChooseCard(scn.GetDSCard("emperor"));
        if (scn.DSDecisionAvailable("Choose character")) scn.DSChooseCard(scn.GetLSCard(victim));
        scn.PassDestinyDrawResponses();
    }
    private boolean offer(VirtualTableScenario scn) {
        for (int i = 0; i < 32; i++) {
            if (scn.GetADParam(scn.LS, "cardId") != null && scn.GetADParam(scn.LS, "actionText") != null
                    && scn.LSCardActionAvailable(scn.GetLSCard("wesa"), "Place lost Ewok or Gungan in Used Pile")) return true;
            if (scn.DSDecisionAvailable("optional")) scn.DSPass();
            else if (scn.LSDecisionAvailable("optional")) scn.LSPass();
            else return false;
        }
        return false;
    }
    private void save(VirtualTableScenario scn) {
        assertTrue(offer(scn)); scn.LSUseCardAction(scn.GetLSCard("wesa"), "Place lost Ewok or Gungan in Used Pile"); scn.PassAllResponses();
    }
    @Test
    public void WesaReadyPlacesOnlyJustLostGunganInUsedPile() {
        var scn = scenario(); setupLoss(scn); var old = scn.GetLSCard("oldGungan"); scn.MoveCardsToTopOfLSLostPile(old);
        lightning(scn, "lightning", "gungan"); save(scn);
        assertTrue(scn.GetLSUsedPile().contains(scn.GetLSCard("gungan"))); assertTrue(scn.GetLSLostPile().contains(old));
    }
    @Test
    public void WesaReadyMaySaveJustLostEwokAnywhere() {
        var scn = scenario(); setupLoss(scn); lightning(scn, "lightning", "ewok"); save(scn);
        assertTrue(scn.GetLSUsedPile().contains(scn.GetLSCard("ewok")));
    }
    @Test
    public void WesaReadyCannotSaveNonEwokNonGungan() {
        var scn = scenario(); setupLoss(scn); lightning(scn, "lightning", "trooper"); assertFalse(offer(scn));
        scn.PassAllResponses(); assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("trooper")));
    }
    @Test
    public void WesaReadyMayDeclineSavingJustLostNative() {
        var scn = scenario(); setupLoss(scn); lightning(scn, "lightning", "gungan"); assertTrue(offer(scn));
        scn.LSPass(); scn.PassAllResponses(); assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("gungan")));
    }
    private void forfeitSecondNative(VirtualTableScenario scn) {
        scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetLSStartingLocation());
        scn.PrepareDSDestiny(3); scn.SkipToDamageSegment(true);
        assertTrue(scn.LSDecisionAvailable("card from battle to forfeit")); scn.LSChooseCard(scn.GetLSCard("gungan2"));
    }
    @Test
    public void WesaReadySaveIsOncePerTurnAcrossPhasesAndResetsNextTurn() {
        var scn = scenario(); setupLoss(scn); lightning(scn, "lightning", "gungan"); save(scn);
        forfeitSecondNative(scn); assertFalse(offer(scn)); scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("gungan2")));
        scn.SkipToLSTurn(Phase.CONTROL); scn.SkipToDSTurn(Phase.CONTROL);
        lightning(scn, "lightning3", "ewok"); save(scn); assertTrue(scn.GetLSUsedPile().contains(scn.GetLSCard("ewok")));
    }
    @Test
    public void WesaReadyDecliningDoesNotUseTurnAllowance() {
        var scn = scenario(); setupLoss(scn); lightning(scn, "lightning", "gungan"); assertTrue(offer(scn)); scn.LSPass(); scn.PassAllResponses();
        forfeitSecondNative(scn); save(scn); assertTrue(scn.GetLSUsedPile().contains(scn.GetLSCard("gungan2")));
    }
    @Test
    public void WesaReadyIsExcludedFromActualAlterWhileOrdinaryEffectCanBeCanceled() {
        var scn = scenario(); scn.StartGame(); var system = scn.GetLSCard("endor"); var bay = scn.GetLSCard("bay");
        scn.MoveLocationToTable(system); scn.MoveLocationToTable(bay);
        var wesa = scn.GetLSCard("wesa"); var planners = scn.GetLSCard("planners");
        scn.AttachCardsTo(system, wesa); scn.AttachCardsTo(bay, planners);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("emperor"));
        scn.MoveCardsToHand(scn.GetDSCard("alter")); scn.SkipToDSTurn(Phase.CONTROL); scn.PrepareDSDestiny(3);
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("alter")));
        scn.DSPlayCard(scn.GetDSCard("alter"), "Draw destiny to cancel Effect");
        if (scn.DSDecisionAvailable("Choose Effect")) {
            assertFalse(scn.DSHasCardChoicesAvailable(wesa)); assertTrue(scn.DSHasCardChoicesAvailable(planners)); scn.DSChooseCard(planners);
        }
        if (scn.DSDecisionAvailable("Choose a highest-ability character")) scn.DSChooseCard(scn.GetDSCard("emperor"));
        scn.PassDestinyDrawResponses(); scn.PassAllResponses();
        assertEquals(system, wesa.getAttachedTo()); assertTrue(scn.GetLSLostPile().contains(planners));
    }

    @Test
    public void WesaReadyCanSaveAnActuallyForfeitedEwok() {
        var scn = scenario(); scn.StartGame(); var system = scn.GetLSCard("endor"); scn.MoveLocationToTable(system);
        scn.AttachCardsTo(system, scn.GetLSCard("wesa"));
        var ewok = scn.GetLSCard("ewok");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ewok, scn.GetDSCard("emperor"));
        scn.LSStartBattleAndSkipToWeaponsSegment(scn.GetLSStartingLocation()); scn.PrepareDSDestiny(3); scn.SkipToDamageSegment(true);
        assertTrue(scn.LSDecisionAvailable("card from battle to forfeit")); scn.LSChooseCard(ewok); save(scn);
        assertTrue(scn.GetLSUsedPile().contains(ewok)); assertFalse(scn.GetLSLostPile().contains(ewok));
    }
}
