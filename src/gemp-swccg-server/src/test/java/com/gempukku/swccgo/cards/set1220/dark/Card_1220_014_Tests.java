package com.gempukku.swccgo.cards.set1220.dark;

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

public class Card_1220_014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("lando", "1220_14");
                    put("devastator", "1_301");
                    put("tatooine", "1_289");
                    put("trooper", "1_194");
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
    public void LandoCalrissianStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("lando").getBlueprint();

        assertEquals("Lando Calrissian", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.GAMBLER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.LANDO);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.CLOUD_CITY);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LandoCalrissianAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var lando = scn.GetDSCard("lando");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, lando);
        assertEquals(12, scn.GetPower(devastator));
    }

    @Test
    public void LandoCalrissianAddsOneToJustDrawnBattleDestiny() {
        var scn = GetScenario();
        var lando = scn.GetDSCard("lando");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando, trooper, luke);
        scn.EnsureDSForcePile(2);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.PrepareDSDestiny(3);
        scn.DSChooseYes();
        int safety = 0;
        while (safety++ < 16 && !scn.GetDSAvailableActions().toString().contains("Add 1 to destiny")) {
            if (scn.DSDecisionAvailable("optional") || scn.DSDecisionAvailable("Optional")
                    || (scn.DSGetDecision() != null && scn.GetDSAvailableActions().isEmpty())) {
                scn.DSPass();
            } else if (scn.LSDecisionAvailable("optional") || scn.LSDecisionAvailable("Optional")
                    || scn.LSGetDecision() != null) {
                scn.LSPass();
            } else {
                break;
            }
        }
        assertTrue("Lando add destiny missing. decision=" + scn.DSGetDecision()
                        + " actions=" + scn.GetDSAvailableActions(),
                scn.GetDSAvailableActions().toString().contains("Add 1 to destiny"));
        scn.DSChooseAction("Add 1 to destiny");
        scn.PassAllResponses();
        assertEquals(4, scn.GetDSTotalDestiny());
    }
}
