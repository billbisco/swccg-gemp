package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
public class ReflectionsGoldThrowChargeCancellationTests {
    private void cancel(String attackId) {
        var light=new HashMap<String,String>();light.put("card","15_124");light.put("luke","1_19");var dark=new HashMap<String,String>();dark.put("attack",attackId);dark.put("vader","1_168");dark.put("blaster","1_317");var s=new VirtualTableScenario(light,dark);var card=s.GetLSCard("card");var attack=s.GetDSCard("attack");var trooper=s.GetDSFiller(1);var site=s.GetDSStartingLocation();s.StartGame();s.MoveCardsToLSHand(card);s.MoveCardsToDSHand(attack);s.MoveCardsToLocation(site,s.GetLSCard("luke"),trooper,s.GetDSCard("vader"));s.AttachCardsTo(trooper,s.GetDSCard("blaster"));boolean blasted=attackId.equals("5_132");s.SkipToDSTurn(blasted?Phase.CONTROL:Phase.BATTLE);s.EnsureDSForcePile(4);
        if(!blasted){s.DSInitiateBattle(site);if(!attackId.equals("5_159"))s.PassAllResponses();}
        s.DSPlayCard(attack);if(attackId.equals("1_274"))s.DSChooseCard(trooper);if(attackId.equals("1_240"))s.DSChooseCard(s.GetDSCard("vader"));if(blasted)s.DSChooseCard(s.GetDSCard("blaster"));
        for(int i=0;i<24;i++){if(s.LSGetDecision()!=null&&s.LSCardPlayAvailable(card,"Cancel"))break;if(s.LSGetDecision()!=null)s.LSPass();else s.DSPass();}
        s.LSPlayCard(card,"Cancel");s.PassAllResponses();assertTrue(s.GetLSUsedPile().contains(card));assertTrue(s.GetDSLostPile().contains(attack));assertEquals(1,s.GetPower(trooper));assertFalse(s.GetLSCard("luke").isHit());
    }
    @Test public void CancelsTrooperChargeBeforeItsPowerBonus(){cancel("1_274");}
    @Test public void CancelsTrooperAssaultAtBattleInitiation(){cancel("5_159");}
    @Test public void CancelsDarkJediPresenceBeforeDoublingPower(){cancel("1_240");}
    @Test public void CancelsBlastedDroidBeforeWeaponFires(){cancel("5_132");}
    @Test public void ThrowChargeAddsOneToActualShieldGeneratorTotalAndBlowsBunker() {
        var light=new HashMap<String,String>();light.put("card","15_124");light.put("event","8_43");light.put("bunker","8_70");light.put("charge","8_90");light.put("luke","1_19");
        var s=new VirtualTableScenario(light,new HashMap<>(),40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);var card=s.GetLSCard("card");var bunker=s.GetLSCard("bunker");var event=s.GetLSCard("event");s.StartGame();s.MoveCardsToLSHand(card);s.MoveLocationToTable(bunker);s.MoveCardsToLocation(bunker,s.GetLSCard("luke"));s.AttachCardsTo(bunker,event,s.GetLSCard("charge"));s.SkipToLSTurn(Phase.CONTROL);s.EnsureDSForcePile(10);s.EnsureLSForcePile(10);s.PrepareLSDestiny(3);s.PrepareLSDestiny(6);s.LSUseCardAction(event);
        for(int i=0;i<24;i++){if(s.LSGetDecision()!=null&&s.LSCardPlayAvailable(card,"Add 1 to Deactivate"))break;if(s.LSGetDecision()!=null)s.LSPass();else s.DSPass();}
        s.LSPlayCard(card,"Add 1 to Deactivate");s.PassAllResponses();s.LSChooseCard(s.GetLSCard("charge"));s.PassAllResponses();for(int i=0;i<12&&s.AwaitingDSForceLossPayment();i++){s.DSPayForceLossFromForcePile();s.PassAllResponses();}assertTrue(bunker.isBlownAway());assertTrue(s.GetLSUsedPile().contains(card));
    }
}
