package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_033_Tests {
    private static final String BOOST = "Add 1 to Force drain";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("order", "1250_33"); put("endor", "8_68"); put("site", "8_69"); put("otherSite", "8_77");
            put("nonBg", "8_70"); put("unrelated", "12_74"); put("bespin", "5_76"); put("clouds", "5_85");
            put("capital", "2_73"); put("red", "103_2"); put("gold", "103_1"); put("artoo", "111_2");
            put("luke", "1_19"); put("trooper", "1_28"); put("speeder", "3_69"); put("k2", "206_5");
            put("objective", "14_52");
        }}, new HashMap<>() {{
            put("capital", "1_301"); put("droid", "14_73"); put("trooper", "1_194"); put("scout", "8_175");
        }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario setup() {
        var s = scenario(); s.StartGame();
        for (String label : List.of("endor", "site", "otherSite", "nonBg", "unrelated", "bespin", "clouds")) {
            s.MoveLocationToTable(s.GetLSCard(label));
        }
        s.MoveCardsToLSSideOfTable(s.GetLSCard("order"));
        return s;
    }

    @Test
    public void LetsGoLeftHasTheFinalErrataMetadata() {
        var s = scenario(); var bp = s.GetLSCard("order").getBlueprint();
        assertEquals("Let's Go Left", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(6, bp.getDestiny(), s.epsilon);
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.ADMIRALS_ORDER)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.ADMIRALS_ORDER, Icon.EPISODE_I,
                Icon.THEED_PALACE, Icon.LEGACY_BLOCK_5)));
    }

    private void deployAndPay(VirtualTableScenario s, boolean dark, PhysicalCardImpl card, PhysicalCardImpl site, int expected) {
        s.MoveCardsToHand(card);
        if (dark) { s.SkipToDSTurn(Phase.DEPLOY); s.DSActivateForceCheat(8); }
        else { s.SkipToLSTurn(Phase.DEPLOY); s.LSActivateForceCheat(8); }
        int force = dark ? s.GetDSForcePileCount() : s.GetLSForcePileCount();
        if (dark) s.DSDeployCard(card); else s.LSDeployCard(card);
        s.PassAllResponses();
        assertTrue(s.GetCurrentDecision().getText(), s.GetCurrentDecision().getText().contains("deploy"));
        if (dark) s.DSChooseCard(site); else s.LSChooseCard(site);
        s.PassAllResponses();
        assertEquals(site, s.game().getModifiersQuerying().getLocationThatCardIsAt(s.gameState(), card));
        assertEquals(expected, force - (dark ? s.GetDSForcePileCount() : s.GetLSForcePileCount()));
    }

    @Test
    public void LightCapitalControlDiscountsActualCharacterDeployment() {
        var s = setup(); s.MoveCardsToLocation(s.GetLSCard("endor"), s.GetLSCard("capital"));
        deployAndPay(s, false, s.GetLSCard("luke"), s.GetLSCard("site"), 2);
    }

    @Test
    public void DarkCapitalControlDiscountsActualVehicleDeployment() {
        var s = setup(); s.MoveCardsToLocation(s.GetLSCard("endor"), s.GetDSCard("capital"));
        deployAndPay(s, true, s.GetDSCard("scout"), s.GetLSCard("site"), 2);
    }

    @Test
    public void PresenceCharacterGetsNoDiscountEvenWithOwnCapital() {
        var s = setup(); s.MoveCardsToLocation(s.GetLSCard("endor"), s.GetDSCard("capital"));
        deployAndPay(s, true, s.GetDSCard("droid"), s.GetLSCard("site"), 3);
    }

    @Test
    public void CapitalAtContestedSystemDoesNotDiscountDeployment() {
        var s = setup(); var endor = s.GetLSCard("endor");
        s.MoveCardsToLocation(endor, s.GetLSCard("capital"), s.GetDSCard("capital"));
        assertTrue(Filters.occupies(s.LS).accepts(s.game(), endor));
        assertFalse(Filters.controls(s.LS).accepts(s.game(), endor));
        deployAndPay(s, false, s.GetLSCard("luke"), s.GetLSCard("site"), 3);
    }

    @Test
    public void CapitalControlDoesNotDiscountUnrelatedSiteOrOpposingCards() {
        var s = setup(); s.MoveCardsToLocation(s.GetLSCard("endor"), s.GetLSCard("capital"));
        deployAndPay(s, false, s.GetLSCard("luke"), s.GetLSCard("unrelated"), 3);
        deployAndPay(s, true, s.GetDSCard("scout"), s.GetLSCard("site"), 3);
    }

    @Test
    public void CapitalControlDiscountAlsoAppliesToRelatedSector() {
        var s = setup(); var bespin = s.GetLSCard("bespin");
        s.MoveCardsToLocation(bespin, s.GetLSCard("capital"));
        var clouds = s.GetLSCard("clouds");
        assertTrue(Filters.relatedSectorTo(s.GetLSCard("order"), Filters.and(bespin)).accepts(s.game(), clouds));
        var query = s.game().getModifiersQuerying();
        var luke = s.GetLSCard("luke");
        assertEquals(2, query.getDeployCost(s.gameState(), luke, luke, clouds, false, null, false, 0, null, true), s.epsilon);
        s.MoveCardsToLocation(s.GetLSCard("endor"), s.GetLSCard("capital"));
        assertEquals(3, query.getDeployCost(s.gameState(), luke, luke, clouds, false, null, false, 0, null, true), s.epsilon);
    }

    private boolean boostOffered(VirtualTableScenario s) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("order"), BOOST);
    }

    private void lightDrain(VirtualTableScenario s, String label, boolean offer, boolean take, int displayed, int lost) {
        var site = s.GetLSCard(label);
        s.LSForceDrainAt(site);
        for (int i = 0; i < 20 && !boostOffered(s) && !s.AwaitingDSForceLossPayment(); i++) {
            String prompt = s.GetCurrentDecision().getText();
            assertTrue(prompt, prompt.contains("Optional responses"));
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertEquals(offer, boostOffered(s));
        if (take) s.LSUseCardAction(s.GetLSCard("order"), BOOST);
        s.PassAllResponses();
        assertEquals(displayed, s.GetForceDrainTotal());
        int initial = s.GetDSLostPileCount();
        for (int i = 0; i < 8 && s.AwaitingDSForceLossPayment(); i++) {
            s.DSPayForceLossFromReserveDeck(); s.PassAllResponses();
        }
        assertEquals(lost, s.GetDSLostPileCount() - initial);
        assertFalse(s.IsActiveForceDrain());
    }

    @Test
    public void EachRelatedDrainMayBeBoostedAndActualLossIsCapped() {
        var s = setup(); var endor = s.GetLSCard("endor");
        s.MoveCardsToLocation(endor, s.GetLSCard("capital"), s.GetDSCard("capital"));
        s.MoveCardsToLocation(s.GetLSCard("site"), s.GetLSCard("luke"));
        s.MoveCardsToLocation(s.GetLSCard("otherSite"), s.GetLSCard("trooper"));
        s.SkipToLSTurn(Phase.CONTROL);
        lightDrain(s, "site", true, true, 3, 2);
        lightDrain(s, "otherSite", true, true, 2, 2);
    }

    @Test
    public void LightMayDeclineBoostWithoutChangingDrainOrLoss() {
        var s = setup();
        s.MoveCardsToLocation(s.GetLSCard("endor"), s.GetLSCard("capital"));
        s.MoveCardsToLocation(s.GetLSCard("otherSite"), s.GetLSCard("luke"));
        s.SkipToLSTurn(Phase.CONTROL);
        lightDrain(s, "otherSite", true, false, 1, 1);
    }

    private void fleetBoundary(boolean thirdAtSystem, boolean objective, String location, boolean offer, int base) {
        var s = setup(); var endor = s.GetLSCard("endor");
        s.MoveCardsToLocation(endor, s.GetLSCard("red"), s.GetLSCard("gold"));
        s.MoveCardsToLocation(thirdAtSystem ? endor : s.GetLSCard("bespin"), s.GetLSCard("artoo"));
        if (objective) s.MoveCardsToLSSideOfTable(s.GetLSCard("objective"));
        s.MoveCardsToLocation(s.GetLSCard(location), s.GetLSCard("luke"));
        s.SkipToLSTurn(Phase.CONTROL);
        lightDrain(s, location, offer, offer, base + (offer ? 1 : 0), Math.min(2, base + (offer ? 1 : 0)));
    }

    @Test
    public void ThreeUniqueStarshipsAtOneSystemQualifyWithoutCapital() { fleetBoundary(true, false, "otherSite", true, 1); }

    @Test
    public void TwoAtTheSystemAndThirdElsewhereDoNotQualify() { fleetBoundary(false, false, "otherSite", false, 1); }

    @Test
    public void ObjectiveDisablesTheBoostDespiteThreeUniqueStarships() { fleetBoundary(true, true, "otherSite", false, 1); }

    @Test
    public void UnrelatedBattlegroundDoesNotGetTheBoost() { fleetBoundary(true, false, "unrelated", false, 1); }

    @Test
    public void RelatedNonBattlegroundDoesNotGetTheBoost() { fleetBoundary(true, false, "nonBg", false, 2); }
}
