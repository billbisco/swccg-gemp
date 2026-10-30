package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1103_007_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("willard", "1103_7");
                    put("leia", "1_17");
                    put("luke", "1_19");
                    put("warroom", "1_139");
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
    public void CommanderVandenWillardStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("willard").getBlueprint();

        assertEquals("Commander Vanden Willard", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.COMMANDER);
            add(Keyword.LEADER);
            add(Keyword.SPY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.WARRIOR);
            add(Icon.A_NEW_HOPE);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WillardMakesLeiaDefenseValueAndForfeitPlusThree() {
        var scn = GetScenario();
        var willard = scn.GetLSCard("willard");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia);
        assertEquals(3, scn.GetDefense(leia));
        assertEquals(6, scn.GetForfeit(leia));
        scn.MoveCardsToLocation(site, willard);
        assertEquals(6, scn.GetDefense(leia));
        assertEquals(11, scn.GetForfeit(leia));
    }

    @Test
    public void WillardMakesCharactersAtYavin4ForfeitPlusTwo() {
        var scn = GetScenario();
        var willard = scn.GetLSCard("willard");
        var luke = scn.GetLSCard("luke");
        var warroom = scn.GetLSCard("warroom");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(warroom);
        scn.MoveCardsToLocation(warroom, luke);
        assertEquals(7, scn.GetForfeit(luke));
        scn.MoveCardsToLocation(site, willard);
        assertEquals(9, scn.GetForfeit(luke));
    }

    @Test
    public void WillardTakesLeiaIntoHandOncePerGame() {
        var scn = GetScenario();
        var willard = scn.GetLSCard("willard");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, willard);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(leia);
        assertTrue(scn.LSCardActionAvailable(willard, "Take Leia into hand from Reserve Deck"));
        scn.LSUseCardAction(willard, "Take Leia into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(leia);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, leia.getZone());
    }
}
