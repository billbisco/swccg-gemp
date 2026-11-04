package com.gempukku.swccgo.cards.set1292.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1292_011_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("mishap", "1292_11");
                    put("obiwan", "1_21");
                    put("trooper", "1_28");
                }},
                new HashMap<>() {{
                    put("huttbountyv", "1230_34");
                    put("huttbounty", "6_144");
                    put("scum", "6_149");
                    put("jabba", "6_109");
                    put("stormtrooper", "1_194");
                    put("handcard", "1_194");
                    put("handcard2", "1_165");
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
    public void MandalorianMishapAndJediMindTrickStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("mishap").getBlueprint();

        assertEquals("Mandalorian Mishap & Jedi Mind Trick", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        assertTrue(card.getTitles().contains("Mandalorian Mishap"));
        assertTrue(card.getTitles().contains("Jedi Mind Trick"));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MandalorianMishapUsesOneForceToSuspendVirtualBlock3HuttBounty() {
        var scn = GetScenario();
        var mishap = scn.GetLSCard("mishap");
        var huttbountyv = scn.GetDSCard("huttbountyv");
        var jabba = scn.GetDSCard("jabba");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, jabba);
        scn.AttachCardsTo(jabba, huttbountyv);
        scn.MoveCardsToLSHand(mishap);
        scn.SkipToLSTurn(Phase.CONTROL);
        int force = scn.GetLSForcePileCount();

        assertTrue(scn.LSCardPlayAvailable(mishap, "Suspend"));
        scn.LSPlayCard(mishap, "Suspend");
        if (scn.LSDecisionAvailable("Choose Hutt Bounty")) {
            scn.LSChooseCard(huttbountyv);
        }
        scn.PassAllResponses();

        assertTrue(huttbountyv.isSuspended());
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertInZone(Zone.USED_PILE, mishap);
    }

    @Test
    public void MandalorianMishapSuspendsScumAndVillainy() {
        var scn = GetScenario();
        var mishap = scn.GetLSCard("mishap");
        var scum = scn.GetDSCard("scum");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.AttachCardsTo(site, scum);
        scn.MoveCardsToLSHand(mishap);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardPlayAvailable(mishap, "Suspend"));
        scn.LSPlayCard(mishap, "Suspend");
        if (scn.LSDecisionAvailable("Choose Hutt Bounty")) {
            scn.LSChooseCard(scum);
        }
        scn.PassAllResponses();
        assertTrue(scum.isSuspended());
    }

    @Test
    public void MandalorianMishapMayNotSuspendHuttBountyWithoutVirtualBlock3Icon() {
        var scn = GetScenario();
        var mishap = scn.GetLSCard("mishap");
        var huttbounty = scn.GetDSCard("huttbounty");
        var jabba = scn.GetDSCard("jabba");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, jabba);
        scn.AttachCardsTo(jabba, huttbounty);
        scn.MoveCardsToLSHand(mishap);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertFalse(scn.LSCardPlayAvailable(mishap, "Suspend"));
    }

    private int lsIcons(VirtualTableScenario scn) {
        return scn.game().getModifiersQuerying().getIconCount(scn.gameState(), scn.GetDSStartingLocation(), Icon.LIGHT_FORCE);
    }

    @Test
    public void MandalorianMishapAddsLightSideIconToPlanetSiteYouOccupyForRemainderOfTurn() {
        var scn = GetScenario();
        var mishap = scn.GetLSCard("mishap");
        var trooper = scn.GetLSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper);
        scn.MoveCardsToLSHand(mishap);
        scn.SkipToLSTurn(Phase.CONTROL);
        int icons = lsIcons(scn);

        assertTrue(scn.LSCardPlayAvailable(mishap, "icon"));
        scn.LSPlayCard(mishap, "icon");
        if (scn.LSDecisionAvailable("planet site")) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();
        assertEquals(icons + 1, lsIcons(scn));
        assertInZone(Zone.USED_PILE, mishap);

        scn.SkipToDSTurn(Phase.CONTROL);
        assertEquals(icons, lsIcons(scn));
    }

    @Test
    public void MandalorianMishapMayAddIconAtBeginningOfTurn() {
        var scn = GetScenario();
        var mishap = scn.GetLSCard("mishap");
        var trooper = scn.GetLSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper);
        scn.MoveCardsToLSHand(mishap);
        scn.SkipToLSTurn();
        // start of turn, before Light Side activates Force
        assertEquals(Phase.BETWEEN_TURNS, scn.GetCurrentPhase());
        assertEquals(scn.LS, scn.GetCurrentPlayer());
        int icons = lsIcons(scn);
        assertTrue(scn.LSCardPlayAvailable(mishap, "icon"));
        scn.LSPlayCard(mishap, "icon");
        if (scn.LSDecisionAvailable("planet site")) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();
        assertEquals(icons + 1, lsIcons(scn));
    }

    @Test
    public void MandalorianMishapMayNotAddIconToSiteYouDoNotOccupy() {
        var scn = GetScenario();
        var mishap = scn.GetLSCard("mishap");

        scn.StartGame();
        scn.MoveCardsToLSHand(mishap);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(mishap, "icon"));
    }

    private int playJediMindTrickAndSelect(VirtualTableScenario scn, String dsChoiceText) {
        var mishap = scn.GetLSCard("mishap");
        var obiwan = scn.GetLSCard("obiwan");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var handcard = scn.GetDSCard("handcard");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, obiwan, stormtrooper);
        scn.MoveCardsToDSHand(handcard, scn.GetDSCard("handcard2"));
        scn.MoveCardsToLSHand(mishap);
        scn.EnsureDSForcePile(4);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardPlayAvailable(mishap, "Reveal"));
        scn.LSPlayCard(mishap, "Reveal");
        for (int i = 0; i < 10 && !scn.LSDecisionAvailable("Select a card"); i++) {
            if (scn.LSDecisionAvailable("Opponent's hand")) {
                scn.LSDecided("");
            }
            else {
                scn.PassResponses();
            }
        }
        assertTrue(scn.LSDecisionAvailable("Select a card"));
        scn.ChooseCardBPFromSelection(scn.LS, handcard);
        assertTrue(scn.DSDecisionAvailable("Choose effect"));
        assertEquals(3, scn.DSGetMultipleChoices().size());
        int dsForce = scn.GetDSForcePileCount();
        scn.DSChoose(dsChoiceText);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, mishap);
        return dsForce - scn.GetDSForcePileCount();
    }

    @Test
    public void JediMindTrickSelectedCardIsLostIfOpponentDoesNotUseOrLoseTwoForce() {
        var scn = GetScenario();
        var handcard = scn.GetDSCard("handcard");
        assertEquals(0, playJediMindTrickAndSelect(scn, "to be lost"));
        assertInZone(Zone.LOST_PILE, handcard);
    }

    @Test
    public void JediMindTrickSelectedCardStaysIfOpponentUsesTwoForce() {
        var scn = GetScenario();
        var handcard = scn.GetDSCard("handcard");
        assertEquals(2, playJediMindTrickAndSelect(scn, "Use 2 Force"));
        assertInZone(Zone.HAND, handcard);
    }

    @Test
    public void JediMindTrickNotPlayableAgainstJabba() {
        var scn = GetScenario();
        var mishap = scn.GetLSCard("mishap");
        var obiwan = scn.GetLSCard("obiwan");
        var jabba = scn.GetDSCard("jabba");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, obiwan, jabba);
        scn.MoveCardsToDSHand(scn.GetDSCard("handcard"));
        scn.MoveCardsToLSHand(mishap);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertFalse(scn.LSCardPlayAvailable(mishap, "Reveal"));
    }

    @Test
    public void JediMindTrickNeedsCharacterOfAbilityMoreThanFour() {
        var scn = GetScenario();
        var mishap = scn.GetLSCard("mishap");
        var trooper = scn.GetLSCard("trooper");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, stormtrooper);
        scn.MoveCardsToDSHand(scn.GetDSCard("handcard"));
        scn.MoveCardsToLSHand(mishap);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertFalse(scn.LSCardPlayAvailable(mishap, "Reveal"));
    }
}
