package com.gempukku.swccgo.cards.set1108.light;

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

public class Card_1108_014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("obiwan", "1108_14");
                    put("yoda", "4_2");
                    put("mwyhl", "7_138");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void ObiWanKenobiStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("obiwan").getBlueprint();

        assertEquals("Obi-Wan Kenobi", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(5, card.getPower(), scn.epsilon);
        assertEquals(6, card.getAbility(), scn.epsilon);
        assertEquals(9, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.GENERAL);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.WARRIOR);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ObiWanKenobiGameTextMayNotBeCanceled() {
        var scn = GetScenario();
        var obiwan = scn.GetLSCard("obiwan");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, obiwan);
        assertTrue(scn.game().getModifiersQuerying().isProhibitedFromHavingGameTextCanceled(scn.gameState(), obiwan));
    }

    @Test
    public void ObiWanKenobiMayNotBeTargetedToBeCaptured() {
        var scn = GetScenario();
        var obiwan = scn.GetLSCard("obiwan");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, obiwan);
        assertFalse(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), obiwan, obiwan,
                java.util.Collections.singleton(com.gempukku.swccgo.common.TargetingReason.TO_BE_CAPTURED)));
    }

    @Test
    public void ObiWanKenobiIsImmuneToAttritionLessThanFiveAndEightWithADarkJedi() {
        var scn = GetScenario();
        var obiwan = scn.GetLSCard("obiwan");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, obiwan);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), obiwan), scn.epsilon);
        scn.MoveCardsToLocation(site, vader);
        assertEquals(8, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), obiwan), scn.epsilon);
    }

    @Test
    public void ObiWanKenobiIgnoresObjectiveRestrictionsWhenForceDrainingUnlessAnotherJediPresent() {
        StartingSetup mwyhlObjective = new StartingSetup() {
            @Override
            public HashMap<String, String> Cards() {
                return new HashMap<>() {{
                    put("mwyhl", "7_138");
                    put("dagobah", "4_084");
                }};
            }

            @Override
            public void Setup(VirtualTableScenario scn) {
            }
        };
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("obiwan", "1108_14");
                    put("yoda", "4_2");
                    put("training", "4_88");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("stormtrooper", "1_194");
                }},
                10, 10,
                mwyhlObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var obiwan = scn.GetLSCard("obiwan");
        var yoda = scn.GetLSCard("yoda");
        var training = scn.GetLSCard("training");

        scn.StartGame();
        scn.MoveLocationToTable(training);
        scn.MoveCardsToLocation(training, obiwan);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(2);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSForceDrainAvailable(training));
        scn.MoveCardsToLocation(training, yoda);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(2);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertFalse(scn.LSForceDrainAvailable(training));
    }
}
