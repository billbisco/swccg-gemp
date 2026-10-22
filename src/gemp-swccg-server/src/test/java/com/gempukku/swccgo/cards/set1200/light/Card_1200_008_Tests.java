package com.gempukku.swccgo.cards.set1200.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1200_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("dont", "1200_8");
                    put("immediate", "9_44");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("icestorm", "3_104");
                    put("sandwhirl", "6_148");
                    put("stomach", "8_135");
                    put("mob", "9_129");
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
    public void DontDoThatAgainVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("dont").getBlueprint();

        assertEquals("Don't Do That Again", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.DEFENSIVE_SHIELD);
            add(Icon.LEGACY_BLOCK_D);
        }});
        assertEquals(ExpansionSet.VIRTUAL_SHIELDS, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DontDoThatAgainVCancelsIceStormAndSandwhirl() {
        var scn = GetScenario();
        var dont = scn.GetLSCard("dont");
        var icestorm = scn.GetDSCard("icestorm");
        var sandwhirl = scn.GetDSCard("sandwhirl");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(dont);
        scn.MoveCardsToDSSideOfTable(icestorm);
        AdvanceTableChanged(scn);
        assertInZone(Zone.LOST_PILE, icestorm);
        scn.MoveCardsToDSSideOfTable(sandwhirl);
        AdvanceTableChanged(scn);
        assertInZone(Zone.LOST_PILE, sandwhirl);
    }

    @Test
    public void DontDoThatAgainVCancelsAlwaysThinkingWhenPlayedToCancelAForceDrain() {
        var scn = GetScenario();
        var dont = scn.GetLSCard("dont");
        var stomach = scn.GetDSCard("stomach");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(dont);
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToDSHand(stomach);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(site));
        scn.LSForceDrainAt(site);
        scn.LSPass();
        assertTrue(dsMayPlay(scn, stomach, "Cancel Force drain"));
        scn.DSPlayCard(stomach, "Cancel Force drain");
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, stomach);
    }

    @Test
    public void DontDoThatAgainVTakesAnImmediateEffectFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var dont = scn.GetLSCard("dont");
        var immediate = scn.GetLSCard("immediate");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(dont);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(immediate);

        assertTrue(scn.LSCardActionAvailable(dont, "Immediate Effect"));
        scn.LSUseCardAction(dont, "Immediate Effect");
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(immediate));
        scn.LSChooseCard(immediate);
        scn.PassAllResponses();
        assertInHand(immediate);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(dont, "Immediate Effect"));
    }

    @Test
    public void DontDoThatAgainVSuspendsMobilizationPointsUnlessOpponentOccupiesABattlegroundSystem() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("dont", "1200_8");
                    put("corellia", "2_61");
                }},
                new HashMap<>() {{
                    put("mob", "9_129");
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
        var dont = scn.GetLSCard("dont");
        var mob = scn.GetDSCard("mob");
        var corellia = scn.GetLSCard("corellia");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(dont);
        scn.MoveCardsToDSSideOfTable(mob);
        scn.MoveLocationToTable(corellia);
        AdvanceTableChanged(scn);
        assertTrue(mob.isSuspended());

        scn.MoveCardsToLocation(corellia, tie);
        AdvanceTableChanged(scn);
        assertFalse(mob.isSuspended());
    }

    private boolean dsMayPlay(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card, String text) {
        if (scn.GetAwaitingDecision(scn.DS) == null) {
            return false;
        }
        String[] cardIds = scn.DSGetADParam("cardId");
        String[] actionTexts = scn.DSGetADParam("actionText");
        if (cardIds == null || actionTexts == null) {
            return false;
        }
        String id = String.valueOf(card.getCardId());
        for (int i = 0; i < cardIds.length; i++) {
            if (cardIds[i].equals(id) && (text == null || actionTexts[i].contains(text))) {
                return true;
            }
        }
        return false;
    }

    private void AdvanceTableChanged(VirtualTableScenario scn) {
        if (scn.gameState().getCurrentPhase() == Phase.DEPLOY) {
            scn.SkipToPhase(Phase.BATTLE);
        } else {
            scn.SkipToPhase(Phase.DEPLOY);
        }
    }
}
