package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import java.util.*;
import static org.junit.Assert.*;
public class ReflectionsGoldDioxisComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>();light.put("obi","1_21");
        var dark=new HashMap<String,String>();dark.put("combo","15_235");
        var s=new VirtualTableScenario(light,dark,40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);s.StartGame();s.MoveCardsToDSHand(s.GetDSCard("combo"));s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetLSCard("obi"));s.SkipToDSTurn(Phase.CONTROL);return s;
    }
    private void cancel(int destiny) {
        var s=scenario();var obi=s.GetLSCard("obi");var combo=s.GetDSCard("combo");s.PrepareDSDestiny(destiny);
        s.DSPlayCard(combo,"Cancel Jedi's game text");s.DSChooseCard(obi);s.PassAllResponses();
        assertEquals(destiny>2,s.game().getModifiersQuerying().isGameTextCanceled(s.gameState(),obi));assertTrue(s.GetDSLostPile().contains(combo));
        s.SkipToPhase(Phase.ACTIVATE);assertFalse(s.game().getModifiersQuerying().isGameTextCanceled(s.gameState(),obi));
    }
    @Test public void DioxisDestinyThreeCancelsJediTextUntilTurnEnds() {cancel(3);}
    @Test public void DioxisDestinyTwoDoesNotCancelJediText() {cancel(2);}
    private void peek(String option) {
        var s=scenario();var combo=s.GetDSCard("combo");var top=s.GetDSFiller(1);s.MoveCardsToTopOfDSForcePile(top);
        s.DSPlayCard(combo,"Peek at top card of Force Pile");s.PassAllResponses();s.DSDismissRevealedCards();s.PassAllResponses();s.DSChooseOption(option);s.PassAllResponses();
        assertTrue(s.GetDSUsedPile().contains(combo));
        if(option.equals("Return it")) assertEquals(top,s.GetTopOfDSForcePile());
        else if(option.contains("Reserve")) {assertTrue(s.GetDSReserveDeck().contains(top));assertFalse(s.GetDSForcePile().contains(top));}
        else {assertTrue(s.GetDSForcePile().contains(top));assertNotEquals(top,s.GetTopOfDSForcePile());}
    }
    @Test public void TheyreStillComingThroughReturnsPeekedCard() {peek("Return it");}
    @Test public void TheyreStillComingThroughBottomsPeekedForceCard() {peek("Place it under Force Pile");}
    @Test public void TheyreStillComingThroughBottomsPeekedReserveCard() {peek("Place it under Reserve Deck");}
    private void respondWith(VirtualTableScenario s, PhysicalCardImpl card, String action) {
        for(int i=0;i<30&&!(s.DSGetDecision()!=null&&s.DSCardPlayAvailable(card,action));i++) {
            var decision=s.GetCurrentDecision();
            assertNotNull("Expected response window for "+action,decision);
            assertTrue("Unexpected decision: "+decision.getText(),decision.getText().toLowerCase().contains("optional"));
            if(s.DSGetDecision()!=null) s.DSPass(); else s.LSPass();
        }
        assertTrue("Expected printed response "+action,s.DSCardPlayAvailable(card,action));
        s.DSPlayCard(card,action);
    }
    @Test public void TheyreStillComingThroughCancelsSmokeScreenAsUsed() {
        var light=new HashMap<String,String>(); light.put("smoke","5_69"); light.put("obi","1_21");
        var dark=new HashMap<String,String>(); dark.put("card","15_235"); dark.put("vader","1_168");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var smoke=s.GetLSCard("smoke");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLSHand(smoke);
        s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetDSCard("vader"),s.GetLSCard("obi"));
        s.SkipToLSTurn(Phase.BATTLE); s.EnsureLSForcePile(2); s.LSInitiateBattle(s.GetDSStartingLocation()); s.PassAllResponses();
        s.PrepareLSDestiny(2); s.SkipToPowerSegment(); s.LSChooseYes(); s.PassResponses("COST_TO_DRAW_DESTINY_CARD"); s.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        int destiny=s.GetLSBattleDestinyCount(); s.LSPlayCard(smoke); s.LSChooseCard(s.GetLSCard("obi")); respondWith(s,card,"Cancel"); s.PassAllResponses();
        assertEquals(destiny,s.GetLSBattleDestinyCount()); assertTrue(s.GetLSLostPile().contains(smoke)); assertTrue(s.GetDSUsedPile().contains(card));
    }

    @Test public void TheyreStillComingThroughRetargetsFallenPortalToOpponentsCharacter() {
        var light=new HashMap<String,String>(); light.put("portal","6_66"); light.put("han","1_11");
        var dark=new HashMap<String,String>(); dark.put("card","15_235"); dark.put("vader","1_168"); dark.put("bay","1_291");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var vader=s.GetDSCard("vader"); var han=s.GetLSCard("han"); var bay=s.GetDSCard("bay");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLSHand(s.GetLSCard("portal")); s.MoveLocationToTable(bay); s.MoveCardsToLocation(bay,han,vader);
        s.SkipToDSTurn(Phase.BATTLE); s.EnsureDSForcePile(3); s.PrepareLSDestiny(5);
        s.DSUseCardAction(bay,"Initiate battle"); s.PassForceUseResponses(); s.DSPass();
        s.LSPlayCard(s.GetLSCard("portal")); s.LSChooseCard(vader); respondWith(s,card,"Re-target Fallen Portal"); s.DSChooseCard(han); s.PassAllResponses(); s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(han)); assertFalse(s.GetDSLostPile().contains(vader)); assertTrue(s.GetDSUsedPile().contains(card));
    }
    @Test public void DioxisCannotTargetJediOutsideCurrentBattle() {
        var light=new HashMap<String,String>();light.put("luke","1_19");light.put("obi","1_21");var dark=new HashMap<String,String>();dark.put("combo","15_235");dark.put("vader","1_168");var s=new VirtualTableScenario(light,dark);s.StartGame();s.MoveCardsToDSHand(s.GetDSCard("combo"));s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("obi"));s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetLSCard("luke"),s.GetDSCard("vader"));s.SkipToDSTurn(Phase.BATTLE);s.EnsureDSForcePile(3);s.DSInitiateBattle(s.GetDSStartingLocation());s.PassAllResponses();assertTrue(s.DSCardPlayAvailable(s.GetDSCard("combo"),"Peek at top"));assertFalse(s.DSCardPlayAvailable(s.GetDSCard("combo"),"Cancel Jedi"));
    }}
