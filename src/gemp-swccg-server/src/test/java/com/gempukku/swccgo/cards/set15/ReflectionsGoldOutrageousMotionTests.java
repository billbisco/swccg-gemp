package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.*;

import com.gempukku.swccgo.framework.*;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ReflectionsGoldOutrageousMotionTests {
    private void ready(VirtualTableScenario s) { assertEquals(s.DS,s.GetCurrentPlayer()); if(s.DSGetDecision()==null && s.LSGetDecision()!=null) s.LSPass(); assertNotNull(s.DSGetDecision()); }
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>();
        light.put("luke","1_19");
        var dark=new HashMap<String,String>();
        dark.put("combo","15_211");
        dark.put("lott","12_110");
        dark.put("trooper","1_194");
        dark.put("orn","12_113");
        dark.put("vader","1_168");

        var s=new VirtualTableScenario(light,dark,40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);

        s.StartGame();
        s.MoveCardsToDSHand(s.GetDSCard("combo"),s.GetDSCard("lott"));
        s.SkipToDSTurn(Phase.DEPLOY);
        s.DSPlayCardAndPassResponses(s.GetDSCard("combo"));
        return s;

    }
    @Test public void OutrageousCancelsOpponentDrainByStackingSenator() {
        var s=scenario();
        var combo=s.GetDSCard("combo");
        s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("luke"));
        s.SkipToLSTurn(Phase.CONTROL);
        s.LSForceDrainAt(s.GetLSStartingLocation());

        s.DSUseCardAction(combo,"Cancel Force drain");
        s.PassAllResponses();
        assertEquals(combo,s.GetDSCard("lott").getStackedOn());
        assertFalse(s.AwaitingDSForceLossPayment());

    }
    @Test public void MotionStacksSenatorAndTradeAgendaCanExchangeOncePerTurn() {
        var s=scenario();
        var combo=s.GetDSCard("combo");
        s.SkipToPhase(Phase.MOVE);
        ready(s); s.DSUseCardAction(combo,"Add 2 to your next");
        s.PassAllResponses();
        assertEquals(combo,s.GetDSCard("lott").getStackedOn());

        s.MoveCardsToDSHand(s.GetDSCard("trooper"));
        s.MoveCardsToTopOfDSLostPile(s.GetDSFiller(1));
        s.SkipToPhase(Phase.DRAW);

        int hand=s.GetDSHandCount(),lost=s.GetDSLostPileCount();
        ready(s); s.DSUseCardAction(combo,"Trade card");
        s.PassAllResponses();
        assertEquals(hand,s.GetDSHandCount());
        assertEquals(lost,s.GetDSLostPileCount());
        ready(s); assertFalse(s.DSCardActionAvailable(combo,"Trade card"));
        assertFalse(s.DSCardActionAvailable(combo,"Add 2 to your next"));

    }
    @Test public void MotionAddsTwoOnlyToNextBattlegroundDrain() {
        var s=scenario();
        var combo=s.GetDSCard("combo");
        s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetDSCard("trooper"));
        s.SkipToDSTurn(Phase.CONTROL);
        ready(s); s.DSUseCardAction(combo,"Add 2 to your next");
        s.PassAllResponses();

        ready(s); s.DSForceDrainAt(s.GetDSStartingLocation());
        s.PassAllResponses();
        int before=s.GetLSLostPileCount();
        s.LSPayRemainingForceLossFromReserveDeck();
        s.PassAllResponses();
        assertEquals(3,s.GetLSLostPileCount()-before);

    }    @Test public void WealthAgendaSubstitutesHandDestinyAndLosesOriginalDraw() {
        var s=scenario();
        var combo=s.GetDSCard("combo");
        s.MoveCardsToTopOfDSReserveDeck(s.GetDSCard("lott"));
        s.MoveCardsToDSHand(s.GetDSCard("orn"));
        s.SkipToPhase(Phase.MOVE);
        ready(s); s.DSUseCardAction(combo,"Add 2 to your next");
        s.PassAllResponses();
        assertEquals(combo,s.GetDSCard("orn").getStackedOn());

        var replacement=s.GetDSDestiny(6);
        s.MoveCardsToDSHand(replacement);
        s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetLSCard("luke"),s.GetDSCard("vader"));
        s.SkipToDSTurn(Phase.BATTLE);
        s.PrepareDSDestiny(1);
        var original=s.GetTopOfDSReserveDeck();
        s.DSInitiateBattle(s.GetDSStartingLocation());
        s.SkipToPowerSegment();
        s.DSChooseYes();

        for(int i=0;
        i<12&&(s.DSGetDecision()==null||!s.DSCardActionAvailable(combo,"Cancel and substitute"));
        i++) {if(s.LSGetDecision()!=null&&s.LSDecisionAvailable("Optional"))s.LSPass();
        else if(s.DSGetDecision()!=null&&s.DSDecisionAvailable("Optional"))s.DSPass();
        else break;
        }
        ready(s); s.DSUseCardAction(combo,"Cancel and substitute"); s.DSChooseCard(replacement);
        s.PassAllResponses();
        assertTrue("original="+original.getZone()+", pending="+s.DSGetDecision()+", LS="+s.LSGetDecision(),s.GetDSLostPile().contains(original));
        assertTrue(s.GetDSUsedPile().contains(replacement));
        assertEquals(6,s.GetDSTotalDestiny());

    }}
