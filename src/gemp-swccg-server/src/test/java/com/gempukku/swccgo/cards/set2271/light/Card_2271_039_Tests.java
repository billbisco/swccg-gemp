package com.gempukku.swccgo.cards.set2271.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Original Luke, Trust Me (Jun 2010 slip: "Unless your Epic Event on table"), a version of 1271_39. */
public class Card_2271_039_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("trust", "2271_39", "current", "1271_39", "attack", "2_42", "setup", "1271_46")),
                new HashMap<>(Map.of("epic", "5_129")), 40, 40,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalLukeTrustMeIsTheCurrentCardWithTheJune2010Text() {
        var s = scenario();
        assertVersion(s.GetLSCard("trust").getBlueprint(), s.GetLSCard("current").getBlueprint(), "1271_39", "",
                LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21),
                "Unless your Epic Event on table, deploy on table. Once per game, may take a [Setup] card into hand from Reserve Deck; reshuffle. May place this Effect in Used Pile to take an Immediate Effect into hand from Reserve Deck; reshuffle. May place this Effect in Lost Pile to cancel a Force drain. (Immune to Alter.)");
    }

    @Test
    public void OriginalLukeTrustMeIsTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : List.of(true, false)) {
            for (boolean otherOut : List.of(false, true)) {
                var s = scenario();
                var onTable = s.GetLSCard(currentOnTable ? "current" : "trust");
                var inHand = s.GetLSCard(currentOnTable ? "trust" : "current");
                s.StartGame();
                if (otherOut)
                    s.MoveCardsToLSSideOfTable(onTable);
                s.MoveCardsToLSHand(inHand);
                s.SkipToLSTurn(Phase.DEPLOY);
                s.EnsureLSForcePile(10);
                assertEquals("current on table=" + currentOnTable + ", other out=" + otherOut, !otherOut, s.LSDeployAvailable(inHand));
            }
        }
    }

    @Test
    public void OriginalLukeTrustMeDeploysWithOnlyOpponentsEpicEventButNotWithYourOwn() {
        for (boolean ownEpic : List.of(false, true)) {
            var s = scenario();
            var trust = s.GetLSCard("trust");
            s.StartGame();
            s.MoveCardsToDSSideOfTable(s.GetDSCard("epic"));
            if (ownEpic) s.MoveCardsToLSSideOfTable(s.GetLSCard("attack"));
            s.MoveCardsToLSHand(trust);
            s.SkipToLSTurn(Phase.DEPLOY);
            s.EnsureLSForcePile(10);
            assertEquals(!ownEpic, s.LSDeployAvailable(trust));
        }
    }

    @Test
    public void OriginalLukeTrustMeDeploysAfterYourStartingEpicEventLeavesTheTableButTheCurrentCardDoesNot() {
        var s = new VirtualTableScenario(
                new HashMap<>(Map.of("trust", "2271_39", "current", "1271_39", "interrupt", "216_39", "communing", "216_24",
                        "kenobi", "216_36", "aimHigh", "8_34")),
                new HashMap<>(), 30, 30,
                StartingSetup.LSStartingLocation("216_43"), StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        var communing = s.GetLSCard("communing");
        var kenobi = s.GetLSCard("kenobi");
        var aimHigh = s.GetLSCard("aimHigh");
        var trust = s.GetLSCard("trust");
        var current = s.GetLSCard("current");
        s.MoveCardsToLSHand(trust, current);
        s.DSDecided("0");
        s.LSDecided("0");
        StartingSetup.DefaultDSSpaceSystem.Setup(s);
        s.LSChooseCard(s.GetLSCard("starting-location"));
        assertTrue(s.LSDecisionAvailable("Choose starting interrupt"));
        s.LSChooseCard(s.GetLSCard("interrupt"));
        if (s.LSHasCardChoiceAvailable(communing)) s.LSChooseCard(communing);
        if (s.LSHasCardChoiceAvailable(kenobi)) s.LSChooseCard(kenobi);
        if (s.LSHasCardChoiceAvailable(aimHigh)) s.LSChooseCard(aimHigh);
        assertTrue(s.gameState().hasPlayedStartingEpicEvent(s.LS));

        s.MoveOutOfPlay(communing);
        s.SkipToLSTurn(Phase.DEPLOY);
        s.EnsureLSForcePile(10);
        assertEquals(Zone.HAND, trust.getZone());
        assertFalse(s.LSDeployAvailable(current));
        assertTrue(s.LSDeployAvailable(trust));
    }
}
