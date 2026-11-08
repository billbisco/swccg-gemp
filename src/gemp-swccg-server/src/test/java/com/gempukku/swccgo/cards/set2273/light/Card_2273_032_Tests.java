package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original Bacta Infirmary (2011-06-03 sheet), a version of 1273_32: same play, sheet wording. */
public class Card_2273_032_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("cot", "1273_42");
            put("version", "2273_32");
            put("current", "1273_32");
        }}, new HashMap<>() {{
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalBactaInfirmaryIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_32"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_32"), "1273_32", "",
                LocalDate.of(2011, 6, 3), LocalDate.of(2012, 7, 21),
                "If Center Of Tyranny on table, deploy on table. Insurrection is lost. Rogue Squadron pilots of ability < 3 are forfeit +1 and, if just lost from table, may be placed here. During your control phase, may use 2 Force to take any character from here into hand. [Immune to Alter.]");
    }

    @Test
    public void OriginalBactaInfirmaryIsTheSameUniqueCardAsTheCurrentCard() {
        for (int mode = 0; mode < 3; mode++) {
            var s = scenario();
            var version = s.GetLSCard("version");
            var current = s.GetLSCard("current");
            var inHand = mode == 1 ? current : version;
            s.StartGame();
            s.MoveCardsToLSHand(inHand);
            s.LSActivateForceCheat(5);
            s.SkipToLSTurn(Phase.DEPLOY);
            com.gempukku.swccgo.cards.set1273.light.Vb73LightTestSupport.putOnTable(s, s.LS, "1273_42");
            if (mode == 0)
                s.MoveCardsToLSSideOfTable(current);
            else if (mode == 1)
                s.MoveCardsToLSSideOfTable(version);
            s.SkipToNextTurn(s.LS);
            s.SkipToLSTurn(Phase.DEPLOY);
            assertEquals("mode " + mode, mode == 2, s.LSDeployAvailable(inHand));
        }
    }
}
