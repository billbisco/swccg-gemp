package com.gempukku.swccgo.cards.set1282.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_023_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("found", "1282_23");
                    put("luke", "1_19"); // Luke Skywalker, ability 4
                    put("obiwan", "1_21"); // Obi-Wan Kenobi, ability 6
                    put("saber", "1_155"); // Jedi Lightsaber
                    put("filler", "1_105"); // a non-character card
                    put("cantina", "1_128"); // Tatooine: Cantina
                    put("db94", "1_129"); // Tatooine: Docking Bay 94
                }},
                new HashMap<>() {{
                    put("vader", "1_168"); // Darth Vader: immune to attrition
                    put("trooper", "1_194");
                    put("zuckuss", "4_107"); // may move as a 'react'
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
    public void FoundSomeoneYouHaveAndHigherGroundStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("found").getBlueprint();

        assertEquals("Found Someone You Have & Higher Ground", card.getTitle());
        assertTrue(card.getTitles().contains("Found Someone You Have"));
        assertTrue(card.getTitles().contains("Higher Ground"));
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void FoundSomeoneYouHaveAndHigherGroundInsteadOfFiringMakesTargetImmunityToAttritionMinus3() {
        var scn = GetScenario();
        var found = scn.GetLSCard("found");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation(); // opponent's site

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader);
        scn.AttachCardsTo(luke, saber);
        scn.MoveCardsToLSHand(found);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());

        float immunity = scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vader);
        assertTrue(immunity > 3);
        assertTrue(scn.LSCardPlayAvailable(found, "immunity"));
        scn.LSPlayCard(found, "immunity");
        scn.LSChooseCard(saber);
        scn.LSChooseCard(vader);
        scn.PassAllResponses();

        assertEquals(immunity - 3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vader), scn.epsilon);
        assertInZone(Zone.USED_PILE, found);
    }

    @Test
    public void FoundSomeoneYouHaveAndHigherGroundFindsAMissingCharacter() {
        var scn = GetScenario();
        var found = scn.GetLSCard("found");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MakeCardGoMissing(luke);
        scn.MoveCardsToLSHand(found);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(luke.isMissing());

        assertTrue(scn.LSCardPlayAvailable(found, "missing"));
        scn.LSPlayCard(found, "missing");
        scn.LSChooseCard(luke);
        scn.PassAllResponses();

        assertFalse(luke.isMissing());
    }

    @Test
    public void FoundSomeoneYouHaveAndHigherGroundCancelsAReact() {
        var scn = GetScenario();
        var found = scn.GetLSCard("found");
        var luke = scn.GetLSCard("luke");
        var cantina = scn.GetLSCard("cantina");
        var db94 = scn.GetLSCard("db94");
        var zuckuss = scn.GetDSCard("zuckuss");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(cantina, zuckuss);
        scn.MoveCardsToLocation(db94, luke, trooper);
        scn.MoveCardsToLSHand(found);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(db94);

        assertTrue(scn.DSCardActionAvailable(zuckuss, "react"));
        scn.DSUseCardAction(zuckuss, "react");
        if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(db94)) {
            scn.DSChooseCard(db94);
        }
        assertTrue(LegacyVb82TestSupport.passUntilLSCardAction(scn, found, "react"));
        scn.LSPlayCard(found, "react");
        scn.PassAllResponses();

        assertEquals(cantina, zuckuss.getAtLocation());
    }

    @Test
    public void FoundSomeoneYouHaveAndHigherGroundOncePerGameExchangesWithTopmostCharacterOfAbilityLessThan5() {
        var scn = GetScenario();
        var found = scn.GetLSCard("found");
        var luke = scn.GetLSCard("luke");
        var obiwan = scn.GetLSCard("obiwan");
        var filler = scn.GetLSCard("filler");
        var saber = scn.GetLSCard("saber");

        scn.StartGame();
        scn.MoveCardsToLSHand(found, saber);
        // Lost Pile, top first: filler (not a character), Obi-Wan (ability 6), Luke (ability 4)
        scn.MoveCardsToTopOfLSLostPile(luke);
        scn.MoveCardsToTopOfLSLostPile(obiwan);
        scn.MoveCardsToTopOfLSLostPile(filler);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(1);

        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(found, "Exchange"));
        scn.LSPlayCard(found, "Exchange");
        scn.PassAllResponses();
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(saber)) {
            scn.LSChooseCard(saber);
        }
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(luke)) {
            scn.LSChooseCard(luke);
        }
        scn.PassAllResponses();

        assertInHand(luke);
        assertFalse(obiwan.getZone() == Zone.HAND);
        assertEquals(Zone.LOST_PILE, saber.getZone() == Zone.TOP_OF_LOST_PILE ? Zone.LOST_PILE : saber.getZone());
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }
}
