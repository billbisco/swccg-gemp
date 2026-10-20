package com.gempukku.swccgo.async;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * The web client must not load scripts, fonts or other assets from Google-hosted services
 * (analytics, tag manager, fonts, reCAPTCHA, YouTube). Scans every text resource under
 * src/main/web.
 */
public class WebClientGoogleHostsTests {
    private static final Path WEB_ROOT = Paths.get("src", "main", "web");

    private static final Set<String> TEXT_EXTENSIONS = Set.of(
            "html", "htm", "js", "mjs", "css", "inc", "json", "svg", "xml", "txt");

    private static final List<String> BANNED = List.of(
            "googletagmanager.com",
            "google-analytics.com",
            "analytics.google.com",
            "gtag(",
            "gtag/js",
            "datalayer.push",
            "googlesyndication.com",
            "googleadservices.com",
            "doubleclick.net",
            "fonts.googleapis.com",
            "fonts.gstatic.com",
            "ajax.googleapis.com",
            "storage.googleapis.com",
            "gstatic.com",
            "google.com/recaptcha",
            "recaptcha.net",
            "youtube.com",
            "youtube-nocookie.com",
            "youtu.be",
            "ytimg.com");

    @Test
    public void WebClientDoesNotReferenceGoogleHostedAssets() throws IOException {
        assertTrue(Files.isDirectory(WEB_ROOT), "web root not found: " + WEB_ROOT.toAbsolutePath());

        List<Path> files;
        try (Stream<Path> walk = Files.walk(WEB_ROOT)) {
            files = walk.filter(Files::isRegularFile).filter(WebClientGoogleHostsTests::isText).toList();
        }
        assertTrue(files.size() > 10, "expected to scan the web client, found " + files.size() + " text files");

        List<String> found = new ArrayList<>();
        for (Path file : files) {
            List<String> lines = Files.readAllLines(file, StandardCharsets.ISO_8859_1);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).toLowerCase(Locale.ROOT);
                for (String banned : BANNED) {
                    if (line.contains(banned))
                        found.add(WEB_ROOT.relativize(file) + ":" + (i + 1) + " references " + banned);
                }
            }
        }
        if (!found.isEmpty())
            fail("Google-hosted assets referenced in the web client:\n" + String.join("\n", found));
    }

    private static boolean isText(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot >= 0 && TEXT_EXTENSIONS.contains(name.substring(dot + 1).toLowerCase(Locale.ROOT));
    }
}
