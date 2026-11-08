package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.cards.set1230.light.Card_1230_118_Tests;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Original Enhanced Proton Torpedoes (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_118.
 * Runs every gameplay test of Card_1230_118_Tests against the version (it plays the same), and replaces the
 * identity test with the version's own.
 */
public class Card_2230_118_Tests extends Card_1230_118_Tests {
    @Override
    protected String cardId() {
        return "2230_118";
    }

    @Override
    @Test
    public void EnhancedProtonTorpedoesHasTheLatestText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1230_118"), scn.GetLSCard("torps").getBlueprint(),
                "Use 1 Force to deploy on your starfighter. May target a starship for free. Draw destiny (If on a Heroic starfighter, may first use 1 Force to add 3 to total). Target hit if total destiny > defense value.",
                "1230_118", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }
}
