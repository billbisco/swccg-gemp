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

public class Card_1240_006_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19"); put("obiwan", "1_21"); put("han", "1_11");
            put("chewie", "2_3"); put("wioslea", "1_33"); put("bolt", "1_38");
        }}, new HashMap<>() {{
            put("guri", "1240_6"); put("xizor", "10_45"); put("motti", "1_164");
            put("droid", "4_91"); put("ship", "1_301"); put("bike", "8_169");
            put("city", "7_277"); put("trooper", "1_194"); put("system", "1_282");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void GuriPrintedStatsIconsKeywordsAndAssassinDroidIdentityAreCorrect() {
        var scn = GetScenario();
        var bp = scn.GetDSCard("guri").getBlueprint();
        assertEquals("Guri", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(2, bp.getDestiny(), scn.epsilon);
        assertEquals(4, bp.getDeployCost(), scn.epsilon);
        assertEquals(6, bp.getPower(), scn.epsilon);
        assertEquals(5, bp.getArmor(), scn.epsilon);
        assertEquals(6, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals("Human-replica droid. Programmed to function as Xizor's personal bodyguard. Black Sun agent. Cost 9 million credits. Worth every decicred.", bp.getLore());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.DROID)));
        scn.BlueprintModelTypeCheck(bp, new ArrayList<>(List.of(ModelType.ASSASSIN)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.FEMALE, Keyword.BLACK_SUN_AGENT,
                Keyword.BODYGUARD, Keyword.ASSASSIN)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.DROID, Icon.PILOT, Icon.WARRIOR,
                Icon.REFLECTIONS_II, Icon.LEGACY_BLOCK_4)));
    }

    @Test
    public void GuriAddsTwoPowerToAStarshipShePilots() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetDSCard("ship"));
        scn.BoardAsPilot(scn.GetDSCard("ship"), scn.GetDSCard("guri"));
        assertEquals(11, scn.GetPower(scn.GetDSCard("ship")));
        scn.MoveCardsToDSHand(scn.GetDSCard("guri"));
        assertEquals(9, scn.GetPower(scn.GetDSCard("ship")));
    }

    @Test
    public void GuriAddsTwoPowerToAVehicleShePilotsButNotAsPassenger() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("bike"));
        scn.BoardAsPilot(scn.GetDSCard("bike"), scn.GetDSCard("guri"));
        assertEquals(3, scn.GetPower(scn.GetDSCard("bike")));
        scn.BoardAsPassenger(scn.GetDSCard("bike"), scn.GetDSCard("guri"));
        scn.BoardAsPilot(scn.GetDSCard("bike"), scn.GetDSCard("motti"));
        assertEquals(3, scn.GetPower(scn.GetDSCard("bike")));
        scn.MoveCardsToDSHand(scn.GetDSCard("guri"));
        assertEquals(3, scn.GetPower(scn.GetDSCard("bike")));
    }

    private VirtualTableScenario DrainReady(boolean agent, boolean coruscant, boolean guriHere) {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetDSCard("city"));
        var site = coruscant ? scn.GetDSCard("city") : scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard(agent ? "xizor" : "motti"));
        scn.MoveCardsToLocation(guriHere ? site : scn.GetLSStartingLocation(), scn.GetDSCard("guri"));
        scn.SkipToPhase(Phase.CONTROL);
        return scn;
    }

    @Test
    public void GuriAddsOneToCoruscantDrainWithAnAgentWhileGuriIsElsewhere() {
        var scn = DrainReady(true, true, false);
        int lost = scn.GetLSLostPileCount();
        scn.DSForceDrainAt(scn.GetDSCard("city"));
        scn.PassAllResponses();
        assertEquals(1, scn.GetForceDrainTotal());
        scn.LSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(lost + 1, scn.GetLSLostPileCount());
    }

    @Test
    public void GuriDoesNotMultiplyDrainBonusForTwoBlackSunAgents() {
        var scn = DrainReady(true, true, true);
        scn.DSForceDrainAt(scn.GetDSCard("city"));
        scn.PassAllResponses();
        assertEquals(1, scn.GetForceDrainTotal());
    }

    @Test
    public void GuriDoesNotEnableDrainAtZeroIconCoruscantSiteWithoutAnAgent() {
        var scn = DrainReady(false, true, false);
        assertTrue(scn.DSDecisionAvailable("Choose Control action or Pass"));
        assertFalse(scn.DSForceDrainAvailable(scn.GetDSCard("city")));
    }

    @Test
    public void GuriDoesNotIncreaseDrainAtAnotherPlanetWithABlackSunAgent() {
        var scn = DrainReady(true, false, true);
        var site = scn.GetDSStartingLocation();
        int base = scn.GetIconCount(site, Icon.LIGHT_FORCE);
        scn.DSForceDrainAt(site);
        scn.PassAllResponses();
        assertEquals(base, scn.GetForceDrainTotal());
    }

    @Test
    public void GuriCannotBeTargetedByRestrainingBoltWhileAnotherDroidCan() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("guri"), scn.GetDSCard("droid"));
        scn.MoveCardsToLSHand(scn.GetLSCard("bolt"));
        scn.EnsureLSForcePile(10);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.LSDeployCard(scn.GetLSCard("bolt"));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetDSCard("droid")));
        assertTrue(scn.LSHasCardChoiceNotAvailable(scn.GetDSCard("guri")));
        scn.LSChooseCard(scn.GetDSCard("droid"));
        scn.PassAllResponses();
        assertEquals(scn.GetDSCard("droid"), scn.GetLSCard("bolt").getAttachedTo());
    }

    @Test
    public void GuriCannotBePurchasedWhileAnotherDroidCanBeTargeted() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("wioslea"),
                scn.GetDSCard("guri"), scn.GetDSCard("droid"));
        scn.EnsureLSForcePile(10);
        scn.SkipToPhase(Phase.CONTROL);
        scn.LSUseCardAction(scn.GetLSCard("wioslea"), "'Purchase' transport vehicle or droid");
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetDSCard("droid")));
        assertTrue(scn.LSHasCardChoiceNotAvailable(scn.GetDSCard("guri")));
    }

    private VirtualTableScenario DamageReady(boolean xizorHere) {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("guri"),
                scn.GetDSCard(xizorHere ? "xizor" : "motti"), scn.GetLSCard("luke"),
                scn.GetLSCard("obiwan"), scn.GetLSCard("han"), scn.GetLSCard("chewie"));
        if (!xizorHere) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("xizor"));
        scn.EnsureDSForcePile(10);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PrepareDSDestiny(0);
        scn.PrepareLSDestiny(7);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(7, scn.GetLSTotalDestiny());
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        return scn;
    }

    @Test
    public void GuriMayForfeitWithXizorToSatisfyAllBattleDamageButOnlyOrdinaryAttrition() {
        var scn = DamageReady(true);
        int attrition = scn.GetUnpaidDSAttrition();
        int forfeit = scn.GetForfeit(scn.GetDSCard("guri"));
        assertEquals(6, forfeit);
        assertTrue(scn.GetUnpaidDSBattleDamage() > forfeit);
        assertTrue(attrition > forfeit);
        scn.DSUseCardAction(scn.GetDSCard("guri"), "Forfeit to satisfy all battle damage");
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("guri"));
        assertEquals(0, scn.GetUnpaidDSBattleDamage());
        assertEquals(attrition - forfeit, scn.GetUnpaidDSAttrition());
    }

    @Test
    public void GuriMayDeclineFullDamageCancellationAndForfeitNormally() {
        var scn = DamageReady(true);
        int damage = scn.GetUnpaidDSBattleDamage();
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("guri"), "Forfeit to satisfy all battle damage"));
        scn.PassAllResponses();
        scn.DSPayBattleDamageFromCardInPlay(scn.GetDSCard("guri"));
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("guri"));
        assertEquals(damage - 6, scn.GetUnpaidDSBattleDamage());
    }

    @Test
    public void GuriCannotCancelAllDamageWhenXizorIsAtAnotherSite() {
        var scn = DamageReady(false);
        assertTrue(scn.GetUnpaidDSBattleDamage() > 6);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("guri"), "Forfeit to satisfy all battle damage"));
    }

    private VirtualTableScenario ImmunityReady(int destiny) {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("guri"), scn.GetDSCard("trooper"), scn.GetLSCard("obiwan"));
        scn.EnsureDSForcePile(10);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PrepareLSDestiny(destiny);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(destiny, scn.GetLSTotalDestiny());
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        scn.DSPayRemainingBattleDamageFromReserveDeck();
        scn.DSPayAttritionFromCardInPlay(scn.GetDSCard("trooper"));
        return scn;
    }

    @Test
    public void GuriSurvivesFourAttritionAfterTheOtherCharacterIsForfeited() {
        var scn = ImmunityReady(4);
        scn.SkipToPhase(Phase.MOVE);
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetDSCard("guri"));
    }

    @Test
    public void GuriIsStillVulnerableAtExactlyFiveAttrition() {
        var scn = ImmunityReady(5);
        assertTrue(scn.GetUnpaidDSAttrition() > 0);
        assertEquals("1", scn.GetADParam(scn.DS, "min")[0]);
        scn.DSPayAttritionFromCardInPlay(scn.GetDSCard("guri"));
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("guri"));
    }

    @Test
    public void GuriDeploysToASiteForFourForce() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSHand(scn.GetDSCard("guri"));
        scn.EnsureDSForcePile(4);
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(scn.GetDSCard("guri"));
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetDSCard("guri"));
        assertEquals(force - 4, scn.GetDSForcePileCount());
    }

    @Test
    public void GuriCannotDeployWithOnlyThreeForce() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSHand(scn.GetDSCard("guri"));
        scn.EnsureDSForcePile(3);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertEquals(3, scn.GetDSForcePileCount());
        assertFalse(scn.DSDeployAvailable(scn.GetDSCard("guri")));
    }
}
