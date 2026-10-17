package com.gempukku.swccgo.rules.influence;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.filters.LegacyFilters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.InfluenceModifier;
import com.gempukku.swccgo.logic.modifiers.PoliticsModifier;
import com.gempukku.swccgo.logic.modifiers.ResetInfluenceModifier;
import com.gempukku.swccgo.logic.modifiers.TotalInfluenceAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InfluenceTests {

    /**
     * Test-only blueprint used until Sim Aloo (V) is coded.
     */
    private static final class TestInfluenceCharacter extends AbstractAlien {
        TestInfluenceCharacter(float influence) {
            super(Side.DARK, 1, 1, 1, 1, 1, "Test Influence Character", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1);
            setInfluence(influence);
        }
    }

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("trooper2", "1_194");
                    put("lott", "12_110");
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private ModifiersQuerying mq(VirtualTableScenario scn) {
        return scn.game().getModifiersQuerying();
    }

    @Test
    public void PrintedInfluenceReadFromTestBlueprint() {
        assertEquals(2f, new TestInfluenceCharacter(2).getInfluence(), 0);
        assertEquals(0f, new TestInfluenceCharacter(0).getInfluence(), 0);
    }

    @Test
    public void InfluenceModifierPlusOneFromZeroHasInfluence() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper);
        assertEquals(0f, mq(scn).getInfluence(scn.gameState(), trooper), 0);
        assertFalse(mq(scn).hasInfluence(scn.gameState(), trooper));
        assertFalse(LegacyFilters.hasInfluence().accepts(scn.game(), trooper));

        scn.ApplyAdHocModifier(new InfluenceModifier(trooper, 1));
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(1f, mq(scn).getInfluence(scn.gameState(), trooper), 0);
        assertTrue(mq(scn).hasInfluence(scn.gameState(), trooper));
        assertTrue(LegacyFilters.hasInfluence().accepts(scn.game(), trooper));
        assertTrue(LegacyFilters.influenceMoreThan(0).accepts(scn.game(), trooper));
    }

    @Test
    public void InfluenceMinusTwoFromOneFloorsAtZero() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        var lott = scn.GetDSCard("lott");
        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, lott);
        scn.ApplyAdHocModifier(new InfluenceModifier(trooper, Filters.sameCardId(trooper), 1));
        scn.ApplyAdHocModifier(new InfluenceModifier(lott, Filters.sameCardId(trooper), -2));
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(0f, mq(scn).getInfluence(scn.gameState(), trooper), 0);
        assertFalse(mq(scn).hasInfluence(scn.gameState(), trooper));
        assertFalse(LegacyFilters.hasInfluence().accepts(scn.game(), trooper));
    }

    @Test
    public void UnmodifiableResetWinsOverModifiersLowestResetWins() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper);
        scn.ApplyAdHocModifier(new InfluenceModifier(trooper, 5));
        scn.ApplyAdHocModifier(new ResetInfluenceModifier(trooper, Filters.sameCardId(trooper), 3));
        scn.ApplyAdHocModifier(new ResetInfluenceModifier(trooper, Filters.sameCardId(trooper), 1));
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(1f, mq(scn).getInfluence(scn.gameState(), trooper), 0);
        assertTrue(mq(scn).hasInfluence(scn.gameState(), trooper));
    }

    @Test
    public void TotalInfluenceAtLocationSumsThatPlayersCharacters() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, trooper2, luke);
        scn.ApplyAdHocModifier(new InfluenceModifier(trooper, 1));
        scn.ApplyAdHocModifier(new InfluenceModifier(trooper2, 1));
        scn.ApplyAdHocModifier(new InfluenceModifier(luke, 4));
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(2f, mq(scn).getTotalInfluenceAtLocation(scn.gameState(), trooper.getOwner(), site), 0);
        assertEquals(4f, mq(scn).getTotalInfluenceAtLocation(scn.gameState(), luke.getOwner(), site), 0);

        scn.ApplyAdHocModifier(new TotalInfluenceAtLocationModifier(trooper, Filters.sameCardId(site), -1, trooper.getOwner()));
        scn.SkipToLSTurn();

        assertEquals(1f, mq(scn).getTotalInfluenceAtLocation(scn.gameState(), trooper.getOwner(), site), 0);
        assertEquals(4f, mq(scn).getTotalInfluenceAtLocation(scn.gameState(), luke.getOwner(), site), 0);
    }

    @Test
    public void HighestPrintedInfluenceIgnoresModifiers() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        var lott = scn.GetDSCard("lott");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, lott);
        scn.ApplyAdHocModifier(new InfluenceModifier(trooper, 5));
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(5f, mq(scn).getInfluence(scn.gameState(), trooper), 0);
        assertEquals(0f, mq(scn).getHighestPrintedInfluenceAtLocation(scn.gameState(), site), 0);
        assertEquals(0f, trooper.getBlueprint().getInfluence(), 0);
        assertEquals(0f, lott.getBlueprint().getInfluence(), 0);
    }

    @Test
    public void PoliticsUntouchedByInfluenceModifiersAndViceVersa() {
        var scn = GetScenario();
        var lott = scn.GetDSCard("lott");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lott);

        assertEquals(4f, mq(scn).getPolitics(scn.gameState(), lott), 0);
        assertEquals(0f, mq(scn).getInfluence(scn.gameState(), lott), 0);

        scn.ApplyAdHocModifier(new InfluenceModifier(lott, 1));
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(4f, mq(scn).getPolitics(scn.gameState(), lott), 0);
        assertEquals(1f, mq(scn).getInfluence(scn.gameState(), lott), 0);

        scn.ApplyAdHocModifier(new PoliticsModifier(lott, 2));
        scn.SkipToLSTurn();

        assertEquals(6f, mq(scn).getPolitics(scn.gameState(), lott), 0);
        assertEquals(1f, mq(scn).getInfluence(scn.gameState(), lott), 0);
    }
}
