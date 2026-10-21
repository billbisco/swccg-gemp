package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.ResponseWriter;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** Keeps the response a handler wrote, for tests. */
class RecordingResponseWriter implements ResponseWriter {
    int status;
    final Map<String, String> headers = new HashMap<>();
    String body;
    Document xml;

    @Override
    public void writeError(int status) {
        writeError(status, null);
    }

    @Override
    public void writeError(int status, Map<String, String> headers) {
        this.status = status;
        if (headers != null)
            this.headers.putAll(headers);
    }

    @Override
    public void writeFile(File file, Map<String, String> headers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeHtmlResponse(String html) {
        status = 200;
        body = html;
    }

    @Override
    public void writeJsonResponse(String json) {
        status = 200;
        body = json;
    }

    @Override
    public void writeByteResponse(byte[] bytes, Map<? extends CharSequence, String> headers) {
        status = 200;
        body = new String(bytes, StandardCharsets.UTF_8);
        if (headers != null)
            headers.forEach((k, v) -> this.headers.put(k.toString(), v));
    }

    @Override
    public void writeXmlResponse(Document document) {
        writeXmlResponse(document, null);
    }

    @Override
    public void writeXmlResponse(Document document, Map<? extends CharSequence, String> addHeaders) {
        status = 200;
        xml = document;
        if (addHeaders != null)
            addHeaders.forEach((k, v) -> this.headers.put(k.toString(), v));
    }
}
