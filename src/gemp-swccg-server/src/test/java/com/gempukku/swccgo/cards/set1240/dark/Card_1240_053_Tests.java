package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_053_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("office", "1240_53");
                    put("room", "1240_51");
                    put("evax", "1240_12");
                    put("aloo", "1230_18");
                    put("tatooine", "1_289");
                }},
                10,
                10,
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
    public void ISBSectorOfficeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("office").getBlueprint();

        assertEquals("ISB Sector Office", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DARK_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.SPECIAL_EDITION);
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals(
                "Dark: Your total weapon and battle destiny here is + X, where X = highest printed influence at Conference Room. At related sites, may apply the ability of one Imperial Council Member here toward drawing battle destiny. Light: May not deploy to Bespin, Dagobah, Endor, Hoth, Kashyyyk, Ralltiir or Yavin 4.",
                card.getGameText());
    }

    @Test
    public void ISBSectorOfficeUsesHighestPrintedInfluenceAndRestrictsLightSystems() {
        var scn = GetScenario();
        var office = scn.GetDSCard("office");
        var room = scn.GetDSCard("room");
        var evax = scn.GetDSCard("evax");
        var aloo = scn.GetDSCard("aloo");

        scn.StartGame();
        scn.SkipToLSTurn(com.gempukku.swccgo.common.Phase.DEPLOY);
        scn.MoveLocationToTable(room);
        scn.MoveLocationToTable(office);
        scn.MoveCardsToLocation(room, evax, aloo);

        assertEquals(3, aloo.getBlueprint().getInfluence(), scn.epsilon);
        float highest = scn.game().getModifiersQuerying().getHighestPrintedInfluenceAtLocation(scn.gameState(), room);
        assertTrue("highest printed influence was " + highest, highest == 3);
        assertEquals(2, office.getBlueprint().getIconCount(Icon.DARK_FORCE));
        assertEquals(1, office.getBlueprint().getIconCount(Icon.LIGHT_FORCE));

        assertTrue(office.getBlueprint().mayNotBePartOfSystem(scn.game(), Title.Bespin));
        assertTrue(office.getBlueprint().mayNotBePartOfSystem(scn.game(), Title.Ralltiir));
        assertTrue(office.getBlueprint().mayNotBePartOfSystem(scn.game(), Title.Yavin_4));
        assertFalse(office.getBlueprint().mayNotBePartOfSystem(scn.game(), Title.Tatooine));
        assertEquals(3, scn.game().getModifiersQuerying().getHighestPrintedInfluenceAtLocation(scn.gameState(), room), scn.epsilon);
    }

    @Test
    public void ISBSectorOfficeCannotDeployToBannedLightSystemsOnDarkTurn() {
        var scn = GetPlayScenario();
        var office = scn.GetDSCard("office");
        var tatooine = scn.GetDSCard("tatooine");
        var yavin = scn.GetLSCard("yavin");
        var market = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToDSHand(office);
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(office));
        scn.DSPlayCard(office);
        assertTrue("expected side-of-Marketplace picker (Yavin banned), text="
                        + (scn.DSGetDecision() == null ? "null" : scn.DSGetDecision().getText()),
                scn.DSDecisionAvailable("On which side")
                        && scn.DSDecisionAvailable("Marketplace"));
        assertFalse(scn.DSDecisionAvailable("Yavin"));
        assertFalse(scn.DSHasCardChoiceAvailable(yavin));
    }

    @Test
    public void ISBSectorOfficeBattleDestinyUsesHighestPrintedInfluence() {
        var scn = GetPlayScenario();
        var office = scn.GetDSCard("office");
        var room = scn.GetDSCard("room");
        var aloo = scn.GetDSCard("aloo");
        var janus = scn.GetDSCard("janus");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveLocationToTable(office);
        scn.MoveCardsToLocation(room, aloo);
        scn.MoveCardsToLocation(office, janus, luke);
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.InfluenceModifier(aloo, 10));
        assertEquals(3, scn.game().getModifiersQuerying().getHighestPrintedInfluenceAtLocation(scn.gameState(), room), scn.epsilon);

        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.BATTLE);
        scn.PrepareDSDestiny(1);
        scn.DSInitiateBattle(office);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.PassAllResponses();
        assertTrue("expected battle destiny prompt, ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision(),
                scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();
        scn.PassDestinyDrawResponses();
        assertEquals(4, scn.GetDSTotalDestiny());
    }

    @Test
    public void ISBSectorOfficeWeaponDestinyUsesHighestPrintedInfluenceForYourWeapons() {
        var scn = GetPlayScenario();
        var office = scn.GetDSCard("office");
        var room = scn.GetDSCard("room");
        var aloo = scn.GetDSCard("aloo");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveLocationToTable(office);
        scn.MoveCardsToLocation(room, aloo);
        scn.MoveCardsToLocation(office, trooper, luke);
        scn.AttachCardsTo(trooper, blaster);
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.InfluenceModifier(aloo, 10));
        assertEquals(3, scn.game().getModifiersQuerying().getHighestPrintedInfluenceAtLocation(scn.gameState(), room), scn.epsilon);

        scn.DSActivateForceCheat(4);
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.BATTLE);
        scn.PrepareDSDestiny(1);
        scn.DSInitiateBattle(office);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        Integer last = null;
        for (String msg : scn.gameState().getLastMessages()) {
            String lower = msg.toLowerCase();
            for (String prefix : new String[] {"total weapon destiny is ", "total destiny: "}) {
                int idx = lower.lastIndexOf(prefix);
                if (idx >= 0) {
                    String rest = msg.substring(idx + prefix.length()).trim();
                    last = (int) Float.parseFloat(rest.split("[^0-9.]")[0]);
                }
            }
        }
        assertTrue("Expected a total weapon destiny log, got: " + String.join("\n", scn.gameState().getLastMessages()), last != null);
        assertEquals(4, last.intValue());
    }

    @Test
    public void ISBSectorOfficeAppliesCouncilMemberAbilityAtRelatedSite() {
        var scn = GetPlayScenario();
        var office = scn.GetDSCard("office");
        var tatooine = scn.GetDSCard("tatooine");
        var evax = scn.GetDSCard("evax");
        var yularen = scn.GetDSCard("yularen");
        var luke = scn.GetLSCard("luke");
        var market = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToDSHand(office);
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(office));
        scn.DSPlayCard(office);
        assertTrue("expected side-of-Marketplace picker, text="
                        + (scn.DSGetDecision() == null ? "null" : scn.DSGetDecision().getText()),
                scn.DSDecisionAvailable("On which side"));
        scn.DSChoose("Left");
        scn.PassAllResponses();
        assertTrue("office should be on table, zone=" + office.getZone(),
                office.getZone() == com.gempukku.swccgo.common.Zone.LOCATIONS
                        || office.getZone() == com.gempukku.swccgo.common.Zone.AT_LOCATION);
        scn.MoveCardsToLocation(office, evax);
        scn.MoveCardsToLocation(market, yularen, luke);
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.BATTLE);
        assertTrue("cannot battle at Marketplace. zone=" + market.getZone()
                        + " office=" + office.getZone()
                        + " yularenAt=" + yularen.getAtLocation()
                        + " lukeAt=" + luke.getAtLocation()
                        + " phase=" + scn.gameState().getCurrentPhase()
                        + " ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision()
                        + " actions=" + scn.GetDSAvailableActions(),
                scn.DSCanInitiateBattle(market));
        scn.DSInitiateBattle(market);
        scn.PassBattleStartResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertEquals(0, scn.game().getModifiersQuerying().getNumBattleDestinyDraws(scn.gameState(), scn.DS, false, true));
        assertTrue(scn.DSCardActionAvailable(office, "Apply a Council Member's ability"));
        scn.DSUseCardAction(office, "Apply a Council Member's ability");
        scn.DSChooseCard(evax);
        scn.PassAllResponses();
        assertEquals(1, scn.game().getModifiersQuerying().getNumBattleDestinyDraws(scn.gameState(), scn.DS, false, true));
    }

    private VirtualTableScenario GetPlayScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("yavin", "1_135");
                }},
                new HashMap<>() {{
                    put("office", "1240_53");
                    put("room", "1240_51");
                    put("evax", "1240_12");
                    put("aloo", "1230_18");
                    put("yularen", "1240_4");
                    put("trooper", "1_194");
                    put("janus", "1240_7");
                    put("tatooine", "1_289");
                    put("droid", "1_175");
                    put("blaster", "1_317");
                }},
                15,
                15,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }
}
