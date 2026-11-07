package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1262_007_Tests {
    private static final String LOSS = "Make opponent lose 1 Force";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("obi", "1_21", "saber", "1_157", "control", "4_48",
                "disarmed", "1_48", "clash", "5_38", "sorry", "2_57")),
                new HashMap<>(Map.of("power", "1262_7", "vader", "1_168", "maul", "11_54",
                        "anger", "1110_13", "saber", "1_324", "trooper", "1_194", "ice", "3_148")),
                26, 26, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveCardsToDSHand(scn.GetDSCard("power"));
        return scn;
    }

    @Test public void PowerOfTheSithPrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("power").getBlueprint();
        assertEquals("Power Of The Sith", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(6, b.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.IMMEDIATE, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.isLegacy());
        assertFalse(b.hasVirtualSuffix());
        assertEquals("Many have experienced the awesome power of the Dark Lord in battle. Few have lived to tell about it.", b.getLore());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void PowerOfTheSithDeploysOnlyOnTheActualEpicDuelWinner() {
        duel(true, true);
    }

    @Test public void PowerOfTheSithCannotDeployAfterOpponentWinsEpicDuel() {
        duel(true, false);
    }

    @Test public void PowerOfTheSithCannotDeployAfterMaulWinsOrdinaryDuel() {
        duel(false, true);
    }

    private void duel(boolean epic, boolean darkWins) {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var fighter = scn.GetDSCard(epic ? "vader" : "maul");
        var power = scn.GetDSCard("power");
        var anger = scn.GetDSCard("anger");
        var obi = scn.GetLSCard("obi");
        scn.MoveCardsToLocation(site, fighter, obi, scn.GetDSCard("trooper"));
        scn.MoveCardsToDSHand(anger);
        scn.EnsureDSForcePile(2);
        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(power));
        scn.PrepareDSDestiny(darkWins ? 5 : 0);
        scn.PrepareDSDestiny(darkWins ? 6 : 1);
        scn.PrepareLSDestiny(darkWins ? 0 : 5);
        scn.PrepareLSDestiny(darkWins ? 1 : 6);
        if (epic) scn.DSPlayCard(anger, "Initiate epic duel");
        else scn.DSUseCardAction(fighter, "Duel a Jedi");
        boolean offered = false;
        for (int i = 0; i < 120 && !scn.AwaitingDSControlPhaseActions(); i++) {
            if (scn.AwaitingLSForceLossPayment()) scn.LSPayRemainingForceLossFromReserveDeck();
            else if (scn.AwaitingDSForceLossPayment()) {
                if (epic) scn.DSPayRemainingForceLossFromReserveDeck(); else scn.DSPayForceLossFromForcePile();
            } else if (scn.DSGetDecision() instanceof CardActionSelectionDecision && scn.DSCardPlayAvailable(power)) {
                assertTrue(epic && darkWins);
                offered = true;
                scn.DSPlayCard(power);
            } else if (scn.DSHasCardChoiceAvailable(obi)) scn.DSChooseCard(obi);
            else if (scn.DSHasCardChoiceAvailable(fighter)) {
                assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("trooper")));
                scn.DSChooseCard(fighter);
            } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected duel decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertEquals(epic && darkWins, offered);
        if (offered) assertSame(fighter, power.getAttachedTo());
        else assertInZone(Zone.HAND, power);
    }

    private VirtualTableScenario armed(boolean protectedVader) {
        var scn = scenario();
        var vader = scn.GetDSCard("vader");
        var obi = scn.GetLSCard("obi");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader, obi);
        scn.AttachCardsTo(vader, scn.GetDSCard("saber"));
        scn.AttachCardsTo(obi, scn.GetLSCard("saber"));
        scn.SkipToLSTurn(Phase.ACTIVATE);
        if (protectedVader) scn.AttachCardsTo(vader, scn.GetDSCard("power"));
        scn.MoveCardsToLSHand(scn.GetLSCard("clash"), scn.GetLSCard("sorry"), scn.GetLSCard("disarmed"),
                scn.GetLSCard("control"));
        return scn;
    }

    @Test public void PowerOfTheSithProtectsAgainstActualDisarmedAndSorryAboutTheMess() {
        for (boolean protection : List.of(false, true)) {
            var scn = armed(protection);
            scn.SkipToPhase(Phase.CONTROL);
            assertEquals(!protection, scn.LSCardPlayAvailable(scn.GetLSCard("disarmed")));
            assertEquals(!protection, scn.LSCardPlayAvailable(scn.GetLSCard("sorry"), "Fire a weapon"));
        }
    }

    @Test public void PowerOfTheSithRemovesBearerFromActualClashTargetsAndIsImmuneToControl() {
        var scn = armed(true);
        var vader = scn.GetDSCard("vader");
        var trooper = scn.GetDSCard("trooper");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), trooper);
        scn.SkipToPhase(Phase.CONTROL);
        scn.EnsureLSForcePile(2);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("control")));
        var clash = scn.GetLSCard("clash");
        assertTrue(scn.LSCardPlayAvailable(clash, "Prevent character from moving or battling"));
        scn.LSPlayCard(clash, "Prevent character from moving or battling");
        if (scn.LSHasCardChoiceAvailable(trooper)) {
            assertFalse(scn.LSHasCardChoiceAvailable(vader));
            scn.LSChooseCard(trooper);
        }
        scn.PassAllResponses();
        assertInZone(Zone.ATTACHED, scn.GetDSCard("power"));
    }

    @Test public void PowerOfTheSithCausesMandatoryLossOnlyInOwnControlAtBattleground() {
        for (boolean battleground : List.of(false, true)) {
            var scn = scenario();
            var vader = scn.GetDSCard("vader");
            var power = scn.GetDSCard("power");
            var site = battleground ? scn.GetDSStartingLocation() : scn.GetDSCard("ice");
            if (!battleground) scn.MoveLocationToTable(site);
            scn.MoveCardsToLocation(site, vader);
            scn.AttachCardsTo(vader, power);
            scn.SkipToPhase(Phase.CONTROL);
            assertEquals(battleground, scn.DSCardActionAvailable(power, LOSS));
            int before = scn.GetLSLostPileCount();
            scn.DSPass();
            if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            for (int i = 0; i < 40 && !scn.AwaitingDSDeployPhaseActions(); i++) {
                if (scn.AwaitingLSForceLossPayment()) scn.LSPayRemainingForceLossFromReserveDeck();
                else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected decision after Control: " + scn.GetCurrentDecision().getText());
            }
            assertTrue(scn.AwaitingDSDeployPhaseActions());
            assertEquals(battleground ? 1 : 0, scn.GetLSLostPileCount() - before);
        }
    }

    @Test public void PowerOfTheSithGoesToUsedWhenBearerIsActuallyForfeited() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var vader = scn.GetDSCard("vader");
        var power = scn.GetDSCard("power");
        scn.MoveCardsToLocation(site, vader, scn.GetLSCard("obi"));
        scn.SkipToPhase(Phase.BATTLE);
        scn.AttachCardsTo(vader, power);
        scn.EnsureDSForcePile(1);
        scn.DSInitiateBattle(site);
        scn.PassBattleStartResponses();
        scn.PrepareLSDestiny(6);
        scn.SkipToPowerSegment();
        boolean lightDrew = false;
        for (int i = 0; i < 55 && !scn.AwaitingDSAttritionPayment(); i++) {
            if (scn.DSDecisionAvailable("battle destiny?")) scn.DSChooseNo();
            else if (scn.LSDecisionAvailable("battle destiny?")) {
                scn.LSChooseYes();
                scn.PassDestinyDrawResponses();
                lightDrew = true;
            } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected battle decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(lightDrew);
        assertEquals(6, scn.GetUnpaidDSAttrition());
        scn.DSPayAttritionFromCardInPlay(vader);
        for (int i = 0; i < 40 && (GameUtils.getZoneFromZoneTop(vader.getZone()) != Zone.LOST_PILE
                || GameUtils.getZoneFromZoneTop(power.getZone()) != Zone.USED_PILE); i++) {
            if (scn.DSHasCardChoiceAvailable(vader)) scn.DSChooseCard(vader);
            else if (scn.DSHasCardChoiceAvailable(power)) scn.DSChooseCard(power);
            else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected forfeiture decision: " + scn.GetCurrentDecision().getText());
        }
        assertInZone(Zone.LOST_PILE, vader);
        assertInZone(Zone.USED_PILE, power);
    }
}
