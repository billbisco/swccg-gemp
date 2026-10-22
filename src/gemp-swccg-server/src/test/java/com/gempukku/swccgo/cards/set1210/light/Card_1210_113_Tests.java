package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
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

public class Card_1210_113_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("theron", "1210_113");
                    put("red10", "7_145");
                    put("red3", "1_145");
                    put("r2", "2_14");
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
    public void TheronNettStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("theron").getBlueprint();

        assertEquals("Theron Nett", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.RED_SQUADRON);
            add(Keyword.SMUGGLER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TheronAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var theron = scn.GetLSCard("theron");
        var red10 = scn.GetLSCard("red10");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, red10);
        scn.BoardAsPilot(red10, theron);
        assertEquals(5, scn.GetPower(red10));
    }

    @Test
    public void TheronPilotingRed10DeploysAstromechAboardYourStarshipFromReserveDeck() {
        var scn = GetScenario();
        var theron = scn.GetLSCard("theron");
        var red10 = scn.GetLSCard("red10");
        var red3 = scn.GetLSCard("red3");
        var r2 = scn.GetLSCard("r2");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, red10, red3);
        scn.BoardAsPilot(red10, theron);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(r2);
        assertTrue(scn.LSCardActionAvailable(theron, "Deploy astromech or matching pilot aboard"));
        scn.LSUseCardAction(theron, "Deploy astromech or matching pilot aboard");
        scn.PassAllResponses();
        scn.LSChooseCard(red3);
        scn.PassAllResponses();
        scn.LSChooseCard(r2);
        scn.PassAllResponses();
        assertEquals(red3, r2.getAttachedTo());
    }
}
