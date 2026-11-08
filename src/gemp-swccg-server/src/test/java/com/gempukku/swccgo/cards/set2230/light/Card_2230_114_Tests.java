package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.cards.set1230.light.Card_1230_114_Tests;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Original Tydirium (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_114.
 * Runs every gameplay test of Card_1230_114_Tests against the version (it plays the same), and replaces the
 * identity test with the version's own.
 */
public class Card_2230_114_Tests extends Card_1230_114_Tests {
    @Override
    protected String cardId() {
        return "2230_114";
    }

    @Override
    @Test
    public void TydiriumHasTheLatestText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1230_114"), scn.GetLSCard("tydirium").getBlueprint(),
                "May deploy (with a pilot) without presence or Force icons. May add 2 pilots and 6 passengers. May forfeit a scout aboard to cancel all remaining battle damage against you here.",
                "1230_114", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }
}
