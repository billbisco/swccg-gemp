package com.gempukku.swccgo.cards.set1220.dark;

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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_016_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("chewie", "13_9");
                }},
                new HashMap<>() {{
                    put("bursk", "1220_16");
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
    public void SergeantMajorBurskStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("bursk").getBlueprint();

        assertEquals("Sergeant Major Bursk", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.WARRIOR);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SergeantMajorBurskDeploysToStartingSite() {
        var scn = GetScenario();
        var bursk = scn.GetDSCard("bursk");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(bursk);
        scn.EnsureDSForcePile(3);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(bursk));
        scn.DSPlayCard(bursk);
        scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, bursk.getAtLocation());
    }

    @Test
    public void SergeantMajorBurskPreventsMaintenanceCardsFromBattlingTheTurnTheyDeploy() {
        var scn = GetScenario();
        var bursk = scn.GetDSCard("bursk");
        var chewie = scn.GetLSCard("chewie");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bursk, trooper);
        scn.MoveCardsToLSHand(chewie);
        scn.EnsureLSForcePile(8);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(chewie));
        scn.LSPlayCard(chewie);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, chewie.getAtLocation());
        scn.SkipToPhase(Phase.BATTLE);
        assertFalse(scn.LSCanInitiateBattle(site));
    }

    @Test
    public void SergeantMajorBurskPlacesMaintenanceCardsOutOfPlayWhenTheyLeaveTable() {
        var scn = GetScenario();
        var bursk = scn.GetDSCard("bursk");
        var chewie = scn.GetLSCard("chewie");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bursk, chewie);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSExecuteAdHocEffect(chewie, new LoseCardFromTableEffect(
                new TopLevelGameTextAction(chewie, chewie.getCardId()), chewie));
        scn.PassAllResponses();
        assertEquals(Zone.OUT_OF_PLAY, chewie.getZone());
    }
}
