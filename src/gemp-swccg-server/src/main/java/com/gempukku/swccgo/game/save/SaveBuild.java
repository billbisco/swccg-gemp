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
    private final String _classListFingerprint;

    SaveBuild(List<String> classNames, String classFingerprint, String buildCommit) {
        _classNames = List.copyOf(classNames);
        _classFingerprint = classFingerprint;
        _buildCommit = buildCommit;
        _classListFingerprint = fingerprintOf(_classNames);
    }

    /** SHA-256 (first 16 hex digits) of the class list, one name per line, in id order. */
    static String fingerprintOf(List<String> classNames) {
        try {
            java.security.MessageDigest sha = java.security.MessageDigest.getInstance("SHA-256");
            for (String name : classNames) {
                sha.update(name.getBytes(StandardCharsets.UTF_8));
                sha.update((byte) '\n');
            }
            return java.util.HexFormat.of().formatHex(sha.digest()).substring(0, 16);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
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

    /** Identifies the Kryo class list, and so every class id a snapshot uses. Recorded in each save's header. */
    public String getClassListFingerprint() {
        return _classListFingerprint;
    }

    public String getBuildCommit() {
        return _buildCommit;
    }
}
