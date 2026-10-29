package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldDarkBattleComboTests {
    private void haveYouNow(String rebelId, boolean darkJedi, int bonus) {
        var light = new HashMap<String, String>();
        light.put("rebel", rebelId);
        var dark = new HashMap<String, String>();
        dark.put("combo", "15_247");
        dark.put("fighter", darkJedi ? "1_168" : "1_179");
        var scn = new VirtualTableScenario(light, dark);
        scn.StartGame();
        var combo = scn.GetDSCard("combo");
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(site, scn.GetLSCard("rebel"), scn.GetDSCard("fighter"));
        scn.EnsureDSForcePile(3);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertFalse(scn.DSCardPlayAvailable(combo));
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        int before = scn.GetDSBattleDestinyCount();
        if (bonus == 0) { assertFalse(scn.DSCardPlayAvailable(combo)); return; }
        assertTrue(scn.DSCardPlayAvailable(combo));
        scn.DSPlayCard(combo);
        scn.DSChooseCard(scn.GetDSCard("fighter"));
        scn.DSChooseCard(scn.GetLSCard("rebel"));
        scn.PassAllResponses();
        assertEquals(before + bonus, scn.GetDSBattleDestinyCount());
        assertTrue(scn.GetDSLostPile().contains(combo));
    }
    @Test public void IHaveYouNowAndYourPowersAreWeakAddsOneForLeia() { haveYouNow("1_17", true, 1); }
    @Test public void IHaveYouNowAndYourPowersAreWeakAddsTwoForLuke() { haveYouNow("1_19", true, 2); }
    @Test public void IHaveYouNowAndYourPowersAreWeakAddsThreeForObiWan() { haveYouNow("1_21", true, 3); }
    @Test public void IHaveYouNowAndYourPowersAreWeakRequiresDarkJedi() { haveYouNow("1_19", false, 0); }

    private void collaboration(boolean companion, boolean leia, int bonus) {
        var light = new HashMap<String, String>();
        light.put("leia", "1_17");
        light.put("luke", "1_19");
        var dark = new HashMap<String, String>();
        dark.put("combo", "15_230");
        dark.put("tarkin", "1_179");
        dark.put("motti", "1_164");
        var scn = new VirtualTableScenario(light, dark);
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        var combo = scn.GetDSCard("combo");
        scn.MoveCardsToHand(combo);
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("tarkin"));
        if (companion) scn.MoveCardsToLocation(site, scn.GetDSCard("motti"));
        if (leia) scn.MoveCardsToLocation(site, scn.GetLSCard("leia"));
        scn.EnsureDSForcePile(3);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        int before = scn.GetDSBattleDestinyCount();
        if (bonus == 0) { assertFalse(scn.DSCardPlayAvailable(combo)); return; }
        scn.DSPlayCard(combo);
        scn.DSChooseCard(scn.GetDSCard("tarkin"));
        scn.DSChooseCard(scn.GetDSCard("motti"));
        if (leia) scn.DSChooseCard(scn.GetLSCard("leia"));
        scn.PassAllResponses();
        assertEquals(before + bonus, scn.GetDSBattleDestinyCount());
        assertTrue(scn.GetDSLostPile().contains(combo));
    }
    @Test public void DarkCollaborationAndCharmingToTheLastAddsTwoWithMotti() { collaboration(true, false, 2); }
    @Test public void DarkCollaborationAndCharmingToTheLastAddsFourWithLeia() { collaboration(true, true, 4); }
    @Test public void DarkCollaborationAndCharmingToTheLastRequiresOfficer() { collaboration(false, true, 0); }
}
