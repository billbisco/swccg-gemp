const assert = require('assert');
const fs = require('fs');
const vm = require('vm');
const source = fs.readFileSync(process.argv[2], 'utf8');
let fields = {};
let formatOptions;
function wrap(nodes) {
    const result = {0: nodes[0],
        prop(key, value) { if (arguments.length === 1) return nodes[0]?.[key] ?? ''; nodes.forEach(n => n[key] = value); return this; },
        val(value) { if (value === undefined) return nodes[0]?.value; nodes.forEach(n => n.value = value); return this; },
        change(fn) { nodes.forEach(n => n.change = fn); return this; },
        click(fn) { nodes.forEach(n => n.click = fn); return this; },
        trigger(event) { nodes.forEach(n => n[event]?.()); return this; }
    };
    for (const key of ['append', 'button', 'slider', 'css']) result[key] = () => result;
    return result;
}
function $(selector) {
    if (selector.startsWith('<')) {
        const id = selector.match(/id=['"]([^'"]+)/)?.[1];
        const options = [...selector.matchAll(/<option value='([^']*)'([^>]*)>([^<]*)/g)];
        const selected = options.find(o => o[2].includes('selected')) || options[0];
        const n = {outerHTML: selector, value: selected?.[1] ?? ''};
        if (id) fields[id] = n;
        if (id === 'formatSelect') formatOptions = options.map(o => [o[1],o[3]]);
        return wrap([n]);
    }
    return wrap(selector.split(',').map(s => fields[s.trim().split(' ')[0].slice(1)]).filter(Boolean));
}
const context = {Class: {extend: value => value}, $};
vm.runInNewContext(source, context);
const requests = [];
const filter = Object.assign({}, context.CardFilter);
filter.init($('<div/>'), $('<div/>'), value => requests.push(value), () => {}, () => {}, () => {});
assert(formatOptions.some(([value,label]) => value === 'anything_goes' && label === 'Anything Goes'));
assert.strictEqual(fields.formatSelect.value, 'anything_goes');
assert(requests[0].split(' ').includes('format:anything_goes'), requests[0]);
fields.nameInput.value = 'Superficial Damage';
fields.setSelect.value = '601';
for (const format of ['all', 'open', 'legacy', 'anything_goes']) {
    fields.formatSelect.value = format;
    fields.formatSelect.change();
    assert(requests.at(-1).includes('format:' + format), requests.at(-1));
    assert(requests.at(-1).includes('set:601'));
    assert(requests.at(-1).includes('name:Superficial name:Damage'));
}
fields.formatSelect.value = 'legacy';
fields.resetAllFiltersButton.click();
assert.strictEqual(fields.formatSelect.value, 'anything_goes');
assert(requests.at(-1).includes('format:anything_goes'));
assert(!requests.at(-1).includes('set:601'));
assert(!requests.at(-1).includes('name:Superficial'));
console.log('deckbuilder format default and explicit selection requests passed');

