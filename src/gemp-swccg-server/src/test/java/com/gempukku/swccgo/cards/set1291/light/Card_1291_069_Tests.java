package com.gempukku.swccgo.cards.set1291.light;

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
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.state.WhileInPlayData;
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
 * Republic At War / Aggressive Negotiations (Legacy VB9.1, 1291_69).
 *
 * The objective starts the game for real: {@link #RepublicAtWarObjective} puts the objective, Geonosis: Forward
 * Command Center (1291_57) and Begun, The Clone War Has (1291_50) in the Light Side deck and the engine deploys all
 * three during the starting-cards phase.
 *
 * The "remainder of game" trigger and both flips are required triggers that fire on any table change; the tests
 * drive them with the end of a phase (both players passing), which is a table change in GEMP.
 */
public class Card_1291_069_Tests {

    static final StartingSetup RepublicAtWarObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("raw", "1291_69"); // Republic At War / Aggressive Negotiations
                put("fcc", "1291_57"); // Geonosis: Forward Command Center
                put("begun", "1291_50"); // Begun, The Clone War Has
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("raw"));
            assertInZone(Zone.LOCATIONS, scn.GetLSCard("fcc"));
            assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("begun"));
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("assault", "1291_65"); // Assault On Muunilinst ([Clone Army] Mission, deploys on Begun)
                    put("rescue", "1291_67"); // Rescue On Geonosis ([Clone Army] Mission, completes by while-in-play data)
                    put("amidala", "12_22"); // Amidala (not a Rebel)
                    put("junk", "1_29"); // any card to stack on the Mission to make it 'complete'
                    put("luke", "1_19"); // Luke Skywalker (Rebel)
                    put("gold1", "1_141"); // Gold 1 (Rebel starship)
                    put("muunilinst", "1291_60"); // Muunilinst system ([Episode I] battleground)
                    put("harnaidan", "1291_61"); // Muunilinst: City Of Harnaidan ([Episode I] battleground)
                    put("trooper", "1116_9"); // Clone Trooper (not a Rebel; power 2, ability 2)
                    put("clonepilot", "221_55"); // Clone Pilot ([Clone Army] character)
                    put("obi", "11_6"); // Obi-Wan Kenobi (Episode I, not a Rebel)
                    put("blaster", "1_152"); // Blaster (character weapon, no printed forfeit)
                    put("saber", "1_155"); // Jedi Lightsaber
                    put("h1", "1_30");
                    put("h2", "1_31");
                    put("h3", "1_32");
                    put("lost1", "1_33");
                    put("lost2", "1_34");
                }},
                new HashMap<>() {{
                    put("vader", "1_168"); // Darth Vader: power 6, ability 6
                    put("trooper", "1_194"); // Stormtrooper: power 1, ability 1
                    put("yd", "9_134"); // Your Destiny
                }},
                10, 10,
                RepublicAtWarObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void RepublicAtWarStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("raw").getBlueprint();

        assertEquals("Republic At War", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
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
    public void AggressiveNegotiationsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        scn.StartGame();
        var card = scn.GetLSCard("raw").getOtherSideBlueprint();

        assertEquals("Aggressive Negotiations", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
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
     * Makes Assault On Muunilinst a 'complete' [Clone Army] Mission: attached to the real Begun, The Clone War Has with
     * a card stacked on it (Assault On Muunilinst completes by stacking a lost card).
     */
    private void PutCompleteCloneArmyMissionOnTable(VirtualTableScenario scn) {
        var begun = scn.GetLSCard("begun");
        var assault = scn.GetLSCard("assault");
        var junk = scn.GetLSCard("junk");
        scn.AttachCardsTo(begun, assault);
        scn.StackCardsOn(assault, junk);
        assertTrue(scn.IsAttachedTo(begun, assault));
        assertTrue(scn.IsStackedOn(assault, junk));
    }

    /**
     * From the Dark Side Activate phase of turn 1: ends the phase (a table change), which fires the required flip;
     * Light Side declines the once-per-game draw and play returns to the Dark Side Control phase.
     */
    private void FlipToAggressiveNegotiationsDecliningDraw(VirtualTableScenario scn) {
        var raw = scn.GetLSCard("raw");
        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSActivateMaxForceAndPass();

        assertTrue(raw.isFlipped());
        assertEquals("Aggressive Negotiations", raw.getTitle());
        // Dark Side (whose turn it is) responds to the flip first
        assertTrue(scn.DSDecisionAvailable("DOUBLE_SIDED_CARD_FLIPPED"));
        scn.DSPass();
        assertTrue(scn.LSActionAvailable("Place X cards from hand"));
        scn.LSDecline();
        scn.PassResponses("DOUBLE_SIDED_CARD_FLIPPED");
        assertTrue(scn.AwaitingDSControlPhaseActions());
    }

    /**
     * Light Side initiates a battle at Forward Command Center that it loses: Light Side draws no destiny, Dark Side
     * draws the given destiny. Stops at the Light Side's first damage-segment payment decision.
     */
    private void LoseBattleAtForwardCommandCenter(VirtualTableScenario scn, int dsDestiny) {
        var site = scn.GetLSCard("fcc");
        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle(site));
        scn.LSInitiateBattle(site);
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(dsDestiny);
        scn.SkipToEndOfPowerSegment(true);
        assertTrue(scn.GetDSTotalPower() > scn.GetLSTotalPower());
        assertTrue(scn.DSDecisionAvailable("INITIAL_ATTRITION_CALCULATED"));
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertTrue(scn.AwaitingLSAttritionPayment());
    }

    // ----------------------------------------------------------------------------------------------------------
    // Republic At War: Deploy Forward Command Center and Begun, The Clone War Has.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void RepublicAtWarDeploysForwardCommandCenterAndBegunTheCloneWarHas() {
        var scn = GetScenario();
        var raw = scn.GetLSCard("raw");
        var fcc = scn.GetLSCard("fcc");
        var begun = scn.GetLSCard("begun");

        scn.StartGame();

        assertInZone(Zone.SIDE_OF_TABLE, raw);
        assertFalse(raw.isFlipped());
        assertEquals("Republic At War", raw.getTitle());
        assertTrue(scn.IsCardActive(raw));
        assertEquals(Zone.LOCATIONS, fcc.getZone());
        assertTrue(scn.IsCardActive(fcc));
        assertInZone(Zone.SIDE_OF_TABLE, begun);
        assertTrue(scn.IsCardActive(begun));
        assertTrue(messageSent(scn, "deploys", "Geonosis: Forward Command Center", "from Reserve Deck"));
        assertTrue(messageSent(scn, "deploys", "Begun, The Clone War Has", "from Reserve Deck"));
    }

    // ----------------------------------------------------------------------------------------------------------
    // For remainder of game, Rebels and Rebel starships are lost.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void RepublicAtWarMakesRebelsLost() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSCard("fcc");

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        assertAtLocation(site, luke);

        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSActivateMaxForceAndPass();
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, luke);
        assertTrue(messageSent(scn, "Republic At War required response is initiated"));
    }

    @Test
    public void RepublicAtWarMakesRebelStarshipsLost() {
        var scn = GetScenario();
        var gold1 = scn.GetLSCard("gold1");
        var muunilinst = scn.GetLSCard("muunilinst");

        scn.StartGame();
        scn.MoveLocationToTable(muunilinst);
        scn.MoveCardsToLocation(muunilinst, gold1);
        assertAtLocation(muunilinst, gold1);

        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSActivateMaxForceAndPass();
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, gold1);
    }

    @Test
    public void RepublicAtWarDoesNotMakeNonRebelsLost() {
        var scn = GetScenario();
        var trooper = scn.GetLSCard("trooper");
        var obi = scn.GetLSCard("obi");
        var site = scn.GetLSCard("fcc");

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, obi);

        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSActivateMaxForceAndPass();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertAtLocation(site, trooper, obi);
        assertEquals(0, scn.GetLSLostPileCount());
    }

    // ----------------------------------------------------------------------------------------------------------
    // You may forfeit non-lightsaber weapons (using forfeit value = 3); place your just forfeited weapon in Used Pile.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void RepublicAtWarNonLightsaberWeaponMayBeForfeitedForThreeToUsedPile() {
        var scn = GetScenario();
        // Clone Pilot is a warrior (Blaster deploys on your warrior), ability 2: draws no battle destiny
        var trooper = scn.GetLSCard("clonepilot");
        var blaster = scn.GetLSCard("blaster");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSCard("fcc");

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, vader);
        scn.AttachCardsTo(trooper, blaster);
        assertFalse(blaster.getBlueprint().hasForfeitAttribute());

        LoseBattleAtForwardCommandCenter(scn, 6);
        int attrition = scn.GetUnpaidLSAttrition();
        assertTrue(attrition >= 6);
        assertTrue(scn.LSHasCardChoiceAvailable(trooper));
        assertTrue(scn.LSHasCardChoiceAvailable(blaster));

        scn.LSChooseCard(blaster);
        scn.PassResponses("FORFEITED_TO_USED_PILE_FROM_TABLE");

        assertInZone(Zone.USED_PILE, blaster);
        assertEquals(0, scn.GetLSLostPileCount());
        assertEquals(attrition - 3, scn.GetUnpaidLSAttrition());
    }

    @Test
    public void RepublicAtWarLightsabersMayNotBeForfeited() {
        var scn = GetScenario();
        var obi = scn.GetLSCard("obi");
        var saber = scn.GetLSCard("saber");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSCard("fcc");

        scn.StartGame();
        scn.MoveCardsToLocation(site, obi, vader);
        scn.AttachCardsTo(obi, saber);
        assertTrue(saber.getBlueprint().hasKeyword(Keyword.LIGHTSABER));

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(6);
        scn.SkipToEndOfPowerSegment(true);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertTrue(scn.AwaitingLSAttritionPayment());
        assertTrue(scn.LSHasCardChoiceAvailable(obi));
        assertTrue(scn.LSHasCardChoiceNotAvailable(saber));
    }

    // ----------------------------------------------------------------------------------------------------------
    // Your Destiny is suspended. You lose no Force to Vengeance Of The Dark Prince.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void RepublicAtWarSuspendsYourDestiny() {
        var scn = GetScenario();
        var obi = scn.GetLSCard("obi");
        var site = scn.GetLSCard("fcc");

        scn.StartGame();
        scn.MoveCardsToLocation(site, obi);
        var yourDestiny = scn.GetDSCard("yd");
        scn.AttachCardsTo(obi, yourDestiny);
        scn.DSActivateMaxForceAndPass();

        assertEquals(Title.Your_Destiny, yourDestiny.getTitle());
        assertTrue(yourDestiny.isSuspended());
        assertFalse(scn.IsCardActive(yourDestiny));
    }

    @Test
    public void RepublicAtWarYouLoseNoForceToVengeanceOfTheDarkPrince() {
        var scn = GetScenario();
        scn.StartGame();
        var vengeance = MissionTestSupport.putOnTable(scn, 9802, scn.DS,
                new AbstractNormalEffect(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Vengeance_Of_The_Dark_Prince, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
                });
        var vader = scn.GetDSCard("vader");

        assertEquals(0, scn.game().getModifiersQuerying().getForceToLoseFromCardLimit(scn.gameState(), scn.LS, vengeance), scn.epsilon);
        // Other cards, and Dark Side, are not limited
        assertTrue(scn.game().getModifiersQuerying().getForceToLoseFromCardLimit(scn.gameState(), scn.LS, vader) > 1000);
        assertTrue(scn.game().getModifiersQuerying().getForceToLoseFromCardLimit(scn.gameState(), scn.DS, vengeance) > 1000);
    }

    // ----------------------------------------------------------------------------------------------------------
    // While this side up, once per turn, may deploy an [Episode I] battleground from Reserve Deck.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void RepublicAtWarDeploysEpisodeIBattlegroundFromReserveDeckOncePerTurn() {
        var scn = GetScenario();
        var raw = scn.GetLSCard("raw");
        var muunilinst = scn.GetLSCard("muunilinst");
        var harnaidan = scn.GetLSCard("harnaidan");

        scn.StartGame();
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(muunilinst, harnaidan);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        int forceBefore = scn.GetLSForcePileCount();

        assertTrue(scn.LSCardActionAvailable(raw, "Deploy [Episode I] battleground from Reserve Deck"));
        scn.LSUseCardAction(raw, "Deploy [Episode I] battleground from Reserve Deck");
        assertTrue(scn.LSDecisionAvailable("Choose card to deploy from Reserve Deck"));
        assertTrue(scn.LSHasCardChoiceAvailable(muunilinst));
        scn.LSChooseCard(muunilinst);
        scn.PassAllResponses();

        assertEquals(Zone.LOCATIONS, muunilinst.getZone());
        // No Force is used for this
        assertEquals(forceBefore, scn.GetLSForcePileCount());

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(raw, "Deploy [Episode I] battleground from Reserve Deck"));
    }

    // ----------------------------------------------------------------------------------------------------------
    // Flip this card if a complete [Clone Army] Mission on table.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void RepublicAtWarFlipsWhenACompleteCloneArmyMissionIsOnTable() {
        var scn = GetScenario();
        var raw = scn.GetLSCard("raw");

        scn.StartGame();
        PutCompleteCloneArmyMissionOnTable(scn);
        FlipToAggressiveNegotiationsDecliningDraw(scn);

        assertTrue(messageSent(scn, "Republic At War is flipped to Aggressive Negotiations"));
    }

    @Test
    public void RepublicAtWarFlipsWhenRescueOnGeonosisIsComplete() {
        var scn = GetScenario();
        var raw = scn.GetLSCard("raw");
        var begun = scn.GetLSCard("begun");
        var rescue = scn.GetLSCard("rescue");
        var amidala = scn.GetLSCard("amidala");

        scn.StartGame();
        // Amidala on table keeps Rescue On Geonosis's Turn the Tide from placing the complete Mission in Used Pile
        scn.MoveCardsToLocation(scn.GetLSCard("fcc"), amidala);
        scn.AttachCardsTo(begun, rescue);
        // Rescue On Geonosis records completion in its while-in-play data (it releases a captive, stacks nothing)
        rescue.setWhileInPlayData(new WhileInPlayData(true));
        FlipToAggressiveNegotiationsDecliningDraw(scn);
        assertTrue(raw.isFlipped());
    }

    @Test
    public void RepublicAtWarDoesNotFlipWhileCloneArmyMissionIsNotComplete() {
        var scn = GetScenario();
        var raw = scn.GetLSCard("raw");
        var begun = scn.GetLSCard("begun");
        var assault = scn.GetLSCard("assault");

        scn.StartGame();
        scn.AttachCardsTo(begun, assault);
        assertTrue(scn.GetStackedCards(assault).isEmpty());

        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSActivateMaxForceAndPass();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(raw.isFlipped());
        assertEquals("Republic At War", raw.getTitle());
    }

    // ----------------------------------------------------------------------------------------------------------
    // Aggressive Negotiations: Immediately, may place X cards from hand on bottom of Force Pile to draw up to X+1
    // cards from Reserve Deck once per game.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void AggressiveNegotiationsPlacesXCardsOnForcePileToDrawUpToXPlusOneOncePerGame() {
        var scn = GetScenario();
        var raw = scn.GetLSCard("raw");
        var h1 = scn.GetLSCard("h1");
        var h2 = scn.GetLSCard("h2");
        var h3 = scn.GetLSCard("h3");
        var assault = scn.GetLSCard("assault");
        var junk = scn.GetLSCard("junk");

        scn.StartGame();
        scn.MoveCardsToLSHand(h1, h2, h3);
        PutCompleteCloneArmyMissionOnTable(scn);
        int handBefore = scn.GetLSHandCount();
        int reserveBefore = scn.GetLSReserveDeckCount();
        int forceBefore = scn.GetLSForcePileCount();

        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSActivateMaxForceAndPass();
        assertTrue(raw.isFlipped());
        scn.DSPass();
        assertTrue(scn.LSActionAvailable("Place X cards from hand"));
        scn.LSChooseAction("Place X cards from hand");

        assertTrue(scn.LSDecisionAvailable("Choose X cards to place on bottom of Force Pile"));
        scn.LSChooseCards(h1, h2);
        scn.PassAllResponses();

        assertTrue(scn.LSDecisionAvailable("Choose number of cards to draw from Reserve Deck"));
        assertEquals(0, scn.LSGetChoiceMin());
        assertEquals(3, scn.LSGetChoiceMax());
        scn.LSDecided(3);
        scn.PassAllResponses();

        // Force Pile was empty: the first card placed is now its top card
        assertTrue(h1.getZone() == Zone.FORCE_PILE || h1.getZone() == Zone.TOP_OF_FORCE_PILE);
        assertTrue(h2.getZone() == Zone.FORCE_PILE || h2.getZone() == Zone.TOP_OF_FORCE_PILE);
        assertInHand(h3);
        assertEquals(forceBefore + 2, scn.GetLSForcePileCount());
        assertEquals(handBefore - 2 + 3, scn.GetLSHandCount());
        assertEquals(reserveBefore - 3, scn.GetLSReserveDeckCount());

        // Once per game: flip back and forth again; no draw offered
        scn.MoveCardsToTopOfLSUsedPile(junk);
        assertTrue(scn.GetStackedCards(assault).isEmpty());
        scn.PassControlActions();
        assertFalse(raw.isFlipped());
        scn.PassAllResponses();
        scn.StackCardsOn(assault, junk);
        scn.PassDeployActions();
        assertTrue(raw.isFlipped());
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(t -> t.contains("Place X cards from hand")));
        scn.PassResponses("DOUBLE_SIDED_CARD_FLIPPED");
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(t -> t.contains("Place X cards from hand")));
        assertTrue(scn.GetLSHandCount() == 4);
    }

    // ----------------------------------------------------------------------------------------------------------
    // While this side up, opponent's battle destiny draws are -1. Your total power in battles is +Y.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void AggressiveNegotiationsOpponentBattleDestinyMinus1AndTotalPowerPlusCompleteMissions() {
        var scn = GetScenario();
        var obi = scn.GetLSCard("obi");
        var vader = scn.GetDSCard("vader");
        var stormtrooper = scn.GetDSCard("trooper");
        var site = scn.GetLSCard("fcc");

        scn.StartGame();
        PutCompleteCloneArmyMissionOnTable(scn);
        // Vader adds 1 to Dark Side battle destiny; the Stormtrooper adds nothing
        scn.MoveCardsToLocation(site, obi, vader, stormtrooper);
        FlipToAggressiveNegotiationsDecliningDraw(scn);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle(site));
        scn.LSInitiateBattle(site);
        scn.PrepareLSDestiny(5);
        scn.PrepareDSDestiny(4);
        scn.SkipToEndOfPowerSegment(true);

        // 4 - 1 (Aggressive Negotiations) + 1 (Vader)
        assertEquals(4, scn.GetDSTotalDestiny());
        assertEquals(5, scn.GetLSTotalDestiny());
        // Y = 1 complete [Clone Army] Mission
        assertEquals(scn.GetPower(obi) + 5 + 1, scn.GetLSTotalPower());
        assertEquals(scn.GetPower(vader) + scn.GetPower(stormtrooper) + 4, scn.GetDSTotalPower());
    }

    // ----------------------------------------------------------------------------------------------------------
    // Whenever you win a battle, retrieve up to Y Force.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void AggressiveNegotiationsRetrievesUpToYForceWhenYouWinABattle() {
        var scn = GetScenario();
        var obi = scn.GetLSCard("obi");
        var trooper = scn.GetDSCard("trooper");
        var lost1 = scn.GetLSCard("lost1");
        var lost2 = scn.GetLSCard("lost2");
        var site = scn.GetLSCard("fcc");

        scn.StartGame();
        PutCompleteCloneArmyMissionOnTable(scn);
        scn.MoveCardsToTopOfLSLostPile(lost1, lost2);
        scn.MoveCardsToLocation(site, obi, trooper);
        FlipToAggressiveNegotiationsDecliningDraw(scn);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PrepareLSDestiny(5);
        scn.PrepareDSDestiny(0);
        scn.SkipToEndOfPowerSegment(true);
        assertTrue(scn.GetLSTotalPower() > scn.GetDSTotalPower());
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertTrue(scn.LSWonBattle());

        assertTrue(scn.LSDecisionAvailable("Choose amount of Force to retrieve"));
        assertEquals(0, scn.LSGetChoiceMin());
        assertEquals(1, scn.LSGetChoiceMax());
        int lostBefore = scn.GetLSLostPileCount();
        scn.LSDecided(1);
        scn.PassAllResponses();
        assertEquals(lostBefore - 1, scn.GetLSLostPileCount());
    }

    // ----------------------------------------------------------------------------------------------------------
    // During your control phase, may use 2 Force to take a [Clone Army] character, starship or vehicle into hand.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void AggressiveNegotiationsTakesCloneArmyCardIntoHandForTwoForceDuringControlPhase() {
        var scn = GetScenario();
        var raw = scn.GetLSCard("raw");
        var clonePilot = scn.GetLSCard("clonepilot");
        var trooper = scn.GetLSCard("trooper");

        scn.StartGame();
        PutCompleteCloneArmyMissionOnTable(scn);
        FlipToAggressiveNegotiationsDecliningDraw(scn);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(clonePilot, trooper);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        int forceBefore = scn.GetLSForcePileCount();

        assertTrue(scn.LSCardActionAvailable(raw, "Take [Clone Army] card into hand from Reserve Deck"));
        scn.LSUseCardAction(raw, "Take [Clone Army] card into hand from Reserve Deck");
        scn.PassResponses("Use 2 Force");
        assertTrue(scn.LSDecisionAvailable("Choose card to take into hand"));
        assertTrue(scn.LSHasCardChoiceAvailable(clonePilot));
        scn.LSChooseCard(clonePilot);
        scn.PassAllResponses();

        assertInHand(clonePilot);
        assertEquals(forceBefore - 2, scn.GetLSForcePileCount());
        // No "once" limit on the printed card
        assertTrue(scn.AwaitingDSControlPhaseActions() || scn.AwaitingLSControlPhaseActions());
        if (scn.AwaitingDSControlPhaseActions()) {
            scn.DSPass();
        }
        assertTrue(scn.LSCardActionAvailable(raw, "Take [Clone Army] card into hand from Reserve Deck"));
    }

    @Test
    public void RepublicAtWarFrontSideHasNoTakeCloneArmyCardAction() {
        var scn = GetScenario();
        var raw = scn.GetLSCard("raw");

        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertFalse(raw.isFlipped());
        assertFalse(scn.LSCardActionAvailable(raw, "Take [Clone Army] card into hand from Reserve Deck"));
    }

    // ----------------------------------------------------------------------------------------------------------
    // Flip this card if no complete [Clone Army] Missions on table.
    // ----------------------------------------------------------------------------------------------------------

    @Test
    public void AggressiveNegotiationsFlipsBackWhenNoCompleteCloneArmyMissionOnTable() {
        var scn = GetScenario();
        var raw = scn.GetLSCard("raw");
        var assault = scn.GetLSCard("assault");
        var junk = scn.GetLSCard("junk");

        scn.StartGame();
        PutCompleteCloneArmyMissionOnTable(scn);
        FlipToAggressiveNegotiationsDecliningDraw(scn);
        assertTrue(raw.isFlipped());

        scn.MoveCardsToTopOfLSUsedPile(junk);
        assertTrue(scn.GetStackedCards(assault).isEmpty());
        scn.PassControlActions();

        assertFalse(raw.isFlipped());
        assertEquals("Republic At War", raw.getTitle());
        assertTrue(messageSent(scn, "Aggressive Negotiations is flipped to Republic At War"));
    }
}
