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
// Batch B03: current cards show the CardMaker image of their latest text; versions keep the slip
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_4'), 'https://wiki.swccg.com/images/1/17/VB1-04-Commander_Praji-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_6'), 'https://wiki.swccg.com/images/3/34/VB1-06-Danz_Borin-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_15'), 'https://wiki.swccg.com/images/7/7d/VB1-15-Kitik_Keed_kak-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_17'), 'https://wiki.swccg.com/images/a/a3/VB1-17-Prophetess-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('601_152'), 'https://wiki.swccg.com/images/a/a3/VB1-17-Prophetess-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_26'), 'https://wiki.swccg.com/images/d/d7/VB1-26-Ur_Ru_r-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_30'), 'https://wiki.swccg.com/images/2/2f/VB1-30-A_Disturbance_In_The_Force-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_43'), 'https://wiki.swccg.com/images/0/03/VB1-38E-Imperial_Detainment-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_44'), 'https://wiki.swccg.com/images/9/92/VB1-44E-Luke_Luuuuke-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_45'), 'https://wiki.swccg.com/images/3/38/VB1-45-Maneuver_Check-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1210_56'), 'https://wiki.swccg.com/images/5/50/VB1-56-Coordinated_Attack-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2210_4'), 'https://wiki.swccg.com/images/2/28/VB1-04-Commander_Praji.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2220_1156'), 'https://wiki.swccg.com/images/b/ba/VB2-156-Through_The_Force_Things_You_Will_See-PC-E1-cardmaker.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('2220_156'), 'https://wiki.swccg.com/images/8/80/VB2-156-Through_The_Force_Things_You_Will_See.png');
assert.strictEqual(defaults.CardUnderTest.getImageUrl('1220_156'), 'https://res.starwarsccg.org/cards/legacy/VirtualBlock2-Light/large/throughtheforcethingsyouwillsee.gif');
console.log('card image URL routing passed: ' + legacy.length + ' Legacy mappings');
