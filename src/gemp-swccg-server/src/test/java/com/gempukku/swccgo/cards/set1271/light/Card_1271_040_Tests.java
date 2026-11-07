package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.filters.LegacyFilters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1271_040_Tests {
    private static final String POWER = "Add up to 2 power to each Rebel warrior";
    private static final String RETRIEVE = "Retrieve 1 Force with Massassi Base Sentry";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.ofEntries(
                Map.entry("sentry", "1271_40"), Map.entry("restore", "1271_46"),
                Map.entry("yavin", "1_135"), Map.entry("bespin", "5_76"), Map.entry("bay", "5_83"),
                Map.entry("yavinSite", "1_138"), Map.entry("xwing", "1_146"), Map.entry("yavinXwing", "1_146"),
                Map.entry("luke", "1_19"), Map.entry("jek", "1_13"), Map.entry("owen", "1_22"))),
            new HashMap<>(Map.of("vader", "1_168")), 40, 40,
            StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void MassassiBaseSentryPrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario();
        var b = s.GetLSCard("sentry").getBlueprint();
        assertEquals("Massassi Base Sentry", b.getTitle());
        assertEquals(Side.LIGHT, b.getSide());
        assertEquals(4, b.getDestiny(), s.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        assertFalse(b.hasVirtualSuffix());
        s.BlueprintCardTypeCheck(b, List.of(CardType.EFFECT));
        s.BlueprintIconCheck(b, List.of(Icon.EFFECT, Icon.SETUP, Icon.LEGACY_BLOCK_7));
        s.BlueprintKeywordCheck(b, List.of(Keyword.DEPLOYS_ON_LOCATION));
    }

    private VirtualTableScenario deploySentry(boolean restoreBespin, boolean controlYavin) {
        var s = scenario();
        s.StartGame();
        var yavin = s.GetLSCard("yavin");
        var bespin = s.GetLSCard("bespin");
        var sentry = s.GetLSCard("sentry");
        var restore = s.GetLSCard("restore");
        s.MoveLocationToTable(yavin);
        s.MoveLocationToTable(bespin);
        s.MoveLocationToTable(s.GetLSCard("bay"));
        s.MoveLocationToTable(s.GetLSCard("yavinSite"));
        s.MoveCardsToLocation(bespin, s.GetLSCard("xwing"));
        if (controlYavin) s.MoveCardsToLocation(yavin, s.GetLSCard("yavinXwing"));
        s.MoveCardsToLSHand(sentry, restore);
        s.SkipToLSTurn(Phase.DEPLOY);
        s.LSDeployCard(sentry);
        if (s.LSHasCardChoiceAvailable(yavin)) s.LSChooseCard(yavin);
        s.PassAllResponses();
        assertEquals(yavin, sentry.getAttachedTo());
        if (restoreBespin) {
            if (s.LSGetDecision() == null && s.DSGetDecision() != null) s.DSPass();
            s.LSDeployCard(restore);
            if (s.LSHasCardChoiceAvailable(yavin)) s.LSChooseCard(yavin);
            s.PassAllResponses();
            s.SkipToLSTurn(Phase.CONTROL);
            s.PrepareLSDestiny(7);
            s.LSForceDrainAt(bespin);
            if (s.DSDecisionAvailable("optional")) s.DSPass();
            s.LSChooseAction("Draw destiny to liberate");
            s.PassAllResponses();
            s.LSChoose("Light side");
            s.PassAllResponses();
            s.PassForceDrainStartResponses();
            if (s.AwaitingDSForceLossPayment()) s.DSPayRemainingForceLossFromReserveDeck();
        }
        assertEquals(restoreBespin, Filters.liberated_system.accepts(s.game(), bespin));
        return s;
    }

    private VirtualTableScenario battle(boolean liberated) {
        var s = deploySentry(liberated, false);
        var bay = s.GetLSCard("bay");
        s.MoveCardsToLocation(bay, s.GetLSCard("luke"), s.GetLSCard("jek"), s.GetLSCard("owen"), s.GetDSCard("vader"));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(5);
        s.SkipToPhase(Phase.BATTLE);
        s.StartBattleAndSkipToWeaponsSegment(s.DS, bay);
        if (s.LSGetDecision() == null && s.DSGetDecision() != null) s.DSPass();
        return s;
    }

    @Test public void MassassiBaseSentryAddsPowerInActualBattleAtARelatedLiberatedSiteWithCapOfFour() {
        var s = battle(true);
        var luke = s.GetLSCard("luke");
        var jek = s.GetLSCard("jek");
        var owen = s.GetLSCard("owen");
        assertEquals(3, s.GetPower(luke));
        assertEquals(2, s.GetPower(jek));
        assertEquals(1, s.GetPower(owen));
        s.LSUseCardAction(s.GetLSCard("sentry"), POWER);
        s.PassAllResponses();
        assertEquals(4, s.GetPower(luke));
        assertEquals(4, s.GetPower(jek));
        assertEquals(1, s.GetPower(owen));
    }

    @Test public void MassassiBaseSentryDoesNotAddPowerInActualBattleAtAnUnliberatedSite() {
        var s = battle(false);
        assertFalse(s.LSAnyActionsAvailable() && s.LSCardActionAvailable(s.GetLSCard("sentry"), POWER));
        assertEquals(3, s.GetPower(s.GetLSCard("luke")));
    }

    private void retrieve(boolean control) {
        var s = deploySentry(true, control);
        var lost = s.GetLSCard("owen");
        s.MoveCardsToTopOfLSLostPile(lost);
        s.SkipToLSTurn(Phase.DEPLOY);
        s.SkipToLSTurn(Phase.CONTROL);
        s.LSForceDrainAt(s.GetLSCard("bespin"));
        if (s.DSDecisionAvailable("optional")) s.DSPass();
        assertEquals(control, s.LSAnyActionsAvailable() && s.LSCardActionAvailable(s.GetLSCard("sentry"), RETRIEVE));
        if (control) {
            s.LSUseCardAction(s.GetLSCard("sentry"), RETRIEVE);
            s.PassAllResponses();
            assertTrue(s.GetLSUsedPile().contains(lost));
        }
    }

    @Test public void MassassiBaseSentryActuallyRetrievesWhenLightControlsYavinAndDrainsAtAnotherLiberatedSystem() { retrieve(true); }
    @Test public void MassassiBaseSentryDoesNotRetrieveWhenLightDoesNotControlYavin() { retrieve(false); }

    @Test public void MassassiBaseSentryLiberatesYavinWithoutAStackedCardAndRestoreFreedomBoostsItsDrain() {
        var s = deploySentry(false, true);
        var yavin = s.GetLSCard("yavin");
        var sentry = s.GetLSCard("sentry");
        var restore = s.GetLSCard("restore");
        var query = s.game().getModifiersQuerying();
        assertTrue(LegacyFilters.liberatedSystem.accepts(s.game(), yavin));
        assertTrue(s.GetStackedCards(yavin).isEmpty());
        float base = query.getForceDrainAmount(s.gameState(), yavin, s.LS);
        if (s.LSGetDecision() == null && s.DSGetDecision() != null) s.DSPass();
        s.LSDeployCard(restore);
        if (s.LSHasCardChoiceAvailable(yavin)) s.LSChooseCard(yavin);
        s.PassAllResponses();
        assertEquals(yavin, restore.getAttachedTo());
        assertEquals(base + 1, query.getForceDrainAmount(s.gameState(), yavin, s.LS), s.epsilon);
        s.MoveOutOfPlay(sentry);
        assertFalse(LegacyFilters.liberatedSystem.accepts(s.game(), yavin));
        assertEquals(base, query.getForceDrainAmount(s.gameState(), yavin, s.LS), s.epsilon);
        assertTrue(s.GetStackedCards(yavin).isEmpty());
    }
}
