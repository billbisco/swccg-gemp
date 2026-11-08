package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/** Original Galen Marek, Starkiller (Dec 2009 sheet, printed as Galen, Secret Apprentice), a version of 1240_8. */
public class Card_2240_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("galen", "2240_8");
                    put("current", "1240_8");
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
    public void GalenIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("galen").getBlueprint(),
                "Galen's forfeit may not be reduced. Power and defense value +2 while with Fallen Jedi. If defending a battle alone (or defending with Black Leader), may be targeted by Force Lightning. Immune to Clash Of Sabers and attrition < 4.",
                "1240_8", "", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27));
    }

    @Test
    public void GalenIsTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : java.util.List.of(true, false)) {
            for (boolean otherOut : java.util.List.of(false, true)) {
                var scn = GetScenario();
                var onTable = scn.GetDSCard(currentOnTable ? "current" : "galen");
                var inHand = scn.GetDSCard(currentOnTable ? "galen" : "current");
                scn.StartGame();
                if (otherOut)
                    scn.MoveCardsToLocation(scn.GetDSStartingLocation(), onTable);
                scn.MoveCardsToDSHand(inHand);
                scn.SkipToDSTurn(Phase.DEPLOY);
                scn.EnsureDSForcePile(8);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.DSDeployAvailable(inHand));
            }
        }
    }
}
