package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.common.CardSubtype;
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

public class Card_1220_053_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("cloud-car", "5_88");
                    put("bespin", "5_76");
                    put("clouds", "5_85");
                }},
                new HashMap<>() {{
                    put("assault", "1220_53");
                    put("tie", "1_304");
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
    public void AtmosphericAssaultStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("assault").getBlueprint();

        assertEquals("Atmospheric Assault", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AtmosphericAssaultAddsTwoDestinyToPowerWhenTieDefendsAloneAtCloudSector() {
        var scn = GetScenario();
        var assault = scn.GetDSCard("assault");
        var tie = scn.GetDSCard("tie");
        var cloudCar = scn.GetLSCard("cloud-car");
        var bespin = scn.GetLSCard("bespin");
        var clouds = scn.GetLSCard("clouds");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLocation(clouds, tie, cloudCar);
        scn.MoveCardsToDSHand(assault);

        scn.LSStartBattleAndSkipToWeaponsSegment(clouds);
        scn.LSPass();
        assertTrue(scn.DSCardPlayAvailable(assault, "Add two destiny to power"));
        scn.DSPlayCard(assault, "Add two destiny to power");
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, assault);
        assertTrue(scn.gameState().isDuringBattle());
    }
}
