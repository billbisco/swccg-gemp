package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1104_011_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("uprising", "1104_11");
                    put("sentry", "7_64");
                }},
                new HashMap<>() {{
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
    public void LocalUprisingStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("uprising").getBlueprint();

        assertEquals("Local Uprising", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.OBJECTIVE);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LocalUprisingMayNotDeployHothSentry() {
        var scn = GetScenario();
        var uprising = scn.GetLSCard("uprising");
        var sentry = scn.GetLSCard("sentry");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(uprising);
        scn.MoveCardsToLSHand(sentry);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(sentry) || scn.LSDeployAvailable(sentry));
    }

    private VirtualTableScenario FormationScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("uprising", "1104_11");
                    put("formation", "7_107");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("garrison", "104_7");
                }},
                30, 30,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private void PlayFormationToCancelWalkerGarrison(VirtualTableScenario scn, boolean uprisingOnTable) {
        var formation = scn.GetLSCard("formation");
        var garrison = scn.GetDSCard("garrison");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(garrison);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToLSHand(formation);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(5);
        // Without Main Power Generators on table, Local Uprising would leave play during the turn: put it there now
        // (its while-in-play modifiers are registered when it enters play, so mark its deployment complete first)
        if (uprisingOnTable) {
            scn.GetLSCard("uprising").setObjectiveDeploymentComplete(true);
            scn.MoveCardsToLSSideOfTable(scn.GetLSCard("uprising"));
        }
        assertEquals(uprisingOnTable ? CardSubtype.OUT_OF_PLAY : CardSubtype.LOST,
                scn.game().getModifiersQuerying().getInterruptType(scn.gameState(), formation));
        assertTrue(scn.LSCardPlayAvailable(formation, "Cancel Walker Garrison"));
        scn.LSPlayCard(formation, "Cancel Walker Garrison");
        if (scn.LSHasCardChoiceAvailable(garrison))
            scn.LSChooseCard(garrison);
        scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(garrison));
    }

    @Test
    public void LocalUprisingMakesT47BattleFormationAnOutOfPlayInterrupt() {
        // "T-47 Battle Formation is an Out Of Play Interrupt": placed out of play when completed, not lost.
        var scn = FormationScenario();
        var formation = scn.GetLSCard("formation");
        PlayFormationToCancelWalkerGarrison(scn, true);
        assertEquals(Zone.OUT_OF_PLAY, formation.getZone());
    }

    @Test
    public void WithoutLocalUprisingT47BattleFormationIsLost() {
        var scn = FormationScenario();
        var formation = scn.GetLSCard("formation");
        PlayFormationToCancelWalkerGarrison(scn, false);
        assertTrue(scn.GetLSLostPile().contains(formation));
    }
}
