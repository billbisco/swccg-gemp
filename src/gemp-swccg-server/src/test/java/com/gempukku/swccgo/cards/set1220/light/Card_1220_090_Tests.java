package com.gempukku.swccgo.cards.set1220.light;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_090_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rieekan", "1220_90");
                    put("atgar", "3_72");
                    put("echoBay", "3_59");
                    put("haven", "5_24");
                    put("hoth", "3_55");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void GeneralCarlistRieekanStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("rieekan").getBlueprint();

        assertEquals("General Carlist Rieekan", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.GENERAL);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.WARRIOR);
            add(Icon.HOTH);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void GeneralCarlistRieekanPowersArtilleryWeaponsAtSameAndRelatedSitesAndAddsTwoForfeit() {
        var scn = GetScenario();
        var rieekan = scn.GetLSCard("rieekan");
        var atgar = scn.GetLSCard("atgar");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, rieekan, atgar);
        assertTrue(scn.game().getModifiersQuerying().isPowered(scn.gameState(), atgar));
        assertEquals(4, scn.GetForfeit(atgar));
    }

    @Test
    public void GeneralCarlistRieekanDeploysHothLocationFromReserveDeck() {
        var scn = GetScenario();
        var rieekan = scn.GetLSCard("rieekan");
        var echoBay = scn.GetLSCard("echoBay");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, rieekan);
        scn.EnsureLSForcePile(2);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(echoBay);
        assertTrue(scn.LSCardActionAvailable(rieekan, "Deploy card from Reserve Deck"));
        scn.LSUseCardAction(rieekan, "Deploy card from Reserve Deck");
        chooseFromSearch(scn, echoBay);
        scn.PassAllResponses();
        assertTrue(echoBay.getZone().isInPlay());
    }

    @Test
    public void GeneralCarlistRieekanDeploysHavenToHothFromReserveDeck() {
        var scn = GetScenario();
        var rieekan = scn.GetLSCard("rieekan");
        var haven = scn.GetLSCard("haven");
        var hoth = scn.GetLSCard("hoth");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLocation(site, rieekan);
        scn.EnsureLSForcePile(2);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(haven);
        assertTrue(scn.LSCardActionAvailable(rieekan, "Deploy Haven to Hoth"));
        scn.LSUseCardAction(rieekan, "Deploy Haven to Hoth");
        chooseFromSearch(scn, haven);
        scn.PassAllResponses();
        assertEquals(hoth, haven.getAttachedTo());
    }

    private void chooseFromSearch(VirtualTableScenario scn, PhysicalCardImpl card) {
        int safety = 0;
        while (safety++ < 16 && !scn.LSHasCardChoiceAvailable(card)) {
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else {
                break;
            }
        }
        scn.LSChooseCard(card);
    }
}
