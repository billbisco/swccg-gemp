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

/** Original Dack Ralter (V) (2011-06-03 sheet), a version of 1273_22: same play, sheet wording. */
public class Card_2273_022_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("camp", "1_131");
            put("version", "2273_22");
            put("current", "1273_22");
        }}, new HashMap<>() {{
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalDackRalterIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_22"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_22"), "1273_22", "",
                LocalDate.of(2011, 6, 3), LocalDate.of(2012, 7, 21),
                "[Pilot] 2. Heroic. Rogue Squadron pilot. During battle, when Dack (or a starship or vehicle he pilots) fires a weapon, may add 1 to total weapon destiny and hit targets are power -2. If Dack about to leave table, may place him in Used Pile instead.");
    }

    @Test
    public void OriginalDackRalterIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, false, "version", "current", (s, c) -> {
            s.MoveLocationToTable(s.GetLSCard("camp")); if (c != null) s.MoveCardsToLocation(s.GetLSCard("camp"), c);
        });
    }
}
