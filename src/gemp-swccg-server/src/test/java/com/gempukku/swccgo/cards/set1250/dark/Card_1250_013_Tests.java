package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

public class Card_1250_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("luke", "1_19"); put("obi", "1_21"); put("controlEffect", "1_45"); put("leia", "1_17"); put("rifle", "1_153"); put("r2", "1_5"); put("falcon", "1_143"); put("alter", "1_71"); }},
                new HashMap<>() {{
                    put("results", "1250_13");
                    put("invasion", "14_113"); put("controlEffect", "1_224");
                    put("droid", "14_74"); put("plainDroid", "1_193"); put("trooper", "1_194");
                    put("maul", "12_101"); put("vader", "1_168"); put("objective", "10_29");
                    put("ship", "1_299"); put("system", "1_282");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private VirtualTableScenario ScenarioWithInvasionOutOfPlay() {
        var scn = GetScenario();
        scn.MoveOutOfPlay(scn.GetDSCard("invasion"));
        return scn;
    }

    @Test
    public void AtLastWeAreGettingResultsDeploysOnlyWhileInvasionIsOnTable() {
        var scn = ScenarioWithInvasionOutOfPlay();
        var results = scn.GetDSCard("results");
        var blueprint = results.getBlueprint();
        var invasion = scn.GetDSCard("invasion");

        assertEquals("At Last We Are Getting Results", blueprint.getTitle());
        assertTrue(blueprint.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, blueprint.getUniqueness());
        assertEquals(Side.DARK, blueprint.getSide());
        assertEquals(4, blueprint.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(blueprint, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintIconCheck(blueprint, new ArrayList<>() {{
            add(Icon.THEED_PALACE);
            add(Icon.EPISODE_I);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_5);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, blueprint.getExpansionSet());
        assertEquals(Rarity.V, blueprint.getRarity());
        scn.BlueprintKeywordCheck(blueprint, new ArrayList<>());
        assertNull(blueprint.getDeployCost());

        scn.StartGame();
        scn.MoveCardsToDSHand(results);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSDeployAvailable(results));
    }

    @Test
    public void AtLastWeAreGettingResultsDeploysOnTableWhenInvasionIsOnTable() {
        var scn = ScenarioWithInvasionOutOfPlay();
        var results = scn.GetDSCard("results");
        var invasion = scn.GetDSCard("invasion");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(invasion);
        scn.MoveCardsToDSHand(results);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(results));
        scn.DSDeployCard(results);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, results);
    }

    private VirtualTableScenario active(boolean effect) {
        var scn = ScenarioWithInvasionOutOfPlay(); scn.StartGame();
        if (effect) scn.MoveCardsToDSSideOfTable(scn.GetDSCard("results"));
        return scn;
    }
    @Test public void AtLastWeAreGettingResultsAddsDefenseOnlyToOwnPresenceDroidsAndEndsWhenAbsent() {
        var scn = active(false); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("droid"), scn.GetDSCard("plainDroid"), scn.GetDSCard("trooper"), scn.GetLSCard("r2"));
        scn.SkipToDSTurn(Phase.CONTROL);
        int plain = scn.GetDefense(scn.GetDSCard("plainDroid")), trooper = scn.GetDefense(scn.GetDSCard("trooper")), opponent = scn.GetDefense(scn.GetLSCard("r2"));
        assertEquals(2, scn.GetDefense(scn.GetDSCard("droid")));
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("results"));
        assertEquals(4, scn.GetDefense(scn.GetDSCard("droid")));
        assertEquals(plain, scn.GetDefense(scn.GetDSCard("plainDroid")));
        assertEquals(trooper, scn.GetDefense(scn.GetDSCard("trooper")));
        assertEquals(opponent, scn.GetDefense(scn.GetLSCard("r2")));
        scn.MoveCardsToDSHand(scn.GetDSCard("results")); assertEquals(2, scn.GetDefense(scn.GetDSCard("droid")));
    }
    private void weaponThreshold(boolean effect, int destiny, boolean hit) {
        var scn = active(effect); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("droid"), scn.GetLSCard("luke"));
        scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("rifle"));
        scn.SkipToDSTurn(Phase.BATTLE); scn.DSInitiateBattle(site); scn.PassAllResponses(); scn.DSPass();
        scn.PrepareLSDestiny(destiny); scn.LSUseCardAction(scn.GetLSCard("rifle"), "Fire"); scn.LSChooseCard(scn.GetDSCard("droid"));
        scn.PassForceUseResponses(); scn.PassWeaponFireWithDestinyDraw(); assertEquals(hit, scn.GetDSCard("droid").isHit());
    }
    @Test public void AtLastWeAreGettingResultsDefensePreventsHitBelowRaisedDefense() { weaponThreshold(true, 2, false); }
    @Test public void AtLastWeAreGettingResultsDefensePreventsHitEqualToRaisedDefense() { weaponThreshold(true, 3, false); }
    @Test public void AtLastWeAreGettingResultsDefenseAllowsHitAboveRaisedDefense() { weaponThreshold(true, 4, true); }
    @Test public void AtLastWeAreGettingResultsAbsentAllowsHitAtOriginalDefenseThreshold() { weaponThreshold(false, 2, true); }
    private void maulDeployment(boolean effect) {
        var scn = active(effect); var maul = scn.GetDSCard("maul"); var vader = scn.GetDSCard("vader");
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("objective")); scn.MoveCardsToDSHand(maul, vader); scn.SkipToDSTurn(Phase.DEPLOY); scn.EnsureDSForcePile(20);
        assertEquals(effect, scn.DSDeployAvailable(maul)); assertFalse(scn.DSDeployAvailable(vader));
        if (effect) { scn.DSDeployCard(maul); scn.DSChooseCard(scn.GetDSStartingLocation()); scn.PassAllResponses(); assertInZone(Zone.AT_LOCATION, maul); }
    }
    @Test public void AtLastWeAreGettingResultsMaulActuallyDeploysDespiteObjectiveWhileVaderCannot() { maulDeployment(true); }
    @Test public void AtLastWeAreGettingResultsAbsentLeavesObjectiveDeploymentRestriction() { maulDeployment(false); }
    @Test public void AtLastWeAreGettingResultsStarshipActuallyDeploysForOneLessForce() {
        var scn = active(true); var ship = scn.GetDSCard("ship"); scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToDSHand(ship); scn.SkipToDSTurn(Phase.DEPLOY); scn.EnsureDSForcePile(10); int before = scn.GetDSForcePileCount();
        scn.DSDeployCard(ship); scn.DSChooseCard(scn.GetDSCard("system")); scn.PassAllResponses();
        assertEquals(before, scn.GetDSForcePileCount()); assertInZone(Zone.AT_LOCATION, ship);
    }
    @Test public void AtLastWeAreGettingResultsReducesOpponentsImmunityAndRestoresOnDeparture() {
        var scn = active(true); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("luke"), scn.GetLSCard("leia"), scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.CONTROL); var q = scn.game().getModifiersQuerying();
        assertEquals(0, q.getImmunityToAttritionLessThan(scn.gameState(), scn.GetLSCard("luke")), scn.epsilon);
        assertEquals(0, q.getImmunityToAttritionLessThan(scn.gameState(), scn.GetLSCard("leia")), scn.epsilon);
        assertEquals(5, q.getImmunityToAttritionLessThan(scn.gameState(), scn.GetDSCard("vader")), scn.epsilon);
        scn.MoveCardsToDSHand(scn.GetDSCard("results"));
        assertEquals(3, q.getImmunityToAttritionLessThan(scn.gameState(), scn.GetLSCard("luke")), scn.epsilon);
        assertEquals(2, q.getImmunityToAttritionLessThan(scn.gameState(), scn.GetLSCard("leia")), scn.epsilon);
    }

    @Test public void AtLastWeAreGettingResultsCannotBeChosenByActualAlterWhileOtherEffectCan() {
        var scn = active(true); scn.MoveCardsToLSSideOfTable(scn.GetLSCard("controlEffect")); scn.MoveCardsToDSSideOfTable(scn.GetDSCard("controlEffect"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke")); scn.MoveCardsToLSHand(scn.GetLSCard("alter"));
        scn.SkipToLSTurn(Phase.CONTROL); scn.LSPlayCard(scn.GetLSCard("alter"));
        assertTrue(scn.LSHasCardChoiceNotAvailable(scn.GetDSCard("results")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("controlEffect")));
    }
    private void attrition(boolean effect, int destiny, boolean immune) {
        var scn = active(effect); var obi = scn.GetLSCard("obi"); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, obi, scn.GetDSCard("vader")); scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(7); scn.PrepareDSDestiny(destiny - 1); scn.LSInitiateBattle(site);
        scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions(); assertEquals(0, scn.GetUnpaidLSBattleDamage()); assertEquals(destiny, scn.GetUnpaidLSAttrition());
        assertTrue(scn.LSDecisionAvailable("Choose a card from battle to forfeit"));
        assertEquals(immune, scn.LSDecisionAvailable("(if desired)"));
        if (immune) { scn.LSPass(); assertInZone(Zone.AT_LOCATION, obi); }
        else { scn.LSChooseCard(obi); scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, obi); }
    }
    @Test public void AtLastWeAreGettingResultsLeavesObiWanImmuneBelowReducedThreshold() { attrition(true, 1, true); }
    @Test public void AtLastWeAreGettingResultsForcesObiWanLossAtReducedThreshold() { attrition(true, 2, false); }
    @Test public void AtLastWeAreGettingResultsForcesObiWanLossAboveReducedThreshold() { attrition(true, 3, false); }
    @Test public void AtLastWeAreGettingResultsAbsentLeavesObiWanOriginalImmunity() { attrition(false, 2, true); }

    @Test public void AtLastWeAreGettingResultsDiscountExcludesOpponentsShipsAndNonStarshipsAndEndsOnDeparture() {
        var scn = active(false); var ship = scn.GetDSCard("ship"); var opponent = scn.GetLSCard("falcon"); var droid = scn.GetDSCard("droid");
        scn.MoveCardsToDSHand(ship, droid); scn.MoveCardsToLSHand(opponent); scn.SkipToDSTurn(Phase.DEPLOY);
        int ownCost = scn.GetDeployCost(ship), opponentCost = scn.GetDeployCost(opponent), droidCost = scn.GetDeployCost(droid);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("results"));
        assertEquals(ownCost - 1, scn.GetDeployCost(ship)); assertEquals(opponentCost, scn.GetDeployCost(opponent)); assertEquals(droidCost, scn.GetDeployCost(droid));
        scn.MoveCardsToDSHand(scn.GetDSCard("results")); assertEquals(ownCost, scn.GetDeployCost(ship));
    }
}
