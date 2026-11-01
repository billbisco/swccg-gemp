package com.gempukku.swccgo.cards.set1115.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1115_017_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("vaporator", "1115_17");
                    put("farm", "1_132");
                    put("luke", "1_19");
                    put("stew", "1_072");
                    put("milk", "2_046");
                    put("camie", "7_009");
                    put("owen", "1_22");
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
    public void LarsVaporatorStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("vaporator").getBlueprint();

        assertEquals("Lars' Vaporator", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_LOCATION);
            add(Keyword.DEPLOYS_ON_SITE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LarsVaporatorDeploysOnAFarm() {
        var scn = GetScenario();
        var vaporator = scn.GetLSCard("vaporator");
        var farm = scn.GetLSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLSHand(vaporator);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(vaporator));
        scn.LSPlayCard(vaporator);
        scn.LSChooseCard(farm);
        scn.PassAllResponses();
        assertEquals(farm, vaporator.getAttachedTo());
    }

    @Test
    public void LarsVaporatorMakesCharactersAtSameSiteImmuneToSandwhirl() {
        var scn = GetScenario();
        var vaporator = scn.GetLSCard("vaporator");
        var luke = scn.GetLSCard("luke");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var farm = scn.GetLSCard("farm");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.AttachCardsTo(farm, vaporator);
        scn.MoveCardsToLocation(farm, luke);
        scn.MoveCardsToLocation(marketplace, stormtrooper);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), luke, Title.Sandwhirl));
        assertFalse(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), stormtrooper, Title.Sandwhirl));
    }

    @Test
    public void LarsVaporatorTakesACardIntoHandFromReserveDeckWhenPlayingBeruStew() {
        var scn = GetScenario();
        var vaporator = scn.GetLSCard("vaporator");
        var stew = scn.GetLSCard("stew");
        var camie = scn.GetLSCard("camie");
        var farm = scn.GetLSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.AttachCardsTo(farm, vaporator);
        scn.MoveCardsToLSHand(stew);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(6);
        scn.EnsureDSForcePile(6);
        scn.MoveCardsToTopOfLSReserveDeck(camie);
        assertTrue(scn.LSPlayLostInterruptAvailable(stew) || scn.LSCardPlayAvailable(stew));
        if (scn.LSPlayLostInterruptAvailable(stew)) {
            scn.LSPlayLostInterrupt(stew);
        } else {
            scn.LSPlayCard(stew);
        }
        boolean took = false;
        for (int i = 0; i < 12; i++) {
            if (scn.LSGetDecision() != null) {
                try {
                    var actions = scn.LSGetADParamAsList("actionText");
                    if (actions != null) {
                        for (String a : actions) {
                            if (a != null && a.toLowerCase().contains("reserve deck")) {
                                scn.LSChooseAction("Take a card into hand from Reserve Deck");
                                took = true;
                                break;
                            }
                        }
                    }
                } catch (RuntimeException ignored) {
                }
                if (took) {
                    break;
                }
                if (scn.LSDecisionAvailable("optional")) {
                    scn.LSPass();
                    continue;
                }
            }
            if (scn.DSGetDecision() != null && scn.DSDecisionAvailable("optional")) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(took);
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(camie);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, camie.getZone());
    }

    @Test
    public void LarsVaporatorTakesACardIntoHandFromReserveDeckWhenPlayingBlueMilk() {
        var scn = GetScenario();
        var vaporator = scn.GetLSCard("vaporator");
        var milk = scn.GetLSCard("milk");
        var owen = scn.GetLSCard("owen");
        var farm = scn.GetLSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.AttachCardsTo(farm, vaporator);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(4);
        scn.EnsureDSForcePile(4);
        scn.MoveCardsToLSHand(milk);
        scn.MoveCardsToTopOfLSReserveDeck(owen);
        scn.LSPass();
        if (scn.DSGetDecision() != null) {
            scn.DSPass();
        }
        boolean milkReady = scn.GetLSAvailableActions().stream().anyMatch(a -> a != null
                && (a.contains("USED:") || a.toLowerCase().contains("blue milk") || a.toLowerCase().contains("activate")));
        assertTrue(milkReady);
        if (scn.GetLSAvailableActions().stream().anyMatch(a -> a != null && a.contains("USED:"))) {
            scn.LSPlayUsedInterrupt(milk);
        } else {
            scn.LSPlayCard(milk);
        }
        boolean took = false;
        for (int i = 0; i < 16; i++) {
            if (scn.LSGetDecision() != null) {
                String text = scn.LSGetDecision().getText();
                if (text != null && text.toLowerCase().contains("choose player")) {
                    scn.LSChoose("Light side");
                    continue;
                }
                try {
                    var actions = scn.LSGetADParamAsList("actionText");
                    if (actions != null) {
                        for (String a : actions) {
                            if (a != null && a.toLowerCase().contains("reserve deck")) {
                                scn.LSChooseAction("Take a card into hand from Reserve Deck");
                                took = true;
                                break;
                            }
                        }
                    }
                } catch (RuntimeException ignored) {
                }
                if (took) {
                    break;
                }
                if (scn.LSDecisionAvailable("optional")) {
                    scn.LSPass();
                    continue;
                }
            }
            if (scn.DSGetDecision() != null && scn.DSDecisionAvailable("optional")) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(took);
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(owen);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, owen.getZone());
    }
}
