package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class JediMindTrickDecisionOwnerTests {
    protected String blueprint() { return "6_68"; }
    private void release(boolean cancel) {
        var light=new HashMap<String,String>();light.put("card",blueprint());light.put("obi","1_21");light.put("captive","1_19");
        var dark=new HashMap<String,String>();dark.put("vader","1_179");dark.put("prison","6_164");
        var s=new VirtualTableScenario(light,dark);var card=s.GetLSCard("card");var captive=s.GetLSCard("captive");var prison=s.GetDSCard("prison");
        s.StartGame();s.MoveCardsToLSHand(card);s.MoveLocationToTable(prison);s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetLSCard("obi"),s.GetDSCard("vader"));s.gameState().imprisonCharacter(s.game(),captive,prison);s.SkipToLSTurn(Phase.CONTROL);s.EnsureDSForcePile(4);int before=s.GetDSLifeForceRemaining();
        s.LSPlayCard(card,"Release captive");s.LSChooseCard(captive);s.PassAllResponses();
        assertTrue(s.DSDecisionAvailable("lose 2 Force"));assertFalse(s.LSDecisionAvailable("lose 2 Force"));
        if(cancel){s.DSChooseYes();s.PassAllResponses();s.DSChooseCard(s.GetTopOfDSForcePile());s.PassAllResponses();s.DSChooseCard(s.GetTopOfDSForcePile());s.PassAllResponses();assertTrue(captive.isCaptive());assertEquals(before-2,s.GetDSLifeForceRemaining());}
        else{s.DSChooseNo();s.PassAllResponses();assertFalse(captive.isCaptive());assertEquals(before,s.GetDSLifeForceRemaining());}
        assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void OpponentChoosesAndPaysToCancelRelease(){release(true);}
    @Test public void OpponentMayDeclinePaymentAndReleaseCaptive(){release(false);}
}
