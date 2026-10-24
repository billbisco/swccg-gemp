package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.common.CardSubtype;
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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_071_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("chute", "1_089");
                    put("trash", "1_125");
                    put("moseisley", "1_133");
                }},
                new HashMap<>() {{
                    put("thinner", "1210_71");
                    put("trooper", "1_194");
                    put("detention", "1_284");
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
    public void WereAllGonnaBeALotThinnerStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("thinner").getBlueprint();

        assertEquals("We're All Gonna Be A Lot Thinner!", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WereAllGonnaBeALotThinnerUsedAtStartOfActivateUsesOpponentsForceIcons() {
        var scn = GetScenario();
        var thinner = scn.GetDSCard("thinner");
        var moseisley = scn.GetLSCard("moseisley");

        scn.StartGame();
        scn.MoveLocationToTable(moseisley);
        scn.MoveCardsToDSHand(thinner);
        scn.SkipToDSTurn();

        float before = scn.game().getModifiersQuerying().getTotalForceGeneration(scn.gameState(), scn.DS);
        assertTrue(scn.DSCardPlayAvailable(thinner));
        scn.DSPlayCard(thinner);
        scn.PassAllResponses();

        float after = scn.game().getModifiersQuerying().getTotalForceGeneration(scn.gameState(), scn.DS);
        assertTrue(after > before);
        assertTrue(scn.AwaitingDSActivatePhaseActions());
        int activated = scn.DSActivateMaxForceAndPass();
        assertEquals((int) after, activated);
        assertInZone(Zone.USED_PILE, thinner);
    }

    @Test
    public void WereAllGonnaBeALotThinnerUsedIsNotOfferedLaterInActivatePhase() {
        var scn = GetScenario();
        var thinner = scn.GetDSCard("thinner");

        scn.StartGame();
        scn.MoveCardsToDSHand(thinner);
        scn.SkipToDSTurn();

        assertTrue(scn.DSCardPlayAvailable(thinner));
        scn.DSPass();
        if (scn.LSDecisionAvailable("optional")) {
            scn.LSPass();
        }
        assertTrue(scn.AwaitingDSActivatePhaseActions());
        assertFalse(scn.DSCardPlayAvailable(thinner));
    }

    @Test
    public void WereAllGonnaBeALotThinnerLostCancelsIntoTheGarbageChuteFlyboy() {
        var scn = GetScenario();
        var thinner = scn.GetDSCard("thinner");
        var chute = scn.GetLSCard("chute");
        var trash = scn.GetLSCard("trash");
        var detention = scn.GetDSCard("detention");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(trash);
        scn.MoveLocationToTable(detention);
        scn.MoveCardsToLocation(detention, luke, trooper);
        scn.MoveCardsToLSHand(chute);
        scn.MoveCardsToDSHand(thinner);
        scn.SkipToLSTurn(Phase.BATTLE);

        scn.LSInitiateBattle(detention);
        scn.PassForceUseResponses();
        if (scn.DSDecisionAvailable("Battle just initiated")) {
            scn.DSPass();
        }
        assertTrue(scn.LSCardPlayAvailable(chute));
        scn.LSPlayCard(chute);
        assertTrue(scn.DSCardPlayAvailable(thinner));
        scn.DSPlayCard(thinner);
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, thinner);
        assertTrue(chute.getZone() == Zone.LOST_PILE || chute.getZone() == Zone.TOP_OF_LOST_PILE
                || chute.getZone() == Zone.USED_PILE || chute.getZone() == Zone.TOP_OF_USED_PILE);
        assertTrue(scn.gameState().isDuringBattle());
    }
}
