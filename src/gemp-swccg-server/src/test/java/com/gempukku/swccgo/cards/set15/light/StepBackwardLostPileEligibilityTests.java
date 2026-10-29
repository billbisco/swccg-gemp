package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class StepBackwardLostPileEligibilityTests {
    public void retrievesPaidCharacterWithoutUsedOrLostPile(String blueprint) {
        var light=new HashMap<String,String>(); light.put("card",blueprint); light.put("han","1_11");
        var s=new VirtualTableScenario(light,new HashMap<>()); var card=s.GetLSCard("card"); var han=s.GetLSCard("han");
        s.StartGame(); s.SkipToLSTurn(Phase.CONTROL);
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSHand().toArray(new com.gempukku.swccgo.game.PhysicalCardImpl[0]));
        s.MoveCardsToLSHand(card,han,s.GetLSFiller(1),s.GetLSFiller(2));
        assertEquals(0,s.GetLSUsedPile().size()); assertEquals(0,s.GetLSLostPileCount());
        s.LSPass(); s.DSPass();
        s.LSPlayCard(card,"Take character into hand from Lost Pile"); s.PassAllResponses(); s.LSChooseCard(han); s.PassAllResponses();
        assertTrue(s.GetLSHand().contains(han)); assertEquals(1,s.GetLSHandCount()); assertEquals(3,s.GetLSLostPileCount()); assertTrue(s.GetLSLostPile().contains(card));
    }
    @Test public void OriginalSearchesLostPileCreatedByItsPayment() { retrievesPaidCharacterWithoutUsedOrLostPile("11_30"); }
}
