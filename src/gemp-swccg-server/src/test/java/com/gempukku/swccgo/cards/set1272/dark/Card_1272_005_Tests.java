package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_005_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("stockpile", "1272_5");
                    put("rifle", "1_312");
                    put("saber", "1_314");
                    put("disruptor", "7_319");
                    put("trooper", "1_194");
                    put("trooper2", "1_194");
                    put("trooper3", "1_194");
                    put("trooper4", "1_194");
                }},
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

    private static boolean selectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        String[] blueprints = scn.GetADParam(scn.DS, "blueprintId");
        String[] selectable = scn.GetADParam(scn.DS, "selectable");
        for (int i = 0; i < blueprints.length; i++) {
            if (card.getBlueprintId(true).equals(blueprints[i]) && "true".equals(selectable[i]))
                return true;
        }
        return false;
    }

    @Test
    public void ImperialStockpileStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("stockpile").getBlueprint();

        assertEquals("Imperial Stockpile", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.HOTH);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ImperialStockpileStacksNonLightsaberWeaponWithoutLostFromReserveDeckWhenDeployed() {
        var scn = GetScenario();
        var stockpile = scn.GetDSCard("stockpile");
        var rifle = scn.GetDSCard("rifle");
        var saber = scn.GetDSCard("saber");
        var disruptor = scn.GetDSCard("disruptor");

        //test1: deploying it stacks a weapon from Reserve Deck here
        //test2: lightsabers and weapons with 'lost' in game text are not valid
        scn.StartGame();
        scn.MoveCardsToHand(stockpile);
        scn.EnsureDSForcePile(6);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(rifle, saber, disruptor);

        assertTrue(scn.DSCardPlayAvailable(stockpile));
        scn.DSDeployCard(stockpile);
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose card from Reserve Deck to stack on"));
        assertTrue(selectable(scn, rifle)); //test1
        assertFalse(selectable(scn, saber)); //test2
        assertFalse(selectable(scn, disruptor)); //test2
        assertEquals(1, scn.DSGetSelectableCount()); //test2
        scn.DSChooseCard(rifle);
        scn.PassAllResponses();
        assertTrue(scn.IsStackedOn(stockpile, rifle)); //test1
    }

    @Test
    public void ImperialStockpileDeploysWeaponOnParticipatingCardAndReturnsItAfterWeaponsSegment() {
        var scn = GetScenario();
        var stockpile = scn.GetDSCard("stockpile");
        var rifle = scn.GetDSCard("rifle");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        //test1: when a battle was just initiated, may deploy the stacked weapon on a participating card
        //test2: the weapon returns here at the end of the weapons segment
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(stockpile);
        scn.StackCardsOn(stockpile, rifle);
        scn.MoveCardsToLocation(site, trooper, luke);
        scn.EnsureDSForcePile(6);
        scn.SkipToDSTurn(Phase.BATTLE);

        scn.DSInitiateBattle(site);
        assertTrue(scn.DSCardActionAvailable(stockpile, "Deploy weapon on a participating card")); //test1
        scn.DSUseCardAction(stockpile, "Deploy weapon on a participating card");
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(trooper, rifle)); //test1

        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();
        assertTrue(scn.IsStackedOn(stockpile, rifle)); //test2
    }

    @Test
    public void ImperialStockpileDoesNotOfferDeployWithoutStackedWeapon() {
        var scn = GetScenario();
        var stockpile = scn.GetDSCard("stockpile");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        //test1: nothing to deploy if no weapon is stacked here
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(stockpile);
        scn.MoveCardsToLocation(site, trooper, luke);
        scn.EnsureDSForcePile(6);
        scn.SkipToDSTurn(Phase.BATTLE);

        scn.DSInitiateBattle(site);
        assertFalse(scn.DSCardActionAvailable(stockpile, "Deploy weapon on a participating card")); //test1
    }
}
