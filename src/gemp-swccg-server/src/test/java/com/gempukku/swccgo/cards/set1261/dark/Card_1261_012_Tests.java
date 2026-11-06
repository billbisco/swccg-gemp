package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_012_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("luke", "1_21"); put("han", "1_11"); put("rebel", "1_28"); }},
                new HashMap<>() {{ put("warrior", "1261_12"); put("other", "1261_12"); put("vader", "1_168"); put("trooper", "1_194"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void MassassiWarriorPrintedStatsAndBlankLoreArePreserved() {
        var scn = scenario(); var card = scn.GetDSCard("warrior").getBlueprint();
        assertEquals("Massassi Warrior", card.getTitle()); assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide()); assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(2, card.getDestiny(), scn.epsilon); assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon); assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon); assertTrue(card.getLore() == null || card.getLore().isEmpty());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, card.getExpansionSet()); assertEquals(Rarity.V, card.getRarity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.SITH); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{ add(Icon.SITH); add(Icon.WARRIOR); add(Icon.SPECIAL_EDITION); add(Icon.LEGACY_BLOCK_6); }});
    }

    @Test
    public void MassassiWarriorDiscountRequiresDarkJediAtDeploymentSite() {
        var scn = scenario(); var warrior = scn.GetDSCard("warrior"); var vader = scn.GetDSCard("vader");
        scn.StartGame(); var site = scn.GetDSStartingLocation(); var elsewhere = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(elsewhere, vader); var q = scn.game().getModifiersQuerying();
        assertEquals(3, q.getDeployCost(scn.gameState(), warrior, warrior, site, false, null, false, 0, null, false), scn.epsilon);
        scn.MoveCardsToLocation(site, vader);
        assertEquals(2, q.getDeployCost(scn.gameState(), warrior, warrior, site, false, null, false, 0, null, false), scn.epsilon);
        scn.MoveCardsToLocation(elsewhere, vader); scn.MoveCardsToLocation(site, scn.GetLSCard("luke"));
        assertEquals(3, q.getDeployCost(scn.gameState(), warrior, warrior, site, false, null, false, 0, null, false), scn.epsilon);
    }

    @Test
    public void MassassiWarriorPowerPenaltyRequiresAnotherSithHereAndAffectsOnlyOpponent() {
        var scn = scenario(); var warrior = scn.GetDSCard("warrior"); var other = scn.GetDSCard("other"); var luke = scn.GetLSCard("luke");
        scn.StartGame(); var site = scn.GetDSStartingLocation(); scn.MoveCardsToLocation(site, warrior, luke);
        assertEquals(5, scn.GetPower(luke));
        scn.MoveCardsToLocation(site, other); assertEquals(4, scn.GetPower(luke)); assertEquals(3, scn.GetPower(warrior));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), other); assertEquals(5, scn.GetPower(luke));
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader")); assertEquals(5, scn.GetPower(luke));
    }

    @Test
    public void MassassiWarriorDrawsOneBattleDestinyWithAnotherSithDespiteAbilityBelowFour() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("warrior"), scn.GetDSCard("other"), scn.GetLSCard("luke"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); assertEquals(1, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void MassassiWarriorAloneDoesNotGrantBattleDestiny() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("warrior"), scn.GetLSCard("luke"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); assertEquals(0, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void MassassiWarriorLostAtJedisSiteMakesOpponentLoseOneForce() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation(); var warrior = scn.GetDSCard("warrior");
        scn.MoveCardsToLocation(site, warrior, scn.GetLSCard("luke"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); scn.SkipToDamageSegment(false); scn.PassDamageSegmentActions();
        int lost = scn.GetLSLostPileCount(); scn.DSPayBattleDamageFromCardInPlay(warrior);
        assertTrue(scn.AwaitingLSForceLossPayment()); scn.LSPayForceLossFromReserveDeck(); scn.PassAllResponses();
        assertEquals(lost + 1, scn.GetLSLostPileCount()); com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, warrior);
    }

    @Test
    public void MassassiWarriorLostWithoutJediDoesNotCauseForceLoss() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation(); var warrior = scn.GetDSCard("warrior");
        scn.MoveCardsToLocation(site, warrior, scn.GetLSCard("han"), scn.GetLSCard("rebel"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); scn.SkipToDamageSegment(false); scn.PassDamageSegmentActions();
        int lost = scn.GetLSLostPileCount(); scn.DSPayBattleDamageFromCardInPlay(warrior); scn.PassAllResponses();
        assertFalse(scn.AwaitingLSForceLossPayment()); assertEquals(lost, scn.GetLSLostPileCount());
    }
}
