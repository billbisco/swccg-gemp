package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_1210_109_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("r2", "1210_109");
                    put("r5", "2_15");
                    put("fe", "2_25");
                    put("cantina", "1_128");
                    put("farm", "1_132");
                    put("red2", "2_70");
                    put("wedge", "2_23");
                    put("kessel", "1_126");
                    put("luke", "1_19");
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

    protected VirtualTableScenario GetLegacyScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("r2", "1210_109");
                    put("luke", "1_19");
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
                "legacy"
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
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertTrue(card.getModelTypes().contains(ModelType.ASTROMECH));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void R2D2AddsOneLightForceIconWhilePresentWithAScompLink() {
        var scn = GetScenario();
        var r2 = scn.GetLSCard("r2");
        var farm = scn.GetLSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        int before = scn.GetIconCount(farm, Icon.LIGHT_FORCE);
        scn.MoveCardsToLocation(farm, r2);
        assertEquals(before + 1, scn.GetIconCount(farm, Icon.LIGHT_FORCE));
    }

    @Test
    public void R2D2DoesNotAddALightForceIconWithoutAScompLink() {
        var scn = GetScenario();
        var r2 = scn.GetLSCard("r2");
        var cantina = scn.GetLSCard("cantina");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        int before = scn.GetIconCount(cantina, Icon.LIGHT_FORCE);
        scn.MoveCardsToLocation(cantina, r2);
        assertEquals(before, scn.GetIconCount(cantina, Icon.LIGHT_FORCE));
    }

    @Test
    public void R2D2AddsTwoToPowerManeuverAndHyperspeedOfAStarfighterHeIsAboard() {
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
        assertEquals(power + 2, scn.GetPower(red2));
        assertEquals(maneuver + 2, scn.GetManeuver(red2));
        assertEquals(hyperspeed + 2, scn.GetHyperspeed(red2));
    }

    @Test
    public void R2D2IsImmuneToFireExtinguisher() {
        var scn = GetScenario();
        var r2 = scn.GetLSCard("r2");
        var fe = scn.GetLSCard("fe");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r2);
        scn.MoveCardsToLSHand(fe);
        scn.LSActivateForceCheat(6);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse("Fire Extinguisher deployed on leftover R2-D2. decision=" + scn.LSGetDecision(),
                scn.LSCardActionAvailable(fe, "Deploy"));
        assertNull(fe.getAttachedTo());
    }

    @Test
    public void R2D2ImmunityDoesNotStopFireExtinguisherOnAnotherAstromech() {
        var scn = GetScenario();
        var r2 = scn.GetLSCard("r2");
        var r5 = scn.GetLSCard("r5");
        var fe = scn.GetLSCard("fe");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r2, r5);
        scn.MoveCardsToLSHand(fe);
        scn.LSActivateForceCheat(6);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue("Fire Extinguisher should still deploy on another astromech. decision=" + scn.LSGetDecision(),
                scn.LSCardActionAvailable(fe, "Deploy"));
        scn.LSPlayCard(fe);
        assertTrue(scn.LSHasCardChoiceAvailable(r5));
        assertTrue(scn.LSHasCardChoiceNotAvailable(r2));
        scn.LSChooseCard(r5);
        scn.PassAllResponses();
        assertEquals(r5, fe.getAttachedTo());
    }

    @Test
    public void R2D2IsHeroicWhenLoreHeroicRuleIsOn() {
        var scn = GetLegacyScenario();
        var r2 = scn.GetLSCard("r2");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r2, luke, trooper);
        scn.AttachCardsTo(trooper, blaster);
        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(Filters.and(Keyword.HEROIC).accepts(scn.game(), r2));
        assertFalse(Filters.and(Keyword.HEROIC).accepts(scn.game(), luke));

        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        assertTrue("Fire not offered. actions=" + scn.GetDSAvailableActions(),
                scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(blaster, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(r2));
        assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
    }
}
