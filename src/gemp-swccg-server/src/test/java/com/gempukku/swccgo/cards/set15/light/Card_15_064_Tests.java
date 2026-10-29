package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_064_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("combo","15_64","luke","1_19","gun","1_154","han","1_11")),new HashMap<>(Map.of("vader","1_168","saber","1_324")));
    }
    @Test public void AwayPutYourWeaponAndPutThatDownStatsAndKeywordsAreCorrect() {
        var scn=scenario();var card=scn.GetLSCard("combo").getBlueprint();
        assertEquals("Away Put Your Weapon & Put That Down",card.getTitle());assertEquals(List.of("Away Put Your Weapon","Put That Down"),card.getTitles());assertEquals(Uniqueness.UNIQUE,card.getUniqueness());
        assertEquals(3f,card.getDestiny(),scn.epsilon);assertEquals(CardSubtype.USED_OR_LOST,card.getCardSubtype());assertEquals(0,card.getCredits());assertEquals("",card.getLore());assertFalse(card.isHorizontal());assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());
        scn.BlueprintCardTypeCheck(card,List.of(CardType.INTERRUPT));scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of());scn.BlueprintPersonaCheck(card,List.of());
    }
    @Test public void AwayPutYourWeaponAndPutThatDownReturnsOwnWeaponAndIsLost() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var han=scn.GetLSCard("han");var gun=scn.GetLSCard("gun");
        scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),han);scn.AttachCardsTo(han,gun);scn.MoveCardsToHand(combo);scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPlayCard(combo,"Place devices");scn.LSChooseCard(gun);scn.PassAllResponses();
        assertTrue(scn.GetLSUsedPile().contains(gun));assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void AwayPutYourWeaponAndPutThatDownRemovesLightsaberAfterDrainEnhancementAndIsUsed() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var vader=scn.GetDSCard("vader");var saber=scn.GetDSCard("saber");var site=scn.GetLSStartingLocation();
        scn.StartGame();scn.MoveCardsToLocation(site,vader);scn.AttachCardsTo(vader,saber);scn.MoveCardsToHand(combo);scn.SkipToDSTurn(Phase.CONTROL);scn.DSForceDrainAt(site);
        for(int i=0;i<20;i++) {
            if(scn.DSGetDecision()!=null && scn.DSCardActionAvailable(saber,"Add 1"))break;
            if(scn.LSGetDecision()!=null)scn.LSPass();else scn.DSPass();
        }
        scn.DSUseCardAction(saber,"Add 1");
        for(int i=0;i<20;i++) {
            if(scn.LSGetDecision()!=null && scn.LSCardPlayAvailable(combo,"Place"))break;
            if(scn.LSGetDecision()!=null)scn.LSPass();else scn.DSPass();
        }
        scn.LSPlayCard(combo,"Place");scn.PassAllResponses();assertTrue(scn.GetDSUsedPile().contains(saber));assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    private void targeting(boolean affordable) {
        var scn=scenario();var combo=scn.GetLSCard("combo");var luke=scn.GetLSCard("luke");var vader=scn.GetDSCard("vader");var saber=scn.GetDSCard("saber");var site=scn.GetDSStartingLocation();
        scn.StartGame();scn.MoveCardsToLocation(site,luke,vader);scn.AttachCardsTo(vader,saber);scn.MoveCardsToHand(combo);scn.SkipToDSTurn(Phase.BATTLE);
        scn.MoveCardsToTopOfLSUsedPile(scn.GetLSForcePile().toArray(new com.gempukku.swccgo.game.PhysicalCardImpl[0]));scn.EnsureLSForcePile(affordable?3:2);assertEquals(affordable?3:2,scn.GetLSForcePileCount());
        scn.DSInitiateBattle(site);scn.PassBattleStartResponses();scn.DSUseCardAction(saber);scn.DSChooseCard(luke);
        for(int i=0;i<20;i++) {
            if(scn.LSGetDecision()!=null)break;
            scn.DSPass();
        }
        assertEquals(affordable,scn.LSCardPlayAvailable(combo,"Cancel weapon targeting"));
        if(!affordable)return;
        int before=scn.GetLSForcePileCount();scn.LSPlayCard(combo,"Cancel weapon targeting");scn.PassAllResponses();
        assertEquals(before-3,scn.GetLSForcePileCount());assertFalse(luke.isHit());assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void AwayPutYourWeaponAndPutThatDownPaysThreeToCancelWeaponTargetingAndIsLost() {targeting(true);}
    @Test public void AwayPutYourWeaponAndPutThatDownRequiresThreeForceToCancelWeaponTargeting() {targeting(false);}
}
