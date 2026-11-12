package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tempest Scout 1 text states (docs/legacy-versions.md, "Text states"): 8_172o as printed ("adds 1 to attrition
 * against opponent", every battle while Arnet pilots it) and the bare card 8_172, whose PC errata
 * ([[Tempest Scout 1 (PC Errata)]]) adds "here".
 */
public class Card_8_172_TextStates_Tests {
    private static final String PRINTED_TEXT = "May add 2 pilots or passengers. May move as a 'react' for free. When Arnet piloting, immune to attrition < 4 and adds 1 to attrition against opponent.";
    private static final String ERRATA_TEXT = "May add 2 pilots or passengers. May move as a 'react' for free. When Arnet piloting, immune to attrition < 4 and adds 1 to attrition against opponent here.";

    private static VirtualTableScenario scenario(String id) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rebel", "1_28"); // Rebel Trooper
                }},
                new HashMap<>() {{
                    put("tempest", id);
                    put("arnet", "8_101");  // Lieutenant Arnet
                    put("djas", "1_171");   // Djas Puhr, ability 4
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes"
        );
    }

    @Test
    public void TempestScout1PrintedHasThePrintedText() {
        var card = scenario("8_172o").GetDSCard("tempest").getBlueprint();
        assertEquals(PRINTED_TEXT, card.getGameText());
        TextState state = card.getTextState();
        assertEquals("8_172", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 6, 1), state.printedOn());
        assertEquals("Tempest Scout 1 [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void TempestScout1BareCardIsUnchanged() {
        var card = scenario("8_172").GetDSCard("tempest").getBlueprint();
        assertEquals(ERRATA_TEXT, card.getGameText());
        assertNull(card.getTextState());
    }

    /** Arnet pilots Tempest Scout 1 at the Dark Side site; Djas Puhr battles a Rebel Trooper at the Light Side site. */
    private static float lightSideAttritionInBattleElsewhere(String id) {
        var scn = scenario(id);
        var tempest = scn.GetDSCard("tempest");
        var battleSite = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), tempest);
        scn.BoardAsPilot(tempest, scn.GetDSCard("arnet"));
        scn.MoveCardsToLocation(battleSite, scn.GetDSCard("djas"), scn.GetLSCard("rebel"));
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.BATTLE);
        scn.DSStartBattleAndSkipToWeaponsSegment(battleSite);
        scn.PrepareDSDestiny(3);
        scn.PrepareLSDestiny(1);
        scn.SkipToDamageSegment(true);
        return scn.gameState().getBattleState().getAttritionTotal(scn.game(), scn.LS);
    }

    @Test
    public void TempestScout1PrintedAddsOneToAttritionInABattleElsewhere() {
        assertEquals(4, lightSideAttritionInBattleElsewhere("8_172o"), 0.001);
    }

    @Test
    public void TempestScout1BareCardAddsNothingToAttritionInABattleElsewhere() {
        assertEquals(3, lightSideAttritionInBattleElsewhere("8_172"), 0.001);
    }
}
