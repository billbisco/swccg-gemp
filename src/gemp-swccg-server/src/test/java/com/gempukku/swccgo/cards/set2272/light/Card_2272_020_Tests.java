package com.gempukku.swccgo.cards.set2272.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original Declaration Of Rebellion (2010-12-15 sheet), a version of 1272_20: same play, sheet wording. */
public class Card_2272_020_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("version", "2272_20");
            put("current", "1272_20");
        }}, new HashMap<>() {{
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalDeclarationOfRebellionIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2272_20"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1272_20"), "1272_20", "",
                LocalDate.of(2010, 12, 15), LocalDate.of(2012, 7, 21),
                "Deploy on table. Bail, Leia, and Mon Mothma are Insurgents. While an Insurgent present at a battleground, your total battle destiny is +1, and, if with your non-unique character, each may forfeit for 4 when hit. Once per game may [upload] K'lor'slug. [Immune to Alter.]");
    }

    @Test
    public void OriginalDeclarationOfRebellionIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, false, "version", "current", (s, c) -> {
            if (c != null) s.MoveCardsToLSSideOfTable(c);
        });
    }
}
