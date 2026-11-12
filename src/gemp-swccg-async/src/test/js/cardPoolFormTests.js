// Run by CardPoolFormWebClientTests (node). Exits non-zero on the first failure.
// argv: cardPoolForm.js, hallUi.js, communication.js, hall.html
var assert = require("assert");
var fs = require("fs");
var P = require(process.argv[2]);

//test1: set lists: blank is the format's own sets; numbers are sorted without repeats; anything else is refused
assert.strictEqual(P.parseSets(""), null);
assert.strictEqual(P.parseSets("   "), null);
assert.deepStrictEqual(P.parseSets("7, 1 2;1"), [1, 2, 7]);
assert.throws(function () { P.parseSets("1, Hoth"); }, /not a set number/);
assert.throws(function () { P.parseSets("0"); }, /not a set number/);

//test2: the JSON the server reads; no customization is "" (the named format as it is)
assert.strictEqual(P.poolJson({enabled: false, setsText: "1", allow: ["3_1"], ban: []}), "");
assert.strictEqual(P.poolJson({enabled: true, setsText: "", allow: [], ban: []}), "");
assert.strictEqual(P.poolJson({enabled: true, setsText: "2,1", allow: ["3_10", "3_10*"], ban: ["1_5"]}),
    '{"sets":[1,2],"allow":["3_10"],"ban":["1_5"]}');
assert.strictEqual(P.poolJson({enabled: true, setsText: "", allow: [], ban: ["1_5^"]}), '{"allow":[],"ban":["1_5"]}');

//test3: an id both allowed and banned (a foil counts as the same card) is caught before submit
assert.deepStrictEqual(P.conflicts(["1_80", "2_3"], ["1_80*"]), ["1_80"]);
assert.deepStrictEqual(P.conflicts(["1_80"], ["1_81"]), []);

//test4: search results show side, set and id only when titles collide
var labels = P.labelCards([
    {id: "1_121", title: "Alderaan", side: "Light", set: "1"},
    {id: "1_281", title: "Alderaan", side: "Dark", set: "1"},
    {id: "1_80", title: "Han Solo", side: "Light", set: "1"}]);
assert.deepStrictEqual(labels, ["Alderaan (Light, set 1, 1_121)", "Alderaan (Dark, set 1, 1_281)", "Han Solo"]);

//test5: server answers: cards, rules, and a refused pool
function node(tag, attrs) {
    return {tagName: tag, getAttribute: function (n) { return attrs.hasOwnProperty(n) ? attrs[n] : null; }};
}
function doc(tag, attrs, childTag, children) {
    var root = node(tag, attrs);
    root.getElementsByTagName = function (t) { return t === childTag ? children : []; };
    return {documentElement: root};
}
assert.deepStrictEqual(P.cardsFromXml(doc("cards", {}, "card", [node("card", {id: "1_121", title: "Alderaan", side: "Light", set: "1", label: "L"})])),
    [{id: "1_121", title: "Alderaan", side: "Light", set: "1", label: "L"}]);
assert.deepStrictEqual(P.rulesFromXml(doc("rules", {}, "rule", [node("rule", {text: "Sets: 1, 2 (instead of Open's sets)"}), node("rule", {text: "Banned: X"})])),
    ["Sets: 1, 2 (instead of Open's sets)", "Banned: X"]);
assert.deepStrictEqual(P.rulesFromXml(doc("error", {message: "Custom card pool: Card 1_80 is both allowed and banned; choose one."}, "rule", [])),
    {error: "Custom card pool: Card 1_80 is both allowed and banned; choose one."});

//test6: hall rows offer the rules only for events with a custom pool (the server's chip)
assert.strictEqual(P.hasPool("Deck: 45 cards · Custom card pool"), true);
assert.strictEqual(P.hasPool("Deck: 45 cards"), false);
assert.strictEqual(P.hasPool(null), false);

//test7: the hall wires it in: the form on Create Tournament, the pool sent on create, rules in join and on rows
var hall = fs.readFileSync(process.argv[3], "utf8");
assert.ok(hall.indexOf("new CardPoolForm.Form(this.comm, tFields") >= 0, "Create Tournament has the pool form");
assert.ok(hall.indexOf("params.cardPool = this.tournamentCardPoolForm.json()") >= 0, "the pool is sent on create");
assert.ok(hall.indexOf("this.tournamentCardPoolForm.show(!limited)") >= 0, "the pool form is Constructed only");
assert.ok(hall.indexOf("CardPoolForm.showRules(this.comm, pending.id, rulesDiv)") >= 0, "join shows the rules first");
assert.ok((hall.match(/appendCardPoolRulesLink\(row\.children\(\)\.last\(\)/g) || []).length === 2, "queue and tournament rows link the rules");
var comm = fs.readFileSync(process.argv[4], "utf8");
["/tournament/cardSearch", "/tournament/poolPreview", "/tournament/rules/", "cardPool: params.cardPool"].forEach(function (s) {
    assert.ok(comm.indexOf(s) >= 0, "communication.js: " + s);
});
var html = fs.readFileSync(process.argv[5], "utf8");
assert.ok(html.indexOf("js/gemp-016/cardPoolForm.js") >= 0 && html.indexOf("cardPoolForm.js") < html.indexOf("js/gemp-016/hallUi.js"),
    "hall.html loads cardPoolForm.js before hallUi.js");

console.log("card pool form tests passed");
