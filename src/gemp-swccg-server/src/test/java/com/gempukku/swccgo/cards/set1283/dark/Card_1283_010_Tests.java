package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tatooine", "1_127"); // Tatooine system
                    put("xwing", "1_146"); // X-wing (Rebel starship)
                    put("gold1", "103_1"); // Gold Leader In Gold 1 (permanent pilot)
                }},
                new HashMap<>() {{
                    put("ig2000", "1283_10");
                    put("ig88", "200_83"); // IG-88 (V)
                    put("tie", "1_304"); // TIE Fighter
                    put("mauler", "1_168"); // Darth Vader, a non-IG-88 pilot
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
    public void IG2000VStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("ig2000").getBlueprint();
        assertEquals("IG-2000", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(5, card.getManeuver(), scn.epsilon);
        assertEquals(5, card.getHyperspeed(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPilotCapacity());
        assertEquals(2, card.getPassengerCapacity());
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.TRILON_AGGRESSOR);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.IG2000);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.INDEPENDENT);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void IG2000VMakesRebelStarshipsDeployPlusOneHere() {
        var scn = GetScenario();
        var ig2000 = scn.GetDSCard("ig2000");
        var ig88 = scn.GetDSCard("ig88");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        var qs = scn.game().getModifiersQuerying();
        float before = qs.getDeployCost(scn.game().getGameState(), xwing, xwing, tatooine, false, null, false, 0, null, false);
        scn.MoveCardsToLocation(tatooine, ig2000);
        scn.BoardAsPilot(ig2000, ig88);
        float after = qs.getDeployCost(scn.game().getGameState(), xwing, xwing, tatooine, false, null, false, 0, null, false);
        assertEquals(before + 1, after, scn.epsilon);
    }

    @Test
    public void IG2000VIsImmuneToAttritionLessThan3() {
        var scn = GetScenario();
        var ig2000 = scn.GetDSCard("ig2000");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, ig2000);
        scn.BoardAsPilot(ig2000, scn.GetDSCard("ig88"));
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.game().getGameState(), ig2000), scn.epsilon);
    }

    /** @return {Light total power, number of battle destiny Dark drew} */
    private int[] battleAtTatooine(boolean ig2000WithIG88, boolean drawDestiny) {
        var scn = GetScenario();
        var ig2000 = scn.GetDSCard("ig2000");
        var tie = scn.GetDSCard("tie");
        var vader = scn.GetDSCard("mauler");
        var gold1 = scn.GetLSCard("gold1");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        // Vader in a TIE provides the ability to draw battle destiny
        scn.MoveCardsToLocation(tatooine, tie, gold1);
        scn.BoardAsPilot(tie, vader);
        if (ig2000WithIG88) {
            scn.MoveCardsToLocation(tatooine, ig2000);
            scn.BoardAsPilot(ig2000, scn.GetDSCard("ig88"));
        }
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(tatooine);
        scn.PassAllResponses();
        scn.PrepareDSDestiny(3);
        scn.PrepareLSDestiny(3);
        scn.SkipToEndOfPowerSegment(drawDestiny);
        assertTrue(scn.IsActiveBattle());
        return new int[]{scn.GetLSTotalPower(), drawDestiny ? scn.GetDSBattleDestinyCount() : 0};
    }

    @Test
    public void IG2000VWithIG88PilotingSubtractsOneFromOpponentsTotalPowerForEachDestinyYouDraw() {
        // compared with the same battle without IG-2000
        int[] other = battleAtTatooine(false, true);
        int[] ig88 = battleAtTatooine(true, true);
        assertTrue(ig88[1] >= 1);
        assertEquals(other[0] - ig88[1], ig88[0]);
    }

    @Test
    public void IG2000VWithIG88PilotingDoesNotSubtractWhenYouDrawNoDestiny() {
        assertEquals(battleAtTatooine(false, false)[0], battleAtTatooine(true, false)[0]);
    }
}
