package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1261_089_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("contacts", "1261_89"); put("jawa", "1_12"); put("jawa2", "1_12"); put("jawa3", "1_12");
            put("talz", "1_31"); put("talz2", "1_31"); put("het", "2_9"); put("rebel", "1_28"); put("rebel2", "1_28");
            put("luke", "1_19"); put("leia", "1_17"); put("skywalkers", "1_110"); put("remote", "1_136");
            put("audience", "6_81"); put("entrance", "6_82"); put("traffic", "1_66"); put("traffic2", "1_66");
        }}, new HashMap<>() {{
            put("ugnaught", "5_105"); put("ugnaught2", "5_105"); put("jawa", "1_182");
            put("jango", "601_13"); put("trooper", "1_194"); put("vader", "1_168"); put("alter", "1_234");
            put("audience", "6_162"); put("entrance", "6_165"); put("bay", "1_291");
        }}, 60, 60, StartingSetup.LSStartingLocation("1_129"), StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    private VirtualTableScenario started(boolean contacts) {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("remote"));
        if (contacts) scn.MoveCardsToLSSideOfTable(scn.GetLSCard("contacts"));
        return scn;
    }
    @Test public void UnderworldContactsStatsMatchPrintedLegacyCard() {
        var scn = scenario(); var b = scn.GetLSCard("contacts").getBlueprint();
        assertEquals(Title.Underworld_Contacts, b.getTitle()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertTrue(b.hasVirtualSuffix()); assertEquals(4, b.getDestiny(), scn.epsilon);
        assertEquals(0, b.getDeployCost(), scn.epsilon); assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.EFFECT, Icon.PREMIUM, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }
    @Test public void UnderworldContactsActuallyDeploysOnTableForFree() {
        var scn = started(false); scn.MoveCardsToLSHand(scn.GetLSCard("contacts")); scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount(); scn.LSDeployCard(scn.GetLSCard("contacts")); scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("contacts")); assertEquals(force, scn.GetLSForcePileCount());
    }
    private void finishDestinies(VirtualTableScenario scn) {
        // Resolve each actual draw, including up to3 LS and2 DS draws in uncapped controls.
        for (int i = 0; i < 4; i++) { scn.SkipToEndOfPowerSegment(true); scn.PassDestinyDrawResponses(); }
    }
    private VirtualTableScenario lowAbility(boolean contacts, String[] ls, String[] ds) {
        var scn = started(contacts);
        for (String key : ls) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard(key));
        for (String key : ds) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard(key));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(scn.GetLSStartingLocation()); scn.PassAllResponses();
        scn.PrepareLSDestiny(2); scn.PrepareDSDestiny(3); return scn;
    }
    @Test public void UnderworldContactsTwoOwnJawasActuallyDrawOneWithOnlyTwoAbility() {
        var scn = lowAbility(true, new String[]{"jawa", "jawa2"}, new String[]{"trooper"});
        finishDestinies(scn); assertEquals(2, scn.GetLSTotalDestiny());
    }
    @Test public void UnderworldContactsAbsentSameLowAbilityPairCannotDraw() {
        var scn = lowAbility(false, new String[]{"jawa", "jawa2"}, new String[]{"trooper"});
        finishDestinies(scn); assertEquals(0, scn.GetLSTotalDestiny()); assertInZone(Zone.RESERVE_DECK, scn.GetLSDestiny(2));
    }
    @Test public void UnderworldContactsOneAlienDoesNotQualify() {
        var scn = lowAbility(true, new String[]{"jawa"}, new String[]{"trooper"}); finishDestinies(scn); assertEquals(0, scn.GetLSTotalDestiny());
    }
    @Test public void UnderworldContactsDifferentAlienTitlesDoNotQualify() {
        var scn = lowAbility(true, new String[]{"jawa", "talz"}, new String[]{"trooper"}); finishDestinies(scn); assertEquals(0, scn.GetLSTotalDestiny());
    }
    @Test public void UnderworldContactsSameSpeciesWithDifferentTitlesDoesNotQualify() {
        var scn = lowAbility(true, new String[]{"jawa", "het"}, new String[]{"trooper"});
        assertTrue(Filters.Jawa.accepts(scn.game(), scn.GetLSCard("jawa"))); assertTrue(Filters.Jawa.accepts(scn.game(), scn.GetLSCard("het")));
        finishDestinies(scn); assertEquals(0, scn.GetLSTotalDestiny());
    }
    @Test public void UnderworldContactsIdenticalAliensOwnedByDifferentPlayersAreNotPair() {
        var scn = lowAbility(true, new String[]{"jawa"}, new String[]{"jawa"});
        finishDestinies(scn); assertEquals(0, scn.GetLSTotalDestiny()); assertEquals(0, scn.GetDSTotalDestiny());
    }
    @Test public void UnderworldContactsMatchingAliensAtDifferentLocationsAreNotTogether() {
        var scn = started(true); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("jawa"), scn.GetDSCard("trooper"));
        scn.MoveCardsToLocation(scn.GetLSCard("remote"), scn.GetLSCard("jawa2")); scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSStartingLocation()); scn.PassAllResponses(); scn.PrepareLSDestiny(2); finishDestinies(scn); assertEquals(0, scn.GetLSTotalDestiny());
    }
    @Test public void UnderworldContactsThreeSameTitleAliensStillDrawOnlyOne() {
        var scn = lowAbility(true, new String[]{"jawa", "jawa2", "jawa3"}, new String[]{"trooper"}); finishDestinies(scn); assertEquals(2, scn.GetLSTotalDestiny());
    }
    @Test public void UnderworldContactsAlsoWorksForLimitedThreeAliensOtherThanJawas() {
        var scn = lowAbility(true, new String[]{"talz", "talz2"}, new String[]{"trooper"}); finishDestinies(scn); assertEquals(2, scn.GetLSTotalDestiny());
    }
    @Test public void UnderworldContactsOpponentLowAbilityPairActuallyDrawsOne() {
        var scn = lowAbility(true, new String[]{"rebel"}, new String[]{"ugnaught", "ugnaught2"}); finishDestinies(scn); assertEquals(3, scn.GetDSTotalDestiny());
    }
    @Test public void UnderworldContactsAbsentOpponentLowAbilityPairCannotDraw() {
        var scn = lowAbility(false, new String[]{"rebel"}, new String[]{"ugnaught", "ugnaught2"}); finishDestinies(scn); assertEquals(0, scn.GetDSTotalDestiny());
    }
    @Test public void UnderworldContactsTwoNonAlienSameTitleCharactersDoNotQualify() {
        var scn = lowAbility(true, new String[]{"rebel", "rebel2"}, new String[]{"trooper"}); finishDestinies(scn); assertEquals(0, scn.GetLSTotalDestiny());
    }
    @Test public void UnderworldContactsPairsOnBothSidesGiveOneDrawEachWithoutStacking() {
        var scn = lowAbility(true, new String[]{"jawa", "jawa2"}, new String[]{"ugnaught", "ugnaught2"});
        finishDestinies(scn); assertEquals(2, scn.GetLSTotalDestiny()); assertEquals(3, scn.GetDSTotalDestiny());
    }
    private VirtualTableScenario highAbility(boolean contacts, boolean pair, boolean dsPair, boolean remotePair, boolean rebelPair) {
        var scn = started(contacts); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetLSCard("leia"));
        if (dsPair) scn.MoveCardsToLocation(site, scn.GetDSCard("ugnaught"), scn.GetDSCard("ugnaught2"));
        else scn.MoveCardsToLocation(site, scn.GetDSCard("jango"), scn.GetDSCard("trooper"));
        if (pair) scn.MoveCardsToLocation(remotePair ? scn.GetLSCard("remote") : site, scn.GetLSCard("jawa"), scn.GetLSCard("jawa2"));
        if (rebelPair) scn.MoveCardsToLocation(site, scn.GetLSCard("rebel"), scn.GetLSCard("rebel2"));
        scn.MoveCardsToLSHand(scn.GetLSCard("skywalkers")); scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site); scn.PassAllResponses(); scn.LSPlayCard(scn.GetLSCard("skywalkers"), "Add two battle destiny"); scn.PassAllResponses();
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSDestiny(1), scn.GetLSDestiny(2), scn.GetLSDestiny(4));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(2), scn.GetDSDestiny(3)); return scn;
    }
    @Test public void UnderworldContactsOwnPairCapsBothPlayersActualExtraDestiniesToOne() {
        var scn = highAbility(true, true, false, false, false);
        assertEquals(3, scn.GetLSBattleDestinyCount()); assertEquals(2, scn.GetDSBattleDestinyCount());
        assertEquals(1, scn.GetLSBattleDestinyLimit()); assertEquals(1, scn.GetDSBattleDestinyLimit()); finishDestinies(scn);
        assertTrue(Set.of(1, 2, 4).contains(scn.GetLSTotalDestiny())); assertTrue(Set.of(2, 3).contains(scn.GetDSTotalDestiny()));
    }
    @Test public void UnderworldContactsAbsentSamePartiesDrawAllThreeAndTwo() {
        var scn = highAbility(false, true, false, false, false); finishDestinies(scn); assertEquals(7, scn.GetLSTotalDestiny()); assertEquals(5, scn.GetDSTotalDestiny());
    }
    @Test public void UnderworldContactsOpponentsPairAlsoCapsOwnThreeDestinies() {
        var scn = highAbility(true, false, true, false, false); assertEquals(1, scn.GetLSBattleDestinyLimit());
        finishDestinies(scn); assertTrue(Set.of(1, 2, 4).contains(scn.GetLSTotalDestiny())); assertTrue(Set.of(2, 3).contains(scn.GetDSTotalDestiny()));
    }
    @Test public void UnderworldContactsRemotePairDoesNotCapBattleHere() {
        var scn = highAbility(true, true, false, true, false); finishDestinies(scn); assertEquals(7, scn.GetLSTotalDestiny()); assertEquals(5, scn.GetDSTotalDestiny());
    }
    @Test public void UnderworldContactsSameTitleRebelPairDoesNotCapBattle() {
        var scn = highAbility(true, false, false, false, true); finishDestinies(scn); assertEquals(7, scn.GetLSTotalDestiny()); assertEquals(5, scn.GetDSTotalDestiny());
    }
    @Test public void UnderworldContactsPairRemovalImmediatelyRemovesBothLimits() {
        var scn = highAbility(true, true, false, false, false); assertEquals(1, scn.GetLSBattleDestinyLimit());
        scn.MoveCardsToLSHand(scn.GetLSCard("jawa2")); assertEquals(3, scn.GetLSBattleDestinyLimit()); assertEquals(2, scn.GetDSBattleDestinyLimit());
        finishDestinies(scn); assertEquals(7, scn.GetLSTotalDestiny()); assertEquals(5, scn.GetDSTotalDestiny());
    }
    @Test public void UnderworldContactsLeavingTableImmediatelyRemovesBothLimits() {
        var scn = highAbility(true, true, false, false, false); scn.MoveCardsToLSHand(scn.GetLSCard("contacts"));
        finishDestinies(scn); assertEquals(7, scn.GetLSTotalDestiny()); assertEquals(5, scn.GetDSTotalDestiny());
    }
    private VirtualTableScenario converted(boolean both, boolean opponentTurn) {
        var scn = started(true);
        scn.MoveLocationToTable(scn.GetLSCard("audience")); scn.MoveLocationToTable(scn.GetDSCard("audience"));
        assertInZone(Zone.CONVERTED_LOCATIONS, scn.GetLSCard("audience"));
        if (both) {
            scn.MoveLocationToTable(scn.GetLSCard("entrance")); scn.MoveLocationToTable(scn.GetDSCard("entrance"));
            assertInZone(Zone.CONVERTED_LOCATIONS, scn.GetLSCard("entrance"));
        }
        if (opponentTurn) { scn.SkipToDSTurn(Phase.CONTROL); scn.DSPass(); } else scn.SkipToLSTurn(Phase.CONTROL);
        while (scn.GetLSForcePileCount() > 0) scn.MoveCardsToTopOfLSReserveDeck(scn.GetTopOfLSForcePile()); return scn;
    }
    private void raise(VirtualTableScenario scn, String key) {
        scn.LSUseCardAction(scn.GetLSCard("contacts"), "Raise a converted");
        if (scn.LSHasCardChoiceAvailable(scn.GetDSCard(key))) scn.LSChooseCard(scn.GetDSCard(key));
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard(key))) scn.LSChooseCard(scn.GetLSCard(key));
        scn.PassAllResponses();
        assertInZone(Zone.LOCATIONS, scn.GetLSCard(key)); assertInZone(Zone.CONVERTED_LOCATIONS, scn.GetDSCard(key));
        assertEquals(0, scn.GetLSForcePileCount());
    }
    @Test public void UnderworldContactsActuallyRaisesOwnConvertedJabbaSiteForFree() { raise(converted(false, false), "audience"); }
    @Test public void UnderworldContactsCanRaiseDuringOpponentTurn() { raise(converted(false, true), "audience"); }
    @Test public void UnderworldContactsCannotRaiseSecondSiteInSameTurn() {
        var scn = converted(true, false); raise(scn, "audience"); scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("contacts"), "Raise")); assertInZone(Zone.CONVERTED_LOCATIONS, scn.GetLSCard("entrance"));
    }
    @Test public void UnderworldContactsRaiseResetsDuringNextOpponentTurn() {
        var scn = converted(true, false); raise(scn, "audience"); scn.SkipToDSTurn(Phase.CONTROL); scn.DSPass(); raise(scn, "entrance");
    }
    @Test public void UnderworldContactsCannotRaiseUnconvertedSite() {
        var scn = started(true); scn.MoveLocationToTable(scn.GetLSCard("audience")); scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("contacts"), "Raise"));
    }
    @Test public void UnderworldContactsCannotRaiseOpponentsConvertedSite() {
        var scn = started(true); scn.MoveLocationToTable(scn.GetDSCard("audience")); scn.MoveLocationToTable(scn.GetLSCard("audience"));
        scn.SkipToLSTurn(Phase.CONTROL); assertInZone(Zone.CONVERTED_LOCATIONS, scn.GetDSCard("audience"));
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("contacts"), "Raise"));
    }
    @Test public void UnderworldContactsCannotRaiseOtherConvertedTatooineSite() {
        var scn = started(true); scn.MoveLocationToTable(scn.GetDSCard("bay")); scn.SkipToLSTurn(Phase.CONTROL);
        assertInZone(Zone.CONVERTED_LOCATIONS, scn.GetLSStartingLocation()); assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("contacts"), "Raise"));
    }
    @Test public void UnderworldContactsImmuneToActualAlterWhileOrdinaryEffectIsCanceled() {
        var scn = started(true); scn.MoveCardsToLSSideOfTable(scn.GetLSCard("traffic"), scn.GetLSCard("traffic2"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader")); scn.MoveCardsToDSHand(scn.GetDSCard("alter"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.PrepareDSDestiny(0); scn.DSPlayCard(scn.GetDSCard("alter"));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("contacts"))); assertTrue(scn.DSHasCardChoiceAvailable(scn.GetLSCard("traffic")));
        scn.DSChooseCard(scn.GetLSCard("traffic")); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("traffic")); assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("contacts"));
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("traffic2")); assertInZone(Zone.USED_PILE, scn.GetDSCard("alter"));
    }
}
