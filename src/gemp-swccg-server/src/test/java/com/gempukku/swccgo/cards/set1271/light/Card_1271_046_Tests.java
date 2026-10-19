package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
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


public class Card_1271_046_Tests {
    protected VirtualTableScenario GetScenario() {
        return GetScenario(StartingSetup.DefaultLSGroundLocation);
    }

    protected VirtualTableScenario GetScenario(StartingSetup lsStart) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("restore", "1271_46");
                    put("yavin", "1_135");
                    put("bespin", "5_076");
                    put("throne", "1_138");
                    put("xwing", "1_146");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("tie", "1_304");
                }},
                10, 10,
                lsStart,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void RestoreFreedomToTheGalaxyStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("restore").getBlueprint();
        assertEquals("Restore Freedom To The Galaxy", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CRITICAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.MISSION); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>() {{ add(Keyword.DEPLOYS_ON_LOCATION); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SETUP);
            add(Icon.DEATH_STAR_II);
            add(Icon.MISSION);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RestoreFreedomDeploysOnYavin4() {
        var scn = GetScenario();
        var restore = scn.GetLSCard("restore");
        var yavin = scn.GetLSCard("yavin");
        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToHand(restore);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(restore));
        scn.LSDeployCard(restore);
        scn.LSChooseCard(yavin);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(yavin, restore));
    }

    @Test
    public void RestoreFreedomNotOfferedWithObjectiveStart() {
        var scn = GetScenario(StartingSetup.ThereIsGoodInHimObjective);
        var restore = scn.GetLSCard("restore");
        var yavin = scn.GetLSCard("yavin");
        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToHand(restore);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(restore));
    }

    @Test
    public void RestoreFreedomBlocksASecondCritical() {
        var scn = GetScenario();
        var restore = scn.GetLSCard("restore");
        var yavin = scn.GetLSCard("yavin");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToHand(restore, luke);
        var second = MissionTestSupport.putInHand(scn, 9102, scn.LS, MissionTestSupport.testCritical(Side.LIGHT, "Test LS Critical"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(restore);
        scn.LSChooseCard(yavin);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPass();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(luke));
        assertFalse(scn.LSDeployAvailable(second));
    }

    @Test
    public void RestoreFreedomDeploysInteriorYavin4SiteFromReserve() {
        var scn = GetScenario();
        var restore = scn.GetLSCard("restore");
        var yavin = scn.GetLSCard("yavin");
        var throne = scn.GetLSCard("throne");
        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToHand(restore);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(restore);
        scn.LSChooseCard(yavin);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPass();
        scn.PassAllResponses();
        scn.MoveCardsToTopOfOwnReserveDeck(throne);
        assertTrue(scn.LSActionAvailable("Deploy battleground system or interior Yavin 4 site"));
        scn.LSUseCardAction(restore, "Deploy battleground system or interior Yavin 4 site");
        scn.PassAllResponses();
        scn.LSChooseCard(throne);
        scn.PassAllResponses();
        assertInZone(Zone.LOCATIONS, throne);
    }

    @Test
    public void RestoreFreedomLiberatesASystemAndAddsToForceDrains() {
        var scn = GetScenario();
        var restore = scn.GetLSCard("restore");
        var yavin = scn.GetLSCard("yavin");
        var bespin = scn.GetLSCard("bespin");
        var xwing = scn.GetLSCard("xwing");
        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToHand(restore);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(restore);
        scn.LSChooseCard(yavin);
        scn.PassAllResponses();
        scn.MoveLocationToTable(bespin);
        scn.MoveCardsToLocation(bespin, xwing);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(7);
        assertTrue(scn.LSForceDrainAvailable(bespin));
        scn.LSForceDrainAt(bespin);
        assertTrue(scn.DSDecisionAvailable("optional"));
        scn.DSPass();
        assertTrue(scn.LSActionAvailable("Draw destiny to liberate"));
        scn.LSChooseAction("Draw destiny to liberate");
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Choose player"));
        scn.LSChoose("Light side");
        scn.PassAllResponses();
        assertFalse(scn.GetStackedCards(bespin).isEmpty());
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void RestoreFreedomUnstacksWhenOpponentDrainsAtLiberatedSystem() {
        var scn = GetScenario();
        var restore = scn.GetLSCard("restore");
        var yavin = scn.GetLSCard("yavin");
        var bespin = scn.GetLSCard("bespin");
        var xwing = scn.GetLSCard("xwing");
        var tie = scn.GetDSCard("tie");
        var junk = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToHand(restore);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(restore);
        scn.LSChooseCard(yavin);
        scn.PassAllResponses();
        scn.MoveLocationToTable(bespin);
        scn.MoveCardsToLocation(bespin, xwing);
        scn.StackCardsOn(bespin, junk);
        junk.setLiberationCard(true);
        scn.MoveCardsToTopOfLSLostPile(xwing);
        scn.MoveCardsToLocation(bespin, tie);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(bespin));
        scn.DSForceDrainAt(bespin);
        scn.PassAllResponses();
        assertTrue(scn.GetStackedCards(bespin).isEmpty());
        assertInZone(Zone.USED_PILE, junk);
    }

    @Test
    public void RestoreFreedomLiberationFailsWhenTotalDestinyIsAtMost6() {
        var scn = GetScenario();
        var restore = scn.GetLSCard("restore");
        var yavin = scn.GetLSCard("yavin");
        var throne = scn.GetLSCard("throne");
        var bespin = scn.GetLSCard("bespin");
        var xwing = scn.GetLSCard("xwing");
        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveLocationToTable(throne);
        scn.MoveCardsToHand(restore);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(restore);
        scn.LSChooseCard(yavin);
        scn.PassAllResponses();
        scn.MoveLocationToTable(bespin);
        scn.MoveCardsToLocation(bespin, xwing);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(5);
        scn.LSForceDrainAt(bespin);
        assertTrue(scn.DSDecisionAvailable("optional"));
        scn.DSPass();
        assertTrue(scn.LSActionAvailable("Draw destiny to liberate"));
        scn.LSChooseAction("Draw destiny to liberate");
        scn.PassAllResponses();
        assertTrue(scn.GetStackedCards(bespin).isEmpty());
    }

    @Test
    public void RestoreFreedomLiberationSucceedsWhenOccupiedYavin4SiteAdds2() {
        var scn = GetScenario();
        var restore = scn.GetLSCard("restore");
        var yavin = scn.GetLSCard("yavin");
        var throne = scn.GetLSCard("throne");
        var bespin = scn.GetLSCard("bespin");
        var xwing = scn.GetLSCard("xwing");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveLocationToTable(throne);
        scn.MoveCardsToHand(restore);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(restore);
        scn.LSChooseCard(yavin);
        scn.PassAllResponses();
        scn.MoveLocationToTable(bespin);
        scn.MoveCardsToLocation(bespin, xwing);
        scn.MoveCardsToLocation(throne, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(5);
        scn.LSForceDrainAt(bespin);
        assertTrue(scn.DSDecisionAvailable("optional"));
        scn.DSPass();
        assertTrue(scn.LSActionAvailable("Draw destiny to liberate"));
        scn.LSChooseAction("Draw destiny to liberate");
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Choose player"));
        scn.LSChoose("Light side");
        scn.PassAllResponses();
        assertFalse(scn.GetStackedCards(bespin).isEmpty());
    }

    @Test
    public void RestoreFreedomAddsPowerAndBattleDestinyAtLiberatedLocations() {
        var scn = GetScenario();
        var restore = scn.GetLSCard("restore");
        var yavin = scn.GetLSCard("yavin");
        var bespin = scn.GetLSCard("bespin");
        var xwing = scn.GetLSCard("xwing");
        var luke = scn.GetLSCard("luke");
        var tie = scn.GetDSCard("tie");
        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToHand(restore);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(restore);
        scn.LSChooseCard(yavin);
        scn.PassAllResponses();
        scn.MoveLocationToTable(bespin);
        scn.StackCardsOn(bespin, luke);
        luke.setLiberationCard(true);
        scn.MoveCardsToLocation(bespin, xwing);
        scn.MoveCardsToLocation(bespin, tie);
        int printed = scn.GetPower(xwing);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(bespin);
        scn.PassAllResponses();
        assertEquals(printed + 3, scn.GetLSTotalPower());
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void RestoreFreedomOpponentDrainsAtLiberatedSystemAreNotPlus1() {
        var scn = GetScenario();
        var restore = scn.GetLSCard("restore");
        var yavin = scn.GetLSCard("yavin");
        var bespin = scn.GetLSCard("bespin");
        var xwing = scn.GetLSCard("xwing");
        var tie = scn.GetDSCard("tie");
        var junk = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToHand(restore);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(restore);
        scn.LSChooseCard(yavin);
        scn.PassAllResponses();
        scn.MoveLocationToTable(bespin);
        scn.StackCardsOn(bespin, junk);
        junk.setLiberationCard(true);
        scn.MoveCardsToLocation(bespin, tie);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(bespin));
        scn.DSForceDrainAt(bespin);
        scn.PassResponses("required");
        scn.PassForceDrainStartResponses();
        int drain = scn.GetForceDrainTotal();
        assertTrue("Opponent drain at a liberated system is not +1", drain <= 1);
        assertTrue(junk.getZone() == Zone.USED_PILE || junk.getZone() == Zone.TOP_OF_USED_PILE
                || scn.GetStackedCards(bespin).isEmpty());
    }
}
