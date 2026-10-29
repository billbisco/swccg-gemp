package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.google.gson.*;
import org.junit.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;
public class ReflectionsGoldDarkFinalStatsTests {
    @Test public void FinalTenDarkCardsMatchIndependentlyInspectedPrintedMetadata() throws Exception {
        var library=new SwccgCardBlueprintLibrary();
        try(var reader=new InputStreamReader(getClass().getResourceAsStream("/reflections-gold-dark-final.json"),StandardCharsets.UTF_8)) {
            var rows=JsonParser.parseReader(reader).getAsJsonArray(); assertEquals(10,rows.size());
            for(var item:rows) {
                var row=item.getAsJsonObject();String id=row.get("id").getAsString();var card=library.getSwccgoCardBlueprint(id);
                assertEquals(id,row.get("title").getAsString(),card.getTitle());
                assertEquals(id,java.util.Set.of(CardType.valueOf(row.get("type").getAsString())),card.getCardTypes());
                assertEquals(id,CardSubtype.valueOf(row.get("subtype").getAsString()),card.getCardSubtype());
                assertTrue(id,card.hasIcon(Icon.REFLECTIONS_GOLD));
                assertEquals(id,row.get("destiny").getAsFloat(),card.getDestiny(),0.001f); assertEquals(id,0,card.getCredits());
                assertEquals(id,Side.DARK,card.getSide()); assertEquals(id,Rarity.UNKNOWN,card.getRarity()); assertEquals(id,ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());
                assertEquals(id,"",card.getLore()); assertFalse(id,card.isHorizontal());
                assertEquals(id,row.get("gameText").getAsString(),card.getGameText());
                assertEquals(id,row.get("episodeI").getAsBoolean(),card.hasIcon(Icon.EPISODE_I));
                for(int i=0;i<2;i++) assertEquals(id,Uniqueness.valueOf(row.getAsJsonArray("components").get(i).getAsString()),card.getTitleComponents().get(i).uniqueness());
            }
        }
    }
}
