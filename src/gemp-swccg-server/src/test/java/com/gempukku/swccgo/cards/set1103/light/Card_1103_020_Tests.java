package com.gempukku.swccgo.cards.set1103.light;

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

public class Card_1103_020_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("scanner", "1103_20");
                }},
                new HashMap<>() {{
                    put("political", "12_137");
                    put("trooper", "1_194");
                }},
                13, 10,
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
    public void ScannerTechsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("scanner").getBlueprint();

        assertEquals("Scanner Techs", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.EFFECT);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ScannerTechsDeploysOnTable() {
        var scn = GetScenario();
        var scanner = scn.GetLSCard("scanner");

        scn.StartGame();
        scn.MoveCardsToLSHand(scanner);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(scanner));
        scn.LSPlayCard(scanner);
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, scanner.getZone());
    }

    @Test
    public void ScannerTechsPeeksAtOpponentsHandDuringOpponentsMovePhase() {
        var scn = GetScenario();
        var scanner = scn.GetLSCard("scanner");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(scanner);
        scn.MoveCardsToDSHand(trooper);
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.MOVE);
        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(scanner, "Peek"));
        scn.LSUseCardAction(scanner, "Peek");
        scn.PassForceUseResponses();
        if (scn.LSChoiceAvailable("Peek at opponent's hand")) {
            scn.LSChoose("Peek at opponent's hand");
        }
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Opponent's hand")) {
            scn.LSDecided("");
        }
        scn.PassAllResponses();
        assertEquals(Zone.HAND, trooper.getZone());
    }

    @Test
    public void ScannerTechsPlacesAPoliticalEffectInOwnersUsedPile() {
        var scn = GetScenario();
        var scanner = scn.GetLSCard("scanner");
        var political = scn.GetDSCard("political");
        var stacked = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(scanner);
        scn.MoveCardsToDSSideOfTable(political);
        scn.StackCardsOn(political, stacked);
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.MOVE);
        scn.DSPass();
        String[] texts = scn.LSGetDecision() != null ? scn.LSGetADParam("actionText") : null;
        assertEquals(Zone.SIDE_OF_TABLE, political.getZone());
        assertTrue("place action missing; actions=" + java.util.Arrays.toString(texts)
                + " subtype=" + political.getBlueprint().getCardSubtype()
                + " scanner zone=" + scanner.getZone(),
                scn.LSCardActionAvailable(scanner, "Political Effect"));
        scn.LSUseCardAction(scanner, "Political Effect");
        if (scn.LSDecisionAvailable("Force") || scn.DSDecisionAvailable("Force")) {
            scn.PassForceUseResponses();
        }
        for (int i = 0; i < 12; i++) {
            boolean politicalUsed = political.getZone() == Zone.USED_PILE || political.getZone() == Zone.TOP_OF_USED_PILE;
            boolean stackedUsed = stacked.getZone() == Zone.USED_PILE || stacked.getZone() == Zone.TOP_OF_USED_PILE;
            if (politicalUsed && stackedUsed) {
                break;
            }
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(political)) {
                scn.LSChooseCard(political);
                continue;
            }
            if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(political)) {
                scn.DSChooseCard(political);
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(stacked)) {
                scn.LSChooseCard(stacked);
                continue;
            }
            if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(stacked)) {
                scn.DSChooseCard(stacked);
                continue;
            }
            if (scn.LSDecisionAvailable("Force") || scn.DSDecisionAvailable("Force")) {
                scn.PassForceUseResponses();
                continue;
            }
            String lsText = scn.LSGetDecision() != null ? String.valueOf(scn.LSGetDecision().getText()).toLowerCase() : "";
            String dsText = scn.DSGetDecision() != null ? String.valueOf(scn.DSGetDecision().getText()).toLowerCase() : "";
            if (lsText.contains("optional") || dsText.contains("optional")
                    || lsText.contains("pass") || dsText.contains("pass")) {
                scn.PassAllResponses();
                continue;
            }
            break;
        }
        assertTrue("political zone=" + political.getZone() + " stacked zone=" + stacked.getZone()
                        + " LS=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : "null")
                        + " DS=" + (scn.DSGetDecision() != null ? scn.DSGetDecision().getText() : "null"),
                political.getZone() == Zone.USED_PILE || political.getZone() == Zone.TOP_OF_USED_PILE);
        assertTrue(stacked.getZone() == Zone.USED_PILE || stacked.getZone() == Zone.TOP_OF_USED_PILE);
    }

    @Test
    public void ScannerTechsStopsOpponentPlacingPoliticalEffectsInUsedPileUnlessYouHaveThirteenCardsInHand() {
        var scn = GetScenario();
        var scanner = scn.GetLSCard("scanner");
        var political = scn.GetDSCard("political");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(scanner);
        scn.MoveCardsToDSSideOfTable(political);
        assertTrue(scn.game().getModifiersQuerying().mayNotBePlacedInUsedPile(scn.gameState(), political, scn.DS));
        assertFalse(scn.game().getModifiersQuerying().mayNotBePlacedInUsedPile(scn.gameState(), political, scn.LS));
        scn.MoveCardsToLSHand(scn.GetLSFillerRange(1, 13));
        assertEquals(13, scn.GetLSHandCount());
        assertFalse(scn.game().getModifiersQuerying().mayNotBePlacedInUsedPile(scn.gameState(), political, scn.DS));
    }
}
