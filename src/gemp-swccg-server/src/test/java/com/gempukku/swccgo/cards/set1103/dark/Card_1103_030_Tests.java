package com.gempukku.swccgo.cards.set1103.dark;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1103_030_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("han", "1_11");
                    put("lostcard", "1_154");
                }},
                new HashMap<>() {{
                    put("dannik", "1103_30");
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
    public void DannikJerrikoStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("dannik").getBlueprint();

        assertEquals("Dannik Jerriko", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ASSASSIN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.A_NEW_HOPE);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DannikJerrikoPlacesCardOutOfPlayFromOpponentsLostPileAfterWinningBattle() {
        var scn = GetScenario();
        var dannik = scn.GetDSCard("dannik");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var han = scn.GetLSCard("han");
        var lostcard = scn.GetLSCard("lostcard");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dannik, stormtrooper, han);
        scn.MoveCardsToTopOfLSLostPile(lostcard);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(3);
        scn.PrepareDSDestiny(7);
        scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        scn.PassResponses("BEFORE_BATTLE_DESTINY_DRAWS");
        scn.DSChooseYes();
        scn.PassDestinyDrawResponses();
        for (int i = 0; i < 8; i++) {
            if (scn.GetDSAvailableActions().toString().contains("Place card from Lost Pile out of play")) {
                break;
            }
            if (scn.DSDecisionAvailable("optional")) {
                scn.DSPass();
            } else if (scn.LSDecisionAvailable("optional")) {
                scn.LSPass();
            } else {
                break;
            }
        }
        assertTrue(scn.DSCardActionAvailable(dannik, "Place card from Lost Pile out of play"));
        scn.DSUseCardAction(dannik, "Place card from Lost Pile out of play");
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(lostcard);
        scn.PassAllResponses();
        assertEquals(Zone.OUT_OF_PLAY, lostcard.getZone());
    }
}
