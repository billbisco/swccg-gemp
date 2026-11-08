package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.cards.set1230.light.Card_1230_104_Tests;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Original Endor Celebration (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_104.
 * Runs every gameplay test of Card_1230_104_Tests against the version (it plays the same), and replaces the
 * identity test with the version's own.
 */
public class Card_2230_104_Tests extends Card_1230_104_Tests {
    @Override
    protected String cardId() {
        return "2230_104";
    }

    @Override
    @Test
    public void EndorCelebrationHasTheLatestText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1230_104"), scn.GetLSCard("celebration").getBlueprint(),
                "Cancel an Effect with 'occupation' in title. (Immune to Sense.) OR During your draw phase, if you have a Rebel scout alone at Back Door, draw destiny. If destiny < the number of your scouts on table, activate or retrieve 5 Force and place this Interrupt on bottom of Lost Pile.",
                "1230_104", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }
}
