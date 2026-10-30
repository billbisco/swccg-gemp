package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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

public class Card_1103_016_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("r2", "1103_16");
                    put("c3po", "1_5");
                    put("luke", "1_19");
                    put("red2", "2_70");
                    put("wedge", "2_23");
                    put("kessel", "1_126");
                    put("junk", "1_3");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("blaster", "1_317");
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
    public void R2D2StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("r2").getBlueprint();

        assertEquals("R2-D2 (Artoo-Detoo)", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getAlternateDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SPY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.NAV_COMPUTER);
            add(Icon.A_NEW_HOPE);
        }});
        assertTrue(card.getModelTypes().contains(ModelType.ASTROMECH));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void R2D2AddsThreeToPowerManeuverAndHyperspeedOfAStarfighterHeIsAboard() {
        var scn = GetScenario();
        var r2 = scn.GetLSCard("r2");
        var red2 = scn.GetLSCard("red2");
        var wedge = scn.GetLSCard("wedge");
        var kessel = scn.GetLSCard("kessel");

        scn.StartGame();
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(kessel, red2);
        scn.BoardAsPilot(red2, wedge);
        int power = scn.GetPower(red2);
        int maneuver = scn.GetManeuver(red2);
        int hyperspeed = scn.GetHyperspeed(red2);
        scn.BoardAsPassenger(red2, r2);
        assertEquals(power + 3, scn.GetPower(red2));
        assertEquals(maneuver + 3, scn.GetManeuver(red2));
        assertEquals(hyperspeed + 3, scn.GetHyperspeed(red2));
    }

    @Test
    public void R2D2BlocksOpponentPlacingHandCardsInUsedPileUnlessHandHasThirteen() {
        var scn = GetScenario();
        var r2 = scn.GetLSCard("r2");
        var junk = scn.GetLSCard("junk");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r2);
        scn.MoveCardsToLSHand(junk);

        assertTrue(scn.game().getModifiersQuerying().mayNotBePlacedInUsedPile(scn.gameState(), junk, scn.DS));
    }

    @Test
    public void R2D2DoesNotBlockUsedPilePlacementWhenHandHasThirteenCards() {
        var scn = GetScenario();
        var r2 = scn.GetLSCard("r2");
        var junk = scn.GetLSCard("junk");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r2);
        scn.MoveCardsToLSHand(junk);
        int need = 13 - scn.gameState().getHand(scn.LS).size();
        if (need > 0) {
            scn.DrawCardsFromReserve(scn.LS, need);
        }

        assertFalse(scn.game().getModifiersQuerying().mayNotBePlacedInUsedPile(scn.gameState(), junk, scn.DS));
    }
}
