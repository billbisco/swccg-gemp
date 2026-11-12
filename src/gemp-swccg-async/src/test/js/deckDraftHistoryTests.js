// Run by DeckFixOnJoinWebClientTests (node). Exits non-zero on the first failure.
var assert = require("assert");
var H = require(process.argv[2]);

function c(id) { return {blueprintId: id, testingText: null, backSideTestingText: null, horizontal: "false"}; }
function s(deck, outside) { return {deck: deck.map(c), outside: outside.map(c)}; }

var start = s(["1_1", "1_2"], ["200_16"]);
var h = new H(start);

//test1: a fresh draft is clean and has nothing to undo or redo
assert.strictEqual(h.isDirty(), false);
assert.strictEqual(h.canUndo(), false);
assert.strictEqual(h.canRedo(), false);
assert.strictEqual(H.contentsOf(start), "1_1,1_2|200_16");

//test2: add, remove and move are each one step; an unchanged state is not a step
var added = s(["1_1", "1_2", "1_3"], ["200_16"]);
var removed = s(["1_2", "1_3"], ["200_16"]);
var moved = s(["1_2"], ["200_16", "1_3"]);
assert.ok(h.record(added));
assert.ok(!h.record(s(["1_1", "1_2", "1_3"], ["200_16"])));
assert.ok(h.record(removed));
assert.ok(h.record(moved));
assert.ok(h.isDirty());

//test3: undo each edit in turn, back to the loaded deck
assert.strictEqual(H.contentsOf(h.undo()), H.contentsOf(removed));
assert.strictEqual(H.contentsOf(h.undo()), H.contentsOf(added));
assert.strictEqual(H.contentsOf(h.undo()), H.contentsOf(start));
assert.strictEqual(h.undo(), null);
assert.strictEqual(h.isDirty(), false);

//test4: redo each edit in turn
assert.strictEqual(H.contentsOf(h.redo()), H.contentsOf(added));
assert.strictEqual(H.contentsOf(h.redo()), H.contentsOf(removed));
assert.strictEqual(H.contentsOf(h.redo()), H.contentsOf(moved));
assert.strictEqual(h.redo(), null);

//test5: a new edit after undo drops the redo steps
h.undo();
var grouped = s(["1_2", "1_3"], ["200_16", "200_17", "200_18"]);
assert.ok(h.record(grouped));
assert.strictEqual(h.canRedo(), false);
assert.strictEqual(H.contentsOf(h.undo()), H.contentsOf(removed));

//test6: saving makes the current draft the clean point; undo past it is dirty again
h.redo();
h.markSaved();
assert.strictEqual(h.isDirty(), false);
h.undo();
assert.strictEqual(h.isDirty(), true);

//test7: reset after loading another revision
h.reset(start);
assert.strictEqual(h.canUndo(), false);
assert.strictEqual(h.isDirty(), false);

//test8: deck-specific join failures are told apart from table problems
assert.ok(H.isDeckJoinFailure("Your selected deck is not valid for this format: Deck must contain 60 cards"));
assert.ok(!H.isDeckJoinFailure("Table is already taken or was removed"));
assert.ok(!H.isDeckJoinFailure(null));

//test9: copy names follow the Deckbuilder rule and must differ from the original
assert.ok(H.deckNameProblem("ab", "Hunt Down") != null);
assert.ok(H.deckNameProblem("Hunt Down", "Hunt Down") != null);
assert.strictEqual(H.deckNameProblem("Hunt Down 40", "Hunt Down"), null);

console.log("deckDraftHistory ok");
