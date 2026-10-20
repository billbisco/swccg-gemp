package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_053_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("more", "1291_53");
                    put("luck", "1281_37");
                    put("bravado", "1281_34");
                    put("charm", "1281_35");
                    put("ingenuity", "1281_36");
                    put("lando", "601_50");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("blaster", "1_317");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private static final String INFILTRATION_ID = "2000_9103";

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

    private PhysicalCardImpl setupLuckStacked(VirtualTableScenario scn, PhysicalCardImpl luck,
                                              PhysicalCardImpl bravado, PhysicalCardImpl charm, PhysicalCardImpl ingenuity) {
        PhysicalCardImpl infiltration = putInfiltrationOnTable(scn);
        beginLsDeploy(scn, luck);
        scn.MoveCardsToTopOfLSReserveDeck(bravado, charm, ingenuity);
        deployLuckOnInfiltration(scn, luck, infiltration);
        completeStackThreeFromReserve(scn, luck, bravado, charm, ingenuity);
        return infiltration;
    }

    private boolean lsMayPlay(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        if (scn.GetAwaitingDecision(scn.LS) == null) {
            return false;
        }
        String[] cardIds = scn.LSGetADParam("cardId");
        String[] actionTexts = scn.LSGetADParam("actionText");
        if (cardIds == null || actionTexts == null) {
            return false;
        }
        String id = String.valueOf(card.getCardId());
        for (int i = 0; i < cardIds.length; i++) {
            if (cardIds[i].equals(id) && (text == null || actionTexts[i].contains(text))) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void ThisIsMoreLikeItVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("more").getBlueprint();

        assertEquals("This Is More Like It", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ThisIsMoreLikeItVCancelsWeaponTargetingAScoundrel() {
        var scn = GetScenario();
        var more = scn.GetLSCard("more");
        var lando = scn.GetLSCard("lando");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando, trooper);
        scn.AttachCardsTo(trooper, blaster);
        scn.MoveCardsToLSHand(more);
        scn.DSActivateForceCheat(4);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(lando);
        scn.LSPass();
        scn.DSPass();
        assertTrue(lsMayPlay(scn, more, "Cancel weapon targeting"));
        scn.LSPlayCard(more, "Cancel weapon targeting");
        scn.PassAllResponses();

        assertFalse(lando.isHit());
        assertInZone(Zone.USED_PILE, more);
    }

    @Test
    public void ThisIsMoreLikeItVDeploysAScoundrelEffectFromUnderLuck() {
        var scn = GetScenario();
        var more = scn.GetLSCard("more");
        var luck = scn.GetLSCard("luck");
        var bravado = scn.GetLSCard("bravado");
        var charm = scn.GetLSCard("charm");
        var ingenuity = scn.GetLSCard("ingenuity");
        var lando = scn.GetLSCard("lando");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        setupLuckStacked(scn, luck, bravado, charm, ingenuity);
        scn.MoveCardsToLocation(site, lando, luke);
        scn.ApplyAdHocModifier(new KeywordModifier(luke, Keyword.SCOUNDREL));
        scn.MoveCardsToLSHand(more);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(lsMayPlay(scn, more, "Scoundrel's Luck"));
        scn.LSPlayCard(more, "Scoundrel's Luck");
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(bravado));
        scn.LSChooseCard(bravado);
        assertTrue(scn.LSHasCardChoiceAvailable(lando));
        assertTrue(scn.LSHasCardChoiceAvailable(luke));
        scn.LSChooseCard(lando);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(lando, bravado));
        assertInZone(Zone.USED_PILE, more);
    }

    @Test
    public void ThisIsMoreLikeItVDoesNotOfferTransferWithOnlyOneScoundrel() {
        var scn = GetScenario();
        var more = scn.GetLSCard("more");
        var bravado = scn.GetLSCard("bravado");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        scn.AttachCardsTo(lando, bravado);
        scn.MoveCardsToLSHand(more);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertFalse(lsMayPlay(scn, more, "Transfer"));
    }

    @Test
    public void ThisIsMoreLikeItVDoesNotCancelWeaponTargetingANonScoundrel() {
        var scn = GetScenario();
        var more = scn.GetLSCard("more");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.AttachCardsTo(trooper, blaster);
        scn.MoveCardsToLSHand(more);
        scn.DSActivateForceCheat(4);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(luke);
        scn.LSPass();
        scn.DSPass();
        assertFalse(lsMayPlay(scn, more, "Cancel"));
        assertEquals(Zone.HAND, more.getZone());
    }
}
