package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("obiwan", "1_21");
                    put("jedisaber", "1_155"); // Jedi Lightsaber
                }},
                new HashMap<>() {{
                    put("vader", "1230_13");
                    put("original", "2230_13");
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
    public void LordVaderStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("vader").getBlueprint();

        assertEquals("Lord Vader", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(6, card.getAbility(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{

        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.VADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.DEATH_STAR_II);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LordVaderIsImmuneToAttritionLessThanFive() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, vader);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vader), scn.epsilon);
    }

    @Test
    public void LordVaderHasTheMasterListText() {
        var scn = GetScenario();
        var card = scn.GetDSCard("vader").getBlueprint();
        assertEquals("Adds 3 to power of anything he pilots. May steal (and then use) an opponent's lightsaber just lost from same site; may then use two weapons. While The Quick And Easy Path on table, Jedi Escape and What Was It? may not cancel duels. Immune to attrition < 5.",
                card.getGameText());
        assertEquals(null, card.getLegacyVersion());
    }

    private VirtualTableScenario obiWanWithSaberAtVadersSite() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader"), scn.GetLSCard("obiwan"));
        scn.AttachCardsTo(scn.GetLSCard("obiwan"), scn.GetLSCard("jedisaber"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        return scn;
    }

    private boolean dsLoses(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        var action = new TopLevelGameTextAction(scn.GetDSCard("trooper"), scn.DS, scn.GetDSCard("trooper").getCardId());
        action.appendEffect(new LoseCardFromTableEffect(action, card));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        // pass the responses until the Dark Side is offered Vader's response to the just-lost lightsaber (or give up)
        var saber = scn.GetLSCard("jedisaber");
        for (int i = 0; i < 12; i++) {
            if (scn.DSGetDecision() != null && scn.GetDSAvailableActions().stream().anyMatch(x -> x.contains("Steal") || x.contains("on Vader")))
                return true;
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(saber)) scn.LSChooseCard(saber);
            else if (scn.LSGetDecision() != null) scn.LSPass();
            else if (scn.DSGetDecision() != null) scn.DSPass();
        }
        return false;
    }

    @Test
    public void LordVaderStealsAndMayUseAnOpponentsLightsaberJustLostFromSameSite() {
        //test1: Steal is offered (not the old free deploy)
        //test2: the lightsaber is attached to Vader, still the opponent's card, so 'stolen'
        //test3: Vader may use it
        var scn = obiWanWithSaberAtVadersSite();
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetLSCard("jedisaber");

        dsLoses(scn, saber);
        assertInZone(Zone.LOST_PILE, saber);
        assertTrue(scn.DSCardActionAvailable(vader, "Steal")); //test1
        assertFalse(scn.DSCardActionAvailable(vader, "Deploy"));
        scn.DSUseCardAction(vader, "Steal");
        if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(saber))
            scn.DSChooseCard(saber);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(vader, saber)); //test2
        assertTrue(com.gempukku.swccgo.filters.Filters.stolen.accepts(scn.game(), saber)); //test2
        assertTrue(scn.game().getModifiersQuerying().grantedToUseWeapon(scn.gameState(), vader, saber)); //test3
    }

    @Test
    public void LordVaderStealsALightsaberLostWithAnOpponentsCharacterAtSameSite() {
        var scn = obiWanWithSaberAtVadersSite();
        var vader = scn.GetDSCard("vader");
        var obiwan = scn.GetLSCard("obiwan");
        var saber = scn.GetLSCard("jedisaber");

        dsLoses(scn, obiwan);
        assertInZone(Zone.LOST_PILE, saber);
        assertTrue(scn.DSCardActionAvailable(vader, "Steal"));
        scn.DSUseCardAction(vader, "Steal");
        if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(saber))
            scn.DSChooseCard(saber);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(vader, saber));
    }

    @Test
    public void LordVaderDoesNotStealALightsaberLostFromAnotherSite() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var obiwan = scn.GetLSCard("obiwan");
        var saber = scn.GetLSCard("jedisaber");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), obiwan);
        scn.AttachCardsTo(obiwan, saber);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertFalse(dsLoses(scn, saber));
        assertInZone(Zone.LOST_PILE, saber);
    }

    @Test
    public void LordVaderHasNoneOfTheOriginalFreeDeployClause() {
        // The original text deployed the lightsaber on him; the current text steals it (the card stays the opponent's).
        var scn = obiWanWithSaberAtVadersSite();
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetLSCard("jedisaber");
        dsLoses(scn, saber);
        assertFalse(scn.DSCardActionAvailable(vader, "Deploy"));
        assertFalse(scn.GetDSAvailableActions().stream().anyMatch(x -> x.contains("on Vader")));
    }

    @Test
    public void LordVaderAndHisOriginalVersionAreTheSameUniqueCard() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var original = scn.GetDSCard("original");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), original);
        scn.MoveCardsToDSHand(vader);
        scn.EnsureDSForcePile(8);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSDeployAvailable(vader));
    }
}
