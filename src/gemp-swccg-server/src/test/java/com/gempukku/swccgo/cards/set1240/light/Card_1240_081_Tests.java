package com.gempukku.swccgo.cards.set1240.light;

import static org.junit.Assert.assertNull;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_081_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("eject", "1240_81");
                    put("boshek", "1_4");
                }},
                new HashMap<>() {{
                    put("garrison", "13_87"); // [Reflections III] [Maintenance] character
                    put("trooper", "1_194");
                }},
                10, 20,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void EjectEjectVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("eject").getBlueprint();

        assertEquals(Title.Eject_Eject, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Control));
    }

    private void deployGarrisonAndEject(VirtualTableScenario scn) {
        var eject = scn.GetLSCard("eject");
        var garrison = scn.GetDSCard("garrison");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(eject);
        scn.MoveCardsToDSHand(garrison);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureForcePile(scn.DS, 10);
        scn.DSDeployCard(garrison);
        if (scn.DSHasCardChoiceAvailable(site)) {
            scn.DSChooseCard(site);
        }
        for (int i = 0; i < 8; i++) {
            if (scn.LSGetDecision() != null && scn.LSCardPlayAvailable(eject)) {
                break;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            }
            else {
                scn.LSPass();
            }
        }
        assertTrue(scn.LSCardPlayAvailable(eject));
        scn.LSPlayCard(eject);
        if (scn.LSHasCardChoiceAvailable(garrison)) {
            scn.LSChooseCard(garrison);
        }
    }

    @Test
    public void EjectEjectVDeploysOnJustDeployedReflections3MaintenanceCard() {
        var scn = GetScenario();
        var eject = scn.GetLSCard("eject");
        var garrison = scn.GetDSCard("garrison");

        deployGarrisonAndEject(scn);
        int dsLost = scn.GetDSLostPileCount();
        for (int i = 0; i < 10 && eject.getAttachedTo() == null; i++) {
            scn.PassAllResponses();
        }
        assertEquals(garrison, eject.getAttachedTo());
        // Opponent loses 1 Force (optional retrieve passed).
        for (int i = 0; i < 6; i++) {
            if (scn.AwaitingDSForceLossPayment()) {
                scn.DSPayForceLossFromReserveDeck();
            }
            else if (scn.LSDecisionAvailable("Retrieve 1 Force")) {
                scn.LSPass();
            }
            else {
                scn.PassAllResponses();
            }
        }
        assertTrue(scn.GetDSLostPileCount() >= dsLost + 1);

        var mq = scn.game().getModifiersQuerying();
        assertEquals(scn.GetDSCard("garrison").getBlueprint().getPower() - 3, scn.GetPower(garrison), scn.epsilon);
        assertFalse(mq.getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_APPLY_ABILITY_FOR_BATTLE_DESTINY, garrison).isEmpty());
    }

    @Test
    public void EjectEjectVDoesNotDeployOnNonMaintenanceCard() {
        var scn = GetScenario();
        var eject = scn.GetLSCard("eject");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(eject);
        scn.MoveCardsToDSHand(trooper);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureForcePile(scn.DS, 10);
        scn.DSDeployCard(trooper);
        if (scn.DSHasCardChoiceAvailable(site)) {
            scn.DSChooseCard(site);
        }
        assertFalse(scn.LSCardPlayAvailable(eject));
    }

    @Test
    public void EjectEjectVOpponentMayPlaceTargetInLostPileDuringDrawPhaseAndThisGoesToUsedPile() {
        var scn = GetScenario();
        var eject = scn.GetLSCard("eject");
        var garrison = scn.GetDSCard("garrison");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(site, garrison);
        scn.AttachCardsTo(garrison, eject);
        scn.SkipToPhase(Phase.DRAW);
        for (int i = 0; i < 6 && !scn.DSDecisionAvailable("Choose for"); i++) {
            scn.PassAllResponses();
        }
        assertTrue(scn.DSDecisionAvailable("Choose for"));
        scn.DSChooseOption("Place");
        scn.PassAllResponses();
        assertTrue(garrison.getZone() == Zone.LOST_PILE || garrison.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(eject.getZone() == Zone.USED_PILE || eject.getZone() == Zone.TOP_OF_USED_PILE);
    }

    @Test
    public void EjectEjectVOpponentMayLose1ForceDuringDrawPhaseToKeepTarget() {
        var scn = GetScenario();
        var eject = scn.GetLSCard("eject");
        var garrison = scn.GetDSCard("garrison");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(site, garrison);
        scn.AttachCardsTo(garrison, eject);
        scn.SkipToPhase(Phase.DRAW);
        for (int i = 0; i < 6 && !scn.DSDecisionAvailable("Choose for"); i++) {
            scn.PassAllResponses();
        }
        int dsLifeForce = scn.GetPlayerLifeForceRemaining(scn.DS);
        scn.DSChooseOption("Lose 1 Force");
        for (int i = 0; i < 6; i++) {
            if (scn.AwaitingDSForceLossPayment()) {
                scn.DSPayForceLossFromReserveDeck();
            }
            else {
                scn.PassAllResponses();
            }
        }
        assertEquals(Zone.AT_LOCATION, garrison.getZone());
        assertEquals(dsLifeForce - 1, scn.GetPlayerLifeForceRemaining(scn.DS));
        assertEquals(garrison, eject.getAttachedTo());
    }

    @Test
    public void EjectEjectVNoDrawPhaseChoiceDuringYourOwnDrawPhase() {
        var scn = GetScenario();
        var eject = scn.GetLSCard("eject");
        var garrison = scn.GetDSCard("garrison");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(site, garrison);
        scn.AttachCardsTo(garrison, eject);
        scn.SkipToPhase(Phase.DRAW);
        assertFalse(scn.DSDecisionAvailable("Choose for"));
        assertEquals(Zone.AT_LOCATION, garrison.getZone());
    }

    @Test
    public void EjectEjectVDeploysOnTableToReduceForceLossDuringOpponentsControlPhase() {
        var scn = GetScenario();
        var eject = scn.GetLSCard("eject");
        var trooper = scn.GetDSCard("trooper");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(eject);
        scn.MoveCardsToLocation(dsSite, trooper);
        scn.SkipToDSTurn(Phase.CONTROL);
        int lost = scn.GetLSLostPileCount();
        scn.DSForceDrainAt(dsSite);
        for (int i = 0; i < 12; i++) {
            if (scn.LSGetDecision() != null && !scn.AwaitingLSForceLossPayment() && scn.LSCardPlayAvailable(eject)) {
                break;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            }
            else {
                scn.LSPass();
            }
        }
        int drainAmount = scn.GetForceDrainTotal();
        assertTrue(scn.LSCardPlayAvailable(eject));
        scn.LSPlayCard(eject);
        for (int i = 0; i < 10; i++) {
            if (scn.AwaitingLSForceLossPayment()) {
                scn.LSPayForceLossFromReserveDeck();
            }
            else {
                scn.PassAllResponses();
            }
        }
        assertEquals(Zone.SIDE_OF_TABLE, eject.getZone());
        assertEquals(lost + Math.max(0, drainAmount - 2), scn.GetLSLostPileCount());
    }

    @Test
    public void EjectEjectVOpponentMayUse1ForceToApplyTargetsAbilityDuringBattle() {
        var scn = GetScenario();
        var eject = scn.GetLSCard("eject");
        var boshek = scn.GetLSCard("boshek");
        var garrison = scn.GetDSCard("garrison");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(site, garrison, boshek);
        scn.AttachCardsTo(garrison, eject);
        scn.SkipToPhase(Phase.BATTLE);
        scn.EnsureForcePile(scn.DS, 4);
        var mq = scn.game().getModifiersQuerying();
        assertFalse(mq.getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_APPLY_ABILITY_FOR_BATTLE_DESTINY, garrison).isEmpty());

        scn.LSInitiateBattle(site);
        boolean offered = false;
        for (int i = 0; i < 6 && !offered; i++) {
            if (scn.DSGetDecision() != null && scn.DSCardActionAvailable(eject, "Use 1 Force to apply")) {
                offered = true;
            }
            else if (scn.DSGetDecision() != null) {
                scn.DSPass();
            }
            else {
                scn.LSPass();
            }
        }
        assertTrue(offered);
        scn.DSUseCardAction(eject, "Use 1 Force to apply");
        scn.PassAllResponses();
        assertTrue(mq.getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_APPLY_ABILITY_FOR_BATTLE_DESTINY, garrison).isEmpty());
    }

    @Test
    public void EjectEjectHasTheLatestText() {
        var scn = GetScenario();
        var card = scn.GetLSCard("eject").getBlueprint();
        assertEquals("Deploy on opponent's just deployed [Reflections III][Maintenance] card; opponent loses 1 Force, and you may retrieve 1 Force and/or play a Defensive Shield from under your Starting Effect. That card is power -3, opponent must use 1 Force for that card to apply its ability towards drawing battle destiny, and, during opponent's draw phase, opponent must lose 1 Force or place that card in Lost Pile. If just lost, place this card in Used Pile. (Immune to Control.) OR If you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 2.",
                card.getGameText());
        assertNull(card.getLegacyVersion());
        // the undated PC card-data wording ("Target is power -3 and opponent must use 1 Force") is gone
        assertFalse(card.getGameText().contains("Target is power -3"));
    }
}
