package com.gempukku.swccgo.cards.set2271.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original Biggs, Rogue Legend (2010-06-18 sheet), a version of 1271_31: same play, sheet wording. */
public class Card_2271_031_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("version", "2271_31");
            put("current", "1271_31");
        }}, new HashMap<>() {{
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalBiggsRogueLegendIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2271_31"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_31"), "1271_31", "",
                LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21),
                "[Pilot] 2, 3: Red 3. While piloting Red 3, it is maneuver +1 and he draws one battle destiny if not able to otherwise. If at opponent's system, instead of Force draining here, may take any one card into hand from Force Pile; reshuffle.");
    }

    @Test
    public void OriginalBiggsRogueLegendIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, false, "version", "current", (s, c) -> {
            if (c != null) s.MoveCardsToLocation(s.GetLSStartingLocation(), c);
        });
    }
}
