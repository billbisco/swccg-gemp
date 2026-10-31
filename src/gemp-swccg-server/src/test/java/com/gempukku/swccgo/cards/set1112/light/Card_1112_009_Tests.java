package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1112_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("village", "1112_9");
                    put("endor", "8_068");
                    put("ewok", "8_011");
                    put("chirpa", "8_003");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
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
    public void EndorEwokVillageStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("village").getBlueprint();

        assertEquals(Title.Ewok_Village, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.LIGHT_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.ENDOR);
            add(Icon.PLANET);
            add(Icon.INTERIOR_SITE);
            add(Icon.EXTERIOR_SITE);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void EndorEwokVillageMakesNonUniqueEwoksHeroicAndGainsLightForceWhileOccupied() {
        var scn = GetScenario();
        var village = scn.GetLSCard("village");
        var endor = scn.GetLSCard("endor");
        var ewok = scn.GetLSCard("ewok");
        var chirpa = scn.GetLSCard("chirpa");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(endor);
        scn.MoveLocationToTable(village);
        assertEquals(1, scn.GetIconCount(village, Icon.LIGHT_FORCE));
        scn.MoveCardsToLocation(village, ewok, chirpa);
        assertTrue(scn.HasKeyword(ewok, Keyword.HEROIC));
        assertFalse(scn.HasKeyword(chirpa, Keyword.HEROIC));
        assertEquals(2, scn.GetIconCount(village, Icon.LIGHT_FORCE));
        scn.MoveCardsToLocation(village, luke);
        assertEquals(2, scn.GetIconCount(village, Icon.LIGHT_FORCE));
    }
}
