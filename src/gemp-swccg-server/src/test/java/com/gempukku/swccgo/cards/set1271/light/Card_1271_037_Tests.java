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
                "adjacent", "1_138", "luke", "1_19", "nabrun", "1_97", "undercover", "2_40", "dressing", "1271_38")),
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
        s.MoveCardsToLSHand(s.GetLSCard("plans"), s.GetLSCard("nabrun"), s.GetLSCard("undercover"));
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

    @Test public void EmperorsSinisterPlansSpyMayNotRelocateUntilCompleted() {
        var s = ready(true, true);
        var nabrun = s.GetLSCard("nabrun");
        var spy = s.GetLSCard("spy");
        // Inverse: before the Utinni Effect is on him, Nabrun Leids may 'transport' the spy.
        assertTrue(com.gempukku.swccgo.filters.Filters.canBeRelocatedToLocation(s.GetLSCard("room"), true, 0).accepts(s.game(), spy));
        assertTrue(s.LSCardPlayAvailable(nabrun));
        playPlans(s);
        assertTrue(s.game().getModifiersQuerying().mayNotRelocate(s.gameState(), spy));
        assertFalse(com.gempukku.swccgo.filters.Filters.canBeRelocatedToLocation(s.GetLSCard("room"), true, 0).accepts(s.game(), spy));
        if (s.LSGetDecision() == null && s.DSGetDecision() != null) s.DSPass();
        assertFalse(s.LSCardPlayAvailable(nabrun));
        // Regular movement is still allowed, and completion lifts the restriction.
        completeWithLandspeed(s);
        assertFalse(s.game().getModifiersQuerying().mayNotRelocate(s.gameState(), spy));
    }

    @Test public void EmperorsSinisterPlansSpyMayNotBecomeUndercoverUntilCompleted() {
        var s = ready(true, true);
        var undercover = s.GetLSCard("undercover");
        var spy = s.GetLSCard("spy");
        playPlans(s);
        assertTrue(s.game().getModifiersQuerying().mayNotBeUndercover(s.gameState(), spy));
        if (s.LSGetDecision() == null && s.DSGetDecision() != null) s.DSPass();
        if (s.LSDeployAvailable(undercover)) {
            s.LSDeployCard(undercover);
            if (s.LSHasCardChoiceAvailable(spy)) s.LSChooseCard(spy);
            s.PassAllResponses();
        }
        assertFalse(spy.isUndercover());
        completeWithLandspeed(s);
        assertFalse(s.game().getModifiersQuerying().mayNotBeUndercover(s.gameState(), spy));
    }

    @Test public void EmperorsSinisterPlansUndercoverStillWorksWithoutIt() {
        var s = ready(true, true);
        var undercover = s.GetLSCard("undercover");
        var spy = s.GetLSCard("spy");
        assertTrue(s.LSDeployAvailable(undercover));
        s.LSDeployCard(undercover);
        if (s.LSHasCardChoiceAvailable(spy)) s.LSChooseCard(spy);
        s.PassAllResponses();
        assertTrue(spy.isUndercover());
    }

    @Test public void EmperorsSinisterPlansStackedKeepsYourSpiesForfeitAtLeastThree() {
        var s = ready(true, true);
        var spy = s.GetLSCard("spy");
        var luke = s.GetLSCard("luke");
        var site = s.GetLSCard("adjacent");
        playPlans(s);
        s.MoveCardsToLocation(site, luke);
        // Leia (spy, forfeit 6) and Luke (not a spy) are both reduced by 5.
        s.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.ForfeitModifier(s.GetDSCard("darkRoom"),
            com.gempukku.swccgo.filters.Filters.or(spy, luke), null, -5, true));
        s.PassAllResponses();
        // Not yet completed: no floor.
        assertEquals(1, s.GetForfeit(spy), s.epsilon);
        completeWithLandspeed(s);
        assertEquals(3, s.GetForfeit(spy), s.epsilon);
        assertEquals(Math.max(0, luke.getBlueprint().getForfeit() - 5), s.GetForfeit(luke), s.epsilon);
        assertTrue(s.GetForfeit(luke) < 3);
        // A plain reset to 0 is also a reduction that may not go below 3.
        s.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier(s.GetLSCard("door"), spy, 0));
        assertEquals(3, s.GetForfeit(spy), s.epsilon);
    }

    @Test public void FieldDressingForfeitZeroSupersedesEmperorsSinisterPlansFloor() {
        // Ruling: Field Dressing's "(even if forfeit may not normally be reduced)" overrides "may not have their
        // forfeit reduced below 3".
        var s = ready(true, true);
        var spy = s.GetLSCard("spy");
        playPlans(s);
        completeWithLandspeed(s);
        assertEquals(6, s.GetForfeit(spy), s.epsilon);
        s.AttachCardsTo(spy, s.GetLSCard("dressing"));
        assertEquals(spy, s.GetLSCard("dressing").getAttachedTo());
        assertEquals(0, s.GetForfeit(spy), s.epsilon);
    }

    @Test public void EmperorsSinisterPlansFloorDoesNotRaiseOrBlockSmallReductions() {
        var s = ready(true, true);
        var spy = s.GetLSCard("spy");
        playPlans(s);
        completeWithLandspeed(s);
        assertEquals(6, s.GetForfeit(spy), s.epsilon);
        s.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.ForfeitModifier(s.GetDSCard("darkRoom"), spy, null, -2, true));
        assertEquals(4, s.GetForfeit(spy), s.epsilon);
    }
}
