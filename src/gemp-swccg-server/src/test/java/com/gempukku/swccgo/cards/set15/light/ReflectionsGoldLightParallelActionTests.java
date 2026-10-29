package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldLightParallelActionTests {
    private VirtualTableScenario race(boolean damaged, boolean stacked) {
        var light=new HashMap<String,String>(); light.put("card","15_99"); light.put("racer","11_47");
        var dark=new HashMap<String,String>(); dark.put("arena","11_94"); dark.put("racer","11_98"); dark.put("boonta","11_79");
        var s=new VirtualTableScenario(light,dark); s.StartGame();
        var arena=s.GetDSCard("arena"); s.MoveLocationToTable(arena);
        s.MoveCardsToLocation(arena,s.GetLSCard("racer"),s.GetDSCard("racer")); s.AttachCardsTo(arena,s.GetDSCard("boonta"));
        s.GetLSCard("racer").setDamaged(damaged);
        if(stacked) {
            var l=s.GetLSDestiny(3); var d=s.GetDSDestiny(4);
            s.StackCardsOn(s.GetLSCard("racer"),l); l.setRaceDestinyForPlayer(s.LS);
            s.StackCardsOn(s.GetDSCard("racer"),d); d.setRaceDestinyForPlayer(s.DS);
        }
        s.EnsureLSForcePile(2);
        s.MoveCardsToLSHand(s.GetLSCard("card")); s.SkipToDSTurn(Phase.CONTROL);
        s.DSUseCardAction(s.GetDSCard("boonta")); s.PassAllResponses(); return s;
    }
    @Test public void LosingTrackRepairsOwnDamagedPodracerForOneForceAsLost() {
        var s=race(true,false); var racer=s.GetLSCard("racer"); var card=s.GetLSCard("card"); racer.setDamaged(true);
        s.EnsureLSForcePile(2); int before=s.GetLSForcePileCount(); s.DSPass();
        s.LSPlayCard(card,"'Repair' a Podracer"); s.LSChooseCard(racer); s.PassAllResponses();
        assertFalse(racer.isDamaged()); assertEquals(before-1,s.GetLSForcePileCount()); assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void LosingTrackDamagesOpponentPodracerWithHigherDestiny() {
        var s=race(false,false); var racer=s.GetDSCard("racer"); var card=s.GetLSCard("card");
        s.PrepareLSDestiny(7); s.DSPass(); s.LSPlayCard(card,"Make a Podracer 'damaged'"); s.LSChooseCard(racer); s.PassAllResponses();
        assertTrue(racer.isDamaged()); assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void StepBackwardSendsBothTopRaceDestiniesToTheirOwnersUsedPiles() {
        var s=race(false,true); var card=s.GetLSCard("card"); var light=s.GetStackedCards(s.GetLSCard("racer")).getFirst(); var dark=s.GetStackedCards(s.GetDSCard("racer")).getFirst(); s.DSPass();
        s.LSPlayCard(card,"Place top race destinies in Used Pile"); s.PassAllResponses(); s.LSChooseCard(light); s.PassAllResponses();
        assertTrue(s.GetLSUsedPile().contains(light)); assertTrue(s.GetDSUsedPile().contains(dark)); assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void QuiteAMercenaryStopsAnExistingSmugglerAndExpiresAtEndOfTurn() {
        var light=new HashMap<String,String>(); light.put("card","15_106"); light.put("han","1_11");
        var s=new VirtualTableScenario(light,new HashMap<>()); var card=s.GetLSCard("card"); var han=s.GetLSCard("han");
        s.StartGame(); s.MoveCardsToLSHand(card); s.MoveCardsToLocation(s.GetLSStartingLocation(),han); s.SkipToLSTurn(Phase.CONTROL);
        s.LSPlayCard(card,"Prevent a smuggler from moving"); s.LSChooseCard(han); s.PassAllResponses();
        assertTrue(s.game().getModifiersQuerying().mayNotMove(s.gameState(),han)); assertTrue(s.GetLSLostPile().contains(card));
        s.SkipToDSTurn(Phase.ACTIVATE); assertFalse(s.game().getModifiersQuerying().mayNotMove(s.gameState(),han));
    }
    @Test public void YubYubRaisesOnlyOwnEwokForfeitUntilEndOfTurnAsUsed() {
        var light=new HashMap<String,String>(); light.put("card","15_100"); light.put("ewok","8_13"); light.put("han","1_11");
        var s=new VirtualTableScenario(light,new HashMap<>()); var card=s.GetLSCard("card"); var ewok=s.GetLSCard("ewok"); var han=s.GetLSCard("han");
        s.StartGame(); s.MoveCardsToLSHand(card); s.MoveCardsToLocation(s.GetLSStartingLocation(),ewok,han); s.SkipToLSTurn(Phase.CONTROL);
        int ewokBefore=s.GetForfeit(ewok), hanBefore=s.GetForfeit(han);
        assertFalse(s.LSCardPlayAvailable(card,"Retrieve card with 'Ewok' in title"));
        s.LSPlayCard(card,"Make Ewoks forfeit +1"); s.PassAllResponses();
        assertEquals(ewokBefore+1,s.GetForfeit(ewok)); assertEquals(hanBefore,s.GetForfeit(han)); assertTrue(s.GetLSUsedPile().contains(card));
        s.SkipToDSTurn(Phase.ACTIVATE); assertEquals(ewokBefore,s.GetForfeit(ewok));
    }
    @Test public void MightOfTheRepublicSearchesPrintedAdvisorAsUsed() {
        var light=new HashMap<String,String>(); light.put("card","15_105"); light.put("mas","12_15");
        var s=new VirtualTableScenario(light,new HashMap<>()); var card=s.GetLSCard("card"); var mas=s.GetLSCard("mas");
        s.StartGame(); s.MoveCardsToLSHand(card); s.SkipToLSTurn(Phase.CONTROL); s.MoveCardsToTopOfLSReserveDeck(mas);
        s.LSPlayCard(card,"Take card into hand from Reserve Deck"); s.PassAllResponses(); s.LSChooseCard(mas); s.PassAllResponses();
        assertTrue(s.GetLSHand().contains(mas)); assertTrue(s.GetLSUsedPile().contains(card));
    }
    @Test public void MightOfTheRepublicRetrievesForSenatorOnlyOncePerGame() {
        var light=new HashMap<String,String>(); light.put("card","15_105"); light.put("senator","12_28");
        var dark=new HashMap<String,String>(); dark.put("senate","12_167");
        var s=new VirtualTableScenario(light,dark); var card=s.GetLSCard("card");
        s.StartGame(); s.MoveLocationToTable(s.GetDSCard("senate")); s.MoveCardsToLocation(s.GetDSCard("senate"),s.GetLSCard("senator"));
        s.MoveCardsToLSHand(card); s.SkipToLSTurn(Phase.CONTROL); var lost=s.GetLSFiller(1); s.MoveCardsToTopOfLSLostPile(lost);
        s.LSPlayCard(card,"Retrieve 1 Force"); s.PassAllResponses();
        assertTrue(s.GetLSUsedPile().contains(lost)); assertTrue(s.GetLSUsedPile().contains(card));
        s.MoveCardsToLSHand(card); s.MoveCardsToTopOfLSLostPile(s.GetLSFiller(2)); s.DSPass();
        assertFalse(s.LSCardPlayAvailable(card,"Retrieve 1 Force"));
    }
    @Test public void StepBackwardRetrievesPaidCharacterFromInitiallyEmptyLostPile() { new StepBackwardLostPileEligibilityTests().retrievesPaidCharacterWithoutUsedOrLostPile("15_99"); }
    @Test public void NeckAndNeckRetrievesFourForEachPlayerBelowTenLifeForceAsLost() {
        var light=new HashMap<String,String>(); light.put("card","15_109");
        var s=new VirtualTableScenario(light,new HashMap<>(),40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open); var card=s.GetLSCard("card");
        s.StartGame(); s.MoveCardsToLSHand(card); s.SkipToLSTurn(Phase.CONTROL);
        while(s.GetLSLifeForceRemaining()>9) s.MoveCardsToTopOfLSLostPile((com.gempukku.swccgo.game.PhysicalCardImpl)s.GetLSReserveDeck().getFirst());
        while(s.GetDSLifeForceRemaining()>9) s.MoveCardsToTopOfDSLostPile((com.gempukku.swccgo.game.PhysicalCardImpl)s.GetDSReserveDeck().getFirst());
        int lightLost=s.GetLSLostPileCount(), darkLost=s.GetDSLostPileCount(); s.LSPass(); s.DSPass();
        s.LSPlayCard(card,"Make both players retrieve 4 Force"); s.PassAllResponses();
        assertEquals(lightLost-4+1,s.GetLSLostPileCount()); assertEquals(darkLost-4,s.GetDSLostPileCount()); assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void NeckAndNeckLosesOneForceToStopOpponentsNextControlRaceDestiny() {
        var light=new HashMap<String,String>(); light.put("card","15_109"); light.put("racer","11_47");
        var dark=new HashMap<String,String>(); dark.put("racer","11_98"); dark.put("arena","11_94"); dark.put("boonta","11_79");
        var s=new VirtualTableScenario(light,dark); var card=s.GetLSCard("card");
        s.StartGame(); s.MoveLocationToTable(s.GetDSCard("arena")); s.MoveCardsToLocation(s.GetDSCard("arena"),s.GetLSCard("racer"),s.GetDSCard("racer"));
        s.MoveCardsToLSHand(card); s.SkipToLSTurn(Phase.BATTLE); s.gameState().setPodraceStarted(s.GetDSCard("boonta")); s.EnsureLSForcePile(2); int before=s.GetLSLifeForceRemaining(); s.LSPass(); s.DSPass();
        s.LSPlayCard(card,"Make opponent draw no race destiny"); s.PassAllResponses(); s.LSPayForceLossFromForcePile(); s.PassAllResponses();
        assertEquals(before-1,s.GetLSLifeForceRemaining()); assertTrue(s.GetLSLostPile().contains(card));
        s.SkipToDSTurn(Phase.CONTROL); assertTrue(s.game().getModifiersQuerying().mayNotDrawRaceDestiny(s.gameState(),s.DS));
        s.SkipToLSTurn(Phase.CONTROL); assertFalse(s.game().getModifiersQuerying().mayNotDrawRaceDestiny(s.gameState(),s.DS));
    }
}
