package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_112_Tests {
    @Test public void generatorsUseTheFinalLocationPrinting() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("generators", "1261_112")), new HashMap<>(),
                40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        var b = scn.GetLSCard("generators").getBlueprint();
        assertEquals(Title.Main_Power_Generators, b.getTitle());
        assertEquals(Title.Hoth, b.getSystemName());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        assertEquals(CardSubtype.SITE, b.getCardSubtype()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(Rarity.V, b.getRarity()); assertEquals(Side.LIGHT, b.getSide()); assertTrue(b.hasVirtualSuffix());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.LOCATION)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.LIGHT_FORCE, Icon.HOTH, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.MARKER_1)));
    }

}
