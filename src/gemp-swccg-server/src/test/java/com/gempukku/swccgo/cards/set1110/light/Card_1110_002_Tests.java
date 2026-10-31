package com.gempukku.swccgo.cards.set1110.light;

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

public class Card_1110_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("evac", "1110_2");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
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

    @Test
    public void EvacuationControlStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("evac").getBlueprint();

        assertEquals("Evacuation Control", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.EFFECT);        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_10, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void EvacuationControlDeploysOnTableAndStacksTopThreeReserveCards() {
        var scn = GetScenario();
        var evac = scn.GetLSCard("evac");

        scn.StartGame();
        scn.MoveCardsToLSHand(evac);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(3);

        assertTrue(scn.LSCardPlayAvailable(evac));
        scn.LSPlayCard(evac);
        scn.PassAllResponses();
        for (int i = 0; i < 12; i++) {
            if (scn.gameState().getStackedCards(evac).size() >= 3) {
                break;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            break;
        }

        assertEquals(Zone.SIDE_OF_TABLE, evac.getZone());
        assertEquals(3, scn.gameState().getStackedCards(evac).size());
    }

    @Test
    public void EvacuationControlTakesAStackedCardIntoHandDuringBattle() {
        var scn = GetScenario();
        var evac = scn.GetLSCard("evac");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader);
        scn.MoveCardsToLSHand(evac);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(3);
        scn.LSPlayCard(evac);
        scn.PassAllResponses();
        for (int i = 0; i < 12; i++) {
            if (scn.gameState().getStackedCards(evac).size() >= 3) {
                break;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            break;
        }
        var stacked = (PhysicalCardImpl) scn.gameState().getStackedCards(evac).iterator().next();
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();

        assertTrue(scn.LSCardActionAvailable(evac, "Take stacked card into hand"));
        scn.LSUseCardAction(evac, "Take stacked card into hand");
        if (scn.LSHasCardChoiceAvailable(stacked)) {
            scn.LSChooseCard(stacked);
        }
        scn.PassAllResponses();

        assertEquals(Zone.HAND, stacked.getZone());
        assertTrue(scn.game().getModifiersQuerying().mayNotCancelBattleDestinyDraws(
                scn.gameState(), scn.LS, scn.DS, false));
        assertTrue(scn.game().getModifiersQuerying().mayNotModifyBattleDestinyDraws(
                scn.gameState(), scn.LS, scn.DS));
        assertEquals(1, scn.game().getModifiersQuerying().getNumBattleDestinyDraws(
                scn.gameState(), scn.LS, true, false));
        assertEquals(1, scn.game().getModifiersQuerying().getNumBattleDestinyDraws(
                scn.gameState(), scn.DS, true, false));
    }
}
