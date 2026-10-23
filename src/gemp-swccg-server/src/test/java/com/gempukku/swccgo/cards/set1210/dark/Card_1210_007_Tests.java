package com.gempukku.swccgo.cards.set1210.dark;

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

public class Card_1210_007_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("vader", "1210_7");
                    put("ds612", "1_173");
                    put("devastator", "1_301");
                    put("tatooine", "1_289");
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
    public void DarthVaderStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("vader").getBlueprint();

        assertEquals("Darth Vader", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(6, card.getAbility(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DarthVaderAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, vader);
        assertEquals(12, scn.GetPower(devastator));
    }

    @Test
    public void DarthVaderStarshipIsImmuneToAttritionLessThanFiveWhileAboard() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), devastator), scn.epsilon);
        scn.BoardAsPilot(devastator, vader);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), devastator), scn.epsilon);
    }

    @Test
    public void DarthVaderImmuneToAttritionLessThanFive() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vader), scn.epsilon);
    }

    @Test
    public void DarthVaderSubtractsTwoFromJustDrawnDestinyDuringBattleAtSameSystem() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, vader, luke);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.PrepareLSDestiny(5);
        scn.LSChooseYes();
        scn.DSPass();
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        assertTrue("Vader destiny subtract missing. decision=" + scn.DSGetDecision(),
                scn.DSCardActionAvailable(vader, "Subtract 2 from destiny"));
        scn.DSUseCardAction(vader, "Subtract 2 from destiny");
        scn.PassAllResponses();
        assertEquals(3, scn.GetLSTotalDestiny());
    }

    @Test
    public void DarthVaderDoesNotSubtractDestinyDuringBattleAtSite() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, luke);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.PrepareLSDestiny(5);
        scn.LSChooseYes();
        scn.DSPass();
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(vader, "Subtract 2 from destiny"));
    }

    @Test
    public void DarthVaderSubtractsTwiceIfWithBlackSquadronPilot() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var ds612 = scn.GetDSCard("ds612");
        var luke = scn.GetLSCard("luke");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, vader, ds612, luke);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.PrepareLSDestiny(5);
        scn.LSChooseYes();
        scn.DSPass();
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(vader, "Subtract 2 from destiny"));
        scn.DSUseCardAction(vader, "Subtract 2 from destiny");
        scn.LSPass();
        assertTrue("Second Vader subtract missing. decision=" + scn.DSGetDecision(),
                scn.DSCardActionAvailable(vader, "Subtract 2 from destiny"));
        scn.DSUseCardAction(vader, "Subtract 2 from destiny");
        scn.PassAllResponses();
        assertEquals(1, scn.GetLSTotalDestiny());
    }

    @Test
    public void DarthVaderSubtractsOnlyOnceWithoutBlackSquadronPilot() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, vader, luke);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.PrepareLSDestiny(5);
        scn.LSChooseYes();
        scn.DSPass();
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(vader, "Subtract 2 from destiny"));
        scn.DSUseCardAction(vader, "Subtract 2 from destiny");
        scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(vader, "Subtract 2 from destiny"));
        scn.PassAllResponses();
        assertEquals(3, scn.GetLSTotalDestiny());
    }
}
