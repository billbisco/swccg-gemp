package com.gempukku.swccgo.cards.set1210.light;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_096_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("il19", "1210_96");
                    put("echo", "3_59");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
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
    public void IL19StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("il19").getBlueprint();

        assertEquals("IL-19", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SPY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.A_NEW_HOPE);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void IL19AddsOneLightForceIconAtANonHothSite() {
        var scn = GetScenario();
        var il19 = scn.GetLSCard("il19");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        int before = scn.GetIconCount(site, Icon.LIGHT_FORCE);
        scn.MoveCardsToLocation(site, il19);
        assertEquals(before + 1, scn.GetIconCount(site, Icon.LIGHT_FORCE));
    }

    @Test
    public void IL19DoesNotAddALightForceIconAtAHothSite() {
        var scn = GetScenario();
        var il19 = scn.GetLSCard("il19");
        var echo = scn.GetLSCard("echo");

        scn.StartGame();
        scn.MoveLocationToTable(echo);
        int before = scn.GetIconCount(echo, Icon.LIGHT_FORCE);
        scn.MoveCardsToLocation(echo, il19);
        assertEquals(before, scn.GetIconCount(echo, Icon.LIGHT_FORCE));
    }

    @Test
    public void IL19MayBePlacedInUsedPileWhenJustForfeited() {
        var scn = GetScenario();
        var il19 = scn.GetLSCard("il19");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        var rebel = scn.GetLSFiller(1);
        scn.MoveCardsToLocation(site, il19, rebel, stormtrooper, vader);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        scn.PrepareDSDestiny(6);
        scn.PrepareLSDestiny(0);
        scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        scn.SkipBattleDestinyDraws(true);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertTrue(scn.AwaitingLSAttritionPayment());
        scn.LSChooseCard(il19);
        scn.PassCardLeavingTable();
        scn.LSChooseAction("Place IL-19 in Used Pile");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        assertTrue(il19.getZone() == Zone.USED_PILE || il19.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
