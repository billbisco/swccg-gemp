package com.gempukku.swccgo.game;

import com.gempukku.swccgo.db.GameHistoryDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/** swccg-guild/work#264: replays are filed and found under the account id, so a rename never loses them. */
public class GameRecorderReplayFolderTests {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private final Map<String, Player> players = new HashMap<>();
    private final Map<String, Integer> owners = new HashMap<>();

    private GameRecorder recorder(File root) {
        PlayerDAO playerDAO = (PlayerDAO) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{PlayerDAO.class},
                (proxy, method, args) -> "getPlayer".equals(method.getName()) && args.length == 2 ? players.get((String) args[0]) : null);
        GameHistoryDAO historyDAO = (GameHistoryDAO) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{GameHistoryDAO.class},
                (proxy, method, args) -> "getRecordingOwnerId".equals(method.getName()) ? owners.get(args[0] + "$" + args[1]) : null);
        return new GameRecorder(new GameHistoryService(historyDAO), null, playerDAO, root);
    }

    private static Player player(int id, String name) {
        return new Player(id, name, "", "u", null, null, null, null);
    }

    private static File replay(File root, String folder, String recordingId) throws Exception {
        File file = new File(new File(root, folder), recordingId + ".xml.gz");
        file.getParentFile().mkdirs();
        Files.write(file.toPath(), new byte[]{1});
        return file;
    }

    @Test
    public void GameRecorderFilesAccountReplaysUnderTheIdAndOthersUnderTheName() throws Exception {
        //test1: a seat with an account is filed under replays/<id>/
        //test2: a bot or guest seat (no account) stays under its name
        players.put("Luke", player(3812, "Luke"));
        GameRecorder recorder = recorder(tmp.getRoot());

        assertEquals("3812", recorder.folderFor("Luke")); //test1
        assertEquals("Bot", recorder.folderFor("Bot")); //test2
    }

    @Test
    public void GameRecorderFindsAReplayAfterARename() throws Exception {
        //test1: the history page links with the current name: found under the account id
        //test2: a link with the name shown at the table: found through that game's history row
        //test3: replays not yet moved are still found under the name
        //test4: an unknown replay is not found
        File root = tmp.getRoot();
        File moved = replay(root, "3812", "abc123");
        players.put("Skywalker", player(3812, "Skywalker"));
        owners.put("Luke$abc123", 3812);
        File legacy = replay(root, "Bot", "def456");
        GameRecorder recorder = recorder(root);

        assertEquals(moved, recorder.findRecordingFile("Skywalker", "abc123")); //test1
        assertEquals(moved, recorder.findRecordingFile("Luke", "abc123")); //test2
        assertEquals(legacy, recorder.findRecordingFile("Bot", "def456")); //test3
        assertNull(recorder.findRecordingFile("Skywalker", "nothere")); //test4
    }

    @Test
    public void GameRecorderDoesNotTreatAnotherAccountsFolderAsTheViewers() throws Exception {
        //test1: someone else's id folder is never reached through the viewer's name
        File root = tmp.getRoot();
        replay(root, "3813", "abc123");
        players.put("Leia", player(3812, "Leia"));
        GameRecorder recorder = recorder(root);

        assertNull(recorder.findRecordingFile("Leia", "abc123")); //test1
    }
}
