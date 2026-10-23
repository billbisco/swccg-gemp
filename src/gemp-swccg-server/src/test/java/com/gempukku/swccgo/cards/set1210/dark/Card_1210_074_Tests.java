package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_1210_074_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("outrider", "10_17");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("black3", "1210_74");
                    put("ds613", "1_174");
                    put("tie", "1_304");
                    put("tallon", "1_270");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void Black3StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("black3").getBlueprint();

        assertEquals("Black 3", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertNull(card.getArmor());
        assertEquals(3, card.getManeuver(), scn.epsilon);
        assertNull(card.getHyperspeed());
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPilotCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.TIE_LN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.NO_HYPERDRIVE);
            add(Keyword.BLACK_SQUADRON);
            add(Keyword.HEROIC);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void Black3IsHeroic() {
        var scn = GetScenario();
        var black3 = scn.GetDSCard("black3");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, black3);
        assertTrue(scn.HasKeyword(black3, Keyword.HEROIC));
    }

    @Test
    public void Black3LetsTallonRollTreatRebelAsIndependentStarfighter() {
        var scn = GetScenario();
        var black3 = scn.GetDSCard("black3");
        var ds613 = scn.GetDSCard("ds613");
        var tallon = scn.GetDSCard("tallon");
        var outrider = scn.GetLSCard("outrider");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, black3, outrider);
        scn.BoardAsPilot(black3, ds613);
        scn.MoveCardsToDSHand(tallon);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardPlayAvailable(tallon));
        scn.DSPlayCard(tallon);
        assertTrue(scn.DSHasCardChoiceAvailable(black3));
        scn.DSChooseCard(black3);
        assertTrue(scn.DSHasCardChoiceAvailable(outrider));
    }

    @Test
    public void Black3TallonRollDoesNotLetANormalTIETargetAnIndependentStarfighter() {
        var scn = GetScenario();
        var tie = scn.GetDSCard("tie");
        var tallon = scn.GetDSCard("tallon");
        var outrider = scn.GetLSCard("outrider");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, tie, outrider);
        scn.MoveCardsToDSHand(tallon);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(tallon));
    }

    @Test
    public void Black3IsImmuneToAttritionLessThanFiveWhileDS613Piloting() {
        var scn = GetScenario();
        var black3 = scn.GetDSCard("black3");
        var ds613 = scn.GetDSCard("ds613");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, black3);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), black3), scn.epsilon);
        scn.BoardAsPilot(black3, ds613);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), black3), scn.epsilon);
    }
}
