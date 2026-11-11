package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Kessel Surveillance System PC-E2 (PC card list 2012-07-21 to 2013-01-11), a version of 1273_10 (legacy batch B41). */
public class Card_2273_1010_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("office", "1273_18");
            put("doole", "1273_9");
            put("operations", "1273_19");
            put("spice", "1_194");
            put("version", "2273_1010");
            put("current", "1273_10");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void CardListKesselSurveillanceSystemIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_1010"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_10"), "1273_10", "PC-E2",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25),
                "Deploy on a Kessel site. Opponents Force drains are -1 here. Oncer per turn, if you just lost a card from Reserve Deck, may place a 'spice' card in owners Lost Pile to satisfy 1 additional Force loss. Once per game, may deploy Spice Mine Administrator here.");
    }

    @Test
    public void CardListKesselSurveillanceSystemIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, true, "version", "current", (s, c) -> {
            MissionTestSupport.ensurePlanetLayout(s, "Kessel", 8); s.MoveLocationToTable(s.GetDSCard("office")); if (c != null) s.AttachCardsTo(s.GetDSCard("office"), c);
        });
    }

    @Test
    public void CardListKesselSurveillanceSystemSpiceMakesOneReserveDeckCardSatisfyTwoForceLoss() {
        var s = scenario();
        s.StartGame();
        MissionTestSupport.ensurePlanetLayout(s, "Kessel", 8);
        var office = s.GetDSCard("office");
        s.MoveLocationToTable(office);
        s.AttachCardsTo(office, s.GetDSCard("version"));
        var operations = s.GetDSCard("operations");
        var spice = s.GetDSCard("spice");
        s.MoveCardsToDSSideOfTable(operations);
        s.StackCardsOn(operations, spice);
        s.SkipToDSTurn(Phase.CONTROL);
        int reserveBefore = s.GetDSReserveDeckCount();
        int forcePileBefore = s.GetDSForcePileCount();
        s.DSExecuteAdHocEffect(office, new LoseForceEffect(new TopLevelGameTextAction(office, s.DS, office.getCardId()), s.DS, 2));
        com.gempukku.swccgo.cards.set2271.VersionAsserts.resolveUntil(s, () -> {
            var d = s.GetAwaitingDecision(s.DS);
            String[] texts = d == null ? null : d.getDecisionParameters().get("actionText");
            return texts != null && java.util.Arrays.stream(texts).anyMatch(t -> t != null && t.contains("Place a 'spice' card in Lost Pile"));
        });
        s.DSChooseAction("Place a 'spice' card in Lost Pile");
        com.gempukku.swccgo.cards.set2271.VersionAsserts.resolveUntil(s, () -> s.AwaitingDSControlPhaseActions());
        assertTrue(s.GetDSLostPile().contains(spice));
        assertEquals(reserveBefore - 1, s.GetDSReserveDeckCount());
        assertEquals(forcePileBefore, s.GetDSForcePileCount());
    }
}
