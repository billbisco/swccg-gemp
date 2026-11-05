package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.MayBeFiredTwicePerBattleModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_012_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("obiwan", "1_21");
                    put("saber", "1_157");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("maze", "1272_12");
                    put("bossk", "4_92");
                    put("trandoshan", "6_126");
                    put("trooper", "1_194");
                }},
                10,
                10,
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
    public void KashyyykForestMazeStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("maze").getBlueprint();

        assertEquals("Kashyyyk: Forest Maze", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ENDOR);
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.EPISODE_I);
            add(Icon.DARK_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void KashyyykForestMazeOpponentLosesOneForceWhenYouOccupyWithTwoTrandoshans() {
        var scn = GetScenario();
        var maze = scn.GetDSCard("maze");
        var bossk = scn.GetDSCard("bossk");
        var trandoshan = scn.GetDSCard("trandoshan");

        //test1: during your control phase, two Trandoshans here make opponent lose 1 Force
        //test2: only once per control phase
        scn.StartGame();
        scn.MoveLocationToTable(maze);
        scn.MoveCardsToLocation(maze, bossk, trandoshan);
        scn.SkipToDSTurn(Phase.CONTROL);

        int lsLifeBefore = scn.GetLSLifeForceRemaining();
        assertTrue(scn.DSCardActionAvailable(maze, "Make opponent lose 1 Force")); //test1
        scn.DSUseCardAction(maze, "Make opponent lose 1 Force");
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassAllResponses();
        assertEquals(lsLifeBefore - 1, scn.GetLSLifeForceRemaining()); //test1

        scn.LSPass();
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.DSCardActionAvailable(maze, "Make opponent lose 1 Force")); //test2
    }

    @Test
    public void KashyyykForestMazeNeedsAtLeastTwoTrandoshans() {
        var scn = GetScenario();
        var maze = scn.GetDSCard("maze");
        var bossk = scn.GetDSCard("bossk");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(maze);
        scn.MoveCardsToLocation(maze, bossk, trooper);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.DSCardActionAvailable(maze, "Make opponent lose 1 Force"));
    }

    @Test
    public void KashyyykForestMazeForceLossOnlyDuringYourControlPhase() {
        var scn = GetScenario();
        var maze = scn.GetDSCard("maze");
        var bossk = scn.GetDSCard("bossk");
        var trandoshan = scn.GetDSCard("trandoshan");

        scn.StartGame();
        scn.MoveLocationToTable(maze);
        scn.MoveCardsToLocation(maze, bossk, trandoshan);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(maze, "Make opponent lose 1 Force"));
    }

    @Test
    public void KashyyykForestMazeJediDeployPlusOneUnlessLightControls() {
        var scn = GetScenario();
        var maze = scn.GetDSCard("maze");
        var obiwan = scn.GetLSCard("obiwan");
        var luke = scn.GetLSCard("luke");

        //test1: Jedi deploy +1 here while Light Side does not control
        //test2: no extra cost once Light Side controls
        scn.StartGame();
        scn.MoveLocationToTable(maze);
        float printed = obiwan.getBlueprint().getDeployCost();
        assertEquals(printed + 1, deployCostTo(scn, obiwan, maze), scn.epsilon); //test1

        scn.MoveCardsToLocation(maze, luke);
        assertEquals(printed, deployCostTo(scn, obiwan, maze), scn.epsilon); //test2
    }

    @Test
    public void KashyyykForestMazeJediDeployPlusOneThroughRealDeploy() {
        var scn = GetScenario();
        var maze = scn.GetDSCard("maze");
        var obiwan = scn.GetLSCard("obiwan");

        scn.StartGame();
        scn.MoveLocationToTable(maze);
        scn.MoveCardsToLSHand(obiwan);
        scn.EnsureLSForcePile(10);
        scn.SkipToLSTurn(Phase.DEPLOY);

        int forceBefore = scn.GetLSForcePileCount();
        scn.LSDeployCard(obiwan);
        scn.LSChooseCard(maze);
        scn.PassAllResponses();
        assertEquals(maze, obiwan.getAtLocation());
        assertEquals(forceBefore - 6, scn.GetLSForcePileCount());
    }

    @Test
    public void KashyyykForestMazeLightsabersSwingOnlyOncePerBattleUnlessLightControls() {
        var scn = GetScenario();
        var maze = scn.GetDSCard("maze");
        var obiwan = scn.GetLSCard("obiwan");
        var saber = scn.GetLSCard("saber");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        //test1: a lightsaber that may swing twice may swing only once at the Forest Maze
        //test2: the limit is off once Light Side controls
        //test3: elsewhere the lightsaber may still swing twice
        scn.StartGame();
        scn.MoveLocationToTable(maze);
        scn.MoveCardsToLocation(maze, obiwan, trooper);
        scn.AttachCardsTo(obiwan, saber);
        scn.ApplyAdHocModifier(new MayBeFiredTwicePerBattleModifier(saber, Filters.sameCardId(saber)));

        assertFalse(scn.game().getModifiersQuerying().mayBeFiredTwicePerBattle(scn.gameState(), saber)); //test1

        scn.MoveCardsToLocation(maze, luke);
        scn.MoveCardsToLocation(site, trooper);
        assertTrue(scn.game().getModifiersQuerying().mayBeFiredTwicePerBattle(scn.gameState(), saber)); //test2

        scn.MoveCardsToLocation(site, obiwan, luke);
        assertTrue(scn.game().getModifiersQuerying().mayBeFiredTwicePerBattle(scn.gameState(), saber)); //test3
    }


    private float deployCostTo(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
