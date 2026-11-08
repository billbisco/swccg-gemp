package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.*;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_1271_001_Tests {
    private VirtualTableScenario scenario(){
        return new VirtualTableScenario(new HashMap<>() {{
            put("obi","1_21");put("mace","12_13");put("luke","1_19");put("speeder","3_69");
        }},new HashMap<>() {{
            put("grievous","1271_1");put("blade","1250_32");put("oldBlade","1_314");put("ovsBlade","1112_30");
            put("rifle","1_312");put("vader","1_168");put("vehicle","7_315");
        }},40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    private VirtualTableScenario ready(){
        var s=scenario();s.StartGame();s.SkipToDSTurn(Phase.CONTROL);
        s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetDSCard("grievous"));return s;
    }
    @Test public void GrievousHunterOfJediPrintedStatsIconsKeywordsPersonaAndDefaultLandspeedAreCorrect(){
        var s=scenario();var b=s.GetDSCard("grievous").getBlueprint();
        assertEquals("Grievous, Hunter Of Jedi",b.getTitle());assertFalse(b.hasVirtualSuffix());assertEquals(Side.DARK,b.getSide());
        assertEquals(1,b.getDestiny(),s.epsilon);assertEquals(6,b.getDeployCost(),s.epsilon);assertEquals(7,b.getPower(),s.epsilon);
        assertEquals(5,b.getAbility(),s.epsilon);assertEquals(8,b.getForfeit(),s.epsilon);assertEquals(1,b.getLandspeed(),s.epsilon);
        assertEquals(Uniqueness.UNIQUE,b.getUniqueness());assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1,b.getExpansionSet());assertEquals(Rarity.V,b.getRarity());
        s.BlueprintCardTypeCheck(b,List.of(CardType.REPUBLIC));s.BlueprintKeywordCheck(b,List.of(Keyword.COMMANDER,Keyword.LEADER));
        s.BlueprintPersonaCheck(b,List.of(Persona.GRIEVOUS));s.BlueprintIconCheck(b,List.of(Icon.REPUBLIC,Icon.WARRIOR,Icon.EPISODE_I,Icon.SPECIAL_EDITION,Icon.LEGACY_BLOCK_7));
    }
    @Test public void GrievousHunterOfJediActuallyDeploysForSixForce(){
        var s=scenario();s.StartGame();s.SkipToDSTurn(Phase.CONTROL);var g=s.GetDSCard("grievous");s.MoveCardsToDSHand(g);s.EnsureDSForcePile(10);s.SkipToPhase(Phase.DEPLOY);int before=s.GetDSForcePileCount();
        s.DSDeployCard(g);s.DSChooseCard(s.GetDSStartingLocation());s.PassAllResponses();assertEquals(s.GetDSStartingLocation(),g.getAtLocation());assertEquals(before-6,s.GetDSForcePileCount());
    }
    @Test public void GrievousHunterOfJediActuallyDeploysTheBlockFiveLightsaberOnHim(){
        var s=ready();var blade=s.GetDSCard("blade");s.MoveCardsToDSHand(blade);s.SkipToPhase(Phase.DEPLOY);
        s.DSDeployCard(blade);s.PassAllResponses();
        if(blade.getAttachedTo()==null){s.DSChooseCard(s.GetDSCard("grievous"));s.PassAllResponses();}
        assertEquals(s.GetDSCard("grievous"),blade.getAttachedTo());
    }
    @Test public void GrievousHunterOfJediDoesNotGrantTheOriginalVirtualLightsaberItsBlockFivePermission(){
        var s=ready();s.MoveCardsToDSHand(s.GetDSCard("ovsBlade"));s.SkipToPhase(Phase.DEPLOY);
        assertFalse(s.DSCardPlayAvailable(s.GetDSCard("ovsBlade")));
    }
    @Test public void GrievousHunterOfJediReducesAnOrdinaryJediByOneAndACouncilMemberByTwo(){
        var s=ready();s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("obi"),s.GetLSCard("mace"));s.SkipToPhase(Phase.DEPLOY);
        assertEquals(4,s.GetPower(s.GetLSCard("obi")));assertEquals(4,s.GetPower(s.GetLSCard("mace")));
    }
    @Test public void GrievousHunterOfJediDoesNotReduceAForceSensitiveWhoIsNotAJedi(){
        var s=ready();s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("luke"));s.SkipToPhase(Phase.DEPLOY);assertEquals(3,s.GetPower(s.GetLSCard("luke")));
    }
    @Test public void GrievousHunterOfJediStopsReducingAJediWhoLeavesHisLocation(){
        var s=ready();var obi=s.GetLSCard("obi");s.MoveCardsToLocation(s.GetLSStartingLocation(),obi);s.SkipToPhase(Phase.DEPLOY);assertEquals(4,s.GetPower(obi));
        s.MoveCardsToLocation(s.GetDSStartingLocation(),obi);s.SkipToPhase(Phase.BATTLE);assertEquals(5,s.GetPower(obi));
    }
    @Test public void GrievousHunterOfJediDoesNotReduceAJediInsideAnEnclosedVehicle(){
        var s=ready();s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("speeder"));s.BoardAsPilot(s.GetLSCard("speeder"),s.GetLSCard("obi"));s.SkipToPhase(Phase.DEPLOY);
        assertEquals(5,s.GetPower(s.GetLSCard("obi")));
    }
    @Test public void GrievousHunterOfJediForfeitProtectionRequiresALightsaberAndEndsWhenItLeaves(){
        var s=ready();var g=s.GetDSCard("grievous");s.ApplyAdHocModifier(new ForfeitModifier(null,Filters.sameCardId(g),-3));s.SkipToPhase(Phase.DEPLOY);assertEquals(5,s.GetForfeit(g));
        s.AttachCardsTo(g,s.GetDSCard("rifle"));s.SkipToPhase(Phase.BATTLE);assertEquals(5,s.GetForfeit(g));
        s.MoveCardsToDSHand(s.GetDSCard("rifle"));s.AttachCardsTo(g,s.GetDSCard("oldBlade"));s.SkipToPhase(Phase.MOVE);assertEquals(8,s.GetForfeit(g));
        s.MoveCardsToDSHand(s.GetDSCard("oldBlade"));s.SkipToPhase(Phase.DRAW);assertEquals(5,s.GetForfeit(g));
    }
    private void battleDestiny(boolean armed,boolean elsewhere){
        var s=ready();var g=s.GetDSCard("grievous");s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("obi"));
        if(armed)s.AttachCardsTo(g,s.GetDSCard("oldBlade"));
        if(elsewhere){s.MoveCardsToLocation(s.GetDSStartingLocation(),g);s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetDSCard("vader"));}
        s.SkipToPhase(Phase.BATTLE);s.PrepareDSDestiny(2);s.PrepareLSDestiny(0);s.DSInitiateBattle(s.GetLSStartingLocation());s.PassAllResponses();s.SkipToEndOfPowerSegment(true);
        assertEquals(elsewhere?9:armed?10:9,s.GetDSTotalPower());
    }
    @Test public void GrievousHunterOfJediAddsOneToAnActualBattleDestinyWhileArmed(){battleDestiny(true,false);}
    @Test public void GrievousHunterOfJediDoesNotAddToBattleDestinyWhenUnarmed(){battleDestiny(false,false);}
    @Test public void GrievousHunterOfJediDoesNotAddToABattleAtAnotherLocation(){battleDestiny(true,true);}
    @Test public void GrievousHunterOfJediActuallyFiresItsGrantedLightsaber(){
        var s=ready();var blade=s.GetDSCard("blade");var obi=s.GetLSCard("obi");s.AttachCardsTo(s.GetDSCard("grievous"),blade);s.MoveCardsToLocation(s.GetLSStartingLocation(),obi);
        s.SkipToPhase(Phase.BATTLE);s.DSInitiateBattle(s.GetLSStartingLocation());s.PassAllResponses();assertTrue(s.DSCardActionAvailable(blade,"Fire"));
        s.MoveCardsToTopOfDSReserveDeck(s.GetDSDestiny(5),s.GetDSDestiny(6));s.DSUseCardAction(blade,"Fire");s.DSChooseCard(obi);s.PassWeaponFireWithDestinyDraw(2);s.PassAllResponses();assertTrue(obi.isHit());
    }
    private void attrition(int destiny,boolean immune){
        var s=ready();var g=s.GetDSCard("grievous");s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("obi"));s.SkipToPhase(Phase.BATTLE);
        s.PrepareDSDestiny(0);s.PrepareLSDestiny(destiny);s.DSInitiateBattle(s.GetLSStartingLocation());s.PassAllResponses();s.SkipToDamageSegment(true);s.PassDamageSegmentActions();
        if(s.AwaitingDSBattleDamagePayment())s.DSPayRemainingBattleDamageFromReserveDeck();
        assertEquals(immune,s.DSDecisionAvailable("(if desired)"));
        if(immune)s.DSChooseCards();else s.DSPayAttritionFromCardInPlay(g);
        if(immune)assertEquals(Zone.AT_LOCATION,g.getZone());else assertTrue(s.GetDSLostPile().contains(g));
    }
    @Test public void GrievousHunterOfJediMaySurviveActualAttritionThree(){attrition(3,true);}
    @Test public void GrievousHunterOfJediMustSatisfyActualAttritionFour(){attrition(4,false);}
    @Test public void GrievousHunterOfJediCannotProjectItsJediPenaltyFromInsideAnEnclosedVehicle(){
        var s=ready();s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetDSCard("vehicle"),s.GetLSCard("obi"));
        s.BoardAsPilot(s.GetDSCard("vehicle"),s.GetDSCard("grievous"));s.SkipToPhase(Phase.DEPLOY);assertEquals(5,s.GetPower(s.GetLSCard("obi")));
    }

    @Test
    public void GrievousHunterOfJediShowsTheMasterListPcE1Text() {
        String t = com.gempukku.swccgo.framework.VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_1").getGameText();
        org.junit.Assert.assertEquals("[Virtual Block 5] Dark Jedi Lightsaber may deploy on Grievous. Jedi present are power -1 (-2 if a Jedi Council Member). While armed with a lightsaber, your battle destiny draws here are +1 and his forfeit may not be reduced. Immune to attrition < 4.", t);
        org.junit.Assert.assertFalse(t.contains("draws are +1 here"));
    }
}
