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

/** Original I Want Every Part Of This Ship Checked (2010-06-18 sheet), a version of 1271_10: same play, sheet wording. */
public class Card_2271_010_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
        }}, new HashMap<>() {{
            put("version", "2271_10");
            put("current", "1271_10");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalIWantEveryPartIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2271_10"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_10"), "1271_10", "",
                LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21),
                "Deploy on table. Once per game, if Obi-Wan on table, may [download] Vader. May place this Effect in Used Pile to [upload] a [Grabber]. If you just attempted to [download] or [upload] a card, and the card was not found, may place this Effect in Lost Pile to search your Force Pile for that card; reshuffle.");
    }

    @Test
    public void OriginalIWantEveryPartIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, true, "version", "current", (s, c) -> {
            if (c != null) s.MoveCardsToDSSideOfTable(c);
        });
    }
}
