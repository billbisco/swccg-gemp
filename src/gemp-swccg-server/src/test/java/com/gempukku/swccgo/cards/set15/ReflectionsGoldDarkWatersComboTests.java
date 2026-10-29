package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldDarkWatersComboTests {
    @Test public void PresenceOfTheForceAndDarkWatersDeploysOnlyToAllowedSitesAndAddsIcons() {
        var dark=new HashMap<String,String>(); dark.put("combo","15_199"); dark.put("hoth","3_148");
        dark.put("interior","6_167"); dark.put("creature","2_110");
        var scn=new VirtualTableScenario(new HashMap<>(),dark); scn.StartGame();
        var combo=scn.GetDSCard("combo"); var site=scn.GetDSStartingLocation(); var creature=scn.GetDSCard("creature");
        scn.MoveLocationToTable(scn.GetDSCard("hoth")); scn.MoveLocationToTable(scn.GetDSCard("interior"));
        scn.MoveCardsToDSHand(combo); scn.SkipToDSTurn(Phase.DEPLOY);
        var query=scn.game().getModifiersQuerying();
        int light=query.getIconCount(scn.gameState(),site,Icon.LIGHT_FORCE);
        int darkIcons=query.getIconCount(scn.gameState(),site,Icon.DARK_FORCE);
        float drain=query.getForceDrainAmount(scn.gameState(),site,scn.LS);
        scn.DSPlayCard(combo);
        assertTrue(scn.DSHasCardChoiceAvailable(site));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("hoth")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("interior")));
        scn.DSChooseCard(site); scn.PassAllResponses();
        assertEquals(site,combo.getAttachedTo());
        assertEquals(light+1,query.getIconCount(scn.gameState(),site,Icon.LIGHT_FORCE));
        assertEquals(darkIcons+1,query.getIconCount(scn.gameState(),site,Icon.DARK_FORCE));
        assertEquals(drain,query.getForceDrainAmount(scn.gameState(),site,scn.LS),scn.epsilon);
        assertFalse(query.isImmuneToCardTitle(scn.gameState(),combo,Title.Alter));
        scn.MoveCardsToLocation(site,creature);
        assertTrue(query.isImmuneToCardTitle(scn.gameState(),combo,Title.Alter));
        scn.MoveCardsToDSHand(creature);
        assertFalse(query.isImmuneToCardTitle(scn.gameState(),combo,Title.Alter));
    }
    @Test public void PresenceOfTheForceAndDarkWatersKeepsIndependentLimitsInEitherDeploymentOrder() {
        for(boolean comboFirst:new boolean[]{true,false}) {
            var dark=new HashMap<String,String>(); dark.put("combo","15_199"); dark.put("presence","1_227"); dark.put("third","1_227");
            dark.put("control","15_215"); dark.put("waters1","2_119"); dark.put("waters2","2_119"); dark.put("site2","8_164"); dark.put("site3","8_165"); dark.put("site4","8_158");
            var scn=new VirtualTableScenario(new HashMap<>(),dark); scn.StartGame();
            for(String n:java.util.List.of("site2","site3","site4")) scn.MoveLocationToTable(scn.GetDSCard(n));
            for(String n:java.util.List.of("combo","presence","third","waters1","waters2","control")) scn.MoveCardsToDSHand(scn.GetDSCard(n));
            scn.SkipToDSTurn(Phase.DEPLOY);
            var first=scn.GetDSCard(comboFirst?"combo":"presence"); var second=scn.GetDSCard(comboFirst?"presence":"combo");
            scn.DSPlayCardAndPassResponses(first,scn.GetDSStartingLocation()); if(scn.LSGetDecision()!=null) scn.LSPass();
            scn.DSPlayCardAndPassResponses(second,scn.GetDSCard("site2")); if(scn.LSGetDecision()!=null) scn.LSPass();
            assertNotNull(scn.DSGetDecision()); assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("third")));
            scn.DSPlayCardAndPassResponses(scn.GetDSCard("waters1"),scn.GetDSCard("site3")); if(scn.LSGetDecision()!=null) scn.LSPass();
            scn.DSPlayCardAndPassResponses(scn.GetDSCard("waters2"),scn.GetDSCard("site4"));
            assertTrue(scn.GetDSCard("waters1").getZone().isInPlay()); assertTrue(scn.GetDSCard("waters2").getZone().isInPlay());
            scn.SkipToDSTurn(Phase.DEPLOY);
            assertNotNull(scn.DSGetDecision()); assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("third")));
        }
    }
}
