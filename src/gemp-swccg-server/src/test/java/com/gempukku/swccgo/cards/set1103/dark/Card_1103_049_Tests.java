package com.gempukku.swccgo.cards.set1103.dark;

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

public class Card_1103_049_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("rifle", "1_153");
                    put("cantina", "1_128");
                }},
                new HashMap<>() {{
                    put("davin", "1103_49");
                    put("looksir", "1210_64");
                    put("trouble", "1_256");
                    put("st1", "1_194");
                    put("st2", "1210_22");
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
    public void TrooperDavinFelthStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("davin").getBlueprint();

        assertEquals("Trooper Davin Felth", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SANDTROOPER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.WARRIOR);
            add(Icon.A_NEW_HOPE);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TrooperDavinFelthTakesLookSirDroidsDuringControlPhase() {
        var scn = GetScenario();
        var davin = scn.GetDSCard("davin");
        var looksir = scn.GetDSCard("looksir");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        if (scn.DSDecisionAvailable("Choose starting interrupt")) {
            scn.DSPass();
        }
        scn.MoveCardsToLocation(site, davin);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(2);
        scn.MoveCardsToTopOfDSReserveDeck(looksir);
        assertTrue(scn.DSCardActionAvailable(davin, "Take card into hand from Reserve Deck"));
        scn.DSUseCardAction(davin, "Take card into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(looksir);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, looksir.getZone());
        assertFalse(scn.GetDSAvailableActions().toString().contains("Take card into hand from Reserve Deck"));
        assertFalse(scn.GetDSAvailableActions().toString().contains("Lose 1 Force to retrieve card"));
    }

    @Test
    public void TrooperDavinFelthLosesOneForceToRetrieveLookSirDroids() {
        var scn = GetScenario();
        var davin = scn.GetDSCard("davin");
        var looksir = scn.GetDSCard("looksir");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        if (scn.DSDecisionAvailable("Choose starting interrupt")) {
            scn.DSPass();
        }
        scn.MoveCardsToLocation(site, davin);
        scn.MoveCardsToTopOfDSLostPile(looksir);
        scn.EnsureDSForcePile(2);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(2);
        int lifeBefore = scn.GetDSLifeForceRemaining();
        assertTrue(scn.DSCardActionAvailable(davin, "Lose 1 Force to retrieve card"));
        scn.DSUseCardAction(davin, "Lose 1 Force to retrieve card");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        if (scn.DSDecisionAvailable("FORCE_LOSS") || scn.DSDecisionAvailable("ABOUT_TO_LOSE")) {
            scn.PassResponses("FORCE_LOSS_INITIATED");
            scn.PassResponses("ABOUT_TO_LOSE");
        }
        if (scn.GetDSForcePileCount() > 0 && scn.DSHasCardChoiceAvailable(scn.GetTopOfDSForcePile())) {
            scn.DSChooseCard(scn.GetTopOfDSForcePile());
        }
        else if (scn.GetDSReserveDeckCount() > 0 && scn.DSHasCardChoiceAvailable(scn.GetTopOfDSReserveDeck())) {
            scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
        }
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(looksir)) {
            scn.DSChooseCard(looksir);
        }
        scn.PassAllResponses();
        assertEquals(Zone.HAND, looksir.getZone());
        assertEquals(lifeBefore - 1, scn.GetDSLifeForceRemaining());
    }

    @Test
    public void TrooperDavinFelthPreventsOpponentWeaponsDuringLocalTroubleBattles() {
        var scn = GetScenario();
        var davin = scn.GetDSCard("davin");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var trouble = scn.GetDSCard("trouble");
        var luke = scn.GetLSCard("luke");
        var rifle = scn.GetLSCard("rifle");
        var cantina = scn.GetLSCard("cantina");

        scn.StartGame();
        if (scn.DSDecisionAvailable("Choose starting interrupt")) {
            scn.DSPass();
        }
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, st1, st2, luke);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), davin);
        scn.AttachCardsTo(luke, rifle);
        scn.MoveCardsToDSHand(trouble);
        scn.EnsureDSForcePile(2);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCardPlayAvailable(trouble));
        scn.DSPlayCard(trouble);
        scn.DSChooseCards(st1, st2);
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        assertTrue(scn.gameState().isDuringLocalTroubleBattle());
        assertFalse(scn.game().getModifiersQuerying().notProhibitedFromFiringWeapons(scn.gameState(), luke));
    }
}
