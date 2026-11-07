package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1262_003_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("luke", "1_19"); }},
                new HashMap<>() {{ put("altering", "1262_3"); put("dining", "5_168"); put("platform", "5_169"); put("corridor", "5_171");
                    put("bespin", "5_164"); put("cloudcity", "5_165"); put("deal", "5_115");
                    put("t1", "1_194"); put("t2", "1_194"); put("t3", "1_194"); put("avenger", "1263_19"); put("vader", "1_168"); }},
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void AlteringTheDealStatsAndIdentityMatchPrintedCard() {
        var scn = scenario();
        var bp = scn.GetDSCard("altering").getBlueprint();
        assertEquals("Altering The Deal", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertTrue(bp.isLegacy());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.EFFECT); add(Icon.CLOUD_CITY); add(Icon.LEGACY_BLOCK_6); }});
        assertTrue(bp.getGameText().endsWith("(Immune to Alter.)"));
    }

    private VirtualTableScenario started() {
        var scn = scenario();
        scn.StartGame();
        for (String loc : new String[]{"bespin", "cloudcity", "dining", "platform", "corridor"}) scn.MoveLocationToTable(scn.GetDSCard(loc));
        return scn;
    }

    @Test
    public void AlteringTheDealDeploysOnlyOnDiningRoom() {
        var scn = started();
        var altering = scn.GetDSCard("altering");
        scn.MoveCardsToDSHand(altering);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(altering);
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("dining")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("platform")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("cloudcity")));
        scn.DSChooseCard(scn.GetDSCard("dining"));
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(scn.GetDSCard("dining"), altering));
    }

    private VirtualTableScenario dealInHand(int sitesControlled) {
        var scn = started();
        scn.AttachCardsTo(scn.GetDSCard("dining"), scn.GetDSCard("altering"));
        String[] sites = {"dining", "platform", "corridor"};
        String[] troopers = {"t1", "t2", "t3"};
        for (int i = 0; i < sitesControlled; i++) scn.MoveCardsToLocation(scn.GetDSCard(sites[i]), scn.GetDSCard(troopers[i]));
        scn.MoveCardsToDSHand(scn.GetDSCard("deal"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        return scn;
    }

    @Test
    public void AlteringTheDealLetsDarkDealDeployHereWhenYouControlThreeBespinSites() {
        var scn = dealInHand(3);
        var deal = scn.GetDSCard("deal");
        assertTrue(scn.DSDeployAvailable(deal));
        scn.DSDeployCard(deal);
        if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("dining"))) scn.DSChooseCard(scn.GetDSCard("dining"));
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(scn.GetDSCard("dining"), deal));
    }

    @Test
    public void AlteringTheDealDoesNotLetDarkDealDeployWithOnlyTwoBespinSites() {
        var scn = dealInHand(2);
        assertFalse(scn.DSDeployAvailable(scn.GetDSCard("deal")));
    }

    @Test
    public void AlteringTheDealCancelsDarkDealHereWhenOpponentControlsThisSite() {
        var scn = started();
        var dining = scn.GetDSCard("dining");
        scn.AttachCardsTo(dining, scn.GetDSCard("altering"));
        scn.AttachCardsTo(dining, scn.GetDSCard("deal"));
        scn.MoveCardsToLocation(dining, scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("deal"));
    }

    @Test
    public void AlteringTheDealDoesNotCancelDarkDealWhileYouControlThisSite() {
        var scn = started();
        var dining = scn.GetDSCard("dining");
        scn.AttachCardsTo(dining, scn.GetDSCard("altering"));
        scn.AttachCardsTo(dining, scn.GetDSCard("deal"));
        scn.MoveCardsToLocation(dining, scn.GetDSCard("t1"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.IsAttachedTo(dining, scn.GetDSCard("deal")));
    }

    /** This Deal Is Getting Worse All The Time as the Dark Side starting Objective, deploying Cloud City: Dining Room. */
    static final StartingSetup ThisDealObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{ put("thisdeal", "109_12"); put("platform", "5_169"); }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            for (int i = 0; i < 10; i++) {
                if (scn.DSDecisionAvailable("On which side")) scn.DSChoose("Left");
                else if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(scn.GetDSCard("thisdeal"))) scn.DSChooseCard(scn.GetDSCard("thisdeal"));
                else if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(scn.GetDSCard("platform"))) scn.DSChooseCard(scn.GetDSCard("platform"));
                else return;
            }
        }
    };

    private VirtualTableScenario thisDeal(boolean altering) {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{ put("luke", "1_19"); }},
                new HashMap<>() {{ put("altering", "1262_3"); put("bespin", "5_164"); put("cloudcity", "5_165"); put("deal", "5_115"); put("dining", "5_168");
                    put("avenger", "1263_19"); put("vader", "1_168"); }},
                20, 20, StartingSetup.DefaultLSGroundLocation, ThisDealObjective,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetDSCard("thisdeal").getZone());
        scn.MoveLocationToTable(scn.GetDSCard("bespin"));
        scn.MoveLocationToTable(scn.GetDSCard("cloudcity"));
        scn.MoveLocationToTable(scn.GetDSCard("dining"));
        if (altering) scn.AttachCardsTo(scn.GetDSCard("dining"), scn.GetDSCard("altering"));
        scn.AttachCardsTo(scn.GetDSCard("cloudcity"), scn.GetDSCard("deal"));
        scn.MoveCardsToLocation(scn.GetDSCard("bespin"), scn.GetDSCard("avenger"));
        scn.BoardAsPilot(scn.GetDSCard("avenger"), scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        return scn;
    }

    @Test
    public void AlteringTheDealFlipsThisDealWhileDarkDealOnTableAndYouOccupyBespin() {
        var scn = thisDeal(true);
        var objective = scn.GetDSCard("thisdeal");
        assertTrue(objective.isFlipped());
        assertFalse(GameConditions.canBeFlipped(scn.game(), objective));
    }

    @Test
    public void ThisDealDoesNotFlipFromBespinAloneWithoutAlteringTheDeal() {
        var scn = thisDeal(false);
        var objective = scn.GetDSCard("thisdeal");
        assertFalse(objective.isFlipped());
        assertTrue(GameConditions.canBeFlipped(scn.game(), objective));
    }
}
