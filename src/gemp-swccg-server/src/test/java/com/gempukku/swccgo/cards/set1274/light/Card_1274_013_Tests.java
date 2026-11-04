package com.gempukku.swccgo.cards.set1274.light;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("cfa", "1274_13");
                    put("red1", "1_144");
                    put("wedge", "200_67");
                    put("xwing", "1_146");
                    put("xwing2", "1_146");
                    put("ywing", "1_147");
                    put("tantive", "2_73");
                    put("luke", "1_19");
                    put("han", "1_11");
                    put("dockingbay", "1_136");
                    put("yavin", "1_135");
                }},
                new HashMap<>() {{
                    put("black2", "1_299");
                    put("tie", "1_304");
                    put("vader", "1_168");
                    put("trooper", "1_194");
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

    /** Sum of Combined Fleet Action's 'each battle destiny' modifiers for the player at the current battle location. */
    private static float eachBattleDestinyBonus(VirtualTableScenario scn, String playerId) {
        var gameState = scn.gameState();
        var querying = scn.game().getModifiersQuerying();
        PhysicalCardImpl location = (PhysicalCardImpl) gameState.getBattleLocation();
        float total = 0;
        for (Modifier modifier : querying.getModifiersAffectingCard(gameState, ModifierType.EACH_BATTLE_DESTINY_AT_LOCATION, location)) {
            if (modifier.getSource(gameState) == scn.GetLSCard("cfa")) {
                total += modifier.getBattleDestinyAtLocationModifier(playerId, gameState, querying, location);
            }
        }
        return total;
    }

    private static float immunity(VirtualTableScenario scn, PhysicalCardImpl card) {
        return scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), card);
    }

    @Test
    public void CombinedFleetActionVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("cfa").getBlueprint();

        assertEquals("Combined Fleet Action", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ADMIRALS_ORDER);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEATH_STAR_II);
            add(Icon.ADMIRALS_ORDER);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CombinedFleetActionVUniqueStarfighterImmunityPlusTwoPerNonUniqueOfItsClassPresent() {
        var scn = GetScenario();
        var cfa = scn.GetLSCard("cfa");
        var red1 = scn.GetLSCard("wedge");
        var xwing = scn.GetLSCard("xwing");
        var xwing2 = scn.GetLSCard("xwing2");
        var ywing = scn.GetLSCard("ywing");
        var system = scn.GetDSStartingLocation();
        var black2 = scn.GetDSCard("black2");
        scn.StartGame();
        scn.MoveCardsToLocation(system, red1, xwing, xwing2, ywing, black2);
        scn.BoardAsPilot(black2, scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        scn.LSInitiateBattle(system);
        assertEquals(4, immunity(scn, red1), scn.epsilon);

        scn.MoveCardsToLSSideOfTable(cfa);
        // two X-wings count; the Y-wing is another class
        assertEquals(4 + 2 + 2, immunity(scn, red1), scn.epsilon);
    }

    @Test
    public void CombinedFleetActionVUniqueCapitalStarshipDefenseValuePlusOnePerNonUniqueStarshipAtSameSystem() {
        var scn = GetScenario();
        var cfa = scn.GetLSCard("cfa");
        var tantive = scn.GetLSCard("tantive");
        var xwing = scn.GetLSCard("xwing");
        var ywing = scn.GetLSCard("ywing");
        var red1 = scn.GetLSCard("red1");
        var system = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, tantive, xwing, ywing, red1);
        int armor = scn.GetDefense(tantive);
        scn.MoveCardsToLSSideOfTable(cfa);
        // X-wing and Y-wing are non-unique; Red 1 is unique
        assertEquals(armor + 2, scn.GetDefense(tantive));
    }

    @Test
    public void CombinedFleetActionVBattleDestinyPlusOnePerUniqueNonUniquePairOfSameClassAtSystem() {
        var scn = GetScenario();
        var cfa = scn.GetLSCard("cfa");
        var red1 = scn.GetLSCard("red1");
        var xwing = scn.GetLSCard("xwing");
        var ywing = scn.GetLSCard("ywing");
        var luke = scn.GetLSCard("luke");
        var black2 = scn.GetDSCard("black2");
        var dantooine = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(cfa);
        scn.MoveCardsToLocation(dantooine, red1, xwing, ywing, black2);
        scn.BoardAsPilot(red1, luke);
        scn.BoardAsPilot(black2, scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        scn.LSInitiateBattle(dantooine);
        // Red 1 / X-wing is one pair; the Y-wing has no unique partner
        assertEquals(1, eachBattleDestinyBonus(scn, scn.LS), scn.epsilon);
        assertEquals(0, eachBattleDestinyBonus(scn, scn.DS), scn.epsilon);
    }

    @Test
    public void CombinedFleetActionVBattleDestinyBonusAppliesAtRelatedSites() {
        var scn = GetScenario();
        var cfa = scn.GetLSCard("cfa");
        var red1 = scn.GetLSCard("red1");
        var xwing = scn.GetLSCard("xwing");
        var han = scn.GetLSCard("han");
        var yavin = scn.GetLSCard("yavin");
        var dockingBay = scn.GetLSCard("dockingbay");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveLocationToTable(dockingBay);
        scn.MoveCardsToLSSideOfTable(cfa);
        scn.MoveCardsToLocation(yavin, red1, xwing);
        scn.MoveCardsToLocation(dockingBay, han, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        scn.LSInitiateBattle(dockingBay);
        assertEquals(1, eachBattleDestinyBonus(scn, scn.LS), scn.epsilon);
    }

    @Test
    public void CombinedFleetActionVNoBonusWithoutAPairOfTheSameClass() {
        var scn = GetScenario();
        var cfa = scn.GetLSCard("cfa");
        var red1 = scn.GetLSCard("red1");
        var ywing = scn.GetLSCard("ywing");
        var luke = scn.GetLSCard("luke");
        var black2 = scn.GetDSCard("black2");
        var dantooine = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(cfa);
        scn.MoveCardsToLocation(dantooine, red1, ywing, black2);
        scn.BoardAsPilot(red1, luke);
        scn.BoardAsPilot(black2, scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        scn.LSInitiateBattle(dantooine);
        assertEquals(0, eachBattleDestinyBonus(scn, scn.LS), scn.epsilon);
        assertFalse(scn.gameState().getBattleLocation() == null);
    }
}
