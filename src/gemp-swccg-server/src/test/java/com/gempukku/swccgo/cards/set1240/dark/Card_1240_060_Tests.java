package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.*;

public class Card_1240_060_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
                    put("corvette", "1_140"); put("luke", "1_19"); put("cannon", "1_159");
                }},
                new HashMap<>() {{
                    put("katana", "1240_60");
                    put("dreadnaught", "106_13");
                    put("other", "106_13");
                    put("devastator", "1_301");
                }}, 40, 40, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void KatanaStatsIconsKeywordsAndCapacityMatchPrintedShip() {
        var scn = GetScenario();
        var bp = scn.GetDSCard("katana").getBlueprint();
        assertEquals("Katana", bp.getTitle());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(6, bp.getDeployCost(), scn.epsilon);
        assertEquals(5, bp.getPower(), scn.epsilon);
        assertEquals(4, bp.getArmor(), scn.epsilon);
        assertNull(bp.getManeuver());
        assertEquals(3, bp.getHyperspeed(), scn.epsilon);
        assertEquals(6, bp.getForfeit(), scn.epsilon);
        assertEquals(3, bp.getPilotCapacity());
        assertEquals(6, bp.getPassengerCapacity());
        assertEquals(4, bp.getStarfighterOrTIECapacity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.STARSHIP)));
        scn.BlueprintModelTypeCheck(bp, new ArrayList<>(List.of(ModelType.DREADNAUGHT_CLASS_HEAVY_CRUISER)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.SHIP_DOCKING_CAPABILITY)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.STARSHIP, Icon.SPECIAL_EDITION,
                Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_4)));
    }

    @Test
    public void KatanaDeploysPilotedToSystemForSixForce() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSHand(scn.GetDSCard("katana"));
        scn.EnsureDSForcePile(6);
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(scn.GetDSCard("katana"));
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetDSCard("katana"));
        assertEquals(force - 6, scn.GetDSForcePileCount());
        assertEquals(3, scn.GetAbility(scn.GetDSCard("katana")));
        assertTrue(scn.game().getModifiersQuerying().isPiloted(scn.gameState(), scn.GetDSCard("katana"), false));
    }

    @Test
    public void KatanaCannotDeployWithOnlyFiveForce() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSHand(scn.GetDSCard("katana"));
        scn.EnsureDSForcePile(5);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertEquals(5, scn.GetDSForcePileCount());
        assertFalse(scn.DSDeployAvailable(scn.GetDSCard("katana")));
    }

    private VirtualTableScenario Ready() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("katana"),
                scn.GetDSCard("dreadnaught"), scn.GetDSCard("devastator"));
        return scn;
    }

    @Test
    public void KatanaBoostsOnlyOtherFriendlyDreadnaughtsHere() {
        var scn = Ready();
        var dread = scn.GetDSCard("dreadnaught");
        assertEquals(6, scn.GetPower(dread));
        assertEquals(7, scn.GetForfeit(dread));
        assertTrue(scn.HasKeyword(dread, Keyword.HEROIC));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), dread), scn.epsilon);
        assertEquals(5, scn.GetPower(scn.GetDSCard("katana")));
        assertEquals(6, scn.GetForfeit(scn.GetDSCard("katana")));
        assertFalse(scn.HasKeyword(scn.GetDSCard("katana"), Keyword.HEROIC));
        assertFalse(scn.HasKeyword(scn.GetDSCard("devastator"), Keyword.HEROIC));
    }

    @Test
    public void KatanaDoesNotBoostDreadnaughtAtAnotherSystemAndRestoresBonusOnReturn() {
        var scn = Ready();
        var dread = scn.GetDSCard("dreadnaught");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), dread);
        assertEquals(5, scn.GetPower(dread));
        assertEquals(5, scn.GetForfeit(dread));
        assertFalse(scn.HasKeyword(dread, Keyword.HEROIC));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), dread), scn.epsilon);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), dread);
        assertEquals(6, scn.GetPower(dread));
        assertTrue(scn.HasKeyword(dread, Keyword.HEROIC));
    }

    @Test
    public void KatanaDoesNotBoostAnOpponentsDreadnaught() {
        var scn = Ready();
        var dread = scn.GetDSCard("other");
        dread.setOwner(scn.LS);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), dread);
        assertEquals(scn.LS, dread.getOwner());
        assertEquals(5, scn.GetPower(dread));
        assertEquals(5, scn.GetForfeit(dread));
        assertFalse(scn.HasKeyword(dread, Keyword.HEROIC));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), dread), scn.epsilon);
    }

    @Test
    public void KatanaLeavingTableRemovesAllGrantedBonuses() {
        var scn = Ready();
        var dread = scn.GetDSCard("dreadnaught");
        scn.MoveCardsToDSHand(scn.GetDSCard("katana"));
        assertEquals(5, scn.GetPower(dread));
        assertEquals(5, scn.GetForfeit(dread));
        assertFalse(scn.HasKeyword(dread, Keyword.HEROIC));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), dread), scn.epsilon);
    }

    @Test
    public void KatanaGrantedHeroicForcesWeaponToTargetDreadnaughtBeforeKatana() {
        var scn = WeaponReady();
        scn.LSUseCardAction(scn.GetLSCard("cannon"), "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetDSCard("dreadnaught")));
        assertTrue(scn.LSHasCardChoiceNotAvailable(scn.GetDSCard("katana")));
    }

    @Test
    public void KatanaGrantedHeroicAddsOneWeaponDestinyAndHitsAtArmorBoundary() {
        var scn = WeaponReady();
        scn.PrepareLSDestiny(4);
        scn.LSUseCardAction(scn.GetLSCard("cannon"), "Fire");
        scn.LSChooseCard(scn.GetDSCard("dreadnaught"));
        scn.PassWeaponFireWithDestinyDraw();
        assertTrue(scn.GetDSCard("dreadnaught").isHit());
    }

    private VirtualTableScenario WeaponReady() {
        var scn = Ready();
        scn.MoveCardsToDSHand(scn.GetDSCard("devastator"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("corvette"));
        scn.AttachCardsTo(scn.GetLSCard("corvette"), scn.GetLSCard("cannon"));
        scn.EnsureLSForcePile(10);
        scn.LSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation());
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("cannon"), "Fire"));
        return scn;
    }

    @Test
    public void KatanaAndFriendlyDreadnaughtIgnoreThreeAttritionInActualBattle() {
        var scn = BattleReady(3);
        assertEquals(3, scn.GetUnpaidDSAttrition());
        scn.SkipToPhase(Phase.MOVE);
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetDSCard("katana"));
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetDSCard("dreadnaught"));
    }

    @Test
    public void KatanaAndFriendlyDreadnaughtAreVulnerableAtFourAttrition() {
        var scn = BattleReady(4);
        assertEquals(4, scn.GetUnpaidDSAttrition());
        assertTrue(scn.AwaitingDSAttritionPayment());
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("katana")));
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("dreadnaught")));
    }

    private VirtualTableScenario BattleReady(int destiny) {
        return BattleReady(destiny, true);
    }

    @Test
    public void KatanaMustSatisfyExactlyFourAttritionWithoutOtherFriendlyShips() {
        var scn = BattleReady(4, false);
        scn.DSPayRemainingBattleDamageFromReserveDeck();
        assertEquals("1", scn.GetADParam(scn.DS, "min")[0]);
        scn.DSPayAttritionFromCardInPlay(scn.GetDSCard("katana"));
        assertEquals(Zone.LOST_PILE, scn.GetDSCard("katana").getZone());
    }

    private VirtualTableScenario BattleReady(int destiny, boolean otherDreadnaught) {
        var scn = Ready();
        var system = scn.GetDSStartingLocation();
        scn.MoveCardsToDSHand(scn.GetDSCard("devastator"));
        if (!otherDreadnaught) scn.MoveCardsToDSHand(scn.GetDSCard("dreadnaught"));
        scn.MoveCardsToLocation(system, scn.GetLSCard("corvette"));
        scn.BoardAsPilot(scn.GetLSCard("corvette"), scn.GetLSCard("luke"));
        scn.EnsureDSForcePile(5);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(system);
        scn.PrepareDSDestiny(0);
        scn.PrepareLSDestiny(destiny);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(destiny, scn.GetLSTotalDestiny());
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        return scn;
    }
}
