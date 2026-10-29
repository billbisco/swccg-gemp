package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.StartingCredits;
import com.gempukku.swccgo.hall.AwaitingTable;
import com.gempukku.swccgo.game.save.GameSaveHeader;
import org.junit.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import static org.junit.Assert.*;

public class StartingCreditsFormatTests {
    @Test public void ReflectionsGoldFormatUsesExactlyDecipherPoolPlusGoldAndExplicitCapability() throws Exception {
        try(var reader=new InputStreamReader(getClass().getResourceAsStream("/swccgFormats.json"),StandardCharsets.UTF_8)) {
            var defs=(JSONArray)new JSONParser().parse(reader);
            Map<String,JSONObject> byCode=new HashMap<>();
            for(Object value:defs) { var def=(JSONObject)value;byCode.put((String)def.get("code"),def); }
            var original=new HashSet<>((JSONArray)byCode.get("open_no_virtual").get("set"));original.add(15L);
            assertEquals(original,new HashSet<>((JSONArray)byCode.get("premiere_reflections_gold").get("set")));
            assertEquals(Boolean.TRUE,byCode.get("premiere_reflections_gold").get("startingCredits"));
            assertEquals(Boolean.TRUE,byCode.get("anything_goes").get("startingCredits"));
            assertNull(byCode.get("open").get("startingCredits"));
            assertNull(byCode.get("open_no_virtual").get("startingCredits"));
        }
    }
    @Test public void StartingCreditsServerValidatesDefaultsFourFiveZeroAndCustomBudget() {
        var lib=new SwccgoFormatLibrary(VirtualTableScenario._cardLibrary);
        var rg=lib.getFormat("premiere_reflections_gold");var open=lib.getFormat("open");
        assertEquals(5,StartingCredits.resolve(rg,null));assertEquals(0,StartingCredits.resolve(open,null));
        for(String value:List.of("0","4","5","60")) assertEquals(Integer.parseInt(value),StartingCredits.resolve(rg,value));
        for(String value:List.of("-1","61","1.5","five"," 5","1000000000000","+4"))
            assertThrows(IllegalArgumentException.class,()->StartingCredits.resolve(rg,value));
        assertThrows(IllegalArgumentException.class,()->StartingCredits.resolve(open,"5"));
        assertThrows(IllegalArgumentException.class,()->StartingCredits.resolve(open,"0"));
        var table=new AwaitingTable(rg,null,null,null,"test",false);
        assertEquals(5,table.getStartingCredits());table.setStartingCredits("4");assertEquals(4,table.getStartingCredits());
        assertEquals(5,new AwaitingTable(lib.getFormat("anything_goes"),null,null,null,"league default",false).getStartingCredits());
    }
    @Test public void StartingCreditsSaveMetadataRoundTripsAndOlderHeadersDefaultToZero() {
        var header=new GameSaveHeader();header.startingCredits=4;
        assertEquals(4,GameSaveHeader.fromJson(header.toJson()).startingCredits);
        assertEquals(0,GameSaveHeader.fromJson("{\"version\":1,\"seats\":[]}").startingCredits);
    }
}
