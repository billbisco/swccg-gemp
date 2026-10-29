package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class ReflectionsGoldImperialCommandComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("destiny","1_116"); light.put("luke","1_19"); light.put("xwing","1_146");
        var dark=new HashMap<String,String>(); dark.put("combo","15_250"); dark.put("veers","3_087");
        var scn=new VirtualTableScenario(light,dark); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("combo")); return scn;
    }
    @Test public void ImperialCommandAndItsAnOlderCodePrintedMetadataMatchesRemaster() {
        var scn=scenario(); var card=scn.GetDSCard("combo").getBlueprint();
        assertEquals(4f,card.getDestiny(),scn.epsilon); assertEquals(CardSubtype.USED_OR_LOST,card.getCardSubtype()); assertEquals(Uniqueness.UNIQUE,card.getUniqueness());
        assertEquals(0,card.getCredits()); assertFalse(card.isHorizontal()); scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD)); scn.BlueprintKeywordCheck(card,List.of());
    }
    @Test public void ImperialCommandAndItsAnOlderCodeUploadsGeneralAndGoesUsed() {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var veers=scn.GetDSCard("veers");
        scn.SkipToDSTurn(Phase.DEPLOY); scn.MoveCardsToTopOfDSReserveDeck(veers);
        scn.DSPlayCard(combo,"Take card"); scn.PassAllResponses(); scn.DSChooseCard(veers); scn.PassAllResponses();
        assertTrue(scn.GetDSHand().contains(veers)); assertTrue(scn.GetDSUsedPile().contains(combo));
    }
    private void battle(boolean add) {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var site=scn.GetDSStartingLocation();
        if(!add) scn.MoveCardsToLSHand(scn.GetLSCard("destiny"));
        scn.MoveCardsToLocation(site,scn.GetDSCard("veers"),scn.GetLSCard("luke")); scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureDSForcePile(2);
        scn.DSInitiateBattle(site); scn.PassAllResponses(); int before=scn.GetDSBattleDestinyCount();
        if(!add) {
            scn.DSPass(); scn.LSPlayCard(scn.GetLSCard("destiny")); scn.PassAllResponses();
            assertEquals(2,scn.game().getModifiersQuerying().getNumBattleDestinyDraws(scn.gameState(),scn.LS,false,false));
            if(scn.LSGetDecision()!=null) scn.LSPass();
        }
        scn.DSPlayCard(combo,add?"Add one":"Limit opponent"); scn.PassAllResponses();
        if(add) assertEquals(before+1,scn.GetDSBattleDestinyCount());
        else {
            assertEquals(1,scn.game().getModifiersQuerying().getNumBattleDestinyDraws(scn.gameState(),scn.LS,true,false));
            assertEquals(1,scn.game().getModifiersQuerying().getNumBattleDestinyDraws(scn.gameState(),scn.LS,false,true));
        }
        assertTrue(scn.GetDSUsedPile().contains(combo));
    }
    @Test public void ImperialCommandAndItsAnOlderCodeAddsDestinyWithGeneralAtSiteAndGoesUsed() { battle(true); }
    @Test public void ImperialCommandAndItsAnOlderCodeLimitsOpponentsDestinyAndGoesUsed() { battle(false); }
    @Test public void ImperialCommandAndItsAnOlderCodeScansAndPutsStarshipUsedForTwoForceThenGoesLost() {
        var scn=scenario(); var combo=scn.GetDSCard("combo"); var xwing=scn.GetLSCard("xwing"); var luke=scn.GetLSCard("luke");
        scn.MoveCardsToLSHand(xwing,luke); scn.SkipToDSTurn(Phase.DEPLOY); scn.EnsureDSForcePile(2); int before=scn.GetDSForcePileCount();
        scn.DSPlayCard(combo,"'Scan'"); scn.PassAllResponses(); scn.DSChooseCards(); scn.DSChooseYes();
        scn.DSChooseCard(xwing); scn.PassAllResponses();
        assertTrue(scn.GetLSUsedPile().contains(xwing)); assertTrue(scn.GetLSHand().contains(luke));
        assertTrue(scn.GetDSLostPile().contains(combo)); assertEquals(before-2,scn.GetDSForcePileCount());
    }
}
