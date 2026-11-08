package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** PC-E1 Hunt Down And Destroy The Jedi (V) (AR 2.0 Appendix A, by Aug 2012), a version of 1240_58. */
public class Card_2240_1058_Tests {
    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    protected VirtualTableScenario GetScenario() {
        var scn = newScenario();
        // Two objectives in the deck: keep both out of the starting choice (the tests place them by hand).
        scn.MoveOutOfPlay(scn.GetDSCard("hunt"));
        scn.MoveOutOfPlay(scn.GetDSCard("current"));
        return scn;
    }

    private VirtualTableScenario newScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("obiwan", "1_21");
                }},
                new HashMap<>() {{
                    put("hunt", "2240_1058");
                    put("current", "1240_58");
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
    public void FrontIsTheSameCardWithTheOldText() {
        LegacyVersionAsserts.assertVersionOf(library.getSwccgoCardBlueprint("1240_58"), library.getSwccgoCardBlueprint("2240_1058"),
                "Deploy Coruscant system, Imperial City, and A Sith's Plans. May also deploy If The Trace Was Correct.\nFor remainder of game, you may not deploy [Episode I] Dark Jedi. Opponent loses 2 Force when a character hit by Vader's (or Galen's) Lightsaber leaves table on the same turn.\nWhile this side up, may ▲ Galen's Fighter. Galen's immunity to attrition is +2 for each Jedi present.\nFlip this card if Galen or Vader at a battleground site and opponent does not have a unique (•) character of ability > 3 present at a battleground site.",
                "1240_58", "PC-E1", LocalDate.of(2012, 8, 27), LocalDate.of(2013, 4, 27));
    }

    @Test
    public void BackHasTheSameTextAsTheCurrentBack() {
        // The Virtual Cards Master List (PC-E2, the current card) did not change the back: the PC-E1 back text is the
        // current back text, so this back differs from the current back only in its version info.
        var current = library.getSwccgoCardBlueprintBack("1240_58");
        var version = library.getSwccgoCardBlueprintBack("2240_1058");
        assertEquals("While this side up, opponent's Force drain bonuses are canceled and your Force drains at battlegrounds may not be canceled. During your control phase, may retrieve 1 Force. During your control phase, may use 2 Force to ▲ any one card without ability. If targeting a vehicle or starship with Lightsaber Parry, Tarkin's Orders, or There Is No Conflict, that destiny draw is +2.\nFlip this card if opponent has a unique (•) character of ability > 3 present at a battleground site or if neither Vader nor Galen is on table.",
                version.getGameText());
        assertEquals(current.getGameText(), version.getGameText());
        assertEquals(current.getTitle(), version.getTitle());
        assertNull(current.getLegacyVersion());
        assertEquals("1240_58_BACK", version.getLegacyVersion().currentPrintingId());
        assertEquals("PC-E1", version.getLegacyVersion().label());
        assertEquals(LocalDate.of(2012, 8, 27), version.getLegacyVersion().printedOn());
        assertEquals(LocalDate.of(2013, 4, 27), version.getLegacyVersion().supersededOn());
    }

    @Test
    public void HitCharacterLeavingTableLosesTwoForce() {
        var scn = GetScenario();
        var hunt = scn.GetDSCard("hunt");
        var target = scn.GetLSFiller(1);
        var site = scn.GetDSCard("corridor");
        //test1: a character hit by Vader's Lightsaber leaves table: opponent loses 2 Force
        //test2: the objective stays on its front side (not at a battleground)
        setupVaderAndTargetAtSite(scn, "hunt", site, target, false);
        int lifeForceBefore = scn.GetLSLifeForceRemaining();
        vaderSnipesTarget(scn, target);
        assertFalse(scn.GetDSAvailableActions().contains("Flip"));
        lsLosesForce(scn, 2, lifeForceBefore); //test1
        assertEquals(Title.Hunt_Down_And_Destroy_The_Jedi, hunt.getBlueprint().getTitle()); //test2
    }

    /** Back side up with neither Vader nor Galen on table; the table changes. Returns the objective's title afterwards. */
    private String backSideTitleWithNeitherVaderNorGalen(String huntKey) {
        var scn = GetScenario();
        var hunt = scn.GetDSCard(huntKey);
        scn.MoveOutOfPlay(hunt);
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(hunt);
        hunt.setObjectiveDeploymentComplete(true);
        scn.gameState().flipCard(scn.game(), hunt, true);
        assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, hunt.getBlueprint().getTitle());
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveLocationToTable(scn.GetDSCard("corridor"));
        scn.SkipToPhase(Phase.BATTLE);
        return hunt.getBlueprint().getTitle();
    }

    @Test
    public void CurrentBackFlipsWhenNeitherVaderNorGalenIsOnTable() {
        assertEquals(Title.Hunt_Down_And_Destroy_The_Jedi, backSideTitleWithNeitherVaderNorGalen("current"));
    }

    @Test
    public void PcE1BackFlipsWhenNeitherVaderNorGalenIsOnTable() {
        assertEquals(Title.Hunt_Down_And_Destroy_The_Jedi, backSideTitleWithNeitherVaderNorGalen("hunt"));
    }

    /**
     * Puts the objective on Dark Side's side of table (deployment already complete), with Vader, the target and a
     * second Light Side character at the given site, and starts Dark Side's control phase with Sniper in hand.
     */
    private void setupVaderAndTargetAtSite(VirtualTableScenario scn, String huntKey, PhysicalCardImpl site, PhysicalCardImpl target, boolean backSideUp) {
        var hunt = scn.GetDSCard(huntKey);
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
     * Dark Side plays Sniper and Vader hits the target with Vader's Lightsaber (6 + 7 > defense value), then resolves
     * the objective's trigger to the hit before the rule that loses the hit character.
     */
    private void vaderSnipesTarget(VirtualTableScenario scn, PhysicalCardImpl target) {
        vaderSnipesTarget(scn, target, false);
    }

    /**
     * Dark Side plays Sniper and Vader hits the target with Vader's Lightsaber (6 + 7 > defense value). Dark Side then
     * picks which of the two required responses to the hit resolves first: the objective's trigger, or (ruleFirst) the
     * rule that loses a character hit outside battle.
     */
    private void vaderSnipesTarget(VirtualTableScenario scn, PhysicalCardImpl target, boolean ruleFirst) {
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
        // loses a character hit outside battle (no action text). Dark Side chooses the order.
        var requiredResponses = scn.GetDSAvailableActions();
        assertEquals(2, requiredResponses.size());
        assertTrue(requiredResponses.contains(null));
        int ruleIndex = requiredResponses.indexOf(null);
        scn.DSDecided(ruleFirst ? ruleIndex : 1 - ruleIndex);
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
}
