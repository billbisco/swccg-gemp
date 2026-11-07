package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1261_068_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("lando", "1261_68"); put("lando2", "1261_68"); put("rebel", "1_28"); put("alien", "1_4");
            put("jedi", "9_24"); put("saber", "9_90"); put("droid", "2_14"); put("falcon", "1_143"); put("xwing", "1_144");
            put("effect", "5_20"); put("device", "5_14"); put("wrongLore", "1_66");
            put("remote", "1_136"); put("discard1", "1_28"); put("discard2", "1_28"); put("discard3", "1_28");
        }}, new HashMap<>() {{ put("enemy", "5_99"); put("enemy2", "5_99"); put("vader", "1_168");
            put("walker", "3_155"); put("trooper", "1_194"); }}, 60, 60,
                StartingSetup.LSStartingLocation("1_129"), StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    private VirtualTableScenario started() {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("remote"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("lando")); return scn;
    }
    @Test public void LandoScoundrelStatsMatchLegacyHostAndMaster() {
        var scn = scenario(); var b = scn.GetLSCard("lando").getBlueprint();
        assertEquals("Lando Calrissian, Scoundrel", b.getTitle()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertTrue(b.hasVirtualSuffix());
        assertEquals(1, b.getDestiny(), scn.epsilon); assertEquals(4, b.getDeployCost(), scn.epsilon);
        assertEquals(4, b.getPower(), scn.epsilon); assertEquals(3, b.getAbility(), scn.epsilon); assertEquals(7, b.getForfeit(), scn.epsilon);
        assertEquals(CardCategory.CHARACTER, b.getCardCategory()); assertNull(b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.REBEL)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.REBEL, Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(Arrays.asList(Keyword.SCOUNDREL)));
        scn.BlueprintPersonaCheck(b, new ArrayList<>(Arrays.asList(Persona.LANDO)));
    }
    @Test public void LandoScoundrelActuallyDeploysForFourForce() {
        var scn = started(); scn.MoveCardsToLSHand(scn.GetLSCard("lando")); scn.SkipToLSTurn(Phase.CONTROL);
        while (scn.GetLSForcePileCount() > 0) scn.MoveCardsToTopOfLSReserveDeck(scn.GetTopOfLSForcePile());
        scn.LSActivateForceCheat(4); scn.SkipToPhase(Phase.DEPLOY);
        scn.LSDeployCard(scn.GetLSCard("lando")); scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses();
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("lando")); assertEquals(0, scn.GetLSForcePileCount());
    }
    private void pilot(String key) {
        var scn = started(); var ship = scn.GetLSCard(key); var lando = scn.GetLSCard("lando");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ship); scn.BoardAsPilot(ship, lando); assertEquals(5, scn.GetPower(ship));
        scn.MoveCardsToLSHand(lando); assertEquals(0, scn.GetPower(ship));
    }
    @Test public void LandoScoundrelAddsTwoPowerToFalconHePilots() { pilot("falcon"); }
    @Test public void LandoScoundrelAddsTwoPowerToOtherShipHePilots() { pilot("xwing"); }
    private VirtualTableScenario convertSetup(boolean remote, boolean enclosed) {
        var scn = started(); var site = remote ? scn.GetLSCard("remote") : scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("enemy"));
        if (enclosed) { scn.MoveCardsToLocation(site, scn.GetDSCard("walker")); scn.BoardAsPassenger(scn.GetDSCard("walker"), scn.GetDSCard("enemy")); }
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("discard1"), scn.GetLSCard("discard2"), scn.GetLSCard("discard3"));
        scn.SkipToLSTurn(Phase.CONTROL); return scn;
    }
    private void convert(VirtualTableScenario scn, boolean retrieve) {
        scn.LSUseCardAction(scn.GetLSCard("lando"), "Convert opponent's Lando");
        if (scn.LSHasCardChoiceAvailable(scn.GetDSCard("enemy"))) scn.LSChooseCard(scn.GetDSCard("enemy"));
        scn.PassAllResponses(); assertEquals(scn.LS, scn.GetDSCard("enemy").getOwner());
        assertTrue(scn.LSDecisionAvailable("retrieve 2 Force"));
        if (retrieve) scn.LSChooseYes(); else scn.LSChooseNo();
        scn.PassAllResponses();
    }
    @Test public void LandoScoundrelActuallyConvertsLandoAndOptionallyRetrievesTwo() {
        var scn = convertSetup(false, false); int used = scn.GetLSUsedPileCount(); convert(scn, true);
        assertEquals(1, scn.GetLSLostPileCount()); assertEquals(used + 2, scn.GetLSUsedPileCount());
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetDSCard("enemy"));
    }
    @Test public void LandoScoundrelMayDeclineRetrievalAfterConversion() {
        var scn = convertSetup(false, false); int used = scn.GetLSUsedPileCount(); convert(scn, false);
        assertEquals(3, scn.GetLSLostPileCount()); assertEquals(used, scn.GetLSUsedPileCount());
    }
    @Test public void LandoScoundrelCanConvertOpponentLandoAtRemoteSite() {
        // Printed "present at a site" is not "present with" the source; reuse correct OVS conversion scope.
        var scn = convertSetup(true, false); convert(scn, false); assertAtLocation(scn.GetLSCard("remote"), scn.GetDSCard("enemy"));
    }
    @Test public void LandoScoundrelCannotConvertLandoEnclosedAtSite() {
        var scn = convertSetup(false, true); assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("lando"), "Convert"));
        assertEquals(scn.DS, scn.GetDSCard("enemy").getOwner());
    }
    @Test public void LandoScoundrelCannotConvertDifferentPersona() {
        var scn = started(); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader")); scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("lando"), "Convert"));
    }
    @Test public void LandoScoundrelConversionDoesNotResetNextTurnOrOnSourceReentry() {
        var scn = convertSetup(false, false); convert(scn, false);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("enemy2"));
        scn.MoveCardsToLSHand(scn.GetLSCard("lando")); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("lando"));
        scn.SkipToLSTurn(Phase.CONTROL); assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("lando"), "Convert"));
        assertEquals(scn.DS, scn.GetDSCard("enemy2").getOwner());
    }
    private VirtualTableScenario battle(String company) {
        var scn = started();
        if (company != null) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard(company));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(scn.GetLSStartingLocation()); scn.PassAllResponses(); return scn;
    }
    @Test public void LandoScoundrelAddsActualSecondBattleDestinyWithOwnRebel() {
        var scn = battle("rebel"); scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSDestiny(2), scn.GetLSDestiny(3)); scn.PrepareDSDestiny(0); scn.SkipToEndOfPowerSegment(true);
        assertEquals(5, scn.GetLSTotalDestiny());
    }
    @Test public void LandoScoundrelAddsActualSecondBattleDestinyWithOwnAlien() {
        var scn = battle("alien"); scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSDestiny(2), scn.GetLSDestiny(3)); scn.PrepareDSDestiny(0); scn.SkipToEndOfPowerSegment(true);
        assertEquals(5, scn.GetLSTotalDestiny());
    }
    @Test public void LandoScoundrelAloneDoesNotCountHimselfAsRebelCompany() {
        var scn = battle(null); assertEquals(0, scn.GetLSBattleDestinyCount());
    }
    @Test public void LandoScoundrelDroidDoesNotSatisfyAlienOrRebelCompany() {
        var scn = battle("droid"); assertEquals(0, scn.GetLSBattleDestinyCount());
    }
    @Test public void LandoScoundrelOpponentAlienDoesNotSatisfyOwnCompany() {
        var scn = started(); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("enemy"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(scn.GetLSStartingLocation()); scn.PassAllResponses();
        assertEquals(0, scn.GetLSBattleDestinyCount());
    }
    @Test public void LandoScoundrelRemoteOwnRebelDoesNotSatisfyCompany() {
        var scn = started(); scn.MoveCardsToLocation(scn.GetLSCard("remote"), scn.GetLSCard("rebel"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(scn.GetLSStartingLocation()); scn.PassAllResponses(); assertEquals(0, scn.GetLSBattleDestinyCount());
    }
    private float immunity(VirtualTableScenario scn) { return scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), scn.GetLSCard("lando")); }
    @Test public void LandoScoundrelMatchingEffectIncreasesImmunityAndRemovalRestoresThree() {
        var scn = started(); assertEquals(3, immunity(scn), scn.epsilon);
        scn.MoveCardsToDSSideOfTable(scn.GetLSCard("effect")); assertEquals(5, immunity(scn), scn.epsilon);
        scn.MoveCardsToLSHand(scn.GetLSCard("effect")); assertEquals(3, immunity(scn), scn.epsilon);
    }
    @Test public void LandoScoundrelLoreDeviceIsNotMatchingEffect() {
        var scn = started(); scn.AttachCardsTo(scn.GetLSCard("lando"), scn.GetLSCard("device")); assertEquals(3, immunity(scn), scn.epsilon);
    }
    @Test public void LandoScoundrelEffectWithoutLandoInLoreDoesNotImproveImmunity() {
        var scn = started(); scn.MoveCardsToLSSideOfTable(scn.GetLSCard("wrongLore")); assertEquals(3, immunity(scn), scn.epsilon);
    }
    private void attrition(boolean effect, int amount, boolean immune) {
        var scn = started(); var lando = scn.GetLSCard("lando");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("jedi"), scn.GetDSCard("vader"));
        scn.AttachCardsTo(scn.GetLSCard("jedi"), scn.GetLSCard("saber"));
        scn.SkipToLSTurn(Phase.BATTLE);
        if (effect) scn.MoveCardsToDSSideOfTable(scn.GetLSCard("effect"));
        scn.LSInitiateBattle(scn.GetLSStartingLocation()); scn.PassAllResponses(); scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSDestiny(0), scn.GetLSDestiny(1)); scn.PrepareDSDestiny(amount - 1);
        scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions(); assertEquals(amount, scn.GetUnpaidLSAttrition());
        assertEquals(0, scn.GetUnpaidLSBattleDamage());
        // Armed Jedi Knight is immune below6; Lando alone decides whether this attrition payment is mandatory.
        assertEquals(immune, scn.LSDecisionAvailable("(if desired)"));
        if (immune) {
            scn.LSChooseCards();
            assertAtLocation(scn.GetLSStartingLocation(), lando);
        } else {
            scn.LSPayAttritionFromCardInPlay(lando); scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, lando);
        }
    }
    @Test public void LandoScoundrelSurvivesActualAttritionTwoWithoutEffect() { attrition(false, 2, true); }
    @Test public void LandoScoundrelMayBeForfeitedAtActualAttritionThreeWithoutEffect() { attrition(false, 3, false); }
    @Test public void LandoScoundrelSurvivesActualAttritionFourWithMatchingEffect() { attrition(true, 4, true); }
    @Test public void LandoScoundrelMustStillSatisfyActualAttritionFiveWithMatchingEffect() { attrition(true, 5, false); }
}
