package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.*;

import com.gempukku.swccgo.framework.*;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ReflectionsGoldLookDroidsCounterTests {
    private VirtualTableScenario scenario() { return scenario("15_262"); }
    private VirtualTableScenario scenario(String blueprint) {
        var light=new HashMap<String,String>();
        light.put("droid","1_5");
        light.put("luke","1_19");
        var dark=new HashMap<String,String>();
        dark.put("combo",blueprint);

        var s=new VirtualTableScenario(light,dark,40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
        s.StartGame();
        s.MoveCardsToDSHand(s.GetDSCard("combo"));
        return s;

    }
    @Test public void LookSirDroidsPaysOneToLoseOnlyUsedPileDroidsAsUsed() { search("15_262",true); }
    @Test public void OriginalLookSirDroidsSearchStillGoesLost() { search("1_259",false); }
    private void search(String blueprint, boolean used) {
        var s=scenario(blueprint);
        s.SkipToDSTurn(Phase.CONTROL);
        s.MoveCardsToTopOfLSUsedPile(s.GetLSCard("droid"),s.GetLSCard("luke"));
        s.SkipToPhase(Phase.DEPLOY);
        int force=s.GetDSForcePileCount();
        s.DSPlayCard(s.GetDSCard("combo"),"Search opponent");
        s.PassAllResponses();
        s.DSDismissRevealedCards();
        s.PassAllResponses();
        s.LSDismissRevealedCards(); s.PassAllResponses(); s.DSChooseCard(s.GetLSCard("droid"));s.PassAllResponses();s.DSDismissRevealedCards();s.LSDismissRevealedCards();
        s.PassAllResponses();
        assertTrue("Droid lost, LS="+s.LSGetDecision()+", DS="+s.DSGetDecision()+", zone="+s.GetLSCard("droid").getZone(),s.GetLSLostPile().contains(s.GetLSCard("droid")));
        assertTrue(s.GetLSUsedPile().contains(s.GetLSCard("luke")));
        assertEquals(force-1,s.GetDSForcePileCount());
        assertTrue("combo="+s.GetDSCard("combo").getZone()+", pending="+s.DSGetDecision()+", LS="+s.LSGetDecision(),(used?s.GetDSUsedPile():s.GetDSLostPile()).contains(s.GetDSCard("combo")));

    }
    @Test public void LookSirDroidsPaysPrintedDeployToDestroyDroidAsLost() { destroy("15_262"); }
    @Test public void OriginalLookSirDroidsDestroyStillGoesLost() { destroy("1_259"); }
    private void destroy(String blueprint) {
        var s=scenario(blueprint);
        var droid=s.GetLSCard("droid");
        s.MoveCardsToLocation(s.GetLSStartingLocation(),droid);
        s.SkipToDSTurn(Phase.CONTROL);
        int force=s.GetDSForcePileCount(),cost=s.GetDeployCost(droid);
        s.DSPlayCard(s.GetDSCard("combo"),"Destroy a droid");
        s.DSChooseCard(droid);
        s.PassAllResponses();
        assertEquals(force-cost,s.GetDSForcePileCount());
        assertTrue(s.GetLSLostPile().contains(droid));
        assertTrue(s.GetDSLostPile().contains(s.GetDSCard("combo")));

    }
    private void counter(int destiny) {
        var s=scenario();
        s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("luke"));
        s.SkipToLSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(3);
        s.PrepareDSDestiny(destiny);
        int power=s.GetPower(s.GetLSCard("luke"));
        s.LSForceDrainAt(s.GetLSStartingLocation());
        s.DSPlayCard(s.GetDSCard("combo"),"Cancel Force drain");
        s.PassAllResponses();

        if(destiny>power) {assertTrue(s.AwaitingLSForceLossPayment());
        s.LSPayRemainingForceLossFromReserveDeck();
        s.PassAllResponses();
        assertEquals(destiny-power,s.GetLSLostPileCount());
        }
        else if(destiny<power) {assertTrue(s.AwaitingDSForceLossPayment());
        s.DSPayRemainingForceLossFromReserveDeck();
        s.PassAllResponses();
        assertEquals(power-destiny+1,s.GetDSLostPileCount());
        }
        assertTrue(s.GetDSLostPile().contains(s.GetDSCard("combo")));

    }
    @Test public void CounterAssaultHigherDestinyCostsOpponentDifference() {counter(6);
        }
    @Test public void CounterAssaultLowerDestinyCostsOwnerDifference() {counter(1);
        }
    @Test public void CounterAssaultLimitsThreePresentCharactersToTwoDraws() {
        var s=scenario();
        var site=s.GetLSStartingLocation();
        s.MoveCardsToLocation(site,s.GetLSCard("luke"),s.GetLSFiller(1),s.GetLSFiller(2));
        s.SkipToLSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(3);
        s.PrepareDSDestiny(4);
        var third=s.GetTopOfDSReserveDeck();
        s.PrepareDSDestiny(5);
        s.PrepareDSDestiny(6);

        float power=s.game().getModifiersQuerying().getTotalPowerAtLocation(s.gameState(),site,s.LS,false,false);
        s.LSForceDrainAt(site);
        s.DSPlayCard(s.GetDSCard("combo"),"Cancel Force drain");
        s.PassAllResponses();
        assertEquals(third,s.GetTopOfDSReserveDeck());
        assertTrue(s.AwaitingLSForceLossPayment());
        s.LSPayRemainingForceLossFromReserveDeck();
        s.PassAllResponses();
        assertEquals(11-power,s.GetLSLostPileCount(),s.epsilon);

    }}
