package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.common.CardSubtype;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_049_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("army", "1291_49");
                    put("atte", "1291_72"); // AT-TE ([Clone Army] combat vehicle, may add 1 pilot)
                    put("atte2", "1291_72");
                    put("clonepilot", "1116_8"); // Clone Pilot: clone, pilot, ability 2
                    put("odd", "200_6"); // Cody: clone, ability 2, not a pilot
                    put("obi", "11_6"); // Obi-Wan: pilot, not a clone
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void AGrandArmyOfTheRepublicStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("army").getBlueprint();
        assertEquals("A Grand Army Of The Republic", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AGrandArmyOfTheRepublicDeploysOnTable() {
        var scn = GetScenario();
        var army = scn.GetLSCard("army");
        scn.StartGame();
        scn.MoveCardsToLSHand(army);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(army));
        scn.LSPlayCard(army);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, army);
    }

    @Test
    public void AGrandArmyOfTheRepublicDrawsTopCardOfReserveDeckOncePerTurnWhenYouDeployACombatVehicle() {
        var scn = GetScenario();
        var army = scn.GetLSCard("army");
        var atte = scn.GetLSCard("atte");
        var atte2 = scn.GetLSCard("atte2");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation(); // AT-TE may not deploy to the Light Side Cloud City site

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(army);
        scn.MoveCardsToLSHand(atte, atte2);
        scn.EnsureLSForcePile(10);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(luke);

        assertTrue(scn.LSDeployAvailable(atte));
        scn.LSDeployCard(atte);
        scn.LSChooseCard(site);
        scn.PassResponses("Use ");
        scn.PassResponses("PLAY");
        assertTrue(scn.LSActionAvailable("Draw top card of Reserve Deck"));
        scn.LSChooseAction("Draw top card of Reserve Deck");
        scn.PassAllResponses();
        assertAtLocation(site, atte);
        assertInHand(luke);

        // Once per turn: a second combat vehicle deployed this turn offers no draw
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        int handBefore = scn.GetLSHandCount();
        int reserveBefore = scn.GetLSReserveDeckCount();
        scn.LSDeployCard(atte2);
        scn.LSChooseCard(site);
        scn.PassResponses("Use ");
        scn.PassResponses("PLAY");
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(t -> t.contains("Draw top card of Reserve Deck")));
        scn.PassAllResponses();
        assertAtLocation(site, atte2);
        assertEquals(handBefore - 1, scn.GetLSHandCount());
        assertEquals(reserveBefore, scn.GetLSReserveDeckCount());
    }

    @Test
    public void AGrandArmyOfTheRepublicDoesNotDrawWhenYouDeployANonVehicle() {
        var scn = GetScenario();
        var army = scn.GetLSCard("army");
        var obi = scn.GetLSCard("obi");
        var site = scn.GetDSStartingLocation(); // AT-TE may not deploy to the Light Side Cloud City site

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(army);
        scn.MoveCardsToLSHand(obi);
        scn.EnsureLSForcePile(10);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int reserveBefore = scn.GetLSReserveDeckCount();
        scn.LSDeployCard(obi);
        scn.LSChooseCard(site);
        scn.PassResponses("Use ");
        scn.PassResponses("PLAY");
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(t -> t.contains("Draw top card of Reserve Deck")));
        scn.PassAllResponses();
        assertAtLocation(site, obi);
        assertEquals(reserveBefore, scn.GetLSReserveDeckCount());
    }

    @Test
    public void AGrandArmyOfTheRepublicRevealsCloneArmyVehicleToDeployItWithAClonePilotFromReserveDeck() {
        var scn = GetScenario();
        var army = scn.GetLSCard("army");
        var atte = scn.GetLSCard("atte");
        var clonePilot = scn.GetLSCard("clonepilot");
        var obi = scn.GetLSCard("obi");
        var site = scn.GetDSStartingLocation(); // AT-TE may not deploy to the Light Side Cloud City site

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(army);
        scn.MoveCardsToLSHand(atte);
        scn.EnsureLSForcePile(10);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(clonePilot, obi);
        int forceBefore = scn.GetLSForcePileCount();

        assertTrue(scn.LSCardActionAvailable(army, "Reveal [Clone Army] vehicle or clone pilot from hand"));
        scn.LSUseCardAction(army, "Reveal [Clone Army] vehicle or clone pilot from hand");
        if (scn.LSDecisionAvailable("Choose card from hand")) {
            scn.LSChooseCard(atte);
        }
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(clonePilot));
        scn.LSChooseCard(clonePilot);
        if (scn.LSDecisionAvailable("Choose where to deploy")) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();

        assertAtLocation(site, atte);
        assertTrue(scn.IsAboardAsPilot(atte, clonePilot));
        // 1 Force for A Grand Army, 4 for AT-TE, 2 for Clone Pilot
        assertEquals(forceBefore - 1 - 4 - 2, scn.GetLSForcePileCount());
        assertInZone(Zone.RESERVE_DECK, obi);
    }

    @Test
    public void AGrandArmyOfTheRepublicRevealsClonePilotToDeployItWithACloneArmyVehicleFromReserveDeck() {
        var scn = GetScenario();
        var army = scn.GetLSCard("army");
        var atte = scn.GetLSCard("atte");
        var clonePilot = scn.GetLSCard("clonepilot");
        var site = scn.GetDSStartingLocation(); // AT-TE may not deploy to the Light Side Cloud City site

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(army);
        scn.MoveCardsToLSHand(clonePilot);
        scn.EnsureLSForcePile(10);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(atte);

        assertTrue(scn.LSCardActionAvailable(army, "Reveal [Clone Army] vehicle or clone pilot from hand"));
        scn.LSUseCardAction(army, "Reveal [Clone Army] vehicle or clone pilot from hand");
        if (scn.LSDecisionAvailable("Choose card from hand")) {
            scn.LSChooseCard(clonePilot);
        }
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(atte));
        scn.LSChooseCard(atte);
        if (scn.LSDecisionAvailable("Choose where to deploy")) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();

        assertAtLocation(site, atte);
        assertTrue(scn.IsAboardAsPilot(atte, clonePilot));
    }

    @Test
    public void AGrandArmyOfTheRepublicDoesNotRevealNonClonePilotsOrNonPilotClones() {
        var scn = GetScenario();
        var army = scn.GetLSCard("army");
        var obi = scn.GetLSCard("obi");
        var cody = scn.GetLSCard("odd");
        var atte = scn.GetLSCard("atte");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(army);
        scn.MoveCardsToLSHand(obi, cody);
        scn.EnsureLSForcePile(10);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(atte);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(army, "Reveal [Clone Army] vehicle or clone pilot from hand"));
    }
}
