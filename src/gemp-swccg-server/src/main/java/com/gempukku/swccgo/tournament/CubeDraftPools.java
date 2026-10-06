package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.MutableCardCollection;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class CubeDraftPools {
    public final List<String> lightCards = new ArrayList<String>();
    public final List<String> darkCards = new ArrayList<String>();
    public final List<String> coreCards = new ArrayList<String>();
    public final List<String> lightObjCards = new ArrayList<String>();
    public final List<String> darkObjCards = new ArrayList<String>();

    public static CubeDraftPools load(String draftType) {
        CubeDraftPools pools = new CubeDraftPools();
        if (draftType == null)
            return pools;
        try {
            JSONParser parser = new JSONParser();
            InputStreamReader indexReader = new InputStreamReader(
                    SwccgoFormatLibrary.class.getResourceAsStream("/swccgDrafts.json"), "UTF-8");
            JSONArray index = (JSONArray) parser.parse(indexReader);
            indexReader.close();
            String location = null;
            for (Object entryObj : index) {
                JSONObject entry = (JSONObject) entryObj;
                if (draftType.equals(entry.get("type"))) {
                    location = (String) entry.get("location");
                    break;
                }
            }
            if (location == null)
                return pools;
            InputStreamReader draftReader = new InputStreamReader(
                    SwccgoFormatLibrary.class.getResourceAsStream(location), "UTF-8");
            JSONObject draft = (JSONObject) parser.parse(draftReader);
            draftReader.close();
            addAll(pools.lightObjCards, draft.get("lightObjCards"));
            addAll(pools.darkObjCards, draft.get("darkObjCards"));
            JSONObject startingPool = (JSONObject) draft.get("startingPool");
            if (startingPool != null) {
                JSONObject data = (JSONObject) startingPool.get("data");
                if (data != null)
                    addAll(pools.coreCards, data.get("coreCards"));
            }
            JSONArray choices = (JSONArray) draft.get("choices");
            boolean firstPackPick = true;
            if (choices != null) {
                for (Object choiceObj : choices) {
                    JSONObject choice = (JSONObject) choiceObj;
                    String type = String.valueOf(choice.get("type"));
                    JSONObject data = (JSONObject) choice.get("data");
                    if (data == null)
                        continue;
                    if ("cubePackPick".equals(type)) {
                        if (firstPackPick) {
                            addAll(pools.lightCards, data.get("availableCards"));
                            firstPackPick = false;
                        } else if (pools.darkCards.isEmpty())
                            addAll(pools.darkCards, data.get("availableCards"));
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return pools;
    }

    public MutableCardCollection startingKeep() {
        MutableCardCollection keep = new DefaultCardCollection();
        for (String card : coreCards)
            keep.addItem(card, 1);
        return keep;
    }

    private static void addAll(List<String> target, Object raw) {
        if (!(raw instanceof JSONArray))
            return;
        for (Object item : (JSONArray) raw) {
            if (item != null)
                target.add(String.valueOf(item));
        }
    }
}
