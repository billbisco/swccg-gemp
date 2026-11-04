package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_029_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tycho", "1273_29");
                    put("red1", "1_144");
                    put("bwing", "7_140");
                    put("corvette", "1_140");
                }},
                new HashMap<>() {{
                    put("black2", "1_299");
                    put("bast", "1_165");
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
    public void TychoCelchuVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("tycho").getBlueprint();

        assertEquals("Tycho Celchu", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SPY);
            add(Keyword.ROGUE_SQUADRON);
            add(Keyword.GREEN_SQUADRON);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.TYCHO);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TychoCelchuVIsMatchingPilotForAnyStarfighter() {
        var scn = GetScenario();
        var tycho = scn.GetLSCard("tycho");
        var red1 = scn.GetLSCard("red1");
        var bwing = scn.GetLSCard("bwing");
        var corvette = scn.GetLSCard("corvette");

        scn.StartGame();
        assertTrue(scn.IsMatchingPilot(red1, tycho));
        assertTrue(scn.IsMatchingPilot(bwing, tycho));
        assertFalse(scn.IsMatchingPilot(corvette, tycho));
    }

    @Test
    public void TychoCelchuVAddsThreeToPowerAndStarshipIsImmuneToAttritionLessThan4() {
        var scn = GetScenario();
        var tycho = scn.GetLSCard("tycho");
        var red1 = scn.GetLSCard("red1");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, red1);
        int before = (int) (float) red1.getBlueprint().getPower();
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), red1), scn.epsilon);
        scn.BoardAsPilot(red1, tycho);
        assertEquals(before + 3, scn.GetPower(red1));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), red1), scn.epsilon);
    }

    @Test
    public void TychoCelchuVDrawsOneBattleDestinyIfUnableToOtherwiseWhilePilotingStarship() {
        var scn = GetScenario();
        var tycho = scn.GetLSCard("tycho");
        var corvette = scn.GetLSCard("corvette");
        var black2 = scn.GetDSCard("black2");
        var dantooine = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dantooine, corvette, black2);
        scn.BoardAsPilot(corvette, tycho);
        scn.BoardAsPilot(black2, scn.GetDSCard("bast"));
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(dantooine);
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MIN_BATTLE_DESTINY_DRAWS, tycho).isEmpty());
    }
}
