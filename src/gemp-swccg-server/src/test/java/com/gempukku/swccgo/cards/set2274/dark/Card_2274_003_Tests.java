package com.gempukku.swccgo.cards.set2274.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original Baktoid Armor Workshop (2011-12-16 sheet), a version of 1274_3: same play, sheet wording. */
public class Card_2274_003_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
        }}, new HashMap<>() {{
            put("version", "2274_3");
            put("current", "1274_3");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalBaktoidArmorWorkshopIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2274_3"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1274_3"), "1274_3", "",
                LocalDate.of(2011, 12, 16), LocalDate.of(2012, 7, 21),
                "Deploy on table. Your [Theed Palace] vehicles are defense value and immunity to attrition +1. May [download] Deployment Orders. Once per game, if Deployment Orders on table, may [download] an exterior battleground site not on table. Once per game, may retrieve a [Theed Palace] vehicle into hand. [Immune to Alter.]");
    }

    @Test
    public void OriginalBaktoidArmorWorkshopIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, true, "version", "current", (s, c) -> {
            if (c != null) s.MoveCardsToDSSideOfTable(c);
        });
    }
}
