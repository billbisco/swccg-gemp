package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldProblemChokeComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("luke","1_19"); light.put("xwing","1_146");
        var dark=new HashMap<String,String>(); dark.put("combo","15_257"); dark.put("vader","1_168"); dark.put("ship","4_167"); dark.put("system","1_282");
        var scn=new VirtualTableScenario(light,dark); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("combo")); return scn;
    }
    private void choke(int destiny) {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var luke=scn.GetLSCard("luke"); var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,scn.GetDSCard("vader"),luke); scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureDSForcePile(3);
        scn.DSInitiateBattle(site); scn.SkipToDamageSegment(false);
        if(scn.LSGetDecision()!=null) scn.LSPass();
        scn.PrepareDSDestiny(destiny); int force=scn.GetDSForcePileCount();
        scn.DSPlayCard(combo,"Choke"); scn.DSChooseCard(luke); scn.PassAllResponses();
        assertEquals(force-1,scn.GetDSForcePileCount()); assertTrue(scn.GetDSUsedPile().contains(combo));
        assertEquals(destiny>4,scn.GetLSLostPile().contains(luke));
    }
    @Test public void IveGotAProblemHereAndPhysicalChokeLosesCharacterWhenDestinyExceedsAbility() { choke(5); }
    @Test public void IveGotAProblemHereAndPhysicalChokeDoesNotLoseCharacterAtEqualAbility() { choke(4); }
    @Test public void IveGotAProblemHereAndPhysicalChokeTargetsStarfighterAfterBattleAndGoesUsed() {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var system=scn.GetDSCard("system"); var xwing=scn.GetLSCard("xwing");
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system,scn.GetDSCard("ship"),xwing);
        scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureDSForcePile(3);
        for(int i=1;i<=10;i++) scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSFiller(i));
        scn.DSInitiateBattle(system); scn.SkipToDamageSegment(false); scn.PassAllResponses();
        scn.LSPayRemainingBattleDamageFromReserveDeck(); scn.PassDamageSegmentActions();
        if(scn.LSGetDecision()!=null) scn.LSPass();
        scn.PrepareDSDestiny(5); int force=scn.GetDSForcePileCount(); scn.DSPlayCard(combo); scn.DSChooseCard(xwing);
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(xwing)); assertTrue(scn.GetDSUsedPile().contains(combo)); assertEquals(force-1,scn.GetDSForcePileCount());
    }
}
