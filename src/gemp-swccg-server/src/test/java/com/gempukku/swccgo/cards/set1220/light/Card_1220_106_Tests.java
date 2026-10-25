package com.gempukku.swccgo.cards.set1220.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1220_106_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("wyron", "1220_106");
                    put("ie", "2_33");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("vader", "1_168");
                    put("corridor", "4_162");
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
    public void WyronSerperStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("wyron").getBlueprint();

        assertEquals("Wyron Serper", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.HOTH);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WyronSerperTakesAnImmediateEffectIntoHandFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var wyron = scn.GetLSCard("wyron");
        var ie = scn.GetLSCard("ie");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, wyron);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(ie);
        assertTrue(scn.LSCardActionAvailable(wyron, "Take Immediate Effect into hand"));
        scn.LSUseCardAction(wyron, "Take Immediate Effect into hand");
        chooseFromSearch(scn, ie);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, ie.getZone());
    }

    @Test
    public void WyronSerperSatisfiesRemainingAttritionAndBattleDamageWhenForfeitedFromOpponentsStarshipSite() {
        var scn = GetScenario();
        var wyron = scn.GetLSCard("wyron");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var vader = scn.GetDSCard("vader");
        var corridor = scn.GetDSCard("corridor");

        scn.StartGame();
        scn.MoveLocationToTable(corridor);
        scn.MoveCardsToLocation(corridor, wyron, luke, trooper, vader);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        scn.PrepareDSDestiny(6);
        scn.PrepareLSDestiny(0);
        scn.DSInitiateBattle(corridor);
        scn.PassWeaponsSegmentActions();
        scn.SkipBattleDestinyDraws(true);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertTrue(scn.AwaitingLSAttritionPayment());
        scn.LSChooseCard(wyron);
        scn.PassCardLeavingTable();
        assertTrue(scn.LSDecisionAvailable("Satisfy remaining attrition and battle damage")
                || scn.GetLSAvailableActions().toString().contains("Satisfy remaining attrition and battle damage"));
        scn.LSChooseAction("Satisfy remaining attrition and battle damage");
        scn.PassAllResponses();
        assertTrue(wyron.getZone() == Zone.LOST_PILE || wyron.getZone() == Zone.TOP_OF_LOST_PILE);
        assertEquals(Zone.AT_LOCATION, luke.getZone());
    }

    private void chooseFromSearch(VirtualTableScenario scn, PhysicalCardImpl card) {
        int safety = 0;
        while (safety++ < 16 && !scn.LSHasCardChoiceAvailable(card)) {
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else {
                break;
            }
        }
        scn.LSChooseCard(card);
    }
}
