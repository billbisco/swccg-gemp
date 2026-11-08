package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/** Coruscant: Private Platform (Docking Bay) (1273_16): the printed (latest) text; Light: only transit from here requires 2 Force. */
public class Card_601_150_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("platform", "601_150");
            put("db94", "1_291");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private float transitCost(VirtualTableScenario s, PhysicalCard card, PhysicalCard from, PhysicalCard to) {
        return s.game().getModifiersQuerying().getDockingBayTransitCost(s.game().getGameState(), card, from, to, 0);
    }

    @Test
    public void PrivatePlatformShowsThePrintedText() {
        var b = com.gempukku.swccgo.framework.VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_16");
        org.junit.Assert.assertEquals("All docking bay transit from same site as Insidious Prisoner is free. Your docking bay transit from here requires 2 Force.", b.getLocationLightSideGameText());
        org.junit.Assert.assertEquals("If deployed by your [Block 7] objective, may not be separated from Palpatine's Quarters. Your docking bay transit to or from here is free.", b.getLocationDarkSideGameText());
        org.junit.Assert.assertFalse(b.getLocationLightSideGameText().contains("transit to or from here requires") || b.getLocationDarkSideGameText().contains("transit to or from here requires"));
    }

    @Test
    public void PrivatePlatformLightTransitFromHereCostsTwoMoreButTransitToHereDoesNot() {
        var s = scenario();
        var platform = s.GetDSCard("platform");
        var db94 = s.GetDSCard("db94");
        var luke = s.GetLSCard("luke");
        s.StartGame();
        s.MoveLocationToTable(platform);
        s.MoveLocationToTable(db94);
        s.MoveCardsToLocation(db94, luke);
        float toHere = transitCost(s, luke, db94, platform);
        s.MoveCardsToLocation(platform, luke);
        float fromHere = transitCost(s, luke, platform, db94);
        // from here: the platform's 2 (Docking Bay 94 adds nothing to transit to it)
        assertEquals(2, fromHere, s.epsilon);
        // to here: only Docking Bay 94's own "from here" cost; the platform no longer adds 2 (the old GEMP-only "to or from" made it 4)
        assertEquals(2, toHere, s.epsilon);
    }
}
