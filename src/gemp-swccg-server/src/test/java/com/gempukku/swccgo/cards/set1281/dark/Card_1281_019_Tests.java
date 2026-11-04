package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_019_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("falcon", "1_143");
                    put("han", "1_11");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("bespin", "1281_19");
                    put("executor", "4_167");
                    put("sorry", "11_72");
                    put("plans", "601_73");
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
    public void BespinVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("bespin").getBlueprint();

        assertEquals(Title.Bespin, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getParsec());
        assertEquals(CardSubtype.SYSTEM, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PLANET);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BespinVDeploysExecutorHereFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var bespin = scn.GetDSCard("bespin");
        var executor = scn.GetDSCard("executor");
        var sorry = scn.GetDSCard("sorry");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveCardsToDSSideOfTable(sorry);
        scn.DSActivateForceCheat(10);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(bespin, "Deploy Executor from Reserve Deck"));
        scn.DSUseCardAction(bespin, "Deploy Executor from Reserve Deck");
        scn.DSChooseCard(executor);
        scn.PassAllResponses();
        assertAtLocation(bespin, executor);
        assertFalse(Vb81RestTestSupport.dsCardActionAvailable(scn, bespin, "Deploy Executor from Reserve Deck"));
    }

    @Test
    public void BespinVExecutorIsDeployEightWhileImSorryOnTableAndASithsPlansIsNot() {
        var scn = GetScenario();
        var bespin = scn.GetDSCard("bespin");
        var executor = scn.GetDSCard("executor");
        var sorry = scn.GetDSCard("sorry");
        var plans = scn.GetDSCard("plans");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        assertEquals(15, scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), executor), scn.epsilon);
        scn.MoveCardsToDSSideOfTable(sorry);
        assertEquals(8, scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), executor), scn.epsilon);
        scn.MoveCardsToDSSideOfTable(plans);
        assertEquals(15, scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), executor), scn.epsilon);
    }

    @Test
    public void BespinVMayNotBeConvertedAndIsImmuneToRevolution() {
        var scn = GetScenario();
        var bespin = scn.GetDSCard("bespin");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        assertTrue(scn.game().getModifiersQuerying().cannotBeConverted(scn.gameState(), bespin));
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), bespin, Title.Revolution));
    }

    @Test
    public void BespinVLightSideCharactersDeployMinusOneToRelatedLocationsIfTheyControl() {
        var scn = GetScenario();
        var bespin = scn.GetDSCard("bespin");
        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var leia = scn.GetLSCard("leia");
        var walkway = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        float normal = deployCostTo(scn, leia, walkway);
        scn.MoveCardsToLocation(bespin, falcon);
        scn.BoardAsPilot(falcon, han);
        assertEquals(normal - 1, deployCostTo(scn, leia, walkway), scn.epsilon);
    }

    private static float deployCostTo(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCard card, com.gempukku.swccgo.game.PhysicalCard target) {
        return scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
