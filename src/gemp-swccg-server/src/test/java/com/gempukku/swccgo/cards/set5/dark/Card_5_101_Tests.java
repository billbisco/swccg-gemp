package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_5_101_Tests {
    private static final String TAKE = "Take card into hand from Reserve Deck";

    @Test
    public void SheckilSearchFindsHumanShieldButNotOtherCards() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19")),
                new HashMap<>(Map.of("sheckil", "5_101", "shield", "5_145", "trooper", "1_194")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        var sheckil = scn.GetDSCard("sheckil");
        var shield = scn.GetDSCard("shield");
        var trooper = scn.GetDSCard("trooper");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), sheckil);
        scn.EnsureDSForcePile(2);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(shield, trooper);
        assertTrue(scn.DSCardActionAvailable(sheckil, TAKE));
        scn.DSUseCardAction(sheckil, TAKE);
        int selectable = -1;
        for (int i = 0; i < 20 && shield.getZone() != Zone.HAND; i++) {
            if (scn.DSHasCardChoiceAvailable(shield)) {
                // Only Human Shield may be chosen; the rest of the Reserve Deck is shown but not selectable.
                selectable = scn.DSGetSelectableCount();
                scn.DSChooseCard(shield);
                continue;
            }
            scn.PassAllResponses();
        }
        assertEquals(Zone.HAND, shield.getZone());
        assertEquals(1, selectable);
        assertTrue(trooper.getZone() == Zone.RESERVE_DECK || trooper.getZone() == Zone.TOP_OF_RESERVE_DECK);
    }
}
