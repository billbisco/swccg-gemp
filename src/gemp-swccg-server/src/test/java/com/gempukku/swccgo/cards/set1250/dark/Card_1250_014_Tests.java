package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.layout.LocationPlacement;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.PoliticsModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1250_014_Tests {
    private static final String RAISE = "Raise your converted Senate to the top";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("senate", "12_75", "obi", "1_21",
                "luke", "1_19", "alien", "1_7", "alter", "1_71")),
                new HashMap<>(Map.ofEntries(Map.entry("expand", "1250_14"), Map.entry("senate", "12_167"),
                        Map.entry("sidious", "1116_46"), Map.entry("trooper", "1_194"), Map.entry("mara", "110_10"),
                        Map.entry("maul", "11_54"), Map.entry("alien", "13_59"), Map.entry("senator", "12_110"),
                        Map.entry("independent", "4_168"), Map.entry("imperialShip", "1_301"),
                        Map.entry("system", "1_289"), Map.entry("ice", "3_148"))),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        for (String key : List.of("senate", "system", "ice")) scn.MoveLocationToTable(scn.GetDSCard(key));
        scn.MoveCardsToDSHand(scn.GetDSCard("expand"));
        return scn;
    }

    @Test public void ExpandTheEmpirePrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("expand").getBlueprint();
        assertEquals(Title.Expand_The_Empire, b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertEquals(3, b.getDestiny(), scn.epsilon);
        assertTrue(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        assertEquals("The Emperor disbanded the Imperial Senate \"for the duration of the emergency,\" seizing absolute power. He planned to extend rule by terrorizing planets into submission.", b.getLore());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.EPISODE_I, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void ExpandTheEmpireDeploysOnSenateAndCanRaiseOwnConvertedSenate() {
        var scn = scenario();
        var expand = scn.GetDSCard("expand");
        var dark = scn.GetDSCard("senate");
        var light = scn.GetLSCard("senate");
        scn.MoveLocationToTable(light, new LocationPlacement(dark.getPartOfSystem(), null, null, dark,
                LocationPlacementDirection.REPLACE));
        assertInZone(Zone.CONVERTED_LOCATIONS, dark);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(expand);
        if (scn.DSHasCardChoiceAvailable(light)) {
            assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSStartingLocation()));
            scn.DSChooseCard(light);
        }
        finishDeploy(scn);
        assertSame(light, expand.getAttachedTo());
        assertTrue(scn.DSCardActionAvailable(expand, RAISE));
        scn.DSUseCardAction(expand, RAISE);
        if (scn.DSHasCardChoiceAvailable(dark)) scn.DSChooseCard(dark);
        finishDeploy(scn);
        assertInZone(Zone.LOCATIONS, dark);
        assertInZone(Zone.CONVERTED_LOCATIONS, light);
        assertSame(dark, expand.getAttachedTo());
        assertFalse(scn.DSCardActionAvailable(expand, RAISE));
    }

    @Test public void ExpandTheEmpireGivesFourPoliticsOnlyToJediHere() {
        var scn = scenario();
        var expand = scn.GetDSCard("expand");
        var senate = scn.GetDSCard("senate");
        var obi = scn.GetLSCard("obi");
        var luke = scn.GetLSCard("luke");
        scn.MoveCardsToLocation(senate, obi, luke);
        var q = scn.game().getModifiersQuerying();
        var state = scn.gameState();
        float obiBase = q.getPolitics(state, obi);
        float lukeBase = q.getPolitics(state, luke);
        scn.AttachCardsTo(senate, expand);
        assertEquals(obiBase + 4, q.getPolitics(state, obi), scn.epsilon);
        assertEquals(lukeBase, q.getPolitics(state, luke), scn.epsilon);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), obi);
        assertEquals(obiBase, q.getPolitics(state, obi), scn.epsilon);
    }

    @Test public void ExpandTheEmpireActuallyLosesOwnProhibitedCardsAndKeepsMaraAndOtherOwnersCards() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var senate = scn.GetDSCard("senate");
        var system = scn.GetDSCard("system");
        var expand = scn.GetDSCard("expand");
        scn.MoveCardsToLocation(site, scn.GetDSCard("mara"), scn.GetDSCard("alien"), scn.GetDSCard("maul"),
                scn.GetDSCard("trooper"), scn.GetLSCard("alien"));
        scn.MoveCardsToLocation(senate, scn.GetDSCard("senator"));
        scn.MoveCardsToLocation(system, scn.GetDSCard("independent"), scn.GetDSCard("imperialShip"));
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(expand);
        if (scn.DSHasCardChoiceAvailable(senate)) scn.DSChooseCard(senate);
        finishDeploy(scn);
        for (String key : List.of("maul", "alien", "senator", "independent")) {
            assertInZone(Zone.LOST_PILE, scn.GetDSCard(key));
        }
        for (String key : List.of("mara", "trooper", "imperialShip")) {
            assertInZone(Zone.AT_LOCATION, scn.GetDSCard(key));
        }
        assertInZone(Zone.AT_LOCATION, scn.GetLSCard("alien"));
    }

    @Test public void ExpandTheEmpireCannotBeCanceledByAlter() {
        var scn = scenario();
        var expand = scn.GetDSCard("expand");
        var alter = scn.GetLSCard("alter");
        scn.AttachCardsTo(scn.GetDSCard("senate"), expand);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("obi"));
        scn.MoveCardsToLSHand(alter);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(alter));
        assertInZone(Zone.ATTACHED, expand);
    }

    private void setPolitics(VirtualTableScenario scn, int politics) {
        var senate = scn.GetDSCard("senate");
        var sidious = scn.GetDSCard("sidious");
        scn.MoveCardsToLocation(senate, sidious);
        scn.AttachCardsTo(senate, scn.GetDSCard("expand"));
        var query = scn.game().getModifiersQuerying();
        float before = query.getTotalPoliticsAtGalacticSenate(scn.gameState(), scn.DS);
        // Seed exact boundaries without adding a senator the Effect would be required to lose.
        scn.ApplyAdHocModifier(new PoliticsModifier(sidious, sidious, politics - before));
        assertEquals(politics, query.getTotalPoliticsAtGalacticSenate(scn.gameState(), scn.DS), scn.epsilon);
    }

    @Test public void ExpandTheEmpireBoostsActualDrainOnlyAboveFiveWithOwnImperial() {
        checkDrain(5, true, 1);
        checkDrain(6, false, 1);
        checkDrain(6, true, 2);
    }

    private void checkDrain(int politics, boolean withImperial, int expectedLoss) {
        var scn = scenario();
        setPolitics(scn, politics);
        var site = scn.GetDSStartingLocation();
        var trooper = scn.GetDSCard("trooper");
        scn.MoveCardsToLocation(site, scn.GetDSCard("mara"));
        scn.MoveCardsToLocation(withImperial ? site : scn.GetDSCard("ice"), trooper);
        var q = scn.game().getModifiersQuerying();
        assertEquals(0, q.getForceDrainAmount(scn.gameState(), scn.GetDSCard("ice"), scn.DS), scn.epsilon);
        scn.SkipToPhase(Phase.CONTROL);
        int before = scn.GetLSLostPileCount();
        scn.DSForceDrainAt(site);
        for (int i = 0; i < 45 && !scn.AwaitingDSControlPhaseActions(); i++) {
            if (scn.AwaitingLSForceLossPayment()) scn.LSPayRemainingForceLossFromReserveDeck();
            else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected drain decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertEquals(expectedLoss, scn.GetLSLostPileCount() - before);
    }

    @Test public void ExpandTheEmpireBoostsEachActualBattleDestinyOnlyAboveFiveWithOwnImperial() {
        checkDestiny(5, true, 5);
        checkDestiny(6, false, 5);
        checkDestiny(6, true, 7);
    }

    private void checkDestiny(int politics, boolean withImperial, int expectedTotal) {
        var scn = scenario();
        setPolitics(scn, politics);
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("mara"), scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(withImperial ? site : scn.GetDSCard("ice"), scn.GetDSCard("trooper"));
        scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(site, 1, scn.DS));
        scn.EnsureDSForcePile(1);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(3), scn.GetDSDestiny(2));
        scn.PrepareLSDestiny(3);
        scn.SkipToPowerSegment();
        assertTrue(scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();
        scn.PassDestinyDrawResponses();
        scn.PassDestinyDrawResponses();
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        assertTrue(scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassDestinyDrawResponses();
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");
        assertEquals(expectedTotal, scn.GetDSTotalDestiny());
        assertEquals(3, scn.GetLSTotalDestiny());
    }

    private void finishDeploy(VirtualTableScenario scn) {
        for (int i = 0; i < 60 && !scn.AwaitingDSDeployPhaseActions(); i++) {
            boolean selected = false;
            for (String key : List.of("maul", "alien", "senator", "independent", "senate")) {
                var card = scn.GetDSCard(key);
                if (scn.DSHasCardChoiceAvailable(card)) {
                    scn.DSChooseCard(card);
                    selected = true;
                    break;
                }
            }
            if (selected) continue;
            if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected deployment decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions());
    }
}
