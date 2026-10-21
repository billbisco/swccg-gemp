package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.framework.VirtualTableScenario;

import java.nio.file.Files;
import java.nio.file.Path;

/** Run in a separate JVM by {@link GameSaveFreshKryoTests}: reads a snapshot file and prints the position's hash. */
public final class ReadSnapshotInNewJvm {
    public static void main(String[] args) throws Exception {
        byte[] snapshot = Files.readAllBytes(Path.of(args[0]));
        GameSaveCodec codec = new GameSaveCodec(VirtualTableScenario._cardLibrary, VirtualTableScenario._formatLibrary, SaveBuild.current());
        SaveRoot root = codec.read(snapshot);
        System.out.println("hash=" + GameStateHash.of(root.game()));
        System.exit(0);
    }
}
