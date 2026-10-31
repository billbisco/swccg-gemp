package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1109_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1109_13");
                    put("forest", "8_72");
                    put("filler", "1_5");
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
    public void LukeSkywalkerRebelScoutStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("luke").getBlueprint();
        assertEquals("Luke Skywalker, Rebel Scout", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(6, card.getAbility(), scn.epsilon);
        assertEquals(9, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SCOUT);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.LUKE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.DEATH_STAR_II);
            add(Icon.REFLECTIONS_II);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LukeSkywalkerRebelScoutDeploysMinusTwoToAForest() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var forest = scn.GetLSCard("forest");
        scn.StartGame();
        scn.MoveLocationToTable(forest);
        scn.MoveCardsToLSHand(luke);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(6);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSDeployAvailable(luke));
        scn.LSDeployCard(luke);
        scn.LSChooseCard(forest);
        scn.PassAllResponses();
        assertAtLocation(forest, luke);
        assertEquals(force - 4, scn.GetLSForcePileCount());
    }

    @Test
    public void LukeSkywalkerRebelScoutIsImmuneToAttritionLessThanFive() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var forest = scn.GetLSCard("forest");
        scn.StartGame();
        scn.MoveLocationToTable(forest);
        scn.MoveCardsToLocation(forest, luke);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), luke), scn.epsilon);
    }
}
