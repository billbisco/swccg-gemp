package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_029_Tests {
    StartingSetup UprisingOutsideDeck = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("uprising", "1291_29");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("defense", "1291_27");
                    put("vader", "1_168");
                    put("tie", "1_304");
                    put("eweb", "3_159");
                    put("dooku", "200_76");
                    put("keder", "12_109");
                    put("junk", "12_181");
                    put("lost1", "12_182");
                    put("lost2", "12_183");
                    put("lost3", "3_156");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                UprisingOutsideDeck,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void SeparatistUprisingStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("uprising").getBlueprint();

        assertEquals("Separatist Uprising", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.OBJECTIVE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AtWarWithItselfStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        scn.StartGame();
        var card = scn.GetDSCard("uprising").getOtherSideBlueprint();

        assertEquals("At War With Itself", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(7, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.OBJECTIVE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private static final String WAR_HAS_BEGUN_ID = "2000_9102";

    private static class TestWarHasBegun extends AbstractNormalEffect {
        TestWarHasBegun() {
            super(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "War Has Begun", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R);
        }
    }

    @After
    public void removeWarHasBegunStub() {
        unregisterWarHasBegun();
    }

    @SuppressWarnings("unchecked")
    private void registerWarHasBegun() {
        try {
            Field field = SwccgCardBlueprintLibrary.class.getDeclaredField("_blueprintMap");
            field.setAccessible(true);
            Map<String, SwccgCardBlueprint> map = (Map<String, SwccgCardBlueprint>) field.get(VirtualTableScenario._cardLibrary);
            map.put(WAR_HAS_BEGUN_ID, new TestWarHasBegun());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void unregisterWarHasBegun() {
        try {
            Field field = SwccgCardBlueprintLibrary.class.getDeclaredField("_blueprintMap");
            field.setAccessible(true);
            Map<String, SwccgCardBlueprint> map = (Map<String, SwccgCardBlueprint>) field.get(VirtualTableScenario._cardLibrary);
            map.remove(WAR_HAS_BEGUN_ID);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void PutUprisingInPlay(VirtualTableScenario scn) {
        var uprising = scn.GetDSCard("uprising");
        scn.MoveCardsToDSSideOfTable(uprising);
        uprising.setObjectiveDeploymentComplete(true);
        scn.gameState().reapplyAffectingForCard(scn.game(), uprising);
        scn.PassAllResponses();
    }

    private void PutCompleteSeparatistMissionOnTable(VirtualTableScenario scn) {
        registerWarHasBegun();
        PhysicalCardImpl war = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.DS, VirtualTableScenario._cardLibrary, WAR_HAS_BEGUN_ID);
        scn.gameState().addCardToZone(war, Zone.SIDE_OF_TABLE, scn.DS, true, false, false);
        var defense = scn.GetDSCard("defense");
        var junk = scn.GetDSCard("junk");
        scn.AttachCardsTo(war, defense);
        scn.StackCardsOn(defense, junk);
    }

    private void AdvanceTableChanged(VirtualTableScenario scn) {
        if (scn.gameState().getCurrentPhase() == Phase.DEPLOY) {
            scn.SkipToPhase(Phase.BATTLE);
        } else {
            scn.SkipToPhase(Phase.DEPLOY);
        }
    }

    private boolean dsActionContains(VirtualTableScenario scn, String text) {
        if (scn.GetAwaitingDecision(scn.DS) == null) {
            return false;
        }
        var actions = scn.GetDSAvailableActions();
        return actions != null && actions.toString().contains(text);
    }

    private void takeFirstAvailableAction(VirtualTableScenario scn) {
        scn.PlayerDecided(scn.GetDecidingPlayer(), "0");
    }

    private void TakeRemainderLost(VirtualTableScenario scn, PhysicalCardImpl vader) {
        for (int i = 0; i < 20; i++) {
            if (vader.getZone() == Zone.LOST_PILE || vader.getZone() == Zone.TOP_OF_LOST_PILE) {
                return;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                return;
            }
            String lower = decision.getText().toLowerCase();
            var actions = scn.GetAvailableActions(scn.GetDecidingPlayer());
            if (scn.DSHasCardChoiceAvailable(vader) || scn.LSHasCardChoiceAvailable(vader)) {
                if (scn.GetDecidingPlayer().equals(scn.DS)) {
                    scn.DSChooseCard(vader);
                } else {
                    scn.LSChooseCard(vader);
                }
                continue;
            }
            if (actions.toString().contains("Make Galen") || actions.toString().contains("Imperial starships")) {
                takeFirstAvailableAction(scn);
                continue;
            }
            if (lower.contains("required")) {
                takeFirstAvailableAction(scn);
                continue;
            }
            if (lower.contains("optional")) {
                scn.PassResponses("optional");
            } else if (lower.contains("action")) {
                scn.PassResponses("action");
            } else {
                takeFirstAvailableAction(scn);
            }
        }
    }

    private boolean retrieveOffered(VirtualTableScenario scn) {
        var decision = scn.GetCurrentDecision();
        if (decision != null) {
            String lower = decision.getText().toLowerCase();
            if (lower.contains("retrieve") || lower.contains("choose amount")) {
                return true;
            }
        }
        if (scn.DSDecisionAvailable("Retrieve up to 3 Force")
                || scn.DSDecisionAvailable("Choose amount of Force to retrieve")
                || scn.DSDecisionAvailable("Retrieve")
                || dsActionContains(scn, "Retrieve")) {
            return true;
        }
        if (scn.GetDecidingPlayer() != null) {
            var actions = scn.GetAvailableActions(scn.GetDecidingPlayer());
            return actions != null && actions.toString().contains("Retrieve");
        }
        return false;
    }

    private void FlipUprisingCatchingRetrieve(VirtualTableScenario scn) {
        PutCompleteSeparatistMissionOnTable(scn);
        for (int i = 0; i < 30; i++) {
            if (retrieveOffered(scn)) {
                return;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                return;
            }
            String lower = decision.getText().toLowerCase();
            var actions = scn.GetAvailableActions(scn.GetDecidingPlayer());
            if (lower.contains("required") || actions.toString().contains("Flip")) {
                takeFirstAvailableAction(scn);
                continue;
            }
            if (lower.contains("optional")) {
                scn.PassResponses("optional");
            } else if (lower.contains("action")) {
                scn.PassResponses("action");
            } else {
                takeFirstAvailableAction(scn);
            }
        }
    }

    private void WaitUntilAwaitingDSDeploy(VirtualTableScenario scn) {
        for (int i = 0; i < 10; i++) {
            if (scn.AwaitingDSDeployPhaseActions()) {
                return;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                return;
            }
            String lower = decision.getText().toLowerCase();
            if (lower.contains("required") || lower.contains("on which side")) {
                takeFirstAvailableAction(scn);
            } else if (lower.contains("optional")) {
                scn.PassResponses("optional");
            } else if (lower.contains("action") && scn.GetDecidingPlayer().equals(scn.LS)) {
                scn.LSPass();
            } else {
                return;
            }
        }
    }

    private void TakeRequiredLoseForce(VirtualTableScenario scn) {
        for (int i = 0; i < 15; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                return;
            }
            String lower = decision.getText().toLowerCase();
            var actions = scn.GetAvailableActions(scn.GetDecidingPlayer());
            if (lower.contains("lose") && (lower.contains("force") || actions.toString().contains("lose"))) {
                takeFirstAvailableAction(scn);
                scn.PassAllResponses();
                return;
            }
            if (actions.toString().contains("Make opponent lose")) {
                takeFirstAvailableAction(scn);
                scn.PassAllResponses();
                return;
            }
            if (lower.contains("required")) {
                takeFirstAvailableAction(scn);
                continue;
            }
            if (lower.contains("optional")) {
                scn.PassResponses("optional");
            } else if (lower.contains("action") || lower.contains("attrition") || lower.contains("damage")) {
                return;
            } else {
                return;
            }
        }
    }

    @Test
    public void SeparatistUprisingMakesGalenImperialsAndImperialStarshipsLost() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        PutUprisingInPlay(scn);
        scn.MoveCardsToLocation(site, vader);
        scn.gameState().reapplyAffectingForCard(scn.game(), vader);
        TakeRemainderLost(scn, vader);

        assertInZone(Zone.LOST_PILE, vader);
    }

    @Test
    public void SeparatistUprisingRepublicCharactersGainSeparatist() {
        var scn = GetScenario();
        var uprising = scn.GetDSCard("uprising");
        var keder = scn.GetDSCard("keder");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        PutUprisingInPlay(scn);
        scn.MoveCardsToLocation(site, keder);

        assertEquals(1, scn.GetIconCount(keder, Icon.SEPARATIST));
    }

    @Test
    public void SeparatistUprisingNonLightsaberWeaponsHaveForfeit3() {
        var scn = GetScenario();
        var eweb = scn.GetDSCard("eweb");
        var dooku = scn.GetDSCard("dooku");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dooku, eweb);
        PutUprisingInPlay(scn);

        assertEquals(3, scn.GetForfeit(eweb));
    }

    @Test
    public void SeparatistUprisingDeploysEpisodeIBattlegroundFromReserveDeckOncePerTurn() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("geonosis", "1291_15");
                    put("muunilinst", "1291_20");
                    put("defense", "1291_27");
                    put("junk", "12_181");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                UprisingOutsideDeck,
                VirtualTableScenario.Open
        );
        var uprising = scn.GetDSCard("uprising");
        var geonosis = scn.GetDSCard("geonosis");
        var muunilinst = scn.GetDSCard("muunilinst");

        scn.StartGame();
        PutUprisingInPlay(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(geonosis);
        scn.EnsureDSForcePile(2);

        assertTrue(scn.DSCardActionAvailable(uprising, "Episode I"));
        scn.DSUseCardAction(uprising, "Episode I");
        scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoiceAvailable(geonosis));
        scn.DSChooseCard(geonosis);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, geonosis.getZone());
        WaitUntilAwaitingDSDeploy(scn);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(uprising, "Episode I"));

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(muunilinst);
        scn.EnsureDSForcePile(2);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSCardActionAvailable(uprising, "Episode I"));
        scn.DSUseCardAction(uprising, "Episode I");
        scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoiceAvailable(muunilinst));
        scn.DSChooseCard(muunilinst);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, muunilinst.getZone());
    }

    @Test
    public void SeparatistUprisingFlipsWhenACompleteSeparatistMissionIsOnTable() {
        var scn = GetScenario();
        var uprising = scn.GetDSCard("uprising");

        scn.StartGame();
        PutUprisingInPlay(scn);
        PutCompleteSeparatistMissionOnTable(scn);
        AdvanceTableChanged(scn);

        assertEquals("At War With Itself", uprising.getBlueprint().getTitle());
    }

    @Test
    public void AtWarWithItselfRetrievesUpTo3ForceIntoHandOncePerGame() {
        var scn = GetScenario();
        var uprising = scn.GetDSCard("uprising");
        var lost1 = scn.GetDSCard("lost1");
        var lost2 = scn.GetDSCard("lost2");
        var lost3 = scn.GetDSCard("lost3");

        scn.StartGame();
        scn.MoveCardsToTopOfDSLostPile(lost1, lost2, lost3);
        PutUprisingInPlay(scn);
        FlipUprisingCatchingRetrieve(scn);
        assertEquals("At War With Itself", uprising.getBlueprint().getTitle());

        assertTrue(dsActionContains(scn, "Retrieve") || scn.DSDecisionAvailable("Retrieve"));
        scn.DSChooseAction("Retrieve");
        assertTrue(scn.DSDecisionAvailable("Choose amount of Force to retrieve"));
        scn.DSDecided(3);
        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable("Choose where to retrieve"));
        assertTrue(scn.DSChoiceAvailable("Hand"));
        scn.DSChoose("Hand");
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose where to retrieve"));
        scn.DSChoose("Hand");
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose where to retrieve"));
        scn.DSChoose("Hand");
        scn.PassAllResponses();

        assertInHand(lost1);
        assertInHand(lost2);
        assertInHand(lost3);
    }

    @Test
    public void AtWarWithItselfOpponentBattleDestinyMinus1AndTotalPowerPlusCompleteMissions() {
        var scn = GetScenario();
        var uprising = scn.GetDSCard("uprising");
        var dooku = scn.GetDSCard("dooku");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        PutUprisingInPlay(scn);
        PutCompleteSeparatistMissionOnTable(scn);
        AdvanceTableChanged(scn);
        assertEquals("At War With Itself", uprising.getBlueprint().getTitle());

        scn.MoveCardsToLocation(site, dooku, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PrepareLSDestiny(4);
        scn.PrepareDSDestiny(0);
        scn.SkipToEndOfPowerSegment(true);

        assertEquals(scn.GetPower(dooku) + 1, scn.GetDSTotalPower());
        scn.SkipToDamageSegment(true);
        assertEquals(3, scn.GetUnpaidDSAttrition());
    }

    @Test
    public void AtWarWithItselfOpponentLosesForceEqualToCompleteMissionsWhenYouWinABattle() {
        var scn = GetScenario();
        var uprising = scn.GetDSCard("uprising");
        var dooku = scn.GetDSCard("dooku");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        PutUprisingInPlay(scn);
        PutCompleteSeparatistMissionOnTable(scn);
        AdvanceTableChanged(scn);
        assertEquals("At War With Itself", uprising.getBlueprint().getTitle());

        scn.MoveCardsToLocation(site, dooku, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PrepareDSDestiny(5);
        scn.PrepareLSDestiny(0);
        scn.SkipToEndOfPowerSegment(true);
        assertTrue(scn.GetDSTotalPower() > scn.GetLSTotalPower());
        int lsLife = scn.GetLSLifeForceRemaining();
        for (int i = 0; i < 20; i++) {
            if (scn.GetLSLifeForceRemaining() < lsLife) {
                break;
            }
            if (scn.AwaitingLSForceLossPayment()) {
                scn.LSPayForceLossFromForcePile();
                break;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                break;
            }
            String lower = decision.getText().toLowerCase();
            if (lower.contains("optional")) {
                scn.PassResponses("optional");
                continue;
            }
            var actions = scn.GetAvailableActions(scn.GetDecidingPlayer());
            if (lower.contains("make opponent lose") || actions.toString().contains("Make opponent lose")) {
                takeFirstAvailableAction(scn);
                continue;
            }
            if (lower.contains("required")) {
                takeFirstAvailableAction(scn);
                continue;
            }
            break;
        }

        assertEquals(lsLife - 1, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void AtWarWithItselfTakesSeparatistCardFromReserveDuringControlPhase() {
        var scn = GetScenario();
        var uprising = scn.GetDSCard("uprising");
        var dooku = scn.GetDSCard("dooku");

        scn.StartGame();
        PutUprisingInPlay(scn);
        PutCompleteSeparatistMissionOnTable(scn);
        AdvanceTableChanged(scn);
        assertEquals("At War With Itself", uprising.getBlueprint().getTitle());

        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(dooku);
        scn.EnsureDSForcePile(2);

        assertTrue(scn.DSCardActionAvailable(uprising, "Separatist"));
        scn.DSUseCardAction(uprising, "Separatist");
        scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoiceAvailable(dooku));
        scn.DSChooseCard(dooku);
        scn.PassAllResponses();
        assertInHand(dooku);
    }
}
