package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.common.CardSubtype;
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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_050_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("dungeon", "1230_50");
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
    public void DungeonStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("dungeon").getBlueprint();
        assertEquals("Dungeon (Prison)", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.LOCATION); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.PRISON); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EXTERIOR_SITE);
            add(Icon.INTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DungeonDeploysToTatooine() {
        var scn = GetScenario();
        var dungeon = scn.GetDSCard("dungeon");
        var tatooine = scn.GetDSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToDSHand(dungeon);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(dungeon));
        scn.DSPlayCard(dungeon);
        if (scn.DSHasCardChoiceAvailable(tatooine)) {
            scn.DSChooseCard(tatooine);
        }
        if (scn.DSDecisionAvailable("On which side")) {
            scn.DSChoose("Left");
        }
        scn.PassAllResponses();
        assertTrue(dungeon.getZone() == Zone.AT_LOCATION || dungeon.getZone() == Zone.LOCATIONS);
    }
}
