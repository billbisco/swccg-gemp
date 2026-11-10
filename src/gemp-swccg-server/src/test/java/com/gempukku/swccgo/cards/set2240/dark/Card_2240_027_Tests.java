package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;

/** Original Imperial Propaganda (V) (Dec 2009 sheet), a version of 1240_27. */
public class Card_2240_027_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("senate", "12_75");
                }},
                new HashMap<>() {{
                    put("propaganda", "2240_27");
                    put("current", "1240_27");
                    put("original", "7_230");
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
    public void ImperialPropagandaIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("propaganda").getBlueprint(),
                "If you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 2. Whenever you lose force during opponent's control phase (except from a Force drain at a battleground or your card), that loss is cumulatively -1 (to a minimum of 1).",
                "1240_27", "", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 6, 14));
    }

    private PhysicalCardImpl otherCopyAfterTableChange(String key) {
        var scn = GetScenario();
        var effect = scn.GetDSCard(key);
        var original = scn.GetDSCard("original");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(effect, original);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveLocationToTable(scn.GetLSCard("senate"));
        scn.SkipToPhase(Phase.BATTLE);
        return original;
    }

    @Test
    public void OriginalImperialPropagandaDoesNotCancelTheNonVirtualBlock4Card() {
        //test1: no "Non-[Virtual Block 4] ImperialPropaganda is canceled." on the original text
        assertInZone(Zone.SIDE_OF_TABLE, otherCopyAfterTableChange("propaganda")); //test1
    }

    @Test
    public void CurrentImperialPropagandaCancelsTheNonVirtualBlock4Card() {
        assertInZone(Zone.LOST_PILE, otherCopyAfterTableChange("current"));
    }
}
