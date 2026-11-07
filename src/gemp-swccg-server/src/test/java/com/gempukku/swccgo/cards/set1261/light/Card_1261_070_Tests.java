package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
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

public class Card_1261_070_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of(
                "obi", "1261_70", "obiSaber", "1_157", "luke", "9_24", "lukeSaber", "9_90",
                "guard", "1_26", "oldBen", "1_100")),
                new HashMap<>(Map.of("maul", "14_77", "vader", "1_168", "vaderSaber", "1_324", "trooper", "1_194")),
                40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DSStartingLocation("1_295"),
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        return scn;
    }

    @Test public void crazyWizardUsesTheExactFinalPrinting() {
        var scn = scenario(); var b = scn.GetLSCard("obi").getBlueprint();
        assertEquals("Obi-Wan, Crazy Wizard", b.getTitle()); assertEquals(1, b.getDestiny(), scn.epsilon);
        assertEquals(5, b.getDeployCost(), scn.epsilon); assertEquals(5, b.getPower(), scn.epsilon);
        assertEquals(6, b.getAbility(), scn.epsilon); assertEquals(9, b.getForfeit(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Rarity.V, b.getRarity()); assertFalse(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.REBEL)));
        scn.BlueprintPersonaCheck(b, new ArrayList<>(List.of(Persona.OBIWAN)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.REBEL, Icon.WARRIOR, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    private boolean maulCanSwingAgainAfterActualFirstShot(boolean sameSite) {
        var scn = scenario(); var obi = scn.GetLSCard("obi"); var luke = scn.GetLSCard("luke");
        var maul = scn.GetDSCard("maul"); var vader = scn.GetDSCard("vader");
        var battle = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(sameSite ? battle : scn.GetLSStartingLocation(), obi);
        scn.MoveCardsToLocation(battle, luke, maul, vader);
        scn.AttachCardsTo(luke, scn.GetLSCard("lukeSaber"));
        scn.AttachCardsTo(vader, scn.GetDSCard("vaderSaber"));
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(battle);
        var modifiers = scn.game().getModifiersQuerying();
        assertTrue(scn.IsBattleStarted());
        assertEquals(sameSite, modifiers.mayFireOnlyOncePerBattle(scn.gameState(), scn.GetDSCard("vaderSaber")));
        assertFalse(modifiers.mayFireOnlyOncePerBattle(scn.gameState(), scn.GetLSCard("lukeSaber")));
        assertTrue(scn.DSCardActionAvailable(maul, "Fire"));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(0), scn.GetDSDestiny(1));
        scn.DSUseCardAction(maul, "Fire");
        if (scn.DSHasCardChoiceAvailable(luke)) scn.DSChooseCard(luke);
        scn.PassAllResponses(); scn.PassWeaponFireWithDestinyDraw(2);
        assertFalse(luke.isHit());
        if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass();
        return scn.GetDSAvailableActions().stream().anyMatch(x -> x.contains("Double-Bladed Lightsaber"));
    }

    @Test public void crazyWizardAllowsMaulsFirstActualSwingButStopsTheSecondHere() {
        assertFalse(maulCanSwingAgainAfterActualFirstShot(true));
    }

    @Test public void maulCanStillSwingTwiceInAnActualBattleAtAnotherSite() {
        assertTrue(maulCanSwingAgainAfterActualFirstShot(false));
    }

    private void attrition(int amount, boolean immune) {
        var scn = scenario(); var obi = scn.GetLSCard("obi"); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, obi, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("lukeSaber"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site); scn.PassAllResponses();
        scn.PrepareLSDestiny(0); scn.PrepareDSDestiny(amount - 1);
        for (int i = 0; i < 4; i++) { scn.SkipToEndOfPowerSegment(true); scn.PassDestinyDrawResponses(); }
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED"); scn.PassDamageSegmentActions();
        assertEquals(amount, scn.GetUnpaidLSAttrition()); assertEquals(0, scn.GetUnpaidLSBattleDamage());
        assertEquals(immune, scn.LSDecisionAvailable("(if desired)"));
        if (immune) { scn.LSChooseCards(); assertAtLocation(site, obi); }
        else { scn.LSPayAttritionFromCardInPlay(obi); scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, obi); }
    }

    @Test public void crazyWizardSurvivesActualAttritionFour() { attrition(4, true); }
    @Test public void crazyWizardMustForfeitAtActualAttritionFive() { attrition(5, false); }


    /** Passes optional responses and orders cards going to the Lost Pile until another kind of decision is reached. */
    private void resolveLoss(VirtualTableScenario scn, PhysicalCardImpl... cards) {
        for (int i = 0; i < 12; i++) {
            var d = scn.GetCurrentDecision();
            if (d == null) return;
            if (d.getText().contains("Optional responses")) { scn.PlayerPass(scn.GetDecidingPlayer()); continue; }
            if (d.getText().contains("to put on Lost Pile")) {
                for (var card : cards) {
                    if (scn.LSHasCardChoiceAvailable(card)) { scn.LSChooseCard(card); break; }
                }
                continue;
            }
            return;
        }
    }

    /** Light initiates a battle at its (non-Tatooine) Cloud City site and must pay the given attrition. */
    private void battleToAttrition(VirtualTableScenario scn, int amount) {
        var site = scn.GetLSStartingLocation();
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site); scn.PassAllResponses();
        scn.PrepareLSDestiny(0); scn.PrepareDSDestiny(amount - 1);
        for (int i = 0; i < 4; i++) { scn.SkipToEndOfPowerSegment(true); scn.PassDestinyDrawResponses(); }
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED"); scn.PassDamageSegmentActions();
        assertEquals(amount, scn.GetUnpaidLSAttrition());
    }

    /** Forfeits the guard at the Cloud City site and reports whether Old Ben may then be played. */
    private boolean oldBenPlayableAfterGuardForfeited(VirtualTableScenario scn) {
        var guard = scn.GetLSCard("guard"); var oldBen = scn.GetLSCard("oldBen");
        scn.MoveCardsToHand(oldBen);
        scn.LSActivateForceCheat(3);
        battleToAttrition(scn, 1);
        scn.LSPayAttritionFromCardInPlay(guard);
        assertInZone(Zone.LOST_PILE, guard);
        for (int i = 0; i < 10; i++) {
            var d = scn.GetCurrentDecision();
            String player = scn.GetDecidingPlayer();
            if (player.equals(scn.LS) && d.getDecisionType().toString().equals("CARD_ACTION_CHOICE") && scn.LSCardPlayAvailable(oldBen)) return true;
            if (!d.getText().contains("Optional responses")) break;
            scn.PlayerPass(player);
        }
        return false;
    }

    @Test public void actualOldBenRevivesFromObiWansNonTatooineSiteAsAUsedInterrupt() {
        var scn = scenario(); var site = scn.GetLSStartingLocation(); var guard = scn.GetLSCard("guard");
        var oldBen = scn.GetLSCard("oldBen");
        scn.MoveCardsToLocation(site, scn.GetLSCard("obi"), scn.GetLSCard("luke"), guard, scn.GetDSCard("vader"));
        scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("lukeSaber"));
        assertFalse(site.getBlueprint().getTitle().contains("Tatooine"));
        assertTrue(oldBenPlayableAfterGuardForfeited(scn));
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(oldBen);
        for (int i = 0; i < 8 && !scn.LSDecisionAvailable("Choose card from Lost Pile"); i++) scn.PlayerPass(scn.GetDecidingPlayer());
        assertTrue(scn.LSHasCardChoiceAvailable(guard));
        scn.LSChooseCard(guard);
        scn.PassAllResponses();
        assertAtLocation(site, guard);
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertInZone(Zone.USED_PILE, oldBen);
    }

    @Test public void oldBenMayNotTargetANonTatooineSiteWhereCrazyWizardIsNot() {
        var scn = scenario(); var site = scn.GetLSStartingLocation(); var guard = scn.GetLSCard("guard");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("obi"));
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), guard, scn.GetDSCard("vader"));
        scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("lukeSaber"));
        assertFalse(oldBenPlayableAfterGuardForfeited(scn));
        assertInZone(Zone.LOST_PILE, guard);
    }

    @Test public void oldBenIsUsedOnlyWhileCrazyWizardIsOnTable() {
        var scn = scenario(); var oldBen = scn.GetLSCard("oldBen"); var obi = scn.GetLSCard("obi");
        var modifiers = scn.game().getModifiersQuerying();
        assertFalse(modifiers.hasGameTextModification(scn.gameState(), oldBen, com.gempukku.swccgo.logic.modifiers.ModifyGameTextType.LEGACY__OBI_WAN_CRAZY_WIZARD__OLD_BEN_MAY_TARGET_SITE));
        assertEquals(CardSubtype.LOST, modifiers.getInterruptType(scn.gameState(), oldBen));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), obi);
        assertEquals(CardSubtype.USED, modifiers.getInterruptType(scn.gameState(), oldBen));
        assertTrue(modifiers.hasGameTextModification(scn.gameState(), oldBen, com.gempukku.swccgo.logic.modifiers.ModifyGameTextType.LEGACY__OBI_WAN_CRAZY_WIZARD__OLD_BEN_MAY_TARGET_SITE));
        assertEquals(List.of(obi), new ArrayList<>(modifiers.getGameTextModificationSources(scn.gameState(), oldBen, com.gempukku.swccgo.logic.modifiers.ModifyGameTextType.LEGACY__OBI_WAN_CRAZY_WIZARD__OLD_BEN_MAY_TARGET_SITE)));
    }

    @Test public void yourCardsOnCrazyWizardGoToUsedPileWhenHeIsActuallyForfeited() {
        var scn = scenario(); var site = scn.GetLSStartingLocation(); var obi = scn.GetLSCard("obi");
        var obiSaber = scn.GetLSCard("obiSaber");
        scn.MoveCardsToLocation(site, obi, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.AttachCardsTo(obi, obiSaber);
        scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("lukeSaber"));
        battleToAttrition(scn, 5);
        scn.LSPayAttritionFromCardInPlay(obi);
        resolveLoss(scn, obi, obiSaber);
        assertInZone(Zone.LOST_PILE, obi);
        assertInZone(Zone.USED_PILE, obiSaber);
    }

    @Test public void cardsOnAnotherCharacterStillFollowNormalRulesWhenItIsForfeited() {
        var scn = scenario(); var site = scn.GetLSStartingLocation(); var luke = scn.GetLSCard("luke");
        var lukeSaber = scn.GetLSCard("lukeSaber");
        scn.MoveCardsToLocation(site, scn.GetLSCard("obi"), luke, scn.GetDSCard("vader"));
        scn.AttachCardsTo(luke, lukeSaber);
        battleToAttrition(scn, 5);
        scn.LSPayAttritionFromCardInPlay(luke);
        resolveLoss(scn, luke, lukeSaber);
        assertInZone(Zone.LOST_PILE, luke);
        assertInZone(Zone.LOST_PILE, lukeSaber);
    }
}
