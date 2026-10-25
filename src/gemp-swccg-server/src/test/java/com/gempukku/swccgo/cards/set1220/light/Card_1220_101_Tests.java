package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_101_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1220_101");
                    put("xwing", "1_146");
                    put("tatooine", "1_127");
                    put("saber", "9_90");
                    put("jungle", "4_86");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("destiny", "9_134");
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
    public void SonOfSkywalkerStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("luke").getBlueprint();

        assertEquals("Son Of Skywalker", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(5, card.getPower(), scn.epsilon);
        assertEquals(5, card.getAbility(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.LUKE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.DAGOBAH);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SonOfSkywalkerAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        scn.BoardAsPilot(xwing, luke);
        assertEquals(xwing.getBlueprint().getPower().intValue() + 3, scn.GetPower(xwing));
    }

    @Test
    public void SonOfSkywalkerMakesLukesLightsaberImmuneToOverload() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.AttachCardsTo(luke, saber);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), saber, Title.Overload));
    }

    @Test
    public void SonOfSkywalkerDeploysLukesLightsaberFromReserveDeck() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(saber);
        assertTrue(scn.LSCardActionAvailable(luke, "Deploy Luke's Lightsaber"));
        scn.LSUseCardAction(luke, "Deploy Luke's Lightsaber");
        chooseFromSearch(scn, saber);
        scn.PassAllResponses();
        assertEquals(luke, saber.getAttachedTo());
        assertEquals(Zone.ATTACHED, saber.getZone());
    }

    @Test
    public void SonOfSkywalkerLosesNoForceToYourDestinyWhileOnDagobah() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var jungle = scn.GetLSCard("jungle");
        var destiny = scn.GetDSCard("destiny");

        scn.StartGame();
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLocation(jungle, luke);
        scn.MoveCardsToLocation(jungle, destiny);
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(
                scn.gameState(), ModifierType.LIMIT_FORCE_LOSS_FROM_CARD, destiny).isEmpty());
    }

    @Test
    public void SonOfSkywalkerIsImmuneToAttritionLessThanFour() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), luke), scn.epsilon);
    }

    @Test
    public void SonOfSkywalkerMayDeployToADagobahLocation() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var jungle = scn.GetLSCard("jungle");

        scn.StartGame();
        scn.MoveLocationToTable(jungle);
        assertEquals(5, deployCostTo(scn, luke, jungle), scn.epsilon);
    }

    private float deployCostTo(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
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
