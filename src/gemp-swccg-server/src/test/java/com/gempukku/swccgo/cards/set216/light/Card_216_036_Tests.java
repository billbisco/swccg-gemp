package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_216_036_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
            new HashMap<>() {{
                put("kenobi", "216_36");
                put("communing", "216_24");
                put("han", "1_11");
                put("luke", "1_19");
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
    public void MasterKenobiAddsThreeToTotalPowerWhileCommuning() {
        //test1: while 'communing', may add 3 to total power when a Rebel is in battle
        //test2: total power goes up by 3
        var scn = GetScenario();

        var kenobi = scn.GetLSCard("kenobi");
        var communing = scn.GetLSCard("communing");
        var han = scn.GetLSCard("han");
        var margo = scn.GetDSCard("margo");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(communing);
        scn.StackCardsOn(communing, kenobi);
        scn.MoveCardsToLocation(site, han, margo);

        scn.StartBattleAndSkipToWeaponsSegment();
        int powerBefore = scn.GetLSTotalPower();

        assertTrue(scn.LSCardActionAvailable(kenobi, "Add 3 to total power")); //test1
        scn.LSUseCardAction(kenobi, "Add 3 to total power");
        scn.PassAllResponses();

        assertEquals(powerBefore + 3, scn.GetLSTotalPower()); //test2
    }

    @Test
    public void MasterKenobiAddsFiveToTotalPowerWhileCommuningWithLuke() {
        var scn = GetScenario();

        var kenobi = scn.GetLSCard("kenobi");
        var communing = scn.GetLSCard("communing");
        var luke = scn.GetLSCard("luke");
        var margo = scn.GetDSCard("margo");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(communing);
        scn.StackCardsOn(communing, kenobi);
        scn.MoveCardsToLocation(site, luke, margo);

        scn.StartBattleAndSkipToWeaponsSegment();
        int powerBefore = scn.GetLSTotalPower();

        assertTrue(scn.LSCardActionAvailable(kenobi, "Add 5 to total power"));
        scn.LSUseCardAction(kenobi, "Add 5 to total power");
        scn.PassAllResponses();

        assertEquals(powerBefore + 5, scn.GetLSTotalPower());
    }

    @Test
    public void MasterKenobiCannotAddToTotalPowerWhenNotCommuning() {
        //test1: stacked somewhere other than Communing, Master Kenobi is not 'communing'
        //test2: so his total power action is not offered
        var scn = GetScenario();

        var kenobi = scn.GetLSCard("kenobi");
        var han = scn.GetLSCard("han");
        var margo = scn.GetDSCard("margo");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.StackCardsOn(site, kenobi);
        scn.MoveCardsToLocation(site, han, margo);

        scn.StartBattleAndSkipToWeaponsSegment();

        assertTrue(scn.IsStackedOn(site, kenobi)); //test1
        assertFalse(scn.LSCardActionAvailable(kenobi, "to total power")); //test2
    }
}
