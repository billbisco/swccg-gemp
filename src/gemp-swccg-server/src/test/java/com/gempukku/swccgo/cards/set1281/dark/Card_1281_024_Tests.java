package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
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
import com.gempukku.swccgo.logic.modifiers.DeathMarkTotalModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_024_Tests {
    StartingSetup ContractKillersObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("killers", "1281_24");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("trooper", "1_28");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("coruscant", "7_275");
                    put("lair", "1281_22");
                    put("casino", "1281_21");
                    put("mara", "110_10");
                    put("aurra", "11_51");
                    put("vader", "1_168");
                    put("saber", "1281_25");
                    put("blaster", "1_317");
                    put("lostcard", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                ContractKillersObjective,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /**
     * Starts the game with an On The Hunt stand-in in the Reserve Deck (the real card is coded in a parallel wave),
     * so Contract Killers can satisfy its deployment requirements.
     */
    private static void startGame(VirtualTableScenario scn) {
        MissionTestSupport.putInReserve(scn, 98500, scn.DS, Vb81RestTestSupport.onTheHunt());
        scn.StartGame();
    }

    /**
     * Passes the rest of the Dark Side turn (through its end-of-turn responses) until the Light Side turn starts.
     */
    private static void endDarkSideTurn(VirtualTableScenario scn) {
        scn.SkipToPhase(Phase.DRAW);
        scn.PassDrawActions();
        for (int i = 0; i < 20 && !scn.LS.equals(scn.gameState().getCurrentPlayerId()); ++i) {
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertEquals(scn.LS, scn.gameState().getCurrentPlayerId());
    }

    private static PhysicalCardImpl putDeathMarkOnTable(VirtualTableScenario scn) {
        PhysicalCardImpl deathMark = MissionTestSupport.putInReserve(scn, 98501, scn.DS, Vb81RestTestSupport.deathMark());
        scn.MoveCardsToDSSideOfTable(deathMark);
        return deathMark;
    }

    /**
     * Flips Contract Killers by drawing destiny for (the stand-in) Death Mark during the Dark Side deploy phase.
     */
    private static PhysicalCardImpl flipByDeathMarkDestiny(VirtualTableScenario scn) {
        PhysicalCardImpl deathMark = putDeathMarkOnTable(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSUseCardAction(deathMark, "Draw destiny for Death Mark");
        scn.PassAllResponses();
        return deathMark;
    }

    @Test
    public void ContractKillersStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("killers").getBlueprint();

        assertEquals("Contract Killers", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.CLOUD_CITY);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void FearedThroughoutTheGalaxyStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        startGame(scn);
        var card = scn.GetDSCard("killers").getOtherSideBlueprint();

        assertEquals("Feared Throughout The Galaxy", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(7, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.CLOUD_CITY);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ContractKillersDeploysCoruscantAndSubCityLair() {
        var scn = GetScenario();
        var killers = scn.GetDSCard("killers");
        var coruscant = scn.GetDSCard("coruscant");
        var lair = scn.GetDSCard("lair");

        startGame(scn);
        assertEquals(Zone.SIDE_OF_TABLE, killers.getZone());
        assertTrue(coruscant.getZone().isInPlay());
        assertTrue(lair.getZone().isInPlay());
    }

    @Test
    public void ContractKillersMakesAurraMaraAndPlottingAliensAssassinsForRemainderOfGame() {
        var scn = GetScenario();
        var mara = scn.GetDSCard("mara");
        var aurra = scn.GetDSCard("aurra");
        var vader = scn.GetDSCard("vader");
        var lair = scn.GetDSCard("lair");

        startGame(scn);
        var plotter = MissionTestSupport.putInReserve(scn, 98502, scn.DS, Vb81RestTestSupport.plottingAlien());
        scn.MoveCardsToLocation(lair, mara, aurra, vader, plotter);
        assertTrue(scn.HasKeyword(mara, Keyword.ASSASSIN));
        assertTrue(scn.HasKeyword(aurra, Keyword.ASSASSIN));
        assertTrue(scn.HasKeyword(plotter, Keyword.ASSASSIN));
        assertFalse(scn.HasKeyword(vader, Keyword.ASSASSIN));

        flipByDeathMarkDestiny(scn);
        assertEquals("Feared Throughout The Galaxy", scn.GetDSCard("killers").getBlueprint().getTitle());
        assertTrue(scn.HasKeyword(mara, Keyword.ASSASSIN));
    }

    @Test
    public void ContractKillersYourCardsWithAbilityAreDeployPlusThreeExceptAssassins() {
        var scn = GetScenario();
        var mara = scn.GetDSCard("mara");
        var vader = scn.GetDSCard("vader");

        startGame(scn);
        assertEquals(vader.getBlueprint().getDeployCost() + 3, scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), vader), scn.epsilon);
        assertEquals(mara.getBlueprint().getDeployCost(), scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), mara), scn.epsilon);
    }

    @Test
    public void ContractKillersCharactersAddOneToDeathMarkTotalForEachWeaponOrDeviceCarried() {
        var scn = GetScenario();
        var mara = scn.GetDSCard("mara");
        var saber = scn.GetDSCard("saber");
        var blaster = scn.GetDSCard("blaster");
        var lair = scn.GetDSCard("lair");

        startGame(scn);
        scn.MoveCardsToLocation(lair, mara);
        assertEquals(0, DeathMarkTotalModifier.getDeathMarkTotalModifier(scn.gameState(), scn.game().getModifiersQuerying(), mara), scn.epsilon);
        scn.AttachCardsTo(mara, saber);
        scn.AttachCardsTo(mara, blaster);
        assertEquals(2, DeathMarkTotalModifier.getDeathMarkTotalModifier(scn.gameState(), scn.game().getModifiersQuerying(), mara), scn.epsilon);
    }

    @Test
    public void ContractKillersDeploysACoruscantBattlegroundFromReserveDeckOncePerTurn() {
        var scn = GetScenario();
        var killers = scn.GetDSCard("killers");
        var casino = scn.GetDSCard("casino");

        startGame(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(killers, "Deploy a Coruscant battleground from Reserve Deck"));
        scn.DSUseCardAction(killers, "Deploy a Coruscant battleground from Reserve Deck");
        assertTrue(Vb81RestTestSupport.passUntil(scn, () -> scn.DSHasCardChoiceAvailable(casino)));
        scn.DSChooseCard(casino);
        if (scn.DSDecisionAvailable("On which side")) {
            scn.DSDecided("0");
        }
        scn.PassAllResponses();
        assertTrue(casino.getZone().isInPlay());
        assertFalse(Vb81RestTestSupport.dsCardActionAvailable(scn, killers, "Deploy a Coruscant battleground from Reserve Deck"));
    }

    @Test
    public void ContractKillersFlipsWhenYouJustDrewDestinyForDeathMark() {
        var scn = GetScenario();
        var killers = scn.GetDSCard("killers");

        startGame(scn);
        assertEquals("Contract Killers", killers.getBlueprint().getTitle());
        flipByDeathMarkDestiny(scn);
        assertEquals("Feared Throughout The Galaxy", killers.getBlueprint().getTitle());
    }

    @Test
    public void ContractKillersFlipsWhenAssassinsControlTwoBattlegrounds() {
        var scn = GetScenario();
        var killers = scn.GetDSCard("killers");
        var lair = scn.GetDSCard("lair");
        var casino = scn.GetDSCard("casino");
        var mara = scn.GetDSCard("mara");
        var aurra = scn.GetDSCard("aurra");

        startGame(scn);
        scn.MoveLocationToTable(casino);
        scn.MoveCardsToLocation(lair, mara);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals("Contract Killers", killers.getBlueprint().getTitle());
        scn.MoveCardsToLocation(casino, aurra);
        scn.SkipToPhase(Phase.BATTLE);
        assertEquals("Feared Throughout The Galaxy", killers.getBlueprint().getTitle());
    }

    @Test
    public void ContractKillersDoesNotFlipFromBattlegroundsWhileOpponentsNonDroidCharacterAtABattlegroundSite() {
        var scn = GetScenario();
        var killers = scn.GetDSCard("killers");
        var lair = scn.GetDSCard("lair");
        var casino = scn.GetDSCard("casino");
        var mara = scn.GetDSCard("mara");
        var aurra = scn.GetDSCard("aurra");
        var trooper = scn.GetLSCard("trooper");

        startGame(scn);
        scn.MoveLocationToTable(casino);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), trooper);
        scn.MoveCardsToLocation(lair, mara);
        scn.MoveCardsToLocation(casino, aurra);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertEquals("Contract Killers", killers.getBlueprint().getTitle());
    }

    @Test
    public void FearedThroughoutTheGalaxyOpponentLosesOneForceDuringYourControlPhase() {
        var scn = GetScenario();
        var killers = scn.GetDSCard("killers");

        startGame(scn);
        flipByDeathMarkDestiny(scn);
        assertEquals("Feared Throughout The Galaxy", killers.getBlueprint().getTitle());
        scn.MoveCardsToTopOfDSUsedPile(scn.GetDSCard("lostcard"));
        scn.SkipToDSTurn(Phase.ACTIVATE);
        int lsLife = scn.GetLSLifeForceRemaining();
        scn.SkipToPhase(Phase.CONTROL);
        Vb81RestTestSupport.payForceLossFromReserveDeck(scn);
        assertEquals(lsLife - 1, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void FearedThroughoutTheGalaxyAddsOneBattleDestinyWithTwoAssassinsInBattle() {
        var scn = GetScenario();
        var killers = scn.GetDSCard("killers");
        var mara = scn.GetDSCard("mara");
        var aurra = scn.GetDSCard("aurra");
        var luke = scn.GetLSCard("luke");
        var lair = scn.GetDSCard("lair");

        startGame(scn);
        scn.MoveCardsToLocation(lair, mara, aurra, luke);
        flipByDeathMarkDestiny(scn);
        assertEquals("Feared Throughout The Galaxy", killers.getBlueprint().getTitle());
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(lair);
        assertEquals(2, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void FearedThroughoutTheGalaxyAddsNoBattleDestinyWithOneAssassinInBattle() {
        var scn = GetScenario();
        var killers = scn.GetDSCard("killers");
        var mara = scn.GetDSCard("mara");
        var luke = scn.GetLSCard("luke");
        var lair = scn.GetDSCard("lair");

        startGame(scn);
        scn.MoveCardsToLocation(lair, mara, luke);
        flipByDeathMarkDestiny(scn);
        assertEquals("Feared Throughout The Galaxy", killers.getBlueprint().getTitle());
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(lair);
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void FearedThroughoutTheGalaxyAssassinGainsReputationWhenOpponentJustLostACharacterThere() {
        var scn = GetScenario();
        var killers = scn.GetDSCard("killers");
        var mara = scn.GetDSCard("mara");
        var trooper = scn.GetLSCard("trooper");
        var lostcard = scn.GetDSCard("lostcard");
        var lair = scn.GetDSCard("lair");

        startGame(scn);
        scn.MoveCardsToLocation(lair, mara, trooper);
        scn.MoveCardsToTopOfDSLostPile(lostcard);
        flipByDeathMarkDestiny(scn);
        assertEquals("Feared Throughout The Galaxy", killers.getBlueprint().getTitle());
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(lair);
        scn.PrepareDSDestiny(5);
        scn.PrepareLSDestiny(0);
        scn.SkipToDamageSegment(true);
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSChooseCard(trooper);
        assertTrue(Vb81RestTestSupport.passUntil(scn, () -> scn.DSCardActionAvailable(killers, "Have an assassin gain 'reputation'")));
        scn.DSUseCardAction(killers, "Have an assassin gain 'reputation'");
        assertTrue(Vb81RestTestSupport.passUntil(scn, () -> scn.DSHasCardChoiceAvailable(lostcard)));
        scn.DSChooseCard(lostcard);
        scn.PassAllResponses();

        assertEquals(mara, lostcard.getStackedOn());
        assertEquals(Zone.STACKED_FACE_DOWN, lostcard.getZone());
    }

    @Test
    public void FearedThroughoutTheGalaxyAssassinWithReputationMayAddOneToForceDrain() {
        var scn = GetScenario();
        var killers = scn.GetDSCard("killers");
        var mara = scn.GetDSCard("mara");
        var lostcard = scn.GetDSCard("lostcard");
        var lair = scn.GetDSCard("lair");

        startGame(scn);
        scn.MoveCardsToLocation(lair, mara);
        scn.StackCardsFaceDownOn(mara, lostcard);
        flipByDeathMarkDestiny(scn);
        assertEquals("Feared Throughout The Galaxy", killers.getBlueprint().getTitle());
        scn.SkipToDSTurn(Phase.CONTROL);
        Vb81RestTestSupport.payForceLossFromReserveDeck(scn);
        int lsLife = scn.GetLSLifeForceRemaining();
        int drain = (int) scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), lair, scn.DS);
        scn.DSForceDrainAt(lair);
        assertTrue(Vb81RestTestSupport.passUntil(scn, () -> scn.DSCardActionAvailable(killers, "Add 1 to Force drain")));
        scn.DSUseCardAction(killers, "Add 1 to Force drain");
        scn.PassAllResponses();
        Vb81RestTestSupport.payForceLossFromReserveDeck(scn);
        assertEquals(lsLife - drain - 1, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void FearedThroughoutTheGalaxyFlipsBackAtEndOfTurnIfOpponentsCharacterAtBattlegroundSite() {
        var scn = GetScenario();
        var killers = scn.GetDSCard("killers");
        var trooper = scn.GetLSCard("trooper");

        startGame(scn);
        var deathMark = flipByDeathMarkDestiny(scn);
        assertEquals("Feared Throughout The Galaxy", killers.getBlueprint().getTitle());
        scn.MoveCardsToTopOfDSUsedPile(deathMark);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), trooper);
        endDarkSideTurn(scn);
        assertEquals("Contract Killers", killers.getBlueprint().getTitle());
    }

    @Test
    public void FearedThroughoutTheGalaxyDoesNotFlipBackWhileDeathMarkOnYourSideOfTable() {
        var scn = GetScenario();
        var killers = scn.GetDSCard("killers");
        var trooper = scn.GetLSCard("trooper");

        startGame(scn);
        flipByDeathMarkDestiny(scn);
        assertEquals("Feared Throughout The Galaxy", killers.getBlueprint().getTitle());
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), trooper);
        endDarkSideTurn(scn);
        assertEquals("Feared Throughout The Galaxy", killers.getBlueprint().getTitle());
    }
}
