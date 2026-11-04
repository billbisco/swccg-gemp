package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * swccg-guild/work#145, game option "Shields: no play limit": a Starting Effect's "N times per game, may play a
 * Defensive Shield from here" has no limit, and the Starting Effect may hold any number of shields. Without the
 * option, Fear Is My Ally plays three (Card_1200_004_Tests covers the fourth being refused).
 */
public class ShieldsNoPlayLimitTests {
    private VirtualTableScenario scenario(boolean noLimit) {
        VirtualTableScenario scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("fear", "13_69"); // Fear Is My Ally
                    put("s1", "13_81");
                    put("s2", "13_54");
                    put("s3", "13_84");
                    put("s4", "13_51");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open);
        scn.gameState().setShieldsNoPlayLimit(noLimit);
        return scn;
    }

    private void awaitDSControl(VirtualTableScenario scn) {
        for (int i = 0; i < 12 && !scn.AwaitingDSControlPhaseActions(); i++) {
            if (scn.LSAnyDecisionsAvailable() && !scn.DSAnyDecisionsAvailable()) {
                scn.LSPass();
                continue;
            }
            if (scn.DSAnyDecisionsAvailable() && !scn.AwaitingDSControlPhaseActions()) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(scn.AwaitingDSControlPhaseActions());
    }

    private void playStackedShield(VirtualTableScenario scn, PhysicalCardImpl starting, PhysicalCardImpl shield) {
        assertTrue(scn.DSCardActionAvailable(starting, "Defensive Shield"));
        scn.DSUseCardAction(starting, "Defensive Shield");
        scn.DSChooseCard(shield);
        scn.PassAllResponses();
        assertTrue(shield.getZone().isInPlay());
        awaitDSControl(scn);
    }

    @Test
    public void ShieldsNoPlayLimitLetsFearIsMyAllyPlayAFourthShield() {
        VirtualTableScenario scn = scenario(true);
        PhysicalCardImpl fear = scn.GetDSCard("fear");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(fear);
        scn.StackCardsOn(fear, scn.GetDSCard("s1"), scn.GetDSCard("s2"), scn.GetDSCard("s3"), scn.GetDSCard("s4"));
        scn.SkipToDSTurn(Phase.CONTROL);
        awaitDSControl(scn);

        playStackedShield(scn, fear, scn.GetDSCard("s1"));
        playStackedShield(scn, fear, scn.GetDSCard("s2"));
        playStackedShield(scn, fear, scn.GetDSCard("s3"));
        playStackedShield(scn, fear, scn.GetDSCard("s4"));
    }

    @Test
    public void ShieldsNoPlayLimitLiftsTheStackingCap() {
        VirtualTableScenario off = scenario(false);
        VirtualTableScenario on = scenario(true);
        assertEquals(10, GameConditions.startingEffectShieldCap(off.game(), 10));
        assertEquals(Integer.MAX_VALUE, GameConditions.startingEffectShieldCap(on.game(), 10));
    }

    @Test
    public void WithoutTheOptionTheThirdShieldIsTheLast() {
        VirtualTableScenario scn = scenario(false);
        PhysicalCardImpl fear = scn.GetDSCard("fear");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(fear);
        scn.StackCardsOn(fear, scn.GetDSCard("s1"), scn.GetDSCard("s2"), scn.GetDSCard("s3"), scn.GetDSCard("s4"));
        scn.SkipToDSTurn(Phase.CONTROL);
        awaitDSControl(scn);

        playStackedShield(scn, fear, scn.GetDSCard("s1"));
        playStackedShield(scn, fear, scn.GetDSCard("s2"));
        playStackedShield(scn, fear, scn.GetDSCard("s3"));
        assertFalse(scn.DSCardActionAvailable(fear, "Defensive Shield"));
    }
}
