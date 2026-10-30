package com.gempukku.swccgo.cards.set1102.dark;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1102_040_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("labria", "1102_40");
                    put("undercover", "2_129");
                    put("revealed", "1_194");
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
    public void LabriaStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("labria").getBlueprint();

        assertEquals("Labria", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.INFORMATION_BROKER);
            add(Keyword.SPY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LabriaDeploysUndercoverOnSelfOncePerGame() {
        var scn = GetScenario();
        var labria = scn.GetDSCard("labria");
        var undercover = scn.GetDSCard("undercover");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, labria);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(undercover);

        assertTrue(scn.DSCardActionAvailable(labria, "Deploy Undercover from Reserve Deck"));
        scn.DSUseCardAction(labria, "Deploy Undercover from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(undercover);
        scn.PassAllResponses();
        assertTrue(labria.isUndercover());
        assertEquals(labria, undercover.getAttachedTo());
        assertFalse(scn.GetDSAvailableActions().toString().contains("Deploy Undercover from Reserve Deck"));
    }

    @Test
    public void LabriaRevealsTopOfReserveDeckAndPlacesItOnForcePile() {
        var scn = GetScenario();
        var labria = scn.GetDSCard("labria");
        var revealed = scn.GetDSCard("revealed");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, labria);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(revealed);

        assertTrue(scn.DSCardActionAvailable(labria, "Reveal top card of Reserve Deck"));
        scn.DSUseCardAction(labria, "Reveal top card of Reserve Deck");
        scn.PassAllResponses();
        scn.DSPass();
        scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("Choose card pile to put revealed card on top of"));
        scn.DSChoose("Force Pile");
        scn.PassAllResponses();
        assertEquals(Zone.TOP_OF_FORCE_PILE, revealed.getZone());
    }
}
