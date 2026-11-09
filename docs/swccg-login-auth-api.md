# swccg-login ↔ GEMP

GEMP stays the source of truth for accounts. swccg-login (login.swccg.com) is an
OpenID Connect provider that checks passwords and account status with GEMP over a
private, signed HTTP API, and GEMP can sign players in through swccg-login as an
OIDC client. Both halves are **off unless configured**; classic GEMP login is unchanged
and stays the rollback.

## 1. Internal API v2 (swccg-login → GEMP)

Served by the existing internal listener (`INTERNAL_AUTH_PORT`, private Docker
network only, never published through nginx). v2 starts when
`GEMP_INTERNAL_AUTH_V2_KEYS` is set:

    GEMP_INTERNAL_AUTH_V2_KEYS="swccg-login-test=<secret>[,<previous secret>];<other-key-id>=<secret>"

Each caller has its own key id and secret (32+ chars). A second secret after a
comma is accepted for verification only, for rotation.

Every request is a POST with JSON and these headers:

| Header | Value |
|---|---|
| `X-Gemp-Key-Id` | the caller's key id |
| `X-Gemp-Timestamp` | Unix seconds, within ±60 s |
| `X-Gemp-Nonce` | 16–64 chars `[A-Za-z0-9_-]`, single use per key id |
| `X-Gemp-Signature` | `v2=` + hex HMAC-SHA256 of the canonical string |

Request canonical: `GEMP-AUTH-V2\n{keyId}\n{ts}\n{nonce}\n{METHOD}\n{path}\n{sha256hex(body)}`.
Responses to signed requests carry `X-Gemp-Signature` over
`GEMP-AUTH-V2-RESP\n{keyId}\n{nonce}\n{status}\n{sha256hex(body)}`; callers must
check it. A known-answer vector is in `InternalAuthKeysV2Test` (same vector in
swccg-login `internal/gemp`).

| Path | Body | Success |
|---|---|---|
| `/internal/v2/verify` | `{username,password,client_ip}` | `{ok:true,user:{id,name}}` |
| `/internal/v2/register` | `{username,password,client_ip}` | `{ok:true,user:{id,name}}` |
| `/internal/v2/players` | `{username,client_ip}` (account with no usable password, for social sign-up) | `{ok:true,user:{id,name}}` |
| `/internal/v2/status` | `{id}` | `{ok:true,user:{id,name}}` |

Errors: `{ok:false,error}` with `invalid_credentials`, `banned`, `inactive`,
`not_found`, `name_taken`, `name_invalid`, `signup_closed`, `rate_limited`,
`bad_request`. There is **no admin field**; swccg-login never infers roles.

Rate limits (in-memory): 1200/min overall, 30 per 5 min per client IP, 10 failures
per 15 min per username, 5 sign-ups per hour per IP. Sign-ups honour the
“new account registration” setting.

## 2. OIDC client (GEMP → swccg-login)

Off unless `SWCCG_LOGIN_ENABLED=true`. When off, or misconfigured,
`/gemp-swccg-server/oidc/*` returns 404 and nothing else changes.

| Variable | Meaning |
|---|---|
| `SWCCG_LOGIN_ENABLED` | `true` to switch on |
| `SWCCG_LOGIN_ISSUER` | e.g. `https://login.swccg.com/test` (https, no trailing slash) |
| `SWCCG_LOGIN_CLIENT_ID` | e.g. `gemp-test` |
| `SWCCG_LOGIN_CLIENT_SECRET_FILE` | file with the client secret, mode 0400/0600 |
| `SWCCG_LOGIN_REDIRECT_URI` | `https://<host>/gemp-swccg-server/oidc/callback` |
| `SWCCG_LOGIN_AFTER_LOGIN_URL` | default `/gemp-swccg/hall.html` |
| `SWCCG_LOGIN_REVOKE_URL` / `SWCCG_LOGIN_REVOKE_SECRET` | optional: tell swccg-login to end sessions on ban/deactivate/reset |

Routes: `GET /gemp-swccg-server/oidc/start` (sends the browser to swccg-login with
state, nonce and PKCE S256, bound to a `gempOidcBind` cookie),
`GET /gemp-swccg-server/oidc/callback` (checks state, browser binding and `iss`,
exchanges the code with client_secret_basic, verifies the RS256 ID token,
re-checks the account with GEMP and sets the normal session cookie), and
`POST /gemp-swccg-server/oidc/backchannel-logout` (OIDC back-channel logout:
logs the player out of GEMP).

`sub` is the GEMP player id. The ID-token verifier uses the JDK only (no new
dependency): RS256 only, RSA ≥ 2048, kid required, JWKS refreshed at most once a
minute, `iss`/`aud`/`azp`/`exp`/`iat`/`nonce` checked.

## Rollback

Unset `SWCCG_LOGIN_ENABLED` (or `GEMP_INTERNAL_AUTH_V2_KEYS`) and restart. Classic
login never depended on either.
