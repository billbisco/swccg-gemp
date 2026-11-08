package com.gempukku.swccgo.cards.set2274.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original Motivator (2011-12-16 sheet), a version of 1274_17: same play, sheet wording. */
public class Card_2274_017_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("r2x2", "1_24");
            put("version", "2274_17");
            put("current", "1274_17");
        }}, new HashMap<>() {{
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalMotivatorIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2274_17"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1274_17"), "1274_17", "",
                LocalDate.of(2011, 12, 16), LocalDate.of(2012, 7, 21),
                "Deploy on your non-[Reflections 2] astromech droid. Droid is power +2, gains [Presence Droid], and if present at a site, draws one battle destiny if unable to otherwise. During any move phase, may place this device in Lost Pile to [upload] one What're You Tryin' To Push On Us? or R2-D2.");
    }

    @Test
    public void OriginalMotivatorIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, false, "version", "current", (s, c) -> {
            s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("r2x2")); if (c != null) s.AttachCardsTo(s.GetLSCard("r2x2"), c);
        });
    }
}
