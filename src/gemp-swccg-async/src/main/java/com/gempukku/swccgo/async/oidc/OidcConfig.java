package com.gempukku.swccgo.async.oidc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

/**
 * Settings for signing in to GEMP through swccg-login (OpenID Connect). OFF unless
 * SWCCG_LOGIN_ENABLED=true; the classic name + password login always keeps working, so turning
 * this off is the rollback.
 *
 * <pre>
 * SWCCG_LOGIN_ENABLED=true
 * SWCCG_LOGIN_ISSUER=https://login.swccg.com/test
 * SWCCG_LOGIN_CLIENT_ID=gemp-test
 * SWCCG_LOGIN_CLIENT_SECRET_FILE=/run/secrets/swccg-login-client   (owner-only file)
 * SWCCG_LOGIN_REDIRECT_URI=https://test.swccg.com/gemp-swccg-server/oidc/callback
 * SWCCG_LOGIN_AFTER_LOGIN_URL=/gemp-swccg/hall.html                (optional)
 * </pre>
 */
public final class OidcConfig {
    public final String issuer;
    public final String clientId;
    public final String clientSecret;
    public final String redirectUri;
    public final String afterLoginUrl;

    public OidcConfig(String issuer, String clientId, String clientSecret, String redirectUri, String afterLoginUrl) {
        if (issuer == null || !issuer.startsWith("https://") || issuer.endsWith("/"))
            throw new IllegalArgumentException("SWCCG_LOGIN_ISSUER must be https without a trailing slash");
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.length() < 32)
            throw new IllegalArgumentException("SWCCG_LOGIN_CLIENT_ID / client secret missing or too short");
        if (redirectUri == null || !redirectUri.startsWith("https://"))
            throw new IllegalArgumentException("SWCCG_LOGIN_REDIRECT_URI must be https");
        this.issuer = issuer;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.afterLoginUrl = afterLoginUrl == null || afterLoginUrl.isBlank() || !afterLoginUrl.startsWith("/")
                || afterLoginUrl.startsWith("//") ? "/gemp-swccg/hall.html" : afterLoginUrl;
    }

    /** @return the configuration, or null when the feature is off. */
    public static OidcConfig fromEnvironment() throws IOException {
        if (!"true".equalsIgnoreCase(System.getenv("SWCCG_LOGIN_ENABLED")))
            return null;
        String secretFile = System.getenv("SWCCG_LOGIN_CLIENT_SECRET_FILE");
        if (secretFile == null || secretFile.isBlank())
            throw new IllegalArgumentException("SWCCG_LOGIN_CLIENT_SECRET_FILE is not set");
        Path p = Path.of(secretFile);
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(p);
            if (perms.contains(PosixFilePermission.OTHERS_READ) || perms.contains(PosixFilePermission.GROUP_READ))
                throw new IllegalArgumentException("SWCCG_LOGIN_CLIENT_SECRET_FILE must be readable by its owner only");
        } catch (UnsupportedOperationException ignored) {
            // non-POSIX file system: nothing to check
        }
        String secret = Files.readString(p, StandardCharsets.UTF_8).trim();
        return new OidcConfig(System.getenv("SWCCG_LOGIN_ISSUER"), System.getenv("SWCCG_LOGIN_CLIENT_ID"), secret,
                System.getenv("SWCCG_LOGIN_REDIRECT_URI"), System.getenv("SWCCG_LOGIN_AFTER_LOGIN_URL"));
    }
}
