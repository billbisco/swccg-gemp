package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1263_019_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("luke", "1_19"); put("red1", "1_144"); }},
                new HashMap<>() {{ put("avenger", "1263_19"); put("other", "1263_19"); put("facility", "1117_21");
                    put("vader", "1_168"); put("droid", "1_192"); put("droid2", "2_101"); }},
                40, 40, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void TieAvengerVStatsMatchPrintedCard() {
        var scn = scenario();
        var b = scn.GetDSCard("avenger").getBlueprint();
        assertEquals("TIE Avenger", b.getTitle());
        assertTrue(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness());
        assertEquals(3, b.getDestiny(), scn.epsilon);
        assertEquals(2, b.getDeployCost(), scn.epsilon);
        assertEquals(3, b.getPower(), scn.epsilon);
        assertEquals(4, b.getManeuver(), scn.epsilon);
        assertEquals(3, b.getHyperspeed(), scn.epsilon);
        assertEquals(4, b.getForfeit(), scn.epsilon);
        assertEquals(1, b.getPilotCapacity());
        assertEquals(1, b.getAstromechCapacity());
        assertEquals(CardSubtype.STARFIGHTER, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.STARSHIP)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.STARSHIP, Icon.DAGOBAH, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintModelTypeCheck(b, new ArrayList<>(Arrays.asList(ModelType.TIE_AD)));
    }

    private VirtualTableScenario started(boolean facility) {
        var scn = scenario();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("avenger"));
        if (facility) scn.MoveCardsToDSSideOfTable(scn.GetDSCard("facility"));
        return scn;
    }

    @Test
    public void TieAvengerVPilotIsForfeitPlusTwoOnlyWhileFacilityOnTable() {
        var scn = started(false);
        var vader = scn.GetDSCard("vader");
        scn.BoardAsPilot(scn.GetDSCard("avenger"), vader);
        int base = (int) scn.GetForfeit(vader);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("facility"));
        assertEquals(base + 2, scn.GetForfeit(vader), scn.epsilon);
        scn.MoveCardsToDSHand(scn.GetDSCard("facility"));
        assertEquals(base, scn.GetForfeit(vader), scn.epsilon);
    }

    @Test
    public void TieAvengerVDoesNotBoostPilotOfAnotherStarship() {
        var scn = started(true);
        var other = scn.GetDSCard("other");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), other);
        var vader = scn.GetDSCard("vader");
        float before = scn.GetForfeit(vader);
        scn.BoardAsPilot(other, vader);
        // Other TIE Avenger only boosts its own pilot (by 2), not twice.
        assertEquals(before + 2, scn.GetForfeit(vader), scn.epsilon);
    }

    @Test
    public void TieAvengerVCarriesOnePilotAndOneAstromech() {
        var scn = started(false);
        var ship = scn.GetDSCard("avenger");
        scn.BoardAsPilot(ship, scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(scn.GetDSCard("droid2"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(10);
        scn.DSDeployCard(scn.GetDSCard("droid2"));
        if (scn.DSHasCardChoiceAvailable(ship)) scn.DSChooseCard(ship);
        scn.PassAllResponses();
        assertEquals(ship, scn.GetDSCard("droid2").getAttachedTo());
        assertEquals(ship, scn.GetDSCard("vader").getAttachedTo());
    }

    @Test
    public void TieAvengerVDeploysAstromechAboardFromReserveDeck() {
        var scn = started(false);
        var ship = scn.GetDSCard("avenger");
        scn.BoardAsPilot(ship, scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(4);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("droid2"), scn.GetDSCard("droid"), scn.GetDSCard("other"));
        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(ship, "Deploy astromech aboard"));
        scn.DSUseCardAction(ship, "Deploy astromech aboard");
        assertTrue(selectable(scn, "2_101"));
        assertFalse(selectable(scn, "1263_19"));
        scn.DSChooseCard(scn.GetDSCard("droid2"));
        scn.PassAllResponses();
        assertEquals(ship, scn.GetDSCard("droid2").getAttachedTo());
        assertTrue(force > scn.GetDSForcePileCount());
        // Once per turn.
        scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(ship, "Deploy astromech aboard"));
    }

    private boolean selectable(VirtualTableScenario scn, String blueprint) {
        String[] cards = scn.GetADParam(scn.DS, "blueprintId");
        String[] selectable = scn.GetADParam(scn.DS, "selectable");
        if (cards == null) return false;
        for (int i = 0; i < cards.length; i++) {
            if (blueprint.equals(cards[i]) && (selectable == null || Boolean.parseBoolean(selectable[i]))) return true;
        }
        return false;
    }

    private VirtualTableScenario battle(boolean facility) {
        var scn = started(facility);
        var site = scn.GetDSStartingLocation();
        scn.BoardAsPilot(scn.GetDSCard("avenger"), scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(site, scn.GetLSCard("red1"));
        scn.BoardAsPilot(scn.GetLSCard("red1"), scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(2);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        return scn;
    }

    private void lose(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl ship) {
        var action = new TopLevelGameTextAction(ship, scn.DS, ship.getCardId());
        action.appendEffect(new LoseCardFromTableEffect(action, ship));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
    }

    @Test
    public void TieAvengerVMayNotBeTargetedToBeLostBeforeDamageSegmentWithFacility() {
        var scn = battle(true);
        assertFalse(Filters.canBeTargetedBy(scn.GetLSCard("luke"), TargetingReason.TO_BE_LOST).accepts(scn.game(), scn.GetDSCard("avenger")));
    }

    @Test
    public void TieAvengerVMayBeTargetedToBeLostWithoutFacility() {
        var scn = battle(false);
        assertTrue(Filters.canBeTargetedBy(scn.GetLSCard("luke"), TargetingReason.TO_BE_LOST).accepts(scn.game(), scn.GetDSCard("avenger")));
    }

    @Test
    public void TieAvengerVIsNotLostBeforeDamageSegmentWithFacility() {
        var scn = battle(true);
        var ship = scn.GetDSCard("avenger");
        lose(scn, ship);
        scn.PassAllResponses();
        assertAtLocation(scn.GetDSStartingLocation(), ship);
    }

    @Test
    public void TieAvengerVIsLostBeforeDamageSegmentWithoutFacility() {
        var scn = battle(false);
        var ship = scn.GetDSCard("avenger");
        lose(scn, ship);
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(ship)) { scn.DSChooseCard(ship); scn.PassAllResponses(); }
        if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("vader"))) { scn.DSChooseCard(scn.GetDSCard("vader")); scn.PassAllResponses(); }
        assertInZone(Zone.LOST_PILE, ship);
    }

    @Test
    public void TieAvengerVProtectionDoesNotApplyOutsideBattle() {
        var scn = started(true);
        var ship = scn.GetDSCard("avenger");
        scn.SkipToDSTurn(Phase.DEPLOY);
        lose(scn, ship);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, ship);
    }
}
