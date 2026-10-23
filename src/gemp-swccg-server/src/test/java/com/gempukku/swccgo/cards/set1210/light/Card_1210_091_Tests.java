package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_091_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("bron", "1210_91");
                    put("rifle", "1_153");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                    put("workshop", "6_163");
                    put("db94", "1_285");
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
    public void BronBursStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("bron").getBlueprint();

        assertEquals("Bron Burs", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BronBursDrawsOneBattleDestinyIfUnableToOtherwise() {
        var scn = GetScenario();
        var bron = scn.GetLSCard("bron");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bron, stormtrooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void BronBursCancelsDarkSideGameTextAtOpponentsNonDockingBaySite() {
        var scn = GetScenario();
        var bron = scn.GetLSCard("bron");
        var workshop = scn.GetDSCard("workshop");

        scn.StartGame();
        scn.MoveLocationToTable(workshop);
        assertFalse(scn.HasKeyword(workshop, Keyword.DOCKING_BAY));
        assertTrue(scn.game().getModifiersQuerying().getCardsMarkingGameTextCanceledForPlayer(scn.gameState(), workshop, scn.DS).isEmpty());
        scn.MoveCardsToLocation(workshop, bron);
        assertTrue(scn.game().getModifiersQuerying().getCardsMarkingGameTextCanceledForPlayer(scn.gameState(), workshop, scn.DS).contains(bron));
    }

    @Test
    public void BronBursDoesNotCancelGameTextAtDockingBay() {
        var scn = GetScenario();
        var bron = scn.GetLSCard("bron");
        var db94 = scn.GetDSCard("db94");

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(db94, bron);
        assertTrue(scn.HasKeyword(db94, Keyword.DOCKING_BAY));
        assertTrue(scn.game().getModifiersQuerying().getCardsMarkingGameTextCanceledForPlayer(scn.gameState(), db94, scn.DS).isEmpty());
    }
}
