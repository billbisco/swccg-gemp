package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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

public class Card_1220_096_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("neb", "1220_96");
                    put("cctrooper", "5_3");
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
    public void NebDuloStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("neb").getBlueprint();

        assertEquals("Neb Dulo", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void NebDuloMakesYourAliensHereDefenseValuePlusTwo() {
        var scn = GetScenario();
        var neb = scn.GetLSCard("neb");
        var cctrooper = scn.GetLSCard("cctrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, cctrooper);
        int before = scn.GetDefense(cctrooper);
        scn.MoveCardsToLocation(site, neb);
        assertEquals(4, scn.GetDefense(neb));
        assertEquals(before + 2, scn.GetDefense(cctrooper));
    }

    @Test
    public void NebDuloPreventsYourRepFromBeingTargetedByWeaponsDuringBattle() {
        var scn = GetScenario();
        var neb = scn.GetLSCard("neb");
        var cctrooper = scn.GetLSCard("cctrooper");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, neb, cctrooper, stormtrooper);
        scn.game().getGameState().setRep(scn.LS, cctrooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(
                scn.gameState(), ModifierType.MAY_NOT_BE_TARGETED_BY, cctrooper).isEmpty());
    }
}
