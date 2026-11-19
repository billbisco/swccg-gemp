// Exercise the real form methods: collapsed presentation must never change the rules sent to the server.
var assert = require("assert"), fs = require("fs"), vm = require("vm");
var $ = {trim: function (v) { return String(v).trim(); }, each: function (o, fn) {
    Object.keys(o).forEach(function (k) { fn(k, o[k]); });
}};
var context = {Class: {extend: function (methods) {
    function Form() {} Form.prototype = methods; return Form;
}}, $: $};
vm.createContext(context);
vm.runInContext(fs.readFileSync(process.argv[2], "utf8"), context);
var form = new context.GameOptionsForm(), changes = [], notices = 0;
var defaults = {deckSize: "60", sameSide: "false", operativeRule: "true", timer: "default"};
form.formatOption = {attr: function (key) {
    return defaults[key.replace("data-opt-", "").replace("-default", "")];
}};
form.container = {trigger: function () { notices++; }};
form.onChange = function (id) { changes.push(id); };
form.syncPreset = function () {};
form.setSaveNote = function (s) { form.note = s; };
form.storeSaved = function () { throw new Error("Per-table reset must not write saved preferences"); };
function input(value, disabled, min, max) {
    return {val: function (v) { if (arguments.length) { value = v; return this; } return value; },
        prop: function () { return disabled; }, removeAttr: function () { return this; },
        attr: function (name) { return name === "min" ? min : max; }};
}
function row(id, type, value, disabled, min, max) {
    return {row: {is: function () { throw new Error("Availability must not depend on collapsed visibility"); }},
        input: input(value, disabled, min, max), option: {type: type, label: id}};
}
form.rows = {
    deckSize: row("deckSize", "INTEGER", "100", false, 40, 100),
    sameSide: row("sameSide", "BOOLEAN", "true", false),
    operativeRule: row("operativeRule", "BOOLEAN", "false", false),
    timer: row("timer", "CHOICE", "blitz", false),
    unavailable: row("unavailable", "INTEGER", "999999", true, 0, 1)
};
assert.strictEqual(form.effective("operativeRule"), "false");
assert.strictEqual(form.values().operativeRule, "false");
assert.strictEqual(form.deckListCode("open"), "open@100");
assert.strictEqual(form.value("unavailable"), "");
assert.strictEqual(form.setValue("unavailable", "0"), false);
assert.strictEqual(form.validate(), null);
form.rows.deckSize.input.val("101");
assert.match(form.validate(), /40 to 100/);
assert.strictEqual(form.invalidInput, form.rows.deckSize.input);
form.rows.deckSize.input.val("60.5");
assert.match(form.validate(), /whole number/);
form.useFormatDefaults();
assert.strictEqual(JSON.stringify(form.values()), "{}");
assert.strictEqual(form.effective("sameSide"), "false");
assert.strictEqual(form.effective("operativeRule"), "true");
assert.strictEqual(form.rows.timer.input.val(), "default", "remembered timer reset in draft");
assert.strictEqual(form.validate(), null);
assert.strictEqual(form.invalidInput, null);
assert.deepStrictEqual(changes, ["deckSize", "sameSide"]);
assert.match(form.note, /saved settings are kept/);
form.setValues({operativeRule: "false", unavailable: "0", unknown: "true"});
assert.strictEqual(form.effective("operativeRule"), "false");
assert.strictEqual(form.rows.unavailable.input.val(), "999999");
assert.strictEqual(form.setValue("deckSize", "100"), true);
assert.strictEqual(form.deckListCode("open"), "open@100");
assert.ok(notices >= 3, "programmatic changes update collapsed summaries");
console.log("game options disclosure tests passed");
