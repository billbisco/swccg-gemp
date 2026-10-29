package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
public class ReflectionsGoldScoutStomachTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>();light.put("luke","1_19");var dark=new HashMap<String,String>();dark.put("combo","15_219");dark.put("scout","8_092");dark.put("igar","8_094");dark.put("grond","8_102");
        var s=new VirtualTableScenario(light,dark,40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);s.StartGame();s.MoveCardsToDSHand(s.GetDSCard("combo"));return s;
    }
    @Test public void ScoutReconAddsLandspeedForThisTurnAsUsed() {
        var s=scenario();var scout=s.GetDSCard("scout");var combo=s.GetDSCard("combo");s.MoveCardsToLocation(s.GetDSStartingLocation(),scout);s.SkipToDSTurn(Phase.CONTROL);int speed=s.GetLandspeed(scout);s.DSPlayCard(combo,"Add 1 to scout");s.DSChooseCard(scout);s.PassAllResponses();assertEquals(speed+1,s.GetLandspeed(scout));assertTrue(s.GetDSUsedPile().contains(combo));s.SkipToPhase(Phase.ACTIVATE);assertEquals(speed,s.GetLandspeed(scout));
    }
    private void missing(int destiny) {
        var s=scenario();var luke=s.GetLSCard("luke");var combo=s.GetDSCard("combo");s.MoveCardsToLocation(s.GetDSStartingLocation(),luke);s.SkipToLSTurn(Phase.CONTROL);s.PrepareDSDestiny(destiny);s.LSForceDrainAt(s.GetDSStartingLocation());s.DSPlayCard(combo,"Cancel Force drain");s.PassAllResponses();

        assertEquals(destiny>1,luke.isMissing());assertFalse(s.AwaitingDSForceLossPayment());assertTrue(s.GetDSLostPile().contains(combo));
    }
    @Test public void StomachDestinyTwoMakesSoleDrainerMissing() {missing(2);}
    @Test public void StomachDestinyOneCancelsDrainWithoutMissingCharacter() {missing(1);}
    @Test public void ScoutReconWithIgarAndGrondSubtractsDestinyFromOpponentsAttrition() {
        var s=scenario();var combo=s.GetDSCard("combo");var site=s.GetDSStartingLocation();s.MoveCardsToLocation(site,s.GetLSCard("luke"),s.GetDSCard("igar"),s.GetDSCard("grond"));s.SkipToDSTurn(Phase.BATTLE);s.PrepareLSDestiny(5);s.PrepareDSDestiny(1);s.DSInitiateBattle(site);s.SkipToEndOfPowerSegment(true);
        for(int i=0;i<12&&(s.DSGetDecision()==null||!s.DSCardPlayAvailable(combo,"Reduce opponent"));i++) {if(s.LSGetDecision()!=null&&s.LSDecisionAvailable("Optional"))s.LSPass();else if(s.DSGetDecision()!=null&&s.DSDecisionAvailable("Optional"))s.DSPass();else break;}
        assertEquals(5,s.gameState().getBattleState().getAttritionTotal(s.game(),s.DS),s.epsilon);s.PrepareDSDestiny(3);s.DSPlayCard(combo,"Reduce opponent");s.PassAllResponses();assertEquals(2,s.gameState().getBattleState().getAttritionTotal(s.game(),s.DS),s.epsilon);assertTrue(s.GetDSUsedPile().contains(combo));
    }    @Test public void StomachCannotCancelDrainWhereOpponentHasLandedStarship() {
        var light=new HashMap<String,String>();light.put("luke","1_19");light.put("xwing","1_146");var dark=new HashMap<String,String>();dark.put("combo","15_219");dark.put("control","15_262");var s=new VirtualTableScenario(light,dark);s.StartGame();s.MoveCardsToDSHand(s.GetDSCard("combo"),s.GetDSCard("control"));s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetLSCard("luke"),s.GetLSCard("xwing"));s.SkipToLSTurn(Phase.CONTROL);s.EnsureDSForcePile(2);s.LSForceDrainAt(s.GetDSStartingLocation());assertTrue(s.DSCardPlayAvailable(s.GetDSCard("control"),"Cancel Force drain"));assertFalse(s.DSCardPlayAvailable(s.GetDSCard("combo"),"Cancel Force drain"));
    }}
