package com.gempukku.swccgo.cards.set1220.light;

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

public class Card_1220_157_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("vision", "1220_157");
                    put("yavin", "1_135");
                }},
                new HashMap<>() {{
                    put("detention", "1_284");
                    put("core", "1_283");
                    put("warroom", "1_287");
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
    public void VisoredVisionStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("vision").getBlueprint();

        assertEquals("Visored Vision", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_2, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
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
    public void VisoredVisionUsedActivatesFiveForceWhenOpponentHasThreeNonBattlegroundLocations() {
        var scn = GetScenario();
        var vision = scn.GetLSCard("vision");
        var detention = scn.GetDSCard("detention");
        var core = scn.GetDSCard("core");
        var warroom = scn.GetDSCard("warroom");

        scn.StartGame();
        scn.MoveLocationToTable(detention);
        scn.MoveLocationToTable(core);
        scn.MoveLocationToTable(warroom);
        scn.MoveCardsToLSHand(vision);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.GetLSReserveDeckCount() >= 5);
        int forceBefore = scn.GetLSForcePileCount();
        int reserveBefore = scn.GetLSReserveDeckCount();

        assertTrue(scn.LSCardPlayAvailable(vision, "Activate 5 Force"));
        scn.LSPlayCard(vision, "Activate 5 Force");
        scn.PassAllResponses();

        assertEquals(forceBefore + 5, scn.GetLSForcePileCount());
        assertEquals(reserveBefore - 5, scn.GetLSReserveDeckCount());
        assertInZone(Zone.USED_PILE, vision);
    }

    @Test
    public void VisoredVisionUsedDoesNotOfferWhenOpponentHasFewerThanThreeNonBattlegroundLocations() {
        var scn = GetScenario();
        var vision = scn.GetLSCard("vision");
        var detention = scn.GetDSCard("detention");
        var core = scn.GetDSCard("core");

        scn.StartGame();
        scn.MoveLocationToTable(detention);
        scn.MoveLocationToTable(core);
        scn.MoveCardsToLSHand(vision);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertFalse(scn.LSCardPlayAvailable(vision, "Activate 5 Force"));
    }

    @Test
    public void VisoredVisionLostDeploysBattlegroundLocationFromReserveDeck() {
        var scn = GetScenario();
        var vision = scn.GetLSCard("vision");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveCardsToLSHand(vision);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(yavin);

        assertTrue(scn.LSCardPlayAvailable(vision, "Deploy battleground location from Reserve Deck"));
        scn.LSPlayCard(vision, "Deploy battleground location from Reserve Deck");
        for (int i = 0; i < 20; i++) {
            if (yavin.getZone() == Zone.LOCATIONS) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(yavin)) {
                scn.LSChooseCard(yavin);
                continue;
            }
            if (scn.LSDecisionAvailable("which side") || scn.LSDecisionAvailable("On which side")) {
                scn.LSChoose("Left");
                continue;
            }
            scn.PassAllResponses();
        }
        scn.PassAllResponses();

        assertEquals(Zone.LOCATIONS, yavin.getZone());
        assertInZone(Zone.LOST_PILE, vision);
    }
}
