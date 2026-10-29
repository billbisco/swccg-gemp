package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class ReflectionsGoldLightParallelResponseTests {
    @Test public void WildernessCancelsOpponentDrainAndMakesCharacterMissingAsLost() {
        var light=new HashMap<String,String>(); light.put("card","15_100");
        var s=new VirtualTableScenario(light,new HashMap<>()); var card=s.GetLSCard("card"); var trooper=s.GetDSFiller(1); var site=s.GetDSStartingLocation();
        s.StartGame(); s.MoveCardsToLSHand(card); s.MoveCardsToLocation(site,trooper); s.SkipToDSTurn(Phase.CONTROL); s.PrepareLSDestiny(7);
        int life=s.GetLSLifeForceRemaining(); s.DSForceDrainAt(site); s.DSPass(); s.LSPlayCard(card,"Cancel Force drain"); s.PassAllResponses();
        assertTrue(trooper.isMissing()); assertEquals(life,s.GetLSLifeForceRemaining()); assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void LosingTrackMovesOpponentLostTopToReserveDuringBattleAsUsed() {
        var light=new HashMap<String,String>(); light.put("card","15_99"); light.put("luke","1_19");
        var s=new VirtualTableScenario(light,new HashMap<>()); var card=s.GetLSCard("card"); var lost=s.GetDSFiller(2); var site=s.GetDSStartingLocation();
        s.StartGame(); s.MoveCardsToLSHand(card); s.MoveCardsToLocation(site,s.GetLSCard("luke"),s.GetDSFiller(1)); s.SkipToLSTurn(Phase.BATTLE); s.MoveCardsToTopOfDSLostPile(lost);
        s.LSInitiateBattle(site); s.PassBattleStartResponses();
        s.LSPlayCard(card,"Place top card of opponent's Lost Pile on Reserve Deck"); s.PassAllResponses();
        assertEquals(lost,s.GetDSReserveDeck().getFirst()); assertTrue(s.GetLSUsedPile().contains(card));
    }
    @Test public void LoyalBodyguardDeploysHandmaidenFromReserveToAmidala() {
        var light=new HashMap<String,String>(); light.put("card","15_105"); light.put("amidala","12_22"); light.put("sabe","12_25");
        var s=new VirtualTableScenario(light,new HashMap<>()); var card=s.GetLSCard("card"); var sabe=s.GetLSCard("sabe"); var site=s.GetDSStartingLocation();
        s.StartGame(); s.MoveCardsToLSHand(card); s.MoveCardsToLocation(site,s.GetLSCard("amidala")); s.SkipToLSTurn(Phase.DEPLOY); s.EnsureLSForcePile(3); s.MoveCardsToTopOfLSReserveDeck(sabe);
        s.LSPlayCard(card,"Deploy handmaiden from Reserve Deck"); s.PassAllResponses(); s.LSChooseCard(sabe); s.PassAllResponses();
        assertEquals(site,sabe.getAtLocation()); assertTrue(s.GetLSUsedPile().contains(card));
    }
    private void reach(VirtualTableScenario s,com.gempukku.swccgo.game.PhysicalCardImpl card,String mode) {
        for(int i=0;i<24;i++) {
            if(s.LSGetDecision()!=null && s.LSCardPlayAvailable(card,mode))return;
            if(s.LSGetDecision()!=null)s.LSPass(); else if(s.DSGetDecision()!=null)s.DSPass();
        }
        fail("Missing printed response: "+mode);
    }
    @Test public void LoyalBodyguardAddsBattleDestinyWhenOpponentInitiatesAgainstAmidalaAndHandmaiden() {
        var light=new HashMap<String,String>(); light.put("card","15_105"); light.put("amidala","12_22"); light.put("sabe","12_25");
        var s=new VirtualTableScenario(light,new HashMap<>()); var card=s.GetLSCard("card"); var site=s.GetDSStartingLocation();
        s.StartGame(); s.MoveCardsToLSHand(card); s.MoveCardsToLocation(site,s.GetLSCard("amidala"),s.GetLSCard("sabe"),s.GetDSFiller(1));
        s.SkipToDSTurn(Phase.BATTLE); s.EnsureDSForcePile(2); s.DSInitiateBattle(site); reach(s,card,"");
        int before=s.GetLSBattleDestinyCount();
        s.LSPlayCard(card); s.PassAllResponses();
        assertEquals(before+1,s.GetLSBattleDestinyCount()); assertTrue(s.GetLSUsedPile().contains(card));
    }
    @Test public void NarrowEscapeMovesRebelToAdjacentSiteAndCancelsBattle() {
        var light=new HashMap<String,String>(); light.put("card","15_107"); light.put("luke","1_19"); light.put("adjacent","1_128");
        var s=new VirtualTableScenario(light,new HashMap<>()); var card=s.GetLSCard("card"); var luke=s.GetLSCard("luke"); var site=s.GetDSStartingLocation(); var adjacent=s.GetLSCard("adjacent");
        s.StartGame(); s.MoveLocationToTable(adjacent); s.MoveCardsToLSHand(card); s.MoveCardsToLocation(site,luke,s.GetDSFiller(1));
        s.SkipToDSTurn(Phase.BATTLE); s.EnsureLSForcePile(5); s.EnsureDSForcePile(2); s.DSInitiateBattle(site); reach(s,card,"Move cards with ability away");
        s.LSPlayCard(card,"Move cards with ability away"); s.PassAllResponses();
        assertEquals(adjacent,luke.getAtLocation()); assertNull(s.gameState().getBattleState()); assertTrue(s.GetLSUsedPile().contains(card));
    }
    @Test public void ChangingOddsRemovesInterruptAfterActualFailedOpponentReserveSearch() {
        var light=new HashMap<String,String>(); light.put("card","15_109");
        var dark=new HashMap<String,String>(); dark.put("advisor","6_160"); dark.put("interrupt","1_240");
        var s=new VirtualTableScenario(light,dark,30,30,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open); var card=s.GetLSCard("card"); var target=s.GetDSCard("interrupt");
        s.StartGame(); s.DSChooseCards(); s.MoveCardsToLSHand(card); s.MoveCardsToDSHand(s.GetDSCard("advisor")); s.SkipToDSTurn(Phase.CONTROL); s.EnsureDSForcePile(4); s.MoveCardsToTopOfDSReserveDeck(target);
        s.DSPlayCard(s.GetDSCard("advisor"),"Take card into hand from Reserve Deck"); s.PassAllResponses(); s.DSChooseCards(); s.LSChooseCards();
        reach(s,card,"Place an Interrupt from Reserve Deck out of play"); s.LSPlayCard(card,"Place an Interrupt from Reserve Deck out of play"); s.PassAllResponses(); s.LSChooseCard(target); s.PassAllResponses();
        assertEquals(Zone.OUT_OF_PLAY,target.getZone()); assertTrue(s.GetLSUsedPile().contains(card));
    }
    @Test public void ChangingOddsChangesWattosEvenDestinyToOddAndStopsBonusDestinies() {
        var light=new HashMap<String,String>(); light.put("card","15_109"); light.put("obi","1_21");
        var dark=new HashMap<String,String>(); dark.put("cube","15_302"); dark.put("watto","11_65");
        var s=new VirtualTableScenario(light,dark); var card=s.GetLSCard("card"); var site=s.GetDSStartingLocation();
        s.StartGame(); s.MoveCardsToLSHand(card); s.MoveCardsToDSHand(s.GetDSCard("cube")); s.MoveCardsToLocation(site,s.GetLSCard("obi"),s.GetDSCard("watto"));
        s.SkipToDSTurn(Phase.BATTLE); s.EnsureDSForcePile(3); s.DSInitiateBattle(site); s.PassAllResponses(); s.PrepareDSDestiny(2);
        int before=s.GetDSBattleDestinyCount(); s.DSPlayCard(s.GetDSCard("cube"),"Draw destiny"); reach(s,card,"Subtract 1 from destiny");
        s.LSPlayCard(card,"Subtract 1 from destiny"); s.PassAllResponses();
        assertEquals(before,s.GetDSBattleDestinyCount()); assertTrue(s.GetLSUsedPile().contains(card));
    }
    @Test public void NabrunTransportsOwnCharacterForDrawnCostAndIsLost() {
        var light=new HashMap<String,String>(); light.put("card","15_106"); light.put("han","1_11");
        var s=new VirtualTableScenario(light,new HashMap<>()); var card=s.GetLSCard("card"); var han=s.GetLSCard("han");
        s.StartGame(); s.MoveCardsToLSHand(card); s.MoveCardsToLocation(s.GetLSStartingLocation(),han); s.SkipToLSTurn(Phase.CONTROL); s.EnsureLSForcePile(4); s.PrepareLSDestiny(2); int force=s.GetLSForcePileCount();
        s.LSPlayCard(card,"'Transport' characters"); s.LSChooseCard(s.GetLSStartingLocation()); s.LSChooseCard(s.GetDSStartingLocation()); s.LSChooseCard(han); s.PassAllResponses(); s.LSChooseYes(); s.PassAllResponses();
        assertEquals(s.GetDSStartingLocation(),han.getAtLocation()); assertEquals(force-2,s.GetLSForcePileCount()); assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void QuiteAMercenaryLosesElisAndAllTransportedCharactersForTwoForce() {
        var light=new HashMap<String,String>(); light.put("card","15_106");
        var dark=new HashMap<String,String>(); dark.put("elis","1_243"); dark.put("vader","1_168");
        var s=new VirtualTableScenario(light,dark); var card=s.GetLSCard("card"); var vader=s.GetDSCard("vader"); var elis=s.GetDSCard("elis");
        s.StartGame(); s.MoveCardsToLSHand(card); s.MoveCardsToDSHand(elis); s.MoveCardsToLocation(s.GetDSStartingLocation(),vader); s.SkipToDSTurn(Phase.CONTROL); s.EnsureLSForcePile(3); s.PrepareDSDestiny(0); int force=s.GetLSForcePileCount();
        s.DSPlayCard(elis); s.DSChooseCard(s.GetDSStartingLocation()); s.DSChooseCard(s.GetLSStartingLocation()); s.DSChooseCard(vader); s.PassAllResponses(); s.DSChooseYes();
        reach(s,card,"Elis"); s.LSPlayCard(card); s.PassAllResponses();
        assertTrue(s.GetDSLostPile().contains(vader)); assertTrue(s.GetDSLostPile().contains(elis)); assertTrue(s.GetLSLostPile().contains(card)); assertEquals(force-2,s.GetLSForcePileCount());
    }
    @Test public void ChangingOddsSubtractsOneFromOpponentBattleDestinyAsUsed() {
        var light=new HashMap<String,String>(); light.put("card","15_109"); light.put("luke","1_19");
        var dark=new HashMap<String,String>(); dark.put("vader","1_168");
        var s=new VirtualTableScenario(light,dark); var card=s.GetLSCard("card"); var site=s.GetDSStartingLocation();
        s.StartGame(); s.MoveCardsToLSHand(card); s.MoveCardsToLocation(site,s.GetLSCard("luke"),s.GetDSCard("vader")); s.SkipToDSTurn(Phase.BATTLE); s.EnsureDSForcePile(3);
        s.PrepareDSDestiny(5); s.DSInitiateBattle(site); s.PassBattleStartResponses(); s.SkipToPowerSegment(); s.DSChooseYes();
        reach(s,card,"Subtract 1 from destiny");
        // Premiere Vader adds one to the drawn five before this Interrupt subtracts one.
        assertEquals(6f,s.gameState().getTopDrawDestinyState().getDrawDestinyEffect().getDestinyDrawValue(),s.epsilon);
        s.LSPlayCard(card,"Subtract 1 from destiny"); s.PassAllResponses();
        s.SkipToEndOfPowerSegment(false); assertEquals(5,s.GetDSTotalDestiny()); assertTrue(s.GetLSUsedPile().contains(card));
    }
    @Test public void NarrowEscapeRequiresRebelAbilityGreaterThanTwo() {
        var light=new HashMap<String,String>(); light.put("card","15_107"); light.put("chewie","2_3"); light.put("adjacent","1_128");
        var s=new VirtualTableScenario(light,new HashMap<>()); var card=s.GetLSCard("card"); var site=s.GetDSStartingLocation();
        s.StartGame(); s.MoveLocationToTable(s.GetLSCard("adjacent")); s.MoveCardsToLSHand(card); s.MoveCardsToLocation(site,s.GetLSCard("chewie"),s.GetDSFiller(1));
        s.SkipToDSTurn(Phase.BATTLE); s.EnsureLSForcePile(4); s.EnsureDSForcePile(2); s.DSInitiateBattle(site); s.DSPass();
        assertFalse(s.LSCardPlayAvailable(card,"Move cards with ability away")); assertTrue(s.GetLSHand().contains(card));
    }
}
