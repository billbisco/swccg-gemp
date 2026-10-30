package com.gempukku.swccgo.cards.set1102.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_1102_026_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("motti", "1102_26");
                    put("isd", "1_302");
                    put("devastator", "1_301");
                    put("tatooine", "1_289");
                    put("dantooine", "1_282");
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
    public void AdmiralMottiStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("motti").getBlueprint();

        assertEquals("Admiral Motti", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
            add(Keyword.ADMIRAL);
            add(Keyword.COMMANDER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AdmiralMottiAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var isd = scn.GetDSCard("isd");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, isd);
        scn.BoardAsPilot(isd, motti);
        assertEquals(10, scn.GetPower(isd));
    }

    @Test
    public void AdmiralMottiMakesImperialClassStarDestroyersDeployMinusTwoToSameSystem() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var isd = scn.GetDSCard("isd");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");
        var dantooine = scn.GetDSCard("dantooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, motti);

        assertEquals(6, deployCostTo(scn, isd, tatooine), scn.epsilon);
        assertEquals(8, deployCostTo(scn, isd, dantooine), scn.epsilon);
    }

    private float deployCostTo(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card,
                               com.gempukku.swccgo.game.PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
