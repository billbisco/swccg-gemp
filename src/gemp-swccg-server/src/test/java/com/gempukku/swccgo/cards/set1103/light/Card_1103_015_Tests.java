package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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

public class Card_1103_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("merc", "1103_15");
                    put("ooc", "2_54");
                    put("junk", "1_3");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("dsjunk", "1_177");
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
    public void MercSunletStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("merc").getBlueprint();

        assertEquals("Merc Sunlet", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.EFFECT);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MercSunletDeploysOnTable() {
        var scn = GetScenario();
        var merc = scn.GetLSCard("merc");

        scn.StartGame();
        scn.MoveCardsToLSHand(merc);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(4);

        assertTrue(scn.LSCardPlayAvailable(merc) || scn.LSDeployAvailable(merc));
        scn.LSPlayCard(merc);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, merc);
    }

    @Test
    public void MercSunletReducesForceLossByPlacingLostPileCardsOutOfPlay() {
        var scn = GetScenario();
        var merc = scn.GetLSCard("merc");
        var junk = scn.GetLSCard("junk");
        var trooper = scn.GetDSCard("trooper");
        var dsStart = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(merc);
        scn.MoveCardsToLocation(dsStart, trooper);
        scn.MoveCardsToTopOfLSLostPile(junk);
        scn.EnsureLSForcePile(2);

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        int lifeBefore = scn.GetLSLifeForceRemaining();
        scn.DSForceDrainAt(dsStart);
        scn.PassResponses("FORCE_DRAIN_INITIATED");
        scn.PassResponses("FORCE_LOSS_INITIATED");
        assertTrue(scn.LSActionAvailable("Reduce Force loss"));
        scn.LSChooseAction("Reduce Force loss");
        for (int i = 0; i < 20; i++) {
            if (junk.getZone() == Zone.OUT_OF_PLAY) {
                break;
            }
            var decision = scn.LSGetDecision();
            if (decision != null && decision.getText() != null
                    && (decision.getText().contains("Choose X") || decision.getText().contains("use X Force"))) {
                scn.LSDecided(1);
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(junk)) {
                scn.LSChooseCard(junk);
                continue;
            }
            if (decision != null) {
                try {
                    scn.LSDecided(1);
                    continue;
                } catch (RuntimeException ignored) {
                    scn.PassAllResponses();
                    continue;
                }
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            break;
        }
        scn.PassResponses("optional");
        scn.PassForceDrainPendingResponses();
        scn.PassForceDrainEndResponses();

        assertEquals(Zone.OUT_OF_PLAY, junk.getZone());
        assertEquals(lifeBefore, scn.GetLSLifeForceRemaining());
    }
}
