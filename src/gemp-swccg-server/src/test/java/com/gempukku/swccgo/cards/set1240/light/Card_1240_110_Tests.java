package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.*;

public class Card_1240_110_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("yt", "1240_110");
            put("site", "1_133");
            put("boshek", "1_4");
            put("jedi", "1_21");
            put("luke", "1_19");
            put("corvette", "1_140");
            put("droid", "1_5");
        }}, new HashMap<>() {{ put("devastator", "1_301"); put("pilot", "1_164"); }}, 40, 40,
                StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void Yt1300TransportStatsIconsAndKeywordsMatchPrintedShip() {
        var scn = GetScenario();
        var bp = scn.GetLSCard("yt").getBlueprint();
        assertEquals("YT-1300 Transport", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.LIGHT, bp.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(2, bp.getDeployCost(), scn.epsilon);
        assertEquals(2, bp.getPower(), scn.epsilon);
        assertNull(bp.getArmor());
        assertEquals(3, bp.getManeuver(), scn.epsilon);
        assertEquals(5, bp.getHyperspeed(), scn.epsilon);
        assertEquals(4, bp.getForfeit(), scn.epsilon);
        assertEquals(1, bp.getPilotCapacity());
        assertEquals(4, bp.getPassengerCapacity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.STARSHIP)));
        scn.BlueprintModelTypeCheck(bp, new ArrayList<>(List.of(ModelType.MODIFIED_LIGHT_FREIGHTER)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.STARSHIP, Icon.ENDOR, Icon.INDEPENDENT,
                Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_4)));
    }

    @Test
    public void Yt1300TransportDeploysWithPermanentPilotForTwoForce() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLSHand(scn.GetLSCard("yt"));
        scn.EnsureLSForcePile(2);
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(scn.GetLSCard("yt"));
        scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("yt"));
        assertEquals(force - 2, scn.GetLSForcePileCount());
        assertEquals(1, scn.GetAbility(scn.GetLSCard("yt")));
        assertTrue(scn.game().getModifiersQuerying().isPiloted(scn.gameState(), scn.GetLSCard("yt"), false));
    }

    @Test
    public void Yt1300TransportCannotDeployWithOnlyOneForce() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLSHand(scn.GetLSCard("yt"));
        scn.EnsureLSForcePile(1);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertEquals(1, scn.GetLSForcePileCount());
        assertFalse(scn.LSDeployAvailable(scn.GetLSCard("yt")));
    }

    private VirtualTableScenario Ready() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetLSCard("site"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("yt"));
        return scn;
    }

    private void CheckBonus(VirtualTableScenario scn, boolean active) {
        var yt = scn.GetLSCard("yt");
        assertEquals(active ? 5 : 4, scn.GetForfeit(yt));
        assertEquals(active ? 4 : 3, scn.GetManeuver(yt));
        assertEquals(active ? 4 : 0,
                scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), yt), scn.epsilon);
    }

    @Test
    public void Yt1300TransportPermanentIndependentPilotQualifiesForAllBonuses() {
        CheckBonus(Ready(), true);
    }

    @Test
    public void Yt1300TransportAllowsAlienAbilityAtAnotherLocation() {
        var scn = Ready();
        scn.MoveCardsToLocation(scn.GetLSCard("site"), scn.GetLSCard("boshek"));
        CheckBonus(scn, true);
    }

    @Test
    public void Yt1300TransportAllowsJediAbilityAtAnotherLocation() {
        var scn = Ready();
        scn.MoveCardsToLocation(scn.GetLSCard("site"), scn.GetLSCard("jedi"));
        CheckBonus(scn, true);
    }

    @Test
    public void Yt1300TransportAllowsRebelPilotingIndependentStarship() {
        var scn = Ready();
        scn.BoardAsPilot(scn.GetLSCard("yt"), scn.GetLSCard("luke"));
        CheckBonus(scn, true);
    }

    @Test
    public void Yt1300TransportDoesNotAllowRebelPassengerOnIndependentStarship() {
        var scn = Ready();
        scn.BoardAsPassenger(scn.GetLSCard("yt"), scn.GetLSCard("luke"));
        CheckBonus(scn, false);
    }

    @Test
    public void Yt1300TransportDoesNotAllowRebelPilotingNonIndependentStarship() {
        var scn = Ready();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("corvette"));
        scn.BoardAsPilot(scn.GetLSCard("corvette"), scn.GetLSCard("luke"));
        CheckBonus(scn, false);
    }

    @Test
    public void Yt1300TransportDoesNotAllowNonIndependentPermanentPilot() {
        var scn = Ready();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("corvette"));
        CheckBonus(scn, false);
    }

    @Test
    public void Yt1300TransportLosesBonusesForRebelAbilityElsewhereAndRegainsThemWhenItLeaves() {
        var scn = Ready();
        scn.MoveCardsToLocation(scn.GetLSCard("site"), scn.GetLSCard("luke"));
        CheckBonus(scn, false);
        scn.MoveCardsToLSHand(scn.GetLSCard("luke"));
        CheckBonus(scn, true);
    }

    @Test
    public void Yt1300TransportIgnoresOpponentsAbilityAndDroidsWithoutAbility() {
        var scn = Ready();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("devastator"));
        scn.BoardAsPassenger(scn.GetLSCard("yt"), scn.GetLSCard("droid"));
        CheckBonus(scn, true);
    }

    @Test
    public void Yt1300TransportIgnoresThreeAttritionInActualBattle() {
        var scn = BattleReady(3);
        assertEquals(3, scn.GetUnpaidLSAttrition());
        scn.LSPayRemainingBattleDamageFromReserveDeck();
        scn.SkipToPhase(Phase.MOVE);
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("yt"));
    }

    @Test
    public void Yt1300TransportIsVulnerableAtExactlyFourAttrition() {
        var scn = BattleReady(4);
        scn.LSPayRemainingBattleDamageFromReserveDeck();
        assertEquals(4, scn.GetUnpaidLSAttrition());
        assertEquals("1", scn.GetADParam(scn.LS, "min")[0]);
        scn.LSPayAttritionFromCardInPlay(scn.GetLSCard("yt"));
        assertEquals(Zone.LOST_PILE, scn.GetLSCard("yt").getZone());
    }

    private VirtualTableScenario BattleReady(int destiny) {
        var scn = Ready();
        var system = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(system, scn.GetDSCard("devastator"));
        scn.BoardAsPilot(scn.GetDSCard("devastator"), scn.GetDSCard("pilot"));
        scn.EnsureLSForcePile(5);
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(system);
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(destiny);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(destiny, scn.GetDSTotalDestiny());
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        return scn;
    }
}
