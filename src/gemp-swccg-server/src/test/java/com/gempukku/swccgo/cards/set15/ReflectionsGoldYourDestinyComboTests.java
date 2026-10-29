package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldYourDestinyComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("luke","1_19"); light.put("trooper","1_28");
        var dark=new HashMap<String,String>(); dark.put("combo","15_218"); dark.put("objective","9_151"); dark.put("vader","1_168");
        var scn=new VirtualTableScenario(light,dark); scn.MoveOutOfPlay(scn.GetDSCard("objective")); scn.StartGame();
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("objective")); scn.MoveCardsToDSHand(scn.GetDSCard("combo"));
        scn.SkipToDSTurn(Phase.DEPLOY); return scn;
    }
    private void deploy(VirtualTableScenario scn) {
        var combo=scn.GetDSCard("combo"); int force=scn.GetDSForcePileCount();
        scn.DSPlayCard(combo); scn.DSChooseCard(scn.GetDSCard("objective")); scn.PassAllResponses();
        assertEquals(force,scn.GetDSForcePileCount()); assertEquals(scn.GetDSCard("objective"),combo.getAttachedTo());
    }
    @Test public void YourDestinyAndInsignificantRebellionDeploysForFreeAndProtectsLuke() {
        var scn=scenario(); deploy(scn);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(),scn.GetDSCard("combo"),Title.Alter));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(),scn.GetLSCard("luke"));
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(),scn.GetLSCard("luke"),Title.Responsibility_Of_Command));
    }
    @Test public void YourDestinyAndInsignificantRebellionLosesThreeAtStartWithVaderButNoLuke() {
        var scn=scenario(); deploy(scn); scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("vader"));
        scn.SkipToDSTurn(); scn.PassAllResponses();
        int before=scn.GetLSLostPileCount(); assertTrue(scn.AwaitingLSForceLossPayment());
        scn.LSPayRemainingForceLossFromReserveDeck(); assertEquals(before+3,scn.GetLSLostPileCount());
    }
    @Test public void YourDestinyAndInsignificantRebellionStacksOpponentsLossAfterWinningBattle() {
        var scn=scenario(); deploy(scn); var combo=scn.GetDSCard("combo"); var trooper=scn.GetLSCard("trooper");
        var site=scn.GetDSStartingLocation(); scn.MoveCardsToLocation(site,scn.GetDSFiller(1),scn.GetDSFiller(2),trooper);
        scn.EnsureDSForcePile(2); scn.SkipToPhase(Phase.BATTLE); scn.DSInitiateBattle(site); scn.SkipToDamageSegment(false); scn.PassAllResponses();
        assertTrue(scn.AwaitingLSForceLossPayment()); var lost=scn.GetTopOfLSReserveDeck();
        scn.LSChooseCard(lost); scn.PassAllResponses();
        assertEquals(Zone.STACKED_FACE_DOWN,lost.getZone());
        assertTrue(scn.gameState().getStackedCards(combo).contains(lost));
    }
}
