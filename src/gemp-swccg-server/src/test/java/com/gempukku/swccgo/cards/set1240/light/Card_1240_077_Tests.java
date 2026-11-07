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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1240_077_Tests {
    private static final String SEARCH = "Take Undercover into hand from Reserve Deck";

    private VirtualTableScenario Started() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of(
                "armor", "1240_77", "luke", "1_19", "han", "1_11", "jawa", "1_12", "harc", "7_20",
                "droid", "2_16", "undercover", "2_40", "undercover2", "2_40", "alter", "1_71", "effect", "1_66")),
                new HashMap<>(Map.of("trooper", "1_194", "vader", "1_168", "guri", "10_41",
                        "jabba", "6_109", "smuggler", "1_172", "spy", "1_163", "thief", "4_91", "blaster", "1_317")),
                45, 45, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test
    public void MercenaryArmorMatchesTheBlockFourPrinting() {
        var scn = Started();
        var card = scn.GetLSCard("armor").getBlueprint();
        assertEquals("Mercenary Armor", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_2, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>(List.of(CardType.DEVICE)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>(List.of(Keyword.DEPLOYS_ON_CHARACTERS)));
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.DEVICE, Icon.REFLECTIONS_II, Icon.LEGACY_BLOCK_4)));
    }

    private void ChooseBearer(VirtualTableScenario scn, String name) {
        var bearer = scn.GetLSCard(name);
        scn.LSChooseCard(bearer);
        scn.PassAllResponses();
        // Dark then gets its own deploy-phase decision; passing it returns the phase to Light.
        if (scn.GetAwaitingDecision(scn.DS) != null && scn.DSDecisionAvailable("action or Pass")) scn.DSPass();
        assertInZone(Zone.ATTACHED, scn.GetLSCard("armor"));
        assertEquals(bearer, scn.GetLSCard("armor").getAttachedTo());
        assertEquals(0, scn.GetLSForcePileCount());
    }

    private VirtualTableScenario ChooseArmorDeployment() {
        var scn = Started();
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetLSCard("jawa"),
                scn.GetLSCard("harc"), scn.GetLSCard("droid"), scn.GetDSCard("smuggler"));
        scn.MoveCardsToLSHand(scn.GetLSCard("armor"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.DrainLSForcePileTo(0);
        scn.LSDeployCard(scn.GetLSCard("armor"));
        return scn;
    }

    private float Armor(VirtualTableScenario scn, PhysicalCardImpl bearer) {
        return scn.game().getModifiersQuerying().getArmor(scn.gameState(), bearer);
    }

    private float Immunity(VirtualTableScenario scn, PhysicalCardImpl bearer) {
        return scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), bearer);
    }

    private boolean IsSpy(VirtualTableScenario scn, PhysicalCardImpl bearer) {
        return scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), bearer, Keyword.SPY);
    }

    @Test
    public void MercenaryArmorAllowsOwnRebelAndUnarmoredAlienButRejectsArmoredAlienDroidAndOpponent() {
        var scn = ChooseArmorDeployment();
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("jawa")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("harc")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("droid")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetDSCard("smuggler")));
        ChooseBearer(scn, "luke");
        var luke = scn.GetLSCard("luke");
        assertTrue(IsSpy(scn, luke));
        assertFalse(IsSpy(scn, scn.GetLSCard("jawa")));
        assertEquals(4, Armor(scn, luke), scn.epsilon);
        assertEquals(3, Armor(scn, scn.GetLSCard("harc")), scn.epsilon);
        assertEquals(5, Immunity(scn, luke), scn.epsilon);
        scn.MoveCardsToLSHand(scn.GetLSCard("armor"));
        assertFalse(IsSpy(scn, luke));
        assertEquals(3, Immunity(scn, luke), scn.epsilon);
    }

    @Test
    public void AnAlienKeepsSpyArmorAndSearchAfterArmorItselfGrantsArmorFour() {
        var scn = ChooseArmorDeployment();
        ChooseBearer(scn, "jawa");
        var jawa = scn.GetLSCard("jawa");
        assertTrue(IsSpy(scn, jawa));
        assertEquals(4, Armor(scn, jawa), scn.epsilon);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("undercover"));
        scn.LSUseCardAction(scn.GetLSCard("armor"), SEARCH);
        scn.LSChooseCard(scn.GetLSCard("undercover"));
        scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetLSCard("undercover"));
    }

    private void DeployOn(VirtualTableScenario scn, String name) {
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard(name));
        scn.MoveCardsToLSHand(scn.GetLSCard("armor"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.DrainLSForcePileTo(0);
        scn.LSDeployCard(scn.GetLSCard("armor"));
        ChooseBearer(scn, name);
    }

    private void ImperialBlasterFiresAtHan(boolean armored) {
        var scn = Started();
        var site = scn.GetLSStartingLocation();
        var han = scn.GetLSCard("han");
        var vader = scn.GetDSCard("vader");
        var blaster = scn.GetDSCard("blaster");
        if (armored) DeployOn(scn, "han");
        else scn.MoveCardsToLocation(site, han);
        scn.MoveCardsToLocation(site, vader);
        scn.AttachCardsTo(vader, blaster);
        scn.EnsureDSForcePile(2);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(4);
        scn.DSUseCardAction(blaster, "Fire Imperial Blaster");
        if (scn.DSHasCardChoiceAvailable(han)) scn.DSChooseCard(han);
        scn.PassAllResponses();
        assertEquals(!armored, han.isHit());
    }

    @Test public void ArmorFourMakesOriginalImperialBlasterMissAtFour() { ImperialBlasterFiresAtHan(true); }
    @Test public void OriginalImperialBlasterHitsUnarmoredHanAtFour() { ImperialBlasterFiresAtHan(false); }

    /**
     * Armored Luke alone defends against Vader, whose battle destiny totals the given attrition. Light's battle
     * damage is paid from Reserve Deck first.
     */
    private VirtualTableScenario ArmorAtAttrition(int attrition) {
        var scn = Started();
        DeployOn(scn, "luke");
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader"));
        scn.EnsureDSForcePile(1);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(attrition - 1); // Original Vader adds one to his battle destiny.
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(attrition, scn.GetDSTotalDestiny());
        for (int i = 0; i < 60 && !(scn.AwaitingLSAttritionPayment() && scn.GetUnpaidLSBattleDamage() == 0); i++) {
            if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayBattleDamageFromReserveDeck();
            else if (scn.DSDecisionAvailable("destiny?")) scn.DSChooseYes();
            else if (scn.LSDecisionAvailable("destiny?")) scn.LSChooseYes();
            else scn.PassResponses();
        }
        assertEquals(attrition, scn.GetUnpaidLSAttrition());
        assertTrue(scn.AwaitingLSAttritionPayment());
        return scn;
    }

    // Attrition is ignored only when every card present is immune to the total.
    @Test
    public void ArmorAddsTwoToLukesImmunitySoFourAttritionMayBeIgnored() {
        var scn = ArmorAtAttrition(4);
        assertEquals(5, Immunity(scn, scn.GetLSCard("luke")), scn.epsilon);
        assertTrue(scn.LSDecisionAvailable("(if desired)"));
        scn.LSPass();
        assertInZone(Zone.AT_LOCATION, scn.GetLSCard("luke"));
    }

    @Test
    public void ArmorDoesNotProtectLukeAtExactlyFiveAttrition() {
        var scn = ArmorAtAttrition(5);
        var luke = scn.GetLSCard("luke");
        assertFalse(scn.LSDecisionAvailable("(if desired)"));
        assertTrue(scn.LSHasCardChoiceAvailable(luke));
        scn.LSPayAttritionFromCardInPlay(luke);
        // Luke and his attached Armor go to the Lost Pile together; the order of the two is chosen.
        for (int i = 0; i < 6; i++) {
            scn.PassAllResponses();
            if (scn.GetCurrentDecision() == null || !scn.GetCurrentDecision().getText().contains("put on Lost Pile")) break;
            String player = scn.GetDecidingPlayer();
            var ids = scn.GetADParam(player, "cardId");
            var blueprints = scn.GetADParam(player, "blueprintId");
            scn.PlayerDecided(player, ids[Math.max(0, java.util.Arrays.asList(blueprints).indexOf(luke.getBlueprintId(true)))]);
        }
        assertTrue(luke.getZone() == Zone.LOST_PILE || luke.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    private void DrawBattleDestinyWith(String... qualifiers) {
        var scn = Started();
        DeployOn(scn, "luke");
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader"), scn.GetLSCard("han"));
        for (var name : qualifiers) scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard(name));
        scn.EnsureLSForcePile(1);
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PrepareLSDestiny(2);
        scn.PrepareDSDestiny(1);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(qualifiers.length == 0 ? 2 : 3, scn.GetLSTotalDestiny());
        assertEquals(2, scn.GetDSTotalDestiny()); // Original Vader's printed +1, no bonus from Armor.
    }

    @Test public void AnOpposingBlackSunAgentIncreasesActualDestiny() { DrawBattleDestinyWith("guri"); }
    @Test public void AnOpposingGangsterIncreasesActualDestiny() { DrawBattleDestinyWith("jabba"); }
    @Test public void AnOpposingSmugglerIncreasesActualDestiny() { DrawBattleDestinyWith("smuggler"); }
    @Test public void AnOpposingSpyIncreasesActualDestiny() { DrawBattleDestinyWith("spy"); }
    @Test public void AnOpposingThiefIncreasesActualDestiny() { DrawBattleDestinyWith("thief"); }
    @Test public void MultipleOpposingQualifiersOnlyAddOneAndDoNotAffectDarkDestiny() { DrawBattleDestinyWith("guri", "jabba", "smuggler"); }
    @Test public void AQualifyingFriendlyCardDoesNotIncreaseDestiny() { DrawBattleDestinyWith(); }

    private void PlayOriginalAlter(boolean opposingQualifier) {
        var scn = Started();
        var effect = scn.GetLSCard("effect");
        var alter = scn.GetLSCard("alter");
        scn.MoveCardsToLSSideOfTable(effect);
        if (opposingQualifier) scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("jabba"));
        scn.MoveCardsToLSHand(alter);
        DeployOn(scn, "luke");
        // Alter is played in Dark's next control phase, once Light has Force again.
        scn.EnsureLSForcePile(3);
        scn.SkipToDSTurn(Phase.CONTROL);
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass();
        scn.PrepareLSDestiny(3);
        scn.LSPlayCard(alter, "Draw destiny to cancel Effect");
        if (scn.LSHasCardChoiceAvailable(effect)) scn.LSChooseCard(effect);
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke"))) scn.LSChooseCard(scn.GetLSCard("luke"));
        scn.PassAllResponses();
        assertInZone(opposingQualifier ? Zone.SIDE_OF_TABLE : Zone.LOST_PILE, effect);
    }

    @Test public void DestinyBonusAlsoChangesTheOutcomeOfOriginalAlter() { PlayOriginalAlter(true); }
    @Test public void OriginalAlterCancelsAtThreeWithoutTheBonus() { PlayOriginalAlter(false); }

    private boolean IsSelectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        var blueprints = scn.GetADParam(scn.LS, "blueprintId");
        var selectable = scn.GetADParam(scn.LS, "selectable");
        assertNotNull(blueprints);
        assertEquals(blueprints.length, selectable.length);
        for (int i = 0; i < blueprints.length; i++) {
            if (blueprints[i].equals(card.getBlueprintId(true)) && Boolean.parseBoolean(selectable[i])) return true;
        }
        return false;
    }

    @Test
    public void MercenaryArmorTakesExactlyOneOriginalUndercoverReshufflesAndResetsNextTurn() {
        var scn = Started();
        DeployOn(scn, "luke");
        var armor = scn.GetLSCard("armor");
        var undercover = scn.GetLSCard("undercover");
        var second = scn.GetLSCard("undercover2");
        var unrelated = scn.GetLSCard("droid");
        scn.MoveCardsToTopOfLSReserveDeck(unrelated, second, undercover);
        var before = new ArrayList<>(scn.GetLSReserveDeck());
        int hand = scn.GetLSHandCount();
        scn.gameState().setRandomSeed(124077);
        scn.LSUseCardAction(armor, SEARCH);
        assertTrue(IsSelectable(scn, undercover));
        assertFalse(IsSelectable(scn, unrelated));
        scn.LSChooseCard(undercover);
        scn.PassAllResponses();
        assertEquals(hand + 1, scn.GetLSHandCount());
        assertEquals(before.size() - 1, scn.GetLSReserveDeckCount());
        assertEquals(0, scn.GetLSForcePileCount());
        var picked = List.of(undercover, second).stream().filter(card -> card.getZone() == Zone.HAND).toList();
        assertEquals(1, picked.size());
        before.remove(picked.getFirst());
        assertNotEquals(before, new ArrayList<>(scn.GetLSReserveDeck()));
        if (scn.GetAwaitingDecision(scn.DS) != null && scn.DSDecisionAvailable("action or Pass")) scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(armor, SEARCH));
        scn.SkipToDSTurn(Phase.CONTROL);
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(armor, SEARCH));
        scn.LSUseCardAction(armor, SEARCH);
        scn.LSChooseCard(picked.getFirst() == undercover ? second : undercover);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, undercover);
        assertInZone(Zone.HAND, second);
    }

    @Test
    public void MercenaryArmorCannotSearchAnEmptyReserveDeck() {
        var scn = Started();
        var luke = scn.GetLSCard("luke");
        var armor = scn.GetLSCard("armor");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), luke);
        scn.MoveCardsToLSHand(armor);
        scn.SkipToLSTurn(Phase.DEPLOY);
        // Empty Reserve Deck (into Used Pile, keeping Life Force) before the deployment, so the actions
        // offered afterwards reflect it.
        for (var card : new ArrayList<>(scn.GetLSReserveDeck())) scn.MoveCardsToTopOfLSUsedPile((PhysicalCardImpl) card);
        scn.LSDeployCard(armor);
        scn.LSChooseCard(luke);
        scn.PassAllResponses();
        if (scn.GetAwaitingDecision(scn.DS) != null && scn.DSDecisionAvailable("action or Pass")) scn.DSPass();
        assertEquals(luke, armor.getAttachedTo());
        assertEquals(0, scn.GetLSReserveDeckCount());
        assertFalse(scn.LSCardActionAvailable(armor, SEARCH));
    }

}
