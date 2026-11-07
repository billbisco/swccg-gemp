package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1250_019_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("amidala", "14_25", "senator", "12_28", "other", "1_19")),
                new HashMap<>(Map.of("effect", "1250_19", "senator", "14_81", "trooper", "1_194")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test public void NuteGunraysBountyPrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("effect").getBlueprint();
        assertEquals("Nute Gunray's Bounty", b.getTitle());
        assertEquals("Palpatine suggested to the Queen that she acquiesce to the Trade Federation, just for the time being.", b.getLore());
        assertEquals(4, b.getDestiny(), scn.epsilon);
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertFalse(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.EPISODE_I, Icon.CORUSCANT, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void NuteGunraysBountyActuallyDeploysOnAmidalaOrOpposingSenator() {
        for (String target : List.of("amidala", "senator")) {
            var scn = scenario();
            var effect = scn.GetDSCard("effect");
            var bearer = scn.GetLSCard(target);
            var site = scn.GetDSStartingLocation();
            scn.MoveCardsToLocation(site, bearer, scn.GetLSCard("other"), scn.GetDSCard("senator"),
                    scn.GetDSCard("trooper"));
            scn.MoveCardsToDSHand(effect);
            scn.SkipToPhase(Phase.DEPLOY);
            scn.DSDeployCard(effect);
            assertTrue(scn.DSHasCardChoiceAvailable(bearer));
            assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("senator")));
            assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("other")));
            scn.DSChooseCard(bearer);
            for (int i = 0; i < 35 && !scn.AwaitingDSDeployPhaseActions(); i++) {
                if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected Bounty deployment decision: " + scn.GetCurrentDecision().getText());
            }
            assertSame(bearer, effect.getAttachedTo());
        }
    }
}
