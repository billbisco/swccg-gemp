package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

public class Card_1220_076_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("xwing", "1_146");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("punishing", "1220_76");
                    put("dengar", "1220_10");
                    put("zuckuss", "1220_19");
                    put("tatooine", "1_289");
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
    public void PunishingOneStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("punishing").getBlueprint();

        assertEquals("Punishing One", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(5, card.getManeuver(), scn.epsilon);
        assertEquals(3, card.getHyperspeed(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.PUNISHING_ONE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.DAGOBAH);
            add(Icon.INDEPENDENT);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void PunishingOneIsImmuneToAttritionLessThanFourEvenWithoutDengar() {
        var scn = GetScenario();
        var punishing = scn.GetDSCard("punishing");
        var zuckuss = scn.GetDSCard("zuckuss");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, punishing);
        scn.BoardAsPilot(punishing, zuckuss);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), punishing), scn.epsilon);
    }

    @Test
    public void PunishingOneDuringBattleIfDengarPilotingAttritionAgainstOpponentIsPlusThree() {
        var scn = GetScenario();
        var punishing = scn.GetDSCard("punishing");
        var dengar = scn.GetDSCard("dengar");
        var xwing = scn.GetLSCard("xwing");
        var luke = scn.GetLSCard("luke");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, punishing, xwing);
        scn.BoardAsPilot(punishing, dengar);
        scn.BoardAsPilot(xwing, luke);
        assertTrue(scn.IsMatchingPilot(punishing, dengar));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(0);
        scn.DSInitiateBattle(tatooine);
        scn.SkipToDamageSegment(true);
        assertEquals(3, scn.GetUnpaidLSAttrition());
    }

    @Test
    public void PunishingOneWithoutDengarDoesNotAddAttrition() {
        var scn = GetScenario();
        var punishing = scn.GetDSCard("punishing");
        var zuckuss = scn.GetDSCard("zuckuss");
        var xwing = scn.GetLSCard("xwing");
        var luke = scn.GetLSCard("luke");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, punishing, xwing);
        scn.BoardAsPilot(punishing, zuckuss);
        scn.BoardAsPilot(xwing, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(0);
        scn.DSInitiateBattle(tatooine);
        scn.SkipToDamageSegment(true);
        assertEquals(0, scn.GetUnpaidLSAttrition());
    }
}
