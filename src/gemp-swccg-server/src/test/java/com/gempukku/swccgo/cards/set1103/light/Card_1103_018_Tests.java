package com.gempukku.swccgo.cards.set1103.light;

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
import static org.junit.Assert.assertTrue;

public class Card_1103_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("red5", "1103_18");
                    put("luke", "1_19");
                    put("r2", "2_14");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
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
    public void Red5StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("red5").getBlueprint();

        assertEquals("Red 5", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getManeuver(), scn.epsilon);
        assertEquals(5, card.getHyperspeed(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPilotCapacity());
        assertEquals(1, card.getAstromechCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.X_WING);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.RED_SQUADRON);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.SCOMP_LINK);
            add(Icon.A_NEW_HOPE);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void Red5IsImmuneToAttritionLessThanSix() {
        var scn = GetScenario();
        var red5 = scn.GetLSCard("red5");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, red5);
        scn.BoardAsPilot(red5, scn.GetLSCard("luke"));
        assertEquals(6, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), red5), scn.epsilon);
    }

    @Test
    public void Red5DeploysLukeAboardFromReserveDeck() {
        var scn = GetScenario();
        var red5 = scn.GetLSCard("red5");
        var luke = scn.GetLSCard("luke");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, red5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(6);
        scn.MoveCardsToTopOfLSReserveDeck(luke);
        assertTrue(scn.LSCardActionAvailable(red5, "Deploy Luke or [A New Hope] R2-D2 aboard"));
        scn.LSUseCardAction(red5, "Deploy Luke or [A New Hope] R2-D2 aboard");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(luke);
        scn.PassAllResponses();
        assertEquals(red5, luke.getAttachedTo());
    }

    @Test
    public void Red5DeploysANHR2D2AboardFromReserveDeck() {
        var scn = GetScenario();
        var red5 = scn.GetLSCard("red5");
        var r2 = scn.GetLSCard("r2");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, red5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(6);
        scn.MoveCardsToTopOfLSReserveDeck(r2);
        assertTrue(scn.LSCardActionAvailable(red5, "Deploy Luke or [A New Hope] R2-D2 aboard"));
        scn.LSUseCardAction(red5, "Deploy Luke or [A New Hope] R2-D2 aboard");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(r2);
        scn.PassAllResponses();
        assertEquals(red5, r2.getAttachedTo());
    }

    @Test
    public void Red5CancelsAStarfightersGameTextHereDuringBattleLeavingPermanentPilot() {
        var scn = GetScenario();
        var red5 = scn.GetLSCard("red5");
        var luke = scn.GetLSCard("luke");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, red5, tie);
        scn.BoardAsPilot(red5, luke);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(system);
        scn.PassAllResponses();
        assertEquals(1, scn.GetBattleDestinyAbility(tie));
        assertTrue(scn.LSCardActionAvailable(red5, "Cancel a starfighter's game text"));
        scn.LSUseCardAction(red5, "Cancel a starfighter's game text");
        scn.LSChooseCard(tie);
        scn.PassAllResponses();
        assertEquals(1, scn.GetBattleDestinyAbility(tie));
        assertEquals(1, scn.game().getModifiersQuerying().getAstromechCapacity(scn.gameState(), red5));
    }
}
