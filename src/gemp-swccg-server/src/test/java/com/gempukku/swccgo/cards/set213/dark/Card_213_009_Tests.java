package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_213_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
            new HashMap<>() {{
                put("han", "1_11");
            }},
            new HashMap<>() {{
                put("margo", "213_9");
            }},
            10,
            10,
            StartingSetup.DefaultLSGroundLocation,
            StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts,
            StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields,
            StartingSetup.NoDSShields,
            VirtualTableScenario.Open
        );
    }

    @Test
    public void MargoDrawDestinyResolvesWhenDestinyDrawFails() {
        //test1: the action resolves with no Reserve Deck to draw from
        //test2: the game continues in opponent's deploy phase
        var scn = GetScenario();

        var margo = scn.GetDSCard("margo");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, margo, han);

        scn.SkipToLSTurn(Phase.DEPLOY);
        while (scn.GetDSReserveDeckCount() > 0)
            scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSReserveDeck());
        int forcePileBefore = scn.GetDSForcePileCount();

        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(margo, "Draw Destiny"));
        scn.DSUseCardAction(margo, "Draw Destiny");
        scn.PassAllResponses();

        assertEquals(forcePileBefore - 1, scn.GetDSForcePileCount()); //test1
        assertTrue(scn.AwaitingLSDeployPhaseActions()); //test2
    }
}
