package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

public class Card_1240_064_Tests {
    private static final String FIRE = "Fire Galen's Lightsaber";

    private VirtualTableScenario Started() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of(
                "luke", "1_19", "han", "1_11")),
                new HashMap<>(Map.of("saber", "1240_64", "galen", "601_81", "juno", "601_89",
                        "trooper", "1_194", "mynock", "4_110", "vader", "1_168")),
                40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test
    public void GalensLightsaberMatchesTheBlockFourPrinting() {
        var scn = Started();
        var card = scn.GetDSCard("saber").getBlueprint();
        assertEquals("Galen's Lightsaber", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertTrue(card.getLore() == null || card.getLore().isEmpty());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>(List.of(CardType.WEAPON)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>(List.of(Keyword.LIGHTSABER)));
        scn.BlueprintPersonaCheck(card, new ArrayList<>(List.of(Persona.GALENS_LIGHTSABER)));
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.WEAPON, Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_4)));
    }

    private void DeployOnGalen(VirtualTableScenario scn) {
        var galen = scn.GetDSCard("galen");
        var saber = scn.GetDSCard("saber");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), galen, scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(saber);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DrainDSForcePileTo(0);
        scn.DSDeployCard(saber);
        assertTrue(scn.DSHasCardChoiceAvailable(galen));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("vader")));
        scn.DSChooseCard(galen);
        scn.PassAllResponses();
        assertInZone(Zone.ATTACHED, saber);
        assertEquals(galen, saber.getAttachedTo());
        assertEquals(0, scn.GetDSForcePileCount());
        scn.MoveCardsToDSHand(scn.GetDSCard("vader"));
    }

    @Test public void GalensLightsaberActuallyDeploysForFreeOnlyOnGalen() { DeployOnGalen(Started()); }

    @Test
    public void GalensLightsaberCannotDeployOnVaderWithoutGalen() {
        var scn = Started();
        var saber = scn.GetDSCard("saber");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(saber);
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(saber));
        assertInZone(Zone.HAND, saber);
    }

    private float Immunity(VirtualTableScenario scn, String name) {
        return scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), scn.GetDSCard(name));
    }

    private VirtualTableScenario Defending(boolean junoHere) { return Defending(junoHere, true); }

    private VirtualTableScenario Defending(boolean junoHere, boolean withTrooper) {
        var scn = Started();
        DeployOnGalen(scn);
        var site = scn.GetDSStartingLocation();
        var junoSite = junoHere ? site : scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(junoSite, scn.GetDSCard("juno"));
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"));
        if (withTrooper) scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"));
        assertEquals(4, Immunity(scn, "galen"), scn.epsilon);
        assertEquals(3, Immunity(scn, "juno"), scn.epsilon);
        scn.EnsureLSForcePile(1);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        return scn;
    }

    @Test
    public void DefendingGalenAndJunoEachGainTwoEvenWhenJunoIsAtAnotherSite() {
        var scn = Defending(false);
        assertEquals(6, Immunity(scn, "galen"), scn.epsilon);
        assertEquals(5, Immunity(scn, "juno"), scn.epsilon);
        assertEquals(0, Immunity(scn, "trooper"), scn.epsilon);
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(1);
        scn.SkipToEndOfPowerSegment(true);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayRemainingBattleDamageFromReserveDeck();
        scn.SkipToPhase(Phase.MOVE);
        assertEquals(4, Immunity(scn, "galen"), scn.epsilon);
        assertEquals(3, Immunity(scn, "juno"), scn.epsilon);
    }

    /**
     * Finishes the remaining destiny draws and responses of the current battle, paying all battle damage of either
     * side from its Reserve Deck. Returns true when the given side must pay attrition, or false when the battle ended
     * without that side having to forfeit anything.
     */
    static boolean ToAttrition(VirtualTableScenario scn, boolean lightPays) {
        for (int i = 0; i < 60; i++) {
            if (scn.gameState().getCurrentPhase() != Phase.BATTLE) return false;
            if (lightPays ? scn.AwaitingLSAttritionPayment() && scn.GetUnpaidLSBattleDamage() == 0
                    : scn.AwaitingDSAttritionPayment() && scn.GetUnpaidDSBattleDamage() == 0) return true;
            if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayBattleDamageFromReserveDeck();
            else if (scn.AwaitingDSBattleDamagePayment()) scn.DSPayBattleDamageFromReserveDeck();
            else if (scn.DSDecisionAvailable("battle destiny?")) scn.DSChooseYes();
            else if (scn.LSDecisionAvailable("battle destiny?")) scn.LSChooseYes();
            else scn.PassResponses();
        }
        fail("Battle never reached attrition payment or its end");
        return false;
    }

    /** Galen (with his lightsaber) and Juno alone defend against Luke, who draws the given battle destiny. */
    private VirtualTableScenario DefendingAtAttrition(int amount, boolean expectPayment) {
        var scn = Defending(true, false);
        scn.PrepareLSDestiny(amount);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(1), scn.GetDSDestiny(0));
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(amount, scn.GetLSTotalDestiny());
        assertEquals(expectPayment, ToAttrition(scn, false));
        return scn;
    }

    // Attrition is ignored only when every card present is immune to the total; otherwise any card may be forfeited.
    @Test
    public void AtFourAttritionDefendingGalenAndJunoAreBothProtected() {
        var scn = DefendingAtAttrition(4, true);
        assertTrue(scn.DSDecisionAvailable("(if desired)"));
        scn.DSPass();
        var site = scn.GetDSStartingLocation();
        assertAtLocation(site, scn.GetDSCard("galen"));
        assertAtLocation(site, scn.GetDSCard("juno"));
    }

    @Test
    public void JunoIsVulnerableAtFiveSoAttritionMustBeSatisfied() {
        var scn = DefendingAtAttrition(5, true);
        var juno = scn.GetDSCard("juno");
        assertEquals(5, scn.GetUnpaidDSAttrition());
        assertFalse(scn.DSDecisionAvailable("(if desired)"));
        assertTrue(scn.DSHasCardChoiceAvailable(juno));
        scn.DSPayAttritionFromCardInPlay(juno);
        assertTrue(juno.getZone() == Zone.LOST_PILE || juno.getZone() == Zone.TOP_OF_LOST_PILE);
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetDSCard("galen"));
    }

    @Test
    public void GalenIsVulnerableAtSixDespiteDefending() {
        var scn = DefendingAtAttrition(6, true);
        var galen = scn.GetDSCard("galen");
        assertEquals(6, scn.GetUnpaidDSAttrition());
        assertFalse(scn.DSDecisionAvailable("(if desired)"));
        assertTrue(scn.DSHasCardChoiceAvailable(galen));
    }

    private VirtualTableScenario Attacking() {
        var scn = Started();
        DeployOnGalen(scn);
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetLSCard("han"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("juno"));
        scn.EnsureDSForcePile(3);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        return scn;
    }

    @Test
    public void GalensLightsaberDoesNotIncreaseImmunityWhenGalenInitiates() {
        var scn = Attacking();
        assertEquals(4, Immunity(scn, "galen"), scn.epsilon);
        assertEquals(3, Immunity(scn, "juno"), scn.epsilon);
    }

    private void FireAtLuke(VirtualTableScenario scn, int first, int second) {
        var luke = scn.GetLSCard("luke");
        var one = scn.GetDSDestiny(first);
        var two = scn.GetDSDestiny(second);
        scn.MoveCardsToTopOfDSReserveDeck(two, one);
        int used = scn.GetDSUsedPileCount();
        assertEquals(4, scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(), luke), scn.epsilon);
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(scn.GetDSCard("saber"), FIRE);
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        assertEquals(force, scn.GetDSForcePileCount());
        assertEquals(used + 2, scn.GetDSUsedPileCount());
        assertInZone(Zone.USED_PILE, one);
        assertInZone(Zone.USED_PILE, two);
    }

    @Test
    public void TwoWeaponDestiniesEqualToDefenseMissWithoutChangingPowerOrAttritionEligibility() {
        var scn = Attacking();
        var luke = scn.GetLSCard("luke");
        FireAtLuke(scn, 1, 3);
        assertFalse(luke.isHit());
        assertEquals(3, scn.GetPower(luke));
        assertFalse(scn.game().getModifiersQuerying().cannotSatisfyAttrition(scn.gameState(), luke));
    }

    @Test
    public void TwoWeaponDestiniesOneAboveDefenseHitAndRemoveThreePowerAndAttritionEligibility() {
        var scn = Attacking();
        var luke = scn.GetLSCard("luke");
        var han = scn.GetLSCard("han");
        FireAtLuke(scn, 2, 3);
        assertTrue(luke.isHit());
        assertEquals(0, scn.GetPower(luke));
        assertEquals(3, scn.GetPower(han));
        assertTrue(scn.game().getModifiersQuerying().cannotSatisfyAttrition(scn.gameState(), luke));
        assertFalse(scn.game().getModifiersQuerying().cannotSatisfyAttrition(scn.gameState(), han));
    }

    @Test
    public void ForfeitingHitLukeForBattleDamageDoesNotPayAnyAttrition() {
        var scn = Attacking();
        var luke = scn.GetLSCard("luke");
        var han = scn.GetLSCard("han");
        FireAtLuke(scn, 2, 3);
        scn.PrepareDSDestiny(4);
        scn.PrepareLSDestiny(2);
        scn.SkipToEndOfPowerSegment(true);
        for (int i = 0; i < 20 && !scn.AwaitingLSBattleDamagePayment(); i++) {
            if (scn.DSDecisionAvailable("battle destiny?")) scn.DSChooseYes();
            else if (scn.LSDecisionAvailable("battle destiny?")) scn.LSChooseYes();
            else if (scn.DSDecisionAvailable("(if desired)")) scn.DSPass();
            else scn.PassResponses();
        }
        assertEquals(4, scn.GetUnpaidLSAttrition());
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSPayBattleDamageFromCardInPlay(luke);
        assertInZone(Zone.LOST_PILE, luke);
        assertEquals(4, scn.GetUnpaidLSAttrition());
        scn.LSPayAttritionFromCardInPlay(han);
        assertInZone(Zone.LOST_PILE, han);
    }

    @Test
    public void GalensLightsaberCanActuallyHitAnOriginalCreatureDuringANativeAttack() {
        var scn = Started();
        DeployOnGalen(scn);
        var site = scn.GetDSStartingLocation();
        var mynock = scn.GetDSCard("mynock");
        var saber = scn.GetDSCard("saber");
        scn.MoveCardsToLocation(site, mynock);
        scn.EnsureDSForcePile(1);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(site, "attack");
        scn.PassAllResponses();
        int force = scn.GetDSForcePileCount();
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(3), scn.GetDSDestiny(2));
        assertTrue(scn.DSCardActionAvailable(saber, FIRE));
        scn.DSUseCardAction(saber, FIRE);
        if (scn.DSHasCardChoiceAvailable(mynock)) scn.DSChooseCard(mynock);
        scn.PassAllResponses();
        assertTrue(mynock.isHit() || mynock.getZone() == Zone.LOST_PILE);
        assertEquals(force, scn.GetDSForcePileCount());
    }
}
