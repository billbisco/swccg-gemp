package com.gempukku.swccgo.cards.set601.dark;

import org.junit.Test;

/** Latest (Legacy) text of 1273_4. */
public class Card_601_083_Tests {
    @Test
    public void GarindanShowsTheLatestText() {
        String t = com.gempukku.swccgo.framework.VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_4").getGameText();
        org.junit.Assert.assertEquals("Adds one to the power of anything he pilots. May deploy -1 as a 'react'; shuffle any Reserve Deck. Ignores Objective deployment restrictions. Unless alone, may place Garindan in Used Pile to cancel a just drawn Weapons Destiny here. [Virtual Block 2] Imperial Domination ignores Garindan.", t);
        org.junit.Assert.assertFalse(t.contains("just drawn weapon destiny"));
    }
}
