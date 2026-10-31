package com.gempukku.swccgo.cards.set1105.light;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1105_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("apparition", "1105_15");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
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
    public void ObiWansApparitionStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("apparition").getBlueprint();

        assertEquals("Obi-Wan's Apparition", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.EFFECT);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ObiWansApparitionDeploysOnTable() {
        var scn = GetScenario();
        var apparition = scn.GetLSCard("apparition");

        scn.StartGame();
        scn.MoveCardsToLSHand(apparition);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(apparition));
        scn.LSDeployCard(apparition);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, apparition);
    }

    @Test
    public void ObiWansApparitionPlacesJustLostJediOutOfPlay() {
        var scn = GetScenario();
        var apparition = scn.GetLSCard("apparition");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(apparition);
        scn.MoveCardsToLocation(site, luke, vader, stormtrooper);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassBattleStartResponses();
        scn.SkipToDamageSegment(false);
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSPayBattleDamageFromCardInPlay(luke);
        scn.PassCardLeavingTable();
        int safety = 0;
        while (safety++ < 16 && scn.LSGetDecision() != null
                && !String.valueOf(scn.LSGetDecision().getText()).toLowerCase().contains("out of play")) {
            scn.LSPass();
        }
        if (scn.LSGetDecision() != null
                && String.valueOf(scn.LSGetDecision().getText()).toLowerCase().contains("out of play")) {
            scn.LSChooseAction("out of play");
            scn.PassAllResponses();
        }
        assertTrue(luke.getZone() == Zone.OUT_OF_PLAY || luke.getZone() == Zone.LOST_PILE
                || luke.getZone() == Zone.TOP_OF_LOST_PILE);
    }
}
