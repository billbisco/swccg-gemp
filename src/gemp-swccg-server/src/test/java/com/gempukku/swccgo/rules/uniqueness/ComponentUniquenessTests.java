package com.gempukku.swccgo.rules.uniqueness;

import com.gempukku.swccgo.cards.*;
import com.gempukku.swccgo.cards.conditions.CardTitleNotPlayedThisTurnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import org.junit.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;

public class ComponentUniquenessTests {
    private static final TitleComponent PROJECTION = new TitleComponent("Test Projection", Uniqueness.RESTRICTED_2);
    private static final TitleComponent YARNA = new TitleComponent("Test Yarna", Uniqueness.UNIQUE);
    private static final TitleComponent REPEAT = new TitleComponent("Test Repeat", Uniqueness.UNRESTRICTED);
    private static final TitleComponent ONCE = new TitleComponent("Test Once", Uniqueness.UNIQUE);
    private static final AtomicInteger NEXT_ID = new AtomicInteger();
    private final List<String> registered = new ArrayList<>();

    private static class TestEffect extends AbstractEffect {
        TestEffect(Side side, TitleComponent... components) {
            super(side, 3f, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, components[0].name(), components[0].uniqueness(), ExpansionSet.PREMIERE, Rarity.C1);
            if (components.length > 1) setTitleComponents(components);
        }
    }
    private static class TestInterrupt extends AbstractUsedOrLostInterrupt {
        TestInterrupt(Side side, TitleComponent... components) {
            super(side, 3, components[0].name(), components[0].uniqueness(), ExpansionSet.PREMIERE, Rarity.C1);
            if (components.length > 1) setTitleComponents(components);
        }
        @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String player, SwccgGame game, PhysicalCard self) {
            var actions = new ArrayList<PlayInterruptAction>();
            for (CardSubtype subtype : List.of(CardSubtype.USED, CardSubtype.LOST)) {
                var action = new PlayInterruptAction(game, self, subtype);
                action.setText(subtype.name());
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override protected void performActionResults(Action targetingAction) { }
                });
                actions.add(action);
            }
            return actions;
        }
    }
    @SuppressWarnings("unchecked") private Map<String,SwccgCardBlueprint> library() throws Exception {
        var field=SwccgCardBlueprintLibrary.class.getDeclaredField("_blueprintMap"); field.setAccessible(true);
        return (Map<String,SwccgCardBlueprint>) field.get(VirtualTableScenario._cardLibrary);
    }
    @After public void removeFixtures() throws Exception { for(String id:registered) library().remove(id); }
    private PhysicalCardImpl hand(VirtualTableScenario s, SwccgCardBlueprint blueprint) throws Exception {
        String id="2100_"+NEXT_ID.incrementAndGet(); library().put(id,blueprint); registered.add(id);
        String owner=blueprint.getSide()==Side.LIGHT?s.LS:s.DS;
        var card=(PhysicalCardImpl)s.gameState().createPhysicalCard(owner,VirtualTableScenario._cardLibrary,id);
        s.gameState().addCardToZone(card,Zone.HAND,owner,true,false,false); return card;
    }
    private VirtualTableScenario scenario() {
        var dark=new HashMap<String,String>(); dark.put("sense","1_267"); dark.put("vader","1_168");
        var light=new HashMap<String,String>(); light.put("luke","1_19");
        var s=new VirtualTableScenario(light,dark,40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
        s.StartGame(); return s;
    }
    private void deploy(VirtualTableScenario s, PhysicalCardImpl card) {
        s.LSDeployCard(card); s.PassAllResponses(); if(s.AwaitingDSDeployPhaseActions()) s.DSPass();
        assertTrue(s.AwaitingLSDeployPhaseActions()); assertEquals(Zone.SIDE_OF_TABLE,card.getZone());
    }
    private void play(VirtualTableScenario s, PhysicalCardImpl card, String mode) {
        s.LSPlayCard(card,mode); s.PassAllResponses(); if(s.AwaitingDSControlPhaseActions()) s.DSPass();
        assertTrue(s.AwaitingLSControlPhaseActions());
    }
    @Test public void MixedEffectAllowsRemainingRestrictedCopyInEitherDeploymentOrder() throws Exception {
        for(boolean comboFirst:new boolean[]{true,false}) {
            var s=scenario(); var combo=hand(s,new TestEffect(Side.LIGHT,PROJECTION,YARNA)); var projection=hand(s,new TestEffect(Side.LIGHT,PROJECTION)); var third=hand(s,new TestEffect(Side.LIGHT,PROJECTION)); var yarna=hand(s,new TestEffect(Side.LIGHT,YARNA)); var secondCombo=hand(s,new TestEffect(Side.LIGHT,PROJECTION,YARNA));
            s.SkipToLSTurn(Phase.DEPLOY); deploy(s,comboFirst?combo:projection); deploy(s,comboFirst?projection:combo);
            assertFalse(s.LSDeployAvailable(third)); assertFalse(s.LSDeployAvailable(yarna)); assertFalse(s.LSDeployAvailable(secondCombo));
        }
    }
    @Test public void MixedEffectBlockedByTwoRestrictedCopiesOrUniqueComponent() throws Exception {
        for(boolean restricted:new boolean[]{true,false}) {
            var s=scenario(); var combo=hand(s,new TestEffect(Side.LIGHT,PROJECTION,YARNA)); var first=hand(s,new TestEffect(Side.LIGHT,restricted?PROJECTION:YARNA)); var second=hand(s,new TestEffect(Side.LIGHT,PROJECTION));
            s.SkipToLSTurn(Phase.DEPLOY); deploy(s,first); if(restricted) deploy(s,second); assertFalse(s.LSDeployAvailable(combo));
        }
    }
    @Test public void MixedEffectCountsOpponentsComponentsOnTableAndInactiveCards() throws Exception {
        var s=scenario(); var combo=hand(s,new TestEffect(Side.LIGHT,PROJECTION,YARNA)); var opponent=hand(s,new TestEffect(Side.DARK,YARNA));
        s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetDSCard("vader")); s.RemoveCardZone(opponent); s.gameState().stackCard(opponent,s.GetDSCard("vader"),false,true,false);
        assertTrue(opponent.isStackedAsInactive());
        s.SkipToLSTurn(Phase.DEPLOY); assertFalse(s.LSDeployAvailable(combo));
        assertTrue(s.game().getModifiersQuerying().isUniquenessOnTableLimitReached(s.gameState(),combo));
    }
    @Test public void MixedEffectTurnCountsSurviveLeavingTableAndResetNextTurn() throws Exception {
        var s=scenario(); var combo=hand(s,new TestEffect(Side.LIGHT,PROJECTION,YARNA)); var projection=hand(s,new TestEffect(Side.LIGHT,PROJECTION)); var third=hand(s,new TestEffect(Side.LIGHT,PROJECTION)); var yarna=hand(s,new TestEffect(Side.LIGHT,YARNA));
        s.SkipToLSTurn(Phase.DEPLOY); deploy(s,combo); s.MoveCardsToTopOfLSLostPile(combo); deploy(s,projection); s.MoveCardsToTopOfLSLostPile(projection);
        assertTrue(s.game().getModifiersQuerying().isPlayingCardTitleTurnLimitReached(s.gameState(),third)); assertTrue(s.game().getModifiersQuerying().isPlayingCardTitleTurnLimitReached(s.gameState(),yarna));
        s.SkipToPhase(Phase.ACTIVATE);
        assertFalse(s.game().getModifiersQuerying().isPlayingCardTitleTurnLimitReached(s.gameState(),third)); assertFalse(s.game().getModifiersQuerying().isPlayingCardTitleTurnLimitReached(s.gameState(),yarna));
    }
    @Test public void MixedInterruptAllowsUnrestrictedRepeatsButRecordsUniqueComponentForBothModes() throws Exception {
        for(String mode:List.of("USED","LOST")) {
            var s=scenario(); var before=hand(s,new TestInterrupt(Side.LIGHT,REPEAT)); var combo=hand(s,new TestInterrupt(Side.LIGHT,REPEAT,ONCE)); var after=hand(s,new TestInterrupt(Side.LIGHT,REPEAT)); var unique=hand(s,new TestInterrupt(Side.LIGHT,ONCE)); var other=hand(s,new TestInterrupt(Side.DARK,REPEAT,ONCE));
            s.SkipToLSTurn(Phase.CONTROL); play(s,before,"USED"); play(s,combo,mode); play(s,after,"USED");
            assertFalse(s.LSCardPlayAvailable(unique)); assertTrue(s.game().getModifiersQuerying().isPlayingCardTitleTurnLimitReached(s.gameState(),other));
            assertTrue((mode.equals("USED")?s.GetLSUsedPile():s.GetLSLostPile()).contains(combo));
            assertEquals(3,s.game().getModifiersQuerying().getCardTitlePlayedTurnLimitCounter(REPEAT.name()).getUsedLimit());
            assertEquals(1,s.game().getModifiersQuerying().getTitleComponentPlayedTurnLimitCounter(ONCE).getUsedLimit());
            assertFalse(new CardTitleNotPlayedThisTurnCondition(unique).isFulfilled(s.gameState(),s.game().getModifiersQuerying()));
        }
    }
    @Test public void PrintedSymbolsDistinguishIdenticalNamesWhileNamedTextStillMatches() throws Exception {
        var s=scenario(); var combo=hand(s,new TestEffect(Side.LIGHT,PROJECTION,YARNA)); var same=hand(s,new TestEffect(Side.LIGHT,new TitleComponent("test projection",Uniqueness.RESTRICTED_2))); var different=hand(s,new TestEffect(Side.LIGHT,new TitleComponent(PROJECTION.name(),Uniqueness.UNIQUE))); var punctuation=hand(s,new TestEffect(Side.LIGHT,new TitleComponent("Test Projection!",Uniqueness.RESTRICTED_2)));
        assertTrue(Filters.sameTitleAs(combo).accepts(s.game(),same)); assertFalse(Filters.sameTitleAs(combo).accepts(s.game(),different)); assertFalse(Filters.sameTitleAs(combo).accepts(s.game(),punctuation));
        assertTrue(Filters.title(PROJECTION.name()).accepts(s.game(),different)); assertTrue(Filters.title(YARNA.name()).accepts(s.game(),combo));
        s.SkipToLSTurn(Phase.DEPLOY); deploy(s,different); deploy(s,combo); deploy(s,same);
    }
    @Test public void MixedComponentFiltersAndDisplayRetainEachPrintedSymbol() throws Exception {
        var s=scenario(); var combo=hand(s,new TestInterrupt(Side.LIGHT,REPEAT,ONCE));
        assertTrue(Filters.unique.accepts(s.game(),combo)); assertTrue(Filters.non_unique.accepts(s.game(),combo)); assertTrue(Filters.uniqueness(Uniqueness.UNRESTRICTED).accepts(s.game(),combo));
        assertTrue(GameUtils.getCardLink(combo).contains("Test Repeat & •Test Once"));
    }
    @Test public void TitleComponentsRejectDuplicateNamesAndRemainImmutable() {
        assertThrows(IllegalArgumentException.class,()->new TestEffect(Side.LIGHT,PROJECTION,new TitleComponent("test projection",Uniqueness.UNIQUE)));
        assertThrows(IllegalArgumentException.class,()->new TitleComponent("",Uniqueness.UNIQUE));
        var card=new TestEffect(Side.LIGHT,PROJECTION,YARNA);
        assertThrows(UnsupportedOperationException.class,()->card.getTitleComponents().add(ONCE));
        assertEquals(new TitleComponent("TEST PROJECTION",Uniqueness.RESTRICTED_2),PROJECTION);
        assertEquals(new TitleComponent("TEST PROJECTION",Uniqueness.RESTRICTED_2).hashCode(),PROJECTION.hashCode());
    }

    @Test public void MixedInterruptCanceledUniquePlayStillBlocksComboUntilNextTurn() throws Exception {
        var s=scenario(); var once=hand(s,new TestInterrupt(Side.LIGHT,ONCE)); var combo=hand(s,new TestInterrupt(Side.LIGHT,REPEAT,ONCE));
        s.MoveCardsToDSHand(s.GetDSCard("sense")); s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetDSCard("vader"));
        s.SkipToLSTurn(Phase.CONTROL); s.PrepareDSDestiny(1); s.LSPlayCard(once,"USED"); s.LSPass();
        s.DSPlayCard(s.GetDSCard("sense")); s.DSChooseCard(s.GetDSCard("vader")); s.PassAllResponses();
        if(s.AwaitingDSControlPhaseActions()) s.DSPass();
        assertTrue(s.GetLSLostPile().contains(once)); assertFalse(s.LSCardPlayAvailable(combo));
        assertEquals(1,s.game().getModifiersQuerying().getTitleComponentPlayedTurnLimitCounter(ONCE).getUsedLimit());
        s.SkipToPhase(Phase.ACTIVATE); assertFalse(s.game().getModifiersQuerying().isPlayingCardTitleTurnLimitReached(s.gameState(),combo));
        assertTrue(new CardTitleNotPlayedThisTurnCondition(combo).isFulfilled(s.gameState(),s.game().getModifiersQuerying()));
    }
    @Test public void MixedComponentUniquenessModifiersChangeLimitsWithoutChangingIdentity() throws Exception {
        var s=scenario(); var combo=hand(s,new TestEffect(Side.LIGHT,PROJECTION,YARNA)); var first=hand(s,new TestEffect(Side.LIGHT,PROJECTION)); var second=hand(s,new TestEffect(Side.LIGHT,PROJECTION));
        var marker=hand(s,new TestEffect(Side.LIGHT,new TitleComponent("Marker",Uniqueness.UNRESTRICTED)));
        s.SkipToLSTurn(Phase.DEPLOY); deploy(s,first); deploy(s,second);
        s.game().getModifiersEnvironment().addUntilEndOfTurnModifier(new UniqueModifier(first,Filters.sameCardId(combo)));
        assertTrue(s.game().getModifiersQuerying().isUniquenessOnTableLimitReached(s.gameState(),combo));
        s.game().getModifiersEnvironment().addUntilEndOfTurnModifier(new NotUniqueModifier(first,Filters.sameCardId(combo)));
        assertFalse(s.game().getModifiersQuerying().isUniquenessOnTableLimitReached(s.gameState(),combo));
        assertEquals(List.of(PROJECTION,YARNA),combo.getTitleComponents()); assertTrue(Filters.sameTitleAs(combo).accepts(s.game(),second));
        // A newly established action window uses the relaxed limits.
        deploy(s,marker); deploy(s,combo);
        assertEquals(3,s.game().getModifiersQuerying().getTitleComponentPlayedTurnLimitCounter(PROJECTION).getUsedLimit());
    }
    @Test public void BuiltInTitleMatchingUsesPrintedSymbolsAndCanonicalPunctuation() throws Exception {
        var s=scenario(); var combo=hand(s,new TestEffect(Side.LIGHT,PROJECTION,YARNA)); var host=s.GetDSCard("vader");
        var matching=new AbstractPermanentWeapon(PROJECTION.name(),Uniqueness.RESTRICTED_2) { }; matching.setPhysicalCard(host);
        var different=new AbstractPermanentWeapon(PROJECTION.name(),Uniqueness.UNIQUE) { }; different.setPhysicalCard(host);
        assertTrue(Filters.sameTitleAs(combo,false).accepts(s.gameState(),s.game().getModifiersQuerying(),matching));
        assertFalse(Filters.sameTitleAs(combo,false).accepts(s.gameState(),s.game().getModifiersQuerying(),different));
        assertTrue(Filters.title(PROJECTION.name()).accepts(s.gameState(),s.game().getModifiersQuerying(),different));
    }

    @Test public void HomogeneousRestrictedComboCountsEachTitleIndependently() throws Exception {
        var s=scenario(); var other=new TitleComponent("Test Other",Uniqueness.RESTRICTED_2);
        var first=hand(s,new TestEffect(Side.LIGHT,PROJECTION)); var second=hand(s,new TestEffect(Side.LIGHT,other)); var combo=hand(s,new TestEffect(Side.LIGHT,PROJECTION,other));
        var extraFirst=hand(s,new TestEffect(Side.LIGHT,PROJECTION)); var extraSecond=hand(s,new TestEffect(Side.LIGHT,other));
        s.SkipToLSTurn(Phase.DEPLOY); deploy(s,first); deploy(s,second); deploy(s,combo);
        assertFalse(s.LSDeployAvailable(extraFirst)); assertFalse(s.LSDeployAvailable(extraSecond));
        assertEquals(2,s.game().getModifiersQuerying().getTitleComponentPlayedTurnLimitCounter(PROJECTION).getUsedLimit());
        assertEquals(2,s.game().getModifiersQuerying().getTitleComponentPlayedTurnLimitCounter(other).getUsedLimit());
    }

    @Test public void LegacyNullUniquenessRetainsItsPredicateResults() throws Exception {
        var s=scenario(); var card=hand(s,new AbstractEffect(Side.LIGHT,3f,PlayCardZoneOption.YOUR_SIDE_OF_TABLE,"No Symbol",null,ExpansionSet.PREMIERE,Rarity.C1) { });
        assertFalse(Filters.unique.accepts(s.game(),card)); assertTrue(Filters.non_unique.accepts(s.game(),card));
        assertFalse(Filters.restricted.accepts(s.game(),card)); assertTrue(Filters.uniqueness(null).accepts(s.game(),card));
        assertEquals(Uniqueness.UNRESTRICTED,card.getTitleComponents().getFirst().uniqueness());
    }
    @Test public void BlownAwayLocationLosesExactComponentAndSameTitleMatching() {
        var light = new HashMap<String, String>();
        light.put("deactivate", "8_043"); light.put("bunker", "8_070"); light.put("copy", "8_070");
        var s = new VirtualTableScenario(light, new HashMap<>(), 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        var bunker = s.GetLSCard("bunker"); var copy = s.GetLSCard("copy");
        var deactivate = s.GetLSCard("deactivate");
        s.StartGame();
        s.MoveLocationToTable(bunker);
        s.MoveCardsToLocation(bunker, s.GetLSFiller(1));
        s.AttachCardsTo(bunker, deactivate);
        var component = bunker.getTitleComponents().get(0);
        assertTrue(Filters.titleComponent(component).accepts(s.game(), bunker));
        assertTrue(Filters.sameTitle(copy).accepts(s.game(), bunker));
        s.SkipToLSTurn(Phase.CONTROL);
        s.PrepareLSDestiny(7); s.PrepareLSDestiny(6);
        s.LSUseCardAction(deactivate); s.PassAllResponses();
        s.DSPayRemainingForceLossFromReserveDeck(); s.PassAllResponses();
        assertTrue(bunker.isBlownAway());
        assertFalse(Filters.titleComponent(component).accepts(s.game(), bunker));
        assertFalse(Filters.sameTitle(copy).accepts(s.game(), bunker));
        assertFalse(s.game().getModifiersQuerying().cardTitlesMatch(s.gameState(), copy, bunker));
    }
}
