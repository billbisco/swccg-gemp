package com.gempukku.swccgo.db.vo;

import java.util.Date;

public class GameHistoryEntry {
    private String _winner;
    private String _loser;

    private String _winReason;
    private String _loseReason;

    private String _winnerRecording;
    private String _loserRecording;

    private String _formatName;
    private String _tournament;
    private String _winnerDeckName;
    private String _winnerDeckArchetype;
    private String _loserDeckName;
    private String _loserDeckArchetype;

    private Date _startTime;
    private Date _endTime;

    private String _winnerSide;
    private String _leagueType;

    public GameHistoryEntry(String winner, String winReason, String winnerRecording, String loser, String loseReason, String loserRecording, String formatName, String tournament, String winnerDeckName, String winnerDeckArchetype, String loserDeckName, String loserDeckArchetype, Date startTime, Date endTime) {
        _winner = winner;
        _winReason = winReason;
        _winnerRecording = winnerRecording;
        _loser = loser;
        _loseReason = loseReason;
        _loserRecording = loserRecording;
        _formatName = formatName;
        _tournament = tournament;
        _winnerDeckName = winnerDeckName;
        _winnerDeckArchetype = winnerDeckArchetype;
        _loserDeckName = loserDeckName;
        _loserDeckArchetype = loserDeckArchetype;
        _startTime = startTime;
        _endTime = endTime;
    }

    /** As above, plus the winner's side ("Dark", "Light" or "None") and the league type code ("" outside leagues). */
    public GameHistoryEntry(String winner, String winReason, String winnerRecording, String loser, String loseReason, String loserRecording, String formatName, String tournament, String winnerDeckName, String winnerDeckArchetype, String loserDeckName, String loserDeckArchetype, Date startTime, Date endTime, String winnerSide, String leagueType) {
        this(winner, winReason, winnerRecording, loser, loseReason, loserRecording, formatName, tournament, winnerDeckName, winnerDeckArchetype, loserDeckName, loserDeckArchetype, startTime, endTime);
        _winnerSide = winnerSide;
        _leagueType = leagueType;
    }

    public String getLoser() {
        return _loser;
    }

    public String getLoseReason() {
        return _loseReason;
    }

    public String getLoserRecording() {
        return _loserRecording;
    }

    public String getWinner() {
        return _winner;
    }

    public String getWinnerRecording() {
        return _winnerRecording;
    }

    public String getWinReason() {
        return _winReason;
    }

    public String getFormatName() {
        return _formatName;
    }

    public String getTournament() {
        return _tournament;
    }

    public String getWinnerDeckName() {
        return _winnerDeckName;
    }

    public String getwinnerDeckArchetype() {
        return _winnerDeckArchetype;
    }

    public String getLoserDeckName() {
        return _loserDeckName;
    }

    public String getloserDeckArchetype() {
        return _loserDeckArchetype;
    }

    public Date getEndTime() {
        return _endTime;
    }

    public Date getStartTime() {
        return _startTime;
    }

    public String getWinnerSide() {
        return _winnerSide;
    }

    public String getLeagueType() {
        return _leagueType;
    }

    /**
     * The side the given player played, from the winner's side: "Dark" or "Light", or null when it was not recorded
     * (cancelled games, or a player who is in neither seat).
     */
    public String getSideOf(String playerName) {
        if (!"Dark".equals(_winnerSide) && !"Light".equals(_winnerSide))
            return null;
        String other = "Dark".equals(_winnerSide) ? "Light" : "Dark";
        if (playerName != null && playerName.equals(_winner))
            return _winnerSide;
        if (playerName != null && playerName.equals(_loser))
            return other;
        return null;
    }
}
