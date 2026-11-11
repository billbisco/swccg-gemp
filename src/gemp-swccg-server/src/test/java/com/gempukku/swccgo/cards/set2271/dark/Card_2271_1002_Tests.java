package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Darth Vader, More Machine Than Man as on the PC card list (PC-E2, by 2012-07-21; Batch S1 date order), a version of 1271_2: same play as the current card, sentence order only. */
public class Card_2271_1002_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
        }}, new HashMap<>() {{
            put("rise", "1271_19");
            put("version", "2271_1002");
            put("current", "1271_2");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void CardListVaderIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2271_1002"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_2"), "1271_2", "PC-E2",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25),
                "Adds 3 to power of anything he pilots. [Vader] cards never deploy unless Rise Of The Dark Lord on table. Once per turn, if at a site you control, may apply his ability to draw battle destiny elsewhere. Immune to Clash Of Sabers and attrition < 5. May not be Disarmed.");
    }

    @Test
    public void CardListVaderIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, true, "version", "current", (s, c) -> {
            s.MoveCardsToDSSideOfTable(s.GetDSCard("rise")); if (c != null) s.MoveCardsToLocation(s.GetDSStartingLocation(), c);
        });
    }

    @Test
    public void CardListVaderMayNotBeDisarmedLikeTheCurrentCard() {
        var s = scenario();
        var vader = s.GetDSCard("version");
        s.StartGame();
        s.MoveCardsToDSSideOfTable(s.GetDSCard("rise"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), vader);
        s.SkipToDSTurn(Phase.CONTROL);
        var q = s.game().getModifiersQuerying();
        assertFalse(q.getModifiersAffectingCard(s.gameState(), com.gempukku.swccgo.logic.modifiers.ModifierType.MAY_NOT_BE_DISARMED, vader).isEmpty());
        assertTrue(q.isImmuneToCardTitle(s.gameState(), vader, Title.Clash_Of_Sabers));
        assertFalse(q.isImmuneToCardTitle(s.gameState(), vader, Title.Disarmed));
        assertEquals(5f, q.getImmunityToAttritionLessThan(s.gameState(), vader), s.epsilon);
    }
}
