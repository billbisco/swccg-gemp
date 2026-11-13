package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Luke Skywalker, The Emperor's Prize (205_14), as its current PC card image (E1, 16 August 2023): "Plays only during
 * start of game by revealing from outside your deck if Your Destiny is on table and suspended; deploy to Throne Room
 * as a frozen captive. If not revealed, place under your Starting Effect. ... Flip this card if Vader here and not
 * escorting a captive." The 2018 text (still in the PC card data) needed Bring Him Before Me on table, was revealed
 * with the Starting Effect, was otherwise placed out of play and could never be placed in Reserve Deck.
 */
public class Card_205_014_Tests {

    /** The Light Side objective The Hyperdrive Generator's Gone (12_89), which suspends Your Destiny. */
    private static StartingSetup hyperdriveObjective(Boolean revealLuke) {
        return new StartingSetup() {
            @Override
            public HashMap<String, String> Cards() {
                return new HashMap<>() {{
                    put("hyperdrive", "12_89");
                    put("junkyard", "12_87"); // Tatooine: Watto's Junkyard
                    put("outskirts", "11_42"); // Tatooine: City Outskirts
                    put("credits", "12_42"); // Credits Will Do Fine
                }};
            }

            @Override
            public void Setup(VirtualTableScenario scn) {
                for (int i = 0; i < 20; i++) {
                    if (scn.LSDecisionAvailable("On which side")) {
                        scn.LSChoose("Left");
                    } else if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(scn.GetLSCard("junkyard"))) {
                        scn.LSChooseCard(scn.GetLSCard("junkyard"));
                    } else if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(scn.GetLSCard("outskirts"))) {
                        scn.LSChooseCard(scn.GetLSCard("outskirts"));
                    } else if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(scn.GetLSCard("credits"))) {
                        scn.LSChooseCard(scn.GetLSCard("credits"));
                    } else if (revealLuke != null && scn.DSDecisionAvailable("Reveal")) {
                        if (revealLuke) scn.DSChooseYes();
                        else scn.DSChooseNo();
                    } else if (scn.DSDecisionAvailable("On which side")) {
                        scn.DSChoose("Left");
                    } else {
                        return;
                    }
                }
            }
        };
    }

    private static final StartingSetup LUKE_OUTSIDE = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() { return new HashMap<>(Map.of("luke", "205_14")); }

        @Override
        public void Setup(VirtualTableScenario scn) { }
    };

    private static VirtualTableScenario scenario(StartingSetup lightStart) {
        return new VirtualTableScenario(
                new HashMap<>(),
                new HashMap<>() {{
                    put("effect", "200_110"); // Knowledge And Defense (V), a Starting Effect
                    put("vader", "1_168");    // Darth Vader
                }},
                40, 40,
                lightStart,
                StartingSetup.BHBMObjective,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                LUKE_OUTSIDE,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void TextAndFlagsFollowTheCurrentImage() {
        SwccgCardBlueprintLibrary library = new SwccgCardBlueprintLibrary();
        SwccgCardBlueprint front = library.getSwccgoCardBlueprint("205_14");
        SwccgCardBlueprint back = library.getSwccgoCardBlueprint("205_14_BACK");
        assertTrue(front.getGameText().startsWith("Luke Skywalker, The Emperor's Prize is a Dark Side card and is a persona of Luke only while on table. Plays only during start of game by revealing from outside your deck if Your Destiny is on table and suspended"));
        assertTrue(front.getGameText().contains("If not revealed, place under your Starting Effect."));
        assertTrue(front.getGameText().contains("Flip this card if Vader here and not escorting a captive."));
        assertTrue(front.isDoesNotCountTowardDeckLimit());
        // "For remainder of game, may not be placed in Reserve Deck" is no longer printed.
        assertFalse(front.isMayNotBePlacedInReserveDeck());
        assertFalse(back.isMayNotBePlacedInReserveDeck());
    }

    @Test
    public void RevealedWhenYourDestinyIsSuspendedDeploysToThroneRoomAsAFrozenCaptive() {
        var scn = scenario(hyperdriveObjective(true));
        var luke = scn.GetDSCard("luke");
        var effect = scn.GetDSCard("effect");
        scn.StartGame();

        assertTrue(scn.IsCardActive(effect));
        assertTrue("zone=" + luke.getZone(), Filters.frozenCaptive.accepts(scn.game(), luke));
        var throneRoom = scn.GetDSCard("throne");
        assertTrue(scn.IsCardActive(throneRoom));
        assertTrue(Filters.at(throneRoom).accepts(scn.game(), luke));
        assertFalse(scn.GetStackedCards(effect).contains(luke));
        assertTrue(scn.gameState().getLastMessages().toString(), scn.gameState().getLastMessages().stream()
                .anyMatch(msg -> msg.contains("reveals") && msg.contains("205_14")));
    }

    @Test
    public void NotRevealedIsPlacedUnderYourStartingEffect() {
        var scn = scenario(hyperdriveObjective(false));
        var luke = scn.GetDSCard("luke");
        var effect = scn.GetDSCard("effect");
        scn.StartGame();

        assertTrue(scn.IsCardActive(effect));
        assertEquals(Zone.STACKED_FACE_DOWN, luke.getZone());
        assertTrue(scn.GetStackedCards(effect).contains(luke));
    }

    @Test
    public void WithoutASuspendedYourDestinyItDoesNotDeployAndGoesUnderTheStartingEffect() {
        // Bring Him Before Me deploys Your Destiny, but nothing suspends it.
        var scn = scenario(StartingSetup.DefaultLSGroundLocation);
        var luke = scn.GetDSCard("luke");
        var effect = scn.GetDSCard("effect");
        scn.StartGame();

        assertFalse(scn.DSDecisionAvailable("Reveal"));
        assertEquals(Zone.STACKED_FACE_DOWN, luke.getZone());
        assertTrue(scn.GetStackedCards(effect).contains(luke));
    }

    @Test
    public void FlipsWhenVaderIsHere() {
        var scn = scenario(hyperdriveObjective(true));
        var luke = scn.GetDSCard("luke");
        var vader = scn.GetDSCard("vader");
        scn.StartGame();
        assertTrue(Filters.frozenCaptive.accepts(scn.game(), luke));
        assertFalse(luke.isFlipped());

        var throneRoom = scn.GetDSCard("throne");
        scn.MoveCardsToLocation(throneRoom, vader);
        // The next action lets GEMP notice Vader there.
        scn.DSActivateMaxForceAndPass();
        scn.PassAllResponses();

        assertTrue(luke.isFlipped());
    }
}
