package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1240_092_Tests {
    private static final String ADD = "Make total ability +";

    private static final StartingSetup WATCH_YOUR_STEP = new StartingSetup() {
        @Override public HashMap<String, String> Cards() {
            return new HashMap<>(Map.of("objective", "10_26", "cantina", "1_128", "bay", "1_129", "tatooine", "1_127"));
        }

        @Override public void Setup(VirtualTableScenario scn) {
            while (scn.LSDecisionAvailable("On which side")) scn.LSChoose("Left");
        }
    };

    private VirtualTableScenario Started(boolean objective) {
        var ls = new HashMap<>(Map.of("mask", "1240_92", "dash", "10_6", "karrde", "10_24", "luke", "1_19",
                "jawa", "1_12", "mos", "1_133", "hut", "1_134", "dune", "1_130"));
        if (!objective) {
            // The starting location is the Cantina; the system and docking bay are added separately.
            ls.put("bay", "1_129");
            ls.put("tatooine", "1_127");
        }
        ls.put("otherPlanet", "5_79");
        ls.put("red", "1_144");
        var scn = new VirtualTableScenario(ls, new HashMap<>(Map.of("trooper", "1_194", "second", "1_194", "tie", "1_305")),
                45, 45, objective ? WATCH_YOUR_STEP : StartingSetup.LSStartingLocation("1_128"), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                VirtualTableScenario.Open);
        scn.StartGame();
        while (scn.LSDecisionAvailable("On which side")) scn.LSChoose("Left");
        if (objective) {
            assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("objective"));
            assertInZone(Zone.LOCATIONS, scn.GetLSCard("cantina"));
        }
        scn.MoveLocationToTable(scn.GetLSCard("mos"));
        scn.MoveLocationToTable(scn.GetLSCard("dune"));
        scn.MoveLocationToTable(scn.GetLSCard("hut"));
        scn.MoveLocationToTable(scn.GetLSCard("otherPlanet"));
        return scn;
    }

    @Test
    public void TuskenBreathMaskMatchesTheBlockFourPrinting() {
        var scn = Started(false);
        var card = scn.GetLSCard("mask").getBlueprint();
        assertEquals("Tusken Breath Mask", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>(List.of(Keyword.DEPLOYS_ON_SITE)));
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.EFFECT, Icon.LEGACY_BLOCK_4)));
    }

    private void StartDeploy(VirtualTableScenario scn) {
        scn.MoveCardsToLSHand(scn.GetLSCard("mask"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.DrainLSForcePileTo(0);
        scn.LSDeployCard(scn.GetLSCard("mask"));
    }

    private void FinishDeploy(VirtualTableScenario scn, String site) {
        scn.LSChooseCard(scn.GetLSCard(site));
        scn.PassAllResponses();
        assertInZone(Zone.ATTACHED, scn.GetLSCard("mask"));
        assertEquals(scn.GetLSCard(site), scn.GetLSCard("mask").getAttachedTo());
        assertEquals(0, scn.GetLSForcePileCount());
    }

    @Test
    public void MaskDeploysOnlyOnAnExteriorTatooineBattleground() {
        var scn = Started(true);
        StartDeploy(scn);
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("mos")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("dune")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("cantina")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("hut")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("otherPlanet")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("tatooine")));
        FinishDeploy(scn, "dune");
    }

    private VirtualTableScenario AtBattle(boolean objective, boolean flip, boolean nonAlienElsewhere) {
        var scn = Started(objective);
        StartDeploy(scn);
        FinishDeploy(scn, "dune");
        var mos = scn.GetLSCard("mos");
        var cantina = objective ? scn.GetLSCard("cantina") : scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(mos, scn.GetLSCard("dash"));
        if (flip) scn.MoveCardsToLocation(cantina, scn.GetLSCard("karrde"));
        if (nonAlienElsewhere) scn.MoveCardsToLocation(scn.GetLSCard("hut"), scn.GetLSCard("luke"));
        scn.EnsureLSForcePile(2);
        scn.SkipToPhase(Phase.BATTLE);
        if (objective) assertEquals(flip ? 7 : 0, scn.GetLSCard("objective").getDestinyValueToUse(), scn.epsilon);
        scn.MoveCardsToLocation(mos, scn.GetDSCard("trooper"));
        scn.LSStartBattleAndSkipToWeaponsSegment(mos);
        return scn;
    }

    private float AbilityAt(VirtualTableScenario scn, PhysicalCardImpl site) {
        return scn.game().getModifiersQuerying().getTotalAbilityAtLocation(scn.gameState(), scn.LS, site);
    }

    @Test
    public void RealFlippedObjectiveAddsSevenToAlienAbilityAndEnablesActualBattleDestinyAtAnotherSite() {
        var scn = AtBattle(true, true, false);
        var mask = scn.GetLSCard("mask");
        var site = scn.GetLSCard("mos");
        assertEquals(3, AbilityAt(scn, site), scn.epsilon);
        assertEquals(scn.GetLSCard("dune"), mask.getAttachedTo());
        assertTrue(scn.LSCardActionAvailable(mask, ADD + "7"));
        scn.LSUseCardAction(mask, ADD + "7");
        scn.PassAllResponses();
        assertEquals(10, AbilityAt(scn, site), scn.epsilon);
        scn.PrepareLSDestiny(2);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(2, scn.GetLSTotalDestiny());
    }

    @Test
    public void OriginalObjectiveFrontGivesZeroAndDoesNotEnableBattleDestiny() {
        var scn = AtBattle(true, false, false);
        var mask = scn.GetLSCard("mask");
        assertTrue(scn.LSCardActionAvailable(mask, ADD + "0"));
        scn.LSUseCardAction(mask, ADD + "0");
        scn.PassAllResponses();
        assertEquals(3, AbilityAt(scn, scn.GetLSCard("mos")), scn.epsilon);
        scn.PrepareLSDestiny(2);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(0, scn.GetLSTotalDestiny());
    }

    @Test
    public void NoObjectiveMeansUndefinedXUsesZeroAndCannotRaiseAbility() {
        var scn = AtBattle(false, false, false);
        scn.LSUseCardAction(scn.GetLSCard("mask"), ADD + "0");
        scn.PassAllResponses();
        assertEquals(3, AbilityAt(scn, scn.GetLSCard("mos")), scn.epsilon);
    }

    @Test
    public void NonAlienAbilityAnywherePreventsTheActionEvenThoughOnlyAnAlienIsInBattle() {
        var scn = AtBattle(true, true, true);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("mask"), ADD));
        assertEquals(3, AbilityAt(scn, scn.GetLSCard("mos")), scn.epsilon);
    }

    @Test
    public void MaskDoesNotOfferAbilityInASystemBattleEvenWithOnlyAlienAbility() {
        var scn = Started(true);
        StartDeploy(scn);
        FinishDeploy(scn, "dune");
        var system = scn.GetLSCard("tatooine");
        var red = scn.GetLSCard("red");
        scn.MoveCardsToLocation(system, red, scn.GetDSCard("tie"));
        scn.BoardAsPilot(red, scn.GetLSCard("dash"));
        scn.EnsureLSForcePile(1);
        scn.LSStartBattleAndSkipToWeaponsSegment(system);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("mask"), ADD));
    }

    private void FinishBattleWithoutAttrition(VirtualTableScenario scn) {
        scn.PrepareLSDestiny(0);
        scn.SkipToEndOfPowerSegment(true);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        if (scn.AwaitingDSBattleDamagePayment()) scn.DSPayRemainingBattleDamageFromReserveDeck();
        if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayRemainingBattleDamageFromReserveDeck();
        scn.PassAllResponses();
    }

    @Test
    public void MaskCanBeUsedOnlyOnceAcrossTwoBattlesInOneTurnAndResetsForTheNextTurn() {
        var scn = AtBattle(true, true, false);
        var mask = scn.GetLSCard("mask");
        var cantina = scn.GetLSCard("cantina");
        var mos = scn.GetLSCard("mos");
        scn.LSUseCardAction(mask, ADD + "7");
        scn.PassAllResponses();
        FinishBattleWithoutAttrition(scn);
        assertEquals(3, AbilityAt(scn, mos), scn.epsilon);
        scn.MoveCardsToLocation(cantina, scn.GetDSCard("second"));
        if (scn.AwaitingDSBattlePhaseActions()) scn.DSPass();
        assertTrue(scn.AwaitingLSBattlePhaseActions());
        scn.LSInitiateBattle(cantina);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardActionAvailable(mask, ADD));
        FinishBattleWithoutAttrition(scn);
        scn.EnsureDSForcePile(1);
        scn.DSStartBattleAndSkipToWeaponsSegment(mos);
        if (scn.AwaitingDSWeaponsSegmentActions()) scn.DSPass();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(mask, ADD + "7"));
    }

    @Test
    public void MaskStaysOnAContestedSiteButIsCanceledWhenDarkTakesControlByDeployingThere() {
        var scn = Started(false);
        StartDeploy(scn);
        FinishDeploy(scn, "mos");
        var mos = scn.GetLSCard("mos");
        var mask = scn.GetLSCard("mask");
        var trooper = scn.GetDSCard("trooper");
        var second = scn.GetDSCard("second");
        scn.MoveCardsToLocation(mos, scn.GetLSCard("dash"), trooper);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertInZone(Zone.ATTACHED, mask);
        scn.MoveCardsToDSHand(trooper, second);
        scn.MoveCardsToLocation(scn.GetLSCard("hut"), scn.GetLSCard("dash"));
        scn.EnsureDSForcePile(1);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(second);
        scn.DSChooseCard(mos);
        scn.PassAllResponses();
        assertAtLocation(mos, second);
        assertInZone(Zone.LOST_PILE, mask);
    }
}
