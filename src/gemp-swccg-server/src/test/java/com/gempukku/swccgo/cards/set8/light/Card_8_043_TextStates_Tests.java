package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Deactivate The Shield Generator text states (docs/legacy-versions.md, "Text states"): 8_43o as printed (no
 * once-per-phase limit on the attempt) and the bare card 8_43, whose PC errata
 * ([[Deactivate The Shield Generator (PC Errata)]]) allows one attempt during each of your control phases.
 */
public class Card_8_043_TextStates_Tests {
    private static VirtualTableScenario scenario(String id) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("event", id);
                    put("bunker", "8_70");  // Endor: Bunker
                    put("luke", "1_19");    // Luke Skywalker (a Rebel)
                }},
                new HashMap<>(),
                40, 40,
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
    public void DeactivateTheShieldGeneratorPrintedHasThePrintedText() {
        var card = scenario("8_43o").GetLSCard("event").getBlueprint();
        assertEquals("Deploy on Bunker. During your control phase, if you control Bunker with a Rebel, attempt to \"blow away\" Bunker as follows: Charges! Come On, Come On!: Draw two destiny. Add 3 to total for each Explosive Charge on Bunker. Move! Move! Move!: If total destiny > 12, your characters here may relocate to Back Door for free, Bunker and Landing Platform (if on table) are \"blown away,\" opponent loses 8 Force and this card is lost.", card.getGameText());
        TextState state = card.getTextState();
        assertEquals("8_43", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 6, 1), state.printedOn());
        assertEquals("Deactivate The Shield Generator [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void DeactivateTheShieldGeneratorBareCardIsUnchanged() {
        var bare = scenario("8_43").GetLSCard("event").getBlueprint();
        var printed = scenario("8_43o").GetLSCard("event").getBlueprint();
        assertNull(bare.getTextState());
        assertEquals(bare.getTitle(), printed.getTitle());
        assertEquals(bare.getUniqueness(), printed.getUniqueness());
    }

    private static void giveLightSideTheAction(VirtualTableScenario s) {
        for (int i = 0; i < 10 && s.LSGetDecision() == null && s.DSGetDecision() != null; i++)
            s.DSPass();
    }

    /** Makes one failed attempt (destiny 1 + 1) and says whether another attempt is offered in the same control phase. */
    private static boolean secondAttemptOffered(String id) {
        var s = scenario(id);
        var bunker = s.GetLSCard("bunker");
        var event = s.GetLSCard("event");
        s.StartGame();
        s.MoveLocationToTable(bunker);
        s.MoveCardsToLocation(bunker, s.GetLSCard("luke"));
        s.AttachCardsTo(bunker, event);
        s.SkipToLSTurn(Phase.CONTROL);
        s.PrepareLSDestiny(1);
        s.PrepareLSDestiny(1);
        giveLightSideTheAction(s);
        assertTrue(s.LSCardActionAvailable(event));
        s.LSUseCardAction(event);
        s.PassAllResponses();
        giveLightSideTheAction(s);
        assertFalse(bunker.isBlownAway());
        assertEquals(Phase.CONTROL, s.GetCurrentPhase());
        return s.LSCardActionAvailable(event);
    }

    @Test
    public void DeactivateTheShieldGeneratorPrintedMayAttemptAgainInTheSameControlPhase() {
        assertTrue(secondAttemptOffered("8_43o"));
    }

    @Test
    public void DeactivateTheShieldGeneratorBareCardAttemptsOncePerControlPhase() {
        assertFalse(secondAttemptOffered("8_43"));
    }
}
