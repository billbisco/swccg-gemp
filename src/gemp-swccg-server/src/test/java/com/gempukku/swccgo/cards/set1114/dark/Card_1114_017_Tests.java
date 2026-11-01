package com.gempukku.swccgo.cards.set1114.dark;

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

public class Card_1114_017_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("ozzel", "1114_17");
                    put("devastator", "1_301");
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
    public void AdmiralOzzelStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("ozzel").getBlueprint();

        assertEquals("Admiral Ozzel", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ADMIRAL);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.OZZEL);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.HOTH);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_14, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AdmiralOzzelDeploysToStartingSite() {
        var scn = GetScenario();
        var ozzel = scn.GetDSCard("ozzel");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(ozzel);
        scn.EnsureDSForcePile(3);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(ozzel));
        scn.DSPlayCard(ozzel);
        scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, ozzel.getAtLocation());
    }

    @Test
    public void AdmiralOzzelAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var ozzel = scn.GetDSCard("ozzel");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, ozzel);
        assertEquals(11, scn.GetPower(devastator));
    }

    @Test
    public void AdmiralOzzelFirstBattleDestinyAtBattlegroundSystemMayNotBeCanceled() {
        var scn = GetScenario();
        var ozzel = scn.GetDSCard("ozzel");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator, xwing);
        scn.BoardAsPilot(devastator, ozzel);
        scn.BoardAsPilot(xwing, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(tatooine);
        scn.PassAllResponses();
        assertTrue(scn.game().getModifiersQuerying().mayNotCancelBattleDestinyDraws(
                scn.gameState(), scn.DS, scn.LS, false));
    }
}
