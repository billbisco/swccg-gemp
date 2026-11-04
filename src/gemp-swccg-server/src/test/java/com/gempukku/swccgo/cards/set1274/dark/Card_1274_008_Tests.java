package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("shadows", "1274_8");
                    put("secretplans", "7_240");  // Secret Plans
                    put("square", "7_278");       // Coruscant: Imperial Square
                    put("emperor", "9_109");      // Emperor Palpatine
                    put("moseisley", "1_295");
                    put("jundland", "1_293");
                    put("trooper", "1_194");
                    put("trooper2", "1_194");
                    put("trooper3", "1_194");
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

    // Stand-in for the Agents Of Black Sun Objective (the real Objective changes the test rig's game start)
    private static AbstractNormalEffect agentsOfBlackSun() {
        return new AbstractNormalEffect(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Agents_Of_Black_Sun, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
        };
    }

    private static boolean dsCan(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        try {
            return scn.DSCardActionAvailable(card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @Test
    public void ShadowsOfTheEmpireStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("shadows").getBlueprint();

        assertEquals("Shadows Of The Empire", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ShadowsOfTheEmpireDeploysOnAgentsOfBlackSunAndMistrylAreLost() {
        var scn = GetScenario();
        var shadows = scn.GetDSCard("shadows");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        var agents = MissionTestSupport.putOnTable(scn, 9781, scn.DS, agentsOfBlackSun());
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), luke);
        scn.ApplyAdHocModifier(new KeywordModifier(luke, Filters.sameCardId(luke), Keyword.MISTRYL));
        scn.MoveCardsToDSHand(shadows);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(shadows));
        scn.DSPlayCard(shadows);
        if (scn.DSDecisionAvailable("Choose")) {
            scn.DSChooseCard(agents);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(agents, shadows));
        assertInZone(Zone.LOST_PILE, luke);
    }

    @Test
    public void ShadowsOfTheEmpireOncePerGameTakesSecretPlansIntoHand() {
        var scn = GetScenario();
        var shadows = scn.GetDSCard("shadows");
        var secretplans = scn.GetDSCard("secretplans");

        scn.StartGame();
        var agents = MissionTestSupport.putOnTable(scn, 9781, scn.DS, agentsOfBlackSun());
        scn.AttachCardsTo(agents, shadows);
        scn.MoveCardsToBottomOfDSReserveDeck(secretplans);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(shadows, "Take Secret Plans"));
        scn.DSUseCardAction(shadows, "Take Secret Plans");
        scn.DSChooseCard(secretplans);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, secretplans);
        assertFalse(dsCan(scn, shadows, "Take Secret Plans"));
    }

    @Test
    public void ShadowsOfTheEmpireMayUseThreeForceToDeployImperialSquare() {
        var scn = GetScenario();
        var shadows = scn.GetDSCard("shadows");
        var square = scn.GetDSCard("square");

        scn.StartGame();
        MissionTestSupport.ensurePlanetLayout(scn, Title.Coruscant, 0);
        var agents = MissionTestSupport.putOnTable(scn, 9781, scn.DS, agentsOfBlackSun());
        scn.AttachCardsTo(agents, shadows);
        scn.EnsureDSForcePile(5);
        scn.MoveCardsToBottomOfDSReserveDeck(square);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(shadows, "Deploy Imperial Square"));
        scn.DSUseCardAction(shadows, "Deploy Imperial Square");
        scn.PassAllResponses();
        scn.DSChooseCard(square);
        scn.PassAllResponses();
        assertTrue(square.getZone().isInPlay());
        assertTrue(scn.GetDSForcePileCount() <= forceBefore - 3);
    }

    @Test
    public void ShadowsOfTheEmpireOncePerTurnDrawsFromForcePileIfEmperorAtImperialSquare() {
        var scn = GetScenario();
        var shadows = scn.GetDSCard("shadows");
        var square = scn.GetDSCard("square");
        var emperor = scn.GetDSCard("emperor");

        scn.StartGame();
        MissionTestSupport.ensurePlanetLayout(scn, Title.Coruscant, 0);
        scn.MoveLocationToTable(square);
        var agents = MissionTestSupport.putOnTable(scn, 9781, scn.DS, agentsOfBlackSun());
        scn.AttachCardsTo(agents, shadows);
        scn.EnsureDSForcePile(5);
        scn.MoveCardsToLocation(square, emperor);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int handBefore = scn.GetDSHandCount();
        int lsLostBefore = scn.GetLSLostPileCount();
        assertTrue(scn.DSCardActionAvailable(shadows, "Draw a card from Force Pile"));
        scn.DSUseCardAction(shadows, "Draw a card from Force Pile");
        scn.PassAllResponses();
        assertEquals(handBefore + 1, scn.GetDSHandCount());
        assertEquals(lsLostBefore, scn.GetLSLostPileCount());
        assertFalse(dsCan(scn, shadows, "Draw a card from Force Pile"));
    }

    @Test
    public void ShadowsOfTheEmpireMayNotDrawFromForcePileWithoutEmperorAtImperialSquare() {
        var scn = GetScenario();
        var shadows = scn.GetDSCard("shadows");
        var square = scn.GetDSCard("square");
        var emperor = scn.GetDSCard("emperor");

        scn.StartGame();
        MissionTestSupport.ensurePlanetLayout(scn, Title.Coruscant, 0);
        scn.MoveLocationToTable(square);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), emperor);
        var agents = MissionTestSupport.putOnTable(scn, 9781, scn.DS, agentsOfBlackSun());
        scn.AttachCardsTo(agents, shadows);
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(shadows, "Draw a card from Force Pile"));
    }

    @Test
    public void ShadowsOfTheEmpireDrawFromForcePileMakesOpponentLoseOneForceIfYouOccupyThreeBattlegrounds() {
        var scn = GetScenario();
        var shadows = scn.GetDSCard("shadows");
        var square = scn.GetDSCard("square");
        var emperor = scn.GetDSCard("emperor");
        var moseisley = scn.GetDSCard("moseisley");
        var jundland = scn.GetDSCard("jundland");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        var trooper3 = scn.GetDSCard("trooper3");

        scn.StartGame();
        MissionTestSupport.ensurePlanetLayout(scn, Title.Coruscant, 0);
        scn.MoveLocationToTable(square);
        scn.MoveLocationToTable(moseisley);
        scn.MoveLocationToTable(jundland);
        scn.MoveCardsToLocation(square, emperor);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), trooper);
        scn.MoveCardsToLocation(moseisley, trooper2);
        scn.MoveCardsToLocation(jundland, trooper3);
        var agents = MissionTestSupport.putOnTable(scn, 9781, scn.DS, agentsOfBlackSun());
        scn.AttachCardsTo(agents, shadows);
        scn.EnsureDSForcePile(5);
        scn.EnsureLSForcePile(5);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int lsLostBefore = scn.GetLSLostPileCount();
        scn.DSUseCardAction(shadows, "Draw a card from Force Pile");
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose Force to lose")) {
            scn.LSPayRemainingForceLossFromReserveDeck();
            scn.PassAllResponses();
        }
        assertEquals(lsLostBefore + 1, scn.GetLSLostPileCount());
    }
}
