package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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

/**
 * A Stunning Move / A Valuable Hostage (Legacy VB7.3, 1273_20). The objective starts the game for real: the engine
 * deploys Palpatine's Quarters, Private Platform and Insidious Prisoner (on the Quarters) in the starting step.
 */
public class Card_1273_020_Tests {

    static final StartingSetup StunningMoveObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("asm", "1273_20");
                put("quarters", "1273_17");
                put("platform", "601_150"); // Coruscant: Private Platform (Docking Bay)
                put("prisoner", "1273_13");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            // Place Private Platform next to Palpatine's Quarters, then let Insidious Prisoner deploy
            LegacyVb73DarkTestSupport.resolveUntil(scn, () -> scn.GetDSCard("prisoner").getZone() == Zone.ATTACHED
                    && scn.GetAwaitingDecision(scn.DS) != null && scn.GetAwaitingDecision(scn.DS).getText().contains("Activate"));
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("obiwan", "1_21"); // Obi-Wan Kenobi (Jedi)
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194"); // Imperial
                    put("bridge", "12_164"); // Blockade Flagship: Bridge
                    put("flagship", "14_114"); // Blockade Flagship
                    put("grievous", "203_27"); // General Grievous
                    put("squad", "1273_3"); // Battle Droid Squad ([Presence] droid)
                    put("marketplace", "12_176"); // Tatooine: Marketplace (battleground)
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StunningMoveObjective,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void AStunningMoveStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("asm").getBlueprint();
        assertEquals("A Stunning Move", card.getTitle());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.EPISODE_I);
            add(Icon.THEED_PALACE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());

        var back = scn.GetDSCard("asm").getOtherSideBlueprint();
        assertEquals("A Valuable Hostage", back.getTitle());
        assertEquals(7, back.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(back, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.EPISODE_I);
            add(Icon.THEED_PALACE);
            add(Icon.LEGACY_BLOCK_7);
        }});
    }

    @Test
    public void AStunningMoveDeploysPalpatinesQuartersPrivatePlatformAndInsidiousPrisoner() {
        var scn = GetScenario();
        scn.StartGame();
        var asm = scn.GetDSCard("asm");
        var quarters = scn.GetDSCard("quarters");
        assertInZone(Zone.SIDE_OF_TABLE, asm);
        assertFalse(asm.isFlipped());
        assertEquals(Zone.LOCATIONS, quarters.getZone());
        assertEquals(Zone.LOCATIONS, scn.GetDSCard("platform").getZone());
        assertTrue(scn.IsAttachedTo(quarters, scn.GetDSCard("prisoner")));
    }

    @Test
    public void AStunningMoveMakesImperialsLostForRemainderOfGame() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSCard("quarters"), trooper);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertInZone(Zone.LOST_PILE, trooper);
    }

    @Test
    public void AStunningMoveDeploysBlockadeFlagshipSiteFromReserveDeckOncePerTurn() {
        var scn = GetScenario();
        var bridge = scn.GetDSCard("bridge");
        scn.StartGame();
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(bridge);
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("asm"), "Deploy a Blockade Flagship site"));
        scn.DSUseCardAction(scn.GetDSCard("asm"), "Deploy a Blockade Flagship site");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> bridge.getZone() == Zone.LOCATIONS);
        assertEquals(Zone.LOCATIONS, bridge.getZone());
    }

    @Test
    public void AStunningMoveDeploysGrievousForMinusTwoToASiteYouOccupyOncePerGame() {
        var scn = GetScenario();
        var grievous = scn.GetDSCard("grievous");
        var quarters = scn.GetDSCard("quarters");
        scn.StartGame();
        scn.MoveCardsToLocation(quarters, scn.GetDSCard("squad"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(grievous);
        int fullCost = (int) scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), grievous, grievous, quarters, false, null, false, 0, null, false);
        int forceBefore = scn.GetDSForcePileCount();
        scn.DSUseCardAction(scn.GetDSCard("asm"), "Deploy Grievous or Dooku");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> grievous.getZone() == Zone.AT_LOCATION);
        scn.PassAllResponses();
        assertEquals(quarters, grievous.getAtLocation());
        assertEquals(forceBefore - Math.max(0, fullCost - 2), scn.GetDSForcePileCount());
    }

    @Test
    public void AStunningMoveBlockadeFlagshipMayNotBeBlownAway() {
        var scn = GetScenario();
        var flagship = scn.GetDSCard("flagship");
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("bridge"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSSideOfTable(flagship);
        assertTrue(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(),
                com.gempukku.swccgo.logic.modifiers.ModifierType.LEGACY__MAY_NOT_BE_BLOWN_AWAY, flagship).size() == 1);
        assertTrue(com.gempukku.swccgo.logic.modifiers.MayNotBeBlownAwayModifier.mayBeBlownAway(scn.game(), java.util.List.of(flagship)).isEmpty());
        assertFalse(com.gempukku.swccgo.logic.modifiers.MayNotBeBlownAwayModifier.mayBeBlownAway(scn.game(), java.util.List.of(scn.GetDSCard("trooper"))).isEmpty());
    }

    private void moveInsidiousPrisonerToBridge(VirtualTableScenario scn) {
        var bridge = scn.GetDSCard("bridge");
        scn.MoveLocationToTable(bridge);
        scn.AttachCardsTo(bridge, scn.GetDSCard("prisoner"));
    }

    @Test
    public void AStunningMoveFlipsWhenInsidiousPrisonerIsOnBlockadeFlagshipAndTakesPresenceDroid() {
        var scn = GetScenario();
        var asm = scn.GetDSCard("asm");
        var squad = scn.GetDSCard("squad");
        scn.StartGame();
        moveInsidiousPrisonerToBridge(scn);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(asm.isFlipped());
        assertEquals("A Valuable Hostage", asm.getTitle());
    }

    @Test
    public void AValuableHostageImmediatelyTakesPresenceDroidIntoHand() {
        var scn = GetScenario();
        var asm = scn.GetDSCard("asm");
        var squad = scn.GetDSCard("squad");
        scn.StartGame();
        moveInsidiousPrisonerToBridge(scn);
        scn.MoveCardsToTopOfDSReserveDeck(squad);
        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSPass();
        LegacyVb73DarkTestSupport.passUntilAction(scn, scn.DS, "Take a [Presence] droid");
        assertTrue(asm.isFlipped());
        scn.DSChooseAction("Take a [Presence] droid");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> squad.getZone() == Zone.HAND);
        assertInZone(Zone.HAND, squad);
    }

    @Test
    public void AValuableHostageLimitsOpponentsJediImmunityToAttrition() {
        var scn = GetScenario();
        var obiwan = scn.GetLSCard("obiwan");
        scn.StartGame();
        moveInsidiousPrisonerToBridge(scn);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), obiwan);
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier(scn.GetLSStartingLocation(), obiwan, 8));
        assertEquals(8, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), obiwan), scn.epsilon);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.GetDSCard("asm").isFlipped());
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), obiwan), scn.epsilon);
    }

    @Test
    public void AValuableHostageOpponentLosesOneForcePerBattlegroundYouControlInControlPhase() {
        var scn = GetScenario();
        scn.StartGame();
        moveInsidiousPrisonerToBridge(scn);
        var marketplace = scn.GetDSCard("marketplace");
        scn.MoveLocationToTable(marketplace);
        scn.MoveCardsToLocation(marketplace, scn.GetDSCard("squad"));
        int battlegrounds = com.gempukku.swccgo.filters.Filters.countTopLocationsOnTable(scn.game(),
                com.gempukku.swccgo.filters.Filters.and(com.gempukku.swccgo.filters.Filters.battleground, com.gempukku.swccgo.filters.Filters.controls(scn.DS)));
        assertTrue(battlegrounds > 0);
        int lsLifeBefore = scn.GetLSLifeForceRemaining();
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> scn.GetCurrentPhase() == Phase.DEPLOY);
        assertTrue(scn.GetDSCard("asm").isFlipped());
        assertEquals(lsLifeBefore - battlegrounds, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void AValuableHostageRevealsForcePileCardAndOpponentLetsYouTakeIt() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        moveInsidiousPrisonerToBridge(scn);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSForcePile(trooper);
        assertTrue(scn.GetDSCard("asm").isFlipped());
        scn.DSUseCardAction(scn.GetDSCard("asm"), "Search your Force Pile and reveal a card");
        scn.DSChooseCard(trooper);
        LegacyVb73DarkTestSupport.passUntilChoice(scn, scn.LS, "Opponent takes card into hand");
        scn.LSChoose("Opponent takes card into hand");
        scn.PassAllResponses();
        assertInZone(Zone.HAND, trooper);
    }

    @Test
    public void AValuableHostageFlipsBackWhenInsidiousPrisonerNotAtBlockadeFlagshipSite() {
        var scn = GetScenario();
        var asm = scn.GetDSCard("asm");
        scn.StartGame();
        moveInsidiousPrisonerToBridge(scn);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(asm.isFlipped());
        scn.AttachCardsTo(scn.GetDSCard("quarters"), scn.GetDSCard("prisoner"));
        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(asm.isFlipped());
        assertEquals("A Stunning Move", asm.getTitle());
    }

    @Test
    public void AStunningMoveShowsTheMasterListPcE1FrontText() {
        String t = com.gempukku.swccgo.framework.VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_20").getGameText();
        org.junit.Assert.assertEquals("A Stunning Move: Deploy Palpatine's Quarters, Private Platform, and Insidious Prisoner. For remainder of game, Sidious and Imperials are lost. Once per turn, may deploy a Blockade Flagship site from Reserve Deck; reshuffle. Blockade Flagship may not be 'blown away.' While this side up, Insidious Prisoner only follows opponent's characters when moving to Palpatine's Quarters. Once per game, may deploy (for -2 Force) Dooku or Grievous to a site you occupy from Reserve Deck; reshuffle. Flip this card if Insidious Prisoner is on Blockade Flagship. A Valuable Hostage: Immediately take into hand from Reserve Deck; reshuffle (or retrieve into hand) a [Presence] droid. While this side up, immunity to attrition of opponent's Jedi, starships, and vehicles is limited to < 5. During your control phase, opponent loses 1 Force for each battleground you control. Once during your turn, may search your Force Pile and reveal any one card; reshuffle. Opponent may lose 2 Force to place that card on bottom of your Used Pile; otherwise, take it into hand. Flip this card if Insidious Prisoner not at a Blockade Flagship site.", t);
        org.junit.Assert.assertFalse(t.contains("Cyborg Commander"));
        org.junit.Assert.assertFalse(t.contains("The Count"));
    }
}
