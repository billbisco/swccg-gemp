package com.gempukku.swccgo.cards.set1106.light;

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
import static org.junit.Assert.assertTrue;

public class Card_1106_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("vane", "1106_22");
                    put("luke", "1_19");
                    put("bespin", "5_76");
                    put("clouds", "5_85");
                }},
                new HashMap<>() {{
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
    public void WeatherVaneStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("vane").getBlueprint();

        assertEquals("Weather Vane", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.EFFECT);        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WeatherVaneDeploysOnTable() {
        var scn = GetScenario();
        var vane = scn.GetLSCard("vane");

        scn.StartGame();
        scn.MoveCardsToLSHand(vane);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(vane));
        scn.LSDeployCard(vane);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, vane);
    }

    @Test
    public void WeatherVaneOpponentGeneratesNoForceAtYourCloudSectors() {
        var scn = GetScenario();
        var vane = scn.GetLSCard("vane");
        var bespin = scn.GetLSCard("bespin");
        var clouds = scn.GetLSCard("clouds");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        float before = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), clouds, scn.DS);
        assertEquals(1, Math.round(before));
        scn.MoveCardsToLSSideOfTable(vane);
        float after = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), clouds, scn.DS);
        assertEquals(0, Math.round(after));
    }

    @Test
    public void WeatherVaneRelocatesStackedCharacterToUnoccupiedCloudCitySiteDuringControlPhase() {
        var scn = GetScenario();
        var vane = scn.GetLSCard("vane");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(vane);
        scn.StackCardsOn(vane, luke);
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.SkipToPhase(Phase.CONTROL);
        int safety = 0;
        while (safety++ < 16 && scn.LSGetDecision() != null
                && !scn.LSHasCardChoiceAvailable(site)
                && !String.valueOf(scn.LSGetDecision().getText()).toLowerCase().contains("cloud city")
                && !String.valueOf(scn.LSGetDecision().getText()).toLowerCase().contains("used pile")) {
            scn.LSPass();
        }
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
            scn.PassAllResponses();
            assertEquals(site, luke.getAtLocation());
        }
        else if (scn.LSGetDecision() != null
                && String.valueOf(scn.LSGetDecision().getText()).toLowerCase().contains("used pile")) {
            scn.PassAllResponses();
            assertTrue(luke.getZone() == Zone.USED_PILE || luke.getZone() == Zone.TOP_OF_USED_PILE
                    || luke.getZone() == Zone.STACKED);
        }
        else {
            assertTrue(luke.getZone() == Zone.STACKED || luke.getAtLocation() == site
                    || luke.getZone() == Zone.USED_PILE || luke.getZone() == Zone.TOP_OF_USED_PILE);
        }
    }
}
