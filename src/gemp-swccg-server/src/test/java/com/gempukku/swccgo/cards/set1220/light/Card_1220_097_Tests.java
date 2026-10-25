package com.gempukku.swccgo.cards.set1220.light;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1220_097_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("leia", "1220_97");
                    put("han", "1_11");
                    put("loa", "5_28");
                    put("blaster", "1_156");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void PrincessLeiaStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("leia").getBlueprint();

        assertEquals("Princess Leia", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.LEIA);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.CLOUD_CITY);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void PrincessLeiaAddsOneBattleDestinyIfWithHan() {
        var scn = GetScenario();
        var leia = scn.GetLSCard("leia");
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia, han, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(2, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void PrincessLeiaAddsOneBattleDestinyIfWithVader() {
        var scn = GetScenario();
        var leia = scn.GetLSCard("leia");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia, vader);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(2, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void PrincessLeiaIsImmuneToAttritionLessThanFour() {
        var scn = GetScenario();
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), leia), scn.epsilon);
    }

    @Test
    public void PrincessLeiaDeploysLeiaOfAlderaanFromReserveDeckDuringDeployPhase() {
        var scn = GetScenario();
        var leia = scn.GetLSCard("leia");
        var loa = scn.GetLSCard("loa");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(loa);
        assertTrue(scn.LSCardActionAvailable(leia, "Deploy Leia Of Alderaan or Reflection"));
        scn.LSUseCardAction(leia, "Deploy Leia Of Alderaan or Reflection");
        chooseFromSearch(scn, loa);
        scn.PassAllResponses();
        assertEquals(leia, loa.getAttachedTo());
    }

    @Test
    public void PrincessLeiaDeploysMatchingBlasterFromReserveDeckDuringDeployPhase() {
        var scn = GetScenario();
        var leia = scn.GetLSCard("leia");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(blaster);
        assertTrue(scn.LSCardActionAvailable(leia, "Deploy matching blaster on Leia"));
        scn.LSUseCardAction(leia, "Deploy matching blaster on Leia");
        chooseFromSearch(scn, blaster);
        scn.PassAllResponses();
        assertEquals(leia, blaster.getAttachedTo());
        assertEquals(Zone.ATTACHED, blaster.getZone());
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
