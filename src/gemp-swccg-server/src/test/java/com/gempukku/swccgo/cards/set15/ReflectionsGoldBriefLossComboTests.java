package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldBriefLossComboTests {
    private VirtualTableScenario scenario() {
        var light = new HashMap<String, String>(); light.put("luke", "1_19"); light.put("leia", "1_17");
        var dark = new HashMap<String, String>(); dark.put("combo", "15_225"); dark.put("vader", "1_168");
        var scn = new VirtualTableScenario(light, dark);
        scn.StartGame();
        scn.MoveCardsToHand(scn.GetDSCard("combo"));
        scn.EnsureDSForcePile(6);
        return scn;
    }
    private void activation(int amount) {
        var scn = scenario();
        var combo = scn.GetDSCard("combo");
        scn.SkipToDSTurn(Phase.CONTROL);
        for (int i = 1; i <= 5; i++) scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSFiller(i));
        assertTrue(scn.GetDSReserveDeckCount() >= 4);
        int before = scn.GetDSForcePileCount();
        scn.DSPlayCard(combo, "Activate " + amount + " Force");
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSForceLossPayment());
        scn.DSChooseCard(scn.GetTopOfDSForcePile());
        scn.PassAllResponses();
        assertEquals(before - 1 + amount, scn.GetDSForcePileCount());
        if (amount == 2) assertTrue(scn.GetDSUsedPile().contains(combo));
        else assertTrue(scn.GetDSLostPile().contains(combo));
    }
    @Test public void BriefLossOfControlAndImperialSupplyUsedLosesOneToActivateTwo() { activation(2); }
    @Test public void BriefLossOfControlAndImperialSupplyLostLosesOneToActivateFour() { activation(4); }
    @Test public void BriefLossOfControlAndImperialSupplyLostPreventsBothCharactersAbilityAfterWeapons() {
        var scn = scenario();
        var combo = scn.GetDSCard("combo");
        var vader = scn.GetDSCard("vader"); var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, vader, luke, scn.GetLSCard("leia"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        int force = scn.GetDSForcePileCount();
        scn.PassWeaponsSegmentActions();
        assertTrue(scn.DSCardPlayAvailable(combo, "Target characters"));
        scn.DSPlayCard(combo, "Target characters");
        scn.DSChooseCard(vader);
        scn.LSChooseCard(luke);
        scn.PassAllResponses();
        assertEquals(force - 2, scn.GetDSForcePileCount());
        assertTrue(scn.game().getModifiersQuerying().cannotApplyAbilityForBattleDestiny(scn.gameState(), vader));
        assertTrue(scn.game().getModifiersQuerying().cannotApplyAbilityForBattleDestiny(scn.gameState(), luke));
        assertFalse(scn.game().getModifiersQuerying().cannotApplyAbilityForBattleDestiny(scn.gameState(), scn.GetLSCard("leia")));
        assertTrue(scn.GetDSLostPile().contains(combo));
    }
}
