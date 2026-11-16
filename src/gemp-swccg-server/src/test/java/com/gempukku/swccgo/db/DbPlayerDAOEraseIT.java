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
 * Close account on Play (#524) against a real MariaDB with the live Play schema. Runs only when
 * SWCCG_TEST_GEMP_JDBC (jdbc:mysql://host:port/?user=..&password=..) points at a server holding a
 * `gemp-swccg` schema; tables are copied into a throwaway database.
 */
@EnabledIfEnvironmentVariable(named = "SWCCG_TEST_GEMP_JDBC", matches = ".+")
public class DbPlayerDAOEraseIT {
    private static final String DB = "a9_gemp_erase_it";
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
            st.execute("INSERT INTO player(id,name,password,type,last_ip,create_ip) VALUES (10,'Luke Sky','x','u','203.0.113.9','203.0.113.9'),(11,'Leia','x','u','198.51.100.4','198.51.100.4')");
            st.execute("INSERT INTO deck(player_id,name,type,contents) VALUES (10,'casual','LS',''),(10,'league deck','LS',''),(10,'tourney deck','DS',''),(10,'comp game','DS',''),(10,'casual game','LS',''),(11,'casual','LS','')");
            st.execute("INSERT INTO league_participation(league_type,player_name,join_ip,locked_ls_deck_name) VALUES ('L1','Luke Sky','203.0.113.9','league deck')");
            st.execute("INSERT INTO tournament_player(tournament_id,player,deck_name) VALUES ('T1','Luke Sky','tourney deck'),('T2','Luke Sky','')");
            st.execute("INSERT INTO game_history(winner,loser,win_reason,lose_reason,start_date,end_date,winner_deck_name,loser_deck_name,tournament,league_type,sealed_league_type,winner_id,loser_id,win_recording_id,lose_recording_id) VALUES "
                    + "('Luke Sky','Leia','w','l',0,0,'comp game','casual','T1','','',10,11,'r1','r2'),"
                    + "('Leia','Luke Sky','w','l',0,0,'casual','casual game','Casual - Open','','',11,10,'r3','r4')");
            st.execute("INSERT INTO league_match(league_type,season_type,winner,loser) VALUES ('L1','S','Luke Sky','Leia')");
            st.execute("INSERT INTO tournament_match(tournament_id,round,player_one,player_two,winner) VALUES ('T1',1,'Luke Sky','Leia','Luke Sky')");
            st.execute("INSERT INTO collection(player_id,type,collection) VALUES (10,'permanent',''),(11,'permanent','')");
            st.execute("INSERT INTO player_game_settings(player_id,form,format,options) VALUES (10,'f','f','')");
            st.execute("INSERT INTO player_password_reset(player_id,code_hash,expires_at,created_at) VALUES (10,'h',0,0)");
            st.execute("INSERT INTO game_save(game_id,owner,header,move_log,updated_at) VALUES ('g1','Luke Sky','','',0)");
            st.execute("INSERT INTO transfer(player,name,notify,reason,currency,collection,transfer_date,direction) VALUES ('Luke Sky','gift',0,'r',0,'',0,'to')");
        }
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (ds == null) return;
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            st.execute("DROP DATABASE IF EXISTS " + DB);
        }
        ds.close();
    }

    private static List<String> list(String sql) throws Exception {
        List<String> out = new ArrayList<>();
        try (Connection c = ds.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) out.add(String.valueOf(rs.getObject(1)));
        }
        return out;
    }

    @Test
    void eraseKeepsCompetitiveDecksAndRenamesEverywhere() throws Exception {
        DbPlayerDAO dao = new DbPlayerDAO(new DbAccess(ds));
        assertEquals("name_taken", dao.erasePlayer(10, "Leia"));
        assertEquals(List.of("Luke Sky"), list("SELECT name FROM player WHERE id=10"), "a refused erase changes nothing");
        assertNull(dao.erasePlayer(10, "Mynock 10"));
        assertEquals(List.of("Mynock 10||null|null"), list("SELECT CONCAT_WS('|',name,password,IFNULL(last_ip,'null'),IFNULL(create_ip,'null')) FROM player WHERE id=10"));
        assertEquals(List.of("comp game", "league deck", "tourney deck"), list("SELECT name FROM deck WHERE player_id=10 ORDER BY name"));
        assertEquals(List.of("casual"), list("SELECT name FROM deck WHERE player_id=11"));
        assertEquals(List.of("11"), list("SELECT player_id FROM collection"));
        for (String t : new String[]{"player_game_settings", "player_password_reset", "game_save"})
            assertEquals(List.of("0"), list("SELECT COUNT(*) FROM " + t), t);
        assertEquals(List.of("Mynock 10|"), list("SELECT CONCAT_WS('|',player_name,IFNULL(join_ip,'')) FROM league_participation"));
        assertEquals(List.of("Mynock 10|Leia", "Leia|Mynock 10"), list("SELECT CONCAT(winner,'|',loser) FROM game_history ORDER BY id"));
        assertEquals(List.of("Mynock 10|Leia|Mynock 10"), list("SELECT CONCAT(player_one,'|',player_two,'|',winner) FROM tournament_match"));
        assertEquals(List.of("Mynock 10|Leia"), list("SELECT CONCAT(winner,'|',loser) FROM league_match"));
        assertEquals(List.of("Mynock 10"), list("SELECT DISTINCT player FROM tournament_player"));
        assertEquals(List.of("Mynock 10"), list("SELECT player FROM transfer"));
        assertEquals(4, dao.replaysOf(10).size(), "both seats of both games");
        assertNull(dao.erasePlayer(10, "Mynock 10"), "a retry is safe");
        //test: close account deactivates first (block step); the erase must still find the player
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            st.execute("INSERT INTO player(id,name,password,type) VALUES (12,'Deact Han','x','du')");
        }
        assertNull(dao.getPlayer(12), "the normal lookup leaves out deactivated players");
        com.gempukku.swccgo.service.AuthService auth = new com.gempukku.swccgo.service.AuthService(dao,
                new com.gempukku.swccgo.security.PasswordHasher(1), new com.gempukku.swccgo.service.LoginThrottle());
        assertNull(auth.eraseAccount(12, "Mynock 12"));
        assertEquals(List.of("Mynock 12"), list("SELECT name FROM player WHERE id=12"));
        assertEquals(List.of("comp game", "league deck", "tourney deck"), list("SELECT name FROM deck WHERE player_id=10 ORDER BY name"));
    }
}
