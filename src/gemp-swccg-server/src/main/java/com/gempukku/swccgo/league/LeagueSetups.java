package com.gempukku.swccgo.league;

import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.game.setup.GameSetup;

import java.util.List;

/** The game setup of a league serie (swccg-guild/work#119): its stored options, or what its format code implies. */
public final class LeagueSetups {
    private LeagueSetups() {
    }

    public static GameSetup forSerie(SwccgoFormatLibrary formats, League league, LeagueSeriesData serie,
            SoloDraftDefinitions soloDraftDefinitions) {
        String json = null;
        if (league != null && serie != null && league.getGameSetup() != null) {
            try {
                json = league.getSerieSetupJson(indexOf(league.getLeagueData(soloDraftDefinitions).getSeries(), serie));
            } catch (RuntimeException exp) {
                json = null;
            }
        }
        return formats.storedSetup(serie != null ? serie.getFormat() : null, json);
    }

    /** A serie's place in its league; league data may be rebuilt, so match by name, start and format, not identity. */
    static int indexOf(List<LeagueSeriesData> series, LeagueSeriesData serie) {
        for (int i = 0; i < series.size(); i++) {
            LeagueSeriesData candidate = series.get(i);
            if (candidate == serie || (candidate.getStart() == serie.getStart()
                    && java.util.Objects.equals(candidate.getName(), serie.getName())
                    && java.util.Objects.equals(candidate.getFormat(), serie.getFormat())))
                return i;
        }
        return -1;
    }

    /** The setup of the serie at {@code index}, when the caller already walks the series. */
    public static GameSetup forSerie(SwccgoFormatLibrary formats, League league, LeagueSeriesData serie, int index) {
        String json = null;
        try {
            json = league != null ? league.getSerieSetupJson(index) : null;
        } catch (RuntimeException exp) {
            json = null;
        }
        return formats.storedSetup(serie.getFormat(), json);
    }
}
