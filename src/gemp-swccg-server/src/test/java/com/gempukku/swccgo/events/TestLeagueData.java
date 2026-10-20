package com.gempukku.swccgo.events;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.competitive.PlayerStanding;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.draft2.SoloDraft;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.league.LeagueData;
import com.gempukku.swccgo.league.LeagueSeriesData;

import java.util.ArrayList;
import java.util.List;

/**
 * A LeagueData for the Events tab tests that needs no card library or prize pools: its parameters are one serie
 * per "start:end:format" group separated by ";" (days as yyyyMMdd).  League.getLeagueData builds it by reflection
 * from the league row's class name, exactly as it builds the real ones.
 */
public class TestLeagueData implements LeagueData {
    private final List<LeagueSeriesData> _series = new ArrayList<>();

    public TestLeagueData(SwccgCardBlueprintLibrary library, SoloDraftDefinitions soloDraftDefinitions, String parameters) {
        int index = 0;
        for (String serie : parameters.split(";")) {
            String[] parts = serie.split(":");
            if (parts.length != 3)
                throw new IllegalArgumentException("Not a test serie: " + serie);
            _series.add(new Serie("Serie " + (++index), Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2]));
        }
    }

    @Override
    public List<LeagueSeriesData> getSeries() {
        return _series;
    }

    @Override
    public SoloDraft getSoloDraft() {
        return null;
    }

    @Override
    public boolean isSoloDraftLeague() {
        return false;
    }

    @Override
    public CardCollection joinLeague(CollectionsManager collectionsManager, Player player, int currentTime) {
        return null;
    }

    @Override
    public int process(CollectionsManager collectionsManager, List<PlayerStanding> leagueStandings, int oldStatus, int currentTime) {
        return oldStatus;
    }

    @Override
    public boolean isSealed() {
        return false;
    }

    private static class Serie implements LeagueSeriesData {
        private final String _name;
        private final int _start;
        private final int _end;
        private final String _format;

        Serie(String name, int start, int end, String format) {
            _name = name;
            _start = start;
            _end = end;
            _format = format;
        }

        @Override
        public String getName() {
            return _name;
        }

        @Override
        public int getStart() {
            return _start;
        }

        @Override
        public int getEnd() {
            return _end;
        }

        @Override
        public int getMaxMatches() {
            return 6;
        }

        @Override
        public boolean isLimited() {
            return false;
        }

        @Override
        public String getFormat() {
            return _format;
        }

        @Override
        public CollectionType getCollectionType() {
            return CollectionType.ALL_CARDS;
        }

        @Override
        public CardCollection getPrizeForLeagueMatchWinner(int winCountThisSeries, int totalGamesPlayedThisSeries) {
            return null;
        }

        @Override
        public CardCollection getPrizeForLeagueMatchLoser(int winCountThisSeries, int totalGamesPlayedThisSeries) {
            return null;
        }
    }
}
