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
}
