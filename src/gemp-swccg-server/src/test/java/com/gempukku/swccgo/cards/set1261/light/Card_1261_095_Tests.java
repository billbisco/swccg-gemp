package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1261_095_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("scum", "1261_95"); put("scum2", "1261_95"); put("lando", "5_5"); put("dash", "10_6");
            put("luke", "1_19"); put("leia", "1_17"); put("falcon", "1_143"); put("landoFalcon", "109_2");
            put("red1", "1_144"); put("eject", "2_32"); put("eject2", "2_32"); put("skywalkers", "1_110");
            put("site", "1_129"); put("remote", "1_135");
        }}, new HashMap<>() {{
            put("devastator", "1_301"); put("vader", "1_168"); put("tie", "1_304");
            put("system", "1220_61"); put("lando", "5_99"); put("gravel", "1_247");
            put("mos", "1_295");
        }}, 60, 60, StartingSetup.LSStartingLocation("2_65"), StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    private VirtualTableScenario started() {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("site"));
        scn.MoveLocationToTable(scn.GetLSCard("remote")); scn.MoveCardsToLSHand(scn.GetLSCard("scum"), scn.GetLSCard("scum2"));
        return scn;
    }
    @Test public void ScumStatsMatchFinalMasterRatherThanEarlierFiveDestinyImage() {
        var scn = scenario(); var b = scn.GetLSCard("scum").getBlueprint();
        assertEquals(Title.I_Dont_Need_Their_Scum_Either, b.getTitle()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(6, b.getDestiny(), scn.epsilon); assertEquals(CardSubtype.USED_OR_LOST, b.getCardSubtype());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertTrue(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.INTERRUPT, Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }
    private VirtualTableScenario spaceBattle(String ship, String pilot, boolean extraAbility) {
        var scn = spaceSetup(ship, pilot, extraAbility); startSpaceBattle(scn); return scn;
    }
    private VirtualTableScenario spaceSetup(String ship, String pilot, boolean extraAbility) {
        var scn = started(); var craft = scn.GetLSCard(ship);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), craft, scn.GetDSCard("devastator"));
        if (pilot != null) { scn.MoveCardsToLSHand(scn.GetLSCard(pilot)); scn.BoardAsPilot(craft, scn.GetLSCard(pilot)); }
        if (extraAbility) { scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("red1")); scn.BoardAsPilot(scn.GetLSCard("red1"), scn.GetLSCard("luke")); }
        return scn;
    }
    private void startSpaceBattle(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(scn.GetLSStartingLocation()); scn.PassAllResponses();
    }
    private void playDestiny(VirtualTableScenario scn, String ship) {
        scn.LSPlayCard(scn.GetLSCard("scum"), "Falcon draws two");
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard(ship))) scn.LSChooseCard(scn.GetLSCard(ship));
        scn.PassAllResponses(); assertInZone(Zone.USED_PILE, scn.GetLSCard("scum"));
    }
    private void draw(VirtualTableScenario scn) {
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSDestiny(4), scn.GetLSDestiny(3), scn.GetLSDestiny(2)); scn.PrepareDSDestiny(0);
        for (int i = 0; i < 4; i++) {
            scn.SkipToEndOfPowerSegment(true);
            if (scn.LSDecisionAvailable("Do you want to draw") && scn.LSDecisionAvailable("battle destiny")) scn.LSChooseYes();
            if (scn.DSDecisionAvailable("Do you want to draw") && scn.DSDecisionAvailable("battle destiny")) scn.DSChooseYes();
            scn.PassDestinyDrawResponses();
        }
    }
    @Test public void ScumRaisesZeroNaturalDestiniesToTwoForLandoPilotingFalcon() {
        var scn = spaceBattle("falcon", "lando", false); playDestiny(scn, "falcon"); draw(scn); assertEquals(5, scn.GetLSTotalDestiny());
    }
    @Test public void ScumRaisesOneNaturalDestinyToTwoRatherThanAddingTwo() {
        var scn = spaceBattle("falcon", "lando", true); playDestiny(scn, "falcon"); draw(scn); assertEquals(5, scn.GetLSTotalDestiny());
    }
    @Test public void ScumRecognizesPermanentLandoPilot() {
        var scn = spaceBattle("landoFalcon", null, false); playDestiny(scn, "landoFalcon"); draw(scn); assertEquals(5, scn.GetLSTotalDestiny());
    }
    @Test public void ScumDoesNotAddTwoToThreeAlreadyAvailableDestinies() {
        var scn = spaceSetup("falcon", "lando", true);
        scn.BoardAsPassenger(scn.GetLSCard("falcon"), scn.GetLSCard("leia")); scn.MoveCardsToLSHand(scn.GetLSCard("skywalkers"));
        startSpaceBattle(scn); scn.LSPlayCard(scn.GetLSCard("skywalkers"));
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke"))) scn.LSChooseCard(scn.GetLSCard("luke"));
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("leia"))) scn.LSChooseCard(scn.GetLSCard("leia"));
        scn.PassAllResponses(); scn.DSPass(); playDestiny(scn, "falcon"); draw(scn); assertEquals(9, scn.GetLSTotalDestiny());
    }
    @Test public void ScumUniqueSecondCopyUnavailableAndUsedDispositionDoesNotRemoveDuration() {
        var scn = spaceBattle("falcon", "lando", false); playDestiny(scn, "falcon"); scn.DSPass();
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("scum2"))); assertInHand(scn.GetLSCard("scum2"));
        draw(scn); assertEquals(5, scn.GetLSTotalDestiny());
    }
    @Test public void ScumSnapshotsPilotingConditionAtPlayRatherThanContinuously() {
        var scn = spaceSetup("falcon", "lando", false); scn.MoveCardsToLSHand(scn.GetLSCard("luke"));
        scn.BoardAsPilot(scn.GetLSCard("falcon"), scn.GetLSCard("luke")); startSpaceBattle(scn);
        playDestiny(scn, "falcon"); scn.MoveCardsToLSHand(scn.GetLSCard("lando")); draw(scn); assertEquals(5, scn.GetLSTotalDestiny());
    }
    @Test public void ScumAbsentLandoFalconWithThreeAbilityCannotDraw() {
        var scn = spaceBattle("falcon", "lando", false); draw(scn); assertEquals(0, scn.GetLSTotalDestiny());
    }
    @Test public void ScumCannotUseFalconModeWithLukePiloting() {
        var scn = spaceBattle("falcon", "luke", false); assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("scum")));
    }
    @Test public void ScumCannotUseFalconModeWithLandoPilotingAnotherShip() {
        var scn = spaceBattle("red1", "lando", false); assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("scum")));
    }
    @Test public void ScumCannotUseFalconModeOutsideBattle() {
        var scn = started(); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("falcon"));
        scn.BoardAsPilot(scn.GetLSCard("falcon"), scn.GetLSCard("lando")); scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("scum")));
    }
    @Test public void ScumCannotUseRemoteLandoFalconToModifyThisBattle() {
        var scn = spaceSetup("red1", "luke", false);
        scn.MoveCardsToLocation(scn.GetLSCard("remote"), scn.GetLSCard("landoFalcon"));
        startSpaceBattle(scn); assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("scum")));
    }
    @Test public void ScumLandoPassengerIsNotPilot() {
        var scn = spaceSetup("falcon", "luke", false); scn.BoardAsPassenger(scn.GetLSCard("falcon"), scn.GetLSCard("lando"));
        startSpaceBattle(scn); assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("scum")));
    }
    @Test public void ScumActuallyCancelsLegacyLandoSystemAsUsed() {
        var scn = started(); scn.MoveCardsToDSHand(scn.GetDSCard("system")); scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("lando"));
        scn.DSPlayCard(scn.GetDSCard("system")); scn.DSPass();
        assertTrue(scn.LSPlayUsedInterruptAvailable(scn.GetLSCard("scum"))); scn.LSPlayUsedInterrupt(scn.GetLSCard("scum")); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("system")); assertInZone(Zone.USED_PILE, scn.GetLSCard("scum"));
        assertInZone(Zone.RESERVE_DECK, scn.GetDSCard("lando"));
    }
    private VirtualTableScenario lose(String key, boolean opponent) {
        var scn = started(); var victim = opponent ? scn.GetDSCard(key) : scn.GetLSCard(key);
        scn.MoveLocationToTable(scn.GetDSCard("mos")); scn.MoveCardsToLocation(scn.GetDSCard("mos"), victim);
        if (opponent) {
            scn.SkipToLSTurn(Phase.CONTROL);
            scn.LSExecuteAdHocEffect(scn.GetLSCard("scum"), new LoseCardFromTableEffect(new TopLevelGameTextAction(scn.GetLSCard("scum"), scn.GetLSCard("scum").getCardId()), victim));
        } else {
            scn.MoveCardsToDSHand(scn.GetDSCard("gravel")); scn.SkipToDSTurn(Phase.CONTROL); scn.PrepareDSDestiny(5);
            scn.DSPlayCard(scn.GetDSCard("gravel")); scn.DSChooseCard(victim);
            scn.PassCardAndForceUseResponses(); scn.PassDestinyDrawResponses();
        }
        scn.PassResponses("ABOUT_TO_BE_LOST"); return scn;
    }
    private void assertNoRetrieval(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCard victim) {
        assertEquals(Zone.LOST_PILE, GameUtils.getZoneFromZoneTop(victim.getZone()));
        // If no response is legal the engine advances without an LS action decision.
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains("Retrieve") && text.contains("into hand")));
    }
    @Test public void ScumRetrievesActuallyJustLostGamblerIntoHandAsLost() {
        var scn = lose("lando", false); assertTrue(scn.LSPlayLostInterruptAvailable(scn.GetLSCard("scum")));
        scn.LSPlayLostInterrupt(scn.GetLSCard("scum")); scn.PassAllResponses();
        assertInHand(scn.GetLSCard("lando")); assertInZone(Zone.LOST_PILE, scn.GetLSCard("scum"));
    }
    @Test public void ScumCannotRetrieveDashEvenThoughHeIsGambler() {
        var scn = lose("dash", false); assertTrue(Filters.gambler.accepts(scn.game(), scn.GetLSCard("dash")));
        assertNoRetrieval(scn, scn.GetLSCard("dash"));
    }
    @Test public void ScumCannotRetrieveNonGambler() {
        var scn = lose("luke", false); assertNoRetrieval(scn, scn.GetLSCard("luke"));
    }
    @Test public void ScumCannotRetrieveOpponentsJustLostGambler() {
        var scn = lose("lando", true); assertNoRetrieval(scn, scn.GetDSCard("lando"));
    }
    @Test public void ScumCannotRetrieveOldLostGamblerOutsideJustLostWindow() {
        var scn = started(); scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("lando")); scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSPlayLostInterruptAvailable(scn.GetLSCard("scum")));
    }
    @Test public void ScumCannotRetrieveGamblerAfterResponseWindowCloses() {
        var scn = lose("lando", false); scn.PassAllResponses(); scn.SkipToPhase(Phase.DEPLOY);
        assertNoRetrieval(scn, scn.GetLSCard("lando"));
    }
    @Test public void ScumActuallyRemovesDeployedEjectAndRestoresPermanentPilot() {
        var scn = started(); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("tie"));
        scn.MoveCardsToLSHand(scn.GetLSCard("eject")); scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(2);
        assertEquals(1, scn.GetBattleDestinyAbility(scn.GetDSCard("tie"))); scn.PrepareLSDestiny(3);
        scn.LSDeployCard(scn.GetLSCard("eject")); scn.LSChooseCard(scn.GetDSCard("tie")); scn.PassAllResponses(); scn.PassDestinyDrawResponses(); scn.PassAllResponses();
        assertEquals(scn.GetDSCard("tie"), scn.GetLSCard("eject").getAttachedTo()); assertEquals(0, scn.GetBattleDestinyAbility(scn.GetDSCard("tie")));
        scn.DSPass(); scn.LSPlayCard(scn.GetLSCard("scum"), "Place Eject!");
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("eject"))) scn.LSChooseCard(scn.GetLSCard("eject")); scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, scn.GetLSCard("eject")); assertInZone(Zone.LOST_PILE, scn.GetLSCard("scum"));
        assertEquals(1, scn.GetBattleDestinyAbility(scn.GetDSCard("tie")));
    }
    @Test public void ScumCannotMoveEjectFromHandOrReserveDeck() {
        var scn = started(); scn.MoveCardsToLSHand(scn.GetLSCard("eject")); scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSPlayLostInterruptAvailable(scn.GetLSCard("scum"))); assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("eject2"));
    }
}
