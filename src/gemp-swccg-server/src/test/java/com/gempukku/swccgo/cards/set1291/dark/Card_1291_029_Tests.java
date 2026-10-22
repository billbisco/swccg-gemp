package com.gempukku.swccgo.cards.set1291.dark;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Separatist Uprising / At War With Itself (Legacy VB9.1, 1291_29).
 *
 * The objective starts the game for real: {@link #SeparatistUprisingObjective} puts the objective, Geonosis:
 * Separatist Council Room (1291_19) and War Has Begun (1291_11) in the Dark Side deck and the engine deploys all
 * three during the starting-cards phase.
 *
 * The "remainder of game" trigger and both flips are required triggers that fire on any table change; the tests
 * drive them with the end of a phase (both players passing), which is a table change in GEMP.
 */
public class Card_1291_029_Tests {

    /**
     * Separatist Uprising and the two cards it deploys. Both "Deploy Separatist Council Room" and "Deploy War Has
     * Begun" have exactly one matching card in Reserve Deck, so the engine deploys them without asking anything
     * (probed: no "On which side" or card-choice decision is raised). Setup therefore asserts that the deployment
     * happened instead of answering decisions that never come.
     */
    static final StartingSetup SeparatistUprisingObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("uprising", "1291_29"); // Separatist Uprising / At War With Itself
                put("council", "1291_19"); // Geonosis: Separatist Council Room
                put("war", "1291_11"); // War Has Begun
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            assertFalse("Objective could not find Separatist Council Room in Reserve Deck",
                    scn.DSDecisionAvailable("Verify Reserve Deck after unsuccessful attempt to 'Choose Separatist Council Room to deploy'"));
            assertFalse("Objective could not find War Has Begun in Reserve Deck",
                    scn.DSDecisionAvailable("Verify Reserve Deck after unsuccessful attempt to 'Choose War Has Begun to deploy'"));
            assertInZone(Zone.SIDE_OF_TABLE, scn.GetDSCard("uprising"));
            assertInZone(Zone.LOCATIONS, scn.GetDSCard("council"));
            assertInZone(Zone.SIDE_OF_TABLE, scn.GetDSCard("war"));
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19"); // Luke Skywalker: power 3, ability 4, forfeit 7
                }},
                new HashMap<>() {{
                    put("defense", "1291_27"); // Defense Of Muunilinst ([Separatist] Mission, deploys on War Has Begun)
                    put("junk", "12_181"); // any card to stack on the Mission to make it 'complete'
                    put("vader", "1_168"); // Darth Vader (Imperial)
                    put("tie", "1_304"); // Black 2 (Imperial starship)
                    put("geonosis", "1291_15"); // Geonosis system ([Episode I] battleground)
                    put("muunilinst", "1291_20"); // Muunilinst system ([Episode I] battleground)
                    put("dooku", "200_76"); // Count Dooku ([Separatist], power 5, ability 7)
                    put("keder", "12_109"); // Keder The Black (Republic warrior, power 4, ability 3, forfeit 4)
                    put("rifle", "1_312"); // Blaster Rifle (character weapon, no printed forfeit)
                    put("saber", "200_142"); // Dooku's Lightsaber
                    put("eweb", "3_159"); // E-web Blaster (artillery weapon, printed forfeit 1)
                    put("lost1", "12_182");
                    put("lost2", "12_183");
                    put("lost3", "3_156");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                SeparatistUprisingObjective,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void SeparatistUprisingStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("uprising").getBlueprint();

        assertEquals("Separatist Uprising", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.OBJECTIVE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AtWarWithItselfStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        scn.StartGame();
        var card = scn.GetDSCard("uprising").getOtherSideBlueprint();

        assertEquals("At War With Itself", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(7, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.OBJECTIVE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    // ----------------------------------------------------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------------------------------------------------

    /**
     * True if the game log contains a message with all of the given fragments (card links are stripped).
     */
    private static boolean messageSent(VirtualTableScenario scn, String... fragments) {
        for (String message : scn.gameState().getLastMessages()) {
            String plain = message.replaceAll("<[^>]*>", "");
            boolean all = true;
            for (String fragment : fragments) {
                if (!plain.contains(fragment)) {
                    all = false;
                    break;
                }
            }
            if (all) {
                return true;
            }
        }
        return false;
    }

    /**
     * Makes Defense Of Muunilinst a 'complete' [Separatist] Mission: attached to the real War Has Begun with a card
     * stacked on it (see Card_1291_027_Tests for how completion stacks a card).
     */
    private void PutCompleteSeparatistMissionOnTable(VirtualTableScenario scn) {
        var war = scn.GetDSCard("war");
        var defense = scn.GetDSCard("defense");
        var junk = scn.GetDSCard("junk");
        scn.AttachCardsTo(war, defense);
        scn.StackCardsOn(defense, junk);
        assertTrue(scn.IsAttachedTo(war, defense));
        assertTrue(scn.IsStackedOn(defense, junk));
    }

    /**
     * From the Dark Side Activate phase of turn 1: ends the Activate phase (a table change), which fires the
     * required flip trigger, then declines the once-per-game retrieval and lands in the Control phase with
     * At War With Itself face up.
     */
    private void FlipToAtWarWithItselfDecliningRetrieval(VirtualTableScenario scn) {
        var uprising = scn.GetDSCard("uprising");
        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSActivateMaxForceAndPass();

        assertTrue(uprising.isFlipped());
        assertEquals("At War With Itself", uprising.getTitle());
        assertTrue(scn.DSDecisionAvailable("Flipped"));
        assertTrue(scn.DSDecisionAvailable("At War With Itself"));
        assertTrue(scn.DSActionAvailable("Retrieve up to 3 Force"));
        scn.DSDecline();

        assertTrue(scn.LSDecisionAvailable("DOUBLE_SIDED_CARD_FLIPPED"));
        scn.PassResponses("DOUBLE_SIDED_CARD_FLIPPED");
        assertTrue(scn.AwaitingDSControlPhaseActions());
    }

    /**
     * Ends the Activate phase of Dark Side turn 1 and walks the responses of the objective's "remainder of game"
     * required trigger making the given card lost.
     */
    private void EndActivatePhaseAndLoseToObjective(VirtualTableScenario scn, PhysicalCardImpl card) {
        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSActivateMaxForceAndPass();

        assertTrue(scn.LSDecisionAvailable("ABOUT_TO_BE_LOST_FROM_TABLE"));
        scn.PassResponses("ABOUT_TO_BE_LOST_FROM_TABLE");
        assertTrue(scn.LSDecisionAvailable("LOST_FROM_TABLE"));
        scn.PassResponses("LOST_FROM_TABLE");

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertInZone(Zone.LOST_PILE, card);
        String title = card.getBlueprint().getTitle();
        assertTrue("Expected the objective's required trigger to make " + title + " lost",
                messageSent(scn, "Separatist Uprising required response is initiated"));
        assertTrue(messageSent(scn, "causes", title, "on table to be lost using Separatist Uprising"));
    }

    /**
     * Initiates a battle at the Council Room that the Dark Side loses: Light Side draws the given destiny, Dark Side
     * draws 0 (if it may draw at all). Stops at the Dark Side's first damage-segment payment decision.
     */
    private void LoseBattleAtCouncilRoom(VirtualTableScenario scn, int lsDestiny) {
        var site = scn.GetDSCard("council");
        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(site));
        scn.DSInitiateBattle(site);
        scn.PrepareDSDestiny(0);
        scn.PrepareLSDestiny(lsDestiny);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(lsDestiny, scn.GetLSTotalDestiny());
        assertTrue(scn.GetLSTotalPower() > scn.GetDSTotalPower());
        assertTrue(scn.LSDecisionAvailable("INITIAL_ATTRITION_CALCULATED"));
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertTrue(scn.AwaitingDSAttritionPayment());
    }

    // ----------------------------------------------------------------------------------------------------------
    // Separatist Uprising: Deploy Separatist Council Room and War Has Begun.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void SeparatistUprisingDeploysSeparatistCouncilRoomAndWarHasBegun() {
        var scn = GetScenario();
        var uprising = scn.GetDSCard("uprising");
        var council = scn.GetDSCard("council");
        var war = scn.GetDSCard("war");

        scn.StartGame();

        assertInZone(Zone.SIDE_OF_TABLE, uprising);
        assertEquals(scn.DS, uprising.getZoneOwner());
        assertFalse(uprising.isFlipped());
        assertEquals("Separatist Uprising", uprising.getTitle());
        assertTrue(scn.IsCardActive(uprising));

        assertEquals(Zone.LOCATIONS, council.getZone());
        assertEquals("Geonosis: Separatist Council Room", council.getTitle());
        assertTrue(scn.IsCardActive(council));

        assertInZone(Zone.SIDE_OF_TABLE, war);
        assertEquals(scn.DS, war.getZoneOwner());
        assertEquals("War Has Begun", war.getTitle());
        assertTrue(scn.IsCardActive(war));

        assertTrue(messageSent(scn, "deploys", "Geonosis: Separatist Council Room", "from Reserve Deck"));
        assertTrue(messageSent(scn, "deploys", "War Has Begun", "from Reserve Deck"));
        assertTrue(scn.AwaitingDSActivatePhaseActions());
    }

    // ----------------------------------------------------------------------------------------------------------
    // For remainder of game, Galen, Imperials, and Imperial starships are lost.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void SeparatistUprisingMakesGalenImperialsAndImperialStarshipsLost() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSCard("council");

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader);
        assertAtLocation(site, vader);
        assertTrue(scn.IsCardActive(vader));

        EndActivatePhaseAndLoseToObjective(scn, vader);

        assertInZone(Zone.LOST_PILE, vader);
        assertEquals(1, scn.GetDSLostPileCount());
    }

    @Test
    public void SeparatistUprisingMakesImperialStarshipAtSystemLost() {
        var scn = GetScenario();
        var tie = scn.GetDSCard("tie");
        var geonosis = scn.GetDSCard("geonosis");

        scn.StartGame();
        scn.MoveLocationToTable(geonosis);
        assertEquals(Zone.LOCATIONS, geonosis.getZone());
        scn.MoveCardsToLocation(geonosis, tie);
        assertAtLocation(geonosis, tie);
        // An unpiloted starship at a system is still an active card on table
        assertTrue(scn.IsCardActive(tie));

        EndActivatePhaseAndLoseToObjective(scn, tie);

        assertInZone(Zone.LOST_PILE, tie);
        assertEquals(1, scn.GetDSLostPileCount());
    }

    @Test
    public void SeparatistUprisingDoesNotMakeNonImperialCardsLost() {
        var scn = GetScenario();
        var dooku = scn.GetDSCard("dooku");
        var keder = scn.GetDSCard("keder");
        var site = scn.GetDSCard("council");

        scn.StartGame();
        scn.MoveCardsToLocation(site, dooku, keder);

        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSActivateMaxForceAndPass();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertAtLocation(site, dooku, keder);
        assertEquals(0, scn.GetDSLostPileCount());
    }

    // ----------------------------------------------------------------------------------------------------------
    // Your Republic characters and [Episode I] vehicles gain [Separatist].
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void SeparatistUprisingRepublicCharactersGainSeparatist() {
        var scn = GetScenario();
        var keder = scn.GetDSCard("keder");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSCard("council");

        assertEquals(0, keder.getBlueprint().getIconCount(Icon.SEPARATIST));
        assertTrue(keder.getBlueprint().isCardType(CardType.REPUBLIC));

        scn.StartGame();
        scn.MoveCardsToLocation(site, keder, luke);

        assertEquals(1, scn.GetIconCount(keder, Icon.SEPARATIST));
        assertEquals(0, scn.GetIconCount(luke, Icon.SEPARATIST));
    }

    // ----------------------------------------------------------------------------------------------------------
    // You may forfeit non-lightsaber weapons (using forfeit value = 3); place your just forfeited weapon in Used Pile.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void SeparatistUprisingNonLightsaberCharacterWeaponMayBeForfeitedForThreeToUsedPile() {
        var scn = GetScenario();
        var keder = scn.GetDSCard("keder");
        var rifle = scn.GetDSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSCard("council");

        scn.StartGame();
        scn.MoveCardsToLocation(site, keder, luke);
        scn.AttachCardsTo(keder, rifle);
        assertTrue(scn.IsAttachedTo(keder, rifle));
        assertFalse(rifle.getBlueprint().hasForfeitAttribute());

        // Keder (ability 3) draws no destiny; Luke draws 6: LS 9 vs DS 4, attrition against DS = 6
        LoseBattleAtCouncilRoom(scn, 6);
        assertEquals(6, scn.GetUnpaidDSAttrition());
        assertEquals(5, scn.GetUnpaidDSBattleDamage());
        assertTrue(scn.DSDecisionAvailable("Choose Force to lose or a card from battle to forfeit"));
        assertTrue(scn.DSHasCardChoiceAvailable(keder));
        assertTrue(scn.DSHasCardChoiceAvailable(rifle));

        int usedBefore = scn.GetDSUsedPileCount();
        scn.DSChooseCard(rifle);
        assertTrue(scn.LSDecisionAvailable("FORFEITED_TO_USED_PILE_FROM_TABLE"));
        scn.PassResponses("FORFEITED_TO_USED_PILE_FROM_TABLE");

        assertInZone(Zone.USED_PILE, rifle);
        assertEquals(usedBefore + 1, scn.GetDSUsedPileCount());
        assertEquals(0, scn.GetDSLostPileCount());
        assertEquals(3, scn.GetUnpaidDSAttrition());
        assertEquals(2, scn.GetUnpaidDSBattleDamage());
        assertTrue(scn.AwaitingDSAttritionPayment());
        assertTrue(scn.DSHasCardChoiceAvailable(keder));
        assertEquals(rifle, scn.GetTopOfDSUsedPile());
    }

    @Test
    public void SeparatistUprisingLightsabersMayNotBeForfeited() {
        var scn = GetScenario();
        var dooku = scn.GetDSCard("dooku");
        var saber = scn.GetDSCard("saber");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSCard("council");

        scn.StartGame();
        scn.MoveCardsToLocation(site, dooku, luke);
        scn.AttachCardsTo(dooku, saber);
        assertTrue(scn.IsAttachedTo(dooku, saber));
        assertTrue(saber.getBlueprint().hasKeyword(Keyword.LIGHTSABER));

        // Dooku draws 0, Luke draws 7: LS 10 vs DS 5, attrition against DS = 7
        LoseBattleAtCouncilRoom(scn, 7);
        assertEquals(7, scn.GetUnpaidDSAttrition());
        assertTrue(scn.DSDecisionAvailable("Choose Force to lose or a card from battle to forfeit"));
        assertTrue(scn.DSHasCardChoiceAvailable(dooku));
        assertTrue(scn.DSHasCardChoiceNotAvailable(saber));
    }

    @Test
    public void SeparatistUprisingArtilleryWeaponForfeitsForThreeToUsedPile() {
        var scn = GetScenario();
        var keder = scn.GetDSCard("keder");
        var eweb = scn.GetDSCard("eweb");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSCard("council");

        scn.StartGame();
        scn.MoveCardsToLocation(site, keder, eweb, luke);
        assertAtLocation(site, eweb);
        assertEquals(1, eweb.getBlueprint().getForfeit(), scn.epsilon);

        LoseBattleAtCouncilRoom(scn, 6);
        assertEquals(6, scn.GetUnpaidDSAttrition());
        assertTrue(scn.DSHasCardChoiceAvailable(eweb));

        scn.DSChooseCard(eweb);
        assertTrue(scn.LSDecisionAvailable("FORFEITED_TO_USED_PILE_FROM_TABLE"));
        scn.PassResponses("FORFEITED_TO_USED_PILE_FROM_TABLE");

        // Forfeited for 3 (not its printed forfeit of 1) and placed in Used Pile
        assertInZone(Zone.USED_PILE, eweb);
        assertEquals(0, scn.GetDSLostPileCount());
        assertEquals(3, scn.GetUnpaidDSAttrition());
        assertTrue(scn.AwaitingDSAttritionPayment());
    }

    // ----------------------------------------------------------------------------------------------------------
    // Once per turn, may use 1 Force to deploy an [Episode I] battleground from your reserve deck; reshuffle.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void SeparatistUprisingDeploysEpisodeIBattlegroundFromReserveDeckOncePerTurn() {
        var scn = GetScenario();
        var uprising = scn.GetDSCard("uprising");
        var geonosis = scn.GetDSCard("geonosis");
        var muunilinst = scn.GetDSCard("muunilinst");

        scn.StartGame();
        scn.SkipToPhase(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(geonosis, muunilinst);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(forceBefore >= 2);

        assertTrue(scn.DSCardActionAvailable(uprising, "Deploy [Episode I] battleground from Reserve Deck"));
        scn.DSUseCardAction(uprising, "Deploy [Episode I] battleground from Reserve Deck");
        assertTrue(scn.LSDecisionAvailable("Use 1 Force - Optional responses"));
        scn.PassResponses("Use 1 Force");
        assertTrue(scn.DSDecisionAvailable("Choose card to deploy from Reserve Deck"));
        assertTrue(scn.DSHasCardChoiceAvailable(muunilinst));
        assertTrue(scn.DSHasCardChoiceAvailable(geonosis));
        scn.DSChooseCard(muunilinst);
        assertTrue(scn.LSDecisionAvailable("PLAY - Optional responses"));
        scn.PassResponses("PLAY");
        assertTrue(scn.LSDecisionAvailable("LOOKED_AT_CARDS_IN_CARD_PILE"));
        scn.PassResponses("LOOKED_AT_CARDS_IN_CARD_PILE");

        assertEquals(Zone.LOCATIONS, muunilinst.getZone());
        assertEquals(forceBefore - 1, scn.GetDSForcePileCount());
        assertInZone(Zone.RESERVE_DECK, geonosis);

        // Opponent gets to act after our deploy action, then it is our deploy phase again: no second use this turn
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        scn.LSPass();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(uprising, "Deploy [Episode I] battleground from Reserve Deck"));

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(geonosis);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSCardActionAvailable(uprising, "Deploy [Episode I] battleground from Reserve Deck"));
        scn.DSUseCardAction(uprising, "Deploy [Episode I] battleground from Reserve Deck");
        assertTrue(scn.LSDecisionAvailable("Use 1 Force - Optional responses"));
        scn.PassResponses("Use 1 Force");
        assertTrue(scn.DSDecisionAvailable("Choose card to deploy from Reserve Deck"));
        scn.DSChooseCard(geonosis);
        assertTrue(scn.LSDecisionAvailable("PLAY - Optional responses"));
        scn.PassResponses("PLAY");
        assertTrue(scn.LSDecisionAvailable("LOOKED_AT_CARDS_IN_CARD_PILE"));
        scn.PassResponses("LOOKED_AT_CARDS_IN_CARD_PILE");

        assertEquals(Zone.LOCATIONS, geonosis.getZone());
    }

    // ----------------------------------------------------------------------------------------------------------
    // Flip this card if you have one 'complete' [Separatist] Mission on table.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void SeparatistUprisingFlipsWhenACompleteSeparatistMissionIsOnTable() {
        var scn = GetScenario();
        var uprising = scn.GetDSCard("uprising");

        scn.StartGame();
        assertFalse(uprising.isFlipped());
        PutCompleteSeparatistMissionOnTable(scn);

        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSActivateMaxForceAndPass();

        assertTrue(uprising.isFlipped());
        assertEquals("At War With Itself", uprising.getTitle());
        assertTrue(messageSent(scn, "Separatist Uprising is flipped to At War With Itself"));
        assertTrue(scn.DSDecisionAvailable("Flipped"));
        assertTrue(scn.DSDecisionAvailable("At War With Itself"));
        assertTrue(scn.DSActionAvailable("Retrieve up to 3 Force"));
    }

    @Test
    public void SeparatistUprisingDoesNotFlipWhileSeparatistMissionIsNotComplete() {
        var scn = GetScenario();
        var uprising = scn.GetDSCard("uprising");
        var war = scn.GetDSCard("war");
        var defense = scn.GetDSCard("defense");

        scn.StartGame();
        scn.AttachCardsTo(war, defense);
        assertTrue(scn.IsAttachedTo(war, defense));
        assertTrue(scn.GetStackedCards(defense).isEmpty());

        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSActivateMaxForceAndPass();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(uprising.isFlipped());
        assertEquals("Separatist Uprising", uprising.getTitle());
    }

    // ----------------------------------------------------------------------------------------------------------
    // At War With Itself: Immediately, may retrieve up to 3 Force (may be taken into hand) once per game.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void AtWarWithItselfRetrievesUpTo3ForceIntoHandOncePerGame() {
        var scn = GetScenario();
        var uprising = scn.GetDSCard("uprising");
        var junk = scn.GetDSCard("junk");
        var defense = scn.GetDSCard("defense");
        var lost1 = scn.GetDSCard("lost1");
        var lost2 = scn.GetDSCard("lost2");
        var lost3 = scn.GetDSCard("lost3");

        scn.StartGame();
        scn.MoveCardsToTopOfDSLostPile(lost1, lost2, lost3);
        assertEquals(3, scn.GetDSLostPileCount());
        PutCompleteSeparatistMissionOnTable(scn);

        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSActivateMaxForceAndPass();
        assertTrue(uprising.isFlipped());
        assertTrue(scn.DSDecisionAvailable("Flipped"));
        assertTrue(scn.DSActionAvailable("Retrieve up to 3 Force"));
        scn.DSChooseAction("Retrieve up to 3 Force");

        assertTrue(scn.DSDecisionAvailable("Choose amount of Force to retrieve"));
        assertEquals(0, scn.DSGetChoiceMin());
        assertEquals(3, scn.DSGetChoiceMax());
        scn.DSDecided(3);
        assertTrue(scn.LSDecisionAvailable("FORCE_RETRIEVAL_INITIATED"));
        scn.PassResponses("FORCE_RETRIEVAL_INITIATED");
        assertTrue(scn.LSDecisionAvailable("FORCE_RETRIEVAL_ABOUT_TO_RETRIEVE"));
        scn.PassResponses("FORCE_RETRIEVAL_ABOUT_TO_RETRIEVE");

        // Top of Lost Pile is retrieved first; each card may go to Used Pile or hand
        assertTrue(scn.DSDecisionAvailable("Choose where to retrieve"));
        assertTrue(scn.DSChoiceAvailable("Hand"));
        assertTrue(scn.DSChoiceAvailable("Used Pile"));
        scn.DSChoose("Hand");
        assertTrue(scn.LSDecisionAvailable("RETRIEVED_FORCE"));
        scn.PassResponses("RETRIEVED_FORCE");
        assertInHand(lost3);

        assertTrue(scn.DSDecisionAvailable("Choose where to retrieve"));
        scn.DSChoose("Hand");
        assertTrue(scn.LSDecisionAvailable("RETRIEVED_FORCE"));
        scn.PassResponses("RETRIEVED_FORCE");
        assertInHand(lost2);

        assertTrue(scn.DSDecisionAvailable("Choose where to retrieve"));
        scn.DSChoose("Hand");
        assertTrue(scn.LSDecisionAvailable("RETRIEVED_FORCE"));
        scn.PassResponses("RETRIEVED_FORCE");
        assertInHand(lost1);

        assertEquals(0, scn.GetDSLostPileCount());
        assertTrue(scn.LSDecisionAvailable("DOUBLE_SIDED_CARD_FLIPPED"));
        scn.PassResponses("DOUBLE_SIDED_CARD_FLIPPED");
        assertTrue(scn.AwaitingDSControlPhaseActions());

        // Once per game: flip back (Mission no longer complete) and flip again; no retrieval is offered
        scn.MoveCardsToTopOfDSUsedPile(junk);
        assertTrue(scn.GetStackedCards(defense).isEmpty());
        scn.PassControlActions();
        assertFalse(uprising.isFlipped());
        assertTrue(scn.DSDecisionAvailable("DOUBLE_SIDED_CARD_FLIPPED"));
        scn.PassResponses("DOUBLE_SIDED_CARD_FLIPPED");
        assertTrue(scn.AwaitingDSDeployPhaseActions());

        scn.StackCardsOn(defense, junk);
        assertTrue(scn.IsStackedOn(defense, junk));
        scn.PassDeployActions();
        assertTrue(uprising.isFlipped());
        assertFalse(scn.DSActionAvailable("Retrieve up to 3 Force"));
        assertTrue(scn.DSDecisionAvailable("DOUBLE_SIDED_CARD_FLIPPED"));
        scn.PassResponses("DOUBLE_SIDED_CARD_FLIPPED");
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        assertEquals(0, scn.GetDSLostPileCount());
    }

    // ----------------------------------------------------------------------------------------------------------
    // While this side up, opponent's battle destiny draws are -1. Your total power in battles is +X.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void AtWarWithItselfOpponentBattleDestinyMinus1AndTotalPowerPlusCompleteMissions() {
        var scn = GetScenario();
        var dooku = scn.GetDSCard("dooku");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSCard("council");

        scn.StartGame();
        PutCompleteSeparatistMissionOnTable(scn);
        scn.MoveCardsToLocation(site, dooku, luke);
        FlipToAtWarWithItselfDecliningRetrieval(scn);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(site));
        scn.DSInitiateBattle(site);
        scn.PrepareDSDestiny(5);
        scn.PrepareLSDestiny(4);
        scn.SkipToEndOfPowerSegment(true);

        // Opponent's destiny 4 becomes 3; ours is unmodified
        assertEquals(3, scn.GetLSTotalDestiny());
        assertEquals(5, scn.GetDSTotalDestiny());
        // X = 1 complete [Separatist] Mission
        assertEquals(scn.GetPower(dooku) + 5 + 1, scn.GetDSTotalPower());
        assertEquals(scn.GetPower(luke) + 3, scn.GetLSTotalPower());
    }

    // ----------------------------------------------------------------------------------------------------------
    // Whenever you win a battle, opponent loses X Force, where X = the number of 'complete' [Separatist] Missions.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void AtWarWithItselfOpponentLosesForceEqualToCompleteMissionsWhenYouWinABattle() {
        var scn = GetScenario();
        var dooku = scn.GetDSCard("dooku");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSCard("council");

        scn.StartGame();
        PutCompleteSeparatistMissionOnTable(scn);
        scn.MoveCardsToLocation(site, dooku, luke);
        FlipToAtWarWithItselfDecliningRetrieval(scn);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PrepareDSDestiny(5);
        scn.PrepareLSDestiny(4);
        scn.SkipToEndOfPowerSegment(true);
        assertTrue(scn.GetDSTotalPower() > scn.GetLSTotalPower());
        assertTrue(scn.LSDecisionAvailable("INITIAL_ATTRITION_CALCULATED"));
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertTrue(scn.DSWonBattle());

        // The objective's required trigger makes opponent lose 1 Force (X = 1) when the battle is won, before
        // battle damage and attrition are paid
        int lsLostBefore = scn.GetLSLostPileCount();
        int lsLifeBefore = scn.GetLSLifeForceRemaining();
        assertTrue(scn.DSDecisionAvailable("FORCE_LOSS_INITIATED"));
        scn.PassResponses("FORCE_LOSS_INITIATED");
        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE"));
        scn.PassResponses("ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE");
        assertTrue(scn.LSDecisionAvailable("Choose Force to lose"));
        assertEquals(1, scn.LSGetChoiceMin());
        assertEquals(1, scn.LSGetChoiceMax());
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        assertTrue(messageSent(scn, "At War With Itself required response is initiated"));
        assertTrue(messageSent(scn, "At War With Itself targets to make opponent lose 1 Force"));

        assertEquals(lsLostBefore + 1, scn.GetLSLostPileCount());
        assertEquals(lsLifeBefore - 1, scn.GetLSLifeForceRemaining());
        // Battle damage and attrition are still owed on top of that
        assertEquals(5, scn.GetUnpaidLSAttrition());
        assertEquals(5, scn.GetUnpaidLSBattleDamage());
    }

    // ----------------------------------------------------------------------------------------------------------
    // Once during your control phase, may use 2 Force to take into hand a [Separatist] character, vehicle, or
    // starship from reserve deck; reshuffle.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void AtWarWithItselfTakesSeparatistCardFromReserveDuringControlPhase() {
        var scn = GetScenario();
        var uprising = scn.GetDSCard("uprising");
        var dooku = scn.GetDSCard("dooku");

        scn.StartGame();
        PutCompleteSeparatistMissionOnTable(scn);
        FlipToAtWarWithItselfDecliningRetrieval(scn);

        scn.MoveCardsToTopOfDSReserveDeck(dooku);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(forceBefore >= 2);

        assertTrue(scn.DSCardActionAvailable(uprising, "Take [Separatist] card into hand from Reserve Deck"));
        scn.DSUseCardAction(uprising, "Take [Separatist] card into hand from Reserve Deck");
        assertTrue(scn.LSDecisionAvailable("Use 2 Force - Optional responses"));
        scn.PassResponses("Use 2 Force");
        assertTrue(scn.DSDecisionAvailable("Choose card to take into hand"));
        assertTrue(scn.DSHasCardChoiceAvailable(dooku));
        scn.DSChooseCard(dooku);
        assertTrue(scn.LSDecisionAvailable("Optional responses"));
        scn.PassAllResponses();

        assertInHand(dooku);
        assertEquals(forceBefore - 2, scn.GetDSForcePileCount());

        // Once per control phase
        assertTrue(scn.AwaitingLSControlPhaseActions());
        scn.LSPass();
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.DSCardActionAvailable(uprising, "Take [Separatist] card into hand from Reserve Deck"));
    }

    @Test
    public void SeparatistUprisingFrontSideHasNoTakeSeparatistCardAction() {
        var scn = GetScenario();
        var uprising = scn.GetDSCard("uprising");
        var dooku = scn.GetDSCard("dooku");

        scn.StartGame();
        scn.DSActivateMaxForceAndPass();
        scn.MoveCardsToTopOfDSReserveDeck(dooku);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(uprising.isFlipped());
        assertFalse(scn.DSCardActionAvailable(uprising, "Take [Separatist] card into hand from Reserve Deck"));
    }

    // ----------------------------------------------------------------------------------------------------------
    // Flip this card if you have no 'complete' [Separatist] Missions on table.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void AtWarWithItselfFlipsBackWhenNoCompleteSeparatistMissionOnTable() {
        var scn = GetScenario();
        var uprising = scn.GetDSCard("uprising");
        var defense = scn.GetDSCard("defense");
        var junk = scn.GetDSCard("junk");

        scn.StartGame();
        PutCompleteSeparatistMissionOnTable(scn);
        FlipToAtWarWithItselfDecliningRetrieval(scn);
        assertTrue(uprising.isFlipped());

        // Un-complete the Mission; the next table change (end of Control phase) flips the objective back
        scn.MoveCardsToTopOfDSUsedPile(junk);
        assertTrue(scn.GetStackedCards(defense).isEmpty());
        assertTrue(scn.AwaitingDSControlPhaseActions());
        scn.PassControlActions();

        assertFalse(uprising.isFlipped());
        assertEquals("Separatist Uprising", uprising.getTitle());
        assertTrue(messageSent(scn, "At War With Itself is flipped to Separatist Uprising"));
        assertTrue(scn.DSDecisionAvailable("DOUBLE_SIDED_CARD_FLIPPED"));
        scn.PassResponses("DOUBLE_SIDED_CARD_FLIPPED");
        assertTrue(scn.AwaitingDSDeployPhaseActions());
    }
}
