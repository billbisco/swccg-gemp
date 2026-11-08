package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

/** Original version of 1210_43 (Anything Goes only). */
public class Card_2210_043_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("version", "2210_43");
                    put("current", "1210_43");
                    put("prisoner", "2_142");
                    put("vader", "1_168");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void VersionHasItsTextAndVersionInfo() {
        var scn = GetScenario();
        var card = scn.GetDSCard("version").getBlueprint();
        var current = scn.GetDSCard("current").getBlueprint();

        assertEquals(current.getTitle(), card.getTitle());
        assertEquals(current.getUniqueness(), card.getUniqueness());
        assertEquals(current.getSide(), card.getSide());
        assertEquals(current.hasVirtualSuffix(), card.hasVirtualSuffix());
        assertEquals(current.getCardTypes(), card.getCardTypes());
        for (Icon icon : Icon.values())
            assertEquals(icon.name(), current.getIconCount(icon), card.getIconCount(icon));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("Deploy on table. We Have A Prisoner is a Used Interrupt. TIEs are deploy +1 to Launch Bay. During your deploy phase, may deploy from Reserve Deck (or retrieve) one Besieged or a Launch Bay; reshuffle. Once per turn, when your Imperial captures a character, opponent loses 1 Force. (Immune to Alter.)",
                card.getGameText());
        assertNotEquals(current.getGameText(), card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1210_43", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2013, 4, 27), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    @Test
    public void VersionIsTheSameUniqueCardAsTheCurrentPrinting() {
        for (boolean currentOnTable : java.util.List.of(true, false)) {
            for (boolean otherOut : java.util.List.of(false, true)) {
                var scn = GetScenario();
                var onTable = scn.GetDSCard(currentOnTable ? "current" : "version");
                var inHand = scn.GetDSCard(currentOnTable ? "version" : "current");
                scn.StartGame();

                if (otherOut)
                    scn.MoveCardsToDSSideOfTable(onTable);
                scn.MoveCardsToDSHand(inHand);
                scn.SkipToDSTurn(Phase.DEPLOY);
                scn.EnsureDSForcePile(8);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.DSCardPlayAvailable(inHand));
            }
        }
    }

    @Test
    public void OriginalDoesNotTriggerWhenYourImperialOnlySeizesACaptive() {
        var scn = GetScenario();
        var detainment = scn.GetDSCard("version");
        var prisoner = scn.GetDSCard("prisoner");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(detainment);
        scn.MoveCardsToLocation(site, vader, luke);
        scn.MoveCardsToDSHand(prisoner);
        scn.SkipToPhase(Phase.BATTLE);
        scn.EnsureDSForcePile(1);
        int lsForceBefore = scn.GetLSLifeForceRemaining();
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(false);

        boolean captured = false;
        for (int i = 0; i < 24; i++) {
            if (dsHasCardActions(scn) && (scn.DSPlayUsedInterruptAvailable(prisoner)
                    || scn.DSCardActionAvailable(prisoner, "Capture")
                    || scn.DSCardPlayAvailable(prisoner))) {
                if (scn.DSPlayUsedInterruptAvailable(prisoner)) {
                    scn.DSPlayUsedInterrupt(prisoner);
                } else if (scn.DSCardActionAvailable(prisoner, "Capture")) {
                    scn.DSUseCardAction(prisoner, "Capture");
                } else {
                    scn.DSPlayCard(prisoner);
                }
                captured = true;
                break;
            }
            if (scn.LSGetDecision() != null && (scn.AwaitingLSBattleDamagePayment() || scn.AwaitingLSAttritionPayment())
                    && scn.LSHasCardChoiceAvailable(luke)) {
                scn.LSChooseCard(luke);
                continue;
            }
            if (scn.LSGetDecision() != null && (scn.LSChoiceAvailable("Pass")
                    || scn.LSDecisionAvailable("optional") || scn.LSDecisionAvailable("Optional"))) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && !dsHasCardActions(scn)
                    && (scn.DSChoiceAvailable("Pass") || scn.DSDecisionAvailable("optional")
                    || scn.DSDecisionAvailable("Optional"))) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() == null && scn.DSGetDecision() == null) {
                break;
            }
            break;
        }
        assertTrue(captured);
        boolean forceLoss = false;
        for (int i = 0; i < 24; i++) {
            var actionTexts = scn.DSGetDecision() != null && scn.DSGetADParam("actionText") != null
                    ? scn.DSGetADParamAsList("actionText") : null;
            if (actionTexts != null) {
                for (String a : actionTexts) {
                    if (a != null && a.toLowerCase().contains("lose 1 force")) {
                        scn.DSChooseAction(a);
                        forceLoss = true;
                        break;
                    }
                }
            }
            if (forceLoss) {
                break;
            }
            if (scn.DSCaptureDecisionAvailable()) {
                scn.DSChooseSeizeCaptive();
                continue;
            }
            if (scn.DSHasCardChoiceAvailable(vader)) {
                scn.DSChooseCard(vader);
                continue;
            }
            if (scn.DSHasCardChoiceAvailable(luke)) {
                scn.DSChooseCard(luke);
                continue;
            }
            if (Boolean.TRUE.equals(scn.DSChoiceAvailable("Yes"))) {
                scn.DSChooseYes();
                continue;
            }
            if (scn.DSDecisionAvailable(" Force - Optional responses")
                    && (actionTexts == null || actionTexts.stream().noneMatch(a -> a != null && a.toLowerCase().contains("lose 1 force")))) {
                scn.PassForceUseResponses();
                continue;
            }
            if (scn.DSDecisionAvailable("Playing <div")) {
                scn.PassCardPlayResponses();
                continue;
            }
            if (scn.LSGetDecision() != null && (scn.LSChoiceAvailable("Pass")
                    || scn.LSDecisionAvailable("optional") || scn.LSDecisionAvailable("Optional"))) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && (scn.DSDecisionAvailable("optional")
                    || scn.DSDecisionAvailable("Optional"))) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertFalse(forceLoss); //capture by We Have A Prisoner, Vader only seizes
    }


    private boolean dsHasCardActions(VirtualTableScenario scn) {
        return scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null;
    }
}
