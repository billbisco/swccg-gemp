package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1263_034_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("rogue", "1263_34"); put("rogue2", "1263_34"); put("aces", "1271_45");
            put("luke", "1_19"); put("commander", "3_3"); put("wedge", "7_12"); put("dack", "3_4");
            put("pilot", "1_27"); put("artoo", "2_14"); put("arfive", "2_15"); put("red1", "1_144");
            put("ground", "1_136"); put("home", "9_74"); put("corvette", "1_140"); put("torpedoes", "1_158");
        }}, new HashMap<>() {{ put("devastator", "1_301"); put("vader", "1_168"); put("overload", "1_263"); }},
                60, 60, StartingSetup.LSStartingLocation("2_65"), StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test public void RogueSquadronXwingStatsMatchPrintedLegacySlip() {
        var scn = scenario(); var b = scn.GetLSCard("rogue").getBlueprint();
        assertEquals("Rogue Squadron X-wing", b.getTitle()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness()); assertFalse(b.hasVirtualSuffix());
        assertEquals(2, b.getDestiny(), scn.epsilon); assertEquals(2, b.getDeployCost(), scn.epsilon);
        assertEquals(3, b.getPower(), scn.epsilon); assertEquals(4, b.getManeuver(), scn.epsilon);
        assertEquals(5, b.getHyperspeed(), scn.epsilon); assertEquals(4, b.getForfeit(), scn.epsilon);
        assertEquals(1, b.getPilotCapacity()); assertEquals(1, b.getAstromechCapacity());
        assertEquals(CardSubtype.STARFIGHTER, b.getCardSubtype()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.STARSHIP)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.STARSHIP, Icon.SPECIAL_EDITION, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(Arrays.asList(Keyword.ROGUE_SQUADRON)));
        scn.BlueprintModelTypeCheck(b, new ArrayList<>(Arrays.asList(ModelType.X_WING)));
    }
    private VirtualTableScenario started(boolean aces) {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("ground")); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("rogue"));
        if (aces) scn.MoveCardsToLSSideOfTable(scn.GetLSCard("aces"));
        else scn.MoveCardsToLSHand(scn.GetLSCard("aces"));
        return scn;
    }
    @Test public void RogueSquadronXwingHasNoUnprintedPermanentPilot() {
        var scn = started(false); var ship = scn.GetLSCard("rogue");
        assertEquals(0, scn.GetBattleDestinyAbility(ship)); assertEquals(0, scn.GetPower(ship));
        scn.BoardAsPilot(ship, scn.GetLSCard("luke"));
        assertEquals(6, scn.GetPower(ship)); assertEquals(4, scn.GetManeuver(ship)); assertEquals(5, scn.GetHyperspeed(ship));
    }
    @Test public void RogueSquadronXwingActuallyDeploysForTwoForce() {
        var scn = started(false); var ship = scn.GetLSCard("rogue"); scn.MoveCardsToLSHand(ship);
        scn.SkipToLSTurn(Phase.CONTROL);
        while (scn.GetLSForcePileCount() > 0) scn.MoveCardsToTopOfLSReserveDeck(scn.GetTopOfLSForcePile());
        scn.LSActivateForceCheat(2); scn.SkipToPhase(Phase.DEPLOY); scn.LSDeployCard(ship);
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation()));
        scn.LSChooseCard(scn.GetLSCard("ground")); scn.PassAllResponses();
        assertAtLocation(scn.GetLSCard("ground"), ship); assertEquals(0, scn.GetLSForcePileCount());
    }
    @Test public void RogueSquadronXwingCanCarryOnePilotAndOneAstromechTogether() {
        var scn = started(true); var ship = scn.GetLSCard("rogue"); scn.BoardAsPilot(ship, scn.GetLSCard("luke"));
        scn.MoveCardsToLSHand(scn.GetLSCard("artoo")); scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(4);
        scn.LSDeployCard(scn.GetLSCard("artoo")); scn.LSChooseCard(ship); scn.PassAllResponses();
        assertEquals(ship, scn.GetLSCard("artoo").getAttachedTo()); assertEquals(ship, scn.GetLSCard("luke").getAttachedTo());
        assertEquals(8, scn.GetPower(ship)); assertEquals(6, scn.GetManeuver(ship)); assertEquals(7, scn.GetHyperspeed(ship));
        assertEquals(9, scn.GetForfeit(scn.GetLSCard("luke"))); assertEquals(4, scn.GetForfeit(scn.GetLSCard("artoo")));
    }
    @Test public void RogueSquadronXwingDoesNotGetSecondAstromechSlotFromRebelAces() {
        var scn = started(true); var ship = scn.GetLSCard("rogue"); scn.BoardAsPilot(ship, scn.GetLSCard("luke"));
        scn.MoveCardsToLSHand(scn.GetLSCard("artoo"), scn.GetLSCard("arfive"));
        scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(8);
        scn.LSDeployCard(scn.GetLSCard("artoo")); scn.LSChooseCard(ship); scn.PassAllResponses(); scn.DSPass();
        scn.LSDeployCard(scn.GetLSCard("arfive")); assertFalse(scn.LSHasCardChoiceAvailable(ship));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("ground"))); scn.LSChooseCard(scn.GetLSCard("ground")); scn.PassAllResponses();
    }
    @Test public void RogueSquadronXwingCannotCarryASecondPilot() {
        var scn = started(false); var ship = scn.GetLSCard("rogue"); scn.BoardAsPilot(ship, scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("red1"));
        scn.MoveCardsToLSHand(scn.GetLSCard("pilot")); scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(4);
        scn.LSDeployCard(scn.GetLSCard("pilot")); assertFalse(scn.LSHasCardChoiceAvailable(ship));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("red1"))); scn.LSChooseCard(scn.GetLSCard("red1")); scn.PassAllResponses();
    }
    @Test public void RogueSquadronXwingBoostsCurrentPilotForfeitOnlyWhileRebelAcesOnTable() {
        var scn = started(false); var ship = scn.GetLSCard("rogue"); var pilot = scn.GetLSCard("luke");
        scn.BoardAsPilot(ship, pilot); assertEquals(7, scn.GetForfeit(pilot));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("aces")); assertEquals(9, scn.GetForfeit(pilot));
        scn.MoveCardsToLSHand(scn.GetLSCard("aces")); assertEquals(7, scn.GetForfeit(pilot));
    }
    @Test public void RogueSquadronXwingDoesNotBoostPilotOfOtherStarship() {
        var scn = started(true); var other = scn.GetLSCard("red1"); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), other);
        scn.BoardAsPilot(other, scn.GetLSCard("luke")); assertEquals(7, scn.GetForfeit(scn.GetLSCard("luke")));
    }
    @Test public void RogueSquadronXwingForfeitBonusFollowsPilotLeavingSeat() {
        var scn = started(true); var pilot = scn.GetLSCard("luke"); scn.BoardAsPilot(scn.GetLSCard("rogue"), pilot);
        assertEquals(9, scn.GetForfeit(pilot)); scn.MoveCardsToLSHand(pilot); assertEquals(7, scn.GetForfeit(pilot));
    }
    @Test public void RogueSquadronXwingMatchesMultipleRogueSquadronPilotsWithoutRebelAces() {
        var scn = started(false); var ship = scn.GetLSCard("rogue");
        assertTrue(scn.IsMatchingPilot(ship, scn.GetLSCard("commander")));
        assertTrue(scn.IsMatchingPilot(ship, scn.GetLSCard("wedge")));
        assertFalse(scn.IsMatchingPilot(ship, scn.GetLSCard("dack")));
        assertFalse(scn.IsMatchingPilot(ship, scn.GetLSCard("luke")));
    }
    @Test public void RogueSquadronXwingConfersRogueSquadronPilotCharacteristicWhilePiloted() {
        var scn = started(false); var ship = scn.GetLSCard("rogue"); var pilot = scn.GetLSCard("luke");
        assertFalse(scn.IsMatchingPilot(ship, pilot)); scn.BoardAsPilot(ship, pilot);
        assertTrue(Filters.Rogue_Squadron_pilot.accepts(scn.game(), pilot)); assertTrue(scn.IsMatchingPilot(ship, pilot));
    }

    private VirtualTableScenario overloadSetup(boolean aces, boolean battle, boolean damage) {
        var scn = started(aces); var ship = scn.GetLSCard("rogue"); scn.BoardAsPilot(ship, scn.GetLSCard("luke"));
        scn.AttachCardsTo(ship, scn.GetLSCard("torpedoes")); scn.MoveCardsToDSHand(scn.GetDSCard("overload"));
        if (battle) {
            scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("devastator"));
            scn.BoardAsPilot(scn.GetDSCard("devastator"), scn.GetDSCard("vader"));
            if (damage) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("home"), scn.GetLSCard("corvette"));
            scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(scn.GetLSStartingLocation()); scn.PassAllResponses();
            if (damage) {
                scn.PrepareLSDestiny(0); scn.PrepareDSDestiny(3);
                scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
            } else {
                assertTrue(scn.AwaitingLSWeaponsSegmentActions()); scn.LSPass(); assertTrue(scn.AwaitingDSWeaponsSegmentActions());
            }
        } else scn.SkipToDSTurn(Phase.CONTROL);
        scn.PrepareDSDestiny(0); return scn;
    }
    private void overload(VirtualTableScenario scn) {
        scn.DSPlayCard(scn.GetDSCard("overload")); scn.DSChooseCard(scn.GetLSCard("torpedoes"));
        scn.PassAllResponses(); scn.PassDestinyDrawResponses(); scn.PassAllResponses();
        finishOrderedLosses(scn);
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("overload")); assertInZone(Zone.LOST_PILE, scn.GetLSCard("torpedoes"));
    }
    private void finishOrderedLosses(VirtualTableScenario scn) {
        // The current player orders the physical losses of carrier and attachments.
        for (int i = 0; i < 12; i++) {
            boolean chose = false;
            for (String key : new String[]{"torpedoes", "rogue", "luke"}) {
                var card = scn.GetLSCard(key);
                if (scn.LSHasCardChoiceAvailable(card)) { scn.LSChooseCard(card); chose = true; break; }
                if (scn.DSHasCardChoiceAvailable(card)) { scn.DSChooseCard(card); chose = true; break; }
            }
            scn.PassAllResponses(); if (!chose) break;
        }
    }
    @Test public void RogueSquadronXwingSurvivesActualIndirectLossBeforeDamage() {
        var scn = overloadSetup(true, true, false); overload(scn);
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("rogue"));
        assertEquals(scn.GetLSCard("rogue"), scn.GetLSCard("luke").getAttachedTo());
    }
    @Test public void RogueSquadronXwingWithoutRebelAcesIsLostToSameOverload() {
        var scn = overloadSetup(false, true, false); overload(scn); assertInZone(Zone.LOST_PILE, scn.GetLSCard("rogue"));
    }
    @Test public void RogueSquadronXwingProtectionEndsAtDamageSegment() {
        var scn = overloadSetup(true, true, true);
        assertEquals(4, scn.GetUnpaidLSAttrition()); assertEquals(0, scn.GetUnpaidLSBattleDamage());
        assertTrue(Filters.canBeTargetedBy(scn.GetDSCard("overload"), TargetingReason.TO_BE_LOST).accepts(scn.game(), scn.GetLSCard("rogue")));
        scn.LSPayAttritionFromCardInPlay(scn.GetLSCard("rogue")); finishOrderedLosses(scn);
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("rogue"));
    }
    @Test public void RogueSquadronXwingRebelAcesDoesNotPreventLossOutsideBattle() {
        var scn = overloadSetup(true, false, false); overload(scn); assertInZone(Zone.LOST_PILE, scn.GetLSCard("rogue"));
    }
    @Test public void RogueSquadronXwingCannotBeTargetedForLossBeforeDamage() {
        var scn = overloadSetup(true, true, false);
        assertFalse(Filters.canBeTargetedBy(scn.GetDSCard("overload"), TargetingReason.TO_BE_LOST).accepts(scn.game(), scn.GetLSCard("rogue")));
        assertTrue(Filters.canBeTargetedBy(scn.GetDSCard("overload"), TargetingReason.TO_BE_LOST).accepts(scn.game(), scn.GetLSCard("torpedoes")));
    }
    @Test public void RogueSquadronXwingWithoutAcesCanBeTargetedForLoss() {
        var scn = overloadSetup(false, true, false);
        assertTrue(Filters.canBeTargetedBy(scn.GetDSCard("overload"), TargetingReason.TO_BE_LOST).accepts(scn.game(), scn.GetLSCard("rogue")));
    }
}
