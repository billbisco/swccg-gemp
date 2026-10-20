package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_058_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("obiwan", "1_21");
                }},
                new HashMap<>() {{
                    put("hunt", "1240_58");
                    put("vader", "108_006"); // Darth Vader With Lightsaber: permanent weapon Vader's Lightsaber
                    put("sniper", "2_139");
                    put("corridor", "1_284"); // Death Star: Detention Block Corridor (not a battleground)
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation, // Tatooine: Marketplace (battleground site)
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void HuntDownAndDestroyTheJediStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("hunt").getBlueprint();

        assertEquals(Title.Hunt_Down_And_Destroy_The_Jedi, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_4);
        }});
    }

    @Test
    public void TheirFireHasGoneOutOfTheUniverseStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var hunt = scn.GetDSCard("hunt");
        scn.MoveOutOfPlay(hunt);
        scn.StartGame();
        var card = hunt.getOtherSideBlueprint();

        assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(7, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_4);
        }});
    }

    /**
     * Puts the objective on Dark Side's side of table (deployment already complete), with Vader, the target and a
     * second Light Side character at the given site, and starts Dark Side's control phase with Sniper in hand.
     */
    private void setupVaderAndTargetAtSite(VirtualTableScenario scn, PhysicalCardImpl site, PhysicalCardImpl target, boolean backSideUp) {
        var hunt = scn.GetDSCard("hunt");
        var vader = scn.GetDSCard("vader");
        var sniper = scn.GetDSCard("sniper");
        scn.MoveOutOfPlay(hunt);

        scn.StartGame();
        if (site != scn.GetDSStartingLocation()) {
            scn.MoveLocationToTable(site);
        }
        scn.MoveCardsToDSSideOfTable(hunt);
        hunt.setObjectiveDeploymentComplete(true);
        if (backSideUp) {
            scn.gameState().flipCard(scn.game(), hunt, true);
        }
        // A second Light Side character, so the Sniper target is a real choice.
        scn.MoveCardsToLocation(site, vader, target, scn.GetLSFiller(2));
        scn.MoveCardsToDSHand(sniper);
        scn.SkipToPhase(Phase.CONTROL);
        scn.PrepareDSDestiny(6);
        scn.PrepareDSDestiny(7);
    }

    /**
     * Dark Side plays Sniper and Vader hits the target with Vader's Lightsaber (6 + 7 > defense value). Stops at the
     * required responses to the hit.
     */
    private void vaderSnipesTarget(VirtualTableScenario scn, PhysicalCardImpl target) {
        var vader = scn.GetDSCard("vader");
        var sniper = scn.GetDSCard("sniper");

        scn.DSPlayCard(sniper);
        scn.DSChooseCard(vader);
        scn.LSPass(); // Playing Sniper - Optional responses
        scn.DSPass();
        scn.DSChooseCard(target);
        scn.PassAllResponses();
        assertTrue(target.isHit());
        // Two required responses to the hit: the objective's 'hit character leaves table' trigger and the rule that
        // loses a character hit outside battle (no action text). Dark Side resolves the objective's trigger first.
        var requiredResponses = scn.GetDSAvailableActions();
        assertEquals(2, requiredResponses.size());
        assertTrue(requiredResponses.contains(null));
        scn.DSDecided(requiredResponses.indexOf(null) == 0 ? 1 : 0);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, target);
    }

    private void lsLosesForce(VirtualTableScenario scn, int amount, int lifeForceBefore) {
        for (int i = 0; i < amount; ++i) {
            assertTrue(scn.LSDecisionAvailable("Choose Force to lose"));
            scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
            scn.PassAllResponses();
        }
        assertFalse(scn.LSDecisionAvailable("Choose Force to lose"));
        assertEquals(amount, lifeForceBefore - scn.GetLSLifeForceRemaining());
    }

    private void jediHitAtBattlegroundLeavesTable(boolean flipFirst) {
        var scn = GetScenario();
        var hunt = scn.GetDSCard("hunt");
        var obiwan = scn.GetLSCard("obiwan");
        var site = scn.GetDSStartingLocation();
        //test1: with Obi-Wan (unique, ability 6) at a battleground site the objective has not flipped
        //test2: when Obi-Wan leaves table both required responses are offered: Flip and Lose 2 Force
        //test3: in either order, opponent loses 2 Force
        //test4: and the objective flips to Their Fire Has Gone Out Of The Universe

        setupVaderAndTargetAtSite(scn, site, obiwan, false);
        int lifeForceBefore = scn.GetLSLifeForceRemaining();
        assertEquals(Title.Hunt_Down_And_Destroy_The_Jedi, hunt.getBlueprint().getTitle()); //test1

        vaderSnipesTarget(scn, obiwan);

        var leaveTableResponses = scn.GetDSAvailableActions();
        assertEquals(2, leaveTableResponses.size()); //test2
        assertTrue(leaveTableResponses.contains("Flip")); //test2
        assertTrue(leaveTableResponses.contains("Lose 2 Force")); //test2
        scn.DSChooseAction(flipFirst ? "Flip" : "Lose 2 Force");
        scn.PassAllResponses();

        lsLosesForce(scn, 2, lifeForceBefore); //test3
        assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, hunt.getBlueprint().getTitle()); //test4
    }

    @Test
    public void HuntDownAndDestroyTheJediLosesTwoForceWhenHitJediLeavingTableAlsoFlipsItFlipResolvedFirst() {
        jediHitAtBattlegroundLeavesTable(true);
    }

    @Test
    public void HuntDownAndDestroyTheJediLosesTwoForceWhenHitJediLeavingTableAlsoFlipsItLossResolvedFirst() {
        jediHitAtBattlegroundLeavesTable(false);
    }

    @Test
    public void HuntDownAndDestroyTheJediLosesTwoForceWhenHitCharacterLeavesTableWithoutFlipping() {
        var scn = GetScenario();
        var hunt = scn.GetDSCard("hunt");
        var target = scn.GetLSFiller(1);
        var site = scn.GetDSCard("corridor");
        //test1: the hit character leaving a non-battleground site is the only required response (no flip)
        //test2: opponent loses 2 Force
        //test3: the objective stays on its front side

        setupVaderAndTargetAtSite(scn, site, target, false);
        int lifeForceBefore = scn.GetLSLifeForceRemaining();

        vaderSnipesTarget(scn, target);

        assertFalse(scn.GetDSAvailableActions().contains("Flip")); //test1
        lsLosesForce(scn, 2, lifeForceBefore); //test2
        assertEquals(Title.Hunt_Down_And_Destroy_The_Jedi, hunt.getBlueprint().getTitle()); //test3
    }

    @Test
    public void TheirFireHasGoneOutOfTheUniverseLosesTwoForceWhenHitCharacterLeavesTable() {
        var scn = GetScenario();
        var hunt = scn.GetDSCard("hunt");
        var target = scn.GetLSFiller(1);
        var site = scn.GetDSCard("corridor");
        //test1: the 'for remainder of game' clause still applies with the back side up: opponent loses 2 Force
        //test2: the objective stays on its back side

        setupVaderAndTargetAtSite(scn, site, target, true);
        assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, hunt.getBlueprint().getTitle());
        int lifeForceBefore = scn.GetLSLifeForceRemaining();

        vaderSnipesTarget(scn, target);

        lsLosesForce(scn, 2, lifeForceBefore); //test1
        assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, hunt.getBlueprint().getTitle()); //test2
    }
}
