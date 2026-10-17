package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.common.Agenda;
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

public class Card_1230_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("aloo", "1230_18");
                    put("evax", "1240_12");
                    put("room", "1240_51");
                    put("bay", "1_291");
                }},
                10,
                10,
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
    public void SimAlooStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("aloo").getBlueprint();

        assertEquals("Sim Aloo", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(3, card.getInfluence(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.DEATH_STAR_II);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SimAlooHasLawAndRuleAgendasAndAttritionImmunity() {
        var scn = GetScenario();
        var aloo = scn.GetDSCard("aloo");
        var room = scn.GetDSCard("room");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToLocation(room, aloo);

        assertTrue(scn.game().getModifiersQuerying().hasAgenda(scn.gameState(), aloo, Agenda.LAW));
        assertTrue(scn.game().getModifiersQuerying().hasAgenda(scn.gameState(), aloo, Agenda.RULE));
        assertFalse(scn.game().getModifiersQuerying().hasAgenda(scn.gameState(), aloo, Agenda.LOGISTICS));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), aloo), scn.epsilon);
        assertEquals(3, scn.game().getModifiersQuerying().getInfluence(scn.gameState(), aloo), scn.epsilon);
    }

    @Test
    public void SimAlooAddsDarkForceIconOnlyWhileAtConferenceRoom() {
        var scn = GetScenario();
        var aloo = scn.GetDSCard("aloo");
        var room = scn.GetDSCard("room");
        var bay = scn.GetDSCard("bay");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveLocationToTable(bay);
        assertEquals(0, scn.GetDSIconsOnLocation(room));
        assertEquals(0, scn.GetIconCount(room, Icon.DARK_FORCE));

        scn.MoveCardsToLocation(room, aloo);
        assertEquals(1, scn.GetIconCount(room, Icon.DARK_FORCE));

        scn.MoveCardsToLocation(bay, aloo);
        assertEquals(0, scn.GetIconCount(room, Icon.DARK_FORCE));
    }

    @Test
    public void SimAlooAddsBattleDestinyOnlyWhileWithCouncilMember() {
        var scn = GetScenario();
        var aloo = scn.GetDSCard("aloo");
        var evax = scn.GetDSCard("evax");
        var luke = scn.GetLSCard("luke");
        var bay = scn.GetDSCard("bay");

        scn.StartGame();
        scn.MoveLocationToTable(bay);
        scn.MoveCardsToLocation(bay, aloo, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(bay);
        assertEquals(1, scn.game().getModifiersQuerying().getNumBattleDestinyDraws(scn.gameState(), scn.DS, false, true));

        scn.MoveCardsToLocation(bay, evax);
        assertTrue(scn.HasKeyword(evax, Keyword.IMPERIAL_COUNCIL_MEMBER));
        assertEquals(2, scn.game().getModifiersQuerying().getNumBattleDestinyDraws(scn.gameState(), scn.DS, false, true));
    }
}
