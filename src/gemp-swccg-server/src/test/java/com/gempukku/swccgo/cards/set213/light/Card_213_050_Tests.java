package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertTrue;

public class Card_213_050_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
            new HashMap<>() {{
                put("dice", "213_50");
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
    public void HansDiceResolvesWhenDestinyDrawFails() {
        //test1: Han's Dice resolves to the Used Pile with no Reserve Deck to draw from
        //test2: the battle continues
        var scn = GetScenario();

        var dice = scn.GetLSCard("dice");
        var han = scn.GetLSCard("han");
        var margo = scn.GetDSCard("margo");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, margo);
        scn.MoveCardsToLSHand(dice);

        scn.StartBattleAndSkipToWeaponsSegment();
        while (scn.GetLSReserveDeckCount() > 0)
            scn.MoveCardsToTopOfLSUsedPile(scn.GetTopOfLSReserveDeck());

        assertTrue(scn.LSCardPlayAvailable(dice));
        scn.LSPlayCardAndPassResponses(dice);
        scn.PassAllResponses();

        assertInZone(Zone.USED_PILE, dice); //test1
        assertTrue(scn.IsActiveBattle()); //test2
    }
}
