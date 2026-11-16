package com.gempukku.swccgo.game;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Close account (swccg-guild/work#524): replaces a player's old name with the placeholder in saved replays.
 * Attributes that hold a player name are replaced when they equal the old name (comma lists item by item).
 * In message text, the old name is replaced as a whole word outside HTML tags and outside card hints, so a
 * player called "Luke" doesn't turn "Luke Skywalker" card names into the placeholder. Files are written to a
 * temporary file and moved into place, so a crash never leaves a half-written replay. Rewriting a file that
 * no longer holds the old name changes nothing (safe to retry).
 */
public final class ReplayNameRewriter {
    private static final Set<String> NAME_ATTRIBUTES = Set.of("participantId", "zoneOwnerId", "playerAttacking", "playerDefending");
    private static final String LIST_ATTRIBUTE = "allParticipantIds";
    private static final String MESSAGE_ATTRIBUTE = "message";
    /** Card hints and other markup: never rewritten. */
    private static final Pattern PROTECTED = Pattern.compile("<div[^>]*class='cardHint'[^>]*>.*?</div>|<[^>]*>", Pattern.DOTALL);

    private ReplayNameRewriter() {
    }

    /** @return true if the file changed */
    public static boolean rewrite(File file, String oldName, String newName) throws Exception {
        if (oldName == null || oldName.isEmpty() || oldName.equals(newName) || !file.isFile())
            return false;
        Document doc;
        try (InputStream in = new InflaterInputStream(new FileInputStream(file))) {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            f.setExpandEntityReferences(false);
            doc = f.newDocumentBuilder().parse(in);
        }
        boolean changed = false;
        NodeList all = doc.getElementsByTagName("*");
        for (int i = 0; i < all.getLength(); i++) {
            Element e = (Element) all.item(i);
            NamedNodeMap attrs = e.getAttributes();
            for (int a = 0; a < attrs.getLength(); a++) {
                Node attr = attrs.item(a);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                String updated = value;
                if (NAME_ATTRIBUTES.contains(name)) {
                    if (value.equals(oldName))
                        updated = newName;
                } else if (LIST_ATTRIBUTE.equals(name)) {
                    String[] parts = value.split(",", -1);
                    for (int p = 0; p < parts.length; p++)
                        if (parts[p].equals(oldName))
                            parts[p] = newName;
                    updated = String.join(",", parts);
                } else if (MESSAGE_ATTRIBUTE.equals(name)) {
                    updated = replaceInText(value, oldName, newName);
                }
                if (!updated.equals(value)) {
                    attr.setNodeValue(updated);
                    changed = true;
                }
            }
        }
        if (!changed)
            return false;
        File tmp = new File(file.getParentFile(), file.getName() + ".erase-tmp");
        try (OutputStream out = new DeflaterOutputStream(new FileOutputStream(tmp), new Deflater(9))) {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.transform(new DOMSource(doc), new StreamResult(out));
        }
        Files.move(tmp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        return true;
    }

    /** Whole-word replacement outside markup and card hints. */
    static String replaceInText(String text, String oldName, String newName) {
        if (text == null || !text.contains(oldName))
            return text;
        Pattern word = Pattern.compile("(?<![\\p{L}\\p{N}_])" + Pattern.quote(oldName) + "(?![\\p{L}\\p{N}_])");
        StringBuilder sb = new StringBuilder();
        Matcher m = PROTECTED.matcher(text);
        int last = 0;
        while (m.find()) {
            sb.append(word.matcher(text.substring(last, m.start())).replaceAll(Matcher.quoteReplacement(newName)));
            sb.append(m.group());
            last = m.end();
        }
        sb.append(word.matcher(text.substring(last)).replaceAll(Matcher.quoteReplacement(newName)));
        return sb.toString();
    }
}
