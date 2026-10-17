package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.modifiers.InfluenceModifier;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_051_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("sense", "1_109");
                    put("alter", "1_71");
                    put("pilot", "1_27");
                }},
                new HashMap<>() {{
                    put("room", "1240_51");
                    put("aloo", "1230_18");
                    put("evax", "1240_12");
                    put("commander", "1272_2");
                    put("trooper1", "1_194");
                    put("trooper2", "1_194");
                    put("cantina", "1_290");
                    put("bay", "1_291");
                    put("visage", "4_135");
                    put("pcgate", "2_113");
                    put("stun", "1_268");
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
    public void ConferenceRoomStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("room").getBlueprint();

        assertEquals(Title.Death_Star_Conference_Room, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.INTERIOR_SITE);
            add(Icon.MOBILE);
            add(Icon.SCOMP_LINK);
        }});
        assertEquals(0, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(0, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals("Dark: While you occupy two non-Death Star battlegrounds and your total influence here is > 5, you lose no Force to Visage of the Emperor. Light: While opponent has total influence > 5 here, your Sense and Alter destiny draws are +2. [Virtual Block 4] Laser Gate is Immune to Sabotage.",
                card.getGameText());
    }

    @Test
    public void ConferenceRoomLosesNoForceToVisageOnlyWhenOccupyingTwoBattlegroundsAndInfluenceExceedsFive() {
        var scn = GetScenario();
        var room = scn.GetDSCard("room");
        var aloo = scn.GetDSCard("aloo");
        var evax = scn.GetDSCard("evax");
        var commander = scn.GetDSCard("commander");
        var trooper1 = scn.GetDSCard("trooper1");
        var trooper2 = scn.GetDSCard("trooper2");
        var cantina = scn.GetDSCard("cantina");
        var bay = scn.GetDSCard("bay");
        var visage = scn.GetDSCard("visage");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(bay);
        scn.MoveCardsToLocation(room, aloo, evax, commander, visage);

        assertEquals(5, scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, room), scn.epsilon);
        assertTrue(scn.game().getModifiersQuerying().getForceToLoseFromCardLimit(scn.gameState(), scn.DS, visage) > 1000);

        scn.MoveCardsToLocation(cantina, trooper1);
        scn.MoveCardsToLocation(bay, trooper2);
        assertTrue(scn.game().getModifiersQuerying().getForceToLoseFromCardLimit(scn.gameState(), scn.DS, visage) > 1000);

        scn.ApplyAdHocModifier(new InfluenceModifier(aloo, 1));
        scn.SkipToPhase(Phase.CONTROL);
        assertEquals(6, scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, room), scn.epsilon);
        assertEquals(0, scn.game().getModifiersQuerying().getForceToLoseFromCardLimit(scn.gameState(), scn.DS, visage), scn.epsilon);
    }

    @Test
    public void ConferenceRoomAddsTwoToLightSideSenseAndAlterDrawsWhenOpponentInfluenceExceedsFive() {
        assertTrue(senseDrawWhileCancelingSetForStun(false).contains("as a 1 for"));
        String boosted = senseDrawWhileCancelingSetForStun(true);
        assertTrue(boosted, boosted.contains("as a 3 for"));
        // Set For Stun's own destiny (Stormtrooper, printed 1) is not increased.
        assertTrue(boosted, boosted.contains("as a 1 for"));
        assertFalse(boosted, boosted.contains("as a 4 for"));
    }

    /**
     * Light Side cancels (or tries to cancel) Set For Stun with Sense. Reserve destiny is 1.
     * Set For Stun's own destiny, if the cancel fails, is prepared as 2.
     */
    private String senseDrawWhileCancelingSetForStun(boolean influenceGreaterThanFive) {
        var scn = GetScenario();
        var room = scn.GetDSCard("room");
        var aloo = scn.GetDSCard("aloo");
        var evax = scn.GetDSCard("evax");
        var commander = scn.GetDSCard("commander");
        var sense = scn.GetLSCard("sense");
        var pilot = scn.GetLSCard("pilot");
        var stun = scn.GetDSCard("stun");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToLocation(room, aloo, evax, commander);
        scn.MoveCardsToLocation(room, pilot);
        scn.MoveCardsToLSHand(sense);
        scn.MoveCardsToDSHand(stun);
        if (influenceGreaterThanFive) {
            scn.ApplyAdHocModifier(new InfluenceModifier(aloo, 1));
        }
        scn.PrepareLSDestiny(1);
        scn.PrepareDSDestiny(2);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertEquals(influenceGreaterThanFive ? 6 : 5,
                scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, room), scn.epsilon);
        assertTrue(scn.DSCardPlayAvailable(stun));
        scn.DSPlayCard(stun);
        scn.DSChooseCard(pilot);
        scn.LSPass();
        scn.DSPass();

        assertTrue("Sense was not offered. log=" + String.join("\n", scn.gameState().getLastMessages())
                        + " decision=" + scn.LSGetDecision(),
                scn.LSCardPlayAvailable(sense) || scn.LSPlayUsedInterruptAvailable(sense));
        if (scn.LSPlayUsedInterruptAvailable(sense))
            scn.LSPlayCard(sense);
        else
            scn.LSUseCardAction(sense);
        if (scn.LSHasCardChoiceAvailable(pilot))
            scn.LSChooseCard(pilot);

        scn.DSPass();
        scn.LSPass();
        if (scn.DSDecisionAvailable("COST_TO_DRAW") || scn.DSDecisionAvailable("Optional"))
            scn.DSPass();
        if (scn.LSDecisionAvailable("COST_TO_DRAW") || scn.LSDecisionAvailable("Optional"))
            scn.LSPass();
        if (scn.DSDecisionAvailable("ABOUT_TO_DRAW_DESTINY"))
            scn.DSPass();
        if (scn.LSDecisionAvailable("ABOUT_TO_DRAW_DESTINY"))
            scn.LSPass();

        String log = String.join("\n", scn.gameState().getLastMessages());
        assertTrue("Expected a Sense destiny draw. decision=" + scn.LSGetDecision() + " / " + scn.DSGetDecision() + "\n" + log,
                log.contains("as a "));

        if (influenceGreaterThanFive) {
            // Sense fails (3 is not < ability 2), so Set For Stun still draws destiny 2.
            scn.PassAllResponses();
            log = String.join("\n", scn.gameState().getLastMessages());
        }
        return log;
    }

    @Test
    public void VirtualBlock4LaserGateIsImmuneToSabotageAndPremiereLaserGateIsNot() throws Exception {
        var scn = GetScenario();
        var room = scn.GetDSCard("room");
        var pcGate = scn.GetDSCard("pcgate");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        PhysicalCardImpl vbGate = virtualBlock4LaserGate(scn);
        scn.MoveCardsToLocation(room, pcGate, vbGate);

        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), vbGate, Title.Sabotage));
        assertFalse(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), pcGate, Title.Sabotage));
    }

    @SuppressWarnings("unchecked")
    private PhysicalCardImpl virtualBlock4LaserGate(VirtualTableScenario scn) throws Exception {
        String blueprintId = "2000_9002";
        Field field = SwccgCardBlueprintLibrary.class.getDeclaredField("_blueprintMap");
        field.setAccessible(true);
        Map<String, SwccgCardBlueprint> map = (Map<String, SwccgCardBlueprint>) field.get(VirtualTableScenario._cardLibrary);
        map.put(blueprintId, new TestVirtualBlock4LaserGate());
        PhysicalCardImpl gate = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.DS, VirtualTableScenario._cardLibrary, blueprintId);
        scn.gameState().addCardToZone(gate, Zone.HAND, scn.DS, true, false, false);
        return gate;
    }

    private static final class TestVirtualBlock4LaserGate extends AbstractDevice {
        TestVirtualBlock4LaserGate() {
            super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_LOCATION, Title.Laser_Gate, Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
            setVirtualSuffix(true);
        }
    }
}
