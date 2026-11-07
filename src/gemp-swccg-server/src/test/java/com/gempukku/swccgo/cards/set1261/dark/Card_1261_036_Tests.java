package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_036_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19")),
                new HashMap<>(Map.of("failure", "1261_36", "vader", "1_168", "trooper", "1_194")),
                25, 25, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printedImmediateIdentityAndControlImmunity() {
        var scn = scenario();
        var bp = scn.GetDSCard("failure").getBlueprint();
        assertEquals("Protocol Failure", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(4, bp.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNRESTRICTED, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(CardSubtype.IMMEDIATE, bp.getCardSubtype());
        assertEquals(PlayCardZoneOption.ATTACHED, bp.getSinglePlayCardZoneOption());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.DEPLOYS_ON_LOCATION)));
        assertTrue(bp.isImmuneToCardTitle(Title.Control));
    }

    @Test(timeout = 10000)
    public void realSiteDrainIsCanceledAndOnlyVaderGetsLocalDiscountUntilYourEndOfTurn() {
        var scn = scenario();
        var site = scn.GetLSStartingLocation();
        var card = scn.GetDSCard("failure");
        var vader = scn.GetDSCard("vader");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"));
        scn.MoveCardsToDSHand(card, vader, trooper);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(6);
        int lostBefore = scn.GetDSLostPileCount();
        scn.LSForceDrainAt(site);

        for (int i = 0; i < 8; i++) {
            var ds = scn.DSGetDecision();
            var ls = scn.LSGetDecision();
            if (ds != null && ds.getText().toLowerCase().contains("optional response")
                    && scn.DSCardActionAvailable(card))
                break;
            if (ls != null && ls.getText().toLowerCase().contains("optional response"))
                scn.LSPass();
            else if (ds != null && ds.getText().toLowerCase().contains("optional response"))
                scn.DSPass();
            else
                fail("No site drain response: DS=" + ds + ", LS=" + ls);
        }
        scn.DSUseCardAction(card);
        if (scn.DSHasCardChoiceAvailable(site))
            scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertSame(site, card.getAttachedTo());
        assertFalse(scn.IsActiveForceDrain());
        assertEquals(lostBefore, scn.GetDSLostPileCount());
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(card.getZone().isInPlay());
        assertEquals(vader.getBlueprint().getDeployCost() - 1, cost(scn, vader, site), scn.epsilon);
        assertEquals(vader.getBlueprint().getDeployCost(), cost(scn, vader, scn.GetDSStartingLocation()), scn.epsilon);
        assertEquals(trooper.getBlueprint().getDeployCost(), cost(scn, trooper, site), scn.epsilon);
        scn.SkipToLSTurn(Phase.ACTIVATE);
        assertTrue(scn.GetDSLostPile().contains(card));
    }

    @Test
    public void noOrdinarySiteDeploymentWhenThereIsNoDrain() {
        var scn = scenario();
        var card = scn.GetDSCard("failure");
        scn.StartGame();
        scn.MoveCardsToDSHand(card);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        assertFalse(scn.DSDeployAvailable(card));
    }

    private float cost(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl site) {
        return scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), card, card, site,
                false, null, false, 0, null, false);
    }
}
