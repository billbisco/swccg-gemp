package com.gempukku.swccgo.cards.set1210.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_115_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("fgst", "1210_115");
                    put("falcon", "1_143");
                    put("han", "1_11");
                    put("db94", "1_129");
                    put("xwing", "1_146");
                    put("biggs", "1_3");
                    put("corvette", "1_140");
                }},
                new HashMap<>() {{
                    put("avenger", "4_166");
                    put("lateral", "1_222");
                    put("stormtrooper", "1_194");
                    put("tatooine", "1_289");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    protected VirtualTableScenario GetGroundScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("fgst", "1210_115");
                    put("db94", "1_129");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                    put("ds2bay", "9_145");
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
    public void FusionGeneratorSupplyTanksStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("fgst").getBlueprint();

        assertEquals("Fusion Generator Supply Tanks", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void FusionGeneratorSupplyTanksDeploysOnADockingBay() {
        var scn = GetGroundScenario();
        var fgst = scn.GetLSCard("fgst");
        var db94 = scn.GetLSCard("db94");

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLSHand(fgst);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(fgst));
        scn.LSPlayCard(fgst);
        scn.LSChooseCard(db94);
        scn.PassAllResponses();
        assertEquals(db94, fgst.getAttachedTo());
    }

    @Test
    public void FusionGeneratorSupplyTanksMakesYourUniqueStarshipsPowerManeuverAndHyperspeedPlusOne() {
        var scn = GetScenario();
        var fgst = scn.GetLSCard("fgst");
        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var xwing = scn.GetLSCard("xwing");
        var biggs = scn.GetLSCard("biggs");
        var avenger = scn.GetDSCard("avenger");
        var db94 = scn.GetLSCard("db94");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(system, falcon, xwing, avenger);
        scn.BoardAsPilot(falcon, han);
        scn.BoardAsPilot(xwing, biggs);
        int falconPower = scn.GetPower(falcon);
        int falconManeuver = scn.GetManeuver(falcon);
        int falconHyperspeed = scn.GetHyperspeed(falcon);
        int xwingPower = scn.GetPower(xwing);
        int avengerPower = scn.GetPower(avenger);
        scn.AttachCardsTo(db94, fgst);
        assertEquals(falconPower + 1, scn.GetPower(falcon));
        assertEquals(falconManeuver + 1, scn.GetManeuver(falcon));
        assertEquals(falconHyperspeed + 1, scn.GetHyperspeed(falcon));
        assertEquals(xwingPower, scn.GetPower(xwing));
        assertEquals(avengerPower, scn.GetPower(avenger));
    }

    @Test
    public void FusionGeneratorSupplyTanksCancelsLateralDamageOnTableByGoingToLostPile() {
        var scn = GetScenario();
        var fgst = scn.GetLSCard("fgst");
        var db94 = scn.GetLSCard("db94");
        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var lateral = scn.GetDSCard("lateral");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.AttachCardsTo(db94, fgst);
        scn.MoveCardsToLocation(system, falcon);
        scn.BoardAsPilot(falcon, han);
        scn.MoveCardsToDSHand(lateral);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(lateral));
        scn.DSPlayCard(lateral);
        scn.DSChooseCard(system);
        scn.DSChooseCard(falcon);
        assertTrue(scn.LSActionAvailable("Cancel Lateral Damage") || scn.LSDecisionAvailable("optional"));
        if (scn.LSActionAvailable("Cancel Lateral Damage")) {
            scn.LSChooseAction("Cancel Lateral Damage");
        } else {
            scn.LSChooseAction("Cancel");
        }
        scn.PassAllResponses();
        assertTrue(fgst.getZone() == Zone.LOST_PILE || fgst.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(lateral.getZone() == Zone.LOST_PILE || lateral.getZone() == Zone.TOP_OF_LOST_PILE
                || lateral.getZone() == Zone.HAND || lateral.getZone() == Zone.VOID);
    }

    @Test
    public void FusionGeneratorSupplyTanksGoesToUsedPileWhenOpponentControlsThisLocation() {
        var scn = GetGroundScenario();
        var fgst = scn.GetLSCard("fgst");
        var ds2bay = scn.GetDSCard("ds2bay");
        var stormtrooper = scn.GetDSCard("stormtrooper");

        scn.StartGame();
        scn.MoveLocationToTable(ds2bay);
        scn.AttachCardsTo(ds2bay, fgst);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(Zone.ATTACHED, fgst.getZone());
        scn.MoveCardsToLocation(ds2bay, stormtrooper);
        scn.LSPass();
        scn.DSPass();
        if (scn.GetLSAvailableActions().stream().anyMatch(a -> a != null && a.contains("Place in Used Pile"))) {
            scn.LSChooseAction("Place in Used Pile");
        } else if (scn.GetDSAvailableActions().stream().anyMatch(a -> a != null && a.contains("Place in Used Pile"))) {
            scn.DSChooseAction("Place in Used Pile");
        }
        scn.PassAllResponses();
        assertTrue(fgst.getZone() == Zone.USED_PILE || fgst.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
