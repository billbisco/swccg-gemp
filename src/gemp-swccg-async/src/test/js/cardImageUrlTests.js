const assert = require('assert');
const fs = require('fs');
const vm = require('vm');
const [cardPath, imagePath, packPath] = process.argv.slice(2);
function load(origin) {
    const context = vm.createContext({window: origin ? {SWCCG_RES_ORIGIN: origin} : {}});
    for (const file of [imagePath, packPath, cardPath])
        vm.runInContext(fs.readFileSync(file, 'utf8'), context, {filename: file});
    vm.runInContext('this.CardUnderTest = Card; this.ImagesUnderTest = fixedImages; this.PacksUnderTest = packBlueprints;', context);
    return context;
}
const official = 'https://res.starwarsccg.org';
const legacyPrefix = official + '/cards/legacy/';
const defaults = load();
const legacy = Object.entries(defaults.ImagesUnderTest).filter(([,url]) => url.startsWith(legacyPrefix));
assert(legacy.length > 1000, 'Exercise the real published Legacy image mappings');
for (const [id,url] of legacy)
    assert.strictEqual(defaults.CardUnderTest.getImageUrl(id), url, 'Canonical Legacy scan: ' + id);
for (const id of ['1230_94', '1230_97', '1230_95', '1230_98'])
    assert(defaults.CardUnderTest.getImageUrl(id).startsWith(legacyPrefix), id);
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1_19'), 'https://swccg.com/cards/Premiere-Light/large/lukeskywalker.gif');
assert.strictEqual(defaults.CardUnderTest.resUrl(official + '/gemp/pixel.png'), 'https://swccg.com/gemp/pixel.png');
assert.strictEqual(defaults.CardUnderTest.FoilStaticImage, 'https://swccg.com/cards/holo.jpg');
for (const [id,url] of Object.entries(defaults.PacksUnderTest))
    assert.strictEqual(defaults.CardUnderTest.getImageUrl(id), url.replace(official, 'https://swccg.com'), id);
const configured = load('https://assets.example.test');
for (const [id,url] of legacy)
    assert.strictEqual(configured.CardUnderTest.getImageUrl(id), url.replace(official, 'https://assets.example.test'), id);
assert.strictEqual(configured.CardUnderTest.getImageUrl('1_19'), 'https://assets.example.test/cards/Premiere-Light/large/lukeskywalker.gif');
const originalVirtual = Object.entries(defaults.ImagesUnderTest).filter(([id]) => /^11(?:0\d|1[0-7])_/.test(id));
assert(originalVirtual.length > 700, 'Exercise the Original Virtual artwork registry');
for (const [id, url] of originalVirtual) {
    assert(url.startsWith('https://wiki.swccg.com/images/'), 'Original Virtual artwork uses a direct file: ' + id);
    assert.strictEqual(defaults.CardUnderTest.getImageUrl(id), url, id);
    assert.strictEqual(configured.CardUnderTest.getImageUrl(id), url, id);
}
assert.notStrictEqual(defaults.CardUnderTest.getImageUrl('1101_2'), defaults.CardUnderTest.getImageUrl('1101_11'));
assert.strictEqual(defaults.CardUnderTest.resUrl('https://wiki.swccg.com/images/card.png'), 'https://wiki.swccg.com/images/card.png');
assert.strictEqual(defaults.CardUnderTest.resUrl(null), null);
assert.strictEqual(defaults.CardUnderTest.resUrl(undefined), undefined);
assert.strictEqual(defaults.CardUnderTest.getImageUrl('unknown_card'), null);
// Current-card override after // END legacy wins over the generated entry (docs/legacy-versions.md, "Current card image")
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_124'), 'https://wiki.swccg.com/images/c/c5/VB1-123-Faithful_Service-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2210_124'), 'https://wiki.swccg.com/images/5/54/VB1-123-Faithful_Service.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_36'), 'https://wiki.swccg.com/images/3/33/VB1-36-Forced_Servitude-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2210_36'), 'https://wiki.swccg.com/images/2/2e/VB1-36-Forced_Servitude.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_63'), 'https://wiki.swccg.com/images/f/f3/VB1-63-Local_Trouble-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2210_63'), 'https://wiki.swccg.com/images/9/9e/VB1-63-Local_Trouble.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_101'), 'https://wiki.swccg.com/images/8/88/VB1-99E-Han_Solo-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2210_101'), 'https://wiki.swccg.com/images/d/d7/VB1-99E-Han_Solo.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1220_43'), 'https://wiki.swccg.com/images/6/65/VB2-43-Precision_Targeting-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2220_43'), 'https://wiki.swccg.com/images/b/b3/VB2-43-Precision_Targeting.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1240_1'), 'https://wiki.swccg.com/images/3/33/VB4-01-Arica-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2240_1'), 'https://wiki.swccg.com/images/e/e6/VB4-01-Arica.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1240_29'), 'https://wiki.swccg.com/images/0/0d/VB4-30E-Krayt_Dragon_Bones-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2240_29'), 'https://wiki.swccg.com/images/0/04/VB4-30E-Krayt_Dragon_Bones.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_92'), 'https://wiki.swccg.com/images/0/0f/VB1-92-C_3PO_See_Threepio-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2210_92'), 'https://wiki.swccg.com/images/a/a6/VB1-92-C_3PO_See_Threepio.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1220_123'), 'https://wiki.swccg.com/images/7/74/VB2-123-Frostbite-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2220_123'), 'https://wiki.swccg.com/images/5/59/VB2-123-Frostbite.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1240_9'), 'https://wiki.swccg.com/images/4/40/VB4-07-Imperial_Stormtrooper-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2240_9'), 'https://wiki.swccg.com/images/1/1a/VB4-07-Imperial_Stormtrooper.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1240_68'), 'https://wiki.swccg.com/images/5/56/VB4-70-LE_BO2D9_Leebo-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2240_68'), 'https://wiki.swccg.com/images/7/73/VB4-70-LE_BO2D9_Leebo.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1230_102'), 'https://wiki.swccg.com/images/0/0e/VB3-103-Diversionary_Tactics-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2230_102'), 'https://wiki.swccg.com/images/8/89/VB3-103-Diversionary_Tactics.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('601_180'), 'https://wiki.swccg.com/images/3/33/VB4-01-Arica-PC-E1-cardmaker.png');
console.log('card image URL routing passed: ' + legacy.length + ' Legacy mappings');
