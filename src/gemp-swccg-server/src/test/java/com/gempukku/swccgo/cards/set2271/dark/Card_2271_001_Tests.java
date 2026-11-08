package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.assertEquals;

/** Original Grievous, Hunter Of Jedi (Jun 2010 slip, printed as Cyborg Commander), a version of 1271_1. */
public class Card_2271_001_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("obi", "1_21");
        }}, new HashMap<>() {{
            put("grievous", "2271_1");
            put("current", "1271_1");
            put("blade", "1250_32");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalGrievousIsTheCurrentCardWithTheJune2010Text() {
        var s = scenario();
        assertVersion(s.GetDSCard("grievous").getBlueprint(), s.GetDSCard("current").getBlueprint(), "1271_1", "",
                LocalDate.of(2010, 6, 18), LocalDate.of(2013, 4, 27),
                "[Virtual Block 5] Dark Jedi Lightsaber may deploy on Cyborg Commander. Jedi present are power -1 (-2 if a Jedi Council Member). While armed with a lightsaber, your battle destiny draws here are +1 and his forfeit may not be reduced. Immune to attrition < 4.");
    }

    @Test
    public void OriginalGrievousIsTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : List.of(true, false)) {
            var s = scenario();
            var onTable = s.GetDSCard(currentOnTable ? "current" : "grievous");
            var inHand = s.GetDSCard(currentOnTable ? "grievous" : "current");
            s.StartGame();
            s.MoveCardsToLocation(s.GetDSStartingLocation(), onTable);
            s.MoveCardsToDSHand(inHand);
            s.SkipToDSTurn(Phase.DEPLOY);
            s.EnsureDSForcePile(10);
            assertEquals(false, s.DSDeployAvailable(inHand));
        }
    }

    @Test
    public void OriginalGrievousPlaysLikeTheCurrentCard() {
        // Cyborg Commander was this card's own printed title: same play as the Master List's "Grievous".
        var s = scenario();
        var grievous = s.GetDSCard("grievous");
        var blade = s.GetDSCard("blade");
        var obi = s.GetLSCard("obi");
        s.StartGame();
        s.SkipToDSTurn(Phase.CONTROL);
        s.MoveCardsToLocation(s.GetLSStartingLocation(), grievous, obi);
        s.MoveCardsToDSHand(blade);
        s.EnsureDSForcePile(10);
        s.SkipToPhase(Phase.DEPLOY);
        assertEquals(4, s.GetPower(obi));
        s.DSDeployCard(blade);
        s.PassAllResponses();
        if (blade.getAttachedTo() == null) {
            s.DSChooseCard(grievous);
            s.PassAllResponses();
        }
        assertEquals(grievous, blade.getAttachedTo());
        assertEquals(4, s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(), grievous), s.epsilon);
    }
}
