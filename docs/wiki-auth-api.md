# GEMP ⇄ wiki auth API contract — `GEMP-AUTH-V1`

Status: **draft for review** (Alpha4, 2026-10-19). Wiki side: `ops/wiki-config/extensions/GempAuth` (swccg-wiki).
GEMP side: `gemp-swccg-async/.../async/internal/` (swccg-gemp). Coordinate changes with Alpha3 (GEMP password/session owner).

## 1. Purpose and scope

The wiki (MediaWiki 1.43, `GempAuth` extension) asks GEMP "is this name + password a valid, usable GEMP login?"
so a GEMP account logs in to wiki.swccg.com (approved plan, Level 2). GEMP stays the only owner of passwords
and password hashes. The wiki never reads the GEMP database and never stores GEMP passwords.

In scope (v1): `POST /internal/v1/verify`.
Planned (v1.1, **not implemented**): `POST /internal/v1/set-email` (wiki → GEMP after the wiki confirms an
email, for GEMP self-service reset). Same signing; body `{"gemp_id":123,"email":"…","confirmed_at":1760000000}`.

## 2. Transport and network

| Item | Value |
|---|---|
| Listener | Separate Netty listener inside the GEMP app container, enabled only when `INTERNAL_AUTH_PORT` > 0 **and** `GEMP_INTERNAL_AUTH_SECRET` is set. Recommended port `8090`. |
| Exposure | **Never published to the host** (no `ports:` entry) and never proxied by nginx. The public GEMP port (80) has no `/internal` route at all (404). |
| Reachability | Docker network `swccg-auth` (internal, no egress), joined by `gemp_swccg_app_1` and `swccg_wiki` only. URL from the wiki: `http://gemp_swccg_app_1:8090/internal/v1/verify`. |
| TLS | None on the private bridge network; integrity and authenticity come from the HMAC below. If the endpoint ever crosses hosts, put it behind TLS (mTLS or WireGuard) first. |
| Timeouts | Wiki: connect 2 s, total 5 s. GEMP: read timeout 10 s, max body 4 KB, one request per connection. |
| Redirects / proxies | Wiki never follows redirects and never uses an HTTP proxy for this call. |

## 3. Authentication between wiki and GEMP (HMAC-SHA256)

Shared secret: ≥ 32 random bytes (generate with `openssl rand -base64 48`). Stored only in untracked files:
GEMP `/opt/gemp-swccg/src/.env` (`GEMP_INTERNAL_AUTH_SECRET`), wiki `/opt/swccg-wiki/secrets/gempauth-secret.php`
(`$wgGempAuthSecret = '…';`, mode 0640 root:www-data). Never in git, chat, logs or command lines.

Request headers:

| Header | Value |
|---|---|
| `Content-Type` | `application/json` |
| `X-Gemp-Timestamp` | Unix seconds (decimal) |
| `X-Gemp-Nonce` | 16–64 chars `[A-Za-z0-9_-]`, random per request (wiki uses 32 hex chars) |
| `X-Gemp-Signature` | `v1=` + lowercase hex `HMAC_SHA256(secret, canonical_request)` |

```
canonical_request = "GEMP-AUTH-V1" "\n" timestamp "\n" nonce "\n" METHOD "\n" path "\n" hex(SHA256(raw_body))
```
`path` is the URL path only (`/internal/v1/verify`), no query. `raw_body` is the exact bytes sent.

GEMP rejects (HTTP 401) when: signature missing or wrong (constant-time compare); `|now − timestamp| > 60 s`
(`stale_timestamp`); nonce seen within the last 120 s (`replayed_nonce`).

Response signature (GEMP → wiki), header `X-Gemp-Signature`:
```
canonical_response = "GEMP-AUTH-V1-RESP" "\n" request_nonce "\n" http_status "\n" hex(SHA256(raw_response_body))
```
The wiki treats any response whose signature does not verify as `unavailable` (login refused, not "wrong password").
(GEMP does not sign 401 bad-signature responses, since it could not trust the nonce.)

Rotation: GEMP also accepts `GEMP_INTERNAL_AUTH_SECRET_PREVIOUS` on incoming requests. Rotate by
(1) set new as current + old as previous on GEMP, restart; (2) switch the wiki to the new secret; (3) remove previous.

Known-answer vector (both test suites assert it):
secret `0123456789abcdef0123456789abcdef`, timestamp `1760000000`, nonce `abcdefghijklmnop`,
body `{"username":"Bill","password":"hunter22","client_ip":"203.0.113.7"}` →
`v1=46e1e96f0b78c038fe19bbe9c6fd7214d328d11f5261711b99876c3f7e22abf1`.
Response body `{"ok":false,"error":"invalid_credentials"}`, status 200, same nonce →
`v1=bdc04e5d3a97a420aa22a9394c66ae2a5408d52ff1c6aded80ce6bae78c1486a`.

## 4. `POST /internal/v1/verify`

Request body (UTF-8 JSON, ≤ 4096 bytes):
```json
{ "username": "bill", "password": "…", "client_ip": "203.0.113.7" }
```
- `username` (required, 1–64 chars): **exactly as typed**; GEMP names are case-sensitive (`utf8_bin`).
- `password` (required, 1–1024 chars).
- `client_ip` (optional): the end user's IP as MediaWiki sees it (after the `$wgCdnServersNoPurge` fix). Used only for rate limiting and the GEMP log line. Format `[0-9A-Fa-f:.]{2,45}`, else 400.

GEMP checks credentials through `PlayerDAO.loginPlayer(username, password)` — the same call the game login uses —
so Alpha3's bcrypt/Argon2id lazy-rehash work applies automatically. **Contract requirements on that DAO work:**
it must keep returning `null` for wrong password / unknown / deactivated, keep `getPassword()` empty for
admin-reset accounts (or replace that signal — tell Alpha4), and must not log passwords.

Responses (all `application/json`, `Cache-Control: no-store`, signed as in §3):

| HTTP | Body | Meaning | Wiki behaviour |
|---|---|---|---|
| 200 | `{"ok":true,"user":{"id":42,"name":"bill","admin":false}}` | Valid, usable login. `name` is GEMP's canonical name. | Pass; wiki name = MediaWiki canonical form of `name`; auto-create + link on first login |
| 200 | `{"ok":false,"error":"invalid_credentials"}` | Wrong password, unknown, deactivated, or bot account (indistinguishable on purpose) | "Incorrect username or password" |
| 200 | `{"ok":false,"error":"password_reset_required"}` | Admin-reset account (blank stored password). **Never accepted**, whatever password was sent. | Tell user to set a new password on GEMP |
| 200 | `{"ok":false,"error":"banned"}` | Permanent ban | Refuse with ban message |
| 200 | `{"ok":false,"error":"temp_banned","until":1760003600}` | Temporary ban until Unix seconds | Refuse with date |
| 429 | `{"ok":false,"error":"rate_limited","retry_after":120}` | See §5 | Refuse, show wait |
| 400 | `{"ok":false,"error":"bad_request"}` | Malformed JSON / fields | `unavailable` |
| 401 | `{"ok":false,"error":"bad_signature" \| "stale_timestamp" \| "replayed_nonce"}` | Auth between services failed | `unavailable`; check secret/clock |
| 404 / 405 / 413 | `not_found` / `method_not_allowed` / `body_too_large` | | `unavailable` |
| 503 | `{"ok":false,"error":"unavailable"}` | DB error etc. | `unavailable` |

## 5. Rate limiting (GEMP side, in-memory, per process)

| Key | Limit | Counts |
|---|---|---|
| Account (username, case-folded) | 10 per 15 min | failed attempts only; reset on success |
| End-user IP (`client_ip`, or `-` if absent) | 30 per 5 min | all attempts |
| Global (the wiki as a caller) | 600 per min | all attempts |

Limits are checked before the DB lookup; a limited request does not touch the DB. The wiki adds its own layers
(`$wgPasswordAttemptThrottle`, ConfirmEdit `badlogin` captcha, `$wgAccountCreationThrottle`).
Note: per-account lockout can be triggered by someone else (DoS of one account for ≤ 15 min); accepted trade-off.

## 6. Logging rules (both sides)

- Never log the request body, the password, the secret or signatures.
- GEMP logs one line per decision: `wiki-auth: verify user=<sanitized name> ip=<client_ip> result=<code>`.
- The wiki logs to channel `GempAuth` (link mismatches, bad signatures, HTTP errors) without passwords.
- The GEMP access log on the public port is unaffected; the internal port has no access log.

## 7. Wiki account mapping

- MediaWiki canonical name: first letter upper-cased, `_` → space. The typed name must map to the same wiki name as GEMP's canonical `name`, else refused (`gempauth-name-mismatch`).
- First successful login auto-creates the wiki account and stores the GEMP id in user option `gempauth-id` (not a registered preference, so users cannot set it via the API).
- Later logins require the same GEMP id. GEMP `bill` and `Bill` both map to wiki `Bill`: only the first linked one gets in; the other is refused (`gempauth-link-mismatch`) and an admin resolves it with `linkGempAccount.php`.
- Existing local wiki accounts that are not linked (Admin, Holocron Claude, Datacard Grok, Comlink Cursor, Abuse filter, …) never consult GEMP: their passwords are never sent to GEMP and a GEMP account with the same name cannot take them over.

## 8. Versioning

Breaking changes get a new path (`/internal/v2/…`) and a new prefix (`GEMP-AUTH-V2`). Additive response
fields are allowed in v1; clients must ignore unknown fields.
