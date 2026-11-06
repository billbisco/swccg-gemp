package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import com.gempukku.swccgo.common.Zone;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1230_105_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("kechu", "1230_105");
                    put("leebo", "10_9");
                    put("any", "1_11");
                    put("luke", "1_19");
                    put("dismantle", "1220_118");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                    put("vader", "1_168");
                    put("garindan", "1_177");
                }},
                10, 10,
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
    public void KeChuKeKukutaStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("kechu").getBlueprint();

        assertEquals("Ke Chu Ke Kukuta?", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JABBAS_PALACE);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void KeChuKeKukutaPlaysWhenYouHaveAnInformationBrokerOnTable() {
        var scn = GetScenario();
        var kechu = scn.GetLSCard("kechu");
        var leebo = scn.GetLSCard("leebo");
        var any = scn.GetLSCard("any");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leebo);
        scn.MoveCardsToLSHand(kechu);
        scn.MoveCardsToTopOfLSReserveDeck(any);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardPlayAvailable(kechu));
    }

    @Test public void KeChuKeKukutaSuccessfulDrawMayPayOneAndSearchAnyCard() {
        var scn = PrepareDraw(0, 1);
        int reserve = scn.GetLSReserveDeckCount();
        scn.LSChooseYes(); scn.PassAllResponses();
        scn.LSChooseCard(scn.GetLSCard("any")); scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetLSCard("any"));
        assertEquals(0, scn.GetLSForcePileCount());
        assertEquals(reserve - 1, scn.GetLSReserveDeckCount());
        assertInZone(Zone.USED_PILE, scn.GetLSCard("kechu"));
    }
    @Test public void KeChuKeKukutaSuccessfulDrawMayDeclineWithoutPayingOrSearching() {
        var scn = PrepareDraw(0, 1);
        int reserve = scn.GetLSReserveDeckCount();
        scn.LSChooseNo(); scn.PassAllResponses();
        assertEquals(1, scn.GetLSForcePileCount()); assertEquals(reserve, scn.GetLSReserveDeckCount());
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("any"));
        assertInZone(Zone.USED_PILE, scn.GetLSCard("kechu"));
    }
    @Test public void KeChuKeKukutaEqualDestinyFailsWithoutPaymentOrSearch() {
        var scn = PrepareDraw(1, 1); scn.PassAllResponses();
        assertFalse(scn.LSChoiceAvailable("Yes")); assertEquals(1, scn.GetLSForcePileCount());
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("any"));
        assertInZone(Zone.USED_PILE, scn.GetLSCard("kechu"));
    }
    @Test public void KeChuKeKukutaLowerDestinyFails() {
        var scn = PrepareDraw(2, 1); scn.PassAllResponses();
        assertFalse(scn.LSChoiceAvailable("Yes")); assertEquals(1, scn.GetLSForcePileCount());
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("any"));
    }
    @Test public void KeChuKeKukutaCanDrawWithNoForceButCannotSearch() {
        var scn = PrepareDraw(0, 0); scn.PassAllResponses();
        assertFalse(scn.LSChoiceAvailable("Yes")); assertEquals(0, scn.GetLSForcePileCount());
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("any"));
    }
    @Test public void KeChuKeKukutaNeedsYourInformationBrokerOnTable() {
        var scn = GetScenario(); scn.StartGame();
        scn.MoveCardsToLSHand(scn.GetLSCard("kechu"), scn.GetLSCard("leebo"));
        scn.SkipToLSTurn(Phase.CONTROL); assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("kechu")));
    }
    private VirtualTableScenario PrepareDraw(int characters, int force) {
        var scn = GetScenario(); scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leebo"));
        scn.MoveCardsToLSHand(scn.GetLSCard("kechu"));
        if (characters > 0) scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("stormtrooper"));
        if (characters > 1) scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("any"));
        scn.EnsureLSForcePile(force); scn.DrainLSForcePileTo(force);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLSHand(scn.GetLSCard("any"), scn.GetLSCard("luke")); scn.EnsureLSForcePile(force); scn.DrainLSForcePileTo(force);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("any"));
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("luke")); // Printed destiny 1.
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("kechu"));
        return scn;
    }

    @Test public void KeChuKeKukutaCountsOpponentsUndercoverCharactersOnTable() {
        var scn = GetScenario(); scn.StartGame(); var spy = scn.GetDSCard("garindan");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leebo"), scn.GetLSCard("luke"), spy);
        scn.gameState().putUndercover(spy);
        scn.MoveCardsToLSHand(scn.GetLSCard("kechu")); scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(2); scn.DrainLSForcePileTo(2); scn.PrepareLSDestiny(1);
        assertTrue(spy.isUndercover());
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("kechu"));
        assertFalse(scn.LSChoiceAvailable("Yes")); assertEquals(2, scn.GetLSForcePileCount());
        assertInZone(Zone.USED_PILE, scn.GetLSCard("kechu"));
    }

    @Test public void KeChuKeKukutaMayDrawWhenOptionalReserveDeckSearchIsBlocked() {
        var scn = GetScenario(); scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leebo"));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("dismantle")); scn.MoveCardsToLSHand(scn.GetLSCard("kechu"));
        scn.SkipToLSTurn(Phase.CONTROL); scn.EnsureLSForcePile(2); scn.DrainLSForcePileTo(2); scn.PrepareLSDestiny(1);
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("kechu"));
        assertFalse(scn.LSChoiceAvailable("Yes")); assertEquals(2, scn.GetLSForcePileCount());
        assertInZone(Zone.USED_PILE, scn.GetLSCard("kechu"));
        assertInZone(Zone.USED_PILE, scn.GetLSDestiny(1));
    }
}
