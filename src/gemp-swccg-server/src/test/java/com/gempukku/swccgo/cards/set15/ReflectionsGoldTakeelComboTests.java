package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldTakeelComboTests {
    @Test public void InformantAndTakeelLostUsesOneForceToSwitchBattleDestiny() {
        var light = new HashMap<String,String>(); light.put("luke","1_19");
        var dark = new HashMap<String,String>(); dark.put("combo","15_255"); dark.put("vader","1_168");
        var scn = new VirtualTableScenario(light,dark); scn.StartGame();
        var combo = scn.GetDSCard("combo"); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToDSHand(combo); scn.MoveCardsToLocation(site,scn.GetLSCard("luke"),scn.GetDSCard("vader"));
        scn.EnsureDSForcePile(3); scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(2); scn.PrepareLSDestiny(5);
        scn.DSInitiateBattle(site); scn.PassAllResponses(); scn.SkipToPowerSegment();
        scn.DSChooseYes(); scn.PassDestinyDrawResponses(); scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        scn.LSChooseYes(); scn.PassDestinyDrawResponses(); scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        if (scn.DSGetDecision() == null) scn.LSPass();
        assertTrue(scn.DSCardPlayAvailable(combo,"Switch battle destiny"));
        int force = scn.GetDSForcePileCount();
        scn.DSPlayCard(combo,"Switch battle destiny"); scn.PassAllResponses();
        assertEquals(force-1,scn.GetDSForcePileCount());
        assertEquals(5f,scn.gameState().getBattleState().getTotalBattleDestiny(scn.game(),scn.DS),scn.epsilon);
        // Vader adds one to the original Dark draw of two.
        assertEquals(3f,scn.gameState().getBattleState().getTotalBattleDestiny(scn.game(),scn.LS),scn.epsilon);
        assertTrue(scn.GetDSLostPile().contains(combo));
    }
}
