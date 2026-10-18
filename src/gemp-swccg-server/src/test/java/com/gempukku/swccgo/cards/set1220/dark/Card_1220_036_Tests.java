package com.gempukku.swccgo.cards.set1220.dark;

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
import static org.junit.Assert.assertTrue;

public class Card_1220_036_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("imsorry", "1220_36");
                    put("refinery", "7_216");
                    put("clouds1", "5_174");
                    put("clouds2", "5_174");
                    put("bespin", "5_164");
                    put("tie1", "1_304");
                    put("tie2", "1_304");
                    put("obsidian", "9_159");
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
    public void ImSorryStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("imsorry").getBlueprint();

        assertEquals("I'm Sorry", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.TATOOINE);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ImSorryDeploysOnTable() {
        var scn = GetScenario();
        var imsorry = scn.GetDSCard("imsorry");

        scn.StartGame();
        scn.MoveCardsToDSHand(imsorry);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.DSDeployAvailable(imsorry));
        scn.DSDeployCard(imsorry);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, imsorry);
    }

    @Test
    public void ImSorryDeploysFloatingRefineryFromReserveOncePerPhase() {
        var scn = GetScenario();
        var imsorry = scn.GetDSCard("imsorry");
        var refinery = scn.GetDSCard("refinery");
        var clouds1 = scn.GetDSCard("clouds1");
        var bespin = scn.GetDSCard("bespin");
        var obsidian = scn.GetDSCard("obsidian");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds1);
        scn.MoveCardsToDSSideOfTable(imsorry);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(refinery, obsidian);

        assertTrue(scn.DSCardActionAvailable(imsorry, "Floating Refinery"));
        scn.DSUseCardAction(imsorry, "Floating Refinery");
        assertTrue(scn.DSDecisionAvailable("Choose card"));
        scn.DSChooseCard(refinery);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(clouds1, refinery) || refinery.getZone() == Zone.ATTACHED);
    }

    @Test
    public void ImSorryMakesOpponentLoseOneForceWhenControllingTwoCloudSectorsWithTies() {
        var scn = GetScenario();
        var imsorry = scn.GetDSCard("imsorry");
        var clouds1 = scn.GetDSCard("clouds1");
        var clouds2 = scn.GetDSCard("clouds2");
        var bespin = scn.GetDSCard("bespin");
        var tie1 = scn.GetDSCard("tie1");
        var tie2 = scn.GetDSCard("tie2");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveCardsToDSHand(clouds1, clouds2);
        scn.MoveCardsToDSSideOfTable(imsorry);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPlayCard(clouds1);
        scn.PassAllResponses();
        scn.LSPass();
        scn.DSPlayCard(clouds2);
        scn.DSChoose("Left");
        scn.PassAllResponses();
        scn.MoveCardsToLocation(clouds1, tie1);
        scn.MoveCardsToLocation(clouds2, tie2);
        scn.LSPass();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSCardActionAvailable(imsorry, "Make opponent lose"));
        int lsForceBefore = scn.GetLSForcePileCount();
        scn.DSUseCardAction(imsorry, "Make opponent lose");
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Choose Force to lose"));
        scn.LSChooseCard(scn.GetTopOfLSForcePile());
        scn.PassAllResponses();

        assertEquals(lsForceBefore - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void ImSorryRequiresOpponentLoseOneForceAtEndOfTurnIfNotUsed() {
        var scn = GetScenario();
        var imsorry = scn.GetDSCard("imsorry");
        var clouds1 = scn.GetDSCard("clouds1");
        var clouds2 = scn.GetDSCard("clouds2");
        var bespin = scn.GetDSCard("bespin");
        var tie1 = scn.GetDSCard("tie1");
        var tie2 = scn.GetDSCard("tie2");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveCardsToDSHand(clouds1, clouds2);
        scn.MoveCardsToDSSideOfTable(imsorry);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPlayCard(clouds1);
        scn.PassAllResponses();
        scn.LSPass();
        scn.DSPlayCard(clouds2);
        scn.DSChoose("Left");
        scn.PassAllResponses();
        scn.MoveCardsToLocation(clouds1, tie1);
        scn.MoveCardsToLocation(clouds2, tie2);
        scn.LSPass();
        scn.SkipToPhase(Phase.DRAW);

        int lsForceBefore = scn.GetLSForcePileCount();
        scn.DSPass();
        scn.LSPass();
        scn.PassResponses("RECIRCULATED");
        scn.PassResponses("RECIRCULATED");
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Choose Force to lose"));
        scn.LSChooseCard(scn.GetTopOfLSForcePile());
        scn.PassAllResponses();

        assertEquals(lsForceBefore - 1, scn.GetLSForcePileCount());
    }
}
