package com.gempukku.swccgo.cards.set601.dark;

import org.junit.Test;

/** Latest (Legacy) text of 1271_3. */
public class Card_601_182_Tests {
    @Test
    public void DS615ShowsTheLatestText() {
        String t = com.gempukku.swccgo.framework.VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_3").getGameText();
        org.junit.Assert.assertEquals("[Pilot] 2, 3: Black 5. While piloting Black 5, it is maneuver +1 and he draws one battle destiny if not able to otherwise. If at opponent's system, instead of Force draining here, may take any one card into hand from Force Pile; reshuffle.", t);
        org.junit.Assert.assertFalse(t.contains("[Pilot] 2, 3: Black 5"));
        org.junit.Assert.assertFalse(t.contains("any one card"));
    }
}
