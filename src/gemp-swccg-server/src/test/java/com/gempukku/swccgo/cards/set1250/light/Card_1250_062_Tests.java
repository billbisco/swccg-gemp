package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_062_Tests {
    private static final String STACK = "Stack the lost Wookiee";
    private static final String SPEND = "Place a stacked card out of play to add a power destiny";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("roar", "1250_62"); put("wookiee", "7_50"); put("guard", "1_26");
            put("marker", "1_109"); put("second", "11_28"); put("ordinary", "1_60");
        }}, new HashMap<>() {{
            put("emperor", "9_109"); put("lightning", "9_136"); put("snoova", "10_48");
            put("trooper", "1_194"); put("alter", "1_234"); put("vader", "1_168");
        }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FinalRoarIsUniqueNormalEffectWithPrintedIcons() {
        var s = scenario(); var bp = s.GetLSCard("roar").getBlueprint();
        assertEquals("Grrrghrrrgh!", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(5, bp.getDestiny(), s.epsilon);
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.A_NEW_HOPE, Icon.EPISODE_I, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test
    public void ActualEffectAndWookieeDeployPayThePrintedDiscountWithAnEmptyStack() {
        var s = scenario(); s.StartGame(); var roar = s.GetLSCard("roar"); var wookiee = s.GetLSCard("wookiee");
        s.MoveCardsToHand(roar, wookiee);
        s.LSActivateForceCheat(8); s.SkipToLSTurn(Phase.DEPLOY);
        s.LSDeployCard(roar); s.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, roar.getZone());
        if (s.AwaitingDSDeployPhaseActions()) s.DSPass();
        int force = s.GetLSForcePileCount(); s.LSDeployCard(wookiee);
        if (s.LSDecisionAvailable("Choose where to deploy")) s.LSChooseCard(s.GetLSStartingLocation());
        s.PassAllResponses();
        assertEquals(force - 3, s.GetLSForcePileCount()); assertEquals(s.GetLSStartingLocation(), wookiee.getAtLocation());
    }

    @Test
    public void OneStackedCardGivesFixedArmorAndImmunityToBothSidesWookiees() {
        var s = scenario(); s.StartGame(); var roar = s.GetLSCard("roar");
        var wookiee = s.GetLSCard("wookiee"); var snoova = s.GetDSCard("snoova"); var guard = s.GetLSCard("guard");
        s.MoveCardsToLSSideOfTable(roar); s.MoveCardsToLocation(s.GetLSStartingLocation(), wookiee, snoova, guard);
        var query = s.game().getModifiersQuerying();
        assertFalse(query.hasArmor(s.gameState(), wookiee));
        assertEquals(0, query.getImmunityToAttritionLessThan(s.gameState(), wookiee), s.epsilon);
        s.StackCardsOn(roar, s.GetLSCard("marker"));
        for (var card : List.of(wookiee, snoova)) {
            assertEquals(4, query.getArmor(s.gameState(), card), s.epsilon);
            assertEquals(3, query.getImmunityToAttritionLessThan(s.gameState(), card), s.epsilon);
        }
        assertFalse(query.hasArmor(s.gameState(), guard));
        s.StackCardsOn(roar, s.GetLSCard("second"));
        assertEquals(4, query.getArmor(s.gameState(), wookiee), s.epsilon);
        assertEquals(3, query.getImmunityToAttritionLessThan(s.gameState(), wookiee), s.epsilon);
        s.MoveCardsToTopOfLSUsedPile(s.GetLSCard("marker"), s.GetLSCard("second"));
        assertFalse(query.hasArmor(s.gameState(), wookiee));
    }

    private boolean offered(VirtualTableScenario s, String text) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("roar"), text);
    }

    private VirtualTableScenario lightningLoss(String name) {
        var s = scenario(); s.StartGame();
        var target = s.GetLSCard(name); var lightning = s.GetDSCard("lightning");
        s.MoveCardsToLSSideOfTable(s.GetLSCard("roar"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), target, s.GetDSCard("emperor"));
        s.MoveCardsToHand(lightning); s.SkipToDSTurn(Phase.CONTROL); s.PrepareDSDestiny(3);
        s.DSPlayCard(lightning, "Make target lost");
        if (s.DSDecisionAvailable("Choose Emperor")) s.DSChooseCard(s.GetDSCard("emperor"));
        if (s.DSDecisionAvailable("Choose character")) s.DSChooseCard(target);
        s.PassDestinyDrawResponses();
        for (int i = 0; i < 15 && !offered(s, STACK)
                && s.GetCurrentDecision().getText().contains("Optional responses"); i++) {
            s.PlayerPass(s.GetDecidingPlayer());
        }
        return s;
    }

    @Test
    public void ActualJustLostWookieeCanStackAndNonWookieeCannot() {
        var s = lightningLoss("wookiee");
        assertTrue(offered(s, STACK)); s.LSUseCardAction(s.GetLSCard("roar"), STACK); s.PassAllResponses();
        assertEquals(List.of(s.GetLSCard("wookiee")), s.GetStackedCards(s.GetLSCard("roar")));
        var other = lightningLoss("guard");
        assertFalse(offered(other, STACK)); assertTrue(other.GetLSLostPile().contains(other.GetLSCard("guard")));
    }

    private VirtualTableScenario battle(boolean stack) {
        var s = scenario(); s.StartGame(); var site = s.GetDSStartingLocation();
        s.MoveCardsToLSSideOfTable(s.GetLSCard("roar"));
        s.MoveCardsToLocation(site, s.GetLSCard("guard"), s.GetDSCard("trooper"));
        if (stack) s.StackCardsOn(s.GetLSCard("roar"), s.GetLSCard("marker"), s.GetLSCard("second"));
        s.DSActivateForceCheat(4); s.SkipToDSTurn(Phase.BATTLE);
        assertFalse(offered(s, SPEND)); s.DSInitiateBattle(site); s.PassAllResponses();
        if (s.DSAnyDecisionsAvailable()) s.DSPass();
        return s;
    }

    @Test
    public void DuringOpponentsBattleActualStackPaymentAddsOnlyPower() {
        var s = battle(true); var roar = s.GetLSCard("roar"); var paid = s.GetLSCard("marker");
        assertTrue(offered(s, SPEND)); s.LSUseCardAction(roar, SPEND); s.PassAllResponses();
        s.LSChooseCard(paid); s.PassAllResponses();
        assertEquals(Zone.OUT_OF_PLAY, paid.getZone()); assertEquals(List.of(s.GetLSCard("second")), s.GetStackedCards(roar));
        s.PrepareLSDestiny(3); s.SkipToPowerSegment(); s.PassDestinyDrawResponses();
        s.SkipBattleDestinyDraws(false);
        assertEquals(s.GetPower(s.GetLSCard("guard")) + 3, s.GetLSTotalPower());
        assertEquals(0, s.GetLSTotalDestiny());
    }

    @Test
    public void BattleWithoutStackDoesNotOfferPowerDestiny() {
        var s = battle(false); assertFalse(offered(s, SPEND));
    }

    @Test
    public void ActualAlterCancelsOtherEffectButNotRoar() {
        var s = scenario(); s.StartGame(); var roar = s.GetLSCard("roar"); var ordinary = s.GetLSCard("ordinary");
        s.MoveCardsToLSSideOfTable(roar); s.AttachCardsTo(s.GetLSStartingLocation(), ordinary);
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("vader"));
        var alter = s.GetDSCard("alter"); s.MoveCardsToHand(alter); s.SkipToDSTurn(Phase.CONTROL); s.PrepareDSDestiny(3);
        s.DSPlayCard(alter, "cancel Effect");
        if (s.DSDecisionAvailable("Choose Effect")) {
            assertFalse(s.DSHasCardChoiceAvailable(roar)); s.DSChooseCard(ordinary);
        }
        if (s.DSDecisionAvailable("Choose a highest-ability character")) s.DSChooseCard(s.GetDSCard("vader"));
        s.PassDestinyDrawResponses(); s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(ordinary)); assertEquals(Zone.SIDE_OF_TABLE, roar.getZone());
    }
}
