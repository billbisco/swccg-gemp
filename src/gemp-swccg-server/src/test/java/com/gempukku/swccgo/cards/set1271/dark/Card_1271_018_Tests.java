package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1271_018_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
            new HashMap<>(Map.of("alderaan", "1_121", "yavin", "1_135", "throne", "1_138", "dantooineSite", "601_256")),
            new HashMap<>(Map.ofEntries(Map.entry("ignition", "1271_18"), Map.entry("core", "1271_22"),
                Map.entry("deathStar", "2_143"), Map.entry("laser", "2_161"), Map.entry("dantooine", "1_282"),
                Map.entry("corridor", "1_284"), Map.entry("bay", "1_285"), Map.entry("military", "1_286"),
                Map.entry("warRoom", "1_287"), Map.entry("ship", "1_301"))), 40, 40,
            StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void CommencePrimaryIgnitionPrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario();
        var b = s.GetDSCard("ignition").getBlueprint();
        assertEquals(Title.Commence_Primary_Ignition, b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(0, b.getDestiny(), s.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        assertTrue(b.hasVirtualSuffix());
        s.BlueprintCardTypeCheck(b, List.of(CardType.EPIC_EVENT));
        s.BlueprintIconCheck(b, List.of(Icon.EPIC_EVENT, Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_7));
        s.BlueprintKeywordCheck(b, List.of());
    }

    private VirtualTableScenario deploySetup(boolean core, boolean orbiting, boolean laser) {
        var s = scenario();
        s.StartGame();
        var deathStar = s.GetDSCard("deathStar");
        s.MoveLocationToTable(deathStar);
        s.MoveLocationToTable(s.GetLSCard("alderaan"));
        s.MoveLocationToTable(s.GetDSCard("dantooine"));
        s.MoveLocationToTable(s.GetLSCard("yavin"));
        if (core) s.MoveLocationToTable(s.GetDSCard("core"));
        deathStar.setSystemOrbited(orbiting ? Title.Alderaan : Title.Dantooine);
        if (laser) s.AttachCardsTo(deathStar, s.GetDSCard("laser"));
        s.MoveCardsToDSHand(s.GetDSCard("ignition"));
        s.SkipToDSTurn(Phase.DEPLOY);
        return s;
    }

    private void deployAndBlowAlderaan(VirtualTableScenario s) {
        var ignition = s.GetDSCard("ignition");
        var deathStar = s.GetDSCard("deathStar");
        s.DSDeployCard(ignition);
        if (s.DSHasCardChoiceAvailable(deathStar)) s.DSChooseCard(deathStar);
        s.PassAllResponses();
        assertEquals(deathStar, ignition.getAttachedTo());
        assertTrue(s.GetLSCard("alderaan").isBlownAway());
    }

    @Test public void CommencePrimaryIgnitionActuallyDeploysAndImmediatelyDestroysAlderaan() {
        deployAndBlowAlderaan(deploySetup(true, true, true));
    }

    @Test public void CommencePrimaryIgnitionCannotDeployWithoutCentralCoreOrTheCorrectOrbitOrSuperlaser() {
        for (var s : List.of(deploySetup(false, true, true), deploySetup(true, false, true), deploySetup(true, true, false))) {
            assertFalse(s.DSDeployAvailable(s.GetDSCard("ignition")));
        }
    }

    private VirtualTableScenario laterShot(String orbit, boolean relatedSite) {
        var s = deploySetup(true, true, true);
        deployAndBlowAlderaan(s);
        for (String name : List.of("corridor", "bay", "military", "warRoom")) s.MoveLocationToTable(s.GetDSCard(name));
        s.MoveLocationToTable(s.GetLSCard("throne"));
        if (relatedSite) s.MoveLocationToTable(s.GetLSCard("dantooineSite"));
        s.GetDSCard("deathStar").setSystemOrbited(orbit);
        s.SkipToDSTurn(Phase.CONTROL);
        return s;
    }

    private void laterShotWithControl(boolean control) {
        var s = laterShot(Title.Dantooine, false);
        var target = s.GetDSCard("dantooine");
        var ignition = s.GetDSCard("ignition");
        if (control) s.MoveCardsToLocation(target, s.GetDSCard("ship"));
        s.PrepareDSDestiny(4);
        int before = s.GetLSLostPileCount();
        int hand = s.GetLSHandCount();
        assertTrue(s.DSCardActionAvailable(ignition, "Fire Superlaser at Dantooine"));
        s.DSUseCardAction(ignition, "Fire Superlaser at Dantooine");
        s.PassAllResponses();
        assertEquals(control, target.isBlownAway());
        if (control) {
            assertTrue(s.AwaitingLSForceLossPayment());
            s.LSPayRemainingForceLossFromReserveDeck();
            assertEquals(before + 4, s.GetLSLostPileCount());
            assertEquals(hand, s.GetLSHandCount());
        } else {
            assertEquals(before, s.GetLSLostPileCount());
            assertFalse(s.AwaitingLSForceLossPayment());
        }
    }

    @Test public void CommencePrimaryIgnitionLaterShotNeedsGreaterThanEightAndDoesNotSpendForceOnFailure() {
        laterShotWithControl(false);
    }

    @Test public void CommencePrimaryIgnitionLaterControlBonusDestroysPlanetAndCostsFourLifeForce() {
        laterShotWithControl(true);
    }

    @Test public void CommencePrimaryIgnitionCannotFireAtARebelBaseOrAPlanetWithRelatedSites() {
        for (var s : List.of(laterShot(Title.Yavin_4, false), laterShot(Title.Dantooine, true))) {
            assertFalse(s.DSCardActionAvailable(s.GetDSCard("ignition"), "Fire Superlaser at"));
        }
    }
}
