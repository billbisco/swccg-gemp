package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_019_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("uutkik", "1283_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("backpack", "1_206");
                    put("rifle", "1_312");
                    put("rifle2", "1_312");
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
    public void UutkikVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("uutkik").getBlueprint();

        assertEquals("Uutkik", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(Species.JAWA, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.THIEF);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private VirtualTableScenario pilferSetup(int destiny) {
        var scn = GetScenario();
        var uutkik = scn.GetLSCard("uutkik");
        var trooper = scn.GetDSCard("trooper");
        var backpack = scn.GetDSCard("backpack");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, uutkik, trooper);
        scn.AttachCardsTo(trooper, backpack);
        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.LSCardActionAvailable(uutkik, "pilfer"));
        scn.PrepareLSDestiny(destiny);
        scn.LSUseCardAction(uutkik, "pilfer");
        if (scn.LSHasCardChoiceAvailable(backpack)) {
            scn.LSChooseCard(backpack);
        }
        scn.PassAllResponses();
        return scn;
    }

    @Test
    public void UutkikPilfersDeviceHereWhenDestinyGreaterThanFour() {
        var scn = pilferSetup(5);
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("backpack"));
    }

    @Test
    public void UutkikDoesNotPilferDeviceWhenDestinyIsFour() {
        var scn = pilferSetup(4);
        assertTrue(scn.IsAttachedTo(scn.GetDSCard("trooper"), scn.GetDSCard("backpack")));
    }

    @Test
    public void UutkikMayPilferOnlyDuringYourMovePhaseAndOncePerPhase() {
        var scn = GetScenario();
        var uutkik = scn.GetLSCard("uutkik");
        var trooper = scn.GetDSCard("trooper");
        var backpack = scn.GetDSCard("backpack");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, uutkik, trooper);
        scn.AttachCardsTo(trooper, backpack);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(uutkik, "pilfer"));
        scn.SkipToLSTurn(Phase.MOVE);
        scn.PrepareLSDestiny(0);
        scn.LSUseCardAction(uutkik, "pilfer");
        if (scn.LSHasCardChoiceAvailable(backpack)) {
            scn.LSChooseCard(backpack);
        }
        scn.PassAllResponses();
        if (scn.DSAnyDecisionsAvailable()) {
            scn.DSPass();
        }
        assertEquals(Phase.MOVE, scn.GetCurrentPhase());
        assertFalse(scn.LSCardActionAvailable(uutkik, "pilfer"));
    }

    @Test
    public void UutkikStealsAWeaponFromOpponentsLostPileOncePerGameDuringOpponentsTurn() {
        var scn = GetScenario();
        var uutkik = scn.GetLSCard("uutkik");
        var rifle = scn.GetDSCard("rifle");
        var rifle2 = scn.GetDSCard("rifle2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, uutkik);
        scn.MoveCardsToTopOfDSLostPile(rifle, rifle2);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(uutkik, "Steal"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(uutkik, "Steal"));
        scn.LSUseCardAction(uutkik, "Steal");
        if (scn.LSHasCardChoiceAvailable(rifle)) {
            scn.LSChooseCard(rifle);
        }
        scn.PassAllResponses();
        assertTrue(rifle.getZone() == Zone.HAND || rifle2.getZone() == Zone.HAND);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(uutkik, "Steal"));
    }
}
