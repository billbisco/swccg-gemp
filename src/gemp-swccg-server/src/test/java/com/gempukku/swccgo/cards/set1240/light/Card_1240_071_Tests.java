package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_071_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("droid", "1240_71");
                    put("farm", "1_132");
                    put("boshek", "1_4");
                    put("harvest", "1210_154");
                    put("r5d4", "2_15");
                }},
                new HashMap<>() {{
                    put("dsfarm", "1_294");
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

    @Test
    public void LarsProtocolDroidStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("droid").getBlueprint();

        assertEquals("Lars' Protocol Droid", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.PROTOCOL);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.DROID);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LarsProtocolDroidYourCharactersPresentAreForfeitPlus3OnlyAtAFarm() {
        var scn = GetScenario();
        var droid = scn.GetLSCard("droid");
        var farm = scn.GetLSCard("farm");
        var boshek = scn.GetLSCard("boshek");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(site, droid, boshek);
        int printed = scn.GetForfeit(boshek);
        assertEquals(printed, scn.GetForfeit(boshek));

        scn.MoveCardsToLocation(farm, droid, boshek);
        assertEquals(printed + 3, scn.GetForfeit(boshek));
    }

    @Test
    public void LarsProtocolDroidMayNotRaiseUnconvertedFarm() {
        var scn = GetScenario();
        var droid = scn.GetLSCard("droid");
        var farm = scn.GetLSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, droid);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(droid, "Raise converted site to the top"));
    }

    @Test
    public void LarsProtocolDroidMayRaiseConvertedFarmToTheTop() {
        var scn = GetScenario();
        var droid = scn.GetLSCard("droid");
        var farm = scn.GetLSCard("farm");
        var dsfarm = scn.GetDSCard("dsfarm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveLocationToTable(dsfarm);
        // The Dark Side farm now converts the Light Side one.
        assertEquals(dsfarm, scn.gameState().getLocationAtTopOfConvertedLocation(farm));
        scn.MoveCardsToLocation(dsfarm, droid);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(droid, "Raise converted site to the top"));
        scn.LSUseCardAction(droid, "Raise converted site to the top");
        scn.PassAllResponses();
        assertEquals(farm, scn.gameState().getLocationAtTopOfConvertedLocation(dsfarm));
    }

    @Test
    public void LarsProtocolDroidHarvestDeviceMayBeAnANewHopeOrHothDroid() {
        var scn = GetScenario();
        var droid = scn.GetLSCard("droid");
        var harvest = scn.GetLSCard("harvest");
        var r5d4 = scn.GetLSCard("r5d4");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, droid);
        scn.MoveCardsToLSHand(harvest);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(r5d4);

        assertTrue(scn.LSCardActionAvailable(harvest, "Take a device into hand"));
        scn.LSUseCardAction(harvest, "Take a device into hand");
        scn.PassAllResponses();
        scn.LSChooseCard(r5d4);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, r5d4.getZone());
    }

    @Test
    public void HarvestDeviceIsOnlyADeviceWithoutLarsProtocolDroid() {
        var scn = GetScenario();
        var harvest = scn.GetLSCard("harvest");
        var r5d4 = scn.GetLSCard("r5d4");

        scn.StartGame();
        scn.MoveCardsToLSHand(harvest);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(r5d4);

        scn.LSUseCardAction(harvest, "Take a device into hand");
        scn.PassAllResponses();
        int index = scn.LSGetADParamAsList("blueprintId").indexOf(r5d4.getBlueprintId(true));
        assertTrue(index >= 0);
        assertFalse(Boolean.parseBoolean(scn.LSGetADParam("selectable")[index]));
    }
}
