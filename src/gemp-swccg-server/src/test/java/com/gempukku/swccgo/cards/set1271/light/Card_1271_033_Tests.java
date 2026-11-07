package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1271_033_Tests {
    private static final String RECYCLE = "Place your forfeited card in Used Pile";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
            new HashMap<>(Map.of("hudsol", "1271_33", "restore", "1271_46", "yavin", "1_135",
                "interior", "1_138", "exterior", "1_137", "bespin", "5_76", "ship", "2_73", "sentry", "1271_40")),
            new HashMap<>(Map.of("devastator", "1_301", "vader", "1_168", "alter", "1_234")), 40, 40,
            StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario forfeit(boolean liberate, boolean interior) {
        var s = scenario();
        s.StartGame();
        var yavin = s.GetLSCard("yavin");
        var bespin = s.GetLSCard("bespin");
        var ship = s.GetLSCard("ship");
        var site = s.GetLSCard(interior ? "interior" : "exterior");
        s.MoveLocationToTable(yavin);
        s.MoveLocationToTable(bespin);
        s.MoveLocationToTable(site);
        s.MoveCardsToLocation(site, s.GetLSCard("hudsol"));
        s.MoveCardsToLocation(bespin, ship);
        if (liberate) {
            var restore = s.GetLSCard("restore");
            s.MoveCardsToLSHand(restore);
            s.SkipToLSTurn(Phase.DEPLOY);
            s.LSDeployCard(restore);
            s.LSChooseCard(yavin);
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
        assertEquals(liberate, Filters.liberated_system.accepts(s.game(), bespin));

        s.MoveCardsToLocation(bespin, s.GetDSCard("devastator"));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(6);
        s.SkipToPhase(Phase.BATTLE);
        s.DSInitiateBattle(bespin);
        s.PrepareLSDestiny(0);
        s.SkipToDamageSegment(true);
        s.PassDamageSegmentActions();
        assertTrue(s.AwaitingLSAttritionPayment() || s.AwaitingLSBattleDamagePayment());
        s.LSChooseCard(ship);
        if (s.LSDecisionAvailable("Do you still want to forfeit it?")) s.LSChooseYes();
        s.PassResponses("ABOUT_TO_BE_FORFEITED");
        s.PassResponses("ABOUT_TO_BE_LOST_FROM_TABLE");
        s.PassResponses("ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE");
        assertTrue(s.GetLSLostPile().contains(ship));
        if (s.DSDecisionAvailable("FORFEITED_TO_LOST_PILE_FROM_TABLE")) s.DSPass();
        return s;
    }

    @Test public void GeneralBobHudsolPrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario();
        var b = s.GetLSCard("hudsol").getBlueprint();
        assertEquals("General Bob Hudsol", b.getTitle());
        assertEquals(Side.LIGHT, b.getSide());
        assertEquals(3, b.getDestiny(), s.epsilon);
        assertEquals(1, b.getPower(), s.epsilon);
        assertEquals(2, b.getAbility(), s.epsilon);
        assertEquals(2, b.getDeployCost(), s.epsilon);
        assertEquals(3, b.getForfeit(), s.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertFalse(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        s.BlueprintCardTypeCheck(b, List.of(CardType.REBEL));
        s.BlueprintIconCheck(b, List.of(Icon.REBEL, Icon.WARRIOR, Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_7));
        s.BlueprintKeywordCheck(b, List.of(Keyword.GENERAL, Keyword.LEADER));
    }

    @Test public void GeneralBobHudsolActuallyMovesAForfeitedShipToUsedAtALiberatedOneIconSystem() {
        var s = forfeit(true, true);
        assertTrue("LS: " + (s.LSGetDecision() == null ? "none" : s.LSGetDecision().getText())
            + "; DS: " + (s.DSGetDecision() == null ? "none" : s.DSGetDecision().getText()),
            s.LSAnyActionsAvailable() && s.LSCardActionAvailable(s.GetLSCard("hudsol"), RECYCLE));
        s.LSUseCardAction(s.GetLSCard("hudsol"), RECYCLE);
        s.PassAllResponses();
        assertTrue(s.GetLSUsedPile().contains(s.GetLSCard("ship")));
    }

    @Test public void GeneralBobHudsolDoesNotRecycleAForfeitureAtAnUnliberatedSystem() {
        var s = forfeit(false, true);
        assertFalse(s.LSAnyActionsAvailable() && s.LSCardActionAvailable(s.GetLSCard("hudsol"), RECYCLE));
    }

    @Test public void GeneralBobHudsolDoesNotRecycleFromAnExteriorYavinSite() {
        var s = forfeit(true, false);
        assertFalse(s.LSAnyActionsAvailable() && s.LSCardActionAvailable(s.GetLSCard("hudsol"), RECYCLE));
    }

    @Test public void GeneralBobHudsolProtectsActualMassassiBaseSentryFromAlterOnlyAtAnInteriorYavinSite() {
        checkAlter(true);
        checkAlter(false);
    }

    private void checkAlter(boolean interior) {
        var s = scenario();
        s.StartGame();
        var yavin = s.GetLSCard("yavin");
        var site = s.GetLSCard(interior ? "interior" : "exterior");
        var sentry = s.GetLSCard("sentry");
        var vader = s.GetDSCard("vader");
        var alter = s.GetDSCard("alter");
        s.MoveLocationToTable(yavin);
        s.MoveLocationToTable(site);
        s.MoveCardsToLocation(site, s.GetLSCard("hudsol"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), vader);
        s.AttachCardsTo(yavin, sentry);
        s.MoveCardsToDSHand(alter);
        s.SkipToDSTurn(Phase.CONTROL);
        assertEquals(!interior, s.DSCardPlayAvailable(alter));
        if (interior) {
            assertEquals(yavin, sentry.getAttachedTo());
            return;
        }
        s.PrepareDSDestiny(0);
        s.DSPlayCard(alter);
        if (s.DSHasCardChoiceAvailable(sentry)) s.DSChooseCard(sentry);
        if (s.DSHasCardChoiceAvailable(vader)) s.DSChooseCard(vader);
        s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(sentry));
    }
}
