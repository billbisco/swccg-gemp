package com.gempukku.swccgo.cards.set1101.light;

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

public class Card_1101_005_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1101_5");
                    put("xwing", "1_146");
                    put("spin", "7_85");
                    put("lost", "1_17");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void LukeSkywalkerStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("luke").getBlueprint();

        assertEquals("Luke Skywalker", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LukeSkywalkerAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, xwing);
        scn.BoardAsPilot(xwing, luke);
        assertEquals(6, scn.GetPower(xwing));
    }

    @Test
    public void LukeSkywalkerRetrievesOneForceDuringControlPhaseIfPilotingAtABattleground() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");
        var lost = scn.GetLSCard("lost");
        var vader = scn.GetDSCard("vader");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, xwing, vader);
        scn.BoardAsPilot(xwing, luke);
        scn.MoveCardsToTopOfLSLostPile(lost);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(luke, "Retrieve 1 Force"));
        scn.LSUseCardAction(luke, "Retrieve 1 Force");
        scn.PassAllResponses();
        assertTrue(lost.getZone() == Zone.USED_PILE || lost.getZone() == Zone.TOP_OF_USED_PILE);
    }

    @Test
    public void LukeSkywalkerTakesDarklighterSpinFromReserveDeckDuringControlPhase() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");
        var spin = scn.GetLSCard("spin");
        var vader = scn.GetDSCard("vader");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, xwing, vader);
        scn.BoardAsPilot(xwing, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(spin);
        assertTrue(scn.LSCardActionAvailable(luke, "Take Darklighter Spin into hand from Reserve Deck"));
        scn.LSUseCardAction(luke, "Take Darklighter Spin into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(spin);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, spin.getZone());
    }
}
