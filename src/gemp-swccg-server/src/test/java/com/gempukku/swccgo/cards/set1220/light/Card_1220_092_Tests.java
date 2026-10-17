package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1220_092_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("webb", "1220_92");
                    put("pilot", "1_27");
                }},
                new HashMap<>() {{
                    put("evax", "1240_12");
                    put("site", "1_291");
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
    public void JeroenWebbStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("webb").getBlueprint();

        assertEquals("Jeroen Webb", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SPY);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.HOTH);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void JeroenWebbReducesOpponentTotalInfluenceForEachSpy() {
        var scn = GetScenario();
        var webb = scn.GetLSCard("webb");
        var pilot = scn.GetLSCard("pilot");
        var site = scn.GetDSCard("site");
        var evax = scn.GetDSCard("evax");

        scn.StartGame();
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, webb, evax);

        // Webb is one of your spies at a site, so opponent's total influence here is 1 - 1.
        assertEquals(0, scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, site), scn.epsilon);

        scn.MoveCardsToLocation(site, pilot);
        scn.ApplyAdHocModifier(new KeywordModifier(webb, pilot, Keyword.SPY));
        assertEquals(0, scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, site), scn.epsilon);
    }
}
