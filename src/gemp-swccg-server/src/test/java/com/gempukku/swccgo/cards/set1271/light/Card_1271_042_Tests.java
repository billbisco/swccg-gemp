package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_1271_042_Tests {
    private VirtualTableScenario scenario(boolean rebelBase) {
        return new VirtualTableScenario(new HashMap<>() {{
            put("foils","1271_42");put("red3","1_145");put("red1","1_144");put("biggs","1_3");put("luke","1_19");
            put("capital","221_058");put("navy","221_060");put("kilian","217_026");
            put("presence","1_146");put("lost","1_28");put("lost2","1_2");put("away","3_55");
        }},new HashMap<>() {{
            put("ground","12_176");put("vader","1_168");put("tie","1_306");put("alter","1_234");put("crossfire","13_63");put("deathStar","2_143");
            put("virago","10_54");
        }},40,40,StartingSetup.LSStartingLocation(rebelBase?"1_135":"6_87"),StartingSetup.DefaultDSSpaceSystem,
            StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    private VirtualTableScenario ready() {
        var s=scenario(true);s.StartGame();s.SkipToLSTurn(Phase.CONTROL);s.AttachCardsTo(s.GetLSStartingLocation(),s.GetLSCard("foils"));
        s.MoveLocationToTable(s.GetLSCard("away"));s.MoveCardsToTopOfLSLostPile(s.GetLSCard("lost"),s.GetLSCard("lost2"));s.EnsureLSForcePile(20);return s;
    }
    @Test public void SFoilsPrintedStatsIconsAndKeywordsAreCorrect() {
        var s=scenario(true);var b=s.GetLSCard("foils").getBlueprint();assertEquals("S-foils",b.getTitle());assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT,b.getSide());assertEquals(4,b.getDestiny(),s.epsilon);assertEquals(Uniqueness.UNIQUE,b.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1,b.getExpansionSet());assertEquals(Rarity.V,b.getRarity());
        s.BlueprintCardTypeCheck(b,List.of(CardType.EFFECT));s.BlueprintKeywordCheck(b,List.of(Keyword.DEPLOYS_ON_LOCATION));
        s.BlueprintIconCheck(b,List.of(Icon.EFFECT,Icon.SPECIAL_EDITION,Icon.LEGACY_BLOCK_7));
    }
    @Test public void SFoilsActuallyDeploysForFreeOnARebelBaseSystem() {
        var s=scenario(true);s.StartGame();s.SkipToLSTurn(Phase.CONTROL);s.MoveCardsToLSHand(s.GetLSCard("foils"));s.SkipToPhase(Phase.DEPLOY);int force=s.GetLSForcePileCount();
        s.LSDeployCard(s.GetLSCard("foils"));s.PassAllResponses();if(s.GetLSCard("foils").getAttachedTo()==null){s.LSChooseCard(s.GetLSStartingLocation());s.PassAllResponses();}
        assertEquals(s.GetLSStartingLocation(),s.GetLSCard("foils").getAttachedTo());assertEquals(force,s.GetLSForcePileCount());
    }
    @Test public void SFoilsCannotDeployOnANonRebelBaseSystem() {
        var s=scenario(false);s.StartGame();s.SkipToLSTurn(Phase.CONTROL);s.MoveCardsToLSHand(s.GetLSCard("foils"));s.SkipToPhase(Phase.DEPLOY);assertFalse(s.LSCardPlayAvailable(s.GetLSCard("foils")));
    }
    private boolean reachReward(VirtualTableScenario s) {
        for(int i=0;i<12;i++) {
            if(s.LSAnyActionsAvailable() && s.LSCardActionAvailable(s.GetLSCard("foils"),"Retrieve 1 Force"))return true;
            if(s.DSDecisionAvailable("optional response"))s.DSPass();
            else if(s.LSDecisionAvailable("optional response"))s.LSPass();
            else break;
        }
        return false;
    }
    private void deployPair(VirtualTableScenario s,String ship,String pilot,boolean away) {
        s.MoveCardsToLSHand(s.GetLSCard(ship),s.GetLSCard(pilot));
        if(s.GetCurrentPhase()!=Phase.DEPLOY)s.SkipToPhase(Phase.DEPLOY);
        if(s.AwaitingDSDeployPhaseActions())s.DSPass();
        s.LSDeployCard(s.GetLSCard(ship));
        var system=away?s.GetLSCard("away"):s.GetLSStartingLocation();
        for(int i=0;i<8;i++) {
            if(s.LSDecisionAvailable("Do you want to simultaneously"))s.LSChooseYes();
            else if(s.LSHasCardChoiceAvailable(s.GetLSCard(pilot)))s.LSChooseCard(s.GetLSCard(pilot));
            else if(s.LSDecisionAvailable("capacity slot"))s.LSDecided("0");
            else if(s.LSHasCardChoiceAvailable(system))s.LSChooseCard(system);
            else break;
        }
    }
    private void resolveReward(VirtualTableScenario s,boolean retrieve,boolean draw) {
        assertTrue(reachReward(s));s.LSUseCardAction(s.GetLSCard("foils"),"Retrieve 1 Force");
        assertTrue(s.LSDecisionAvailable("Retrieve 1 Force?"));if(retrieve)s.LSChooseYes();else s.LSChooseNo();
        if(retrieve && s.LSHasCardChoiceAvailable(s.GetLSCard("lost2")))s.LSChooseCard(s.GetLSCard("lost2"));s.PassAllResponses();
        assertTrue(s.LSDecisionAvailable("Draw top card"));if(draw)s.LSChooseYes();else s.LSChooseNo();s.PassAllResponses();
    }
    private void reward(boolean retrieve,boolean draw,boolean away) {
        var s=ready();s.MoveCardsToLSHand(s.GetLSCard("red3"),s.GetLSCard("biggs"));var top=s.GetLSDestiny(7);s.MoveCardsToTopOfLSReserveDeck(top);
        int lost=s.GetLSLostPileCount();deployPair(s,"red3","biggs",away);resolveReward(s,retrieve,draw);
        assertEquals(lost-(retrieve?1:0),s.GetLSLostPileCount());assertEquals(draw,top.getZone()==Zone.HAND);assertFalse(reachReward(s));
    }
    @Test public void SFoilsActuallyRetrievesOnlyAfterSimultaneousPilotAndRebelStarfighterDeployment() {reward(true,false,false);}
    @Test public void SFoilsActuallyDrawsOnlyAfterSimultaneousPilotAndRebelStarfighterDeployment() {reward(false,true,false);}
    @Test public void SFoilsActuallyAllowsBothRewardsAndDoesNotDuplicateForThePilotResult() {reward(true,true,false);}
    @Test public void SFoilsRewardsAQualifyingDeploymentAtAnotherSystemWithoutOccupyingItsOwnSystem() {reward(true,true,true);}
    @Test public void SFoilsMayDeclineItsDeploymentReward() {
        var s=ready();int lost=s.GetLSLostPileCount();deployPair(s,"red3","biggs",false);assertTrue(reachReward(s));s.PassAllResponses();assertEquals(lost,s.GetLSLostPileCount());assertFalse(reachReward(s));
    }
    @Test public void SFoilsDoesNotRewardAStarfighterAndPilotDeployedSeparately() {
        var s=ready();s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("red3"));
        s.MoveCardsToLSHand(s.GetLSCard("biggs"));s.SkipToPhase(Phase.DEPLOY);
        s.LSDeployCard(s.GetLSCard("biggs"));if(s.LSHasCardChoiceAvailable(s.GetLSCard("red3")))s.LSChooseCard(s.GetLSCard("red3"));
        assertFalse(reachReward(s));s.PassAllResponses();assertEquals(s.GetLSCard("red3"),s.GetLSCard("biggs").getAttachedTo());
    }
    @Test public void SFoilsDoesNotRewardAStarfighterWithOnlyAPermanentPilot() {
        var s=ready();s.MoveCardsToLSHand(s.GetLSCard("presence"));s.SkipToPhase(Phase.DEPLOY);
        s.LSDeployCard(s.GetLSCard("presence"));if(s.LSHasCardChoiceAvailable(s.GetLSStartingLocation()))s.LSChooseCard(s.GetLSStartingLocation());
        assertFalse(reachReward(s));s.PassAllResponses();assertEquals(Zone.AT_LOCATION,s.GetLSCard("presence").getZone());
    }
    @Test public void SFoilsDoesNotRewardASimultaneouslyDeployedCapitalStarship() {
        var s=ready();var navy=s.GetLSCard("navy");var capital=s.GetLSCard("capital");var pilot=s.GetLSCard("kilian");
        s.MoveCardsToLSSideOfTable(navy);s.MoveCardsToLSHand(capital);s.MoveCardsToTopOfLSReserveDeck(pilot);s.SkipToPhase(Phase.DEPLOY);
        s.LSUseCardAction(navy,"Reveal starship or pilot from hand");s.LSChooseCard(capital);s.PassAllResponses();
        s.LSChooseCard(pilot);s.PassAllResponses();
        if(s.LSHasCardChoiceAvailable(s.GetLSStartingLocation()))s.LSChooseCard(s.GetLSStartingLocation());
        if(s.LSDecisionAvailable("capacity slot"))s.LSDecided("0");
        assertFalse(reachReward(s));s.PassAllResponses();assertTrue(s.IsAboardAsPilot(capital,pilot));
    }
    @Test public void SFoilsCanRewardTwoDifferentQualifyingPairsInTheSameTurn() {
        var s=ready();s.MoveCardsToLSHand(s.GetLSCard("red1"),s.GetLSCard("luke"));int lost=s.GetLSLostPileCount();deployPair(s,"red3","biggs",false);resolveReward(s,true,false);
        deployPair(s,"red1","luke",true);resolveReward(s,true,false);assertEquals(lost-2,s.GetLSLostPileCount());
    }
    private void alter(boolean occupy) {
        var s=ready();if(occupy)s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("presence"));
        s.MoveLocationToTable(s.GetDSCard("ground"));s.MoveCardsToLocation(s.GetDSCard("ground"),s.GetDSCard("vader"));s.MoveCardsToDSHand(s.GetDSCard("alter"));s.SkipToDSTurn(Phase.CONTROL);
        if(occupy){assertFalse(s.DSCardPlayAvailable(s.GetDSCard("alter")));return;}
        s.PrepareDSDestiny(0);s.DSPlayCard(s.GetDSCard("alter"));s.DSChooseCard(s.GetLSCard("foils"));s.DSChooseCard(s.GetDSCard("vader"));s.PassAllResponses();s.PassDestinyDrawResponses();s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("foils")));
    }
    @Test public void SFoilsCannotBeCanceledByAnActualAlterWhileItsSystemIsOccupied() {alter(true);}
    @Test public void SFoilsCanBeCanceledByAnActualAlterWhileItsSystemIsUnoccupied() {alter(false);}
    @Test public void SFoilsCrossfireImmunityRequiresOccupationOfItsAttachedSystem() {
        var s=ready();s.MoveCardsToSideOfTable(s.GetDSCard("crossfire"));s.SkipToPhase(Phase.DEPLOY);
        assertTrue(Filters.canBeTargetedBy(s.GetDSCard("crossfire")).accepts(s.game(),s.GetLSCard("foils")));
        s.MoveCardsToLocation(s.GetLSStartingLocation(),s.GetLSCard("presence"));s.SkipToPhase(Phase.BATTLE);
        assertFalse(Filters.canBeTargetedBy(s.GetDSCard("crossfire")).accepts(s.game(),s.GetLSCard("foils")));
    }
    @Test public void SFoilsCrossfireSuspendsTheRewardOnlyAtTheAffectedSystemUnlessTheEffectIsImmune() {
        crossfireAtDeployment(false, true, false);
        crossfireAtDeployment(false, false, true);
        crossfireAtDeployment(true, true, true);
    }

    private void crossfireAtDeployment(boolean occupyHome, boolean darkShipAtDeployment, boolean rewardAvailable) {
        var s = ready();
        s.MoveCardsToSideOfTable(s.GetDSCard("crossfire"));
        var darkShip = s.GetDSCard("virago");
        s.MoveCardsToLocation(darkShipAtDeployment ? s.GetLSCard("away") : s.GetDSStartingLocation(), darkShip);
        if (occupyHome) s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("presence"));
        deployPair(s, "red3", "biggs", true);
        assertEquals(rewardAvailable, reachReward(s));
        s.PassAllResponses();
        assertTrue(s.IsAboardAsPilot(s.GetLSCard("red3"), s.GetLSCard("biggs")));
    }

    @Test public void SFoilsIsSuspendedAndDoesNotRewardDeploymentIfDeathStarWasAlreadyBlownAway() {
        var s=scenario(true);s.StartGame();s.SkipToLSTurn(Phase.CONTROL);s.EnsureLSForcePile(20);
        var deathStar=s.GetDSCard("deathStar");s.MoveLocationToTable(deathStar);
        s.game().getModifiersQuerying().blownAway(deathStar);
        s.game().getGameState().flipBlownAwayLocations(s.game(),List.of(deathStar));
        s.MoveCardsToLSHand(s.GetLSCard("foils"),s.GetLSCard("red3"),s.GetLSCard("biggs"));s.SkipToPhase(Phase.DEPLOY);
        s.LSDeployCard(s.GetLSCard("foils"));s.PassAllResponses();if(s.GetLSCard("foils").getAttachedTo()==null){s.LSChooseCard(s.GetLSStartingLocation());s.PassAllResponses();}
        assertTrue(s.GetLSCard("foils").isSuspended());
        s.SkipToPhase(Phase.DRAW);s.SkipToLSTurn(Phase.CONTROL);s.EnsureLSForcePile(20);
        deployPair(s,"red3","biggs",false);assertFalse(reachReward(s));s.PassAllResponses();
        assertTrue(s.IsAboardAsPilot(s.GetLSCard("red3"),s.GetLSCard("biggs")));
        assertTrue(s.GetLSCard("foils").isSuspended());
    }
}
