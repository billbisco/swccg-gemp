package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_002_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); }},
                new HashMap<>() {{ put("piett", "1261_2"); put("executor", "4_167"); put("devastator", "1_301");
                    put("driver", "3_83"); put("hoth", "3_143"); put("tie", "1_299"); put("flagship", "9_125"); put("payment", "1_194"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void AdmiralPiettPrintedStatsAndCharacteristicsAreCorrect() {
        var scn = scenario(); var card = scn.GetDSCard("piett").getBlueprint();
        assertEquals("Admiral Piett", card.getTitle()); assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness()); assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon); assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon); assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, card.getExpansionSet()); assertEquals(Rarity.V, card.getRarity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.IMPERIAL); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.ADMIRAL); add(Keyword.LEADER); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{ add(Icon.IMPERIAL); add(Icon.PILOT); add(Icon.WARRIOR); add(Icon.DEATH_STAR_II); add(Icon.LEGACY_BLOCK_6); }});
    }

    @Test
    public void AdmiralPiettAddsThreeToAnyShipHePilotsButNotAsPassenger() {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("hoth")); var piett = scn.GetDSCard("piett"); var tie = scn.GetDSCard("tie");
        scn.MoveCardsToLocation(scn.GetDSCard("hoth"), tie);
        scn.BoardAsPilot(tie, piett); assertEquals(4, scn.GetPower(tie));
        var ship = scn.GetDSCard("devastator"); scn.MoveCardsToLocation(scn.GetDSCard("hoth"), ship);
        scn.BoardAsPassenger(ship, piett); assertEquals(9, scn.GetPower(ship));
        scn.BoardAsPilot(ship, piett); assertEquals(12, scn.GetPower(ship));
    }

    @Test
    public void AdmiralPiettDeployDiscountAppliesToExecutorOnly() {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("hoth")); var piett = scn.GetDSCard("piett");
        var executor = scn.GetDSCard("executor"); var ship = scn.GetDSCard("devastator");
        scn.MoveCardsToLocation(scn.GetDSCard("hoth"), executor, ship); var q = scn.game().getModifiersQuerying();
        assertEquals(2, q.getDeployCost(scn.gameState(), piett, piett, executor, false, null, false, 0, null, false), scn.epsilon);
        assertEquals(4, q.getDeployCost(scn.gameState(), piett, piett, ship, false, null, false, 0, null, false), scn.epsilon);
    }

    @Test
    public void AdmiralPiettGrantsExecutorHyperspeedAndImmunityAsPassengerAndRemovesThemWhenLeaving() {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("hoth")); var piett = scn.GetDSCard("piett");
        // Isolate the granted immunity from the real Executor's intrinsic immunity below 12.
        var executor = MissionTestSupport.putInHand(scn, 9183, scn.DS,
                new AbstractCapitalStarship(Side.DARK, 1, 15, 12, 12, null, 2f, 15, "Executor", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R) {
                    { addPersona(Persona.EXECUTOR); setPilotCapacity(10); setPassengerCapacity(10); }
                });
        scn.MoveCardsToLocation(scn.GetDSCard("hoth"), executor);
        scn.BoardAsPilot(executor, scn.GetDSCard("driver")); scn.BoardAsPassenger(executor, piett); var q = scn.game().getModifiersQuerying();
        assertEquals(3, scn.GetHyperspeed(executor));
        assertEquals(5, q.getImmunityToAttritionLessThan(scn.gameState(), executor), scn.epsilon);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), piett);
        assertEquals(2, scn.GetHyperspeed(executor));
        assertEquals(0, q.getImmunityToAttritionLessThan(scn.gameState(), executor), scn.epsilon);
    }

    @Test
    public void AdmiralPiettBattleBonusPaysHandCardToUsedPileAndCannotRepeat() {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("hoth")); var piett = scn.GetDSCard("piett"); var site = scn.GetDSStartingLocation();
        var flagship = scn.GetDSCard("flagship"); var payment = scn.GetDSCard("payment");
        scn.MoveCardsToLocation(site, piett, scn.GetLSCard("obiwan")); scn.MoveCardsToDSHand(payment); scn.MoveCardsToSideOfTable(flagship);
        scn.DSStartBattleAndSkipToWeaponsSegment(site); int power = scn.GetDSTotalPower();
        scn.DSUseCardAction(piett, "Add 3 to total power"); scn.PassAllResponses();
        if (payment.getZone() == Zone.HAND) { scn.DSChooseCard(payment); scn.PassAllResponses(); }
        assertEquals(Zone.USED_PILE, payment.getZone()); assertEquals(power + 3, scn.GetDSTotalPower());
        if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(piett, "Add 3 to total power"));
    }

    @Test
    public void AdmiralPiettBattleBonusNeedsFlagshipOperations() {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("hoth")); var piett = scn.GetDSCard("piett"); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, piett, scn.GetLSCard("obiwan")); scn.MoveCardsToDSHand(scn.GetDSCard("payment"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); assertFalse(scn.DSCardActionAvailable(piett, "Add 3 to total power"));
    }

    @Test
    public void AdmiralPiettCannotPayBattleBonusWithoutHandCard() {
        var scn = scenario(); scn.StartGame(); var piett = scn.GetDSCard("piett"); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, piett, scn.GetLSCard("obiwan")); scn.MoveCardsToSideOfTable(scn.GetDSCard("flagship"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSHand().toArray(new com.gempukku.swccgo.game.PhysicalCardImpl[0]));
        assertFalse(scn.DSCardActionAvailable(piett, "Add 3 to total power"));
    }

    @Test
    public void AdmiralPiettCannotAddPowerToUnrelatedBattle() {
        var scn = scenario(); scn.StartGame(); var piett = scn.GetDSCard("piett"); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), piett);
        scn.MoveCardsToLocation(site, scn.GetDSCard("payment"), scn.GetLSCard("obiwan"));
        scn.MoveCardsToDSHand(scn.GetDSCard("tie")); scn.MoveCardsToSideOfTable(scn.GetDSCard("flagship"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); assertFalse(scn.DSCardActionAvailable(piett, "Add 3 to total power"));
    }
}
