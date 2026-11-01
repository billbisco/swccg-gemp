package com.gempukku.swccgo.cards.set1113.light;

import com.gempukku.swccgo.common.CardSubtype;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1113_011_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("post", "1113_11");
                    put("redemption", "5_87");
                    put("hoth", "3_55");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                    put("garindan", "1_177");
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
    public void RedemptionCommandPostStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("post").getBlueprint();

        assertEquals("Redemption: Command Post", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERIOR_SITE);
            add(Icon.STARSHIP_SITE);
            add(Icon.MOBILE);
            add(Icon.SCOMP_LINK);
            add(Icon.A_NEW_HOPE);
            add(Icon.LIGHT_FORCE);
}});
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(0, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_13, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RedemptionCommandPostRelocatesYourLeaderHereToASiteYouOccupyDuringMovePhase() {
        var scn = GetScenario();
        var post = scn.GetLSCard("post");
        var redemption = scn.GetLSCard("redemption");
        var hoth = scn.GetLSCard("hoth");
        var leia = scn.GetLSCard("leia");
        var occupied = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLocation(hoth, redemption);
        scn.MoveCardsToHand(post);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(post);
        scn.PassAllResponses();
        scn.MoveCardsToLocation(post, leia);
        scn.MoveCardsToLocation(occupied, scn.GetLSFiller(1));
        scn.SkipToLSTurn(Phase.MOVE);

        assertTrue(scn.LSCardActionAvailable(post, "Relocate a leader here to a site you occupy"));
        scn.LSUseCardAction(post, "Relocate a leader here to a site you occupy");
        if (scn.LSHasCardChoiceAvailable(leia)) {
            scn.LSChooseCard(leia);
        }
        if (scn.LSHasCardChoiceAvailable(occupied)) {
            scn.LSChooseCard(occupied);
        }
        scn.PassAllResponses();

        assertAtLocation(occupied, leia);
    }

    @Test
    public void RedemptionCommandPostDoesNotOfferRelocateDuringDeployPhase() {
        var scn = GetScenario();
        var post = scn.GetLSCard("post");
        var redemption = scn.GetLSCard("redemption");
        var hoth = scn.GetLSCard("hoth");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLocation(hoth, redemption);
        scn.MoveCardsToHand(post);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(post);
        scn.PassAllResponses();
        scn.MoveCardsToLocation(post, leia);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardActionAvailable(post, "Relocate a leader here to a site you occupy"));
    }

    @Test
    public void RedemptionCommandPostDarkMayOnlyDeploySpiesHereWhileRedemptionAtUnoccupiedSystem() {
        var scn = GetScenario();
        var post = scn.GetLSCard("post");
        var redemption = scn.GetLSCard("redemption");
        var hoth = scn.GetLSCard("hoth");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var garindan = scn.GetDSCard("garindan");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLocation(hoth, redemption);
        scn.MoveCardsToHand(post);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(post);
        scn.PassAllResponses();
        scn.MoveCardsToLocation(post, scn.GetDSFiller(1));
        scn.MoveCardsToDSHand(stormtrooper, garindan);
        scn.SkipToDSTurn(Phase.DEPLOY);

        scn.DSDeployCard(stormtrooper);
        assertFalse(scn.DSHasCardChoiceAvailable(post));
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(garindan);
        assertTrue(scn.DSHasCardChoiceAvailable(post));
        scn.DSChooseCard(post);
        scn.PassAllResponses();
        assertAtLocation(post, garindan);
    }

    @Test
    public void RedemptionCommandPostDarkMayDeployNonSpiesHereWhenDarkOccupiesTheSystem() {
        var scn = GetScenario();
        var post = scn.GetLSCard("post");
        var redemption = scn.GetLSCard("redemption");
        var hoth = scn.GetLSCard("hoth");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLocation(hoth, redemption, tie);
        scn.MoveCardsToHand(post);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(post);
        scn.PassAllResponses();
        scn.MoveCardsToLocation(post, scn.GetDSFiller(1));
        scn.MoveCardsToDSHand(stormtrooper);
        scn.SkipToDSTurn(Phase.DEPLOY);

        scn.DSDeployCard(stormtrooper);
        assertTrue(scn.DSHasCardChoiceAvailable(post));
        scn.DSChooseCard(post);
        scn.PassAllResponses();
        assertAtLocation(post, stormtrooper);
    }
}
