package com.gempukku.swccgo.cards.set1115.light;

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
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1115_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("owen", "1115_21");
                    put("vaporator", "1210_118");
                    put("hydro", "1210_117");
                    put("farm", "1_132");
                    put("stew", "1_072");
                    put("crop", "1_3");
                    put("handcard", "1_17");
                    put("saber", "1_155");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
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
    public void OwenLarsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("owen").getBlueprint();

        assertEquals("Owen Lars", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void OwenLarsMakesOpponentActivateNoForceFromBeruStew() {
        var scn = GetScenario();
        var owen = scn.GetLSCard("owen");
        var stew = scn.GetLSCard("stew");
        var farm = scn.GetLSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, owen);
        scn.MoveCardsToLSHand(stew);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSFiller(1));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSFiller(1));
        assertTrue(scn.game().getModifiersQuerying().hasGameTextModification(scn.gameState(), stew,
                ModifyGameTextType.LEGACY__OWEN_LARS_V__OPPONENT_ACTIVATES_NO_FORCE_FROM_BERU_STEW));
        int dsForce = scn.GetDSForcePileCount();
        int lsForce = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(stew));
        scn.LSPlayCard(stew);
        scn.PassAllResponses();
        assertEquals(dsForce, scn.GetDSForcePileCount());
        assertTrue(scn.GetLSForcePileCount() > lsForce);
    }

    @Test
    public void OwenLarsExchangesACardFromHandWithACropWhilePresentWithLarsVaporator() {
        var scn = GetScenario();
        var owen = scn.GetLSCard("owen");
        var vaporator = scn.GetLSCard("vaporator");
        var hydro = scn.GetLSCard("hydro");
        var farm = scn.GetLSCard("farm");
        var crop = scn.GetLSCard("crop");
        var handcard = scn.GetLSCard("handcard");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, owen);
        scn.AttachCardsTo(farm, vaporator, hydro);
        scn.StackCardsOn(hydro, crop);
        scn.MoveCardsToLSHand(handcard, luke);
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(owen, "Exchange a card from hand with a 'crop'"));
        scn.LSUseCardAction(owen, "Exchange a card from hand with a 'crop'");
        scn.PassForceUseResponses();
        scn.LSChooseCard(handcard);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, crop.getZone());
        assertEquals(Zone.STACKED, handcard.getZone());
    }

    @Test
    public void OwenLarsPreventsLightsabersFromDeployingHereWhilePresentWithLarsVaporator() {
        var scn = GetScenario();
        var owen = scn.GetLSCard("owen");
        var vaporator = scn.GetLSCard("vaporator");
        var farm = scn.GetLSCard("farm");
        var saber = scn.GetLSCard("saber");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, owen, luke);
        scn.MoveCardsToLSHand(saber);
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(saber));
        scn.AttachCardsTo(farm, vaporator);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(saber));
    }
}
