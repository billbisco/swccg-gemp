package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_040_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("rebel1", "1_28");
                    put("rebel2", "1_28");
                    put("rebel3", "1_28");
                }},
                new HashMap<>() {{
                    put("trace", "1240_40");
                    put("galen", "1117_13");
                    put("saber", "1117_15");
                    put("rogue", "601_95");
                    put("trooper", "1_194");
                }},
                10,
                10,
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
    public void IfTheTraceWasCorrectVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("trace").getBlueprint();

        assertEquals("If The Trace Was Correct", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardType.EPIC_EVENT, card.getCardType());
        assertTrue(card.hasIcon(Icon.TATOOINE));
        assertTrue(card.hasIcon(Icon.LEGACY_BLOCK_4));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void IfTheTraceWasCorrectVRogueShadowIsImmuneToAttritionLessThanFour() {
        var scn = GetScenario();
        var trace = scn.GetDSCard("trace");
        var rogue = scn.GetDSCard("rogue");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, rogue);
        float before = scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), rogue);
        assertTrue(before < 4);

        scn.MoveCardsToSideOfTable(trace);
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), rogue), scn.epsilon);
    }

    @Test
    public void IfTheTraceWasCorrectVRogueShadowMayDeployToSites() {
        var scn = GetScenario();
        var trace = scn.GetDSCard("trace");
        var rogue = scn.GetDSCard("rogue");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToHand(rogue);
        scn.MoveCardsToSideOfTable(trace);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureDSForcePile(10);

        assertTrue(scn.DSDeployAvailable(rogue));
        scn.DSDeployCard(rogue);
        assertTrue(scn.DSHasCardChoiceAvailable(site));
    }

    @Test
    public void IfTheTraceWasCorrectVThrowGalensLightsaberHitsUpToHalfDestinyOfOpponentsLowAbilityCharacters() {
        var scn = GetScenario();
        var trace = scn.GetDSCard("trace");
        var galen = scn.GetDSCard("galen");
        var saber = scn.GetDSCard("saber");
        var luke = scn.GetLSCard("luke");
        var rebel1 = scn.GetLSCard("rebel1");
        var rebel2 = scn.GetLSCard("rebel2");
        var rebel3 = scn.GetLSCard("rebel3");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToSideOfTable(trace);
        scn.MoveCardsToLocation(site, galen, luke, rebel1, rebel2, rebel3);
        scn.AttachCardsTo(galen, saber);

        scn.SkipToPhase(Phase.BATTLE);
        scn.EnsureDSForcePile(2);
        scn.DSInitiateBattle(site);
        for (int i = 0; i < 6 && !scn.AwaitingDSWeaponsSegmentActions(); i++) {
            if (scn.LSAnyDecisionsAvailable()) scn.LSPass(); else scn.DSPass();
        }
        assertTrue(scn.DSCardActionAvailable(trace, "'Throw' Galen's Lightsaber"));
        scn.PrepareDSDestiny(3);
        scn.DSUseCardAction(trace, "'Throw' Galen's Lightsaber");
        for (int i = 0; i < 20 && !scn.DSHasCardChoiceAvailable(rebel1); i++) {
            if (scn.LSAnyDecisionsAvailable()) scn.LSPass(); else scn.DSPass();
        }
        assertEquals(Zone.USED_PILE, saber.getZone());
        assertTrue(scn.DSHasCardChoiceAvailable(rebel1));
        assertFalse(scn.DSHasCardChoiceAvailable(luke)); // ability 4
        assertEquals(2, scn.DSGetSelectableCount() >= 2 ? 2 : scn.DSGetSelectableCount());
        scn.DSChooseCards(rebel1, rebel2);
        scn.PassAllResponses();

        assertTrue(rebel1.isHit() || rebel1.getZone() != Zone.AT_LOCATION);
        assertTrue(rebel2.isHit() || rebel2.getZone() != Zone.AT_LOCATION);
        assertFalse(rebel3.isHit());
    }

    @Test
    public void IfTheTraceWasCorrectVUseThreeForceToRelocateRogueShadowToBattlegroundSite() {
        var scn = GetScenario();
        var trace = scn.GetDSCard("trace");
        var rogue = scn.GetDSCard("rogue");
        var luke = scn.GetLSCard("luke");
        var dsSite = scn.GetDSStartingLocation();
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToSideOfTable(trace);
        scn.MoveCardsToLocation(dsSite, rogue);
        scn.MoveCardsToLocation(lsSite, luke);
        scn.MoveCardsToTopOfOwnUsedPile(scn.GetDSFiller(1));

        scn.SkipToPhase(Phase.MOVE);
        scn.EnsureDSForcePile(6);
        int forceBefore = scn.GetDSForcePileCount();
        int handBefore = scn.GetDSHandCount();
        assertTrue(scn.DSCardActionAvailable(trace, "Relocate Rogue Shadow"));
        scn.DSUseCardAction(trace, "Relocate Rogue Shadow");
        scn.DSChooseCard(lsSite);
        for (int i = 0; i < 10; i++) {
            if (scn.DSDecisionAvailable("Optional") && scn.DSCardActionAvailable(trace, "Take top card of Used Pile into hand")) {
                scn.DSUseCardAction(trace, "Take top card of Used Pile into hand");
                continue;
            }
            if (scn.LSAnyDecisionsAvailable()) scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable() && !scn.AwaitingDSMovePhaseActions()) scn.DSPass();
            else break;
        }
        assertEquals(lsSite.getCardId(), scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), rogue).getCardId());
        assertEquals(forceBefore - 3, scn.GetDSForcePileCount());
        assertEquals(handBefore + 1, scn.GetDSHandCount());
    }
}
