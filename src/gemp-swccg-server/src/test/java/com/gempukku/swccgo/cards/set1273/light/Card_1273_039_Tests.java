package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_039_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("lowerlevels", "1273_39");
                    put("coruscant", "1273_41");
                    put("rebel", "1_028");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void CoruscantLowerLevelsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("lowerlevels").getBlueprint();

        assertEquals("Coruscant: Lower Levels", card.getTitle());
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
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CoruscantLowerLevelsForceDrainPlusOneWithYourInsurgentPresent() {
        var scn = GetScenario();
        var lowerlevels = scn.GetLSCard("lowerlevels");
        var coruscant = scn.GetLSCard("coruscant");
        var rebel = scn.GetLSCard("rebel");

        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.MoveLocationToTable(lowerlevels);
        scn.MoveCardsToLocation(lowerlevels, rebel);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(lowerlevels));
        float withoutInsurgent = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), lowerlevels, scn.LS);

        var insurgent = MissionTestSupport.putInReserve(scn, 97301, scn.LS, Vb73LightTestSupport.insurgent());
        scn.MoveCardsToLocation(lowerlevels, insurgent);
        float withInsurgent = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), lowerlevels, scn.LS);
        assertEquals(withoutInsurgent + 1, withInsurgent, scn.epsilon);
    }

    @Test
    public void CoruscantLowerLevelsCharactersOfAbilityMoreThan4AreDefenseValueMinusOne() {
        var scn = GetScenario();
        var lowerlevels = scn.GetLSCard("lowerlevels");
        var coruscant = scn.GetLSCard("coruscant");
        var vader = scn.GetDSCard("vader");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.MoveLocationToTable(lowerlevels);
        scn.MoveCardsToLocation(site, vader, trooper);
        int vaderDefense = scn.GetDefense(vader);
        int trooperDefense = scn.GetDefense(trooper);
        scn.MoveCardsToLocation(lowerlevels, vader, trooper);
        assertEquals(vaderDefense - 1, scn.GetDefense(vader));
        assertEquals(trooperDefense, scn.GetDefense(trooper));
    }
}
