package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.cards.set1230.light.Card_1230_116_Tests;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Original Mon Calamari Star Cruiser (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_116.
 * Runs every gameplay test of Card_1230_116_Tests against the version (it plays the same), and replaces the
 * identity test with the version's own.
 */
public class Card_2230_116_Tests extends Card_1230_116_Tests {
    @Override
    protected String cardId() {
        return "2230_116";
    }

    @Override
    @Test
    public void MonCalamariStarCruiserHasTheLatestText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1230_116"), scn.GetLSCard("cruiser").getBlueprint(),
                "Deploys -1 (to a minimum of 3) for each Imperial starship on table. May add 5 pilots, 6 passengers, 1 vehicle, and 3 starfighters. Permanent pilot provides ability of 2.",
                "1230_116", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }
}
