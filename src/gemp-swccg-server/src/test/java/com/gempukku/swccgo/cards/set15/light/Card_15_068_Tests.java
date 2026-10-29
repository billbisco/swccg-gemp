package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_068_Tests {
    private VirtualTableScenario scenario(boolean starting,int effects) {
        var light=new HashMap<>(Map.of("combo","15_68","luke","1_19","site","5_80","insurrection","8_40","staging","9_40","planning","9_41"));
        StartingSetup setup=new StartingSetup(){public HashMap<String,String> Cards(){return new HashMap<>();}public void Setup(VirtualTableScenario scn){
            if(!starting){scn.LSPass();return;}scn.LSChooseCard(scn.GetLSCard("combo"));scn.PassAllResponses();scn.LSChooseOption("Left");scn.PassAllResponses();
            if(effects>0){scn.LSChooseCard(scn.GetLSCard("insurrection"));scn.PassAllResponses();scn.LSChooseCard(scn.GetLSCard("staging"));scn.PassAllResponses();}else{scn.LSDecided("");scn.PassAllResponses();}
        }};
        return new VirtualTableScenario(light,new HashMap<>(Map.of("trooper","1_194")),40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,setup,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    @Test public void CarefulPlanningAndHeadingForTheMedicalFrigateStatsAndKeywordsAreCorrect() {
        var scn=scenario(false,0);var card=scn.GetLSCard("combo").getBlueprint();assertEquals("Careful Planning & Heading For The Medical Frigate",card.getTitle());assertEquals(Side.LIGHT,card.getSide());assertEquals(List.of(Title.Careful_Planning,"Heading For The Medical Frigate"),card.getTitles());assertEquals(Uniqueness.UNIQUE,card.getUniqueness());assertEquals(3f,card.getDestiny(),scn.epsilon);assertEquals(CardSubtype.USED_OR_STARTING,card.getCardSubtype());assertEquals(0,card.getCredits());assertEquals("",card.getLore());assertFalse(card.isHorizontal());assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());scn.BlueprintCardTypeCheck(card,List.of(CardType.INTERRUPT));scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of());scn.BlueprintPersonaCheck(card,List.of());
    }
    private void activate(int amount) {
        var scn=scenario(false,0);var combo=scn.GetLSCard("combo");scn.StartGame();scn.MoveCardsToHand(combo);scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetLSCard("luke"),scn.GetDSCard("trooper"));scn.SkipToLSTurn(Phase.BATTLE);scn.PrepareLSDestiny(3);scn.LSInitiateBattle(scn.GetDSStartingLocation());for(int i=0;i<24;i++){if(scn.LSGetDecision()!=null && scn.LSCardPlayAvailable(combo))break;if(scn.LSGetDecision()!=null)scn.LSPass();else scn.DSPass();}int force=scn.GetLSForcePileCount();scn.LSPlayCard(combo);scn.PassAllResponses();scn.LSDecided(amount);scn.PassAllResponses();assertEquals(force+amount,scn.GetLSForcePileCount());assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    @Test public void CarefulPlanningAndHeadingForTheMedicalFrigateMayActivateZero() {activate(0);}
    @Test public void CarefulPlanningAndHeadingForTheMedicalFrigateMayActivateLessThanDestiny() {activate(2);}
    @Test public void CarefulPlanningAndHeadingForTheMedicalFrigateMayActivateFullDestiny() {activate(3);}
    @Test public void CarefulPlanningAndHeadingForTheMedicalFrigateStartsRelatedSiteAndTwoEffectsThenLost() {
        var scn=scenario(true,2);scn.StartGame();assertEquals(Zone.LOCATIONS,scn.GetLSCard("site").getZone());assertTrue(scn.GetLSCard("insurrection").getZone().isInPlay());assertTrue(scn.GetLSCard("staging").getZone().isInPlay());assertFalse(scn.GetLSCard("planning").getZone().isInPlay());assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("combo")));
    }
    @Test public void CarefulPlanningAndHeadingForTheMedicalFrigateMayStartZeroEffects() {
        var scn=scenario(true,0);scn.StartGame();assertEquals(Zone.LOCATIONS,scn.GetLSCard("site").getZone());assertFalse(scn.GetLSCard("insurrection").getZone().isInPlay());assertFalse(scn.GetLSCard("staging").getZone().isInPlay());assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("combo")));
    }
    @Test public void ObjectiveIsDeployedBeforeStartingChoiceAndBlocksSiteAndEffects() {
        var light=new HashMap<>(Map.of("combo","15_68","alternative","9_51","site","1_133","insurrection","8_40","staging","9_40"));
        StartingSetup starting=new StartingSetup() {
            public HashMap<String,String> Cards(){return new HashMap<>();}
            public void Setup(VirtualTableScenario scn){
                assertTrue(scn.GetLSCard("profit").getZone().isInPlay());
                assertEquals(Zone.LOCATIONS,scn.GetLSCard("palace").getZone());
                assertEquals(Zone.LOCATIONS,scn.GetLSCard("chamber").getZone());
                assertTrue(scn.LSDecisionAvailable("Choose starting interrupt"));
                assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("alternative")));
                assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("combo")));
                scn.LSPass();
            }
        };
        var scn=new VirtualTableScenario(light,new HashMap<>(),40,40,StartingSetup.ProfitObjective,StartingSetup.DefaultDSGroundLocation,starting,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
        scn.StartGame();
        for(String key:List.of("site","insurrection","staging"))assertFalse(key,scn.GetLSCard(key).getZone().isInPlay());
        assertFalse(scn.GetLSLostPile().contains(scn.GetLSCard("combo")));
    }
}
