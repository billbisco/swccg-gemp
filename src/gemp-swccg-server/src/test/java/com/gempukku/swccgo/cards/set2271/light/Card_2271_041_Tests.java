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

/** Original Oh, He's Not Dead, Not Yet (2010-06-18 sheet), a version of 1271_41: same play, sheet wording. */
public class Card_2271_041_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("version", "2271_41");
            put("current", "1271_41");
        }}, new HashMap<>() {{
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalOhHesNotDeadNotYetIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2271_41"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_41"), "1271_41", "",
                LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21),
                "Deploy on table. Once per game, if Vader on table, may [download] Obi-Wan. May place this Effect in Used Pile to [upload] a [Grabber]. If you just attempted to [download] or [upload] a card, and the card was not found, may place this Effect in Lost Pile to search your Force Pile for that card; reshuffle.");
    }

    @Test
    public void OriginalOhHesNotDeadNotYetIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, false, "version", "current", (s, c) -> {
            if (c != null) s.MoveCardsToLSSideOfTable(c);
        });
    }
}
