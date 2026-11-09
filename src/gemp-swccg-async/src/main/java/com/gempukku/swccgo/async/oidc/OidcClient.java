package com.gempukku.swccgo.async.oidc;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

/** swccg-login endpoints (from discovery), the token call, and PKCE helpers. */
public class OidcClient {
    private final OidcConfig _cfg;
    private final HttpClient _http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NEVER).build();
    private final SecureRandom _random = new SecureRandom();
    private volatile JSONObject _discovery;
    private volatile JwtVerifier _verifier;

    public OidcClient(OidcConfig cfg) {
        _cfg = cfg;
    }

    public OidcConfig config() {
        return _cfg;
    }

    public synchronized JSONObject discovery() throws Exception {
        if (_discovery == null) {
            JSONObject d = (JSONObject) new JSONParser().parse(get(_cfg.issuer + "/.well-known/openid-configuration"));
            if (!_cfg.issuer.equals(d.get("issuer")))
                throw new IllegalStateException("discovery issuer mismatch");
            for (String k : new String[]{"authorization_endpoint", "token_endpoint", "jwks_uri"})
                if (!(d.get(k) instanceof String) || !((String) d.get(k)).startsWith("https://"))
                    throw new IllegalStateException("discovery: bad " + k);
            _discovery = d;
        }
        return _discovery;
    }

    public JwtVerifier verifier() throws Exception {
        if (_verifier == null) {
            String jwks = (String) discovery().get("jwks_uri");
            _verifier = new JwtVerifier(_cfg.issuer, _cfg.clientId, () -> {
                try {
                    return get(jwks);
                } catch (Exception e) {
                    return "{\"keys\":[]}";
                }
            });
        }
        return _verifier;
    }

    public String random() {
        byte[] b = new byte[32];
        _random.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    public static String challenge(String verifier) throws Exception {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(
                MessageDigest.getInstance("SHA-256").digest(verifier.getBytes(StandardCharsets.US_ASCII)));
    }

    public String authorizeUrl(String state, String nonce, String verifier) throws Exception {
        return discovery().get("authorization_endpoint") + "?response_type=code"
                + "&client_id=" + enc(_cfg.clientId)
                + "&redirect_uri=" + enc(_cfg.redirectUri)
                + "&scope=" + enc("openid profile")
                + "&state=" + enc(state) + "&nonce=" + enc(nonce)
                + "&code_challenge=" + enc(challenge(verifier)) + "&code_challenge_method=S256";
    }

    /** Exchanges the code (client_secret_basic + PKCE) and returns the raw ID token. */
    public String exchange(String code, String verifier) throws Exception {
        String body = "grant_type=authorization_code&code=" + enc(code) + "&redirect_uri=" + enc(_cfg.redirectUri)
                + "&code_verifier=" + enc(verifier);
        String basic = Base64.getEncoder().encodeToString((enc(_cfg.clientId) + ":" + enc(_cfg.clientSecret)).getBytes(StandardCharsets.UTF_8));
        HttpRequest req = HttpRequest.newBuilder(URI.create((String) discovery().get("token_endpoint")))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + basic)
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpResponse<String> resp = _http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200)
            throw new IllegalStateException("token endpoint status " + resp.statusCode());
        Object idt = ((JSONObject) new JSONParser().parse(resp.body())).get("id_token");
        if (!(idt instanceof String))
            throw new IllegalStateException("no id_token");
        return (String) idt;
    }

    private String get(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10)).GET().build();
        HttpResponse<String> resp = _http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200)
            throw new IllegalStateException("GET status " + resp.statusCode());
        return resp.body();
    }

    static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
