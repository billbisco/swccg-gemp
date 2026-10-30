package com.gempukku.swccgo.cards.set1102.dark;

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
import static org.junit.Assert.assertTrue;

public class Card_1102_045_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("han", "1_11");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1102_45");
                    put("djas", "1_171");
                    put("rifle", "1_312");
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
    public void StormtrooperStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("stormtrooper").getBlueprint();

        assertEquals("Stormtrooper", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.STORMTROOPER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void StormtrooperIsDestinyPlusTwoWhenDrawnForBattleDestiny() {
        var scn = GetScenario();
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var djas = scn.GetDSCard("djas");
        var han = scn.GetLSCard("han");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, djas, han);
        scn.EnsureDSForcePile(4);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.MoveCardsToTopOfDSReserveDeck(stormtrooper);
        scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        scn.SkipBattleDestinyDraws(true);
        assertEquals(3, scn.GetDSTotalDestiny());
    }

    @Test
    public void StormtrooperArmedWithBlasterRifleAtSiteIsForfeitPlusTwoAndDrawsBattleDestiny() {
        var scn = GetScenario();
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var rifle = scn.GetDSCard("rifle");
        var han = scn.GetLSCard("han");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, stormtrooper, han);
        assertEquals(2, scn.GetForfeit(stormtrooper));
        scn.AttachCardsTo(stormtrooper, rifle);
        assertEquals(4, scn.GetForfeit(stormtrooper));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }
}
