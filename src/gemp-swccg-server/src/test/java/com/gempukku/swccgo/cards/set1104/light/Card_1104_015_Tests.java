package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
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

public class Card_1104_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("r5", "1104_15");
                    put("corvette", "1_140");
                    put("tatooine", "1_127");
                    put("shawn", "3_19");
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
    public void R5M2StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("r5").getBlueprint();

        assertEquals("R5-M2 (Arfive-Emmtoo)", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(0, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.ASTROMECH);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.NAV_COMPUTER);
            add(Icon.HOTH);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void R5M2MakesYourCapitalStarshipsDeployMinusOne() {
        var scn = GetScenario();
        var r5 = scn.GetLSCard("r5");
        var corvette = scn.GetLSCard("corvette");
        var tatooine = scn.GetLSCard("tatooine");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(site, r5);
        assertEquals(3, deployCostTo(scn, corvette, tatooine), scn.epsilon);
    }

    @Test
    public void R5M2MakesYourCapitalStarshipsDestinyPlusTwoWhenDrawnForWeaponOrBattleDestiny() {
        var scn = GetScenario();
        var r5 = scn.GetLSCard("r5");
        var corvette = scn.GetLSCard("corvette");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r5);
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(
                scn.gameState(), ModifierType.DESTINY_WHEN_DRAWN_FOR_BATTLE_DESTINY, corvette).isEmpty());
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(
                scn.gameState(), ModifierType.DESTINY_WHEN_DRAWN_FOR_WEAPON_DESTINY, corvette).isEmpty());
    }

    @Test
    public void R5M2DeploysShawnValdezForFreeToSameSiteOncePerGame() {
        var scn = GetScenario();
        var r5 = scn.GetLSCard("r5");
        var shawn = scn.GetLSCard("shawn");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(shawn);
        assertTrue(scn.LSCardActionAvailable(r5, "Deploy Shawn Valdez for free"));
        scn.LSUseCardAction(r5, "Deploy Shawn Valdez for free");
        chooseFromSearch(scn, shawn);
        scn.PassAllResponses();
        assertEquals(site, shawn.getAtLocation());
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(r5, "Deploy Shawn Valdez for free"));
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
