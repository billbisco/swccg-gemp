package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Original Darth Vader, More Machine Than Man (Jun 2010 slip: immune to Disarmed), a version of 1271_2. */
public class Card_2271_002_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("vader", "2271_2");
            put("current", "1271_2");
            put("rise", "1271_19");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalVaderIsTheCurrentCardWithTheJune2010Text() {
        var s = scenario();
        assertVersion(s.GetDSCard("vader").getBlueprint(), s.GetDSCard("current").getBlueprint(), "1271_2", "",
                LocalDate.of(2010, 6, 18), LocalDate.of(2011, 3, 21),
                "Adds 3 to power of anything he pilots. [Vader] cards never deploy unless Rise Of The Dark Lord on table. Once per turn, if at a site you control, may apply his ability to draw battle destiny elsewhere. Immune to Clash Of Sabers, Disarmed, and attrition < 5.");
    }

    @Test
    public void OriginalVaderIsTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : List.of(true, false)) {
            for (boolean otherOut : List.of(false, true)) {
                var s = scenario();
                var onTable = s.GetDSCard(currentOnTable ? "current" : "vader");
                var inHand = s.GetDSCard(currentOnTable ? "vader" : "current");
                s.StartGame();
                s.MoveCardsToDSSideOfTable(s.GetDSCard("rise"));
                if (otherOut)
                    s.MoveCardsToLocation(s.GetDSStartingLocation(), onTable);
                s.MoveCardsToDSHand(inHand);
                s.SkipToDSTurn(Phase.DEPLOY);
                s.EnsureDSForcePile(10);
                assertEquals("current on table=" + currentOnTable + ", other out=" + otherOut, !otherOut, s.DSDeployAvailable(inHand));
            }
        }
    }

    @Test
    public void OriginalVaderIsImmuneToTheCardDisarmedButMayBeDisarmedByOtherCards() {
        var s = scenario();
        var vader = s.GetDSCard("vader");
        var current = s.GetDSCard("current");
        s.StartGame();
        s.MoveCardsToDSSideOfTable(s.GetDSCard("rise"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), vader);
        s.SkipToDSTurn(Phase.CONTROL);
        var q = s.game().getModifiersQuerying();
        assertTrue(q.isImmuneToCardTitle(s.gameState(), vader, Title.Disarmed));
        assertTrue(q.isImmuneToCardTitle(s.gameState(), vader, Title.Clash_Of_Sabers));
        assertEquals(5f, q.getImmunityToAttritionLessThan(s.gameState(), vader), s.epsilon);
        // the Master List's "May not be Disarmed" (any card) is not on the original
        assertTrue(q.getModifiersAffectingCard(s.gameState(), ModifierType.MAY_NOT_BE_DISARMED, vader).isEmpty());

        s.MoveCardsToDSHand(vader);
        s.MoveCardsToLocation(s.GetDSStartingLocation(), current);
        assertFalse(q.getModifiersAffectingCard(s.gameState(), ModifierType.MAY_NOT_BE_DISARMED, current).isEmpty());
        assertFalse(q.isImmuneToCardTitle(s.gameState(), current, Title.Disarmed));
    }
}
