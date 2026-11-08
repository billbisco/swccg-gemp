package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Hound's Tooth (V), Virtual Block 2 #73 (blueprint 1220_73, implemented by Card601_209).
 */
public class Card_1220_073_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("red3", "1_145");
                }},
                new HashMap<>() {{
                    put("tooth", "1220_73");
                    put("bossk", "4_92");
                    put("zuckuss", "4_107");
                    put("dengar", "4_100");
                    put("imppilot", "1_180");
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
    public void HoundsToothGameTextIsOfficialText() {
        var scn = GetScenario();
        var card = scn.GetDSCard("tooth").getBlueprint();
        assertEquals("Hound's Tooth", card.getTitle());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        scn.BlueprintCardTypeCheck(card, new java.util.ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.CAPITAL, card.getCardSubtype());
        assertEquals("May add 1 alien pilot, 6 passengers, and 1 vehicle. Deploys (even using Combat Response) and moves like a starfighter. While Bossk piloting, adds one battle destiny and immune to attrition < 4.",
                card.getGameText());
        assertTrue(card.isDeploysLikeStarfighter());
        assertTrue(card.isMovesLikeStarfighter());
    }

    @Test
    public void HoundsToothMayDeployInsteadOfAStarfighterUsingCombatResponse() {
        var scn = GetScenario();
        var tooth = scn.GetDSCard("tooth");
        scn.StartGame();
        assertTrue(Filters.mayDeployInsteadOfStarfighterUsingCombatResponse.accepts(scn.game(), tooth));
    }

    @Test
    public void HoundsToothMayAddAnAlienPilotButNotANonAlienPilot() {
        var scn = GetScenario();
        var tooth = scn.GetDSCard("tooth");
        var zuckuss = scn.GetDSCard("zuckuss");
        var imppilot = scn.GetDSCard("imppilot");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tooth);
        var pilotFilter = tooth.getBlueprint().getValidPilotFilter(scn.game().getDarkPlayer(), scn.game(), tooth, true);
        assertTrue(pilotFilter.accepts(scn.game(), zuckuss));
        assertFalse(pilotFilter.accepts(scn.game(), imppilot));
    }

    @Test
    public void HoundsToothAddsBattleDestinyAndIsImmuneToAttritionLessThanFourWhileBosskPiloting() {
        var scn = GetScenario();
        var tooth = scn.GetDSCard("tooth");
        var bossk = scn.GetDSCard("bossk");
        var tatooine = scn.GetDSCard("tatooine");
        var red3 = scn.GetLSCard("red3");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tooth, red3);
        scn.BoardAsPilot(red3, luke);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), tooth), scn.epsilon);
        scn.BoardAsPilot(tooth, bossk);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), tooth), scn.epsilon);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(tatooine);
        scn.PassAllResponses();
        // Bossk alone (ability 2) gives no battle destiny; Hound's Tooth adds one
        assertEquals(1, battleDestinyDraws(scn));
    }

    @Test
    public void HoundsToothWithAnotherAlienPilotAddsNoBattleDestinyOrImmunity() {
        var scn = GetScenario();
        var tooth = scn.GetDSCard("tooth");
        var dengar = scn.GetDSCard("dengar");
        var tatooine = scn.GetDSCard("tatooine");
        var red3 = scn.GetLSCard("red3");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tooth, red3);
        scn.BoardAsPilot(red3, luke);
        scn.BoardAsPilot(tooth, dengar);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), tooth), scn.epsilon);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(tatooine);
        scn.PassAllResponses();
        assertEquals(0, battleDestinyDraws(scn));
    }

    private int battleDestinyDraws(VirtualTableScenario scn) {
        return scn.game().getModifiersQuerying().getNumBattleDestinyDraws(scn.gameState(), scn.game().getDarkPlayer(), false, false);
    }
}
