package com.gempukku.swccgo.cards.set1220.light;

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

public class Card_1220_105_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("toryn", "1220_105");
                    put("xwing", "1_146");
                    put("tatooine", "1_127");
                    put("chewie", "13_9");
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
    public void TorynFarrStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("toryn").getBlueprint();

        assertEquals("Toryn Farr", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.HOTH);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TorynFarrAddsTwoToPowerOfAnythingShePilots() {
        var scn = GetScenario();
        var toryn = scn.GetLSCard("toryn");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        scn.BoardAsPilot(xwing, toryn);
        assertEquals(xwing.getBlueprint().getPower().intValue() + 2, scn.GetPower(xwing));
    }

    @Test
    public void TorynFarrPreventsMaintenanceCardsFromBattlingTheTurnTheyDeploy() {
        var scn = GetScenario();
        var toryn = scn.GetLSCard("toryn");
        var chewie = scn.GetLSCard("chewie");
        var trooper = scn.GetDSCard("trooper");
        var lsSite = scn.GetLSStartingLocation();
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(lsSite, toryn);
        scn.MoveCardsToLocation(site, trooper);
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
    public void TorynFarrPlacesMaintenanceCardsOutOfPlayWhenTheyLeaveTable() {
        var scn = GetScenario();
        var toryn = scn.GetLSCard("toryn");
        var chewie = scn.GetLSCard("chewie");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, toryn, chewie);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSExecuteAdHocEffect(chewie, new LoseCardFromTableEffect(
                new TopLevelGameTextAction(chewie, chewie.getCardId()), chewie));
        scn.PassAllResponses();
        assertEquals(Zone.OUT_OF_PLAY, chewie.getZone());
    }
}
