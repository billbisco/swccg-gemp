package com.gempukku.swccgo.cards.set1210.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Imperial Entanglements / No One To Stop Us This Time (Legacy VB1, 1210_68).
 */
public class Card_1210_068_Tests {

    static final StartingSetup ImperialEntanglementsObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("entanglements", "1210_68");
                put("tatooine", "1_289");
                put("devastator", "1_301");
                put("moseisley", "1_295");
                put("jundland", "1_293");
                put("jp", "6_171");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            for (int i = 0; i < 8; i++) {
                if (scn.DSDecisionAvailable("Tatooine battleground site")
                        || (scn.DSHasCardChoiceAvailable(scn.GetDSCard("moseisley"))
                        && scn.DSHasCardChoiceAvailable(scn.GetDSCard("jundland")))) {
                    assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("moseisley")));
                    assertTrue("Jabba's Palace site must not be a starting-deploy choice",
                            scn.DSHasCardChoiceNotAvailable(scn.GetDSCard("jp")));
                    scn.DSChooseCard(scn.GetDSCard("moseisley"));
                    continue;
                }
                if (scn.DSDecisionAvailable("On which side")) {
                    scn.DSChoose("Left");
                    continue;
                }
                break;
            }
            assertFalse("Objective could not find Tatooine in Reserve Deck",
                    scn.DSDecisionAvailable("Verify Reserve Deck after unsuccessful attempt to 'Choose Tatooine to deploy'"));
            assertFalse("Objective could not find Devastator in Reserve Deck",
                    scn.DSDecisionAvailable("Verify Reserve Deck after unsuccessful attempt to 'Choose Devastator to deploy'"));
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("rebel1", "1_28");
                    put("rebel2", "1_28");
                }},
                new HashMap<>() {{
                    put("farm", "1_294");
                    put("dantooine", "1_282");
                    put("garindan", "1_177");
                    put("slave", "109_8");
                    put("st1", "1_194");
                    put("st2", "1_194");
                    put("st3", "1_194");
                    put("dewback", "7_312");
                    put("occupation", "7_244");
                    put("trouble", "1_256");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                ImperialEntanglementsObjective,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void ImperialEntanglementsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("entanglements").getBlueprint();

        assertEquals("Imperial Entanglements", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.OBJECTIVE);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void NoOneToStopUsThisTimeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        scn.StartGame();
        var card = scn.GetDSCard("entanglements").getOtherSideBlueprint();

        assertEquals("No One To Stop Us This Time", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(7, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.OBJECTIVE);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ImperialEntanglementsStartDeploysTatooineDevastatorAndNonJabbasPalaceBattlegroundSite() {
        var scn = GetScenario();
        var objective = scn.GetDSCard("entanglements");
        var tatooine = scn.GetDSCard("tatooine");
        var devastator = scn.GetDSCard("devastator");
        var moseisley = scn.GetDSCard("moseisley");
        var jp = scn.GetDSCard("jp");

        scn.StartGame();

        assertInZone(Zone.SIDE_OF_TABLE, objective);
        assertFalse(objective.isFlipped());
        assertEquals("Imperial Entanglements", objective.getTitle());
        assertEquals(Zone.LOCATIONS, tatooine.getZone());
        assertEquals("Tatooine", tatooine.getTitle());
        assertAtLocation(tatooine, devastator);
        assertEquals(Zone.LOCATIONS, moseisley.getZone());
        assertEquals("Tatooine: Mos Eisley", moseisley.getTitle());
        assertInZone(Zone.RESERVE_DECK, jp);
        assertTrue(scn.AwaitingDSActivatePhaseActions());
    }

    @Test
    public void ImperialEntanglementsRemainderMayNotDeploySystemNonImperialCharacterNonImperialStarshipOrJabbasPalaceSite() {
        var scn = GetScenario();
        var dantooine = scn.GetDSCard("dantooine");
        var garindan = scn.GetDSCard("garindan");
        var slave = scn.GetDSCard("slave");
        var jp = scn.GetDSCard("jp");
        var st1 = scn.GetDSCard("st1");

        scn.StartGame();
        scn.MoveCardsToDSHand(dantooine, garindan, slave, jp, st1);
        scn.EnsureDSForcePile(10);
        scn.SkipToPhase(Phase.DEPLOY);

        assertFalse(scn.DSCardPlayAvailable(dantooine));
        assertFalse(scn.DSCardPlayAvailable(garindan));
        assertFalse(scn.DSCardPlayAvailable(slave));
        assertFalse(scn.DSCardPlayAvailable(jp));
        assertTrue(scn.DSCardPlayAvailable(st1));
    }

    @Test
    public void ImperialEntanglementsDownloadsTatooineBattlegroundSiteFromReserveOncePerTurn() {
        var scn = GetScenario();
        var objective = scn.GetDSCard("entanglements");
        var jundland = scn.GetDSCard("jundland");

        scn.StartGame();
        scn.SkipToPhase(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(jundland);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSCardActionAvailable(objective, "Deploy Tatooine battleground site from Reserve Deck"));

        scn.DSUseCardAction(objective, "Deploy Tatooine battleground site from Reserve Deck");
        if (scn.DSDecisionAvailable("Choose card")) {
            assertTrue(scn.DSHasCardChoiceAvailable(jundland));
            scn.DSChooseCard(jundland);
        }
        for (int i = 0; i < 4 && scn.DSDecisionAvailable("On which side"); i++) {
            scn.DSChoose("Left");
        }
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, jundland.getZone());

        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(objective, "Deploy Tatooine battleground site from Reserve Deck"));

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(objective, "Deploy Tatooine battleground site from Reserve Deck"));
    }

    @Test
    public void ImperialEntanglementsOpponentLosesNoForceToTatooineOccupationOrForceDrainAtTatooineSystem() {
        var scn = GetScenario();
        var tatooine = scn.GetDSCard("tatooine");
        var moseisley = scn.GetDSCard("moseisley");
        var jundland = scn.GetDSCard("jundland");
        var farm = scn.GetDSCard("farm");
        var occupation = scn.GetDSCard("occupation");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");

        scn.StartGame();
        scn.MoveLocationToTable(jundland);
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(moseisley, st1);
        scn.MoveCardsToLocation(jundland, st2);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.MoveCardsToDSHand(occupation);
        scn.EnsureDSForcePile(8);
        if (scn.DSCardPlayAvailable(occupation)) {
            scn.DSPlayCard(occupation);
            if (scn.DSHasCardChoiceAvailable(tatooine)) {
                scn.DSChooseCard(tatooine);
            }
            scn.PassAllResponses();
        }
        else {
            scn.AttachCardsTo(tatooine, occupation);
        }
        assertTrue(scn.IsAttachedTo(tatooine, occupation));

        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        int lsBeforeOccupation = scn.GetLSForcePileCount() + scn.GetLSReserveDeckCount();
        assertEquals(0, scn.game().getModifiersQuerying().getForceToLoseFromCardLimit(scn.gameState(), scn.LS, occupation), scn.epsilon);
        if (scn.DSCardActionAvailable(occupation, "Make opponent lose")) {
            scn.DSUseCardAction(occupation, "Make opponent lose");
            scn.PassAllResponses();
        }
        assertEquals(lsBeforeOccupation, scn.GetLSForcePileCount() + scn.GetLSReserveDeckCount());

        if (!scn.AwaitingDSControlPhaseActions()) {
            scn.SkipToDSTurn(Phase.CONTROL);
        }
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertTrue(scn.DSForceDrainAvailable(tatooine));
        int lsBeforeDrain = scn.GetLSForcePileCount() + scn.GetLSReserveDeckCount();
        assertEquals(0, scn.game().getModifiersQuerying().getForceToLoseFromForceDrainLimit(scn.gameState(), scn.LS, tatooine), scn.epsilon);
        scn.DSForceDrainAt(tatooine);
        scn.PassAllResponses();
        assertEquals(lsBeforeDrain, scn.GetLSForcePileCount() + scn.GetLSReserveDeckCount());
    }

    @Test
    public void ImperialEntanglementsFlipsWhenYouControlThreeTatooineSitesAndOpponentControlsLessThanThree() {
        var scn = GetScenario();
        FlipToNoOneToStopUsThisTime(scn);
        assertEquals("No One To Stop Us This Time", scn.GetDSCard("entanglements").getTitle());
        assertTrue(scn.GetDSCard("entanglements").isFlipped());
    }

    @Test
    public void NoOneToStopUsThisTimeTrooperRidingDewbackAddsBattleDestiny() {
        var scn = GetScenario();
        var moseisley = scn.GetDSCard("moseisley");
        var st1 = scn.GetDSCard("st1");
        var dewback = scn.GetDSCard("dewback");
        var luke = scn.GetLSCard("luke");

        FlipToNoOneToStopUsThisTime(scn);
        scn.MoveCardsToLocation(moseisley, dewback, st1, luke);
        scn.BoardAsPassenger(dewback, st1);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(moseisley));
        scn.DSInitiateBattle(moseisley);
        // Stormtrooper ability 1 is below the normal battle-destiny threshold of 4, so the +1 is the only draw.
        assertEquals(1, scn.game().getModifiersQuerying().getNumBattleDestinyDraws(scn.gameState(), scn.DS, false, true));
    }

    @Test
    public void NoOneToStopUsThisTimeDoesNotAddBattleDestinyWithoutRidingDewback() {
        var scn = GetScenario();
        var moseisley = scn.GetDSCard("moseisley");
        var st1 = scn.GetDSCard("st1");
        var dewback = scn.GetDSCard("dewback");
        var luke = scn.GetLSCard("luke");

        FlipToNoOneToStopUsThisTime(scn);
        scn.MoveCardsToLocation(moseisley, dewback, st1, luke);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(moseisley));
        scn.DSInitiateBattle(moseisley);
        assertEquals(0, scn.game().getModifiersQuerying().getNumBattleDestinyDraws(scn.gameState(), scn.DS, false, true));
    }

    @Test
    public void NoOneToStopUsThisTimeLocalTroubleOfferedAtExteriorTatooineSite() {
        var scn = GetScenario();
        var moseisley = scn.GetDSCard("moseisley");
        var trouble = scn.GetDSCard("trouble");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var luke = scn.GetLSCard("luke");

        FlipToNoOneToStopUsThisTime(scn);
        scn.MoveCardsToDSHand(trouble);
        scn.MoveCardsToLocation(moseisley, st1, st2, luke);
        scn.EnsureDSForcePile(2);
        scn.SkipToPhase(Phase.BATTLE);

        assertTrue(scn.DSAnyDecisionsAvailable()
                && (scn.DSActionAvailable("Initiate Local Trouble battle")
                || scn.DSActionAvailable("Local Trouble")
                || scn.DSCardPlayAvailable(trouble)));
    }

    @Test
    public void NoOneToStopUsThisTimePeeksAtExactlyXOccupiedTatooineLocationsTakesOneAndReshuffles() {
        var scn = GetScenario();
        var objective = scn.GetDSCard("entanglements");
        var moseisley = scn.GetDSCard("moseisley");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var st3 = scn.GetDSCard("st3");
        var peek1 = scn.GetDSCard("garindan");
        var peek2 = scn.GetDSCard("slave");
        var peek3 = scn.GetDSCard("dantooine");

        FlipToNoOneToStopUsThisTime(scn);
        scn.MoveOutOfPlay(st2);
        scn.MoveOutOfPlay(st3);
        scn.MoveCardsToLocation(moseisley, st1);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(peek3, peek2, peek1);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertTrue(scn.DSCardActionAvailable(objective, "Peek at top cards of Reserve Deck"));

        scn.DSUseCardAction(objective, "Peek at top cards of Reserve Deck");
        assertFalse(scn.DSDecisionAvailable("Choose number of cards to peek at"));
        assertTrue(scn.DSDecisionAvailable("Choose card"));
        assertTrue(scn.DSHasCardChoiceAvailable(peek1));
        assertTrue(scn.DSHasCardChoiceAvailable(peek2));
        assertTrue(scn.DSHasCardChoiceNotAvailable(peek3));
        scn.DSChooseCard(peek1);
        scn.PassAllResponses();

        assertInHand(peek1);
        assertInZone(Zone.RESERVE_DECK, peek2);
        assertInZone(Zone.RESERVE_DECK, peek3);
    }

    @Test
    public void NoOneToStopUsThisTimeOpponentsCharactersRequirePlusOneForceToMoveFromTatooineSitesUsingLandspeed() {
        var scn = GetScenario();
        var moseisley = scn.GetDSCard("moseisley");
        var jundland = scn.GetDSCard("jundland");
        var st1 = scn.GetDSCard("st1");
        var luke = scn.GetLSCard("luke");

        FlipToNoOneToStopUsThisTime(scn);
        scn.MoveLocationToTable(jundland);
        scn.MoveCardsToLocation(moseisley, st1, luke);

        float dsCost = scn.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scn.gameState(), st1, moseisley, jundland, false, 0);
        float lsCost = scn.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scn.gameState(), luke, moseisley, jundland, false, 0);
        assertEquals(dsCost + 1, lsCost, scn.epsilon);
        assertTrue(lsCost > dsCost);
    }

    @Test
    public void NoOneToStopUsThisTimeFlipsBackIfOpponentControlsMoreTatooineSites() {
        var scn = GetScenario();
        var objective = scn.GetDSCard("entanglements");
        var moseisley = scn.GetDSCard("moseisley");
        var jundland = scn.GetDSCard("jundland");
        var farm = scn.GetDSCard("farm");
        var st2 = scn.GetDSCard("st2");
        var st3 = scn.GetDSCard("st3");
        var luke = scn.GetLSCard("luke");
        var rebel1 = scn.GetLSCard("rebel1");

        FlipToNoOneToStopUsThisTime(scn);
        assertTrue(objective.isFlipped());

        scn.MoveLocationToTable(farm);
        scn.MoveOutOfPlay(st2);
        scn.MoveOutOfPlay(st3);
        scn.MoveCardsToLocation(jundland, luke);
        scn.MoveCardsToLocation(farm, rebel1);

        scn.SkipToDSTurn();
        assertFalse(objective.isFlipped());
        assertEquals("Imperial Entanglements", objective.getTitle());
    }

    /**
     * Occupies three Tatooine sites and ends Dark Side's Activate phase so the required flip trigger fires.
     */
    private void FlipToNoOneToStopUsThisTime(VirtualTableScenario scn) {
        var objective = scn.GetDSCard("entanglements");
        var moseisley = scn.GetDSCard("moseisley");
        var jundland = scn.GetDSCard("jundland");
        var farm = scn.GetDSCard("farm");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var st3 = scn.GetDSCard("st3");

        scn.StartGame();
        scn.MoveLocationToTable(jundland);
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(moseisley, st1);
        scn.MoveCardsToLocation(jundland, st2);
        scn.MoveCardsToLocation(farm, st3);

        if (!objective.isFlipped()) {
            assertTrue(scn.AwaitingDSActivatePhaseActions());
            scn.DSActivateMaxForceAndPass();
            if (scn.LSDecisionAvailable("DOUBLE_SIDED_CARD_FLIPPED") || scn.DSDecisionAvailable("DOUBLE_SIDED_CARD_FLIPPED")) {
                scn.PassResponses("DOUBLE_SIDED_CARD_FLIPPED");
            }
        }
        assertTrue(objective.isFlipped());
        assertEquals("No One To Stop Us This Time", objective.getTitle());
    }
}
