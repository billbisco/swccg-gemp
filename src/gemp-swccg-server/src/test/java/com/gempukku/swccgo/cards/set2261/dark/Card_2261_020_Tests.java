package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.MayNotBeGrabbedModifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static org.junit.Assert.assertEquals;

/** Pre-errata A Useless Gesture (V) (Sep 2009 sheet), a version of 1261_20: no "(if possible)". */
public class Card_2261_020_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("grabbable", "1_72", "copy", "1_73")),
                new HashMap<>(Map.of("gesture", "2261_20", "current", "1261_20")), 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void AUselessGestureOriginalIdentity() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("gesture").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_20", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 8, 27),
                "Plays on table. In order to play an Interrupt from Lost Pile, opponent must first stack it here and use +1 Force for each card here, even if Interrupt is normally free.");
    }

    private boolean mayNotPlay(VirtualTableScenario scn, String name) {
        return !scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_PLAY,
                scn.GetLSCard(name)).isEmpty();
    }

    @Test
    public void AUselessGestureOriginalStopsInterruptsFromLostPileThatCannotBeStackedHere() {
        for (boolean original : java.util.List.of(true, false)) {
            var scn = scenario();
            var gesture = scn.GetDSCard(original ? "gesture" : "current");
            scn.StartGame();
            scn.MoveCardsToDSSideOfTable(gesture);
            scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("grabbable"), scn.GetLSCard("copy"));
            assertEquals(false, mayNotPlay(scn, "grabbable"));
            assertEquals(false, mayNotPlay(scn, "copy"));
            // an Interrupt that may not be 'grabbed' cannot be stacked here
            scn.ApplyAdHocModifier(new MayNotBeGrabbedModifier(gesture, Filters.sameCardId(scn.GetLSCard("copy"))));
            assertEquals("original=" + original, original, mayNotPlay(scn, "copy"));
            assertEquals(false, mayNotPlay(scn, "grabbable"));
        }
    }
}
