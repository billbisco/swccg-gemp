package com.gempukku.swccgo.game.save;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

/**
 * The Kryo allowlist and build fingerprint written by {@link KryoClassListGenerator} at build time.
 */
public final class SaveBuild {
    private static volatile SaveBuild _current;

    private final List<String> _classNames;
    private final String _classFingerprint;
    private final String _buildCommit;

    SaveBuild(List<String> classNames, String classFingerprint, String buildCommit) {
        _classNames = List.copyOf(classNames);
        _classFingerprint = classFingerprint;
        _buildCommit = buildCommit;
    }

    /**
     * Gets the allowlist and fingerprint of the running build.
     * @return the build
     */
    public static SaveBuild current() {
        SaveBuild b = _current;
        if (b == null) {
            synchronized (SaveBuild.class) {
                b = _current;
                if (b == null) {
                    b = load();
                    _current = b;
                }
            }
        }
        return b;
    }

    private static SaveBuild load() {
        try (InputStream list = SaveBuild.class.getResourceAsStream("/save/kryo-classes.txt");
             InputStream props = SaveBuild.class.getResourceAsStream("/save/save-build.properties")) {
            if (list == null || props == null) {
                throw new IllegalStateException("save/kryo-classes.txt is missing; it is written by the build (KryoClassListGenerator)");
            }
            List<String> names = new String(list.readAllBytes(), StandardCharsets.UTF_8).lines().filter(l -> !l.isEmpty()).toList();
            Properties p = new Properties();
            p.load(new InputStreamReader(props, StandardCharsets.UTF_8));
            return new SaveBuild(names, p.getProperty("classFingerprint"), p.getProperty("buildCommit", "unknown"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<String> getClassNames() {
        return _classNames;
    }

    /** Identifies this exact build's classes; a snapshot is only read by a build with the same fingerprint. */
    public String getClassFingerprint() {
        return _classFingerprint;
    }

    public String getBuildCommit() {
        return _buildCommit;
    }
}
