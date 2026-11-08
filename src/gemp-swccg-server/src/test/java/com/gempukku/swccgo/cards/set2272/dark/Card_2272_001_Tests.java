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

/** Original General Nevar (2010-12-15 sheet), a version of 1272_1: same play, sheet wording. */
public class Card_2272_001_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
        }}, new HashMap<>() {{
            put("version", "2272_1");
            put("current", "1272_1");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalGeneralNevarIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2272_1"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1272_1"), "1272_1", "",
                LocalDate.of(2010, 12, 15), LocalDate.of(2012, 7, 21),
                "[Pilot] 2, 3: Blizzard 2. Deploys -1 on Hoth. Once per game, may [download] a marker site with < 2 [Dark Side] or a battleground (except Endor system or a war room). Once per game, if piloting at a site where no characters present, may peek at opponent's hand.");
    }

    @Test
    public void OriginalGeneralNevarIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, true, "version", "current", (s, c) -> {
            if (c != null) s.MoveCardsToLocation(s.GetDSStartingLocation(), c);
        });
    }
}
