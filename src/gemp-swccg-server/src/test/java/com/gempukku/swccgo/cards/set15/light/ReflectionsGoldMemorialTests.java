package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class ReflectionsGoldMemorialTests {
    private VirtualTableScenario scenario(int id) {
        return new VirtualTableScenario(new HashMap<>(Map.of("memorial","15_"+id,"droid","1_5","traffic","1_66","otherMemorial","1_45")),new HashMap<>(Map.of("vader","1_168")));
    }
    private void metadata(int id) {
        var scn=scenario(id);var card=scn.GetLSCard("memorial").getBlueprint();
        assertEquals(id==31?"Crash Site Memorial":"Traffic Control & Crash Site Memorial",card.getTitle());
        assertEquals(id==31?5f:3f,card.getDestiny(),scn.epsilon);assertEquals(id==31?2:0,card.getCredits());assertEquals(id==31,card.isHorizontal());
        assertEquals(Uniqueness.UNIQUE,card.getUniqueness());assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());assertEquals("",card.getLore());
        scn.BlueprintCardTypeCheck(card,List.of(CardType.EFFECT));scn.BlueprintIconCheck(card,List.of(Icon.EFFECT,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of());scn.BlueprintPersonaCheck(card,List.of());
        if(id==56)assertEquals(List.of(new TitleComponent("Traffic Control",Uniqueness.UNRESTRICTED),new TitleComponent(Title.Crash_Site_Memorial,Uniqueness.UNIQUE)),card.getTitleComponents());
    }
    @Test public void CrashSiteMemorialStatsAndKeywordsAreCorrect() {metadata(31);}
    @Test public void TrafficControlAndCrashSiteMemorialStatsAndKeywordsAreCorrect() {metadata(56);}
    private void lostDroid(int id) {
        var scn=scenario(id);var memorial=scn.GetLSCard("memorial");var droid=scn.GetLSCard("droid");var site=scn.GetDSStartingLocation();
        scn.StartGame();scn.MoveCardsToSideOfTable(memorial);scn.MoveCardsToLocation(site,droid,scn.GetLSFiller(1),scn.GetDSCard("vader"));scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);scn.SkipToDamageSegment(false);assertTrue(scn.AwaitingLSBattleDamagePayment());scn.LSChooseCard(droid);
        for(int i=0;i<24;i++) {
            if(scn.LSGetDecision()!=null && scn.LSCardActionAvailable(memorial,"Stack"))break;
            if(scn.LSGetDecision()!=null)scn.LSPass();else if(scn.DSGetDecision()!=null)scn.DSPass();
        }
        scn.LSUseCardAction(memorial,"Stack");scn.PassAllResponses();
        assertEquals(memorial,droid.getStackedOn());assertFalse(scn.GetLSLostPile().contains(droid));
    }
    @Test public void CrashSiteMemorialStacksDroidActuallyForfeitedFromBattle() {lostDroid(31);}
    @Test public void TrafficControlAndCrashSiteMemorialStacksDroidActuallyForfeitedFromBattle() {lostDroid(56);}
    private void exchange(int id) {
        var scn=scenario(id);var memorial=scn.GetLSCard("memorial");var droid=scn.GetLSCard("droid");var hand=scn.GetLSFiller(1);
        scn.StartGame();scn.MoveCardsToSideOfTable(memorial);scn.StackCardsOn(memorial,droid,scn.GetLSCard("traffic"));scn.MoveCardsToHand(hand,scn.GetLSFiller(2));scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(memorial,"Exchange"));scn.LSUseCardAction(memorial,"Exchange");scn.LSChooseCard(hand);scn.LSChooseCard(droid);scn.PassAllResponses();
        assertEquals(Zone.HAND,droid.getZone());assertEquals(memorial,hand.getStackedOn());scn.DSPass();assertFalse(scn.LSCardActionAvailable(memorial,"Exchange"));
    }
    @Test public void CrashSiteMemorialExchangesHandCardOnlyOnceDuringDeployPhase() {exchange(31);}
    @Test public void TrafficControlAndCrashSiteMemorialExchangesHandCardOnlyOnceDuringDeployPhase() {exchange(56);}
    @Test public void TrafficControlAndCrashSiteMemorialReturnsSelectedHandCardsDuringControl() {
        var scn=scenario(56);var memorial=scn.GetLSCard("memorial");var one=scn.GetLSFiller(1);var two=scn.GetLSFiller(2);var keep=scn.GetLSFiller(3);
        scn.StartGame();scn.MoveCardsToSideOfTable(memorial);scn.MoveCardsToHand(one,two,keep);scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSUseCardAction(memorial,"Place cards");scn.LSChooseCard(one);scn.PassAllResponses();scn.LSChooseCard(two);scn.PassAllResponses();scn.LSDecided("");scn.PassAllResponses();
        assertEquals(two,scn.GetTopOfLSUsedPile());assertTrue(scn.GetLSUsedPile().contains(one));assertEquals(Zone.HAND,keep.getZone());
        scn.SkipToPhase(Phase.DEPLOY);assertFalse(scn.LSCardActionAvailable(memorial,"Place cards"));
    }
    @Test public void TrafficControlAndCrashSiteMemorialDeploysFreeBesideTrafficControlButNotAnotherMemorial() {
        var scn=scenario(56);var memorial=scn.GetLSCard("memorial");scn.StartGame();scn.MoveCardsToSideOfTable(scn.GetLSCard("traffic"));scn.MoveCardsToHand(memorial);scn.SkipToLSTurn(Phase.DEPLOY);
        int force=scn.GetLSForcePileCount();assertTrue(scn.LSDeployAvailable(memorial));scn.LSPlayCard(memorial);scn.PassAllResponses();assertEquals(force,scn.GetLSForcePileCount());assertEquals(Zone.SIDE_OF_TABLE,memorial.getZone());
        var blocked=scenario(56);blocked.StartGame();blocked.MoveCardsToSideOfTable(blocked.GetLSCard("otherMemorial"));blocked.MoveCardsToHand(blocked.GetLSCard("memorial"));blocked.SkipToLSTurn(Phase.DEPLOY);assertFalse(blocked.LSDeployAvailable(blocked.GetLSCard("memorial")));
    }
}
