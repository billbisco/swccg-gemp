package com.gempukku.swccgo.cards.set2283.dark;

import com.gempukku.swccgo.cards.set1283.dark.Card_1283_003_Tests;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Original Lady Valarian (Virtual Block 8.3 sheet, Mar 2013), a version of 1283_3. Runs every Lady Valarian test on
 * this version ("valarian" = 2283_3); the clauses that differ are overridden.
 */
public class Card_2283_003_Tests extends Card_1283_003_Tests {
    @Override
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("valarian", "2283_3");
                    put("bib", "13_55"); // "While with Jabba, power +3"
                    put("boba", "108_5"); // "Adds one battle destiny if with Han or Jabba"
                    put("jabba", "6_109");
                    put("no_bargain", "1115_56"); // non-[Lightsaber] alien females are Mistryl
                    put("trooper", "1_194");
                    put("prize", "10_42"); // Jabba's Prize
                    put("original", "2283_3");
                    put("current", "1283_3");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Override
    @Test
    public void LadyValarianHasTheMasterListText() {
        var scn = GetScenario();
        var card = scn.GetDSCard("original").getBlueprint();
        assertEquals("Your total battle destiny here is +1. Unless Jabba on table, 'Jabba' in the game text of your cards (except Hutt Bounty) may be treated as 'Lady Valarian.' Once per turn, may target a Mistryl of ability > 2 here; target gains Heroic until end of turn.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1283_3", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2013, 3, 28), version.printedOn());
        assertEquals(LocalDate.of(2013, 4, 27), version.supersededOn());
        assertEquals("Lady Valarian", GameUtils.getFullName(card));
    }

    @Override
    @Test
    public void LadyValarianJabbaInJabbasPrizeIsNotTreatedAsLadyValarian() {
        // Original "(except Hutt Bounty)": Jabba's Prize is not excepted
        var result = jabbaMayBeLadyValarianOnBibAndPrize(GetScenario(), "original");
        assertTrue(result[0]);
        assertTrue(result[1]);
    }
}
