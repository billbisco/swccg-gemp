package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.db.vo.CollectionType;

public class TournamentInfo {
    private String _tournamentId;
    private String _draftType;
    private String _tournamentName;
    private String _tournamentFormat;
    private CollectionType _collectionType;
    private String _prizesScheme;
    private int _tournamentRound;
    private String _pairingMechanism;
    private Tournament.Stage _tournamentStage;
    private long _startTime;

    public TournamentInfo(String tournamentId, String draftType, String tournamentName, String tournamentFormat, CollectionType collectionType,
                          Tournament.Stage tournamentStage, String pairingMechanism, String prizesScheme, int tournamentRound) {
        this(tournamentId, draftType, tournamentName, tournamentFormat, collectionType, tournamentStage, pairingMechanism, prizesScheme, tournamentRound, 0L);
    }

    /**
     * @param startTime when the tournament started (epoch milliseconds, the {@code tournament.start} column), 0 when not read
     */
    public TournamentInfo(String tournamentId, String draftType, String tournamentName, String tournamentFormat, CollectionType collectionType,
                          Tournament.Stage tournamentStage, String pairingMechanism, String prizesScheme, int tournamentRound, long startTime) {
        _tournamentId = tournamentId;
        _draftType = draftType;
        _tournamentName = tournamentName;
        _tournamentFormat = tournamentFormat;
        _collectionType = collectionType;
        _prizesScheme = prizesScheme;
        _tournamentRound = tournamentRound;
        _pairingMechanism = pairingMechanism;
        _tournamentStage = tournamentStage;
        _startTime = startTime;
    }

    /** When the tournament started, in epoch milliseconds (UTC); 0 when it was not read. */
    public long getStartTime() {
        return _startTime;
    }

    public String getTournamentId() {
        return _tournamentId;
    }

    public String getDraftType() {
        return _draftType;
    }

    public String getTournamentName() {
        return _tournamentName;
    }

    public String getTournamentFormat() {
        return _tournamentFormat;
    }

    public CollectionType getCollectionType() {
        return _collectionType;
    }

    public int getTournamentRound() {
        return _tournamentRound;
    }

    public String getPairingMechanism() {
        return _pairingMechanism;
    }

    public String getPrizesScheme() {
        return _prizesScheme;
    }

    public Tournament.Stage getTournamentStage() {
        return _tournamentStage;
    }
}
