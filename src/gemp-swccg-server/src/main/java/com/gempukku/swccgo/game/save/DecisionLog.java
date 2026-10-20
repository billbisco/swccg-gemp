package com.gempukku.swccgo.game.save;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Every answer of a game, in order, human and bot alike (bots are never asked again on replay), with a position
 * hash every {@link #HASH_EVERY} answers and on the last one. Replaying the log on a fresh game with the same seed
 * and decks rebuilds the game; the hashes show where a replay went a different way.
 */
public final class DecisionLog {
    /** Answers between position hashes. */
    public static final int HASH_EVERY = 10;

    /**
     * One answer.
     * @param player who answered
     * @param answer the answer, exactly as given to the decision
     * @param hash the {@link GameStateHash} right after the answer, or null when not recorded for this step
     */
    public record Entry(String player, String answer, String hash) {
    }

    private final List<Entry> _entries = new ArrayList<>();

    /**
     * Records an answer. Call after the answer was applied and the game ran to its next decision.
     * @param player who answered
     * @param answer the answer
     * @param positionHash the position hash now; kept every {@link #HASH_EVERY} answers
     */
    public void add(String player, String answer, String positionHash) {
        boolean keep = (_entries.size() + 1) % HASH_EVERY == 0;
        _entries.add(new Entry(player, answer, keep ? positionHash : null));
    }

    /** Sets the hash on the last entry (on save, so a replay is checked right up to the end). */
    public void markLast(String positionHash) {
        if (!_entries.isEmpty()) {
            Entry last = _entries.get(_entries.size() - 1);
            _entries.set(_entries.size() - 1, new Entry(last.player(), last.answer(), positionHash));
        }
    }

    public int size() {
        return _entries.size();
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(_entries);
    }

    /** Gzipped text, one answer per line: player, answer, hash (tab-separated, escaped). */
    public byte[] toBytes() {
        StringBuilder sb = new StringBuilder();
        for (Entry e : _entries) {
            sb.append(escape(e.player())).append('\t').append(escape(e.answer())).append('\t').append(e.hash() == null ? "" : e.hash()).append('\n');
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (GZIPOutputStream z = new GZIPOutputStream(bytes)) {
            z.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return bytes.toByteArray();
    }

    public static DecisionLog fromBytes(byte[] bytes) {
        DecisionLog log = new DecisionLog();
        try (GZIPInputStream z = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            String text = new String(z.readAllBytes(), StandardCharsets.UTF_8);
            for (String line : text.split("\n")) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] p = line.split("\t", -1);
                log._entries.add(new Entry(unescape(p[0]), unescape(p[1]), p[2].isEmpty() ? null : p[2]));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Unreadable move log", e);
        }
        return log;
    }

    private static String escape(String s) {
        if (s == null) {
            return "\\0";
        }
        return s.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String unescape(String s) {
        if (s.equals("\\0")) {
            return null;
        }
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(++i);
                sb.append(n == 't' ? '\t' : n == 'n' ? '\n' : n == 'r' ? '\r' : n);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
