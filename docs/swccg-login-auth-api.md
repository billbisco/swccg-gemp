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
| `/internal/v2/password` | `{id,password,client_ip,end_sessions?}`. Sets a new password by player id for swccg-login's self-service reset, after the player has proved a confirmed email. It burns any admin one-time code. With `end_sessions:true` it also logs the player out of every GEMP session (classic and swccg.com); games and tables are untouched, and the player signs in again to rejoin. **Off unless `GEMP_INTERNAL_AUTH_V2_SET_PASSWORD=true`** (otherwise 404). Errors: `weak_password`, `not_found`. Limit: 5 per account per hour. | `{ok:true,user:{id,name},sessions_ended}` |
| `/internal/v2/close` | `{id,client_ip?}`. A member closes their own account from swccg-login's account page (swccg-guild/work#261). GEMP deactivates the player (nothing is deleted; an admin can reactivate) and logs them out of every GEMP session. Bots can't be closed. Closing twice is fine. Errors: `not_found`. An empty body answers a signed 400, which swccg-login uses to see that the call exists. | `{ok:true,user:{id,name}}` |
| `/internal/v2/rename` | `{id,username,client_ip?}`. Renames a player from swccg-login's account page or an approved rename request (swccg-guild/work#261). Same name rules as signup; a change of case to one's own name is allowed. Refused while the player is at a table, waiting or playing (`busy`). League and tournament entries and results, transfers and saved Play-now games move to the new name; game history keeps the name that was at the table and is tied to the player by `winner_id` / `loser_id` (#264). Ends the player's GEMP sessions under the old name. Errors: `name_invalid`, `name_taken`, `busy`, `deactivated`, `not_found`, `throttled` (5 per hour per player). An empty body answers a signed 400. | `{ok:true,user:{id,name}}` |

Errors: `{ok:false,error}` with `invalid_credentials`, `banned`, `inactive`,
`not_found`, `name_taken`, `name_invalid`, `signup_closed`, `rate_limited`,
`bad_request`. There is **no admin field**; swccg-login never infers roles.

Rate limits (in-memory): 1200/min overall, 30 per 5 min per client IP, 10 failures
per 15 min per username, 5 sign-ups per hour per IP. Sign-ups honour the
“new account registration” setting.

**Play's own classic signup (GEMP → swccg-login, #261).** When `SWCCG_LOGIN_REVOKE_URL`
and `SWCCG_LOGIN_REVOKE_SECRET` are set, Play's classic signup asks swccg-login first:
a signed GEMP-AUTH-V2 `POST /internal/v1/name-check` `{name}` (same listener and key id
`gemp` as revoke; the URL is the revoke URL's host with that path). A reserved name,
a name on its 90-day hold after a rename or close, or a name swccg-login has issued is
refused; so is any answer that is unsigned, not 200, or late (fail closed, "try again
in a few minutes"). The guest seat names "Light Side", "Light_Side", "Dark Side" and
"Dark_Side" (any case) are refused on Play even without swccg-login. A space and an
underscore are different names (2026-11-11): "Luke Sky" and "Luke_Sky" are two accounts.

**Name rule on Play (2026-11-11, t122u).** New names (classic signup, `/internal/v2/register`,
`/internal/v2/players`, `/internal/v2/rename`) follow `PlayerNames`: 1–32 characters; letters and digits
from any writing system, space, `-`, `_`; no space or `_` at either end or two in a row. Play stores
names as 3-byte UTF-8, so characters outside the Basic Multilingual Plane are `name_invalid`.
`player.name`, `tournament_player.player` and `player_password_reset.created_by` are 32 characters
(`db-scripts/update_full_names.sql`). Existing names are grandfathered; sign-in is unchanged. Classic name + password
login is unchanged.

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
| `SWCCG_LOGIN_REVOKE_URL` / `SWCCG_LOGIN_REVOKE_SECRET` | optional: tell swccg-login to end sessions on ban/deactivate/reset. The body names the reason (`reset`, `deactivate`, `ban`, `temp_ban`); on `reset` swccg-login also removes the account's passkeys and connected accounts. Failed calls are retried after 2 s, 10 s and 60 s |

Routes: `GET /gemp-swccg-server/oidc/status` (`{"enabled":true}`; the login page shows its
"Sign in with swccg.com" button only when this answers),
`GET /gemp-swccg-server/oidc/start` (sends the browser to swccg-login with
state, nonce and PKCE S256, bound to a `gempOidcBind` cookie; at most 20 open starts per
client IP (429 beyond), and the oldest are evicted when 20 000 are open),
`GET /gemp-swccg-server/oidc/callback` (checks state, browser binding and `iss`,
exchanges the code with client_secret_basic, verifies the RS256 ID token,
re-checks the account with GEMP and sets the normal session cookie), and
`POST /gemp-swccg-server/oidc/backchannel-logout` (OIDC back-channel logout:
logs the player out of GEMP; `/oidc/backchannel` is kept as an alias).

`sub` is the GEMP player id. The ID-token verifier uses the JDK only (no new
dependency): RS256 only, RSA ≥ 2048, kid required, JWKS refreshed at most once a
minute, `iss`/`aud`/`azp`/`exp`/`iat`/`nonce` checked.

## Rollback

Unset `SWCCG_LOGIN_ENABLED` (or `GEMP_INTERNAL_AUTH_V2_KEYS`) and restart. Classic
login never depended on either.
