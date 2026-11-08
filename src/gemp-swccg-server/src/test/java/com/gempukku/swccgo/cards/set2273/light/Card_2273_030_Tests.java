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

/** Original Wes Janson, Rogue Veteran (2011-06-03 sheet), a version of 1273_30: same play, sheet wording. */
public class Card_2273_030_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("camp", "1_131");
            put("version", "2273_30");
            put("current", "1273_30");
        }}, new HashMap<>() {{
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalWesJansonIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_30"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_30"), "1273_30", "",
                LocalDate.of(2011, 6, 3), LocalDate.of(2012, 7, 21),
                "[Pilot] 2. Once per turn, may [download] (or deploy from hand) Disruptor Pistol for free on Wes. May fire Disruptor Pistol for 1 Force. Power and forfeit +1 while with Hobbie or Wedge. If about to leave table, place cards on Wes in owner's Used Pile.");
    }

    @Test
    public void OriginalWesJansonIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, false, "version", "current", (s, c) -> {
            s.MoveLocationToTable(s.GetLSCard("camp")); if (c != null) s.MoveCardsToLocation(s.GetLSCard("camp"), c);
        });
    }
}
