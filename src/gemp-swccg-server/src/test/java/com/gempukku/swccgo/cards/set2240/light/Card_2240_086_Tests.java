package com.gempukku.swccgo.cards.set2240.light;

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

/** Original Imperial Atrocity (V) (Dec 2009 sheet), a version of 1240_86. */
public class Card_2240_086_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("senate", "12_75");
                    put("atrocity", "2240_86");
                    put("current", "1240_86");
                    put("original", "7_65");
                }},
                new HashMap<>() {{
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
    public void ImperialAtrocityIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetLSCard("current").getBlueprint(), scn.GetLSCard("atrocity").getBlueprint(),
                "If you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 2. Whenever you lose Force during opponent's control phase (except from a Force drain at a battleground or your card) that loss is cumulatively -1 (to a minimum of 1).",
                "1240_86", "", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27));
    }

    private PhysicalCardImpl otherCopyAfterTableChange(String key) {
        var scn = GetScenario();
        var effect = scn.GetLSCard(key);
        var original = scn.GetLSCard("original");
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(effect, original);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveLocationToTable(scn.GetLSCard("senate"));
        scn.SkipToPhase(Phase.BATTLE);
        return original;
    }

    @Test
    public void OriginalImperialAtrocityDoesNotCancelTheNonVirtualBlock4Card() {
        //test1: no "Non-[Virtual Block 4] ImperialAtrocity is canceled." on the original text
        assertInZone(Zone.SIDE_OF_TABLE, otherCopyAfterTableChange("atrocity")); //test1
    }

    @Test
    public void CurrentImperialAtrocityCancelsTheNonVirtualBlock4Card() {
        assertInZone(Zone.LOST_PILE, otherCopyAfterTableChange("current"));
    }
}
