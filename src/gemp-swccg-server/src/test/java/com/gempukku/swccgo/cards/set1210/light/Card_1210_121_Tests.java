package com.gempukku.swccgo.cards.set1210.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_121_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("memorial", "1210_121");
                    put("dune", "1_130");
                    put("jawa", "1_12");
                    put("jawa2", "1_12");
                    put("ronto", "7_155");
                    put("sandcrawler", "1_150");
                }},
                new HashMap<>() {{
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
    public void CrashSiteMemorialStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("memorial").getBlueprint();

        assertEquals("Crash Site Memorial", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CrashSiteMemorialDeploysOnTable() {
        var scn = GetScenario();
        var memorial = scn.GetLSCard("memorial");

        scn.StartGame();
        scn.MoveCardsToLSHand(memorial);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);

        assertTrue(scn.LSCardPlayAvailable(memorial));
        scn.LSPlayCard(memorial);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, memorial);
    }

    @Test
    public void CrashSiteMemorialMakesDuneSeaADesert() {
        var scn = GetScenario();
        var memorial = scn.GetLSCard("memorial");
        var dune = scn.GetLSCard("dune");

        scn.StartGame();
        scn.MoveLocationToTable(dune);
        assertFalse(scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), dune, Keyword.DESERT));

        scn.MoveCardsToLSSideOfTable(memorial);
        assertTrue(scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), dune, Keyword.DESERT));
    }

    @Test
    public void CrashSiteMemorialAddsPowerAndForfeitToRontoAndSandcrawlerAtDesertOccupiedWithJawa() {
        var scn = GetScenario();
        var memorial = scn.GetLSCard("memorial");
        var dune = scn.GetLSCard("dune");
        var jawa = scn.GetLSCard("jawa");
        var jawa2 = scn.GetLSCard("jawa2");
        var ronto = scn.GetLSCard("ronto");
        var sandcrawler = scn.GetLSCard("sandcrawler");

        scn.StartGame();
        scn.MoveLocationToTable(dune);
        scn.MoveCardsToLSSideOfTable(memorial);
        scn.MoveCardsToLocation(dune, jawa, jawa2, ronto, sandcrawler);
        scn.BoardAsPassenger(ronto, jawa);
        scn.BoardAsPilot(sandcrawler, jawa2);

        assertEquals(4, scn.GetPower(ronto));
        assertEquals(5, scn.GetForfeit(ronto));
        assertEquals(4, scn.GetPower(sandcrawler));
        assertEquals(7, scn.GetForfeit(sandcrawler));
    }

    @Test
    public void CrashSiteMemorialCancelsOpponentsForceGenerationAtDesertOccupiedWithJawa() {
        var scn = GetScenario();
        var memorial = scn.GetLSCard("memorial");
        var dune = scn.GetLSCard("dune");
        var jawa = scn.GetLSCard("jawa");

        scn.StartGame();
        scn.MoveLocationToTable(dune);
        scn.MoveCardsToLSSideOfTable(memorial);

        float dsGenWithoutJawa = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), dune, scn.DS);
        assertEquals(1, Math.round(dsGenWithoutJawa));

        scn.MoveCardsToLocation(dune, jawa);
        float dsGenWithJawa = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), dune, scn.DS);
        assertEquals(0, Math.round(dsGenWithJawa));
    }

    @Test
    public void CrashSiteMemorialDoesNotAddPowerAndForfeitWhenNoJawaOccupiesTheDesert() {
        var scn = GetScenario();
        var memorial = scn.GetLSCard("memorial");
        var dune = scn.GetLSCard("dune");
        var ronto = scn.GetLSCard("ronto");
        var sandcrawler = scn.GetLSCard("sandcrawler");
        var walkway = scn.GetLSStartingLocation();
        var jawa = scn.GetLSCard("jawa");

        scn.StartGame();
        scn.MoveLocationToTable(dune);
        scn.MoveCardsToLSSideOfTable(memorial);
        scn.MoveCardsToLocation(dune, ronto, sandcrawler);
        scn.MoveCardsToLocation(walkway, jawa);
        var driver = scn.GetLSFiller(1);
        var rider = scn.GetLSFiller(2);
        scn.MoveCardsToLocation(dune, driver, rider);
        scn.BoardAsPilot(sandcrawler, driver);
        scn.BoardAsPassenger(ronto, rider);

        assertEquals(3, scn.GetPower(ronto));
        assertEquals(3, scn.GetForfeit(ronto));
        assertEquals(3, scn.GetPower(sandcrawler));
        assertEquals(5, scn.GetForfeit(sandcrawler));
    }
}
