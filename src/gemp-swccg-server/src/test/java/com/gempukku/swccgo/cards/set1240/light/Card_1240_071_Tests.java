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
                    put("station", "1210_117"); // Lars' Hydroponics Station
                    put("crop", "1_4");         // any card to be the 'crop'
                    put("handcard", "2_14");
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
        assertEquals(3, scn.GetForfeit(droid));
        assertEquals(printed, scn.GetForfeit(boshek));

        int droidPrinted = scn.GetForfeit(droid);
        scn.MoveCardsToLocation(farm, droid, boshek);
        assertEquals(printed + 3, scn.GetForfeit(boshek));
        // The droid itself is one of your characters present.
        assertEquals(droidPrinted + 3, scn.GetForfeit(droid));
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

    private VirtualTableScenario cropScenario(boolean atFarm, boolean withCrop) {
        var scn = GetScenario();
        var droid = scn.GetLSCard("droid");
        var farm = scn.GetLSCard("farm");
        var station = scn.GetLSCard("station");
        var crop = scn.GetLSCard("crop");
        var handcard = scn.GetLSCard("handcard");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.AttachCardsTo(farm, station);
        if (withCrop) {
            scn.StackCardsOn(station, crop);
        }
        scn.MoveCardsToLocation(atFarm ? farm : scn.GetLSStartingLocation(), droid);
        scn.MoveCardsToLSHand(handcard);
        scn.SkipToLSTurn(Phase.MOVE);
        return scn;
    }

    @Test
    public void LarsProtocolDroidOncePerMovePhasePutsHandInUsedPileToPlaceCropOnReserveDeck() {
        var scn = cropScenario(true, true);
        var droid = scn.GetLSCard("droid");
        var crop = scn.GetLSCard("crop");
        var handcard = scn.GetLSCard("handcard");

        assertTrue(scn.LSCardActionAvailable(droid, "Place a 'crop' on Reserve Deck"));
        scn.LSUseCardAction(droid, "Place a 'crop' on Reserve Deck");
        for (int i = 0; i < 6 && crop.getZone() != Zone.TOP_OF_RESERVE_DECK && crop.getZone() != Zone.RESERVE_DECK; i++) {
            if (scn.LSHasCardChoiceAvailable(crop)) {
                scn.LSChooseCard(crop);
            }
            else {
                scn.PassAllResponses();
            }
        }
        assertEquals(crop, scn.GetTopOfLSReserveDeck());
        assertEquals(0, scn.GetLSHandCount());
        assertTrue(handcard.getZone() == Zone.USED_PILE || handcard.getZone() == Zone.TOP_OF_USED_PILE);

        scn.PassAllResponses();
        if (scn.DSGetDecision() != null) {
            scn.DSPass();
        }
        if (scn.LSGetDecision() != null && scn.GetCurrentPhase() == Phase.MOVE) {
            assertFalse(scn.LSCardActionAvailable(droid, "Place a 'crop' on Reserve Deck"));
        }
    }

    @Test
    public void LarsProtocolDroidNoCropActionAwayFromFarmOrWithoutCrop() {
        var scn = cropScenario(false, true);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("droid"), "Place a 'crop' on Reserve Deck"));

        var scn2 = cropScenario(true, false);
        assertFalse(scn2.LSCardActionAvailable(scn2.GetLSCard("droid"), "Place a 'crop' on Reserve Deck"));
    }

    @Test
    public void LarsProtocolDroidNoCropActionOutsideMovePhase() {
        var scn = GetScenario();
        var droid = scn.GetLSCard("droid");
        var farm = scn.GetLSCard("farm");
        var station = scn.GetLSCard("station");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.AttachCardsTo(farm, station);
        scn.StackCardsOn(station, scn.GetLSCard("crop"));
        scn.MoveCardsToLocation(farm, droid);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(droid, "Place a 'crop' on Reserve Deck"));
    }
}
