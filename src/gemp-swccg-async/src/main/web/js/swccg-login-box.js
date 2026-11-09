/* swccg.com sign-in box (Mockup A). No framework, no build step, ES5 + fetch + Promise.
 *
 * Every sign-in is a TOP-LEVEL navigation to login.swccg.com (form POST or link), never a
 * credentialed cross-origin XHR:
 *   - name + password: POST <issuer>/signin/password   (mode=signin | signup)
 *   - passkey:         GET  <issuer>/signin/passkey/options (credential-less CORS: a challenge only),
 *                      then POST <issuer>/signin/passkey with the signed assertion
 *   - Google, Discord, ...: GET <issuer>/authorize?...&idp=<id> (302s straight to the provider)
 * Each carries this site's own OIDC request (client_id, redirect_uri, state, nonce, PKCE S256).
 * login.swccg.com answers with a 302 to redirect_uri: ?code=&state= on success, or
 * ?error=access_denied&error_description=<reason>&state= on failure. The site's callback finishes
 * the sign-in, or sends the reason back to this page (data-error-param), where it's shown inline.
 *
 * Options, as data- attributes on .sbox:
 *   data-issuer       https://login.swccg.com (no trailing slash)
 *   data-params-url   same-origin URL returning the OIDC fields as JSON (a fresh request each call).
 *                     Leave it out when the server already rendered them into the hidden inputs.
 *   data-error-param  query parameter carrying the failure reason back to this page (default sso_error)
 *   data-forgot-url   optional: where "Forgot password?" goes; otherwise its inline note opens
 * Events (bubbling, on .sbox): "sbox:error" {detail:{reason}}, e.g. fall back to an older form on
 * reset_required or unavailable; call event.preventDefault() to keep the box from showing the message.
 */
(function () {
  "use strict";

  var OIDC_FIELDS = ["client_id", "redirect_uri", "response_type", "scope", "state", "nonce",
    "code_challenge", "code_challenge_method"];

  var MESSAGES = {
    bad_credentials: "That name and password don't match. Check them and try again.",
    rate_limited: "Too many tries. Wait a few minutes, then try again.",
    unavailable: "Sign-in is having trouble right now. Try again in a minute.",
    blocked: "This account can't sign in right now.",
    reset_required: "Your password was reset. Choose a new password to continue.",
    passkey_failed: "That passkey didn't work here. Try again, or sign in with your name and password.",
    expired: "That took too long. Try again.",
    cancelled: "Sign-in was cancelled.",
    failed: "Sign-in didn't finish. Try again.",
    name_invalid: "Player names are 2 to 10 letters, digits, - or _, and can't start with a reserved word.",
    name_taken: "That name is taken. Pick another, or sign in if it's yours.",
    weak_password: "That password is too short or too common. Choose a longer one.",
    signup_limited: "Too many new accounts from here today. Try again tomorrow.",
    signup_closed: "New accounts are closed right now.",
    password_mismatch: "The two passwords don't match.",
    network: "Can't reach the sign-in service. Check your connection and try again.",
    no_passkey: "This browser can't use passkeys. Sign in with your name and password."
  };

  function $(root, sel) { return root.querySelector(sel); }
  function $all(root, sel) { return Array.prototype.slice.call(root.querySelectorAll(sel)); }

  function b64uToBuf(s) {
    s = String(s).replace(/-/g, "+").replace(/_/g, "/");
    while (s.length % 4) s += "=";
    var bin = atob(s), b = new Uint8Array(bin.length);
    for (var i = 0; i < bin.length; i++) b[i] = bin.charCodeAt(i);
    return b.buffer;
  }
  function bufToB64u(buf) {
    var b = new Uint8Array(buf), s = "";
    for (var i = 0; i < b.length; i++) s += String.fromCharCode(b[i]);
    return btoa(s).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
  }
  function credJSON(c) {
    var r = c.response, out = { id: c.id, rawId: bufToB64u(c.rawId), type: c.type, response: {},
      clientExtensionResults: c.getClientExtensionResults ? c.getClientExtensionResults() : {} };
    out.response.clientDataJSON = bufToB64u(r.clientDataJSON);
    out.response.authenticatorData = bufToB64u(r.authenticatorData);
    out.response.signature = bufToB64u(r.signature);
    if (r.userHandle) out.response.userHandle = bufToB64u(r.userHandle);
    if (c.authenticatorAttachment) out.authenticatorAttachment = c.authenticatorAttachment;
    return out;
  }
  function requestOptions(pk) {
    var o = {};
    for (var k in pk) if (Object.prototype.hasOwnProperty.call(pk, k)) o[k] = pk[k];
    o.challenge = b64uToBuf(pk.challenge);
    if (pk.allowCredentials) o.allowCredentials = pk.allowCredentials.map(function (c) {
      return { type: c.type, id: b64uToBuf(c.id), transports: c.transports };
    });
    return o;
  }
  function passkeysSupported() {
    return !!(window.PublicKeyCredential && navigator.credentials && navigator.credentials.get);
  }

  function SignInBox(root) {
    this.root = root;
    this.issuer = (root.getAttribute("data-issuer") || "https://login.swccg.com").replace(/\/+$/, "");
    this.paramsUrl = root.getAttribute("data-params-url") || "";
    this.errorParam = root.getAttribute("data-error-param") || "sso_error";
    this.forgotUrl = root.getAttribute("data-forgot-url") || "";
    this.form = $(root, "form.sbox-form");
    this.pkForm = $(root, "form.sbox-pk-form");
    this.err = $(root, ".sbox-error");
    this.name = $(root, "input[name=name]");
    this.password = $(root, "input[name=password]");
    this.confirmRow = $(root, ".sbox-confirm");
    this.confirm = $(root, "input[name=confirm]");
    this.mode = $(root, "input[name=mode]");
    this.submit = $(root, ".sbtn-primary");
    this.passkeyBtn = $(root, ".sbtn-passkey");
    this.conditional = null;
    this.busy = false;
    this.inlinePasskey = false; // set once providers.json says the passkey RP ID covers this site
    this.form.setAttribute("action", this.issuer + "/signin/password");
    if (this.pkForm) this.pkForm.setAttribute("action", this.issuer + "/signin/passkey");
    this.wire();
    this.restore();
    this.loadProviders();
  }

  SignInBox.prototype.wire = function () {
    var self = this;
    this.form.addEventListener("submit", function (e) { e.preventDefault(); self.signIn(); });
    $all(this.root, "[data-sbox-toggle=signup]").forEach(function (b) {
      b.addEventListener("click", function () { self.setMode(self.mode.value === "signup" ? "signin" : "signup"); });
    });
    var forgot = $(this.root, "[data-sbox-toggle=forgot]");
    if (forgot) forgot.addEventListener("click", function () {
      if (self.forgotUrl) { window.location.assign(self.forgotUrl); return; }
      var note = $(self.root, ".sbox-forgot");
      var open = note.hidden;
      note.hidden = !open;
      forgot.setAttribute("aria-expanded", open ? "true" : "false");
      if (open) note.focus();
    });
    var reveal = $(this.root, ".sbox-reveal");
    if (reveal) reveal.addEventListener("click", function () {
      var show = self.password.type === "password";
      self.password.type = show ? "text" : "password";
      if (self.confirm) self.confirm.type = self.password.type;
      reveal.textContent = show ? "Hide" : "Show";
      reveal.setAttribute("aria-pressed", show ? "true" : "false");
      reveal.setAttribute("aria-label", show ? "Hide password" : "Show password");
    });
    if (this.passkeyBtn) this.passkeyBtn.addEventListener("click", function () { self.passkey(false); });
    $all(this.root, ".sbtn[data-idp]").forEach(function (b) {
      b.addEventListener("click", function () { self.provider(b.getAttribute("data-idp")); });
    });
    // Passkey autofill (conditional UI): offered in the name field's suggestions.
    this.name.addEventListener("focus", function () { self.startConditional(); });
    // Leaving with Back after a redirect restores this page from cache: clear the spinner.
    window.addEventListener("pageshow", function (e) { if (e.persisted) self.setBusy(null, false); });
  };

  SignInBox.prototype.restore = function () {
    try {
      var saved = window.sessionStorage.getItem("sbox:name");
      if (saved && !this.name.value) this.name.value = saved;
    } catch (ignore) { /* storage off */ }
    var q = window.location.search, reason = null;
    if (q && window.URLSearchParams) {
      var p = new URLSearchParams(q);
      reason = p.get(this.errorParam);
      if (reason !== null) {
        p.delete(this.errorParam);
        var rest = p.toString();
        if (window.history && history.replaceState)
          history.replaceState(null, "", window.location.pathname + (rest ? "?" + rest : "") + window.location.hash);
      }
    }
    if (reason) {
      try { if (window.sessionStorage.getItem("sbox:mode") === "signup") this.setMode("signup", true); } catch (ignore) { }
      this.fail(reason);
    }
  };

  SignInBox.prototype.setMode = function (mode, quiet) {
    var signup = mode === "signup";
    this.mode.value = signup ? "signup" : "signin";
    this.confirmRow.hidden = !signup;
    this.confirm.required = signup;
    this.password.setAttribute("autocomplete", signup ? "new-password" : "current-password");
    this.name.setAttribute("autocomplete", signup ? "username" : "username webauthn");
    $(this.root, ".sbox-title").textContent = signup ? "Create your account" : "Sign in";
    this.submit.textContent = signup ? "Create account" : "Sign in";
    $all(this.root, ".sbox-name-hint").forEach(function (h) { h.hidden = !signup; });
    $all(this.root, "[data-sbox-show=signin]").forEach(function (el) { el.hidden = signup; });
    $all(this.root, "[data-sbox-show=signup]").forEach(function (el) { el.hidden = !signup; });
    if (!quiet) { this.clearError(); this.name.focus(); }
    try { window.sessionStorage.setItem("sbox:mode", this.mode.value); } catch (ignore) { }
  };

  SignInBox.prototype.loadProviders = function () {
    var self = this;
    if (this.passkeyBtn && !passkeysSupported()) this.disable(this.passkeyBtn, "not on this browser");
    if (!window.fetch) return;
    fetch(this.issuer + "/providers.json", { credentials: "omit", cache: "no-cache" })
      .then(function (r) { return r.ok ? r.json() : null; })
      .then(function (d) {
        if (!d) return;
        if (self.passkeyBtn && d.passkey === false) self.disable(self.passkeyBtn, "coming soon");
        var rp = String(d.passkey_rp_id || ""), host = window.location.hostname;
        self.inlinePasskey = !!rp && (host === rp || host.slice(-(rp.length + 1)) === "." + rp);
        (d.providers || []).forEach(function (p) {
          var b = $(self.root, '.sbtn[data-idp="' + String(p.id).replace(/[^a-z0-9_-]/gi, "") + '"]');
          if (b && p.enabled) self.enable(b);
        });
      })
      .catch(function () { /* providers stay "coming soon" */ });
  };

  SignInBox.prototype.enable = function (b) {
    b.disabled = false;
    b.removeAttribute("aria-disabled");
    var soon = $(b, ".sbox-soon");
    if (soon) soon.parentNode.removeChild(soon);
  };
  SignInBox.prototype.disable = function (b, why) {
    b.disabled = true;
    var soon = $(b, ".sbox-soon");
    if (!soon) { soon = document.createElement("span"); soon.className = "sbox-soon"; b.appendChild(soon); }
    soon.textContent = why;
  };

  /** A fresh OIDC request: fetched from this site's server, or the one it already rendered. */
  SignInBox.prototype.params = function () {
    var self = this;
    if (!this.paramsUrl) {
      var p = {};
      OIDC_FIELDS.forEach(function (f) { var i = $(self.form, 'input[name="' + f + '"]'); p[f] = i ? i.value : ""; });
      return Promise.resolve(p);
    }
    return fetch(this.paramsUrl, { credentials: "same-origin", cache: "no-store", headers: { Accept: "application/json" } })
      .then(function (r) {
        if (r.status === 429) throw { reason: "rate_limited" };
        if (!r.ok) throw { reason: "unavailable" };
        return r.json();
      }, function () { throw { reason: "network" }; })
      .then(function (p) {
        if (p.issuer && String(p.issuer).replace(/\/+$/, "") !== self.issuer) throw { reason: "unavailable" };
        return p;
      });
  };

  SignInBox.prototype.fill = function (form, p) {
    OIDC_FIELDS.forEach(function (f) {
      var i = $(form, 'input[name="' + f + '"]');
      if (!i) { i = document.createElement("input"); i.type = "hidden"; i.name = f; form.appendChild(i); }
      i.value = p[f] == null ? "" : String(p[f]);
    });
  };

  SignInBox.prototype.signIn = function () {
    if (this.busy) return;
    this.clearError();
    var name = this.name.value.replace(/^\s+|\s+$/g, "");
    this.name.value = name;
    if (!name) return this.fieldError(this.name, "Enter your player name.");
    if (!this.password.value) return this.fieldError(this.password, "Enter your password.");
    if (this.mode.value === "signup" && this.password.value !== this.confirm.value)
      return this.fieldError(this.confirm, MESSAGES.password_mismatch);
    this.abortConditional();
    var self = this;
    this.setBusy(this.submit, true);
    try { window.sessionStorage.setItem("sbox:name", name); } catch (ignore) { }
    this.params().then(function (p) {
      self.fill(self.form, p);
      HTMLFormElement.prototype.submit.call(self.form); // top-level POST; the page navigates away
    }).catch(function (e) { self.setBusy(self.submit, false); self.fail((e && e.reason) || "unavailable"); });
  };

  SignInBox.prototype.provider = function (idp, btn) {
    if (this.busy) return;
    this.clearError();
    this.abortConditional();
    var self = this;
    btn = btn || $(this.root, '.sbtn[data-idp="' + idp + '"]');
    this.setBusy(btn, true);
    this.params().then(function (p) {
      var q = [];
      OIDC_FIELDS.forEach(function (f) { if (p[f]) q.push(encodeURIComponent(f) + "=" + encodeURIComponent(p[f])); });
      q.push("idp=" + encodeURIComponent(idp));
      window.location.assign(self.issuer + "/authorize?" + q.join("&"));
    }).catch(function (e) { self.setBusy(btn, false); self.fail((e && e.reason) || "unavailable"); });
  };

  SignInBox.prototype.options = function () {
    return fetch(this.issuer + "/signin/passkey/options", { credentials: "omit", cache: "no-store" })
      .then(function (r) {
        return r.json().catch(function () { return {}; }).then(function (d) {
          if (!r.ok || !d.publicKey) throw { reason: d.error || "unavailable" };
          return d;
        });
      }, function () { throw { reason: "network" }; });
  };

  SignInBox.prototype.passkey = function (conditional) {
    var self = this;
    // A site outside the passkey RP ID (or before providers.json answered) uses login.swccg.com's
    // own passkey prompt instead: /authorize?...&idp=passkey.
    if (!this.inlinePasskey) { if (!conditional) this.provider("passkey", this.passkeyBtn); return Promise.resolve(); }
    if (!passkeysSupported()) { if (!conditional) this.fail("no_passkey"); return Promise.resolve(); }
    if (!conditional) {
      if (this.busy) return Promise.resolve();
      this.clearError();
      this.abortConditional();
      this.setBusy(this.passkeyBtn, true);
    }
    var ctl = window.AbortController ? new AbortController() : null;
    if (conditional) this.conditional = ctl;
    var flow;
    return this.options().then(function (d) {
      flow = d.flow;
      var req = { publicKey: requestOptions(d.publicKey) };
      if (conditional) req.mediation = "conditional";
      if (ctl) req.signal = ctl.signal;
      return navigator.credentials.get(req);
    }).then(function (cred) {
      if (!cred) throw { reason: "cancelled" };
      if (conditional) self.setBusy(self.submit, true);
      return self.params().then(function (p) {
        self.fill(self.pkForm, p);
        $(self.pkForm, "input[name=flow]").value = flow;
        $(self.pkForm, "input[name=credential]").value = JSON.stringify(credJSON(cred));
        HTMLFormElement.prototype.submit.call(self.pkForm);
      });
    }).catch(function (e) {
      if (conditional && self.conditional === ctl) self.conditional = null;
      self.setBusy(conditional ? self.submit : self.passkeyBtn, false);
      if (e && e.name === "AbortError" && conditional) return; // the button or the form took over
      if (conditional && !(e && e.reason)) return; // autofill just went unused
      if (e && (e.name === "NotAllowedError" || e.name === "AbortError")) return self.fail("cancelled");
      self.fail((e && e.reason) || "passkey_failed");
    });
  };

  SignInBox.prototype.startConditional = function () {
    if (this.conditional || this.busy || !this.inlinePasskey || this.mode.value === "signup" || !passkeysSupported()) return;
    if (!PublicKeyCredential.isConditionalMediationAvailable) return;
    var self = this;
    this.conditional = true; // placeholder until the AbortController exists
    PublicKeyCredential.isConditionalMediationAvailable().then(function (ok) {
      if (self.conditional !== true) return; // aborted meanwhile
      if (!ok) { self.conditional = null; return; }
      self.passkey(true);
    }, function () { self.conditional = null; });
  };
  SignInBox.prototype.abortConditional = function () {
    var c = this.conditional;
    this.conditional = null;
    if (c && c.abort) c.abort();
  };

  SignInBox.prototype.setBusy = function (btn, on) {
    this.busy = !!on;
    $all(this.root, ".sbtn").forEach(function (b) { b.removeAttribute("aria-busy"); });
    if (btn && on) btn.setAttribute("aria-busy", "true");
  };

  SignInBox.prototype.clearError = function () {
    this.err.hidden = true;
    this.err.textContent = "";
    $all(this.root, "[aria-invalid]").forEach(function (i) { i.removeAttribute("aria-invalid"); i.removeAttribute("aria-describedby"); });
  };
  SignInBox.prototype.fieldError = function (input, msg) {
    this.show(msg);
    input.setAttribute("aria-invalid", "true");
    input.setAttribute("aria-describedby", this.err.id);
    input.focus();
  };
  SignInBox.prototype.show = function (msg) {
    this.err.textContent = msg;
    this.err.hidden = false;
  };
  SignInBox.prototype.fail = function (reason) {
    reason = MESSAGES.hasOwnProperty(reason) ? reason : "failed";
    var ev;
    try { ev = new CustomEvent("sbox:error", { bubbles: true, cancelable: true, detail: { reason: reason } }); }
    catch (x) { ev = document.createEvent("CustomEvent"); ev.initCustomEvent("sbox:error", true, true, { reason: reason }); }
    if (!this.root.dispatchEvent(ev)) return;
    this.show(MESSAGES[reason]);
    if (reason === "bad_credentials") {
      this.password.value = "";
      this.password.setAttribute("aria-invalid", "true");
      this.password.setAttribute("aria-describedby", this.err.id);
      (this.name.value ? this.password : this.name).focus();
    } else if (/^(name_invalid|name_taken)$/.test(reason)) {
      this.setMode("signup", true);
      this.name.setAttribute("aria-invalid", "true");
      this.name.focus();
    } else if (/^(weak_password|password_mismatch)$/.test(reason)) {
      this.setMode("signup", true);
      this.password.setAttribute("aria-invalid", "true");
      this.password.focus();
    } else {
      this.err.focus();
    }
  };

  function boot() {
    $all(document, ".sbox").forEach(function (el) {
      if (!el.sbox && !el.hasAttribute("data-sbox-manual")) el.sbox = new SignInBox(el);
    });
  }
  window.SwccgSignInBox = SignInBox;
  if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", boot);
  else boot();
})();
