package com.gempukku.swccgo.cards.set1291.dark;

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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_030_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("xwing1", "1_146");
                    put("xwing2", "1_146");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("sailer", "1291_30");
                    put("dooku", "601_181");
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
    public void DookusSolarSailerStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("sailer").getBlueprint();

        assertEquals("Dooku's Solar Sailer", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getManeuver(), scn.epsilon);
        assertEquals(6, card.getHyperspeed(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPilotCapacity());
        assertEquals(2, card.getPassengerCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.PUNWORCCA_116_CLASS_SLOOP);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.TRADE_FEDERATION);
            add(Icon.SEPARATIST);
            add(Icon.PILOT);
            add(Icon.NAV_COMPUTER);
            add(Icon.EPISODE_I);
            add(Icon.PRESENCE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DookusSolarSailerIsPilotedByItsPermanentPilot() {
        var scn = GetScenario();
        var sailer = scn.GetDSCard("sailer");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, sailer);
        // [Pilot] icon with no stated ability: a permanent (droid) pilot of ability 0, as GEMP's other droid-piloted starfighters
        assertTrue(Filters.piloted.accepts(scn.game(), sailer));
        assertEquals(0, scn.GetDSAbilityAtLocation(tatooine));
        assertEquals(3, scn.GetPower(sailer));
    }

    @Test
    public void DookusSolarSailerDookuDeploysFreeAboard() {
        var scn = GetScenario();
        var sailer = scn.GetDSCard("sailer");
        var dooku = scn.GetDSCard("dooku");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, sailer);
        scn.MoveCardsToDSHand(dooku);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.DSDeployAvailable(dooku));
        scn.DSDeployCard(dooku);
        assertTrue(scn.DSHasCardChoiceAvailable(sailer));
        scn.DSChooseCard(sailer);
        assertTrue(scn.DSDecisionAvailable("Choose capacity slot"));
        scn.DSChoose("Passenger");
        scn.PassAllResponses();
        assertTrue(scn.IsAboard(sailer, dooku));
        assertEquals(forceBefore, scn.GetDSForcePileCount());
    }

    @Test
    public void DookusSolarSailerImmuneToAttritionOnlyWhileDookuAboard() {
        var scn = GetScenario();
        var sailer = scn.GetDSCard("sailer");
        var dooku = scn.GetDSCard("dooku");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, sailer);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), sailer), scn.epsilon);
        scn.BoardAsPassenger(sailer, dooku);
        assertEquals(Float.MAX_VALUE, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), sailer), scn.epsilon);
    }

    @Test
    public void DookusSolarSailerOpponentMayNotInitiateBattleWithOnePilotedStarshipWhileDookuAboard() {
        var scn = GetScenario();
        var sailer = scn.GetDSCard("sailer");
        var dooku = scn.GetDSCard("dooku");
        var xwing1 = scn.GetLSCard("xwing1");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, sailer, xwing1);
        scn.BoardAsPassenger(sailer, dooku);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertFalse(scn.LSCanInitiateBattle(tatooine));
    }

    @Test
    public void DookusSolarSailerOpponentMayInitiateBattleWithTwoPilotedStarshipsWhileDookuAboard() {
        var scn = GetScenario();
        var sailer = scn.GetDSCard("sailer");
        var dooku = scn.GetDSCard("dooku");
        var xwing1 = scn.GetLSCard("xwing1");
        var xwing2 = scn.GetLSCard("xwing2");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, sailer, xwing1, xwing2);
        scn.BoardAsPassenger(sailer, dooku);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle(tatooine));
    }

    @Test
    public void DookusSolarSailerDoesNotRestrictBattleWithoutDookuAboard() {
        var scn = GetScenario();
        var sailer = scn.GetDSCard("sailer");
        var xwing1 = scn.GetLSCard("xwing1");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, sailer, xwing1);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle(tatooine));
    }
}
