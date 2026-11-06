package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1271_036_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("wedge", "1271_36"); put("red2", "2_70"); put("ywing", "1_147"); put("corvette", "1_140"); put("luke", "1_19");
        }}, new HashMap<>() {{ put("isd", "1_302"); put("vader", "1_168"); }}, 10, 10,
                StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void WedgeAntillesLegendaryRogueStatsAndKeywordsAreCorrect() {
        var scn = GetScenario(); var physical = scn.GetLSCard("wedge"); var card = physical.getBlueprint();
        assertEquals("Wedge Antilles, Legendary Rogue", card.getTitle()); assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide()); assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(4, card.getDestiny(), scn.epsilon); assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon); assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon); assertEquals(1, scn.GetLandspeed(physical));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.REBEL); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{ add(Icon.REBEL); add(Icon.PILOT); add(Icon.WARRIOR); add(Icon.A_NEW_HOPE); add(Icon.LEGACY_BLOCK_7); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.RED_SQUADRON); add(Keyword.ROGUE_SQUADRON); }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{ add(Persona.WEDGE); }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, card.getExpansionSet()); assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WedgeAntillesLegendaryRogueAddsPowerManeuverAndImmunityToMatchingRedTwo() {
        var scn = GetScenario(); var ship = scn.GetLSCard("red2");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), ship);
        scn.BoardAsPilot(ship, scn.GetLSCard("wedge"));
        assertEquals(6, scn.GetPower(ship)); assertEquals(6, scn.GetManeuver(ship));
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
        assertEquals(2, scn.GetIconCount(ship, Icon.NAV_COMPUTER));
    }

    @Test
    public void WedgeAntillesLegendaryRogueOnlyAddsPowerToAnotherShip() {
        var scn = GetScenario(); var ship = scn.GetLSCard("ywing");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), ship);
        scn.BoardAsPilot(ship, scn.GetLSCard("wedge"));
        assertEquals(5, scn.GetPower(ship)); assertEquals(3, scn.GetManeuver(ship));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
    }

    @Test
    public void WedgeAntillesLegendaryRogueDrawsOneBattleDestinyWhenOtherwiseUnableOnRedTwo() {
        var scn = GetScenario(); var ship = scn.GetLSCard("red2"); var system = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(system, ship, scn.GetDSCard("isd"));
        scn.BoardAsPilot(ship, scn.GetLSCard("wedge"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(5); scn.LSInitiateBattle(system); scn.PassAllResponses();
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void WedgeAntillesLegendaryRogueDoesNotGrantBattleDestinyOnAnotherShip() {
        var scn = GetScenario(); var ship = scn.GetLSCard("ywing"); var system = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(system, ship, scn.GetDSCard("isd"));
        scn.BoardAsPilot(ship, scn.GetLSCard("wedge"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(5); scn.LSInitiateBattle(system); scn.PassAllResponses();
        assertEquals(0, scn.GetLSBattleDestinyCount());
    }
    @Test
    public void WedgeAntillesLegendaryRogueMayPayOneForceToGoToUsedPileAfterForfeitFromRedTwo() {
        var scn = ForfeitWedgeFromShip("red2", 2, true, true);
        assertInZone(Zone.USED_PILE, scn.GetLSCard("wedge"));
        assertEquals(1, scn.GetLSForcePileCount());
    }

    @Test
    public void WedgeAntillesLegendaryRogueCannotGoToUsedPileAfterForfeitFromAnotherShip() {
        var scn = ForfeitWedgeFromShip("ywing", 2, false, false);
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("wedge"));
        assertEquals(2, scn.GetLSForcePileCount());
    }

    @Test
    public void WedgeAntillesLegendaryRogueCannotPayForUsedPileReturnWithoutForce() {
        var scn = ForfeitWedgeFromShip("red2", 0, false, false);
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("wedge"));
    }

    @Test
    public void WedgeAntillesLegendaryRogueMayDeclineUsedPileReturn() {
        var scn = ForfeitWedgeFromShip("red2", 2, true, false);
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("wedge"));
        assertEquals(2, scn.GetLSForcePileCount());
    }

    @Test
    public void WedgeAntillesLegendaryRogueBonusesEndWhenHeStopsPilotingRedTwo() {
        var scn = GetScenario(); var ship = scn.GetLSCard("red2"); var wedge = scn.GetLSCard("wedge");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), ship); scn.BoardAsPilot(ship, wedge);
        assertEquals(6, scn.GetManeuver(ship));
        scn.MoveCardsToLSHand(wedge); scn.BoardAsPilot(ship, scn.GetLSCard("luke"));
        assertEquals(4, scn.GetManeuver(ship)); assertEquals(1, scn.GetIconCount(ship, Icon.NAV_COMPUTER));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship), scn.epsilon);
    }

    @Test
    public void WedgeAntillesLegendaryRogueDoesNotAddExtraDestinyWhenAlreadyAbleToDraw() {
        var scn = GetScenario(); var ship = scn.GetLSCard("red2"); var corvette = scn.GetLSCard("corvette");
        var system = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(system, ship, corvette, scn.GetDSCard("isd"));
        scn.BoardAsPilot(ship, scn.GetLSCard("wedge")); scn.BoardAsPilot(corvette, scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(5); scn.LSInitiateBattle(system); scn.PassAllResponses();
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    private VirtualTableScenario ForfeitWedgeFromShip(String shipName, int force, boolean expected, boolean use) {
        var scn = GetScenario(); var wedge = scn.GetLSCard("wedge"); var ship = scn.GetLSCard(shipName);
        var isd = scn.GetDSCard("isd"); var system = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(system, ship, isd);
        scn.BoardAsPilot(ship, wedge); scn.BoardAsPilot(isd, scn.GetDSCard("vader"));
        scn.EnsureDSForcePile(5); scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureLSForcePile(force); scn.DrainLSForcePileTo(force);
        scn.DSInitiateBattle(system); scn.PassAllResponses(); scn.SkipToDamageSegment(false);
        scn.PassPowerSegmentActions(); scn.PassAllResponses(); scn.PassDamageSegmentActions();
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSChooseCard(wedge);
        boolean offered = false;
        for (int n = 0; n < 40 && scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"); n++) {
            if (scn.GetDecidingPlayer().equals(scn.LS) && scn.LSCardActionAvailable(wedge, "Place Wedge")) {
                offered = true; assertTrue(expected);
                if (use) { scn.LSUseCardAction(wedge, "Place Wedge"); scn.PassAllResponses(); break; }
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertEquals(expected, offered);
        return scn;
    }

}
