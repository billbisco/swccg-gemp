package com.gempukku.swccgo.cards.set1283.light;

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
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("cantina", "1283_25");
                    put("kyle", "1281_28");
                    put("moseisley", "1_133");
                    put("tatooine", "1_127");
                    put("kessel", "1_126");
                    put("falcon", "1_143");
                    put("han", "1_11");
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
    public void TatooineCantinaVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("cantina").getBlueprint();

        assertEquals(Title.Cantina, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CantinaMakesScoundrelsSmugglers() {
        var scn = GetScenario();
        var cantina = scn.GetLSCard("cantina");
        var kyle = scn.GetLSCard("kyle");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), kyle);
        assertFalse(scn.HasKeyword(kyle, Keyword.SMUGGLER));
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(cantina);
        assertTrue(scn.HasKeyword(kyle, Keyword.SMUGGLER));
    }

    @Test
    public void CantinaMayUseOneForceToDeployMosEisleyFromReserveDeck() {
        var scn = GetScenario();
        var cantina = scn.GetLSCard("cantina");
        var moseisley = scn.GetLSCard("moseisley");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(cantina);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(moseisley);
        scn.EnsureLSForcePile(3);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(cantina, "Mos Eisley"));
        scn.LSUseCardAction(cantina, "Mos Eisley");
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(moseisley)) {
            scn.LSChooseCard(moseisley);
        }
        scn.PassAllResponses();
        assertEquals(com.gempukku.swccgo.common.Zone.LOCATIONS, moseisley.getZone());
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void CantinaIsImmuneToRevolutionAndMayNotBeConverted() {
        var scn = GetScenario();
        var cantina = scn.GetLSCard("cantina");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(cantina);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), cantina, Title.Revolution));
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_CONVERTED, cantina).isEmpty());
    }

    @Test
    public void DarkSideLosesNoForceToForceDrainsAtLightSidesKessel() {
        var scn = GetScenario();
        var cantina = scn.GetLSCard("cantina");
        var tatooine = scn.GetLSCard("tatooine");
        var kessel = scn.GetLSCard("kessel");
        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(kessel, falcon);
        scn.BoardAsPilot(falcon, han);
        scn.SkipToLSTurn(Phase.CONTROL);
        int dsLifeForce = scn.GetDSLifeForceRemaining();
        scn.LSForceDrainAt(kessel);
        scn.PassAllResponses();
        assertEquals(dsLifeForce, scn.GetDSLifeForceRemaining());
    }

    @Test
    public void DarkSideLosesForceToForceDrainsAtKesselWithoutCantinaV() {
        var scn = GetScenario();
        var tatooine = scn.GetLSCard("tatooine");
        var kessel = scn.GetLSCard("kessel");
        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(kessel, falcon);
        scn.BoardAsPilot(falcon, han);
        scn.SkipToLSTurn(Phase.CONTROL);
        int dsLifeForce = scn.GetDSLifeForceRemaining();
        scn.LSForceDrainAt(kessel);
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Choose Force to lose")) {
            scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
            scn.PassAllResponses();
        }
        assertTrue(scn.GetDSLifeForceRemaining() < dsLifeForce);
    }
}
