package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInForcePileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOnReserveDeckFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_037_Tests {
    private static final String INFILTRATION_ID = "2000_9101";

    private static class TestInfiltration extends AbstractObjective {
        TestInfiltration() {
            super(Side.LIGHT, 0, "Infiltration", ExpansionSet.PREMIERE, Rarity.R);
            setFrontOfDoubleSidedCard(true);
        }
    }

    private static class TestUnlikelyAllies extends AbstractObjective {
        TestUnlikelyAllies() {
            super(Side.LIGHT, 7, "Unlikely Allies", ExpansionSet.PREMIERE, Rarity.R);
        }
    }

    protected VirtualTableScenario GetScenario() {
        return GetScenario(new HashMap<>() {{
            put("trooper", "1_194");
        }});
    }

    protected VirtualTableScenario GetScenario(HashMap<String, String> extraDS) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luck", "1281_37");
                    put("bravado", "1281_34");
                    put("charm", "1281_35");
                    put("ingenuity", "1281_36");
                    put("lando", "601_50");
                    put("han", "1_11");
                    put("blaster", "1_152");
                    put("grenade", "3_073");
                }},
                extraDS,
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @After
    public void removeInfiltrationStub() {
        unregisterInfiltration();
    }

    @SuppressWarnings("unchecked")
    private void registerInfiltration() {
        try {
            Field field = SwccgCardBlueprintLibrary.class.getDeclaredField("_blueprintMap");
            field.setAccessible(true);
            Map<String, SwccgCardBlueprint> map = (Map<String, SwccgCardBlueprint>) field.get(VirtualTableScenario._cardLibrary);
            map.put(INFILTRATION_ID, new TestInfiltration());
            map.put(INFILTRATION_ID + "_BACK", new TestUnlikelyAllies());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void unregisterInfiltration() {
        try {
            Field field = SwccgCardBlueprintLibrary.class.getDeclaredField("_blueprintMap");
            field.setAccessible(true);
            Map<String, SwccgCardBlueprint> map = (Map<String, SwccgCardBlueprint>) field.get(VirtualTableScenario._cardLibrary);
            map.remove(INFILTRATION_ID);
            map.remove(INFILTRATION_ID + "_BACK");
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private PhysicalCardImpl putInfiltrationOnTable(VirtualTableScenario scn) {
        registerInfiltration();
        PhysicalCardImpl card = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.LS, VirtualTableScenario._cardLibrary, INFILTRATION_ID);
        scn.gameState().addCardToZone(card, Zone.SIDE_OF_TABLE, scn.LS, true, false, false);
        return card;
    }

    private PhysicalCardImpl putHuntDownOnTable(VirtualTableScenario scn) {
        PhysicalCardImpl hunt = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.DS, VirtualTableScenario._cardLibrary, "7_297");
        scn.gameState().addCardToZone(hunt, Zone.SIDE_OF_TABLE, scn.DS, true, false, false);
        return hunt;
    }

    private boolean isLsCardChoiceSelectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        String[] selectable = scn.LSGetADParam("selectable");
        String[] blueprintIds = scn.LSGetADParam("blueprintId");
        if (blueprintIds != null) {
            for (int i = 0; i < blueprintIds.length; i++) {
                if (card.getBlueprintId(true).equals(blueprintIds[i])) {
                    return selectable == null || Boolean.parseBoolean(selectable[i]);
                }
            }
        }
        String[] ids = scn.LSGetADParam("cardId");
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                try {
                    if (Integer.parseInt(ids[i]) == card.getCardId()) {
                        return selectable == null || Boolean.parseBoolean(selectable[i]);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return false;
    }

    private void beginLsDeploy(VirtualTableScenario scn, PhysicalCardImpl luck) {
        scn.MoveCardsToLSHand(luck);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
    }

    private void deployLuckOnInfiltration(VirtualTableScenario scn, PhysicalCardImpl luck, PhysicalCardImpl infiltration) {
        assertTrue(scn.LSDeployAvailable(luck));
        scn.LSDeployCard(luck);
        assertTrue(scn.LSHasCardChoiceAvailable(infiltration));
        scn.LSChooseCard(infiltration);
        scn.PassAllResponses();
    }

    private PhysicalCardImpl setupLuckStacked(VirtualTableScenario scn, PhysicalCardImpl luck,
                                              PhysicalCardImpl bravado, PhysicalCardImpl charm, PhysicalCardImpl ingenuity) {
        PhysicalCardImpl infiltration = putInfiltrationOnTable(scn);
        beginLsDeploy(scn, luck);
        scn.MoveCardsToTopOfLSReserveDeck(bravado, charm, ingenuity);
        deployLuckOnInfiltration(scn, luck, infiltration);
        completeStackThreeFromReserve(scn, luck, bravado, charm, ingenuity);
        return infiltration;
    }

    private void completeStackThreeFromReserve(VirtualTableScenario scn, PhysicalCardImpl luck,
                                               PhysicalCardImpl bravado, PhysicalCardImpl charm, PhysicalCardImpl ingenuity) {
        if (scn.GetStackedCards(luck).size() < 3) {
            assertTrue(scn.GetLSAvailableActions().toString().contains("Stack three"));
            scn.LSChooseAction("Stack three");
            scn.PassAllResponses();
        }
        assertEquals(3, scn.GetStackedCards(luck).size());
        assertTrue(scn.GetStackedCards(luck).contains(bravado));
        assertTrue(scn.GetStackedCards(luck).contains(charm));
        assertTrue(scn.GetStackedCards(luck).contains(ingenuity));
    }

    private void leaveTableAndExpectRestack(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl luck,
                                            Function<Action, StandardEffect> leave) {
        scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.LSGetDecision() != null);
        int index = scn.GetLSAvailableActions().size();
        TopLevelGameTextAction action = new TopLevelGameTextAction(card, scn.LS, card.getCardId());
        action.appendEffect(leave.apply(action));
        ((CardActionSelectionDecision) scn.LSGetDecision()).addAction(action);
        scn.PlayerDecided(scn.LS, String.valueOf(index));
        scn.PassAllResponses();
        assertTrue(scn.GetStackedCards(luck).contains(card));
    }

    @Test
    public void ScoundrelsLuckStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("luck").getBlueprint();

        assertEquals("Scoundrel's Luck", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ScoundrelsLuckDeploysOnInfiltrationAndStacksThreeScoundrelCards() {
        var scn = GetScenario();
        var luck = scn.GetLSCard("luck");
        var bravado = scn.GetLSCard("bravado");
        var charm = scn.GetLSCard("charm");
        var ingenuity = scn.GetLSCard("ingenuity");

        scn.StartGame();
        var infiltration = setupLuckStacked(scn, luck, bravado, charm, ingenuity);

        assertTrue(scn.IsAttachedTo(infiltration, luck));
        assertEquals(3, scn.GetStackedCards(luck).size());
        assertTrue(scn.GetStackedCards(luck).contains(bravado));
        assertTrue(scn.GetStackedCards(luck).contains(charm));
        assertTrue(scn.GetStackedCards(luck).contains(ingenuity));
    }

    @Test
    public void ScoundrelsLuckDeploysBravadoFromUnderHereOntoAScoundrelOncePerTurn() {
        var scn = GetScenario();
        var luck = scn.GetLSCard("luck");
        var bravado = scn.GetLSCard("bravado");
        var charm = scn.GetLSCard("charm");
        var ingenuity = scn.GetLSCard("ingenuity");
        var lando = scn.GetLSCard("lando");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando, han);
        scn.ApplyAdHocModifier(new KeywordModifier(han, Keyword.SCOUNDREL));
        setupLuckStacked(scn, luck, bravado, charm, ingenuity);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());

        assertTrue(scn.LSCardActionAvailable(luck, "Deploy a [Scoundrel] Effect from here"));
        scn.LSUseCardAction(luck, "Deploy a [Scoundrel] Effect from here");
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(bravado));
        scn.LSChooseCard(bravado);
        assertTrue(scn.LSHasCardChoiceAvailable(lando));
        assertTrue(scn.LSHasCardChoiceAvailable(han));
        scn.LSChooseCard(lando);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPass();

        assertTrue(scn.IsAttachedTo(lando, bravado));
        assertFalse(scn.LSCardActionAvailable(luck, "Deploy a [Scoundrel] Effect from here"));
    }

    @Test
    public void ScoundrelsLuckStaysAttachedWhenInfiltrationFlipsToUnlikelyAllies() {
        var scn = GetScenario();
        var luck = scn.GetLSCard("luck");
        var bravado = scn.GetLSCard("bravado");
        var charm = scn.GetLSCard("charm");
        var ingenuity = scn.GetLSCard("ingenuity");

        scn.StartGame();
        var infiltration = setupLuckStacked(scn, luck, bravado, charm, ingenuity);
        assertTrue(infiltration.isDoubleSided());
        scn.gameState().flipCard(scn.game(), infiltration, true);
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals("Unlikely Allies", infiltration.getBlueprint().getTitle());
        assertTrue(scn.IsAttachedTo(infiltration, luck));
        assertEquals(Zone.ATTACHED, luck.getZone());
    }

    @Test
    public void ScoundrelsLuckFlipsTheirFireWhenTwoNonDroidScoundrelsAreAtBattlegroundsAndMayNotFlipBack() {
        var scn = GetScenario();
        var luck = scn.GetLSCard("luck");
        var bravado = scn.GetLSCard("bravado");
        var charm = scn.GetLSCard("charm");
        var ingenuity = scn.GetLSCard("ingenuity");
        var lando = scn.GetLSCard("lando");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando, han);
        scn.ApplyAdHocModifier(new KeywordModifier(han, Keyword.SCOUNDREL));
        var hunt = putHuntDownOnTable(scn);
        assertTrue(hunt.isDoubleSided());
        scn.gameState().flipCard(scn.game(), hunt, true);
        assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, hunt.getBlueprint().getTitle());

        assertTrue(scn.HasKeyword(lando, Keyword.SCOUNDREL));
        assertTrue(scn.HasKeyword(han, Keyword.SCOUNDREL));
        setupLuckStacked(scn, luck, bravado, charm, ingenuity);
        assertEquals(Title.Hunt_Down_And_Destroy_The_Jedi, hunt.getBlueprint().getTitle());
        assertFalse(GameConditions.canBeFlipped(scn.game(), hunt));
    }

    @Test
    public void ScoundrelsLuckStacksScoundrelEffectThatWasLostFromTable() {
        runLeaveTableRestack(action -> new LoseCardFromTableEffect(action, action.getActionSource()));
    }

    @Test
    public void ScoundrelsLuckStacksScoundrelEffectPlacedInUsedPile() {
        runLeaveTableRestack(action -> new PlaceCardInUsedPileFromTableEffect(action, action.getActionSource()));
    }

    @Test
    public void ScoundrelsLuckStacksScoundrelEffectReturnedToHand() {
        runLeaveTableRestack(action -> new ReturnCardToHandFromTableEffect(action, action.getActionSource()));
    }

    @Test
    public void ScoundrelsLuckStacksScoundrelEffectPlacedOutOfPlay() {
        runLeaveTableRestack(action -> new PlaceCardOutOfPlayFromTableEffect(action, action.getActionSource()));
    }

    @Test
    public void ScoundrelsLuckStacksScoundrelEffectPlacedInReserveDeck() {
        runLeaveTableRestack(action -> new PlaceCardOnReserveDeckFromTableEffect(action, action.getActionSource()));
    }

    @Test
    public void ScoundrelsLuckStacksScoundrelEffectPlacedInForcePile() {
        runLeaveTableRestack(action -> new PlaceCardInForcePileFromTableEffect(action, action.getActionSource()));
    }

    private void runLeaveTableRestack(Function<Action, StandardEffect> leave) {
        var scn = GetScenario();
        var luck = scn.GetLSCard("luck");
        var bravado = scn.GetLSCard("bravado");
        var charm = scn.GetLSCard("charm");
        var ingenuity = scn.GetLSCard("ingenuity");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        setupLuckStacked(scn, luck, bravado, charm, ingenuity);
        scn.AttachCardsTo(lando, bravado);
        leaveTableAndExpectRestack(scn, bravado, luck, leave);
    }

    @Test
    public void ScoundrelsLuckStacksAllScoundrelEffectsAtStartOfTurn() {
        var scn = GetScenario();
        var luck = scn.GetLSCard("luck");
        var bravado = scn.GetLSCard("bravado");
        var charm = scn.GetLSCard("charm");
        var ingenuity = scn.GetLSCard("ingenuity");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        setupLuckStacked(scn, luck, bravado, charm, ingenuity);
        scn.AttachCardsTo(lando, bravado);
        scn.SkipToLSTurn();
        scn.PassAllResponses();
        assertTrue(scn.GetStackedCards(luck).contains(bravado));
    }

    @Test
    public void ScoundrelsLuckSecondCopyMayNotDeploy() {
        var scn = GetScenario();
        var luck = scn.GetLSCard("luck");
        var bravado = scn.GetLSCard("bravado");
        var charm = scn.GetLSCard("charm");
        var ingenuity = scn.GetLSCard("ingenuity");

        scn.StartGame();
        setupLuckStacked(scn, luck, bravado, charm, ingenuity);
        PhysicalCardImpl luck2 = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.LS, VirtualTableScenario._cardLibrary, "1281_37");
        scn.gameState().addCardToZone(luck2, Zone.HAND, scn.LS, true, false, false);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSDeployAvailable(luck2));
    }

    @Test
    public void ScoundrelsLuckDeploysCharacterWeaponFromLostPile() {
        var scn = GetScenario();
        var luck = scn.GetLSCard("luck");
        var bravado = scn.GetLSCard("bravado");
        var charm = scn.GetLSCard("charm");
        var ingenuity = scn.GetLSCard("ingenuity");
        var lando = scn.GetLSCard("lando");
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var grenade = scn.GetLSCard("grenade");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        var junk = scn.GetLSFiller(1);
        scn.MoveCardsToLocation(site, lando, han);
        scn.MoveCardsToTopOfLSLostPile(junk, blaster, grenade);
        setupLuckStacked(scn, luck, bravado, charm, ingenuity);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSCardActionAvailable(luck, "Deploy character weapon from Lost Pile"));
        scn.LSUseCardAction(luck, "Deploy character weapon from Lost Pile");
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(junk));
        scn.LSChooseCard(junk);
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(blaster));
        assertFalse(isLsCardChoiceSelectable(scn, grenade));
        scn.LSChooseCard(blaster);
        assertTrue(scn.LSHasCardChoiceAvailable(lando));
        assertTrue(scn.LSHasCardChoiceAvailable(han));
        scn.LSChooseCard(lando);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(lando, blaster));
        assertEquals(Zone.OUT_OF_PLAY, junk.getZone());
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(luck, "Deploy character weapon from Lost Pile"));
    }

    @Test
    public void ScoundrelsLuckIsImmuneToAlter() {
        var scn = GetScenario(new HashMap<>() {{
            put("alter", "1_234");
            put("trooper", "1_194");
        }});
        var luck = scn.GetLSCard("luck");
        var alter = scn.GetDSCard("alter");

        scn.StartGame();
        var infiltration = putInfiltrationOnTable(scn);
        scn.AttachCardsTo(infiltration, luck);
        scn.MoveCardsToDSHand(alter);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardPlayAvailable(alter));
        assertTrue(scn.IsAttachedTo(infiltration, luck));
    }
}
