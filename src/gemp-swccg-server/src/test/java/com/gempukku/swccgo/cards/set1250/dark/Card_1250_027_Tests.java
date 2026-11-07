package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1250_027_Tests {
    private static final String DEPLOY="Deploy a Dark Jedi from Reserve Deck";
    private static final String REDRAW="Take character destiny into hand and redraw";
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("luke","1_19");put("leia","1_17"); }},
                new HashMap<>() {{ put("fury","1250_27");put("vader","1_168");put("veers","3_87");put("character","1_194");put("device","1_201");put("droid","1_193");put("walker","3_154");put("cannon","3_158"); }},
                15,15,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    @Test public void SithFuryStatsIconsAndKeywordsMatchPrintedImage() {
        var scn=scenario();var b=scn.GetDSCard("fury").getBlueprint();
        assertEquals("Sith Fury",b.getTitle());assertTrue(b.hasVirtualSuffix());assertTrue(b.isLegacy());assertEquals(Side.DARK,b.getSide());
        assertTrue(GameTextActionId.LEGACY__SITH_FURY_V__DEPLOY_DARK_JEDI.isSearchCardPile());
        assertTrue(GameTextActionId.LEGACY__SITH_FURY_V__DEPLOY_DARK_JEDI.isPerGame());
        assertEquals(4,b.getDestiny(),scn.epsilon);assertEquals(Uniqueness.UNIQUE,b.getUniqueness());
        assertEquals(CardSubtype.USED_OR_LOST,b.getCardSubtype());assertEquals(ExpansionSet.VIRTUAL_BLOCK_5,b.getExpansionSet());assertEquals(Rarity.V,b.getRarity());
        assertEquals("At his peak, no one could stand up to the Dark Lord of the Sith. His superior tactics devastated those who opposed him.",b.getLore());
        scn.BlueprintCardTypeCheck(b,new ArrayList<>() {{add(CardType.INTERRUPT);}});
        scn.BlueprintIconCheck(b,new ArrayList<>() {{add(Icon.INTERRUPT);add(Icon.TATOOINE);add(Icon.LEGACY_BLOCK_5);}});
        scn.BlueprintKeywordCheck(b,new ArrayList<Keyword>());
    }
    private VirtualTableScenario deploy(int force) {
        var scn=scenario();scn.StartGame();scn.MoveCardsToDSHand(scn.GetDSCard("fury"),scn.GetDSCard("vader"),scn.GetDSCard("character"));
        scn.SkipToDSTurn(Phase.CONTROL);scn.EnsureDSForcePile(force);
        while(scn.GetDSForcePileCount()>force)scn.MoveCardsToTopOfDSReserveDeck((com.gempukku.swccgo.game.PhysicalCardImpl)scn.game().getGameState().getForcePile(scn.DS).getFirst());
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("vader"),scn.GetDSCard("character"));
        scn.SkipToPhase(Phase.DEPLOY);assertEquals(force,scn.GetDSForcePileCount());return scn;
    }
    private boolean selectable(VirtualTableScenario scn,String id) {
        var ids=scn.GetADParam(scn.DS,"blueprintId");var flags=scn.GetADParam(scn.DS,"selectable");
        assertEquals(ids.length,flags.length);for(int i=0;i<ids.length;i++)if(id.equals(ids[i])&&Boolean.parseBoolean(flags[i]))return true;return false;
    }
    private void openDeploy(VirtualTableScenario scn) {
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("fury"),DEPLOY));scn.DSPlayCard(scn.GetDSCard("fury"),DEPLOY);
        scn.PassAllResponses();scn.PassCardAndForceUseResponses();
    }
    @Test public void SithFuryLostPaysFourPlusNormalDeploymentCostAndDeploysDarkJedi() {
        var scn=deploy(10);openDeploy(scn);
        assertEquals(6,scn.GetDSForcePileCount());assertTrue(selectable(scn,"1_168"));assertFalse(selectable(scn,"1_194"));
        scn.DSChooseCard(scn.GetDSCard("vader"));scn.DSChooseCard(scn.GetDSStartingLocation());scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION,scn.GetDSCard("vader").getZone());assertEquals(scn.GetDSStartingLocation(),scn.GetDSCard("vader").getAtLocation());
        assertEquals(0,scn.GetDSForcePileCount());assertInZone(Zone.LOST_PILE,scn.GetDSCard("fury"));
    }
    @Test public void SithFuryLostCannotBePlayedWithThreeForce() {
        var scn=deploy(3);assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("fury"),DEPLOY));
    }
    @Test public void SithFuryLostCannotBePlayedWithoutForce() {
        var scn=deploy(0);assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("fury"),DEPLOY));
    }
    @Test public void SithFuryLostSearchDoesNotMakeAnUnaffordableDarkJediFree() {
        var scn=deploy(9);openDeploy(scn);assertEquals(5,scn.GetDSForcePileCount());assertFalse(selectable(scn,"1_168"));
        assertEquals(Zone.RESERVE_DECK,scn.GetDSCard("vader").getZone());
    }
    @Test public void SithFuryLostCannotDeployAnOrdinaryCharacter() {
        var scn=deploy(10);openDeploy(scn);assertTrue(selectable(scn,"1_168"));assertFalse(selectable(scn,"1_194"));
    }
    @Test public void SithFuryLostUsageRemainsSpentAfterInterruptReturnsToHandAndNewTurn() {
        var scn=deploy(10);openDeploy(scn);scn.DSChooseCard(scn.GetDSCard("vader"));scn.DSChooseCard(scn.GetDSStartingLocation());scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE,scn.GetDSCard("fury"));scn.MoveCardsToDSHand(scn.GetDSCard("fury"));
        scn.MoveCardsToDSHand(scn.GetDSCard("vader"));scn.SkipToDSTurn(Phase.CONTROL);scn.EnsureDSForcePile(4);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("vader"));scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.GetDSForcePileCount()>=4);assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("fury"),DEPLOY));
    }
    private VirtualTableScenario battle(boolean character) {
        var scn=scenario();scn.StartGame();var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,scn.GetDSCard("vader"),scn.GetLSCard("luke"));scn.MoveCardsToDSHand(scn.GetDSCard("fury"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("device"));
        if(character)scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("character"));
        return scn;
    }
    private com.gempukku.swccgo.logic.effects.DrawDestinyEffect reachDraw(VirtualTableScenario scn,boolean dark) {
        for(int i=0;i<60;i++) {
            var state=scn.game().getGameState().getTopDrawDestinyState();
            if(state!=null && state.getDrawDestinyEffect().getDrawnDestinyCard()!=null
                    && state.getDrawDestinyEffect().getPlayerDrawingDestiny().equals(dark?scn.DS:scn.LS)
                    && (scn.DSDecisionAvailable("DESTINY_DRAWN")||scn.DSDecisionAvailable("Just drew")))return state.getDrawDestinyEffect();
            if(scn.DSDecisionAvailable("battle destiny?"))scn.DSChooseYes();else if(scn.LSDecisionAvailable("battle destiny?"))scn.LSChooseYes();
            else if(scn.DSGetDecision()!=null)scn.DSPass();else if(scn.LSGetDecision()!=null)scn.LSPass();else fail("Sith Fury requires a live destiny response");
        }
        fail("Sith Fury did not reach the real destiny draw");return null;
    }
    @Test public void SithFuryUsedTakesCharacterIntoHandCancelsAndActuallyRedrawsDestiny() {
        var scn=battle(true);var draw=reachDraw(scn,true);assertEquals(scn.GetDSCard("character"),draw.getDrawnDestinyCard());
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("fury"),REDRAW));scn.DSPlayCard(scn.GetDSCard("fury"),REDRAW);scn.PassAllResponses();
        assertEquals(Zone.HAND,scn.GetDSCard("character").getZone());assertInZone(Zone.USED_PILE,scn.GetDSCard("fury"));
        scn.PassDestinyDrawResponses();scn.PassAllResponses();
        assertTrue(scn.game().getGameState().getUsedPile(scn.DS).contains(scn.GetDSCard("device")));
        // Original Darth Vader adds 1 to the replacement Comlink's printed destiny of 3.
        assertEquals(4,scn.GetDSTotalDestiny());
    }
    @Test public void SithFuryUsedUnavailableForNonCharacterDestiny() {
        var scn=battle(false);var draw=reachDraw(scn,true);assertEquals(scn.GetDSCard("device"),draw.getDrawnDestinyCard());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("fury"),REDRAW));assertFalse(draw.isDestinyCanceled());
    }
    @Test public void SithFuryUsedMayBeDeclinedLeavingCharacterDrawAndInterruptInHand() {
        var scn=battle(true);var draw=reachDraw(scn,true);assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("fury"),REDRAW));
        scn.DSPass();scn.PassAllResponses();assertFalse(draw.isDestinyCanceled());assertEquals(Zone.HAND,scn.GetDSCard("fury").getZone());
        assertTrue(scn.game().getGameState().getUsedPile(scn.DS).contains(scn.GetDSCard("character")));
    }
    @Test public void SithFuryUsedUnavailableBeforeAnyDestinyIsDrawn() {
        var scn=deploy(10);assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("fury"),REDRAW));
    }
    @Test public void SithFuryUsedCannotTakeOpponentsCharacterDestiny() {
        var scn=battle(true);scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("leia"));
        var draw=reachDraw(scn,false);assertEquals(scn.GetLSCard("leia"),draw.getDrawnDestinyCard());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("fury"),REDRAW));assertFalse(draw.isDestinyCanceled());
        assertEquals(Zone.HAND,scn.GetDSCard("fury").getZone());
    }
    @Test public void SithFuryLostDoesNotBypassNormalDeploymentTimingDuringMovePhase() {
        var scn=deploy(10);scn.SkipToPhase(Phase.MOVE);assertNotNull(scn.DSGetDecision());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("fury"),DEPLOY));assertEquals(10,scn.GetDSForcePileCount());
    }
    @Test public void SithFuryLostDoesNotDeployDuringOpponentsDeployPhase() {
        var scn=deploy(10);scn.SkipToLSTurn(Phase.DEPLOY);scn.LSPass();assertNotNull(scn.DSGetDecision());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("fury"),DEPLOY));
    }
    @Test public void SithFuryUsedAlsoTakesADroidDrawnForDestiny() {
        var scn=battle(true);scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("droid"));var draw=reachDraw(scn,true);
        assertEquals(scn.GetDSCard("droid"),draw.getDrawnDestinyCard());assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("fury"),REDRAW));
        scn.DSPlayCard(scn.GetDSCard("fury"),REDRAW);scn.PassAllResponses();assertEquals(Zone.HAND,scn.GetDSCard("droid").getZone());assertInZone(Zone.USED_PILE,scn.GetDSCard("fury"));
    }
    @Test public void SithFuryUsedWorksForActualWeaponDestinyAndReplacementCanHit() {
        var scn=scenario();scn.StartGame();var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,scn.GetDSCard("walker"),scn.GetLSCard("luke"));
        scn.MoveCardsToDSHand(scn.GetDSCard("veers"),scn.GetDSCard("fury"));scn.BoardAsPilot(scn.GetDSCard("walker"),scn.GetDSCard("veers"));
        scn.AttachCardsTo(scn.GetDSCard("walker"),scn.GetDSCard("cannon"));scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(6);scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("character"));
        scn.DSUseCardAction(scn.GetDSCard("cannon"),"Fire");scn.DSChooseCard(scn.GetLSCard("luke"));scn.PassForceUseResponses();scn.PassResponses("Fire ");
        var draw=reachDraw(scn,true);assertEquals(DestinyType.WEAPON_DESTINY,draw.getDestinyType());
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("fury"),REDRAW));scn.DSPlayCard(scn.GetDSCard("fury"),REDRAW);scn.PassAllResponses();
        scn.PassDestinyDrawResponses();scn.PassAllResponses();scn.PassResponses("ABOUT_TO_BE_HIT");
        assertEquals(Zone.HAND,scn.GetDSCard("character").getZone());assertInZone(Zone.USED_PILE,scn.GetDSCard("fury"));assertTrue(scn.GetLSCard("luke").isHit());
    }
}
