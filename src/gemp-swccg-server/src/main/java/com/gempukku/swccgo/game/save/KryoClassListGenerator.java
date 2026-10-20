package com.gempukku.swccgo.game.save;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * Build step (exec-maven-plugin, process-classes): writes the Kryo allowlist for game saves and the build fingerprint.
 * <ul>
 * <li>{@code save/kryo-classes.txt}: every GEMP class on the classpath, the JDK collection classes a game holds, and the
 * array types GEMP fields and parameters use. Sorted; a class's Kryo id is its line number.</li>
 * <li>{@code save/save-build.properties}: {@code classFingerprint}, a SHA-256 of every GEMP class file and the list, and
 * {@code buildCommit}. A snapshot is only read by a build with the same fingerprint.</li>
 * </ul>
 * Args: output directory, repository root (for the commit).
 */
public final class KryoClassListGenerator {
    static final String PACKAGE_PREFIX = "com/gempukku/";

    private static final String[] JDK_NESTED_OF = {"java.util.Collections", "java.util.ImmutableCollections", "java.util.Arrays"};
    private static final String[] JDK_CLASSES = {
            "java.util.ArrayList", "java.util.LinkedList", "java.util.HashMap", "java.util.LinkedHashMap", "java.util.TreeMap",
            "java.util.HashSet", "java.util.LinkedHashSet", "java.util.TreeSet", "java.util.ArrayDeque", "java.util.Stack", "java.util.Vector",
            "java.util.EnumMap", "java.util.RegularEnumSet", "java.util.JumboEnumSet", "java.util.IdentityHashMap", "java.util.PriorityQueue",
            "java.util.HashMap$KeySet", "java.util.HashMap$Values", "java.util.HashMap$EntrySet",
            "java.util.LinkedHashMap$LinkedKeySet", "java.util.LinkedHashMap$LinkedValues", "java.util.LinkedHashMap$LinkedEntrySet",
            "java.util.TreeMap$KeySet", "java.util.TreeMap$Values", "java.util.TreeMap$EntrySet", "java.util.Random",
            "java.util.concurrent.ConcurrentHashMap", "java.util.concurrent.CopyOnWriteArrayList",
            "java.util.concurrent.atomic.AtomicLong", "java.util.concurrent.atomic.AtomicInteger",
            "java.util.concurrent.atomic.AtomicBoolean", "java.util.concurrent.atomic.AtomicReference",
            "[Ljava.lang.Object;", "[Ljava.lang.String;", "[Ljava.lang.Integer;", "[I", "[J", "[Z", "[F", "[D"};

    private KryoClassListGenerator() {
    }

    public static void main(String[] args) throws Exception {
        Path out = Path.of(args[0]);
        Path repoRoot = args.length > 1 ? Path.of(args[1]) : null;
        Map<String, byte[]> classFiles = gempClassFiles();
        List<String> names = classList(classFiles.keySet());
        Files.createDirectories(out);
        Files.write(out.resolve("kryo-classes.txt"), names, StandardCharsets.UTF_8);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        for (Map.Entry<String, byte[]> e : classFiles.entrySet()) {
            digest.update(e.getKey().getBytes(StandardCharsets.UTF_8));
            digest.update(e.getValue());
        }
        digest.update(String.join("\n", names).getBytes(StandardCharsets.UTF_8));
        digest.update(System.getProperty("java.version").getBytes(StandardCharsets.UTF_8));
        Properties p = new Properties();
        p.setProperty("classFingerprint", HexFormat.of().formatHex(digest.digest(), 0, 16));
        p.setProperty("buildCommit", repoRoot == null ? "unknown" : gitCommit(repoRoot));
        p.setProperty("classCount", Integer.toString(names.size()));
        try (var w = Files.newBufferedWriter(out.resolve("save-build.properties"), StandardCharsets.UTF_8)) {
            p.store(w, null);
        }
        System.out.println("kryo class list: " + names.size() + " classes, fingerprint " + p.getProperty("classFingerprint"));
    }

    /** GEMP class files on the classpath (directories and jars), by class name, sorted. */
    static Map<String, byte[]> gempClassFiles() throws IOException {
        Map<String, byte[]> files = new TreeMap<>();
        for (String entry : System.getProperty("java.class.path").split(java.io.File.pathSeparator)) {
            collect(Path.of(entry), files);
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl instanceof java.net.URLClassLoader ucl) { // exec:java puts the project classpath on a URLClassLoader
            for (java.net.URL url : ucl.getURLs()) {
                if (url.getProtocol().equals("file")) {
                    collect(Path.of(java.net.URI.create(url.toString())), files);
                }
            }
        }
        return files;
    }

    private static void collect(Path root, Map<String, byte[]> files) throws IOException {
        if (Files.isDirectory(root)) {
            try (Stream<Path> walk = Files.walk(root)) {
                for (Path f : (Iterable<Path>) walk::iterator) {
                    String rel = root.relativize(f).toString().replace('\\', '/');
                    if (rel.startsWith(PACKAGE_PREFIX) && rel.endsWith(".class")) {
                        files.putIfAbsent(className(rel), Files.readAllBytes(f));
                    }
                }
            }
        } else if (root.toString().endsWith(".jar") && Files.isRegularFile(root)) {
            try (JarFile jar = new JarFile(root.toFile())) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry e = entries.nextElement();
                    if (e.getName().startsWith(PACKAGE_PREFIX) && e.getName().endsWith(".class")) {
                        try (InputStream in = jar.getInputStream(e)) {
                            files.putIfAbsent(className(e.getName()), in.readAllBytes());
                        }
                    }
                }
            }
        }
    }

    private static String className(String path) {
        return path.substring(0, path.length() - ".class".length()).replace('/', '.');
    }

    /** The sorted allowlist for the given GEMP class names. */
    static List<String> classList(Iterable<String> gempClasses) throws ClassNotFoundException {
        TreeSet<String> names = new TreeSet<>();
        for (String n : gempClasses) {
            names.add(n);
        }
        for (String outer : JDK_NESTED_OF) {
            for (Class<?> c : Class.forName(outer).getDeclaredClasses()) {
                names.add(c.getName());
            }
        }
        names.addAll(List.of(JDK_CLASSES));
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        for (String n : new ArrayList<>(names)) {
            if (!n.startsWith("com.gempukku.")) {
                continue;
            }
            try {
                Class<?> c = Class.forName(n, false, cl);
                for (Field f : c.getDeclaredFields()) {
                    addArray(names, f.getType());
                }
                // varargs arrays end up inside Arrays$ArrayList and Object fields
                for (Method m : c.getDeclaredMethods()) {
                    for (Class<?> t : m.getParameterTypes()) {
                        addArray(names, t);
                    }
                }
                for (Constructor<?> m : c.getDeclaredConstructors()) {
                    for (Class<?> t : m.getParameterTypes()) {
                        addArray(names, t);
                    }
                }
            } catch (Throwable t) {
                // a class that can't be linked here (optional dependency) can't be in a game either
            }
        }
        return new ArrayList<>(names);
    }

    private static void addArray(TreeSet<String> names, Class<?> t) {
        if (t.isArray()) {
            names.add(t.getName());
        }
    }

    /** The checked-out commit, read from .git without running git. */
    static String gitCommit(Path repoRoot) {
        try {
            Path git = repoRoot.resolve(".git");
            if (Files.isRegularFile(git)) { // worktree: "gitdir: <path>"
                git = repoRoot.resolve(Files.readString(git).trim().substring("gitdir:".length()).trim());
            }
            String head = Files.readString(git.resolve("HEAD")).trim();
            if (!head.startsWith("ref: ")) {
                return head;
            }
            String ref = head.substring(5);
            Path common = Files.isRegularFile(git.resolve("commondir")) ? git.resolve(Files.readString(git.resolve("commondir")).trim()) : git;
            for (Path dir : new Path[]{git, common}) {
                if (Files.isRegularFile(dir.resolve(ref))) {
                    return Files.readString(dir.resolve(ref)).trim();
                }
            }
            for (Path dir : new Path[]{git, common}) {
                Path packed = dir.resolve("packed-refs");
                if (Files.isRegularFile(packed)) {
                    for (String line : Files.readAllLines(packed)) {
                        if (line.endsWith(" " + ref)) {
                            return line.substring(0, line.indexOf(' '));
                        }
                    }
                }
            }
        } catch (Exception e) {
            // fall through
        }
        return "unknown";
    }
}
