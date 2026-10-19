package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Side;
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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class Card_1291_027_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                    put("obi", "1_21");
                }},
                new HashMap<>() {{
                    put("defense", "1291_27");
                    put("trooper", "1_194");
                    put("vader", "1_168");
                    put("ds61", "1_173");
                    put("junk", "1_304");
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

    private PhysicalCardImpl[] muunilinst(VirtualTableScenario scn) {
        var system = MissionTestSupport.putLocationOnTable(scn, 9401, scn.DS, MissionTestSupport.muunilinstSystem());
        var site1 = MissionTestSupport.putLocationOnTable(scn, 9402, scn.DS, MissionTestSupport.muunilinstSite("Muunilinst: Plaza"));
        var site2 = MissionTestSupport.putLocationOnTable(scn, 9403, scn.DS, MissionTestSupport.muunilinstSite("Muunilinst: Vault"));
        return new PhysicalCardImpl[] {system, site1, site2};
    }

    @Test
    public void DefenseOfMuunilinstStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("defense").getBlueprint();
        assertEquals("Defense Of Muunilinst", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.MISSION); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.SEPARATIST);
            add(Icon.MISSION);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DefenseOfMuunilinstDeploysOnWarHasBegun() {
        var scn = GetScenario();
        var defense = scn.GetDSCard("defense");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9400, scn.DS, MissionTestSupport.warHasBegun());
        scn.MoveCardsToHand(defense);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(defense));
        scn.DSDeployCard(defense);
        scn.DSChooseCard(host);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(host, defense));
    }

    @Test
    public void DefenseOfMuunilinstCompletesWhenYouControlThreeLocations() {
        var scn = GetScenario();
        var defense = scn.GetDSCard("defense");
        var trooper = scn.GetDSCard("trooper");
        var vader = scn.GetDSCard("vader");
        var ds61 = scn.GetDSCard("ds61");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9400, scn.DS, MissionTestSupport.warHasBegun());
        scn.AttachCardsTo(host, defense);
        var locs = muunilinst(scn);
        scn.MoveCardsToLocation(locs[0], trooper);
        scn.MoveCardsToLocation(locs[1], vader);
        scn.MoveCardsToLocation(locs[2], ds61);
        scn.EnterControlOrKeepRequired();
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Choose Force to lose"));
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassAllResponses();
        assertFalse(scn.GetStackedCards(defense).isEmpty());
    }

    @Test
    public void DefenseOfMuunilinstRewardAddsToForceDrains() {
        var scn = GetScenario();
        var defense = scn.GetDSCard("defense");
        var trooper = scn.GetDSCard("trooper");
        var junk = scn.GetDSCard("junk");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9400, scn.DS, MissionTestSupport.warHasBegun());
        scn.AttachCardsTo(host, defense);
        var locs = muunilinst(scn);
        scn.StackCardsFaceDownOn(defense, junk);
        scn.MoveCardsToLocation(locs[0], trooper);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(locs[0]));
        scn.DSForceDrainAt(locs[0]);
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void DefenseOfMuunilinstTurnTheTideWhenOpponentControlsTwo() {
        var scn = GetScenario();
        var defense = scn.GetDSCard("defense");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9400, scn.DS, MissionTestSupport.warHasBegun());
        scn.AttachCardsTo(host, defense);
        var locs = muunilinst(scn);
        scn.MoveCardsToLocation(locs[0], luke);
        scn.MoveCardsToLocation(locs[1], leia);
        scn.EnterControlOrKeepRequired();
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, defense);
    }

    @Test
    public void DefenseOfMuunilinstDoesNotCompleteWhenOpponentControlsAMuunilinstLocation() {
        var scn = GetScenario();
        var defense = scn.GetDSCard("defense");
        var trooper = scn.GetDSCard("trooper");
        var vader = scn.GetDSCard("vader");
        var ds61 = scn.GetDSCard("ds61");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9400, scn.DS, MissionTestSupport.warHasBegun());
        scn.AttachCardsTo(host, defense);
        var locs = muunilinst(scn);
        scn.MoveCardsToLocation(locs[0], trooper);
        scn.MoveCardsToLocation(locs[1], vader);
        scn.MoveCardsToLocation(locs[2], ds61);
        scn.MoveCardsToLocation(locs[0], luke);
        scn.EnterControlOrKeepRequired();
        scn.PassAllResponses();
        assertTrue(scn.GetStackedCards(defense).isEmpty());
        assertInZone(Zone.SIDE_OF_TABLE, defense);
    }

    @Test
    public void DefenseOfMuunilinstRewardRaisesConvertedMuunilinstSite() {
        var scn = GetScenario();
        var defense = scn.GetDSCard("defense");
        var junk = scn.GetDSCard("junk");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9400, scn.DS, MissionTestSupport.warHasBegun());
        scn.AttachCardsTo(host, defense);
        scn.StackCardsFaceDownOn(defense, junk);
        var dsPlaza = MissionTestSupport.putLocationOnTable(scn, 9410, scn.DS, MissionTestSupport.muunilinstSite(Side.DARK, "Muunilinst: Plaza"));
        var lsPlaza = MissionTestSupport.putInHand(scn, 9411, scn.LS, MissionTestSupport.muunilinstSite(Side.LIGHT, "Muunilinst: Plaza"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(lsPlaza));
        scn.LSDeployCard(lsPlaza);
        scn.PassAllResponses();
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSActionAvailable("Raise converted Muunilinst site"));
        scn.DSUseCardAction(defense, "Raise converted Muunilinst site");
        scn.DSChooseCard(lsPlaza);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, dsPlaza.getZone());
    }
}
