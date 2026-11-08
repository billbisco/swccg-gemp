package com.gempukku.swccgo.cards.set2272.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original Where Are You Taking Them? (2010-12-15 sheet), a version of 1272_7: same play, sheet wording. */
public class Card_2272_007_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
        }}, new HashMap<>() {{
            put("version", "2272_7");
            put("current", "1272_7");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalWhereAreYouTakingThemIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2272_7"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1272_7"), "1272_7", "",
                LocalDate.of(2010, 12, 15), LocalDate.of(2012, 7, 21),
                "Deploy on table. At non-[Episode I] sites, immunity to attrition of [Coruscant] Jedi is limited to < 5. Your gametext on [Coruscant] systems is canceled. During your control phase, may say 'Coruscant?' and lose 1 Force to place a just-resolved Interrupt in owner's Lost Pile. [Immune to Alter.]");
    }

    @Test
    public void OriginalWhereAreYouTakingThemIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, true, "version", "current", (s, c) -> {
            if (c != null) s.MoveCardsToDSSideOfTable(c);
        });
    }
}
