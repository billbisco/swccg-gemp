package com.gempukku.swccgo.cards.set1240.light;

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

public class Card_1240_065_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("nqa", "1240_65");
                    put("tat", "1_127");
                    put("xwing", "1_146");
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
    public void NoQuestionsAskedStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("nqa").getBlueprint();

        assertEquals("No Questions Asked", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ADMIRALS_ORDER);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ADMIRALS_ORDER);
            add(Icon.REFLECTIONS_II);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void NoQuestionsAskedDeploysWhileYouOccupyABattlegroundSystem() {
        var scn = GetScenario();
        var nqa = scn.GetLSCard("nqa");
        var tat = scn.GetLSCard("tat");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveLocationToTable(tat);
        scn.MoveCardsToLocation(tat, xwing);
        scn.MoveCardsToLSHand(nqa);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(nqa));
        scn.LSPlayCard(nqa);
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, nqa.getZone());
    }

    @Test
    public void NoQuestionsAskedAddsForceGenerationAtOccupiedSystems() {
        var scn = GetScenario();
        var nqa = scn.GetLSCard("nqa");
        var tat = scn.GetLSCard("tat");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveLocationToTable(tat);
        scn.MoveCardsToLocation(tat, xwing);
        scn.MoveCardsToLSSideOfTable(nqa);

        float gen = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), tat, scn.LS);
        assertEquals(3, Math.round(gen));
    }

    @Test
    public void NoQuestionsAskedCorellianSmugglerPilotedStarshipIsPowerPlus2ImmuneAndOutriderExcepted() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("nqa", "1240_65");
                    put("xwing", "1_146");
                    put("han", "1_11");
                    put("outrider", "10_17");
                    put("corellia", "2_61");
                    put("city", "7_125");
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
        var nqa = scn.GetLSCard("nqa");
        var xwing = scn.GetLSCard("xwing");
        var han = scn.GetLSCard("han");
        var outrider = scn.GetLSCard("outrider");
        var corellia = scn.GetLSCard("corellia");
        var city = scn.GetLSCard("city");

        scn.StartGame();
        scn.MoveLocationToTable(corellia);
        scn.MoveLocationToTable(city);
        scn.MoveCardsToLocation(corellia, xwing);
        scn.BoardAsPilot(xwing, han);

        int xwingBefore = scn.GetPower(xwing);
        scn.MoveCardsToLSSideOfTable(nqa);
        assertEquals(xwingBefore + 3, scn.GetPower(xwing));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), xwing), scn.epsilon);

        scn.BoardAsPilot(outrider, han);
        scn.MoveCardsToLocation(corellia, outrider);
        int outriderPower = scn.GetPower(outrider);
        scn.MoveOutOfPlay(nqa);
        assertEquals(outriderPower, scn.GetPower(outrider));
    }

    @Test
    public void NoQuestionsAskedForceDrainsPlus1AtRelatedBattlegroundSitesWhileYouOccupyCorellia() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("nqa", "1240_65");
                    put("falcon", "1_143");
                    put("han", "1_11");
                    put("corellia", "2_61");
                    put("city", "7_125");
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
        var nqa = scn.GetLSCard("nqa");
        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var corellia = scn.GetLSCard("corellia");
        var city = scn.GetLSCard("city");

        scn.StartGame();
        scn.MoveLocationToTable(corellia);
        var cityPlacement = scn.gameState().getLocationPlacement(scn.game(), city, "Corellia", null);
        scn.MoveLocationToTable(city, cityPlacement.getFirst());
        scn.MoveCardsToLSSideOfTable(nqa);

        float withoutOccupy = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), city, scn.LS);
        scn.MoveCardsToLocation(corellia, falcon);
        scn.BoardAsPilot(falcon, han);
        float withOccupy = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), city, scn.LS);
        assertEquals(Math.round(withoutOccupy) + 1, Math.round(withOccupy));
    }
}
