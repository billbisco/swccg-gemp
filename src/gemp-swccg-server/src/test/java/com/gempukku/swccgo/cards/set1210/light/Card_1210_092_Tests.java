package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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

public class Card_1210_092_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("c3po", "1210_92");
                    put("r2", "2_14");
                    put("effect", "1_66");
                    put("rebel", "1_28");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                    put("rifle", "1_312");
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
    public void C3POStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("c3po").getBlueprint();

        assertEquals("C-3PO (See-Threepio)", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertTrue(card.getModelTypes().contains(ModelType.PROTOCOL));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void C3POMayNotBeTargetedByWeaponsWhileInBattleAtSiteWithR2D2() {
        var scn = GetScenario();
        var c3po = scn.GetLSCard("c3po");
        var r2 = scn.GetLSCard("r2");
        var rebel = scn.GetLSCard("rebel");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var rifle = scn.GetDSCard("rifle");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, c3po, r2, rebel, stormtrooper);
        scn.AttachCardsTo(stormtrooper, rifle);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        assertTrue(scn.DSCardActionAvailable(rifle, "Fire"));
        scn.DSUseCardAction(rifle, "Fire");
        assertFalse(scn.DSHasCardChoiceAvailable(c3po));
        scn.DSPass();
    }

    @Test
    public void C3POPlacesNonImmuneToAlterEffectsInUsedPileOncePerGame() {
        var scn = GetScenario();
        var c3po = scn.GetLSCard("c3po");
        var effect = scn.GetLSCard("effect");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, c3po);
        scn.MoveCardsToLSSideOfTable(effect);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(c3po, "Place your Effects in Used Pile"));
        scn.LSUseCardAction(c3po, "Place your Effects in Used Pile");
        scn.PassAllResponses();
        assertTrue(effect.getZone() == Zone.USED_PILE || effect.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
