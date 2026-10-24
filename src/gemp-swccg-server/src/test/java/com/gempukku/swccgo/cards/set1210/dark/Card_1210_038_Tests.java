package com.gempukku.swccgo.cards.set1210.dark;

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
import static org.junit.Assert.assertTrue;

public class Card_1210_038_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("justice", "1210_38");
                    put("vader", "1_168");
                    put("stormtrooper", "1_194");
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
    public void ImperialJusticeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("justice").getBlueprint();

        assertEquals("Imperial Justice", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ImperialJusticeDeploysOnTableAndStacksTopThreeReserveCards() {
        var scn = GetScenario();
        var justice = scn.GetDSCard("justice");

        scn.StartGame();
        scn.MoveCardsToDSHand(justice);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(3);

        assertTrue(scn.DSCardPlayAvailable(justice));
        scn.DSPlayCard(justice);
        scn.PassAllResponses();
        for (int i = 0; i < 12; i++) {
            if (scn.gameState().getStackedCards(justice).size() >= 3) {
                break;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            break;
        }

        assertEquals(Zone.SIDE_OF_TABLE, justice.getZone());
        assertEquals(3, scn.gameState().getStackedCards(justice).size());
    }

    @Test
    public void ImperialJusticeTakesAStackedCardIntoHandDuringBattle() {
        var scn = GetScenario();
        var justice = scn.GetDSCard("justice");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, luke);
        scn.MoveCardsToDSHand(justice);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(3);
        scn.DSPlayCard(justice);
        scn.PassAllResponses();
        for (int i = 0; i < 12; i++) {
            if (scn.gameState().getStackedCards(justice).size() >= 3) {
                break;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            break;
        }
        var stacked = (PhysicalCardImpl) scn.gameState().getStackedCards(justice).iterator().next();
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        assertTrue(scn.DSCardActionAvailable(justice, "Take stacked card into hand"));
        scn.DSUseCardAction(justice, "Take stacked card into hand");
        if (scn.DSHasCardChoiceAvailable(stacked)) {
            scn.DSChooseCard(stacked);
        }
        scn.PassAllResponses();

        assertEquals(Zone.HAND, stacked.getZone());
        assertTrue(scn.game().getModifiersQuerying().mayNotCancelBattleDestinyDraws(
                scn.gameState(), scn.DS, scn.LS, false));
        assertTrue(scn.game().getModifiersQuerying().mayNotModifyBattleDestinyDraws(
                scn.gameState(), scn.DS, scn.LS));
        assertEquals(1, scn.game().getModifiersQuerying().getNumBattleDestinyDraws(
                scn.gameState(), scn.DS, true, false));
        assertEquals(1, scn.game().getModifiersQuerying().getNumBattleDestinyDraws(
                scn.gameState(), scn.LS, true, false));
    }
}
