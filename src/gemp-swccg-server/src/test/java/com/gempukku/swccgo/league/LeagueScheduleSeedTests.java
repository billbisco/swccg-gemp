package com.gempukku.swccgo.league;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gempukku.swccgo.db.LeagueDAO;
import com.gempukku.swccgo.db.vo.LeagueScheduleRow;
import com.gempukku.swccgo.draft2.SoloDraft;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * db-scripts/seed_league_schedules.sql (Bill's first seven recurring leagues) must be definitions this server can
 * turn into leagues: real format, sealed and draft codes, names that fit, and off by default.
 */
public class LeagueScheduleSeedTests {
    private static final Pattern ROW = Pattern.compile(
            "\\('([^']*)', '([A-Z]+)',\\s*'(\\{.*?\\})',\\s*'(\\[.*?\\])',\\s*'([^']*)', (\\d{8}), (\\d+), ([\\d.]+), (\\d+), b'([01])', '([^']*)'\\)",
            Pattern.DOTALL);

    private static String seedSql() throws Exception {
        Path path = Paths.get("..", "db-scripts", "seed_league_schedules.sql");
        if (!Files.exists(path))
            path = Paths.get("src", "db-scripts", "seed_league_schedules.sql");
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    private static List<LeagueScheduleRow> seedRows() throws Exception {
        List<LeagueScheduleRow> rows = new ArrayList<>();
        Matcher m = ROW.matcher(seedSql());
        while (m.find()) {
            LeagueScheduleRow row = new LeagueScheduleRow();
            row.name = m.group(1);
            row.leagueType = m.group(2);
            row.template = m.group(3);
            row.events = m.group(4);
            row.namePattern = m.group(5);
            row.nextEventDate = Integer.parseInt(m.group(6));
            row.nextEventIndex = Integer.parseInt(m.group(7));
            row.intervalMonths = Double.parseDouble(m.group(8));
            row.leadDays = Integer.parseInt(m.group(9));
            row.enabled = m.group(10).equals("1");
            row.createdBy = m.group(11);
            rows.add(row);
        }
        return rows;
    }

    private static Set<String> codesOf(String resource, String key) throws Exception {
        try (InputStream in = LeagueScheduleSeedTests.class.getResourceAsStream(resource)) {
            JSONArray array = JSON.parseArray(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            Set<String> codes = new HashSet<>();
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (obj.getString(key) != null && !Boolean.TRUE.equals(obj.getBoolean("legacy")))
                    codes.add(obj.getString(key));
            }
            return codes;
        }
    }

    /** A factory whose format library and draft definitions know exactly the codes the resource files list. */
    private static LeagueFactory factoryFromResources() throws Exception {
        SwccgoFormatLibrary formatLibrary = Mockito.mock(SwccgoFormatLibrary.class);
        for (String code : codesOf("/swccgFormats.json", "code")) {
            SwccgFormat format = Mockito.mock(SwccgFormat.class);
            Mockito.when(format.getName()).thenReturn(code);
            Mockito.when(formatLibrary.getFormat(code)).thenReturn(format);
        }
        SoloDraftDefinitions drafts = Mockito.mock(SoloDraftDefinitions.class);
        for (String code : codesOf("/swccgDrafts.json", "type")) {
            SoloDraft draft = Mockito.mock(SoloDraft.class);
            Mockito.when(draft.getFormat()).thenReturn("cube");
            Mockito.when(drafts.getSoloDraft(code)).thenReturn(draft);
        }
        return new LeagueFactory(Mockito.mock(SwccgCardBlueprintLibrary.class), formatLibrary, drafts, Mockito.mock(LeagueDAO.class), Mockito.mock(LeagueService.class));
    }

    @Test
    public void TheSevenSeedSchedulesAreValidAndOff() throws Exception {
        //test1: Bill's seven leagues, in his order
        //test2: every definition passes the same validation the admin page's Save runs (real codes, names that fit)
        //test3: all disabled, monthly, 7 lead days, first start 2026-11-01
        List<LeagueScheduleRow> rows = seedRows();
        assertEquals(List.of("Premiere - Death Star II", "Classic No V (Modified)", "Ewok", "Reflections Sealed", "Premiere - A New Hope", "Open", "Cube Draft"),
                rows.stream().map(r -> r.name).toList()); //test1

        LeagueFactory factory = factoryFromResources();
        LeagueScheduleService service = new LeagueScheduleService(Mockito.mock(com.gempukku.swccgo.db.LeagueScheduleDAO.class), factory,
                Mockito.mock(LeagueService.class), Mockito.mock(LeagueDAO.class), Mockito.mock(SwccgCardBlueprintLibrary.class));
        for (LeagueScheduleRow row : rows) {
            service.validateDefinition(row); //test2
            assertTrue(row.name, !row.enabled); //test3
            assertEquals(1.0, row.intervalMonths, 0.0001); //test3
            assertEquals(7, row.leadDays); //test3
            assertEquals(20261101, row.nextEventDate); //test3
            LeagueSchedule schedule = new LeagueSchedule(row);
            for (LeagueScheduleService.ProjectedEvent event : service.projectNext(schedule, 14)) {
                assertTrue(event.leagueName(), event.error() == null);
                assertTrue(event.leagueName(), event.leagueName().length() <= LeagueFactory.MAX_NAME_LENGTH);
            }
        }
    }

    @Test
    public void TheSeedUsesTheCodesBillNamed() throws Exception {
        List<LeagueScheduleRow> rows = seedRows();
        assertTrue(rows.get(0).events.contains("\"premiere_ds2\""));
        assertTrue(rows.get(1).events.contains("\"classic_no_virtual_m\""));
        assertTrue(rows.get(2).events.contains("\"ewok\""));
        assertEquals("SEALED", rows.get(3).leagueType);
        assertTrue(rows.get(3).events.contains("\"reflections_sealed\""));
        assertTrue(SealedLeagueType.getLeagueType("reflections_sealed") != null);
        assertTrue(rows.get(4).events.contains("\"premiere_anh\""));
        assertTrue(rows.get(5).events.contains("\"open\""));
        assertEquals("SOLODRAFT", rows.get(6).leagueType);
        assertTrue(rows.get(6).events.contains("\"c7obj_draft\""));
        assertEquals("Open - November 2026", LeagueScheduleService.renderName(rows.get(5).namePattern, rows.get(5).name, "Open", LocalDate.of(2026, 11, 1)));
    }
}
