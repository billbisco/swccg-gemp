package com.gempukku.swccgo.cards.set1115.light;

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

public class Card_1115_030_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("farm", "1115_30");
                    put("owen", "1_022");
                    put("hydro", "1210_117");
                    put("vaporator", "1210_118");
                }},
                new HashMap<>() {{
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
    public void TatooineLarsMoistureFarmStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("farm").getBlueprint();

        assertEquals("Tatooine: Lars' Moisture Farm", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.FARM);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.SCOMP_LINK);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
}});
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TatooineLarsMoistureFarmDeploysALarsFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var farm = scn.GetLSCard("farm");
        var owen = scn.GetLSCard("owen");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(owen);

        assertTrue(scn.LSCardActionAvailable(farm, "Deploy a Lars from Reserve Deck"));
        scn.LSUseCardAction(farm, "Deploy a Lars from Reserve Deck");
        if (scn.LSHasCardChoiceAvailable(owen)) {
            scn.LSChooseCard(owen);
        }
        scn.PassAllResponses();

        assertAtLocation(farm, owen);
        if (!scn.AwaitingLSDeployPhaseActions()) {
            scn.SkipToLSTurn(Phase.DEPLOY);
        }
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(farm, "Deploy a Lars from Reserve Deck"));
    }

    @Test
    public void TatooineLarsMoistureFarmAddsForceGenerationWithLarsAndHydroponicsOrVaporator() {
        var scn = GetScenario();
        var farm = scn.GetLSCard("farm");
        var owen = scn.GetLSCard("owen");
        var hydro = scn.GetLSCard("hydro");
        var vaporator = scn.GetLSCard("vaporator");

        scn.StartGame();
        scn.MoveLocationToTable(farm);

        float base = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), farm, scn.LS);

        scn.MoveCardsToLocation(farm, owen);
        scn.AttachCardsTo(farm, hydro);
        float withHydro = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), farm, scn.LS);
        assertEquals(base + 1, withHydro, scn.epsilon);

        scn.AttachCardsTo(farm, vaporator);
        float withBoth = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), farm, scn.LS);
        assertEquals(base + 2, withBoth, scn.epsilon);
    }

    protected VirtualTableScenario GetNotusttScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("farm", "1115_30");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                NotusttObjective,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    static final StartingSetup NotusttObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("notustt", "1210_68");
                put("tatooine", "1_289");
                put("devastator", "1_301");
                put("moseisley", "1_295");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            for (int i = 0; i < 8; i++) {
                if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("moseisley"))) {
                    scn.DSChooseCard(scn.GetDSCard("moseisley"));
                    continue;
                }
                if (scn.DSDecisionAvailable("On which side")) {
                    scn.DSChoose("Left");
                    continue;
                }
                break;
            }
        }
    };

    @Test
    public void TatooineLarsMoistureFarmMakesImperialsDeployMinusOneWhileNoOneToStopUsThisTimeOnTable() {
        var scn = GetNotusttScenario();
        var farm = scn.GetLSCard("farm");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var notustt = scn.GetDSCard("notustt");

        scn.StartGame();
        scn.MoveLocationToTable(farm);

        float before = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), stormtrooper, stormtrooper, farm, false, null, false, 0, null, false);

        scn.gameState().flipCard(scn.game(), notustt, true);
        assertEquals(Title.No_One_To_Stop_Us_This_Time, notustt.getTitle());

        float after = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), stormtrooper, stormtrooper, farm, false, null, false, 0, null, false);
        assertEquals(before - 1, after, scn.epsilon);
    }
}
