package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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

/**
 * The Light Side text ("attempts to 'disable' the Planetary Shield are +2 for each Rogue Squadron pilot here") is
 * play-tested in Card_1273_031_Tests.
 */
public class Card_1273_040_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("powerplant", "1273_40");
                    put("coruscant", "1273_41");
                }},
                new HashMap<>(),
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
    public void CoruscantMainPowerPlantStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("powerplant").getBlueprint();

        assertEquals("Coruscant: Main Power Plant", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.SCOMP_LINK);
            add(Icon.LIGHT_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CoruscantMainPowerPlantDarkSideGeneratesNoForceHereWhileCenterOfTyrannyOnTable() {
        var scn = GetScenario();
        var powerplant = scn.GetLSCard("powerplant");
        var coruscant = scn.GetLSCard("coruscant");

        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.MoveLocationToTable(powerplant);
        assertEquals(1, scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), powerplant, scn.DS), scn.epsilon);
        assertEquals(1, scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), powerplant, scn.LS), scn.epsilon);

        Vb73LightTestSupport.putOnTable(scn, scn.LS, "1273_42");
        assertEquals(0, scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), powerplant, scn.DS), scn.epsilon);
        assertEquals(1, scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), powerplant, scn.LS), scn.epsilon);
    }
}
