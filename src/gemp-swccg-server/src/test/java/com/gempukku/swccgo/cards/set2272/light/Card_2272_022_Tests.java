package com.gempukku.swccgo.cards.set2272.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Original Rebel Gunrunner (Dec 2010 slip: returns the weapon "if it leaves table"), a version of 1272_22. */
public class Card_2272_022_Tests {
    private VirtualTableScenario scenario(String gunrunnerId) {
        return new VirtualTableScenario(new HashMap<>() {{
            put("gunrunner", gunrunnerId);
            put("other", "1272_22".equals(gunrunnerId) ? "2272_22" : "1272_22");
            put("blaster", "1_152");
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("trooper", "1_194");
            put("trooper2", "1_194");
            put("enemyBlaster", "1_317");
            put("enemyBlaster2", "1_317");
            put("vader", "1_168");
            put("levitation", "5_160");
            put("dontMove", "8_140");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalRebelGunrunnerIsTheCurrentCardWithTheDecember2010Text() {
        var s = scenario("2272_22");
        assertVersion(s.GetLSCard("gunrunner").getBlueprint(), s.GetLSCard("other").getBlueprint(), "1272_22", "",
                LocalDate.of(2010, 12, 15), LocalDate.of(2012, 7, 21),
                "Deploy on table; take and stack a non-lightsaber weapon (without 'lost' in game text) here from Reserve Deck; reshuffle. If a battle was just initiated, may deploy weapon here on a participating card. Return weapon here at end of weapons segment (if possible), if it leaves table, or at end of battle. (Immune to Alter.)");
    }

    @Test
    public void OriginalRebelGunrunnerIsTheSameUniqueCardAsTheCurrentVersion() {
        for (String onTableId : List.of("1272_22", "2272_22")) {
            for (boolean otherOut : List.of(false, true)) {
                var s = scenario(onTableId);
                var onTable = s.GetLSCard("gunrunner");
                var inHand = s.GetLSCard("other");
                s.StartGame();
                if (otherOut)
                    s.MoveCardsToLSSideOfTable(onTable);
                s.MoveCardsToLSHand(inHand);
                s.SkipToLSTurn(Phase.DEPLOY);
                s.EnsureLSForcePile(10);
                assertEquals(onTableId + " on table=" + otherOut, !otherOut, s.LSDeployAvailable(inHand));
            }
        }
    }

    private void battleWithGunrunnerBlasterOnLuke(VirtualTableScenario s, PhysicalCardImpl... darkPresent) {
        var gunrunner = s.GetLSCard("gunrunner");
        var blaster = s.GetLSCard("blaster");
        var luke = s.GetLSCard("luke");
        var site = s.GetDSStartingLocation();
        s.StartGame();
        s.MoveCardsToLSSideOfTable(gunrunner);
        s.MoveCardsToLocation(site, luke);
        s.MoveCardsToLocation(site, darkPresent);
        if (java.util.Arrays.asList(darkPresent).contains(s.GetDSCard("trooper2"))) {
            s.AttachCardsTo(s.GetDSCard("trooper"), s.GetDSCard("enemyBlaster"));
            s.AttachCardsTo(s.GetDSCard("trooper2"), s.GetDSCard("enemyBlaster2"));
        }
        s.StackCardsOn(gunrunner, blaster);
        s.SkipToLSTurn(Phase.BATTLE);
        s.EnsureDSForcePile(6);
        s.LSInitiateBattle(site);
        for (int i = 0; i < 20 && !(s.GetDecidingPlayer().equals(s.LS) && s.LSCardActionAvailable(gunrunner, "Deploy")); i++)
            s.PlayerPass(s.GetDecidingPlayer());
        s.LSUseCardAction(gunrunner, "Deploy");
        if (s.LSHasCardChoiceAvailable(luke))
            s.LSChooseCard(luke);
        for (int i = 0; i < 20 && blaster.getAttachedTo() != luke; i++)
            s.PlayerPass(s.GetDecidingPlayer());
        assertEquals(luke, blaster.getAttachedTo());
    }

    private void stealBlaster(VirtualTableScenario s) {
        var levitation = s.GetDSCard("levitation");
        for (int i = 0; i < 20 && !(s.GetDecidingPlayer().equals(s.DS) && s.DSCardPlayAvailable(levitation, "Steal")); i++)
            s.PlayerPass(s.GetDecidingPlayer());
        assertTrue(s.DSCardPlayAvailable(levitation, "Steal"));
        s.DSPlayCard(levitation, "Steal");
        if (s.DSHasCardChoiceAvailable(s.GetLSCard("blaster")))
            s.DSChooseCard(s.GetLSCard("blaster"));
        if (s.DSHasCardChoiceAvailable(s.GetDSCard("vader")))
            s.DSChooseCard(s.GetDSCard("vader"));
        s.PassAllResponses();
    }

    private void captureLuke(VirtualTableScenario s) {
        var dontMove = s.GetDSCard("dontMove");
        for (int i = 0; i < 20 && !(s.GetDecidingPlayer().equals(s.DS) && s.DSCardPlayAvailable(dontMove)); i++)
            s.PlayerPass(s.GetDecidingPlayer());
        assertTrue(s.DSCardPlayAvailable(dontMove));
        s.PrepareDSDestiny(6);
        s.DSPlayCard(dontMove);
        if (s.DSHasCardChoiceAvailable(s.GetLSCard("luke")))
            s.DSChooseCard(s.GetLSCard("luke"));
        for (int i = 0; i < 30 && s.GetLSCard("luke").getZone().isInPlay() && !s.GetLSCard("luke").isCaptive(); i++) {
            var decider = s.GetDecidingPlayer();
            if (decider == null) break;
            if (decider.equals(s.DS) && s.DSCaptureDecisionAvailable())
                s.DSChooseSeizeCaptive();
            else if (decider.equals(s.DS) && s.DSHasCardChoiceAvailable(s.GetDSCard("trooper")))
                s.DSChooseCard(s.GetDSCard("trooper"));
            else
                s.PlayerPass(decider);
        }
    }

    @Test
    public void OriginalRebelGunrunnerDoesNotSaveItsWeaponFromBeingStolen() {
        var s = scenario("2272_22");
        var vader = s.GetDSCard("vader");
        s.MoveCardsToDSHand(s.GetDSCard("levitation"));
        battleWithGunrunnerBlasterOnLuke(s, vader);
        stealBlaster(s);
        assertEquals(vader, s.GetLSCard("blaster").getAttachedTo());
    }

    @Test
    public void CurrentRebelGunrunnerReturnsItsWeaponInsteadOfItBeingStolen() {
        var s = scenario("1272_22");
        s.MoveCardsToDSHand(s.GetDSCard("levitation"));
        battleWithGunrunnerBlasterOnLuke(s, s.GetDSCard("vader"));
        stealBlaster(s);
        assertInZone(Zone.STACKED, s.GetLSCard("blaster"));
    }

    @Test
    public void OriginalRebelGunrunnerLeavesItsWeaponOnACapturedCarrier() {
        // "become inactive" was added by the Master List: the original does not return the weapon when Luke is captured
        var s = scenario("2272_22");
        s.MoveCardsToDSHand(s.GetDSCard("dontMove"));
        battleWithGunrunnerBlasterOnLuke(s, s.GetDSCard("trooper"), s.GetDSCard("trooper2"));
        captureLuke(s);
        assertTrue(s.GetLSCard("luke").isCaptive());
        assertFalse(s.GetLSCard("gunrunner") == s.GetLSCard("blaster").getStackedOn());
    }
}
