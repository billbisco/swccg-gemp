package com.gempukku.swccgo.util;

import com.gempukku.swccgo.cards.AbstractEffect;
import com.gempukku.swccgo.common.*;
import com.google.gson.*;
import org.junit.Test;
import java.util.Set;
import static org.junit.Assert.*;

public class ComponentUniquenessExportTests {
    @Test public void MixedComponentsExportAdditivelyWithLegacyScalarUnchanged() {
        var blueprint = new AbstractEffect(Side.LIGHT, 3f, PlayCardZoneOption.YOUR_SIDE_OF_TABLE,
                "Projection & Yarna", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            { setTitleComponents(new TitleComponent("Projection", Uniqueness.RESTRICTED_2), new TitleComponent("Yarna", Uniqueness.UNIQUE)); }
        };
        var data = new CardDataExporter().extractCardData("2100_1", blueprint, Set.of());
        var json = JsonParser.parseString(new Gson().toJson(data)).getAsJsonObject();
        assertEquals("UNIQUE", json.get("uniqueness").getAsString());
        var components = json.getAsJsonArray("titleComponents");
        assertEquals(2, components.size());
        assertEquals("Projection", components.get(0).getAsJsonObject().get("name").getAsString());
        assertEquals("RESTRICTED_2", components.get(0).getAsJsonObject().get("uniqueness").getAsString());
        assertEquals("Yarna", components.get(1).getAsJsonObject().get("name").getAsString());
        assertEquals("UNIQUE", components.get(1).getAsJsonObject().get("uniqueness").getAsString());
    }
}
