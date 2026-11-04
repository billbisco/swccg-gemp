package com.gempukku.swccgo.game;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;

/**
 * swccg-guild/work#136: a pile search shows cards through getBlueprintId(boolean), which passes no game state. With
 * an Objective on top of the Reserve Deck (Anything Goes decks may hold several), that threw a NullPointerException.
 */
public class TopOfReserveDeckBlueprintTests {
    @Test
    public void TopOfReserveDeckCardShownWithoutAGameStateDoesNotCrash() {
        VirtualTableScenario scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>() {{ put("objective", "111_6"); }});
        PhysicalCardImpl objective = scn.GetDSCard("objective");
        objective.setZone(Zone.TOP_OF_RESERVE_DECK);
        assertNotNull(objective.getBlueprintId(false));
        assertNotNull(objective.getBlueprintId(true));
    }
}
