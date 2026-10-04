package com.gempukku.swccgo.game.formats;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SwccgFormatsJsonTests {
    @Test
    public void SwccgFormatsJsonParsesAndIncludesEwok() throws Exception {
        JSONParser parser = new JSONParser();
        try (InputStreamReader reader = new InputStreamReader(
                SwccgoFormatLibrary.class.getResourceAsStream("/swccgFormats.json"), StandardCharsets.UTF_8)) {
            JSONArray formats = (JSONArray) parser.parse(reader);
            JSONObject ewok = null;
            for (Object formatDefObj : formats) {
                JSONObject formatDef = (JSONObject) formatDefObj;
                if ("ewok".equals(formatDef.get("code"))) {
                    ewok = formatDef;
                    break;
                }
            }
            assertNotNull("Ewok format must parse from swccgFormats.json", ewok);
            assertEquals("Ewok", ewok.get("name"));
            assertNotNull(ewok.get("banned"));
            assertTrue(((JSONArray) ewok.get("banned")).size() > 0);
            assertNotNull(ewok.get("tenetsLink"));
        }
    }

    @Test
    public void PremiereAnhSealedUsesConstructedPremiereAnhSetPool() throws Exception {
        JSONParser parser = new JSONParser();
        try (InputStreamReader reader = new InputStreamReader(
                SwccgoFormatLibrary.class.getResourceAsStream("/swccgFormats.json"), StandardCharsets.UTF_8)) {
            JSONArray formats = (JSONArray) parser.parse(reader);
            JSONArray constructed = null;
            JSONArray sealed = null;
            for (Object formatDefObj : formats) {
                JSONObject formatDef = (JSONObject) formatDefObj;
                if ("premiere_anh".equals(formatDef.get("code"))) {
                    constructed = (JSONArray) formatDef.get("set");
                }
                if ("premiere_anh_sealed".equals(formatDef.get("code"))) {
                    sealed = (JSONArray) formatDef.get("set");
                }
            }
            assertNotNull("premiere_anh must exist", constructed);
            assertNotNull("premiere_anh_sealed must exist", sealed);
            assertEquals("Premiere-ANH Sealed must use the constructed Premiere-ANH set pool", constructed, sealed);
            assertEquals(5, sealed.size());
        }
    }
}
