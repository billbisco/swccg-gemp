package com.gempukku.swccgo.cards.set1291.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_062_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("db", "1291_62");
                    put("otherdb", "1291_56");
                    put("traveler", "1_19");
                    put("cody", "200_6");
                }},
                new HashMap<>() {{
                    put("opptraveler", "1_194");
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
    public void MuunilinstDockingBayStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("db").getBlueprint();

        assertEquals("Muunilinst: Docking Bay", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DOCKING_BAY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.PLANET);
            add(Icon.EXTERIOR_SITE);
            add(Icon.INTERIOR_SITE);
            add(Icon.LIGHT_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }


    @Test
    public void MuunilinstDockingBayLightTransitFromHereRequires1Force() {
        var scn = GetScenario();
        var from = scn.GetLSCard("db");
        var to = scn.GetLSCard("otherdb");
        var traveler = scn.GetLSCard("traveler");

        scn.StartGame();
        scn.MoveLocationToTable(from);
        scn.MoveLocationToTable(to);
        scn.MoveCardsToLocation(from, traveler);

        assertEquals(1, Math.round(scn.game().getModifiersQuerying().getDockingBayTransitCost(scn.gameState(), traveler, from, to, 0)));
    }

    @Test
    public void MuunilinstDockingBayLightTransitFromHereIsFreeIfCloneArmyCardHere() {
        var scn = GetScenario();
        var from = scn.GetLSCard("db");
        var to = scn.GetLSCard("otherdb");
        var traveler = scn.GetLSCard("traveler");
        var cody = scn.GetLSCard("cody");

        scn.StartGame();
        scn.MoveLocationToTable(from);
        scn.MoveLocationToTable(to);
        scn.MoveCardsToLocation(from, traveler);
        scn.MoveCardsToLocation(from, cody);

        assertEquals(0, Math.round(scn.game().getModifiersQuerying().getDockingBayTransitCost(scn.gameState(), traveler, from, to, 0)));
    }

    @Test
    public void MuunilinstDockingBayDarkTransitFromHereRequires2Force() {
        var scn = GetScenario();
        var from = scn.GetLSCard("db");
        var to = scn.GetLSCard("otherdb");
        var traveler = scn.GetDSCard("opptraveler");

        scn.StartGame();
        scn.MoveLocationToTable(from);
        scn.MoveLocationToTable(to);
        scn.MoveCardsToLocation(from, traveler);

        assertEquals(2, Math.round(scn.game().getModifiersQuerying().getDockingBayTransitCost(scn.gameState(), traveler, from, to, 0)));
    }

    @Test
    public void MuunilinstDockingBayLightTransitIsNotFreeWhenCloneArmyCardIsElsewhere() {
        var scn = GetScenario();
        var from = scn.GetLSCard("db");
        var to = scn.GetLSCard("otherdb");
        var traveler = scn.GetLSCard("traveler");
        var cody = scn.GetLSCard("cody");

        scn.StartGame();
        scn.MoveLocationToTable(from);
        scn.MoveLocationToTable(to);
        scn.MoveCardsToLocation(from, traveler);
        scn.MoveCardsToLocation(to, cody);

        assertEquals(1, Math.round(scn.game().getModifiersQuerying().getDockingBayTransitCost(scn.gameState(), traveler, from, to, 0)));
    }
}
