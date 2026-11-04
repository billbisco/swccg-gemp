package com.gempukku.swccgo.db;

import java.util.Map;

/**
 * Game settings a player saved per setup form and format (swccg-guild/work#135): the options the Play form or the
 * Create Tournament form starts from when that format is picked. Stored on the account so they follow the player.
 */
public interface PlayerGameSettingsDAO {
    /** The player's saved settings, keyed {@code "<form>|<format>"}, each a JSON object of option id to value. */
    Map<String, String> load(int playerId);

    /** Saves one form and format; {@code optionsJson} null or {@code "{}"} removes it. */
    void save(int playerId, String form, String format, String optionsJson);
}
