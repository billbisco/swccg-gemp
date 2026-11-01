package com.gempukku.swccgo.cards.set1100.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import static org.junit.Assert.assertTrue;

public class Card_1100_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("shield", "1100_3"); put("ship", "1_146"); put("system", "1_127");
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
    public void BattlePlanStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("shield").getBlueprint();

        assertEquals("Battle Plan", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEFENSIVE_SHIELD);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SHIELDS, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BattlePlanPlaysOnTable() {
        var scn = GetScenario();
        var shield = scn.GetLSCard("shield");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(shield);
        assertInZone(Zone.SIDE_OF_TABLE, shield);
    }

    @Test
    public void BattlePlanRequiresThreeForceToDrainUnlessOccupyingBattlegroundSiteAndSystem() {
        var scn = GetScenario();
        var shield = scn.GetLSCard("shield");
        var character = scn.GetLSFiller(1);
        var ship = scn.GetLSCard("ship");
        var site = scn.GetLSStartingLocation();
        var system = scn.GetLSCard("system");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(shield);
        scn.MoveCardsToLocation(site, character);
        assertEquals(3, scn.game().getModifiersQuerying().getInitiateForceDrainCost(scn.gameState(), site, scn.LS), scn.epsilon);

        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, ship);
        assertEquals(0, scn.game().getModifiersQuerying().getInitiateForceDrainCost(scn.gameState(), site, scn.LS), scn.epsilon);
    }
}
