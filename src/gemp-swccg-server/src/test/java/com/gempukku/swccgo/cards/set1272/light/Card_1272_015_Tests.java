package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("kota", "1272_15");
                    put("quigon", "11_10");
                    put("luke", "1_19");
                    put("saber", "1_155");
                    put("camp", "1_131");
                    put("jungle", "1_134");
                }},
                new HashMap<>() {{
                    put("galen", "601_81");
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

    private static boolean inUsedPile(PhysicalCardImpl card) {
        return card.getZone() == Zone.USED_PILE || card.getZone() == Zone.TOP_OF_USED_PILE;
    }

    @Test
    public void RahmKotaBlindJediStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("kota").getBlueprint();

        assertEquals("Rahm Kota, Blind Jedi", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(6, card.getAbility(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.INSURGENT);
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RahmKotaBlindJediMakesEpisodeIJediLost() {
        var scn = GetScenario();
        var kota = scn.GetLSCard("kota");
        var quigon = scn.GetLSCard("quigon");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, quigon, luke);
        scn.MoveCardsToLSHand(kota);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(6);

        scn.LSDeployCard(kota);
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();

        assertTrue(quigon.getZone() == Zone.LOST_PILE || quigon.getZone() == Zone.TOP_OF_LOST_PILE);
        assertEquals(site, kota.getAtLocation());
        assertEquals(site, luke.getAtLocation());
    }

    @Test
    public void RahmKotaBlindJediMakesInsurgentsImmuneToAttritionLessThanFiveAtBattlegrounds() {
        var scn = GetScenario();
        var kota = scn.GetLSCard("kota");
        var camp = scn.GetLSCard("camp");
        var jungle = scn.GetLSCard("jungle");

        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLocation(camp, kota);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), kota), scn.epsilon);

        scn.MoveCardsToLocation(jungle, kota);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), kota), scn.epsilon);
    }

    @Test
    public void RahmKotaBlindJediPeeksAtOwnReserveDeckAndPlacesCardOnUsedPileOncePerTurn() {
        var scn = GetScenario();
        var kota = scn.GetLSCard("kota");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), kota);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(luke);

        assertTrue(scn.LSCardActionAvailable(kota, "Peek at top card of Reserve Deck"));
        scn.LSUseCardAction(kota, "Peek at top card of Reserve Deck");
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Top card of Reserve Deck"));
        scn.LSDismissRevealedCards();
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Used Pile"));
        scn.LSChooseYes();
        scn.PassAllResponses();
        assertTrue(inUsedPile(luke));

        if (scn.AwaitingDSDeployPhaseActions()) {
            scn.DSPass();
        }
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(kota, "Peek at top card of Reserve Deck"));
    }

    @Test
    public void RahmKotaBlindJediMayPeekAtOpponentsReserveDeckWhileGalenOnTable() {
        var scn = GetScenario();
        var kota = scn.GetLSCard("kota");
        var luke = scn.GetLSCard("luke");
        var galen = scn.GetDSCard("galen");
        var trooper = scn.GetDSCard("trooper");

        //test1: with Galen on table, Kota may choose either Reserve Deck
        //test2: the peeked card goes on top of its owner's Used Pile
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), kota);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), galen);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(luke);
        scn.MoveCardsToTopOfDSReserveDeck(trooper);

        assertTrue(scn.LSCardActionAvailable(kota, "Peek at top card of Reserve Deck"));
        scn.LSUseCardAction(kota, "Peek at top card of Reserve Deck");
        assertTrue(scn.LSDecisionAvailable("Choose card pile")); //test1
        assertTrue(scn.LSHasCardChoiceAvailable(luke)); //test1
        assertTrue(scn.LSHasCardChoiceAvailable(trooper)); //test1
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Top card of Dark Side Player's Reserve Deck"));
        scn.LSDismissRevealedCards();
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Used Pile"));
        scn.LSChooseYes();
        scn.PassAllResponses();
        assertTrue(inUsedPile(trooper)); //test2
        assertEquals(scn.DS, trooper.getZoneOwner()); //test2
        assertEquals(luke, scn.GetTopOfLSReserveDeck());
    }

    @Test
    public void RahmKotaBlindJediPeeksOnlyAtYourReserveDeckWithoutGalen() {
        var scn = GetScenario();
        var kota = scn.GetLSCard("kota");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), kota);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(luke);
        scn.MoveCardsToTopOfDSReserveDeck(trooper);

        scn.LSUseCardAction(kota, "Peek at top card of Reserve Deck");
        scn.PassAllResponses();
        assertFalse(scn.LSDecisionAvailable("Choose card pile"));
        assertTrue(scn.LSDecisionAvailable("Top card of Reserve Deck"));
        scn.LSDismissRevealedCards();
        scn.PassAllResponses();
        scn.LSChooseYes();
        scn.PassAllResponses();
        assertTrue(inUsedPile(luke));
        assertEquals(trooper, scn.GetTopOfDSReserveDeck());
    }

    @Test
    public void RahmKotaBlindJediMayNotCarryWeapons() {
        var scn = GetScenario();
        var kota = scn.GetLSCard("kota");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), kota, luke);
        assertTrue(scn.game().getModifiersQuerying().prohibitedFromCarrying(scn.gameState(), kota, saber));
        assertFalse(scn.game().getModifiersQuerying().prohibitedFromCarrying(scn.gameState(), luke, saber));
    }
}
