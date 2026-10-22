package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_1210_102_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("jek", "1210_102");
                    put("red6", "2_72");
                    put("xwing", "1_146");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
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
    public void JekPorkinsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("jek").getBlueprint();

        assertEquals("Jek Porkins", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.RED_SQUADRON);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void JekAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var jek = scn.GetLSCard("jek");
        var red6 = scn.GetLSCard("red6");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, red6);
        scn.BoardAsPilot(red6, jek);
        assertEquals(5, scn.GetPower(red6));
    }

    @Test
    public void JekPilotingRed6MakesItHeroicAndImmuneToAttritionLessThanFour() {
        var scn = GetScenario();
        var jek = scn.GetLSCard("jek");
        var red6 = scn.GetLSCard("red6");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, red6);
        scn.BoardAsPilot(red6, jek);
        assertTrue(scn.HasKeyword(red6, Keyword.HEROIC));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), red6), scn.epsilon);
    }

    @Test
    public void JekPilotingRed6DrawsOneBattleDestinyIfUnableToOtherwise() {
        var scn = GetScenario();
        var jek = scn.GetLSCard("jek");
        var red6 = scn.GetLSCard("red6");
        var tatooine = scn.GetLSCard("tatooine");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, red6, tie);
        scn.BoardAsPilot(red6, jek);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        scn.PassWeaponsSegmentActions();
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void JekUsesOneForceToRetrieveAnXwing() {
        var scn = GetScenario();
        var jek = scn.GetLSCard("jek");
        var red6 = scn.GetLSCard("red6");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, red6);
        scn.BoardAsPilot(red6, jek);
        scn.MoveCardsToTopOfLSLostPile(xwing);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(2);
        int forceBefore = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(jek, "Retrieve Rogue squadron pilot or X-Wing"));
        scn.LSUseCardAction(jek, "Retrieve Rogue squadron pilot or X-Wing");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(xwing);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, xwing.getZone());
        assertEquals(forceBefore - 1, scn.GetLSForcePileCount());
    }
}
