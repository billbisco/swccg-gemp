package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.set2.light.Card2_042;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Bare cards play the text in force (current PC errata, Players Committee SWCCG 2023 Advanced Rulebook Appendix A),
 * printed text states keep the printed play (swccg-guild/work#285 follow-up): Remote, All Too Easy, Luminous (with
 * Covert Landing), Attack Run, Luke Seeker, Chief Bast.
 */
public class ErrataPlayFixesTests {

    private static VirtualTableScenario scenario(Map<String, String> ls, Map<String, String> ds,
                                                 StartingSetup lsStart, StartingSetup dsStart) {
        HashMap<String, String> lsCards = new HashMap<>(ls);
        HashMap<String, String> dsCards = new HashMap<>(ds);
        if (lsCards.isEmpty()) lsCards.put("filler", "1_28");
        if (dsCards.isEmpty()) dsCards.put("filler", "1_194");
        return new VirtualTableScenario(lsCards, dsCards, 20, 20,
                lsStart, dsStart,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
    }

    private static VirtualTableScenario scenario(Map<String, String> ls, Map<String, String> ds) {
        return scenario(ls, ds, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation);
    }

    /** Passes whoever is deciding until the condition holds (or gives up). */
    private static boolean passUntil(VirtualTableScenario scn, java.util.function.BooleanSupplier condition) {
        StringBuilder trace = new StringBuilder();
        for (int i = 0; i < 25; i++) {
            if (condition.getAsBoolean())
                return true;
            String player = scn.DSAnyDecisionsAvailable() ? scn.DS : (scn.LSAnyDecisionsAvailable() ? scn.LS : null);
            if (player == null)
                return condition.getAsBoolean();
            var decision = scn.GetAwaitingDecision(player);
            trace.append(" | ").append(player.startsWith("D") ? "DS: " : "LS: ").append(decision.getText());
            if (decision.getText().contains("You have not activated Force"))
                throw new AssertionError("ran to the next turn: " + trace);
            try {
                if (player.equals(scn.DS)) scn.DSPass(); else scn.LSPass();
            }
            catch (RuntimeException e) {
                throw new AssertionError(trace + " || could not pass " + player + " decision '" + decision.getText() + "' "
                        + decision.getDecisionType() + " " + decision.getDecisionParameters().keySet() + " " + java.util.Arrays.toString(decision.getDecisionParameters().get("results")), e);
            }
        }
        return condition.getAsBoolean();
    }

    // ---- Remote (2_28): "Select one character present to be power or forfeit +1" ----

    @Test
    public void RemoteForfeitOptionMakesTheCharacterForfeitPlusOne() {
        for (String id : new String[] {"2_28", "2_28o"}) {
            var scn = scenario(Map.of("remote", id, "luke", "1_19"), Map.of());
            scn.StartGame();
            var remote = scn.GetLSCard("remote");
            var luke = scn.GetLSCard("luke");
            scn.MoveCardsToLocation(scn.GetLSStartingLocation(), remote, luke);
            scn.SkipToLSTurn(Phase.CONTROL);
            int power = scn.GetPower(luke);
            int forfeit = scn.GetForfeit(luke);
            assertTrue(id, scn.LSCardActionAvailable(remote, "forfeit +1"));
            scn.LSUseCardAction(remote, "forfeit +1");
            if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(luke))
                scn.LSChooseCard(luke);
            scn.PassAllResponses();
            assertEquals(id, forfeit + 1, scn.GetForfeit(luke));
            assertEquals(id, power, scn.GetPower(luke));
        }
    }

    // ---- All Too Easy (5_112): "is about to be 'hit' by a lightsaber, deploy on that character" ----

    /** Vader's Lightsaber 'hits' Luke at the Carbonite Chamber (during a battle there, so a 'hit' Luke is not lost at once). */
    private static VirtualTableScenario allTooEasyHit(String id) {
        var scn = scenario(Map.of("luke", "1_19"), Map.of("easy", id, "chamber", "5_166", "saber", "1_324", "trooper", "1_194"));
        scn.StartGame();
        var chamber = scn.GetDSCard("chamber");
        scn.MoveLocationToTable(chamber);
        scn.MoveCardsToLocation(chamber, scn.GetLSCard("luke"), scn.GetDSCard("trooper"));
        scn.MoveCardsToDSHand(scn.GetDSCard("easy"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(10);
        scn.DSInitiateBattle(chamber);
        passUntil(scn, () -> scn.DSDecisionAvailable("weapons segment action"));
        var saber = scn.GetDSCard("saber");
        var action = new TopLevelGameTextAction(saber, scn.DS, saber.getCardId());
        action.appendEffect(new HitCardEffect(action, scn.GetLSCard("luke"), saber));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        return scn;
    }

    @Test
    public void AllTooEasyDeploysWhenTheCharacterIsAboutToBeHitAndPreventsTheHit() {
        var scn = allTooEasyHit("5_112");
        var easy = scn.GetDSCard("easy");
        var luke = scn.GetLSCard("luke");
        // the about-to-be-'hit' window: Luke is not 'hit' yet and All Too Easy may deploy on him
        assertTrue(passUntil(scn, () -> scn.DSAnyDecisionsAvailable() && scn.DSCardPlayAvailable(easy)));
        assertFalse(luke.isHit());
        scn.DSPlayCard(easy);
        if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(luke))
            scn.DSChooseCard(luke);
        passUntil(scn, () -> luke.isCaptive() || scn.DSDecisionAvailable("weapons segment action"));
        assertTrue(scn.IsAttachedTo(luke, easy));
        assertTrue(luke.isCaptive());
        assertTrue(luke.isFrozen());
        // the 'hit' was prevented: Luke was captured and 'frozen' instead
        assertFalse(luke.isHit());
    }

    @Test
    public void AllTooEasyPrintedDeploysOnlyAfterTheCharacterWasHit() {
        var scn = allTooEasyHit("5_112o");
        var easy = scn.GetDSCard("easy");
        var luke = scn.GetLSCard("luke");
        // no response is offered before the 'hit'; it is offered once Luke was just 'hit'
        assertTrue(passUntil(scn, () -> luke.isHit() || (scn.DSAnyDecisionsAvailable() && scn.DSCardPlayAvailable(easy))));
        assertTrue("printed All Too Easy waits until the character was just 'hit'", luke.isHit());
        assertTrue(passUntil(scn, () -> scn.DSAnyDecisionsAvailable() && scn.DSCardPlayAvailable(easy)));
        scn.DSPlayCard(easy);
        if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(luke))
            scn.DSChooseCard(luke);
        passUntil(scn, () -> luke.isCaptive() || scn.DSDecisionAvailable("weapons segment action"));
        assertTrue(scn.IsAttachedTo(luke, easy));
        assertTrue(luke.isCaptive());
        assertTrue(luke.isFrozen());
    }

    // ---- Luminous (9_77) + Covert Landing (8_45) ----

    @Test
    public void LuminousHasShipDockingCapabilityOnlyAsPrinted() {
        var scn = scenario(Map.of("bare", "9_77", "printed", "9_77o"), Map.of());
        assertFalse(scn.GetLSCard("bare").getBlueprint().hasKeyword(Keyword.SHIP_DOCKING_CAPABILITY));
        assertTrue(scn.GetLSCard("printed").getBlueprint().hasKeyword(Keyword.SHIP_DOCKING_CAPABILITY));
    }

    private static VirtualTableScenario luminousScenario(String id) {
        return scenario(Map.of("luminous", id, "covert", "8_45", "hoth", "3_55", "hothdb", "3_59", "scout", "1_28"),
                Map.of("trooper", "1_194"));
    }

    @Test
    public void CovertLandingLostMayDeployScoutsAboardLuminousThatJustLandedOnlyWithThePcErrata() {
        for (String id : new String[] {"9_77", "9_77o"}) {
            var scn = luminousScenario(id);
            var luminous = scn.GetLSCard("luminous");
            var covert = scn.GetLSCard("covert");
            var hoth = scn.GetLSCard("hoth");
            var hothdb = scn.GetLSCard("hothdb");
            scn.StartGame();
            scn.MoveLocationToTable(hothdb);
            scn.MoveLocationToTable(hoth);
            scn.MoveCardsToLocation(hoth, luminous);
            scn.MoveCardsToLSHand(covert);
            scn.SkipToLSTurn(Phase.MOVE);
            scn.EnsureLSForcePile(10);
            assertTrue(id, scn.LSCardActionAvailable(luminous, "Land"));
            scn.LSUseCardAction(luminous, "Land");
            if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(hothdb))
                scn.LSChooseCard(hothdb);
            scn.PassCardAndForceUseResponses();
            // PC errata "(even while landed)": the printed Luminous's game text (and so its use of Covert Landing)
            // does not apply once it has landed
            boolean offered;
            try {
                offered = passUntil(scn, () -> scn.LSAnyDecisionsAvailable() && scn.LSCardPlayAvailable(covert, "LOST"));
            }
            catch (AssertionError ranToNextTurn) {
                offered = false;
            }
            assertEquals(id, !id.endsWith("o"), offered);
            assertTrue(id, scn.CardsAtLocation(hothdb, luminous));
        }
    }

    /** Whether Light Side may play Covert Landing's USED (add one battle destiny) in a battle at the docking bay where Luminous is landed. */
    private static boolean covertLandingUsedWhileLanded(String id) {
        var scn = luminousScenario(id);
        var luminous = scn.GetLSCard("luminous");
        var covert = scn.GetLSCard("covert");
        var hothdb = scn.GetLSCard("hothdb");
        scn.StartGame();
        scn.MoveLocationToTable(hothdb);
        scn.MoveLocationToTable(scn.GetLSCard("hoth"));
        scn.MoveCardsToLocation(hothdb, luminous, scn.GetLSCard("scout"), scn.GetDSCard("trooper"));
        scn.MoveCardsToLSHand(covert);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(10);
        scn.LSUseCardAction(hothdb, "Initiate battle");
        return passUntil(scn, () -> scn.LSAnyDecisionsAvailable() && scn.LSCardPlayAvailable(covert, "USED"));
    }

    @Test
    public void LuminousMayUseCovertLandingLikeAShuttleEvenWhileLanded() {
        assertTrue(covertLandingUsedWhileLanded("9_77"));
        assertFalse(covertLandingUsedWhileLanded("9_77o"));
    }

    // ---- Attack Run (2_42): Z = highest ability of opponent's TIE pilots in trench just before Pull Up! ----

    @Test
    public void AttackRunZCountsOnlyOpponentsTiePilots() {
        for (String id : new String[] {"2_42", "2_42d"}) {
            var scn = scenario(Map.of("attackrun", id, "trench", "2_62", "stolentie", "204_34", "luke", "1_19"),
                    Map.of("tie", "1_304", "pilot", "1_173", "deathstar", "2_143"));
            scn.StartGame();
            scn.MoveLocationToTable(scn.GetDSCard("deathstar"));
            var trench = scn.GetLSCard("trench");
            var attackRun = scn.GetLSCard("attackrun");
            scn.MoveLocationToTable(trench);
            scn.AttachCardsTo(trench, attackRun);
            var tie = scn.GetDSCard("tie");
            var stolenTie = scn.GetLSCard("stolentie");
            scn.MoveCardsToLocation(trench, tie, stolenTie);
            scn.BoardAsPilot(tie, scn.GetDSCard("pilot")); // DS-61-2, ability 2
            scn.BoardAsPilot(stolenTie, scn.GetLSCard("luke")); // Luke, ability 4, in Light Side's own TIE
            float z = ((Card2_042) attackRun.getBlueprint()).calculateValueForZ(scn.game(), attackRun);
            // PC errata: opponent's TIE pilots only; Decipher (d): every TIE pilot in trench
            assertEquals(id, id.endsWith("d") ? 4 : 2, z, scn.epsilon);
        }
    }

    // ---- Luke Seeker (1_321): "When present with Luke of ability < 4 or pilot of ability < 3 ..." ----

    @Test
    public void LukeSeekerMakesAPilotOfAbilityLessThanThreeLostButNotLukeOfAbilityFour() {
        var scn = scenario(Map.of("luke", "1_19", "biggs", "1_3"), Map.of("seeker", "1_321"));
        var seeker = scn.GetDSCard("seeker");
        var luke = scn.GetLSCard("luke");
        var biggs = scn.GetLSCard("biggs");
        scn.StartGame();
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, seeker);
        scn.MoveCardsToLSHand(luke, biggs);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(10);
        scn.LSDeployCardAndPassResponses(luke, site);
        passUntil(scn, () -> scn.AwaitingLSDeployPhaseActions());
        assertEquals("Luke is ability 4: not a target", Zone.AT_LOCATION, luke.getZone());
        assertEquals(Zone.AT_LOCATION, seeker.getZone());
        scn.LSDeployCardAndPassResponses(biggs, site);
        for (int i = 0; i < 10 && seeker.getZone() == Zone.AT_LOCATION; i++) {
            if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(biggs)) {
                assertFalse(scn.DSHasCardChoiceAvailable(luke));
                scn.DSChooseCard(biggs);
            }
            else if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(biggs)) {
                assertFalse(scn.LSHasCardChoiceAvailable(luke));
                scn.LSChooseCard(biggs);
            }
            else
                passUntil(scn, () -> true == false || seeker.getZone() != Zone.AT_LOCATION
                        || (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(biggs))
                        || (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(biggs)));
        }
        assertTrue(biggs.getZone().name().endsWith("LOST_PILE"));
        assertTrue(seeker.getZone().name().endsWith("LOST_PILE"));
        assertEquals(Zone.AT_LOCATION, luke.getZone());
    }

    // ---- Chief Bast (1_165): "... a system where Bast is present with your non-droid character, may 'evacuate' ..." ----

    @Test
    public void ChiefBastMayEvacuateWithYourNonDroidCharacterWhenABattleIsInitiatedAtHisSystem() {
        var scn = scenario(Map.of("corvette", "1_140", "pilot", "1_3"),
                Map.of("bast", "1_165", "officer", "1_173", "destroyer", "1_302", "tatooine", "1_289", "mos", "1_295"),
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation);
        var bast = scn.GetDSCard("bast");
        var tatooine = scn.GetDSCard("tatooine");
        var mos = scn.GetDSCard("mos");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(mos);
        var destroyer = scn.GetDSCard("destroyer");
        var corvette = scn.GetLSCard("corvette");
        scn.MoveCardsToLocation(tatooine, destroyer, corvette);
        scn.BoardAsPilot(destroyer, bast);
        scn.BoardAsPassenger(destroyer, scn.GetDSCard("officer"));
        scn.BoardAsPilot(corvette, scn.GetLSCard("pilot"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(10);
        scn.DSUseCardAction(tatooine, "Initiate battle");
        assertTrue(passUntil(scn, () -> scn.DSAnyDecisionsAvailable() && scn.DSCardActionAvailable(bast, "'Evacuate'")));
        scn.DSUseCardAction(bast, "'Evacuate'");
        var officer = scn.GetDSCard("officer");
        for (int i = 0; i < 10 && !scn.CardsAtLocation(mos, bast); i++) {
            if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(mos))
                scn.DSChooseCard(mos);
            else if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(officer))
                scn.DSChooseCard(officer);
            else
                passUntil(scn, () -> scn.CardsAtLocation(mos, bast)
                        || (scn.DSAnyDecisionsAvailable() && (scn.DSHasCardChoiceAvailable(mos) || scn.DSHasCardChoiceAvailable(officer))));
        }
        // both 'evacuated' (relocated from aboard the Star Destroyer) to the related site
        assertTrue(scn.CardsAtLocation(mos, bast, officer));
    }
}
