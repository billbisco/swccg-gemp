# GEMP ⇄ wiki auth API contract — `GEMP-AUTH-V1`

Status: **draft for review** (Alpha4, 2026-10-19; rev 2: stacked on Alpha3's `security/a-password-hashing`, PR #284). Wiki side: `ops/wiki-config/extensions/GempAuth` (swccg-wiki).
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
- `username` (required, 1–64 chars): as typed. GEMP matches names **case-insensitively** (PR #284).
- `password` (required, 1–1024 chars).
- `client_ip` (**required**): the end user's IP as MediaWiki resolves it (`WebRequest::getIP()` through the
  trusted proxy in `$wgCdnServersNoPurge`). It is inside the body, so it is covered by the HMAC (§3: the body's
  SHA-256 is in the canonical string). GEMP passes it to `AuthService` for the per-IP throttle and never uses
  the TCP peer (always the wiki container). Format `[0-9A-Fa-f:.]{2,45}`; missing or malformed → 400.

GEMP checks credentials with `com.gempukku.swccgo.service.AuthService.verifyCredentials(login, password, client_ip)`,
the same instance (from `ServerBuilder`'s object map) the game login uses: one hash format (PBKDF2, legacy
SHA-256 rehashed on success), one reset-code check, one ban check and one throttle shared with play.swccg.com.

Status mapping. **Only `OK` logs in**; every other status is a refusal:

| `AuthService.Status` | HTTP | Body | Wiki behaviour (message key) |
|---|---|---|---|
| `OK` | 200 | `{"ok":true,"user":{"id":42,"name":"Bill","admin":false}}` — `name` is `getCanonicalName()` (stored case) | Pass; wiki name = MediaWiki canonical form of `name` (not what was typed); auto-create + link on first login |
| `INVALID` | 200 | `{"ok":false,"error":"invalid_credentials"}` | "Incorrect username or password" (`wrongpassword`) |
| `RESET_REQUIRED` | 200 | `{"ok":false,"error":"reset_required"}` | The password was a valid one-time reset code. **Never a login.** Tell the user to finish the reset on GEMP (`gempauth-reset-required`) |
| `BANNED` | 200 | `{"ok":false,"error":"banned"}` | `gempauth-banned` |
| `TEMP_BANNED` | 200 | `{"ok":false,"error":"temp_banned","until":1760003600}` (`bannedUntil`, Unix seconds) | `gempauth-temp-banned` with the date |
| `DEACTIVATED` | 200 | `{"ok":false,"error":"deactivated"}` | `gempauth-deactivated` |
| `THROTTLED` | 200 | `{"ok":false,"error":"throttled"}` | `gempauth-throttled` (wiki assumes 5 min) |
| — (bot account `~…`) | 200 | `invalid_credentials` (not looked up) | `wrongpassword` |
| — (§5 endpoint limit) | 429 | `{"ok":false,"error":"throttled","retry_after":120}` | `gempauth-throttled` with the wait |

`INVALID` covers unknown names, wrong passwords and blank stored hashes (an old-style admin reset never
verifies in `AuthService`). `DEACTIVATED`/`BANNED`/`TEMP_BANNED` are only returned after a correct password,
so they do not reveal anything to someone without it. An `OK` without a player is treated as `invalid_credentials`.

Transport errors (wiki shows `gempauth-unavailable`):

| HTTP | Body |
|---|---|
| 400 | `bad_request` (malformed JSON or fields, missing `client_ip`) |
| 401 | `bad_signature` \| `stale_timestamp` \| `replayed_nonce` (check secret/clock) |
| 404 / 405 / 413 | `not_found` / `method_not_allowed` / `body_too_large` |
| 503 | `unavailable` (DB error etc.) |

All responses are `application/json`, `Cache-Control: no-store`, signed as in §3. Unknown `error` codes are
treated by the wiki as `unavailable` (so the old v1-draft `password_reset_required` / `rate_limited` are gone).

## 5. Rate limiting

Two layers, both in memory, per GEMP process:

1. **`AuthService`'s `LoginThrottle`** (Alpha3, shared with the game login): 10 failures per account in 5 minutes
   and 50 failures per IP in 15 minutes → `THROTTLED`. Uses the signed `client_ip`.
2. **This endpoint (second layer)**, checked before `AuthService` is called (a limited request does no
   hashing and no DB work) → HTTP 429 `throttled`:

| Key | Limit | Counts |
|---|---|---|
| Account (username, case-folded) | 10 per 15 min | `invalid_credentials` only; cleared on success |
| End-user IP (`client_ip`) | 30 per 5 min | all attempts |
| Global (the wiki as a caller) | 600 per min | all attempts |

The wiki adds its own layers (`$wgPasswordAttemptThrottle`, ConfirmEdit `badlogin` captcha, nginx account-POST limit).
Per-account lockout can be triggered by someone else (≤ 15 min); accepted trade-off.

## 6. Logging rules (both sides)

- Never log the request body, the password, the secret or signatures.
- GEMP logs one line per decision: `wiki-auth: verify user=<sanitized name> ip=<client_ip> result=<code>`.
- The wiki logs to channel `GempAuth` (link mismatches, bad signatures, HTTP errors) without passwords.
- The GEMP access log on the public port is unaffected; the internal port has no access log.

## 7. Wiki account mapping

- Wiki name = MediaWiki canonical form (first letter upper-cased, `_` → space) of GEMP's `getCanonicalName()`,
  not of what was typed. Typing `BILL` logs in as wiki `Bill` when GEMP's stored name is `Bill`.
- First successful login auto-creates the wiki account and stores the GEMP id in user option `gempauth-id` (not a registered preference, so users cannot set it via the API).
- Later logins require the same GEMP id. If two legacy GEMP accounts map to one wiki name (see Alpha3's
  `report_username_case_collisions.sql`), only the first linked one gets in; the other is refused
  (`gempauth-link-mismatch`) and an admin resolves it with `linkGempAccount.php`.
- Existing local wiki accounts that are not linked (Admin, Holocron Claude, Datacard Grok, Comlink Cursor, Abuse filter, …)
  never consult GEMP, whatever case is typed:
  - exact wiki name → the wiki's own password check (GempAuth abstains);
  - a case variant (`ADMIN`, `aDMIN` for `Admin`) → refused on the wiki (`gempauth-local-case-variant`)
    **before** GEMP is called, so the password is never sent. The lookup is
    `LOWER(CONVERT(user_name USING utf8mb4)) = lower(typed)` on MariaDB/MySQL (`user_name` is `VARBINARY`,
    where plain `LOWER()` does nothing), value passed through `addQuotes()`.
  - After GEMP answers OK, the wiki checks again that GEMP's canonical name does not map onto an unlinked
    account (`gempauth-link-mismatch`).
- Case variants of a **linked** account are fine: GEMP is asked and its canonical name selects the account.

## 8. Versioning

Breaking changes get a new path (`/internal/v2/…`) and a new prefix (`GEMP-AUTH-V2`). Additive response
fields are allowed in v1; clients must ignore unknown fields.
