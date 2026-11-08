package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original Use The Force (2011-06-03 sheet), a version of 1273_34: the destiny bonus is for a draw this turn only. Played, not deployed, so no deploy-from-hand uniqueness test. */
public class Card_2273_034_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("communing", "601_41");
            put("stacked1", "1_028");
            put("lost", "1_028");
            put("version", "2273_34");
            put("current", "1273_34");
        }}, new HashMap<>() {{
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalUseTheForceIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_34"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_34"), "1273_34", "",
                LocalDate.of(2011, 6, 3), LocalDate.of(2012, 7, 21),
                "Choose up to X of the following in any order, where X = number of cards stacked on Communing: Peek at the top card of any deck or pile; may then shuffle it. OR Draw 2 cards from Reserve Deck. OR Retrieve 1 Force. OR Activate X Force. OR Your next destiny draw this turn is +X.");
    }

    @Test
    public void OriginalUseTheForceDestinyBonusEndsWithTheTurn() {
        assertEquals(3f, com.gempukku.swccgo.cards.set1273.light.Card_1273_034_Tests.destinyDrawnNextTurn(scenario(), "version", "Your next destiny draw this turn is +X"), 0.01f);
    }
}
