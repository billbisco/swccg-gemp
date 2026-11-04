package com.gempukku.swccgo.cards.set1283.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("console", "1283_9");
                    put("chamber", "5_166"); // Cloud City: Carbonite Chamber
                    put("boba", "1210_2"); // Boba Fett (Virtual Block 1): not a [VB6][JP] Boba Fett
                    put("audience", "6_162"); // Jabba's Palace: Audience Chamber
                    put("prize", "10_42"); // Jabba's Prize
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
    public void CarboniteChamberConsoleVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("console").getBlueprint();
        assertEquals("Carbonite Chamber Console", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_SITE);
            add(Keyword.DEPLOYS_ON_LOCATION);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CarboniteChamberConsoleVDeploysOnlyOnCarboniteChamber() {
        var scn = GetScenario();
        var console = scn.GetDSCard("console");
        var chamber = scn.GetDSCard("chamber");
        var vader = scn.GetDSCard("vader");
        scn.StartGame();
        scn.MoveCardsToHand(console);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSDeployAvailable(console));
    }

    @Test
    public void CarboniteChamberConsoleVDeploysOnCarboniteChamber() {
        var scn = GetScenario();
        var console = scn.GetDSCard("console");
        var chamber = scn.GetDSCard("chamber");
        var vader = scn.GetDSCard("vader");
        scn.StartGame();
        scn.MoveCardsToHand(console);
        scn.MoveLocationToTable(chamber);
        scn.MoveCardsToLocation(chamber, vader);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(console));
        scn.DSDeployCard(console);
        scn.DSChooseCard(chamber);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(chamber, console));
    }

    private void consoleOnChamber(VirtualTableScenario scn) {
        var console = scn.GetDSCard("console");
        var chamber = scn.GetDSCard("chamber");
        scn.StartGame();
        scn.MoveLocationToTable(chamber);
        scn.AttachCardsTo(chamber, console);
    }

    @Test
    public void CarboniteChamberConsoleVBobaFettSearchUses3ForceOncePerGame() {
        // No [Virtual Block 6][Jabba's Palace] Boba Fett is in GEMP yet; the search still costs 3 Force and is once per game
        var scn = GetScenario();
        var console = scn.GetDSCard("console");
        consoleOnChamber(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int forcePile = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(console, "Take Boba Fett into hand from Reserve Deck"));
        scn.DSUseCardAction(console, "Take Boba Fett into hand from Reserve Deck");
        scn.PassForceUseResponses();
        scn.PassAllResponses();
        assertEquals(forcePile - 3, scn.GetDSForcePileCount());
        assertFalse(scn.GetDSAvailableActions().contains("Take Boba Fett into hand from Reserve Deck"));
    }

    @Test
    public void CarboniteChamberConsoleVDoesNotTakeOtherBobaFett() {
        var scn = GetScenario();
        var console = scn.GetDSCard("console");
        var boba = scn.GetDSCard("boba");
        consoleOnChamber(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(boba);
        scn.DSUseCardAction(console, "Take Boba Fett into hand from Reserve Deck");
        scn.PassForceUseResponses();
        assertEquals(0, scn.DSGetSelectableCount());
        scn.PassAllResponses();
        assertTrue(scn.GetDSReserveDeck().contains(boba));
    }

    @Test
    public void CarboniteChamberConsoleVMayNotTakeAudienceChamberWithoutJabbasPrizeOnTatooine() {
        var scn = GetScenario();
        var console = scn.GetDSCard("console");
        consoleOnChamber(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(console, "Take Audience Chamber into hand from Reserve Deck"));
    }

    @Test
    public void CarboniteChamberConsoleVTakesAudienceChamberOncePerGameWhileJabbasPrizeOnTatooine() {
        var scn = GetScenario();
        var console = scn.GetDSCard("console");
        var audience = scn.GetDSCard("audience");
        var prize = scn.GetDSCard("prize");
        consoleOnChamber(scn);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), prize); // Tatooine: Marketplace
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(audience);
        assertTrue(scn.DSCardActionAvailable(console, "Take Audience Chamber into hand from Reserve Deck"));
        scn.DSUseCardAction(console, "Take Audience Chamber into hand from Reserve Deck");
        scn.DSChooseCard(audience);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, audience);
        assertFalse(scn.GetDSAvailableActions().contains("Take Audience Chamber into hand from Reserve Deck"));
    }

    private int dsTotalBattleDestiny(boolean jabbasPrizeOnTatooine, boolean console) {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var prize = scn.GetDSCard("prize");
        var chamber = scn.GetDSCard("chamber");
        scn.StartGame();
        scn.MoveLocationToTable(chamber);
        if (console) {
            scn.AttachCardsTo(chamber, scn.GetDSCard("console"));
        }
        if (jabbasPrizeOnTatooine) {
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), prize); // Tatooine: Marketplace
        }
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), vader, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        scn.PrepareDSDestiny(3);
        scn.SkipToEndOfPowerSegment(true);
        return scn.GetDSTotalDestiny();
    }

    @Test
    public void CarboniteChamberConsoleVAddsOneToYourDestinyDrawsInBattleWhileJabbasPrizeOnTatooine() {
        assertEquals(dsTotalBattleDestiny(true, false) + 1, dsTotalBattleDestiny(true, true));
    }

    @Test
    public void CarboniteChamberConsoleVDoesNotAddToDestinyWithoutJabbasPrizeOnTatooine() {
        assertEquals(dsTotalBattleDestiny(false, false), dsTotalBattleDestiny(false, true));
    }

    @Test
    public void CarboniteChamberConsoleVIsImmuneToSabotageAndUutkik() {
        var scn = GetScenario();
        var console = scn.GetDSCard("console");
        consoleOnChamber(scn);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.game().getGameState(), console, Title.Sabotage));
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.game().getGameState(), console, "Uutkik"));
    }
}
