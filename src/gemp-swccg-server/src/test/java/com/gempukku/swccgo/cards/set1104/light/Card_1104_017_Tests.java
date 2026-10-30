package com.gempukku.swccgo.cards.set1104.light;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1104_017_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("romas", "1104_17");
                    put("xwing", "1_146");
                    put("tatooine", "1_127");
                    put("echo", "3_57");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void RomasLockNavanderStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("romas").getBlueprint();

        assertEquals("Romas 'Lock' Navander", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.HOTH);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RomasLockNavanderAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var romas = scn.GetLSCard("romas");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        scn.BoardAsPilot(xwing, romas);
        assertEquals(xwing.getBlueprint().getPower().intValue() + 2, scn.GetPower(xwing));
    }

    @Test
    public void RomasLockNavanderDeploysABattlegroundSystemFromReserveWhenPresentWithAScompLink() {
        var scn = GetScenario();
        var romas = scn.GetLSCard("romas");
        var echo = scn.GetLSCard("echo");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(echo);
        scn.MoveCardsToLocation(echo, romas);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(tatooine);
        assertTrue(scn.LSCardActionAvailable(romas, "Deploy battleground system"));
        scn.LSUseCardAction(romas, "Deploy battleground system");
        chooseFromSearch(scn, tatooine);
        scn.PassAllResponses();
        assertTrue(tatooine.getZone().isInPlay());
    }

    @Test
    public void RomasLockNavanderSuspendsHothEnergyShieldRulesAtEchoCommandCenter() {
        var scn = GetScenario();
        var romas = scn.GetLSCard("romas");
        var echo = scn.GetLSCard("echo");

        scn.StartGame();
        scn.MoveLocationToTable(echo);
        scn.MoveCardsToLocation(echo, romas);
        scn.EnsureLSForcePile(2);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(romas, "Suspend Hoth Energy Shield Rules"));
        scn.LSUseCardAction(romas, "Suspend Hoth Energy Shield Rules");
        scn.PassAllResponses();
        assertTrue(scn.game().getModifiersQuerying().hasFlagActive(
                scn.gameState(), ModifierFlag.HOTH_ENERGY_SHIELD_RULES_SUSPENDED));
    }

    private void chooseFromSearch(VirtualTableScenario scn, PhysicalCardImpl card) {
        int safety = 0;
        while (safety++ < 16 && !scn.LSHasCardChoiceAvailable(card)) {
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else {
                break;
            }
        }
        scn.LSChooseCard(card);
    }
}
