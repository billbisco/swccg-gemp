package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

public class Card_1210_089_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("boshek", "1210_89");
                    put("xwing", "1_146");
                    put("tatooine", "1_127");
                    put("cantina", "1_128");
                    put("device", "1_036");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
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
    public void BoShekStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("boshek").getBlueprint();

        assertEquals("BoShek", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BoShekAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var boshek = scn.GetLSCard("boshek");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        scn.BoardAsPilot(xwing, boshek);
        assertEquals(6, scn.GetPower(xwing));
    }

    @Test
    public void BoShekDeploysMinusTwoToCantina() {
        var scn = GetScenario();
        var boshek = scn.GetLSCard("boshek");
        var cantina = scn.GetLSCard("cantina");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLSHand(boshek);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        assertTrue(scn.LSCardPlayAvailable(boshek));
        scn.LSPlayCard(boshek);
        scn.LSChooseCard(cantina);
        scn.PassAllResponses();
        assertEquals(cantina, boshek.getAtLocation());
        assertEquals(3, scn.GetLSForcePileCount());
    }

    @Test
    public void BoShekTakesADeviceIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var boshek = scn.GetLSCard("boshek");
        var device = scn.GetLSCard("device");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boshek);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(device);
        assertTrue(scn.LSCardActionAvailable(boshek, "Take a device into hand from Reserve Deck"));
        scn.LSUseCardAction(boshek, "Take a device into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(device);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, device.getZone());
    }

    @Test
    public void BoShekIsImmuneToAttritionLessThanThree() {
        var scn = GetScenario();
        var boshek = scn.GetLSCard("boshek");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boshek);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), boshek), scn.epsilon);
    }

    protected VirtualTableScenario GetPersonaScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("boshek", "1210_89");
                    put("oldboshek", "1_4");
                    put("freighter", "1116_25");
                    put("tatooine", "1_127");
                    put("cantina", "1_128");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
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
    public void BoShekHasBoShekPersona() {
        var scn = GetPersonaScenario();
        var card = scn.GetLSCard("boshek").getBlueprint();
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.BOSHEK);
        }});
    }

    @Test
    public void BoShekCountsAsBoShekForBoSheksModifiedLightFreighter() {
        var scn = GetPersonaScenario();
        var boshek = scn.GetLSCard("boshek");
        var freighter = scn.GetLSCard("freighter");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, freighter);
        scn.BoardAsPilot(freighter, boshek);
        // "While BoShek piloting: maneuver ... +2" (Filters.persona(BOSHEK))
        assertEquals(5, scn.GetManeuver(freighter));
    }

    @Test
    public void BoShekMayDeployWhileNoOtherVersionIsOnTable() {
        var scn = GetPersonaScenario();
        var boshek = scn.GetLSCard("boshek");
        var cantina = scn.GetLSCard("cantina");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLSHand(boshek);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(8);
        assertTrue(scn.LSCardPlayAvailable(boshek, "Deploy"));
    }

    @Test
    public void BoShekMayNotDeployWhileAnotherBoShekIsOnTable() {
        var scn = GetPersonaScenario();
        var boshek = scn.GetLSCard("boshek");
        var oldboshek = scn.GetLSCard("oldboshek");
        var cantina = scn.GetLSCard("cantina");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, oldboshek);
        scn.MoveCardsToLSHand(boshek);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(8);
        assertFalse(scn.LSCardPlayAvailable(boshek, "Deploy"));
    }
}
