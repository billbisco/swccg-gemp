package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.WidenIncomKoensayrScopeModifier;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Incom Corporation (7_66). E12: Systems Will Slip Through Your Fingers (V) widens the ship filter.
 * Hidden Base (V) is not on this branch; tests apply WidenIncomKoensayrScopeModifier ad-hoc.
 */
public class Card_7_066_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("incom", "7_66");
                    put("warRoom", "1_139");
                    put("yavin4", "1_135");
                    put("jungle", "1_137");
                    put("xwing", "1_146");
                    put("ywing", "1_147");
                    put("speeder", "3_69");
                }},
                new HashMap<>() {{
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
    public void IncomCorporationReplacesAbility1PilotsOnXwingsAndT47sButNotYwings() {
        var scn = GetScenario();
        var incom = scn.GetLSCard("incom");
        var warRoom = scn.GetLSCard("warRoom");
        var yavin4 = scn.GetLSCard("yavin4");
        var jungle = scn.GetLSCard("jungle");
        var xwing = scn.GetLSCard("xwing");
        var ywing = scn.GetLSCard("ywing");
        var speeder = scn.GetLSCard("speeder");

        scn.StartGame();
        scn.MoveLocationToTable(yavin4);
        scn.MoveLocationToTable(warRoom);
        scn.MoveLocationToTable(jungle);
        scn.AttachCardsTo(warRoom, incom);
        scn.MoveCardsToLocation(yavin4, xwing, ywing);
        scn.MoveCardsToLocation(jungle, speeder);

        assertFalse(WidenIncomKoensayrScopeModifier.isActive(scn.game(), incom));
        // X-wing printed 3 +1; Y-wing printed 2 (not in printed Incom filter); T-47 printed 3 +1.
        assertEquals(4, scn.GetPower(xwing), scn.epsilon);
        assertEquals(2, scn.GetPower(ywing), scn.epsilon);
        assertEquals(4, scn.GetPower(speeder), scn.epsilon);
    }

    @Test
    public void IncomCorporationWithWidenAffectsNonUniqueYwingsAndStillAffectsT47s() {
        var scn = GetScenario();
        var incom = scn.GetLSCard("incom");
        var warRoom = scn.GetLSCard("warRoom");
        var yavin4 = scn.GetLSCard("yavin4");
        var jungle = scn.GetLSCard("jungle");
        var xwing = scn.GetLSCard("xwing");
        var ywing = scn.GetLSCard("ywing");
        var speeder = scn.GetLSCard("speeder");

        scn.StartGame();
        scn.MoveLocationToTable(yavin4);
        scn.MoveLocationToTable(warRoom);
        scn.MoveLocationToTable(jungle);
        scn.AttachCardsTo(warRoom, incom);
        scn.MoveCardsToLocation(yavin4, xwing, ywing);
        scn.MoveCardsToLocation(jungle, speeder);
        scn.ApplyAdHocModifier(new WidenIncomKoensayrScopeModifier(warRoom));
        scn.SkipToPhase(Phase.CONTROL);

        assertTrue(WidenIncomKoensayrScopeModifier.isActive(scn.game(), incom));
        assertEquals(4, scn.GetPower(xwing), scn.epsilon);
        // Widen is the only reason the Y-wing gets +1.
        assertEquals(3, scn.GetPower(ywing), scn.epsilon);
        assertEquals(4, scn.GetPower(speeder), scn.epsilon);
    }
}
