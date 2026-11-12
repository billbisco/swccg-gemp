package com.gempukku.swccgo.cards.set105.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Boba Fett, First Anthology white-border preview (May 1997). Printed: "Adds 2 to power and 1 to maneuver of any
 * starship he pilots." (Special Edition 7_167 later printed "anything he pilots"). Image: wiki.swccg.com
 * File:FA-P-D-bobafett.gif (Decipher.com First Anthology page).
 */
public class Card_105_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("boba", "105_4");
                    put("sebobba", "7_167"); // Special Edition Boba Fett, for comparison
                    put("tiescout", "1_305");
                    put("cloudcar", "5_178");
                }},
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
    public void BobaFettFirstAnthologyStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("boba").getBlueprint();

        assertEquals("Boba Fett", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(5, card.getArmor(), scn.epsilon);
        assertEquals(ExpansionSet.FIRST_ANTHOLOGY, card.getExpansionSet());
        assertEquals(Rarity.PV, card.getRarity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BOUNTY_HUNTER);
        }});
    }

    @Test
    public void BobaFettFirstAnthologyAddsPowerAndManeuverToStarshipHePilots() {
        var scn = GetScenario();
        var boba = scn.GetDSCard("boba");
        var tiescout = scn.GetDSCard("tiescout");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tiescout);
        int power = scn.GetPower(tiescout);
        int maneuver = scn.GetManeuver(tiescout);
        scn.BoardAsPilot(tiescout, boba);
        assertEquals(power + 2, scn.GetPower(tiescout));
        assertEquals(maneuver + 1, scn.GetManeuver(tiescout));
    }

    @Test
    public void BobaFettFirstAnthologyDoesNotAddPowerOrManeuverToVehicleHePilots() {
        var scn = GetScenario();
        var boba = scn.GetDSCard("boba");
        var cloudcar = scn.GetDSCard("cloudcar");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, cloudcar);
        int power = scn.GetPower(cloudcar);
        int maneuver = scn.GetManeuver(cloudcar);
        scn.BoardAsPilot(cloudcar, boba);
        assertEquals(power, scn.GetPower(cloudcar));
        assertEquals(maneuver, scn.GetManeuver(cloudcar));
    }

    @Test
    public void BobaFettSpecialEditionStillAddsPowerAndManeuverToVehicleHePilots() {
        var scn = GetScenario();
        var sebobba = scn.GetDSCard("sebobba");
        var cloudcar = scn.GetDSCard("cloudcar");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, cloudcar);
        int power = scn.GetPower(cloudcar);
        int maneuver = scn.GetManeuver(cloudcar);
        scn.BoardAsPilot(cloudcar, sebobba);
        assertEquals(power + 2, scn.GetPower(cloudcar));
        assertEquals(maneuver + 1, scn.GetManeuver(cloudcar));
    }
}
