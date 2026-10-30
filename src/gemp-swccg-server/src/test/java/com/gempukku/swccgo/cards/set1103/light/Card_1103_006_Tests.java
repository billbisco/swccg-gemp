package com.gempukku.swccgo.cards.set1103.light;

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
import static org.junit.Assert.assertTrue;

public class Card_1103_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("lajaie", "1103_6");
                    put("warroom", "1_139");
                    put("red3", "1_145");
                    put("biggs", "1_3");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                }},
                10, 10,
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
    public void CommanderEvramLajaieStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("lajaie").getBlueprint();

        assertEquals("Commander Evram Lajaie", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.COMMANDER);
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.A_NEW_HOPE);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LajaieAtYourWarRoomIsForfeitPlusThree() {
        var scn = GetScenario();
        var lajaie = scn.GetLSCard("lajaie");
        var warroom = scn.GetLSCard("warroom");

        scn.StartGame();
        scn.MoveLocationToTable(warroom);
        scn.MoveCardsToLocation(warroom, lajaie);
        assertEquals(8, scn.GetForfeit(lajaie));
    }

    @Test
    public void LajaieMakesYourPilotedStarshipsForfeitPlusOne() {
        var scn = GetScenario();
        var lajaie = scn.GetLSCard("lajaie");
        var red3 = scn.GetLSCard("red3");
        var biggs = scn.GetLSCard("biggs");
        var tatooine = scn.GetLSCard("tatooine");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, red3);
        scn.BoardAsPilot(red3, biggs);
        scn.MoveCardsToLocation(site, lajaie);
        assertEquals(6, scn.GetForfeit(red3));
    }

    @Test
    public void LajaieGrantsNavComputerToYourStarshipsWithHyperspeed() {
        var scn = GetScenario();
        var lajaie = scn.GetLSCard("lajaie");
        var red3 = scn.GetLSCard("red3");
        var biggs = scn.GetLSCard("biggs");
        var tatooine = scn.GetLSCard("tatooine");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, red3);
        scn.BoardAsPilot(red3, biggs);
        assertEquals(1, scn.GetIconCount(red3, Icon.NAV_COMPUTER));
        scn.MoveCardsToLocation(site, lajaie);
        assertEquals(2, scn.GetIconCount(red3, Icon.NAV_COMPUTER));
    }
}
