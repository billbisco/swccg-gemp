package com.gempukku.swccgo.rules.agenda;

import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AgendaTests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void NewAgendaHumanReadableNames() {
        assertEquals("Logistics", Agenda.LOGISTICS.getHumanReadable());
        assertEquals("Law", Agenda.LAW.getHumanReadable());
        assertEquals("Rule", Agenda.RULE.getHumanReadable());
        assertEquals("Intelligence", Agenda.INTELLIGENCE.getHumanReadable());
    }

    @Test
    public void LogisticsAgendaGrantedByModifier() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper);
        assertFalse(mq(scn).hasAgenda(scn.gameState(), trooper, Agenda.LOGISTICS));
        assertFalse(Filters.and(Agenda.LOGISTICS).accepts(scn.game(), trooper));

        scn.ApplyAdHocModifier(new AgendaModifier(trooper, Agenda.LOGISTICS));
        scn.SkipToPhase(Phase.CONTROL);

        assertTrue(mq(scn).hasAgenda(scn.gameState(), trooper, Agenda.LOGISTICS));
        assertTrue(Filters.and(Agenda.LOGISTICS).accepts(scn.game(), trooper));
        assertEquals(List.of(Agenda.LOGISTICS), mq(scn).getAgendas(scn.gameState(), trooper));
    }

    @Test
    public void LawRuleAndIntelligenceAgendasGrantedTogether() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper);
        scn.ApplyAdHocModifier(new AgendaModifier(trooper, Agenda.LAW, Agenda.RULE, Agenda.INTELLIGENCE));
        scn.SkipToPhase(Phase.CONTROL);

        assertTrue(mq(scn).hasAgenda(scn.gameState(), trooper, Agenda.LAW));
        assertTrue(mq(scn).hasAgenda(scn.gameState(), trooper, Agenda.RULE));
        assertTrue(mq(scn).hasAgenda(scn.gameState(), trooper, Agenda.INTELLIGENCE));
        assertFalse(mq(scn).hasAgenda(scn.gameState(), trooper, Agenda.LOGISTICS));
        assertTrue(Filters.and(Agenda.LAW).accepts(scn.game(), trooper));
        assertTrue(Filters.and(Agenda.RULE).accepts(scn.game(), trooper));
        assertTrue(Filters.and(Agenda.INTELLIGENCE).accepts(scn.game(), trooper));
        assertFalse(Filters.and(Agenda.LOGISTICS).accepts(scn.game(), trooper));
    }

    @Test
    public void ExistingAmbitionAgendaUnchanged() {
        var scn = GetScenario();
        var lott = scn.GetDSCard("lott");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lott);
        scn.SkipToPhase(Phase.CONTROL);

        assertTrue(mq(scn).hasAgenda(scn.gameState(), lott, Agenda.AMBITION));
        assertTrue(mq(scn).hasAgenda(scn.gameState(), lott, Agenda.BLOCKADE));
        assertTrue(mq(scn).hasAgenda(scn.gameState(), lott, Agenda.TRADE));
        assertFalse(mq(scn).hasAgenda(scn.gameState(), lott, Agenda.LOGISTICS));
        assertTrue(Filters.and(Agenda.AMBITION).accepts(scn.game(), lott));
        assertFalse(Filters.and(Agenda.LAW).accepts(scn.game(), lott));
    }

    @Test
    public void NonCharacterHasNoAgenda() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.ApplyAdHocModifier(new AgendaModifier(site, Agenda.LOGISTICS));
        scn.SkipToPhase(Phase.CONTROL);

        assertFalse(mq(scn).hasAgenda(scn.gameState(), site, Agenda.LOGISTICS));
        assertTrue(mq(scn).getAgendas(scn.gameState(), site).isEmpty());
    }
}
