package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

/** Original Abyssin Ornament & Wounded Wookiee (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_40. */
public class Card_2230_040_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("version", "2230_40");
                    put("current", "1230_40");
                }},
                20, 20,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void AbyssinOrnamentAndWoundedWookieeIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("version").getBlueprint(),
                "USED: Creature attacks at pits are immune to At Peace and Yarna d'al' Gargan until end of your next turn. (Immune to Sense.) OR Cancel an attempt to target a rancor with a character weapon. OR Use 1 Force (2 if a rancor on table) to deploy Nal Hutta from Reserve Deck; reshuffle. STARTING: If Great Pit Of Carkoon on table, deploy from Reserve Deck an Effect that is always [Immune to Alter] and choose: deploy from Reserve Deck Rancor Pit and a rancor there, OR deploy from Reserve Deck Power Of The Hutt and Sarlacc. Place this Interrupt in hand.",
                "1230_40", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }
}
