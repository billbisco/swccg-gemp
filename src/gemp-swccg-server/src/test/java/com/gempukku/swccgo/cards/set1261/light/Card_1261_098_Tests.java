package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1261_098_Tests extends SoloDefenseTestSupport {
    public Card_1261_098_Tests() { super("1261_98", "5_5", "1_11", "5_79"); }

    @Test public void exactFinalLegacyInterrupt() {
        var scn = fixture(); var b = scn.GetLSCard("interrupt").getBlueprint();
        assertEquals("Out Of Somewhere", b.getTitle()); assertEquals(4, b.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(CardSubtype.LOST, b.getCardSubtype()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.LEGACY_BLOCK_6, Icon.CLOUD_CITY)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @org.junit.Test
    public void CurrentTextIsTheLatestVersion() {
        // The release sheet text is the latest version: the PC card list only spelled out the icons (audit 2026-11-08), so it is no version. GEMP's wording before r16 matched no published version.
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(java.util.Map.of("c", "1261_98")), new java.util.HashMap<>(), 10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields, com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var text = scn.GetLSCard("c").getBlueprint().getGameText();
        org.junit.Assert.assertEquals("Use 1 Force to ▼ (or retrieve) Lando. OR If a battle just initiated, and your Lando defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)", text);
        org.junit.Assert.assertFalse(text.contains("your Lando is defending"));
        org.junit.Assert.assertNull(scn.GetLSCard("c").getBlueprint().getLegacyVersion());
    }
}
