package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_032_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("charts", "1281_32");
                    put("rest", "1281_39");
                    put("street", "1281_40");
                    put("undercity", "1281_41");
                    put("narshaddaa", "200_58");
                    put("lando", "601_50");
                    put("leia", "1_17");
                    put("han", "1_11");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("trooper2", "1_194");
                    put("trooper3", "1_194");
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
    public void ImperialNavigationChartsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("charts").getBlueprint();

        assertEquals("Imperial Navigation Charts", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ImperialNavigationChartsDeploysOnANarShaddaaBattlegroundSite() {
        var scn = GetScenario();
        var charts = scn.GetLSCard("charts");
        var rest = scn.GetLSCard("rest");
        var undercity = scn.GetLSCard("undercity");
        var narshaddaa = scn.GetLSCard("narshaddaa");

        scn.StartGame();
        scn.MoveLocationToTable(narshaddaa);
        scn.MoveLocationToTable(rest);
        scn.MoveLocationToTable(undercity);
        scn.MoveCardsToLSHand(charts);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(charts));
        scn.LSDeployCard(charts);
        assertTrue(scn.LSHasCardChoiceAvailable(rest));
        assertFalse(scn.LSHasCardChoiceAvailable(undercity));
        scn.LSChooseCard(rest);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(rest, charts));
    }

    @Test
    public void ImperialNavigationChartsStealsWhenYouControlThreeNarShaddaaLocations() {
        var scn = GetScenario();
        var charts = scn.GetLSCard("charts");
        var rest = scn.GetLSCard("rest");
        var street = scn.GetLSCard("street");
        var undercity = scn.GetLSCard("undercity");
        var narshaddaa = scn.GetLSCard("narshaddaa");
        var lando = scn.GetLSCard("lando");
        var leia = scn.GetLSCard("leia");
        var han = scn.GetLSCard("han");

        scn.StartGame();
        scn.MoveLocationToTable(narshaddaa);
        scn.MoveLocationToTable(rest);
        scn.MoveLocationToTable(street);
        scn.MoveLocationToTable(undercity);
        scn.AttachCardsTo(rest, charts);
        scn.MoveCardsToLocation(rest, lando);
        scn.MoveCardsToLocation(street, leia);
        scn.MoveCardsToLocation(undercity, han);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardActionAvailable(charts, "Steal"));
        scn.LSUseCardAction(charts, "Steal");
        scn.PassAllResponses();

        assertEquals(Zone.SIDE_OF_TABLE, charts.getZone());
        assertEquals(scn.LS, charts.getOwner());
    }

    @Test
    public void ImperialNavigationChartsDoesNotGoToUsedPileWhileStillAttached() {
        var scn = GetScenario();
        var charts = scn.GetLSCard("charts");
        var rest = scn.GetLSCard("rest");
        var street = scn.GetLSCard("street");
        var undercity = scn.GetLSCard("undercity");
        var narshaddaa = scn.GetLSCard("narshaddaa");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        var trooper3 = scn.GetDSCard("trooper3");

        scn.StartGame();
        scn.MoveLocationToTable(narshaddaa);
        scn.MoveLocationToTable(rest);
        scn.MoveLocationToTable(street);
        scn.MoveLocationToTable(undercity);
        scn.AttachCardsTo(rest, charts);
        scn.MoveCardsToLocation(rest, trooper);
        scn.MoveCardsToLocation(street, trooper2);
        scn.MoveCardsToLocation(undercity, trooper3);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertEquals(Zone.ATTACHED, charts.getZone());
        assertTrue(scn.DSCardActionAvailable(charts, "Steal"));
        assertFalse(scn.DSDecisionAvailable("Place in Used Pile"));
        assertFalse(scn.LSDecisionAvailable("Place in Used Pile"));
    }

    @Test
    public void ImperialNavigationChartsGoesToUsedPileAfterStealIfOpponentControlsThree() {
        var scn = GetScenario();
        var charts = scn.GetLSCard("charts");
        var rest = scn.GetLSCard("rest");
        var street = scn.GetLSCard("street");
        var undercity = scn.GetLSCard("undercity");
        var narshaddaa = scn.GetLSCard("narshaddaa");
        var lando = scn.GetLSCard("lando");
        var leia = scn.GetLSCard("leia");
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        var trooper3 = scn.GetDSCard("trooper3");

        scn.StartGame();
        scn.MoveLocationToTable(narshaddaa);
        scn.MoveLocationToTable(rest);
        scn.MoveLocationToTable(street);
        scn.MoveLocationToTable(undercity);
        scn.AttachCardsTo(rest, charts);
        scn.MoveCardsToLocation(rest, lando);
        scn.MoveCardsToLocation(street, leia);
        scn.MoveCardsToLocation(undercity, han);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSUseCardAction(charts, "Steal");
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, charts.getZone());

        var lsSite = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(lsSite, lando, leia, han);
        scn.MoveCardsToLocation(rest, trooper);
        scn.MoveCardsToLocation(street, trooper2);
        scn.MoveCardsToLocation(undercity, trooper3);
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        scn.LSChooseAction("Place in Used Pile");
        scn.PassAllResponses();

        assertInZone(Zone.USED_PILE, charts);
    }
}
