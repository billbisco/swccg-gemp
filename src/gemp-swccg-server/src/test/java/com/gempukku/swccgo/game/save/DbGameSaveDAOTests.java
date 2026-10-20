package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.db.DbAccess;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Runs against a real MariaDB with the game_save table, only when -Dgemp.test.dbUrl is set
 * (for example jdbc:mysql://127.0.0.1:3306/gemp-swccg, with -Dgemp.test.dbUser and -Dgemp.test.dbPassword).
 * Skipped otherwise. The Docker integration CI job runs it against the stack's database.
 */
public class DbGameSaveDAOTests {
    private DbGameSaveDAO _dao;

    @BeforeEach
    public void connect() {
        String url = System.getProperty("gemp.test.dbUrl");
        assumeTrue(url != null && !url.isEmpty());
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setUsername(System.getProperty("gemp.test.dbUser", "root"));
        ds.setPassword(System.getProperty("gemp.test.dbPassword", ""));
        _dao = new DbGameSaveDAO(new DbAccess(ds));
        _dao.delete("db-test-1");
        _dao.delete("db-test-2");
    }

    private static GameSave save(String gameId, long updatedAt, byte[] snapshot) {
        GameSaveHeader h = new GameSaveHeader();
        h.gameId = gameId;
        h.formatCode = "premiere_anh";
        h.seed = 9;
        h.seats.add(GameSaveHeader.seat(BotGame.LS, "Light Side", null, BotGame.lsDeck()));
        h.seats.add(GameSaveHeader.seat(BotGame.DS, "Dark Side", "ozzel", BotGame.dsDeck()));
        h.updatedAt = updatedAt;
        DecisionLog log = new DecisionLog();
        for (int i = 0; i < 30; i++) {
            log.add(i % 2 == 0 ? BotGame.LS : BotGame.DS, "answer " + i, "hash" + i);
        }
        return new GameSave(h, log, snapshot, snapshot == null ? 0 : 20, snapshot == null ? null : "fp");
    }

    @Test
    public void DbGameSaveDAOStoresAndReplacesSaves() {
        //test1: a save reads back exactly (header, log, snapshot)
        //test2: saving again replaces it; a save without a snapshot reads back without one
        byte[] snapshot = new byte[300_000];
        new java.util.Random(1).nextBytes(snapshot);
        _dao.save("db-test-1", "guest-a", save("db-test-1", 1000, snapshot));
        GameSave back = _dao.load("db-test-1");
        assertEquals(9, back.getHeader().seed); //test1
        assertEquals(30, back.getLog().size()); //test1
        assertEquals("answer 3", back.getLog().getEntries().get(3).answer()); //test1
        assertArrayEquals(snapshot, back.getSnapshot()); //test1
        assertEquals(20, back.getSnapshotLogSize()); //test1
        assertEquals("guest-a", _dao.getOwner("db-test-1")); //test1

        _dao.save("db-test-1", "guest-a", save("db-test-1", 2000, null));
        assertNull(_dao.load("db-test-1").getSnapshot()); //test2
    }

    @Test
    public void DbGameSaveDAOMovesOwnersAndDeletesOldSaves() {
        //test1: a guest's saves move to their account
        //test2: saves last played at or before the cutoff are deleted, newer ones are kept
        _dao.save("db-test-1", "guest-b", save("db-test-1", 1000, null));
        _dao.save("db-test-2", "guest-b", save("db-test-2", 5000, null));
        _dao.changeOwner("guest-b", "Member");
        assertEquals(List.of("db-test-2", "db-test-1"), _dao.getGameIds("Member")); //test1

        _dao.deleteLastPlayedAtOrBefore(1000);
        assertNull(_dao.load("db-test-1")); //test2
        assertEquals(5000, _dao.load("db-test-2").getHeader().updatedAt); //test2
        _dao.delete("db-test-2");
    }
}
