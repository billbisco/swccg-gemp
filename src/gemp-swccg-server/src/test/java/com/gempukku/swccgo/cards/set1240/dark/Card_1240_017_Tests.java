package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_017_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of(
                "luke", "9_24", "han", "108_1", "obi", "108_4", "system", "1_126", "elsewhere", "1_133",
                "indoor", "1_128")),
                new HashMap<>(Map.of("ysalamir", "1240_17", "trooper", "1_194", "garindan", "1_177",
                        "vader", "108_6", "localRat", "7_215", "otherRat", "7_215", "blizzard", "3_154")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.DeclineLSStartingInterrupt, StartingSetup.DeclineDSStartingInterrupt,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ThrawnsYsalamirHasPrintedNameStatsAndParasiteCharacteristic() {
        var scn = scenario();
        var card = scn.GetDSCard("ysalamir").getBlueprint();
        assertEquals("Thrawn's Ysalamir", card.getTitle());
        assertTrue(card.getCardTypes().contains(CardType.CREATURE));
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getFerocity(), scn.epsilon);
        assertEquals(4, card.getSpecialDefenseValue(), scn.epsilon);
        assertEquals(0, card.getForfeit(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.CREATURE, Icon.DAGOBAH, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>(List.of(Keyword.PARASITE)));
    }

    @Test
    public void ThrawnsYsalamirDeploysForTwoToSitesAndNotASystem() {
        var scn = scenario();
        scn.StartGame();
        var ysalamir = scn.GetDSCard("ysalamir");
        var indoor = scn.GetLSCard("indoor");
        var system = scn.GetLSCard("system");
        scn.MoveLocationToTable(indoor);
        scn.MoveLocationToTable(system);
        scn.MoveCardsToDSHand(ysalamir);
        scn.EnsureDSForcePile(2);
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(ysalamir);
        assertFalse(scn.DSHasCardChoiceAvailable(system));
        assertTrue(scn.DSHasCardChoiceAvailable(indoor));
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSStartingLocation()));
        scn.DSChooseCard(indoor);
        passOptional(scn);
        assertTrue(scn.CardsAtLocation(indoor, ysalamir));
        assertEquals(force - 2, scn.GetDSForcePileCount());
    }

    @Test
    public void ThrawnsYsalamirCannotAttachToNonImperialCharacters() {
        var scn = scenario();
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("garindan"), scn.GetLSCard("luke"));
        deployCreature(scn, scn.GetDSCard("ysalamir"), site);
        scn.SkipToPhase(Phase.BATTLE);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("ysalamir"), "Initiate attack"));
    }

    @Test
    public void NativeParasiteAttachmentCancelsBothPlayersGenerationAndReducesOnlyLocalJediDefense() {
        var scn = scenario();
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        var other = scn.GetLSCard("elsewhere");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var han = scn.GetLSCard("han");
        var obi = scn.GetLSCard("obi");
        scn.MoveLocationToTable(other);
        scn.MoveCardsToLocation(site, luke, han);
        scn.MoveCardsToLocation(other, vader, obi);
        var query = scn.game().getModifiersQuerying();
        var state = scn.gameState();
        assertEquals(1, query.getForceGenerationFromLocation(state, site, scn.LS), scn.epsilon);
        assertEquals(1, query.getForceGenerationFromLocation(state, site, scn.DS), scn.epsilon);
        float otherLight = query.getForceGenerationFromLocation(state, other, scn.LS);
        float otherDark = query.getForceGenerationFromLocation(state, other, scn.DS);
        float lukeBefore = query.getDefenseValue(state, luke);
        float hanBefore = query.getDefenseValue(state, han);
        float vaderBefore = query.getDefenseValue(state, vader);
        float obiBefore = query.getDefenseValue(state, obi);
        var ysalamir = scn.GetDSCard("ysalamir");
        var host = scn.GetDSCard("trooper");
        scn.MoveCardsToLocation(site, host);
        deployCreature(scn, ysalamir, site);
        assertEquals(lukeBefore, query.getDefenseValue(state, luke), scn.epsilon);
        assertEquals(1, query.getForceGenerationFromLocation(state, site, scn.LS), scn.epsilon);
        assertEquals(1, query.getForceGenerationFromLocation(state, site, scn.DS), scn.epsilon);
        attachParasite(scn, ysalamir, host);

        assertEquals(0, query.getForceGenerationFromLocation(state, site, scn.LS), scn.epsilon);
        assertEquals(0, query.getForceGenerationFromLocation(state, site, scn.DS), scn.epsilon);
        assertEquals(otherLight, query.getForceGenerationFromLocation(state, other, scn.LS), scn.epsilon);
        assertEquals(otherDark, query.getForceGenerationFromLocation(state, other, scn.DS), scn.epsilon);
        assertEquals(lukeBefore - 1, query.getDefenseValue(state, luke), scn.epsilon);
        assertEquals(hanBefore, query.getDefenseValue(state, han), scn.epsilon);
        assertEquals(obiBefore, query.getDefenseValue(state, obi), scn.epsilon);
        assertEquals(vaderBefore, query.getDefenseValue(state, vader), scn.epsilon);
        scn.MoveCardsToLocation(site, vader);
        assertEquals(vaderBefore - 1, query.getDefenseValue(state, vader), scn.epsilon);
    }

    @Test
    public void AttachedYsalamirStopsCreatureAttacksForBothSidesAtItsSiteButAllowsBattle() {
        var scn = scenario();
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        var other = scn.GetLSCard("elsewhere");
        var localRat = scn.GetDSCard("localRat");
        var otherRat = scn.GetDSCard("otherRat");
        scn.MoveLocationToTable(other);
        attachAtStartingSite(scn);
        scn.MoveCardsToLocation(site, localRat, scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(other, otherRat, scn.GetLSCard("han"));
        scn.EnsureLSForcePile(1);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertFalse(scn.LSCardActionAvailable(site, "Initiate attack on creature"));
        assertTrue(scn.LSCardActionAvailable(other, "Initiate attack on creature"));
        scn.LSInitiateBattle(site);
        passOptional(scn);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());

        // Independently examine the Dark creature's genuine action window.
        scn = scenario();
        scn.StartGame();
        site = scn.GetDSStartingLocation();
        other = scn.GetLSCard("elsewhere");
        localRat = scn.GetDSCard("localRat");
        otherRat = scn.GetDSCard("otherRat");
        scn.MoveLocationToTable(other);
        attachAtStartingSite(scn);
        scn.MoveCardsToLocation(site, localRat, scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(other, otherRat, scn.GetLSCard("han"));
        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.BATTLE);
        assertFalse(scn.DSCardActionAvailable(localRat, "Initiate attack"));
        assertTrue(scn.DSCardActionAvailable(otherRat, "Initiate attack"));
    }

    @Test
    public void AttachedYsalamirStopsAffectingTheSiteWhenItsHostBoardsAnEnclosedVehicle() {
        var scn = scenario();
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        var host = scn.GetDSCard("trooper");
        var ysalamir = scn.GetDSCard("ysalamir");
        var vehicle = scn.GetDSCard("blizzard");
        attachAtStartingSite(scn);
        var query = scn.game().getModifiersQuerying();
        assertEquals(0, query.getForceGenerationFromLocation(scn.gameState(), site, scn.DS), scn.epsilon);
        scn.MoveCardsToLocation(site, vehicle);
        scn.SkipToPhase(Phase.MOVE);
        scn.DSUseCardAction(host, "Embark");
        scn.DSChooseCard(vehicle);
        passOptional(scn);
        assertTrue(scn.IsAttachedTo(vehicle, host));
        assertTrue(scn.IsAttachedTo(host, ysalamir));
        assertEquals(1, query.getForceGenerationFromLocation(scn.gameState(), site, scn.LS), scn.epsilon);
        assertEquals(1, query.getForceGenerationFromLocation(scn.gameState(), site, scn.DS), scn.epsilon);
    }

    @Test
    public void AttachedYsalamirReducesEachNativeLightJediWeaponDestiny() {
        fireOriginalLightsaber(true, true, true);
        fireOriginalLightsaber(true, false, false);
        fireOriginalLightsaber(true, true, false);
    }

    @Test
    public void AttachedYsalamirReducesEachNativeDarkJediWeaponDestiny() {
        fireOriginalLightsaber(false, true, true);
        fireOriginalLightsaber(false, false, false);
    }

    @Test
    public void AttachedYsalamirDoesNotReduceNativeNonJediWeaponDestiny() {
        var scn = scenario();
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        var han = scn.GetLSCard("han");
        var victim = scn.GetDSCard("garindan");
        attachAtStartingSite(scn);
        scn.MoveCardsToLocation(site, han, victim);
        scn.EnsureLSForcePile(1);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(1);
        scn.LSUseCardAction(han, "Fire");
        scn.LSChooseCard(victim);
        passOptional(scn);
        assertTrue(victim.isHit());
    }

    private void fireOriginalLightsaber(boolean light, boolean attached, boolean sameSite) {
        var scn = scenario();
        scn.StartGame();
        var local = scn.GetDSStartingLocation();
        var other = scn.GetLSCard("elsewhere");
        scn.MoveLocationToTable(other);
        if (attached) attachAtStartingSite(scn);
        var battle = !attached || sameSite ? local : other;
        var shooter = light ? scn.GetLSCard("obi") : scn.GetDSCard("vader");
        var victim = light ? scn.GetDSCard("garindan") : scn.GetLSCard("han");
        scn.MoveCardsToLocation(battle, shooter, victim);
        int defense = light ? 1 : 3;
        assertEquals(defense, scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(), victim), scn.epsilon);
        if (light) {
            scn.EnsureLSForcePile(1);
            scn.LSStartBattleAndSkipToWeaponsSegment(battle);
            scn.PrepareLSDestiny(1);
            scn.PrepareLSDestiny(defense + 1);
            scn.LSUseCardAction(shooter, "Fire");
            scn.LSChooseCard(victim);
        } else {
            scn.EnsureDSForcePile(1);
            scn.DSStartBattleAndSkipToWeaponsSegment(battle);
            scn.PrepareDSDestiny(1);
            scn.PrepareDSDestiny(defense + 1);
            scn.DSUseCardAction(shooter, "Fire");
            scn.DSChooseCard(victim);
        }
        passOptional(scn);
        // A single reduction still hits; reducing both draws reaches equality and misses.
        assertEquals(!attached || !sameSite, victim.isHit());
    }

    private void attachAtStartingSite(VirtualTableScenario scn) {
        var site = scn.GetDSStartingLocation();
        var ysalamir = scn.GetDSCard("ysalamir");
        var host = scn.GetDSCard("trooper");
        scn.MoveCardsToLocation(site, host);
        deployCreature(scn, ysalamir, site);
        attachParasite(scn, ysalamir, host);
    }

    private void deployCreature(VirtualTableScenario scn, PhysicalCardImpl ysalamir, PhysicalCardImpl site) {
        scn.MoveCardsToDSHand(ysalamir);
        scn.EnsureDSForcePile(2);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(ysalamir);
        scn.DSChooseCard(site);
        passOptional(scn);
    }

    private void attachParasite(VirtualTableScenario scn, PhysicalCardImpl ysalamir, PhysicalCardImpl host) {
        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCardActionAvailable(ysalamir, "Initiate attack"));
        scn.DSUseCardAction(ysalamir, "Initiate attack");
        assertFalse(scn.DSDecisionAvailable("Choose whose non-creature to attack"));
        passOptional(scn);
        scn.PassWeaponsSegmentActions();
        passOptional(scn);
        assertTrue(scn.IsAttachedTo(host, ysalamir));
    }

    /** Passes every pending optional-response window (PassAllResponses stops after 20), then any remaining responses. */
    private static void passOptional(VirtualTableScenario scn) {
        for (int i = 0; i < 100; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("optional response")) break;
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }
}
