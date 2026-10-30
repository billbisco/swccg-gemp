package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.google.gson.JsonParser;
import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.HexFormat;
import static org.junit.Assert.*;

public class ReflectionsGoldCompleteRegistrationTests {
    @Test public void All310StableIdsResolveToRegisteredBlueprintsAndCanonicalImages() throws Exception {
        var rows=JsonParser.parseString(Files.readString(Path.of("../../docs/reflections-gold/inventory.json"))).getAsJsonArray();
        assertEquals(310,rows.size());
        var library=new SwccgCardBlueprintLibrary();
        var rarity=new RarityReader().getSetRarity("15");
        var images=Files.readString(Path.of("../gemp-swccg-async/src/main/web/js/gemp-016/cards/CardImages.js"));
        var expectedIds=new HashSet<String>();
        for(int i=1;i<=310;i++)expectedIds.add("15_"+i);
        var actualIds=new HashSet<String>();
        for(var item:rows){
            var row=item.getAsJsonObject();String id=row.get("id").getAsString();
            assertTrue("Duplicate inventory ID "+id,actualIds.add(id));
            assertTrue(id,row.get("implemented").getAsBoolean());
            var card=library.getSwccgoCardBlueprint(id);
            assertNotNull(id,card);assertEquals(id,ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());
            assertEquals(id,Rarity.UNKNOWN,rarity.getCardRarity(id));
            if (!row.has("credits")) {
                // Foundation inventory omits zero credits on the intentional blank cards.
                assertTrue(id,row.get("intentionalBlank").getAsBoolean());
                assertEquals(id,0,card.getCredits());
            } else {
                assertEquals(id,row.get("credits").getAsInt(),card.getCredits());
            }
            String filename=row.get("image").getAsString();
            String hash=HexFormat.of().formatHex(MessageDigest.getInstance("MD5").digest(filename.getBytes(StandardCharsets.UTF_8)));
            String url="https://wiki.swccg.com/images/"+hash.substring(0,1)+"/"+hash.substring(0,2)+"/"+filename;
            assertTrue(id,images.contains("\""+id+"\":\""+url+"\""));
        }
        assertEquals(expectedIds,actualIds);
    }
}
