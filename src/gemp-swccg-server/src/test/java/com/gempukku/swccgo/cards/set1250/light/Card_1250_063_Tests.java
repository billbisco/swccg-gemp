package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1250_063_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("liberty", "1250_63"); put("control", "4_48"); put("senate", "12_75"); put("platform", "1113_3");
            put("bail", "1116_1"); put("palpatine", "12_28"); put("trooper", "1_28"); put("panaka", "12_2");
            put("political", "12_40"); put("planners", "1_60"); put("bay", "8_76"); put("snowspeeder", "3_69");
        }}, new HashMap<>() {{
            put("political", "12_127"); put("nonbg", "1_287"); put("control", "4_139"); put("alter", "1_234");
            put("emperor", "9_109"); put("fear", "1105_36");
        }}, 25, 25, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test
    public void LibertyDiesMetadataMatchesPrinted62PoliticalEffect() {
        var scn = scenario(); var bp = scn.GetLSCard("liberty").getBlueprint();
        assertEquals("So This Is How Liberty Dies", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness()); assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.POLITICAL, bp.getCardSubtype()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals("\"Vote now! Vote now! Vote now!\"", bp.getLore());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.EFFECT); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_5); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }
    @Test
    public void LibertyDiesActuallyDeploysOnlyToSenateNotLandingPlatform() {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); var platform = scn.GetLSCard("platform");
        scn.MoveLocationToTable(senate); scn.MoveLocationToTable(platform); scn.MoveCardsToHand(scn.GetLSCard("liberty"));
        scn.SkipToLSTurn(Phase.DEPLOY); scn.LSDeployCard(scn.GetLSCard("liberty"));
        assertTrue(scn.LSHasCardChoicesAvailable(senate)); assertFalse(scn.LSHasCardChoicesAvailable(platform));
        scn.LSChooseCard(senate); scn.PassAllResponses(); assertEquals(senate, scn.GetLSCard("liberty").getAttachedTo());
    }
    @Test
    public void LibertyDiesDeploymentLosesOnlyYourCoruscantPoliticalEffects() {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); scn.MoveLocationToTable(senate);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("political")); scn.MoveCardsToDSSideOfTable(scn.GetDSCard("political"));
        scn.MoveCardsToLocation(senate, scn.GetLSCard("palpatine")); scn.MoveCardsToHand(scn.GetLSCard("liberty"));
        scn.SkipToLSTurn(Phase.DEPLOY); scn.LSDeployCard(scn.GetLSCard("liberty")); scn.LSChooseCard(senate); scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("political")));
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetDSCard("political").getZone());
        assertEquals(senate, scn.GetLSCard("palpatine").getAtLocation()); assertEquals(senate, scn.GetLSCard("liberty").getAttachedTo());
    }
    @Test
    public void LibertyDiesAlsoLosesCoruscantPoliticalEffectDeployedLater() {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); scn.MoveLocationToTable(senate);
        scn.AttachCardsTo(senate, scn.GetLSCard("liberty")); scn.MoveCardsToHand(scn.GetLSCard("political"));
        scn.SkipToLSTurn(Phase.DEPLOY); scn.LSDeployCard(scn.GetLSCard("political")); scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("political")));
    }
    @Test
    public void LibertyDiesBoostsActualRebelBattlegroundDrainAndPreventsControl() { checkDrain(true, true, true, false, 2, false); }
    @Test
    public void LibertyDiesNeedsRebellionAgendaAtSenate() { checkDrain(false, true, true, false, 1, true); }
    @Test
    public void LibertyDiesNeedsARebelAtDrainLocation() { checkDrain(true, false, true, false, 1, true); }
    @Test
    public void LibertyDiesDoesNotProtectNonBattlegroundDrain() { checkDrain(true, true, false, false, 0, true); }
    @Test
    public void LibertyDiesCountsRebelPassengerAtBattleground() { checkDrain(true, true, true, true, 2, false); }
    private void checkDrain(boolean rebellionHere, boolean rebel, boolean battleground, boolean passenger, int expected, boolean cancelable) {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); scn.MoveLocationToTable(senate);
        scn.AttachCardsTo(senate, scn.GetLSCard("liberty"));
        scn.MoveCardsToLocation(rebellionHere ? senate : scn.GetLSStartingLocation(), scn.GetLSCard("bail"));
        var location = battleground ? scn.GetDSStartingLocation() : scn.GetDSCard("nonbg");
        if (!battleground) scn.MoveLocationToTable(location);
        assertEquals(battleground, Filters.battleground.accepts(scn.game(), location));
        var character = scn.GetLSCard(rebel ? "trooper" : "panaka");
        if (passenger) { scn.MoveCardsToLocation(location, scn.GetLSCard("snowspeeder")); scn.AttachCardsTo(scn.GetLSCard("snowspeeder"), character); }
        else scn.MoveCardsToLocation(location, character);
        scn.MoveCardsToHand(scn.GetDSCard("control")); scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSForceDrainAt(location);
        assertEquals(cancelable, scn.DSCardPlayAvailable(scn.GetDSCard("control"), "Cancel Force drain"));
        if (cancelable) { scn.DSPlayCard(scn.GetDSCard("control"), "Cancel Force drain"); scn.PassAllResponses(); assertFalse(scn.IsActiveForceDrain()); }
        else { scn.DSPass(); scn.PassForceDrainStartResponses(); assertEquals(expected, scn.GetForceDrainTotal()); }
    }
    @Test
    public void LibertyDiesDrainBonusExpiresWhenRebellionCharacterLeavesSenate() {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); scn.MoveLocationToTable(senate);
        scn.AttachCardsTo(senate, scn.GetLSCard("liberty")); scn.MoveCardsToLocation(senate, scn.GetLSCard("bail"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("trooper"));
        assertEquals(2, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), scn.GetDSStartingLocation(), scn.LS), scn.epsilon);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("bail"));
        scn.SkipToLSTurn(Phase.CONTROL); scn.LSForceDrainAt(scn.GetDSStartingLocation()); scn.DSPass(); scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal());
    }
    @Test
    public void LibertyDiesCannotBeCanceledByActualAlter() {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); var bay = scn.GetLSCard("bay");
        scn.MoveLocationToTable(senate); scn.MoveLocationToTable(bay);
        scn.AttachCardsTo(senate, scn.GetLSCard("liberty")); scn.AttachCardsTo(bay, scn.GetLSCard("planners"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("emperor")); scn.MoveCardsToHand(scn.GetDSCard("alter"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.PrepareDSDestiny(3); scn.DSPlayCard(scn.GetDSCard("alter"), "Draw destiny to cancel Effect");
        if (scn.DSDecisionAvailable("Choose Effect")) { assertFalse(scn.DSHasCardChoicesAvailable(scn.GetLSCard("liberty"))); scn.DSChooseCard(scn.GetLSCard("planners")); }
        if (scn.DSDecisionAvailable("Choose a highest-ability character")) scn.DSChooseCard(scn.GetDSCard("emperor"));
        scn.PassAllResponses(); assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("planners")));
        assertEquals(senate, scn.GetLSCard("liberty").getAttachedTo());
    }
    @Test
    public void LibertyDiesCannotBePlacedInUsedByActualFearDestiny() { checkFear(true); }
    @Test
    public void LibertyDiesUsedProtectionDoesNotPreventOtherEffectBeingPlaced() { checkFear(false); }
    private void checkFear(boolean protectedEffect) {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); var bay = scn.GetLSCard("bay");
        scn.MoveLocationToTable(senate); scn.MoveLocationToTable(bay);
        scn.AttachCardsTo(senate, scn.GetLSCard("liberty")); scn.AttachCardsTo(bay, scn.GetLSCard("planners"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("emperor"), scn.GetLSCard("trooper"));
        scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation());
        var fear = scn.GetDSCard("fear"); scn.MoveCardsToTopOfDSReserveDeck(fear); scn.SkipToPowerSegment();
        assertTrue(scn.DSDecisionAvailable("battle destiny?")); scn.DSChooseYes();
        boolean offered = false;
        for (int i = 0; i < 32; i++) {
            if (scn.GetADParam(scn.DS, "cardId") != null && scn.GetADParam(scn.DS, "actionText") != null
                    && scn.DSCardActionAvailable(fear, "Place Effect in Used Pile")) { offered = true; break; }
            if (scn.DSDecisionAvailable("optional")) scn.DSPass();
            else if (scn.LSDecisionAvailable("optional")) scn.LSPass(); else break;
        }
        assertTrue(offered); scn.DSUseCardAction(fear, "Place Effect in Used Pile");
        assertTrue(scn.DSDecisionAvailable("Choose Effect")); scn.DSChooseCard(scn.GetLSCard(protectedEffect ? "liberty" : "planners")); scn.PassAllResponses();
        assertEquals(senate, scn.GetLSCard("liberty").getAttachedTo());
        assertEquals(!protectedEffect, scn.GetLSUsedPile().contains(scn.GetLSCard("planners")));
    }

    @Test
    public void LibertyDiesAlsoPreventsOwnerCancelingProtectedDrain() { checkOwnerControl(true); }
    @Test
    public void LibertyDiesOwnerCanCancelDrainWithoutRebellionCondition() { checkOwnerControl(false); }
    private void checkOwnerControl(boolean rebellionHere) {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); scn.MoveLocationToTable(senate);
        scn.AttachCardsTo(senate, scn.GetLSCard("liberty"));
        scn.MoveCardsToLocation(rebellionHere ? senate : scn.GetLSStartingLocation(), scn.GetLSCard("bail"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("trooper"));
        var control = scn.GetLSCard("control"); scn.MoveCardsToHand(control); scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSForceDrainAt(scn.GetDSStartingLocation()); scn.DSPass();
        boolean available = scn.GetADParam(scn.LS, "cardId") != null && scn.GetADParam(scn.LS, "actionText") != null
                && scn.LSCardPlayAvailable(control, "Cancel Force drain");
        assertEquals(!rebellionHere, available);
        if (available) { scn.LSPlayCard(control, "Cancel Force drain"); scn.PassAllResponses(); assertFalse(scn.IsActiveForceDrain()); }
        else { scn.PassForceDrainStartResponses(); assertEquals(2, scn.GetForceDrainTotal()); }
    }
    @Test
    public void LibertyDiesDoesNotBoostOrProtectDarkDrain() {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); scn.MoveLocationToTable(senate);
        scn.AttachCardsTo(senate, scn.GetLSCard("liberty")); scn.MoveCardsToLocation(senate, scn.GetLSCard("bail"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("emperor"));
        var control = scn.GetLSCard("control"); scn.MoveCardsToHand(control); scn.SkipToDSTurn(Phase.CONTROL);
        assertEquals(1, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), scn.GetDSStartingLocation(), scn.DS), scn.epsilon);
        scn.DSForceDrainAt(scn.GetDSStartingLocation()); assertTrue(scn.LSCardPlayAvailable(control, "Cancel Force drain"));
        scn.LSPlayCard(control, "Cancel Force drain"); scn.PassAllResponses(); assertFalse(scn.IsActiveForceDrain());
    }
}
