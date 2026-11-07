package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_1240_061_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("raider", "1240_61");
                    put("evazan", "1_172"); // smuggler alien pilot
                    put("rodian", "2_104"); // non-smuggler alien pilot
                    put("trooper", "1_194"); // non-alien
                    put("kessel", "1_288");
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
    public void RaiderCraftStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("raider").getBlueprint();

        assertEquals("Raider Craft", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getManeuver(), scn.epsilon);
        assertEquals(3, card.getHyperspeed(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertNull(card.getArmor());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.SENTINEL_CLASS_LANDING_CRAFT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INDEPENDENT);
            add(Icon.STARSHIP);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RaiderCraftCapacitiesAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("raider").getBlueprint();
        assertEquals(2, card.getPilotCapacity());
        assertEquals(4, card.getPassengerCapacity());
        assertEquals(1, card.getVehicleCapacity());
    }

    @Test
    public void RaiderCraftSmugglerIsMatchingPilotAndAddsOneManeuver() {
        var scn = GetScenario();
        var raider = scn.GetDSCard("raider");
        var evazan = scn.GetDSCard("evazan");
        var kessel = scn.GetDSCard("kessel");

        scn.StartGame();
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(kessel, raider);
        scn.BoardAsPilot(raider, evazan);

        assertTrue(scn.IsMatchingPilot(raider, evazan));
        assertEquals(4, scn.GetManeuver(raider));
    }

    @Test
    public void RaiderCraftNonMatchingAlienPilotAddsNoManeuver() {
        var scn = GetScenario();
        var raider = scn.GetDSCard("raider");
        var rodian = scn.GetDSCard("rodian");
        var kessel = scn.GetDSCard("kessel");

        scn.StartGame();
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(kessel, raider);
        scn.BoardAsPilot(raider, rodian);

        assertFalse(scn.IsMatchingPilot(raider, rodian));
        assertEquals(3, scn.GetManeuver(raider));
    }

    @Test
    public void RaiderCraftMatchingPilotDrawsOneBattleDestinyIfNotAbleToOtherwise() {
        var scn = GetScenario();
        var raider = scn.GetDSCard("raider");
        var evazan = scn.GetDSCard("evazan");
        var kessel = scn.GetDSCard("kessel");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(kessel, raider, xwing);
        scn.BoardAsPilot(raider, evazan);

        scn.SkipToPhase(Phase.BATTLE);
        scn.EnsureDSForcePile(2);
        scn.DSInitiateBattle(kessel);
        scn.PassAllResponses();
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void RaiderCraftNonMatchingPilotDrawsNoBattleDestiny() {
        var scn = GetScenario();
        var raider = scn.GetDSCard("raider");
        var rodian = scn.GetDSCard("rodian");
        var kessel = scn.GetDSCard("kessel");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(kessel, raider, xwing);
        scn.BoardAsPilot(raider, rodian);

        scn.SkipToPhase(Phase.BATTLE);
        scn.EnsureDSForcePile(2);
        scn.DSInitiateBattle(kessel);
        scn.PassAllResponses();
        assertEquals(0, scn.GetDSBattleDestinyCount());
    }
}
