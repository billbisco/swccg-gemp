package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class Card_7_030_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
            new HashMap<>() {{
                put("tarn", "7_30");
                put("bwing", "9_66");
                put("missiles", "9_87");
                put("hoth", "3_55");
            }},
            new HashMap<>() {{
                put("executor", "4_167");
                put("margo", "213_9");
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

    @Test
    public void LieutenantTarnMisonResetsForfeitOfCharactersAboardTargetToZero() {
        var scn = GetScenario();

        var tarn = scn.GetLSCard("tarn");
        var bwing = scn.GetLSCard("bwing");
        var missiles = scn.GetLSCard("missiles");
        var hoth = scn.GetLSCard("hoth");
        var executor = scn.GetDSCard("executor");
        var margo = scn.GetDSCard("margo");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLocation(hoth, bwing, executor);
        scn.BoardAsPilot(bwing, tarn);
        scn.BoardAsPassenger(executor, margo);
        scn.AttachCardsTo(bwing, missiles);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(hoth);
        scn.PassBattleStartResponses();

        assertEquals(3, scn.GetForfeit(margo));
        scn.LSUseCardAction(missiles);
        scn.LSChooseCard(executor);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertEquals(0, scn.GetForfeit(margo));
    }
}
