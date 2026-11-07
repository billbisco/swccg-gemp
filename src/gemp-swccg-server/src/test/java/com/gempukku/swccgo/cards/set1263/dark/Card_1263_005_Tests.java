package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1263_005_Tests {
    private static final String CAPTURE="Capture transported character";
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{put("luke","1_19");put("leia","1_17");put("droid","1_24");put("nabrun","1_97");put("nabrun2","1_97");put("obi","108_4");put("blaster","1_153");}},
                new HashMap<>() {{put("jodo","1263_5");put("trooper","1_194");put("blaster","1_317");put("hidden","6_154");put("elis","1_243");put("camp","1_292");put("wastes","1_293");put("farm","1_294");put("town","1_295");}},
                15,15,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    @Test public void JodoKastStatsIconsAndKeywordsMatchPrintedImage() {
        var scn=scenario();var b=scn.GetDSCard("jodo").getBlueprint();
        assertEquals("Jodo Kast",b.getTitle());assertTrue(b.hasVirtualSuffix());assertTrue(b.isLegacy());assertEquals(Side.DARK,b.getSide());assertEquals(Uniqueness.UNIQUE,b.getUniqueness());
        assertEquals(2,b.getDestiny(),scn.epsilon);assertEquals(4,b.getDeployCost(),scn.epsilon);assertEquals(3,b.getPower(),scn.epsilon);assertEquals(3,b.getAbility(),scn.epsilon);assertEquals(5,b.getArmor(),scn.epsilon);assertEquals(5,b.getForfeit(),scn.epsilon);
        assertTrue(GameTextActionId.LEGACY__JODO_KAST_V__CAPTURE.isPerGame());assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3,b.getExpansionSet());assertEquals(Rarity.V,b.getRarity());
        assertEquals("Bounty Hunter and scout. Perfectionist. Equipped with mandalorian armor and a jet pack. He doesn't mind being mistaken for Boba Fett. Occasionally works for Black Sun.",b.getLore());
        scn.BlueprintCardTypeCheck(b,new ArrayList<>() {{add(CardType.ALIEN);}});
        scn.BlueprintIconCheck(b,new ArrayList<>() {{add(Icon.ALIEN);add(Icon.WARRIOR);add(Icon.PREMIUM);add(Icon.LEGACY_BLOCK_6);}});
        scn.BlueprintKeywordCheck(b,new ArrayList<>() {{add(Keyword.BOUNTY_HUNTER);add(Keyword.SCOUT);}});
    }
    private VirtualTableScenario transport(boolean present,String target) {
        var scn=scenario();scn.StartGame();if(present)scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("jodo"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(),scn.GetLSCard(target));scn.MoveCardsToLSHand(scn.GetLSCard("nabrun"),scn.GetLSCard("nabrun2"));scn.SkipToLSTurn(Phase.CONTROL);return scn;
    }
    private void move(VirtualTableScenario scn,PhysicalCardImpl interrupt,PhysicalCardImpl target,PhysicalCardImpl destination) {
        scn.PrepareLSDestiny(0);scn.LSPlayCard(interrupt);scn.LSChooseCard((PhysicalCardImpl)target.getAtLocation());scn.LSChooseCard(destination);scn.LSChooseCard(target);
        scn.PassAllResponses();scn.LSChooseYes();
    }
    private void reachCapture(VirtualTableScenario scn) {
        for(int i=0;i<30&&!scn.DSCardActionAvailable(scn.GetDSCard("jodo"),CAPTURE);i++) {
            assertNotNull(scn.GetCurrentDecision());assertTrue(scn.GetCurrentDecision().getText().toLowerCase().contains("optional"));
            if(scn.DSGetDecision()!=null)scn.DSPass();else scn.LSPass();
        }
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("jodo"),CAPTURE));
    }
    private void capture(VirtualTableScenario scn,PhysicalCardImpl target) {
        reachCapture(scn);scn.DSUseCardAction(scn.GetDSCard("jodo"),CAPTURE);
        if(scn.DSHasCardChoiceAvailable(target))scn.DSChooseCard(target);scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("capturing"));scn.DSChooseSeizeCaptive();scn.PassAllResponses();assertTrue(target.isCaptive());assertEquals(scn.GetDSCard("jodo"),target.getAttachedTo());
    }
    @Test public void JodoKastActuallyCapturesCharacterTransportedHereByNabrunLeids() {
        var scn=transport(true,"luke");move(scn,scn.GetLSCard("nabrun"),scn.GetLSCard("luke"),scn.GetDSStartingLocation());capture(scn,scn.GetLSCard("luke"));
    }
    @Test public void JodoKastCaptureCostsNoForceAndUsesNormalCapturePipeline() {
        var scn=transport(true,"luke");int before=scn.GetDSForcePileCount();move(scn,scn.GetLSCard("nabrun"),scn.GetLSCard("luke"),scn.GetDSStartingLocation());capture(scn,scn.GetLSCard("luke"));assertEquals(before,scn.GetDSForcePileCount());
    }
    @Test public void JodoKastAlsoCapturesTransportedDroidBecauseItIsACharacter() {
        var scn=transport(true,"droid");move(scn,scn.GetLSCard("nabrun"),scn.GetLSCard("droid"),scn.GetDSStartingLocation());capture(scn,scn.GetLSCard("droid"));
    }
    @Test public void JodoKastMayDeclineCaptureLeavingTransportedCharacterAtSite() {
        var scn=transport(true,"luke");move(scn,scn.GetLSCard("nabrun"),scn.GetLSCard("luke"),scn.GetDSStartingLocation());reachCapture(scn);scn.DSPass();scn.PassAllResponses();assertFalse(scn.GetLSCard("luke").isCaptive());assertEquals(scn.GetDSStartingLocation(),scn.GetLSCard("luke").getAtLocation());
    }
    @Test public void JodoKastCannotCaptureCharacterAlreadyHereInsteadOfNewArrival() {
        var scn=transport(true,"luke");scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetLSCard("leia"));move(scn,scn.GetLSCard("nabrun"),scn.GetLSCard("luke"),scn.GetDSStartingLocation());reachCapture(scn);scn.DSUseCardAction(scn.GetDSCard("jodo"),CAPTURE);
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetLSCard("luke")));assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("leia")));
    }
    @Test public void JodoKastAbsentDoesNotCaptureNabrunArrival() {
        var scn=transport(false,"luke");move(scn,scn.GetLSCard("nabrun"),scn.GetLSCard("luke"),scn.GetDSStartingLocation());scn.PassAllResponses();assertFalse(scn.GetLSCard("luke").isCaptive());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("jodo"),CAPTURE));
    }
    @Test public void JodoKastDoesNotCaptureAtAnotherDestination() {
        var scn=transport(true,"luke");scn.MoveLocationToTable(scn.GetDSCard("camp"));move(scn,scn.GetLSCard("nabrun"),scn.GetLSCard("luke"),scn.GetDSCard("camp"));scn.PassAllResponses();assertFalse(scn.GetLSCard("luke").isCaptive());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("jodo"),CAPTURE));
    }
    @Test public void JodoKastCannotCaptureAgainWithSecondNabrunInSameGame() {
        var scn=transport(true,"luke");move(scn,scn.GetLSCard("nabrun"),scn.GetLSCard("luke"),scn.GetDSStartingLocation());capture(scn,scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(),scn.GetLSCard("leia"));scn.SkipToLSTurn(Phase.CONTROL);move(scn,scn.GetLSCard("nabrun2"),scn.GetLSCard("leia"),scn.GetDSStartingLocation());scn.PassAllResponses();assertFalse(scn.GetLSCard("leia").isCaptive());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("jodo"),CAPTURE));
    }
    private void force(VirtualTableScenario scn,boolean dark,int amount) {
        String player=dark?scn.DS:scn.LS;var cards=scn.gameState().getForcePile(player).toArray(PhysicalCardImpl[]::new);
        if(dark){scn.MoveCardsToTopOfDSReserveDeck(cards);scn.EnsureDSForcePile(amount);}else{scn.MoveCardsToTopOfLSReserveDeck(cards);scn.EnsureLSForcePile(amount);}
    }
    private VirtualTableScenario weapon(boolean present,boolean sameSite,boolean permanent,int force) {
        var scn=scenario();scn.StartGame();var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,permanent?scn.GetLSCard("obi"):scn.GetLSCard("luke"),scn.GetDSCard("trooper"));if(present)scn.MoveCardsToLocation(sameSite?site:scn.GetLSStartingLocation(),scn.GetDSCard("jodo"));
        if(!permanent)scn.AttachCardsTo(scn.GetLSCard("luke"),scn.GetLSCard("blaster"));scn.SkipToDSTurn(Phase.BATTLE);force(scn,false,force);scn.DSInitiateBattle(site);scn.PassAllResponses();scn.DSPass();return scn;
    }
    private void fire(VirtualTableScenario scn,boolean permanent) {
        if(permanent){scn.PrepareLSDestiny(3);scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("leia"));}else scn.PrepareLSDestiny(5);
        scn.LSUseCardAction(scn.GetLSCard(permanent?"obi":"blaster"),"Fire");scn.LSChooseCard(scn.GetDSCard("trooper"));scn.PassForceUseResponses();scn.PassWeaponFireWithDestinyDraw(permanent?2:1);scn.PassAllResponses();
    }
    @Test public void JodoKastOpponentOrdinaryWeaponPaysNormalCostPlusOne() {var scn=weapon(true,true,false,3);fire(scn,false);assertEquals(0,scn.GetLSForcePileCount());assertTrue(scn.GetDSCard("trooper").isHit());}
    @Test public void JodoKastOpponentTwoForceCannotPayThreeForceShot() {var scn=weapon(true,true,false,2);assertTrue(scn.AwaitingLSWeaponsSegmentActions());assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("blaster"),"Fire"));}
    @Test public void JodoKastOpponentFreePermanentWeaponStillPaysExtraOne() {var scn=weapon(true,true,true,1);fire(scn,true);assertEquals(0,scn.GetLSForcePileCount());assertTrue(scn.GetDSCard("trooper").isHit());}
    @Test public void JodoKastOpponentPermanentWeaponCannotFireWithoutExtraForce() {var scn=weapon(true,true,true,0);assertTrue(scn.AwaitingLSWeaponsSegmentActions());assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("obi"),"Fire"));}
    @Test public void JodoKastAtAnotherSiteDoesNotTaxWeapon() {var scn=weapon(true,false,false,2);fire(scn,false);assertEquals(0,scn.GetLSForcePileCount());}
    @Test public void JodoKastAbsentDoesNotTaxWeapon() {var scn=weapon(false,false,false,2);fire(scn,false);assertEquals(0,scn.GetLSForcePileCount());}
    @Test public void JodoKastEnablesActualHiddenWeaponsPlayWithoutBobaOrArmorCard() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("jodo"),scn.GetLSCard("luke"));scn.MoveCardsToDSHand(scn.GetDSCard("hidden"));scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation());
        scn.PrepareDSDestiny(6);assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("hidden")));scn.DSPlayCard(scn.GetDSCard("hidden"));scn.DSChooseCard(scn.GetLSCard("luke"));scn.PassAllResponses();assertInZone(Zone.LOST_PILE,scn.GetLSCard("luke"));
    }
    private VirtualTableScenario movement() {
        var scn=scenario();scn.StartGame();for(String n:List.of("camp","wastes","farm","town"))scn.MoveLocationToTable(scn.GetDSCard(n));return scn;
    }
    private PhysicalCardImpl[] pair(VirtualTableScenario scn,int distance) {
        var sites=List.of(scn.GetDSStartingLocation(),scn.GetDSCard("camp"),scn.GetDSCard("wastes"),scn.GetDSCard("farm"),scn.GetDSCard("town"));
        for(var from:sites)for(var to:sites)if(Integer.valueOf(distance).equals(scn.game().getModifiersQuerying().getDistanceBetweenSites(scn.gameState(),from,to)))return new PhysicalCardImpl[]{from,to};
        fail("Jodo Kast movement fixture requires requested real site distance");return null;
    }
    @Test public void JodoKastActuallyFliesThreeSitesUsingPrintedLandspeed() {
        var scn=movement();var sites=pair(scn,3);scn.MoveCardsToLocation(sites[0],scn.GetDSCard("jodo"));scn.SkipToDSTurn(Phase.MOVE);assertEquals(3,scn.GetLandspeed(scn.GetDSCard("jodo")));int before=scn.GetDSForcePileCount();
        scn.DSMoveCard(scn.GetDSCard("jodo"),sites[1]);scn.PassAllResponses();assertEquals(sites[1],scn.GetDSCard("jodo").getAtLocation());assertEquals(before-1,scn.GetDSForcePileCount());
    }
    @Test public void JodoKastCannotFlyFourSitesWithLandspeedThree() {
        var scn=movement();var sites=pair(scn,4);scn.MoveCardsToLocation(sites[0],scn.GetDSCard("jodo"));scn.SkipToDSTurn(Phase.MOVE);scn.DSUseCardAction(scn.GetDSCard("jodo"),"Move");assertFalse(scn.DSHasCardChoiceAvailable(sites[1]));
    }
    @Test public void JodoKastDoesNotCaptureCharacterRelocatedByAnotherTransportInterrupt() {
        var scn=transport(true,"luke");var elis=scn.GetDSCard("elis");elis.setOwner(scn.LS);scn.MoveCardsToLSHand(elis);
        move(scn,elis,scn.GetLSCard("luke"),scn.GetDSStartingLocation());scn.PassAllResponses();assertEquals(scn.GetDSStartingLocation(),scn.GetLSCard("luke").getAtLocation());assertFalse(scn.GetLSCard("luke").isCaptive());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("jodo"),CAPTURE));
    }
    @Test public void JodoKastDoesNotCaptureWhenNabrunTransportIsDeclined() {
        var scn=transport(true,"luke");scn.PrepareLSDestiny(0);scn.LSPlayCard(scn.GetLSCard("nabrun"));scn.LSChooseCard(scn.GetLSStartingLocation());scn.LSChooseCard(scn.GetDSStartingLocation());scn.LSChooseCard(scn.GetLSCard("luke"));scn.PassAllResponses();scn.LSChooseNo();scn.PassAllResponses();
        assertEquals(scn.GetLSStartingLocation(),scn.GetLSCard("luke").getAtLocation());assertFalse(scn.GetLSCard("luke").isCaptive());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("jodo"),CAPTURE));
    }
}
