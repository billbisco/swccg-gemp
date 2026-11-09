package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Imperial Occupation / Imperial Control: PC-E1 version of 1220_72 (Competitive Balance Team errata, PC forum
 * "Current Rulings/Errata", 2012-09-21; replaced 2012-11-19). Batch B36.
 */
public class Card_2220_1072_Tests {
    private static final String UPLOAD = "Take a Hoth card into hand";
    private static final StartingSetup START = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>(Map.of("objective", "2220_1072"));
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            for (int step = 0; step < 12; step++) {
                if (scn.DSDecisionAvailable("On which side")) {
                    scn.DSChoose("Left");
                    continue;
                }
                if (scn.DSDecisionAvailable("Choose Block 6 Ice Plains")) {
                    var ice = scn.GetDSReserveDeck().stream().filter(c -> Filters.Ice_Plains.accepts(scn.game(), c)
                            && c.getBlueprint().getIconCount(Icon.LEGACY_BLOCK_6) > 0).findFirst().orElseThrow();
                    scn.DSChooseCard((PhysicalCardImpl) ice);
                    continue;
                }
                var next = List.of("objective", "hoth", "generators", "decree").stream()
                        .map(scn::GetDSCard).filter(scn::DSHasCardChoiceAvailable).findFirst();
                if (next.isPresent())
                    scn.DSChooseCard(next.get());
                else
                    break;
            }
        }
    };

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("rebel", "1_19")),
                new HashMap<>(Map.ofEntries(Map.entry("hoth", "3_143"), Map.entry("generators", "1282_8"),
                        Map.entry("decree", "1220_38"), Map.entry("blockade", "1220_31"), Map.entry("cannon", "3_158"),
                        Map.entry("landing", "11_77"), Map.entry("landing8", "1282_3"), Map.entry("garrison", "104_7"),
                        Map.entry("walker", "3_157"))),
                30, 30, StartingSetup.DefaultLSGroundLocation, START,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private PhysicalCardImpl setup(VirtualTableScenario scn) {
        // Same Block 6 Ice Plains fixture as Card_1220_072_Tests.
        var ice = MissionTestSupport.putInHand(scn, 9072, scn.DS,
                new AbstractSite(Side.DARK, Title.Ice_Plains, Title.Hoth, Uniqueness.UNIQUE,
                        ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V) {
                    {
                        addIcon(Icon.DARK_FORCE, 2);
                        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.HOTH);
                        addKeywords(Keyword.MARKER_5);
                    }
                });
        scn.MoveCardsToTopOfDSReserveDeck(ice);
        scn.StartGame();
        assertTrue("The Objective starting sequence must finish", scn.GetDSCard("objective").getZone().isInPlay());
        return ice;
    }

    @Test
    public void VersionHasTheErrataTextsAndVersionInfo() {
        var scn = scenario();
        var card = scn.GetDSCard("objective").getBlueprint();
        var current = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1220_72");
        assertEquals(current.getTitle(), card.getTitle());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        for (Icon icon : Icon.values())
            assertEquals(icon.name(), current.getIconCount(icon), card.getIconCount(icon));
        assertEquals("Deploy Hoth system, [Virtual Block 6] Ice Plains, and [Virtual Block 8] Main Power Generators. May deploy Imperial Decree. For remainder of game, Walker Garrison is an Out Of Play Interrupt and [Immune to Sense]. AT-ATs are deploy -1 to marker sites. Non-[Virtual Block 8] You May Start Your Landing is canceled. While this side up, Revolution is canceled. Once during your turn, may take one AT-AT Cannon, Hoth Blockade, Electro-Rangefinder, or a [Hoth] Epic Event into hand from Reserve Deck; reshuffle. Flip this card if Main Power Generators 'blown away' and Hoth Blockade on Hoth.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_72", version.currentPrintingId());
        assertEquals("PC-E1", version.label());
        assertEquals(LocalDate.of(2012, 9, 21), version.printedOn());
        assertEquals(LocalDate.of(2012, 11, 19), version.supersededOn());
        assertTrue(GameUtils.getFullName(card).endsWith(" [PC-E1]"));

        var back = new Card2220_1072_BACK();
        assertEquals("Imperial Control", back.getTitle());
        assertEquals(7, back.getDestiny(), scn.epsilon);
        assertEquals(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1220_72_BACK").getGameText(), back.getGameText());
        assertEquals("PC-E1", back.getLegacyVersion().label());
    }

    @Test(timeout = 10000)
    public void PregameDeploysBlockEightGeneratorsAndWalkerGarrisonIsAnOutOfPlayInterruptImmuneToSense() {
        var scn = scenario();
        var ice = setup(scn);
        var garrison = scn.GetDSCard("garrison");
        assertEquals(Zone.LOCATIONS, scn.GetDSCard("hoth").getZone());
        assertEquals(Zone.LOCATIONS, ice.getZone());
        assertEquals(Zone.LOCATIONS, scn.GetDSCard("generators").getZone());
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), garrison, Title.Sense));
        assertEquals(CardSubtype.OUT_OF_PLAY, scn.game().getModifiersQuerying().getModifiedSubtype(scn.gameState(), garrison));
        assertTrue(scn.game().getModifiersQuerying().isPlacedOutOfPlayWhenPlayedAsSubtype(scn.gameState(), garrison, CardSubtype.LOST));
    }

    @Test(timeout = 10000)
    public void NonBlockEightLandingIsCanceled() {
        var scn = scenario();
        setup(scn);
        var oldLanding = scn.GetDSCard("landing");
        var finalLanding = scn.GetDSCard("landing8");
        scn.MoveCardsToDSSideOfTable(oldLanding, finalLanding);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(oldLanding));
        assertTrue(finalLanding.getZone().isInPlay());
    }

    @Test(timeout = 10000)
    public void TakesAHothCardIntoHandOnceDuringYourTurnOutsideTheControlPhase() {
        var scn = scenario();
        setup(scn);
        var objective = scn.GetDSCard("objective");
        var cannon = scn.GetDSCard("cannon");
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(cannon);
        assertTrue(scn.DSCardActionAvailable(objective, UPLOAD));
        scn.DSUseCardAction(objective, UPLOAD);
        if (scn.DSHasCardChoiceAvailable(cannon))
            scn.DSChooseCard(cannon);
        scn.PassAllResponses();
        assertTrue(scn.GetDSHand().contains(cannon));
        if (scn.AwaitingLSDeployPhaseActions())
            scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(objective, UPLOAD));
    }
}
