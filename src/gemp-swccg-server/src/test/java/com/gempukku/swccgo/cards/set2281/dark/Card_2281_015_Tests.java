package com.gempukku.swccgo.cards.set2281.dark;

import org.junit.Test;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;

/** Well Guarded: original version of 1281_15 (Virtual Block 8.1 release sheet (2012-05-11)). Anything Goes only. */
public class Card_2281_015_Tests {
    @Test
    public void WellGuardedVersionIdentityAndText() {
        assertVersion("2281_15", "1281_15", "Well Guarded",
                "Deploy on table. Your non-unique aliens are immune to Grimtaash (while you have < 13 cards in hand), The Camp, and deployment costs on Watch Your Step. During battle with two non-unique aliens of your Rep's species, your aliens there may not have their forfeit reduced. (Immune to Alter.)",
                "", java.time.LocalDate.of(2012, 5, 11), java.time.LocalDate.of(2012, 7, 21));
        assertSameCard("2281_15", "1281_15");
    }

    /** The original says "Your non-unique aliens": opponent's non-unique aliens are not immune. */
    @Test
    public void WellGuardedOriginalOpponentsNonUniqueAliensAreNotImmune() {
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(
                new java.util.HashMap<>() {{
                    put("jawa", "1_12");
                }},
                new java.util.HashMap<>() {{
                    put("wellguarded", "2281_15");
                    put("guard", "6_105");
                }},
                20, 20,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var jawa = scn.GetLSCard("jawa");
        var guard = scn.GetDSCard("guard");
        scn.StartGame();
        scn.MoveCardsToLSHand(jawa);
        scn.MoveCardsToDSHand(guard);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("wellguarded"));
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.DEPLOY);
        var query = scn.game().getModifiersQuerying();
        org.junit.Assert.assertTrue(query.isImmuneToCardTitle(scn.gameState(), guard, com.gempukku.swccgo.common.Title.The_Camp));
        org.junit.Assert.assertFalse(query.isImmuneToCardTitle(scn.gameState(), jawa, com.gempukku.swccgo.common.Title.The_Camp));
    }
}
