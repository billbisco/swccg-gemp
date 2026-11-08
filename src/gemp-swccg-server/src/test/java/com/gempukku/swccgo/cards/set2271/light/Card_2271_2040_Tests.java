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

/** Massassi Base Sentry as on the PC website card list (PC-E2, by 2012-07-21), a version of 1271_40. */
public class Card_2271_2040_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("yavin", "1_135");
            put("version", "2271_2040");
            put("current", "1271_40");
        }}, new HashMap<>() {{
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void CardListMassassiBaseSentryIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2271_2040"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_40"), "1271_40", "PC-E2",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27),
                "Deploy on Yavin 4; it is 'liberated.' During battle at sites related to other 'liberated' systems, may add 2 to power of each Rebel warrior (to a maximum of 4). Once per turn, while you control this system, may retrieve 1 Force when you Force drain at another 'liberated' system.");
    }

    @Test
    public void CardListMassassiBaseSentryIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, false, "version", "current", (s, c) -> {
            s.MoveLocationToTable(s.GetLSCard("yavin")); if (c != null) s.AttachCardsTo(s.GetLSCard("yavin"), c);
        });
    }
}
