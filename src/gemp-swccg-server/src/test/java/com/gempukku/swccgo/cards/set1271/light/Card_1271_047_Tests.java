package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_1271_047_Tests {
    private VirtualTableScenario scenario(){
        return new VirtualTableScenario(new HashMap<>() {{put("red3","1271_47");put("biggs","1_3");put("luke","1_19");put("other","1_146");put("spin","7_85");}},
            new HashMap<>() {{put("ship","1_304");put("damage","1_222");put("tallon","1_270");put("ship2","1_304");put("ship3","1_304");put("ship4","1_304");}},40,40,
            StartingSetup.DefaultLSSpaceSystem,StartingSetup.DefaultDSSpaceSystem,StartingSetup.NoLSStartingInterrupts,
            StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    private VirtualTableScenario ready(){var s=scenario();s.StartGame();s.SkipToLSTurn(Phase.CONTROL);s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("red3"));return s;}
    @Test public void Red3PrintedStatsIconsKeywordsModelAndCapacityAreCorrect(){
        var s=scenario();var b=s.GetLSCard("red3").getBlueprint();
        assertEquals("Red 3",b.getTitle());assertTrue(b.hasVirtualSuffix());assertEquals(Side.LIGHT,b.getSide());assertEquals(Uniqueness.UNIQUE,b.getUniqueness());
        assertEquals(3,b.getDestiny(),s.epsilon);assertEquals(2,b.getDeployCost(),s.epsilon);assertEquals(3,b.getPower(),s.epsilon);assertEquals(4,b.getManeuver(),s.epsilon);
        assertEquals(5,b.getHyperspeed(),s.epsilon);assertEquals(5,b.getForfeit(),s.epsilon);assertEquals(1,b.getPilotCapacity());assertEquals(0,b.getPassengerCapacity());assertEquals(0,b.getAstromechCapacity());
        assertEquals(CardSubtype.STARFIGHTER,b.getCardSubtype());assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1,b.getExpansionSet());assertEquals(Rarity.V,b.getRarity());
        s.BlueprintCardTypeCheck(b,List.of(CardType.STARSHIP));s.BlueprintModelTypeCheck(b,List.of(ModelType.X_WING));s.BlueprintKeywordCheck(b,List.of(Keyword.RED_SQUADRON));
        s.BlueprintIconCheck(b,List.of(Icon.STARSHIP,Icon.NAV_COMPUTER,Icon.SCOMP_LINK,Icon.LEGACY_BLOCK_7));
    }
    @Test public void Red3ActuallyAllowsBiggsToDeployAsItsPilot(){
        var s=ready();var biggs=s.GetLSCard("biggs");s.MoveCardsToLSHand(biggs);s.SkipToPhase(Phase.DEPLOY);s.LSDeployCard(biggs);s.PassAllResponses();
        if(biggs.getAttachedTo()==null){s.LSChooseCard(s.GetLSCard("red3"));s.PassAllResponses();}assertEquals(s.GetLSCard("red3"),biggs.getAttachedTo());
    }
    @Test public void Red3DoesNotAllowAnotherPilotToDeployAboard(){
        var s=ready();s.MoveCardsToLSHand(s.GetLSCard("luke"));s.SkipToPhase(Phase.DEPLOY);assertFalse(s.LSCardPlayAvailable(s.GetLSCard("luke")));
    }
    private VirtualTableScenario battle(){
        var s=ready();s.BoardAsPilot(s.GetLSCard("red3"),s.GetLSCard("biggs"));s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetDSCard("ship"),s.GetDSCard("ship2"),s.GetDSCard("ship3"),s.GetDSCard("ship4"));
        s.SkipToPhase(Phase.BATTLE);s.LSInitiateBattle(s.GetLSStartingLocation());s.PassAllResponses();return s;
    }
    private void finishLostOrder(VirtualTableScenario s){
        for(int i=0;i<2 && s.LSDecisionAvailable("put on Lost Pile");i++){
            s.LSChooseCard(s.LSHasCardChoiceAvailable(s.GetLSCard("red3"))?s.GetLSCard("red3"):s.GetLSCard("biggs"));s.PassAllResponses();
        }
    }
    private void tallon(boolean inBattle){
        var s=ready();s.BoardAsPilot(s.GetLSCard("red3"),s.GetLSCard("biggs"));s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetDSCard("ship"));
        s.MoveCardsToDSHand(s.GetDSCard("tallon"));s.SkipToDSTurn(inBattle?Phase.BATTLE:Phase.CONTROL);
        if(inBattle){s.DSInitiateBattle(s.GetLSStartingLocation());s.PassAllResponses();}
        s.PrepareDSDestiny(7);s.PrepareLSDestiny(0);s.DSPlayCard(s.GetDSCard("tallon"));s.DSChooseCard(s.GetDSCard("ship"));s.DSChooseCard(s.GetLSCard("red3"));
        s.PassAllResponses();s.PassDestinyDrawResponses();s.PassAllResponses();
        if(inBattle){assertEquals(Zone.AT_LOCATION,s.GetLSCard("red3").getZone());assertTrue(s.GetLSCard("red3").isHit());}
        else {finishLostOrder(s);assertTrue(s.GetLSLostPile().contains(s.GetLSCard("red3")));}
    }
    @Test public void Red3BecomesHitInsteadOfLostToAnActualInterruptDuringBattle(){tallon(true);}
    @Test public void Red3DoesNotReplaceAnActualInterruptLossOutsideBattle(){tallon(false);}
    private void attrition(int amount,boolean immune){
        var s=battle();s.PrepareLSDestiny(0);s.PrepareDSDestiny(amount);s.SkipToDamageSegment(true);s.PassDamageSegmentActions();
        if(s.AwaitingLSBattleDamagePayment())s.LSPayRemainingBattleDamageFromReserveDeck();assertEquals(immune,s.LSDecisionAvailable("(if desired)"));
        if(immune){s.LSChooseCards();assertEquals(Zone.AT_LOCATION,s.GetLSCard("red3").getZone());}
        else {s.LSPayAttritionFromCardInPlay(s.GetLSCard("red3"));finishLostOrder(s);assertTrue(s.GetLSLostPile().contains(s.GetLSCard("red3")));}
    }
    @Test public void Red3MaySurviveActualAttritionThree(){attrition(3,true);}
    @Test public void Red3MustSatisfyActualAttritionFour(){attrition(4,false);}
    @Test public void Red3CannotBeTargetedByLateralDamageWhileAnotherShipCan(){
        var s=ready();s.BoardAsPilot(s.GetLSCard("red3"),s.GetLSCard("biggs"));s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("other"));s.MoveCardsToDSHand(s.GetDSCard("damage"));s.SkipToDSTurn(Phase.DEPLOY);
        s.DSDeployCard(s.GetDSCard("damage"));s.PassAllResponses();
        if(s.GetDSCard("damage").getZone()==Zone.VOID){s.DSChooseCard(s.GetLSStartingLocation());s.PassAllResponses();}
        if(s.DSDecisionAvailable("target")){
            assertFalse(s.DSHasCardChoiceAvailable(s.GetLSCard("red3")));s.DSChooseCard(s.GetLSCard("other"));s.PassAllResponses();
        }
        assertEquals(5,s.GetPower(s.GetLSCard("red3")));assertEquals(5,s.GetForfeit(s.GetLSCard("red3")));assertEquals(0,s.GetForfeit(s.GetLSCard("other")));
    }
    private void spin(boolean targetRed3){
        var s=ready();var red=s.GetLSCard("red3");var other=s.GetLSCard("other");s.BoardAsPilot(red,s.GetLSCard("biggs"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(),other,s.GetDSCard("ship"));s.MoveCardsToLSHand(s.GetLSCard("spin"));
        s.SkipToPhase(Phase.BATTLE);s.LSInitiateBattle(s.GetLSStartingLocation());s.PassAllResponses();s.SkipToPowerSegment();s.PassAllResponses();
        if(s.LSDecisionAvailable("battle destiny"))s.LSChooseYes();s.PassResponses("COST_TO_DRAW_DESTINY_CARD");s.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        for(int i=0;i<12 && !(s.LSAnyActionsAvailable() && s.LSCardPlayAvailable(s.GetLSCard("spin"),"Substitute destiny"));i++){
            if(s.DSGetDecision()!=null)s.DSPass();else if(s.LSGetDecision()!=null)s.LSPass();else break;
        }
        assertTrue(s.LSCardPlayAvailable(s.GetLSCard("spin"),"Substitute destiny"));s.LSPlayCard(s.GetLSCard("spin"));s.LSChooseCard(targetRed3?red:other);s.PassAllResponses();
        assertTrue(targetRed3?s.GetLSUsedPile().contains(s.GetLSCard("spin")):s.GetLSLostPile().contains(s.GetLSCard("spin")));
    }
    @Test public void Red3MakesDarklighterSpinUsedWhenActuallyTargeted(){spin(true);}
    @Test public void Red3DoesNotMakeDarklighterSpinUsedWhenItTargetsAnotherShip(){spin(false);}
}
