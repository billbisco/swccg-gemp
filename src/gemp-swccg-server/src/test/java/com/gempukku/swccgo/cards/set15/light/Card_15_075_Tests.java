package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_075_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("combo","15_75","han","1_11","falcon","1_143","xwing","1_146","system","1_127","effect","1_65","political","12_40","luke","1_19","otherEffect","1_45")), new HashMap<>(Map.of("resources","1_255","ship","1_305")));
    }
    @Test public void EffectiveRepairsAndPunchItStatsAndKeywordsAreCorrect() {
        var scn=scenario(); var card=scn.GetLSCard("combo").getBlueprint();
        assertEquals("Effective Repairs & Punch It!",card.getTitle()); assertEquals(List.of("Effective Repairs","Punch It!"),card.getTitles());
        assertEquals(Uniqueness.UNRESTRICTED,card.getUniqueness()); assertEquals(3f,card.getDestiny(),scn.epsilon); assertEquals(CardSubtype.USED_OR_LOST,card.getCardSubtype());
        assertEquals(0,card.getCredits()); assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet()); assertEquals(Rarity.UNKNOWN,card.getRarity());
        assertEquals("",card.getLore()); assertFalse(card.isHorizontal());
        scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD)); scn.BlueprintKeywordCheck(card,List.of()); scn.BlueprintPersonaCheck(card,List.of());
    }
    @Test public void EffectiveRepairsAndPunchItSearchesPastTopmostEffectButExcludesPoliticalEffects() {
        var scn=scenario(); var combo=scn.GetLSCard("combo"); var effect=scn.GetLSCard("effect"); var political=scn.GetLSCard("political");
        scn.StartGame(); scn.MoveCardsToHand(combo); scn.MoveCardsToTopOfLSLostPile(effect,political,scn.GetLSCard("otherEffect"),scn.GetLSFiller(1)); scn.EnsureLSForcePile(6); scn.SkipToLSTurn(Phase.CONTROL);
        int force=scn.GetLSForcePileCount(); scn.LSPlayCard(combo,"Take an Effect"); scn.PassAllResponses();
        var parameters=scn.LSGetDecision().getDecisionParameters(); var ids=parameters.get("blueprintId"); var selectable=parameters.get("selectable");
        boolean foundEffect=false,foundPolitical=false;
        for(int i=0;i<ids.length;i++) {
            if(ids[i].equals(effect.getBlueprintId(true))) { assertEquals("true",selectable[i]);foundEffect=true; }
            if(ids[i].equals(political.getBlueprintId(true))) { assertEquals("false",selectable[i]);foundPolitical=true; }
        }
        assertTrue(foundEffect); assertTrue(foundPolitical); scn.LSChooseCard(effect); scn.PassAllResponses();
        assertTrue(scn.GetLSHand().contains(effect)); assertTrue(scn.GetLSLostPile().contains(political)); assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("otherEffect"))); assertTrue(scn.GetLSLostPile().contains(combo)); assertEquals(force-3,scn.GetLSForcePileCount());
    }
    @Test public void EffectiveRepairsAndPunchItCancelsLimitedResourcesAsUsed() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var resources=scn.GetDSCard("resources");
        scn.StartGame(); scn.MoveCardsToHand(combo,resources); scn.SkipToDSTurn(Phase.CONTROL); int lost=scn.GetLSLostPileCount();
        assertTrue(scn.GetLSHandCount()<=2); scn.DSPlayCard(resources);
        for(int i=0;i<24;i++) {
            if(scn.LSGetDecision()!=null && scn.LSCardPlayAvailable(combo,"Cancel")) break;
            if(scn.LSGetDecision()!=null) scn.LSPass(); else if(scn.DSGetDecision()!=null) scn.DSPass();
        }
        scn.LSPlayCard(combo,"Cancel");scn.PassAllResponses();
        assertEquals(lost,scn.GetLSLostPileCount());assertTrue(scn.GetDSLostPile().contains(resources));assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    private void punch(String shipName,int extra,boolean defending) {
        var scn=scenario();var combo=scn.GetLSCard("combo");var ship=scn.GetLSCard(shipName);var system=scn.GetLSCard("system");
        scn.StartGame();scn.MoveLocationToTable(system);scn.MoveCardsToLocation(system,ship,scn.GetDSCard("ship"));scn.BoardAsPilot(ship,scn.GetLSCard("han"));scn.MoveCardsToHand(combo);
        if(shipName.equals("falcon")) scn.BoardAsPilot(ship,scn.GetLSCard("luke"));
        if(defending) {scn.SkipToDSTurn(Phase.BATTLE);scn.DSInitiateBattle(system);} else {scn.SkipToLSTurn(Phase.BATTLE);scn.LSInitiateBattle(system);}
        scn.PassAllResponses();if(scn.LSGetDecision()==null)scn.DSPass();
        if(!defending) {assertFalse(scn.LSCardPlayAvailable(combo,"Add battle destiny"));return;}
        int before=scn.GetLSBattleDestinyCount();scn.LSPlayCard(combo,"Add battle destiny");scn.LSChooseCard(ship);scn.PassAllResponses();
        assertEquals(before+extra,scn.GetLSBattleDestinyCount());assertTrue(scn.game().getModifiersQuerying().hasAnyImmunityToAttrition(scn.gameState(),ship));assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void EffectiveRepairsAndPunchItAddsTwoForDefendingFalcon() {punch("falcon",2,true);}
    @Test public void EffectiveRepairsAndPunchItAddsOneForOtherDefendingStarfighter() {punch("xwing",1,true);}
    @Test public void EffectiveRepairsAndPunchItCannotEnhanceAttackingStarfighter() {punch("falcon",0,false);}
}
