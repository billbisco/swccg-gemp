package com.gempukku.swccgo.game.save;

import com.esotericsoftware.kryo.KryoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

/**
 * Loads a {@link GameSave}.
 * <ul>
 * <li>Same build (fingerprint matches): read the snapshot and replay the answers logged after it.</li>
 * <li>Different build, no snapshot, or an unreadable snapshot: rebuild from the header and replay the whole log.</li>
 * </ul>
 * Either way every logged hash is checked; a mismatch means the game can't continue on this build.
 */
public final class GameResumer {
    private static final Logger LOG = LogManager.getLogger(GameResumer.class);

    /** Builds a fresh, started game from a header: same format, decks, seed, players and bots. */
    public interface GameFactory {
        SaveRoot newGame(GameSaveHeader header);
    }

    /** How the game was loaded. */
    public enum Method { SNAPSHOT, MOVE_LOG }

    /**
     * A loaded game.
     * @param root the game
     * @param method how it was loaded
     */
    public record Resumed(SaveRoot root, Method method) {
    }

    private final GameSaveCodec _codec;
    private final GameFactory _factory;

    public GameResumer(GameSaveCodec codec, GameFactory factory) {
        _codec = codec;
        _factory = factory;
    }

    public Resumed resume(GameSave save) throws GameSaveIncompatibleException {
        List<DecisionLog.Entry> entries = save.getLog().getEntries();
        if (save.getSnapshot() != null && Objects.equals(save.getSnapshotFingerprint(), _codec.getBuild().getClassFingerprint())) {
            SaveRoot root = null;
            try {
                root = _codec.read(save.getSnapshot());
            } catch (KryoException | ClassCastException e) {
                LOG.warn("Snapshot of game " + save.getHeader().gameId + " is unreadable, replaying the move log", e);
            }
            if (root != null) {
                replay(root, entries, save.getSnapshotLogSize());
                return new Resumed(root, Method.SNAPSHOT);
            }
        }
        SaveRoot root = _factory.newGame(save.getHeader());
        replay(root, entries, 0);
        return new Resumed(root, Method.MOVE_LOG);
    }

    private static void replay(SaveRoot root, List<DecisionLog.Entry> entries, int from) throws GameSaveIncompatibleException {
        for (int i = from; i < entries.size(); i++) {
            DecisionLog.Entry e = entries.get(i);
            if (root.game().isFinished() || !root.feedback().getUsersPendingDecision().contains(e.player())) {
                throw new GameSaveIncompatibleException(i, e.player() + " has no decision pending");
            }
            DecisionApplier.apply(root.game(), root.feedback(), e.player(), e.answer());
            if (e.hash() != null) {
                String now = GameStateHash.of(root.game());
                if (!now.equals(e.hash())) {
                    throw new GameSaveIncompatibleException(i, "position hash " + now + ", logged " + e.hash());
                }
            }
        }
    }
}
