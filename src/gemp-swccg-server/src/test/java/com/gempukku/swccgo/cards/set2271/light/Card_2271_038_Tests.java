package com.gempukku.swccgo.cards.set2271.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.MayNotBeRevivedModifier;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Field Dressing as on the re-made 7.1 sheet (2010-07-28, PC-E2: the forfeited character "remains at same site"), a version of 1271_38. */
public class Card_2271_038_Tests {
    private static final String DEPLOY = "Deploy Field Dressing on just-forfeited character";
    private static final String CURRENT_RETURN = "Return just-forfeited character with Field Dressing";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("dressing", "2271_38", "current", "1271_38", "patient", "1_19")),
                new HashMap<>(Map.of("vader", "1_168")), 40, 40,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario justForfeited(boolean revivingProhibited) {
        var s = scenario();
        s.StartGame();
        var site = s.GetDSStartingLocation();
        var patient = s.GetLSCard("patient");
        s.MoveCardsToLocation(site, patient, s.GetDSCard("vader"));
        s.MoveCardsToLSHand(s.GetLSCard("dressing"), s.GetLSCard("current"));
        if (revivingProhibited)
            s.ApplyAdHocModifier(new MayNotBeRevivedModifier(s.GetDSCard("vader")));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(4);
        s.PrepareDSDestiny(2);
        s.PrepareLSDestiny(0);
        s.SkipToPhase(Phase.BATTLE);
        s.DSInitiateBattle(site);
        s.SkipToDamageSegment(true);
        s.PassDamageSegmentActions();
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

    @Test
    public void SheetFieldDressingIsTheCurrentCardWithTheJuly2010Text() {
        var s = scenario();
        assertVersion(s.GetLSCard("dressing").getBlueprint(), s.GetLSCard("current").getBlueprint(), "1271_38", "PC-E2",
                LocalDate.of(2010, 7, 28), LocalDate.of(2013, 4, 27),
                "If you just forfeited a character of ability < 5 present at a site, deploy on that character (who remains at same site and, if 'hit,' is restored to normal). Character is forfeit = 0 (even if its forfeit may not normally be reduced). May play First Aid to place this card in Used Pile. Immune to your Control.");
    }

    @Test
    public void SheetFieldDressingKeepsTheForfeitedCharacterAtTheSameSite() {
        var s = justForfeited(false);
        var dressing = s.GetLSCard("dressing");
        var patient = s.GetLSCard("patient");
        var site = s.GetDSStartingLocation();
        assertTrue(s.LSCardActionAvailable(dressing, DEPLOY));
        s.LSUseCardAction(dressing, DEPLOY);
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
        s.PassAllResponses();
        if (s.AwaitingLSBattleDamagePayment()) s.LSPayRemainingBattleDamageFromReserveDeck();
        s.SkipToPhase(Phase.MOVE);
        assertEquals(0, s.GetForfeit(patient), s.epsilon);
    }

    @Test
    public void SheetFieldDressingIsNotStoppedByAMayNotBeRevivedRuleButTheCurrentCardIs() {
        // The original never takes the character out of Lost Pile ('revives' it): it just stays at the site.
        var s = justForfeited(true);
        assertFalse(s.LSCardActionAvailable(s.GetLSCard("current"), CURRENT_RETURN));
        assertTrue(s.LSCardActionAvailable(s.GetLSCard("dressing"), DEPLOY));
    }
}
