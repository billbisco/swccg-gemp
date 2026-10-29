package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldLightningComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("luke","1_19");
        var dark=new HashMap<String,String>(); dark.put("combo","15_242"); dark.put("emperor","9_109");
        dark.put("vader","1_168"); dark.put("throne","9_147"); dark.put("saber","1_324");
        var scn=new VirtualTableScenario(light,dark); scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("throne"));
        scn.MoveCardsToLocation(scn.GetDSCard("throne"),scn.GetDSCard("emperor"),scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(scn.GetDSCard("combo")); scn.SkipToDSTurn(Phase.CONTROL);
        return scn;
    }
    @Test public void ForceLightningAndRiseMyFriendUsedReducesDefenseUntilEndOfTurn() {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var luke=scn.GetLSCard("luke");
        float before=scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(),luke);
        scn.DSPlayCard(combo,"Reduce target"); scn.DSChooseCard(luke); scn.PassAllResponses();
        assertEquals(before-4,scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(),luke),scn.epsilon);
        assertTrue(scn.GetDSUsedPile().contains(combo));
        scn.SkipToLSTurn(Phase.ACTIVATE);
        assertEquals(before,scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(),luke),scn.epsilon);
    }
    @Test public void ForceLightningAndRiseMyFriendLostDefeatsTargetAndWeakensEmperorForTurn() {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var luke=scn.GetLSCard("luke"); var emperor=scn.GetDSCard("emperor");
        float before=scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(),emperor);
        scn.PrepareDSDestiny(5);
        scn.DSPlayCard(combo,"Make target lost");
        if (scn.DSHasCardChoiceAvailable(emperor)) scn.DSChooseCard(emperor);
        scn.DSChooseCard(luke); scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(luke)); assertTrue(scn.GetDSLostPile().contains(combo));
        assertEquals(before-2,scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(),emperor),scn.epsilon);
        scn.SkipToLSTurn(Phase.ACTIVATE);
        assertEquals(before,scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(),emperor),scn.epsilon);
    }
    @Test public void ForceLightningAndRiseMyFriendLostReturnsVaderAndAttachedCardsToHand() {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var vader=scn.GetDSCard("vader"); var saber=scn.GetDSCard("saber");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),vader); scn.AttachCardsTo(vader,saber);
        assertTrue(scn.DSCardPlayAvailable(combo,"Take Vader into hand"));
        scn.DSPlayCard(combo,"Take Vader into hand"); scn.DSChooseCard(vader); scn.PassAllResponses();
        assertEquals(Zone.HAND,vader.getZone()); assertEquals(Zone.HAND,saber.getZone());
        assertTrue(scn.GetDSLostPile().contains(combo));
    }
}
