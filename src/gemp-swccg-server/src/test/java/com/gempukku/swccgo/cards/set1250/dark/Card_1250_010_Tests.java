package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1250_010_Tests {
    private static final String REDRAW = "Take destiny into hand and redraw";
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{put("luke","1_19");put("leia","1_17");put("brisky","11_16");}},
                new HashMap<>() {{put("sebulba","1250_10");put("vader","1_168");put("veers","3_87");put("trooper","1_194");put("device","1_201");put("walker","3_154");put("cannon","3_158");put("fighter","1_300");put("system","1_282");put("arena","11_94");}},
                15,15,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    @Test public void SebulbaStatsIconsAndKeywordsMatchPrintedImage() {
        var scn=scenario();var b=scn.GetDSCard("sebulba").getBlueprint();
        assertEquals("Sebulba",b.getTitle());assertTrue(b.hasVirtualSuffix());assertTrue(b.isLegacy());
        assertEquals(Side.DARK,b.getSide());assertEquals(Uniqueness.UNIQUE,b.getUniqueness());
        assertEquals(2,b.getDestiny(),scn.epsilon);assertEquals(3,b.getDeployCost(),scn.epsilon);
        assertEquals(3,b.getPower(),scn.epsilon);assertEquals(2,b.getAbility(),scn.epsilon);assertEquals(5,b.getForfeit(),scn.epsilon);
        assertEquals(Species.DUG,b.getSpecies());assertEquals(ExpansionSet.VIRTUAL_BLOCK_5,b.getExpansionSet());assertEquals(Rarity.V,b.getRarity());
        assertEquals("Bad tempered Dug from Pixelito. He was about to turn Jar Jar into orange goo, until Anakin intervened.",b.getLore());
        scn.BlueprintCardTypeCheck(b,new ArrayList<>() {{add(CardType.ALIEN);}});
        scn.BlueprintIconCheck(b,new ArrayList<>() {{add(Icon.ALIEN);add(Icon.PILOT);add(Icon.WARRIOR);add(Icon.TATOOINE);add(Icon.EPISODE_I);add(Icon.LEGACY_BLOCK_5);}});
        scn.BlueprintKeywordCheck(b,new ArrayList<Keyword>());
    }
    @Test public void SebulbaAddsTwoToPilotedCombatVehicleAndLosesBonusWhenHeLeaves() {
        var scn=scenario();scn.StartGame();var seb=scn.GetDSCard("sebulba");var walker=scn.GetDSCard("walker");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),walker);scn.MoveCardsToDSHand(seb);scn.BoardAsPilot(walker,seb);scn.SkipToDSTurn(Phase.CONTROL);
        assertEquals(9,scn.GetPower(walker));scn.MoveCardsToDSHand(seb);assertEquals(0,scn.GetPower(walker));
    }
    @Test public void SebulbaDoesNotAddPowerAsCombatVehiclePassenger() {
        var scn=scenario();scn.StartGame();var seb=scn.GetDSCard("sebulba");var walker=scn.GetDSCard("walker");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),walker);scn.MoveCardsToDSHand(seb,scn.GetDSCard("veers"));scn.BoardAsPilot(walker,scn.GetDSCard("veers"));scn.BoardAsPassenger(walker,seb);scn.SkipToDSTurn(Phase.CONTROL);
        assertEquals(10,scn.GetPower(walker));
    }
    @Test public void SebulbaDoesNotAddPowerToPilotedStarfighter() {
        var scn=scenario();scn.StartGame();scn.MoveLocationToTable(scn.GetDSCard("system"));var fighter=scn.GetDSCard("fighter");
        scn.MoveCardsToLocation(scn.GetDSCard("system"),fighter);scn.MoveCardsToDSHand(scn.GetDSCard("sebulba"));scn.BoardAsPilot(fighter,scn.GetDSCard("sebulba"));scn.SkipToDSTurn(Phase.CONTROL);
        assertEquals(1,scn.GetPower(fighter));
    }
    private void race(VirtualTableScenario scn,boolean dark,int value,boolean marked) {
        scn.MoveLocationToTable(scn.GetDSCard("arena"));var card=dark?scn.GetDSDestiny(value):scn.GetLSDestiny(value);
        scn.StackCardsOn(scn.GetDSCard("arena"),card);if(marked)card.setRaceDestinyForPlayer(dark?scn.DS:scn.LS);
        assertEquals(marked?value:0,scn.game().getModifiersQuerying().getHighestRaceTotal(scn.gameState(),dark?scn.DS:scn.LS),scn.epsilon);
    }
    private VirtualTableScenario battle(int total,boolean darkRace,boolean marked,boolean present,boolean elsewhere,String companion) {
        var scn=scenario();scn.StartGame();var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,scn.GetDSCard("veers"),scn.GetLSCard("luke"));
        if(present)scn.MoveCardsToLocation(elsewhere?scn.GetLSStartingLocation():site,scn.GetDSCard("sebulba"));
        if(companion!=null)scn.MoveCardsToLocation(site,scn.GetDSCard(companion));
        if(total>=0)race(scn,darkRace,total,marked);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);scn.PrepareDSDestiny(6);scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("device"));
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("leia"));return scn;
    }
    private com.gempukku.swccgo.logic.effects.DrawDestinyEffect reachDraw(VirtualTableScenario scn,boolean dark) {
        for(int i=0;i<60;i++) {
            var state=scn.game().getGameState().getTopDrawDestinyState();
            if(state!=null && state.getDrawDestinyEffect().getDrawnDestinyCard()!=null
                    && state.getDrawDestinyEffect().getPlayerDrawingDestiny().equals(dark?scn.DS:scn.LS)
                    && (scn.DSDecisionAvailable("DESTINY_DRAWN")||scn.DSDecisionAvailable("Just drew")))return state.getDrawDestinyEffect();
            if(scn.DSDecisionAvailable("battle destiny?"))scn.DSChooseYes();else if(scn.LSDecisionAvailable("battle destiny?"))scn.LSChooseYes();
            else if(scn.DSGetDecision()!=null)scn.DSPass();else if(scn.LSGetDecision()!=null)scn.LSPass();else fail("Sebulba requires a live destiny response");
        }
        fail("Sebulba did not reach the real destiny draw");return null;
    }
    private void redraw(VirtualTableScenario scn) {
        var draw=reachDraw(scn,true);assertEquals(scn.GetDSCard("device"),draw.getDrawnDestinyCard());
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("sebulba"),REDRAW));scn.DSUseCardAction(scn.GetDSCard("sebulba"),REDRAW);scn.PassAllResponses();
        assertEquals(Zone.HAND,scn.GetDSCard("device").getZone());scn.PassDestinyDrawResponses();scn.PassAllResponses();
        assertEquals(6,scn.GetDSTotalDestiny());assertEquals(1,scn.GetDSBattleDestinyCount());
    }
    @Test public void SebulbaTakesNonCharacterDestinyAndActuallyRedrawsWithOwnRaceTotalOne() {redraw(battle(1,true,true,true,false,null));}
    @Test public void SebulbaUsesOpponentsRaceTotalOneEvenWhenOwnTotalZero() {redraw(battle(1,false,true,true,false,null));}
    @Test public void SebulbaMayRedrawWhenElsewhereAndNotParticipatingInBattle() {redraw(battle(1,true,true,true,true,"trooper"));}
    @Test public void SebulbaCannotRedrawWhenBothPodraceTotalsAreZero() {
        var scn=battle(0,true,true,true,false,null);reachDraw(scn,true);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("sebulba"),REDRAW));
    }
    @Test public void SebulbaCannotRedrawWithoutAnyPodraceTotal() {
        var scn=battle(-1,true,true,true,false,null);reachDraw(scn,true);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("sebulba"),REDRAW));
    }
    @Test public void SebulbaDoesNotCountOrdinaryCardsStackedOnArenaAsRaceDestinies() {
        var scn=battle(1,true,false,true,false,null);reachDraw(scn,true);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("sebulba"),REDRAW));
    }
    @Test public void SebulbaAbsentDoesNotOfferRedrawDespitePositiveRaceTotal() {
        var scn=battle(1,true,true,false,false,"trooper");reachDraw(scn,true);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("sebulba"),REDRAW));
    }
    @Test public void SebulbaCannotTakeOpponentsJustDrawnDestiny() {
        var scn=battle(1,true,true,true,false,null);reachDraw(scn,false);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("sebulba"),REDRAW));
    }
    @Test public void SebulbaMayDeclineRedrawAndKeepActualOriginalDestiny() {
        var scn=battle(1,true,true,true,false,null);var draw=reachDraw(scn,true);assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("sebulba"),REDRAW));
        scn.DSPass();scn.PassAllResponses();assertFalse(draw.isDestinyCanceled());assertInZone(Zone.USED_PILE,scn.GetDSCard("device"));assertEquals(3,scn.GetDSTotalDestiny());
    }
    @Test public void SebulbaCannotRedrawTheReplacementAgainInSameTurn() {
        var scn=battle(1,true,true,true,false,null);reachDraw(scn,true);scn.DSUseCardAction(scn.GetDSCard("sebulba"),REDRAW);
        var replacement=reachDraw(scn,true);assertEquals(6,replacement.getDrawnDestinyCard().getBlueprint().getDestiny(),scn.epsilon);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("sebulba"),REDRAW));assertEquals(Zone.HAND,scn.GetDSCard("device").getZone());
    }
    @Test public void SebulbaGrantsOneActualBattleDestinyWhenOtherwiseUnable() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("sebulba"),scn.GetLSCard("luke"));
        scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation());scn.PrepareDSDestiny(4);scn.SkipToEndOfPowerSegment(true);assertEquals(1,scn.GetDSBattleDestinyCount());assertEquals(4,scn.GetDSTotalDestiny());
    }
    @Test public void SebulbaDoesNotAddAnotherBattleDestinyWhenAlreadyAble() {
        var scn=battle(-1,true,true,true,false,null);scn.SkipToEndOfPowerSegment(true);assertEquals(1,scn.GetDSBattleDestinyCount());assertEquals(3,scn.GetDSTotalDestiny());
    }
    private void resolveBrisky(VirtualTableScenario scn) {
        for(int i=0;i<10;i++) {
            scn.PassAllResponses();
            if(scn.DSDecisionAvailable("Required responses"))scn.DSChooseAction("Cancel Brisky Morning Munchen");
            else if(scn.DSDecisionAvailable("Target card to cancel"))scn.DSChooseCard(scn.GetLSCard("brisky"));
            else return;
        }
        fail("Sebulba cancellation did not resolve");
    }
    @Test public void SebulbaActuallyCancelsExistingBriskyWhenDeployedAtUnrelatedSite() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToLSSideOfTable(scn.GetLSCard("brisky"));scn.MoveCardsToDSHand(scn.GetDSCard("sebulba"));
        scn.SkipToDSTurn(Phase.DEPLOY);scn.DSDeployCard(scn.GetDSCard("sebulba"));scn.DSChooseCard(scn.GetDSStartingLocation());resolveBrisky(scn);
        assertInZone(Zone.LOST_PILE,scn.GetLSCard("brisky"));assertEquals(scn.GetDSStartingLocation(),scn.GetDSCard("sebulba").getAtLocation());
    }
    @Test public void SebulbaActuallyCancelsNewBriskyBeforeItDeploys() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("sebulba"));scn.MoveCardsToLSHand(scn.GetLSCard("brisky"));
        scn.SkipToLSTurn(Phase.DEPLOY);scn.LSDeployCard(scn.GetLSCard("brisky"));resolveBrisky(scn);assertInZone(Zone.LOST_PILE,scn.GetLSCard("brisky"));
    }
    @Test public void SebulbaAbsentAllowsActualBriskyDeployment() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToLSHand(scn.GetLSCard("brisky"));scn.SkipToLSTurn(Phase.DEPLOY);scn.LSDeployCard(scn.GetLSCard("brisky"));scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE,scn.GetLSCard("brisky"));
    }
    @Test public void SebulbaCancelsBriskyRegardlessOfItsOwner() {
        var scn=scenario();scn.StartGame();scn.GetLSCard("brisky").setOwner(scn.DS);scn.MoveCardsToDSSideOfTable(scn.GetLSCard("brisky"));scn.MoveCardsToDSHand(scn.GetDSCard("sebulba"));
        scn.SkipToDSTurn(Phase.DEPLOY);scn.DSDeployCard(scn.GetDSCard("sebulba"));scn.DSChooseCard(scn.GetDSStartingLocation());resolveBrisky(scn);assertInZone(Zone.LOST_PILE,scn.GetLSCard("brisky"));
    }
    @Test public void SebulbaMayTakeAndRedrawActualWeaponDestinyOutsideHisBattle() {
        var scn=scenario();scn.StartGame();var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,scn.GetDSCard("walker"),scn.GetLSCard("luke"));scn.MoveCardsToLocation(scn.GetLSStartingLocation(),scn.GetDSCard("sebulba"));
        scn.MoveCardsToDSHand(scn.GetDSCard("veers"));scn.BoardAsPilot(scn.GetDSCard("walker"),scn.GetDSCard("veers"));scn.AttachCardsTo(scn.GetDSCard("walker"),scn.GetDSCard("cannon"));race(scn,false,1,true);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);scn.PrepareDSDestiny(6);scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("device"));
        scn.DSUseCardAction(scn.GetDSCard("cannon"),"Fire");scn.DSChooseCard(scn.GetLSCard("luke"));scn.PassForceUseResponses();scn.PassResponses("Fire ");
        var draw=reachDraw(scn,true);assertEquals(DestinyType.WEAPON_DESTINY,draw.getDestinyType());assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("sebulba"),REDRAW));
        scn.DSUseCardAction(scn.GetDSCard("sebulba"),REDRAW);scn.PassAllResponses();scn.PassDestinyDrawResponses();scn.PassAllResponses();scn.PassResponses("ABOUT_TO_BE_HIT");
        assertEquals(Zone.HAND,scn.GetDSCard("device").getZone());assertTrue(scn.GetLSCard("luke").isHit());
    }
    @Test public void SebulbaMayRedrawAgainInANewTurnAfterSurvivingPreviousBattleElsewhere() {
        var scn=battle(1,true,true,true,true,"trooper");redraw(scn);
        for(int i=0;i<80 && scn.gameState().getBattleState()!=null;i++) {
            if(scn.LSDecisionAvailable("battle destiny?"))scn.LSChooseNo();
            else if(scn.DSDecisionAvailable("battle destiny?"))scn.DSChooseNo();
            else if(scn.AwaitingDSBattleDamagePayment())scn.DSPayRemainingBattleDamageFromReserveDeck();
            else if(scn.AwaitingLSBattleDamagePayment())scn.LSPayRemainingBattleDamageFromReserveDeck();
            else if(scn.DSHasCardChoiceAvailable(scn.GetDSCard("trooper")))scn.DSChooseCard(scn.GetDSCard("trooper"));
            else if(scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke")))scn.LSChooseCard(scn.GetLSCard("luke"));
            else if(scn.DSGetDecision()!=null)scn.DSPass();else if(scn.LSGetDecision()!=null)scn.LSPass();
        }
        assertNull(scn.gameState().getBattleState());assertEquals(Zone.AT_LOCATION,scn.GetDSCard("sebulba").getZone());
        scn.SkipToDSTurn(Phase.CONTROL);scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetLSCard("luke"),scn.GetDSCard("veers"),scn.GetDSCard("trooper"));
        scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation());scn.PrepareDSDestiny(6);scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("device"));redraw(scn);
    }
}
