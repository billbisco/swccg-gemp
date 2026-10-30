package com.gempukku.swccgo.cards.set1102.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1102_019_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("trooper", "1102_19");
                    put("luke", "1_19");
                    put("rifle", "1_153");
                }},
                new HashMap<>() {{
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
    public void RebelTrooperStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("trooper").getBlueprint();

        assertEquals("Rebel Trooper", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getForfeit(), scn.epsilon);
        assertEquals(Species.ALDERAANIAN, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.TROOPER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RebelTrooperIsDestinyPlusTwoWhenDrawnForBattleDestiny() {
        var scn = GetScenario();
        var trooper = scn.GetLSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, stormtrooper);
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.MoveCardsToTopOfLSReserveDeck(trooper);
        scn.LSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        scn.SkipBattleDestinyDraws(true);
        assertEquals(3, scn.GetLSTotalDestiny());
    }

    @Test
    public void RebelTrooperArmedWithBlasterRifleAtSiteIsForfeitPlusTwoAndDrawsBattleDestiny() {
        var scn = GetScenario();
        var trooper = scn.GetLSCard("trooper");
        var rifle = scn.GetLSCard("rifle");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, stormtrooper);
        assertEquals(2, scn.GetForfeit(trooper));
        scn.AttachCardsTo(trooper, rifle);
        assertEquals(4, scn.GetForfeit(trooper));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }
}
