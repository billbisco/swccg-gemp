package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("combo", "1283_21");
                    put("ellorrs", "1_49");
                    put("revolution", "1_62");
                    put("operative", "7_1");
                    put("boshek", "1_4");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("destiny", "9_134");
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

    protected VirtualTableScenario GetHiddenBaseScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("combo", "1283_21");
                    put("ellorrs", "1_49");
                    put("revolution", "1_62");
                    put("operative", "7_1");
                    put("boshek", "1_4");
                    put("quarter", "1283_24");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("destiny", "9_134");
                    put("vader", "1_168");
                }},
                10, 10,
                HiddenBaseVObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void UnderworldContactsAndUnchartedSettlementsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("combo").getBlueprint();

        assertEquals("Underworld Contacts & Uncharted Settlements", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void UnderworldContactsDoesNotDeployWithoutHiddenBase() {
        var scn = GetScenario();
        var combo = scn.GetLSCard("combo");

        scn.StartGame();
        scn.MoveCardsToLSHand(combo);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(combo));
    }

    @Test
    public void UnderworldContactsDeploysOnVirtualBlock4HiddenBaseAndSetsAlienPair() {
        var scn = GetHiddenBaseScenario();
        var combo = scn.GetLSCard("combo");
        var hiddenbase = scn.GetLSCard("hiddenbase");

        scn.StartGame();
        scn.MoveCardsToLSHand(combo);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(combo));
        scn.LSPlayCard(combo);
        scn.LSChooseCard(hiddenbase);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(hiddenbase, combo));
        assertEquals("Alien", hiddenbase.getWhileInPlayData().getTextValue());
    }

    @Test
    public void UnderworldContactsDoesNotDeployOnNonVirtualHiddenBase() {
        StartingSetup originalHiddenBaseOutsideDeck = new StartingSetup() {
            @Override
            public HashMap<String, String> Cards() {
                return new HashMap<>() {{
                    put("hiddenbase", "7_136");
                }};
            }

            @Override
            public void Setup(VirtualTableScenario scn) {
            }
        };
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("combo", "1283_21");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                originalHiddenBaseOutsideDeck,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var combo = scn.GetLSCard("combo");
        var hiddenbase = scn.GetLSCard("hiddenbase");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(hiddenbase);
        scn.MoveCardsToLSHand(combo);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(combo));
    }

    @Test
    public void UnderworldContactsCancelsYourDestinyOnTable() {
        var scn = GetHiddenBaseScenario();
        var combo = scn.GetLSCard("combo");
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var destiny = scn.GetDSCard("destiny");

        scn.StartGame();
        scn.AttachCardsTo(hiddenbase, combo);
        scn.gameState().reapplyAffectingForCard(scn.game(), combo);
        PhysicalCardImpl bhbm = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.DS, VirtualTableScenario._cardLibrary, "9_151");
        scn.gameState().addCardToZone(bhbm, Zone.SIDE_OF_TABLE, scn.DS, true, false, false);
        bhbm.setObjectiveDeploymentComplete(true);
        scn.gameState().reapplyAffectingForCard(scn.game(), bhbm);
        scn.AttachCardsTo(bhbm, destiny);
        scn.gameState().reapplyAffectingForCard(scn.game(), destiny);
        AdvanceTableChangedTakingCancel(scn, destiny);

        assertInZone(Zone.LOST_PILE, destiny);
    }

    @Test
    public void UnderworldContactsMayNotDeployEllorrsRevolutionOrOperatives() {
        var scn = GetHiddenBaseScenario();
        var combo = scn.GetLSCard("combo");
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var ellorrs = scn.GetLSCard("ellorrs");
        var revolution = scn.GetLSCard("revolution");
        var operative = scn.GetLSCard("operative");
        var boshek = scn.GetLSCard("boshek");

        scn.StartGame();
        scn.AttachCardsTo(hiddenbase, combo);
        scn.MoveCardsToLSHand(ellorrs, revolution, operative, boshek);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(ellorrs));
        assertFalse(scn.LSCardPlayAvailable(revolution));
        assertFalse(scn.LSCardPlayAvailable(operative));
        assertTrue(scn.LSCardPlayAvailable(boshek));
    }

    @Test
    public void UnderworldContactsOpponentGeneratesNoForceAtHiddenLandingSite() {
        var scn = GetHiddenBaseScenario();
        var combo = scn.GetLSCard("combo");
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var hls = scn.GetLSCard("hls");

        scn.StartGame();
        scn.AttachCardsTo(hiddenbase, combo);

        float dsGen = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), hls, scn.DS);
        assertEquals(0, Math.round(dsGen));
    }

    @Test
    public void UnderworldContactsAddsAttritionWhileSystemsWillSlipIsOnTable() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1_135");
                    put("hls", "1240_102");
                    put("uncharted", "1240_91");
                    put("combo", "1283_21");
                    put("station", "1240_103");
                    put("perimeter", "1240_104");
                    put("luke", "1_19");
                    put("biggs", "1_3");
                    put("leia", "1_17");
                    put("boshek", "1_4");
                    put("jawa", "1_12");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("vader", "1_168");
                }},
                10, 10,
                HiddenBaseVObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var combo = scn.GetLSCard("combo");
        var hls = scn.GetLSCard("hls");
        var station = scn.GetLSCard("station");
        var perimeter = scn.GetLSCard("perimeter");
        var luke = scn.GetLSCard("luke");
        var biggs = scn.GetLSCard("biggs");
        var leia = scn.GetLSCard("leia");
        var boshek = scn.GetLSCard("boshek");
        var jawa = scn.GetLSCard("jawa");
        var vader = scn.GetDSCard("vader");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        var stationPlacement = scn.gameState().getLocationPlacement(scn.game(), station, "Yavin 4", null);
        scn.MoveLocationToTable(station, stationPlacement.getFirst());
        var perimeterPlacement = scn.gameState().getLocationPlacement(scn.game(), perimeter, "Yavin 4", null);
        scn.MoveLocationToTable(perimeter, perimeterPlacement.getFirst());
        scn.MoveCardsToLocation(hls, luke);
        scn.MoveCardsToLocation(station, biggs);
        scn.MoveCardsToLocation(perimeter, leia);
        scn.SkipToPhase(Phase.CONTROL);
        assertEquals("Systems Will Slip Through Your Fingers", hiddenbase.getBlueprint().getTitle());

        scn.AttachCardsTo(hiddenbase, combo);
        scn.MoveCardsToLocation(hls, boshek);
        scn.MoveCardsToLocation(station, jawa);
        scn.MoveCardsToLocation(hls, vader, trooper);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(hls);
        scn.PrepareDSDestiny(0);
        scn.PrepareLSDestiny(0);
        scn.SkipToDamageSegment(true);

        assertEquals(2, scn.GetUnpaidDSAttrition());
    }

    private void AdvanceTableChangedTakingCancel(VirtualTableScenario scn, PhysicalCardImpl destiny) {
        for (int i = 0; i < 20; i++) {
            if (destiny.getZone() == Zone.LOST_PILE || destiny.getZone() == Zone.TOP_OF_LOST_PILE) {
                return;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                return;
            }
            String lower = decision.getText().toLowerCase();
            if (lower.contains("cancel") || lower.contains("your destiny")) {
                if (scn.LSHasCardChoiceAvailable(destiny)) {
                    scn.LSChooseCard(destiny);
                    continue;
                }
                if (scn.DSHasCardChoiceAvailable(destiny)) {
                    scn.DSChooseCard(destiny);
                    continue;
                }
            }
            var actions = scn.GetAvailableActions(scn.GetDecidingPlayer());
            if (actions.toString().contains("Cancel") || actions.toString().contains("Your Destiny")) {
                scn.PlayerDecided(scn.GetDecidingPlayer(), "0");
                continue;
            }
            if (lower.contains("required")) {
                scn.PlayerDecided(scn.GetDecidingPlayer(), "0");
                continue;
            }
            if (lower.contains("optional")) {
                scn.PassResponses("optional");
            } else if (lower.contains("action")) {
                scn.PassResponses("action");
            } else {
                scn.PlayerDecided(scn.GetDecidingPlayer(), "0");
            }
        }
    }

    StartingSetup HiddenBaseVObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("hiddenbase", "1240_107");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            if (scn.LSDecisionAvailable("starting")) {
                scn.LSChooseCard(scn.GetLSCard("hiddenbase"));
            }
            for (int i = 0; i < 20; i++) {
                boolean progressed = false;
                if (scn.LSDecisionAvailable("battleground")
                        || scn.LSDecisionAvailable("Choose card to deploy from Reserve Deck")) {
                    scn.LSChooseCard(scn.GetLSCard("yavin"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("On which side")) {
                    scn.LSChoose("Left");
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Hidden Landing Site")) {
                    scn.LSChooseCard(scn.GetLSCard("hls"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Uncharted Settlements")) {
                    scn.LSChooseCard(scn.GetLSCard("uncharted"));
                    progressed = true;
                }
                else if (scn.LSDecisionAvailable("Choose pair")
                        || scn.LSDecisionAvailable("Choose pair you may deploy")) {
                    scn.LSChoose("Alien / Independent starship");
                    progressed = true;
                }
                if (!progressed) {
                    break;
                }
            }
        }
    };
}
