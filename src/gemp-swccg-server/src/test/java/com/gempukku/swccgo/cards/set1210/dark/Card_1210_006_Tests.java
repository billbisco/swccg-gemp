package com.gempukku.swccgo.cards.set1210.dark;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("han", "1_11");
                    put("xwing", "1_146");
                    put("sense", "1_85");
                }},
                new HashMap<>() {{
                    put("danz", "1210_6");
                    put("pilot", "1210_13");
                    put("avenger", "4_172");
                    put("slave", "5_177");
                    put("nalhutta", "6_168");
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
    public void DanzBorinStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("danz").getBlueprint();

        assertEquals("Danz Borin", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.GUNNER);
            add(Keyword.BOUNTY_HUNTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.A_NEW_HOPE);
            add(Icon.PILOT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DanzBorinAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var danz = scn.GetDSCard("danz");
        var slave = scn.GetDSCard("slave");
        var nalhutta = scn.GetDSCard("nalhutta");

        scn.StartGame();
        scn.MoveLocationToTable(nalhutta);
        scn.MoveCardsToLocation(nalhutta, slave);
        scn.BoardAsPilot(slave, danz);
        assertEquals(7, scn.GetPower(slave));
    }

    @Test
    public void DanzBorinIsMatchingPilotForIndependentStarship() {
        var scn = GetScenario();
        var danz = scn.GetDSCard("danz");
        var slave = scn.GetDSCard("slave");
        var nalhutta = scn.GetDSCard("nalhutta");

        scn.StartGame();
        scn.MoveLocationToTable(nalhutta);
        scn.MoveCardsToLocation(nalhutta, slave);
        scn.BoardAsPilot(slave, danz);
        assertTrue(scn.game().getModifiersQuerying().isMatchingPair(scn.gameState(), danz, slave));
    }

    @Test
    public void DanzBorinAddsOneToForceDrainsAtSameNalHuttaLocation() {
        var scn = GetScenario();
        var danz = scn.GetDSCard("danz");
        var slave = scn.GetDSCard("slave");
        var nalhutta = scn.GetDSCard("nalhutta");

        scn.StartGame();
        scn.MoveLocationToTable(nalhutta);
        scn.MoveCardsToLocation(nalhutta, slave);
        scn.BoardAsPilot(slave, danz);
        scn.EnsureLSForcePile(5);
        scn.SkipToDSTurn(Phase.CONTROL);
        int beforeLost = scn.GetLSLostPileCount();
        assertTrue(scn.DSForceDrainAvailable(nalhutta));
        scn.DSForceDrainAt(nalhutta);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(danz, "Add 1 to Force drain"));
        scn.DSUseCardAction(danz, "Add 1 to Force drain");
        scn.PassForceDrainStartResponses();
        // Nal Hutta has 1 Light Force icon; the optional add makes the drain 2.
        assertEquals(2, scn.GetForceDrainTotal());
        assertTrue(scn.LSDecisionAvailable("Choose Force to lose"));
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassForceDrainPendingResponses();
        assertTrue(scn.LSDecisionAvailable("Choose Force to lose"));
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassForceDrainPendingResponses();
        assertEquals(2, scn.GetLSLostPileCount() - beforeLost);
    }

    @Test
    public void DanzBorinWhilePilotingIndependentMayNotDrawMoreThanOneBattleDestiny() {
        var scn = GetScenario();
        var danz = scn.GetDSCard("danz");
        var pilot = scn.GetDSCard("pilot");
        var slave = scn.GetDSCard("slave");
        var avenger = scn.GetDSCard("avenger");
        var xwing = scn.GetLSCard("xwing");
        var han = scn.GetLSCard("han");
        var nalhutta = scn.GetDSCard("nalhutta");

        scn.StartGame();
        scn.MoveLocationToTable(nalhutta);
        scn.MoveCardsToLocation(nalhutta, slave, avenger, xwing);
        scn.BoardAsPilot(slave, danz);
        scn.BoardAsPilot(avenger, pilot);
        scn.BoardAsPilot(xwing, han);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(4);
        scn.DSInitiateBattle(nalhutta);
        scn.PassWeaponsSegmentActions();
        scn.SkipBattleDestinyDraws(true);
        assertEquals(4, scn.GetDSTotalDestiny(), scn.epsilon);
        assertFalse(scn.DSDecisionAvailable("battle destiny?"));
    }

    @Test
    public void DanzBorinBattleDestinyMayNotBeCanceledByOpponentWhilePilotingIndependent() {
        var scn = GetScenario();
        var danz = scn.GetDSCard("danz");
        var pilot = scn.GetDSCard("pilot");
        var slave = scn.GetDSCard("slave");
        var avenger = scn.GetDSCard("avenger");
        var xwing = scn.GetLSCard("xwing");
        var han = scn.GetLSCard("han");
        var nalhutta = scn.GetDSCard("nalhutta");

        scn.StartGame();
        scn.MoveLocationToTable(nalhutta);
        scn.MoveCardsToLocation(nalhutta, slave, avenger, xwing);
        scn.BoardAsPilot(slave, danz);
        scn.BoardAsPilot(avenger, pilot);
        scn.BoardAsPilot(xwing, han);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(nalhutta);
        scn.PassAllResponses();
        assertTrue(scn.game().getModifiersQuerying().mayNotCancelBattleDestinyDraws(
                scn.gameState(), scn.DS, scn.LS, false));
    }
}
