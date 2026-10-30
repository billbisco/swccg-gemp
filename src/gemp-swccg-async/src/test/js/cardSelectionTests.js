// Execute the actual shared game UI handlers against a small DOM adapter.
const assert=require('assert'),fs=require('fs'),vm=require('vm');
let nodes=[],byId={};
function node(id,classes=[]){const n={id,classes:new Set(classes),data:{},options:{},html:'',children:[]};nodes.push(n);if(id)byId[id]=n;return n;}
function wrap(list){return {
 addClass(c){list.forEach(n=>n.classes.add(c));return this;},removeClass(c){list.forEach(n=>n.classes.delete(c));return this;},
 hasClass(c){return !!list[0]&&list[0].classes.has(c);},data(k,v){if(arguments.length===1)return list[0]?.data[k];list.forEach(n=>n.data[k]=v);return this;},
 text(v){list.forEach(n=>n.text=v);return this;},
 html(v){if(v===undefined)return list[0]?.html;list.forEach(n=>n.html=v);return this;},
 append(v){list.forEach(n=>n.children.push(v));if(typeof v==='string'){for(const m of v.matchAll(/id=['"]([^'"]+)/g))node(m[1]);}return this;},
 dialog(...a){if(a[0]==='option')list.forEach(n=>n.options[a[1]]=a[2]);return this;},
 button(){return this;},click(fn){list.forEach(n=>n.click=fn);return this;},blur(){return this;},css(){return this;},
 each(fn){list.slice().forEach(n=>fn.call(n));return this;},remove(){list.forEach(n=>{nodes=nodes.filter(x=>x!==n);if(n.id)delete byId[n.id];});return this;},
 closest(){return wrap(list.map(n=>n.parent||n));}
};}
function $(s){if(typeof s!=='string')return wrap([s]);if(s.includes(', '))return wrap(nodes.filter(n=>s.split(', ').some(c=>n.classes.has(c.slice(1)))));let m=s.match(/^\.card:cardId\((.*)\)$/);if(m)return wrap(nodes.filter(n=>n.classes.has('card')&&m[1].split(',').includes(n.data.card.cardId)));if(s[0]==='#')return wrap([byId[s.slice(1)]||node(s.slice(1))]);if(s[0]==='.')return wrap(nodes.filter(n=>n.classes.has(s.slice(1))));return wrap([]);}
const ctx={Class:{extend:x=>x},$,openSizeDialog(){},Card:function(b,t,bt,h,z,id){this.cardId=id;this.zone=z;}};
vm.runInNewContext(fs.readFileSync(process.argv[2],'utf8'),ctx);
const methods=ctx.GempSwccgGameUI;
function decision(params={}){return {getAttribute:k=>k==='id'?'choice':'Choose cards',getElementsByTagName:()=>Object.entries(params).flatMap(([k,v])=>(Array.isArray(v)?v:[v]).map(x=>({getAttribute:a=>a==='name'?k:String(x)})))};}
function setup(auto=false){nodes=[];byId={};const sent=[];const ui=Object.assign({},methods,{settingsAutoAccept:auto,replayMode:false,successfulDrag:false,cardInfoDialog:{isOpen:()=>false},
 cardActionDialog:$(node('dialog')),alertButtons:$(node('buttons')),alertText:null,alertBox:null,extraActionsGroup:{layoutCards(){}},
 playSound(){},arbitraryDialogResize(){},suspendDecisionCountdown(){},stopDecisionCountdown(){},startDecisionCountdown(){},decisionFunction:(id,value)=>sent.push(value),
 createCardDiv(card,text){const n=node(null,['card']);n.data.card=card;n.text=text;return n;},
 createActionChoiceContextMenu(actions,event,select){select(actions[0].actionId);},displayCardInfo(){this.examined=true;}});return {ui,sent};}
function card(id,zone='TABLE'){const n=node(null,['card']);n.data.card={cardId:id,zone};return n;}
function click(ui,id,extra={}){const n=nodes.find(n=>n.data.card?.cardId===id);assert(n,'Missing '+id);ui.clickCardFunction(Object.assign({target:{classes:new Set(['actionArea']),parent:n},which:1,stopPropagation(){}},extra));}
function selected(id){return nodes.find(n=>n.data.card?.cardId===id).classes.has('selectedCard');}
function arbitrary(ui,extra={}){ui.arbitraryCardsDecision(decision(Object.assign({min:0,max:2,cardId:['a','b','c'],blueprintId:['1_1','1_2','1_3'],preselected:['false','false','false'],selectable:['true','true','true'],cardText:['3 credits','2 credits','1 credits']},extra)));}
// Credits and ordinary reserve/deck choices share the same toggle logic, including at maximum.
for(const credits of [false,true]){const {ui,sent}=setup();arbitrary(ui,credits?{startingCredits:5}:{});click(ui,'a');click(ui,'b');assert(selected('a')&&selected('b'));click(ui,'c');assert(!selected('c'));click(ui,'a');assert(!selected('a')&&selected('b'));click(ui,'c');byId.dialog.options.buttons.Done();assert.deepStrictEqual(sent,['b,c']);assert.strictEqual(ui.selectionFunction,null);}
// A preselected editable card can be removed, but a fixed preselection cannot.
{const {ui,sent}=setup();arbitrary(ui,{preselected:['true','true','false'],selectable:['true','false','true']});click(ui,'b');assert(selected('b'));click(ui,'a');assert(!selected('a'));byId.dialog.options.buttons.Done();assert.deepStrictEqual(sent,['b']);}
{const {ui,sent}=setup();arbitrary(ui,{preselected:['true','true','false'],selectable:['true','false','true']});byId.dialog.options.buttons['Clear selection']();assert(!selected('a')&&selected('b'));byId.dialog.options.buttons.Done();assert.deepStrictEqual(sent,['b']);}
// Immediate-return choices report deselection too, and auto-accept single choices still submit once.
{const {ui,sent}=setup();arbitrary(ui,{preselected:['true','false','false'],returnAnyChange:true});click(ui,'a');assert.deepStrictEqual(sent,['']);}
for(const method of ['arbitraryCardsDecision','cardSelectionDecision']){const {ui,sent}=setup(true);if(method==='arbitraryCardsDecision')arbitrary(ui,{max:1});else {card('a');ui[method](decision({min:1,max:1,cardId:['a']}));}click(ui,'a');assert.deepStrictEqual(sent,['a']);click(ui,'a');assert.equal(sent.length,1);}
// Table-target selections support toggle, minimum, maximum, order and replacement.
{const {ui,sent}=setup();card('a');card('b');card('c');ui.cardSelectionDecision(decision({min:2,max:2,cardId:['a','b','c']}));click(ui,'a');click(ui,'b');click(ui,'a');assert(!selected('a'));click(ui,'c');byId.Done.click();assert.deepStrictEqual(sent,['b,c']);}
// Right-click/shift still examine, replay does not change pending selection.
{const {ui}=setup();arbitrary(ui);click(ui,'a');click(ui,'a',{which:3});assert(ui.examined&&selected('a'));ui.replayMode=true;click(ui,'a');assert(selected('a'));}
// Manual action confirmations can be undone; temporary action cards are not duplicated.
for(const temporary of [false,true]){const {ui,sent}=setup();if(!temporary)card('a','TOP_OF_RESERVE_DECK');ui.cardActionChoiceDecision(decision({cardId:['a'],blueprintId:[temporary?'1_1':'inPlay'],actionId:['act'],actionText:['Use action']}));const id=temporary?'extraa':'a';click(ui,id);assert(selected(id));click(ui,id);assert(!selected(id));assert.equal(nodes.filter(n=>n.data.card?.cardId===id).length,1);click(ui,id);byId.Done.click();assert.deepStrictEqual(sent,['act']);}
{const {ui,sent}=setup();ui.actionChoiceDecision(decision({blueprintId:['1_1'],actionId:['act'],actionText:['Use action']}));click(ui,'temp0');assert(selected('temp0'));click(ui,'temp0');assert(!selected('temp0'));click(ui,'temp0');byId.dialog.options.buttons.Done();assert.deepStrictEqual(sent,['act']);}
{const {ui}=setup();ui.settingsCardActionsSilent=true;const n=card('a','TOP_OF_RESERVE_DECK');ui.cardActionChoiceDecision(decision({cardId:['a'],blueprintId:['inPlay'],actionId:['act'],actionText:['Use action']}));for(let i=0;i<3;i++){click(ui,'a');click(ui,'a');assert.equal(n.data.action.length,1);assert(n.classes.has('actionableCardSilent'));}}
// Symbol markup is numeric-only, carries an accessible unit, and appears in both cost and budget.
{const {ui}=setup();arbitrary(ui,{startingCredits:5});const cost=nodes.find(n=>n.data.card?.cardId==='a').text;assert(cost.startsWith('3<img'));assert(cost.includes("alt=' credits'"));assert(cost.includes('reflections-gold-credit.png'));assert(byId.arbitraryChoice.children[0].includes('Budget: 5<img'));assert.equal(ui.creditCostHtml('<script>'),'');}
// Over-budget Done keeps the dialog, selected cards and handler intact for correction.
for(const auto of [false,true]){const {ui,sent}=setup(auto);arbitrary(ui,{startingCredits:4});click(ui,'a');click(ui,'b');if(!auto)byId.dialog.options.buttons.Done();assert.deepStrictEqual(sent,[]);assert(selected('a')&&selected('b'));assert.equal(byId.creditSelectionError.text,'Too many credits selected. Please try again.');click(ui,'b');assert(selected('a')&&!selected('b'));byId.dialog.options.buttons.Done();assert.deepStrictEqual(sent,['a']);}
console.log('card selection interactions passed');
