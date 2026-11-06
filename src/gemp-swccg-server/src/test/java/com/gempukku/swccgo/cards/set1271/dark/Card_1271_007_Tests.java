package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1271_007_Tests {
    private VirtualTableScenario GetScenario() { return GetScenario("1271_7"); }
    private VirtualTableScenario GetScenario(String torentId) {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19"); put("han", "1_11"); put("obi", "1_21"); put("leia", "1_17");
        }}, new HashMap<>() {{
            put("torent", torentId); put("trooper", "1_194"); put("prison", "6_164");
            put("blaster", "1_317"); put("strike", "5_135"); put("prisoner", "2_142");
            put("secondPrisoner", "2_142"); put("vader", "1_168"); put("ig", "109_11");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void SergeantTorentVStatsAndKeywordsAreCorrect() {
        var scn = GetScenario(); var physical = scn.GetDSCard("torent"); var card = physical.getBlueprint();
        assertEquals("Sergeant Torent", card.getTitle()); assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide()); assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(3, card.getDestiny(), scn.epsilon); assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon); assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon); assertEquals(1, scn.GetLandspeed(physical));
        assertEquals(CardCategory.CHARACTER, card.getCardCategory());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.IMPERIAL); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{ add(Icon.IMPERIAL); add(Icon.WARRIOR); add(Icon.SPECIAL_EDITION); add(Icon.LEGACY_BLOCK_7); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.DEATH_STAR_TROOPER); }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, card.getExpansionSet()); assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SergeantTorentVHasPowerFourWhenDefendingAtMobileSite() {
        //test1: Cloud City's mobile site qualifies, independent of its prison status.
        var scn = GetScenario(); var torent = scn.GetDSCard("torent"); var site = scn.GetLSStartingLocation();
        scn.StartGame(); assertTrue(Filters.mobile_site.accepts(scn.game(), site));
        scn.MoveCardsToLocation(site, torent, scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(5); scn.LSInitiateBattle(site); scn.PassAllResponses();
        assertEquals(4, scn.GetPower(torent)); //test1
    }

    @Test
    public void SergeantTorentVHasPowerFourWhenDefendingAtNonMobilePrison() {
        //test1: Jabba's Dungeon qualifies as a prison even though it is not mobile.
        var scn = GetScenario(); var torent = scn.GetDSCard("torent"); var site = scn.GetDSCard("prison");
        scn.StartGame(); scn.MoveLocationToTable(site);
        assertTrue(Filters.prison.accepts(scn.game(), site)); assertFalse(Filters.mobile_site.accepts(scn.game(), site));
        scn.MoveCardsToLocation(site, torent, scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(5); scn.LSInitiateBattle(site); scn.PassAllResponses();
        assertEquals(4, scn.GetPower(torent)); //test1
    }

    @Test
    public void SergeantTorentVHasNoPowerBonusWhenAttackingAtMobileSite() {
        //test1: The same mobile site does not grant the defending-only bonus to the attacker.
        var scn = GetScenario(); var torent = scn.GetDSCard("torent"); var site = scn.GetLSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, torent, scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureDSForcePile(5); scn.DSInitiateBattle(site); scn.PassAllResponses();
        assertEquals(2, scn.GetPower(torent)); //test1
    }

    @Test
    public void SergeantTorentVHasNoPowerBonusWhenDefendingAtOrdinarySite() {
        //test1: An ordinary planetary site does not meet either location condition.
        var scn = GetScenario(); var torent = scn.GetDSCard("torent"); var site = scn.GetDSStartingLocation();
        scn.StartGame(); assertFalse(Filters.mobile_site.accepts(scn.game(), site)); assertFalse(Filters.prison.accepts(scn.game(), site));
        scn.MoveCardsToLocation(site, torent, scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(5); scn.LSInitiateBattle(site); scn.PassAllResponses();
        assertEquals(2, scn.GetPower(torent)); //test1
    }

    @Test
    public void SergeantTorentVHasNoPowerBonusOutsideBattle() {
        //test1: Merely being at a mobile site does not grant the battle bonus.
        var scn = GetScenario(); var torent = scn.GetDSCard("torent");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), torent);
        assertEquals(2, scn.GetPower(torent)); //test1
    }

    @Test
    public void SergeantTorentVReducesBattleDamageAndAttritionByCapturedCharactersForfeit() {
        //test1: A real We Have A Prisoner capture of Luke reduces both obligations by his forfeit7.
        var scn = CaptureLukeBeforeDamage(true);
        assertEquals(4, scn.GetUnpaidDSBattleDamage()); //test1
        assertEquals(0, scn.GetUnpaidDSAttrition()); //test1
    }

    @Test
    public void SergeantTorentVControlCaptureWithoutVirtualAbilityLeavesDamageAndAttritionUnchanged() {
        //test1: The same capture with original Torent leaves damage11 and attrition6.
        var scn = CaptureLukeBeforeDamage(false);
        assertEquals(11, scn.GetUnpaidDSBattleDamage()); //test1
        assertEquals(6, scn.GetUnpaidDSAttrition()); //test1
    }

    @Test
    public void SergeantTorentVReductionExpiresAtEndOfBattle() {
        //test1: The captive remains escorted, but the next battle has no previous reduction.
        var scn = CaptureLukeBeforeDamage(true);
        scn.PassDamageSegmentActions();
        for (int i = 0; i < 4; i++) scn.DSPayBattleDamageFromForcePile(); scn.PassAllResponses();
        scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(5); scn.EnsureDSForcePile(5);
        scn.LSInitiateBattle(scn.GetDSStartingLocation()); scn.PassAllResponses();
        scn.PrepareLSDestiny(6); scn.SkipToDamageSegment(true);
        scn.PassResponses("Just drew"); scn.PassDestinyDrawResponses(); scn.PassAllResponses();
        scn.PassPowerSegmentActions(); scn.PassAllResponses();
        assertTrue(scn.GetLSCard("luke").isCaptive());
        assertEquals(11, scn.GetUnpaidDSBattleDamage()); //test1
        assertEquals(6, scn.GetUnpaidDSAttrition()); //test1
    }

    @Test
    public void SergeantTorentVDoesNotReduceDamageForCaptureByAnotherCard() {
        //test1: A real riot gun capture cannot trigger the We Have A Prisoner-only reduction.
        var scn = GetScenario(); var luke = scn.GetLSCard("luke"); var torent = scn.GetDSCard("torent");
        var ig = scn.GetDSCard("ig");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), luke, scn.GetLSCard("han"), torent, ig);
        scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(5); scn.EnsureDSForcePile(5);
        scn.LSInitiateBattle(scn.GetDSStartingLocation()); scn.PassAllResponses(); scn.LSPass();
        scn.PrepareDSDestiny(6); scn.DSUseCardAction(ig, "Fire"); scn.DSChooseCard(luke);
        scn.PassResponses("Fire "); scn.PassDestinyDrawResponses();
        FinishCapture(scn, luke, torent);
        AssertNoReductionWhilePassingCaptureResponses(scn);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions()); //test1
    }

    @Test
    public void SergeantTorentVCanReduceOnlyOncePerTurn() {
        //test1: A second real We Have A Prisoner capture in the same battle cannot trigger another reduction.
        var scn = CaptureLukeBeforeDamage(true, true);
        scn.PassDamageSegmentActions();
        assertTrue(scn.AwaitingLSAttritionPayment());
        var han = scn.GetLSCard("han"); var prisoner = scn.GetDSCard("secondPrisoner");
        scn.LSChooseCard(han);
        ReachDarkInterrupt(scn, prisoner, "Capture"); scn.DSPlayCard(prisoner, "Capture");
        FinishCapture(scn, han, scn.GetDSCard("vader"));
        AssertNoReductionWhilePassingCaptureResponses(scn);
        assertTrue(han.isCaptive()); //test1
    }

    private VirtualTableScenario CaptureLukeBeforeDamage(boolean virtualTorent) {
        return CaptureLukeBeforeDamage(virtualTorent, false);
    }

    private VirtualTableScenario CaptureLukeBeforeDamage(boolean virtualTorent, boolean secondCapture) {
        var scn = GetScenario(virtualTorent ? "1271_7" : "7_206");
        var site = scn.GetDSStartingLocation(); var luke = scn.GetLSCard("luke");
        var torent = scn.GetDSCard("torent"); var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster"); var strike = scn.GetDSCard("strike"); var prisoner = scn.GetDSCard("prisoner");
        scn.StartGame(); scn.MoveCardsToLocation(site, luke, scn.GetLSCard("han"), scn.GetLSCard("obi"), torent, trooper);
        scn.AttachCardsTo(trooper, blaster); scn.MoveCardsToDSHand(strike, prisoner);
        if (secondCapture) {
            scn.MoveCardsToLocation(site, scn.GetDSCard("vader"));
            scn.MoveCardsToDSHand(scn.GetDSCard("secondPrisoner"));
        }
        scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(5); scn.EnsureDSForcePile(10);
        scn.LSInitiateBattle(site); scn.PassAllResponses(); if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass();
        scn.PrepareDSDestiny(6); scn.DSUseCardAction(blaster, "Fire"); scn.DSChooseCard(luke);
        scn.PassResponses("Fire "); scn.PassDestinyDrawResponses(); scn.PassResponses("ABOUT_TO_BE_HIT");
        ReachDarkInterrupt(scn, strike, "Make"); scn.DSPlayCard(strike, "Make"); scn.DSChooseCard(luke);
        ReachDarkInterrupt(scn, prisoner, "Capture"); scn.DSPlayCard(prisoner, "Capture");
        FinishCapture(scn, luke, trooper); assertTrue(luke.isCaptive()); assertEquals(trooper, luke.getAttachedTo());
        assertEquals(7, scn.GetForfeit(luke));
        if (virtualTorent) {
            for (int i = 0; i < 30 && !(scn.GetDecidingPlayer().equals(scn.DS)
                    && scn.DSCardActionAvailable(torent, "Reduce")); i++) PassOptionalResponse(scn);
            scn.DSUseCardAction(torent, "Reduce");
        }
        scn.PassAllResponses(); scn.PrepareLSDestiny(6);
        if (secondCapture) scn.PrepareDSDestiny(6);
        scn.SkipToDamageSegment(true);
        scn.PassResponses("Just drew"); scn.PassDestinyDrawResponses(); scn.PassAllResponses();
        if (secondCapture && scn.DSDecisionAvailable("battle destiny?")) {
            scn.DSChooseYes(); scn.PassDestinyDrawResponses(); scn.PassResponses("Just drew");
            scn.PassDestinyDrawResponses(); scn.PassAllResponses();
        }
        scn.PassPowerSegmentActions(); scn.PassAllResponses();
        assertTrue(scn.gameState().getBattleState().isReachedDamageSegment());
        return scn;
    }

    private void ReachDarkInterrupt(VirtualTableScenario scn, PhysicalCardImpl card, String action) {
        for (int i = 0; i < 30; i++) {
            if (scn.GetDecidingPlayer().equals(scn.DS) && scn.AwaitingDSForceLossPayment())
                scn.DSPayRemainingForceLossFromReserveDeck();
            else if (scn.GetDecidingPlayer().equals(scn.DS)
                    && scn.GetCurrentDecision().getText().toLowerCase().contains("optional response")
                    && scn.DSCardPlayAvailable(card, action)) return;
            else PassOptionalResponse(scn);
        }
        fail();
    }

    private void FinishCapture(VirtualTableScenario scn, PhysicalCardImpl captive, PhysicalCardImpl escort) {
        for (int i = 0; i < 30 && !captive.isCaptive(); i++) {
            if (scn.GetDecidingPlayer().equals(scn.DS) && scn.DSCaptureDecisionAvailable()) scn.DSChooseSeizeCaptive();
            else if (scn.GetDecidingPlayer().equals(scn.DS) && scn.DSHasCardChoiceAvailable(escort)) scn.DSChooseCard(escort);
            else if (scn.GetDecidingPlayer().equals(scn.DS) && scn.DSHasCardChoiceAvailable(captive)) scn.DSChooseCard(captive);
            else PassOptionalResponse(scn);
        }
        assertTrue(captive.isCaptive());
    }

    private void AssertNoReductionWhilePassingCaptureResponses(VirtualTableScenario scn) {
        for (int i = 0; i < 30 && scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"); i++) {
            if (scn.GetDecidingPlayer().equals(scn.DS))
                assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("torent"), "Reduce"));
            PassOptionalResponse(scn);
        }
    }

    private void PassOptionalResponse(VirtualTableScenario scn) {
        assertTrue(scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"));
        scn.PlayerPass(scn.GetDecidingPlayer());
    }
}
