package com.gempukku.swccgo.game;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import static org.junit.jupiter.api.Assertions.*;

/** Close account (#524): the old name leaves the replay, card names that contain it stay. */
public class ReplayNameRewriterTest {
    @TempDir
    Path dir;

    private static void write(File f, String xml) throws IOException {
        try (OutputStream o = new DeflaterOutputStream(new FileOutputStream(f))) {
            o.write(xml.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static String read(File f) throws IOException {
        try (InputStream i = new InflaterInputStream(new FileInputStream(f))) {
            return new String(i.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void rewritesNamesNotCardNames() throws Exception {
        //test1: participant, zone owner and list attributes are replaced exactly
        //test2: the name in message text is replaced, card hints and longer words are not
        //test3: a second run changes nothing (retry-safe)
        File f = dir.resolve("r.xml.gz").toFile();
        write(f, "<gameReplay>"
                + "<ge type='P' participantId='Luke' allParticipantIds='Luke,Vader'/>"
                + "<ge type='C' zoneOwnerId='Luke' playerAttacking='Vader' playerDefending='Luke'/>"
                + "<ge type='M' message=\"Luke deploys &lt;div class='cardHint' value='1_1'&gt;Luke Skywalker&lt;/div&gt;. Lukewarm.\"/>"
                + "<ge type='P' participantId='Lukas'/>"
                + "</gameReplay>");
        assertTrue(ReplayNameRewriter.rewrite(f, "Luke", "Mynock 3901"));
        String out = read(f);
        assertTrue(out.contains("participantId=\"Mynock 3901\""), out); //test1
        assertTrue(out.contains("allParticipantIds=\"Mynock 3901,Vader\""), out); //test1
        assertTrue(out.contains("zoneOwnerId=\"Mynock 3901\""), out); //test1
        assertTrue(out.contains("playerDefending=\"Mynock 3901\""), out); //test1
        assertTrue(out.contains("Mynock 3901 deploys"), out); //test2
        assertTrue(out.contains("Luke Skywalker"), out); //test2
        assertTrue(out.contains("Lukewarm"), out); //test2
        assertTrue(out.contains("participantId=\"Lukas\""), out); //test2
        assertFalse(ReplayNameRewriter.rewrite(f, "Luke", "Mynock 3901")); //test3
    }
}
