package com.gempukku.swccgo.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// The class represents a library of all the Star Wars CCG cards
// available in Gemp-Swccg.
//
// One library is shared by every thread on the server, and blueprints are loaded lazily on first use,
// so the loaded blueprints are kept in concurrent maps and each id is built at most once. The alternate-id
// mappings are only written in the constructor and are final, so they are safely published.
//
public class SwccgCardBlueprintLibrary {
    private final String[] _packageNames =
            new String[]{
                    ".light", ".dark"
            };
    private final Map<String, SwccgCardBlueprint> _blueprintMap = new ConcurrentHashMap<String, SwccgCardBlueprint>();
    // Ids that have no card. ConcurrentHashMap can't hold null, so they are remembered here instead.
    private final Set<String> _unknownBlueprintIds = ConcurrentHashMap.newKeySet();
    // Reverse of _blueprintMap, so a save can refer to a blueprint by id instead of copying it.
    private Map<SwccgCardBlueprint, String> _blueprintIds = new IdentityHashMap<SwccgCardBlueprint, String>();

    private final Map<String, String> _blueprintMapping = new HashMap<String, String>();
    private final Map<String, Set<String>> _fullBlueprintMapping = new HashMap<String, Set<String>>();

    public SwccgCardBlueprintLibrary() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(SwccgCardBlueprintLibrary.class.getResourceAsStream("/blueprintMapping.txt"), "UTF-8"));
            try {
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    if (!line.startsWith("#")) {
                        String[] split = line.split(",");
                        _blueprintMapping.put(split[0], split[1]);
                        addAlternatives(split[0], split[1]);
                    }
                }
            } finally {
                bufferedReader.close();
            }
        } catch (IOException exp) {
            throw new RuntimeException("Problem loading blueprint mapping", exp);
        }
    }

    public String getBaseBlueprintId(String blueprintId) {
        blueprintId = stripBlueprintModifiers(blueprintId);
        String base = _blueprintMapping.get(blueprintId);
        if (base != null)
            return base;
        return blueprintId;
    }

    private void addAlternatives(String newBlueprint, String existingBlueprint) {
        Set<String> existingAlternates = _fullBlueprintMapping.get(existingBlueprint);
        if (existingAlternates != null) {
            for (String existingAlternate : existingAlternates) {
                addAlternative(newBlueprint, existingAlternate);
                addAlternative(existingAlternate, newBlueprint);
            }
        }
        addAlternative(newBlueprint, existingBlueprint);
        addAlternative(existingBlueprint, newBlueprint);
    }

    private void addAlternative(String from, String to) {
        Set<String> list = _fullBlueprintMapping.get(from);
        if (list == null) {
            list = new HashSet<String>();
            _fullBlueprintMapping.put(from, list);
        }
        list.add(to);
    }

    public Set<String> getAllAlternates(String blueprintId) {
        return _fullBlueprintMapping.get(blueprintId);
    }

    public boolean hasAlternateInSet(String blueprintId, int setNo) {
        Set<String> alternatives = _fullBlueprintMapping.get(blueprintId);
        if (alternatives != null)
            for (String alternative : alternatives)
                if (alternative.startsWith(setNo + "_"))
                    return true;

        return false;
    }

    public SwccgCardBlueprint getSwccgoCardBlueprint(String blueprintId) {
        try {
            int previousLength = blueprintId.length();
            blueprintId = stripBlueprintModifiers(blueprintId);

            // fail if there are multiple suffixes
            if (blueprintId.length()<previousLength-1)
                return null;

            return getOrLoadBlueprint(blueprintId);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SwccgCardBlueprint getSwccgoCardBlueprintBack(String blueprintId) {
        try {
            blueprintId = stripBlueprintModifiers(blueprintId);

            if (!blueprintId.contains("_BACK")) {
                blueprintId = blueprintId + "_BACK";
            }

            return getOrLoadBlueprint(blueprintId);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SwccgCardBlueprint getOrLoadBlueprint(String blueprintId) {
        SwccgCardBlueprint blueprint = _blueprintMap.get(blueprintId);
        if (blueprint != null)
            return blueprint;
        if (_unknownBlueprintIds.contains(blueprintId))
            return null;

        // Builds the blueprint once even if several threads ask for it at the same time.
        // getBlueprint never touches _blueprintMap, so this compute never recurses into the map.
        blueprint = _blueprintMap.computeIfAbsent(blueprintId, this::getBlueprint);
        if (blueprint == null)
            _unknownBlueprintIds.add(blueprintId);
        else
            _blueprintIds.put(blueprint, blueprintId);
        return blueprint;
    }

    /**
     * Gets the id a blueprint was loaded under.
     * @param blueprint a blueprint returned by this library
     * @return the blueprint id (ending in _BACK for a back side), or null if this library did not load it
     */
    public String getLoadedBlueprintId(SwccgCardBlueprint blueprint) {
        return _blueprintIds.get(blueprint);
    }

    public String stripBlueprintModifiers(String blueprintId) {
        return blueprintId.replaceAll("\\*", "").replaceAll("\\^", "");
    }

    public boolean isFoil(String blueprintId) {
        return blueprintId.contains("*");
    }

    public boolean isAlternateImage(String blueprintId) {
        return blueprintId.contains("^");
    }

    private SwccgCardBlueprint getBlueprint(String blueprintId) {
        if (_blueprintMapping.containsKey(blueprintId))
            return getBlueprint(_blueprintMapping.get(blueprintId));

        String[] blueprintParts = blueprintId.split("_");

        String setNumber = blueprintParts[0];
        String cardNumber = stripBlueprintModifiers(blueprintParts[1]);
        String backIndicator = blueprintParts.length > 2 ? blueprintParts[2] : null;

        for (String packageName : _packageNames) {
            SwccgCardBlueprint blueprint = null;
            try {
                blueprint = tryLoadingFromPackage(packageName, setNumber, cardNumber, backIndicator);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new RuntimeException("Problem loading blueprint mapping for " + packageName + "," + setNumber + "," + cardNumber + "," + backIndicator);
            }
            if (blueprint != null)
                return blueprint;
        }

        return null;

        // TODO: throw new IllegalArgumentException("Didn't find card with blueprintId: " + blueprintId);
    }

    private SwccgCardBlueprint tryLoadingFromPackage(String packageName, String setNumber, String cardNumber, String backIndicator) throws IllegalAccessException, InstantiationException {
        try {
            String clazzName = "com.gempukku.swccgo.cards.set" + setNumber + packageName + ".Card" + setNumber + "_" + normalizeId(cardNumber);
            if (backIndicator != null && !backIndicator.isEmpty()) {
                clazzName = clazzName + "_" + backIndicator;
            }
            Class clazz = Class.forName(clazzName);
            return (SwccgCardBlueprint) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            // Ignore
            return null;
        }
    }

    private String normalizeId(String blueprintPart) {
        int id = Integer.parseInt(blueprintPart);
        if (id < 10)
            return "00" + id;
        else if (id < 100)
            return "0" + id;
        else
            return String.valueOf(id);
    }
}
