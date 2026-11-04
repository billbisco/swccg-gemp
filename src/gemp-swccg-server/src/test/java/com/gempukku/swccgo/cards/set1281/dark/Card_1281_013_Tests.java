package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_013_Tests {
    private static final String CONFERENCE_ROOM = "2_144"; // Death Star: Conference Room

    protected VirtualTableScenario GetScenario(StartingSetup dsStartingInterrupts) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("lsTrooper", "3_6"); // Echo Base Trooper
                }},
                new HashMap<>() {{
                    put("circle", "1281_13");
                    put("simaloo", "9_120"); // Sim Aloo
                    put("ordmantell", "3_151"); // Ord Mantell (system)
                    put("trooper", "1_194");
                }},
                20, 20,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DSStartingLocation(CONFERENCE_ROOM),
                StartingSetup.NoLSStartingInterrupts,
                dsStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    protected VirtualTableScenario GetScenario() {
        return GetScenario(StartingSetup.NoDSStartingInterrupts);
    }

    // Stand-in for (Virtual Block 4) There Is No Try & Oppressive Enforcement (not coded on this master).
    private static AbstractNormalEffect vb4ThereIsNoTry() {
        return new AbstractNormalEffect(Side.DARK, 1, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.There_Is_No_Try, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addIcons(Icon.LEGACY_BLOCK_4);
            }
        };
    }

    @Test
    public void TheInnerCircleStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("circle").getBlueprint();
        assertEquals("The Inner Circle", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertTrue(card.isImmuneToCardTitle(Title.Ineffective_Maneuver));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.SETUP);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TheInnerCircleMayNotDeployAfterStartOfGame() {
        var scn = GetScenario();
        var circle = scn.GetDSCard("circle");
        scn.StartGame();
        scn.MoveCardsToHand(circle);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(circle));
    }

    @Test
    public void TheInnerCircleDeploysAtStartOfGameWithSimAlooAndThereIsNoTry() {
        var scn = GetScenario();
        var circle = scn.GetDSCard("circle");
        var simaloo = scn.GetDSCard("simaloo");
        var room = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfOwnReserveDeck(circle);
        var noTry = MissionTestSupport.putInReserve(scn, 98401, scn.DS, vb4ThereIsNoTry());
        // Stand-in for the start-of-game card that deploys The Inner Circle from Reserve Deck: replay the
        // start-of-game window (Phase.PLAY_STARTING_CARDS) for one action.
        var action = new TopLevelGameTextAction(circle, scn.DS, circle.getCardId());
        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(com.gempukku.swccgo.game.SwccgGame game) {
                game.getGameState().setCurrentPhase(Phase.PLAY_STARTING_CARDS);
            }
        });
        action.appendEffect(new DeployCardToTargetFromReserveDeckEffect(action, scn.DS, Filters.title("The Inner Circle"), Filters.Death_Star_Conference_Room, false, null, null, false));
        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(com.gempukku.swccgo.game.SwccgGame game) {
                game.getGameState().setCurrentPhase(Phase.DEPLOY);
            }
        });
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        for (int i = 0; i < 30 && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(circle)) {
                scn.DSChooseCard(circle);
            }
            else if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(room) && circle.getZone() != Zone.ATTACHED) {
                scn.DSChooseCard(room);
            }
            else if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(simaloo)) {
                scn.DSChooseCard(simaloo);
            }
            else if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(room)) {
                scn.DSChooseCard(room);
            }
            else if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(noTry)) {
                scn.DSChooseCard(noTry);
            }
            else if (scn.DSGetDecision() != null) {
                scn.DSPass();
            }
            else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            }
        }
        assertTrue(scn.IsAttachedTo(room, circle));
        assertAtLocation(room, simaloo);
        assertInZone(Zone.SIDE_OF_TABLE, noTry);
    }

    private PhysicalCardImpl attachCircle(VirtualTableScenario scn) {
        var circle = scn.GetDSCard("circle");
        scn.AttachCardsTo(scn.GetDSStartingLocation(), circle);
        return circle;
    }

    @Test
    public void TheInnerCirclePlayersInitiateBattlesHereForFree() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        attachCircle(scn);
        var room = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(room, trooper, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(2);
        int force = scn.GetDSForcePile().size();
        scn.DSInitiateBattle(room);
        assertTrue(scn.IsBattleStarted());
        assertEquals(force, scn.GetDSForcePile().size());
    }

    @Test
    public void TheInnerCircleDeploysOrdMantellFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var ordmantell = scn.GetDSCard("ordmantell");
        scn.StartGame();
        var circle = attachCircle(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfOwnReserveDeck(ordmantell);
        assertTrue(scn.DSCardActionAvailable(circle, "Deploy Ord Mantell"));
        scn.DSUseCardAction(circle, "Deploy Ord Mantell");
        if (scn.DSHasCardChoiceAvailable(ordmantell)) {
            scn.DSChooseCard(ordmantell);
        }
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, ordmantell.getZone());
        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        assertFalse(scn.DSCardActionAvailable(circle, "Deploy Ord Mantell"));
    }

    @Test
    public void TheInnerCircleMayNotBeCanceledExceptByOpponentOrPlacedInUsedPile() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        var circle = attachCircle(scn);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), trooper, luke);
        scn.SkipToDSTurn(Phase.DEPLOY);
        var cancel = Collections.singleton(TargetingReason.TO_BE_CANCELED);
        assertFalse(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), circle, trooper, cancel));
        assertTrue(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), circle, luke, cancel));
        assertTrue(scn.game().getModifiersQuerying().mayNotBePlacedInUsedPile(scn.gameState(), circle, scn.LS));
    }
}
