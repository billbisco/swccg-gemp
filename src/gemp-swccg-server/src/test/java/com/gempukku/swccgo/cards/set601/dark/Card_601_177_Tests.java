package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** 4-LOM With Concussion Rifle (V) (Legacy Block 6.2, 1262_1 / 601_177); text per the Virtual Cards Master List (2014) and card image. */
public class Card_601_177_Tests {
    private VirtualTableScenario scenario() {
        var light = new HashMap<String, String>();
        light.put("luke", "1_19");
        var dark = new HashMap<String, String>();
        dark.put("lom", "601_177");
        dark.put("trooper", "1_194");
        return new VirtualTableScenario(light, dark, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FourLomWithConcussionRifleVTextIsTheMasterListText() {
        var b = scenario().GetDSCard("lom").getBlueprint();
        assertEquals("4-LOM With Concussion Rifle", b.getTitle());
        assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide());
        assertEquals("Permanent weapon is •4-LOM's Concussion Rifle (may target a character for free; target is power -1). Once per turn, may place a card from hand on bottom of Used Pile to draw top card from Reserve Deck.", b.getGameText());
    }

    @Test
    public void ConcussionRifleTargetStaysPowerMinusOneAfterTheTurnEnds() {
        var scn = scenario();
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
        // No "until end of turn" on the official text: still -1 on the next turn.
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(2, scn.GetPower(luke));
    }
}
