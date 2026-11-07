package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1250_002_Tests {
    private static final String RELOCATE="Relocate to opponent's Jedi site",FIRE="Fire weapon at a Jedi";
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{put("luke","1_19");put("knight","9_24");put("obi","1_21");put("leia","1_17");put("chamber","12_76");}},
                new HashMap<>() {{put("aurra","1250_2");put("blaster","1_317");put("rifle","1_312");put("trooper","1_194");put("device","1_201");}},
                15,15,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    @Test public void AurraSingStatsIconsAndKeywordsMatchPrintedImageAndSourcePack() {
        var scn=scenario();var b=scn.GetDSCard("aurra").getBlueprint();
        assertEquals("Aurra Sing",b.getTitle());assertTrue(b.hasVirtualSuffix());assertTrue(b.isLegacy());assertEquals(Side.DARK,b.getSide());assertEquals(Uniqueness.UNIQUE,b.getUniqueness());
        assertEquals(1,b.getDestiny(),scn.epsilon);assertEquals(4,b.getDeployCost(),scn.epsilon);assertEquals(4,b.getPower(),scn.epsilon);assertEquals(4,b.getAbility(),scn.epsilon);assertEquals(3,b.getForfeit(),scn.epsilon);
        assertTrue(b.hasPersona(Persona.AURRA));assertTrue(GameTextActionId.LEGACY__AURRA_SING_V__FIRE_WEAPON.isPerGame());assertEquals(ExpansionSet.VIRTUAL_BLOCK_5,b.getExpansionSet());assertEquals(Rarity.V,b.getRarity());
        assertEquals("Bounty hunter. Former student of the Force. After failing her Jedi training, Aurra became known for hunting down and killing Jedi Knights.",b.getLore());
        scn.BlueprintCardTypeCheck(b,new ArrayList<>() {{add(CardType.ALIEN);}});
        scn.BlueprintIconCheck(b,new ArrayList<>() {{add(Icon.ALIEN);add(Icon.WARRIOR);add(Icon.TATOOINE);add(Icon.EPISODE_I);add(Icon.LEGACY_BLOCK_5);}});
        scn.BlueprintKeywordCheck(b,new ArrayList<>() {{add(Keyword.BOUNTY_HUNTER);}});
    }
    private void darkActivateWindow(VirtualTableScenario scn) {
        if(scn.AwaitingLSActivatePhaseActions())scn.LSPass();
        if(scn.LSDecisionAvailable("You have not activated Force"))scn.LSChooseYes();assertNotNull(scn.DSGetDecision());
    }
    private VirtualTableScenario relocationSetup(String target) {
        var scn=scenario();scn.StartGame();scn.MoveLocationToTable(scn.GetLSCard("chamber"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("aurra"));scn.MoveCardsToLocation(scn.GetLSStartingLocation(),scn.GetLSCard(target));scn.MoveCardsToLocation(scn.GetLSCard("chamber"),scn.GetLSCard("obi"));
        return scn;
    }
    private VirtualTableScenario relocation(String target) {
        var scn=relocationSetup(target);scn.SkipToLSTurn(Phase.ACTIVATE);darkActivateWindow(scn);return scn;
    }
    private void finishOpponentActivation(VirtualTableScenario scn) {
        for(int i=0;i<10 && scn.gameState().getCurrentPhase()==Phase.ACTIVATE;i++) {
            if(scn.DSGetDecision()!=null)scn.DSPass();
            else if(scn.LSDecisionAvailable("You have not activated Force"))scn.LSChooseYes();
            else if(scn.LSGetDecision()!=null)scn.LSPass();
        }
        assertEquals(Phase.CONTROL,scn.gameState().getCurrentPhase());
    }
    private void relocate(VirtualTableScenario scn,PhysicalCardImpl site) {
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),RELOCATE));scn.DSUseCardAction(scn.GetDSCard("aurra"),RELOCATE);if(scn.DSDecisionAvailable("Choose site with opponent"))scn.DSChooseCard(site);scn.PassAllResponses();assertEquals(site,scn.GetDSCard("aurra").getAtLocation());
    }
    @Test public void AurraSingActuallyRelocatesAcrossSystemsToOpponentsJediSiteForFree() {var scn=relocation("knight");int before=scn.GetDSForcePileCount();relocate(scn,scn.GetLSStartingLocation());assertEquals(before,scn.GetDSForcePileCount());}
    @Test public void AurraSingCannotRelocateTwiceInSameOpponentActivatePhase() {
        var scn=relocation("knight");relocate(scn,scn.GetLSStartingLocation());darkActivateWindow(scn);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),RELOCATE));
    }
    @Test public void AurraSingRelocationRenewsInNextOpponentActivatePhase() {
        var scn=relocation("knight");relocate(scn,scn.GetLSStartingLocation());finishOpponentActivation(scn);scn.SkipToLSTurn(Phase.ACTIVATE);darkActivateWindow(scn);relocate(scn,scn.GetLSCard("chamber"));
    }
    @Test public void AurraSingRelocationDoesNotOfferSiteWithOnlyNonJedi() {
        var scn=relocation("leia");scn.DSUseCardAction(scn.GetDSCard("aurra"),RELOCATE);
        if(scn.DSDecisionAvailable("Choose site with opponent")){assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSStartingLocation()));scn.DSChooseCard(scn.GetLSCard("chamber"));}
        scn.PassAllResponses();assertEquals(scn.GetLSCard("chamber"),scn.GetDSCard("aurra").getAtLocation());
    }
    @Test public void AurraSingRelocationExcludesSiteWithOnlyHerOwnersJedi() {
        var scn=scenario();scn.StartGame();scn.GetLSCard("knight").setOwner(scn.DS);scn.MoveCardsToLocation(scn.GetLSStartingLocation(),scn.GetLSCard("knight"));scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("aurra"));scn.SkipToLSTurn(Phase.ACTIVATE);darkActivateWindow(scn);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),RELOCATE));
    }
    @Test public void AurraSingCannotRelocateDuringHerOwnersActivatePhase() {
        var scn=relocationSetup("knight");assertTrue(scn.AwaitingDSActivatePhaseActions());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),RELOCATE));
    }
    @Test public void AurraSingCannotRelocateDuringOpponentsControlPhase() {
        var scn=relocation("knight");finishOpponentActivation(scn);scn.LSPass();assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),RELOCATE));
    }
    @Test public void AurraSingMayDeclineRelocationKeepingOriginalSite() {
        var scn=relocation("knight");assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),RELOCATE));scn.DSPass();assertEquals(scn.GetDSStartingLocation(),scn.GetDSCard("aurra").getAtLocation());
    }
    private void force(VirtualTableScenario scn,int amount) {
        scn.MoveCardsToTopOfDSReserveDeck(scn.gameState().getForcePile(scn.DS).toArray(PhysicalCardImpl[]::new));scn.EnsureDSForcePile(amount);
    }
    private VirtualTableScenario shot(String target,int amount,boolean weapon,boolean here) {
        var scn=scenario();scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("aurra"));
        scn.MoveCardsToLocation(here?scn.GetDSStartingLocation():scn.GetLSStartingLocation(),scn.GetLSCard(target));
        if(weapon)scn.AttachCardsTo(scn.GetDSCard("aurra"),scn.GetDSCard("blaster"));
        scn.SkipToDSTurn(Phase.CONTROL);force(scn,amount);scn.SkipToPhase(Phase.MOVE);return scn;
    }
    private void fire(VirtualTableScenario scn,int destiny) {
        int force=scn.GetDSForcePileCount();assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));scn.PrepareDSDestiny(destiny);scn.DSUseCardAction(scn.GetDSCard("aurra"),FIRE);
        for(int i=0;i<30 && !scn.AwaitingDSMovePhaseActions() && !scn.AwaitingLSMovePhaseActions();i++) {
            if(scn.DSDecisionAvailable("Choose Aurra's weapon"))scn.DSChooseCard(scn.GetDSCard("blaster"));
            else if(scn.DSDecisionAvailable("Choose target"))scn.DSChooseCard(scn.GetLSCard("obi"));
            else {
                var decision=scn.GetCurrentDecision();
                assertTrue("Expected a weapon response window: " + decision.getText(), decision instanceof CardActionSelectionDecision);
                if(scn.DSGetDecision()!=null)scn.DSPass();else scn.LSPass();
            }
        }
        assertTrue("Weapon firing did not resolve", scn.AwaitingDSMovePhaseActions() || scn.AwaitingLSMovePhaseActions());
        assertEquals(force-1,scn.GetDSForcePileCount());
    }
    @Test public void AurraSingMovePhaseShotPaysNormalWeaponCostAndLosesHitJedi() {
        var scn=shot("obi",1,true,true);fire(scn,7);assertEquals(0,scn.GetDSForcePileCount());
        assertInZone(Zone.LOST_PILE,scn.GetLSCard("obi"));
    }
    @Test public void AurraSingMovePhaseShotMissesAtEqualDefenseBoundary() {
        var scn=shot("obi",1,true,true);fire(scn,6);assertEquals(scn.GetDSStartingLocation(),scn.GetLSCard("obi").getAtLocation());assertFalse(scn.GetLSCard("obi").isHit());assertEquals(0,scn.GetDSForcePileCount());
    }
    @Test public void AurraSingCannotFireMovePhaseWeaponWithoutItsNormalForceCost() {var scn=shot("obi",0,true,true);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));}
    @Test public void AurraSingCannotFireMovePhaseWeaponAtNonJedi() {var scn=shot("leia",1,true,true);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));}
    @Test public void AurraSingCannotFireAtJediAtAnotherSite() {var scn=shot("obi",1,true,false);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));}
    @Test public void AurraSingCannotUseMovePhaseShotWithoutWeapon() {var scn=shot("obi",1,false,true);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));}
    @Test public void AurraSingMovePhaseShotIsOncePerGameEvenAfterNewTurn() {
        var scn=shot("obi",2,true,true);fire(scn,6);
        if(scn.AwaitingLSMovePhaseActions())scn.LSPass();
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertEquals(1,scn.GetDSForcePileCount());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));
        scn.SkipToDSTurn(Phase.MOVE);assertTrue(scn.AwaitingDSMovePhaseActions());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));
    }
    @Test public void AurraSingMovePhaseShotCannotBeUsedDuringHerControlPhase() {
        var scn=shot("obi",2,true,true);scn.SkipToDSTurn(Phase.CONTROL);assertTrue(scn.AwaitingDSControlPhaseActions());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));
    }
    @Test public void AurraSingMovePhaseShotCannotBeUsedDuringOpponentsMovePhase() {
        var scn=shot("obi",2,true,true);scn.SkipToLSTurn(Phase.MOVE);scn.LSPass();assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));
    }
    @Test public void AurraSingOrdinaryBattleShotDoesNotAutomaticallyLoseHitJedi() {
        var scn=shot("obi",2,true,true);scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation());scn.PrepareDSDestiny(7);
        scn.DSUseCardAction(scn.GetDSCard("blaster"),"Fire");scn.DSChooseCard(scn.GetLSCard("obi"));scn.PassForceUseResponses();scn.PassWeaponFireWithDestinyDraw();scn.PassAllResponses();assertTrue(scn.GetLSCard("obi").isHit());assertEquals(Zone.AT_LOCATION,scn.GetLSCard("obi").getZone());
    }
    private void attrition(int amount,boolean immune) {
        var scn=scenario();scn.StartGame();var site=scn.GetDSStartingLocation();scn.MoveCardsToLocation(site,scn.GetDSCard("aurra"),scn.GetLSCard("luke"));scn.SkipToDSTurn(Phase.BATTLE);scn.PrepareDSDestiny(6);scn.PrepareLSDestiny(amount);scn.DSInitiateBattle(site);scn.SkipToDamageSegment(true);scn.PassDamageSegmentActions();
        assertEquals(0,scn.GetUnpaidDSBattleDamage());assertEquals(amount,scn.GetUnpaidDSAttrition());
        if(immune){assertTrue(scn.DSDecisionAvailable("Choose a card from battle to forfeit (if desired)"));scn.DSPass();assertEquals(Zone.AT_LOCATION,scn.GetDSCard("aurra").getZone());}
        else{assertFalse(scn.DSDecisionAvailable("Choose a card from battle to forfeit (if desired)"));scn.DSChooseCard(scn.GetDSCard("aurra"));scn.PassAllResponses();assertInZone(Zone.LOST_PILE,scn.GetDSCard("aurra"));}
    }
    @Test public void AurraSingSurvivesActualAttritionBelowFour() {attrition(3,true);}
    @Test public void AurraSingMustForfeitAtActualAttritionEqualToFour() {attrition(4,false);}
    @Test public void AurraSingMustForfeitAtActualAttritionAboveFour() {attrition(5,false);}
}
