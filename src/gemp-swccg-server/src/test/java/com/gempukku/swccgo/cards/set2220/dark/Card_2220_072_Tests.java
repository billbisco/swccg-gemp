package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/** Imperial Occupation / Imperial Control: original (pre-errata) version of 1220_72 (both sides). */
public class Card_2220_072_Tests {
    private static final String UPLOAD = "Take a Hoth card into hand";
    private static final StartingSetup START = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>(Map.of("objective", "2220_72"));
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            for (int step = 0; step < 12; step++) {
                if (scn.DSDecisionAvailable("On which side")) {
                    scn.DSChoose("Left");
                    continue;
                }
                if (scn.DSDecisionAvailable("Choose Block 6 Ice Plains")) {
                    var ice = scn.GetDSReserveDeck().stream().filter(c -> c.getTitle().equals(Title.Ice_Plains)
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
                new HashMap<>(Map.ofEntries(Map.entry("hoth", "3_143"),
                        Map.entry("generators", "1282_8"), Map.entry("decree", "1220_38"), Map.entry("blockade", "1220_31"),
                        Map.entry("cannon", "3_158"), Map.entry("landing", "11_77"), Map.entry("garrison", "104_7"),
                        Map.entry("walker", "3_157"), Map.entry("blownclear", "7_222"))),
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
    public void VersionHasTheOldTextsAndVersionInfo() {
        var scn = scenario();
        var card = scn.GetDSCard("objective").getBlueprint();
        var current = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1220_72");
        assertEquals(current.getTitle(), card.getTitle());
        assertEquals(current.hasVirtualSuffix(), card.hasVirtualSuffix());
        assertEquals(current.getCardTypes(), card.getCardTypes());
        for (Icon icon : Icon.values())
            assertEquals(icon.name(), current.getIconCount(icon), card.getIconCount(icon));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("Deploy Hoth system, [Virtual Block 6] Ice Plains, and Main Power Generators (Light Side). May deploy Imperial Decree. For remainder of game, Walker Garrison is an Out Of Play Interrupt and [Immune to Sense]. AT-ATs are deploy -1 to marker sites. While this side up, Revolution is canceled. Opponent loses no more than 1 Force to your [Special Edition] Effects. During your control phase, may take one AT-AT Cannon, Hoth Blockade, Electro-Rangefinder, or a [Hoth] Epic Event into hand from Reserve Deck; reshuffle. Flip this card if Main Power Generators 'blown away' and Hoth Blockade on Hoth.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_72", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2012, 9, 21), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));

        var back = new Card2220_072_BACK();
        assertEquals("Imperial Control", back.getTitle());
        assertEquals(7, back.getDestiny(), scn.epsilon);
        assertEquals("While this side up, Revolution is canceled. At marker sites, AT-ATs move for free. Rebels are deploy +1 to Hoth sites. Your total battle destiny at Hoth system is +1 for each Imperial there. Unless opponent controls a Hoth location, your Force drain bonuses may not be canceled. Once per turn, when you deploy an AT-AT, may retrieve any one card (except an Interrupt or vehicle) into hand. During your deploy phase, may deploy a marker site from Reserve Deck; reshuffle. Flip this card if Hoth Blockade not on Hoth.",
                back.getGameText());
        assertEquals("1220_72", back.getLegacyVersion().currentPrintingId());
    }

    @Test(timeout = 10000)
    public void PregameDeploysMainPowerGeneratorsAndWalkerGarrisonIsAnOutOfPlayInterruptImmuneToSense() {
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
    public void TakesAHothCardIntoHandOnlyDuringYourControlPhase() {
        var scn = scenario();
        setup(scn);
        var objective = scn.GetDSCard("objective");
        var cannon = scn.GetDSCard("cannon");
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(cannon);
        assertFalse(scn.DSCardActionAvailable(objective, UPLOAD));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(cannon);
        assertTrue(scn.DSCardActionAvailable(objective, UPLOAD));
        scn.DSUseCardAction(objective, UPLOAD);
        if (scn.DSHasCardChoiceAvailable(cannon))
            scn.DSChooseCard(cannon);
        scn.PassAllResponses();
        assertTrue(scn.GetDSHand().contains(cannon));
    }

    @Test(timeout = 10000)
    public void YouMayStartYourLandingIsNotCanceled() {
        var scn = scenario();
        setup(scn);
        var landing = scn.GetDSCard("landing");
        scn.MoveCardsToDSSideOfTable(landing);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PassAllResponses();
        assertTrue(landing.getZone().isInPlay());
    }

    @Test(timeout = 10000)
    public void OpponentLosesNoMoreThanOneForceToYourSpecialEditionEffects() {
        var scn = scenario();
        setup(scn);
        var blownClear = scn.GetDSCard("blownclear");
        scn.MoveCardsToDSSideOfTable(blownClear);
        var mods = scn.game().getModifiersQuerying();
        assertEquals(1, mods.getForceToLoseFromCardLimit(scn.gameState(), scn.LS, blownClear), scn.epsilon);
        assertTrue(mods.getForceToLoseFromCardLimit(scn.gameState(), scn.DS, blownClear) > 1);
    }

    @Test(timeout = 10000)
    public void BackSideAtatsAtMarkerSitesMoveForFree() {
        var scn = scenario();
        var ice = setup(scn);
        var obj = scn.GetDSCard("objective");
        var walker = scn.GetDSCard("walker");
        scn.game().getModifiersQuerying().blownAway(scn.GetDSCard("generators"));
        scn.gameState().flipBlownAwayLocations(scn.game(), List.of(scn.GetDSCard("generators")));
        scn.AttachCardsTo(scn.GetDSCard("hoth"), scn.GetDSCard("blockade"));
        scn.MoveCardsToLocation(ice, walker);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(obj.isFlipped());
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MOVES_FREE, walker).isEmpty());
    }
}
