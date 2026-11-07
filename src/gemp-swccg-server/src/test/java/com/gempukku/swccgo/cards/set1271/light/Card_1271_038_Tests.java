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
            new HashMap<>(Map.of("dressing", "1271_38", "patient", patientId, "firstAid", "7_89", "control", "4_48")),
            new HashMap<>(Map.of("vader", "1_168")), 40, 40,
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
        s.PrepareDSDestiny(5);
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
        return s;
    }

    private void playDressing(VirtualTableScenario s) {
        var dressing = s.GetLSCard("dressing");
        var patient = s.GetLSCard("patient");
        var site = s.GetDSStartingLocation();
        assertTrue(s.LSCardActionAvailable(dressing, RETURN_PATIENT));
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
        assertEquals(0, s.GetForfeit(patient));
        assertFalse(s.GetLSLostPile().contains(patient));
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

    @Test public void FieldDressingLetsFirstAidRemoveItToUsedAndRestoresForfeit() {
        var s = justForfeited("1_19");
        playDressing(s);
        s.PassAllResponses();
        s.SkipToLSTurn(Phase.CONTROL);
        var firstAid = s.GetLSCard("firstAid");
        s.LSUseCardAction(firstAid, "Place Field Dressing in Used Pile");
        s.PassAllResponses();
        assertEquals(Zone.USED_PILE, firstAid.getZone());
        assertEquals(Zone.USED_PILE, s.GetLSCard("dressing").getZone());
        assertEquals(s.GetLSCard("patient").getBlueprint().getForfeit(), s.GetForfeit(s.GetLSCard("patient")), s.epsilon);
    }
}
