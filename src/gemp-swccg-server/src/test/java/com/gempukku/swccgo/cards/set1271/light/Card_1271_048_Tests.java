package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_1271_048_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("cannon","1271_48");put("powerSite","3_61");put("unpoweredSite","3_62");put("farm","1_132");put("interior","1_139");
            put("ship","1_140");put("luke","1_19");
        }},new HashMap<>() {{
            put("target","1_301");put("otherCapital","1_302");put("executor","4_167");put("fighter","1_304");put("weapon","1_318");
        }},40,40,StartingSetup.LSStartingLocation("3_55"),StartingSetup.DefaultDSSpaceSystem,
            StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    @Test public void PlanetDefenderIonCannonPrintedStatsIconsAndKeywordsAreCorrect() {
        var s=scenario();var b=s.GetLSCard("cannon").getBlueprint();
        assertEquals("Planet Defender Ion Cannon",b.getTitle());assertTrue(b.hasVirtualSuffix());assertEquals(Side.LIGHT,b.getSide());
        assertEquals(4,b.getDestiny(),s.epsilon);assertEquals(4,b.getDeployCost(),s.epsilon);assertEquals(4,b.getForfeit(),s.epsilon);
        assertEquals(Uniqueness.UNIQUE,b.getUniqueness());assertEquals(CardSubtype.ARTILLERY,b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1,b.getExpansionSet());assertEquals(Rarity.V,b.getRarity());
        s.BlueprintCardTypeCheck(b,List.of(CardType.WEAPON));s.BlueprintKeywordCheck(b,List.of(Keyword.ION_CANNON));
        s.BlueprintIconCheck(b,List.of(Icon.WEAPON,Icon.HOTH,Icon.LEGACY_BLOCK_7));
    }
    @Test public void PlanetDefenderIonCannonActuallyDeploysForFourAtANonRebelBaseExteriorSite() {
        var s=scenario();s.StartGame();s.SkipToLSTurn(Phase.CONTROL);s.MoveLocationToTable(s.GetLSCard("farm"));s.MoveCardsToLSHand(s.GetLSCard("cannon"));
        s.EnsureLSForcePile(10);s.SkipToPhase(Phase.DEPLOY);int before=s.GetLSForcePileCount();s.LSDeployCard(s.GetLSCard("cannon"));s.PassAllResponses();
        if(s.GetLSCard("cannon").getAttachedTo()==null){s.LSChooseCard(s.GetLSCard("farm"));s.PassAllResponses();}
        assertEquals(s.GetLSCard("farm"),s.GetLSCard("cannon").getAttachedTo());assertEquals(before-4,s.GetLSForcePileCount());
    }
    @Test public void PlanetDefenderIonCannonCannotDeployAtAnInteriorOnlySite() {
        var s=scenario();s.StartGame();s.SkipToLSTurn(Phase.CONTROL);s.MoveLocationToTable(s.GetLSCard("interior"));s.MoveCardsToLSHand(s.GetLSCard("cannon"));s.SkipToPhase(Phase.DEPLOY);
        assertFalse(s.LSCardPlayAvailable(s.GetLSCard("cannon")));
    }
    private VirtualTableScenario ready(boolean atRelatedSystem) { return ready(atRelatedSystem,true,false); }
    private VirtualTableScenario ready(boolean atRelatedSystem,boolean powered,boolean extraTargets) {
        var s=scenario();s.StartGame();s.SkipToLSTurn(Phase.CONTROL);var site=s.GetLSCard(powered?"powerSite":"unpoweredSite");s.MoveLocationToTable(site);s.AttachCardsTo(site,s.GetLSCard("cannon"));
        var system=atRelatedSystem?s.GetLSStartingLocation():s.GetDSStartingLocation();
        s.MoveCardsToLocation(system,s.GetLSCard("ship"),s.GetDSCard("target"));s.BoardAsPilot(s.GetLSCard("ship"),s.GetLSCard("luke"));
        s.AttachCardsTo(s.GetDSCard("target"),s.GetDSCard("weapon"));
        if(extraTargets)s.MoveCardsToLocation(system,s.GetDSCard("executor"),s.GetDSCard("fighter"));s.SkipToPhase(Phase.BATTLE);s.LSInitiateBattle(system);s.PassAllResponses();return s;
    }
    private void shoot(VirtualTableScenario s,int first,int second) {
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSDestiny(first),s.GetLSDestiny(second));
        s.LSUseCardAction(s.GetLSCard("cannon"),"Fire");s.LSChooseCard(s.GetDSCard("target"));s.PassWeaponFireWithDestinyDraw(2);s.PassAllResponses();
    }
    @Test public void PlanetDefenderIonCannonActuallyFiresForFreeWithoutACharacterAtItsSite() {
        var s=ready(true);s.DrainForcePileTo(s.LS,0);shoot(s,3,4);assertEquals(0,s.GetLSForcePileCount());
        assertEquals(0,s.GetPower(s.GetDSCard("target")));assertEquals(0,s.GetHyperspeed(s.GetDSCard("target")));
    }
    @Test public void PlanetDefenderIonCannonUsesTwoDestinyAndDoesNotDestroyWeaponsOrReduceArmor() {
        var s=ready(true);shoot(s,3,4);assertEquals(0,s.GetPower(s.GetDSCard("target")));assertEquals(0,s.GetHyperspeed(s.GetDSCard("target")));
        assertEquals(6,s.GetArmor(s.GetDSCard("target")));assertEquals(s.GetDSCard("target"),s.GetDSCard("weapon").getAttachedTo());
    }
    @Test public void PlanetDefenderIonCannonDoesNotSucceedAtEqualDefenseValue() {
        var s=ready(true);shoot(s,2,4);assertEquals(9,s.GetPower(s.GetDSCard("target")));assertEquals(3,s.GetHyperspeed(s.GetDSCard("target")));
    }
    @Test public void PlanetDefenderIonCannonDoesNotSucceedBelowDefenseValue() {
        var s=ready(true);shoot(s,1,4);assertEquals(9,s.GetPower(s.GetDSCard("target")));assertEquals(3,s.GetHyperspeed(s.GetDSCard("target")));
    }
    @Test public void PlanetDefenderIonCannonCannotFireIntoAnUnrelatedSystemBattle() {
        var s=ready(false);assertFalse(s.LSCardActionAvailable(s.GetLSCard("cannon"),"Fire"));
    }
    @Test public void PlanetDefenderIonCannonExcludesExecutorAndStarfightersFromActualTargetChoices() {
        var s=ready(true,true,true);s.LSUseCardAction(s.GetLSCard("cannon"),"Fire");
        assertTrue(s.LSHasCardChoiceAvailable(s.GetDSCard("target")));assertFalse(s.LSHasCardChoiceAvailable(s.GetDSCard("executor")));assertFalse(s.LSHasCardChoiceAvailable(s.GetDSCard("fighter")));
        s.LSChooseCard(s.GetDSCard("target"));s.PassWeaponFireWithDestinyDraw(2);s.PassAllResponses();
    }
    @Test public void PlanetDefenderIonCannonCannotFireWithoutTheNormalArtilleryPowerSource() {
        var s=ready(true,false,false);assertFalse(s.LSCardActionAvailable(s.GetLSCard("cannon"),"Fire"));
    }
    private void forfeitTarget(VirtualTableScenario s) { forfeit(s,"target"); }
    private void forfeit(VirtualTableScenario s,String name) {
        s.PrepareLSDestiny(5);s.PrepareDSDestiny(0);s.SkipToDamageSegment(true);s.PassDamageSegmentActions();assertTrue(s.AwaitingDSBattleDamagePayment());
        s.DSChooseCard(s.GetDSCard(name));
        if(s.DSDecisionAvailable("Do you still want to forfeit it?"))s.DSChooseYes();
        // Pass only earlier loss windows; leave the forfeiture trigger available for the cannon's choice.
        s.PassResponses("ABOUT_TO_LOSE_CARD_FROM_TABLE");s.PassResponses("ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE");
        // The current player chooses competing pile placement order (AR2 p.11), including opponent's cards.
        for(int i=0;i<4;i++) {
            boolean light=s.LSDecisionAvailable("put on Lost Pile") || s.LSDecisionAvailable("Choose card to be lost");
            boolean dark=s.DSDecisionAvailable("put on Lost Pile") || s.DSDecisionAvailable("Choose card to be lost");
            if(light)s.LSChooseCard(s.LSHasCardChoiceAvailable(s.GetDSCard("weapon"))?s.GetDSCard("weapon"):s.GetDSCard(name));
            else if(dark)s.DSChooseCard(s.DSHasCardChoiceAvailable(s.GetDSCard("weapon"))?s.GetDSCard("weapon"):s.GetDSCard(name));
            else break;
            s.PassResponses("ABOUT_TO_LOSE_CARD_FROM_TABLE");
        }
        assertTrue("Temporary forfeiture diagnostic: zone="+s.GetDSCard(name).getZone()+" LS="+(s.LSGetDecision()==null?null:s.LSGetDecision().getText())+" DS="+(s.DSGetDecision()==null?null:s.DSGetDecision().getText()),s.GetDSLostPile().contains(s.GetDSCard(name)));
    }
    @Test public void PlanetDefenderIonCannonActuallyTradesItselfOutOfPlayForTheForfeitedTargetInHand() {
        var s=ready(true);shoot(s,3,4);forfeitTarget(s);assertTrue(s.LSCardActionAvailable(s.GetLSCard("cannon"),"Steal"));
        s.LSUseCardAction(s.GetLSCard("cannon"),"Steal");s.PassAllResponses();
        if(s.LSHasCardChoiceAvailable(s.GetDSCard("target"))){s.LSChooseCard(s.GetDSCard("target"));s.PassAllResponses();}
        assertEquals(Zone.OUT_OF_PLAY,s.GetLSCard("cannon").getZone());assertEquals(Zone.HAND,s.GetDSCard("target").getZone());assertEquals(s.LS,s.GetDSCard("target").getOwner());
    }
    @Test public void PlanetDefenderIonCannonMayDeclineTheOptionalTheftAndKeepTheWeapon() {
        var s=ready(true);shoot(s,3,4);forfeitTarget(s);assertTrue(s.LSCardActionAvailable(s.GetLSCard("cannon"),"Steal"));s.PassAllResponses();
        assertEquals(s.GetLSCard("powerSite"),s.GetLSCard("cannon").getAttachedTo());assertTrue(s.GetDSLostPile().contains(s.GetDSCard("target")));
    }
    @Test public void PlanetDefenderIonCannonDoesNotOfferTheftAfterAnUnsuccessfulShot() {
        var s=ready(true);shoot(s,2,4);forfeitTarget(s);assertFalse(s.LSAnyActionsAvailable() && s.LSCardActionAvailable(s.GetLSCard("cannon"),"Steal"));s.PassAllResponses();
        assertEquals(s.GetLSCard("powerSite"),s.GetLSCard("cannon").getAttachedTo());assertTrue(s.GetDSLostPile().contains(s.GetDSCard("target")));
    }
    @Test public void PlanetDefenderIonCannonDoesNotStealADifferentForfeitedCapital() {
        var s=ready(true);s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetDSCard("otherCapital"));shoot(s,3,4);forfeit(s,"otherCapital");
        assertFalse(s.LSAnyActionsAvailable() && s.LSCardActionAvailable(s.GetLSCard("cannon"),"Steal"));s.PassAllResponses();
        assertTrue(s.GetDSLostPile().contains(s.GetDSCard("otherCapital")));assertEquals(Zone.AT_LOCATION,s.GetDSCard("target").getZone());
    }
    @Test public void PlanetDefenderIonCannonTheftPermissionExpiresAtTheEndOfThatBattle() {
        var s=ready(true);shoot(s,3,4);s.SkipToDamageSegment(false);s.PassDamageSegmentActions();s.DSPayRemainingBattleDamageFromReserveDeck();s.PassAllResponses();
        s.SkipToPhase(Phase.DRAW);s.SkipToLSTurn(Phase.BATTLE);s.LSInitiateBattle(s.GetLSStartingLocation());s.PassAllResponses();forfeitTarget(s);
        assertFalse(s.LSAnyActionsAvailable() && s.LSCardActionAvailable(s.GetLSCard("cannon"),"Steal"));s.PassAllResponses();
        assertTrue(s.GetDSLostPile().contains(s.GetDSCard("target")));assertEquals(s.GetLSCard("powerSite"),s.GetLSCard("cannon").getAttachedTo());
    }

}
