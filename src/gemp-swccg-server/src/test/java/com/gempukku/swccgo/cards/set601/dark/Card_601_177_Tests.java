package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** 4-LOM With Concussion Rifle (V) (Legacy Block 6.2, 1262_1 / 601_177): current text = PC forum errata 2014-03-27 ("until end of turn"); 2262_1 = release-sheet original. */
public class Card_601_177_Tests {
    private VirtualTableScenario scenario() { return scenario("601_177"); }
    private VirtualTableScenario scenario(String lomId) {
        var light = new HashMap<String, String>();
        light.put("luke", "1_19");
        var dark = new HashMap<String, String>();
        dark.put("lom", lomId);
        dark.put("trooper", "1_194");
        return new VirtualTableScenario(light, dark, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FourLomWithConcussionRifleVTextIsTheErrataText() {
        var b = scenario().GetDSCard("lom").getBlueprint();
        assertEquals("4-LOM With Concussion Rifle", b.getTitle());
        assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide());
        assertEquals("Permanent weapon is •4-LOM's Concussion Rifle (may target a character for free; target is power -1 until end of turn). Once per turn, may place a card from hand on bottom of Used Pile to draw top card from Reserve Deck.", b.getGameText());
        assertEquals("Permanent weapon is •4-LOM's Concussion Rifle (may target a character for free; target is power -1). Once per turn, may place a card from hand on bottom of Used Pile to draw top card from Reserve Deck.",
                scenario("2262_1").GetDSCard("lom").getBlueprint().getGameText());
    }

    @Test
    public void ConcussionRifleTargetIsPowerMinusOneUntilEndOfTurn() {
        fireAndCheckNextTurn("601_177", 3);
    }

    @Test
    public void OriginalVersionTargetStaysPowerMinusOneAfterTheTurnEnds() {
        fireAndCheckNextTurn("2262_1", 2);
    }

    private void fireAndCheckNextTurn(String lomId, int powerNextTurn) {
        var scn = scenario(lomId);
        var lom = scn.GetDSCard("lom");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, lom, scn.GetDSCard("trooper"), luke);
        assertEquals(3, scn.GetPower(luke));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        assertTrue(scn.DSCardActionAvailable(lom, "Fire"));
        scn.DSUseCardAction(lom, "Fire");
        if (scn.DSHasCardChoiceAvailable(luke))
            scn.DSChooseCard(luke);
        scn.PassAllResponses();
        assertEquals(2, scn.GetPower(luke));
        scn.SkipToDamageSegment(false);
        if (scn.AwaitingLSBattleDamagePayment()) {
            scn.LSPayRemainingBattleDamageFromReserveDeck();
            scn.PassAllResponses();
        }
        // Errata 2014-03-27 "until end of turn"; the original (2262_1) has no duration: still -1 on the next turn.
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(powerNextTurn, scn.GetPower(luke));
    }
}
