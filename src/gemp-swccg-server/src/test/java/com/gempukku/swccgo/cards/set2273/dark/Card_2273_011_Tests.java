package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Original Trophy Of A Kill (Jun 2011 slip: "Deploy on Cyborg Commander, Galen, or ..."), a version of 1273_11. */
public class Card_2273_011_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("trophy", "2273_11");
            put("current", "1273_11");
            put("cyborgCommander", "1271_1");
            put("generalGrievous", "203_27");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalTrophyOfAKillIsTheCurrentCardWithTheJune2011Text() {
        var s = scenario();
        assertVersion(s.GetDSCard("trophy").getBlueprint(), s.GetDSCard("current").getBlueprint(), "1273_11", "",
                LocalDate.of(2011, 6, 3), LocalDate.of(2013, 4, 27),
                "Deploy on Cyborg Commander, Galen, or your female of ability > 3. Character is immunity to attrition +2 (and power +2 if female). May add 1 to Force drain where present. This device lost if character Disarmed.");
    }

    private boolean mayDeployOn(String trophyKey, String grievousKey) {
        var s = scenario();
        var trophy = s.GetDSCard(trophyKey);
        var grievous = s.GetDSCard(grievousKey);
        s.StartGame();
        s.MoveCardsToLocation(s.GetDSStartingLocation(), grievous);
        s.MoveCardsToDSHand(trophy);
        s.SkipToDSTurn(Phase.DEPLOY);
        s.EnsureDSForcePile(10);
        boolean available = s.DSDeployAvailable(trophy);
        if (available) {
            s.DSDeployCard(trophy);
            if (s.DSHasCardChoiceAvailable(grievous)) s.DSChooseCard(grievous);
            s.PassAllResponses();
            assertEquals(grievous, trophy.getAttachedTo());
        }
        return available;
    }

    @Test
    public void OriginalTrophyOfAKillDeploysOnCyborgCommander() {
        assertTrue(mayDeployOn("trophy", "cyborgCommander"));
    }

    @Test
    public void OriginalTrophyOfAKillDoesNotDeployOnAnotherGrievous() {
        // Cyborg Commander is only the card printed with that title (Grievous, Hunter Of Jedi), not every Grievous
        assertFalse(mayDeployOn("trophy", "generalGrievous"));
        assertTrue(mayDeployOn("current", "generalGrievous"));
    }
}
