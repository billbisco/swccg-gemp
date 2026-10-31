package com.gempukku.swccgo.cards.set1106.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1106_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hopping", "1106_10");
                    put("threepio", "1_5");
                    put("maker", "1_114");
                    put("savrip", "1_55");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void HoppingMadStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("hopping").getBlueprint();

        assertEquals("Hopping Mad", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.EFFECT);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HoppingMadDeploysOnNonEpisodeIC3PO() {
        var scn = GetScenario();
        var hopping = scn.GetLSCard("hopping");
        var threepio = scn.GetLSCard("threepio");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, threepio);
        scn.MoveCardsToLSHand(hopping);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(hopping));
        scn.LSPlayCard(hopping);
        scn.LSChooseCard(threepio);
        scn.PassAllResponses();
        assertEquals(threepio, hopping.getAttachedTo());
    }

    @Test
    public void HoppingMadTakesThankTheMakerFromReserveDeck() {
        var scn = GetScenario();
        var hopping = scn.GetLSCard("hopping");
        var threepio = scn.GetLSCard("threepio");
        var maker = scn.GetLSCard("maker");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, threepio);
        scn.AttachCardsTo(threepio, hopping);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(maker);
        assertTrue(scn.LSCardActionAvailable(hopping, "Take card into hand from Reserve Deck"));
        scn.LSUseCardAction(hopping, "Take card into hand from Reserve Deck");
        int safety = 0;
        while (safety++ < 16 && !scn.LSHasCardChoiceAvailable(maker)) {
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            }
            else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            }
            else {
                break;
            }
        }
        scn.LSChooseCard(maker);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, maker);
    }

    @Test
    public void HoppingMadMakesSavripImmuneToAlterWhileC3POAtBattleground() {
        var scn = GetScenario();
        var hopping = scn.GetLSCard("hopping");
        var threepio = scn.GetLSCard("threepio");
        var savrip = scn.GetLSCard("savrip");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, threepio);
        scn.AttachCardsTo(threepio, hopping);
        scn.MoveCardsToLSSideOfTable(savrip);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), savrip, Title.Alter));
    }
}
