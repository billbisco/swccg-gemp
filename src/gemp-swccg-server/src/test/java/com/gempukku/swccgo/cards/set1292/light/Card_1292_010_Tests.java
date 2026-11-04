package com.gempukku.swccgo.cards.set1292.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.CancelOpponentsForceDrainBonusesModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1292_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("haashn", "1292_10");
                    put("gold1", "1_141");
                }},
                new HashMap<>() {{
                    put("tatooine", "1_289");
                    put("trooper", "1_194");
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
    public void MajorHaashnVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("haashn").getBlueprint();

        assertEquals("Major Haash'n", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(Species.MON_CALAMARI, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.DEATH_STAR_II);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MajorHaashnVAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var haashn = scn.GetLSCard("haashn");
        var gold1 = scn.GetLSCard("gold1");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, gold1);
        scn.BoardAsPilot(gold1, haashn);
        assertEquals(4, scn.GetPower(gold1), scn.epsilon);
    }

    @Test
    public void MajorHaashnVYourForceDrainsAtSameSystemMayNotBeCanceled() {
        var scn = GetScenario();
        var haashn = scn.GetLSCard("haashn");
        var gold1 = scn.GetLSCard("gold1");
        var tatooine = scn.GetDSCard("tatooine");
        var tatooineSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, gold1);
        var q = scn.game().getModifiersQuerying();
        assertFalse(q.cantCancelForceDrainAtLocation(scn.gameState(), tatooine, null, scn.DS, scn.LS));

        scn.BoardAsPilot(gold1, haashn);
        assertTrue(q.cantCancelForceDrainAtLocation(scn.gameState(), tatooine, null, scn.DS, scn.LS));
        // opponent's drains and other locations are not protected
        assertFalse(q.cantCancelForceDrainAtLocation(scn.gameState(), tatooine, null, scn.LS, scn.DS));
        assertFalse(q.cantCancelForceDrainAtLocation(scn.gameState(), tatooineSite, null, scn.DS, scn.LS));
    }

    @Test
    public void MajorHaashnVYourForceDrainBonusesAtSameSystemMayNotBeCanceled() {
        var scn = GetScenario();
        var haashn = scn.GetLSCard("haashn");
        var gold1 = scn.GetLSCard("gold1");
        var tatooine = scn.GetDSCard("tatooine");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, gold1);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), trooper);
        scn.ApplyAdHocModifier(new CancelOpponentsForceDrainBonusesModifier(trooper, Filters.any));
        var q = scn.game().getModifiersQuerying();
        assertTrue(q.isForceDrainModifierCanceled(scn.gameState(), tatooine, gold1, scn.LS, scn.LS, 1));

        scn.BoardAsPilot(gold1, haashn);
        assertFalse(q.isForceDrainModifierCanceled(scn.gameState(), tatooine, gold1, scn.LS, scn.LS, 1));
    }
}
