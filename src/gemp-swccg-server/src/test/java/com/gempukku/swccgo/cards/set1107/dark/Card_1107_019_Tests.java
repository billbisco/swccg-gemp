package com.gempukku.swccgo.cards.set1107.dark;

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

public class Card_1107_019_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("slave", "1107_19");
                    put("boba", "1220_2");
                    put("tie", "4_173");
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
    public void SlaveIStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("slave").getBlueprint();

        assertEquals("Slave I", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(4, card.getManeuver(), scn.epsilon);
        assertEquals(4, card.getHyperspeed(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.SLAVE_I);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.CLOUD_CITY);
            add(Icon.INDEPENDENT);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SlaveIWhileBobaFettPilotingManeuverPlusTwoAndImmuneToAttritionLessThanFive() {
        var scn = GetScenario();
        var slave = scn.GetDSCard("slave");
        var boba = scn.GetDSCard("boba");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, slave);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), slave), scn.epsilon);
        scn.BoardAsPilot(slave, boba);
        assertTrue(scn.IsMatchingPilot(slave, boba));
        assertEquals(6, scn.GetManeuver(slave));
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), slave), scn.epsilon);
    }

    @Test
    public void SlaveIMayDeployWithBobaFettAsAReactForFree() {
        var scn = GetScenario();
        var slave = scn.GetDSCard("slave");
        var boba = scn.GetDSCard("boba");
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing, tie);
        scn.BoardAsPilot(xwing, luke);
        scn.MoveCardsToDSHand(slave, boba);
        scn.EnsureDSForcePile(6);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        assertTrue(scn.DSDecisionAvailable("Battle just initiated")
                || scn.DSCardPlayAvailable(slave)
                || scn.DSCardActionAvailable(slave));
    }
}
