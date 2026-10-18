package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractMission;
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
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_037_Tests {
    private static class TestInfiltration extends AbstractMission {
        TestInfiltration() {
            super(Side.LIGHT, 0, "Infiltration", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R, false);
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
                    put("blaster", "1_154");
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

    @SuppressWarnings("unchecked")
    private PhysicalCardImpl putInfiltrationInHand(VirtualTableScenario scn) {
        String blueprintId = "2000_9101";
        try {
            Field field = SwccgCardBlueprintLibrary.class.getDeclaredField("_blueprintMap");
            field.setAccessible(true);
            Map<String, SwccgCardBlueprint> map = (Map<String, SwccgCardBlueprint>) field.get(VirtualTableScenario._cardLibrary);
            map.put(blueprintId, new TestInfiltration());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        PhysicalCardImpl card = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.LS, VirtualTableScenario._cardLibrary, blueprintId);
        scn.gameState().addCardToZone(card, Zone.HAND, scn.LS, true, false, false);
        return card;
    }

    private void playInfiltration(VirtualTableScenario scn, PhysicalCardImpl infiltration) {
        scn.SkipToLSTurn(Phase.DEPLOY);
        if (scn.AwaitingDSDeployPhaseActions()) {
            scn.DSPass();
        }
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(infiltration));
        scn.LSDeployCard(infiltration);
        scn.PassAllResponses();
        if (scn.AwaitingDSDeployPhaseActions()) {
            scn.DSPass();
            scn.PassAllResponses();
        }
        assertEquals(Zone.SIDE_OF_TABLE, infiltration.getZone());
    }

    private PhysicalCardImpl putHuntDownOnTable(VirtualTableScenario scn) {
        PhysicalCardImpl hunt = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.DS, VirtualTableScenario._cardLibrary, "7_297");
        scn.gameState().addCardToZone(hunt, Zone.SIDE_OF_TABLE, scn.DS, true, false, false);
        return hunt;
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
        var infiltration = putInfiltrationInHand(scn);
        scn.MoveCardsToLSHand(luck);
        playInfiltration(scn, infiltration);
        scn.MoveCardsToTopOfLSReserveDeck(bravado, charm, ingenuity);

        assertTrue(scn.LSDeployAvailable(luck));
        scn.LSDeployCard(luck);
        scn.LSChooseCard(infiltration);
        scn.PassAllResponses();
        if (scn.AwaitingDSDeployPhaseActions()) {
            scn.DSPass();
            scn.PassAllResponses();
        }

        assertTrue(scn.IsAttachedTo(infiltration, luck));
        assertEquals(3, scn.GetStackedCards(luck).size());
        assertTrue(scn.GetStackedCards(luck).contains(bravado));
        assertTrue(scn.GetStackedCards(luck).contains(charm));
        assertTrue(scn.GetStackedCards(luck).contains(ingenuity));
    }

    @Test
    public void ScoundrelsLuckDeploysBravadoFromUnderHereOntoAScoundrel() {
        var scn = GetScenario();
        var luck = scn.GetLSCard("luck");
        var bravado = scn.GetLSCard("bravado");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        var charm = scn.GetLSCard("charm");
        var ingenuity = scn.GetLSCard("ingenuity");

        scn.StartGame();
        var infiltration = putInfiltrationInHand(scn);
        scn.MoveCardsToLocation(site, lando);
        scn.MoveCardsToLSHand(luck);
        playInfiltration(scn, infiltration);
        scn.MoveCardsToTopOfLSReserveDeck(bravado, charm, ingenuity);
        scn.LSDeployCard(luck);
        scn.LSChooseCard(infiltration);
        scn.PassAllResponses();
        if (scn.AwaitingDSDeployPhaseActions()) {
            scn.DSPass();
            scn.PassAllResponses();
        }

        assertTrue(scn.LSCardActionAvailable(luck, "Deploy a [Scoundrel] Effect from here"));
        scn.LSUseCardAction(luck, "Deploy a [Scoundrel] Effect from here");
        if (scn.LSHasCardChoiceAvailable(bravado)) {
            scn.LSChooseCard(bravado);
        }
        if (scn.LSHasCardChoiceAvailable(lando)) {
            scn.LSChooseCard(lando);
        }
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(lando, bravado));
    }

    @Test
    public void ScoundrelsLuckFlipsTheirFireWhenTwoNonDroidScoundrelsAreAtBattlegrounds() {
        var scn = GetScenario();
        var luck = scn.GetLSCard("luck");
        var bravado = scn.GetLSCard("bravado");
        var charm = scn.GetLSCard("charm");
        var ingenuity = scn.GetLSCard("ingenuity");
        var lando = scn.GetLSCard("lando");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        var infiltration = putInfiltrationInHand(scn);
        scn.MoveCardsToLocation(site, lando, han);
        scn.ApplyAdHocModifier(new KeywordModifier(han, Keyword.SCOUNDREL));
        scn.MoveCardsToLSHand(luck);
        playInfiltration(scn, infiltration);
        var hunt = putHuntDownOnTable(scn);
        assertTrue(hunt.isDoubleSided());
        scn.gameState().flipCard(scn.game(), hunt, true);
        assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, hunt.getBlueprint().getTitle());
        scn.MoveCardsToTopOfLSReserveDeck(bravado, charm, ingenuity);

        assertTrue(scn.HasKeyword(lando, Keyword.SCOUNDREL));
        assertTrue(scn.HasKeyword(han, Keyword.SCOUNDREL));
        assertTrue(hunt.getZone().isInPlay());
        assertTrue(scn.gameState().isCardInPlayActive(hunt));
        assertTrue(scn.LSDeployAvailable(luck));
        scn.LSDeployCard(luck);
        if (scn.LSHasCardChoiceAvailable(infiltration)) {
            scn.LSChooseCard(infiltration);
        }
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("required")) {
            scn.LSChooseAction("Flip");
            scn.PassAllResponses();
        }
        if (scn.AwaitingDSDeployPhaseActions()) {
            scn.DSPass();
            scn.PassAllResponses();
        }

        assertEquals(Title.Hunt_Down_And_Destroy_The_Jedi, hunt.getBlueprint().getTitle());
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
        var infiltration = putInfiltrationInHand(scn);
        playInfiltration(scn, infiltration);
        scn.AttachCardsTo(infiltration, luck);
        scn.MoveCardsToDSHand(alter);
        scn.SkipToDSTurn(Phase.DEPLOY);
        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardPlayAvailable(alter));
        assertTrue(scn.IsAttachedTo(infiltration, luck));
    }
}
