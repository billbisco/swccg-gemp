package com.gempukku.swccgo.cards.set1240.dark;

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

public class Card_1240_036_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("try", "1240_36");
                    put("room", "1240_51");
                    put("emperor", "9_109");
                    put("evax", "1240_12");
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
    public void ThereIsNoTryStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("try").getBlueprint();

        assertEquals("There Is No Try & Oppressive Enforcement", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.REFLECTIONS_II);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.getGameText().startsWith("Deploy on Conference Room."));
        assertTrue(card.getGameText().contains("(Immune to Alter.)"));
    }

    @Test
    public void ThereIsNoTryMakesEmperorACouncilMemberWithInfluencePlusOne() {
        var scn = GetScenario();
        var effect = scn.GetDSCard("try");
        var room = scn.GetDSCard("room");
        var emperor = scn.GetDSCard("emperor");
        var evax = scn.GetDSCard("evax");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.AttachCardsTo(room, effect);
        scn.MoveCardsToLocation(room, emperor, evax);

        assertTrue(scn.HasKeyword(emperor, Keyword.IMPERIAL_COUNCIL_MEMBER));
        assertTrue(scn.HasKeyword(evax, Keyword.IMPERIAL_COUNCIL_MEMBER));
        assertEquals(1, scn.game().getModifiersQuerying().getInfluence(scn.gameState(), emperor), scn.epsilon);
        assertEquals(2, scn.GetIconCount(room, Icon.DARK_FORCE));
    }
}
