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

/** Original Princess Leia, Lost Scion (2010-06-18 sheet), a version of 1271_34: same play, sheet wording. */
public class Card_2271_034_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("version", "2271_34");
            put("current", "1271_34");
        }}, new HashMap<>() {{
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalLeiaLostScionIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2271_34"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_34"), "1271_34", "",
                LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21),
                "While present at a battleground, Their Fire Has Gone Out Of The Universe flips and may not flip back. While armed with Anakin's Lightsaber, Leia's forfeit may not be reduced. Immune to attrition < 4.");
    }

    @Test
    public void OriginalLeiaLostScionIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, false, "version", "current", (s, c) -> {
            if (c != null) s.MoveCardsToLocation(s.GetLSStartingLocation(), c);
        });
    }
}
