// Run by PlayerNamesWebClientTests (node). Exits non-zero on the first failure.
// argv: swccg-names.js, then a file of "codepoint<TAB>Java forDisplay of a<cp>b" lines to mirror.
var assert = require("assert");
var fs = require("fs");
var N = require(process.argv[2]);
assert.ok(N.supported, "Unicode property escapes");

//test1: the name boxes' filter drops what a name can never hold and keeps everything else as typed.
assert.deepStrictEqual(N.filter("Han\uD83D\uDE00Solo"), {value: "HanSolo", removed: ["\uD83D\uDE00"]});
assert.strictEqual(N.filter("Zo\u00eb \u00c5ngstr\u00f6m").value, "Zo\u00eb \u00c5ngstr\u00f6m");
assert.strictEqual(N.filter("T\uD835\uDC00x").value, "Tx");                 // a 4-byte letter
assert.strictEqual(N.filter("Le<i>a\"'&#;:/@[]{}|").value, "Leia");
assert.strictEqual(N.filter("Rey\u200B\u202E\uFE0F").value, "Rey");
assert.strictEqual(N.filter("  Luke__Sky_ ").value, "  Luke__Sky_ ");       // spacing is the server's job
assert.strictEqual(N.filter("Obi\u2013Wan").value, "Obi\u2013Wan");         // kept as typed; the server stores "-"
assert.strictEqual(N.filter("\u0308x").value, "x");                         // a mark needs a letter before it

//test2: display() is the same filter for stored names; bots keep "~"; nothing left shows the placeholder.
assert.strictEqual(N.display("~OzzelBot"), "~OzzelBot");
assert.strictEqual(N.display("Obi\u2013Wan\u00A0K"), "Obi-Wan K");
assert.strictEqual(N.display("\uD83D\uDE00", "(unnamed player)"), "(unnamed player)");
assert.strictEqual(N.display("Luke Sky"), "Luke Sky");

//test3: display() mirrors PlayerNames.forDisplay for every assigned BMP character.
var unassigned = new RegExp("^\\p{Cn}$", "u");
var lines = fs.readFileSync(process.argv[3], "utf8").split("\n"), checked = 0;
for (var i = 0; i < lines.length; i++) {
    if (!lines[i]) continue;
    var tab = lines[i].indexOf("\t"), cp = parseInt(lines[i].substring(0, tab), 10), want = lines[i].substring(tab + 1);
    var ch = String.fromCharCode(cp);
    if (unassigned.test(ch)) continue;   // this node's Unicode is older than the JDK's for this one
    assert.strictEqual(N.display("a" + ch + "b"), want, "U+" + cp.toString(16));
    checked++;
}
assert.ok(checked > 50000, "checked " + checked);
console.log("OK " + checked);
