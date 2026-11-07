package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1250_002_Tests {
    private static final String RELOCATE="Relocate to opponent's Jedi site",FIRE="Fire weapon at a Jedi";
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{put("luke","1_19");put("obi","1_21");put("leia","1_17");put("chamber","12_76");}},
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
    private VirtualTableScenario relocation(String target) {
        var scn=scenario();scn.StartGame();scn.MoveLocationToTable(scn.GetLSCard("chamber"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("aurra"));scn.MoveCardsToLocation(scn.GetLSStartingLocation(),scn.GetLSCard(target));scn.MoveCardsToLocation(scn.GetLSCard("chamber"),scn.GetLSCard("obi"));
        scn.SkipToLSTurn(Phase.ACTIVATE);darkActivateWindow(scn);return scn;
    }
    private void relocate(VirtualTableScenario scn,PhysicalCardImpl site) {
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),RELOCATE));scn.DSUseCardAction(scn.GetDSCard("aurra"),RELOCATE);scn.DSChooseCard(site);scn.PassAllResponses();assertEquals(site,scn.GetDSCard("aurra").getAtLocation());
    }
    @Test public void AurraSingActuallyRelocatesAcrossSystemsToOpponentsJediSiteForFree() {var scn=relocation("luke");int before=scn.GetDSForcePileCount();relocate(scn,scn.GetLSStartingLocation());assertEquals(before,scn.GetDSForcePileCount());}
    @Test public void AurraSingCannotRelocateTwiceInSameOpponentActivatePhase() {
        var scn=relocation("luke");relocate(scn,scn.GetLSStartingLocation());darkActivateWindow(scn);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),RELOCATE));
    }
    @Test public void AurraSingRelocationRenewsInNextOpponentActivatePhase() {
        var scn=relocation("luke");relocate(scn,scn.GetLSStartingLocation());scn.SkipToLSTurn(Phase.ACTIVATE);darkActivateWindow(scn);relocate(scn,scn.GetLSCard("chamber"));
    }
    @Test public void AurraSingRelocationDoesNotOfferSiteWithOnlyNonJedi() {
        var scn=relocation("leia");scn.DSUseCardAction(scn.GetDSCard("aurra"),RELOCATE);assertTrue(scn.DSHasCardChoiceAvailable(scn.GetLSCard("chamber")));assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSStartingLocation()));
    }
    @Test public void AurraSingRelocationExcludesSiteWithOnlyHerOwnersJedi() {
        var scn=scenario();scn.StartGame();scn.GetLSCard("luke").setOwner(scn.DS);scn.MoveCardsToLocation(scn.GetLSStartingLocation(),scn.GetLSCard("luke"));scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("aurra"));scn.SkipToLSTurn(Phase.ACTIVATE);darkActivateWindow(scn);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),RELOCATE));
    }
    @Test public void AurraSingCannotRelocateDuringHerOwnersActivatePhase() {
        var scn=relocation("luke");scn.SkipToDSTurn(Phase.ACTIVATE);assertTrue(scn.AwaitingDSActivatePhaseActions());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),RELOCATE));
    }
    @Test public void AurraSingCannotRelocateDuringOpponentsControlPhase() {
        var scn=relocation("luke");scn.SkipToPhase(Phase.CONTROL);scn.LSPass();assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),RELOCATE));
    }
    @Test public void AurraSingMayDeclineRelocationKeepingOriginalSite() {
        var scn=relocation("luke");assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),RELOCATE));scn.DSPass();assertEquals(scn.GetDSStartingLocation(),scn.GetDSCard("aurra").getAtLocation());
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
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));scn.PrepareDSDestiny(destiny);scn.DSUseCardAction(scn.GetDSCard("aurra"),FIRE);
        if(scn.DSDecisionAvailable("Choose Aurra's weapon"))scn.DSChooseCard(scn.GetDSCard("blaster"));
        scn.DSChooseCard(scn.GetLSCard("luke"));scn.PassForceUseResponses();scn.PassWeaponFireWithDestinyDraw();scn.PassAllResponses();
    }
    @Test public void AurraSingMovePhaseShotPaysNormalWeaponCostAndLosesHitJedi() {
        var scn=shot("luke",1,true,true);fire(scn,5);assertEquals(0,scn.GetDSForcePileCount());assertInZone(Zone.LOST_PILE,scn.GetLSCard("luke"));
    }
    @Test public void AurraSingMovePhaseShotMissesAtEqualDefenseBoundary() {
        var scn=shot("luke",1,true,true);fire(scn,4);assertEquals(scn.GetDSStartingLocation(),scn.GetLSCard("luke").getAtLocation());assertFalse(scn.GetLSCard("luke").isHit());assertEquals(0,scn.GetDSForcePileCount());
    }
    @Test public void AurraSingCannotFireMovePhaseWeaponWithoutItsNormalForceCost() {var scn=shot("luke",0,true,true);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));}
    @Test public void AurraSingCannotFireMovePhaseWeaponAtNonJedi() {var scn=shot("leia",1,true,true);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));}
    @Test public void AurraSingCannotFireAtJediAtAnotherSite() {var scn=shot("luke",1,true,false);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));}
    @Test public void AurraSingCannotUseMovePhaseShotWithoutWeapon() {var scn=shot("luke",1,false,true);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));}
    @Test public void AurraSingMovePhaseShotIsOncePerGameEvenAfterNewTurn() {
        var scn=shot("luke",2,true,true);fire(scn,4);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));scn.SkipToDSTurn(Phase.MOVE);assertTrue(scn.AwaitingDSMovePhaseActions());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));
    }
    @Test public void AurraSingMovePhaseShotCannotBeUsedDuringHerControlPhase() {
        var scn=shot("luke",2,true,true);scn.SkipToDSTurn(Phase.CONTROL);assertTrue(scn.AwaitingDSControlPhaseActions());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));
    }
    @Test public void AurraSingMovePhaseShotCannotBeUsedDuringOpponentsMovePhase() {
        var scn=shot("luke",2,true,true);scn.SkipToLSTurn(Phase.MOVE);scn.LSPass();assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("aurra"),FIRE));
    }
    @Test public void AurraSingOrdinaryBattleShotDoesNotAutomaticallyLoseHitJedi() {
        var scn=shot("luke",2,true,true);scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation());scn.PrepareDSDestiny(5);
        scn.DSUseCardAction(scn.GetDSCard("blaster"),"Fire");scn.DSChooseCard(scn.GetLSCard("luke"));scn.PassForceUseResponses();scn.PassWeaponFireWithDestinyDraw();scn.PassAllResponses();assertTrue(scn.GetLSCard("luke").isHit());assertEquals(Zone.AT_LOCATION,scn.GetLSCard("luke").getZone());
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
