// Run by PlayerMenuWebClientTests (node). Exits non-zero on the first failure.
// argv: playerMenu.js, hallUi.js, muted-user.functions.js
var assert = require("assert");
var fs = require("fs");
var M = require(process.argv[2]);

//test1: kindOf: bots, guests (a guest id or a Play now seat, both spellings), players
assert.strictEqual(M.kindOf("~OzzelBot"), "bot");
assert.strictEqual(M.kindOf("guest-AbCdEf123456"), "guest");
assert.strictEqual(M.kindOf("Light Side"), "guest");
assert.strictEqual(M.kindOf("Dark_Side"), "guest");
assert.strictEqual(M.kindOf("Light Side 2"), "player");
assert.strictEqual(M.kindOf("Luke Sky"), "player");

//test6: Invite to table is offered only to a real player, and only when the viewer is also a real player
var player = {name: "Luke Sky", kind: "player", playerId: "3812"};
assert.strictEqual(M.canInviteToTable(player, "Leia Organa"), true);
assert.strictEqual(M.canInviteToTable(player, "luke sky"), false);           // not yourself
assert.strictEqual(M.canInviteToTable(player, "guest-AbCdEf123456"), false); // guest host
assert.strictEqual(M.canInviteToTable(player, "~OzzelBot"), false);
assert.strictEqual(M.canInviteToTable(player, ""), false);
assert.strictEqual(M.canInviteToTable({name: "guest-AbCdEf123456", kind: "guest"}, "Leia Organa"), false);
assert.strictEqual(M.canInviteToTable({name: "~OzzelBot", kind: "bot"}, "Leia Organa"), false);
assert.strictEqual(M.canInviteToTable({name: "Light Side", kind: "guest"}, "Leia Organa"), false);

//test2: infoFromUser reads ChatRequestHandler's <user name kind playerId>; old servers send only the text
function user(text, attrs) {
    return {childNodes: [{nodeValue: text}], getAttribute: function (n) { return attrs.hasOwnProperty(n) ? attrs[n] : null; }};
}
assert.deepStrictEqual(M.infoFromUser(user("* Bill", {name: "Bill", kind: "player", playerId: "2"})), {name: "Bill", kind: "player", playerId: "2"});
assert.deepStrictEqual(M.infoFromUser(user("Light Side ", {name: "Light Side", kind: "guest"})), {name: "Light Side", kind: "guest", playerId: null});
assert.deepStrictEqual(M.infoFromUser(user("+&beta; Rey ", {})), {name: "Rey", kind: "player", playerId: null});
assert.deepStrictEqual(M.infoFromUser(user("~YodaBot", {})), {name: "~YodaBot", kind: "bot", playerId: null});
assert.strictEqual(M.infoFromUser(user("x", {name: "x", playerId: "12<b>"})).playerId, null);   // only digits

//test3: copyText: the Clipboard API on HTTPS; a textarea and execCommand otherwise or when it is refused
var copied = [], execResult = true, textareas = 0;
global.window = {isSecureContext: true};
global.document = {
    body: {appendChild: function () { textareas++; }, removeChild: function () {}},
    createElement: function () { return {style: {}, setAttribute: function () {}, select: function () {}, setSelectionRange: function () {}}; },
    execCommand: function (c) { assert.strictEqual(c, "copy"); return execResult; }
};
function setClipboard(cb) { Object.defineProperty(globalThis, "navigator", {value: cb ? {clipboard: cb} : {}, configurable: true, writable: true}); }
function copy(text) { return new Promise(function (resolve) { M.copyText(text, resolve); }); }
var steps = Promise.resolve().then(function () {
    setClipboard({writeText: function (t) { copied.push(t); return Promise.resolve(); }});
    return copy("3812");
}).then(function (ok) {
    assert.strictEqual(ok, true, "https copy");
    assert.deepStrictEqual(copied, ["3812"]);
    assert.strictEqual(textareas, 0);
    setClipboard({writeText: function () { return Promise.reject(new Error("denied")); }});
    return copy("3812");
}).then(function (ok) {
    assert.strictEqual(ok, true, "refused -> fallback");
    assert.strictEqual(textareas, 1);
    window.isSecureContext = false;
    setClipboard({writeText: function () { throw new Error("must not be used over http"); }});
    return copy("3812");
}).then(function (ok) {
    assert.strictEqual(ok, true, "http -> fallback");
    assert.strictEqual(textareas, 2);
    execResult = false;
    return copy("3812");
}).then(function (ok) {
    assert.strictEqual(ok, false, "fallback fails");
});

//test4: hall tables: the name in "Name (Side: deck)"; a hidden seat "(Side)" has none
var hall = fs.readFileSync(process.argv[3], "utf8");
var src = /function tablePlayerName\(entry\) \{[\s\S]*?\n\}/.exec(hall)[0];
var tablePlayerName = new Function(src + "; return tablePlayerName;")();
assert.strictEqual(tablePlayerName("Luke Sky (Light: Hidden Base)"), "Luke Sky");
assert.strictEqual(tablePlayerName("~OzzelBot (Dark)"), "~OzzelBot");
assert.strictEqual(tablePlayerName("(Light)"), "");
assert.strictEqual(tablePlayerName("Rey"), "Rey");

//test5: hall mute reads names with spaces from the chat line (chat.js writes "<b>" + escaped name + ":</b>")
var muted = fs.readFileSync(process.argv[4], "utf8");
var nameFrom = new Function("document", muted + "; return getUserNameFromMessage;")({
    createElement: function () { var d = {}; Object.defineProperty(d, "innerHTML", {set: function (v) { d.textContent = v.replace(/&amp;/g, "&"); }}); return d; }
});
assert.strictEqual(nameFrom("<b>Luke Sky:</b> hi"), "Luke Sky");
assert.strictEqual(nameFrom("<div class='timestamp'>[Nov 11 20:00:00]</div> <b>Rey:</b> hi"), "Rey");
assert.strictEqual(nameFrom("no name"), "");

steps.then(function () { console.log("OK"); }, function (e) { console.error(e); process.exit(1); });
