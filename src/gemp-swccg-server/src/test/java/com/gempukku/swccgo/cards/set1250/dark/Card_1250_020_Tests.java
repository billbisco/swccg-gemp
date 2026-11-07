package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1250_020_Tests {
    private static final String RELOCATE = "Relocate Order 66 to an Episode I Jedi";
    private static final String RETRIEVE = "Retrieve 1 Force with Order 66";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("quiGon", "11_10", "mace", "12_13",
                "kiAdi", "12_10", "oldObi", "1_21", "padawan", "11_6")),
                new HashMap<>(Map.of("order", "1250_20", "sidious", "1116_46", "senate", "12_167", "ice", "3_148",
                        "expand", "1250_14", "ovsExpand", "1116_44", "vader", "1_168", "saber", "1_324",
                        "sniper", "2_139", "loss", "1_194")), 28, 28, StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation, StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("senate"));
        scn.MoveLocationToTable(scn.GetDSCard("ice"));
        scn.MoveCardsToDSHand(scn.GetDSCard("order"));
        return scn;
    }

    @Test public void Order66PrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("order").getBlueprint();
        assertEquals("Order 66", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(7, b.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertFalse(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        assertEquals("Hologram.", b.getLore());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.EPISODE_I, Icon.CORUSCANT,
                Icon.SIDIOUS, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.HOLOGRAM)));
    }

    @Test public void Order66ActuallyDeploysOnlyOnSidious() {
        var scn = scenario();
        var order = scn.GetDSCard("order");
        var sidious = scn.GetDSCard("sidious");
        var vader = scn.GetDSCard("vader");
        scn.MoveCardsToLocation(scn.GetDSCard("senate"), sidious);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(order);
        if (scn.DSHasCardChoiceAvailable(sidious)) {
            assertFalse(scn.DSHasCardChoiceAvailable(vader));
            scn.DSChooseCard(sidious);
        }
        scn.PassAllResponses();
        assertSame(sidious, order.getAttachedTo());
    }

    @Test public void Order66RelocatesWithBlockFiveExpandToEitherEligibleLocation() {
        for (String choice : List.of("quiGon", "mace")) {
            var scn = scenario();
            var order = scn.GetDSCard("order");
            var sidious = scn.GetDSCard("sidious");
            var senate = scn.GetDSCard("senate");
            var site = scn.GetDSStartingLocation();
            var quiGon = scn.GetLSCard("quiGon");
            var mace = scn.GetLSCard("mace");
            scn.MoveCardsToLocation(senate, sidious, mace);
            scn.AttachCardsTo(senate, scn.GetDSCard("expand"));
            scn.AttachCardsTo(sidious, order);
            scn.MoveCardsToLocation(site, quiGon, scn.GetLSCard("oldObi"));
            scn.MoveCardsToLocation(scn.GetDSCard("ice"), scn.GetLSCard("kiAdi"));
            scn.SkipToPhase(Phase.CONTROL);
            assertTrue(scn.DSCardActionAvailable(order, RELOCATE));
            assertFalse(scn.DSCardActionAvailable(order, RETRIEVE));
            scn.DSUseCardAction(order, RELOCATE);
            assertTrue(scn.DSHasCardChoiceAvailable(quiGon));
            assertTrue(scn.DSHasCardChoiceAvailable(mace));
            assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("oldObi")));
            assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("kiAdi")));
            scn.DSChooseCard(scn.GetLSCard(choice));
            scn.PassAllResponses();
            if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            assertSame(scn.GetLSCard(choice), order.getAttachedTo());
            assertFalse(scn.DSCardActionAvailable(order, RELOCATE));
            assertFalse(scn.DSCardActionAvailable(order, RETRIEVE));
        }
    }

    @Test public void Order66OtherwiseActuallyRetrievesOneForWrongPrintingWrongLocationOrNoEligibleJedi() {
        for (int reason = 0; reason < 3; reason++) {
            var scn = scenario();
            var order = scn.GetDSCard("order");
            var sidious = scn.GetDSCard("sidious");
            var senate = scn.GetDSCard("senate");
            var lost = scn.GetDSCard("loss");
            scn.MoveCardsToLocation(reason == 1 ? scn.GetLSStartingLocation() : senate, sidious);
            scn.AttachCardsTo(senate, scn.GetDSCard(reason == 0 ? "ovsExpand" : "expand"));
            scn.AttachCardsTo(sidious, order);
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard(reason == 2 ? "padawan" : "quiGon"));
            scn.MoveCardsToTopOfDSLostPile(lost);
            scn.SkipToPhase(Phase.CONTROL);
            assertFalse(scn.DSCardActionAvailable(order, RELOCATE));
            assertTrue(scn.DSCardActionAvailable(order, RETRIEVE));
            scn.DSUseCardAction(order, RETRIEVE);
            scn.PassAllResponses();
            assertInZone(Zone.USED_PILE, lost);
            assertSame(sidious, order.getAttachedTo());
        }
    }

    @Test public void Order66OnJediAddsOneActualBattleDestinyHereAndDoesNotCauseAnEndOfTurnLoss() {
        for (boolean onJedi : List.of(false, true)) {
            var scn = scenario();
            var order = scn.GetDSCard("order");
            var sidious = scn.GetDSCard("sidious");
            var quiGon = scn.GetLSCard("quiGon");
            var site = scn.GetDSStartingLocation();
            scn.MoveCardsToLocation(scn.GetDSCard("senate"), sidious);
            scn.MoveCardsToLocation(site, scn.GetDSCard("vader"), quiGon);
            scn.EnsureDSForcePile(1);
            scn.SkipToPhase(Phase.BATTLE);
            scn.AttachCardsTo(onJedi ? quiGon : sidious, order);
            scn.DSInitiateBattle(site);
            scn.PassAllResponses();
            scn.PrepareDSDestiny(2);
            scn.PrepareDSDestiny(3);
            scn.PrepareLSDestiny(3);
            scn.SkipToPowerSegment();
            assertTrue(scn.DSDecisionAvailable("battle destiny?"));
            scn.DSChooseYes();
            scn.PassDestinyDrawResponses();
            if (onJedi) scn.PassDestinyDrawResponses();
            scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
            assertTrue(scn.LSDecisionAvailable("battle destiny?"));
            scn.LSChooseYes();
            scn.PassDestinyDrawResponses();
            scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
            scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");
            assertEquals(onJedi ? 7 : 4, scn.GetDSTotalDestiny());
            assertEquals(3, scn.GetLSTotalDestiny());
        }
        var scn = scenario();
        var quiGon = scn.GetLSCard("quiGon");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), quiGon);
        scn.SkipToPhase(Phase.DRAW);
        scn.AttachCardsTo(quiGon, scn.GetDSCard("order"));
        scn.SkipToLSTurn(Phase.ACTIVATE);
        assertInZone(Zone.AT_LOCATION, quiGon);
    }

    @Test public void Order66ReturnsToSidiousWhenAttachedJediIsActuallyLost() {
        for (boolean sidiousOnTable : List.of(false, true)) {
            var scn = scenario();
            var order = scn.GetDSCard("order");
            var sidious = scn.GetDSCard("sidious");
            var vader = scn.GetDSCard("vader");
            var quiGon = scn.GetLSCard("quiGon");
            var saber = scn.GetDSCard("saber");
            var sniper = scn.GetDSCard("sniper");
            if (sidiousOnTable) scn.MoveCardsToLocation(scn.GetDSCard("senate"), sidious);
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader, quiGon);
            scn.AttachCardsTo(vader, saber);
            scn.AttachCardsTo(quiGon, order);
            scn.MoveCardsToDSHand(sniper);
            scn.SkipToPhase(Phase.CONTROL);
            scn.PrepareDSDestiny(5);
            scn.PrepareDSDestiny(6);
            scn.DSPlayCard(sniper);
            for (int i = 0; i < 90 && !scn.AwaitingDSControlPhaseActions(); i++) {
                if (scn.DSHasCardChoiceAvailable(saber)) scn.DSChooseCard(saber);
                else if (scn.DSHasCardChoiceAvailable(quiGon)) scn.DSChooseCard(quiGon);
                else if (scn.DSHasCardChoiceAvailable(order)) scn.DSChooseCard(order);
                else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected Sniper or relocation decision: " + scn.GetCurrentDecision().getText());
            }
            assertTrue(scn.AwaitingDSControlPhaseActions());
            assertInZone(Zone.LOST_PILE, quiGon);
            if (sidiousOnTable) assertSame(sidious, order.getAttachedTo());
            else assertInZone(Zone.LOST_PILE, order);
        }
    }
}
