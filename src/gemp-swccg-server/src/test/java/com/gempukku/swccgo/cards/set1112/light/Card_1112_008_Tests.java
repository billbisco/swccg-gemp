package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1112_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tactics", "1112_8");
                    put("xwing", "1_146");
                    put("luke", "1_19");
                    put("tatooine", "1_127");
                    put("red5", "2_071");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
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
    public void DiversionaryTacticsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("tactics").getBlueprint();

        assertEquals(Title.Diversionary_Tactics, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REFLECTIONS_III);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DiversionaryTacticsSubtractsOneHyperspeedFromAPilotedStarshipAtASystem() {
        var scn = GetScenario();
        var tactics = scn.GetLSCard("tactics");
        var xwing = scn.GetLSCard("xwing");
        var luke = scn.GetLSCard("luke");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        scn.BoardAsPilot(xwing, luke);
        scn.MoveCardsToLSHand(tactics);
        assertEquals(5, scn.GetHyperspeed(xwing));
        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.LSCardPlayAvailable(tactics, "Subtract 1 from hyperspeed"));
        scn.LSPlayCard(tactics, "Subtract 1 from hyperspeed");
        scn.LSChooseCard(xwing);
        scn.PassAllResponses();
        assertEquals(4, scn.GetHyperspeed(xwing));
        assertEquals(Zone.TOP_OF_USED_PILE, tactics.getZone());
    }

    @Test
    public void DiversionaryTacticsLostIsNotOfferedWithoutTwoUniqueRebelStarfightersInBattle() {
        var scn = GetScenario();
        var tactics = scn.GetLSCard("tactics");
        var xwing = scn.GetLSCard("xwing");
        var luke = scn.GetLSCard("luke");
        var tatooine = scn.GetLSCard("tatooine");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing, tie);
        scn.BoardAsPilot(xwing, luke);
        scn.MoveCardsToLSHand(tactics);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        assertFalse(scn.LSCardPlayAvailable(tactics, "Draw destiny to exclude a starship"));
    }
}
