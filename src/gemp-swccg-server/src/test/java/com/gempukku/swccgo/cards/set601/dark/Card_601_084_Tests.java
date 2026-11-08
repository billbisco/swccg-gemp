package com.gempukku.swccgo.cards.set601.dark;

import org.junit.Test;

/** Latest (Legacy) text of 1272_1. */
public class Card_601_084_Tests {
    @Test
    public void GeneralNevarShowsTheLatestText() {
        String t = com.gempukku.swccgo.framework.VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1272_1").getGameText();
        org.junit.Assert.assertEquals("[Pilot] 2, 3: Blizzard 2. Deploys -1 on Hoth. Once per game, may [download] a marker site with < 2 [Dark Side] or a battleground (except Endor system or a war room). Once per game, if piloting at a site where no characters present, may peek at opponent's hand.", t);
    }
}
