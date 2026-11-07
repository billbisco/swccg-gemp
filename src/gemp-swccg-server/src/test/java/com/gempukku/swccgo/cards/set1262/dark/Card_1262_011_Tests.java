package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1262_011_Tests {
    /** Bring Him Before Me as the Dark Side starting Objective, deploying Throne Room, Insignificant Rebellion and Your Destiny. */
    static final StartingSetup BringHimBeforeMe = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{ put("bhbm", "9_151"); put("throne", "9_147"); put("ir", "9_127"); put("destiny", "9_134"); }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            for (int i = 0; i < 20; i++) {
                if (scn.DSDecisionAvailable("On which side")) { scn.DSChoose("Left"); continue; }
                boolean chose = false;
                for (String key : new String[]{"bhbm", "throne", "ir", "destiny"}) {
                    if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(scn.GetDSCard(key))) {
                        scn.DSChooseCard(scn.GetDSCard(key)); chose = true; break;
                    }
                }
                if (!chose) return;
            }
        }
    };

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("luke", "1_19"); put("leia", "1_17"); put("cantina", "1_128"); }},
                new HashMap<>() {{ put("sobeit", "1262_11"); put("rise", "9_140"); put("vader", "1_168"); put("trooper", "1_194"); }},
                20, 20, StartingSetup.DefaultLSGroundLocation, BringHimBeforeMe,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void SoBeItJediStatsAndIdentityMatchPrintedCard() {
        var scn = scenario();
        var bp = scn.GetDSCard("sobeit").getBlueprint();
        assertEquals("So Be It, Jedi", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertTrue(bp.isLegacy());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(5, bp.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.EFFECT); add(Icon.DEATH_STAR_II); add(Icon.LEGACY_BLOCK_6); }});
    }

    private VirtualTableScenario started(boolean soBeIt) {
        var scn = scenario();
        scn.StartGame();
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetDSCard("bhbm").getZone());
        if (soBeIt) scn.MoveCardsToDSSideOfTable(scn.GetDSCard("sobeit"));
        return scn;
    }

    @Test
    public void SoBeItJediTakesRiseMyFriendIntoHandOncePerGame() {
        var scn = started(true);
        var sobeit = scn.GetDSCard("sobeit");
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("rise"));
        assertTrue(scn.DSCardActionAvailable(sobeit, "Take Rise, My Friend into hand"));
        scn.DSUseCardAction(sobeit, "Take Rise, My Friend into hand");
        if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("rise"))) scn.DSChooseCard(scn.GetDSCard("rise"));
        scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetDSCard("rise"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(sobeit, "Take Rise, My Friend into hand"));
    }

    private void opponentCancels(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        var source = scn.GetLSCard("leia");
        var action = new TopLevelGameTextAction(source, scn.LS, source.getCardId());
        action.appendEffect(new CancelCardOnTableEffect(action, card));
        scn.carryOutEffectInPhaseActionByPlayer(scn.LS, action);
        scn.PassAllResponses();
    }

    @Test
    public void SoBeItJediCanceledByOpponentIsStackedOnInsignificantRebellion() {
        var scn = started(true);
        scn.SkipToLSTurn(Phase.DEPLOY);
        var sobeit = scn.GetDSCard("sobeit");
        opponentCancels(scn, sobeit);
        assertInZone(Zone.STACKED, sobeit);
        assertEquals(scn.GetDSCard("ir"), sobeit.getStackedOn());
    }

    @Test
    public void SoBeItJediCanceledWithoutInsignificantRebellionGoesToLostPile() {
        var scn = started(true);
        scn.MoveCardsToDSHand(scn.GetDSCard("ir"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        var sobeit = scn.GetDSCard("sobeit");
        opponentCancels(scn, sobeit);
        assertInZone(Zone.LOST_PILE, sobeit);
    }

    private static com.gempukku.swccgo.logic.effects.DuelDirections stubDuelDirections() {
        return (com.gempukku.swccgo.logic.effects.DuelDirections) java.lang.reflect.Proxy.newProxyInstance(
                Card_1262_011_Tests.class.getClassLoader(), new Class<?>[]{com.gempukku.swccgo.logic.effects.DuelDirections.class},
                (proxy, method, args) -> {
                    Class<?> r = method.getReturnType();
                    if (r == boolean.class) return false;
                    if (r == int.class) return 0;
                    if (r == float.class) return 0f;
                    return null;
                });
    }

    private boolean duelMayNotBeCanceled(boolean soBeIt, String initiator) {
        var scn = started(soBeIt);
        scn.SkipToDSTurn(Phase.CONTROL);
        var throne = scn.GetDSCard("throne");
        scn.gameState().beginDuel(scn.DS, scn.GetDSCard(initiator), throne, scn.GetDSCard("vader"), scn.GetLSCard("luke"), stubDuelDirections());
        return scn.game().getModifiersQuerying().mayNotCancelDuel(scn.gameState(), scn.GetLSCard("leia"));
    }

    @Test
    public void SoBeItJediDuelsInitiatedWithYourObjectiveMayNotBeCanceled() {
        assertTrue(duelMayNotBeCanceled(true, "bhbm"));
    }

    @Test
    public void SoBeItJediDoesNotProtectDuelsInitiatedByOtherCards() {
        assertFalse(duelMayNotBeCanceled(true, "trooper"));
        assertFalse(duelMayNotBeCanceled(false, "bhbm"));
    }

    private VirtualTableScenario lukeDeploy(boolean soBeIt) {
        var scn = started(soBeIt);
        scn.MoveLocationToTable(scn.GetLSCard("cantina"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(6);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("luke"));
        scn.SkipToPhase(Phase.DEPLOY);
        var bhbm = scn.GetDSCard("bhbm");
        assertTrue(scn.LSCardActionAvailable(bhbm, "Deploy Luke from Reserve Deck"));
        scn.LSUseCardAction(bhbm, "Deploy Luke from Reserve Deck");
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke"))) scn.LSChooseCard(scn.GetLSCard("luke"));
        return scn;
    }

    @Test
    public void SoBeItJediMakesLukeDeployToVadersSite() {
        var scn = lukeDeploy(true);
        var vaderSite = scn.GetLSStartingLocation();
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("cantina")));
        if (scn.LSHasCardChoiceAvailable(vaderSite)) scn.LSChooseCard(vaderSite);
        scn.PassAllResponses();
        assertEquals(vaderSite, scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), scn.GetLSCard("luke")));
    }

    @Test
    public void WithoutSoBeItJediLukeMayDeployElsewhere() {
        var scn = lukeDeploy(false);
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("cantina")));
    }
}
