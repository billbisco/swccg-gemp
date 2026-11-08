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

/** Original I'll Take Them Myself (2010-06-18 sheet), a version of 1271_11: same play, sheet wording. */
public class Card_2271_011_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
        }}, new HashMap<>() {{
            put("version", "2271_11");
            put("current", "1271_11");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalIllTakeThemMyselfIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2271_11"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_11"), "1271_11", "",
                LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21),
                "Deploy on table. Once per game, may [upload] a [Setup] card. May place this Effect in Used Pile to [upload] an Immediate Effect. May place this Effect in Lost Pile to cancel a Force drain. [Immune to Alter.]");
    }

    @Test
    public void OriginalIllTakeThemMyselfIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, true, "version", "current", (s, c) -> {
            if (c != null) s.MoveCardsToDSSideOfTable(c);
        });
    }
}
