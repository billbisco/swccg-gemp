package com.gempukku.swccgo.cards.set1240.light;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_090_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("bones", "1240_90");
                    put("tauntaun", "3_70");
                    put("bogwing", "4_3");
                    put("worrt", "6_48");
                    put("ridge", "3_62");
                    put("jungle", "4_86");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("secretplans", "12_135");
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

    @Test
    public void TauntaunBonesVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("bones").getBlueprint();

        assertEquals("Tauntaun Bones", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void TauntaunBonesVYourFerociousCreaturesAreDeployMinus2AndFerocityPlus1() {
        var scn = GetScenario();
        var bones = scn.GetLSCard("bones");
        var worrt = scn.GetLSCard("worrt");
        var jungle = scn.GetLSCard("jungle");

        scn.StartGame();
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLSHand(worrt);
        int printedCost = scn.GetDeployCost(worrt);
        scn.MoveCardsToLSSideOfTable(bones);
        assertEquals(printedCost - 2, scn.GetDeployCost(worrt), scn.epsilon);

        scn.MoveCardsToLocation(jungle, worrt);
        // Worrt's printed ferocity is 3.
        assertEquals(4f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), worrt, 0f), scn.epsilon);
    }

    @Test
    public void TauntaunBonesVDoesNotAffectAParasite() {
        var scn = GetScenario();
        var bogwing = scn.GetLSCard("bogwing");
        var jungle = scn.GetLSCard("jungle");

        scn.StartGame();
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLSHand(bogwing);
        int printedCost = scn.GetDeployCost(bogwing);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("bones"));
        assertEquals(printedCost, scn.GetDeployCost(bogwing), scn.epsilon);
        scn.MoveCardsToLocation(jungle, bogwing);
        assertEquals(0f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), bogwing, 0f), scn.epsilon);
    }

    @Test
    public void TauntaunBonesVDeploysACreatureFromReserveDeckOncePerTurnButNotAParasite() {
        var scn = GetScenario();
        var bones = scn.GetLSCard("bones");
        var worrt = scn.GetLSCard("worrt");
        var bogwing = scn.GetLSCard("bogwing");
        var jungle = scn.GetLSCard("jungle");

        scn.StartGame();
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLSSideOfTable(bones);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureForcePile(scn.LS, 6);
        scn.MoveCardsToTopOfLSReserveDeck(worrt, bogwing);

        assertTrue(scn.LSCardActionAvailable(bones, "Deploy creature or tauntaun"));
        scn.LSUseCardAction(bones, "Deploy creature or tauntaun");
        scn.PassAllResponses();
        int parasiteIndex = scn.LSGetADParamAsList("blueprintId").indexOf(bogwing.getBlueprintId(true));
        if (parasiteIndex >= 0) {
            assertFalse(Boolean.parseBoolean(scn.LSGetADParam("selectable")[parasiteIndex]));
        }
        scn.LSChooseCard(worrt);
        for (int i = 0; i < 10 && worrt.getZone() != Zone.AT_LOCATION; i++) {
            if (scn.LSHasCardChoiceAvailable(jungle)) {
                scn.LSChooseCard(jungle);
            }
            else {
                scn.PassAllResponses();
            }
        }
        assertEquals(jungle, worrt.getAtLocation());

        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(bones, "Deploy creature or tauntaun"));
    }

    @Test
    public void TauntaunBonesVDeploysATauntaunFromReserveDeck() {
        var scn = GetScenario();
        var bones = scn.GetLSCard("bones");
        var tauntaun = scn.GetLSCard("tauntaun");
        var ridge = scn.GetLSCard("ridge");

        scn.StartGame();
        scn.MoveLocationToTable(ridge);
        scn.MoveCardsToLSSideOfTable(bones);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureForcePile(scn.LS, 6);
        scn.MoveCardsToTopOfLSReserveDeck(tauntaun);

        scn.LSUseCardAction(bones, "Deploy creature or tauntaun");
        scn.PassAllResponses();
        scn.LSChooseCard(tauntaun);
        for (int i = 0; i < 10 && tauntaun.getZone() != Zone.AT_LOCATION; i++) {
            if (scn.LSHasCardChoiceAvailable(ridge)) {
                scn.LSChooseCard(ridge);
            }
            else {
                scn.PassAllResponses();
            }
        }
        assertEquals(ridge, tauntaun.getAtLocation());
    }

    /** Has a card retrieve 3 Force for Light Side with Secret Plans on table; returns the Secret Plans prompt. */
    private String secretPlansPromptForRetrievalBy(boolean withBones, String sourceKey) {
        var scn = GetScenario();
        var bones = scn.GetLSCard("bones");
        var source = scn.GetLSCard(sourceKey);
        var jungle = scn.GetLSCard("jungle");

        scn.StartGame();
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLocation(jungle, source);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("secretplans"));
        if (withBones) {
            scn.MoveCardsToLSSideOfTable(bones);
        }
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureForcePile(scn.LS, 6);
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
        scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
        scn.LSExecuteAdHocEffect(source, new com.gempukku.swccgo.logic.effects.RetrieveForceEffect(
                new com.gempukku.swccgo.logic.actions.TopLevelGameTextAction(source, scn.LS, source.getCardId()), scn.LS, 3));
        for (int i = 0; i < 6 && !scn.LSDecisionAvailable("Force to proceed with Force retrieval"); i++) {
            scn.PassAllResponses();
        }
        return scn.LSGetDecision() == null ? "" : scn.LSGetDecision().getText();
    }

    @Test
    public void TauntaunBonesVCreatureRetrievalNeedsOnlyXMinus2ForceForSecretPlans() {
        assertTrue(secretPlansPromptForRetrievalBy(true, "worrt").contains("use 1 Force"));
    }

    @Test
    public void TauntaunBonesVSecretPlansUnchangedWithoutBonesOrForNonCreatureRetrieval() {
        assertTrue(secretPlansPromptForRetrievalBy(false, "worrt").contains("use 3 Force"));
        assertTrue(secretPlansPromptForRetrievalBy(true, "tauntaun").contains("use 3 Force"));
    }
}
