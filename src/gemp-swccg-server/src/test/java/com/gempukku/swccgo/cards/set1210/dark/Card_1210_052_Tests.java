package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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

public class Card_1210_052_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("reinforcements", "1_106");
                    put("copy", "1_106");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("tentacle", "1210_52");
                    put("tentacle1", "1210_52");
                    put("tentacle2", "1210_52");
                    put("tentacle3", "1210_52");
                    put("stormtrooper", "1_194");
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
    public void TentacleStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("tentacle").getBlueprint();

        assertEquals("Tentacle", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.GRABBER);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Control));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TentacleGrabsJustPlayedInterrupt() {
        var scn = GetScenario();
        var tentacle = scn.GetDSCard("tentacle");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var reinforcements = scn.GetLSCard("reinforcements");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, stormtrooper);
        scn.MoveCardsToDSHand(tentacle);
        scn.MoveCardsToLSHand(reinforcements);
        scn.LSActivateForceCheat(2);
        scn.DSActivateForceCheat(2);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardPlayAvailable(reinforcements));
        scn.LSPlayCard(reinforcements);
        scn.DSPass();
        scn.LSPass();

        assertTrue(scn.DSCardPlayAvailable(tentacle, "grab"));
        scn.DSPlayCard(tentacle, "grab");
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, tentacle);
        assertTrue(scn.IsStackedOn(tentacle, reinforcements));
        assertInZone(Zone.STACKED, reinforcements);
    }

    @Test
    public void TentacleMakesOtherCopiesOfStackedInterruptDestinyMinusThree() {
        var scn = GetScenario();
        var tentacle = scn.GetDSCard("tentacle");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var reinforcements = scn.GetLSCard("reinforcements");
        var copy = scn.GetLSCard("copy");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, stormtrooper);
        scn.MoveCardsToDSHand(tentacle);
        scn.MoveCardsToLSHand(reinforcements);
        scn.LSActivateForceCheat(2);
        scn.DSActivateForceCheat(2);
        scn.SkipToLSTurn(Phase.CONTROL);

        int printedDestiny = scn.GetDestiny(copy);
        scn.LSPlayCard(reinforcements);
        scn.DSPass();
        scn.LSPass();
        scn.DSPlayCard(tentacle, "grab");
        scn.PassAllResponses();

        assertTrue(scn.IsStackedOn(tentacle, reinforcements));
        assertEquals(printedDestiny - 3, scn.GetDestiny(copy));
    }

    @Test
    public void TentacleDoesNotGrabWhenThreeTentaclesAreOnTable() {
        var scn = GetScenario();
        var tentacle = scn.GetDSCard("tentacle");
        var tentacle1 = scn.GetDSCard("tentacle1");
        var tentacle2 = scn.GetDSCard("tentacle2");
        var tentacle3 = scn.GetDSCard("tentacle3");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var reinforcements = scn.GetLSCard("reinforcements");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, stormtrooper);
        scn.MoveCardsToDSSideOfTable(tentacle1, tentacle2, tentacle3);
        scn.MoveCardsToDSHand(tentacle);
        scn.MoveCardsToLSHand(reinforcements);
        scn.LSActivateForceCheat(2);
        scn.DSActivateForceCheat(2);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardPlayAvailable(reinforcements));
        scn.LSPlayCard(reinforcements);
        scn.DSPass();
        scn.LSPass();

        assertFalse(scn.DSCardPlayAvailable(tentacle));
        assertFalse(scn.DSCardPlayAvailable(tentacle, "grab"));
        assertEquals(Zone.HAND, tentacle.getZone());
    }
}
