package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldColdFeetComboTests {
    private VirtualTableScenario scenario() {
        var light = new HashMap<String,String>(); light.put("luke","1_19");
        var dark = new HashMap<String,String>(); dark.put("combo","15_227"); dark.put("site","3_148");
        var scn = new VirtualTableScenario(light,dark); scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("site"));
        scn.MoveCardsToLocation(scn.GetDSCard("site"),scn.GetLSCard("luke"));
        scn.MoveCardsToDSHand(scn.GetDSCard("combo"));
        scn.SkipToDSTurn(Phase.CONTROL);
        return scn;
    }
    private void coldFeet(int destiny, boolean succeeds) {
        var scn = scenario(); var combo=scn.GetDSCard("combo"); var luke=scn.GetLSCard("luke");
        scn.PrepareDSDestiny(destiny);
        scn.DSPlayCard(combo,"Make character not able move"); scn.DSChooseCard(luke); scn.PassAllResponses();
        assertEquals(succeeds,scn.game().getModifiersQuerying().mayNotMove(scn.gameState(),luke));
        assertTrue(scn.GetDSLostPile().contains(combo));
        scn.SkipToLSTurn(Phase.ACTIVATE);
        assertFalse(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(),luke));
    }
    @Test public void ColdFeetAndHeHasntComeBackYetStopsMovementUntilEndOfTurn() { coldFeet(5,true); }
    @Test public void ColdFeetAndHeHasntComeBackYetEqualDestinyDoesNotStopMovement() { coldFeet(4,false); }
    private void missing(int destiny,boolean succeeds) {
        var scn = scenario(); var combo=scn.GetDSCard("combo"); var luke=scn.GetLSCard("luke");
        scn.PrepareDSDestiny(destiny);
        scn.DSPlayCard(combo,"Make a character missing"); scn.DSChooseCard(luke); scn.PassAllResponses();
        assertEquals(succeeds,luke.isMissing());
        assertTrue(scn.GetDSLostPile().contains(combo));
    }
    @Test public void ColdFeetAndHeHasntComeBackYetMakesAloneCharacterMissingWithDestinyPlusThree() { missing(2,true); }
    @Test public void ColdFeetAndHeHasntComeBackYetMissingRequiresStrictlyGreaterThanAbility() { missing(1,false); }
}
