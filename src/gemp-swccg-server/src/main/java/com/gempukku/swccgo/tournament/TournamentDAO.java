package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.db.vo.CollectionType;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TournamentDAO {
    public void addTournament(String tournamentId, String draftType, String tournamentName, String format,
                              CollectionType collectionType, Tournament.Stage stage, String pairingMechanism, String prizeScheme, Date start);

    public List<TournamentInfo> getUnfinishedTournaments();

    public List<TournamentInfo> getFinishedTournamentsSince(long time);

    public TournamentInfo getTournamentById(String tournamentId);

    public void updateTournamentStage(String tournamentId, Tournament.Stage stage);

    public void updateTournamentRound(String tournamentId, int round);

    public List<TournamentQueueInfo> getUnstartedScheduledTournamentQueues(long tillDate);

    public void updateScheduledTournamentStarted(String scheduledTournamentId);

    // ---- the Events tab (calendar and completed-event browser); times are epoch milliseconds, ranges half-open [from, to) ----

    /** Tournaments of every stage that started in the range, earliest first, with their start times. */
    public List<TournamentInfo> getTournamentsStartingBetween(long from, long to);

    /** Finished tournaments that started in the range, latest first, with their start times. */
    public List<TournamentInfo> getFinishedTournamentsBetween(long from, long to);

    /** Tournament id to the number of players entered, for the finished tournaments that started in the range. */
    public Map<String, Integer> getFinishedTournamentPlayerCountsBetween(long from, long to);

    /** Every "yyyy-MM" (UTC) in which a finished tournament with at least one round played started, in no order. */
    public List<String> getFinishedTournamentMonths();

    /** The ids of the tournaments starting in the range that the player is or was entered in. */
    public Set<String> getTournamentsPlayedBetween(String player, long from, long to);
}
