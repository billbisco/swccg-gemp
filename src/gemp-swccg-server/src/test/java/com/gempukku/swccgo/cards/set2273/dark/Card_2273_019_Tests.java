package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original Spice Mine Operations (2011-06-03 sheet), a version of 1273_19: deploys if your [Virtual Block 7] office on table. */
public class Card_2273_019_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
        }}, new HashMap<>() {{
            put("office", "1273_18");
            put("corulag", "1272_11");
            put("version", "2273_19");
            put("current", "1273_19");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalSpiceMineOperationsIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_19"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_19"), "1273_19", "",
                LocalDate.of(2011, 6, 3), LocalDate.of(2012, 7, 21),
                "If your [Virtual Block 7] office on table, deploy on table. Program Trap is canceled. Cards stacked here are 'spice' cards. Once per turn, if you control Extraction Facility, may place a 'spice' card in owner's Lost Pile to draw up to 2 cards from Reserve Deck. Once during your turn, may: use 2 Force to stack a card from Lost Pile here, [download] a non-character card with \"Kessel\" in title, and/or 'mine' (opponent draws destiny; draw up to X destiny, where X = 1 plus number of Kessel locations you occupy; if any of the printed destiny numbers match, stack opponent's destiny or top card of your Lost Pile here).");
    }

    @Test
    public void OriginalSpiceMineOperationsIsTheSameUniqueCardAsTheCurrentCard() {
        assertSameUniqueCard(this::scenario, true, "version", "current", (s, c) -> {
            com.gempukku.swccgo.cards.legacy.MissionTestSupport.ensurePlanetLayout(s, "Kessel", 8); s.MoveLocationToTable(s.GetDSCard("office")); if (c != null) s.MoveCardsToDSSideOfTable(c);
        });
    }

    private boolean deployAvailable(String key, String officeKey) {
        var s = scenario();
        s.StartGame();
        if ("office".equals(officeKey))
            com.gempukku.swccgo.cards.legacy.MissionTestSupport.ensurePlanetLayout(s, "Kessel", 8);
        if (officeKey != null)
            s.MoveLocationToTable(s.GetDSCard(officeKey));
        s.MoveCardsToHand(s.GetDSCard(key));
        s.EnsureDSForcePile(10);
        s.SkipToDSTurn(Phase.DEPLOY);
        return s.DSDeployAvailable(s.GetDSCard(key));
    }

    @Test
    public void OriginalSpiceMineOperationsDeploysWithAnyVirtualBlock7OfficeButTheCurrentCardNeedsAKesselOffice() {
        assertTrue(s(Icon.LEGACY_BLOCK_7));
        assertTrue(deployAvailable("version", "corulag"));
        assertFalse(deployAvailable("current", "corulag"));
        assertTrue(deployAvailable("version", "office"));
        assertTrue(deployAvailable("current", "office"));
        assertFalse(deployAvailable("version", null));
    }

    private boolean s(Icon icon) {
        return VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1272_11").hasIcon(icon);
    }
}
