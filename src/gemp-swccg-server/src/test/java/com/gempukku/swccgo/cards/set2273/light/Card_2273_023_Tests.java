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

/** Original Captain Antilles (2011-06-03 sheet), a version of 1273_23: same play, sheet wording. */
public class Card_2273_023_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("camp", "1_131");
            put("version", "2273_23");
            put("current", "1273_23");
        }}, new HashMap<>() {{
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalCaptainAntillesIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_23"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_23"), "1273_23", "",
                LocalDate.of(2011, 6, 3), LocalDate.of(2012, 7, 21),
                "[Pilot] 2, 3: any corvette. While piloting a corvette, it is immune to attrition < 5 and, once per turn, may peek at a card in opponent's hand. Once per game, during battle (unless Vader here) may use 2 Force to move starship he pilots away (for free).");
    }

    @Test
    public void OriginalCaptainAntillesIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, false, "version", "current", (s, c) -> {
            s.MoveLocationToTable(s.GetLSCard("camp")); if (c != null) s.MoveCardsToLocation(s.GetLSCard("camp"), c);
        });
    }
}
