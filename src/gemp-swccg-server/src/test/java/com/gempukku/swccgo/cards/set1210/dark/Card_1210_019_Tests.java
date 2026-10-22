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
import static org.junit.Assert.assertTrue;

public class Card_1210_019_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("reserve1", "1210_19");
                    put("reserve2", "1210_19");
                    put("praji", "1_167");
                    put("avenger1", "4_172");
                    put("avenger2", "4_172");
                    put("devastator", "1_301");
                    put("tatooine", "1_289");
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
    public void ReservePilotStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("reserve1").getBlueprint();

        assertEquals("Reserve Pilot", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_2, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.A_NEW_HOPE);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ReservePilotAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var reserve1 = scn.GetDSCard("reserve1");
        var avenger1 = scn.GetDSCard("avenger1");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, avenger1);
        scn.BoardAsPilot(avenger1, reserve1);
        assertEquals(4, scn.GetPower(avenger1));
    }

    @Test
    public void ReservePilotCumulativelyAddsOneToTotalBattleDestinyWhilePilotingAtSystem() {
        var scn = GetScenario();
        var reserve1 = scn.GetDSCard("reserve1");
        var reserve2 = scn.GetDSCard("reserve2");
        var avenger1 = scn.GetDSCard("avenger1");
        var avenger2 = scn.GetDSCard("avenger2");
        var tatooine = scn.GetDSCard("tatooine");
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, avenger1, avenger2, xwing);
        scn.BoardAsPilot(avenger1, reserve1);
        scn.BoardAsPilot(avenger2, reserve2);
        scn.BoardAsPilot(xwing, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(3);
        scn.DSInitiateBattle(tatooine);
        scn.PassWeaponsSegmentActions();
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(5, scn.GetDSTotalDestiny(), scn.epsilon);
    }

    @Test
    public void ReservePilotDeploysFreeAboardStarshipThatForfeitedAPilotAfterSystemBattle() {
        var scn = GetScenario();
        var reserve1 = scn.GetDSCard("reserve1");
        var praji = scn.GetDSCard("praji");
        var avenger1 = scn.GetDSCard("avenger1");
        var tatooine = scn.GetDSCard("tatooine");
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, avenger1, xwing);
        scn.BoardAsPilot(avenger1, praji);
        scn.BoardAsPilot(xwing, luke);
        scn.MoveCardsToDSHand(reserve1);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(tatooine);
        scn.PassWeaponsSegmentActions();
        scn.SkipBattleDestinyDraws(false);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertTrue(scn.AwaitingDSBattleDamagePayment());
        scn.DSChooseCard(praji);
        scn.PassCardLeavingTable();
        scn.PassDamageSegmentActions();
        assertTrue(scn.DSDecisionAvailable("Deploy") || scn.DSDecisionAvailable("optional"));
        scn.DSChooseAction("Deploy");
        scn.PassAllResponses();
        assertEquals(avenger1, reserve1.getAttachedTo());
        assertTrue(reserve1.isPilotOf());
    }
}
