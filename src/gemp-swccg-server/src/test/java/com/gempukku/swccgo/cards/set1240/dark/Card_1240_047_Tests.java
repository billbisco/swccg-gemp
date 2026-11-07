package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1240_047_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("leia", "1_17"); put("luke", "1_19"); put("vehicle", "2_75");
            // This secondary fixture isolates the female non-droid guard; primary fixtures are originals.
            put("droid", "213_38");
        }}, new HashMap<>() {{
            put("unexpected", "1240_47");
            put("xizor", "10_45"); put("mara", "110_10");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.DeclineLSStartingInterrupt, StartingSetup.DeclineDSStartingInterrupt,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void UnexpectedInterruptionPrintedStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var bp = scn.GetDSCard("unexpected").getBlueprint();
        assertEquals("Unexpected Interruption", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(2, bp.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, bp.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals("'Sir! Sir! I've isolated the reverse power flux coupling!'", bp.getLore());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.DAGOBAH, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<Keyword>());
    }

    private VirtualTableScenario Ready() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("xizor"),
                scn.GetLSCard("leia"), scn.GetLSCard("luke"));
        scn.MoveCardsToDSHand(scn.GetDSCard("unexpected"));
        scn.EnsureDSForcePile(10);
        scn.SkipToPhase(Phase.BATTLE);
        return scn;
    }

    private void Play(VirtualTableScenario scn, int destiny) {
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("unexpected")));
        scn.PrepareDSDestiny(destiny);
        scn.DSPlayCard(scn.GetDSCard("unexpected"));
        scn.DSChooseCard(scn.GetLSCard("leia"));
        scn.PassAllResponses();
    }

    @Test
    public void UnexpectedInterruptionExcludesFemaleAndAddsHerPowerAfterSuccessfulDraw() {
        var scn = Ready();
        assertEquals(3, scn.GetPower(scn.GetLSCard("leia")));
        assertEquals(3, scn.GetAbility(scn.GetLSCard("leia")));
        int force = scn.GetDSForcePileCount() - 1; // Ordinary battle initiation costs1.
        Play(scn, 2);
        assertFalse(scn.gameState().isParticipatingInBattle(scn.GetLSCard("leia")));
        assertTrue(scn.game().getModifiersQuerying().isExcludedFromBattle(scn.gameState(), scn.GetLSCard("leia")));
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetLSCard("leia"));
        assertEquals(8, scn.GetPower(scn.GetDSCard("xizor")));
        assertEquals(force, scn.GetDSForcePileCount());
        assertInZone(Zone.USED_PILE, scn.GetDSCard("unexpected"));
    }

    @Test
    public void UnexpectedInterruptionDoesNotExcludeOrAddPowerAtEqualDestinyPlusTwo() {
        var scn = Ready();
        Play(scn, 1);
        assertTrue(scn.gameState().isParticipatingInBattle(scn.GetLSCard("leia")));
        assertFalse(scn.game().getModifiersQuerying().isExcludedFromBattle(scn.gameState(), scn.GetLSCard("leia")));
        assertEquals(5, scn.GetPower(scn.GetDSCard("xizor")));
        assertInZone(Zone.USED_PILE, scn.GetDSCard("unexpected"));
    }

    @Test
    public void UnexpectedInterruptionPowerLastsAfterBattleAndExpiresAtEndOfTurn() {
        var scn = Ready();
        Play(scn, 2);
        scn.PrepareDSDestiny(0);
        scn.PrepareLSDestiny(0);
        scn.SkipToEndOfPowerSegment(true);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertTrue(scn.GetUnpaidLSBattleDamage() > 0);
        scn.LSPayRemainingBattleDamageFromReserveDeck();
        scn.SkipToPhase(Phase.MOVE);
        assertEquals(8, scn.GetPower(scn.GetDSCard("xizor")));
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetLSCard("leia"));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(5, scn.GetPower(scn.GetDSCard("xizor")));
    }

    @Test
    public void UnexpectedInterruptionCannotTargetMalesFemaleDroidsOrYourOwnFemale() {
        var scn = Ready();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("droid"), scn.GetDSCard("mara"));
        assertTrue(scn.HasKeyword(scn.GetLSCard("droid"), Keyword.FEMALE));
        assertTrue(scn.HasKeyword(scn.GetDSCard("mara"), Keyword.FEMALE));
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        scn.DSPlayCard(scn.GetDSCard("unexpected"));
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetLSCard("leia")));
        assertTrue(scn.DSHasCardChoiceNotAvailable(scn.GetLSCard("luke")));
        assertTrue(scn.DSHasCardChoiceNotAvailable(scn.GetLSCard("droid")));
        assertTrue(scn.DSHasCardChoiceNotAvailable(scn.GetDSCard("mara")));
    }

    @Test
    public void UnexpectedInterruptionIsNotPlayableBeforeBattleInitiation() {
        var scn = Ready();
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("unexpected")));
    }

    @Test
    public void UnexpectedInterruptionIsNotPlayableLaterInTheWeaponsSegment() {
        var scn = Ready();
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("unexpected")));
    }

    @Test
    public void UnexpectedInterruptionIsNotPlayableWhenXizorIsAtAnotherSite() {
        var scn = Ready();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("xizor"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("mara"));
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("unexpected")));
    }

    @Test
    public void UnexpectedInterruptionCannotTargetFemaleInsideAnEnclosedVehicle() {
        var scn = Ready();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("vehicle"));
        scn.BoardAsPassenger(scn.GetLSCard("vehicle"), scn.GetLSCard("leia"));
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("unexpected")));
    }

    @Test
    public void UnexpectedInterruptionDoesNotExcludeOrAddPowerWhenDestinyCannotBeDrawn() {
        var scn = Ready();
        while (scn.GetDSReserveDeckCount() > 0) {
            scn.MoveCardsToDSHand((com.gempukku.swccgo.game.PhysicalCardImpl) scn.GetDSReserveDeck().getLast());
        }
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        scn.DSPlayCard(scn.GetDSCard("unexpected"));
        scn.DSChooseCard(scn.GetLSCard("leia"));
        scn.PassAllResponses();
        assertTrue(scn.gameState().isParticipatingInBattle(scn.GetLSCard("leia")));
        assertEquals(5, scn.GetPower(scn.GetDSCard("xizor")));
        assertInZone(Zone.USED_PILE, scn.GetDSCard("unexpected"));
    }

    @Test
    public void UnexpectedInterruptionAddsPowerToXizorWithoutBoostingAnotherFriendlyParticipant() {
        var scn = Ready();
        var xizor = scn.GetDSCard("xizor");
        var mara = scn.GetDSCard("mara");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), mara);
        int originalXizorPower = scn.GetPower(xizor);
        int originalMaraPower = scn.GetPower(mara);
        Play(scn, 2);
        assertEquals(originalXizorPower + 3, scn.GetPower(xizor));
        assertEquals(originalMaraPower, scn.GetPower(mara));
        assertTrue(scn.gameState().isParticipatingInBattle(mara));
    }
}
