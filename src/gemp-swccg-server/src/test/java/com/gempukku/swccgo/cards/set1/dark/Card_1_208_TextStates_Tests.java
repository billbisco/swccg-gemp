package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A Disturbance In The Force as printed (1_208o, Premiere 1995; docs/legacy-versions.md "Text states") has no once-per-game limit on its
 * 'insert'; Decipher's Glossary Supplement (29 January 2002) added it, and the current card (1_208) has it. Texts are
 * checked in Card_1_FollowUp_TextStates_Tests.
 */
public class Card_1_208_TextStates_Tests {

    /** Whether the card may be 'inserted' in this deploy phase, before and after this title's once-per-game 'insert' is used. */
    private static boolean[] mayInsert(String id) {
        var mine = new HashMap<>(Map.of("card", id));
        var other = new HashMap<>(Map.of("filler", "1_28"));
        var scn = new VirtualTableScenario(other, mine, 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
        scn.StartGame();
        var card = scn.GetDSCard("card");
        scn.MoveCardsToDSHand(card);
        scn.EnsureDSForcePile(5);
        scn.SkipToPhase(Phase.DEPLOY);
        var blueprint = (Card1_208) card.getBlueprint();
        boolean before = blueprint.checkGameTextDeployRequirements(card.getOwner(), scn.game(), card, null, false);
        // A copy of this title was 'inserted' earlier this game.
        scn.game().getModifiersQuerying().getUntilEndOfGameLimitCounter(card.getBlueprint().getTitle(),
                GameTextActionId.DEPLOY_CARD_ACTION).incrementToLimit(Integer.MAX_VALUE, 1);
        boolean after = blueprint.checkGameTextDeployRequirements(card.getOwner(), scn.game(), card, null, false);
        return new boolean[]{before, after};
    }

    @Test
    public void ADisturbanceInTheForcePrintedHasNoOncePerGameLimit() {
        var bare = mayInsert("1_208");
        assertTrue(bare[0]);
        assertFalse(bare[1]);
        var printed = mayInsert("1_208o");
        assertTrue(printed[0]);
        assertTrue(printed[1]);
    }
}
