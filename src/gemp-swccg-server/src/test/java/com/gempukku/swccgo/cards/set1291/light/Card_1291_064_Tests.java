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

public class Card_1291_064_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("landing", "1291_64");
                    put("muunilinst", "1291_60");
                    put("cody", "200_6");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("grievous", "203_27");
                    put("vader", "1_168");
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
    public void MuunilinstRepublicLandingSiteStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("landing").getBlueprint();

        assertEquals("Muunilinst: Republic Landing Site", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.PLANET);
            add(Icon.EXTERIOR_SITE);
            add(Icon.LIGHT_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }


    @Test
    public void MuunilinstRepublicLandingSiteCloneArmyCharactersShuttleToAndFromHereForFree() {
        var scn = GetScenario();
        var landing = scn.GetLSCard("landing");
        var cody = scn.GetLSCard("cody");
        var luke = scn.GetLSCard("luke");
        var muunilinst = scn.GetLSCard("muunilinst");
        var mq = scn.game().getModifiersQuerying();

        scn.StartGame();
        scn.MoveLocationToTable(muunilinst);
        scn.MoveLocationToTable(landing);
        scn.MoveCardsToLocation(landing, cody, luke);

        assertEquals(0, Math.round(mq.getShuttleCost(scn.gameState(), cody, landing, muunilinst, 0)));
        assertEquals(0, Math.round(mq.getShuttleCost(scn.gameState(), cody, muunilinst, landing, 0)));
        assertEquals(1, Math.round(mq.getShuttleCost(scn.gameState(), luke, landing, muunilinst, 0)));
        assertEquals(1, Math.round(mq.getShuttleCost(scn.gameState(), luke, muunilinst, landing, 0)));
    }

    @Test
    public void MuunilinstRepublicLandingSiteDarkForceDrainMinus1UnlessControlWithSeparatist() {
        var scn = GetScenario();
        var landing = scn.GetLSCard("landing");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(landing);
        scn.MoveCardsToLocation(landing, vader);

        assertEquals(1, Math.round(scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), landing, scn.DS)));
    }

    @Test
    public void MuunilinstRepublicLandingSiteDarkForceDrainNotReducedWhenControllingWithSeparatist() {
        var scn = GetScenario();
        var landing = scn.GetLSCard("landing");
        var grievous = scn.GetDSCard("grievous");

        scn.StartGame();
        scn.MoveLocationToTable(landing);
        scn.MoveCardsToLocation(landing, grievous);

        assertEquals(2, Math.round(scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), landing, scn.DS)));
    }
}
