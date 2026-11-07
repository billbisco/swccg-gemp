package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1263_018_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("falcon", "1_143"); put("luke", "1_19"); put("capital", "1_140"); put("endor", "8_68"); }},
                new HashMap<>() {{ put("ship", "1263_18"); put("ozzel", "3_82"); put("piett", "9_98"); put("veers", "3_87"); put("fighter", "1_299"); put("capital", "3_152"); put("endor", "8_157"); put("system", "1_282"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test public void JustifierStatsIconsAndKeywordsMatchPrintedImage() {
        var scn = scenario(); var bp = scn.GetDSCard("ship").getBlueprint();
        assertEquals("Justifier", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(1, bp.getDestiny(), scn.epsilon); assertEquals(9, bp.getDeployCost(), scn.epsilon);
        assertEquals(8, bp.getPower(), scn.epsilon); assertEquals(6, bp.getArmor(), scn.epsilon);
        assertEquals(3, bp.getHyperspeed(), scn.epsilon); assertEquals(10, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, bp.getExpansionSet()); assertEquals(Rarity.V, bp.getRarity());
        assertEquals(2, bp.getIconCount(Icon.PILOT));
        assertEquals(1, bp.getPilotCapacity()); assertEquals(0, bp.getPassengerCapacity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.STARSHIP); }});
        scn.BlueprintModelTypeCheck(bp, new ArrayList<>() {{ add(ModelType.IMPERIAL_CLASS_STAR_DESTROYER); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.SHIP_DOCKING_CAPABILITY); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.STARSHIP); add(Icon.PILOT); add(Icon.NAV_COMPUTER); add(Icon.SCOMP_LINK); add(Icon.DAGOBAH); add(Icon.LEGACY_BLOCK_6); }});
    }
    private VirtualTableScenario setup(boolean admiral) {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetDSCard("ship"), scn.GetLSCard("falcon"));
        scn.BoardAsPilot(scn.GetLSCard("falcon"), scn.GetLSCard("luke"));
        if (admiral) scn.BoardAsPilot(scn.GetDSCard("ship"), scn.GetDSCard("ozzel"));
        scn.SkipToDSTurn(Phase.BATTLE); return scn;
    }
    @Test public void JustifierPermanentPilotsProvideFourAbilityAndDrawBattleDestiny() {
        var scn = setup(false); assertEquals(4, scn.GetBattleDestinyAbility(scn.GetDSCard("ship")));
        scn.PrepareDSDestiny(3); scn.PrepareLSDestiny(1); scn.DSInitiateBattle(scn.GetDSCard("system")); scn.SkipToEndOfPowerSegment(true);
        assertEquals(1, scn.GetDSBattleDestinyCount()); assertEquals(3, scn.GetDSTotalDestiny(), scn.epsilon);
    }
    @Test public void JustifierAdmiralRaisesImmunityAndDepartureRestoresSeven() {
        var scn = setup(true); var ship = scn.GetDSCard("ship"); var q = scn.game().getModifiersQuerying();
        assertEquals(10, q.getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
        scn.MoveCardsToDSHand(scn.GetDSCard("ozzel")); assertEquals(7, q.getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
        scn.BoardAsPilot(ship, scn.GetDSCard("ozzel")); assertEquals(10, q.getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
    }
    @Test public void JustifierAdmiralAtAnotherLocationDoesNotRaiseImmunity() {
        var scn = setup(false); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("ozzel"));
        assertEquals(7, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), scn.GetDSCard("ship")), scn.epsilon);
    }
    @Test public void JustifierAllowsOneAdmiralToDeployAsPilot() {
        var scn = setup(false); var ship = scn.GetDSCard("ship"); scn.MoveCardsToDSHand(scn.GetDSCard("ozzel"));
        scn.SkipToDSTurn(Phase.DEPLOY); scn.DSDeployCard(scn.GetDSCard("ozzel"));
        assertTrue(scn.DSHasCardChoiceAvailable(ship)); scn.DSChooseCard(ship); scn.PassAllResponses();
        assertEquals(ship, scn.GetDSCard("ozzel").getAttachedTo());
        assertEquals(10, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
    }
    private void rejectedPilot(String pilot, boolean occupied) {
        var scn = setup(occupied); var ship = scn.GetDSCard("ship");
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetDSCard("fighter")); scn.MoveCardsToDSHand(scn.GetDSCard(pilot));
        scn.SkipToDSTurn(Phase.DEPLOY); scn.DSDeployCard(scn.GetDSCard(pilot));
        assertTrue(scn.DSHasCardChoiceNotAvailable(ship)); assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("fighter")));
    }
    @Test public void JustifierRejectsNonAdmiralPilotWithAnotherLegalShipAvailable() { rejectedPilot("veers", false); }
    @Test public void JustifierRejectsSecondAddedAdmiralPilot() { rejectedPilot("piett", true); }
    @Test public void JustifierIsLostWhenOwnEndorAppears() {
        var scn = setup(false); scn.MoveLocationToTable(scn.GetDSCard("endor")); scn.SkipToDSTurn(Phase.DEPLOY); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("ship"));
    }
    @Test public void JustifierIsLostWhenAnotherOwnCapitalShipDeploys() {
        var scn = setup(false); var other = scn.GetDSCard("capital"); scn.MoveCardsToDSHand(other);
        scn.SkipToDSTurn(Phase.DEPLOY); scn.DSDeployCard(other); scn.DSChooseCard(scn.GetDSCard("system")); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("ship")); assertEquals(Zone.AT_LOCATION, other.getZone());
    }
    @Test public void JustifierDoesNotLoseItselfForOpponentsEndorOrCapitalShip() {
        var scn = setup(false); scn.MoveLocationToTable(scn.GetLSCard("endor"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetLSCard("capital")); scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(Zone.AT_LOCATION, scn.GetDSCard("ship").getZone());
    }
    @Test public void JustifierDoesNotLoseItselfForOwnStarfighter() {
        var scn = setup(false); scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetDSCard("fighter")); scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(Zone.AT_LOCATION, scn.GetDSCard("ship").getZone());
    }
    @Test public void JustifierDoesNotLoseItselfForCapitalShipInHand() {
        var scn = setup(false); scn.MoveCardsToDSHand(scn.GetDSCard("capital")); scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(Zone.AT_LOCATION, scn.GetDSCard("ship").getZone());
    }
    private void attrition(int destiny, boolean admiral, boolean immune) {
        var scn = setup(admiral); var ship = scn.GetDSCard("ship");
        scn.ApplyAdHocModifier(new TotalBattleDestinyModifier(scn.GetLSCard("luke"), destiny - 7, scn.LS));
        scn.PrepareLSDestiny(7); scn.PrepareDSDestiny(7); scn.DSInitiateBattle(scn.GetDSCard("system"));
        scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions(); assertEquals(0, scn.GetUnpaidDSBattleDamage()); assertEquals(destiny, scn.GetUnpaidDSAttrition());
        if (immune) {
            assertTrue(scn.DSDecisionAvailable("Choose a card from battle to forfeit (if desired)")); scn.DSPass();
            assertEquals(Zone.AT_LOCATION, ship.getZone()); if (admiral) assertEquals(ship, scn.GetDSCard("ozzel").getAttachedTo());
        }
        else {
            assertTrue(scn.DSDecisionAvailable("Choose a card from battle to forfeit")); assertFalse(scn.DSDecisionAvailable("(if desired)"));
            var lost = admiral ? scn.GetDSCard("ozzel") : ship; scn.DSChooseCard(lost); scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, lost);
        }
    }
    @Test public void JustifierSurvivesAttritionBelowSevenWithoutAdmiral() { attrition(6, false, true); }
    @Test public void JustifierDoesNotProtectFromAttritionEqualToSevenWithoutAdmiral() { attrition(7, false, false); }
    @Test public void JustifierDoesNotProtectFromAttritionAboveSevenWithoutAdmiral() { attrition(8, false, false); }
    @Test public void JustifierAndAdmiralSurviveAttritionBelowTen() { attrition(9, true, true); }
    @Test public void JustifierDoesNotProtectAdmiralFromAttritionEqualToTen() { attrition(10, true, false); }
    @Test public void JustifierDoesNotProtectAdmiralFromAttritionAboveTen() { attrition(11, true, false); }
}
