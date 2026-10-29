package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class ReflectionsGoldJabbaNevarComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("spy","1_020"); light.put("luke","1_19");
        var dark=new HashMap<String,String>(); dark.put("combo","15_259"); dark.put("boba","5_091"); dark.put("rifle","5_179");
        var scn=new VirtualTableScenario(light,dark); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("combo"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("boba")); return scn;
    }
    @Test public void JabbasThroughWithYouAndNevarYalnalPrintedMetadataMatchesRemaster() {
        var scn=scenario(); var card=scn.GetDSCard("combo").getBlueprint();
        assertEquals(4f,card.getDestiny(),scn.epsilon); assertEquals(CardSubtype.USED,card.getCardSubtype()); assertEquals(Uniqueness.UNIQUE,card.getUniqueness());
        assertEquals(0,card.getCredits()); scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD)); scn.BlueprintKeywordCheck(card,List.of());
    }
    private void spy(int destiny,boolean undercover) {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var spy=scn.GetLSCard("spy");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),spy); if(undercover) scn.MakeCardGoUndercover(spy);
        scn.SkipToDSTurn(Phase.DEPLOY); scn.PrepareDSDestiny(destiny);
        scn.DSPlayCard(combo,"Target opponent's spy"); scn.DSChooseCard(spy); scn.PassAllResponses();
        assertEquals(destiny>(undercover?0:2),scn.GetLSLostPile().contains(spy)); assertTrue(scn.GetDSUsedPile().contains(combo));
    }
    @Test public void JabbasThroughWithYouAndNevarYalnalBountyHunterKillsSpyWithDestinyThree() { spy(3,false); }
    @Test public void JabbasThroughWithYouAndNevarYalnalBountyHunterDoesNotKillSpyWithDestinyTwo() { spy(2,false); }
    @Test public void JabbasThroughWithYouAndNevarYalnalUndercoverSpyLostWithDestinyOne() { spy(1,true); }
    @Test public void JabbasThroughWithYouAndNevarYalnalUndercoverSpySurvivesDestinyZero() { spy(0,true); }
    @Test public void JabbasThroughWithYouAndNevarYalnalDeploysWeaponToBountyHunterFromReserve() {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var rifle=scn.GetDSCard("rifle"); var boba=scn.GetDSCard("boba");
        scn.SkipToDSTurn(Phase.DEPLOY); scn.EnsureDSForcePile(3); scn.MoveCardsToTopOfDSReserveDeck(rifle);
        scn.DSPlayCard(combo,"Deploy card"); scn.PassAllResponses(); scn.DSChooseCard(rifle); scn.PassAllResponses(); if(scn.DSHasCardChoiceAvailable(boba)) scn.DSChooseCard(boba); scn.PassAllResponses();
        assertEquals(boba,rifle.getAttachedTo()); assertTrue(scn.GetDSUsedPile().contains(combo));
    }
    @Test public void JabbasThroughWithYouAndNevarYalnalAddsDestinyAfterBountyHunterFiresWeapon() {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var rifle=scn.GetDSCard("rifle"); var boba=scn.GetDSCard("boba"); var luke=scn.GetLSCard("luke");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),luke); scn.AttachCardsTo(boba,rifle);
        scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureDSForcePile(3); scn.PrepareDSDestiny(0); scn.DSInitiateBattle(scn.GetDSStartingLocation());
        scn.DSUseCardAction(rifle,"Fire"); scn.DSChooseCard(luke); int before=scn.GetDSBattleDestinyCount(); boolean played=false;
        for(int i=0;i<24 && !played;i++) {
            if(scn.DSGetDecision()!=null && scn.DSGetADParam("cardId")!=null && scn.DSCardPlayAvailable(combo,"Add one")) { scn.DSPlayCard(combo,"Add one"); played=true; }
            else if(scn.LSGetDecision()!=null && scn.LSDecisionAvailable("Optional")) scn.LSPass();
            else if(scn.DSGetDecision()!=null && scn.DSDecisionAvailable("Optional")) scn.DSPass();
            else fail("Expected weapon response: "+scn.GetCurrentDecision());
        }
        assertTrue(played); scn.PassAllResponses(); assertEquals(before+1,scn.GetDSBattleDestinyCount()); assertTrue(scn.GetDSUsedPile().contains(combo));
    }
    @Test public void ComponentRestrictionSymbolsMatchPrintedTitles() {
        var card=new com.gempukku.swccgo.cards.set15.dark.Card15_259();
        assertEquals(java.util.List.of(new TitleComponent("Jabba's Through With You",Uniqueness.UNIQUE),new TitleComponent("Nevar Yalnal",Uniqueness.UNRESTRICTED)),card.getTitleComponents());
    }
}
