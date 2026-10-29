package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldDarkComboControlTests {
    private VirtualTableScenario scenario(HashMap<String,String> light, HashMap<String,String> dark) {
        return new VirtualTableScenario(light,dark,30,30,
            com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
            com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
            com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
            com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
            com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
            com.gempukku.swccgo.framework.StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    @Test public void MasterDestroyersAndRollingRollingRollingSuppressesCharacterAtTwoDestroyerDroids() {
        var light = new HashMap<String,String>(); light.put("obi", "1_21");
        var dark = new HashMap<String,String>(); dark.put("card", "15_263"); dark.put("one", "12_104"); dark.put("two", "12_104");
        var s = new VirtualTableScenario(light, dark); var card=s.GetDSCard("card"); var obi=s.GetLSCard("obi");
        s.StartGame(); s.MoveCardsToDSHand(card);
        s.MoveCardsToLocation(s.GetDSStartingLocation(), obi, s.GetDSCard("one"), s.GetDSCard("two"));
        s.SkipToDSTurn(Phase.CONTROL); int power=s.GetPower(obi);
        s.DSPlayCard(card,"Target a character"); s.DSChooseCard(obi); s.PassAllResponses();
        assertEquals(power-2,s.GetPower(obi)); assertTrue(s.GetDSUsedPile().contains(card));
    }
    @Test public void OperationalAsPlannedAndWoundedWarriorCancelsLeiaUntilEndOfTurnAsUsed() {
        var light=new HashMap<String,String>(); light.put("leia","1_17");
        var dark=new HashMap<String,String>(); dark.put("card","15_267");
        var s=scenario(light,dark); var card=s.GetDSCard("card"); var leia=s.GetLSCard("leia");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLocation(s.GetLSStartingLocation(),leia);
        s.SkipToDSTurn(Phase.CONTROL); s.DSPlayCard(card,"Cancel Leia's game text"); s.DSChooseCard(leia); s.PassAllResponses();
        assertTrue(s.game().getModifiersQuerying().isGameTextCanceled(s.gameState(),leia));
        assertTrue(s.GetDSUsedPile().contains(card));
        s.SkipToPhase(Phase.ACTIVATE);
        assertFalse(s.game().getModifiersQuerying().isGameTextCanceled(s.gameState(),leia));
    }
    @Test public void PointManAndSlipSlidingAwayCancelsScrambleAsUsed() {
        var light=new HashMap<String,String>(); light.put("scramble","4_37");
        var dark=new HashMap<String,String>(); dark.put("card","15_273"); dark.put("vader","1_168");
        var s=scenario(light,dark); var card=s.GetDSCard("card"); var effect=s.GetLSCard("scramble");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetDSCard("vader"));
        s.MoveCardsToDSSideOfTable(effect); s.SkipToDSTurn(Phase.CONTROL);
        s.DSPlayCard(card,"Cancel Scramble"); s.DSChooseCard(effect); s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(effect)); assertTrue(s.GetDSUsedPile().contains(card));
    }
    @Test public void PutAllSectionsOnAlertAndFullScaleAlertCountsAbilityThreeImperialsAndExemptsSmugglers() {
        var light=new HashMap<String,String>(); light.put("luke","1_19"); light.put("han","1_11");
        var dark=new HashMap<String,String>(); dark.put("card","15_275"); dark.put("tarkin","1_179"); dark.put("tagge","1_178");
        var s=scenario(light,dark); var card=s.GetDSCard("card"); var luke=s.GetLSCard("luke"); var han=s.GetLSCard("han");
        s.StartGame(); s.MoveCardsToDSHand(card);
        s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetDSCard("tarkin"),s.GetDSCard("tagge"),luke,han);
        s.SkipToDSTurn(Phase.CONTROL); s.EnsureDSForcePile(3); s.PrepareDSDestiny(1); int force=s.GetDSForcePileCount();
        s.DSPlayCard(card,"Block opponent's movement"); s.PassAllResponses();
        assertTrue(s.game().getModifiersQuerying().mayNotMove(s.gameState(),luke));
        assertFalse(s.game().getModifiersQuerying().mayNotMove(s.gameState(),han));
        assertEquals(force-2,s.GetDSForcePileCount()); assertTrue(s.GetDSLostPile().contains(card));
        s.SkipToLSTurn(Phase.CONTROL); assertFalse(s.game().getModifiersQuerying().mayNotMove(s.gameState(),luke));
    }
    @Test public void TrampleAndLightsaberDeficiencyCancelsLightsaberProficiencyAsUsed() {
        var light=new HashMap<String,String>(); light.put("obi","1_21"); light.put("saber","1_157"); light.put("effect","1_54");
        var dark=new HashMap<String,String>(); dark.put("card","15_295");
        var s=scenario(light,dark); var card=s.GetDSCard("card"); var obi=s.GetLSCard("obi"); var effect=s.GetLSCard("effect");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLocation(s.GetLSStartingLocation(),obi);
        s.AttachCardsTo(obi,s.GetLSCard("saber"),effect); s.SkipToDSTurn(Phase.CONTROL);
        s.DSPlayCard(card); s.DSChooseCard(effect); s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(effect)); assertTrue(s.GetDSUsedPile().contains(card));
    }
    @Test public void WeMustAccelerateOurPlansAndVoteNowDeploysBlockadeFlagshipSiteAsUsed() {
        var dark=new HashMap<String,String>(); dark.put("card","15_304"); dark.put("bridge","12_164"); dark.put("ship","14_114"); dark.put("system","1_282");
        var s=new VirtualTableScenario(new HashMap<>(),dark); var card=s.GetDSCard("card"); var bridge=s.GetDSCard("bridge");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveLocationToTable(s.GetDSCard("system"));
        s.MoveCardsToLocation(s.GetDSCard("system"),s.GetDSCard("ship"));
        s.MoveCardsToBottomOfDSReserveDeck(bridge); s.SkipToDSTurn(Phase.DEPLOY);
        s.DSPlayCard(card,"Deploy a Blockade Flagship site from Reserve Deck"); s.PassAllResponses();
        s.DSChooseCard(bridge); s.PassAllResponses();
        assertEquals(Zone.LOCATIONS,bridge.getZone()); assertTrue(s.GetDSUsedPile().contains(card));
    }
    @Test public void WeMustAccelerateOurPlansAndVoteNowSearchesPodracerTextInterruptAsUsed() {
        var dark=new HashMap<String,String>(); dark.put("card","15_304"); dark.put("pit","11_84");
        var s=new VirtualTableScenario(new HashMap<>(),dark); var card=s.GetDSCard("card"); var pit=s.GetDSCard("pit");
        s.StartGame(); s.MoveCardsToDSHand(card); s.SkipToDSTurn(Phase.CONTROL); s.MoveCardsToTopOfDSReserveDeck(pit);
        s.DSPlayCard(card,"Take Interrupt into hand from Reserve Deck"); s.PassAllResponses();
        s.DSChooseCard(pit); s.PassAllResponses();
        assertTrue(s.GetDSHand().contains(pit)); assertTrue(s.GetDSUsedPile().contains(card));
    }

    private void voyeur(boolean lost) {
        var light=new HashMap<String,String>(); light.put("one","1_105"); light.put("two","1_105");
        var dark=new HashMap<String,String>(); dark.put("card","15_280");
        var s=scenario(light,dark); var card=s.GetDSCard("card"); var one=s.GetLSCard("one"); var two=s.GetLSCard("two");
        s.StartGame(); s.MoveCardsToDSHand(card); s.SkipToDSTurn(Phase.CONTROL);
        s.MoveCardsToTopOfLSUsedPile(one,two); s.SkipToPhase(Phase.DEPLOY);
        s.EnsureLSForcePile(3); int force=s.GetLSForcePileCount();
        s.DSPlayCard(card,"Reveal opponent's Used Pile"); s.DSChooseOption("Rebel Barrier"); s.PassAllResponses();
        s.DSDismissRevealedCards(); s.LSDismissRevealedCards(); s.PassAllResponses();
        s.DSChooseOption(lost?"Lost":"Used"); s.PassAllResponses();
        if(lost) {
            s.LSChooseCard(two); s.PassAllResponses();
            assertTrue(s.GetLSLostPile().containsAll(java.util.List.of(one,two)));
            assertTrue(s.GetDSLostPile().contains(card));
        } else {
            for(int i=0;i<2;i++) { s.LSChooseCard(s.GetTopOfLSForcePile()); s.PassAllResponses(); }
            assertEquals(force-2,s.GetLSForcePileCount());
            assertTrue(s.GetLSUsedPile().containsAll(java.util.List.of(one,two))); assertTrue(s.GetDSUsedPile().contains(card));
        }
    }
    @Test public void ShutHimUpOrShutHimDownAndVoyeurUsedLosesForceForEachNamedCopy() { voyeur(false); }
    @Test public void ShutHimUpOrShutHimDownAndVoyeurLostLosesEachNamedCopy() { voyeur(true); }
    @Test public void WattosChanceCubeAndYouSwindledMeRetrievalChoiceRetrievesOneAndThreeAsLost() {
        var dark=new HashMap<String,String>(); dark.put("card","15_302");
        var s=scenario(new HashMap<>(),dark); var card=s.GetDSCard("card");
        var one=s.GetDSFiller(1); var two=s.GetDSFiller(2); var three=s.GetDSFiller(3); var light=s.GetLSFiller(1);
        s.StartGame(); s.MoveCardsToDSHand(card); s.SkipToDSTurn(Phase.CONTROL);
        s.MoveCardsToTopOfDSLostPile(one,two,three); s.MoveCardsToTopOfLSLostPile(light);
        s.DSPlayCard(card,"Make opponent choose"); s.PassAllResponses(); s.LSChooseOption("Retrieve 1 Force"); s.PassAllResponses();
        assertTrue(s.GetDSUsedPile().containsAll(java.util.List.of(one,two,three))); assertTrue(s.GetLSUsedPile().contains(light));
        assertTrue(s.GetDSLostPile().contains(card));
    }

    @Test public void TrampleAndLightsaberDeficiencyDestroysUnpilotedManeuverVehicle() {
        var light=new HashMap<String,String>(); light.put("speeder","1_151");
        var dark=new HashMap<String,String>(); dark.put("card","15_295"); dark.put("walker","3_156");
        var s=scenario(light,dark); var card=s.GetDSCard("card"); var speeder=s.GetLSCard("speeder");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLocation(s.GetDSStartingLocation(),speeder,s.GetDSCard("walker"));
        s.SkipToDSTurn(Phase.CONTROL); s.PrepareDSDestiny(6); s.DSPlayCard(card,"Make character or vehicle lost"); s.DSChooseCard(speeder); s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(speeder)); assertTrue(s.GetDSUsedPile().contains(card));
    }

    @Test public void TrampleAndLightsaberDeficiencyTargetsCrashedButNotPilotedVehicle() {
        var light=new HashMap<String,String>(); light.put("crashed","3_69"); light.put("piloted","3_69");
        var dark=new HashMap<String,String>(); dark.put("card","15_295"); dark.put("walker","3_156");
        var s=scenario(light,dark); var card=s.GetDSCard("card"); var crashed=s.GetLSCard("crashed"); var piloted=s.GetLSCard("piloted");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLocation(s.GetDSStartingLocation(),crashed,piloted,s.GetDSCard("walker")); crashed.setCrashed(true);
        s.SkipToDSTurn(Phase.CONTROL); s.PrepareDSDestiny(6); s.DSPlayCard(card,"Make character or vehicle lost");
        assertTrue(s.DSHasCardChoiceAvailable(crashed)); assertFalse(s.DSHasCardChoiceAvailable(piloted)); s.DSChooseCard(crashed); s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(crashed)); assertFalse(s.GetLSLostPile().contains(piloted)); assertTrue(s.GetDSUsedPile().contains(card));
    }

    @Test public void TrampleAndLightsaberDeficiencyDoesNotDestroyVehicleOnDestinySeven() {
        var light=new HashMap<String,String>(); light.put("speeder","1_151");
        var dark=new HashMap<String,String>(); dark.put("card","15_295"); dark.put("walker","3_156");
        var s=scenario(light,dark); var card=s.GetDSCard("card"); var speeder=s.GetLSCard("speeder");
        s.StartGame(); s.MoveCardsToDSHand(card); s.MoveCardsToLocation(s.GetDSStartingLocation(),speeder,s.GetDSCard("walker"));
        s.SkipToDSTurn(Phase.CONTROL); s.PrepareDSDestiny(7); s.DSPlayCard(card,"Make character or vehicle lost"); s.DSChooseCard(speeder); s.PassAllResponses();
        assertEquals(s.GetDSStartingLocation(),speeder.getAtLocation()); assertFalse(s.GetLSLostPile().contains(speeder)); assertTrue(s.GetDSUsedPile().contains(card));
    }
}
