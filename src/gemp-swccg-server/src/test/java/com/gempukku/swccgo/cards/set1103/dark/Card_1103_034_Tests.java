package com.gempukku.swccgo.cards.set1103.dark;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1103_034_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("scan", "1103_34");
                    put("political", "12_137");
                }},
                10, 13,
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
    public void HyperwaveScanStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("scan").getBlueprint();

        assertEquals("Hyperwave Scan", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
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
    public void HyperwaveScanDeploysOnTable() {
        var scn = GetScenario();
        var scan = scn.GetDSCard("scan");

        scn.StartGame();
        scn.MoveCardsToDSHand(scan);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(scan));
        scn.DSPlayCard(scan);
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, scan.getZone());
    }

    @Test
    public void HyperwaveScanPeeksAtOpponentsHandDuringOpponentsMovePhase() {
        var scn = GetScenario();
        var scan = scn.GetDSCard("scan");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(scan);
        scn.MoveCardsToLSHand(luke);
        scn.EnsureDSForcePile(4);
        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(scan, "Peek"));
        scn.DSUseCardAction(scan, "Peek");
        scn.PassForceUseResponses();
        if (scn.DSChoiceAvailable("Peek at opponent's hand")) {
            scn.DSChoose("Peek at opponent's hand");
        }
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Opponent's hand")) {
            scn.DSDecided("");
        }
        scn.PassAllResponses();
        assertEquals(Zone.HAND, luke.getZone());
    }

    @Test
    public void HyperwaveScanPlacesAPoliticalEffectInOwnersUsedPile() {
        var scn = GetScenario();
        var scan = scn.GetDSCard("scan");
        var political = scn.GetDSCard("political");
        var stacked = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(scan);
        scn.MoveCardsToDSSideOfTable(political);
        scn.StackCardsOn(political, stacked);
        scn.EnsureDSForcePile(4);
        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSPass();
        String[] texts = scn.DSGetDecision() != null ? scn.DSGetADParam("actionText") : null;
        assertEquals(Zone.SIDE_OF_TABLE, political.getZone());
        assertTrue("place action missing; actions=" + java.util.Arrays.toString(texts)
                + " subtype=" + political.getBlueprint().getCardSubtype()
                + " scan zone=" + scan.getZone(),
                scn.DSCardActionAvailable(scan, "Political Effect"));
        scn.DSUseCardAction(scan, "Political Effect");
        if (scn.DSDecisionAvailable("Force") || scn.LSDecisionAvailable("Force")) {
            scn.PassForceUseResponses();
        }
        for (int i = 0; i < 12; i++) {
            boolean politicalUsed = political.getZone() == Zone.USED_PILE || political.getZone() == Zone.TOP_OF_USED_PILE;
            boolean stackedUsed = stacked.getZone() == Zone.USED_PILE || stacked.getZone() == Zone.TOP_OF_USED_PILE;
            if (politicalUsed && stackedUsed) {
                break;
            }
            if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(political)) {
                scn.DSChooseCard(political);
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(political)) {
                scn.LSChooseCard(political);
                continue;
            }
            if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(stacked)) {
                scn.DSChooseCard(stacked);
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(stacked)) {
                scn.LSChooseCard(stacked);
                continue;
            }
            if (scn.DSDecisionAvailable("Force") || scn.LSDecisionAvailable("Force")) {
                scn.PassForceUseResponses();
                continue;
            }
            String dsText = scn.DSGetDecision() != null ? String.valueOf(scn.DSGetDecision().getText()).toLowerCase() : "";
            String lsText = scn.LSGetDecision() != null ? String.valueOf(scn.LSGetDecision().getText()).toLowerCase() : "";
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
    public void HyperwaveScanStopsOpponentPlacingPoliticalEffectsInUsedPileUnlessYouHaveThirteenCardsInHand() {
        var scn = GetScenario();
        var scan = scn.GetDSCard("scan");
        var political = scn.GetDSCard("political");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(scan);
        scn.MoveCardsToDSSideOfTable(political);
        assertTrue(scn.game().getModifiersQuerying().mayNotBePlacedInUsedPile(scn.gameState(), political, scn.LS));
        assertFalse(scn.game().getModifiersQuerying().mayNotBePlacedInUsedPile(scn.gameState(), political, scn.DS));
        scn.MoveCardsToDSHand(scn.GetDSFillerRange(1, 13).toArray(new PhysicalCardImpl[0]));
        assertEquals(13, scn.GetDSHandCount());
        assertFalse(scn.game().getModifiersQuerying().mayNotBePlacedInUsedPile(scn.gameState(), political, scn.LS));
    }
}
