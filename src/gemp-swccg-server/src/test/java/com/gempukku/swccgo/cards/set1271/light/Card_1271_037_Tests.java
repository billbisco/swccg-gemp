package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1271_037_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
            new HashMap<>(Map.of("plans", "1271_37", "door", "8_69", "spy", "1_17", "room", "1_139",
                "adjacent", "1_138", "luke", "1_19")),
            new HashMap<>(Map.of("vader", "1_168", "darkRoom", "1_287")), 40, 40,
            StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void EmperorsSinisterPlansPrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario();
        var b = s.GetLSCard("plans").getBlueprint();
        assertEquals("Emperor's Sinister Plans", b.getTitle());
        assertEquals(Side.LIGHT, b.getSide());
        assertEquals(6, b.getDestiny(), s.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.UTINNI, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        assertFalse(b.hasVirtualSuffix());
        s.BlueprintCardTypeCheck(b, List.of(CardType.EFFECT));
        s.BlueprintIconCheck(b, List.of(Icon.EFFECT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_7));
        s.BlueprintKeywordCheck(b, List.of(Keyword.DEPLOYS_ON_CHARACTERS));
    }

    private VirtualTableScenario ready(boolean yourRoom, boolean atBackDoor) {
        var s = scenario();
        s.StartGame();
        var door = s.GetLSCard("door");
        s.MoveLocationToTable(door);
        s.MoveLocationToTable(s.GetLSCard("adjacent"));
        s.MoveLocationToTable(s.GetDSCard("darkRoom"));
        if (yourRoom) s.MoveLocationToTable(s.GetLSCard("room"));
        s.MoveCardsToLocation(atBackDoor ? door : s.GetLSCard("adjacent"), s.GetLSCard("spy"));
        s.MoveCardsToLSHand(s.GetLSCard("plans"));
        s.SkipToLSTurn(Phase.DEPLOY);
        s.EnsureLSForcePile(10);
        return s;
    }

    private void playPlans(VirtualTableScenario s) {
        var plans = s.GetLSCard("plans");
        var room = s.GetLSCard("room");
        var spy = s.GetLSCard("spy");
        s.LSDeployCard(plans);
        if (s.LSHasCardChoiceAvailable(spy)) s.LSChooseCard(spy);
        if (s.LSHasCardChoiceAvailable(room)) s.LSChooseCard(room);
        s.PassAllResponses();
        assertEquals(spy, plans.getAttachedTo());
        assertEquals(room, plans.getTargetedCard(s.gameState(), TargetId.UTINNI_EFFECT_TARGET_1));
    }

    @Test public void EmperorsSinisterPlansDoesNotDeployWithoutItsOwnWarRoom() {
        var s = ready(false, true);
        assertFalse(s.LSDeployAvailable(s.GetLSCard("plans")));
    }

    @Test public void EmperorsSinisterPlansDoesNotDeployOnASpyAtAnotherSite() {
        var s = ready(true, false);
        assertFalse(s.LSDeployAvailable(s.GetLSCard("plans")));
    }

    private void completeWithLandspeed(VirtualTableScenario s) {
        var plans = s.GetLSCard("plans");
        var room = s.GetLSCard("room");
        var spy = s.GetLSCard("spy");
        // Arrange the final leg after transit; take the last step with the actual landspeed action.
        s.MoveCardsToLocation(s.GetLSCard("adjacent"), spy);
        assertEquals(spy, plans.getAttachedTo());
        s.SkipToPhase(Phase.MOVE);
        s.LSMoveCard(spy, room);
        s.PassAllResponses();
        assertTrue(s.CardsAtLocation(room, spy));
        assertEquals(Zone.STACKED, plans.getZone());
        assertEquals(room, plans.getStackedOn());
    }

    @Test public void EmperorsSinisterPlansStacksOnTheChosenWarRoomAfterActualLandspeedMovement() {
        var s = ready(true, true);
        playPlans(s);
        completeWithLandspeed(s);
    }

    private void battleDestiny(boolean completed) {
        var s = ready(true, true);
        playPlans(s);
        if (completed) completeWithLandspeed(s);
        var site = s.GetDSStartingLocation();
        s.MoveCardsToLocation(site, s.GetLSCard("luke"), s.GetDSCard("vader"));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(5);
        s.SkipToPhase(Phase.BATTLE);
        s.DSInitiateBattle(site);
        s.PrepareDSDestiny(5);
        s.PrepareLSDestiny(1);
        s.SkipToDamageSegment(true);
        // Premiere Vader adds one to the actual five-destiny draw.
        assertEquals(completed ? 5 : 6, s.GetDSTotalDestiny());
        assertEquals(1, s.GetLSTotalDestiny());
    }

    @Test public void EmperorsSinisterPlansReducesOpponentActualBattleDestinyOnlyAfterCompletion() {
        battleDestiny(false);
        battleDestiny(true);
    }
}
