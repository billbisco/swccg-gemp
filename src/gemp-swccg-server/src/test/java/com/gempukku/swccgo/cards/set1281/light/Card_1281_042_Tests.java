package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_042_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("camp", "1281_42");
                    put("jawa", "1_12");
                    put("het", "1281_29");
                    put("desert", "6_85");
                }},
                new HashMap<>() {{
                    put("dsjawa", "1_182");
                    put("stormtrooper", "1_194");
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
    public void TatooineJawaCampVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("camp").getBlueprint();

        assertEquals(Title.Jawa_Camp, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TatooineJawaCampVYourNonUniqueJawasDeployForOneForceFromYouOnly() {
        var scn = GetScenario();
        var camp = scn.GetLSCard("camp");
        var jawa = scn.GetLSCard("jawa");
        var het = scn.GetLSCard("het");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        float hetCost = deployCostTo(scn, het, marketplace);
        assertTrue(jawa.getBlueprint().isDeployUsingBothForcePiles());
        scn.MoveLocationToTable(camp);
        assertEquals(1, deployCostTo(scn, jawa, marketplace), scn.epsilon);
        assertEquals(hetCost, deployCostTo(scn, het, marketplace), scn.epsilon);
        assertFalse(scn.game().getModifiersQuerying().isDeployUsingBothForcePiles(scn.gameState(), jawa, marketplace));
    }

    @Test
    public void TatooineJawaCampVYourDesertsGainOneLightSideForceIcon() {
        var scn = GetScenario();
        var camp = scn.GetLSCard("camp");
        var desert = scn.GetLSCard("desert");

        scn.StartGame();
        scn.MoveLocationToTable(desert);
        int icons = scn.game().getModifiersQuerying().getIconCount(scn.gameState(), desert, Icon.LIGHT_FORCE);
        scn.MoveLocationToTable(camp);
        assertEquals(icons + 1, scn.game().getModifiersQuerying().getIconCount(scn.gameState(), desert, Icon.LIGHT_FORCE));
    }

    @Test
    public void TatooineJawaCampVIfDarkSideControlsLightJawasArePowerAndForfeitMinusOne() {
        var scn = GetScenario();
        var camp = scn.GetLSCard("camp");
        var jawa = scn.GetLSCard("jawa");
        var dsjawa = scn.GetDSCard("dsjawa");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var walkway = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(walkway, jawa, dsjawa);
        int power = scn.GetPower(jawa);
        int forfeit = scn.GetForfeit(jawa);
        int dsPower = scn.GetPower(dsjawa);
        scn.MoveCardsToLocation(camp, stormtrooper);
        assertEquals(power - 1, scn.GetPower(jawa));
        assertEquals(forfeit - 1, scn.GetForfeit(jawa));
        assertEquals(dsPower, scn.GetPower(dsjawa));
    }

    private static float deployCostTo(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCard card, com.gempukku.swccgo.game.PhysicalCard target) {
        return scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
