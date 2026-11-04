package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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

public class Card_1281_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("trooper", "1_28");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("lair", "1281_22");
                    put("coruscant", "7_275");
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
    public void CoruscantSubCityLairStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("lair").getBlueprint();

        assertEquals("Coruscant: Sub City Lair", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.UNDERGROUND);
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
    public void CoruscantSubCityLairLightForceDrainIsMinusOneUnlessTheirSpyPresent() {
        var scn = GetScenario();
        var lair = scn.GetDSCard("lair");
        var coruscant = scn.GetDSCard("coruscant");
        var trooper = scn.GetLSCard("trooper");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.MoveLocationToTable(lair);
        scn.MoveCardsToLocation(lair, trooper);
        assertEquals(1, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), lair, scn.LS), scn.epsilon);
        scn.MoveCardsToLocation(lair, leia);
        assertEquals(2, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), lair, scn.LS), scn.epsilon);
    }

    @Test
    public void CoruscantSubCityLairRelocatesAnAssassinToASiteYouOccupyAsARegularMove() {
        var scn = GetScenario();
        var lair = scn.GetDSCard("lair");
        var coruscant = scn.GetDSCard("coruscant");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.MoveLocationToTable(lair);
        var assassin = MissionTestSupport.putInReserve(scn, 98301, scn.DS, Vb81RestTestSupport.assassin("Test Assassin"));
        scn.MoveCardsToLocation(lair, assassin);
        scn.MoveCardsToLocation(marketplace, stormtrooper);
        scn.DSActivateForceCheat(3);
        scn.SkipToDSTurn(Phase.MOVE);

        assertTrue(scn.DSCardActionAvailable(lair, "Relocate an assassin as a regular move"));
        scn.DSUseCardAction(lair, "Relocate an assassin as a regular move");
        if (scn.DSDecisionAvailable("Choose assassin")) {
            scn.DSChooseCard(assassin);
        }
        if (scn.DSDecisionAvailable("Choose site")) {
            scn.DSChooseCard(marketplace);
        }
        scn.PassAllResponses();
        assertAtLocation(marketplace, assassin);
        assertFalse(Vb81RestTestSupport.dsCardActionAvailable(scn, lair, "Relocate an assassin as a regular move"));
    }

    @Test
    public void CoruscantSubCityLairDoesNotRelocateOutsideTheMovePhase() {
        var scn = GetScenario();
        var lair = scn.GetDSCard("lair");
        var coruscant = scn.GetDSCard("coruscant");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.MoveLocationToTable(lair);
        var assassin = MissionTestSupport.putInReserve(scn, 98302, scn.DS, Vb81RestTestSupport.assassin("Test Assassin"));
        scn.MoveCardsToLocation(lair, assassin);
        scn.MoveCardsToLocation(marketplace, stormtrooper);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertFalse(scn.DSCardActionAvailable(lair, "Relocate an assassin as a regular move"));
    }
}
