package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1250_023_Tests {
    private static final String CANCEL="Stack clone or Coruscant guard to cancel Force drain";
    private VirtualTableScenario scenario() {
        // No equivalent Decipher Dark clone exists; use the already released Legacy clone printing.
        return new VirtualTableScenario(new HashMap<>() {{put("luke","1_19");put("leia","1_17");put("guard","12_3");}},
                new HashMap<>() {{put("reserves","1250_23");put("guard","12_100");put("guard2","12_100");put("clone","1250_6");put("veers","3_87");put("trooper","1_194");put("blaster","1_317");put("device","1_201");put("alter","1_234");}},
                12,12,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    @Test public void StrategicReservesStatsIconsAndKeywordsMatchPublishedSheetAndSourcePack() {
        var scn=scenario();var b=scn.GetDSCard("reserves").getBlueprint();
        assertEquals("Strategic Reserves",b.getTitle());assertTrue(b.hasVirtualSuffix());assertTrue(b.isLegacy());
        assertEquals(Side.DARK,b.getSide());assertEquals(4,b.getDestiny(),scn.epsilon);assertEquals(Uniqueness.UNIQUE,b.getUniqueness());
        assertEquals(CardSubtype.NORMAL,b.getCardSubtype());assertEquals(ExpansionSet.VIRTUAL_BLOCK_5,b.getExpansionSet());assertEquals(Rarity.V,b.getRarity());
        assertEquals("The Empire is able to quickly relocate its manpower, deploying its troops as efficiently as possible.",b.getLore());
        scn.BlueprintCardTypeCheck(b,new ArrayList<>() {{add(CardType.EFFECT);}});
        scn.BlueprintIconCheck(b,new ArrayList<>() {{add(Icon.EFFECT);add(Icon.SPECIAL_EDITION);add(Icon.EPISODE_I);add(Icon.LEGACY_BLOCK_5);}});
        scn.BlueprintKeywordCheck(b,new ArrayList<Keyword>());
    }
    @Test public void StrategicReservesActuallyDeploysOnTableForFree() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToDSHand(scn.GetDSCard("reserves"));scn.SkipToDSTurn(Phase.DEPLOY);
        int before=scn.GetDSForcePileCount();scn.DSDeployCard(scn.GetDSCard("reserves"));scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE,scn.GetDSCard("reserves").getZone());assertEquals(before,scn.GetDSForcePileCount());
    }
    private VirtualTableScenario drain(boolean effect,boolean own) {
        var scn=scenario();scn.StartGame();
        if(effect)scn.MoveCardsToDSSideOfTable(scn.GetDSCard("reserves"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),own?scn.GetDSCard("veers"):scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(),scn.GetLSCard("leia"));
        scn.MoveCardsToDSHand(scn.GetDSCard("guard"),scn.GetDSCard("guard2"),scn.GetDSCard("clone"),scn.GetDSCard("device"));
        if(own)scn.SkipToDSTurn(Phase.CONTROL);else scn.SkipToLSTurn(Phase.CONTROL);
        return scn;
    }
    private void startDrain(VirtualTableScenario scn,boolean own) {
        if(own)scn.DSForceDrainAt(scn.GetDSStartingLocation());else scn.LSForceDrainAt(scn.GetDSStartingLocation());
        if(scn.LSDecisionAvailable("FORCE_DRAIN_INITIATED"))scn.LSPass();
        assertNotNull(scn.game().getGameState().getForceDrainState());assertNotNull(scn.DSGetDecision());
    }
    private void cancel(VirtualTableScenario scn,String troop,boolean own) {
        int life=scn.GetDSLifeForceRemaining(),lightLife=scn.GetLSLifeForceRemaining();var card=scn.GetDSCard(troop);
        startDrain(scn,own);assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("reserves"),CANCEL));
        scn.DSUseCardAction(scn.GetDSCard("reserves"),CANCEL);
        for(int i=0;i<12 && card.getZone()!=Zone.STACKED;i++) {
            if(scn.DSHasCardChoiceAvailable(card))scn.DSChooseCard(card);else scn.PassAllResponses();
        }
        scn.PassAllResponses();assertEquals(Zone.STACKED,card.getZone());assertEquals(scn.GetDSCard("reserves"),card.getStackedOn());
        assertEquals(life,scn.GetDSLifeForceRemaining());assertEquals(lightLife,scn.GetLSLifeForceRemaining());
    }
    @Test public void StrategicReservesCancelsActualOpponentDrainByStackingOriginalCoruscantGuard() {cancel(drain(true,false),"guard",false);}
    @Test public void StrategicReservesAlsoAcceptsAnActualCloneForDrainCancellation() {cancel(drain(true,false),"clone",false);}
    @Test public void StrategicReservesMayCancelItsOwnersForceDrain() {cancel(drain(true,true),"guard",true);}
    @Test public void StrategicReservesDoesNotChargeForceToCancelDrain() {
        var scn=drain(true,false);scn.MoveCardsToTopOfDSReserveDeck(scn.game().getGameState().getForcePile(scn.DS).toArray(new com.gempukku.swccgo.game.PhysicalCardImpl[0]));
        cancel(scn,"guard",false);assertEquals(0,scn.GetDSForcePileCount());
    }
    @Test public void StrategicReservesCannotStackAnUnrelatedHandCard() {
        var scn=drain(true,false);startDrain(scn,false);scn.DSUseCardAction(scn.GetDSCard("reserves"),CANCEL);
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("guard")));assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("clone")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("device")));
    }
    @Test public void StrategicReservesCannotCancelWithoutAnEligibleCardInHand() {
        var scn=drain(true,false);scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("guard"),scn.GetDSCard("guard2"),scn.GetDSCard("clone"));
        startDrain(scn,false);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("reserves"),CANCEL));
    }
    @Test public void StrategicReservesDeclineLeavesDrainPayableAndHandCardUnspent() {
        var scn=drain(true,false);int before=scn.GetDSLifeForceRemaining();startDrain(scn,false);
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("reserves"),CANCEL));scn.DSPass();scn.PassForceDrainStartResponses();
        scn.DSPayRemainingForceLossFromReserveDeck();scn.PassAllResponses();assertEquals(before-1,scn.GetDSLifeForceRemaining());assertEquals(Zone.HAND,scn.GetDSCard("guard").getZone());
    }
    @Test public void StrategicReservesCannotCancelSecondDrainInSameTurn() {
        var scn=drain(true,false);cancel(scn,"guard",false);
        if(scn.DSDecisionAvailable("control"))scn.DSPass();assertTrue(scn.AwaitingLSControlPhaseActions());
        scn.LSForceDrainAt(scn.GetLSStartingLocation());assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("reserves"),CANCEL));
    }
    @Test public void StrategicReservesCanCancelAgainInANewTurn() {
        var scn=drain(true,false);cancel(scn,"guard",false);scn.SkipToLSTurn(Phase.CONTROL);cancel(scn,"guard2",false);
    }
    @Test public void StrategicReservesCannotCancelWhenItIsNotOnTable() {
        var scn=drain(false,false);startDrain(scn,false);assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("reserves"),CANCEL));
    }
    @Test public void StrategicReservesActuallyDeploysThePaidStackedGuardAtNormalCost() {
        var scn=drain(true,false);cancel(scn,"guard",false);scn.SkipToDSTurn(Phase.DEPLOY);int force=scn.GetDSForcePileCount();var guard=scn.GetDSCard("guard");
        assertTrue(scn.DSDeployAvailable(guard));scn.DSDeployCard(guard);scn.DSChooseCard(scn.GetDSStartingLocation());scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION,guard.getZone());assertEquals(force-2,scn.GetDSForcePileCount());assertNull(guard.getStackedOn());
    }
    @Test public void StrategicReservesDeployGrantAlsoCoversOtherCharactersStackedHere() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToDSSideOfTable(scn.GetDSCard("reserves"));scn.StackCardsOn(scn.GetDSCard("reserves"),scn.GetDSCard("trooper"));
        scn.SkipToDSTurn(Phase.DEPLOY);int force=scn.GetDSForcePileCount();scn.DSDeployCard(scn.GetDSCard("trooper"));scn.DSChooseCard(scn.GetDSStartingLocation());scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION,scn.GetDSCard("trooper").getZone());assertEquals(force-1,scn.GetDSForcePileCount());
    }
    @Test public void StrategicReservesDoesNotGrantStackedWeaponDeployment() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToDSSideOfTable(scn.GetDSCard("reserves"));scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("trooper"));
        scn.StackCardsOn(scn.GetDSCard("reserves"),scn.GetDSCard("blaster"));scn.SkipToDSTurn(Phase.DEPLOY);assertNotNull(scn.DSGetDecision());assertFalse(scn.DSDeployAvailable(scn.GetDSCard("blaster")));
    }
    @Test public void StrategicReservesDepartureRemovesStackedCharacterDeploymentPermission() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToDSSideOfTable(scn.GetDSCard("reserves"));scn.StackCardsOn(scn.GetDSCard("reserves"),scn.GetDSCard("guard"));
        scn.MoveOutOfPlay(scn.GetDSCard("reserves"));scn.SkipToDSTurn(Phase.DEPLOY);assertNotNull(scn.DSGetDecision());assertFalse(scn.DSDeployAvailable(scn.GetDSCard("guard")));
    }
    private VirtualTableScenario drawBattle(String drawn,boolean effect,boolean light) {
        var scn=scenario();scn.StartGame();if(effect)scn.MoveCardsToDSSideOfTable(scn.GetDSCard("reserves"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("veers"),scn.GetDSCard("trooper"),scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.BATTLE);scn.PrepareDSDestiny(0);scn.PrepareLSDestiny(0);
        if(light)scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard(drawn));else scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard(drawn));
        scn.DSInitiateBattle(scn.GetDSStartingLocation());scn.SkipToEndOfPowerSegment(true);return scn;
    }
    @Test public void StrategicReservesAddsOneToActualCoruscantGuardBattleDestiny() {assertEquals(4,drawBattle("guard",true,false).GetDSTotalDestiny());}
    @Test public void StrategicReservesAddsOneToActualCloneBattleDestiny() {assertEquals(4,drawBattle("clone",true,false).GetDSTotalDestiny());}
    @Test public void StrategicReservesDoesNotIncreaseOpponentsGuardBattleDestiny() {assertEquals(3,drawBattle("guard",true,true).GetLSTotalDestiny());}
    @Test public void StrategicReservesDoesNotIncreaseUnrelatedCardBattleDestiny() {assertEquals(3,drawBattle("device",true,false).GetDSTotalDestiny());}
    @Test public void StrategicReservesAbsentLeavesGuardBattleDestinyUnchanged() {assertEquals(3,drawBattle("guard",false,false).GetDSTotalDestiny());}
    private void weapon(boolean effect,String drawn,boolean hit) {
        var scn=scenario();scn.StartGame();if(effect)scn.MoveCardsToDSSideOfTable(scn.GetDSCard("reserves"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("trooper"),scn.GetLSCard("leia"));scn.AttachCardsTo(scn.GetDSCard("trooper"),scn.GetDSCard("blaster"));
        scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation());scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard(drawn));
        scn.DSUseCardAction(scn.GetDSCard("blaster"),"Fire");scn.DSChooseCard(scn.GetLSCard("leia"));scn.PassForceUseResponses();scn.PassResponses("Fire ");scn.PassDestinyDrawResponses();scn.PassResponses("ABOUT_TO_BE_HIT");
        assertEquals(hit,scn.GetLSCard("leia").isHit());assertInZone(Zone.USED_PILE,scn.GetDSCard(drawn));
    }
    @Test public void StrategicReservesGuardWeaponDestinyBonusChangesEqualDefenseMissToHit() {weapon(true,"guard",true);}
    @Test public void StrategicReservesCloneWeaponDestinyBonusAlsoProducesActualHit() {weapon(true,"clone",true);}
    @Test public void StrategicReservesAbsentLeavesSameGuardWeaponDrawAMiss() {weapon(false,"guard",false);}
    @Test public void StrategicReservesDoesNotIncreasePrintedDestinyOutsideRelevantDraw() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToDSSideOfTable(scn.GetDSCard("reserves"));
        assertEquals(3,scn.game().getModifiersQuerying().getDestiny(scn.game().getGameState(),scn.GetDSCard("guard")),scn.epsilon);
        assertEquals(3,scn.game().getModifiersQuerying().getDestiny(scn.game().getGameState(),scn.GetDSCard("clone")),scn.epsilon);
    }
    @Test public void StrategicReservesDoesNotIncreaseGuardDrawnForAnOrdinaryInterruptDestiny() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToDSSideOfTable(scn.GetDSCard("reserves"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("veers"));scn.MoveCardsToDSHand(scn.GetDSCard("alter"));
        scn.SkipToDSTurn(Phase.DEPLOY);scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("guard"));scn.DSPlayCard(scn.GetDSCard("alter"));
        com.gempukku.swccgo.logic.effects.DrawDestinyEffect draw=null;
        for(int i=0;i<40;i++) {
            var state=scn.game().getGameState().getTopDrawDestinyState();
            if(state!=null && scn.GetDSCard("guard").equals(state.getDrawDestinyEffect().getDrawnDestinyCard())
                    && (scn.DSDecisionAvailable("DESTINY_DRAWN")||scn.LSDecisionAvailable("DESTINY_DRAWN")||scn.DSDecisionAvailable("Just drew")||scn.LSDecisionAvailable("Just drew"))) {draw=state.getDrawDestinyEffect();break;}
            if(scn.DSHasCardChoiceAvailable(scn.GetDSCard("reserves")))scn.DSChooseCard(scn.GetDSCard("reserves"));
            else if(scn.DSGetDecision()!=null)scn.DSPass();else if(scn.LSGetDecision()!=null)scn.LSPass();else fail("Strategic Reserves needs an actual ordinary destiny response");
        }
        assertNotNull(draw);assertEquals(DestinyType.DESTINY,draw.getDestinyType());assertEquals(3,draw.getDestinyDrawValue(),scn.epsilon);
        scn.PassAllResponses();assertInZone(Zone.USED_PILE,scn.GetDSCard("guard"));
    }
}
