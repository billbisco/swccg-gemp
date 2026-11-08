package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Original Master Kenobi (first printing 2011-06-03: no [Maintenance] restriction), a version of 1273_27. */
public class Card_2273_1027_Tests {
    private static final String PLACE = "Place forfeited Rebel in Used Pile";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("kenobi", "2273_1027", "current", "1273_27", "communing", "216_24", "luke", "1_19", "chewie", "13_9", "camp", "1_131")),
                new HashMap<>(Map.of("vader", "1_168")), 40, 40,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalMasterKenobiIsTheCurrentCardWithTheJune2011Text() {
        var s = scenario();
        assertVersion(s.GetLSCard("kenobi").getBlueprint(), s.GetLSCard("current").getBlueprint(), "1273_27", "",
                LocalDate.of(2011, 6, 3), LocalDate.of(2011, 6, 23),
                "While 'communing': you may not deploy [Episode I] Jedi; Rebels of ability < 5 draw one battle destiny if unable to otherwise and, once per turn, may place one just forfeited in Used Pile; Once per turn, may deploy from Reserve Deck a Tatooine battleground (or Obi-Wan's Hut); reshuffle.");
    }

    @Test
    public void OriginalMasterKenobiIsTheSameUniqueCardAsTheCurrentVersion() {
        // Persona Obi-Wan and the same title: with one of them 'communing', the other may not deploy
        var s = scenario();
        var communing = s.GetLSCard("communing");
        var current = s.GetLSCard("current");
        s.StartGame();
        s.MoveCardsToLSSideOfTable(communing);
        s.StackCardsOn(communing, s.GetLSCard("kenobi"));
        s.MoveCardsToLSHand(current);
        s.SkipToLSTurn(Phase.DEPLOY);
        s.EnsureLSForcePile(20);
        assertEquals(false, s.LSDeployAvailable(current));
    }

    @Test
    public void OriginalMasterKenobiPlacesAJustForfeitedRebelInUsedPileForFree() {
        var s = scenario();
        var communing = s.GetLSCard("communing");
        var kenobi = s.GetLSCard("kenobi");
        var luke = s.GetLSCard("luke");
        var site = s.GetDSStartingLocation();
        s.StartGame();
        s.MoveCardsToLSSideOfTable(communing);
        s.StackCardsOn(communing, kenobi);
        s.MoveCardsToLocation(site, luke, s.GetDSCard("vader"));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(4);
        s.EnsureLSForcePile(4);
        s.PrepareDSDestiny(2);
        s.PrepareLSDestiny(0);
        s.SkipToPhase(Phase.BATTLE);
        s.DSInitiateBattle(site);
        s.SkipToDamageSegment(true);
        s.PassDamageSegmentActions();
        assertTrue(s.LSHasCardChoiceAvailable(luke));
        s.LSChooseCard(luke);
        if (s.LSDecisionAvailable("Do you still want to forfeit it?")) s.LSChooseYes();
        for (int i = 0; i < 20 && !(s.GetDecidingPlayer().equals(s.LS) && s.LSCardActionAvailable(kenobi, PLACE)); i++)
            s.PlayerPass(s.GetDecidingPlayer());
        assertTrue(s.LSCardActionAvailable(kenobi, PLACE));
        int forcePile = s.GetLSForcePileCount();
        int reserve = s.GetLSReserveDeckCount();
        s.LSUseCardAction(kenobi, PLACE);
        s.PassAllResponses();
        assertTrue(s.GetLSUsedPile().contains(luke));
        // no Force used or lost for it (the Master List added "lose 1 Force (or use 2 Force)")
        assertEquals(forcePile, s.GetLSForcePileCount());
        assertEquals(reserve, s.GetLSReserveDeckCount());
    }

    private boolean maintenanceDeploys(String kenobiKey) {
        var s = scenario();
        var communing = s.GetLSCard("communing");
        var chewie = s.GetLSCard("chewie");
        s.StartGame();
        s.MoveLocationToTable(s.GetLSCard("camp"));
        s.MoveCardsToLSSideOfTable(communing);
        s.StackCardsOn(communing, s.GetLSCard(kenobiKey));
        s.MoveCardsToLSHand(chewie);
        s.EnsureLSForcePile(10);
        s.SkipToLSTurn(Phase.DEPLOY);
        return s.LSDeployAvailable(chewie);
    }

    @Test
    public void OriginalMasterKenobiLetsYouDeployMaintenanceCardsWhileCommuning() {
        assertTrue(com.gempukku.swccgo.framework.VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("13_9").hasIcon(com.gempukku.swccgo.common.Icon.MAINTENANCE));
        assertTrue(maintenanceDeploys("kenobi"));
        org.junit.Assert.assertFalse(maintenanceDeploys("current"));
    }
}
