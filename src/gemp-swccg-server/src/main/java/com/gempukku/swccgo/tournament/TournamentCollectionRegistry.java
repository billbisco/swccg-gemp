package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.db.vo.CollectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TournamentCollectionRegistry {
    private final Map<String, CollectionType> _types = new ConcurrentHashMap<String, CollectionType>();
    private final Map<String, String> _players = new ConcurrentHashMap<String, String>();
    private final Map<String, String> _soloDraftTypes = new ConcurrentHashMap<String, String>();

    public void register(CollectionType collectionType, List<String> playerNames, String soloDraftType) {
        if (collectionType == null)
            return;
        _types.put(collectionType.getCode(), collectionType);
        if (soloDraftType != null)
            _soloDraftTypes.put(collectionType.getCode(), soloDraftType);
        if (playerNames != null) {
            for (String player : playerNames)
                _players.put(playerKey(collectionType.getCode(), player), player);
        }
    }

    public void addPlayer(String collectionCode, String playerName) {
        if (collectionCode != null && playerName != null)
            _players.put(playerKey(collectionCode, playerName), playerName);
    }

    public CollectionType get(String code) {
        return code == null ? null : _types.get(code);
    }

    public String getSoloDraftType(String code) {
        return code == null ? null : _soloDraftTypes.get(code);
    }

    public List<CollectionType> collectionsForPlayer(String playerName) {
        List<CollectionType> result = new ArrayList<CollectionType>();
        if (playerName == null)
            return result;
        for (CollectionType type : _types.values()) {
            if (_players.containsKey(playerKey(type.getCode(), playerName)))
                result.add(type);
        }
        return result;
    }

    private static String playerKey(String collectionCode, String playerName) {
        return collectionCode + "\n" + playerName;
    }
}
