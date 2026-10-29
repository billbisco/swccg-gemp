package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldDarkComboTimingTests {
    @Test public void MasterDestroyersAndRollingRollingRollingRelocatesDroidDuringDeployAsUsed() {
        var dark=new HashMap<String,String>(); dark.put("card","15_263"); dark.put("droid","12_104"); dark.put("site","1_291");
        var s=new VirtualTableScenario(new HashMap<>(),dark); var card=s.GetDSCard("card"); var droid=s.GetDSCard("droid"); var site=s.GetDSCard("site");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveLocationToTable(site); s.MoveCardsToLocation(s.GetDSStartingLocation(),droid);
        s.SkipToDSTurn(Phase.DEPLOY); s.DSPlayCard(card,"Relocate destroyer droid to adjacent site");
        s.DSChooseCard(droid); s.DSChooseCard(site); s.PassAllResponses();
        assertEquals(site,droid.getAtLocation()); assertTrue(s.GetDSUsedPile().contains(card));
    }
    @Test public void OverwhelmedAndADarkTimeForTheRebellionDeploysSystemAfterOpponentsPlanetSite() {
        var light=new HashMap<String,String>(); light.put("site","1_128");
        var dark=new HashMap<String,String>(); dark.put("card","15_269"); dark.put("system","1_289");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var system=s.GetDSCard("system"); var site=s.GetLSCard("site");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLSHand(site); s.SkipToLSTurn(Phase.DEPLOY);
        s.MoveCardsToTopOfDSReserveDeck(system); s.LSDeployLocation(site); s.PassCardAndForceUseResponses(); s.LSPass();
        s.DSPlayCard(card,"Deploy Tatooine system from Reserve Deck"); s.PassAllResponses();
        s.DSChooseCard(system); s.PassAllResponses();
        assertEquals(Zone.LOCATIONS,system.getZone()); assertTrue(s.GetDSLostPile().contains(card));
    }
    @Test public void OotaGootaSoloPreventsJustDeployedSmugglerFromMovingAsUsed() {
        var light=new HashMap<String,String>(); light.put("han","1_11");
        var dark=new HashMap<String,String>(); dark.put("card","15_266");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var han=s.GetLSCard("han");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLSHand(han); s.SkipToLSTurn(Phase.DEPLOY); s.EnsureLSForcePile(6);
        s.LSDeployCard(han); s.LSChooseCard(s.GetLSStartingLocation()); s.PassCardAndForceUseResponses(); s.LSPass();
        s.DSPlayCard(card); s.DSChooseCard(han); s.PassAllResponses();
        assertTrue(s.game().getModifiersQuerying().mayNotMove(s.gameState(),han)); assertTrue(s.GetDSUsedPile().contains(card));
    }
    @Test public void OperationalAsPlannedAndWoundedWarriorStartingModeSearchesThreeAndDrawsSixMore() {
        var dark=new HashMap<String,String>(); dark.put("card","15_267"); dark.put("ds2","9_142"); dark.put("jerjerrod","9_117"); dark.put("effect","1_219");
        var s=new VirtualTableScenario(new HashMap<>(),dark,30,30,
            StartingSetup.DefaultLSGroundLocation,StartingSetup.DSStartingLocation("8_157"),
            StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
        var card=s.GetDSCard("card"); s.StartGame(false); s.DSChooseCard(card); s.PassAllResponses();
        s.DSChooseCards(s.GetDSCard("ds2"),s.GetDSCard("jerjerrod"),s.GetDSCard("effect")); s.PassAllResponses();
        assertTrue(s.GetDSHand().contains(s.GetDSCard("ds2")));
        assertTrue(s.GetDSHand().contains(s.GetDSCard("jerjerrod")));
        assertTrue(s.GetDSHand().contains(s.GetDSCard("effect")));
        assertEquals(9,s.GetDSHandCount()); assertTrue(s.GetDSLostPile().contains(card));
    }
    @Test public void StartYourEnginesAndPitCrewsStartingModeDeploysArenaRacerEventAndFreeEffect() {
        var dark=new HashMap<String,String>(); dark.put("card","15_285"); dark.put("arena","11_94"); dark.put("racer","11_98"); dark.put("boonta","11_79"); dark.put("effect","9_121");
        var s=new VirtualTableScenario(new HashMap<>(),dark); var card=s.GetDSCard("card");
        s.StartGame(false); s.DSChooseCard(card); s.PassAllResponses(); s.DSChooseOption("Left"); s.PassAllResponses(); s.LSChooseCards(); s.PassAllResponses();
        assertEquals(Zone.LOCATIONS,s.GetDSCard("arena").getZone());
        assertEquals(s.GetDSCard("arena"),s.GetDSCard("racer").getAtLocation());
        assertEquals(s.GetDSCard("arena"),s.GetDSCard("boonta").getAttachedTo());
        assertEquals(Zone.SIDE_OF_TABLE,s.GetDSCard("effect").getZone());
        assertFalse(s.GetDSLostPile().contains(card)); assertFalse(s.GetDSUsedPile().contains(card));
        assertTrue(s.GetDSReserveDeck().contains(card)||s.GetDSHand().contains(card)); assertEquals(8,s.GetDSHandCount());
    }

    @Test public void PodracerCollisionAndHitRacerDamagesOpponentAndStopsRaceDestinyForTwoForce() {
        var light=new HashMap<String,String>(); light.put("racer","11_47");
        var dark=new HashMap<String,String>(); dark.put("card","15_272"); dark.put("arena","11_94"); dark.put("racer","11_98"); dark.put("boonta","11_79");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var racer=s.GetLSCard("racer"); var arena=s.GetDSCard("arena"); var boonta=s.GetDSCard("boonta");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveLocationToTable(arena); s.MoveCardsToLocation(arena,racer,s.GetDSCard("racer")); s.AttachCardsTo(arena,boonta);
        var race=s.GetLSDestiny(4); s.StackCardsOn(racer,race); race.setRaceDestinyForPlayer(s.LS);
        s.SkipToDSTurn(Phase.CONTROL); s.DSUseCardAction(boonta); s.PassAllResponses(); s.LSPass(); s.EnsureDSForcePile(3); int force=s.GetDSForcePileCount();
        s.PrepareDSDestiny(7); s.DSPlayCard(card,"Target a Podracer"); s.DSChooseCard(racer); s.PassAllResponses();
        assertTrue(racer.isDamaged()); assertTrue(s.game().getModifiersQuerying().mayNotDrawRaceDestiny(s.gameState(),s.LS));
        assertEquals(force-2,s.GetDSForcePileCount()); assertTrue(s.GetDSLostPile().contains(card));
    }

    @Test public void PodracerCollisionAndHitRacerCannotTargetRaceTotalFiveAhead() {
        var light=new HashMap<String,String>(); light.put("racer","11_47");
        var dark=new HashMap<String,String>(); dark.put("card","15_272"); dark.put("arena","11_94"); dark.put("racer","11_98"); dark.put("boonta","11_79");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var racer=s.GetLSCard("racer"); var arena=s.GetDSCard("arena"); var boonta=s.GetDSCard("boonta");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveLocationToTable(arena); s.MoveCardsToLocation(arena,racer,s.GetDSCard("racer")); s.AttachCardsTo(arena,boonta);
        var race=s.GetLSDestiny(5); s.StackCardsOn(racer,race); race.setRaceDestinyForPlayer(s.LS);
        s.SkipToDSTurn(Phase.CONTROL); s.EnsureDSForcePile(3); s.DSUseCardAction(boonta); s.PassAllResponses(); s.LSPass();
        assertEquals(5f,s.game().getModifiersQuerying().getPodracerRaceTotal(s.gameState(),racer),s.epsilon);
        assertFalse(s.DSCardPlayAvailable(card,"Target a Podracer")); assertFalse(racer.isDamaged()); assertTrue(s.GetDSHand().contains(card));
    }
}
