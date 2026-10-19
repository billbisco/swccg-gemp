package com.gempukku.swccgo.cards.set1291.light;

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


public class Card_1291_065_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("assault", "1291_65");
                    put("luke", "1_19");
                    put("leia", "1_17");
                    put("obi", "1_21");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("vader", "1_168");
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
        var system = MissionTestSupport.putLocationOnTable(scn, 9501, scn.LS, MissionTestSupport.muunilinstSystem());
        var site1 = MissionTestSupport.putLocationOnTable(scn, 9502, scn.LS, MissionTestSupport.muunilinstSite("Muunilinst: Plaza"));
        var site2 = MissionTestSupport.putLocationOnTable(scn, 9503, scn.LS, MissionTestSupport.muunilinstSite("Muunilinst: Vault"));
        return new PhysicalCardImpl[] {system, site1, site2};
    }

    @Test
    public void AssaultOnMuunilinstStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("assault").getBlueprint();
        assertEquals("Assault On Muunilinst", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.MISSION); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.CLONE_ARMY);
            add(Icon.MISSION);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AssaultOnMuunilinstDeploysOnBegunTheCloneWarHas() {
        var scn = GetScenario();
        var assault = scn.GetLSCard("assault");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9500, scn.LS, MissionTestSupport.begunTheCloneWarHas());
        scn.MoveCardsToHand(assault);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(assault));
        scn.LSDeployCard(assault);
        scn.LSChooseCard(host);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(host, assault));
    }

    @Test
    public void AssaultOnMuunilinstCompletesWhenYouControlThreeLocations() {
        var scn = GetScenario();
        var assault = scn.GetLSCard("assault");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var obi = scn.GetLSCard("obi");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9500, scn.LS, MissionTestSupport.begunTheCloneWarHas());
        scn.AttachCardsTo(host, assault);
        var locs = muunilinst(scn);
        scn.MoveCardsToLocation(locs[0], luke);
        scn.MoveCardsToLocation(locs[1], leia);
        scn.MoveCardsToLocation(locs[2], obi);
        scn.EnterControlOrKeepRequired();
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose Force to lose"));
        scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
        scn.PassAllResponses();
        assertFalse(scn.GetStackedCards(assault).isEmpty());
    }

    @Test
    public void AssaultOnMuunilinstRewardAddsToForceDrains() {
        var scn = GetScenario();
        var assault = scn.GetLSCard("assault");
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9500, scn.LS, MissionTestSupport.begunTheCloneWarHas());
        scn.AttachCardsTo(host, assault);
        var locs = muunilinst(scn);
        scn.StackCardsFaceDownOn(assault, xwing);
        scn.MoveCardsToLocation(locs[0], luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(locs[0]));
        scn.LSForceDrainAt(locs[0]);
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void AssaultOnMuunilinstTurnTheTideWhenOpponentControlsTwo() {
        var scn = GetScenario();
        var assault = scn.GetLSCard("assault");
        var trooper = scn.GetDSCard("trooper");
        var vader = scn.GetDSCard("vader");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9500, scn.LS, MissionTestSupport.begunTheCloneWarHas());
        scn.AttachCardsTo(host, assault);
        var locs = muunilinst(scn);
        scn.MoveCardsToLocation(locs[0], trooper);
        scn.MoveCardsToLocation(locs[1], vader);
        scn.EnterControlOrKeepRequired();
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, assault);
    }

    @Test
    public void AssaultOnMuunilinstDoesNotCompleteWhenOpponentControlsAMuunilinstLocation() {
        var scn = GetScenario();
        var assault = scn.GetLSCard("assault");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var obi = scn.GetLSCard("obi");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9500, scn.LS, MissionTestSupport.begunTheCloneWarHas());
        scn.AttachCardsTo(host, assault);
        var locs = muunilinst(scn);
        scn.MoveCardsToLocation(locs[0], luke);
        scn.MoveCardsToLocation(locs[1], leia);
        scn.MoveCardsToLocation(locs[2], obi);
        scn.MoveCardsToLocation(locs[0], trooper);
        scn.EnterControlOrKeepRequired();
        scn.PassAllResponses();
        assertTrue(scn.GetStackedCards(assault).isEmpty());
        assertInZone(Zone.SIDE_OF_TABLE, assault);
    }

    @Test
    public void AssaultOnMuunilinstRewardRaisesConvertedMuunilinstSite() {
        var scn = GetScenario();
        var assault = scn.GetLSCard("assault");
        var xwing = scn.GetLSCard("xwing");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9500, scn.LS, MissionTestSupport.begunTheCloneWarHas());
        scn.AttachCardsTo(host, assault);
        scn.StackCardsFaceDownOn(assault, xwing);
        var lsPlaza = MissionTestSupport.putLocationOnTable(scn, 9510, scn.LS, MissionTestSupport.muunilinstSite(Side.LIGHT, "Muunilinst: Plaza"));
        var dsPlaza = MissionTestSupport.putInHand(scn, 9511, scn.DS, MissionTestSupport.muunilinstSite(Side.DARK, "Muunilinst: Plaza"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(dsPlaza));
        scn.DSDeployCard(dsPlaza);
        scn.PassAllResponses();
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSActionAvailable("Raise converted Muunilinst site"));
        scn.LSUseCardAction(assault, "Raise converted Muunilinst site");
        scn.LSChooseCard(dsPlaza);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, lsPlaza.getZone());
    }
}
