package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_026_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1273_26");
                    put("blaster", "1_152");
                    put("blaster2", "1_152");
                    put("red1", "1_144");
                    put("tibrin", "6_087");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("trooper2", "1_194");
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
    public void LukeSkywalkerRebelHeroStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("luke").getBlueprint();

        assertEquals("Luke Skywalker, Rebel Hero", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ROGUE_SQUADRON);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.LUKE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LukeSkywalkerRebelHeroAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var red1 = scn.GetLSCard("red1");
        var tibrin = scn.GetLSCard("tibrin");

        scn.StartGame();
        scn.MoveLocationToTable(tibrin);
        scn.MoveCardsToLocation(tibrin, red1);
        int before = (int) (float) red1.getBlueprint().getPower();
        scn.BoardAsPilot(red1, luke);
        assertEquals(before + 3, scn.GetPower(red1));
    }

    @Test
    public void LukeSkywalkerRebelHeroIsImmuneToAttritionLessThan4() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), luke), scn.epsilon);
    }

    @Test
    public void LukeSkywalkerRebelHeroWhenDeployedMayDeployBlasterFromReserveDeck() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(luke);
        scn.LSActivateForceCheat(8);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(blaster);
        scn.LSDeployCard(luke);
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        Vb81RestTestSupport.passUntil(scn, () -> scn.LSActionAvailable("Deploy a blaster on Luke"));
        assertTrue(scn.LSActionAvailable("Deploy a blaster on Luke"));
        scn.LSChooseAction("Deploy a blaster on Luke");
        for (int i = 0; i < 10 && !scn.IsAttachedTo(luke, blaster); ++i) {
            if (scn.LSGetMultipleChoices() != null && scn.LSGetMultipleChoices().contains("Reserve Deck")) {
                scn.LSChooseOption("Reserve Deck");
            }
            else if (scn.LSHasCardChoiceAvailable(blaster)) {
                scn.LSChooseCard(blaster);
            }
            else if (scn.LSHasCardChoiceAvailable(luke)) {
                scn.LSChooseCard(luke);
            }
            else {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
        }
        assertTrue("decision=" + (scn.GetCurrentDecision() == null ? null : scn.GetCurrentDecision().getText()), scn.IsAttachedTo(luke, blaster));
    }

    @Test
    public void LukeSkywalkerRebelHeroWhenDeployedMayLoseOneForceToDeployBlasterFromLostPile() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var blaster2 = scn.GetLSCard("blaster2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(luke);
        scn.MoveCardsToTopOfLSLostPile(blaster);
        scn.MoveCardsToTopOfLSLostPile(blaster2);
        scn.LSActivateForceCheat(8);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(luke);
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        Vb81RestTestSupport.passUntil(scn, () -> scn.LSActionAvailable("Deploy a blaster on Luke"));
        scn.LSChooseAction("Deploy a blaster on Luke");
        for (int i = 0; i < 12 && !(scn.IsAttachedTo(luke, blaster) || scn.IsAttachedTo(luke, blaster2)); ++i) {
            if (scn.LSGetMultipleChoices() != null && scn.LSGetMultipleChoices().stream().anyMatch(s -> s.startsWith("Lost Pile"))) {
                scn.LSChooseOption(scn.LSGetMultipleChoices().stream().filter(s -> s.startsWith("Lost Pile")).findFirst().get());
            }
            else if (Vb81RestTestSupport.holdsDecision(scn, scn.LS, "lose") && scn.AwaitingLSForceLossPayment()) {
                scn.LSPayForceLossFromReserveDeck();
            }
            else if (scn.LSHasCardChoiceAvailable(blaster)) {
                scn.LSChooseCard(blaster);
            }
            else if (scn.LSHasCardChoiceAvailable(blaster2)) {
                scn.LSChooseCard(blaster2);
            }
            else if (scn.LSHasCardChoiceAvailable(luke)) {
                scn.LSChooseCard(luke);
            }
            else {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
        }
        assertTrue(scn.IsAttachedTo(luke, blaster) || scn.IsAttachedTo(luke, blaster2));
        // One blaster left the Lost Pile and one card was lost to pay the 1 Force
        assertEquals(2, scn.GetLSLostPileCount());
    }

    @Test
    public void LukeSkywalkerRebelHeroBlasterHeIsArmedWithMayNotBeStolen() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.AttachCardsTo(luke, blaster);
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_STOLEN, blaster).isEmpty());
        assertTrue(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_STOLEN, luke).isEmpty());
    }

    @Test
    public void LukeSkywalkerRebelHeroCharactersHitByLukeMayNotSatisfyAttrition() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper, trooper2);
        scn.AttachCardsTo(luke, blaster);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(6);

        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(trooper);
        Vb81RestTestSupport.passUntil(scn, () -> trooper.isHit() && scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(trooper.isHit());
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_SATISFY_ATTRITION, trooper).isEmpty());
        assertTrue(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_SATISFY_ATTRITION, trooper2).isEmpty());
    }
}
