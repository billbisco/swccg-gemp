package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.game.state.EventSerializer;
import com.gempukku.swccgo.game.state.GameCommunicationChannel;
import com.gempukku.swccgo.game.state.GameEvent;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * Headless GEMP replay writer: listens on {@link DefaultSwccgGame} via
 * {@link GameCommunicationChannel}, serializes with {@link EventSerializer},
 * writes {@code <replayDir>/<playerId>/<recordingId>.xml.gz} in the same
 * on-disk format as {@code GameRecorder} — <strong>without</strong> touching
 * {@code GameHistoryService} / MariaDB.
 *
 * <p>Do <em>not</em> call {@code GameRecorder.recordGame}; that API requires a
 * {@code SwccgGameMediator} and always inserts a history row.
 */
public final class HeadlessReplayWriter {

    private static final String POSSIBLE_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CHARS_COUNT = POSSIBLE_CHARS.length();

    private final Path replayDir;
    private final Map<String, GameCommunicationChannel> channels = new LinkedHashMap<>();
    private final Map<String, String> recordingIds = new LinkedHashMap<>();
    private final Map<String, Path> replayFiles = new LinkedHashMap<>();
    private Path metaPath;
    private boolean attached;
    private boolean finished;

    public HeadlessReplayWriter(Path replayDir) {
        this.replayDir = Objects.requireNonNull(replayDir, "replayDir").toAbsolutePath().normalize();
    }

    public Path getReplayDir() {
        return replayDir;
    }

    /**
     * Register one recording channel per seat and attach as game-state listeners.
     * Call after constructing {@link DefaultSwccgGame}, before {@code startGame()}.
     */
    public void attach(DefaultSwccgGame game, Iterable<String> playerIds) {
        if (attached) {
            throw new IllegalStateException("HeadlessReplayWriter already attached");
        }
        Objects.requireNonNull(game, "game");
        for (String playerId : playerIds) {
            GameCommunicationChannel channel = new GameCommunicationChannel(playerId, 0);
            game.addGameStateListener(playerId, channel);
            channels.put(playerId, channel);
        }
        attached = true;
    }

    /**
     * Serialize channels to xml.gz + sibling {@code meta.json}. Safe to call once;
     * subsequent calls are no-ops returning the first result.
     */
    public ReplayArtifacts finish(String winner, String winReason, String loser, String loseReason,
                                  Map<String, String> extraMeta) throws IOException {
        if (finished) {
            return currentArtifacts(winner, winReason, loser, loseReason, extraMeta);
        }
        if (!attached) {
            throw new IllegalStateException("attach() before finish()");
        }
        Files.createDirectories(replayDir);

        for (Map.Entry<String, GameCommunicationChannel> entry : channels.entrySet()) {
            String playerId = entry.getKey();
            String recordingId = allocateRecordingId(playerId);
            Path file = recordingFile(playerId, recordingId);
            Files.createDirectories(file.getParent());
            writeXmlGz(file, entry.getValue().consumeGameEvents());
            recordingIds.put(playerId, recordingId);
            replayFiles.put(playerId, file);
        }

        metaPath = replayDir.resolve("meta.json");
        writeMeta(metaPath, winner, winReason, loser, loseReason, extraMeta);
        finished = true;
        return currentArtifacts(winner, winReason, loser, loseReason, extraMeta);
    }

    public Map<String, String> getRecordingIds() {
        return Collections.unmodifiableMap(recordingIds);
    }

    public Map<String, Path> getReplayFiles() {
        return Collections.unmodifiableMap(replayFiles);
    }

    public Path getMetaPath() {
        return metaPath;
    }

    /** First written xml.gz (Dark seat preferred), or null. */
    public Path primaryReplayFile() {
        if (replayFiles.isEmpty()) {
            return null;
        }
        Path dark = replayFiles.get(HeadlessBotVsBotRunner.DS_PLAYER);
        if (dark != null) {
            return dark;
        }
        return replayFiles.values().iterator().next();
    }

    public static final class ReplayArtifacts {
        public final Path replayDir;
        public final Path metaPath;
        public final Map<String, String> recordingIds;
        public final Map<String, Path> replayFiles;
        public final String winner;
        public final String winReason;
        public final String loser;
        public final String loseReason;

        ReplayArtifacts(Path replayDir, Path metaPath, Map<String, String> recordingIds,
                        Map<String, Path> replayFiles, String winner, String winReason,
                        String loser, String loseReason) {
            this.replayDir = replayDir;
            this.metaPath = metaPath;
            this.recordingIds = recordingIds;
            this.replayFiles = replayFiles;
            this.winner = winner;
            this.winReason = winReason;
            this.loser = loser;
            this.loseReason = loseReason;
        }

        /** GEMP client URL fragment: {@code game.html?replayId=<player>$<id>} */
        public String replayUrlHint(String playerId) {
            String id = recordingIds.get(playerId);
            if (id == null) {
                return null;
            }
            return "game.html?replayId=" + playerId + "$" + id;
        }
    }

    private ReplayArtifacts currentArtifacts(String winner, String winReason, String loser, String loseReason,
                                             Map<String, String> extraMeta) {
        return new ReplayArtifacts(replayDir, metaPath,
                new LinkedHashMap<>(recordingIds), new LinkedHashMap<>(replayFiles),
                winner, winReason, loser, loseReason);
    }

    private Path recordingFile(String playerId, String recordingId) {
        return replayDir.resolve(playerId).resolve(recordingId + ".xml.gz");
    }

    private String allocateRecordingId(String playerId) {
        Random rnd = new Random();
        String result;
        Path file;
        do {
            char[] chars = new char[16];
            for (int i = 0; i < chars.length; i++) {
                chars[i] = POSSIBLE_CHARS.charAt(rnd.nextInt(CHARS_COUNT));
            }
            result = new String(chars);
            file = recordingFile(playerId, result);
        } while (Files.exists(file));
        return result;
    }

    private static void writeXmlGz(Path file, List<GameEvent> gameEvents) throws IOException {
        Deflater deflater = new Deflater(9);
        try (OutputStream replayStream = new DeflaterOutputStream(Files.newOutputStream(file), deflater)) {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document doc = documentBuilder.newDocument();
                Element gameReplay = doc.createElement("gameReplay");
                EventSerializer serializer = new EventSerializer();
                for (GameEvent gameEvent : gameEvents) {
                    gameReplay.appendChild(serializer.serializeEvent(doc, gameEvent));
                }
                doc.appendChild(gameReplay);

                Source source = new DOMSource(doc);
                Result streamResult = new StreamResult(replayStream);
                Transformer xformer = TransformerFactory.newInstance().newTransformer();
                xformer.transform(source, streamResult);
            } catch (Exception exp) {
                throw new IOException("Failed to serialize replay to " + file + ": "
                        + exp.getClass().getSimpleName() + ": " + exp.getMessage(), exp);
            }
        }
    }

    private void writeMeta(Path path, String winner, String winReason, String loser, String loseReason,
                           Map<String, String> extraMeta) throws IOException {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("replayDir", replayDir.toString());
        meta.put("winner", winner);
        meta.put("winReason", winReason);
        meta.put("loser", loser);
        meta.put("loseReason", loseReason);
        meta.put("recordingIds", new LinkedHashMap<>(recordingIds));
        Map<String, String> files = new LinkedHashMap<>();
        for (Map.Entry<String, Path> e : replayFiles.entrySet()) {
            files.put(e.getKey(), e.getValue().toString());
        }
        meta.put("replayFiles", files);
        if (extraMeta != null && !extraMeta.isEmpty()) {
            meta.putAll(extraMeta);
        }
        // Minimal JSON without pulling Gson into this writer (server test already has Gson).
        try (Writer w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            w.write(toJson(meta));
            w.write('\n');
        }
    }

    /** Tiny JSON encoder for String/Number/Boolean/Map/null only (meta payload). */
    @SuppressWarnings("unchecked")
    private static String toJson(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return '"' + escape((String) value) + '"';
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Map) {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            boolean first = true;
            for (Map.Entry<Object, Object> e : ((Map<Object, Object>) value).entrySet()) {
                if (!first) {
                    sb.append(',');
                }
                first = false;
                sb.append('"').append(escape(String.valueOf(e.getKey()))).append("\":");
                sb.append(toJson(e.getValue()));
            }
            sb.append('}');
            return sb.toString();
        }
        return '"' + escape(String.valueOf(value)) + '"';
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}
