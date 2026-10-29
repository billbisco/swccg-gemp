package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldDarkComboResponseTests {
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
    @Test public void TradeFederationTacticsHitsStarfighterAsUsed() {
        var light=new HashMap<String,String>(); light.put("xwing","1_146");
        var dark=new HashMap<String,String>(); dark.put("hit","15_294"); dark.put("droid","14_116"); dark.put("system","1_282");
        var s=new VirtualTableScenario(light,dark); var hit=s.GetDSCard("hit"); var xwing=s.GetLSCard("xwing");
        s.StartGame(); s.MoveCardsToDSHand(hit); s.MoveLocationToTable(s.GetDSCard("system"));
        s.MoveCardsToLocation(s.GetDSCard("system"),xwing,s.GetDSCard("droid")); s.SkipToDSTurn(Phase.BATTLE); s.EnsureDSForcePile(2);
        s.DSInitiateBattle(s.GetDSCard("system")); s.PassAllResponses(); s.PrepareDSDestiny(7); int force=s.GetDSForcePileCount();
        s.DSPlayCard(hit,"Target starfighter"); s.DSChooseCard(xwing);
        s.PassAllResponses();
        assertTrue(xwing.isHit()); assertEquals(force,s.GetDSForcePileCount()); assertTrue(s.GetDSUsedPile().contains(hit));
    }
    @Test public void SomethingHitUsAndWhyDidntYouTellMeCancelsGiftOfTheMentorAsUsed() {
        var light=new HashMap<String,String>(); light.put("gift","1_82"); light.put("luke","1_19"); light.put("obi","1_21");
        var dark=new HashMap<String,String>(); dark.put("card","15_282"); dark.put("vader","1_168");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var gift=s.GetLSCard("gift");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLSHand(gift);
        s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetDSCard("vader"),s.GetLSCard("luke"),s.GetLSCard("obi"));
        s.SkipToLSTurn(Phase.BATTLE); s.EnsureLSForcePile(2); s.LSInitiateBattle(s.GetDSStartingLocation()); s.PassAllResponses();
        int destiny=s.GetLSBattleDestinyCount(); s.LSPlayCard(gift,"Add two battle destiny"); respondWith(s,card,"Cancel"); s.PassAllResponses();
        assertEquals(destiny,s.GetLSBattleDestinyCount()); assertTrue(s.GetLSLostPile().contains(gift)); assertTrue(s.GetDSUsedPile().contains(card));
    }
    @Test public void TheyreStillComingThroughCancelsSmokeScreenAsUsed() {
        var light=new HashMap<String,String>(); light.put("smoke","5_69"); light.put("obi","1_21");
        var dark=new HashMap<String,String>(); dark.put("card","15_291"); dark.put("vader","1_168");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var smoke=s.GetLSCard("smoke");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLSHand(smoke);
        s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetDSCard("vader"),s.GetLSCard("obi"));
        s.SkipToLSTurn(Phase.BATTLE); s.EnsureLSForcePile(2); s.LSInitiateBattle(s.GetDSStartingLocation()); s.PassAllResponses();
        s.PrepareLSDestiny(2); s.SkipToPowerSegment(); s.LSChooseYes(); s.PassResponses("COST_TO_DRAW_DESTINY_CARD"); s.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        int destiny=s.GetLSBattleDestinyCount(); s.LSPlayCard(smoke); s.LSChooseCard(s.GetLSCard("obi")); respondWith(s,card,"Cancel"); s.PassAllResponses();
        assertEquals(destiny,s.GetLSBattleDestinyCount()); assertTrue(s.GetLSLostPile().contains(smoke)); assertTrue(s.GetDSUsedPile().contains(card));
    }

    @Test public void OperationalAsPlannedAndWoundedWarriorSuppressesCharacterHitByBlaster() {
        var light=new HashMap<String,String>(); light.put("han","1_11");
        var dark=new HashMap<String,String>(); dark.put("card","15_267"); dark.put("trooper","1_194"); dark.put("blaster","1_312");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var han=s.GetLSCard("han"); var trooper=s.GetDSCard("trooper");
        s.MoveCardsToDSHand(card); s.StartGame(); s.MoveCardsToDSHand(card);
        s.MoveCardsToLocation(s.GetDSStartingLocation(),han,trooper); s.AttachCardsTo(trooper,s.GetDSCard("blaster"));
        s.SkipToDSTurn(Phase.BATTLE); s.EnsureDSForcePile(5); s.DSInitiateBattle(s.GetDSStartingLocation()); s.PassAllResponses();
        s.PrepareDSDestiny(7); s.DSUseCardAction(s.GetDSCard("blaster")); s.DSChooseCard(han); respondWith(s,card,"Reset"); s.DSChooseCard(han); s.PassAllResponses();
        assertTrue(han.isHit()); assertEquals(0,s.GetPower(han)); assertEquals(0,s.GetLSBattleDestinyCount()); assertTrue(s.GetDSUsedPile().contains(card));
    }
    private void lightsaberDeficiency(int destiny, boolean characterLost) {
        var light=new HashMap<String,String>(); light.put("leia","1_17"); light.put("saber","1_155");
        var dark=new HashMap<String,String>(); dark.put("card","15_295"); dark.put("vader","1_168");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var leia=s.GetLSCard("leia"); var saber=s.GetLSCard("saber");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLocation(s.GetDSStartingLocation(),leia,s.GetDSCard("vader")); s.AttachCardsTo(leia,saber);
        s.SkipToLSTurn(Phase.BATTLE); s.EnsureLSForcePile(5); s.LSInitiateBattle(s.GetDSStartingLocation()); s.PassAllResponses();
        s.PrepareDSDestiny(destiny); s.LSUseCardAction(saber); s.LSChooseCard(s.GetDSCard("vader"));
        respondWith(s,card,"Target character using lightsaber"); s.DSChooseCard(leia); s.PassAllResponses();
        if(characterLost) { s.LSChooseCard(leia); s.PassAllResponses(); }
        assertEquals(characterLost,s.GetLSLostPile().contains(leia)); assertTrue(s.GetLSLostPile().contains(saber)); assertTrue(s.GetDSUsedPile().contains(card));
    }
    @Test public void TrampleAndLightsaberDeficiencyEqualDestinyLosesOnlyLightsaber() { lightsaberDeficiency(3,false); }
    @Test public void TrampleAndLightsaberDeficiencyHigherDestinyLosesCharacter() { lightsaberDeficiency(4,true); }

    @Test public void TheyreStillComingThroughRetargetsFallenPortalToOpponentsCharacter() {
        var light=new HashMap<String,String>(); light.put("portal","6_66"); light.put("han","1_11");
        var dark=new HashMap<String,String>(); dark.put("card","15_291"); dark.put("vader","1_168"); dark.put("bay","1_291");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var vader=s.GetDSCard("vader"); var han=s.GetLSCard("han"); var bay=s.GetDSCard("bay");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLSHand(s.GetLSCard("portal")); s.MoveLocationToTable(bay); s.MoveCardsToLocation(bay,han,vader);
        s.SkipToDSTurn(Phase.BATTLE); s.EnsureDSForcePile(3); s.PrepareLSDestiny(5);
        s.DSUseCardAction(bay,"Initiate battle"); s.PassForceUseResponses(); s.DSPass();
        s.LSPlayCard(s.GetLSCard("portal")); s.LSChooseCard(vader); respondWith(s,card,"Re-target Fallen Portal"); s.DSChooseCard(han); s.PassAllResponses(); s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(han)); assertFalse(s.GetDSLostPile().contains(vader)); assertTrue(s.GetDSUsedPile().contains(card));
    }
    @Test public void TheCircleIsNowCompleteAndVadersObsessionDuelsLukeAfterAdjacentMove() {
        var light=new HashMap<String,String>(); light.put("luke","1_19");
        var dark=new HashMap<String,String>(); dark.put("card","15_290"); dark.put("vader","1_168"); dark.put("cantina","1_290");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var vader=s.GetDSCard("vader"); var luke=s.GetLSCard("luke"); var site=s.GetDSCard("cantina");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveLocationToTable(site); s.MoveCardsToLocation(s.GetDSStartingLocation(),vader); s.MoveCardsToLocation(site,luke);
        s.SkipToDSTurn(Phase.MOVE); s.EnsureDSForcePile(3); s.EnsureLSForcePile(3); int lightForce=s.GetLSForcePileCount();
        s.MoveCardsToTopOfDSLostPile(s.GetDSFiller(1)); int lost=s.GetDSLostPileCount();
        s.MoveCardsToTopOfDSReserveDeck(s.GetDSDestiny(0),s.GetDSDestiny(1)); s.MoveCardsToTopOfLSReserveDeck(s.GetLSDestiny(1),s.GetLSDestiny(2));
        s.DSMoveCard(vader,site); respondWith(s,card,"Initiate duel"); s.DSChooseCard(luke); s.DSChooseCard(vader); s.PassAllResponses(); s.PassAllResponses(); s.PassAllResponses();
        if(s.LSDecisionAvailable("Choose Force to lose")) { s.LSChooseCard(s.GetTopOfLSForcePile()); s.PassAllResponses(); }
        assertTrue(s.GetLSLostPile().contains(luke)); assertTrue(s.GetDSLostPile().contains(card)); assertTrue(s.GetDSUsedPile().contains(s.GetDSFiller(1))); assertEquals(lost,s.GetDSLostPileCount()); assertEquals(lightForce-1,s.GetLSForcePileCount());
    }

    @Test public void OotaGootaSoloCapturesRebelsTransportedByNabrunLeids() {
        var light=new HashMap<String,String>(); light.put("nabrun","1_97"); light.put("han","1_11");
        var dark=new HashMap<String,String>(); dark.put("card","15_266"); dark.put("vader","1_168");
        var s=new VirtualTableScenario(light,dark); var card=s.GetDSCard("card"); var nabrun=s.GetLSCard("nabrun"); var han=s.GetLSCard("han");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLSHand(nabrun); s.MoveCardsToLocation(s.GetLSStartingLocation(),han); s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetDSCard("vader"));
        s.SkipToLSTurn(Phase.CONTROL); s.EnsureDSForcePile(3); int force=s.GetDSForcePileCount(); s.PrepareLSDestiny(0);
        s.LSPlayCard(nabrun); s.LSChooseCard(s.GetLSStartingLocation()); s.LSChooseCard(s.GetDSStartingLocation()); s.LSChooseCard(han); s.PassAllResponses(); s.LSChooseYes();
        respondWith(s,card,"capture Rebels"); s.PassAllResponses();
        assertTrue(s.DSDecisionAvailable("capturing")); s.DSChooseSeizeCaptive(); s.PassAllResponses();
        assertTrue(han.isCaptive()); assertEquals(s.GetDSCard("vader"),han.getAttachedTo()); assertTrue(s.GetLSLostPile().contains(nabrun)); assertEquals(force-2,s.GetDSForcePileCount()); assertTrue(s.GetDSUsedPile().contains(card));
    }

    @Test public void TradeFederationTacticsActivatesFourAfterEnemyStarfighterHitByDroidCannons() {
        var light=new HashMap<String,String>(); light.put("xwing","1_146");
        var dark=new HashMap<String,String>(); dark.put("card","15_294"); dark.put("droid","14_116"); dark.put("weapon","14_127"); dark.put("system","1_282");
        var s=new VirtualTableScenario(light,dark,30,30,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open); var card=s.GetDSCard("card"); var xwing=s.GetLSCard("xwing"); var droid=s.GetDSCard("droid");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveLocationToTable(s.GetDSCard("system")); s.MoveCardsToLocation(s.GetDSCard("system"),xwing,droid); s.AttachCardsTo(droid,s.GetDSCard("weapon"));
        s.SkipToDSTurn(Phase.BATTLE); s.EnsureDSForcePile(5); s.DSInitiateBattle(s.GetDSCard("system")); s.PassAllResponses(); s.PrepareDSDestiny(7); int force=s.GetDSForcePileCount();
        s.DSUseCardAction(s.GetDSCard("weapon")); s.DSChooseCard(xwing); respondWith(s,card,"Activate up to 4 Force"); s.PassAllResponses(); s.PlayerDecided(s.DS,"4"); s.PassAllResponses();
        assertTrue(xwing.isHit()); assertEquals(force+2,s.GetDSForcePileCount()); assertTrue(s.GetDSUsedPile().contains(card));
    }
}
