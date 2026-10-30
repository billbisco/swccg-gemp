package com.gempukku.swccgo.cards.set1101.light;

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
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1101_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("gold1", "1101_3");
                    put("dutch", "1_8");
                    put("top", "1_19");
                    put("bottom", "1_17");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void Gold1StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("gold1").getBlueprint();

        assertEquals("Gold 1", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(3, card.getManeuver(), scn.epsilon);
        assertEquals(4, card.getHyperspeed(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(2, card.getPilotOrPassengerCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.Y_WING);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.GOLD_SQUADRON);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void Gold1HasPowerPlusTwoAndImmunityLessThanFourIfDutchPiloting() {
        var scn = GetScenario();
        var gold1 = scn.GetLSCard("gold1");
        var dutch = scn.GetLSCard("dutch");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, gold1);
        assertEquals(0, scn.GetPower(gold1));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), gold1), scn.epsilon);
        scn.BoardAsPilot(gold1, dutch);
        assertEquals(6, scn.GetPower(gold1));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), gold1), scn.epsilon);
    }

    @Test
    public void Gold1TakesTopCardOfForcePileDuringControlPhaseIfDutchPiloting() {
        var scn = GetScenario();
        var gold1 = scn.GetLSCard("gold1");
        var dutch = scn.GetLSCard("dutch");
        var top = scn.GetLSCard("top");
        var bottom = scn.GetLSCard("bottom");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, gold1);
        scn.BoardAsPilot(gold1, dutch);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.DrainLSForcePileTo(0);
        scn.MoveCardsToTopOfLSForcePile(bottom, top);
        assertTrue(scn.LSCardActionAvailable(gold1, "Take top or bottom card of Force Pile into hand"));
        scn.LSUseCardAction(gold1, "Take top or bottom card of Force Pile into hand");
        scn.LSChooseOption("Take top card of Force Pile into hand");
        scn.PassAllResponses();
        assertEquals(Zone.HAND, top.getZone());
        assertTrue(bottom.getZone() == Zone.FORCE_PILE || bottom.getZone() == Zone.TOP_OF_FORCE_PILE);
    }

    @Test
    public void Gold1TakesBottomCardOfForcePileDuringControlPhaseIfDutchPiloting() {
        var scn = GetScenario();
        var gold1 = scn.GetLSCard("gold1");
        var dutch = scn.GetLSCard("dutch");
        var top = scn.GetLSCard("top");
        var bottom = scn.GetLSCard("bottom");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, gold1);
        scn.BoardAsPilot(gold1, dutch);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.DrainLSForcePileTo(0);
        scn.MoveCardsToTopOfLSForcePile(bottom, top);
        assertTrue(scn.LSCardActionAvailable(gold1, "Take top or bottom card of Force Pile into hand"));
        scn.LSUseCardAction(gold1, "Take top or bottom card of Force Pile into hand");
        scn.LSChooseOption("Take bottom card of Force Pile into hand");
        scn.PassAllResponses();
        assertEquals(Zone.HAND, bottom.getZone());
        assertTrue(top.getZone() == Zone.FORCE_PILE || top.getZone() == Zone.TOP_OF_FORCE_PILE);
    }
}
