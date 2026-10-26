package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class Card_211_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
            new HashMap<>() {{
                put("corvette", "1_140");
                put("han", "1_11");
            }},
            new HashMap<>() {{
                put("poldin", "211_4");
                put("tie", "209_52");
            }},
            10,
            10,
            StartingSetup.DefaultLSSpaceSystem,
            StartingSetup.DefaultDSSpaceSystem,
            StartingSetup.NoLSStartingInterrupts,
            StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields,
            StartingSetup.NoDSShields,
            VirtualTableScenario.Open
        );
    }

    @Test
    public void LtPoldinLehuseDestinyAgainstCapitalResolvesWhenDestinyDrawFails() {
        //test1: the action resolves with no Reserve Deck to draw from
        //test2: the battle continues
        var scn = GetScenario();

        var poldin = scn.GetDSCard("poldin");
        var tie = scn.GetDSCard("tie");
        var corvette = scn.GetLSCard("corvette");
        var han = scn.GetLSCard("han");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, tie, corvette);
        scn.BoardAsPilot(tie, poldin);
        scn.BoardAsPilot(corvette, han);

        scn.DSStartBattleAndSkipToWeaponsSegment(system);
        while (scn.GetDSReserveDeckCount() > 0)
            scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSReserveDeck());

        assertTrue(scn.DSCardActionAvailable(poldin, "Draw destiny against opponent's capital"));
        scn.DSUseCardAction(poldin, "Draw destiny against opponent's capital");
        scn.DSChooseCard(system);
        scn.DSChooseCard(corvette);
        scn.PassAllResponses();

        assertTrue(scn.IsActiveBattle()); //test1 //test2
        assertTrue(scn.AwaitingLSWeaponsSegmentActions()); //test2
    }
}
