package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1250_005_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("falcon", "1_143"); put("cannon", "1_159"); put("luke", "1_19"); put("order", "14_1"); }},
                new HashMap<>() {{ put("dofine", "1250_5"); put("flagship", "14_114"); put("otherShip", "1_299"); put("system", "1_282"); put("order", "14_67"); put("wrongOrder", "9_92"); put("gunray", "12_112"); put("haako", "12_117"); put("trooper", "1_194"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test public void DaultayDofineStatsIconsAndKeywordsMatchPrintedImage() {
        var scn = scenario(); var bp = scn.GetDSCard("dofine").getBlueprint();
        assertEquals("Daultay Dofine", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(2, bp.getDestiny(), scn.epsilon); assertEquals(3, bp.getDeployCost(), scn.epsilon);
        assertEquals(2, bp.getPower(), scn.epsilon); assertEquals(3, bp.getAbility(), scn.epsilon);
        assertEquals(4, bp.getForfeit(), scn.epsilon); assertTrue(bp.hasPersona(Persona.DOFINE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet()); assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.REPUBLIC); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.REPUBLIC); add(Icon.PILOT); add(Icon.CORUSCANT); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_5); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }
    private VirtualTableScenario ships(boolean pilot, boolean order) {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetDSCard("flagship"), scn.GetDSCard("otherShip"), scn.GetLSCard("falcon"));
        scn.BoardAsPilot(scn.GetLSCard("falcon"), scn.GetLSCard("luke"));
        if (pilot) scn.BoardAsPilot(scn.GetDSCard("flagship"), scn.GetDSCard("dofine"));
        else scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("dofine"));
        if (order) scn.MoveCardsToDSSideOfTable(scn.GetDSCard("order"));
        scn.SkipToDSTurn(Phase.BATTLE); return scn;
    }
    @Test public void DaultayDofineAddsThreePowerOnlyWhilePiloting() {
        var scn = ships(true, false); var flagship = scn.GetDSCard("flagship"); var dofine = scn.GetDSCard("dofine");
        assertEquals(9, scn.GetPower(flagship)); scn.MoveCardsToDSHand(dofine);
        assertEquals(6, scn.GetPower(flagship)); scn.BoardAsPassenger(flagship, dofine);
        assertEquals(6, scn.GetPower(flagship)); scn.MoveCardsToDSHand(dofine);
        scn.BoardAsPilot(scn.GetDSCard("otherShip"), dofine); assertEquals(4, scn.GetPower(scn.GetDSCard("otherShip")));
    }
    @Test public void DaultayDofineGrantsFlagshipImmunityWhileAtAnotherLocation() {
        var scn = ships(false, false); var q = scn.game().getModifiersQuerying();
        assertEquals(9, q.getImmunityToAttritionLessThan(scn.gameState(), scn.GetDSCard("flagship")), scn.epsilon);
        assertEquals(0, q.getImmunityToAttritionLessThan(scn.gameState(), scn.GetDSCard("otherShip")), scn.epsilon);
        scn.MoveCardsToDSHand(scn.GetDSCard("dofine"));
        assertEquals(0, q.getImmunityToAttritionLessThan(scn.gameState(), scn.GetDSCard("flagship")), scn.epsilon);
    }
    private void powerReduction(VirtualTableScenario scn, boolean protectedShip) {
        var ship = scn.GetDSCard("flagship"); var q = scn.game().getModifiersQuerying();
        assertEquals(protectedShip, q.isProhibitedFromHavingPowerReduced(scn.gameState(), ship, scn.LS));
        assertEquals(protectedShip, q.isProhibitedFromHavingPowerReduced(scn.gameState(), ship, scn.DS));
        int power = scn.GetPower(ship);
        scn.ApplyAdHocModifier(new PowerModifier(scn.GetLSCard("falcon"), ship, -2));
        scn.DSPass();
        assertEquals(power - (protectedShip ? 0 : 2), scn.GetPower(ship));
    }
    @Test public void DaultayDofineProtectsPilotedFlagshipWithOwnTheedOrder() { powerReduction(ships(true, true), true); }
    @Test public void DaultayDofineDoesNotProtectWithoutOrder() { powerReduction(ships(true, false), false); }
    @Test public void DaultayDofineDoesNotProtectWithoutPiloting() { powerReduction(ships(false, true), false); }
    @Test public void DaultayDofineDoesNotProtectWithWrongSetOrder() {
        var scn = ships(true, false); scn.MoveCardsToDSSideOfTable(scn.GetDSCard("wrongOrder")); powerReduction(scn, false);
    }
    @Test public void DaultayDofineProtectionEndsWhenOrderLeavesTable() {
        var scn = ships(true, true); scn.MoveCardsToDSHand(scn.GetDSCard("order")); powerReduction(scn, false);
    }
    private void weapon(boolean protection) {
        var scn = ships(true, protection); var cannon = scn.GetLSCard("cannon");
        scn.AttachCardsTo(scn.GetLSCard("falcon"), cannon);
        scn.DSInitiateBattle(scn.GetDSCard("system")); scn.PassAllResponses();
        scn.DSPass(); assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(cannon)); scn.LSUseCardAction(cannon, "Fire");
        assertEquals(!protection, scn.LSHasCardChoiceAvailable(scn.GetDSCard("flagship")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetDSCard("otherShip")));
    }
    @Test public void DaultayDofinePreventsActualWeaponTargetingWithOrder() { weapon(true); }
    @Test public void DaultayDofineLeavesWeaponTargetingWithoutOrder() { weapon(false); }
    private boolean reserveChoiceSelectable(VirtualTableScenario scn, String blueprint) {
        String[] cards = scn.GetADParam(scn.DS, "blueprintId");
        String[] selectable = scn.GetADParam(scn.DS, "selectable");
        assertEquals(cards.length, selectable.length);
        for (int i = 0; i < cards.length; i++) {
            if (blueprint.equals(cards[i]) && Boolean.parseBoolean(selectable[i])) return true;
        }
        return false;
    }
    private VirtualTableScenario retrieve(String target) {
        var scn = scenario(); var dofine = scn.GetDSCard("dofine");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), dofine); scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("gunray"), scn.GetDSCard("haako"), scn.GetDSCard("trooper"));
        assertTrue(scn.DSCardActionAvailable(dofine, "Take Gunray or Haako into hand"));
        scn.DSUseCardAction(dofine, "Take Gunray or Haako into hand"); scn.PassAllResponses(); scn.PassCardAndForceUseResponses();
        assertTrue(reserveChoiceSelectable(scn, "12_112"));
        assertTrue(reserveChoiceSelectable(scn, "12_117"));
        assertFalse(reserveChoiceSelectable(scn, "1_194"));
        scn.DSChooseCard(scn.GetDSCard(target)); scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetDSCard(target));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(dofine, "Take Gunray or Haako into hand")); return scn;
    }
    @Test public void DaultayDofineTakesGunrayFromReserveOncePerGame() { retrieve("gunray"); }
    @Test public void DaultayDofineTakesHaakoFromReserveOncePerGame() { retrieve("haako"); }

    @Test public void DaultayDofineDoesNotProtectWithOpponentsTheedOrder() {
        var scn = ships(true, false); scn.MoveCardsToLSSideOfTable(scn.GetLSCard("order")); powerReduction(scn, false);
    }
    @Test public void DaultayDofinePassengerDoesNotProtectWithOrder() {
        var scn = ships(false, true); scn.BoardAsPassenger(scn.GetDSCard("flagship"), scn.GetDSCard("dofine")); powerReduction(scn, false);
    }
    private void attrition(int destiny, boolean immune) {
        var scn = ships(true, false); var dofine = scn.GetDSCard("dofine"); var ship = scn.GetDSCard("flagship");
        scn.ApplyAdHocModifier(new TotalBattleDestinyModifier(scn.GetLSCard("luke"), destiny - 7, scn.LS));
        scn.PrepareLSDestiny(7); scn.PrepareDSDestiny(7);
        scn.DSInitiateBattle(scn.GetDSCard("system")); scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
        assertEquals(0, scn.GetUnpaidDSBattleDamage()); assertEquals(destiny, scn.GetUnpaidDSAttrition());
        if (immune) {
            assertTrue(scn.DSDecisionAvailable("Choose a card from battle to forfeit (if desired)"));
            scn.DSPass(); assertEquals(Zone.AT_LOCATION, ship.getZone()); assertEquals(ship, dofine.getAttachedTo());
        }
        else {
            assertTrue(scn.DSDecisionAvailable("Choose a card from battle to forfeit"));
            assertFalse(scn.DSDecisionAvailable("(if desired)"));
            scn.DSChooseCard(dofine); scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, dofine);
        }
    }
    @Test public void DaultayDofineFlagshipAndCrewSurviveAttritionBelowNine() { attrition(8, true); }
    @Test public void DaultayDofineDoesNotProtectCrewFromAttritionEqualToNine() { attrition(9, false); }
    @Test public void DaultayDofineDoesNotProtectCrewFromAttritionAboveNine() { attrition(10, false); }
    @Test public void DaultayDofineOncePerGameLimitSurvivesLeavingAndReturning() {
        var scn = retrieve("gunray"); var dofine = scn.GetDSCard("dofine");
        scn.MoveCardsToDSHand(dofine); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), dofine);
        scn.SkipToLSTurn(Phase.CONTROL); scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(dofine, "Take Gunray or Haako into hand"));
    }
}
