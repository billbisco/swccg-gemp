package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class ReflectionsGoldDecreeDarkPathTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("revolution","1_062"); light.put("luke","1_019"); light.put("saber","1_155");
        var dark=new HashMap<String,String>(); dark.put("base1","3_145"); dark.put("base2","3_146"); dark.put("combo","15_186"); dark.put("system1","1_282"); dark.put("system2","1_289");
        dark.put("ship1","4_167"); dark.put("ship2","8_168"); dark.put("control","15_225");
        var scn=new VirtualTableScenario(light,dark); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("combo"),scn.GetDSCard("control"));
        scn.SkipToDSTurn(Phase.DEPLOY); scn.DSPlayCardAndPassResponses(scn.GetDSCard("combo")); return scn;
    }
    @Test public void ImperialDecreeAndTheDarkPathPrintedMetadataMatchesRemaster() {
        var scn=scenario(); var card=scn.GetDSCard("combo").getBlueprint();
        assertEquals(2f,card.getDestiny(),scn.epsilon); assertEquals(Uniqueness.UNRESTRICTED,card.getUniqueness());
        assertEquals(0,card.getCredits()); assertFalse(card.isHorizontal()); assertEquals(Rarity.UNKNOWN,card.getRarity());
        scn.BlueprintIconCheck(card,List.of(Icon.EFFECT,Icon.REFLECTIONS_GOLD)); scn.BlueprintKeywordCheck(card,List.of());
    }
    @Test public void ImperialDecreeAndTheDarkPathLosesExactlyTwoOfTopThreeOncePerTurn() {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var one=scn.GetDSCard("ship1"); var two=scn.GetDSFiller(1); var three=scn.GetDSCard("ship2");
        scn.MoveCardsToTopOfDSReserveDeck(one,two,three); if(scn.LSGetDecision()!=null) scn.LSPass();
        scn.DSUseCardAction(combo,"Peak"); scn.DSChooseCards(one,three); scn.PassAllResponses(); scn.DSChooseCard(one); scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(one)); assertTrue(scn.GetDSLostPile().contains(three));
        assertEquals(two,scn.GetTopOfDSReserveDeck());
        if(scn.LSGetDecision()!=null) scn.LSPass();
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("control"))); assertFalse(scn.DSCardActionAvailable(combo,"Peak"));
    }
    @Test public void ImperialDecreeAndTheDarkPathCancelsRevolutionOnlyWithRequiredControl() {
        var scn=scenario(); var revolution=scn.GetLSCard("revolution"); var site=scn.GetDSStartingLocation();
        scn.AttachCardsTo(site,revolution); var query=scn.game().getModifiersQuerying();
        scn.MoveCardsToLocation(site,scn.GetDSFiller(1));
        for(String n:List.of("system1","system2")) scn.MoveLocationToTable(scn.GetDSCard(n));
        scn.MoveCardsToLocation(scn.GetDSCard("system1"),scn.GetDSCard("ship1"));
        assertFalse(query.isEffectsOfRevolutionCanceled(scn.gameState(),revolution));
        scn.MoveCardsToLocation(scn.GetDSCard("system2"),scn.GetDSCard("ship2"));
        assertTrue(query.isEffectsOfRevolutionCanceled(scn.gameState(),revolution));
        scn.MoveCardsToDSHand(scn.GetDSCard("ship2"));
        assertFalse(query.isEffectsOfRevolutionCanceled(scn.gameState(),revolution));
    }
    @Test public void ImperialDecreeAndTheDarkPathTwoRebelBasesCancelOpponentDrainBonus() {
        var scn=scenario(); var query=scn.game().getModifiersQuerying(); var site=scn.GetLSStartingLocation();
        var luke=scn.GetLSCard("luke"); var saber=scn.GetLSCard("saber");
        scn.MoveCardsToLocation(site,luke); scn.AttachCardsTo(luke,saber);
        assertFalse(query.isForceDrainModifierCanceled(scn.gameState(),site,saber,scn.LS,scn.LS,1));
        for(String name:List.of("base1","base2")) scn.MoveLocationToTable(scn.GetDSCard(name));
        scn.MoveCardsToLocation(scn.GetDSCard("base1"),scn.GetDSFiller(1));
        assertFalse(query.isForceDrainModifierCanceled(scn.gameState(),site,saber,scn.LS,scn.LS,1));
        scn.MoveCardsToLocation(scn.GetDSCard("base2"),scn.GetDSFiller(2));
        assertTrue(query.isForceDrainModifierCanceled(scn.gameState(),site,saber,scn.LS,scn.LS,1));
        scn.MoveCardsToDSHand(scn.GetDSFiller(2));
        assertFalse(query.isForceDrainModifierCanceled(scn.gameState(),site,saber,scn.LS,scn.LS,1));
    }
}
