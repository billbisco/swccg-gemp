package com.gempukku.swccgo.cards.set1220.dark;

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

public class Card_1220_007_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("nemet", "1220_7");
                    put("devastator", "1_301");
                    put("tatooine", "1_289");
                    put("hoth", "3_143");
                    put("probe", "3_90");
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
    public void CommanderNemetStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("nemet").getBlueprint();

        assertEquals("Commander Nemet", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.COMMANDER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.DAGOBAH);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CommanderNemetAddsOneToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var nemet = scn.GetDSCard("nemet");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, nemet);
        assertEquals(10, scn.GetPower(devastator));
    }

    @Test
    public void CommanderNemetPlacesAsProbeDuringDeployPhaseWhilePilotingAtProbedSystem() {
        var scn = GetScenario();
        var nemet = scn.GetDSCard("nemet");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");
        var hoth = scn.GetDSCard("hoth");
        var probe = scn.GetDSCard("probe");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, nemet);
        scn.StackCardsOn(tatooine, probe);
        probe.setProbeCard(true);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(nemet, "Place as a 'probe' card"));
        scn.DSUseCardAction(nemet, "Place as a 'probe' card");
        scn.DSChooseCard(hoth);
        scn.PassAllResponses();
        assertTrue(nemet.isProbeCard());
        assertEquals(hoth, nemet.getStackedOn());
    }
}
