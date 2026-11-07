package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1262_014_Tests {
    private static final String TAKE = "Place card under Used Pile to take from Force Pile";
    private static final String RETURN = "Place IL-19 in owner's Used Pile";
    private static final String CANCEL = "Cancel just drawn destiny";
    private VirtualTableScenario scenario() {
        // IL-19 has no Decipher printing. Leia's V text supplies the power/attrition draw under test.
        return new VirtualTableScenario(new HashMap<>() {{ put("il19", "1115_13"); put("leia", "202_1"); put("luke", "1_19"); put("blaster", "1_153"); }},
                new HashMap<>() {{ put("why", "1262_14"); put("vader", "1_168"); put("veers", "3_87"); put("cost", "1_201"); put("force1", "1_194"); put("force2", "1_317"); put("force3", "3_154"); put("used", "1_299"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test public void WhyDidntYouTellMeStatsIconsAndKeywordsMatchPrintedImage() {
        var scn=scenario(); var b=scn.GetDSCard("why").getBlueprint();
        assertEquals("Why Didn't You Tell Me?", b.getTitle()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.DARK,b.getSide()); assertEquals(4,b.getDestiny(),scn.epsilon);
        assertEquals(Uniqueness.UNIQUE,b.getUniqueness()); assertEquals(CardSubtype.USED,b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2,b.getExpansionSet()); assertEquals(Rarity.V,b.getRarity());
        assertEquals("Lacking foresight, Luke suffered the consequences of rushing into battle unprepared.",b.getLore());
        scn.BlueprintCardTypeCheck(b,new ArrayList<>() {{ add(CardType.INTERRUPT); }});
        scn.BlueprintIconCheck(b,new ArrayList<>() {{ add(Icon.INTERRUPT); add(Icon.CLOUD_CITY); add(Icon.LEGACY_BLOCK_6); }});
        scn.BlueprintKeywordCheck(b,new ArrayList<Keyword>());
    }
    private VirtualTableScenario draw(boolean darkTurn) {
        var scn=scenario(); scn.StartGame();
        scn.MoveCardsToDSHand(scn.GetDSCard("why"),scn.GetDSCard("cost"));
        scn.MoveCardsToTopOfDSForcePile(scn.GetDSCard("force1"),scn.GetDSCard("force2"),scn.GetDSCard("force3"));
        scn.MoveCardsToTopOfDSUsedPile(scn.GetDSCard("used"));
        if(darkTurn) scn.SkipToDSTurn(Phase.DRAW); else scn.SkipToLSTurn(Phase.DRAW);
        if(!darkTurn && scn.LSGetDecision()!=null) scn.LSPass();
        return scn;
    }
    private void take(VirtualTableScenario scn,String target) {
        var why=scn.GetDSCard("why"); var cost=scn.GetDSCard("cost"); var card=scn.GetDSCard(target);
        assertTrue(scn.DSCardPlayAvailable(why,TAKE)); scn.DSPlayCard(why,TAKE);
        for(int i=0;i<20 && card.getZone()!=Zone.HAND;i++) {
            if(scn.DSHasCardChoiceAvailable(cost)) scn.DSChooseCard(cost);
            else if(scn.DSHasCardChoiceAvailable(card)) scn.DSChooseCard(card);
            else scn.PassAllResponses();
        }
        scn.PassAllResponses(); assertEquals(Zone.HAND,card.getZone());
        assertInZone(Zone.USED_PILE,cost); assertInZone(Zone.USED_PILE,why);
        var pile=scn.game().getGameState().getUsedPile(scn.DS);
        assertEquals(cost,pile.get(pile.size()-1));
        assertInZone(Zone.USED_PILE,scn.GetDSCard("used"));
    }
    @Test public void WhyDidntYouTellMePaysHandCardUnderUsedToTakeAnyForceCardDuringOwnDraw() { take(draw(true),"force2"); }
    @Test public void WhyDidntYouTellMeTakesDifferentCardDuringOpponentsDraw() { take(draw(false),"force3"); }
    @Test public void WhyDidntYouTellMeCannotPayWithTheInterruptItself() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToDSHand(scn.GetDSCard("why"));scn.SkipToDSTurn(Phase.DRAW);
        assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("why"),TAKE));
    }
    @Test public void WhyDidntYouTellMeCannotSearchEmptyForcePile() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToDSHand(scn.GetDSCard("why"),scn.GetDSCard("cost"));
        scn.SkipToDSTurn(Phase.ACTIVATE);scn.DSPass();if(scn.DSDecisionAvailable("activate"))scn.DSChooseYes();scn.SkipToPhase(Phase.DRAW);
        scn.MoveCardsToDSHand(scn.game().getGameState().getForcePile(scn.DS).toArray(new com.gempukku.swccgo.game.PhysicalCardImpl[0]));
        assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("why"),TAKE));
    }
    @Test public void WhyDidntYouTellMeDrawModeUnavailableDuringDeploy() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToDSHand(scn.GetDSCard("why"),scn.GetDSCard("cost"));scn.SkipToDSTurn(Phase.DEPLOY);
        assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("why"),TAKE));
    }
    private VirtualTableScenario il19(Phase phase,boolean present) {
        var scn=scenario();scn.StartGame();if(present)scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetLSCard("il19"));
        scn.MoveCardsToDSHand(scn.GetDSCard("why"));scn.SkipToDSTurn(phase);return scn;
    }
    private void returnDroid(VirtualTableScenario scn) {
        var card=scn.GetLSCard("il19"); var why=scn.GetDSCard("why");int force=scn.GetDSForcePileCount();
        assertTrue(scn.DSCardPlayAvailable(why,RETURN));scn.DSPlayCard(why,RETURN);
        for(int i=0;i<12 && card.getZone()!=Zone.USED_PILE && card.getZone()!=Zone.TOP_OF_USED_PILE;i++) {
            if(scn.DSHasCardChoiceAvailable(card))scn.DSChooseCard(card);else scn.PassAllResponses();
        }
        scn.PassAllResponses();assertInZone(Zone.USED_PILE,card);assertInZone(Zone.USED_PILE,why);
        assertTrue(scn.game().getGameState().getUsedPile(scn.LS).contains(card));
        assertFalse(scn.game().getGameState().getUsedPile(scn.DS).contains(card));assertEquals(force,scn.GetDSForcePileCount());
    }
    @Test public void WhyDidntYouTellMeReturnsOpponentsIL19ToOwnersUsedPileWithoutForceCost() {returnDroid(il19(Phase.DEPLOY,true));}
    @Test public void WhyDidntYouTellMeIL19ModeAlsoWorksOutsideDrawPhase() {returnDroid(il19(Phase.CONTROL,true));}
    @Test public void WhyDidntYouTellMeCannotReturnIL19InHand() {
        var scn=il19(Phase.DEPLOY,false);assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("why"),RETURN));
    }
    @Test public void WhyDidntYouTellMeDoesNotReturnAnotherCharacter() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetLSCard("luke"));
        scn.MoveCardsToDSHand(scn.GetDSCard("why"));scn.SkipToDSTurn(Phase.DEPLOY);
        assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("why"),RETURN));
    }
    private VirtualTableScenario battle(String add) {
        var scn=scenario();scn.StartGame();var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,scn.GetDSCard("veers"),scn.GetLSCard("leia"),scn.GetLSCard("luke"));
        scn.MoveCardsToDSHand(scn.GetDSCard("why"));scn.PrepareLSDestiny(4);scn.PrepareDSDestiny(3);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        if(add!=null) {scn.LSUseCardAction(scn.GetLSCard("leia"),add);scn.PassAllResponses();}
        return scn;
    }
    private com.gempukku.swccgo.logic.effects.DrawDestinyEffect reachDraw(VirtualTableScenario scn,DestinyType type) {
        for(int i=0;i<80;i++) {
            var state=scn.game().getGameState().getTopDrawDestinyState();
            if(state!=null && state.getDrawDestinyEffect().getDestinyType()==type
                    && state.getDrawDestinyEffect().getDrawnDestinyCard()!=null && scn.DSDecisionAvailable("DESTINY_DRAWN"))
                return state.getDrawDestinyEffect();
            if(scn.DSDecisionAvailable("battle destiny?"))scn.DSChooseYes();
            else if(scn.LSDecisionAvailable("battle destiny?"))scn.LSChooseYes();
            else if(scn.DSGetDecision()!=null)scn.DSPass();
            else if(scn.LSGetDecision()!=null)scn.LSPass();
            else fail("Why Didn't You Tell Me requires a live destiny response window");
        }
        fail("Why Didn't You Tell Me did not reach the requested real destiny draw");return null;
    }
    private void cancelAddedDraw(String add,DestinyType type) {
        var scn=battle(add);var draw=reachDraw(scn,type);var card=draw.getDrawnDestinyCard();var why=scn.GetDSCard("why");
        assertTrue(scn.IsReachedPowerSegment());assertFalse(draw.isDestinyCanceled());
        assertTrue(scn.DSCardPlayAvailable(why,CANCEL));scn.DSPlayCard(why,CANCEL);scn.PassAllResponses();
        assertTrue(draw.isDestinyCanceled());assertInZone(Zone.USED_PILE,why);
        assertTrue(scn.game().getGameState().getUsedPile(scn.LS).contains(card));
    }
    @Test public void WhyDidntYouTellMeCancelsActualPowerDestinyDuringPowerSegment() {
        cancelAddedDraw("Add one destiny to total power",DestinyType.DESTINY_TO_TOTAL_POWER);
    }
    @Test public void WhyDidntYouTellMeAlsoCancelsAttritionOnlyDestinyDuringPowerSegment() {
        cancelAddedDraw("Add one destiny to attrition",DestinyType.DESTINY_TO_ATTRITION);
    }
    @Test public void WhyDidntYouTellMeCannotCancelBattleDestinyEvenInPowerSegment() {
        var scn=battle(null);var draw=reachDraw(scn,DestinyType.BATTLE_DESTINY);
        assertTrue(scn.IsReachedPowerSegment());assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("why"),CANCEL));
        assertFalse(draw.isDestinyCanceled());scn.PassDestinyDrawResponses();assertFalse(draw.isDestinyCanceled());
        assertEquals(Zone.HAND,scn.GetDSCard("why").getZone());
    }
    @Test public void WhyDidntYouTellMeMayDeclineCancellationAndKeepInterruptInHand() {
        var scn=battle("Add one destiny to total power");var draw=reachDraw(scn,DestinyType.DESTINY_TO_TOTAL_POWER);
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("why"),CANCEL));scn.PassDestinyDrawResponses();
        assertFalse(draw.isDestinyCanceled());assertEquals(Zone.HAND,scn.GetDSCard("why").getZone());
    }
    @Test public void WhyDidntYouTellMeCannotCancelWeaponDestinyDuringWeaponsSegment() {
        var scn=scenario();scn.StartGame();var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,scn.GetDSCard("veers"),scn.GetLSCard("luke"));
        scn.AttachCardsTo(scn.GetLSCard("luke"),scn.GetLSCard("blaster"));
        scn.MoveCardsToDSHand(scn.GetDSCard("why"));scn.PrepareLSDestiny(6);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.LSUseCardAction(scn.GetLSCard("blaster"),"Fire");scn.LSChooseCard(scn.GetDSCard("veers"));
        scn.PassForceUseResponses();scn.PassResponses("Fire ");
        var draw=reachDraw(scn,DestinyType.WEAPON_DESTINY);
        assertFalse(scn.IsReachedPowerSegment());assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("why"),CANCEL));
        scn.PassDestinyDrawResponses();scn.PassResponses("ABOUT_TO_BE_HIT");
        assertFalse(draw.isDestinyCanceled());assertTrue(scn.GetDSCard("veers").isHit());
        assertEquals(Zone.HAND,scn.GetDSCard("why").getZone());
    }
}
