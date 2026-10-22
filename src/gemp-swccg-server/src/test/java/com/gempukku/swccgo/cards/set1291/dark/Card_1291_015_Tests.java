package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.common.CardSubtype;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("acclamator", "200_59");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("geonosis", "1291_15");
                    put("tf1", "12_181");
                    put("tf2", "12_181");
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
    public void GeonosisStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("geonosis").getBlueprint();

        assertEquals("Geonosis", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getParsec());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SYSTEM, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PLANET);
            add(Icon.EPISODE_I);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void GeonosisForceDrainPlus1WhenYouControlWithTwoTradeFederationStarships() {
        var scn = GetScenario();
        var geonosis = scn.GetDSCard("geonosis");
        var tf1 = scn.GetDSCard("tf1");
        var tf2 = scn.GetDSCard("tf2");

        scn.StartGame();
        scn.MoveLocationToTable(geonosis);
        scn.MoveCardsToLocation(geonosis, tf1, tf2);

        assertEquals(2, Math.round(scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), geonosis, scn.DS)));
    }

    @Test
    public void GeonosisForceDrainIs2WithoutTwoSeparatistOrTradeFederationStarships() {
        var scn = GetScenario();
        var geonosis = scn.GetDSCard("geonosis");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveLocationToTable(geonosis);
        scn.MoveCardsToLocation(geonosis, tie);

        assertEquals(1, Math.round(scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), geonosis, scn.DS)));
    }

    @Test
    public void GeonosisLightForceDrainMinus1UnlessCloneArmyStarshipHere() {
        var scn = GetScenario();
        var geonosis = scn.GetDSCard("geonosis");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveLocationToTable(geonosis);
        scn.MoveCardsToLocation(geonosis, xwing);

        assertEquals(1, Math.round(scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), geonosis, scn.LS)));
    }

    @Test
    public void GeonosisLightForceDrainIs1WithCloneArmyStarshipHere() {
        var scn = GetScenario();
        var geonosis = scn.GetDSCard("geonosis");
        var acclamator = scn.GetLSCard("acclamator");

        scn.StartGame();
        scn.MoveLocationToTable(geonosis);
        scn.MoveCardsToLocation(geonosis, acclamator);

        assertEquals(2, Math.round(scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), geonosis, scn.LS)));
    }
}
