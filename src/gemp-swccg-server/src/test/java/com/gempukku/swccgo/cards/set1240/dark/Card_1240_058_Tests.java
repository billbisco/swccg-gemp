package com.gempukku.swccgo.cards.set1240.dark;

import static org.junit.Assert.assertNull;
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

    @Test
    public void HitCharacterLostByRuleBeforeObjectiveTriggerStillLosesTwoForce() {
        var scn = GetScenario();
        var hunt = scn.GetDSCard("hunt");
        var target = scn.GetLSFiller(1);
        var site = scn.GetDSCard("corridor");
        //test1: Sniper hit outside battle: Dark Side resolves the 'hit character is lost' rule first; the target is lost
        //test2: the objective's trigger still resolves afterwards and opponent loses 2 Force (Bill 2026-11-07: both apply)
        //test3: the objective stays on its front side

        setupVaderAndTargetAtSite(scn, site, target, false);
        int lifeForceBefore = scn.GetLSLifeForceRemaining();

        vaderSnipesTarget(scn, target, true); //test1

        lsLosesForce(scn, 2, lifeForceBefore); //test2
        assertEquals(Title.Hunt_Down_And_Destroy_The_Jedi, hunt.getBlueprint().getTitle()); //test3
    }

    @Test
    public void TheirFireHasGoneOutOfTheUniverseHitCharacterLostByRuleFirstStillLosesTwoForce() {
        var scn = GetScenario();
        var hunt = scn.GetDSCard("hunt");
        var target = scn.GetLSFiller(1);
        var site = scn.GetDSCard("corridor");
        //test1: back side up, rule resolved first: the target is lost and opponent still loses 2 Force

        setupVaderAndTargetAtSite(scn, site, target, true);
        int lifeForceBefore = scn.GetLSLifeForceRemaining();

        vaderSnipesTarget(scn, target, true);

        lsLosesForce(scn, 2, lifeForceBefore); //test1
        assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, hunt.getBlueprint().getTitle());
    }

    @Test
    public void HitJediLostByRuleFirstAtBattlegroundFlipsAndStillLosesTwoForce() {
        var scn = GetScenario();
        var hunt = scn.GetDSCard("hunt");
        var obiwan = scn.GetLSCard("obiwan");
        var site = scn.GetDSStartingLocation();
        //test1: rule resolved first: Obi-Wan is lost, the objective flips
        //test2: and opponent still loses 2 Force (exactly 2, not 4)

        setupVaderAndTargetAtSite(scn, site, obiwan, false);
        int lifeForceBefore = scn.GetLSLifeForceRemaining();

        vaderSnipesTarget(scn, obiwan, true);

        lsLosesForce(scn, 2, lifeForceBefore); //test2
        assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, hunt.getBlueprint().getTitle()); //test1
    }

    @Test
    public void WithoutObjectiveSniperHitOnlyLosesTheCharacter() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var sniper = scn.GetDSCard("sniper");
        var target = scn.GetLSFiller(1);
        var site = scn.GetDSCard("corridor");
        //test1: no Hunt Down And Destroy The Jedi (V) on table: the hit character is lost by rule (only required response)
        //test2: opponent loses no Force

        scn.MoveOutOfPlay(scn.GetDSCard("hunt"));
        scn.StartGame();
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, vader, target, scn.GetLSFiller(2));
        scn.MoveCardsToDSHand(sniper);
        scn.SkipToPhase(Phase.CONTROL);
        scn.PrepareDSDestiny(6);
        scn.PrepareDSDestiny(7);
        int lifeForceBefore = scn.GetLSLifeForceRemaining();

        scn.DSPlayCard(sniper);
        scn.DSChooseCard(vader);
        scn.LSPass();
        scn.DSPass();
        scn.DSChooseCard(target);
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, target); //test1
        assertFalse(scn.LSDecisionAvailable("Choose Force to lose")); //test2
        assertEquals(lifeForceBefore, scn.GetLSLifeForceRemaining()); //test2
    }

    @Test
    public void HitInBattleCharacterStaysOnTableAndLosesTwoForceWhenForfeited() {
        var scn = GetScenario();
        var hunt = scn.GetDSCard("hunt");
        var vader = scn.GetDSCard("vader");
        var target = scn.GetLSFiller(1);
        var site = scn.GetDSCard("corridor");
        //test1: hit by Vader's Lightsaber in battle: the target is not lost right away (the outside-battle rule does not apply)
        //test2: no Force lost yet
        //test3: once the hit target leaves table later in the battle (forfeited to battle damage), opponent loses 2 Force

        setupVaderAndTargetAtSite(scn, site, target, false);
        scn.EnsureDSForcePile(3);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(6);
        scn.PrepareDSDestiny(7);
        scn.DSUseCardAction(vader, "Fire Vader's Lightsaber");
        scn.DSChooseCard(target);
        scn.PassAllResponses();

        assertTrue(target.isHit());
        assertTrue(target.getZone().isInPlay()); //test1
        int lifeForceBefore = scn.GetLSLifeForceRemaining();
        assertFalse(scn.LSDecisionAvailable("Choose Force to lose")); //test2
        assertEquals(Title.Hunt_Down_And_Destroy_The_Jedi, hunt.getBlueprint().getTitle());

        // Damage segment: Light Side forfeits the hit target to battle damage, so it leaves table.
        scn.SkipToDamageSegment(false);
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSChooseCard(target);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, target);
        for (int i = 0; i < 2; ++i) {
            assertTrue(scn.LSDecisionAvailable("Choose Force to lose")); //test3
            assertFalse(scn.LSDecisionAvailable("card from battle to forfeit")); // the objective's loss, not battle damage
            scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
            scn.PassAllResponses();
        }
        assertEquals(2, lifeForceBefore - scn.GetLSLifeForceRemaining()); //test3
        assertTrue(scn.AwaitingLSBattleDamagePayment()); // back to the remaining battle damage
    }

    @Test
    public void HuntDownAndDestroyTheJediHasTheMasterListText() {
        var scn = GetScenario();
        var front = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1240_58");
        var back = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprintBack("1240_58");
        assertEquals("Deploy Coruscant system, Imperial City, and A Sith's Plans. May also deploy Your Spies Have Located A Jedi?.\nFor remainder of game, you may not deploy [Episode I] Dark Jedi. Opponent loses 2 Force when a character hit by Vader's (or Galen's) Lightsaber leaves table on the same turn.\nWhile this side up, may ▲ Rogue Shadow. Galen's immunity to attrition is +2 for each Jedi present.\nFlip this card if Galen or Vader at a battleground site and opponent does not have a unique (•) character of ability > 3 present at a battleground site.",
                front.getGameText());
        assertEquals("While this side up, opponent's Force drain bonuses are canceled and your Force drains at battlegrounds may not be canceled. During your control phase, may retrieve 1 Force. During your control phase, may use 2 Force to ▲ any one card without ability. If targeting a vehicle or starship with Lightsaber Parry, Tarkin's Orders, or There Is No Conflict, that destiny draw is +2.\nFlip this card if opponent has a unique (•) character of ability > 3 present at a battleground site or if neither Vader nor Galen is on table.",
                back.getGameText());
        assertNull(front.getLegacyVersion());
        assertNull(back.getLegacyVersion());
        // GEMP's old text (no published version): If The Trace Was Correct, "Whenever a character hit ...", no back flip
        // when neither Vader nor Galen is on table
        assertFalse(front.getGameText().contains("If The Trace Was Correct"));
        assertFalse(front.getGameText().contains("Whenever a character hit"));
    }

    @Test
    public void HuntDownAndDestroyTheJediMayAlsoDeployOnlyYourSpiesHaveLocatedAJedi() {
        // The Master List names "Your Spies Have Located A Jedi?" (no card has that title), not If The Trace Was Correct,
        // which the older versions 2240_58 and 2240_1058 keep.
        assertEquals("Your Spies Have Located A Jedi?", com.gempukku.swccgo.cards.set601.dark.Card601_087.YOUR_SPIES_HAVE_LOCATED_A_JEDI);
    }

    private VirtualTableScenario forceUnleashedBattle(boolean objectiveBackSideUp) {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("han", "1_11");
                    put("crawler", "1_150"); // Sandcrawler (vehicle)
                }},
                new HashMap<>() {{
                    put("hunt", "1240_58");
                    put("effect", "1240_41"); // The Force Unleashed
                    put("vader", "1_168");
                    put("trooper", "1_181");
                    put("parry", "11_83"); // Lightsaber Parry
                }},
                12, 12,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.DeclineLSStartingInterrupt,
                StartingSetup.DeclineDSStartingInterrupt,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var hunt = scn.GetDSCard("hunt");
        scn.MoveOutOfPlay(hunt);
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        if (objectiveBackSideUp) {
            scn.MoveCardsToDSSideOfTable(hunt);
            hunt.setObjectiveDeploymentComplete(true);
            scn.gameState().flipCard(scn.game(), hunt, true);
        }
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("effect"));
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader"), scn.GetDSCard("trooper"), scn.GetLSCard("han"), scn.GetLSCard("crawler"));
        scn.MoveCardsToDSHand(scn.GetDSCard("parry"));
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        return scn;
    }

    private boolean throwsTheSandcrawlerWithDestiny(boolean objectiveBackSideUp, int destiny) {
        var scn = forceUnleashedBattle(objectiveBackSideUp);
        if (objectiveBackSideUp)
            assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, scn.GetDSCard("hunt").getBlueprint().getTitle());
        var crawler = scn.GetLSCard("crawler");
        scn.PrepareDSDestiny(destiny);
        scn.DSUseCardAction(scn.GetDSCard("parry"), "USED: Throw a vehicle or starship");
        scn.DSChooseCard(crawler);
        for (int i = 0; i < 100; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("optional response")) break;
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        return crawler.isHit();
    }

    @Test
    public void TheirFireHasGoneOutOfTheUniverseAddsTwoToLightsaberParryDestinyTargetingAVehicle() {
        //test1: The Force Unleashed's 'throw' needs destiny > 2: a destiny of 1 fails without the objective
        //test2: with Their Fire Has Gone Out Of The Universe up, that destiny draw is +2 (1 + 2 = 3 > 2): thrown
        assertFalse(throwsTheSandcrawlerWithDestiny(false, 1)); //test1
        assertTrue(throwsTheSandcrawlerWithDestiny(true, 1)); //test2
    }
}
