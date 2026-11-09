package com.gempukku.swccgo.async.oidc;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/** Review H1: swccg-login is told *why* (a reset makes it drop passkeys and links). */
public class SsoRevokerTest {
    @Test
    public void reasonIsSentAsAFixedWord() {
        assertEquals("{\"gemp_id\":42,\"reason\":\"reset\"}", new String(SsoRevoker.body(42, "reset"), StandardCharsets.UTF_8));
        assertEquals("{\"gemp_id\":7,\"reason\":\"temp_ban\"}", new String(SsoRevoker.body(7, "temp_ban"), StandardCharsets.UTF_8));
        assertEquals("{\"gemp_id\":7,\"reason\":\"gemp_admin\"}", new String(SsoRevoker.body(7, "x\",\"y"), StandardCharsets.UTF_8));
        assertEquals("{\"gemp_id\":7,\"reason\":\"gemp_admin\"}", new String(SsoRevoker.body(7, null), StandardCharsets.UTF_8));
    }
}
