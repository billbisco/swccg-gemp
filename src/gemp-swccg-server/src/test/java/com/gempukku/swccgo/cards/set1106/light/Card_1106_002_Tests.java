package com.gempukku.swccgo.cards.set1106.light;

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

public class Card_1106_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("urchins", "1106_2");
                    put("jawa", "1_14");
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
    public void AllMyUrchinsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("urchins").getBlueprint();

        assertEquals("All My Urchins", card.getTitle());
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
            add(Icon.CLOUD_CITY);
            add(Icon.EFFECT);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AllMyUrchinsDeploysOnTable() {
        var scn = GetScenario();
        var urchins = scn.GetLSCard("urchins");

        scn.StartGame();
        scn.MoveCardsToLSHand(urchins);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(urchins));
        scn.LSDeployCard(urchins);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, urchins);
    }

    @Test
    public void AllMyUrchinsPlacesJustForfeitedCardInUsedPileWhenAllAbilityIsFromAliens() {
        var scn = GetScenario();
        var urchins = scn.GetLSCard("urchins");
        var jawa = scn.GetLSCard("jawa");
        var vader = scn.GetDSCard("vader");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(urchins);
        scn.MoveCardsToLocation(site, jawa, vader, stormtrooper);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassBattleStartResponses();
        scn.SkipToDamageSegment(false);
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSPayBattleDamageFromCardInPlay(jawa);
        scn.PassCardLeavingTable();
        assertTrue(jawa.getZone() == Zone.USED_PILE || jawa.getZone() == Zone.TOP_OF_USED_PILE
                || jawa.getZone() == Zone.LOST_PILE || jawa.getZone() == Zone.TOP_OF_LOST_PILE);
    }
}
