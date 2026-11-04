package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.effects.RespondableWeaponFiringEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BooleanSupplier;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_016_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("a3", "1274_16");
                    put("luke", "1_19");
                    put("han", "1_11");
                    put("leia", "1_17");
                    put("droids", "1108_6");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("rifle", "1_312");
                    put("vader", "1_168");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    static void passUntil(VirtualTableScenario scn, BooleanSupplier done) {
        for (int i = 0; i < 30; ++i) {
            if (done.getAsBoolean())
                return;
            if (scn.LSAnyDecisionsAvailable())
                scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable())
                scn.DSPass();
            else
                return;
        }
    }

    private static boolean lsHasAction(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        return scn.LSAnyDecisionsAvailable() && !scn.GetLSAvailableActions().isEmpty() && scn.LSCardActionAvailable(card, text);
    }

    @Test
    public void ArtooAndThreepioVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("a3").getBlueprint();

        assertEquals("Artoo & Threepio", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.R2D2);
            add(Persona.C3PO);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.ASTROMECH);
            add(ModelType.PROTOCOL);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.NAV_COMPUTER);
            add(Icon.DROID);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ArtooAndThreepioVWhenDeployedMayDrawTopCardOfReserveDeck() {
        var scn = GetScenario();
        var a3 = scn.GetLSCard("a3");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(a3);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(4);
        scn.MoveCardsToTopOfOwnReserveDeck(leia);
        scn.LSDeployCard(a3);
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        passUntil(scn, () -> lsHasAction(scn, a3, "Draw top card of Reserve Deck"));
        assertTrue(lsHasAction(scn, a3, "Draw top card of Reserve Deck"));
        scn.LSUseCardAction(a3, "Draw top card of Reserve Deck");
        scn.PassAllResponses();
        assertInZone(Zone.HAND, leia);
    }

    /** Dark Side fires a Blaster Rifle at the given Light Side character during a battle at Tatooine: Marketplace. */
    private void fireRifleAt(VirtualTableScenario scn, PhysicalCardImpl target) {
        var trooper = scn.GetDSCard("trooper");
        var rifle = scn.GetDSCard("rifle");
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, trooper);
        scn.AttachCardsTo(trooper, rifle);
        scn.EnsureLSForcePile(2);
        scn.EnsureDSForcePile(2);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.DSUseCardAction(rifle, "Fire");
        scn.DSChooseCard(target);
        scn.PassForceUseResponses();
    }

    @Test
    public void ArtooAndThreepioVReTargetsWeaponTargetingLukeToThisDroidAndTargetingMayNotBeCanceled() {
        var scn = GetScenario();
        var a3 = scn.GetLSCard("a3");
        var luke = scn.GetLSCard("luke");
        var droids = scn.GetLSCard("droids");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, a3, luke);
        scn.MoveCardsToHand(droids);
        fireRifleAt(scn, luke);

        passUntil(scn, () -> lsHasAction(scn, a3, "Re-target"));
        assertTrue(lsHasAction(scn, a3, "Re-target"));
        scn.LSUseCardAction(a3, "Re-target");
        if (scn.LSHasCardChoiceAvailable(luke)) {
            scn.LSChooseCard(luke);
        }
        passUntil(scn, () -> scn.gameState().getWeaponFiringState() == null
                || scn.gameState().getWeaponFiringState().getTargets().contains(a3)
                || lsHasAction(scn, droids, "Cancel"));
        var firingState = scn.gameState().getWeaponFiringState();
        assertTrue(firingState.getTargets().contains(a3));
        assertFalse(firingState.getTargets().contains(luke));

        // Don't Forget The Droids may be played, but the targeting is not canceled
        passUntil(scn, () -> scn.gameState().getWeaponFiringState() == null || lsHasAction(scn, droids, "Cancel"));
        assertTrue(lsHasAction(scn, droids, "Cancel"));
        var effect = (RespondableWeaponFiringEffect) scn.gameState().getWeaponFiringState().getWeaponFiringEffect();
        scn.LSUseCardAction(droids, "Cancel");
        passUntil(scn, () -> droids.getZone() != Zone.VOID);
        assertInZone(Zone.USED_PILE, droids);
        assertFalse(effect.isCanceled());
    }

    @Test
    public void ArtooAndThreepioVDoesNotReTargetWeaponTargetingOtherCharacters() {
        var scn = GetScenario();
        var a3 = scn.GetLSCard("a3");
        var han = scn.GetLSCard("han");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, a3, han);
        fireRifleAt(scn, han);

        final boolean[] offered = {false};
        passUntil(scn, () -> {
            if (lsHasAction(scn, a3, "Re-target"))
                offered[0] = true;
            return offered[0] || scn.gameState().getWeaponFiringState() == null;
        });
        assertFalse(offered[0]);
    }

    @Test
    public void ArtooAndThreepioVMayNotReTargetWhileHit() {
        var scn = GetScenario();
        var a3 = scn.GetLSCard("a3");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, a3, leia);
        a3.setHit(true);
        fireRifleAt(scn, leia);

        final boolean[] offered = {false};
        passUntil(scn, () -> {
            if (lsHasAction(scn, a3, "Re-target"))
                offered[0] = true;
            return offered[0] || scn.gameState().getWeaponFiringState() == null;
        });
        assertFalse(offered[0]);
    }

    @Test
    public void ArtooAndThreepioVJustForfeitedDuringOpponentsTurnMayBeTakenIntoHand() {
        var scn = GetScenario();
        var a3 = scn.GetLSCard("a3");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, a3, scn.GetLSCard("leia"), vader);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(2);
        scn.DSInitiateBattle(site);
        scn.PrepareDSDestiny(1);
        scn.SkipToDamageSegment(true);
        assertTrue(scn.AwaitingLSBattleDamagePayment() || scn.AwaitingLSAttritionPayment());
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSChooseCard(a3);
        passUntil(scn, () -> lsHasAction(scn, a3, "Take into hand from Lost Pile"));
        assertTrue(lsHasAction(scn, a3, "Take into hand from Lost Pile"));
        scn.LSUseCardAction(a3, "Take into hand from Lost Pile");
        scn.PassAllResponses();
        assertInZone(Zone.HAND, a3);
    }
}
