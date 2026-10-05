package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

public interface TournamentQueue {
    public int getCost();

    public String getFormat();

    public CollectionType getCollectionType();

    public String getTournamentQueueName();

    public String getPrizesDescription();

    public String getPairingDescription();

    public String getStartCondition();

    public boolean isRequiresDeck();

    public boolean process(TournamentQueueCallback tournamentQueueCallback, CollectionsManager collectionsManager);

    public void joinPlayer(CollectionsManager collectionsManager, Player player, SwccgDeck deck);

    public void leavePlayer(CollectionsManager collectionsManager, Player player);

    public void leaveAllPlayers(CollectionsManager collectionsManager);

    public int getPlayerCount();

    public boolean isPlayerSignedUp(String player);

    public boolean isJoinable();

    default boolean isPlayerMade() {
        return false;
    }

    default boolean isHost(String player) {
        return false;
    }

    default boolean isStartable(String player) {
        return false;
    }

    default boolean canCancel(String player) {
        return false;
    }

    default String getSignedUpPlayersCsv() {
        return "";
    }

    default int getMaxPlayers() {
        return 0;
    }

    default int getReadyCheckSeconds() {
        return 0;
    }

    default boolean isPrivateEvent() {
        return false;
    }

    default long getCreatedAt() {
        return 0;
    }

    default boolean isReadyCheckActive() {
        return false;
    }

    default int getReadyCheckSecsRemaining() {
        return -1;
    }

    default boolean hasConfirmedReady(String player) {
        return false;
    }

    default void requestStart(String player) {
    }

    default void confirmReady(String player) {
    }

    default void cancel(String player) {
    }

    default void joinPlayer(CollectionsManager collectionsManager, Player player, SwccgDeck lightDeck, SwccgDeck darkDeck) {
        joinPlayer(collectionsManager, player, lightDeck);
    }
}
