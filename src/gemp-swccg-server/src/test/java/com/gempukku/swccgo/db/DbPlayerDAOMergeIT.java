package com.gempukku.swccgo.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Staff merge on Play against a real MariaDB with the live Play schema (same setup as DbPlayerDAOEraseIT):
 * runs only when SWCCG_TEST_GEMP_JDBC points at a server holding a `gemp-swccg` schema.
 */
@EnabledIfEnvironmentVariable(named = "SWCCG_TEST_GEMP_JDBC", matches = ".+")
public class DbPlayerDAOMergeIT {
    private static final String DB = "a11_gemp_merge_it";
    private static BasicDataSource ds;

    @BeforeAll
    static void setUp() throws Exception {
        String url = System.getenv("SWCCG_TEST_GEMP_JDBC");
        BasicDataSource admin = new BasicDataSource();
        admin.setUrl(url);
        try (Connection c = admin.getConnection(); Statement st = c.createStatement()) {
            st.execute("DROP DATABASE IF EXISTS " + DB);
            st.execute("CREATE DATABASE " + DB);
            List<String> tables = new ArrayList<>();
            try (ResultSet rs = st.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema='gemp-swccg' AND table_type='BASE TABLE'")) {
                while (rs.next()) tables.add(rs.getString(1));
            }
            for (String t : tables)
                st.execute("CREATE TABLE " + DB + ".`" + t + "` LIKE `gemp-swccg`.`" + t + "`");
        }
        admin.close();
        ds = new BasicDataSource();
        ds.setUrl(url.replace("/?", "/" + DB + "?"));
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            st.execute("SET SESSION sql_mode=''");
            st.execute("INSERT INTO player(id,name,password,type) VALUES (20,'Main One','x','u'),(21,'Extra Two','x','u'),(22,'Other','x','u')");
            st.execute("INSERT INTO deck(player_id,name,type,contents) VALUES (20,'shared','LS','a'),(21,'shared','LS','b'),(21,'only extra','DS','c')");
            st.execute("INSERT INTO collection(player_id,type,collection) VALUES (20,'permanent','a'),(21,'permanent','b'),(21,'extra kind','c')");
            st.execute("INSERT INTO game_history(winner,loser,win_reason,lose_reason,start_date,end_date,winner_deck_name,loser_deck_name,tournament,league_type,sealed_league_type,winner_id,loser_id,win_recording_id,lose_recording_id,game_setup) VALUES "
                    + "('Extra Two','Other','w','l',0,0,'x','y','','','',21,22,'m1','m2','{\"a\":\"Extra Two\"}'),"
                    + "('Other','Extra Two','w','l',0,0,'x','y','','','',22,21,'m3','m4',NULL),"
                    + "('Main One','Other','w','l',0,0,'x','y','','','',20,22,'m5','m6',NULL)");
            st.execute("INSERT INTO league_participation(league_type,player_name,locked_ls_deck_name) VALUES ('L1','Extra Two','only extra')");
            st.execute("INSERT INTO tournament_match(tournament_id,round,player_one,player_two,winner) VALUES ('T1',1,'Extra Two','Other','Extra Two')");
        }
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (ds != null) {
            try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
                st.execute("DROP DATABASE IF EXISTS " + DB);
            }
            ds.close();
        }
    }

    private static int count(String sql) throws Exception {
        try (Connection c = ds.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    @Test
    public void mergeMovesHistoryToTheTarget() throws Exception {
        DbPlayerDAO dao = new DbPlayerDAO(new DbAccess(ds));
        assertEquals(2, dao.gamesOf(21));
        assertEquals(1, dao.gamesOf(20));
        assertNull(dao.mergePlayer(21, 20));
        assertEquals(0, dao.gamesOf(21));
        assertEquals(3, dao.gamesOf(20));
        assertEquals(0, count("SELECT COUNT(*) FROM game_history WHERE winner='Extra Two' OR loser='Extra Two'"));
        assertEquals(1, count("SELECT COUNT(*) FROM game_history WHERE game_setup LIKE '%Main One%'"));
        assertEquals(1, count("SELECT COUNT(*) FROM league_participation WHERE player_name='Main One'"));
        assertEquals(1, count("SELECT COUNT(*) FROM tournament_match WHERE player_one='Main One' AND winner='Main One'"));
        // Decks: both kept on the target; the clashing name got the suffix.
        assertEquals(3, count("SELECT COUNT(*) FROM deck WHERE player_id=20"));
        assertEquals(1, count("SELECT COUNT(*) FROM deck WHERE player_id=20 AND name='shared (from #21)' AND contents='b'"));
        // Collections: only the kind the target lacked moved; the other stays for the source's close.
        assertEquals(1, count("SELECT COUNT(*) FROM collection WHERE player_id=20 AND type='extra kind'"));
        assertEquals(1, count("SELECT COUNT(*) FROM collection WHERE player_id=21 AND type='permanent'"));
        // Retry is harmless; self-merge and unknown players are refused.
        assertNull(dao.mergePlayer(21, 20));
        assertEquals("not_found", dao.mergePlayer(20, 20));
        assertEquals("not_found", dao.mergePlayer(99, 20));
    }
}
