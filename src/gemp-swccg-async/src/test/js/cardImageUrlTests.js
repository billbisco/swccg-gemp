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
// Batch B05 (Virtual Block 2): current cards and PC-E1 version cards show the CardMaker image of their own text
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1220_60'), 'https://wiki.swccg.com/images/f/fa/VB2-60-He_s_All_Yours_Bounty_Hunter-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1220_64'), 'https://wiki.swccg.com/images/0/03/VB2-64-Release_Your_Anger-PC-E2-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1220_68'), 'https://wiki.swccg.com/images/d/d6/VB2-67-Slip_Sliding_Away-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1220_83'), 'https://wiki.swccg.com/images/9/9a/VB2-83-AT_AT_Cannon-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1220_92'), 'https://wiki.swccg.com/images/c/c4/VB2-92-Jeroen_Webb-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1220_93'), 'https://wiki.swccg.com/images/8/80/VB2-93-Kebyc-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1220_125'), 'https://wiki.swccg.com/images/2/20/VB2-125-Hopping_Mad-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1220_139'), 'https://wiki.swccg.com/images/3/3b/VB2-139E-Away_Put_Your_Weapon-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1220_145'), 'https://wiki.swccg.com/images/f/f8/VB2-145-Impressive_Most_Impressive-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('601_167'), 'https://wiki.swccg.com/images/f/f8/VB2-145-Impressive_Most_Impressive-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2220_1064'), 'https://wiki.swccg.com/images/e/ee/VB2-64-Release_Your_Anger-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2220_1121'), 'https://wiki.swccg.com/images/7/78/VB2-121-Evacuation_Control-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1220_121'), 'https://res.starwarsccg.org/cards/legacy/VirtualBlock2-Light/large/evacuationcontrol.gif');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2220_121'), 'https://wiki.swccg.com/images/1/18/VB2-121-Evacuation_Control.png');
console.log('card image URL routing passed: ' + legacy.length + ' Legacy mappings');
