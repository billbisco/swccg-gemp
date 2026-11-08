package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Pre-errata Force Push (V) (Dec 2009 sheet), a version of 1250_26: Out Of Play Interrupt, no once-per-game limit. */
public class Card_2250_026_Tests {
    private VirtualTableScenario scenario(String first, String second) {
        return new VirtualTableScenario(new HashMap<>(Map.of()),
                new HashMap<>(Map.of("first", first, "second", second, "current", "1250_26",
                        "a", "1_194", "b", "1_194", "c", "1_194", "d", "1_194")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ForcePushOriginalIdentity() {
        var scn = scenario("2250_26", "2250_26");
        assertLegacyVersion(scn.GetDSCard("first").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1250_26", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27),
                "During battle, use 2 Force to target your Dark Jedi and opponent's character of equal or lesser ability present. Both targets are excluded from battle. OR Exchange two cards from hand with any one card from Force Pile; reshuffle.");
    }

    private boolean secondExchangeAvailable(String first, String second) {
        var scn = scenario(first, second);
        var one = scn.GetDSCard("first");
        var two = scn.GetDSCard("second");
        scn.StartGame();
        scn.MoveCardsToDSHand(one, two, scn.GetDSCard("a"), scn.GetDSCard("b"), scn.GetDSCard("c"), scn.GetDSCard("d"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        assertTrue(scn.DSCardPlayAvailable(one, "Exchange"));
        scn.DSPlayCard(one, "Exchange");
        scn.PassAllResponses();
        for (int i = 0; i < 10 && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(scn.GetDSCard("a")))
                scn.DSChooseCard(scn.GetDSCard("a"));
            else if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(scn.GetDSCard("b")))
                scn.DSChooseCard(scn.GetDSCard("b"));
            else if (scn.DSAnyDecisionsAvailable() && !scn.DSGetCardChoices().isEmpty())
                scn.DSChoose(new String[]{scn.DSGetCardChoices().getFirst()});
            else if (scn.LSAnyDecisionsAvailable())
                scn.LSPass();
            else
                scn.PassAllResponses();
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        // a later turn (unique Interrupts aside), with two other cards in hand and a Force Pile
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToDSHand(two, scn.GetDSCard("c"), scn.GetDSCard("d"));
        scn.EnsureDSForcePile(5);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        return scn.DSActionAvailable("Exchange cards with card in Force Pile");
    }

    @Test
    public void ForcePushOriginalIsAnOutOfPlayInterrupt() {
        var scn = scenario("2250_26", "2250_26");
        var one = scn.GetDSCard("first");
        //test1: Out Of Play Interrupt (sheet slip); the current card is a Lost Interrupt
        //test2: placed out of play after use
        assertEquals(CardSubtype.OUT_OF_PLAY, one.getBlueprint().getCardSubtype()); //test1
        assertEquals(CardSubtype.LOST, scn.GetDSCard("current").getBlueprint().getCardSubtype());
        scn.StartGame();
        scn.MoveCardsToDSHand(one, scn.GetDSCard("a"), scn.GetDSCard("b"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        assertTrue(scn.DSCardPlayAvailable(one, "Exchange"));
        scn.DSPlayCard(one, "Exchange");
        scn.PassAllResponses();
        for (int i = 0; i < 10 && one.getZone() != Zone.OUT_OF_PLAY; i++) {
            if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(scn.GetDSCard("a")))
                scn.DSChooseCard(scn.GetDSCard("a"));
            else if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(scn.GetDSCard("b")))
                scn.DSChooseCard(scn.GetDSCard("b"));
            else if (scn.DSAnyDecisionsAvailable() && !scn.DSGetCardChoices().isEmpty())
                scn.DSChoose(new String[]{scn.DSGetCardChoices().getFirst()});
            else if (scn.LSAnyDecisionsAvailable())
                scn.LSPass();
            else
                scn.PassAllResponses();
        }
        assertEquals(Zone.OUT_OF_PLAY, one.getZone()); //test2
    }

    @Test
    public void ForcePushOriginalMayBePlayedTwiceInAGame() {
        assertTrue(secondExchangeAvailable("2250_26", "2250_26"));
    }

    @Test
    public void CurrentForcePushIsOncePerGame() {
        assertFalse(secondExchangeAvailable("1250_26", "1250_26"));
    }
}
