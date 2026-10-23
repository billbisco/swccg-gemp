package com.gempukku.swccgo.cards.set1210.light;

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

public class Card_1210_106_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("melas", "1210_106");
                    put("xwing", "1_146");
                    put("tatooine", "1_127");
                    put("spare", "1_3");
                    put("peek1", "1_17");
                    put("peek2", "1_19");
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
    public void MelasStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("melas").getBlueprint();

        assertEquals("Melas", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SMUGGLER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MelasAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var melas = scn.GetLSCard("melas");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        scn.BoardAsPilot(xwing, melas);
        assertEquals(5, scn.GetPower(xwing));
    }

    @Test
    public void MelasSmokesPipeForPowerAndDefenseValuePlusTwoAndMayNotMove() {
        var scn = GetScenario();
        var melas = scn.GetLSCard("melas");
        var spare = scn.GetLSCard("spare");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, melas);
        scn.MoveCardsToLSHand(spare, scn.GetLSCard("peek1"));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(2, scn.GetPower(melas));
        assertTrue(scn.LSCardActionAvailable(melas, "Smoke pipe"));
        scn.LSUseCardAction(melas, "Smoke pipe");
        scn.LSChooseCard(spare);
        scn.PassAllResponses();
        assertTrue(spare.getZone() == Zone.USED_PILE || spare.getZone() == Zone.TOP_OF_USED_PILE);
        assertEquals(4, scn.GetPower(melas));
        assertTrue(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(), melas));
    }

    @Test
    public void MelasMayPeekAtTopTwoCardsOfReserveDeckDuringDrawPhase() {
        var scn = GetScenario();
        var melas = scn.GetLSCard("melas");
        var peek1 = scn.GetLSCard("peek1");
        var peek2 = scn.GetLSCard("peek2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, melas);
        scn.SkipToLSTurn(Phase.DRAW);
        scn.MoveCardsToTopOfLSReserveDeck(peek2, peek1);
        assertTrue(scn.AwaitingLSDrawPhaseActions());
        assertTrue(scn.LSCardActionAvailable(melas, "Peek at top two cards of any Reserve Deck"));
        scn.LSUseCardAction(melas, "Peek at top two cards of any Reserve Deck");
        scn.LSChooseCard(peek1);
        assertTrue(scn.LSDecisionAvailable("Top card"));
        scn.LSDismissRevealedCards();
        scn.PassAllResponses();
    }
}
