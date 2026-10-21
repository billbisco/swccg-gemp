package com.gempukku.swccgo.security;

import java.net.URI;
import java.util.regex.Pattern;

/**
 * CSRF guard for cookie-authenticated requests.
 *
 * <p>Browsers send {@code Origin} on cross-site POSTs (and on same-site ones in current browsers),
 * and usually {@code Referer}. For a state-changing request we require that whichever of the two is
 * present belongs to an allowed site. A request with neither comes from a non-browser client (curl,
 * scripts, the test client); those cannot ride a victim's cookies, so they are let through.
 * Safe methods (GET/HEAD/OPTIONS) only get the Origin check GEMP always had.</p>
 *
 * <p>Together with {@code SameSite=Lax} on the session cookie this blocks the "admin visits a
 * malicious page" attacks on the admin endpoints.</p>
 */
public final class OriginPolicy {
    /**
     * swccg.com, starwarsccg.com and swccggemp.com (any subdomain, https), plus localhost for dev.
     * Override with {@code origin.allowed.pattern}.
     */
    public static final String DEFAULT_PATTERN =
            "^https://([a-z0-9-]+\\.)*(swccg\\.com|starwarsccg\\.com|swccggemp\\.com)$"
                    + "|^https?://(localhost|127\\.0\\.0\\.1)(:\\d+)?$";

    private final Pattern _allowed;

    public OriginPolicy(String pattern) {
        String p = pattern == null || pattern.isBlank() || pattern.equals("^.*$") ? DEFAULT_PATTERN : pattern;
        _allowed = Pattern.compile(p, Pattern.CASE_INSENSITIVE);
    }

    public boolean isAllowedOrigin(String origin) {
        return origin != null && _allowed.matcher(origin.trim()).matches();
    }

    /**
     * @param method HTTP method name
     * @param origin Origin header or null
     * @param referer Referer header or null
     */
    public boolean allows(String method, String origin, String referer) {
        if (origin != null && !origin.equals("null"))
            return isAllowedOrigin(origin);
        boolean unsafe = !("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method));
        if (!unsafe)
            return true;
        if ("null".equals(origin))
            return false; // sandboxed iframe / data: URL posting a form
        if (referer != null)
            return isAllowedOrigin(originOf(referer));
        return true;
    }

    /**
     * The stricter check for the guest (Play now) endpoints, which anyone can call without an account. Like
     * {@link #allows}, but a state-changing request must carry an Origin or a Referer from an allowed site: one with
     * neither is refused instead of being treated as a non-browser client.
     */
    public boolean allowsOnlyFromOurPages(String method, String origin, String referer) {
        if (!allows(method, origin, referer))
            return false;
        boolean unsafe = !("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method));
        return !unsafe || origin != null || referer != null;
    }

    static String originOf(String url) {
        try {
            URI u = new URI(url.trim());
            if (u.getScheme() == null || u.getHost() == null)
                return null;
            return u.getScheme() + "://" + u.getHost() + (u.getPort() != -1 ? ":" + u.getPort() : "");
        } catch (Exception e) {
            return null;
        }
    }
}
