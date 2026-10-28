package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.common.CardSubtype;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_071_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("jungle", "1_137");
                    put("throne", "1_138");
                    put("warroom", "1_139");
                }},
                new HashMap<>() {{
                    put("voyeur", "1220_71");
                    put("kessel", "1_288");
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
    public void VoyeurStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("voyeur").getBlueprint();

        assertEquals("Voyeur", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_2, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void VoyeurUsedActivatesFiveForceWhenOpponentHasThreeNonBattlegroundLocations() {
        var scn = GetScenario();
        var voyeur = scn.GetDSCard("voyeur");
        var jungle = scn.GetLSCard("jungle");
        var throne = scn.GetLSCard("throne");
        var warroom = scn.GetLSCard("warroom");

        scn.StartGame();
        scn.MoveLocationToTable(jungle);
        scn.MoveLocationToTable(throne);
        scn.MoveLocationToTable(warroom);
        scn.MoveCardsToDSHand(voyeur);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.GetDSReserveDeckCount() >= 5);
        int forceBefore = scn.GetDSForcePileCount();
        int reserveBefore = scn.GetDSReserveDeckCount();

        assertTrue(scn.DSCardPlayAvailable(voyeur, "Activate 5 Force"));
        scn.DSPlayCard(voyeur, "Activate 5 Force");
        scn.PassAllResponses();

        assertEquals(forceBefore + 5, scn.GetDSForcePileCount());
        assertEquals(reserveBefore - 5, scn.GetDSReserveDeckCount());
        assertInZone(Zone.USED_PILE, voyeur);
    }

    @Test
    public void VoyeurUsedDoesNotOfferWhenOpponentHasFewerThanThreeNonBattlegroundLocations() {
        var scn = GetScenario();
        var voyeur = scn.GetDSCard("voyeur");
        var jungle = scn.GetLSCard("jungle");
        var throne = scn.GetLSCard("throne");

        scn.StartGame();
        scn.MoveLocationToTable(jungle);
        scn.MoveLocationToTable(throne);
        scn.MoveCardsToDSHand(voyeur);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertFalse(scn.DSCardPlayAvailable(voyeur, "Activate 5 Force"));
    }

    @Test
    public void VoyeurLostDeploysBattlegroundLocationFromReserveDeck() {
        var scn = GetScenario();
        var voyeur = scn.GetDSCard("voyeur");
        var kessel = scn.GetDSCard("kessel");

        scn.StartGame();
        scn.MoveCardsToDSHand(voyeur);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(kessel);

        assertTrue(scn.DSCardPlayAvailable(voyeur, "Deploy battleground location"));
        scn.DSPlayCard(voyeur, "Deploy battleground location");
        for (int i = 0; i < 20; i++) {
            if (kessel.getZone() == Zone.LOCATIONS) {
                break;
            }
            if (scn.DSHasCardChoiceAvailable(kessel)) {
                scn.DSChooseCard(kessel);
                continue;
            }
            if (scn.DSDecisionAvailable("which side") || scn.DSDecisionAvailable("On which side")) {
                scn.DSChoose("Left");
                continue;
            }
            scn.PassAllResponses();
        }
        scn.PassAllResponses();

        assertEquals(Zone.LOCATIONS, kessel.getZone());
        assertInZone(Zone.LOST_PILE, voyeur);
    }

    @Test
    public void VoyeurLostDoesNotOfferOutsideDeployPhase() {
        var scn = GetScenario();
        var voyeur = scn.GetDSCard("voyeur");
        var kessel = scn.GetDSCard("kessel");

        scn.StartGame();
        scn.MoveCardsToDSHand(voyeur);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(kessel);

        assertFalse(scn.DSCardPlayAvailable(voyeur, "Deploy battleground location"));
    }
}
