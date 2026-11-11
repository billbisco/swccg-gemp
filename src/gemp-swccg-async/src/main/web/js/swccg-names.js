/* Player names in the browser (#249). No framework, ES5 syntax.
 *
 * The site's name rule lives on the server (swccg-login internal/names, and Play's PlayerNames): a name holds
 * letters and digits from any writing system in the Basic Multilingual Plane (combining marks after them), plus
 * spaces, hyphens and underscores. Nothing else, and nothing above U+FFFF (no emoji). The server strips or refuses
 * everything else and stays the final word; this file only spares the person a surprise:
 *
 *   - Name boxes (an <input data-name-input>, or SwccgNames.attach(input)) drop characters a name can never hold
 *     as they are typed, pasted or dropped, and say which ones went and why. Allowed characters are never changed.
 *     Spacing (a space or _ at either end, two in a row) is not touched while typing; it gets a hint on blur and
 *     the server tidies it.
 *   - SwccgNames.display(name) is the same filter for showing a name that is already stored (old rows, imports):
 *     it removes, it never trims, collapses or cuts. Escape the result as usual (text(), textContent).
 *
 * Browsers without Unicode property escapes in RegExp skip the filters (the server still checks).
 */
(function (root) {
  "use strict";

  var RE = null;
  try {
    RE = {
      // same order as the server: separators, invisible characters, letters and digits, combining marks
      dash: new RegExp("^\\p{Pd}$", "u"),
      space: new RegExp("^\\p{White_Space}$", "u"),
      invisible: new RegExp("^[\\p{Cf}\\p{Default_Ignorable_Code_Point}]$", "u"),
      alnum: new RegExp("^[\\p{L}\\p{Nd}]$", "u"),
      mark: new RegExp("^[\\p{Mn}\\p{Mc}]$", "u")
    };
  } catch (e) {
    RE = null;
  }

  var WHY = "Names use only letters, digits, spaces, - and _ (no emoji or symbols).";
  var TIDY = "A space or _ at the start or end, or two in a row, is tidied up when you save.";

  // Splits s into code points (ES5: no for..of). A lone surrogate is its own item.
  function codePoints(s) {
    var out = [];
    for (var i = 0; i < s.length; i++) {
      var c = s.charCodeAt(i);
      if (c >= 0xD800 && c <= 0xDBFF && i + 1 < s.length) {
        var d = s.charCodeAt(i + 1);
        if (d >= 0xDC00 && d <= 0xDFFF) {
          out.push(s.charAt(i) + s.charAt(i + 1));
          i++;
          continue;
        }
      }
      out.push(s.charAt(i));
    }
    return out;
  }

  /** {value, removed}: s without the characters a name can never hold; removed lists them in order. */
  function filter(s, forDisplay) {
    s = s == null ? "" : String(s);
    if (!RE)
      return {value: s, removed: []};
    var cps = codePoints(s), out = "", removed = [], base = false;
    for (var i = 0; i < cps.length; i++) {
      var ch = cps[i], c = ch.charCodeAt(0);
      if (ch.length > 1 || (c >= 0xD800 && c <= 0xDFFF)) {
        removed.push(ch);          // above U+FFFF (4 bytes in UTF-8), or a broken surrogate
        base = false;
      } else if (ch === "_" || ch === "-" || ch === " ") {
        out += ch;
        base = false;
      } else if (RE.dash.test(ch) || RE.space.test(ch)) {
        // the server stores these as "-" and " "; a name box keeps them as typed, a shown name as stored
        out += forDisplay ? (RE.dash.test(ch) ? "-" : " ") : ch;
        base = false;
      } else if (RE.invisible.test(ch)) {
        removed.push(ch);
      } else if (RE.alnum.test(ch)) {
        out += ch;
        base = true;
      } else if (RE.mark.test(ch) && base) {
        out += ch;
      } else {
        removed.push(ch);
      }
    }
    return {value: out, removed: removed};
  }

  /** A stored name, ready to show: characters a name can never hold are removed (see the top of this file). */
  function display(name, placeholder) {
    var s = name == null ? "" : String(name), prefix = "";
    if (s.charAt(0) === "~") {   // Play's bots (~OzzelBot): the mark is part of the id and shows as it is
      prefix = "~";
      s = s.substring(1);
    }
    var v = filter(s, true).value;
    if (RE && !/[^\s_\-]/.test(v))
      return placeholder != null ? placeholder : (prefix + v);
    return prefix + v;
  }

  function label(ch) {
    if (RE && (RE.invisible.test(ch) || RE.space.test(ch)) || /^[\u0000-\u001f\u007f-\u009f]$/.test(ch) || ch.length === 1 && ch.charCodeAt(0) >= 0xD800 && ch.charCodeAt(0) <= 0xDFFF) {
      var hex = ch.charCodeAt(0).toString(16).toUpperCase();
      while (hex.length < 4) hex = "0" + hex;
      return "U+" + hex;
    }
    return ch;
  }

  function removedText(removed) {
    var seen = {}, shown = [];
    for (var i = 0; i < removed.length && shown.length < 8; i++) {
      var l = label(removed[i]);
      if (!seen[l]) {
        seen[l] = true;
        shown.push(l);
      }
    }
    return "Removed " + shown.join(" ") + (removed.length > shown.length && shown.length === 8 ? " \u2026" : "") + ". " + WHY;
  }

  function needsTidy(v) {
    return /^[ _]|[ _]$|[ _]{2}/.test(v);
  }

  function maxLen(input) {
    var m = parseInt(input.getAttribute("maxlength"), 10);
    return isNaN(m) || m <= 0 ? 0 : m;
  }

  /**
   * Filters a name box as it is typed, pasted or dropped. opts.note: element for the inline note (made after the
   * box when missing). opts.active(): false to leave the box alone for now (a box that also signs in).
   */
  function attach(input, opts) {
    if (!input || input.getAttribute("data-name-attached") === "1")
      return null;
    input.setAttribute("data-name-attached", "1");
    opts = opts || {};
    var note = opts.note;
    if (!note && input.getAttribute("data-name-note"))
      note = document.getElementById(input.getAttribute("data-name-note"));
    if (!note) {
      note = document.createElement("p");
      note.className = "name-note";
      input.parentNode.insertBefore(note, input.nextSibling);
    }
    note.setAttribute("aria-live", "polite");
    if (!note.textContent) note.hidden = true;
    var active = typeof opts.active === "function" ? opts.active : function () { return true; };
    var removedMsg = "", composing = false;

    function show(tidyMsg) {
      var t = removedMsg + (removedMsg && tidyMsg ? " " : "") + (tidyMsg || "");
      note.textContent = t;
      note.hidden = t === "";
    }

    function clean() {
      if (!RE || composing || !active())
        return;
      var v = input.value, r = filter(v, false);
      if (v === "" && removedMsg) {   // an emptied box starts over
        removedMsg = "";
        show("");
      }
      var m = maxLen(input), out = r.value;
      if (m && out.length > m)
        out = out.substring(0, m);
      if (out === v)
        return;
      var caret = null;
      try { caret = input.selectionStart; } catch (e) { caret = null; }
      var before = caret == null ? out.length : filter(v.substring(0, caret), false).value.length;
      input.value = out;
      try { input.setSelectionRange(Math.min(before, out.length), Math.min(before, out.length)); } catch (e2) { /* not focusable */ }
      removedMsg = removedText(r.removed.length ? r.removed : []);
      if (!r.removed.length) removedMsg = "";
      show("");
    }

    input.addEventListener("compositionstart", function () { composing = true; });
    input.addEventListener("compositionend", function () { composing = false; clean(); });
    input.addEventListener("input", function (e) {
      if (e && e.isComposing) return;
      clean();
    });
    // A paste is filtered before it goes in, so maxlength counts only what is kept (an emoji takes two places).
    input.addEventListener("paste", function (e) {
      if (!RE || !active() || !e.clipboardData || typeof input.setRangeText !== "function")
        return;
      var text = e.clipboardData.getData("text/plain");
      if (text == null)
        return;
      text = text.replace(/[\r\n]+/g, " ");
      var r = filter(text, false);
      if (!r.removed.length)
        return;
      e.preventDefault();
      var start = input.selectionStart, end = input.selectionEnd, m = maxLen(input), ins = r.value;
      if (m) {
        var room = m - (input.value.length - (end - start));
        if (room < 0) room = 0;
        ins = ins.substring(0, room);
      }
      input.setRangeText(ins, start, end, "end");
      removedMsg = removedText(r.removed);
      show("");
      var ev;
      try { ev = new Event("input", {bubbles: true}); } catch (e3) { ev = document.createEvent("Event"); ev.initEvent("input", true, false); }
      input.dispatchEvent(ev);
    });
    input.addEventListener("blur", function () {
      if (!active()) { removedMsg = ""; show(""); return; }
      show(needsTidy(input.value) ? TIDY : "");
    });
    input.addEventListener("focus", function () {
      if (!removedMsg) show("");
    });
    return {clean: clean, reset: function () { removedMsg = ""; show(""); }};
  }

  function attachAll(scope) {
    var list = (scope || document).querySelectorAll("input[data-name-input]");
    for (var i = 0; i < list.length; i++)
      attach(list[i]);
  }

  var api = {filter: function (s) { return filter(s, false); }, display: display, attach: attach, attachAll: attachAll,
    supported: RE !== null, WHY: WHY, TIDY: TIDY};
  if (typeof module === "object" && module.exports)
    module.exports = api;
  else
    root.SwccgNames = api;

  if (typeof document !== "undefined" && document.addEventListener) {
    if (document.readyState === "loading")
      document.addEventListener("DOMContentLoaded", function () { attachAll(); });
    else
      attachAll();
  }
})(this);
