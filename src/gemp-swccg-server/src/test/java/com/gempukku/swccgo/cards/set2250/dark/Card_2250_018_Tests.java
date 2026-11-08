package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/** Pre-errata An Enemy Of The Republic (Dec 2009 sheet), a version of 1250_18. */
public class Card_2250_018_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("obi", "1_21", "luke", "1_19", "droid", "1_5")),
                new HashMap<>(Map.of("enemy", "2250_18", "current", "1250_18", "sidious", "1116_46", "senate", "12_167",
                        "vader", "1_168")),
                28, 28, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("senate"));
        return scn;
    }

    @Test
    public void AnEnemyOfTheRepublicOriginalIdentity() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("enemy").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1250_18", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2014, 2, 4),
                "Deploy on Sidious. While at Senate, if battle was just initiated anywhere, opponent must relocate this card to their participating character; during battle here, your destiny draws may not be canceled. If character about to be lost, relocate to Sidious; may ▲ a card.");
    }

    @Test
    public void AnEnemyOfTheRepublicOriginalMayBeRelocatedToAParticipatingDroid() {
        var scn = scenario();
        var sidious = scn.GetDSCard("sidious");
        var site = scn.GetDSStartingLocation();
        var obi = scn.GetLSCard("obi");
        var droid = scn.GetLSCard("droid");
        var offSite = scn.GetLSCard("luke");
        var enemy = scn.GetDSCard("enemy");
        scn.MoveCardsToLocation(scn.GetDSCard("senate"), sidious);
        scn.AttachCardsTo(sidious, enemy);
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader"), obi, droid);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), offSite);
        scn.EnsureDSForcePile(1);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        boolean chose = false;
        for (int i = 0; i < 35 && !scn.AwaitingDSWeaponsSegmentActions(); i++) {
            if (scn.LSDecisionAvailable("Choose your participating character")) {
                assertTrue(scn.LSHasCardChoiceAvailable(obi));
                assertTrue(scn.LSHasCardChoiceAvailable(droid));
                assertFalse(scn.LSHasCardChoiceAvailable(offSite));
                scn.LSChooseCard(droid);
                chose = true;
            } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected battle-start decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(chose);
        assertSame(droid, enemy.getAttachedTo());
    }

    @Test
    public void AnEnemyOfTheRepublicOriginalIsTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : java.util.List.of(true, false)) {
            for (boolean otherOut : java.util.List.of(false, true)) {
                var scn = scenario();
                var onTable = scn.GetDSCard(currentOnTable ? "current" : "enemy");
                var inHand = scn.GetDSCard(currentOnTable ? "enemy" : "current");
                var sidious = scn.GetDSCard("sidious");
                scn.MoveCardsToLocation(scn.GetDSCard("senate"), sidious);
                if (otherOut)
                    scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
                if (otherOut)
                    scn.AttachCardsTo(scn.GetDSCard("vader"), onTable);
                scn.MoveCardsToDSHand(inHand);
                scn.SkipToPhase(Phase.DEPLOY);
                scn.EnsureDSForcePile(10);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.DSCardPlayAvailable(inHand));
            }
        }
    }
}
