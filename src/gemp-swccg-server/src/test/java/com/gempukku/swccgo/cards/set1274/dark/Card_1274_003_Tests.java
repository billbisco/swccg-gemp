package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("baktoid", "1274_3");
                    put("aat", "14_121");        // Armored Attack Tank: [Theed Palace] vehicle, immune to attrition < 4
                    put("aat2", "14_121");
                    put("pilot", "14_74");       // Battle Droid Pilot
                    put("cantina", "1_290");     // Tatooine: Cantina (interior site)
                    put("moseisley", "1_295");   // Tatooine: Mos Eisley (exterior battleground site)
                    put("trooper", "1_194");
                }},
                30, 30,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    // Deployment Orders (Virtual Block 6.2) is not in GEMP yet: stand-in by title
    private static AbstractNormalEffect deploymentOrders() {
        return new AbstractNormalEffect(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Deployment Orders", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
        };
    }

    private static boolean isSelectable(VirtualTableScenario scn, String player, PhysicalCardImpl card) {
        String[] blueprints = scn.GetADParam(player, "blueprintId");
        String[] selectable = scn.GetADParam(player, "selectable");
        if (blueprints == null) {
            return false;
        }
        for (int i = 0; i < blueprints.length; i++) {
            if (card.getBlueprintId(true).equals(blueprints[i]) && (selectable == null || Boolean.parseBoolean(selectable[i]))) {
                return true;
            }
        }
        return false;
    }

    private static boolean dsCan(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        try {
            return scn.DSCardActionAvailable(card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @Test
    public void BaktoidArmorWorkshopStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("baktoid").getBlueprint();

        assertEquals("Baktoid Armor Workshop", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CORUSCANT);
            add(Icon.EPISODE_I);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BaktoidArmorWorkshopDeploysOnTable() {
        var scn = GetScenario();
        var baktoid = scn.GetDSCard("baktoid");

        scn.StartGame();
        scn.MoveCardsToDSHand(baktoid);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(baktoid));
        scn.DSPlayCard(baktoid);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, baktoid);
    }

    @Test
    public void BaktoidArmorWorkshopYourTheedPalaceVehiclesAreDefenseValueAndImmunityToAttritionPlusOne() {
        var scn = GetScenario();
        var baktoid = scn.GetDSCard("baktoid");
        var aat = scn.GetDSCard("aat");
        var pilot = scn.GetDSCard("pilot");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aat);
        scn.BoardAsPilot(aat, pilot);
        int defenseBefore = scn.GetDefense(aat);
        float immunityBefore = scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), aat);
        scn.MoveCardsToSideOfTable(baktoid);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(defenseBefore + 1, scn.GetDefense(aat));
        assertEquals(immunityBefore + 1, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), aat), scn.epsilon);
    }

    @Test
    public void BaktoidArmorWorkshopMayDeployDeploymentOrdersFromReserveDeck() {
        var scn = GetScenario();
        var baktoid = scn.GetDSCard("baktoid");

        scn.StartGame();
        scn.MoveCardsToSideOfTable(baktoid);
        var orders = MissionTestSupport.putInReserve(scn, 9741, scn.DS, deploymentOrders());
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(baktoid, "Deploy Deployment Orders from Reserve Deck"));
        scn.DSUseCardAction(baktoid, "Deploy Deployment Orders from Reserve Deck");
        scn.DSChooseCard(orders);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, orders);
    }

    @Test
    public void BaktoidArmorWorkshopMayNotDeploySiteWithoutDeploymentOrdersOnTable() {
        var scn = GetScenario();
        var baktoid = scn.GetDSCard("baktoid");
        var moseisley = scn.GetDSCard("moseisley");

        scn.StartGame();
        scn.MoveCardsToSideOfTable(baktoid);
        scn.MoveCardsToBottomOfDSReserveDeck(moseisley);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(baktoid, "Deploy exterior battleground site"));
    }

    @Test
    public void BaktoidArmorWorkshopOncePerGameDeploysExteriorBattlegroundSiteIfDeploymentOrdersOnTable() {
        var scn = GetScenario();
        var baktoid = scn.GetDSCard("baktoid");
        var moseisley = scn.GetDSCard("moseisley");
        var cantina = scn.GetDSCard("cantina");

        scn.StartGame();
        scn.MoveCardsToSideOfTable(baktoid);
        MissionTestSupport.putOnTable(scn, 9742, scn.DS, deploymentOrders());
        scn.MoveCardsToBottomOfDSReserveDeck(moseisley, cantina);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(baktoid, "Deploy exterior battleground site"));
        scn.DSUseCardAction(baktoid, "Deploy exterior battleground site");
        assertFalse(isSelectable(scn, scn.DS, cantina));
        scn.DSChooseCard(moseisley);
        if (scn.DSDecisionAvailable("") && scn.DSChoiceAvailable("Left")) {
            scn.DSChooseOption("Left");
        }
        scn.PassAllResponses();
        assertTrue(moseisley.getZone().isInPlay());
        assertFalse(dsCan(scn, baktoid, "Deploy exterior battleground site"));
    }

    @Test
    public void BaktoidArmorWorkshopOncePerGameRetrievesTheedPalaceVehicleIntoHand() {
        var scn = GetScenario();
        var baktoid = scn.GetDSCard("baktoid");
        var aat = scn.GetDSCard("aat");
        var aat2 = scn.GetDSCard("aat2");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToSideOfTable(baktoid);
        scn.MoveCardsToTopOfDSLostPile(trooper, aat, aat2);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(baktoid, "Retrieve"));
        scn.DSUseCardAction(baktoid, "Retrieve");
        scn.PassAllResponses();
        assertFalse(isSelectable(scn, scn.DS, trooper));
        scn.DSChooseCard(aat);
        scn.PassAllResponses();
        assertEquals(1, (aat.getZone() == Zone.HAND ? 1 : 0) + (aat2.getZone() == Zone.HAND ? 1 : 0));
        assertInZone(Zone.LOST_PILE, trooper);
        assertFalse(dsCan(scn, baktoid, "Retrieve"));
    }
}
