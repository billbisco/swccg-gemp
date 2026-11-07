package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1271_038_Tests {
    private static final String RETURN_PATIENT = "Return just-forfeited character with Field Dressing";

    private VirtualTableScenario scenario(String patientId) {
        return new VirtualTableScenario(
            new HashMap<>(Map.of("dressing", "1271_38", "patient", patientId, "firstAid", "7_89", "control", "4_48", "delevar", "8_5")),
            new HashMap<>(Map.of("vader", "1_168", "darkControl", "4_139")), 40, 40,
            StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario justForfeited(String patientId) {
        var s = scenario(patientId);
        s.StartGame();
        var site = s.GetDSStartingLocation();
        var patient = s.GetLSCard("patient");
        s.MoveCardsToLocation(site, patient, s.GetDSCard("vader"));
        s.MoveCardsToLSHand(s.GetLSCard("dressing"), s.GetLSCard("firstAid"));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(4);
        s.PrepareDSDestiny(2);
        s.PrepareLSDestiny(0);
        s.SkipToPhase(Phase.BATTLE);
        s.DSInitiateBattle(site);
        s.SkipToDamageSegment(true);
        s.PassDamageSegmentActions();
        assertTrue(s.AwaitingLSAttritionPayment() || s.AwaitingLSBattleDamagePayment());
        assertTrue(s.LSHasCardChoiceAvailable(patient));
        s.LSChooseCard(patient);
        if (s.LSDecisionAvailable("Do you still want to forfeit it?")) s.LSChooseYes();
        s.PassResponses("ABOUT_TO_BE_FORFEITED");
        s.PassResponses("ABOUT_TO_BE_LOST_FROM_TABLE");
        s.PassResponses("ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE");
        assertTrue(s.GetLSLostPile().contains(patient));
        if (s.DSDecisionAvailable("FORFEITED_TO_LOST_PILE_FROM_TABLE")) s.DSPass();
        return s;
    }

    private void playDressing(VirtualTableScenario s) {
        var dressing = s.GetLSCard("dressing");
        var patient = s.GetLSCard("patient");
        var site = s.GetDSStartingLocation();
        assertTrue("LS: " + (s.LSGetDecision() == null ? "none" : s.LSGetDecision().getText())
            + "; DS: " + (s.DSGetDecision() == null ? "none" : s.DSGetDecision().getText()),
            s.LSAnyActionsAvailable() && s.LSCardActionAvailable(dressing, RETURN_PATIENT));
        s.LSUseCardAction(dressing, RETURN_PATIENT);
        s.PassAllResponses();
        if (s.LSHasCardChoiceAvailable(patient)) {
            s.LSChooseCard(patient);
            s.PassAllResponses();
        }
        if (s.LSHasCardChoiceAvailable(site)) {
            s.LSChooseCard(site);
            s.PassAllResponses();
        }
        assertTrue(s.CardsAtLocation(site, patient));
        assertEquals(patient, dressing.getAttachedTo());
        assertFalse(s.GetLSLostPile().contains(patient));
        // An Old Ben-style return does not put the character back in the battle being resolved.
        s.PassAllResponses();
        if (s.AwaitingLSBattleDamagePayment()) s.LSPayRemainingBattleDamageFromReserveDeck();
        s.SkipToPhase(Phase.MOVE);
        assertFalse(s.gameState().isDuringBattle());
        var q = s.game().getModifiersQuerying();
        assertTrue("Dressing must be active after the battle ends; zone=" + dressing.getZone(), s.gameState().isCardInPlayActive(dressing));
        assertTrue("The dressing's host filter must match the revived card", com.gempukku.swccgo.filters.Filters.hasAttached(dressing).accepts(s.game(), patient));
        assertFalse("Premiere Luke does not protect his forfeit", q.isProhibitedFromHavingForfeitReduced(s.gameState(), patient));
        assertFalse("Attached dressing must supply an active forfeit reset",
            q.getModifiersAffectingCard(s.gameState(), com.gempukku.swccgo.logic.modifiers.ModifierType.UNMODIFIABLE_FORFEIT_VALUE, patient).isEmpty());
        assertEquals(0, s.GetForfeit(patient));
    }

    @Test public void FieldDressingPrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario("1_19");
        var b = s.GetLSCard("dressing").getBlueprint();
        assertEquals("Field Dressing", b.getTitle());
        assertEquals(Side.LIGHT, b.getSide());
        assertEquals(3, b.getDestiny(), s.epsilon);
        assertEquals(Uniqueness.RESTRICTED_2, b.getUniqueness());
        assertEquals(CardCategory.EFFECT, b.getCardCategory());
        assertEquals(CardSubtype.IMMEDIATE, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        assertFalse(b.hasVirtualSuffix());
        s.BlueprintCardTypeCheck(b, List.of(CardType.EFFECT));
        s.BlueprintIconCheck(b, List.of(Icon.SPECIAL_EDITION, Icon.EFFECT, Icon.LEGACY_BLOCK_7));
        s.BlueprintKeywordCheck(b, List.of());
    }

    @Test public void FieldDressingReturnsTheSameForfeitedCharacterFromLostToTheSameSite() {
        var s = justForfeited("1_19");
        playDressing(s);
    }

    @Test public void FieldDressingCannotReturnAnAbilitySixCharacter() {
        var s = justForfeited("1_21");
        assertFalse(s.LSAnyActionsAvailable() && s.LSCardActionAvailable(s.GetLSCard("dressing"), RETURN_PATIENT));
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("patient")));
    }

    @Test public void FieldDressingCannotReturnAnAbilityFiveCharacter() {
        // Luke With Lightsaber is ability 5: "ability < 5" excludes him.
        var s = justForfeited("108_3");
        assertEquals(5, s.GetLSCard("patient").getBlueprint().getAbility(), s.epsilon);
        assertFalse(s.LSAnyActionsAvailable() && s.LSCardActionAvailable(s.GetLSCard("dressing"), RETURN_PATIENT));
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("patient")));
    }

    @Test public void FieldDressingForfeitZeroAppliesEvenIfForfeitMayNotBeReduced() {
        var s = justForfeited("1_19");
        playDressing(s);
        var patient = s.GetLSCard("patient");
        // Corporal Delevar: "Prevents your characters from having their forfeit reduced at same location."
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetLSCard("delevar"));
        var q = s.game().getModifiersQuerying();
        assertTrue(q.isProhibitedFromHavingForfeitReduced(s.gameState(), patient));
        assertEquals(0, s.GetForfeit(patient), s.epsilon);
        // Delevar's own forfeit is untouched by the dressing.
        assertEquals(4, s.GetForfeit(s.GetLSCard("delevar")), s.epsilon);
    }

    @Test public void FieldDressingLetsFirstAidRemoveItToUsedAndRestoresForfeit() {
        var s = justForfeited("1_19");
        playDressing(s);
        var firstAid = s.GetLSCard("firstAid");
        if (s.LSGetDecision() == null && s.DSGetDecision() != null) s.DSPass();
        s.LSUseCardAction(firstAid, "Place Field Dressing in Used Pile");
        s.PassAllResponses();
        assertTrue(s.GetLSUsedPile().contains(firstAid));
        assertTrue(s.GetLSUsedPile().contains(s.GetLSCard("dressing")));
        assertEquals(s.GetLSCard("patient").getBlueprint().getForfeit(), s.GetForfeit(s.GetLSCard("patient")), s.epsilon);
    }

    @Test public void FieldDressingIsImmuneToActualLightControlButNotDarkControl() {
        var s = justForfeited("1_19");
        playDressing(s);
        var dressing = s.GetLSCard("dressing");
        var lightControl = s.GetLSCard("control");
        var darkControl = s.GetDSCard("darkControl");
        s.MoveCardsToLSHand(lightControl);
        s.MoveCardsToDSHand(darkControl);
        if (s.LSGetDecision() == null && s.DSGetDecision() != null) s.DSPass();
        assertTrue(s.LSAnyActionsAvailable());
        assertFalse(s.LSCardPlayAvailable(lightControl));
        s.LSPass();
        assertTrue(s.DSCardPlayAvailable(darkControl));
        s.DSPlayCard(darkControl);
        if (s.DSHasCardChoiceAvailable(dressing)) s.DSChooseCard(dressing);
        s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(dressing));
    }
}
