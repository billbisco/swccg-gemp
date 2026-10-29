package com.gempukku.swccgo.cards.set1220.light;

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

public class Card_1220_130_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("disintegrations", "1220_130");
                    put("leia", "1_17");
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
    public void NoDisintegrationsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("disintegrations").getBlueprint();

        assertEquals("No Disintegrations!", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void NoDisintegrationsDeploysOnTable() {
        var scn = GetScenario();
        var disintegrations = scn.GetLSCard("disintegrations");

        scn.StartGame();
        scn.MoveCardsToLSHand(disintegrations);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(disintegrations));
        scn.LSDeployCard(disintegrations);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, disintegrations);
    }

    @Test
    public void NoDisintegrationsPlacesJustForfeitedRebelOfAbilityMoreThanTwoOutOfPlayDuringOpponentsTurn() {
        var scn = GetScenario();
        var disintegrations = scn.GetLSCard("disintegrations");
        var leia = scn.GetLSCard("leia");
        var vader = scn.GetDSCard("vader");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(disintegrations);
        scn.MoveCardsToLocation(site, leia, vader, stormtrooper);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassBattleStartResponses();
        scn.SkipToDamageSegment(false);
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSPayBattleDamageFromCardInPlay(leia);
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
        assertTrue(leia.getZone() == Zone.OUT_OF_PLAY || leia.getZone() == Zone.LOST_PILE
                || leia.getZone() == Zone.TOP_OF_LOST_PILE);
    }
}
