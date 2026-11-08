package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.cards.set1230.light.Card_1230_105_Tests;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Original Ke Chu Ke Kukuta? (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_105.
 * Runs every gameplay test of Card_1230_105_Tests against the version (it plays the same), and replaces the
 * identity test with the version's own.
 */
public class Card_2230_105_Tests extends Card_1230_105_Tests {
    @Override
    protected String cardId() {
        return "2230_105";
    }

    @Override
    @Test
    public void KeChuKeKukutaHasTheLatestText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1230_105"), scn.GetLSCard("kechu").getBlueprint(),
                "If you have an information broker on table, draw destiny. If destiny > the number of opponent's characters on table, may use 1 Force to take any one card into hand from Reserve Deck; reshuffle.",
                "1230_105", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }
}
