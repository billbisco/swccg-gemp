package com.gempukku.swccgo.cards.set601.light;

import org.junit.Test;

/** Communing (1273_33): the current class shows the latest (PC-E2) text. */
public class Card_601_041_Tests {
    @Test
    public void CommuningShowsTheLatestPcE2Text() {
        String t = com.gempukku.swccgo.framework.VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_33").getGameText();
        org.junit.Assert.assertEquals("If your starting location has exactly 2 [Light Side Force], play instead of a starting interrupt. deploy from Reserve Deck two always [Immune to Alter] Effects; reshuffle. Deploy on table; take into hand and stack a Jedi here from Reserve Deck (that Jedi is 'communing'); reshuffle. \nLuminous Beings: Whenever a Jedi is lost (or placed out of play) from table, may stack it here. Jedi stacked here are considered out of play. You generate +1 Force for each card stacked here. I Can't Believe He's Gone Is canceled. Once per turn, if two cards here, may use 1 Force to look at top card of Reserve Deck, Force Pile, and/or Used Pile; return one card to each deck or pile.", t);
    }
}
