package com.gempukku.swccgo.cards.set105.light;

import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Commander Wedge Antilles, First Anthology white-border preview (May 1997). Printed: "Adds 3 to power of any starship
 * he pilots." (Special Edition 7_12 later printed "anything he pilots"). Image: wiki.swccg.com
 * File:FA-P-L-commanderwedgeantilles.gif (Decipher.com First Anthology page).
 */
public class Card_105_001_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("wedge", "105_1");
                    put("xwing", "1_146");
                    put("snowspeeder", "3_69");
                    put("tatooine", "1_127");
                }},
                new HashMap<>(),
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes"
        );
    }

    @Test
    public void CommanderWedgeAntillesFirstAnthologyStatsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("wedge").getBlueprint();

        assertEquals("Commander Wedge Antilles", card.getTitle());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.FIRST_ANTHOLOGY, card.getExpansionSet());
        assertEquals(Rarity.PV, card.getRarity());
        assertEquals("Promoted to commander for his heroism in the Battle of Yavin. Leader in charge of training new pilots assigned to Echo Base. Piloted Rogue 3 in the Battle of Hoth.", card.getLore());
    }

    @Test
    public void CommanderWedgeAntillesFirstAnthologyAddsThreeToPowerOfStarshipHePilots() {
        var scn = GetScenario();
        var wedge = scn.GetLSCard("wedge");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        int power = scn.GetPower(xwing);
        scn.BoardAsPilot(xwing, wedge);
        assertEquals(power + 3, scn.GetPower(xwing));
    }

    @Test
    public void CommanderWedgeAntillesFirstAnthologyDoesNotAddPowerToVehicleHePilots() {
        var scn = GetScenario();
        var wedge = scn.GetLSCard("wedge");
        var snowspeeder = scn.GetLSCard("snowspeeder");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, snowspeeder);
        int power = scn.GetPower(snowspeeder);
        scn.BoardAsPilot(snowspeeder, wedge);
        assertEquals(power, scn.GetPower(snowspeeder));
    }
}
