package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class ReflectionsGoldEstablishBaseComboTests {
    private VirtualTableScenario scenario() {
        var light=new HashMap<String,String>(); light.put("xwing","1_146"); light.put("luke","1_19"); light.put("rebel1","1_028"); light.put("rebel2","1_028");
        var dark=new HashMap<String,String>(); dark.put("combo","15_179"); dark.put("endor","8_157");
        dark.put("third","8_158"); dark.put("forest","8_165"); dark.put("clearing","8_164"); dark.put("scout1","8_092"); dark.put("scout2","8_092");
        dark.put("ship","4_167"); dark.put("system1","1_282"); dark.put("system2","1_289"); dark.put("ship2","8_168");
        var scn=new VirtualTableScenario(light,dark); scn.StartGame();
        for(String name:List.of("endor","forest","clearing","system1","system2")) scn.MoveLocationToTable(scn.GetDSCard(name));
        scn.MoveCardsToDSHand(scn.GetDSCard("combo")); scn.SkipToDSTurn(Phase.DEPLOY);
        return scn;
    }
    private void deploy(VirtualTableScenario scn) {
        int force=scn.GetDSForcePileCount();
        scn.DSPlayCardAndPassResponses(scn.GetDSCard("combo"),scn.GetDSCard("endor"));
        assertEquals(force,scn.GetDSForcePileCount());
    }
    @Test public void EstablishSecretBaseAndOminousRumorsHasPrintedMetadata() {
        var scn=scenario(); var card=scn.GetDSCard("combo").getBlueprint();
        assertEquals(3f,card.getDestiny(),scn.epsilon); assertEquals(Uniqueness.UNIQUE,card.getUniqueness());
        assertEquals(0,card.getCredits()); assertEquals(Rarity.UNKNOWN,card.getRarity());
        scn.BlueprintIconCheck(card,List.of(Icon.EFFECT,Icon.REFLECTIONS_GOLD));
        scn.BlueprintKeywordCheck(card,List.of(Keyword.DEPLOYS_ON_LOCATION));
        assertEquals(List.of("Establish Secret Base","Ominous Rumors"),card.getTitles());
    }
    @Test public void EstablishSecretBaseAndOminousRumorsDeploysWithoutSitesAndBonusesRequireTwoScoutControlledBattlegrounds() {
        var scn=scenario(); deploy(scn); var system=scn.GetDSCard("endor"); var combo=scn.GetDSCard("combo");
        var query=scn.game().getModifiersQuerying();
        assertFalse(query.isImmuneToCardTitle(scn.gameState(),combo,Title.Alter));
        float before=query.getForceDrainAmount(scn.gameState(),system,scn.DS);
        scn.MoveCardsToLocation(scn.GetDSCard("forest"),scn.GetDSCard("scout1"));
        assertEquals(before,query.getForceDrainAmount(scn.gameState(),system,scn.DS),scn.epsilon);
        scn.MoveCardsToLocation(scn.GetDSCard("clearing"),scn.GetDSCard("scout2"));
        assertEquals(before+2,query.getForceDrainAmount(scn.gameState(),system,scn.DS),scn.epsilon);
        scn.MoveCardsToLocation(scn.GetDSCard("clearing"),scn.GetLSCard("luke"));
        assertEquals(before,query.getForceDrainAmount(scn.gameState(),system,scn.DS),scn.epsilon);
    }
    @Test public void EstablishSecretBaseAndOminousRumorsGoesUsedWhenOpponentControlsEndor() {
        var scn=scenario(); deploy(scn); var combo=scn.GetDSCard("combo");
        scn.MoveCardsToLocation(scn.GetDSCard("endor"),scn.GetLSCard("xwing"));
        scn.SkipToPhase(Phase.BATTLE); scn.PassAllResponses();
        assertTrue(scn.GetDSUsedPile().contains(combo));
    }
    @Test public void EstablishSecretBaseAndOminousRumorsAddsOneToOnlyOneOtherDrainWithOneOccupiedEndorSite() {
        var scn=scenario(); deploy(scn); var combo=scn.GetDSCard("combo");
        scn.MoveCardsToLocation(scn.GetDSCard("forest"),scn.GetDSCard("scout1"));
        scn.MoveCardsToLocation(scn.GetDSCard("system1"),scn.GetDSCard("ship"));
        scn.MoveCardsToLocation(scn.GetDSCard("system2"),scn.GetDSCard("ship2"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSFiller(1),scn.GetLSFiller(2),scn.GetLSFiller(3)); scn.DSForceDrainAt(scn.GetDSCard("system1"));
        if(scn.LSGetDecision()!=null) scn.LSPass();
        scn.DSUseCardAction(combo,"Add 1"); scn.PassAllResponses();
        assertEquals(2,scn.GetForceDrainTotal());
        scn.LSPayRemainingForceLossFromReserveDeck(); scn.PassAllResponses();
        if(scn.LSGetDecision()!=null) scn.LSPass();
        scn.DSForceDrainAt(scn.GetDSCard("system2")); scn.PassAllResponses();
        assertEquals(1,scn.GetForceDrainTotal());
        assertTrue(scn.AwaitingLSForceLossPayment());
    }
    @Test public void EstablishSecretBaseAndOminousRumorsGoesUsedAtThreeOpponentControlledEndorSites() {
        var scn=scenario(); deploy(scn); var combo=scn.GetDSCard("combo");
        scn.MoveLocationToTable(scn.GetDSCard("third"));
        scn.MoveCardsToLocation(scn.GetDSCard("forest"),scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetDSCard("clearing"),scn.GetLSCard("rebel1"));
        scn.SkipToPhase(Phase.BATTLE); assertTrue(combo.getZone().isInPlay());
        scn.MoveCardsToLocation(scn.GetDSCard("third"),scn.GetLSCard("rebel2"));
        scn.SkipToPhase(Phase.MOVE); scn.PassAllResponses();
        assertTrue(scn.GetDSUsedPile().contains(combo));
    }
}
