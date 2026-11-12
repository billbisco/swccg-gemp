package com.gempukku.swccgo.cards.textstates;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Gameplay of the printed (o) text states built from Bill's rulings of 12 Nov 2026 (ai-team-skills RULINGS.md
 * "printed-text readings"), each against the bare card where they differ:
 * <ul>
 *     <li>For Luck (102_1o): "exclude X Jedi" includes your own Jedi (the bare card: Dark Jedi only).</li>
 *     <li>Obi-Wan's Lightsaber (1_157o): character hit if total destiny &gt; ability (bare: defense value).</li>
 *     <li>Zuckuss' Snare Rifle (4_180o): may target a vehicle (no effect, but it fired, so Jabba's Through With You may
 *     add a battle destiny); "may be immediately captured" is the target's owner's choice.</li>
 *     <li>Uniqueness-only errata (4_21o, 4_123o, ...): the printed card is non-unique, keeps the title (same card for
 *     Grimtaash and the cumulative rule), and two printed Do, Or Do Not do not double the Force loss.</li>
 *     <li>A printed character state (1_168o Darth Vader) is the same persona as the bare card.</li>
 * </ul>
 */
public class RuledTextStatesGameplayTests {
    private static VirtualTableScenario scenario(Map<String, String> light, Map<String, String> dark) {
        return new VirtualTableScenario(new HashMap<>(light), new HashMap<>(dark), 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
    }

    private static boolean lost(com.gempukku.swccgo.game.PhysicalCard card) {
        return card.getZone() == Zone.LOST_PILE || card.getZone() == Zone.TOP_OF_LOST_PILE;
    }

    /** Passes optional responses (either player) until the condition holds, or nothing is left to pass. */
    private static boolean passUntil(VirtualTableScenario scn, java.util.function.BooleanSupplier done) {
        for (int i = 0; i < 30; ++i) {
            if (done.getAsBoolean()) return true;
            if (scn.LSDecisionAvailable("Optional responses")) scn.LSPass();
            else if (scn.DSDecisionAvailable("Optional responses")) scn.DSPass();
            else return done.getAsBoolean();
        }
        return done.getAsBoolean();
    }

    private static void passOpponentResponses(VirtualTableScenario scn) {
        if (scn.DSAnyDecisionsAvailable()) {
            scn.DSPass();
        }
    }

    // ---------------------------------------------------------------- For Luck

    /** Light Side plays Alter on a Dark Side Effect with its own Jedi Obi-Wan as highest-ability character. */
    private VirtualTableScenario alterWithOwnJedi(String forLuckId) {
        var scn = scenario(Map.of("forluck", forLuckId, "obiwan", "1_21", "alter", "1_71"),
                Map.of("rack", "1_211"));
        var forLuck = scn.GetLSCard("forluck");
        var obiwan = scn.GetLSCard("obiwan");
        var alter = scn.GetLSCard("alter");
        var rack = scn.GetDSCard("rack");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(forLuck);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), obiwan);
        scn.MoveCardsToDSSideOfTable(rack);
        scn.MoveCardsToLSHand(alter);
        scn.LSActivateForceCheat(4);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(alter));
        scn.LSPlayCard(alter);
        scn.LSChooseCard(rack);
        scn.LSChooseCard(obiwan);
        passOpponentResponses(scn);
        return scn;
    }

    @Test
    public void PrintedForLuckMayExcludeYourOwnJedi() {
        var scn = alterWithOwnJedi("102_1o");
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("forluck"), "Exclude"));
    }

    @Test
    public void BareForLuckExcludesOnlyDarkJedi() {
        var scn = alterWithOwnJedi("102_1");
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("forluck"), "Exclude"));
    }

    @Test
    public void PrintedForLuckAcceptsJediAndDarkJediOfEitherPlayer() {
        var scn = scenario(Map.of("forluck", "102_1o", "obiwan", "1_21"), Map.of("vader", "1_168"));
        scn.StartGame();
        var printed = (com.gempukku.swccgo.cards.set102.light.Card102_001o) scn.GetLSCard("forluck").getBlueprint();
        var bare = new com.gempukku.swccgo.cards.set102.light.Card102_001();
        var exclPrinted = filterOf(printed);
        var exclBare = filterOf(bare);
        assertTrue(exclPrinted.accepts(scn.game(), scn.GetLSCard("obiwan")));
        assertTrue(exclPrinted.accepts(scn.game(), scn.GetDSCard("vader")));
        assertFalse(exclBare.accepts(scn.game(), scn.GetLSCard("obiwan")));
        assertTrue(exclBare.accepts(scn.game(), scn.GetDSCard("vader")));
    }

    private static com.gempukku.swccgo.filters.Filter filterOf(com.gempukku.swccgo.cards.set102.light.Card102_001 card) {
        try {
            var m = com.gempukku.swccgo.cards.set102.light.Card102_001.class.getDeclaredMethod("getExcludableFromHighestAbilityFilter");
            m.setAccessible(true);
            return (com.gempukku.swccgo.filters.Filter) m.invoke(card);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    // ---------------------------------------------------------------- Premiere lightsabers

    private VirtualTableScenario fireObiWansLightsaberAtBoba(String saberId) {
        var scn = scenario(Map.of("obiwan", "1_21", "saber", saberId), Map.of("boba", "105_4"));
        var obiwan = scn.GetLSCard("obiwan");
        var saber = scn.GetLSCard("saber");
        var boba = scn.GetDSCard("boba");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, obiwan, boba);
        scn.AttachCardsTo(obiwan, saber);
        scn.LSActivateForceCheat(3);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(2);
        scn.PrepareLSDestiny(2);

        assertTrue(scn.LSCardActionAvailable(saber, "Fire"));
        scn.LSUseCardAction(saber, "Fire");
        scn.LSChooseCard(boba);
        scn.PassWeaponFireWithDestinyDraw(2);
        scn.PassAllResponses();
        return scn;
    }

    @Test
    public void PrintedObiWansLightsaberHitsAnArmoredCharacterByAbility() {
        var scn = fireObiWansLightsaberAtBoba("1_157o"); // total 4 > ability 3
        assertTrue(scn.GetDSCard("boba").isHit());
    }

    @Test
    public void BareObiWansLightsaberComparesDefenseValue() {
        var scn = fireObiWansLightsaberAtBoba("1_157"); // total 4 is not > defense value 5 (armor)
        assertFalse(scn.GetDSCard("boba").isHit());
    }

    // ---------------------------------------------------------------- Zuckuss' Snare Rifle

    private VirtualTableScenario snareRifleBattle(String rifleId) {
        var scn = scenario(Map.of("speeder", "3_69", "luke", "1_19"),
                Map.of("zuckuss", "4_107", "rifle", rifleId, "jabba", "7_255"));
        var speeder = scn.GetLSCard("speeder");
        var luke = scn.GetLSCard("luke");
        var zuckuss = scn.GetDSCard("zuckuss");
        var rifle = scn.GetDSCard("rifle");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, zuckuss, speeder);
        scn.BoardAsPilot(speeder, luke);
        scn.AttachCardsTo(zuckuss, rifle);
        scn.MoveCardsToDSHand(scn.GetDSCard("jabba"));
        scn.DSActivateForceCheat(6);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        return scn;
    }

    @Test
    public void PrintedSnareRifleMayTargetAVehicleAndThenJabbasThroughWithYouAddsADestiny() {
        var scn = snareRifleBattle("4_180o");
        var rifle = scn.GetDSCard("rifle");
        var speeder = scn.GetLSCard("speeder");
        scn.PrepareDSDestiny(6);

        assertTrue(scn.DSCardActionAvailable(rifle, "Fire"));
        scn.DSUseCardAction(rifle, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(speeder));
        scn.DSChooseCard(speeder);
        var jabba = scn.GetDSCard("jabba");
        boolean jabbaOffered = passUntil(scn, () -> scn.DSAnyDecisionsAvailable() && scn.DSCardPlayAvailable(jabba));

        // No effect on a vehicle, but the bounty hunter fired a character weapon
        assertEquals(Zone.AT_LOCATION, speeder.getZone());
        assertFalse(speeder.isHit());
        assertTrue(jabbaOffered);
    }

    @Test
    public void BareSnareRifleMayNotTargetAVehicle() {
        var scn = snareRifleBattle("4_180");
        var rifle = scn.GetDSCard("rifle");
        if (scn.DSCardActionAvailable(rifle, "Fire")) {
            scn.DSUseCardAction(rifle, "Fire");
            assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("speeder")));
        }
    }

    private VirtualTableScenario snareRifleAtLuke(String rifleId) {
        var scn = scenario(Map.of("luke", "1_19"), Map.of("zuckuss", "4_107", "rifle", rifleId));
        var luke = scn.GetLSCard("luke");
        var zuckuss = scn.GetDSCard("zuckuss");
        var rifle = scn.GetDSCard("rifle");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, zuckuss, luke);
        scn.AttachCardsTo(zuckuss, rifle);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(6); // 6 - 1 > ability 4

        scn.DSUseCardAction(rifle, "Fire");
        scn.DSChooseCard(luke);
        passUntil(scn, () -> scn.LSDecisionAvailable("to be captured?") || scn.GetLSCard("luke").getZone() != Zone.AT_LOCATION);
        return scn;
    }

    @Test
    public void PrintedSnareRifleCaptureIsTheTargetOwnersChoice() {
        var scn = snareRifleAtLuke("4_180o");
        assertTrue(scn.LSDecisionAvailable("to be captured?"));
        scn.LSChooseNo();
        assertEquals(Zone.AT_LOCATION, scn.GetLSCard("luke").getZone());
    }

    @Test
    public void BareSnareRifleCapturesWithoutAChoice() {
        var scn = snareRifleAtLuke("4_180");
        assertFalse(scn.LSDecisionAvailable("to be captured?"));
        assertTrue(scn.GetLSCard("luke").getZone() != Zone.AT_LOCATION || scn.DSDecisionAvailable("captur"));
    }

    // ---------------------------------------------------------------- Uniqueness-only errata

    @Test
    public void PrintedDoOrDoNotIsNonUniqueAndTwoCopiesDoNotDoubleTheForceLoss() {
        var scn = scenario(Map.of("ddn1", "4_21o", "ddn2", "4_21o", "obiwan", "1_21", "alter", "1_71"),
                Map.of("rack", "1_211"));
        var ddn1 = scn.GetLSCard("ddn1");
        var ddn2 = scn.GetLSCard("ddn2");
        var obiwan = scn.GetLSCard("obiwan");
        var alter = scn.GetLSCard("alter");
        var rack = scn.GetDSCard("rack");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(ddn1);
        scn.MoveCardsToLSHand(ddn2, alter);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), obiwan);
        scn.MoveCardsToDSSideOfTable(rack);
        scn.LSActivateForceCheat(8);
        scn.SkipToLSTurn(Phase.DEPLOY);

        // Non-unique: a second printed copy deploys while one is on table
        assertTrue(scn.LSDeployAvailable(ddn2));
        scn.MoveCardsToLSSideOfTable(ddn2);
        assertTrue(Filters.non_unique.accepts(scn.game(), ddn1));

        int lostBefore = scn.GetLSLostPileCount();
        scn.PrepareLSDestiny(1); // 1 < ability 6: Alter succeeds
        scn.LSPlayCard(alter);
        scn.LSChooseCard(rack);
        scn.LSChooseCard(obiwan);
        for (int i = 0; i < 12; ++i) {
            if (scn.LSDecisionAvailable("Force to lose")) {
                scn.LSChooseCard(scn.GetTopOfLSForcePile());
            } else if (!passUntil(scn, () -> scn.LSDecisionAvailable("Force to lose") || !(scn.LSDecisionAvailable("Optional responses") || scn.DSDecisionAvailable("Optional responses")))) {
                break;
            } else if (!scn.LSDecisionAvailable("Force to lose")) {
                break;
            }
        }
        assertTrue(lost(rack));
        // Alter is a Lost Interrupt (+1), and the player loses 2 Force once (+2), not twice
        assertEquals(lostBefore + 3, scn.GetLSLostPileCount());
    }

    @Test
    public void BareDoOrDoNotIsUniqueAndMayNotDeployWhileAPrintedCopyIsOnTable() {
        var scn = scenario(Map.of("printed", "4_21o", "bare", "4_21"), Map.of("rack", "1_211"));
        var printed = scn.GetLSCard("printed");
        var bare = scn.GetLSCard("bare");
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(printed);
        scn.MoveCardsToLSHand(bare);
        scn.LSActivateForceCheat(4);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSDeployAvailable(bare));
        assertFalse(Filters.non_unique.accepts(scn.game(), bare));
        assertTrue(Filters.non_unique.accepts(scn.game(), printed));
        // Same title: the same card for any "same title" check
        assertTrue(Filters.sameTitleAs(printed).accepts(scn.game(), bare));
    }

    @Test
    public void GrimtaashTreatsAPrintedNonUniqueCopyAsTheSameCardAsTheBareCard() {
        var scn = scenario(Map.of("grimtaash", "2_49"), Map.of("printed", "4_123o", "bare", "4_123", "other", "1_211"));
        var printed = scn.GetDSCard("printed");
        var bare = scn.GetDSCard("bare");
        var other = scn.GetDSCard("other");
        var grimtaash = scn.GetLSCard("grimtaash");
        scn.StartGame();
        scn.MoveCardsToDSHand(printed, bare, other);
        scn.MoveCardsToLSHand(grimtaash);
        scn.LSActivateForceCheat(6);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(grimtaash, "Reveal"));
        scn.LSPlayCard(grimtaash, "Reveal");
        scn.PassAllResponses();
        for (int i = 0; i < 6 && scn.LSDecisionAvailable("Opponent's hand"); ++i) {
            scn.LSPass();
        }
        for (var card : new com.gempukku.swccgo.game.PhysicalCardImpl[]{printed, bare}) {
            if (scn.DSDecisionAvailable("Choose card to put on Lost Pile") && !lost(card)) {
                scn.DSChooseCard(card);
            }
        }
        passUntil(scn, () -> lost(printed) && lost(bare));
        assertTrue(lost(printed));
        assertTrue(lost(bare));
        assertEquals(Zone.HAND, other.getZone());
    }

    @Test
    public void PrintedVaderMayNotDeployWhileTheVaderPersonaIsOnTable() {
        var scn = scenario(Map.of("luke", "1_19"), Map.of("vader", "1_168", "printed", "1_168o"));
        var vader = scn.GetDSCard("vader");
        var printed = scn.GetDSCard("printed");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
        scn.MoveCardsToDSHand(printed);
        scn.DSActivateForceCheat(4);
        scn.SkipToPhase(Phase.DEPLOY);

        assertFalse(scn.DSDeployAvailable(printed));
    }
}
