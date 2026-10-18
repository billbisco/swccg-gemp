package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.Agenda;
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

public class Card_1240_012_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("pilot", "1_27");
                    put("ywing", "1_147");
                }},
                new HashMap<>() {{
                    put("evax", "1240_12");
                    put("aloo", "1230_18");
                    put("commander", "1272_2");
                    put("room", "1240_51");
                    put("tatooine", "1_289");
                    put("dantooine", "1_282");
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
    public void OfficerEvaxStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("evax").getBlueprint();

        assertEquals("Officer Evax", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getInfluence(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.IMPERIAL_COUNCIL_MEMBER);
            add(Keyword.LEADER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.A_NEW_HOPE);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void OfficerEvaxTargetsStarshipOnlyWhenPowerIsLessThanInfluenceHere() {
        var scn = GetScenario();
        var evax = scn.GetDSCard("evax");
        var aloo = scn.GetDSCard("aloo");
        var commander = scn.GetDSCard("commander");
        var room = scn.GetDSCard("room");
        var tatooine = scn.GetDSCard("tatooine");
        var dantooine = scn.GetDSCard("dantooine");
        var ywing = scn.GetLSCard("ywing");
        var pilot = scn.GetLSCard("pilot");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(tatooine, ywing);
        scn.BoardAsPilot(ywing, pilot);

        // Y-wing power 2 plus Rebel Pilot's +2.
        assertEquals(4, scn.game().getModifiersQuerying().getPower(scn.gameState(), ywing), scn.epsilon);

        scn.MoveCardsToLocation(room, evax);
        scn.SkipToDSTurn(Phase.MOVE);
        assertTrue(scn.game().getModifiersQuerying().hasAgenda(scn.gameState(), evax, Agenda.LOGISTICS));
        assertEquals(1, scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, room), scn.epsilon);
        assertFalse(scn.DSCardActionAvailable(evax, "Make opponent use +2 Force"));

        scn.MoveCardsToLocation(room, aloo, commander);
        assertEquals(5, scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, room), scn.epsilon);
        // The open Move-phase decision was built at influence 1. Pass so the next window rebuilds actions.
        scn.DSPass();
        scn.LSPass();
        assertTrue("decision=" + scn.DSGetDecision(), scn.DSCardActionAvailable(evax, "Make opponent use +2 Force"));

        scn.DSUseCardAction(evax, "Make opponent use +2 Force");
        scn.DSChooseCard(ywing);
        scn.PassAllResponses();

        assertEquals(3, scn.game().getModifiersQuerying().getMoveUsingHyperspeedCost(scn.gameState(), ywing, tatooine, dantooine, false, 0), scn.epsilon);
        assertEquals(3, scn.game().getModifiersQuerying().getInitiateBattleCost(scn.gameState(), tatooine, scn.LS, true), scn.epsilon);
        assertEquals(1, scn.game().getModifiersQuerying().getInitiateBattleCost(scn.gameState(), tatooine, scn.DS, true), scn.epsilon);

        scn.MoveCardsToLocation(dantooine, ywing);
        assertEquals(1, scn.game().getModifiersQuerying().getInitiateBattleCost(scn.gameState(), tatooine, scn.LS, true), scn.epsilon);
        assertEquals(3, scn.game().getModifiersQuerying().getInitiateBattleCost(scn.gameState(), dantooine, scn.LS, true), scn.epsilon);
    }
}
