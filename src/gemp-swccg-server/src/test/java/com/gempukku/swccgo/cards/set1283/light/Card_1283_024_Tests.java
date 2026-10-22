package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.common.CardSubtype;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_024_Tests {
    StartingSetup HiddenBaseOutsideDeck = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("hiddenbase", "1240_107");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("quarter", "1283_24");
                    put("hls", "1240_102");
                    put("tat", "1_127");
                    put("boshek", "1_4");
                    put("jawa", "1_12");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                HiddenBaseOutsideDeck,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void RebelCellRefugeeQuarterStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("quarter").getBlueprint();

        assertEquals("Rebel Cell - Refugee Quarter", card.getTitle());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PLANET);
            add(Icon.EXTERIOR_SITE);
            add(Icon.UNDERGROUND);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RebelCellRefugeeQuarterDeploysToSamePlanetAsHiddenLandingSite() {
        var scn = GetScenario();
        var quarter = scn.GetLSCard("quarter");
        var hls = scn.GetLSCard("hls");

        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("tat"));
        scn.MoveCardsToLSHand(hls, quarter);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(hls));
        scn.LSDeployLocation(hls);
        scn.PassAllResponses();
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertEquals(Zone.LOCATIONS, hls.getZone());

        assertTrue(scn.LSDeployAvailable(quarter));
        scn.LSDeployCard(quarter);
        assertTrue(scn.LSHasCardChoiceAvailable(hls));
        scn.LSChooseCard(hls);
        assertTrue(scn.LSDecisionAvailable("On which side"));
        scn.LSChoose("Left");
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, quarter.getZone());
        assertEquals("Tatooine", quarter.getPartOfSystem());
    }

    @Test
    public void RebelCellRefugeeQuarterUniqueAliensAreDeployMinus1WhileVirtualHiddenBaseOnTable() {
        var scn = GetScenario();
        var quarter = scn.GetLSCard("quarter");
        var hls = scn.GetLSCard("hls");
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var boshek = scn.GetLSCard("boshek");

        scn.StartGame();
        scn.MoveLocationToTable(hls);
        scn.MoveLocationToTable(quarter);
        scn.MoveCardsToLSSideOfTable(hiddenbase);
        scn.MoveCardsToLSHand(boshek);
        scn.SkipToLSTurn(Phase.DEPLOY);

        int printed = Math.round(boshek.getBlueprint().getDeployCost());
        assertEquals(printed - 1, scn.GetDeployCost(boshek));
    }

    @Test
    public void RebelCellRefugeeQuarterNonUniqueAliensAreNotDeployMinus1() {
        var scn = GetScenario();
        var quarter = scn.GetLSCard("quarter");
        var hls = scn.GetLSCard("hls");
        var hiddenbase = scn.GetLSCard("hiddenbase");
        var jawa = scn.GetLSCard("jawa");

        scn.StartGame();
        scn.MoveLocationToTable(hls);
        scn.MoveLocationToTable(quarter);
        scn.MoveCardsToLSSideOfTable(hiddenbase);
        scn.MoveCardsToLSHand(jawa);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertEquals(Uniqueness.UNRESTRICTED, jawa.getBlueprint().getUniqueness());
        assertEquals(1, scn.GetDeployCost(jawa));
    }

    @Test
    public void RebelCellRefugeeQuarterUniqueAliensAreNotDiscountedWithoutHiddenBase() {
        var scn = GetScenario();
        var quarter = scn.GetLSCard("quarter");
        var hls = scn.GetLSCard("hls");
        var boshek = scn.GetLSCard("boshek");

        scn.StartGame();
        scn.MoveLocationToTable(hls);
        scn.MoveLocationToTable(quarter);
        scn.MoveCardsToLSHand(boshek);
        scn.SkipToLSTurn(Phase.DEPLOY);

        int printed = Math.round(boshek.getBlueprint().getDeployCost());
        assertEquals(printed, scn.GetDeployCost(boshek));
    }
}
