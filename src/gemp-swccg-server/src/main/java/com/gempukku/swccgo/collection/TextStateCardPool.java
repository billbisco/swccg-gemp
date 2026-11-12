package com.gempukku.swccgo.collection;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Older text states of printings (2_45o, 2_45d; docs/legacy-versions.md, "Text states") in the card pool.
 * The ids are listed in textStates.txt (cards module resources), so the pool does not probe every card number for
 * every state letter.
 */
public final class TextStateCardPool {
    private static final String RESOURCE = "/textStates.txt";
    private static volatile List<String> _listedIds;

    private TextStateCardPool() {
    }

    /** The text-state ids listed in textStates.txt, in file order. */
    public static List<String> listedIds() {
        List<String> ids = _listedIds;
        if (ids != null)
            return ids;
        List<String> read = new ArrayList<>();
        InputStream in = TextStateCardPool.class.getResourceAsStream(RESOURCE);
        if (in != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#"))
                        continue;
                    if (!TextState.isTextStateBlueprint(line))
                        throw new IllegalStateException(RESOURCE + " lists an id that is not a text state: " + line);
                    read.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException("Problem reading " + RESOURCE, e);
            }
        }
        _listedIds = Collections.unmodifiableList(read);
        return _listedIds;
    }

    /** Listed text states that load a card (an alias line in blueprintMapping.txt is not listed separately). */
    public static List<String> implementedIds(SwccgCardBlueprintLibrary library) {
        List<String> ids = new ArrayList<>();
        for (String id : listedIds()) {
            if (library.getBaseBlueprintId(id).equals(id) && library.getSwccgoCardBlueprint(id) != null)
                ids.add(id);
        }
        return ids;
    }

    public static void addCards(SwccgCardBlueprintLibrary library, DefaultCardCollection collection) {
        for (String id : implementedIds(library))
            collection.addItem(id, 60);
    }

    public static void appendValidBlueprints(SwccgCardBlueprintLibrary library, SwccgFormat format,
                                             List<SwccgCardBlueprint> blueprints) {
        for (String id : implementedIds(library)) {
            try {
                format.validateCard(id, true);
                blueprints.add(library.getSwccgoCardBlueprint(id));
            } catch (DeckInvalidException excludedFromFormat) {
                // Text states are only in formats that allow them (Anything Goes)
            }
        }
    }
}
