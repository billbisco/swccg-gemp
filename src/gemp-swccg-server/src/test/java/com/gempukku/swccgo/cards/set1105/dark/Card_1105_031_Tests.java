package com.gempukku.swccgo.cards.set1105.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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

public class Card_1105_031_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("jarjar", "11_4");
                    put("leia", "1_17");
                    put("smuggler", "7_38");
                    put("smuggler2", "7_38");
                }},
                new HashMap<>() {{
                    put("derdram", "1105_31");
                    put("devastator", "1_301");
                    put("tatooine", "1_289");
                    put("corridor", "4_162");
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
    public void CorporalDerdramStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("derdram").getBlueprint();

        assertEquals("Corporal Derdram", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.DAGOBAH);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CorporalDerdramAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var derdram = scn.GetDSCard("derdram");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, derdram);
        assertEquals(11, scn.GetPower(devastator));
    }

    @Test
    public void CorporalDerdramPreventsOpponentDeployingJarJarOrSpiesAtSameNonDockingBaySite() {
        var scn = GetScenario();
        var derdram = scn.GetDSCard("derdram");
        var jarjar = scn.GetLSCard("jarjar");
        var leia = scn.GetLSCard("leia");
        var corridor = scn.GetDSCard("corridor");

        scn.StartGame();
        scn.MoveLocationToTable(corridor);
        scn.MoveCardsToLocation(corridor, derdram);
        scn.MoveCardsToLSHand(jarjar, leia);
        scn.EnsureLSForcePile(6);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(jarjar));
        scn.LSPlayCard(jarjar);
        assertFalse(scn.LSHasCardChoiceAvailable(corridor));
        scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        assertTrue(jarjar.getAtLocation() != corridor);
        int safety = 0;
        while (safety++ < 12 && scn.DSGetDecision() != null
                && (scn.LSGetDecision() == null || scn.LSGetADParam("cardId") == null)) {
            scn.DSPass();
        }
        if (scn.LSGetDecision() != null && scn.LSGetADParam("cardId") != null
                && scn.LSCardPlayAvailable(leia)) {
            scn.LSPlayCard(leia);
            assertFalse(scn.LSHasCardChoiceAvailable(corridor));
        }
    }
}
