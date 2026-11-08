package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.Vb89VersionAsserts.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_012_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("anakin", "1282_12");
                    put("jedisaber", "1_155"); // Jedi Lightsaber
                }},
                new HashMap<>() {{
                    put("trooper", "1_194"); // Stormtrooper
                    put("vadersaber", "1_324"); // Vader's Lightsaber
                    put("darksaber", "1_314"); // Dark Jedi Lightsaber
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
    public void AnakinSoloStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("anakin").getBlueprint();

        assertEquals("Anakin Solo", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals("Padawan.", card.getLore());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.NEW_REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.PADAWAN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.NEW_REPUBLIC);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AnakinSoloIsPowerPlus2WhileArmedWithALightsaber() {
        var scn = GetScenario();
        var anakin = scn.GetLSCard("anakin");
        var jedisaber = scn.GetLSCard("jedisaber");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, anakin);
        assertEquals(2, scn.GetPower(anakin));

        scn.AttachCardsTo(anakin, jedisaber);
        assertEquals(4, scn.GetPower(anakin));
    }

    @Test
    public void AnakinSoloImmuneToAttritionLessThan5OnlyWhileArmedWithAStolenLightsaber() {
        var scn = GetScenario();
        var anakin = scn.GetLSCard("anakin");
        var jedisaber = scn.GetLSCard("jedisaber");
        var darksaber = scn.GetDSCard("darksaber");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, anakin);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), anakin), scn.epsilon);

        // His own lightsaber is not 'stolen'
        scn.AttachCardsTo(anakin, jedisaber);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), anakin), scn.epsilon);

        // An opponent's lightsaber he carries is 'stolen'
        darksaber.setOwner(scn.LS);
        scn.AttachCardsTo(anakin, darksaber);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), anakin), scn.epsilon);
    }

    @Test
    public void AnakinSoloStealsAnOpponentsLightsaberJustLostWherePresent() {
        var scn = GetScenario();
        var anakin = scn.GetLSCard("anakin");
        var jedisaber = scn.GetLSCard("jedisaber");
        var trooper = scn.GetDSCard("trooper");
        var vadersaber = scn.GetDSCard("vadersaber");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, anakin, trooper);
        scn.AttachCardsTo(anakin, jedisaber);
        scn.AttachCardsTo(trooper, vadersaber);
        scn.SkipToLSTurn(Phase.BATTLE);

        scn.LSInitiateBattle(site);
        scn.SkipToDamageSegment(false);
        assertTrue(scn.LSWonBattle());
        scn.DSPayBattleDamageFromCardInPlay(trooper);
        // Trooper and Vader's Lightsaber are lost together; DS orders them on Lost Pile
        scn.DSChooseCard(vadersaber);

        assertTrue(scn.LSCardActionAvailable(anakin, "Steal"));
        scn.LSUseCardAction(anakin, "Steal");
        assertTrue(scn.LSHasCardChoiceAvailable(vadersaber));
        scn.LSChooseCard(vadersaber);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(anakin, vadersaber));
    }

    /** PC website card list text (2012-12-12, PC-E1): the latest version under Bill's option (b). */
    @Test
    public void AnakinSoloCurrentCardIsTheLatestText() {
        assertCurrentCard("1282_12", "Anakin Solo",
                "Power +2 while armed with a lightsaber. May use any 'stolen' lightsaber. Once during battle, where present, may steal an opponent's just-lost lightsaber. Immune to attrition <3 (<5 while armed with a 'stolen' lightsaber).");
    }

    /** An opponent's lightsaber is lost (with its character) where Anakin is present, outside battle. */
    public static boolean anakinMayStealOutsideBattle(String anakinId) {
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(
                new java.util.HashMap<>() {{
                    put("anakin", anakinId);
                }},
                new java.util.HashMap<>() {{
                    put("trooper", "1_194");
                    put("vadersaber", "1_324");
                }},
                20, 20,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var anakin = scn.GetLSCard("anakin");
        var trooper = scn.GetDSCard("trooper");
        var vadersaber = scn.GetDSCard("vadersaber");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, anakin, trooper);
        scn.AttachCardsTo(trooper, vadersaber);
        scn.SkipToLSTurn(com.gempukku.swccgo.common.Phase.CONTROL);
        scn.LSExecuteAdHocEffect(anakin, new com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect(
                new com.gempukku.swccgo.logic.actions.TopLevelGameTextAction(anakin, anakin.getCardId()), trooper));
        for (int i = 0; i < 10 && vadersaber.getZone() != com.gempukku.swccgo.common.Zone.LOST_PILE; i++) {
            if (scn.DSHasCardChoiceAvailable(vadersaber))
                scn.DSChooseCard(vadersaber);
            else if (scn.LSHasCardChoiceAvailable(vadersaber))
                scn.LSChooseCard(vadersaber);
            else
                scn.PassResponses();
        }
        // Dark Side responds first to the loss; then Light Side may respond with Anakin.
        if (scn.DSGetDecision() != null)
            scn.DSPass();
        org.junit.Assert.assertEquals(com.gempukku.swccgo.common.Zone.LOST_PILE, vadersaber.getZone());
        return scn.LSCardActionAvailable(anakin, "Steal");
    }

    @Test
    public void AnakinSoloMayNotStealOutsideBattle() {
        assertFalse(anakinMayStealOutsideBattle("1282_12"));
    }
}
