// Run by PlayNowWebClientTests (node). Exits non-zero on the first failure.
var assert = require("assert");
var PlayNow = require(process.argv[2]);

//test1: known side and bot are read from the link; anything else is ignored.
assert.deepStrictEqual(PlayNow.parseChoice("?side=dark&bot=yoda"), {side: "dark", bot: "yoda"});
assert.deepStrictEqual(PlayNow.parseChoice("?side=LIGHT&bot=ozzel"), {side: "light", bot: "ozzel"});
assert.deepStrictEqual(PlayNow.parseChoice("?side=<script>&bot=rando"), {side: null, bot: null});
assert.deepStrictEqual(PlayNow.parseChoice(""), {side: null, bot: null});

//test2: the game link carries Play now mode, and guest mode only for guests.
assert.strictEqual(PlayNow.gameUrl("g1", "Light Side", true),
    "/gemp-swccg/game.html?gameId=g1&participantId=Light%20Side&playNow=1&guest=1");
assert.strictEqual(PlayNow.gameUrl("g1", "luke", false),
    "/gemp-swccg/game.html?gameId=g1&participantId=luke&playNow=1");

//test3: the server's message wins; otherwise a plain one by error code.
assert.strictEqual(PlayNow.errorText(503, "busy", "From server"), "From server");
assert.ok(PlayNow.errorText(503, "busy", null).indexOf("in use") > 0);
assert.ok(PlayNow.errorText(503, "disabled", null).indexOf("not available") > 0);
assert.ok(PlayNow.errorText(429, "rate_limited", null).indexOf("wait") > 0);

//test4: the end of the game is spotted from the engine's message.
assert.ok(PlayNow.isGameOverMessage("Light Side is the winner due to: Opponent conceded"));
assert.ok(!PlayNow.isGameOverMessage("Light Side deploys Luke"));
assert.ok(!PlayNow.isGameOverMessage(null));

console.log("playNow ok");

//test5: unfinished games are described for the Resume list.
assert.strictEqual(PlayNow.describeGame({side: "dark", bot: "yoda", saved: true, turn: 7}), "Dark Side vs YodaBot, turn 7");
assert.strictEqual(PlayNow.describeGame({side: "light", bot: "ozzel", saved: false}), "Light Side vs OzzelBot (in progress)");
assert.strictEqual(PlayNow.describeGame({side: "light", bot: "ozzel", saved: true, turn: 0}), "Light Side vs OzzelBot");
console.log("playNow resume ok");
