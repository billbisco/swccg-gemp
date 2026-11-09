package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set1273.dark.Card_1273_013_Tests;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Original Insidious Prisoner (Virtual Block 7.3 sheet, Jun 2011), a version of 1273_13. Runs every Insidious
 * Prisoner test on this version ("prisoner" = 2273_13); the clauses that differ are overridden.
 */
public class Card_2273_013_Tests extends Card_1273_013_Tests {
    @Override
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("obiwan", "1_21"); // Obi-Wan Kenobi (Jedi)
                    put("saber", "1_155"); // Jedi Lightsaber
                    put("prison", "1116_7"); // Blockade Flagship: Prison
                    put("palpatine", "12_28"); // Senator Palpatine
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("prisoner", "2273_13");
                    put("quarters", "1273_17");
                    put("platform", "601_150"); // Coruscant: Private Platform (Docking Bay)
                    put("trooper", "1_194");
                    put("original", "2273_13");
                    put("current", "1273_13");
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
    public void InsidiousPrisonerHasTheMasterListText() {
        var card = GetScenario().GetDSCard("original").getBlueprint();
        assertEquals("If A Stunning Move on table, deploy on Palpatine's Quarters (if [Virtual Block 5] We Have A Plan on table, this card always relocates to Palpatine if possible (even if a captive)); opponent may deploy a Jedi and/or a lightsaber here. Evil Is Everywhere: Opponent may not Force drain here. Adds one [Dark Side] at same site. If just lost, relocate to Blockade Flagship: Prison. Once per turn, if a player controls this site, they may have this card (unless on Palpatine) follow their first character to move from here using landspeed or docking bay transit to a non-shielded site.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1273_13", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2011, 6, 3), version.printedOn());
        assertEquals(LocalDate.of(2012, 7, 21), version.supersededOn());
        assertEquals("Insidious Prisoner", GameUtils.getFullName(card));
    }

    @Override
    @Test
    public void InsidiousPrisonerDeployedDuringATurnLetsOpponentDeployNothing() {
        // Sheet text has no timing: whenever it deploys, opponent may deploy a Jedi here (from Reserve Deck, as GEMP has
        // always played it)
        var scn = stunningMoveSetup("prisoner");
        var prisoner = scn.GetDSCard("prisoner");
        var obiwan = scn.GetLSCard("obiwan");
        scn.MoveCardsToTopOfLSReserveDeck(obiwan);
        scn.DSDeployCard(prisoner);
        assertTrue(offeredAction(scn, scn.LS, "Deploy a Jedi here"));
        scn.LSChooseAction("Deploy a Jedi here");
        offeredAction(scn, scn.LS, "(resolve the deploy)");
        assertEquals(scn.GetDSCard("quarters"), obiwan.getAtLocation());
    }
}
