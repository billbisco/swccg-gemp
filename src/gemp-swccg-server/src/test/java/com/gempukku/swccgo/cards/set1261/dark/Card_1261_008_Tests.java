package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_008_Tests {
    @Test
    public void printedArmoredRoyalGuardGetsActualImmunityToAttritionLessThanThree() {
        var scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>(Map.of("jax", "1261_8")),
                12, 12, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        var jax = scn.GetDSCard("jax");
        var bp = jax.getBlueprint();
        assertEquals("Carnor Jax, Royal Guard", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(2, bp.getDestiny(), scn.epsilon);
        assertEquals(4, bp.getDeployCost(), scn.epsilon);
        assertEquals(5, bp.getPower(), scn.epsilon);
        assertEquals(2, bp.getAbility(), scn.epsilon);
        assertEquals(3, bp.getArmor(), scn.epsilon);
        assertEquals(5, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.IMPERIAL)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.IMPERIAL, Icon.WARRIOR,
                Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.ROYAL_GUARD)));
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), jax);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), jax),
                scn.epsilon);
    }

    private VirtualTableScenario gameplayScenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19", "rebelguard", "1_26")),
                new HashMap<>(Map.of("jax", "1261_8", "trooper", "1_194", "trooper2", "1_194",
                        "pike", "9_179", "guard", "9_119", "pike2", "9_179", "site2", "7_277")),
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private static float deployCostTo(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card,
            com.gempukku.swccgo.game.PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }

    @Test
    public void opponentMayNotIncreaseDeployCostOfImperialsToHereOnly() {
        var scn = gameplayScenario();
        var jax = scn.GetDSCard("jax");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        var here = scn.GetDSStartingLocation();
        var elsewhere = scn.GetDSCard("site2");
        scn.MoveLocationToTable(elsewhere);
        scn.MoveCardsToLocation(here, jax);
        // Sources of the ad hoc deploy cost modifiers are on table so their modifiers are active.
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"), scn.GetDSCard("guard"));
        scn.MoveCardsToHand(trooper);
        float printed = trooper.getBlueprint().getDeployCost();
        assertEquals(printed, deployCostTo(scn, trooper, here), scn.epsilon);
        // Opponent's increase
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.DeployCostModifier(
                scn.GetLSCard("luke"), com.gempukku.swccgo.filters.Filters.Imperial, 2));
        assertEquals("Opponent's increase blocked here", printed, deployCostTo(scn, trooper, here), scn.epsilon);
        assertEquals("Opponent's increase still applies elsewhere", printed + 2,
                deployCostTo(scn, trooper, elsewhere), scn.epsilon);
        // Owner's own increase is not blocked
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.DeployCostModifier(
                scn.GetDSCard("guard"), com.gempukku.swccgo.filters.Filters.Imperial, 1));
        assertEquals(printed + 1, deployCostTo(scn, trooper, here), scn.epsilon);
    }

    private void excludeDuringBattle(VirtualTableScenario scn, String player,
            com.gempukku.swccgo.game.PhysicalCardImpl source, com.gempukku.swccgo.game.PhysicalCardImpl target) {
        var action = new com.gempukku.swccgo.logic.actions.TopLevelGameTextAction(source, player, source.getCardId());
        action.appendEffect(new com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect(action, Map.of(target, source)));
        scn.carryOutEffectInPhaseActionByPlayer(player, action);
        scn.PassAllResponses();
    }

    @Test(timeout = 10000)
    public void imperialsHereMayNotBeExcludedFromBattleByOpponent() {
        var scn = gameplayScenario();
        var jax = scn.GetDSCard("jax");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, jax, trooper, luke);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.DSPass();
        excludeDuringBattle(scn, scn.LS, luke, trooper);
        assertFalse(scn.game().getModifiersQuerying().isExcludedFromBattle(scn.gameState(), trooper));
        assertTrue(scn.gameState().getBattleState().isCardParticipatingInBattle(trooper));
    }

    @Test(timeout = 10000)
    public void ownerMayStillExcludeTheirImperialsHere() {
        var scn = gameplayScenario();
        var jax = scn.GetDSCard("jax");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, jax, trooper, luke);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        excludeDuringBattle(scn, scn.DS, jax, trooper);
        assertTrue(scn.game().getModifiersQuerying().isExcludedFromBattle(scn.gameState(), trooper));
    }

    @Test(timeout = 10000)
    public void opponentMayExcludeImperialsWhenJaxIsNotThere() {
        var scn = gameplayScenario();
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("jax"));
        scn.MoveCardsToLocation(site, trooper, scn.GetDSCard("trooper2"), luke);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.DSPass();
        excludeDuringBattle(scn, scn.LS, luke, trooper);
        assertTrue(scn.game().getModifiersQuerying().isExcludedFromBattle(scn.gameState(), trooper));
    }

    /** Fires the Force Pike carried by the bearer at Luke, using the two-destiny option when offered and taking the
     * other destiny into hand. Returns whether the option was offered. */
    private boolean firePikeAtLuke(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl bearer,
            com.gempukku.swccgo.game.PhysicalCardImpl pike, boolean takeIntoHand) {
        var site = scn.GetDSStartingLocation();
        var luke = scn.GetLSCard("luke");
        scn.MoveCardsToLocation(site, bearer, luke);
        scn.AttachCardsTo(bearer, pike);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(1);
        scn.PrepareDSDestiny(5);
        assertTrue(scn.DSCardActionAvailable(pike, "Fire"));
        scn.DSUseCardAction(pike, "Fire");
        scn.DSChooseCard(luke);
        boolean offered = false;
        for (int i = 0; i < 60; i++) {
            if (scn.gameState().getWeaponFiringState() == null && i > 2)
                break;
            var decision = scn.GetCurrentDecision();
            String text = decision.getText().toLowerCase();
            boolean dsActions = scn.DSGetDecision() != null && decision.getDecisionParameters().get("actionId") != null;
            if (scn.DSGetDecision() != null && text.startsWith("choose 1 destiny")) {
                var ids = decision.getDecisionParameters().get("cardId");
                scn.PlayerDecided(scn.DS, ids[0]);
            } else if (scn.DSGetDecision() != null && text.contains("into hand")) {
                if (takeIntoHand) scn.DSChooseYes(); else scn.DSChooseNo();
            } else if (dsActions && scn.DSCardActionAvailable(bearer, "Draw two weapon destiny")) {
                offered = true;
                scn.DSUseCardAction(bearer, "Draw two weapon destiny");
            } else if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else {
                scn.LSPass();
            }
        }
        return offered;
    }

    @Test(timeout = 15000)
    public void jaxFiringForcePikeMayDrawTwoWeaponDestinyAndTakeTheOtherIntoHand() {
        var scn = gameplayScenario();
        var jax = scn.GetDSCard("jax");
        scn.StartGame();
        int handBefore = scn.GetDSHandCount();
        assertTrue(firePikeAtLuke(scn, jax, scn.GetDSCard("pike"), true));
        assertEquals("The unchosen destiny was taken into hand", handBefore + 1, scn.GetDSHandCount());
    }

    @Test(timeout = 15000)
    public void unchosenDestinyIsNotTakenIntoHandWhenDeclined() {
        var scn = gameplayScenario();
        var jax = scn.GetDSCard("jax");
        scn.StartGame();
        int handBefore = scn.GetDSHandCount();
        assertTrue(firePikeAtLuke(scn, jax, scn.GetDSCard("pike"), false));
        assertEquals(handBefore, scn.GetDSHandCount());
    }

    @Test(timeout = 15000)
    public void otherRoyalGuardFiringForcePikeDoesNotGetTwoDestinyOption() {
        var scn = gameplayScenario();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("jax"));
        assertFalse(firePikeAtLuke(scn, scn.GetDSCard("guard"), scn.GetDSCard("pike2"), true));
    }
}
