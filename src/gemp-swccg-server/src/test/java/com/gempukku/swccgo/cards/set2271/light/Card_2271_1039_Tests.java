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

/** Luke, Trust Me PC-E1 (no Epic Events on table or played at start), a version of 1271_39: same play as the current card. */
public class Card_2271_1039_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("version", "2271_1039");
            put("current", "1271_39");
        }}, new HashMap<>() {{
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ErrataLukeTrustMeIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2271_1039"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_39"), "1271_39", "PC-E1",
                LocalDate.of(2012, 7, 21), LocalDate.of(2012, 7, 21),
                "If you have no Epic Events on table (and did not play an Epic Event at the start of the game), deploy on table. Once per game, may [upload] a [Setup] card. May place this Effect in Used Pile to [upload] an Immediate Effect. May place this Effect in Lost Pile to cancel a Force drain. [Immune to Alter.]");
    }

    @Test
    public void ErrataLukeTrustMeIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, false, "version", "current", (s, c) -> {
            if (c != null) s.MoveCardsToLSSideOfTable(c);
        });
    }
}
