package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original Imperial Security Bureau (2010-06-18 sheet), a version of 1271_12: same play, sheet wording. */
public class Card_2271_012_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
        }}, new HashMap<>() {{
            put("version", "2271_12");
            put("current", "1271_12");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalImperialSecurityBureauIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2271_12"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_12"), "1271_12", "",
                LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21),
                "Deploy on table. Sunsdown is canceled. ISB agents who are leaders are deploy -2 to sites with 'ISB' in title. Once per game, may [upload] a [Setup] Mission. Look Sir, Droids and your Interrupts with 'advisor' in title may be played as follows: 'USED: Use 1 Force to [upload] an ISB agent.' [Immune to Alter.]");
    }

    @Test
    public void OriginalImperialSecurityBureauIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, true, "version", "current", (s, c) -> {
            if (c != null) s.MoveCardsToDSSideOfTable(c);
        });
    }
}
