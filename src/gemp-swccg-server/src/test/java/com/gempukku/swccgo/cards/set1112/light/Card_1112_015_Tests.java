package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1112_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ki", "1112_15");
                    put("a", "1_2");
                    put("b", "1_3");
                    put("lost", "1_4");
                }},
                new HashMap<>() {{
                    put("maul", "11_054");
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
    public void KiAdiMundiStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("ki").getBlueprint();

        assertEquals("Ki-Adi-Mundi", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getDeployCost(), scn.epsilon);
        assertEquals(5, card.getPower(), scn.epsilon);
        assertEquals(6, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(Species.CEREAN, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.JEDI_COUNCIL_MEMBER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC);
            add(Icon.CORUSCANT);
            add(Icon.EPISODE_I);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void KiAdiMundiIsHeroicWhileMaulIsPresentAndMayExchangeCardsOncePerGame() {
        var scn = GetScenario();
        var ki = scn.GetLSCard("ki");
        var maul = scn.GetDSCard("maul");
        var a = scn.GetLSCard("a");
        var b = scn.GetLSCard("b");
        var lost = scn.GetLSCard("lost");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, ki);
        assertFalse(scn.HasKeyword(ki, Keyword.HEROIC));
        scn.MoveCardsToLocation(site, maul);
        assertTrue(scn.HasKeyword(ki, Keyword.HEROIC));

        scn.MoveCardsToLSHand(a, b);
        scn.MoveCardsToTopOfLSLostPile(lost);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(ki, "Exchange cards with card in Lost Pile"));
        scn.LSUseCardAction(ki, "Exchange cards with card in Lost Pile");
        scn.PassAllResponses();
        scn.LSChooseCard(lost);
        scn.LSChooseCard(a);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, lost.getZone());
        assertTrue(a.getZone() == Zone.LOST_PILE || a.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(b.getZone() == Zone.LOST_PILE || b.getZone() == Zone.TOP_OF_LOST_PILE);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(ki, "Exchange cards with card in Lost Pile"));
    }
}
