package com.gempukku.swccgo.cards.set203.dark;

import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Ortugg (V) and Ortugg (Jabba's Palace) share the Ortugg persona (Bill, 2026-11-08). */
public class Card_203_028_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("ortuggv", "203_28");
                    put("ortugg", "6_118");
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation, // Tatooine: Marketplace (Ortugg deploys only on Tatooine)
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void BothOrtuggsHaveTheOrtuggPersona() {
        var scn = GetScenario();
        for (String key : new String[] { "ortuggv", "ortugg" }) {
            var b = scn.GetDSCard(key).getBlueprint();
            assertEquals("Ortugg", b.getTitle());
            scn.BlueprintPersonaCheck(b, new ArrayList<>() {{ add(Persona.ORTUGG); }});
        }
        assertTrue(Filters.samePersonaAs(scn.GetDSCard("ortugg")).accepts(scn.game(), scn.GetDSCard("ortuggv")));
        // Ortugg's Ax stays its own (weapon) persona
        assertEquals("Ortugg's Ax", scn.GetDSCard("ortuggv").getBlueprint().getPermanentWeapon(scn.GetDSCard("ortuggv")).getTitle(scn.game()));
    }

    private VirtualTableScenario oneOrtuggOnTableOtherInHand(String onTableKey, String inHandKey, boolean otherOut) {
        var scn = GetScenario();
        scn.StartGame();
        if (otherOut)
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard(onTableKey));
        scn.MoveCardsToDSHand(scn.GetDSCard(inHandKey));
        scn.EnsureDSForcePile(8);
        scn.SkipToDSTurn(Phase.DEPLOY);
        return scn;
    }

    @Test
    public void TwoOrtuggVersionsMayNotBothBeOnTable() {
        //test1: each version deploys when the other is not out
        //test2: neither deploys as a second Ortugg while the other is on table
        for (String[] pair : new String[][] { { "ortugg", "ortuggv" }, { "ortuggv", "ortugg" } }) {
            var scn = oneOrtuggOnTableOtherInHand(pair[0], pair[1], false);
            assertTrue(scn.DSDeployAvailable(scn.GetDSCard(pair[1]))); //test1
            scn = oneOrtuggOnTableOtherInHand(pair[0], pair[1], true);
            assertFalse(scn.DSDeployAvailable(scn.GetDSCard(pair[1]))); //test2
        }
    }

    @Test
    public void OrtuggVMayPersonaReplaceOrtugg() {
        // GEMP persona replacement (AbstractDeployable.getPersonaReplacementAction): same persona, and the card on
        // table is missing an expansion icon of the new card (Ortugg lacks Ortugg (V)'s [Set 3] icon).
        var scn = oneOrtuggOnTableOtherInHand("ortugg", "ortuggv", true);
        var ortugg = scn.GetDSCard("ortugg");
        var ortuggv = scn.GetDSCard("ortuggv");
        var site = scn.GetDSStartingLocation();
        assertTrue(scn.DSCardPlayAvailable(ortuggv, "Persona replace"));
        scn.DSPlayCard(ortuggv, "Persona replace");
        if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(ortugg))
            scn.DSChooseCard(ortugg);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(site, ortuggv));
        assertFalse(scn.CardsAtLocation(site, ortugg));
    }

    @Test
    public void OrtuggMayNotPersonaReplaceOrtuggV() {
        // The other way round: Ortugg (V) has every expansion icon Ortugg has and the same title, so no replacement.
        var scn = oneOrtuggOnTableOtherInHand("ortuggv", "ortugg", true);
        assertFalse(scn.GetDSAvailableActions().stream().anyMatch(a -> a.contains("Persona replace")));
    }
}
